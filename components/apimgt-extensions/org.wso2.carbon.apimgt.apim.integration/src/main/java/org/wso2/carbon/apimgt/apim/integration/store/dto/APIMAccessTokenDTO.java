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

package org.wso2.carbon.apimgt.apim.integration.store.dto;

import java.util.List;

public class APIMAccessTokenDTO {

	private String accessToken;
	private List<String> tokenScopes;
	private Long validityTime;

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public List<String> getTokenScopes() {
		return tokenScopes;
	}

	public void setTokenScopes(List<String> tokenScopes) {
		this.tokenScopes = tokenScopes;
	}

	public Long getValidityTime() {
		return validityTime;
	}

	public void setValidityTime(Long validityTime) {
		this.validityTime = validityTime;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("class APIMAccessTokenDTO {\n");

		sb.append("  accessToken: ").append(accessToken).append("\n");
		sb.append("  tokenScopes: ").append(tokenScopes).append("\n");
		sb.append("  validityTime: ").append(validityTime).append("\n");
		sb.append("}\n");
		return sb.toString();
	}
}
