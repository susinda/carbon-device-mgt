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

import java.io.File;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;

import org.wso2.carbon.apimgt.api.APIManagementException;


public class APIMConfigReader {

	private static APIMConfig config;

	private static final String APIM_INTEGRATION_CONFIG_PATH = "src/test/java/apim-integration.xml";
	// CarbonUtils.getEtcCarbonConfigDirPath() + File.separator +
	// "api-publisher-config.xml";

	public synchronized static APIMConfig getAPIMConfig() throws APIManagementException {
		if (config == null) {
			init();
		}
		return config;
	}
 
	private static void init()  throws APIManagementException{
		try {
			File apimConfigFile = new File(APIM_INTEGRATION_CONFIG_PATH);
			Document doc = convertToDocument(apimConfigFile);

			JAXBContext ctx = JAXBContext.newInstance(APIMConfig.class);
			Unmarshaller unmarshaller = ctx.createUnmarshaller();
			config = (APIMConfig) unmarshaller.unmarshal(doc);
		} catch (JAXBException e) {
			throw new APIManagementException("Error occurred while un-marshalling Webapp " + "Publisher Config", e);
		}
	}

	private static Document convertToDocument(File file) throws APIManagementException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		try {
			factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
			DocumentBuilder docBuilder = factory.newDocumentBuilder();
			return docBuilder.parse(file);
		} catch (Exception e) {
			throw new APIManagementException(
					"Error occurred while parsing file, while converting " + "to a org.w3c.dom.Document", e);
		}
	}

}
