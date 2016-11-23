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

package org.wso2.carbon.apimgt.apim.integration.common;

import org.wso2.carbon.apimgt.apim.integration.common.configs.ClientProfileConfig;
import org.wso2.carbon.apimgt.apim.integration.common.configs.DCREndpointConfig;
import org.wso2.carbon.apimgt.apim.integration.common.configs.PublisherEndpointConfig;
import org.wso2.carbon.apimgt.apim.integration.common.configs.StoreEndpointConfig;
import org.wso2.carbon.apimgt.apim.integration.common.configs.TokenConfig;
import org.wso2.carbon.apimgt.apim.integration.common.configs.TokenEndpointConfig;
import org.wso2.carbon.apimgt.apim.integration.dcr.dto.ClientProfileDTO;
import org.wso2.carbon.apimgt.apim.integration.dcr.dto.OAuthApplicationDTO;
import org.wso2.carbon.apimgt.apim.integration.dcr.dto.TokenDTO;
import org.wso2.carbon.apimgt.apim.integration.publisher.dto.PublisherAPIDTO;
import org.wso2.carbon.apimgt.apim.integration.publisher.dto.PublisherAPIListDTO;
import org.wso2.carbon.apimgt.apim.integration.store.dto.APIMApplicationDTO;
import org.wso2.carbon.apimgt.apim.integration.store.dto.APIMApplicationListDTO;
import org.wso2.carbon.apimgt.apim.integration.store.dto.ApplicationKeyDTO;
import org.wso2.carbon.apimgt.apim.integration.store.dto.ApplicationKeyGenRequestDTO;
import org.wso2.carbon.apimgt.apim.integration.store.dto.StoreAPIListDTO;
import org.wso2.carbon.apimgt.apim.integration.store.dto.SubscriptionDTO;
import org.wso2.carbon.apimgt.apim.integration.store.dto.SubscriptionListDTO;

import feign.Feign;
import feign.Response;
import feign.auth.BasicAuthRequestInterceptor;
import feign.gson.GsonDecoder;
import feign.gson.GsonEncoder;
import feign.jaxrs.JAXRSContract;

/**
 * This is Http(s) client wrapper to communicate WSO2 API-Manager, REST APIs.
 * This supports part of REST APIs defined in following locations
 * https://docs.wso2.com/display/AM200/apidocs/publisher
 * https://docs.wso2.com/display/AM200/apidocs/store
 * 
 * @author wso2
 *
 */
public class APIMClient {

	/**
	 * Creates a client application (client registration) in WSO2-APIM.
	 * 
	 * @param dcrConfig
	 *            - Part of the APIMConfig, which defines the configurations
	 *            parameters for Dynamic Client Regiistration Endpoint, see
	 *            apim-integration.xml
	 * @return An instance of OAuthApplicationDTO which contains the information
	 *         of the created client (registration)application.
	 * @throws APIMIntegrationException
	 *             - If api invocation goes wrong due to any reason.
	 */
	public OAuthApplicationDTO createOAuthApplication(DCREndpointConfig dcrConfig) throws APIMIntegrationException {

		APIMClientInterface dynamicClientRegistrationService = Feign.builder().client(new TrustedFeignClient()).contract(new JAXRSContract()).encoder(new GsonEncoder())
				.decoder(new GsonDecoder()).errorDecoder(new APIMErrorDecoder())
				.requestInterceptor(new BasicAuthRequestInterceptor(dcrConfig.getUserName(), dcrConfig.getPassword())).target(APIMClientInterface.class, dcrConfig.getUrl());

		ClientProfileConfig clinetConfig = dcrConfig.getClientProfile();
		ClientProfileDTO clientProfile = new org.wso2.carbon.apimgt.apim.integration.dcr.dto.ClientProfileDTO();
		clientProfile.setClientName(clinetConfig.getClientName());
		clientProfile.setGrantType(clinetConfig.getGrantType());
		clientProfile.setOwner(clinetConfig.getOwner());
		clientProfile.setSaasApp(clinetConfig.isSaasApp());
		clientProfile.setTokenScope(clinetConfig.getTokenScope());
		OAuthApplicationDTO oAuthApplication = dynamicClientRegistrationService.register(clientProfile);
		return oAuthApplication;
	}

	/**
	 * Gets a user token from WSO2-APIM, passing the client-application as an
	 * input
	 * 
	 * @param tokenConfig
	 *            - Part of the APIMConfig, which defines the configurations
	 *            parameters for Token Endpoint, see apim-integration.xml
	 * @param oAuthApplication
	 *            - Provides the consumerKey and consumerSecret
	 * @return - An instance of TokenDTO which contains the token information
	 * @throws APIMIntegrationException
	 *             - If api invocation goes wrong due to any reason.
	 */
	public TokenDTO getUserToken(TokenEndpointConfig tokenConfig, OAuthApplicationDTO oAuthApplication) throws APIMIntegrationException {

		APIMClientInterface dynamicClientRegistrationService = Feign.builder().client(new TrustedFeignClient()).contract(new JAXRSContract()).encoder(new GsonEncoder())
				.decoder(new GsonDecoder()).errorDecoder(new APIMErrorDecoder())
				.requestInterceptor(new BasicAuthRequestInterceptor(oAuthApplication.getClientId(), oAuthApplication.getClientSecret()))
				.target(APIMClientInterface.class, tokenConfig.getUrl());

		TokenConfig tokenInfo = tokenConfig.getTokenInfo();
		TokenDTO token = dynamicClientRegistrationService.requestToken(tokenInfo.getGrantType(), tokenInfo.getUserName(), tokenInfo.getPassword(), tokenInfo.getScope());
		return token;
	}

	/**
	 * If token is expired, users can use this method to renew the token
	 * 
	 * @param tokenConfig
	 *            - Part of the APIMConfig, which defines the configurations
	 *            parameters for Token Endpoint, see apim-integration.xml
	 * @param oAuthApplication
	 *            - Provides the consumerKey and consumerSecret
	 * @return - An instance of TokenDTO which contains the token information
	 * @throws APIMIntegrationException
	 *             - If api invocation goes wrong due to any reason.
	 */
	public TokenDTO renewUserToken(TokenEndpointConfig tokenConfig, OAuthApplicationDTO oAuthApplication, String refreshToken) throws APIMIntegrationException {

		APIMClientInterface dynamicClientRegistrationService = Feign.builder().client(new TrustedFeignClient()).contract(new JAXRSContract()).encoder(new GsonEncoder())
				.decoder(new GsonDecoder()).errorDecoder(new APIMErrorDecoder())
				.requestInterceptor(new BasicAuthRequestInterceptor(oAuthApplication.getClientId(), oAuthApplication.getClientSecret()))
				.target(APIMClientInterface.class, tokenConfig.getUrl());

		TokenConfig tokenInfo = tokenConfig.getTokenInfo();
		TokenDTO token = dynamicClientRegistrationService.requestTokenRenew("refresh_token", refreshToken, tokenInfo.getScope());
		return token;
	}

	/**
	 * Get a list of of available APIs (from WSO2-APIM) qualifying under a given
	 * search query.
	 * 
	 * @param publisherEndpointConfig
	 *            - Part of the APIMConfig, which defines the configurations
	 *            parameters for Publisher Endpoint, see apim-integration.xml
	 * @param query
	 *            - You can search in attributes by using an ":" modifier. Eg.
	 *            "tag:wso2" will match an API if the tag of the API is "wso2".
	 * @param accessToken
	 *            - OAuth bearer token, required to invoke the (OAuth secured)
	 *            publisher-apis.
	 * @return - An instance of SubscriptionListDTO, which contains the list of
	 *         apis
	 * @throws APIMIntegrationException
	 *             - If api invocation goes wrong due to any reason.
	 */
	public PublisherAPIListDTO searchPublisherAPIs(PublisherEndpointConfig publisherEndpointConfig, String searchQuery, String accessToken) throws APIMIntegrationException {
		APIMClientInterface dynamicClientRegistrationService = Feign.builder().client(new TrustedFeignClient()).contract(new JAXRSContract()).decoder(new GsonDecoder())
				.errorDecoder(new APIMErrorDecoder()).requestInterceptor(new AuthBearerRequestInterceptor(accessToken))
				.target(APIMClientInterface.class, publisherEndpointConfig.getUrl());
		PublisherAPIListDTO resultApp = null;

		resultApp = dynamicClientRegistrationService.getExistingPublisherAPIs();

		return resultApp;
	}

	/**
	 * Creates a API in WSO2-APIM
	 * 
	 * @param publisherEndpointConfig
	 *            - Part of the APIMConfig, which defines the configurations
	 *            parameters for publisher Endpoint, see apim-integration.xml
	 * @param apiDTO
	 *            - Instance of APIDTO, which carries the information of api
	 *            creation request
	 * @param accessToken
	 *            - OAuth bearer token, required to invoke the (OAuth secured)
	 *            publisher-apis.
	 * @return - Instance of APIDTO, which contains the information of created
	 *         API
	 * @throws APIMIntegrationException
	 */
	public PublisherAPIDTO createAPI(PublisherEndpointConfig publisherEndpointConfig, PublisherAPIDTO apiDTO, String accessToken) throws APIMIntegrationException {

		APIMClientInterface dynamicClientRegistrationService = Feign.builder().client(new TrustedFeignClient()).contract(new JAXRSContract()).encoder(new GsonEncoder())
				.decoder(new GsonDecoder()).errorDecoder(new APIMErrorDecoder()).requestInterceptor(new AuthBearerRequestInterceptor(accessToken))
				.target(APIMClientInterface.class, publisherEndpointConfig.getUrl());

		PublisherAPIDTO apiCreationResult = dynamicClientRegistrationService.createAPI(apiDTO);
		return apiCreationResult;
	}

	/**
	 * Publishes a created api (specified by the apiID) to APIM
	 * 
	 * @param publisherEndpointConfig
	 *            - Part of the APIMConfig, which defines the configurations
	 *            parameters for publisher Endpoint, see apim-integration.xml
	 * @param apiID
	 *            - The unique ID which represents the API in WSO2-APIM
	 * @param accessToken
	 *            - OAuth bearer token, required to invoke the (OAuth secured)
	 *            publisher-apis.
	 * @return - True if publishing is successful, false otherwise
	 * @throws APIMIntegrationException
	 */
	public boolean publishAPI(PublisherEndpointConfig publisherEndpointConfig, String apiID, String accessToken) throws APIMIntegrationException {

		APIMClientInterface dynamicClientRegistrationService = Feign.builder().client(new TrustedFeignClient()).contract(new JAXRSContract()).encoder(new GsonEncoder())
				.errorDecoder(new APIMErrorDecoder()).requestInterceptor(new AuthBearerRequestInterceptor(accessToken))
				.target(APIMClientInterface.class, publisherEndpointConfig.getUrl());

		Response apiPublishResult = dynamicClientRegistrationService.publishAPI(apiID, "Publish");
		if (apiPublishResult.status() == 200) {
			return true;
		}
		return false;
	}

	/**
	 * Searches available applications in WSO2-APIM, which then can be used to
	 * subscribe api's
	 * 
	 * @param storeEndpointConfig
	 *            - Part of the APIMConfig, which defines the configurations
	 *            parameters for Store Endpoint, see apim-integration.xml
	 * @param accessToken
	 *            - OAuth bearer token, required to invoke the (OAuth secured)
	 *            store-apis.
	 * @return - An instance of APIMApplicationListDTO, which contains a list of
	 *         APIMApplicationDTO objects corresponds to existing apllications.
	 * @throws APIMIntegrationException
	 *             -
	 */
	public APIMApplicationListDTO searchAPIMApplications(StoreEndpointConfig storeEndpointConfig, String accessToken) throws APIMIntegrationException {
		APIMClientInterface dynamicClientRegistrationService = Feign.builder().client(new TrustedFeignClient()).contract(new JAXRSContract()).decoder(new GsonDecoder())
				.errorDecoder(new APIMErrorDecoder()).requestInterceptor(new AuthBearerRequestInterceptor(accessToken))
				.target(APIMClientInterface.class, storeEndpointConfig.getUrl());

		APIMApplicationListDTO resultApps = dynamicClientRegistrationService.getAPIMApplications();
		return resultApps;
	}

	/**
	 * Gets the details of APIM application (specified by the appId) in
	 * WSO2-APIM.
	 * 
	 * @param storeEndpointConfig
	 *            - Part of the APIMConfig, which defines the configurations
	 *            parameters for Store Endpoint, see apim-integration.xml
	 * @param accessToken
	 *            - OAuth bearer token, required to invoke the (OAuth secured)
	 *            store-apis.
	 * @return - An instance of APIMApplicationDTO, which contains the details
	 *         of application corresponds to appId.
	 * @throws APIMIntegrationException
	 *             - If api invocation goes wrong due to any reason.
	 */
	public APIMApplicationDTO getAPIMApplicationDetails(StoreEndpointConfig storeEndpointConfig, String accessToken, String appId) throws APIMIntegrationException {
		APIMClientInterface dynamicClientRegistrationService = Feign.builder().client(new TrustedFeignClient()).contract(new JAXRSContract()).decoder(new GsonDecoder())
				.errorDecoder(new APIMErrorDecoder()).requestInterceptor(new AuthBearerRequestInterceptor(accessToken))
				.target(APIMClientInterface.class, storeEndpointConfig.getUrl());

		APIMApplicationDTO resultApps = dynamicClientRegistrationService.getAPIMApplicationDetails(appId);
		return resultApps;
	}

	/**
	 * Creates a application in WSO2-APIM, which then can be used to subscribe
	 * api's
	 * 
	 * @param storeEndpointConfig
	 *            - Part of the APIMConfig, which defines the configurations
	 *            parameters for Store Endpoint, see apim-integration.xml
	 * @param requestApp
	 *            - An instance of APIMApplicationDTO, which carries the
	 *            information of application creation request
	 * @param accessToken
	 *            - OAuth bearer token, required to invoke the (OAuth secured)
	 *            store-apis.
	 * @return - An instance of APIMApplicationDTO, which contains the
	 *         information of the application created.
	 * @throws APIMIntegrationException
	 *             - If api invocation goes wrong due to any reason.
	 */
	public APIMApplicationDTO createAPIMApplication(StoreEndpointConfig storeEndpointConfig, APIMApplicationDTO requestApp, String accessToken) throws APIMIntegrationException {
		APIMClientInterface dynamicClientRegistrationService = Feign.builder().client(new TrustedFeignClient()).contract(new JAXRSContract()).encoder(new GsonEncoder())
				.decoder(new GsonDecoder()).errorDecoder(new APIMErrorDecoder()).requestInterceptor(new AuthBearerRequestInterceptor(accessToken))
				.target(APIMClientInterface.class, storeEndpointConfig.getUrl());

		APIMApplicationDTO resultApp = dynamicClientRegistrationService.createAPIMApplication(requestApp);
		return resultApp;
	}

	/**
	 * Get a list of of available APIs (from WSO2-APIM) qualifying under a given
	 * search query.
	 * 
	 * @param storeEndpointConfig
	 *            - Part of the APIMConfig, which defines the configurations
	 *            parameters for Store Endpoint, see apim-integration.xml
	 * @param query
	 *            - You can search in attributes by using an ":" modifier. Eg.
	 *            "tag:wso2" will match an API if the tag of the API is "wso2".
	 * @param accessToken
	 *            - OAuth bearer token, required to invoke the (OAuth secured)
	 *            store-apis.
	 * @return - An instance of SubscriptionListDTO, which contains the list of
	 *         apis
	 * @throws APIMIntegrationException
	 *             - If api invocation goes wrong due to any reason.
	 */
	public StoreAPIListDTO searchStoreAPIs(StoreEndpointConfig storeEndpointConfig, String searchQuery, String accessToken) throws APIMIntegrationException {
		APIMClientInterface dynamicClientRegistrationService = Feign.builder().client(new TrustedFeignClient()).contract(new JAXRSContract()).decoder(new GsonDecoder())
				.errorDecoder(new APIMErrorDecoder()).target(APIMClientInterface.class, storeEndpointConfig.getUrl());

		StoreAPIListDTO resultApp = dynamicClientRegistrationService.getExistingStoreAPIs(searchQuery);
		return resultApp;
	}

	/**
	 * Gets existing subscriptions for a api (specified by apiId).
	 * 
	 * @param storeEndpointConfig
	 *            - Part of the APIMConfig, which defines the configurations
	 *            parameters for Store Endpoint, see apim-integration.xml
	 * @param accessToken
	 *            - OAuth bearer token, required to invoke the (OAuth secured)
	 *            store-apis.
	 * @return - An instance of SubscriptionListDTO, which contains the list of
	 *         subscriptions of the requested api.
	 * @throws APIMIntegrationException
	 *             - If api invocation goes wrong due to any reason.
	 */
	public SubscriptionListDTO getExistingSubscriptions(StoreEndpointConfig storeEndpointConfig, String accessToken, String apiId) throws APIMIntegrationException {
		APIMClientInterface dynamicClientRegistrationService = Feign.builder().client(new TrustedFeignClient()).contract(new JAXRSContract()).decoder(new GsonDecoder())
				.errorDecoder(new APIMErrorDecoder()).requestInterceptor(new AuthBearerRequestInterceptor(accessToken))
				.target(APIMClientInterface.class, storeEndpointConfig.getUrl());

		SubscriptionListDTO subscriptionResult = dynamicClientRegistrationService.getExistingSubscriptions(apiId);
		return subscriptionResult;
	}

	/**
	 * Subscirbe an API to an Application providing the id of the API and the
	 * application.
	 * 
	 * @param storeEndpointConfig
	 *            - Part of the APIMConfig, which defines the configurations
	 *            parameters for Store Endpoint, see apim-integration.xml
	 * @param subscriptionRequest
	 *            - An instance of SubscriptionDTO which contains the API id and
	 *            the Application id.
	 * @param accessToken
	 *            - OAuth bearer token, required to invoke the (OAuth secured)
	 *            store-apis.
	 * @return - An instance of SubscriptionDTO, which contains the information
	 *         of the subscription details.
	 * @throws APIMIntegrationException
	 *             - If api invocation goes wrong due to any reason.
	 */
	public SubscriptionDTO subscribeAPItoApp(StoreEndpointConfig storeEndpointConfig, SubscriptionDTO subscriptionRequest, String accessToken) throws APIMIntegrationException {
		APIMClientInterface dynamicClientRegistrationService = Feign.builder().client(new TrustedFeignClient()).contract(new JAXRSContract()).encoder(new GsonEncoder())
				.decoder(new GsonDecoder()).errorDecoder(new APIMErrorDecoder()).requestInterceptor(new AuthBearerRequestInterceptor(accessToken))
				.target(APIMClientInterface.class, storeEndpointConfig.getUrl());

		SubscriptionDTO subscriptionResult = dynamicClientRegistrationService.subscribeAPItoApp(subscriptionRequest);
		return subscriptionResult;
	}

	/**
	 * Generates keys (consumerKey, consumerSecret, accessToken) for an
	 * Application specified by the applicationId.
	 * 
	 * @param storeEndpointConfig
	 *            - Part of the APIMConfig, which defines the configurations
	 *            parameters for Store Endpoint, see apim-integration.xml
	 * @param keygenRequest
	 *            - An instance of ApplicationKeyGenRequestDTO which contains
	 *            information of key generation request.
	 * @param applicationId
	 *            - Unique Id of the APIM application (which needs to generate
	 *            the keys)
	 * @param accessToken
	 *            - OAuth bearer token, required to invoke the (OAuth secured)
	 *            store-apis.
	 * @return - An instance of ApplicationKeyDTO, which contains the
	 *         information of keys(consumerKey, consumerSecret, accessToken).
	 * @throws APIMIntegrationException
	 *             - If api invocation goes wrong due to any reason.
	 */
	public ApplicationKeyDTO generateKeysforApp(StoreEndpointConfig storeEndpointConfig, ApplicationKeyGenRequestDTO keygenRequest, String applicationId, String accessToken)
			throws APIMIntegrationException {
		APIMClientInterface dynamicClientRegistrationService = Feign.builder().client(new TrustedFeignClient()).contract(new JAXRSContract()).encoder(new GsonEncoder())
				.decoder(new GsonDecoder()).errorDecoder(new APIMErrorDecoder()).requestInterceptor(new AuthBearerRequestInterceptor(accessToken))
				.target(APIMClientInterface.class, storeEndpointConfig.getUrl());

		ApplicationKeyDTO appKeyResult = dynamicClientRegistrationService.generateKeysforApp(keygenRequest, applicationId);
		return appKeyResult;
	}
}
