package org.wso2.carbon.apimgt.apim.integration.publisher;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.apimgt.api.APIManagementException;
import org.wso2.carbon.apimgt.apim.integration.common.APIMIntegrationException;
import org.wso2.carbon.apimgt.apim.integration.common.configs.APIMConfig;
import org.wso2.carbon.apimgt.apim.integration.dcr.DcrClient;
import org.wso2.carbon.apimgt.apim.integration.publisher.dto.PublisherAPIDTO;
import org.wso2.carbon.apimgt.apim.integration.publisher.dto.PublisherAPIListDTO;

public class PublisherClient {
	private static final Log log = LogFactory.getLog(PublisherClient.class);
	private InternalPublisherClient internalPublisherClient;
	private APIMConfig config;
	private DcrClient dcrClient;
	
	//TODO to be removed once apim fix is avaialbe
	public void setDcrClient(DcrClient dcrClient) {
		this.dcrClient = dcrClient;
	}

	/**
	 * Initialize APIMIntegration client with a APIMConfig provided as the config
	 * @param confign - An instance of APIMConfig, see apim-integration.xml
	 * @throws APIManagementException
	 */
	public PublisherClient(APIMConfig config) throws APIManagementException{
		if (config == null) {
			throw new APIManagementException("APIM config should not be null, see apim-ntegration.xml");
		}
		this.config = config;
		this.internalPublisherClient = new InternalPublisherClient();
		this.dcrClient = new DcrClient(config);
	}
	
	/**
	 * Get a list of of available APIs (from WSO2-APIM) qualifying under a given search query. 
	 * @param query - You can search in attributes by using an ":" modifier. Eg. "tag:wso2" will match an API if the tag of the API is "wso2".
	 * @return - An instance of SubscriptionListDTO, which contains the list of apis
	 * @throws APIMIntegrationException - If fails to invoke the operation due to wrong credentials or any other issue
	 */
	public PublisherAPIListDTO searchPublisherAPIs(String searchQuery) throws APIMIntegrationException {
		
		PublisherAPIListDTO publisherApis = null;
		try {
			publisherApis = internalPublisherClient.searchPublisherAPIs(config.getPublisherEndpointConfig(), "", dcrClient.getToken());
		} catch (APIMIntegrationException e) {
			if (e.getResponseStatus() == 401) {
				publisherApis = internalPublisherClient.searchPublisherAPIs(config.getPublisherEndpointConfig(), "", dcrClient.getRenewedToken());
			} else {
				throw e;
			}
		}
		return publisherApis;
	}
	
	/**
	 * Creates a API in WSO2-APIM
	 * @param apiDTO - Instance of APIDTO, which carries the information of api creation request
	 * @return - Instance of APIDTO, which contains the information of created API
	 * @throws APIMIntegrationException - If fails to invoke the operation due to wrong credentials or any other issue
	 */
	public PublisherAPIDTO createAndPublishAPIIfNotExists(PublisherAPIDTO apiDTO) throws APIMIntegrationException {

		PublisherAPIListDTO publisherApiList = searchPublisherAPIs("");
		List<PublisherAPIDTO> apiLIst = publisherApiList.getList();
		PublisherAPIDTO existingAPI = getExistingApi(apiDTO, apiLIst);
		if (existingAPI != null) {
			System.out.println("API " + existingAPI.getName() + " apready exists, therefore not creating");
			if ("PUBLISHED".equals(existingAPI.getStatus())) {
				System.out.println("API " + existingAPI.getName() + " apready in PUBLISHED state, therefore not publishing");
			} else {
				boolean publishResult = publishAPI(existingAPI.getId());
				System.out.println("API publish result " + publishResult);
			}
			return existingAPI;
		} else {
			PublisherAPIDTO createdAPI =  createAPI(apiDTO);
			System.out.println("API creation successfull createdAPI.getId() = " + createdAPI.getId());
			boolean result = publishAPI(createdAPI.getId());
			System.out.println("API publish done result = " + result);
			return createdAPI;
		}
	}
	
	
	/**
	 * Creates a API in WSO2-APIM
	 * @param apiDTO - Instance of APIDTO, which carries the information of api creation request
	 * @return - Instance of APIDTO, which contains the information of created API
	 * @throws APIMIntegrationException - If fails to invoke the operation due to wrong credentials or any other issue
	 */
	private PublisherAPIDTO createAPI(PublisherAPIDTO apiDTO) throws APIMIntegrationException {
		
		PublisherAPIDTO publiserApi = null;
		try {
			publiserApi = internalPublisherClient.createAPI(config.getPublisherEndpointConfig(), apiDTO, dcrClient.getToken());
		} catch (APIMIntegrationException e) {
			if (e.getResponseStatus() == 401) {
				publiserApi = internalPublisherClient.createAPI(config.getPublisherEndpointConfig(), apiDTO, dcrClient.getRenewedToken());
			} else {
				throw e;
			}
		}
		return publiserApi;
	}
	
	/**
	 * Publishes a created api (specified by the apiID) to APIM
	 * @param apiID - The unique ID which represents the API in WSO2-APIM
	 * @return - True if publishing is successful, false otherwise
	 * @throws APIMIntegrationException - If fails to invoke the operation due to wrong credentials or any other issue
	 */
	private boolean publishAPI(String apiID) throws APIMIntegrationException {
		
		boolean result = false;
		try {
			result = internalPublisherClient.publishAPI(config.getPublisherEndpointConfig(), apiID, dcrClient.getToken());
		} catch (APIMIntegrationException e) {
			if (e.getResponseStatus() == 401) {
				result = internalPublisherClient.publishAPI(config.getPublisherEndpointConfig(), apiID, dcrClient.getRenewedToken());
			} else {
				throw e;
			}
		}
		return result;
	}
	
	
	private static  PublisherAPIDTO getExistingApi(PublisherAPIDTO apiDTO, List<PublisherAPIDTO> apiLIst) {
		for (PublisherAPIDTO api : apiLIst) {
			if (api.getContext().equals(apiDTO.getContext())) {
				return api;
			}
		}
		return null;
	}
	
}
