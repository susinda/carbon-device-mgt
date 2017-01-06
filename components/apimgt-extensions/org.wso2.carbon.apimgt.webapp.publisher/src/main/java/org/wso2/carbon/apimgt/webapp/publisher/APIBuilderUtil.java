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
import org.wso2.carbon.apimgt.impl.APIConstants;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import org.wso2.carbon.apimgt.publisher.client.model.*;

public class APIBuilderUtil {
	private static final Log log = LogFactory.getLog(APIBuilderUtil.class);
	

	public static org.wso2.carbon.apimgt.publisher.client.model.API fromAPItoDTO(API model) throws APIManagementException {

		org.wso2.carbon.apimgt.publisher.client.model.API  dto = new org.wso2.carbon.apimgt.publisher.client.model.API();
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
		List<Sequence> sequences = new ArrayList<>();

		String inSequenceName = model.getInSequence();
		if (inSequenceName != null && !inSequenceName.isEmpty()) {
			Sequence inSequence = new Sequence();
			inSequence.setName(inSequenceName);
			inSequence.setType("in");
			sequences.add(inSequence);
		}

		String outSequenceName = model.getOutSequence();
		if (outSequenceName != null && !outSequenceName.isEmpty()) {
			Sequence outSequence = new Sequence();
			outSequence.setName(outSequenceName);
			outSequence.setType("out");
			sequences.add(outSequence);
		}

		String faultSequenceName = model.getFaultSequence();
		if (faultSequenceName != null && !faultSequenceName.isEmpty()) {
			Sequence faultSequence = new Sequence();
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
		dto.setVisibility(mapVisibilityFromAPItoDTO(model.getVisibility()));

		if (model.getVisibleRoles() != null) {
			dto.setVisibleRoles(Arrays.asList(model.getVisibleRoles().split(",")));
		}

		if (model.getVisibleTenants() != null) {
			dto.setVisibleRoles(Arrays.asList(model.getVisibleTenants().split(",")));
		}

		APIBusinessInformation apiBusinessInformationDTO = new APIBusinessInformation();
		apiBusinessInformationDTO.setBusinessOwner(model.getBusinessOwner());
		apiBusinessInformationDTO.setBusinessOwnerEmail(model.getBusinessOwnerEmail());
		apiBusinessInformationDTO.setTechnicalOwner(model.getTechnicalOwner());
		apiBusinessInformationDTO.setTechnicalOwnerEmail(model.getTechnicalOwnerEmail());
		dto.setBusinessInformation(apiBusinessInformationDTO);
		String gatewayEnvironments = StringUtils.join(model.getEnvironments(), ",");
		dto.setGatewayEnvironments(gatewayEnvironments);
		APICorsConfiguration apiCorsConfigurationDTO = new APICorsConfiguration();
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

	private static void setEndpointSecurityFromModelToApiDTO(API api, org.wso2.carbon.apimgt.publisher.client.model.API dto) {
		if (api.isEndpointSecured()) {
			APIEndpointSecurity securityDTO = new APIEndpointSecurity();
			securityDTO.setType(APIEndpointSecurity.TypeEnum.BASIC); // set
																		// default
																		// as
																		// basic
			securityDTO.setUsername(api.getEndpointUTUsername());
			securityDTO.setPassword(api.getEndpointUTPassword());
			if (api.isEndpointAuthDigest()) {
				securityDTO.setType(APIEndpointSecurity.TypeEnum.DIGEST);
			}
			dto.setEndpointSecurity(securityDTO);
		}
	}

	private static void setMaxTpsFromModelToApiDTO(API api, org.wso2.carbon.apimgt.publisher.client.model.API dto) {
		if (StringUtils.isBlank(api.getProductionMaxTps()) && StringUtils.isBlank(api.getSandboxMaxTps())) {
			return;
		}
		APIMaxTps maxTpsDTO = new APIMaxTps();
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

	private static org.wso2.carbon.apimgt.publisher.client.model.API.SubscriptionAvailabilityEnum mapSubscriptionAvailabilityFromAPItoDTO(
			String subscriptionAvailability) {

		switch (subscriptionAvailability) {
		case APIConstants.SUBSCRIPTION_TO_CURRENT_TENANT:
			return org.wso2.carbon.apimgt.publisher.client.model.API.SubscriptionAvailabilityEnum.CURRENT_TENANT;
		case APIConstants.SUBSCRIPTION_TO_ALL_TENANTS:
			return org.wso2.carbon.apimgt.publisher.client.model.API.SubscriptionAvailabilityEnum.ALL_TENANTS;
		case APIConstants.SUBSCRIPTION_TO_SPECIFIC_TENANTS:
			return org.wso2.carbon.apimgt.publisher.client.model.API.SubscriptionAvailabilityEnum.SPECIFIC_TENANTS;
		default:
			return null; // how to handle this?
		}

	}

	private static org.wso2.carbon.apimgt.publisher.client.model.API.VisibilityEnum mapVisibilityFromAPItoDTO(String visibility) {
		switch (visibility) { // public, private,controlled, restricted
		case APIConstants.API_GLOBAL_VISIBILITY:
			return org.wso2.carbon.apimgt.publisher.client.model.API.VisibilityEnum.PUBLIC;
		case APIConstants.API_PRIVATE_VISIBILITY:
			return org.wso2.carbon.apimgt.publisher.client.model.API.VisibilityEnum.PRIVATE;
		case APIConstants.API_RESTRICTED_VISIBILITY:
			return org.wso2.carbon.apimgt.publisher.client.model.API.VisibilityEnum.RESTRICTED;
		case APIConstants.API_CONTROLLED_VISIBILITY:
			return org.wso2.carbon.apimgt.publisher.client.model.API.VisibilityEnum.CONTROLLED;
		default:
			return null; // how to handle this?
		}
	}

}
