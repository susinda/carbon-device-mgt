package org.wso2.carbon.apimgt.webapp.publisher;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.apimgt.api.APIManagementException;
import org.wso2.carbon.apimgt.api.FaultGatewaysException;
import org.wso2.carbon.apimgt.api.model.API;
import org.wso2.carbon.apimgt.api.model.APIIdentifier;
import org.wso2.carbon.apimgt.apim.integration.APIMConfigurations;
import org.wso2.carbon.apimgt.apim.integration.APIMPublisherRestClient;

public class APIPublisherRESTServiceImpl extends APIPublisherServiceImpl {
	private static final Log log = LogFactory.getLog(APIPublisherRESTServiceImpl.class);

	@Override
	public void publishAPI(API apimAPI) throws APIManagementException, FaultGatewaysException {
		log.info("APIPublisherRESTServiceImpl Creating api " + apimAPI.getId().getApiName());
	   APIMPublisherRestClient apimRestClient = new APIMPublisherRestClient();
	   APIMConfigurations.APIMEnvironmentConfig apiEnvironmentConfig = APIMConfigurations.getInstance().getApiEnvironmentConfigList().get(0);
	   apimRestClient.createAPI(apimAPI, apiEnvironmentConfig);
    }
	
	@Override
    public void removeAPI(APIIdentifier id) throws APIManagementException {
        if (log.isDebugEnabled()) {
            log.debug("Removing API '" + id.getApiName() + "'");
        }
        APIMPublisherRestClient apimRestClient = new APIMPublisherRestClient();
 	    APIMConfigurations.APIMEnvironmentConfig apiEnvironmentConfig = APIMConfigurations.getInstance().getApiEnvironmentConfigList().get(0);
 	    apimRestClient.deleteAPI(id.getApiName(), apiEnvironmentConfig);
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
