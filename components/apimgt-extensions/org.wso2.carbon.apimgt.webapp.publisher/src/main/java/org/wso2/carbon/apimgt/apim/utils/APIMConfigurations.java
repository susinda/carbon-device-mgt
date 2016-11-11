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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.apimgt.webapp.publisher.config.WebappPublisherConfig;

import java.util.ArrayList;
import java.util.List;

public class APIMConfigurations {
    private static final Log log = LogFactory.getLog(APIMConfigurations.class);
    private static APIMConfigurations apimConfigurations;
    private List<APIMEnvironmentConfig> apiEnvironmentConfigList = new ArrayList<>();
    
    public static APIMConfigurations getInstance() {
        if (apimConfigurations == null) {
            apimConfigurations = new APIMConfigurations();
        }
        return apimConfigurations;
    }

    public List<APIMEnvironmentConfig> getApiEnvironmentConfigList() {
        return apiEnvironmentConfigList;
    }

    private APIMConfigurations() {
    	APIMEnvironmentConfig environmentConf = new APIMEnvironmentConfig();
    	WebappPublisherConfig webappPublisherConfig = WebappPublisherConfig.getInstance();
    	
    	environmentConf.setAuthUserName(webappPublisherConfig.getAuthUserName());
    	environmentConf.setAuthPassword(webappPublisherConfig.getAuthPassword());
    	environmentConf.setClientRegistrationEndpoint(webappPublisherConfig.getDCREndpoint());
    	environmentConf.setPublishingEndpoint(webappPublisherConfig.getPublisherEndpoint());
    	environmentConf.setTokenEndpoint(webappPublisherConfig.getTokenEndpoint());
    	apiEnvironmentConfigList.add(environmentConf);
    }

    
    public class APIMEnvironmentConfig {
        
        public String getPublishingEndpoint() {
            return publishingEndpoint;
        }

        public void setPublishingEndpoint(String publishingEndpoint) {
            this.publishingEndpoint = publishingEndpoint;
        }
        
        public String getAuthUserName() {
            return authUserName;
        }

        public void setAuthUserName(String authUserName) {
            this.authUserName = authUserName;
        }

        public String getAuthPassword() {
            return authPassword;
        }

        public void setAuthPassword(String authPassword) {
            this.authPassword = authPassword;
        }

        public String getClientRegistrationEndpoint() {
            return clientRegistrationEndpoint;
        }

        public void setClientRegistrationEndpoint(String clientRegistrationEndpoint) {
            this.clientRegistrationEndpoint = clientRegistrationEndpoint;
        }
        
        public String getTokenEndpoint() {
            return tokenEndpoint;
        }

        public void setTokenEndpoint(String tokenEndpoint) {
            this.tokenEndpoint = tokenEndpoint;
        }
        
        /*
        @SuppressWarnings("unused")
        public String getEnvironmentName() {
            return environmentName;
        }

        public void setEnvironmentName(String environmentName) {
            this.environmentName = environmentName;
        }
        */

        public String getCallBackURL() {
            return callBackURL;
        }

        public void setCallBackURL(String callBackURL) {
            this.callBackURL = callBackURL;
        }

        public String getClientName() {
            return clientName;
        }

        public void setClientName(String clientName) {
            this.clientName = clientName;
        }

        public String getTokenScope() {
            return tokenScope;
        }

        public void setTokenScope(String tokenScope) {
            this.tokenScope = tokenScope;
        }

        public String getClientOwner() {
            return clientOwner;
        }

        public void setClientOwner(String clientOwner) {
            this.clientOwner = clientOwner;
        }

        public String getGrantType() {
            return grantType;
        }

        public void setGrantType(String grantType) {
            this.grantType = grantType;
        }

        public String getIsSaasApp() {
            return isSaasApp;
        }

        public void setIsSaasApp(String isSaasApp) {
            this.isSaasApp = isSaasApp;
        }

        private String environmentName;
        private String tokenEndpoint;
        
        private String publishingEndpoint;
        private String authUserName;
        private String authPassword;

        // Client Registration related config
        private String clientRegistrationEndpoint;
        private String callBackURL;
        private String clientName;
        private String tokenScope;
        private String clientOwner;
        private String grantType;
        private String isSaasApp;
    }
}
