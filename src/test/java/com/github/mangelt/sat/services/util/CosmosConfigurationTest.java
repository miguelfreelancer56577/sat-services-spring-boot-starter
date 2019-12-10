package com.github.mangelt.sat.services.util;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.microsoft.azure.cosmosdb.ConnectionPolicy;
import com.microsoft.azure.cosmosdb.ConsistencyLevel;
import com.microsoft.azure.cosmosdb.Document;
import com.microsoft.azure.documentdb.DocumentClient;

import lombok.extern.slf4j.Slf4j;

@RunWith(SpringRunner.class)
@SpringBootTest(properties = {
		"application.cosmosdb.accountHost=https://cn-prod-mbr-common-data-sr.documents.azure.com:443/", 
		"application.cosmosdb.accountKey=zG7tEGSK9lUYlQoZIDBuMyjrTD1e6ub40hUBrzTLrZEjWUC7SmKx6n3rrO3zHgEGtCVSXNbvjalOKXQehIkyVQ==", 
		"application.cosmosdb.databaseName=membership_migration", 
		"application.cosmosdb.containerName=membership_mig_status"})
//@ContextConfiguration(classes = {CosmosConfiguration.class, MyRepo.class})
@Slf4j
public class CosmosConfigurationTest {

	@Value("${application.cosmosdb.accountHost}")
	String HOST;
	
	@Value("${application.cosmosdb.accountKey}")
	String MASTER_KEY;
	
	
	@Test
	public void counter() {
		
		DocumentClient documentClient = new DocumentClient(HOST, MASTER_KEY, ConnectionPolicy.GetDefault(), ConsistencyLevel.Strong);
		
		Document doc = repo.getCount("fwsync_46.json.split00");
		log.info("Retured value {}", doc.toJson());
	}
	
}
