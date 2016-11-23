/*
 *   Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *   WSO2 Inc. licenses this file to you under the Apache License,
 *   Version 2.0 (the "License"); you may not use this file except
 *   in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing,
 *   software distributed under the License is distributed on an
 *   "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *   KIND, either express or implied.  See the License for the
 *   specific language governing permissions and limitations
 *   under the License.
 *
 */

package org.wso2.carbon.device.mgt.jaxrs.service.api;

import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Info;
import io.swagger.annotations.ExtensionProperty;
import io.swagger.annotations.Extension;
import io.swagger.annotations.Tag;
import io.swagger.annotations.Api;
import org.wso2.carbon.apimgt.annotations.api.Permission;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Device Analytics Dashboard related REST-APIs. This can be used to obtain device related analytics.
 */
@SwaggerDefinition(
        info = @Info(
                version = "1.0.0",
                title = "",
                extensions = {
                        @Extension(properties = {
                                @ExtensionProperty(name = "name", value = "DeviceAnalyticsDashboard"),
                                @ExtensionProperty(name = "context", value = "/api/device-mgt/v1.0/dashboard"),
                        })
                }
        ),
        tags = {
                @Tag(name = "device_management", description = "")
        }
)
@Path("/dashboard")
@Api(value = "Device Analytics Dashboard",
        description = "Device Analytics Dashboard related information APIs are described here.")
@Produces(MediaType.APPLICATION_JSON)
@SuppressWarnings("NonJaxWsWebServices")
public interface Dashboard {

    String CONNECTIVITY_STATUS = "connectivity-status";
    String POTENTIAL_VULNERABILITY = "potential-vulnerability";
    String NON_COMPLIANT_FEATURE_CODE = "non-compliant-feature-code";
    String PLATFORM = "platform";
    String OWNERSHIP = "ownership";
    // Constants related to pagination
    String PAGINATION_ENABLED = "pagination-enabled";
    String START_INDEX = "start";
    String RESULT_COUNT = "length";

    @GET
    @Path("device-count-overview")
    @Permission(name = "View Dashboard", permission = "/device-mgt/dashboard/view")
    Response getOverviewDeviceCounts();

    @GET
    @Path("device-counts-by-potential-vulnerabilities")
    @Permission(name = "View Dashboard", permission = "/device-mgt/dashboard/view")
    Response getDeviceCountsByPotentialVulnerabilities();

    @GET
    @Path("non-compliant-device-counts-by-features")
    @Permission(name = "View Dashboard", permission = "/device-mgt/dashboard/view")
    Response getNonCompliantDeviceCountsByFeatures(@QueryParam(START_INDEX) int startIndex,
                                                   @QueryParam(RESULT_COUNT) int resultCount);

    @GET
    @Path("device-counts-by-groups")
    @Permission(name = "View Dashboard", permission = "/device-mgt/dashboard/view")
    Response getDeviceCountsByGroups(@QueryParam(CONNECTIVITY_STATUS) String connectivityStatus,
                                     @QueryParam(POTENTIAL_VULNERABILITY) String potentialVulnerability,
                                     @QueryParam(PLATFORM) String platform,
                                     @QueryParam(OWNERSHIP) String ownership);

    @GET
    @Path("feature-non-compliant-device-counts-by-groups")
    @Permission(name = "View Dashboard", permission = "/device-mgt/dashboard/view")
    Response getFeatureNonCompliantDeviceCountsByGroups(@QueryParam(NON_COMPLIANT_FEATURE_CODE) String nonCompliantFeatureCode,
                                                        @QueryParam(PLATFORM) String platform,
                                                        @QueryParam(OWNERSHIP) String ownership);
    @GET
    @Path("filtered-device-count-over-total")
    @Permission(name = "View Dashboard", permission = "/device-mgt/dashboard/view")
    Response getFilteredDeviceCountOverTotal(@QueryParam(CONNECTIVITY_STATUS) String connectivityStatus,
                                             @QueryParam(POTENTIAL_VULNERABILITY) String potentialVulnerability,
                                             @QueryParam(PLATFORM) String platform,
                                             @QueryParam(OWNERSHIP) String ownership);

    @GET
    @Path("feature-non-compliant-device-count-over-total")
    @Permission(name = "View Dashboard", permission = "/device-mgt/dashboard/view")
    Response getFeatureNonCompliantDeviceCountOverTotal(@QueryParam(NON_COMPLIANT_FEATURE_CODE) String nonCompliantFeatureCode,
                                                        @QueryParam(PLATFORM) String platform,
                                                        @QueryParam(OWNERSHIP) String ownership);

    @GET
    @Path("devices-with-details")
    @Permission(name = "View Dashboard", permission = "/device-mgt/dashboard/view")
    Response getDevicesWithDetails(@QueryParam(CONNECTIVITY_STATUS) String connectivityStatus,
                                   @QueryParam(POTENTIAL_VULNERABILITY) String potentialVulnerability,
                                   @QueryParam(PLATFORM) String platform,
                                   @QueryParam(OWNERSHIP) String ownership,
                                   @QueryParam(PAGINATION_ENABLED) String paginationEnabled,
                                   @QueryParam(START_INDEX) int startIndex,
                                   @QueryParam(RESULT_COUNT) int resultCount);

    @GET
    @Path("feature-non-compliant-devices-with-details")
    @Permission(name = "View Dashboard", permission = "/device-mgt/dashboard/view")
    Response getFeatureNonCompliantDevicesWithDetails(@QueryParam(NON_COMPLIANT_FEATURE_CODE) String nonCompliantFeatureCode,
                                                      @QueryParam(PLATFORM) String platform,
                                                      @QueryParam(OWNERSHIP) String ownership,
                                                      @QueryParam(PAGINATION_ENABLED) String paginationEnabled,
                                                      @QueryParam(START_INDEX) int startIndex,
                                                      @QueryParam(RESULT_COUNT) int resultCount);
}
