/*
 * Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.apimgt.apim.integration.store;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.apimgt.api.APIManagementException;
import org.wso2.carbon.apimgt.apim.integration.common.APIMIntegrationException;
import org.wso2.carbon.apimgt.apim.integration.common.configs.APIMConfig;
import org.wso2.carbon.apimgt.apim.integration.dcr.DcrClient;
import org.wso2.carbon.apimgt.apim.integration.store.dto.APIMApplicationDTO;
import org.wso2.carbon.apimgt.apim.integration.store.dto.APIMApplicationListDTO;
import org.wso2.carbon.apimgt.apim.integration.store.dto.ApplicationKeyDTO;
import org.wso2.carbon.apimgt.apim.integration.store.dto.ApplicationKeyGenRequestDTO;
import org.wso2.carbon.apimgt.apim.integration.store.dto.StoreAPIListDTO;
import org.wso2.carbon.apimgt.apim.integration.store.dto.SubscriptionDTO;
import org.wso2.carbon.apimgt.apim.integration.store.dto.SubscriptionListDTO;

public class StoreClient {
	private static final Log log = LogFactory.getLog(StoreClient.class);
	private InternalStoreClient internalStoreClient;
	private APIMConfig config;
	private DcrClient dcrClient;
	
	//TODO to be removed once apim fix is avaialable
	public void setDcrClient(DcrClient dcrClient) {
		this.dcrClient = dcrClient;
	}


	/**
	 * Initialize APIMIntegration client with a APIMConfig provided as the config
	 * @param confign - An instance of APIMConfig, see apim-integration.xml
	 * @throws APIManagementException
	 */
	public StoreClient(APIMConfig config) throws APIManagementException{
		if (config == null) {
			throw new APIManagementException("APIM config should not be null, see apim-ntegration.xml");
		}
		this.config = config;
		this.internalStoreClient = new InternalStoreClient();
		this.dcrClient = new DcrClient(config);
	}
	

	/**
	 * Get a list of of available APIs (from WSO2-APIM) qualifying under a given search query. 
	 * @param query - You can search in attributes by using an ":" modifier. Eg. "tag:wso2" will match an API if the tag of the API is "wso2".
	 * @param accessToken - OAuth bearer token, required to invoke the (OAuth secured) store-apis.
	 * @return - An instance of SubscriptionListDTO, which contains the list of apis
	 * @throws APIMIntegrationException - If fails to invoke the operation due to wrong credentials or any other issue
	 */
	public StoreAPIListDTO searchStoreAPIs(String searchQuery) throws APIMIntegrationException {
		StoreAPIListDTO result = null;
		try {
			result = internalStoreClient.searchStoreAPIs(config.getStoreEndpointConfig(), searchQuery, dcrClient.getToken());
		} catch (APIMIntegrationException e) {
			if (e.getResponseStatus() == 401) {
				result = internalStoreClient.searchStoreAPIs(config.getStoreEndpointConfig(), searchQuery, dcrClient.getRenewedToken());
			} else {
				throw e;
			}
		}
		return result;
	}
	
	
	/**
	 * Creates a application in WSO2-APIM, which then can be used to subscribe api's
	 * @param requestApp - An instance of APIMApplicationDTO, which carries the information of application creation request
	 * @return - An instance of APIMApplicationDTO, which contains the information of the application created.
	 * @throws APIMIntegrationException - If fails to invoke the operation due to wrong credentials or any other issue
	 */
	public APIMApplicationDTO createAPIMApplicationIfNotExists(APIMApplicationDTO requestApp) throws APIMIntegrationException {
				
		APIMApplicationDTO apimApp = null;
		APIMApplicationListDTO appList = searchAPIMApplications();
		System.out.println("API application listing successfull appList.getApplicationId() = " + appList.getCount());
		List<APIMApplicationDTO> apimAppList = appList.getList();
		APIMApplicationDTO existingApp = getExistingApp(requestApp, apimAppList);
		if (existingApp != null) {
			System.out.println("Application " + existingApp.getName() + " apready exists, therefore not creating");
			APIMApplicationDTO existingApInfo = getAPIMApplicationDetails(existingApp.getApplicationId());
			apimApp = existingApInfo;
		} else {
			apimApp = createAPIMApplication(requestApp);
			System.out.println("API application creation successfull apimApp.getApplicationId() = " + apimApp.getApplicationId());
		}
		return apimApp;
	}
	
	/**
	 * Subscirbe an API to an Application providing the id of the API and the application. 
	 * @param subscriptionRequest - An instance of SubscriptionDTO which contains the API id and the Application id.
	 * @return - An instance of SubscriptionDTO, which contains the information of the subscription details.
	 * @throws APIMIntegrationException - If fails to invoke the operation due to wrong credentials or any other issue
	 */
	public SubscriptionDTO subscribeAPItoAppIfNotExists(SubscriptionDTO subscriptionRequest) throws APIMIntegrationException {
		SubscriptionDTO result = null;
		SubscriptionListDTO existingSubscriptions = getExistingSubscriptions(subscriptionRequest.getApiIdentifier());  
		SubscriptionDTO availableSubscription = getExistingSubscription(existingSubscriptions, subscriptionRequest);
		if (availableSubscription != null) {
			System.out.println("API subscription already exists for apiID " + subscriptionRequest.getApiIdentifier() + " and appID " +  subscriptionRequest.getApplicationId());
			System.out.println("API subscription already exists availableSubscription.getSubscriptionId() = " + availableSubscription.getSubscriptionId());
		} else {
			SubscriptionDTO subscriptionResult = subscribeAPItoApp(subscriptionRequest);
			System.out.println("API getSubscriptionId successfull subscriptionResult.getSubscriptionId() = " + subscriptionResult.getSubscriptionId());
		}
		return result;
	}
	
	/**
	 * Generates keys (consumerKey, consumerSecret, accessToken) for an Application specified by the applicationId. 
	 * @param keygenRequest - An instance of ApplicationKeyGenRequestDTO which contains information of key generation request.
	 * @param applicationId - Unique Id of the APIM application (which needs to generate the keys)
	 * @return - An instance of ApplicationKeyDTO, which contains the information of keys(consumerKey, consumerSecret, accessToken).
	 * @throws APIMIntegrationException - If fails to invoke the operation due to wrong credentials or any other issue
	 */
	public ApplicationKeyDTO generateKeysforAppIfNotExists(ApplicationKeyGenRequestDTO keygenRequest, APIMApplicationDTO apimApp) throws APIMIntegrationException {
		ApplicationKeyDTO applicationKey = getProductionKeyIfExists(apimApp);
		if (applicationKey != null) {
			System.out.println("A PRODCUTION applicationKey is already exists therefore not cretating keys, applicationKey.getConsumerKey and secret " + applicationKey.getConsumerKey() + "  " + applicationKey.getConsumerSecret());
		} else {
			applicationKey = generateKeysforApp(keygenRequest, apimApp.getApplicationId());
			System.out.println("API applicationKey gen OK, applicationKey.getConsumerKey and secret " + applicationKey.getConsumerKey() + "  " + applicationKey.getConsumerSecret());
			System.out.println("API applicationKey generation successfull applicationKey.getToken().toString() = " + applicationKey.getToken().toString());
		}
		return applicationKey;
	}

	/**
	 * Searches available applications in WSO2-APIM, which then can be used to subscribe api's
	 * @param accessToken - OAuth bearer token, required to invoke the (OAuth secured) store-apis.
	 * @return - An instance of APIMApplicationListDTO, which contains a list of APIMApplicationDTO objects corresponds to existing apllications.
	 * @throws APIMIntegrationException - If fails to invoke the operation due to wrong credentials or any other issue
	 */
	private APIMApplicationListDTO searchAPIMApplications() throws APIMIntegrationException {
		
		APIMApplicationListDTO result = null;
		try {
			result = internalStoreClient.searchAPIMApplications(config.getStoreEndpointConfig(), dcrClient.getToken());
		} catch (APIMIntegrationException e) {
			if (e.getResponseStatus() == 401) {
				result = internalStoreClient.searchAPIMApplications(config.getStoreEndpointConfig(), dcrClient.getRenewedToken());
			} else {
				throw e;
			}
		}
		return result;
	}
	
	/**
	 * Gets the details of APIM application (specified by the appId) in WSO2-APIM.
	 * @param appId - The unique identifier of the application
	 * @return - An instance of APIMApplicationDTO, which contains the details of application corresponds to appId.
	 * @throws APIMIntegrationException - If fails to invoke the operation due to wrong credentials or any other issue
	 */
	private APIMApplicationDTO getAPIMApplicationDetails(String appId) throws APIMIntegrationException {
		
		APIMApplicationDTO result = null;
		try {
			result = internalStoreClient.getAPIMApplicationDetails(config.getStoreEndpointConfig(), dcrClient.getToken(), appId);
		} catch (APIMIntegrationException e) {
			if (e.getResponseStatus() == 401) {
				result = internalStoreClient.getAPIMApplicationDetails(config.getStoreEndpointConfig(), dcrClient.getRenewedToken(), appId);
			} else {
				throw e;
			}
		}
		return result;
	}

	/**
	 * Creates a application in WSO2-APIM, which then can be used to subscribe api's
	 * @param requestApp - An instance of APIMApplicationDTO, which carries the information of application creation request
	 * @return - An instance of APIMApplicationDTO, which contains the information of the application created.
	 * @throws APIMIntegrationException - If fails to invoke the operation due to wrong credentials or any other issue
	 */
	private APIMApplicationDTO createAPIMApplication(APIMApplicationDTO requestApp) throws APIMIntegrationException {
				
		APIMApplicationDTO result = null;
		try {
			result = internalStoreClient.createAPIMApplication(config.getStoreEndpointConfig(), requestApp, dcrClient.getToken());
		} catch (APIMIntegrationException e) {
			if (e.getResponseStatus() == 401) {
				result = internalStoreClient.createAPIMApplication(config.getStoreEndpointConfig(), requestApp, dcrClient.getRenewedToken());
			} else {
				throw e;
			}
		}
		return result;
	}
	
	/**
	 * Gets existing subscriptions for a api (specified by apiId). 
	 * @return - An instance of SubscriptionListDTO, which contains the list of subscriptions of the requested api.
	 * @throws APIMIntegrationException - If fails to invoke the operation due to wrong credentials or any other issue
	 */
	private SubscriptionListDTO getExistingSubscriptions(String apiId) throws APIMIntegrationException {
		SubscriptionListDTO result = null;
		try {
			result = internalStoreClient.getExistingSubscriptions(config.getStoreEndpointConfig(), dcrClient.getToken(), apiId);
		} catch (APIMIntegrationException e) {
			if (e.getResponseStatus() == 401) {
				result = internalStoreClient.getExistingSubscriptions(config.getStoreEndpointConfig(), dcrClient.getRenewedToken(), apiId);
			} else {
				throw e;
			}
		}
		return result;
	}
	
	/**
	 * Subscirbe an API to an Application providing the id of the API and the application. 
	 * @param subscriptionRequest - An instance of SubscriptionDTO which contains the API id and the Application id.
	 * @return - An instance of SubscriptionDTO, which contains the information of the subscription details.
	 * @throws APIMIntegrationException - If fails to invoke the operation due to wrong credentials or any other issue
	 */
	private SubscriptionDTO subscribeAPItoApp(SubscriptionDTO subscriptionRequest) throws APIMIntegrationException {
		SubscriptionDTO result = null;
		try {
			result = internalStoreClient.subscribeAPItoApp(config.getStoreEndpointConfig(), subscriptionRequest,dcrClient.getToken());
		} catch (APIMIntegrationException e) {
			if (e.getResponseStatus() == 401) {
				result = internalStoreClient.subscribeAPItoApp(config.getStoreEndpointConfig(), subscriptionRequest, dcrClient.getRenewedToken());
			} else {
				throw e;
			}
		}
		return result;
	}
	
	/**
	 * Generates keys (consumerKey, consumerSecret, accessToken) for an Application specified by the applicationId. 
	 * @param keygenRequest - An instance of ApplicationKeyGenRequestDTO which contains information of key generation request.
	 * @param applicationId - Unique Id of the APIM application (which needs to generate the keys)
	 * @return - An instance of ApplicationKeyDTO, which contains the information of keys(consumerKey, consumerSecret, accessToken).
	 * @throws APIMIntegrationException - If fails to invoke the operation due to wrong credentials or any other issue
	 */
	private ApplicationKeyDTO generateKeysforApp(ApplicationKeyGenRequestDTO keygenRequest, String applicationId) throws APIMIntegrationException {
		ApplicationKeyDTO result = null;
		try {
			result = internalStoreClient.generateKeysforApp(config.getStoreEndpointConfig(), keygenRequest, applicationId, dcrClient.getToken());
		} catch (APIMIntegrationException e) {
			if (e.getResponseStatus() == 401) {
				result = internalStoreClient.generateKeysforApp(config.getStoreEndpointConfig(), keygenRequest, applicationId, dcrClient.getRenewedToken());
			} else {
				throw e;
			}
		}
		return result;
	}
	
	private static  APIMApplicationDTO getExistingApp(APIMApplicationDTO application, List<APIMApplicationDTO> appList) {
		for (APIMApplicationDTO app : appList) {
			if (application.getName().equals(app.getName())) {
				return app;
			}
		}
		return null;
	}
	
	private static ApplicationKeyDTO getProductionKeyIfExists(APIMApplicationDTO apimApp) {
		if (apimApp.getKeys().isEmpty()) {
			return null;
		} else {
			List<ApplicationKeyDTO> appKeys = apimApp.getKeys();
			for (ApplicationKeyDTO key : appKeys) {
				if (key.getKeyType().equals(ApplicationKeyDTO.KeyTypeEnum.PRODUCTION)){
					return key;
				}
			}
			return null;
		}
	}
	
	private static SubscriptionDTO getExistingSubscription(SubscriptionListDTO existingSubscriptions, SubscriptionDTO subscription) {
		for (SubscriptionDTO apiSubscription : existingSubscriptions.getList()) {
			if (apiSubscription.getApplicationId().equals(subscription.getApplicationId())){
				return apiSubscription;
			}
		}
		return null;
	}


}
