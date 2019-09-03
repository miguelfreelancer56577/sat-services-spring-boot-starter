package com.github.mangelt.sat.services.repository;

import java.lang.reflect.ParameterizedType;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import com.microsoft.azure.cosmosdb.Document;
import com.microsoft.azure.cosmosdb.FeedOptions;
import com.microsoft.azure.cosmosdb.FeedResponse;
import com.microsoft.azure.cosmosdb.ResourceResponse;
import com.microsoft.azure.cosmosdb.rx.AsyncDocumentClient;

import hu.akarnokd.rxjava.interop.RxJavaInterop;
import lombok.extern.slf4j.Slf4j;
import reactor.adapter.rxjava.RxJava2Adapter;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
@Slf4j
public abstract class CosmosRepository <T,I>{

	@Autowired
	protected AsyncDocumentClient client;

	@Autowired
	@Qualifier("containerLink")
	protected String containerLink;

	protected Class<T> typeClass;

	protected FeedOptions feedOptions;

	public CosmosRepository(){
		this.typeClass = (Class<T>) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
		this.feedOptions = new FeedOptions();
	}

	public Flux<ResourceResponse<Document>> save(T document) {
		rx.Observable<ResourceResponse<Document>>  oldObs = this.client.createDocument(this.containerLink, document, null, true);
		return this.transformResourceResponse(oldObs);
	}

	public Mono<T> save(T document, boolean onlyDocument) {
		if(onlyDocument){
			Flux<ResourceResponse<Document>> response = this.save(document);
			return response.flatMap(this::mapToObject).singleOrEmpty();
		}
		else
		{
			return Mono.empty();
		}
	}

	public Flux<ResourceResponse<Document>> update(T document){
		rx.Observable<ResourceResponse<Document>>  oldObs = this.client.upsertDocument(this.containerLink, document, null, true);
		return this.transformResourceResponse(oldObs);
	}

	public Mono<T> update(T document, boolean onlyDocument){
		if(onlyDocument){
			Flux<ResourceResponse<Document>> response = this.update(document);
			return response.flatMap(this::mapToObject).singleOrEmpty();
		}
		else
		{
			return Mono.empty();
		}
	}

	public Mono<T> getById(I id){
		String query = String.format("SELECT * FROM c WHERE c.id = '%s'", id.toString());
		rx.Observable<FeedResponse<Document>> documentQueryObservable = this.client.queryDocuments(this.containerLink, query, null);
		Flux<FeedResponse<Document>> trans = this.transformFeedResponse(documentQueryObservable);
		return trans.flatMap(feedResponse->{
			List<Document> docs = feedResponse.getResults();
			if(docs.isEmpty())
			{
				return Mono.empty();
			}
			else
			{
				return Mono.just(docs.get(0));
			}
		})
				.flatMap(this::mapToObject)
				.singleOrEmpty();
	}

	public Flux<T> getAll(){
		this.feedOptions.setEnableCrossPartitionQuery(true);
		String query = "SELECT * FROM c";
		rx.Observable<FeedResponse<Document>> documentQueryObservable = this.client.queryDocuments(this.containerLink, query, this.feedOptions);
		Flux<FeedResponse<Document>> trans = this.transformFeedResponse(documentQueryObservable);
		return trans.flatMap(feedResponse->{
			List<Document> docs = feedResponse.getResults();
			if(!docs.isEmpty())
			{
				return Flux.fromIterable(docs);
			}
			else
			{
				return Flux.empty();
			}
		})
				.flatMap(this::mapToObject);
	}

	protected Mono<T> deleteById(Document doc){
		rx.Observable<ResourceResponse<Document>>  oldObs = this.client.deleteDocument(doc.getSelfLink(), null);
		return this.transformResourceResponse(oldObs)
				.flatMap(this::mapToObject)
				.singleOrEmpty();
	}

	protected Flux<ResourceResponse<Document>> transformResourceResponse(rx.Observable<ResourceResponse<Document>>  from){
		io.reactivex.Observable<ResourceResponse<Document>> newObs = RxJavaInterop.toV2Observable(from);
		return RxJava2Adapter.observableToFlux(newObs, io.reactivex.BackpressureStrategy.BUFFER);
	}

	protected Flux<FeedResponse<Document>> transformFeedResponse(rx.Observable<FeedResponse<Document>>  from){
		io.reactivex.Observable<FeedResponse<Document>> newObs = RxJavaInterop.toV2Observable(from);
		return RxJava2Adapter.observableToFlux(newObs, io.reactivex.BackpressureStrategy.BUFFER);
	}

	protected Mono<T> mapToObject(ResourceResponse resourceResponse){
		return Mono.just(resourceResponse.getResource()
				.toObject(this.typeClass))
				.onErrorResume(error->{
					log.error("Triggered error while trying to convert from Document to {}", this.typeClass);
					return Mono.empty();
				});
	}

	protected Mono<T> mapToObject(Document doc){
		return Mono.just(doc.toObject(this.typeClass))
				.onErrorResume(error->{
					log.error("Triggered error while trying to convert from Document to {}", this.typeClass);
					return Mono.empty();
				});
	}

}
