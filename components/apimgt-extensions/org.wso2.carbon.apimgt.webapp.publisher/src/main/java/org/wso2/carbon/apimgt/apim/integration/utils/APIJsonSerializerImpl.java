package org.wso2.carbon.apimgt.apim.integration.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.wso2.carbon.apimgt.api.model.API;
import org.wso2.carbon.apimgt.api.model.Documentation;
import org.wso2.carbon.apimgt.api.model.Scope;
import org.wso2.carbon.apimgt.api.model.Tier;
import org.wso2.carbon.apimgt.api.model.URITemplate;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.reflect.TypeToken;

public class APIJsonSerializerImpl implements APIJsonSerializer {

	@Override
	public JsonObject serialize(API apimAPI) {
		return buildAPICreationPayload(apimAPI);
	}
	
	private JsonObject buildAPICreationPayload(API apimAPI) {
        JsonObject jsonApiPayLoad = new JsonObject();

        jsonApiPayLoad.addProperty("name", apimAPI.getId().getApiName());
        jsonApiPayLoad.addProperty("provider", apimAPI.getId().getProviderName());
        jsonApiPayLoad.addProperty("version", apimAPI.getId().getVersion());
        jsonApiPayLoad.addProperty("description", apimAPI.getDescription());
        jsonApiPayLoad.addProperty("context", apimAPI.getContext());

        jsonApiPayLoad.addProperty("cacheTimeout", Constants.CACHE_TIMEOUT);
        jsonApiPayLoad.add("subscriptionAvailability", null);
        jsonApiPayLoad.addProperty("isDefaultVersion", apimAPI.isDefaultVersion());
        jsonApiPayLoad.addProperty("responseCaching", apimAPI.getResponseCache());
        jsonApiPayLoad.addProperty("visibility", apimAPI.getVisibility().toUpperCase());

        JsonObject businessInformation = new JsonObject();

        businessInformation.addProperty("businessOwner", apimAPI.getBusinessOwner());
        businessInformation.addProperty("businessOwnerEmail", apimAPI.getBusinessOwnerEmail());
        businessInformation.addProperty("technicalOwner", apimAPI.getTechnicalOwner());
        businessInformation.addProperty("technicalOwnerEmail", apimAPI.getTechnicalOwnerEmail());

        JsonArray sequenceArray = new JsonArray();

        String inSequenceFlow = apimAPI.getInSequence();
        String outSequenceFlow = apimAPI.getOutSequence();
        String faultSequenceFlow = apimAPI.getFaultSequence();

        if(inSequenceFlow != null && !inSequenceFlow.equals("None")) {
            JsonObject jsonInSequenceFlow = new JsonObject();
            jsonInSequenceFlow.addProperty("name", inSequenceFlow);
            jsonInSequenceFlow.add("config", null);
            jsonInSequenceFlow.addProperty("type", "in");
            sequenceArray.add(jsonInSequenceFlow);
        }

        if(outSequenceFlow != null && !outSequenceFlow.equals("None")) {
            JsonObject jsonOutSequenceFlow = new JsonObject();
            jsonOutSequenceFlow.addProperty("name", outSequenceFlow);
            jsonOutSequenceFlow.add("config", null);
            jsonOutSequenceFlow.addProperty("type", "out");
            sequenceArray.add(jsonOutSequenceFlow);
        }

        if(faultSequenceFlow != null && !faultSequenceFlow.equals("None")) {
            JsonObject jsonFaultSequenceFlow = new JsonObject();
            jsonFaultSequenceFlow.addProperty("name", faultSequenceFlow);
            jsonFaultSequenceFlow.add("config", null);
            jsonFaultSequenceFlow.addProperty("type", "fault");
            sequenceArray.add(jsonFaultSequenceFlow);
        }

        jsonApiPayLoad.add("sequences", sequenceArray);

        jsonApiPayLoad.add("businessInformation", businessInformation);

        JsonArray tiers = new JsonArray();
        if(apimAPI.getAvailableTiers() != null) {
        	Set<Tier> apimTiers = apimAPI.getAvailableTiers();
            for (Tier tier : apimTiers) {
                JsonPrimitive element = new JsonPrimitive(tier.getName());
                tiers.add(element);
            }
        }
        jsonApiPayLoad.add("tiers", tiers);

        JsonArray transports = new JsonArray();
        if(apimAPI.getTransports() != null) {
        	String[] apimTransports = apimAPI.getTransports().split(",");
            for (String transport : apimTransports) {
                JsonPrimitive element = new JsonPrimitive(transport);
                transports.add(element);
            }
        }
        jsonApiPayLoad.add("transport", transports);

        JsonArray tags = new JsonArray();
        if(apimAPI.getTags() != null) {
            for (String tag : apimAPI.getTags()) {
                JsonPrimitive element = new JsonPrimitive(tag);
                tags.add(element);
            }
        }
        jsonApiPayLoad.add("tags", tags);

        JsonArray visibleRoles = new JsonArray();
        if(apimAPI.getVisibleRoles() != null) {
        	String[] apimVisibleRoles = apimAPI.getTransports().split(",");
            for (String role : apimVisibleRoles) {
                JsonPrimitive element = new JsonPrimitive(role);
                visibleRoles.add(element);
            }
        }
        jsonApiPayLoad.add("visibleRoles", visibleRoles);

        JsonArray visibleTenants = new JsonArray();
        if(apimAPI.getVisibleTenants() != null) {
        	String[] apimVisibleTenants = apimAPI.getTransports().split(",");
            for (String tenant : apimVisibleTenants) {
                JsonPrimitive element = new JsonPrimitive(tenant);
                visibleTenants.add(element);
            }
        }
        jsonApiPayLoad.add("visibleTenants", visibleTenants);

        jsonApiPayLoad.addProperty("endpointConfig", createEndpointConfig(apimAPI));
        //jsonApiPayLoad.addProperty("apiDefinition", createApiDefinitionConfig(apimAPI));
        jsonApiPayLoad.addProperty("apiDefinition", createSwaggerDefinition(apimAPI));
        
        JsonArray documents = new JsonArray();
        for (Documentation doc :apimAPI.getDocuments()) {
        	documents.add(buildDocumentCreationPayload(doc));
        }
        //jsonApiPayLoad.add("documents", documents);
        return jsonApiPayLoad;
    }

    private String createEndpointConfig(API apimAPI) {

        JsonObject endpointJsonConfig = new JsonObject();
        //FIXME and uncomment
        /*
         String endpointType = apimAPI.getEndpointType();
         endpointJsonConfig.addProperty("endpoint_type", endpointType);
        */

        String productionEndpoint = apimAPI.getEndpointConfig();
        if(productionEndpoint != null) {
            JsonObject jsonProductionEndpoint = new JsonObject();
            jsonProductionEndpoint.addProperty("url", productionEndpoint);
            jsonProductionEndpoint.add("config", null);
            endpointJsonConfig.add("production_endpoints", jsonProductionEndpoint);
        }

        //FIXME and uncomment
        /*String sandboxEndpoint = apimAPI.getSandboxEndpoint();
        if(sandboxEndpoint != null) {
            JsonObject jsonSandboxEndpoint = new JsonObject();
            jsonSandboxEndpoint.addProperty("url", sandboxEndpoint);
            jsonSandboxEndpoint.add("config", null);

            endpointJsonConfig.add("sandbox_endpoints", jsonSandboxEndpoint);
        }
        */

        return endpointJsonConfig.toString();
    }
    

    private String createSwaggerDefinition(API apimAPI) {
        Map<String, JsonObject> httpVerbsMap = new HashMap<>();
        List<Scope> scopes = new ArrayList<>();

        for (URITemplate uriTemplate : apimAPI.getUriTemplates()) {
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
        info.addProperty("title", apimAPI.getId().getApiName());
        info.addProperty("version", apimAPI.getId().getVersion());

        JsonObject swaggerDefinition = new JsonObject();
        swaggerDefinition.add("paths", paths);
        swaggerDefinition.addProperty("swagger", "2.0");
        swaggerDefinition.add("info", info);

        // adding scopes to swagger definition
        if (!apimAPI.getScopes().isEmpty()) {
            Gson gson = new Gson();
            JsonElement element = gson.toJsonTree(apimAPI.getScopes(), new TypeToken<Set<Scope>>() {
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
        return swaggerDefinition.toString();
    }

    
    private JsonObject buildDocumentCreationPayload(Documentation doc) {
        JsonObject documentObj = new JsonObject();
        documentObj.addProperty(Constants.API_DOCUMENT_VISIBILITY, "API_LEVEL");
        documentObj.addProperty(Constants.API_DOCUMENT_SOURCE_TYPE, "URL");
        documentObj.addProperty(Constants.API_DOCUMENT_SOURCE_URL, doc.getSourceUrl());
        documentObj.add(Constants.API_DOCUMENT_OTHER_TYPE_NAME, null);
        documentObj.addProperty(Constants.API_DOCUMENT_SUMMARY, doc.getSummary());
        documentObj.addProperty(Constants.API_DOCUMENT_NAME, doc.getName());
        documentObj.addProperty(Constants.API_DOCUMENT_TYPE, doc.getType().toString());
        return documentObj;
    }


}
