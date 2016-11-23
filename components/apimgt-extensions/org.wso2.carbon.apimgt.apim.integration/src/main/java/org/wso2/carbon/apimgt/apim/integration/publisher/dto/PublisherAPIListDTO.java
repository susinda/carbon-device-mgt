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


package org.wso2.carbon.apimgt.apim.integration.publisher.dto;

import java.util.List;

public class PublisherAPIListDTO {

	private String previous;
	private String next;
	private int count;
	private List<PublisherAPIDTO> list;

	public String getPrevious() {
		return previous;
	}

	public void setPrevious(String previous) {
		this.previous = previous;
	}

	public String getNext() {
		return next;
	}

	public void setNext(String next) {
		this.next = next;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public List<PublisherAPIDTO> getList() {
		return list;
	}

	public void setList(List<PublisherAPIDTO> list) {
		this.list = list;
	}
	
	 @Override
	  public String toString()  {
	    StringBuilder sb = new StringBuilder();
	    sb.append("class PublisherAPIListDTO {\n");
	    
	    sb.append("  count: ").append(count).append("\n");
	    sb.append("  next: ").append(next).append("\n");
	    sb.append("  previous: ").append(previous).append("\n");
	    sb.append("  list: ").append(list).append("\n");
	    sb.append("}\n");
	    return sb.toString();
	  }

}
