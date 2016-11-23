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

package org.wso2.carbon.apimgt.apim.integration.store;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.wso2.carbon.apimgt.apim.integration.common.APIMIntegrationException;
import org.wso2.carbon.apimgt.apim.integration.store.dto.APIMApplicationDTO;
import org.wso2.carbon.apimgt.apim.integration.store.dto.APIMApplicationListDTO;
import org.wso2.carbon.apimgt.apim.integration.store.dto.ApplicationKeyDTO;
import org.wso2.carbon.apimgt.apim.integration.store.dto.ApplicationKeyGenRequestDTO;
import org.wso2.carbon.apimgt.apim.integration.store.dto.StoreAPIListDTO;
import org.wso2.carbon.apimgt.apim.integration.store.dto.SubscriptionDTO;
import org.wso2.carbon.apimgt.apim.integration.store.dto.SubscriptionListDTO;

@Path("/")
public interface StoreClientInterface {

	// Store APIs
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/applications")
	public APIMApplicationListDTO getAPIMApplications() throws APIMIntegrationException;

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/applications/{appId}")
	public APIMApplicationDTO getAPIMApplicationDetails(@PathParam("appId") String appId) throws APIMIntegrationException;

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/applications")
	public APIMApplicationDTO createAPIMApplication(APIMApplicationDTO requestApp) throws APIMIntegrationException;

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/apis?query={searchQuery}")
	public StoreAPIListDTO getExistingStoreAPIs(@QueryParam("searchQuery") String query) throws APIMIntegrationException;

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/subscriptions?apiId={apiId}")
	public SubscriptionListDTO getExistingSubscriptions(@QueryParam("apiId") String apiId) throws APIMIntegrationException;

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/subscriptions")
	public SubscriptionDTO subscribeAPItoApp(SubscriptionDTO subscriptionRequest) throws APIMIntegrationException;

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/applications/generate-keys?applicationId={appId}")
	public ApplicationKeyDTO generateKeysforApp(ApplicationKeyGenRequestDTO keygenRequest, @QueryParam("appId") String appId) throws APIMIntegrationException;

}
