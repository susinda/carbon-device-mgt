package org.wso2.carbon.apimgt.apim.integration;

import feign.RequestInterceptor;
import feign.RequestTemplate;

public class AuthBearerRequestInterceptor implements RequestInterceptor {
	private String accessToken;
	
	AuthBearerRequestInterceptor (String accessToken){
		this.accessToken = accessToken;
	}

	@Override
	public void apply(RequestTemplate template) {
		template.header("Authorization", "Bearer " + this.accessToken);
	}

}
