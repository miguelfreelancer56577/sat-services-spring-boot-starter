package com.github.mangelt.sat.services.config;

import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.util.Objects;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.mangelt.sat.services.exception.BlobConfigurationException;
import com.github.mangelt.sat.services.util.ConfigConstant;
import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;

/**
 * Class used to configure the azure blob storage account.
 *
 * @author mangelt
 *
 */
@Configuration
@Slf4j
@ConditionalOnClass(value = {CloudBlobClient.class, CloudBlobContainer.class})
public class BlobConfiguration
{

	@Value("${azure.storage.connection-string:defult}")
	protected String StorageConnectionString;

	@Value("${container.name:defult}")
	protected String containerName;
	
	/**
	 * Get a CloudBlobClient to manage any blob file
	 *
	 * @return
	 * @throws BlobConfigurationException 
	 * @throws InvalidKeyException
	 * @throws URISyntaxException
	 */
	@Bean
	protected CloudBlobClient getCloudBlobClientInstance() throws BlobConfigurationException
	{
		try {
			if(ConfigConstant.defultProperty.equals(this.StorageConnectionString)){
				log.info("There is no connection string to get a client bean {}.", "getCloudBlobClientInstance");
//				return a null value for this bean 
				return null;
			}
//			return a new client top use blob storage
			return CloudStorageAccount.parse(this.StorageConnectionString).createCloudBlobClient();
		} catch (Exception e) {
			throw new BlobConfigurationException(e);
		}
	}

	/**
	 * Get a CloudBlobContainer to manage any operation against the blob storage file
	 *
	 * @param blobClient
	 * @return
	 * @throws BlobConfigurationException 
	 */
	@Bean
	protected CloudBlobContainer getCloudBlobContainerInstance(@Autowired(required = false) CloudBlobClient blobClient) throws BlobConfigurationException
	{
		try
		{
			if(Objects.isNull(blobClient)){
				log.info("There is no blob client to create this bean {}." , "getCloudBlobContainerInstance");
				return null;
			}
			CloudBlobContainer container = blobClient.getContainerReference(this.containerName);
			container.createIfNotExists();
			return container;
		}
		catch (Exception e)
		{
			throw new BlobConfigurationException(e);
		}
	}

}