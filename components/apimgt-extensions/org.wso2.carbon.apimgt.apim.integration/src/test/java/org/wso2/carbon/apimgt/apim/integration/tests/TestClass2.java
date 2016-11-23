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
import org.wso2.carbon.apimgt.apim.integration.common.APIMConfigReader;
import org.wso2.carbon.apimgt.apim.integration.common.APIMIntegrationException;
import org.wso2.carbon.apimgt.apim.integration.common.configs.APIMConfig;
import org.wso2.carbon.apimgt.apim.integration.dcr.DcrClient;
import org.wso2.carbon.apimgt.apim.integration.publisher.PublisherClient;
import org.wso2.carbon.apimgt.apim.integration.publisher.dto.PublisherAPIDTO;
import org.wso2.carbon.apimgt.apim.integration.publisher.dto.PublisherAPIDTO.VisibilityEnum;
import org.wso2.carbon.apimgt.apim.integration.publisher.dto.PublisherAPIListDTO;
import org.wso2.carbon.apimgt.apim.integration.store.StoreClient;
import org.wso2.carbon.apimgt.apim.integration.store.dto.APIMApplicationDTO;
import org.wso2.carbon.apimgt.apim.integration.store.dto.ApplicationKeyDTO;
import org.wso2.carbon.apimgt.apim.integration.store.dto.ApplicationKeyGenRequestDTO;
import org.wso2.carbon.apimgt.apim.integration.store.dto.StoreAPIListDTO;
import org.wso2.carbon.apimgt.apim.integration.store.dto.SubscriptionDTO;

public class TestClass2 {

	public static void main(String[] args) {

		System.out.println("Testing org.wso2.carbon.apimgt.apim.integration");
		try {

			System.out.println("Reading the config file 'apim-integration.xml'");
			APIMConfig apimConfig = APIMConfigReader.getAPIMConfig("src/test/java/apim-integration.xml");
			System.out.println(
					"Config file red sucessfully and apimConfig.getDcrEndpointConfig().getClientProfile().getClientName() = "
							+ apimConfig.getDcrEndpointConfig().getClientProfile().getClientName());

			DcrClient dcrClient = new DcrClient(apimConfig);
			PublisherClient publisherClient = new PublisherClient(apimConfig);
			publisherClient.setDcrClient(dcrClient);
			StoreClient storeClient = new StoreClient(apimConfig);
			storeClient.setDcrClient(dcrClient);

			// Search publisher apis
			PublisherAPIListDTO publisherApiList = publisherClient.searchPublisherAPIs("tag:apple");
			System.out.println("publicher API list retrived apiList.count = " + publisherApiList.getCount());

			String fileString = new String(Files.readAllBytes(Paths.get("src/test/java/api6.json")));
			PublisherAPIDTO api = new PublisherAPIDTO();
			api.setTags(Arrays.asList("apple"));
			api.setName("Ausi480");
			api.setContext("/Ausi480");
			api.setVersion("1.0.1");
			api.setProvider("admin");
			api.setApiDefinition(fileString);
			api.setIsDefaultVersion(false);
			api.setTransport(Arrays.asList("http", "https"));
			api.setTiers(Arrays.asList("Unlimited"));
			api.setVisibility(VisibilityEnum.PUBLIC);
			api.setEndpointConfig(
					"{\"production_endpoints\":{\"url\":\"https://localhost:9443/am/sample/pizzashack/v1/api/\",\"config\":null}, \"endpoint_type\":\"http\" }");
			api.setGatewayEnvironments("Production");
			// create and publish new api in publisher
			PublisherAPIDTO createdAPIObj = publisherClient.createAndPublishAPIIfNotExists(api);

			// Search store apis
			StoreAPIListDTO storeApiList = storeClient.searchStoreAPIs("tag:apple");
			System.out.println("API list retrived apiList.count = " + storeApiList.getCount());

			String appName = "AusiApp480";
			APIMApplicationDTO requestApp = new APIMApplicationDTO();
			requestApp.setName(appName);
			requestApp.setThrottlingTier("Unlimited");
			// Create new app
			APIMApplicationDTO apimApp = storeClient.createAPIMApplicationIfNotExists(requestApp);

			ApplicationKeyGenRequestDTO keygenRequest = new ApplicationKeyGenRequestDTO();
			keygenRequest.setKeyType(ApplicationKeyGenRequestDTO.KeyTypeEnum.PRODUCTION);
			keygenRequest.setValidityTime("3600");
			keygenRequest.setAccessAllowDomains(Arrays.asList("ALL"));
			// generateKeys
			ApplicationKeyDTO applicationKey = storeClient.generateKeysforAppIfNotExists(keygenRequest, apimApp);

			SubscriptionDTO subscription = new SubscriptionDTO();
			subscription.setTier("Unlimited");
			subscription.setApplicationId(apimApp.getApplicationId());
			subscription.setApiIdentifier(createdAPIObj.getId());
			// subscribe
			storeClient.subscribeAPItoAppIfNotExists(subscription);

		} catch (APIMIntegrationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (APIManagementException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
