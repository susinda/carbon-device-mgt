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

package org.wso2.carbon.apimgt.apim.integration.publisher;

import org.wso2.carbon.apimgt.apim.integration.common.APIMErrorDecoder;
import org.wso2.carbon.apimgt.apim.integration.common.APIMIntegrationException;
import org.wso2.carbon.apimgt.apim.integration.common.AuthBearerRequestInterceptor;
import org.wso2.carbon.apimgt.apim.integration.common.TrustedFeignClient;
import org.wso2.carbon.apimgt.apim.integration.common.configs.PublisherEndpointConfig;
import org.wso2.carbon.apimgt.apim.integration.publisher.dto.PublisherAPIDTO;
import org.wso2.carbon.apimgt.apim.integration.publisher.dto.PublisherAPIListDTO;

import feign.Feign;
import feign.Response;
import feign.gson.GsonDecoder;
import feign.gson.GsonEncoder;
import feign.jaxrs.JAXRSContract;

public class InternalPublisherClient {

	public PublisherAPIListDTO searchPublisherAPIs(PublisherEndpointConfig publisherEndpointConfig, String searchQuery,
			String accessToken) throws APIMIntegrationException {
		PublisherClientInterface clientInterface = Feign.builder()
				.client(new TrustedFeignClient())
				.contract(new JAXRSContract())
				.decoder(new GsonDecoder())
				.errorDecoder(new APIMErrorDecoder())
				.requestInterceptor(new AuthBearerRequestInterceptor(accessToken))
				.target(PublisherClientInterface.class, publisherEndpointConfig.getUrl());
		
		return clientInterface.getExistingPublisherAPIs();
	}

	public PublisherAPIDTO createAPI(PublisherEndpointConfig publisherEndpointConfig, PublisherAPIDTO apiDTO,
			String accessToken) throws APIMIntegrationException {

		PublisherClientInterface clientInterface = Feign.builder()
				.client(new TrustedFeignClient())
				.contract(new JAXRSContract())
				.encoder(new GsonEncoder())
				.decoder(new GsonDecoder())
				.errorDecoder(new APIMErrorDecoder())
				.requestInterceptor(new AuthBearerRequestInterceptor(accessToken))
				.target(PublisherClientInterface.class, publisherEndpointConfig.getUrl());

		return clientInterface.createAPI(apiDTO);
	}

	public boolean publishAPI(PublisherEndpointConfig publisherEndpointConfig, String apiID, String accessToken)
			throws APIMIntegrationException {

		PublisherClientInterface clientInterface = Feign.builder()
				.client(new TrustedFeignClient())
				.contract(new JAXRSContract())
				.encoder(new GsonEncoder())
				.errorDecoder(new APIMErrorDecoder())
				.requestInterceptor(new AuthBearerRequestInterceptor(accessToken))
				.target(PublisherClientInterface.class, publisherEndpointConfig.getUrl());

		Response apiPublishResult = clientInterface.publishAPI(apiID, "Publish");
		if (apiPublishResult.status() == 200) {
			return true;
		}
		return false;
	}

}