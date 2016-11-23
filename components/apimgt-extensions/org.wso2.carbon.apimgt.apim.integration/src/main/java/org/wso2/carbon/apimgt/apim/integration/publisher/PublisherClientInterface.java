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

package org.wso2.carbon.apimgt.apim.integration.publisher;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.wso2.carbon.apimgt.apim.integration.common.APIMIntegrationException;
import org.wso2.carbon.apimgt.apim.integration.publisher.dto.PublisherAPIDTO;
import org.wso2.carbon.apimgt.apim.integration.publisher.dto.PublisherAPIListDTO;

import feign.Response;

@Path("/")
public interface PublisherClientInterface {

	// Publisher APIs
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/apis") // @Path("/apis?query={searchQuery}") ??
	public PublisherAPIListDTO getExistingPublisherAPIs() throws APIMIntegrationException;

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/apis")
	public PublisherAPIDTO createAPI(PublisherAPIDTO apiDTO) throws APIMIntegrationException;

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/apis/change-lifecycle?apiId={apiIdentifier}&action={actionName}")
	public Response publishAPI(@QueryParam("apiIdentifier") String apiID, @QueryParam("actionName") String state) throws APIMIntegrationException;

}
