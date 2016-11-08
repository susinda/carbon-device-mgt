package org.wso2.carbon.apimgt.apim.integration.dto;

public class APIEndpointURLsDTO  {
	  
	  
	  private APIEnvironmentURLsDTO environmentURLs = null;
	  
	  
	  private String environmentName = null;
	  
	  
	  private String environmentType = null;


	  public APIEnvironmentURLsDTO getEnvironmentURLs() {
	    return environmentURLs;
	  }
	  public void setEnvironmentURLs(APIEnvironmentURLsDTO environmentURLs) {
	    this.environmentURLs = environmentURLs;
	  }

	  
	  public String getEnvironmentName() {
	    return environmentName;
	  }
	  public void setEnvironmentName(String environmentName) {
	    this.environmentName = environmentName;
	  }

	  
	  public String getEnvironmentType() {
	    return environmentType;
	  }
	  public void setEnvironmentType(String environmentType) {
	    this.environmentType = environmentType;
	  }

	  

	  @Override
	  public String toString()  {
	    StringBuilder sb = new StringBuilder();
	    sb.append("class APIEndpointURLsDTO {\n");
	    
	    sb.append("  environmentURLs: ").append(environmentURLs).append("\n");
	    sb.append("  environmentName: ").append(environmentName).append("\n");
	    sb.append("  environmentType: ").append(environmentType).append("\n");
	    sb.append("}\n");
	    return sb.toString();
	  }
	}