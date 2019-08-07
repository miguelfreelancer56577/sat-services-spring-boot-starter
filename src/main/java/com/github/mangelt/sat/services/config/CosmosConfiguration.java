package com.github.mangelt.sat.services.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Repository;

import com.azure.data.cosmos.ConnectionPolicy;
import com.azure.data.cosmos.ConsistencyLevel;
import com.azure.data.cosmos.internal.AsyncDocumentClient;
import com.github.mangelt.sat.services.util.ConfigConstant;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
public class CosmosConfiguration{

	@Value("${application.cosmosdb.accountHost:defult}")
    private String accountHost;

    @Value("${application.cosmosdb.accountKey:defult}")
    private String accountKey;
    
    @Value("${application.cosmosdb.databaseName:defult}")
    private String databaseName;
    @Value("${application.cosmosdb.containerName:defult}")
    private String containerName;
    
    @Bean
    public AsyncDocumentClient asyncDocumentClient() {
    	if(ConfigConstant.defultProperty.equals(this.accountHost) || ConfigConstant.defultProperty.equals(this.accountKey)){
    		return null;
    	}
    	return new AsyncDocumentClient
        		.Builder()
        		.withServiceEndpoint(accountHost)
        		.withMasterKeyOrResourceToken(accountKey)
        		.withConnectionPolicy(ConnectionPolicy.defaultPolicy())
        		.withConsistencyLevel(ConsistencyLevel.EVENTUAL)
        		.build();
    }

    @Bean("containerLink")
    protected String containerLink() {
    	if(ConfigConstant.defultProperty.equals(this.databaseName) || ConfigConstant.defultProperty.equals(this.containerName)){
    		return null;
    	}
    	return "dbs/" + databaseName + "/colls/" + containerName;
    }
	
}
