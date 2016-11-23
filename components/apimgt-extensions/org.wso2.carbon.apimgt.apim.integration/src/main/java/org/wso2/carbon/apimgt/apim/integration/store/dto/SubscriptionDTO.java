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

public class SubscriptionDTO {

	private String tier;
	private String apiIdentifier;
	private String applicationId;
	private String subscriptionId;
	private String status;

	public String getTier() {
		return tier;
	}

	public void setTier(String tier) {
		this.tier = tier;
	}

	public String getApiIdentifier() {
		return apiIdentifier;
	}

	public void setApiIdentifier(String apiIdentifier) {
		this.apiIdentifier = apiIdentifier;
	}

	public String getApplicationId() {
		return applicationId;
	}

	public void setApplicationId(String applicationId) {
		this.applicationId = applicationId;
	}

	public String getSubscriptionId() {
		return subscriptionId;
	}

	public void setSubscriptionId(String subscriptionId) {
		this.subscriptionId = subscriptionId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
	 @Override
	  public String toString()  {
	    StringBuilder sb = new StringBuilder();
	    sb.append("class SubscriptionDTO {\n");
	    
	    sb.append("  subscriptionId: ").append(subscriptionId).append("\n");
	    sb.append("  applicationId: ").append(applicationId).append("\n");
	    sb.append("  apiIdentifier: ").append(apiIdentifier).append("\n");
	    sb.append("  tier: ").append(tier).append("\n");
	    sb.append("  status: ").append(status).append("\n");
	    sb.append("}\n");
	    return sb.toString();
	  }

}
