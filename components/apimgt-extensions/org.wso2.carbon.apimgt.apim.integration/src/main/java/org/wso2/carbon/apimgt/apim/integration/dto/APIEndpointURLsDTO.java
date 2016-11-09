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

public class APIEndpointURLsDTO {

	private APIEnvironmentURLsDTO environmentURLs = null;
	private String environmentName = null;
	private String environmentType = null;

	public APIEnvironmentURLsDTO getEnvironmentURLs() {
		return environmentURLs;
	}

	public void setEnvironmentURLs(APIEnvironmentURLsDTO environmentURLs) {
		this.environmentURLs = environmentURLs;
	}

	public String getEnvironmentName() {
		return environmentName;
	}

	public void setEnvironmentName(String environmentName) {
		this.environmentName = environmentName;
	}

	public String getEnvironmentType() {
		return environmentType;
	}

	public void setEnvironmentType(String environmentType) {
		this.environmentType = environmentType;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("class APIEndpointURLsDTO {\n");

		sb.append("  environmentURLs: ").append(environmentURLs).append("\n");
		sb.append("  environmentName: ").append(environmentName).append("\n");
		sb.append("  environmentType: ").append(environmentType).append("\n");
		sb.append("}\n");
		return sb.toString();
	}
}
