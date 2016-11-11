package org.wso2.carbon.apimgt.webapp.publisher;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.apimgt.api.APIManagementException;
import org.wso2.carbon.apimgt.api.model.API;
import org.wso2.carbon.apimgt.api.model.Scope;
import org.wso2.carbon.apimgt.api.model.URITemplate;
import org.wso2.carbon.apimgt.apim.integration.APIMClient;
import org.wso2.carbon.apimgt.apim.integration.APIMConfigReader;
import org.wso2.carbon.apimgt.apim.integration.dto.APIBusinessInformationDTO;
import org.wso2.carbon.apimgt.apim.integration.dto.APICorsConfigurationDTO;
import org.wso2.carbon.apimgt.apim.integration.dto.APIDTO;
import org.wso2.carbon.apimgt.apim.integration.dto.APIEndpointSecurityDTO;
import org.wso2.carbon.apimgt.apim.integration.dto.APIMConfig;
import org.wso2.carbon.apimgt.apim.integration.dto.APIMaxTpsDTO;
import org.wso2.carbon.apimgt.apim.integration.dto.OAuthApplicationDTO;
import org.wso2.carbon.apimgt.apim.integration.dto.SequenceDTO;
import org.wso2.carbon.apimgt.apim.integration.dto.TokenDTO;
import org.wso2.carbon.apimgt.impl.APIConstants;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

public class APIBuilderUtil {
	private static final Log log = LogFactory.getLog(APIBuilderUtil.class);
	
	public static String getAccessToken() throws APIManagementException {
		APIMConfig apimConfig = APIMConfigReader.getAPIMConfig();
		
		APIMClient client = new APIMClient();
		OAuthApplicationDTO dcrApp = client.createOAuthApplication(apimConfig.getDcrEndpointConfig());
		System.out.println("Auth app created sucessfully, app.getClientSecret() = " + dcrApp.getClientSecret());

		TokenDTO token = client.getUserToken(apimConfig.getTokenEndpointConfig(), dcrApp);
		System.out.println("Token generated succesfully, token.getAccessToken() = " + token.getAccess_token());
		return token.getAccess_token();
	}

	public static APIDTO fromAPItoDTO(API model) throws APIManagementException {

		APIDTO dto = new APIDTO();
		dto.setName(model.getId().getApiName());
		dto.setVersion(model.getId().getVersion());
		String providerName = model.getId().getProviderName();
		dto.setProvider(providerName);
		dto.setId(model.getUUID());
		String context = model.getContextTemplate();

		dto.setContext(context);
		dto.setDescription(model.getDescription());

		dto.setIsDefaultVersion(model.isDefaultVersion());
		dto.setResponseCaching(model.getResponseCache());
		dto.setCacheTimeout(model.getCacheTimeout());
		dto.setEndpointConfig(model.getEndpointConfig());
		// if (!StringUtils.isBlank(model.getThumbnailUrl())) {
		// dto.setThumbnailUri(getThumbnailUri(model.getUUID()));
		// }
		List<SequenceDTO> sequences = new ArrayList<>();

		String inSequenceName = model.getInSequence();
		if (inSequenceName != null && !inSequenceName.isEmpty()) {
			SequenceDTO inSequence = new SequenceDTO();
			inSequence.setName(inSequenceName);
			inSequence.setType("in");
			sequences.add(inSequence);
		}

		String outSequenceName = model.getOutSequence();
		if (outSequenceName != null && !outSequenceName.isEmpty()) {
			SequenceDTO outSequence = new SequenceDTO();
			outSequence.setName(outSequenceName);
			outSequence.setType("out");
			sequences.add(outSequence);
		}

		String faultSequenceName = model.getFaultSequence();
		if (faultSequenceName != null && !faultSequenceName.isEmpty()) {
			SequenceDTO faultSequence = new SequenceDTO();
			faultSequence.setName(faultSequenceName);
			faultSequence.setType("fault");
			sequences.add(faultSequence);
		}

		dto.setSequences(sequences);

		dto.setStatus(model.getStatus().getStatus());

		String subscriptionAvailability = model.getSubscriptionAvailability();
		if (subscriptionAvailability != null) {
			dto.setSubscriptionAvailability(mapSubscriptionAvailabilityFromAPItoDTO(subscriptionAvailability));
		}

		if (model.getSubscriptionAvailableTenants() != null) {
			dto.setSubscriptionAvailableTenants(Arrays.asList(model.getSubscriptionAvailableTenants().split(",")));
		}

		// Get Swagger definition which has URL templates, scopes and resource
		// details
		// String apiSwaggerDefinition =
		// apiProvider.getSwagger20Definition(model.getId());
		dto.setApiDefinition(createSwaggerDefinition(model));

		Set<String> apiTags = model.getTags();
		List<String> tagsToReturn = new ArrayList<>();
		tagsToReturn.addAll(apiTags);
		dto.setTags(tagsToReturn);

		Set<org.wso2.carbon.apimgt.api.model.Tier> apiTiers = model.getAvailableTiers();
		List<String> tiersToReturn = new ArrayList<>();
		for (org.wso2.carbon.apimgt.api.model.Tier tier : apiTiers) {
			tiersToReturn.add(tier.getName());
		}
		dto.setTiers(tiersToReturn);
		// dto.setType(model.getType());

		// if (!model.getType().equals(APIConstants.APIType.WS)) {
		dto.setTransport(Arrays.asList(model.getTransports().split(",")));
		// }
		dto.setVisibility(

				mapVisibilityFromAPItoDTO(model.getVisibility()));

		if (model.getVisibleRoles() != null) {
			dto.setVisibleRoles(Arrays.asList(model.getVisibleRoles().split(",")));
		}

		if (model.getVisibleTenants() != null) {
			dto.setVisibleRoles(Arrays.asList(model.getVisibleTenants().split(",")));
		}

		APIBusinessInformationDTO apiBusinessInformationDTO = new APIBusinessInformationDTO();
		apiBusinessInformationDTO.setBusinessOwner(model.getBusinessOwner());
		apiBusinessInformationDTO.setBusinessOwnerEmail(model.getBusinessOwnerEmail());
		apiBusinessInformationDTO.setTechnicalOwner(model.getTechnicalOwner());
		apiBusinessInformationDTO.setTechnicalOwnerEmail(model.getTechnicalOwnerEmail());
		dto.setBusinessInformation(apiBusinessInformationDTO);
		String gatewayEnvironments = StringUtils.join(model.getEnvironments(), ",");
		dto.setGatewayEnvironments(gatewayEnvironments);
		APICorsConfigurationDTO apiCorsConfigurationDTO = new APICorsConfigurationDTO();
		// CORSConfiguration corsConfiguration = model.getCorsConfiguration();
		// if (corsConfiguration == null) {
		// corsConfiguration = APIUtil.getDefaultCorsConfiguration();
		// }
		// apiCorsConfigurationDTO
		// .setAccessControlAllowOrigins(corsConfiguration.getAccessControlAllowOrigins());
		// apiCorsConfigurationDTO
		// .setAccessControlAllowHeaders(corsConfiguration.getAccessControlAllowHeaders());
		// apiCorsConfigurationDTO
		// .setAccessControlAllowMethods(corsConfiguration.getAccessControlAllowMethods());
		// apiCorsConfigurationDTO.setCorsConfigurationEnabled(corsConfiguration.isCorsConfigurationEnabled());
		// apiCorsConfigurationDTO.setAccessControlAllowCredentials(corsConfiguration.isAccessControlAllowCredentials());
		dto.setCorsConfiguration(apiCorsConfigurationDTO);
		dto.setWsdlUri(model.getWsdlUrl());
		setEndpointSecurityFromModelToApiDTO(model, dto);
		setMaxTpsFromModelToApiDTO(model, dto);

		return dto;
	}
	
    private static String createSwaggerDefinition(API api) {
        Map<String, JsonObject> httpVerbsMap = new HashMap<>();
        List<Scope> scopes = new ArrayList<>();

        for (URITemplate uriTemplate : api.getUriTemplates()) {
            JsonObject response = new JsonObject();
            response.addProperty("200", "");

            JsonObject responses = new JsonObject();
            responses.add("responses", response);
            JsonObject httpVerbs = httpVerbsMap.get(uriTemplate.getUriTemplate());
            if (httpVerbs == null) {
                httpVerbs = new JsonObject();
            }
            JsonObject httpVerb = new JsonObject();
            httpVerb.add("responses", response);

            httpVerb.addProperty("x-auth-type", "Application%20%26%20Application%20User");
            httpVerb.addProperty("x-throttling-tier", "Unlimited");
            if (uriTemplate.getScope() != null) {
                httpVerb.addProperty("x-scope", uriTemplate.getScope().getName());
                scopes.add(uriTemplate.getScope());
            }
            httpVerbs.add(uriTemplate.getHTTPVerb().toLowerCase(), httpVerb);
            httpVerbsMap.put(uriTemplate.getUriTemplate(), httpVerbs);
        }

        Iterator it = httpVerbsMap.entrySet().iterator();
        JsonObject paths = new JsonObject();
        while (it.hasNext()) {
            Map.Entry<String, JsonObject> pair = (Map.Entry) it.next();
            paths.add(pair.getKey(), pair.getValue());
            it.remove();
        }

        JsonObject info = new JsonObject();
        info.addProperty("title", api.getId().getApiName());
        info.addProperty("version", api.getId().getVersion());

        JsonObject swaggerDefinition = new JsonObject();
        swaggerDefinition.add("paths", paths);
        swaggerDefinition.addProperty("swagger", "2.0");
        swaggerDefinition.add("info", info);

        // adding scopes to swagger definition
        if (!api.getScopes().isEmpty()) {
            Gson gson = new Gson();
            JsonElement element = gson.toJsonTree(api.getScopes(), new TypeToken<Set<Scope>>() {
            }.getType());
            if (element != null) {
                JsonArray apiScopes = element.getAsJsonArray();
                JsonObject apim = new JsonObject();
                apim.add("x-wso2-scopes", apiScopes);
                JsonObject wso2Security = new JsonObject();
                wso2Security.add("apim", apim);
                swaggerDefinition.add("x-wso2-security", wso2Security);
            }
        }
        if (log.isDebugEnabled()) {
            log.debug("API swagger definition: " + swaggerDefinition.toString());
        }
        return swaggerDefinition.toString();
    }

	private static void setEndpointSecurityFromModelToApiDTO(API api, APIDTO dto) {
		if (api.isEndpointSecured()) {
			APIEndpointSecurityDTO securityDTO = new APIEndpointSecurityDTO();
			securityDTO.setType(APIEndpointSecurityDTO.TypeEnum.basic); // set
																		// default
																		// as
																		// basic
			securityDTO.setUsername(api.getEndpointUTUsername());
			securityDTO.setPassword(api.getEndpointUTPassword());
			if (api.isEndpointAuthDigest()) {
				securityDTO.setType(APIEndpointSecurityDTO.TypeEnum.digest);
			}
			dto.setEndpointSecurity(securityDTO);
		}
	}

	private static void setMaxTpsFromModelToApiDTO(API api, APIDTO dto) {
		if (StringUtils.isBlank(api.getProductionMaxTps()) && StringUtils.isBlank(api.getSandboxMaxTps())) {
			return;
		}
		APIMaxTpsDTO maxTpsDTO = new APIMaxTpsDTO();
		try {
			if (!StringUtils.isBlank(api.getProductionMaxTps())) {
				maxTpsDTO.setProduction(Long.parseLong(api.getProductionMaxTps()));
			}
			if (!StringUtils.isBlank(api.getSandboxMaxTps())) {
				maxTpsDTO.setSandbox(Long.parseLong(api.getSandboxMaxTps()));
			}
			dto.setMaxTps(maxTpsDTO);
		} catch (NumberFormatException e) {
			// logs the error and continues as this is not a blocker
			log.error("Cannot convert to Long format when setting maxTps for API", e);
		}
	}

	private static APIDTO.SubscriptionAvailabilityEnum mapSubscriptionAvailabilityFromAPItoDTO(
			String subscriptionAvailability) {

		switch (subscriptionAvailability) {
		case APIConstants.SUBSCRIPTION_TO_CURRENT_TENANT:
			return APIDTO.SubscriptionAvailabilityEnum.current_tenant;
		case APIConstants.SUBSCRIPTION_TO_ALL_TENANTS:
			return APIDTO.SubscriptionAvailabilityEnum.all_tenants;
		case APIConstants.SUBSCRIPTION_TO_SPECIFIC_TENANTS:
			return APIDTO.SubscriptionAvailabilityEnum.specific_tenants;
		default:
			return null; // how to handle this?
		}

	}

	private static APIDTO.VisibilityEnum mapVisibilityFromAPItoDTO(String visibility) {
		switch (visibility) { // public, private,controlled, restricted
		case APIConstants.API_GLOBAL_VISIBILITY:
			return APIDTO.VisibilityEnum.PUBLIC;
		case APIConstants.API_PRIVATE_VISIBILITY:
			return APIDTO.VisibilityEnum.PRIVATE;
		case APIConstants.API_RESTRICTED_VISIBILITY:
			return APIDTO.VisibilityEnum.RESTRICTED;
		case APIConstants.API_CONTROLLED_VISIBILITY:
			return APIDTO.VisibilityEnum.CONTROLLED;
		default:
			return null; // how to handle this?
		}
	}

}
