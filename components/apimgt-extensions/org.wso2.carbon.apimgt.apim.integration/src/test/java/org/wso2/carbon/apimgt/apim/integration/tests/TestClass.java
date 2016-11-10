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

package org.wso2.carbon.apimgt.apim.integration.tests;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

import org.wso2.carbon.apimgt.api.APIManagementException;
import org.wso2.carbon.apimgt.apim.integration.APIMClient;
import org.wso2.carbon.apimgt.apim.integration.APIMConfigReader;
import org.wso2.carbon.apimgt.apim.integration.dto.APIDTO;
import org.wso2.carbon.apimgt.apim.integration.dto.APIMApplicationDTO;
import org.wso2.carbon.apimgt.apim.integration.dto.APIDTO.VisibilityEnum;
import org.wso2.carbon.apimgt.apim.integration.dto.APIMConfig;
import org.wso2.carbon.apimgt.apim.integration.dto.ApplicationKeyDTO;
import org.wso2.carbon.apimgt.apim.integration.dto.ApplicationKeyGenRequestDTO;
import org.wso2.carbon.apimgt.apim.integration.dto.OAuthApplicationDTO;
import org.wso2.carbon.apimgt.apim.integration.dto.SubscriptionListDTO;
import org.wso2.carbon.apimgt.apim.integration.dto.SubscriptionDTO;
import org.wso2.carbon.apimgt.apim.integration.dto.TokenDTO;


public class TestClass {

	public static void main(String[] args) {

		System.out.println("Testing org.wso2.carbon.apimgt.apim.integration");
		try {

			System.out.println("Reading the config file 'apim-integration.xml'");
			APIMConfig apimConfig = APIMConfigReader.getAPIMConfig();
			System.out.println(
					"Config file red sucessfully and apimConfig.getDcrEndpointConfig().getClientProfile().getClientName() = "
							+ apimConfig.getDcrEndpointConfig().getClientProfile().getClientName());

			APIMClient client = new APIMClient();
			OAuthApplicationDTO dcrApp = client.createOAuthApplication(apimConfig.getDcrEndpointConfig());
			System.out.println("Auth app created sucessfully, app.getClientSecret() = " + dcrApp.getClientSecret());

			
			TokenDTO token = client.getUserToken(apimConfig.getTokenEndpointConfig(), dcrApp);
			System.out.println("Token generated succesfully, token.getAccessToken() = " + token.getAccess_token());

			
			String fileString = "";
			try {
				fileString = new String(Files.readAllBytes(Paths.get("src/test/java/api6.json")));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			APIDTO api = new APIDTO();
			api.setTags(Arrays.asList("apple"));
			api.setName("Ausi34");
			api.setContext("/Ausi34");
			api.setVersion("1.0.1");
			api.setProvider("admin");
			api.setApiDefinition(fileString);
			api.setIsDefaultVersion(false);
			api.setTransport(Arrays.asList("http", "https"));
			api.setTiers(Arrays.asList("Unlimited"));
			api.setVisibility(VisibilityEnum.PUBLIC);
			api.setEndpointConfig("{\"production_endpoints\":{\"url\":\"https://localhost:9443/am/sample/pizzashack/v1/api/\",\"config\":null}, \"endpoint_type\":\"http\" }");
			api.setGatewayEnvironments("Production");
			//api.setResponseCaching("Disabled");
			//api.setDestinationStatsEnabled("false");
			
			APIDTO resultApiObject = client.createAPI(apimConfig.getPublisherEndpointConfig(), api, token.getAccess_token());
			System.out.println("API creation completed succesfully, api.Id = " + resultApiObject.getId());

			boolean response = client.publishAPI(apimConfig.getPublisherEndpointConfig(), resultApiObject.getId(), token.getAccess_token());
			System.out.println("API publish completed and result is = " + response);
			
			
			APIMApplicationDTO requestApp = new APIMApplicationDTO();
			requestApp.setName("AusiApp37");
			requestApp.setThrottlingTier("Unlimited");
			APIMApplicationDTO apimApp = client.createAPIMApplication(apimConfig.getStoreEndpointConfig(), requestApp, token.getAccess_token());
			System.out.println("API application creation successfull apimApp.getApplicationId() = " + apimApp.getApplicationId());
			
			SubscriptionListDTO apiList = client.getAPIs(apimConfig.getStoreEndpointConfig(), "tag:apple", token.getAccess_token());
			System.out.println("API list retrived apiList.count = " + apiList.getCount());
			
			SubscriptionDTO subscription = new SubscriptionDTO();
			subscription.setTier("Unlimited");
			subscription.setApplicationId(apimApp.getApplicationId());
			subscription.setApiIdentifier(resultApiObject.getId());
			SubscriptionDTO subscriptionResult = client.subscribeAPItoApp(apimConfig.getStoreEndpointConfig(), subscription, token.getAccess_token());
			System.out.println("API getSubscriptionId successfull subscriptionResult.getSubscriptionId() = " + subscriptionResult.getSubscriptionId());
			
			ApplicationKeyGenRequestDTO keygenRequest = new ApplicationKeyGenRequestDTO();
			keygenRequest.setKeyType(ApplicationKeyGenRequestDTO.KeyTypeEnum.PRODUCTION);
			keygenRequest.setValidityTime("3600");
			keygenRequest.setAccessAllowDomains(Arrays.asList("ALL"));
			ApplicationKeyDTO applicationKey = client.generateKeysforApp(apimConfig.getStoreEndpointConfig(), keygenRequest, apimApp.getApplicationId(), token.getAccess_token());
			System.out.println("API applicationKey generation successfull applicationKey.getToken().toString() = " + applicationKey.getToken().toString());
			
		} catch (APIManagementException e) {
			e.printStackTrace();
		}
	}

}
