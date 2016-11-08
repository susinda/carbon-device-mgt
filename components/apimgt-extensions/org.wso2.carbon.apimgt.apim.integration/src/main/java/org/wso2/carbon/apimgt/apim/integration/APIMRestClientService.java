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

package org.wso2.carbon.apimgt.apim.integration;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.wso2.carbon.apimgt.apim.integration.dto.APIDTO;
import org.wso2.carbon.apimgt.apim.integration.dto.ClientProfile;
import org.wso2.carbon.apimgt.apim.integration.dto.OAuthApplication;
import org.wso2.carbon.apimgt.apim.integration.dto.Token;
import org.wso2.carbon.apimgt.apim.integration.dto.TokenInfo;

import com.google.gson.JsonObject;

@Path("/")
public interface APIMRestClientService {

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public OAuthApplication register(ClientProfile registrationProfile);

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public Token getToken(@QueryParam("grant_type") String grant, @QueryParam("username") String username,
            @QueryParam("password") String password, @QueryParam("scope") String scope);

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public JsonObject createAPI(APIDTO apiDTO);

	//https://localhost:9443/api/am/publisher/v0.10/apis/change-lifecycle?apiId=xxxxxx&action=Publish"
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/change-lifecycle?apiId={apiIdentifier}&action={actionName}")
	public JsonObject publishAPI(@QueryParam("apiIdentifier") String apiID, @QueryParam("actionName") String state);

}
