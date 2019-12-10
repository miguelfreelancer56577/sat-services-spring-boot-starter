package com.github.mangelt.sat.services.config;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Repository;


import com.github.mangelt.sat.services.util.ConfigConstant;
import com.microsoft.azure.cosmosdb.rx.AsyncDocumentClient;
import com.microsoft.azure.cosmosdb.ConnectionPolicy;
import com.microsoft.azure.cosmosdb.ConsistencyLevel;

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
    	
    	ConnectionPolicy connPolicy = new ConnectionPolicy();
        connPolicy.setConnectionMode(com.microsoft.azure.cosmosdb.ConnectionMode.Gateway);
        connPolicy.setMaxPoolSize(50);
        com.microsoft.azure.cosmosdb.RetryOptions retryOptions = new com.microsoft.azure.cosmosdb.RetryOptions();
        retryOptions.setMaxRetryAttemptsOnThrottledRequests(3);
        retryOptions.setMaxRetryWaitTimeInSeconds(1);
        connPolicy.setRetryOptions(retryOptions);
    	
    	return new AsyncDocumentClient
        		.Builder()
        		.withServiceEndpoint(accountHost)
        		.withMasterKeyOrResourceToken(accountKey)
        		.withConnectionPolicy(connPolicy)
        		.withConsistencyLevel(ConsistencyLevel.Session)
        		.build();
    }

    @Bean("containerLink")
    protected String containerLink() {
    	return "dbs/" + databaseName + "/colls/" + containerName;
    }
	
}
