package org.wso2.carbon.apimgt.application.extension;

import java.io.File;
import java.util.Arrays;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.apimgt.api.APIManagementException;
import org.wso2.carbon.apimgt.apim.integration.common.APIMConfigReader;
import org.wso2.carbon.apimgt.apim.integration.common.APIMIntegrationException;
import org.wso2.carbon.apimgt.apim.integration.common.configs.APIMConfig;
import org.wso2.carbon.apimgt.apim.integration.store.StoreClient;
import org.wso2.carbon.apimgt.apim.integration.store.dto.APIMApplicationDTO;
import org.wso2.carbon.apimgt.apim.integration.store.dto.ApplicationKeyDTO;
import org.wso2.carbon.apimgt.apim.integration.store.dto.ApplicationKeyGenRequestDTO;
import org.wso2.carbon.apimgt.apim.integration.store.dto.StoreAPIDTO;
import org.wso2.carbon.apimgt.apim.integration.store.dto.StoreAPIListDTO;
import org.wso2.carbon.apimgt.apim.integration.store.dto.SubscriptionDTO;
import org.wso2.carbon.apimgt.application.extension.dto.ApiApplicationKey;
import org.wso2.carbon.apimgt.application.extension.exception.APIManagerException;
import org.wso2.carbon.utils.CarbonUtils;


public class APIManagementProviderRESTServiceImpl implements APIManagementProviderService {
	
	private static final Log log = LogFactory.getLog(APIManagementProviderRESTServiceImpl.class);
	

	@Override
	public ApiApplicationKey generateAndRetrieveApplicationKeys(String apiApplicationName1, String[] tags1,
			String keyType1, String username, boolean isAllowedAllDomains1) throws APIManagerException {
		
		StoreClient apimStoreClient = null;
		APIMConfig config = null;
		try {
			String configFile = CarbonUtils.getCarbonConfigDirPath() + File.separator + "apim-integration.xml";
			config = APIMConfigReader.getAPIMConfig(configFile);
			config.getDcrEndpointConfig().getClientProfile().setClientName("store_" + config.getDcrEndpointConfig().getClientProfile().getClientName());
            //TODO above line is a temporary fix, remove it when properly fixed
			apimStoreClient = new StoreClient(config);
		} catch (APIManagementException e) {
			throw new APIManagerException("Error generating accestoken", e);
		}
		
		APIMApplicationDTO requestApp = new APIMApplicationDTO();
		requestApp.setName(apiApplicationName1);
		requestApp.setThrottlingTier("Unlimited");
		APIMApplicationDTO createdApp = null;
		try {
			createdApp = apimStoreClient.createAPIMApplicationIfNotExists(requestApp);
		} catch (APIMIntegrationException e1) {
			log.info("App creation failed " + e1.getMessage(), e1);
		}
		log.info("App created succesfully createdApp.getApplicationId " + createdApp.getApplicationId());
		
		ApplicationKeyGenRequestDTO keygenRequest = new ApplicationKeyGenRequestDTO();
		if ("PRODUCTION".equals(keyType1.toUpperCase())) {
			keygenRequest.setKeyType(ApplicationKeyGenRequestDTO.KeyTypeEnum.PRODUCTION);
		} else {
			keygenRequest.setKeyType(ApplicationKeyGenRequestDTO.KeyTypeEnum.SANDBOX);
		}
		keygenRequest.setValidityTime("3600");
		if (isAllowedAllDomains1) {
			keygenRequest.setAccessAllowDomains(Arrays.asList("ALL"));
		} else {
			//TODO else what to do??
			keygenRequest.setAccessAllowDomains(Arrays.asList("ALL"));
		}
		ApplicationKeyDTO applicationKey = null;
		try {
			applicationKey = apimStoreClient.generateKeysforAppIfNotExists(keygenRequest, createdApp);
		} catch (APIMIntegrationException e) {
			log.error("App  key generation failed " + e.getMessage(), e);
		}
		log.info("API applicationKey generation successfull applicationKey.getToken().toString() = " + applicationKey.getToken().toString());
		
		String searchQuery = "tag:" + tags1[0]; //TODO build the correct queryString to get apis for all tags
		StoreAPIListDTO apiList = null;
		try {
			apiList = apimStoreClient.searchStoreAPIs(searchQuery);
		} catch (APIMIntegrationException e) {
			log.error("API search failed " + e.getMessage(), e);
		}
		log.info("API list retrived apiList.count = " + apiList.getCount());
		
		for (StoreAPIDTO storeApi :apiList.getList()) {
			SubscriptionDTO subscriptionRequest = new SubscriptionDTO();
			subscriptionRequest.setTier("Unlimited");
			subscriptionRequest.setApplicationId(createdApp.getApplicationId());
			subscriptionRequest.setApiIdentifier(storeApi.getId());
			SubscriptionDTO subscriptionResult = null;
			try {
				subscriptionResult = apimStoreClient.subscribeAPItoAppIfNotExists(subscriptionRequest);
			} catch (APIMIntegrationException e) {
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
