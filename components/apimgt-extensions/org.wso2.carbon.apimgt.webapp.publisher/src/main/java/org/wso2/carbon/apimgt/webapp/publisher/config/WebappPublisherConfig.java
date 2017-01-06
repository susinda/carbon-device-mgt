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
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.apimgt.webapp.publisher.config;

import org.w3c.dom.Document;
import org.wso2.carbon.apimgt.webapp.publisher.InvalidConfigurationStateException;
import org.wso2.carbon.apimgt.webapp.publisher.WebappPublisherConfigurationFailedException;
import org.wso2.carbon.apimgt.webapp.publisher.WebappPublisherUtil;
import org.wso2.carbon.utils.CarbonUtils;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.File;

/**
 * This class represents the configuration that are needed
 * when publishing APIs to API Manager.
 */
@XmlRootElement(name = "WebappPublisherConfigs")
public class WebappPublisherConfig {

    private String host;
    private String authUserName;
    private String authPassword;
    private String dcrEndpoint;
    private String tokenEndpoint;
    private String publisherEndpoint;
    private boolean isPublished;
    private boolean isEnabledUpdateApi;
    private Profiles profiles;

    private static WebappPublisherConfig config;

    private static final String WEBAPP_PUBLISHER_CONFIG_PATH =
            CarbonUtils.getEtcCarbonConfigDirPath() + File.separator + "webapp-helpers-config.xml";

    private WebappPublisherConfig() {
    }

    public static WebappPublisherConfig getInstance() {
        if (config == null) {
            throw new InvalidConfigurationStateException("Webapp Authenticator Configuration is not " +
                    "initialized properly");
        }
        return config;
    }

    @XmlElement(name = "Host", required = true)
    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }
    
    
    @XmlElement(name = "AuthUserName", required = true)
    public String getAuthUserName() {
        return this.authUserName;
    }

    public void setAuthUserName(String user) {
        this.authUserName = user;
    }

    @XmlElement(name = "AuthPassword", required = true)
    public String getAuthPassword() {
        return this.authPassword;
    }

    public void setAuthPassword(String pwd) {
        this.authPassword = pwd;
    }
    
    @XmlElement(name = "DCREndpoint", required = true)
    public String getDCREndpoint() {
        return this.dcrEndpoint;
    }

    public void setDCREndpoint(String dcrEp) {
        this.dcrEndpoint = dcrEp;
    }
    
    @XmlElement(name = "TokenEndpoint", required = true)
	public String getTokenEndpoint() {
		return this.tokenEndpoint;
	}
    
    public void setTokenEndpoint(String tokenEp) {
        this.tokenEndpoint = tokenEp;
    }
    
    @XmlElement(name = "PublisherEndpoint", required = true)
   	public String getPublisherEndpoint() {
   		return this.publisherEndpoint;
   	}
       
	public void setPublisherEndpoint(String pubEp) {
	    this.publisherEndpoint = pubEp;
	}

    
    @XmlElement(name = "PublishAPI", required = true)
    public boolean isPublished() {
        return isPublished;
    }

    @XmlElement(name = "Profiles", required = true)
    public Profiles getProfiles() {
        return profiles;
    }

    @XmlElement(name = "EnabledUpdateApi", required = true)
    public boolean isEnabledUpdateApi() {
        return isEnabledUpdateApi;
    }

    public void setEnabledUpdateApi(boolean isEnabledUpdateApi) {
        this.isEnabledUpdateApi = isEnabledUpdateApi;
    }

    public void setPublished(boolean published) {
        isPublished = published;
    }

    public void setProfiles(Profiles profiles) {
        this.profiles = profiles;
    }

    public static void init() throws WebappPublisherConfigurationFailedException {
        try {
            File emailSenderConfig = new File(WEBAPP_PUBLISHER_CONFIG_PATH);
            Document doc = WebappPublisherUtil.convertToDocument(emailSenderConfig);

            /* Un-marshaling Email Sender configuration */
            JAXBContext ctx = JAXBContext.newInstance(WebappPublisherConfig.class);
            Unmarshaller unmarshaller = ctx.createUnmarshaller();
            //unmarshaller.setSchema(getSchema());
            config = (WebappPublisherConfig) unmarshaller.unmarshal(doc);
        } catch (JAXBException e) {
            throw new WebappPublisherConfigurationFailedException("Error occurred while un-marshalling Webapp " +
                    "Publisher Config", e);
        }
    }

}
