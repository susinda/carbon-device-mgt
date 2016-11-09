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

public class APIMClient {

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
	
	public ApplicationKeyDTO generateKeysforApp(StoreEndpointConfig storeEndpointConfig, ApplicationKeyGenRequestDTO keygenRequest, 
			String applicationId, String accessToken) {
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
