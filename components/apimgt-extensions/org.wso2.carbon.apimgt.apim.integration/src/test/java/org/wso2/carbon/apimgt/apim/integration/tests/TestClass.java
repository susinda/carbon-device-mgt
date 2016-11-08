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

import java.util.Arrays;

import org.wso2.carbon.apimgt.api.APIManagementException;
import org.wso2.carbon.apimgt.apim.integration.APIMClient;
import org.wso2.carbon.apimgt.apim.integration.APIMConfigReader;
import org.wso2.carbon.apimgt.apim.integration.dto.APIDTO;
import org.wso2.carbon.apimgt.apim.integration.dto.APIMConfig;
import org.wso2.carbon.apimgt.apim.integration.dto.OAuthApplication;
import org.wso2.carbon.apimgt.apim.integration.dto.Token;

import com.google.gson.JsonObject;

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
			OAuthApplication app = client.createOAuthApplication(apimConfig.getDcrEndpointConfig());
			System.out.println("Auth app created sucessfully, app.getClientSecret() = " + app.getClientSecret());

			
			Token token = client.getUserToken(apimConfig.getTokenEndpointConfig(), app);
			System.out.println("Token generated succesfully, token.getAccessToken() = " + token.getAccess_token());

			
			APIDTO api = new APIDTO();
			api.setName("test13");
			api.setContext("/test13");
			api.setVersion("1.0");
			api.setProvider("admin");
			api.setTransport(Arrays.asList("http", "https"));
			api.setApiDefinition(
					"{\"paths\":{\"/order\":{\"post\":{\"x-auth-type\":\"Application & Application User\",\"x-throttling-tier\":\"Unlimited\",\"description\":\"Create a new Order\",\"parameters\":[{\"schema\":{\"$ref\":\"#/definitions/Order\"},\"description\":\"Order object that needs to be added\",\"name\":\"body\",\"required\":true,\"in\":\"body\"}],\"responses\":{\"201\":{\"headers\":{\"Location\":{\"description\":\"The URL of the newly created resource.\",\"type\":\"string\"}},\"schema\":{\"$ref\":\"#/definitions/Order\"},\"description\":\"Created.\"}}}},\"/menu\":{\"get\":{\"x-auth-type\":\"Application & Application User\",\"x-throttling-tier\":\"Unlimited\",\"description\":\"Return a list of available menu items\",\"parameters\":[],\"responses\":{\"200\":{\"headers\":{},\"schema\":{\"title\":\"Menu\",\"properties\":{\"list\":{\"items\":{\"$ref\":\"#/definitions/MenuItem\"},\"type\":\"array\"}},\"type\":\"object\"},\"description\":\"OK.\"}}}}},\"schemes\":[\"https\"],\"produces\":[\"application/json\"],\"swagger\":\"2.0\",\"definitions\":{\"MenuItem\":{\"title\":\"Pizza menu Item\",\"properties\":{\"price\":{\"type\":\"string\"},\"description\":{\"type\":\"string\"},\"name\":{\"type\":\"string\"},\"image\":{\"type\":\"string\"}},\"required\":[\"name\"]},\"Order\":{\"title\":\"Pizza Order\",\"properties\":{\"customerName\":{\"type\":\"string\"},\"delivered\":{\"type\":\"boolean\"},\"address\":{\"type\":\"string\"},\"pizzaType\":{\"type\":\"string\"},\"creditCardNumber\":{\"type\":\"string\"},\"quantity\":{\"type\":\"number\"},\"orderId\":{\"type\":\"integer\"}},\"required\":[\"orderId\"]}},\"consumes\":[\"application/json\"],\"info\":{\"title\":\"PizzaShackAPI\",\"description\":\"This document describe a RESTFul API for Pizza Shack online pizza delivery store.\\n\",\"license\":{\"name\":\"Apache 2.0\",\"url\":\"http://www.apache.org/licenses/LICENSE-2.0.html\"},\"contact\":{\"email\":\"architecture@pizzashack.com\",\"name\":\"John Doe\",\"url\":\"http://www.pizzashack.com\"},\"version\":\"1.0.0\"}}");
			api.setTiers(Arrays.asList("Gold"));
			api.setVisibility("PUBLIC");
			api.setEndpointConfig(
					"{\"production_endpoints\":{\"url\":\"https://localhost:9443/am/sample/pizzashack/v1/api/\",\"config\":null}, \"endpoint_type\":\"http\" ");

			
			JsonObject resultApiObject = client.createAPI(apimConfig.getPublisherEndpointConfig(), api, token.getAccess_token());
			System.out.println("API creation completed succesfully, api.Id = " + resultApiObject.getAsJsonPrimitive("id").toString().replace("\"", ""));

		
			String apiID = resultApiObject.getAsJsonPrimitive("id").toString().replace("\"", "");
			JsonObject publishResult = client.publishAPI(apimConfig.getPublisherEndpointConfig(), apiID, token.getAccess_token());
			System.out.println("API publish completed succesfully, api.Id = " + publishResult.toString());

		} catch (APIManagementException e) {
			e.printStackTrace();
		}
	}

}
