package org.wso2.carbon.apimgt.apim.integration.dto;

import javax.xml.bind.annotation.XmlElement;

public class PublisherEndpoint {

	private String url;
	
	@XmlElement(name = "URL", required = true)
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

}
