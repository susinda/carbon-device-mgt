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

public class APIMaxTpsDTO {
	
	private Long sandbox = null;
	private Long production = null;

	public Long getSandbox() {
		return sandbox;
	}

	public void setSandbox(Long sandbox) {
		this.sandbox = sandbox;
	}

	public Long getProduction() {
		return production;
	}

	public void setProduction(Long production) {
		this.production = production;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("class APIMaxTpsDTO {\n");

		sb.append("  sandbox: ").append(sandbox).append("\n");
		sb.append("  production: ").append(production).append("\n");
		sb.append("}\n");
		return sb.toString();
	}
}
