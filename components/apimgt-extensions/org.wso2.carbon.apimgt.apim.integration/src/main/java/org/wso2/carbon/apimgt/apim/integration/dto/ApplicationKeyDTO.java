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

public class ApplicationKeyDTO {

	private String consumerKey;
	private String consumerSecret;
	private List<String> supportedGrantTypes;
	private String keyState;
	private KeyTypeEnum keyType;
	private APIMAccessTokenDTO token;
	public enum KeyTypeEnum {
		PRODUCTION, SANDBOX,
	};


	public String getConsumerKey() {
		return consumerKey;
	}

	public void setConsumerKey(String consumerKey) {
		this.consumerKey = consumerKey;
	}

	public String getConsumerSecret() {
		return consumerSecret;
	}

	public void setConsumerSecret(String consumerSecret) {
		this.consumerSecret = consumerSecret;
	}

	public List<String> getSupportedGrantTypes() {
		return supportedGrantTypes;
	}

	public void setSupportedGrantTypes(List<String> supportedGrantTypes) {
		this.supportedGrantTypes = supportedGrantTypes;
	}

	public String getKeyState() {
		return keyState;
	}

	public void setKeyState(String keyState) {
		this.keyState = keyState;
	}

	public KeyTypeEnum getKeyType() {
		return keyType;
	}

	public void setKeyType(KeyTypeEnum keyType) {
		this.keyType = keyType;
	}

	public APIMAccessTokenDTO getToken() {
		return token;
	}

	public void setToken(APIMAccessTokenDTO token) {
		this.token = token;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("class ApplicationKeyDTO {\n");

		sb.append("  consumerKey: ").append(consumerKey).append("\n");
		sb.append("  consumerSecret: ").append(consumerSecret).append("\n");
		sb.append("  supportedGrantTypes: ").append(supportedGrantTypes).append("\n");
		sb.append("  keyState: ").append(keyState).append("\n");
		sb.append("  keyType: ").append(keyType).append("\n");
		sb.append("  token: ").append(token).append("\n");
		sb.append("}\n");
		return sb.toString();
	}
}
