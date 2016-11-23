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

package org.wso2.carbon.apimgt.apim.integration.publisher.dto;

import java.util.ArrayList;
import java.util.List;

import org.wso2.carbon.apimgt.apim.integration.store.dto.APIBusinessInformationDTO;
import org.wso2.carbon.apimgt.apim.integration.store.dto.APICorsConfigurationDTO;
import org.wso2.carbon.apimgt.apim.integration.store.dto.APIEndpointSecurityDTO;
import org.wso2.carbon.apimgt.apim.integration.store.dto.APIMaxTpsDTO;
import org.wso2.carbon.apimgt.apim.integration.store.dto.SequenceDTO;

public class PublisherAPIDTO {

	private String id;
	private String name;
	private String description;
	private String context;
	private String version;
	private String provider;
	private String apiDefinition;
	private String wsdlUri;
	private String status;
	private String responseCaching;
	private Integer cacheTimeout;
	private String destinationStatsEnabled;
	private Boolean isDefaultVersion;
	private List<String> transport = new ArrayList<String>();
	private List<String> tags = new ArrayList<String>();
	private List<String> tiers = new ArrayList<String>();
	private APIMaxTpsDTO maxTps;
	private String thumbnailUri;

	public enum VisibilityEnum {
		PUBLIC, PRIVATE, RESTRICTED, CONTROLLED,
	};

	private VisibilityEnum visibility;
	private List<String> visibleRoles = new ArrayList<String>();
	private List<String> visibleTenants = new ArrayList<String>();
	private String endpointConfig;
	private APIEndpointSecurityDTO endpointSecurity;
	private String gatewayEnvironments;
	private List<SequenceDTO> sequences = new ArrayList<SequenceDTO>();

	public enum SubscriptionAvailabilityEnum {
		current_tenant, all_tenants, specific_tenants,
	};

	private SubscriptionAvailabilityEnum subscriptionAvailability;
	private List<String> subscriptionAvailableTenants = new ArrayList<String>();
	private APIBusinessInformationDTO businessInformation;
	private APICorsConfigurationDTO corsConfiguration;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getContext() {
		return context;
	}

	public void setContext(String context) {
		this.context = context;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getProvider() {
		return provider;
	}

	public void setProvider(String provider) {
		this.provider = provider;
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

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getResponseCaching() {
		return responseCaching;
	}

	public void setResponseCaching(String responseCaching) {
		this.responseCaching = responseCaching;
	}

	public Integer getCacheTimeout() {
		return cacheTimeout;
	}

	public void setCacheTimeout(Integer cacheTimeout) {
		this.cacheTimeout = cacheTimeout;
	}

	public String getDestinationStatsEnabled() {
		return destinationStatsEnabled;
	}

	public void setDestinationStatsEnabled(String destinationStatsEnabled) {
		this.destinationStatsEnabled = destinationStatsEnabled;
	}

	public Boolean getIsDefaultVersion() {
		return isDefaultVersion;
	}

	public void setIsDefaultVersion(Boolean isDefaultVersion) {
		this.isDefaultVersion = isDefaultVersion;
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

	public APIMaxTpsDTO getMaxTps() {
		return maxTps;
	}

	public void setMaxTps(APIMaxTpsDTO maxTps) {
		this.maxTps = maxTps;
	}

	public String getThumbnailUri() {
		return thumbnailUri;
	}

	public void setThumbnailUri(String thumbnailUri) {
		this.thumbnailUri = thumbnailUri;
	}

	public VisibilityEnum getVisibility() {
		return visibility;
	}

	public void setVisibility(VisibilityEnum visibility) {
		this.visibility = visibility;
	}

	public List<String> getVisibleRoles() {
		return visibleRoles;
	}

	public void setVisibleRoles(List<String> visibleRoles) {
		this.visibleRoles = visibleRoles;
	}

	public List<String> getVisibleTenants() {
		return visibleTenants;
	}

	public void setVisibleTenants(List<String> visibleTenants) {
		this.visibleTenants = visibleTenants;
	}

	public String getEndpointConfig() {
		return endpointConfig;
	}

	public void setEndpointConfig(String endpointConfig) {
		this.endpointConfig = endpointConfig;
	}

	public APIEndpointSecurityDTO getEndpointSecurity() {
		return endpointSecurity;
	}

	public void setEndpointSecurity(APIEndpointSecurityDTO endpointSecurity) {
		this.endpointSecurity = endpointSecurity;
	}

	public List<SequenceDTO> getSequences() {
		return sequences;
	}

	public void setSequences(List<SequenceDTO> sequences) {
		this.sequences = sequences;
	}

	public String getGatewayEnvironments() {
		return gatewayEnvironments;
	}

	public void setGatewayEnvironments(String gatewayEnvironments) {
		this.gatewayEnvironments = gatewayEnvironments;
	}

	public SubscriptionAvailabilityEnum getSubscriptionAvailability() {
		return subscriptionAvailability;
	}

	public void setSubscriptionAvailability(SubscriptionAvailabilityEnum subscriptionAvailability) {
		this.subscriptionAvailability = subscriptionAvailability;
	}

	public List<String> getSubscriptionAvailableTenants() {
		return subscriptionAvailableTenants;
	}

	public void setSubscriptionAvailableTenants(List<String> subscriptionAvailableTenants) {
		this.subscriptionAvailableTenants = subscriptionAvailableTenants;
	}

	public APIBusinessInformationDTO getBusinessInformation() {
		return businessInformation;
	}

	public void setBusinessInformation(APIBusinessInformationDTO businessInformation) {
		this.businessInformation = businessInformation;
	}

	public APICorsConfigurationDTO getCorsConfiguration() {
		return corsConfiguration;
	}

	public void setCorsConfiguration(APICorsConfigurationDTO corsConfiguration) {
		this.corsConfiguration = corsConfiguration;
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
		sb.append("  responseCaching: ").append(responseCaching).append("\n");
		sb.append("  cacheTimeout: ").append(cacheTimeout).append("\n");
		sb.append("  destinationStatsEnabled: ").append(destinationStatsEnabled).append("\n");
		sb.append("  isDefaultVersion: ").append(isDefaultVersion).append("\n");
		sb.append("  transport: ").append(transport).append("\n");
		sb.append("  tags: ").append(tags).append("\n");
		sb.append("  tiers: ").append(tiers).append("\n");
		sb.append("  maxTps: ").append(maxTps).append("\n");
		sb.append("  thumbnailUri: ").append(thumbnailUri).append("\n");
		sb.append("  visibility: ").append(visibility).append("\n");
		sb.append("  visibleRoles: ").append(visibleRoles).append("\n");
		sb.append("  visibleTenants: ").append(visibleTenants).append("\n");
		sb.append("  endpointConfig: ").append(endpointConfig).append("\n");
		sb.append("  endpointSecurity: ").append(endpointSecurity).append("\n");
		sb.append("  gatewayEnvironments: ").append(gatewayEnvironments).append("\n");
		sb.append("  sequences: ").append(sequences).append("\n");
		sb.append("  subscriptionAvailability: ").append(subscriptionAvailability).append("\n");
		sb.append("  subscriptionAvailableTenants: ").append(subscriptionAvailableTenants).append("\n");
		sb.append("  businessInformation: ").append(businessInformation).append("\n");
		sb.append("  corsConfiguration: ").append(corsConfiguration).append("\n");
		sb.append("}\n");
		return sb.toString();
	}
}
