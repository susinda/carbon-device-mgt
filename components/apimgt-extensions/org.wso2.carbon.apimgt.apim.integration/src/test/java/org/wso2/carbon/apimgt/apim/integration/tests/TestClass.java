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
import java.util.List;

import org.wso2.carbon.apimgt.api.APIManagementException;
import org.wso2.carbon.apimgt.apim.integration.common.APIMClient;
import org.wso2.carbon.apimgt.apim.integration.common.APIMConfigReader;
import org.wso2.carbon.apimgt.apim.integration.common.APIMIntegrationException;
import org.wso2.carbon.apimgt.apim.integration.common.configs.APIMConfig;
import org.wso2.carbon.apimgt.apim.integration.dcr.dto.OAuthApplicationDTO;
import org.wso2.carbon.apimgt.apim.integration.publisher.dto.PublisherAPIDTO;
import org.wso2.carbon.apimgt.apim.integration.dcr.dto.TokenDTO;
import org.wso2.carbon.apimgt.apim.integration.publisher.dto.PublisherAPIDTO.VisibilityEnum;
import org.wso2.carbon.apimgt.apim.integration.publisher.dto.PublisherAPIListDTO;
import org.wso2.carbon.apimgt.apim.integration.store.dto.APIMApplicationDTO;
import org.wso2.carbon.apimgt.apim.integration.store.dto.APIMApplicationListDTO;
import org.wso2.carbon.apimgt.apim.integration.store.dto.ApplicationKeyDTO;
import org.wso2.carbon.apimgt.apim.integration.store.dto.ApplicationKeyGenRequestDTO;
import org.wso2.carbon.apimgt.apim.integration.store.dto.StoreAPIListDTO;
import org.wso2.carbon.apimgt.apim.integration.store.dto.SubscriptionDTO;
import org.wso2.carbon.apimgt.apim.integration.store.dto.SubscriptionListDTO;

public class TestClass {

	public static void main(String[] args) {

		System.out.println("Testing org.wso2.carbon.apimgt.apim.integration");
		try {

			System.out.println("Reading the config file 'apim-integration.xml'");
			APIMConfig apimConfig = APIMConfigReader.getAPIMConfig("src/test/java/apim-integration.xml");
			System.out.println(
					"Config file red sucessfully and apimConfig.getDcrEndpointConfig().getClientProfile().getClientName() = "
							+ apimConfig.getDcrEndpointConfig().getClientProfile().getClientName());

			APIMClient client = new APIMClient();
			OAuthApplicationDTO dcrApp = client.createOAuthApplication(apimConfig.getDcrEndpointConfig());
			System.out.println("Auth app created sucessfully, app.getClientSecret() = " + dcrApp.getClientSecret());

			TokenDTO token = client.getUserToken(apimConfig.getTokenEndpointConfig(), dcrApp);
			System.out.println("Token generated succesfully, token.getAccessToken() = " + token.getAccess_token());

			TokenDTO token2 = client.renewUserToken(apimConfig.getTokenEndpointConfig(), dcrApp,
					token.getRefresh_token());
			System.out.println("Token RENEW succesfully, token2.getAccessToken() = " + token2.getAccess_token());
			token = token2;

			String fileString = new String(Files.readAllBytes(Paths.get("src/test/java/api6.json")));

			PublisherAPIDTO api = new PublisherAPIDTO();
			api.setTags(Arrays.asList("apple"));
			api.setName("Ausi380");
			api.setContext("/Ausi380");
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
			// api.setResponseCaching("Disabled");
			// api.setDestinationStatsEnabled("false");

			/*
			 * APIDTO resultApiObject =
			 * client.createAPI(apimConfig.getPublisherEndpointConfig(), api,
			 * token.getAccess_token()); System.out.println(
			 * "API creation completed succesfully, api.Id = " +
			 * resultApiObject.getId());
			 * 
			 * boolean response =
			 * client.publishAPI(apimConfig.getPublisherEndpointConfig(),
			 * resultApiObject.getId(), token.getAccess_token());
			 * System.out.println("API publish completed and result is = " +
			 * response);
			 */

			PublisherAPIDTO resultApiObject = null;
			PublisherAPIListDTO list = client.searchPublisherAPIs(apimConfig.getPublisherEndpointConfig(), "",
					token.getAccess_token());

			List<PublisherAPIDTO> apiLIst = list.getList();
			PublisherAPIDTO existingAPI = getExistingApi(api, apiLIst);
			if (existingAPI != null) {
				resultApiObject = existingAPI;
				System.out.println("API " + existingAPI.getName() + " apready exists, therefore not creating");
				if ("PUBLISHED".equals(existingAPI.getStatus())) {
					System.out.println(
							"API " + existingAPI.getName() + " apready in PUBLISHED state, therefore not publishing");
				} else {
					boolean publishResult = client.publishAPI(apimConfig.getPublisherEndpointConfig(),
							existingAPI.getId(), token.getAccess_token());
					System.out.println("API publish result " + publishResult);
				}
			} else {
				resultApiObject = client.createAPI(apimConfig.getPublisherEndpointConfig(), api,
						token.getAccess_token());
				System.out.println("API creation succesful : createdAPI " + resultApiObject.getName() + "  "
						+ resultApiObject.getId());
				boolean publishResult = client.publishAPI(apimConfig.getPublisherEndpointConfig(),
						resultApiObject.getId(), token.getAccess_token());
				System.out.println("API publish result " + publishResult);
			}

			String appName = "AusiApp380";
			APIMApplicationDTO requestApp = new APIMApplicationDTO();
			requestApp.setName(appName);
			requestApp.setThrottlingTier("Unlimited");
			APIMApplicationDTO apimApp = null;

			APIMApplicationListDTO appList = client.searchAPIMApplications(apimConfig.getStoreEndpointConfig(),
					token.getAccess_token());
			System.out
					.println("API application listing successfull appList.getApplicationId() = " + appList.getCount());
			List<APIMApplicationDTO> apimAppList = appList.getList();
			APIMApplicationDTO existingApp = getExistingApp(requestApp, apimAppList);
			if (existingApp != null) {
				System.out.println("Application " + existingApp.getName() + " apready exists, therefore not creating");
				APIMApplicationDTO existingApInfo = client.getAPIMApplicationDetails(
						apimConfig.getStoreEndpointConfig(), token.getAccess_token(), existingApp.getApplicationId());
				apimApp = existingApInfo;
			} else {
				apimApp = client.createAPIMApplication(apimConfig.getStoreEndpointConfig(), requestApp,
						token.getAccess_token());
				System.out.println("API application creation successfull apimApp.getApplicationId() = "
						+ apimApp.getApplicationId());
			}

			StoreAPIListDTO apiList = client.searchStoreAPIs(apimConfig.getStoreEndpointConfig(), "tag:apple",
					token.getAccess_token());
			System.out.println("API list retrived apiList.count = " + apiList.getCount());

			ApplicationKeyDTO applicationKey = getProductionKeyIfExists(apimApp);
			if (applicationKey != null) {
				System.out.println(
						"A PRODCUTION applicationKey is already exists therefore not cretating keys, applicationKey.getConsumerKey and secret "
								+ applicationKey.getConsumerKey() + "  " + applicationKey.getConsumerSecret());
			} else {
				ApplicationKeyGenRequestDTO keygenRequest = new ApplicationKeyGenRequestDTO();
				keygenRequest.setKeyType(ApplicationKeyGenRequestDTO.KeyTypeEnum.PRODUCTION);
				keygenRequest.setValidityTime("3600");
				keygenRequest.setAccessAllowDomains(Arrays.asList("ALL"));
				applicationKey = client.generateKeysforApp(apimConfig.getStoreEndpointConfig(), keygenRequest,
						apimApp.getApplicationId(), token.getAccess_token());
				System.out.println("API applicationKey gen OK, applicationKey.getConsumerKey and secret "
						+ applicationKey.getConsumerKey() + "  " + applicationKey.getConsumerSecret());
				System.out.println("API applicationKey generation successfull applicationKey.getToken().toString() = "
						+ applicationKey.getToken().toString());
			}

			SubscriptionDTO subscription = new SubscriptionDTO();
			subscription.setTier("Unlimited");
			subscription.setApplicationId(apimApp.getApplicationId());
			subscription.setApiIdentifier(resultApiObject.getId());

			SubscriptionListDTO existingSubscriptions = client.getExistingSubscriptions(
					apimConfig.getStoreEndpointConfig(), token.getAccess_token(), resultApiObject.getId());
			SubscriptionDTO availableSubscription = getExistingSubscription(existingSubscriptions, subscription);
			if (availableSubscription != null) {
				System.out.println("API subscription already exists for apiID " + resultApiObject.getId()
						+ " and appID " + apimApp.getApplicationId());
				System.out.println("API subscription already exists availableSubscription.getSubscriptionId() = "
						+ availableSubscription.getSubscriptionId());
			} else {
				SubscriptionDTO subscriptionResult = client.subscribeAPItoApp(apimConfig.getStoreEndpointConfig(),
						subscription, token.getAccess_token());
				System.out.println("API getSubscriptionId successfull subscriptionResult.getSubscriptionId() = "
						+ subscriptionResult.getSubscriptionId());
			}

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

	private static PublisherAPIDTO getExistingApi(PublisherAPIDTO apiDTO, List<PublisherAPIDTO> apiLIst) {
		for (PublisherAPIDTO api : apiLIst) {
			if (api.getContext().equals(apiDTO.getContext())) {
				return api;
			}
		}
		return null;
	}

	private static APIMApplicationDTO getExistingApp(APIMApplicationDTO application, List<APIMApplicationDTO> appList) {
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
				if (key.getKeyType().equals(ApplicationKeyDTO.KeyTypeEnum.PRODUCTION)) {
					return key;
				}
			}
			return null;
		}
	}

	private static SubscriptionDTO getExistingSubscription(SubscriptionListDTO existingSubscriptions,
			SubscriptionDTO subscription) {
		for (SubscriptionDTO apiSubscription : existingSubscriptions.getList()) {
			if (apiSubscription.getApplicationId().equals(subscription.getApplicationId())) {
				return apiSubscription;
			}
		}
		return null;
	}

}
