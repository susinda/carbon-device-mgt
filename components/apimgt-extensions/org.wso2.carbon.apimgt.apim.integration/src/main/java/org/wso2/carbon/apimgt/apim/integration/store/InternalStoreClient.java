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

import org.wso2.carbon.apimgt.apim.integration.common.APIMErrorDecoder;
import org.wso2.carbon.apimgt.apim.integration.common.APIMIntegrationException;
import org.wso2.carbon.apimgt.apim.integration.common.AuthBearerRequestInterceptor;
import org.wso2.carbon.apimgt.apim.integration.common.TrustedFeignClient;
import org.wso2.carbon.apimgt.apim.integration.common.configs.StoreEndpointConfig;
import org.wso2.carbon.apimgt.apim.integration.store.dto.APIMApplicationDTO;
import org.wso2.carbon.apimgt.apim.integration.store.dto.APIMApplicationListDTO;
import org.wso2.carbon.apimgt.apim.integration.store.dto.ApplicationKeyDTO;
import org.wso2.carbon.apimgt.apim.integration.store.dto.ApplicationKeyGenRequestDTO;
import org.wso2.carbon.apimgt.apim.integration.store.dto.StoreAPIListDTO;
import org.wso2.carbon.apimgt.apim.integration.store.dto.SubscriptionDTO;
import org.wso2.carbon.apimgt.apim.integration.store.dto.SubscriptionListDTO;

import feign.Feign;
import feign.gson.GsonDecoder;
import feign.gson.GsonEncoder;
import feign.jaxrs.JAXRSContract;


public class InternalStoreClient {

	public APIMApplicationListDTO searchAPIMApplications(StoreEndpointConfig storeEndpointConfig, String accessToken) throws APIMIntegrationException {
		StoreClientInterface dynamicClientRegistrationService = Feign.builder()
				.client(new TrustedFeignClient())
				.contract(new JAXRSContract())
				.decoder(new GsonDecoder())
				.errorDecoder(new APIMErrorDecoder())
				.requestInterceptor(new AuthBearerRequestInterceptor(accessToken))
				.target(StoreClientInterface.class, storeEndpointConfig.getUrl());

		APIMApplicationListDTO resultApps = dynamicClientRegistrationService.getAPIMApplications();
		return resultApps;
	}
	
	public APIMApplicationDTO getAPIMApplicationDetails(StoreEndpointConfig storeEndpointConfig, String accessToken, String appId) throws APIMIntegrationException {
		StoreClientInterface dynamicClientRegistrationService = Feign.builder()
				.client(new TrustedFeignClient())
				.contract(new JAXRSContract())
				.decoder(new GsonDecoder())
				.errorDecoder(new APIMErrorDecoder())
				.requestInterceptor(new AuthBearerRequestInterceptor(accessToken))
				.target(StoreClientInterface.class, storeEndpointConfig.getUrl());

		APIMApplicationDTO resultApps = dynamicClientRegistrationService.getAPIMApplicationDetails(appId);
		return resultApps;
	}


	public APIMApplicationDTO createAPIMApplication(StoreEndpointConfig storeEndpointConfig, APIMApplicationDTO requestApp,  String accessToken) throws APIMIntegrationException {
		StoreClientInterface dynamicClientRegistrationService = Feign.builder()
				.client(new TrustedFeignClient())
				.contract(new JAXRSContract())
				.encoder(new GsonEncoder())
				.decoder(new GsonDecoder())
				.errorDecoder(new APIMErrorDecoder())
				.requestInterceptor(new AuthBearerRequestInterceptor(accessToken))
				.target(StoreClientInterface.class, storeEndpointConfig.getUrl());

		APIMApplicationDTO resultApp = dynamicClientRegistrationService.createAPIMApplication(requestApp);
		return resultApp;
	}
	
	public StoreAPIListDTO searchStoreAPIs(StoreEndpointConfig storeEndpointConfig, String searchQuery,  String accessToken) throws APIMIntegrationException {
		StoreClientInterface dynamicClientRegistrationService = Feign.builder()
				.client(new TrustedFeignClient())
				.contract(new JAXRSContract())
				.decoder(new GsonDecoder())
				.errorDecoder(new APIMErrorDecoder())
				.target(StoreClientInterface.class, storeEndpointConfig.getUrl());

		StoreAPIListDTO resultApp = dynamicClientRegistrationService.getExistingStoreAPIs(searchQuery);
		return resultApp;
	}
	
	public SubscriptionListDTO getExistingSubscriptions(StoreEndpointConfig storeEndpointConfig, String accessToken, String apiId) throws APIMIntegrationException {
		StoreClientInterface dynamicClientRegistrationService = Feign.builder()
				.client(new TrustedFeignClient())
				.contract(new JAXRSContract())
				.decoder(new GsonDecoder())
				.errorDecoder(new APIMErrorDecoder())
				.requestInterceptor(new AuthBearerRequestInterceptor(accessToken))
				.target(StoreClientInterface.class, storeEndpointConfig.getUrl());

		SubscriptionListDTO subscriptionResult = dynamicClientRegistrationService.getExistingSubscriptions(apiId);
		return subscriptionResult;
	}
	
	public SubscriptionDTO subscribeAPItoApp(StoreEndpointConfig storeEndpointConfig, SubscriptionDTO subscriptionRequest,  String accessToken) throws APIMIntegrationException {
		StoreClientInterface dynamicClientRegistrationService = Feign.builder()
				.client(new TrustedFeignClient())
				.contract(new JAXRSContract())
				.encoder(new GsonEncoder())
				.decoder(new GsonDecoder())
				.errorDecoder(new APIMErrorDecoder())
				.requestInterceptor(new AuthBearerRequestInterceptor(accessToken))
				.target(StoreClientInterface.class, storeEndpointConfig.getUrl());

		SubscriptionDTO subscriptionResult = dynamicClientRegistrationService.subscribeAPItoApp(subscriptionRequest);
		return subscriptionResult;
	}
	
	public ApplicationKeyDTO generateKeysforApp(StoreEndpointConfig storeEndpointConfig, ApplicationKeyGenRequestDTO keygenRequest, String applicationId, String accessToken) throws APIMIntegrationException {
		StoreClientInterface dynamicClientRegistrationService = Feign.builder()
				.client(new TrustedFeignClient())
				.contract(new JAXRSContract())
				.encoder(new GsonEncoder())
				.decoder(new GsonDecoder())
				.errorDecoder(new APIMErrorDecoder())
				.requestInterceptor(new AuthBearerRequestInterceptor(accessToken))
				.target(StoreClientInterface.class, storeEndpointConfig.getUrl());

		ApplicationKeyDTO appKeyResult = dynamicClientRegistrationService.generateKeysforApp(keygenRequest, applicationId);
		return appKeyResult;
	}
}
