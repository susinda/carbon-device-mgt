package org.wso2.carbon.apimgt.application.extension;

import java.io.File;
import java.util.Arrays;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.apimgt.api.APIManagementException;
import org.wso2.carbon.apimgt.apim.integration.common.APIMConfigReader;
import org.wso2.carbon.apimgt.apim.integration.common.configs.APIMConfig;
import org.wso2.carbon.apimgt.apim.integration.common.configs.StoreEndpointConfig;
import org.wso2.carbon.apimgt.apim.integration.dcr.dto.OAuthApplicationDTO;
import org.wso2.carbon.apimgt.apim.integration.dcr.dto.TokenDTO;
import org.wso2.carbon.apimgt.apim.integration.publisher.InternalPublisherClient;
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
	private static TokenDTO accessToken;
	private static OAuthApplicationDTO dcrApp;
	

	@Override
	public ApiApplicationKey generateAndRetrieveApplicationKeys(String apiApplicationName1, String[] tags1,
			String keyType1, String username, boolean isAllowedAllDomains1) throws APIManagerException {
		
		InternalPublisherClient apimClient = new InternalPublisherClient();
		APIMApplicationDTO requestApp = new APIMApplicationDTO();
		requestApp.setName(apiApplicationName1);
		requestApp.setThrottlingTier("Unlimited");
		APIMConfig config = null;
		try {
			String configFile = CarbonUtils.getCarbonConfigDirPath() + File.separator + "apim-integration.xml";
			config = APIMConfigReader.getAPIMConfig(configFile);
			getAccessToken(apimClient, config);
		} catch (APIManagementException e) {
			throw new APIManagerException("Error generating accestoken", e);
		}
		
		StoreEndpointConfig storeConfig = config.getStoreEndpointConfig();
		String apimToken = accessToken.getAccess_token();
		APIMApplicationDTO createdApp = apimClient.createAPIMApplication(storeConfig, requestApp, apimToken);
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
		ApplicationKeyDTO applicationKey = apimClient.generateKeysforApp(storeConfig, keygenRequest, createdApp.getApplicationId(), apimToken);
		log.info("API applicationKey generation successfull applicationKey.getToken().toString() = " + applicationKey.getToken().toString());
		
		String searchQuery = "tag:" + tags1[0]; //TODO build the correct queryString to get apis for all tags
		StoreAPIListDTO apiList = apimClient.searchStoreAPIs(storeConfig, searchQuery, apimToken);
		log.info("API list retrived apiList.count = " + apiList.getCount());
		
		for (StoreAPIDTO storeApi :apiList.getList()) {
			SubscriptionDTO subscription = new SubscriptionDTO();
			subscription.setTier("Unlimited");
			subscription.setApplicationId(createdApp.getApplicationId());
			subscription.setApiIdentifier(storeApi.getId());
			SubscriptionDTO subscriptionResult = apimClient.subscribeAPItoApp(storeConfig, subscription, apimToken);
			log.info("API getSubscriptionId successfull subscriptionResult.getSubscriptionId() = " + subscriptionResult.getSubscriptionId());
		}
		
		ApiApplicationKey key = new ApiApplicationKey();
		key.setConsumerKey(applicationKey.getConsumerKey());
		key.setConsumerSecret(applicationKey.getConsumerSecret());
	
		return key;
	}

	@Override
	public ApiApplicationKey generateAndRetrieveApplicationKeys(String apiApplicationName, String keyType,
			String username, boolean isAllowedAllDomains) throws APIManagerException {

		return null;
	}

	@Override
	public void registerExistingOAuthApplicationToAPIApplication(String jsonString, String applicationName,
			String clientId, String username, boolean isAllowedAllDomains, String keyType, String[] tags)
			throws APIManagerException {

	}

	@Override
	public void removeAPIApplication(String applicationName, String username) throws APIManagerException {

	}
	
	private static String getAccessToken(InternalPublisherClient apimClient, APIMConfig apimConfig) throws APIManagementException {
		if (isTokenNullOrExpired(accessToken)) {
			//TODO do the fix in apim side to return the same app if already created
			dcrApp = apimClient.createOAuthApplication(apimConfig.getDcrEndpointConfig());
			log.info("Auth app created sucessfully, app.getClientSecret() = " + dcrApp.getClientId());
	
			accessToken = apimClient.getUserToken(apimConfig.getTokenEndpointConfig(), dcrApp);
			log.info("Token generated succesfully, token.getExpires_in() = " + accessToken.getExpires_in());
		}
		return accessToken.getAccess_token();
	}

	private static boolean isTokenNullOrExpired(TokenDTO token) {
		if (token == null) {
			return true;
		} else {
			return false; //Here we considered if token is there it is not expired
			//TODO implement this logic properly, than returning false always
			//if (DateTime.parse(token.getExpires_in()).getMillisOfSecond() > DateTime.now().getMillisOfSecond()) {
			//	return true;
			//}
		}
	}


}
