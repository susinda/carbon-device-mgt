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

public class APIBusinessInformationDTO {

	private String businessOwnerEmail = null;
	private String technicalOwnerEmail = null;
	private String technicalOwner = null;
	private String businessOwner = null;

	public String getBusinessOwnerEmail() {
		return businessOwnerEmail;
	}

	public void setBusinessOwnerEmail(String businessOwnerEmail) {
		this.businessOwnerEmail = businessOwnerEmail;
	}

	public String getTechnicalOwnerEmail() {
		return technicalOwnerEmail;
	}

	public void setTechnicalOwnerEmail(String technicalOwnerEmail) {
		this.technicalOwnerEmail = technicalOwnerEmail;
	}

	public String getTechnicalOwner() {
		return technicalOwner;
	}

	public void setTechnicalOwner(String technicalOwner) {
		this.technicalOwner = technicalOwner;
	}

	public String getBusinessOwner() {
		return businessOwner;
	}

	public void setBusinessOwner(String businessOwner) {
		this.businessOwner = businessOwner;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("class APIBusinessInformationDTO {\n");

		sb.append("  businessOwnerEmail: ").append(businessOwnerEmail).append("\n");
		sb.append("  technicalOwnerEmail: ").append(technicalOwnerEmail).append("\n");
		sb.append("  technicalOwner: ").append(technicalOwner).append("\n");
		sb.append("  businessOwner: ").append(businessOwner).append("\n");
		sb.append("}\n");
		return sb.toString();
	}
}
