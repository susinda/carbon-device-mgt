package org.wso2.carbon.apimgt.apim.integration.dto;

import java.util.ArrayList;
import java.util.List;

public class APIDTO {

	private String id = null;

	private String name = null;

	private String description = null;

	private String context = null;

	private String version = null;

	private String provider = null;

	private String apiDefinition = null;

	private String wsdlUri = null;

	private String status = null;

	private Boolean isDefaultVersion = false;

	private List<String> transport = new ArrayList<String>();

	private List<String> tags = new ArrayList<String>();

	private List<String> tiers = new ArrayList<String>();

	private String thumbnailUrl = null;

	private String visibility;
	
	private String endpointConfig;

	//private List<APIEndpointURLsDTO> endpointURLs = new ArrayList<APIEndpointURLsDTO>();

	//private APIBusinessInformationDTO businessInformation = null;

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

	public String getThumbnailUrl() {
		return thumbnailUrl;
	}

	public void setThumbnailUrl(String thumbnailUrl) {
		this.thumbnailUrl = thumbnailUrl;
	}
	
	public String getVisibility() {
		return visibility;
	}
	
	public void setVisibility(String visibility) {
		this.visibility = visibility;
	}
	
	public String getEndpointConfig() {
		return endpointConfig;
	}

	public void setEndpointConfig(String endpointConfig) {
		this.endpointConfig = endpointConfig;
	}


//	public List<APIEndpointURLsDTO> getEndpointURLs() {
//		return endpointURLs;
//	}
//
//	public void setEndpointURLs(List<APIEndpointURLsDTO> endpointURLs) {
//		this.endpointURLs = endpointURLs;
//	}

	/*
	 * public APIBusinessInformationDTO getBusinessInformation() { return
	 * businessInformation; } public void
	 * setBusinessInformation(APIBusinessInformationDTO businessInformation) {
	 * this.businessInformation = businessInformation; }
	 */

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
		// sb.append(" endpointURLs: ").append(endpointURLs).append("\n");
		// sb.append(" businessInformation:
		// ").append(businessInformation).append("\n");
		sb.append("}\n");
		return sb.toString();
	}



}
