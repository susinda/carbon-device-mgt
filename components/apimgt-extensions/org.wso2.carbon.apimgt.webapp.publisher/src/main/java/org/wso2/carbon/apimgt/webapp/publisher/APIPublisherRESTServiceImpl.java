package org.wso2.carbon.apimgt.webapp.publisher;

import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.apimgt.api.APIManagementException;
import org.wso2.carbon.apimgt.api.FaultGatewaysException;
import org.wso2.carbon.apimgt.api.model.API;
import org.wso2.carbon.apimgt.api.model.APIIdentifier;
import org.wso2.carbon.apimgt.apim.integration.APIMClient;
import org.wso2.carbon.apimgt.apim.integration.APIMConfigReader;
import org.wso2.carbon.apimgt.apim.integration.dto.APIDTO;
import org.wso2.carbon.apimgt.apim.integration.dto.APIMConfig;
import org.wso2.carbon.apimgt.apim.utils.APIMConfigurations;

public class APIPublisherRESTServiceImpl extends APIPublisherServiceImpl {
	private static final Log log = LogFactory.getLog(APIPublisherRESTServiceImpl.class);

	@Override
	public void publishAPI(API apimAPI) throws APIManagementException, FaultGatewaysException {
	   log.info("APIPublisherRESTServiceImpl Creating api " + apimAPI.getId().getApiName());
	   APIMClient apimRestClient = new APIMClient();
	   APIMConfig config = APIMConfigReader.getAPIMConfig(); 
	   APIDTO apiDTO = APIBuilderUtil.fromAPItoDTO(apimAPI);
	   String accessToken = APIBuilderUtil.getAccessToken();
	   apimRestClient.createAPI(config.getPublisherEndpointConfig(), apiDTO, accessToken);
    }
		
	@Override
    public void removeAPI(APIIdentifier id) throws APIManagementException {
        if (log.isDebugEnabled()) {
            log.debug("Removing API '" + id.getApiName() + "'");
        }
        APIMClient apimRestClient = new APIMClient();
 	    APIMConfigurations.APIMEnvironmentConfig apiEnvironmentConfig = APIMConfigurations.getInstance().getApiEnvironmentConfigList().get(0);
 	    //apimRestClient.deleteAPI(id.getApiName(), apiEnvironmentConfig);
        if (log.isDebugEnabled()) {
            log.debug("API '" + id.getApiName() + "' has been successfully removed");
        }
    }

    @Override
    public void publishAPIs(List<API> apis) throws APIManagementException, FaultGatewaysException {
        if (log.isDebugEnabled()) {
            log.debug("Publishing a batch of APIs");
        }
        for (API api : apis) {
            try {
                this.publishAPI(api);
            } catch (APIManagementException e) {
                log.error("Error occurred while publishing API '" + api.getId().getApiName() + "'", e);
            }
        }
        if (log.isDebugEnabled()) {
            log.debug("End of publishing the batch of APIs");
        }
    }
	

}
