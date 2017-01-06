package org.wso2.carbon.apimgt.application.extension;

import java.io.File;
import java.util.Arrays;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.apimgt.client.APIMClientException;
import org.wso2.carbon.apimgt.client.APIMConfigReader;
import org.wso2.carbon.apimgt.client.StoreClientHelper;
import org.wso2.carbon.apimgt.client.configs.APIMConfig;

import org.wso2.carbon.apimgt.application.extension.dto.ApiApplicationKey;
import org.wso2.carbon.apimgt.application.extension.exception.APIManagerException;
import org.wso2.carbon.apimgt.store.client.model.*;
import org.wso2.carbon.utils.CarbonUtils;


public class APIManagementProviderRESTServiceImpl implements APIManagementProviderService {
	
	private static final Log log = LogFactory.getLog(APIManagementProviderRESTServiceImpl.class);
	

	@Override
	public ApiApplicationKey generateAndRetrieveApplicationKeys(String apiApplicationName1, String[] tags1,
			String keyType1, String username, boolean isAllowedAllDomains1) throws APIManagerException {
		
		StoreClientHelper apimStoreClient = null;
		APIMConfig config = null;
		try {
			String configFile = CarbonUtils.getCarbonConfigDirPath() + File.separator + "apim-integration.xml";
			config = APIMConfigReader.getAPIMConfig(configFile);
			config.getDcrEndpointConfig().getClientProfile().setClientName("store_" + config.getDcrEndpointConfig().getClientProfile().getClientName());
            //TODO above line is a temporary fix, remove it when properly fixed
			apimStoreClient = new StoreClientHelper(config);
		} catch (APIMClientException e) {
			throw new APIManagerException("Error generating accestoken", e);
		}
		
		Application requestApp = new Application();
		requestApp.setName(apiApplicationName1);
		requestApp.setThrottlingTier("Unlimited");
		Application createdApp = null;
		try {
			createdApp = apimStoreClient.createAPIMApplicationIfNotExists(requestApp);
		} catch (APIMClientException e1) {
			log.info("App creation failed " + e1.getMessage(), e1);
		}
		log.info("App created succesfully createdApp.getApplicationId " + createdApp.getApplicationId());
		
		ApplicationKeyGenerateRequest keygenRequest = new ApplicationKeyGenerateRequest();
		if ("PRODUCTION".equals(keyType1.toUpperCase())) {
			keygenRequest.setKeyType(ApplicationKeyGenerateRequest.KeyTypeEnum.PRODUCTION);
		} else {
			keygenRequest.setKeyType(ApplicationKeyGenerateRequest.KeyTypeEnum.SANDBOX);
		}
		keygenRequest.setValidityTime("3600");
		if (isAllowedAllDomains1) {
			keygenRequest.setAccessAllowDomains(Arrays.asList("ALL"));
		} else {
			//TODO else what to do??
			keygenRequest.setAccessAllowDomains(Arrays.asList("ALL"));
		}
		ApplicationKey applicationKey = null;
		try {
			applicationKey = apimStoreClient.generateKeysForAppIfNotExists(keygenRequest, createdApp);
		} catch (APIMClientException e) {
			log.error("App  key generation failed " + e.getMessage(), e);
		}
		log.info("API applicationKey generation successfull applicationKey.getToken().toString() = " + applicationKey.getToken().toString());
		
		String searchQuery = "tag:" + tags1[0]; //TODO build the correct queryString to get apis for all tags
		APIList apiList = null;
		try {
			apiList = apimStoreClient.searchStoreAPIs("carbon.super", searchQuery);
		} catch (APIMClientException e) {
			log.error("API search failed " + e.getMessage(), e);
		}
		log.info("API list retrived apiList.count = " + apiList.getCount());
		
		for (APIInfo storeApi :apiList.getList()) {
			Subscription subscriptionRequest = new Subscription();
			subscriptionRequest.setTier("Unlimited");
			subscriptionRequest.setApplicationId(createdApp.getApplicationId());
			subscriptionRequest.setApiIdentifier(storeApi.getId());
			Subscription subscriptionResult = null;
			try {
				subscriptionResult = apimStoreClient.subscribeAPItoAppIfNotExists(subscriptionRequest);
			} catch (APIMClientException e) {
				log.error("API susbscription failed " + e.getMessage(), e);
			}
			log.info("API getSubscriptionId successfull subscriptionResult.getSubscriptionId() = " + subscriptionResult.getSubscriptionId());
		}
		
		ApiApplicationKey key = new ApiApplicationKey();
		key.setConsumerKey(applicationKey.getConsumerKey());
		key.setConsumerSecret(applicationKey.getConsumerSecret());
	
		return key;
	}
	

	@Override
	public void registerExistingOAuthApplicationToAPIApplication(String jsonString, String applicationName,
			String clientId, String username, boolean isAllowedAllDomains, String keyType, String[] tags)
			throws APIManagerException {

	}

	@Override
	public void removeAPIApplication(String applicationName, String username) throws APIManagerException {

	}
	
}
