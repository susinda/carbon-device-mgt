/*
 * Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.wso2.carbon.apimgt.apim.utils;

import com.google.gson.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.apimgt.api.APIManagementException;
import org.wso2.carbon.apimgt.api.model.API;
import org.wso2.carbon.context.PrivilegedCarbonContext;
import org.wso2.carbon.utils.multitenancy.MultitenantUtils;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

public class APIMPublisherRestClient {
    private static final Log log = LogFactory.getLog(APIMPublisherRestClient.class);

    public APIMPublisherRestClient() {
    }

    public String createAPI(API apimAPI, APIMConfigurations.APIMEnvironmentConfig apiEnvironmentConfig) throws APIManagementException {
        
       log.debug("APIMPublisherRestClient createAPI ");
       String tenantDomain = MultitenantUtils.getTenantDomain(apimAPI.getApiOwner());
       PrivilegedCarbonContext.startTenantFlow();
       PrivilegedCarbonContext.getThreadLocalCarbonContext().setTenantDomain(tenantDomain, true);
    	
    	APIMDCRRestClient clientRegistration = APIMDCRRestClient.getInstance();

        JsonObject apiCreationRequestBody = new APIJsonSerializerImpl().serialize(apimAPI);

        Gson gson = new GsonBuilder().serializeNulls().setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE).create();

        //if(log.isDebugEnabled()) {
            log.debug("API creation payload = " + gson.toJson(apiCreationRequestBody));
        //}
        String apiCreationRequestBodyString = gson.toJson(apiCreationRequestBody);
        String apiCreationEp = apiEnvironmentConfig.getPublishingEndpoint();
        log.info("API creation apiCreationEp = " + apiCreationEp + "HEAders -> Bearer " + clientRegistration.getUserAccessToken());

        Map<String, String> requestHeaders = new HashMap<>();
        requestHeaders.put("Authorization", "Bearer " + clientRegistration.getUserAccessToken());
        requestHeaders.put("Content-Type", "application/json");

        HttpResponse apiCreationResponse;
        String apiId = null;
        int responseCode;

        try {
            apiCreationResponse = HttpRequestUtil.sendRequest(new URL(apiCreationEp), apiCreationRequestBodyString, requestHeaders, Constants.HTTP_METHOD_POST);

            responseCode = apiCreationResponse.getResponseCode();

            //if(log.isDebugEnabled()) {
                log.info("Response code for API creation in API-M = " + responseCode);
            //}

            // Unauthorized or token expired. Let's
            if(responseCode == 401) {
                clientRegistration.generateAccessToken(apiEnvironmentConfig);
                requestHeaders.put("Authorization", "Bearer " + clientRegistration.getUserAccessToken());
                apiCreationResponse = HttpRequestUtil.sendRequest(new URL(apiCreationEp), apiCreationRequestBodyString, requestHeaders, Constants.HTTP_METHOD_POST);

                responseCode = apiCreationResponse.getResponseCode();
                if(responseCode == 401) {
                    log.warn("Looks like your credentials are invalid. Please check your credentials before trying again");
                    throw new APIManagementException("Invalid credential provided for the environment");
                }
            }

            Map clientRegistrationResponseMap = new Gson().fromJson(apiCreationResponse.getData(), Map.class);
            apiId = (String) clientRegistrationResponseMap.get("id");

            //if(log.isDebugEnabled()) {
                log.info("API Creation is successful in the environment and apiID= " + apiId);
            //}
        } catch(MalformedURLException | APIManagementException e) {
            log.error("An error occurred in the send request while creating the API in API Manager", e);
        } finally {
            PrivilegedCarbonContext.endTenantFlow();
        }

        publishAPI("Created", "Published", apiId, apiEnvironmentConfig);
        return apiId;
    }

    public void publishAPI(String currentState, String targetState, String apiUUID,
                              APIMConfigurations.APIMEnvironmentConfig apiEnvironmentConfig) throws APIManagementException {
        
    	APIMDCRRestClient clientRegistration = APIMDCRRestClient.getInstance();
        String changeAPIStatusEp = apiEnvironmentConfig.getPublishingEndpoint() + "/change-lifecycle";

        Map<String, String> requestHeaders = new HashMap<>();
        requestHeaders.put("Authorization", "Bearer " + clientRegistration.getUserAccessToken());
        requestHeaders.put("Content-Type", "application/json");

        String action = "";

        switch(targetState) {
            case "Created":
                action = Constants.DEMOTE_TO_CREATED;
                break;
            case "Prototyped":
                if(currentState.equals("Published")) {
                    action = Constants.DEMOTE_TO_PROTOTYPED;
                } else {
                    action = Constants.DEPLOY_AS_A_PROTOTYPE;
                }
                break;
            case "Published":
                if(currentState.equals("Blocked")) {
                    action = Constants.REPUBLISH;
                } else {
                    action = Constants.PUBLISH;
                }
                break;
            case "Blocked":
                action = Constants.BLOCK;
                break;
            case "Deprecated":
                action = Constants.DEPRECATE;
                break;
            case "Retired":
                action = Constants.RETIRE;
                break;
        }

        changeAPIStatusEp = changeAPIStatusEp + "?apiId=" + apiUUID + "&action=" + action;
        HttpResponse apiPublishResponse;

        try {
            apiPublishResponse = HttpRequestUtil.sendRequest(new URL(changeAPIStatusEp), "", requestHeaders, Constants.HTTP_METHOD_POST);
            int responseCode = apiPublishResponse.getResponseCode();

            if(log.isDebugEnabled()) {
                log.debug("Response code of publishing the API = " + responseCode);
            }

            // Unauthorized or token expired. Let's
            if(responseCode == 401) {
                clientRegistration.generateAccessToken(apiEnvironmentConfig);
                requestHeaders.put("Authorization", "Bearer " + clientRegistration.getUserAccessToken());
                apiPublishResponse = HttpRequestUtil.sendRequest(new URL(changeAPIStatusEp), "", requestHeaders, Constants.HTTP_METHOD_POST);

                responseCode = apiPublishResponse.getResponseCode();
                if(responseCode == 401) {
                    log.warn("Looks like your credentials are invalid. Please check your credentials before trying again");
                    throw new APIManagementException("Invalid credential provided");
                }
            } else if(responseCode == 404) {
                throw new APIManagementException("Publishing API doesn't exist in the API Manager.");
            }

            if(log.isDebugEnabled()) {
                log.debug("Response code of publishing the API = " + responseCode);
            }
        } catch(MalformedURLException | APIManagementException e) {
            log.error("An error occurred while publishing the API  Check your endpoint address and whether the API has any resources", e);
            throw new APIManagementException(e.getMessage());
        }
    }

    public String updateAPI(API apimAPI, String apiUUID, APIMConfigurations.APIMEnvironmentConfig apiEnvironmentConfig) throws APIManagementException {
        APIMDCRRestClient clientRegistration = APIMDCRRestClient.getInstance();

        JsonObject apiCreationPayload = new APIJsonSerializerImpl().serialize(apimAPI);

        apiCreationPayload.addProperty("id", apiUUID);

        if(apimAPI.getThumbnailUrl() != null) {
            apiCreationPayload.addProperty("thumbnailUrl", apimAPI.getThumbnailUrl());
        }

        Gson gson = new GsonBuilder().serializeNulls().setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE).create();

        if(log.isDebugEnabled()) {
            log.debug("API update payload = " + gson.toJson(apiCreationPayload));
        }
        String apiUpdateRequestBodyString = gson.toJson(apiCreationPayload);

        String apiCreationEp = apiEnvironmentConfig.getPublishingEndpoint() + apiUUID;

        Map<String, String> requestHeaders = new HashMap<>();
        requestHeaders.put("Authorization", "Bearer " + clientRegistration.getUserAccessToken());
        requestHeaders.put("Content-Type", "application/json");

        HttpResponse apiCreationResponse;
        String apiId = null;
        int responseCode;

        try {
            apiCreationResponse = HttpRequestUtil.sendRequest(new URL(apiCreationEp), apiUpdateRequestBodyString, requestHeaders, Constants.HTTP_METHOD_PUT);

            responseCode = apiCreationResponse.getResponseCode();

            if(log.isDebugEnabled()) {
                log.debug("Response code of update API = " + responseCode);
            }

            // Unauthorized or token expired. Let's
            if(responseCode == 401) {
                clientRegistration.generateAccessToken(apiEnvironmentConfig);
                requestHeaders.put("Authorization", "Bearer " + clientRegistration.getUserAccessToken());
                apiCreationResponse = HttpRequestUtil.sendRequest(new URL(apiCreationEp), apiUpdateRequestBodyString, requestHeaders, Constants.HTTP_METHOD_PUT);

                responseCode = apiCreationResponse.getResponseCode();
                if(responseCode == 401) {
                    log.warn("Looks like your credentials are invalid. Please check your credentials before trying again");
                    throw new APIManagementException("Invalid credential provided for the environment");
                }
            } else if(responseCode == 404) {
                throw new APIManagementException("Updating API doesn't exist in the API Manager.");
            }

            Map clientRegistrationResponseMap = new Gson().fromJson(apiCreationResponse.getData(), Map.class);

            apiId = (String) clientRegistrationResponseMap.get("id");

            if(log.isDebugEnabled()) {
                log.debug("API Update successful in the environment");
            }
        } catch(MalformedURLException | APIManagementException e) {
            log.error("An error occurred in the send request while updating the API in API Manager", e);
        }

        return apiId;
    }

    public void deleteAPI(String apiUUID, APIMConfigurations.APIMEnvironmentConfig apiEnvironmentConfig) {
        APIMDCRRestClient clientRegistration = APIMDCRRestClient.getInstance();

        String deleteAPIEp = apiEnvironmentConfig.getPublishingEndpoint();
        Map<String, String> requestHeaders = new HashMap<>();
        requestHeaders.put("Authorization", "Bearer " + clientRegistration.getUserAccessToken());
        requestHeaders.put("Content-Type", "application/json");

        deleteAPIEp = deleteAPIEp + apiUUID;

        HttpResponse apiDeleteResponse;

        try {
            apiDeleteResponse = HttpRequestUtil.sendRequest(new URL(deleteAPIEp), "", requestHeaders, Constants.HTTP_METHOD_DELETE);
            int responseCode = apiDeleteResponse.getResponseCode();

            if(log.isDebugEnabled()) {
                log.debug("Response code of deleting the API = " + responseCode);
            }

            // Unauthorized or token expired. Let's
            if(responseCode == 401) {
                clientRegistration.generateAccessToken(apiEnvironmentConfig);
                requestHeaders.put("Authorization", "Bearer " + clientRegistration.getUserAccessToken());
                apiDeleteResponse = HttpRequestUtil.sendRequest(new URL(deleteAPIEp), "", requestHeaders, Constants.HTTP_METHOD_DELETE);

                responseCode = apiDeleteResponse.getResponseCode();
                if(responseCode == 401) {
                    log.warn("Looks like your credentials are invalid. Please check your credentials before trying again");
                    throw new APIManagementException("Invalid credential provided");
                }
            } else if(responseCode == 404) {
                throw new APIManagementException("Deleting API doesn't exist in the API Manager.");
            }

            if(log.isDebugEnabled()) {
                log.debug("Response code of API deletion = " + responseCode);
            }
        } catch(MalformedURLException | APIManagementException e) {
            log.error("An error occurred in the send request while changing the lc state", e);
        }
    }
}
    
    

