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

public class ApplicationKeyGenRequestDTO {

	public enum KeyTypeEnum {
	     PRODUCTION,  SANDBOX, 
	};
	private String validityTime;
	private KeyTypeEnum keyType;
	private List<String> accessAllowDomains;
	private String callbackUrl;
	private List<String> scopes;

	public String getValidityTime() {
		return validityTime;
	}

	public void setValidityTime(String validityTime) {
		this.validityTime = validityTime;
	}

	public KeyTypeEnum getKeyType() {
		return keyType;
	}

	public void setKeyType(KeyTypeEnum keyType) {
		this.keyType = keyType;
	}

	public List<String> getAccessAllowDomains() {
		return accessAllowDomains;
	}

	public void setAccessAllowDomains(List<String> accessAllowDomains) {
		this.accessAllowDomains = accessAllowDomains;
	}
	
	public String getCallbackUrl() {
		return callbackUrl;
	}

	public void setCallbackUrl(String callbackUrl) {
		this.callbackUrl = callbackUrl;
	}
	
	public List<String> getScopes() {
		return scopes;
	}

	public void setScopes(List<String> scopes) {
		this.scopes = scopes;
	}
	
	@Override
	  public String toString()  {
	    StringBuilder sb = new StringBuilder();
	    sb.append("class ApplicationKeyGenerateRequestDTO {\n");
	    
	    sb.append("  keyType: ").append(keyType).append("\n");
	    sb.append("  validityTime: ").append(validityTime).append("\n");
	    sb.append("  callbackUrl: ").append(callbackUrl).append("\n");
	    sb.append("  accessAllowDomains: ").append(accessAllowDomains).append("\n");
	    sb.append("  scopes: ").append(scopes).append("\n");
	    sb.append("}\n");
	    return sb.toString();
	  }
}
