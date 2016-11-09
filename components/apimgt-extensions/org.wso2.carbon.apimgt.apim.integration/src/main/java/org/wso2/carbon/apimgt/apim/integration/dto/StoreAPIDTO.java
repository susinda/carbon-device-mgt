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

package org.wso2.carbon.apimgt.apim.integration.dto;

import java.util.List;

public class StoreAPIDTO {

	private String provider;
	private String version;
	private String description;
	private String status;
	private String name;
	private String context;
	private String id;
	private String apiDefinition;
	private String wsdlUri;
	private List<String> transport;
	private List<String> tags;
	private List<String> tiers;
	private String thumbnailUrl;
	private List<APIEndpointURLsDTO> endpointURLs;
    private APIBusinessInformationDTO businessInformation;
    private boolean isDefaultVersion;
    
	
	public String getProvider() {
		return provider;
	}

	public void setProvider(String provider) {
		this.provider = provider;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getContext() {
		return context;
	}

	public void setContext(String context) {
		this.context = context;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	
	public String getApiDefinition() {
		return apiDefinition;
	}

	public void setApiDefinition(String apiDefinition) {
		this.apiDefinition = apiDefinition;
	}

	public String getWsdlUri() {
		return wsdlUri;
	}

	public void setWsdlUri(String wsdlUri) {
		this.wsdlUri = wsdlUri;
	}

	public List<String> getTransport() {
		return transport;
	}

	public void setTransport(List<String> transport) {
		this.transport = transport;
	}

	public List<String> getTags() {
		return tags;
	}

	public void setTags(List<String> tags) {
		this.tags = tags;
	}

	public List<String> getTiers() {
		return tiers;
	}

	public void setTiers(List<String> tiers) {
		this.tiers = tiers;
	}

	public String getThumbnailUrl() {
		return thumbnailUrl;
	}

	public void setThumbnailUrl(String thumbnailUrl) {
		this.thumbnailUrl = thumbnailUrl;
	}

	public List<APIEndpointURLsDTO> getEndpointURLs() {
		return endpointURLs;
	}

	public void setEndpointURLs(List<APIEndpointURLsDTO> endpointURLs) {
		this.endpointURLs = endpointURLs;
	}

	public APIBusinessInformationDTO getBusinessInformation() {
		return businessInformation;
	}

	public void setBusinessInformation(APIBusinessInformationDTO businessInformation) {
		this.businessInformation = businessInformation;
	}

	public boolean isDefaultVersion() {
		return isDefaultVersion;
	}

	public void setDefaultVersion(boolean isDefaultVersion) {
		this.isDefaultVersion = isDefaultVersion;
	}
	
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("class APIDTO {\n");

		sb.append("  id: ").append(id).append("\n");
		sb.append("  name: ").append(name).append("\n");
		sb.append("  description: ").append(description).append("\n");
		sb.append("  context: ").append(context).append("\n");
		sb.append("  version: ").append(version).append("\n");
		sb.append("  provider: ").append(provider).append("\n");
		sb.append("  apiDefinition: ").append(apiDefinition).append("\n");
		sb.append("  wsdlUri: ").append(wsdlUri).append("\n");
		sb.append("  status: ").append(status).append("\n");
		sb.append("  isDefaultVersion: ").append(isDefaultVersion).append("\n");
		sb.append("  transport: ").append(transport).append("\n");
		sb.append("  tags: ").append(tags).append("\n");
		sb.append("  tiers: ").append(tiers).append("\n");
		sb.append("  thumbnailUrl: ").append(thumbnailUrl).append("\n");
		sb.append("  endpointURLs: ").append(endpointURLs).append("\n");
		sb.append("  businessInformation: ").append(businessInformation).append("\n");
		sb.append("}\n");
		return sb.toString();
	}
}
