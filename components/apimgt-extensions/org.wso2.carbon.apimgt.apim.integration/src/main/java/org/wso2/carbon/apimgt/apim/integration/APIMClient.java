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

package org.wso2.carbon.apimgt.apim.integration;

import org.wso2.carbon.apimgt.apim.integration.dto.APIDTO;
import org.wso2.carbon.apimgt.apim.integration.dto.APIMApplicationDTO;
import org.wso2.carbon.apimgt.apim.integration.dto.ApplicationKeyDTO;
import org.wso2.carbon.apimgt.apim.integration.dto.ApplicationKeyGenRequestDTO;
import org.wso2.carbon.apimgt.apim.integration.dto.DCREndpointConfig;
import org.wso2.carbon.apimgt.apim.integration.dto.OAuthApplicationDTO;
import org.wso2.carbon.apimgt.apim.integration.dto.PublisherEndpointConfig;
import org.wso2.carbon.apimgt.apim.integration.dto.SubscriptionListDTO;
import org.wso2.carbon.apimgt.apim.integration.dto.StoreEndpointConfig;
import org.wso2.carbon.apimgt.apim.integration.dto.SubscriptionDTO;
import org.wso2.carbon.apimgt.apim.integration.dto.TokenDTO;
import org.wso2.carbon.apimgt.apim.integration.dto.TokenEndpointConfig;
import org.wso2.carbon.apimgt.apim.integration.dto.TokenRequestDTO;
import org.wso2.carbon.apimgt.apim.integration.utils.AuthBearerRequestInterceptor;
import org.wso2.carbon.apimgt.apim.integration.utils.FeignClientUtil;

import feign.Feign;
import feign.Response;
import feign.auth.BasicAuthRequestInterceptor;
import feign.gson.GsonDecoder;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import feign.jaxrs.JAXRSContract;

/**
 * This is Http(s) client wrapper to communicate WSO2 API-Manager, REST APIs. 
 * This supports part of REST APIs defined in following locations
 * https://docs.wso2.com/display/AM200/apidocs/publisher
 * https://docs.wso2.com/display/AM200/apidocs/store
 * @author wso2
 *
 */
public class APIMClient {

	/**
	 * Creates a client application (client registration) in WSO2-APIM.
	 * @param dcrConfig - Part of the APIMConfig, which defines the configurations parameters for
	 * Dynamic Client Regiistration Endpoint, see apim-integration.xml 
	 * @return An instance of OAuthApplicationDTO which contains the information of the created client (registration)application.
	 */
	public OAuthApplicationDTO createOAuthApplication(DCREndpointConfig dcrConfig) {

		APIMRestClientService dynamicClientRegistrationService = Feign.builder()
				.client(FeignClientUtil.getCustomHostnameVerification())
				.contract(new JAXRSContract())
				.encoder(new JacksonEncoder())
				.decoder(new JacksonDecoder())
				.requestInterceptor(new BasicAuthRequestInterceptor(dcrConfig.getUserName(), dcrConfig.getPassword()))
				.target(APIMRestClientService.class, dcrConfig.getUrl());

		OAuthApplicationDTO oAuthApplication = dynamicClientRegistrationService.register(dcrConfig.getClientProfile());
		return oAuthApplication;
	}

	/**
	 * Gets a user token from WSO2-APIM, passing the client-application as an input
	 * @param tokenConfig - Part of the APIMConfig, which defines the configurations parameters for
	 * Token Endpoint, see apim-integration.xml
	 * @param oAuthApplication - Provides the consumerKey and consumerSecret
	 * @return - An instance of TokenDTO which contains the token information
	 */
	public TokenDTO getUserToken(TokenEndpointConfig tokenConfig, OAuthApplicationDTO oAuthApplication) {

		APIMRestClientService dynamicClientRegistrationService = Feign.builder()
				.client(FeignClientUtil.getCustomHostnameVerification())
				.contract(new JAXRSContract())
				.encoder(new JacksonEncoder())
				.decoder(new JacksonDecoder())
				.requestInterceptor(new BasicAuthRequestInterceptor(oAuthApplication.getClientId(), oAuthApplication.getClientSecret()))
				.target(APIMRestClientService.class, tokenConfig.getUrl());

		TokenRequestDTO tokenInfo = tokenConfig.getTokenInfo();
		TokenDTO token = dynamicClientRegistrationService.getToken(tokenInfo.getGrantType(), tokenInfo.getUserName(), tokenInfo.getPassword(), tokenInfo.getScope());
		return token;
	}
	
	/**
	 * Creates a API in WSO2-APIM
	 * @param publisherEndpointConfig - Part of the APIMConfig, which defines the configurations parameters for
	 * publisher Endpoint, see apim-integration.xml
	 * @param apiDTO - Instance of APIDTO, which carries the information of api creation request
	 * @param accessToken - OAuth bearer token, required to invoke the (OAuth secured) publisher-apis.
	 * @return - Instance of APIDTO, which contains the information of created API
	 */
	public APIDTO createAPI(PublisherEndpointConfig publisherEndpointConfig, APIDTO apiDTO, String accessToken) {

		APIMRestClientService dynamicClientRegistrationService = Feign.builder()
				.client(FeignClientUtil.getCustomHostnameVerification())
				.contract(new JAXRSContract())
				.encoder(new JacksonEncoder())
				.decoder(new GsonDecoder())
				.requestInterceptor(new AuthBearerRequestInterceptor(accessToken))
				.target(APIMRestClientService.class, publisherEndpointConfig.getUrl());

		APIDTO apiCreationResult = dynamicClientRegistrationService.createAPI(apiDTO);
		return apiCreationResult;
	}
	
	/**
	 * Publishes a created api (specified by the apiID) to APIM
	 * @param publisherEndpointConfig - Part of the APIMConfig, which defines the configurations parameters for
	 * publisher Endpoint, see apim-integration.xml
	 * @param apiID - The unique ID which represents the API in WSO2-APIM
	 * @param accessToken - OAuth bearer token, required to invoke the (OAuth secured) publisher-apis.
	 * @return - True if publishing is successful, false otherwise
	 */
	public boolean publishAPI(PublisherEndpointConfig publisherEndpointConfig, String apiID, String accessToken) {

		APIMRestClientService dynamicClientRegistrationService = Feign.builder()
				.client(FeignClientUtil.getCustomHostnameVerification())
				.contract(new JAXRSContract())
				.encoder(new JacksonEncoder())
				.requestInterceptor(new AuthBearerRequestInterceptor(accessToken))
				.target(APIMRestClientService.class, publisherEndpointConfig.getUrl());

		Response apiPublishResult = dynamicClientRegistrationService.publishAPI(apiID, "Publish");
		if (apiPublishResult.status() == 200) {
			return true;
		}
		return false;
	}

	/**
	 * Creates a application in WSO2-APIM, which then can be used to subscribe api's
	 * @param storeEndpointConfig - Part of the APIMConfig, which defines the configurations parameters for
	 * Store Endpoint, see apim-integration.xml
	 * @param requestApp - An instance of APIMApplicationDTO, which carries the information of application creation request
	 * @param accessToken - OAuth bearer token, required to invoke the (OAuth secured) store-apis.
	 * @return - An instance of APIMApplicationDTO, which contains the information of the application created.
	 */
	public APIMApplicationDTO createAPIMApplication(StoreEndpointConfig storeEndpointConfig, APIMApplicationDTO requestApp,  String accessToken) {
		APIMRestClientService dynamicClientRegistrationService = Feign.builder()
				.client(FeignClientUtil.getCustomHostnameVerification())
				.contract(new JAXRSContract())
				.encoder(new JacksonEncoder())
				.decoder(new GsonDecoder())
				.requestInterceptor(new AuthBearerRequestInterceptor(accessToken))
				.target(APIMRestClientService.class, storeEndpointConfig.getUrl());

		APIMApplicationDTO resultApp = dynamicClientRegistrationService.createAPIMApplication(requestApp);
		return resultApp;
	}
	
	/**
	 * Get a list of of available APIs (from WSO2-APIM) qualifying under a given search query. 
	 * @param storeEndpointConfig - Part of the APIMConfig, which defines the configurations parameters for
	 * Store Endpoint, see apim-integration.xml
	 * @param searchQuery - You can search in attributes by using an ":" modifier. Eg. "tag:wso2" will match an API if the tag of the API is "wso2".
	 * @param accessToken - OAuth bearer token, required to invoke the (OAuth secured) store-apis.
	 * @return - An instance of SubscriptionListDTO, which contains the list of apis
	 */
	public SubscriptionListDTO getAPIs(StoreEndpointConfig storeEndpointConfig, String searchQuery,  String accessToken) {
		APIMRestClientService dynamicClientRegistrationService = Feign.builder()
				.client(FeignClientUtil.getCustomHostnameVerification())
				.contract(new JAXRSContract())
				.encoder(new JacksonEncoder())
				.decoder(new GsonDecoder())
				.requestInterceptor(new AuthBearerRequestInterceptor(accessToken))
				.target(APIMRestClientService.class, storeEndpointConfig.getUrl());

		SubscriptionListDTO resultApp = dynamicClientRegistrationService.getAPIs(searchQuery);
		return resultApp;
	}
	
	/**
	 * Subscirbe an API to an Application providing the id of the API and the application. 
	 * @param storeEndpointConfig - Part of the APIMConfig, which defines the configurations parameters for
	 * Store Endpoint, see apim-integration.xml
	 * @param subscriptionRequest - An instance of SubscriptionDTO which contains the API id and the Application id.
	 * @param accessToken - OAuth bearer token, required to invoke the (OAuth secured) store-apis.
	 * @return - An instance of SubscriptionDTO, which contains the information of the subscription details.
	 */
	public SubscriptionDTO subscribeAPItoApp(StoreEndpointConfig storeEndpointConfig, SubscriptionDTO subscriptionRequest,  String accessToken) {
		APIMRestClientService dynamicClientRegistrationService = Feign.builder()
				.client(FeignClientUtil.getCustomHostnameVerification())
				.contract(new JAXRSContract())
				.encoder(new JacksonEncoder())
				.decoder(new GsonDecoder())
				.requestInterceptor(new AuthBearerRequestInterceptor(accessToken))
				.target(APIMRestClientService.class, storeEndpointConfig.getUrl());

		SubscriptionDTO subscriptionResult = dynamicClientRegistrationService.subscribeAPItoApp(subscriptionRequest);
		return subscriptionResult;
	}
	
	/**
	 * Generates keys (consumerKey, consumerSecret, accessToken) for an Application specified by the applicationId. 
	 * @param storeEndpointConfig - Part of the APIMConfig, which defines the configurations parameters for
	 * Store Endpoint, see apim-integration.xml
	 * @param keygenRequest - An instance of ApplicationKeyGenRequestDTO which contains information of key generation request.
	 * @param applicationId - Unique Id of the APIM application (which needs to generate the keys)
	 * @param accessToken - OAuth bearer token, required to invoke the (OAuth secured) store-apis.
	 * @return - An instance of ApplicationKeyDTO, which contains the information of keys(consumerKey, consumerSecret, accessToken).
	 */
	public ApplicationKeyDTO generateKeysforApp(StoreEndpointConfig storeEndpointConfig, ApplicationKeyGenRequestDTO keygenRequest, String applicationId, String accessToken) {
		APIMRestClientService dynamicClientRegistrationService = Feign.builder()
				.client(FeignClientUtil.getCustomHostnameVerification())
				.contract(new JAXRSContract())
				.encoder(new JacksonEncoder())
				.decoder(new GsonDecoder())
				.requestInterceptor(new AuthBearerRequestInterceptor(accessToken))
				.target(APIMRestClientService.class, storeEndpointConfig.getUrl());

		ApplicationKeyDTO appKeyResult = dynamicClientRegistrationService.generateKeysforApp(keygenRequest, applicationId);
		return appKeyResult;
	}

}
