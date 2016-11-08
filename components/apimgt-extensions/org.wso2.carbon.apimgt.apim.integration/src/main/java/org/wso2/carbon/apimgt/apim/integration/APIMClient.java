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
import org.wso2.carbon.apimgt.apim.integration.dto.DCREndpointConfig;
import org.wso2.carbon.apimgt.apim.integration.dto.OAuthApplication;
import org.wso2.carbon.apimgt.apim.integration.dto.PublisherEndpoint;
import org.wso2.carbon.apimgt.apim.integration.dto.Token;
import org.wso2.carbon.apimgt.apim.integration.dto.TokenEndpoint;
import org.wso2.carbon.apimgt.apim.integration.dto.TokenInfo;
import org.wso2.carbon.apimgt.apim.integration.utils.FeignClientUtil;

import com.google.gson.JsonObject;

import feign.Feign;
import feign.auth.BasicAuthRequestInterceptor;
import feign.gson.GsonDecoder;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import feign.jaxrs.JAXRSContract;

public class APIMClient {

	public OAuthApplication createOAuthApplication(DCREndpointConfig dcrConfig) {

		APIMRestClientService dynamicClientRegistrationService = Feign.builder()
				.client(FeignClientUtil.getCustomHostnameVerification())
				.contract(new JAXRSContract())
				.encoder(new JacksonEncoder())
				.decoder(new JacksonDecoder())
				.requestInterceptor(new BasicAuthRequestInterceptor(dcrConfig.getUserName(), dcrConfig.getPassword()))
				.target(APIMRestClientService.class, dcrConfig.getUrl());

		OAuthApplication oAuthApplication = dynamicClientRegistrationService.register(dcrConfig.getClientProfile());
		return oAuthApplication;
	}

	public Token getUserToken(TokenEndpoint tokenConfig, OAuthApplication oAuthApplication) {

		APIMRestClientService dynamicClientRegistrationService = Feign.builder()
				.client(FeignClientUtil.getCustomHostnameVerification())
				.contract(new JAXRSContract())
				.encoder(new JacksonEncoder())
				.decoder(new JacksonDecoder())
				.requestInterceptor(new BasicAuthRequestInterceptor(oAuthApplication.getClientId(), oAuthApplication.getClientSecret()))
				.target(APIMRestClientService.class, tokenConfig.getUrl());

		TokenInfo tokenInfo = tokenConfig.getTokenInfo();
		Token token = dynamicClientRegistrationService.getToken(tokenInfo.getGrantType(), tokenInfo.getUserName(), tokenInfo.getPassword(), tokenInfo.getScope());
		return token;
	}
	
	public JsonObject createAPI(PublisherEndpoint publisherEndpointConfig, APIDTO apiDTO, String accessToken) {

		APIMRestClientService dynamicClientRegistrationService = Feign.builder()
				.client(FeignClientUtil.getCustomHostnameVerification())
				.contract(new JAXRSContract())
				.encoder(new JacksonEncoder())
				.decoder(new GsonDecoder())
				.requestInterceptor(new AuthBearerRequestInterceptor(accessToken))
				.target(APIMRestClientService.class, publisherEndpointConfig.getUrl());

		JsonObject apiCreationResult = dynamicClientRegistrationService.createAPI(apiDTO);
		return apiCreationResult;
	}
	
	public JsonObject publishAPI(PublisherEndpoint publisherEndpointConfig, String apiID, String accessToken) {

		APIMRestClientService dynamicClientRegistrationService = Feign.builder()
				.client(FeignClientUtil.getCustomHostnameVerification())
				.contract(new JAXRSContract())
				.encoder(new JacksonEncoder())
				.decoder(new GsonDecoder())
				.requestInterceptor(new AuthBearerRequestInterceptor(accessToken))
				.target(APIMRestClientService.class, publisherEndpointConfig.getUrl());

		JsonObject apiPublishResult = dynamicClientRegistrationService.publishAPI(apiID, "Publish");
		return apiPublishResult;
	}

}
