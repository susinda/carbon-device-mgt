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

import org.wso2.carbon.apimgt.apim.integration.dto.PublisherAPIDTO;
import org.wso2.carbon.apimgt.apim.integration.dto.PublisherAPIListDTO;
import org.wso2.carbon.apimgt.apim.integration.dto.APIMApplicationDTO;
import org.wso2.carbon.apimgt.apim.integration.dto.ApplicationKeyDTO;
import org.wso2.carbon.apimgt.apim.integration.dto.ApplicationKeyGenRequestDTO;
import org.wso2.carbon.apimgt.apim.integration.dto.APIMApplicationListDTO;
import org.wso2.carbon.apimgt.apim.integration.dto.ClientProfileDTO;
import org.wso2.carbon.apimgt.apim.integration.dto.OAuthApplicationDTO;
import org.wso2.carbon.apimgt.apim.integration.dto.StoreAPIListDTO;
import org.wso2.carbon.apimgt.apim.integration.dto.SubscriptionDTO;
import org.wso2.carbon.apimgt.apim.integration.dto.SubscriptionListDTO;
import org.wso2.carbon.apimgt.apim.integration.dto.TokenDTO;
import feign.Response;

@Path("/")
public interface APIMRestClientService {

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public OAuthApplicationDTO register(ClientProfileDTO registrationProfile);

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public TokenDTO requestToken(@QueryParam("grant_type") String grant, @QueryParam("username") String username,
            @QueryParam("password") String password, @QueryParam("scope") String scope);

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/apis")
	public PublisherAPIDTO createAPI(PublisherAPIDTO apiDTO);

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/apis/change-lifecycle?apiId={apiIdentifier}&action={actionName}")
	public Response publishAPI(@QueryParam("apiIdentifier") String apiID, @QueryParam("actionName") String state);

	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/applications")
	public APIMApplicationListDTO getAPIMApplications();
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/applications/{appId}")
	public APIMApplicationDTO getAPIMApplicationDetails(@PathParam("appId") String appId);
	
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/applications")
	public APIMApplicationDTO createAPIMApplication(APIMApplicationDTO requestApp);
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/apis?query={searchQuery}")
	public StoreAPIListDTO getExistingStoreAPIs(@QueryParam("searchQuery") String query);
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/subscriptions?apiId={apiId}")
	public SubscriptionListDTO getExistingSubscriptions(@QueryParam("apiId") String apiId);
	
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/subscriptions")
	public SubscriptionDTO subscribeAPItoApp(SubscriptionDTO subscriptionRequest);

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/applications/generate-keys?applicationId={appId}")
	public ApplicationKeyDTO generateKeysforApp(ApplicationKeyGenRequestDTO keygenRequest, @QueryParam("appId") String appId);

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	//@Path("/apis?query={searchQuery}")
	@Path("/apis")
	public PublisherAPIListDTO getExistingPublisherAPIs();

}
