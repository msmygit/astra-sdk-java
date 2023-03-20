package com.dtsx.astra.sdk.streaming;

import com.dtsx.astra.sdk.AbstractApiClient;
import com.dtsx.astra.sdk.streaming.exception.TenantNotFoundException;
import com.dtsx.astra.sdk.utils.ApiLocator;
import com.dtsx.astra.sdk.utils.Assert;
import com.dtsx.astra.sdk.utils.JsonUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.dtsx.astra.sdk.streaming.domain.CreateTenant;
import com.dtsx.astra.sdk.streaming.domain.Tenant;

import java.net.HttpURLConnection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Group resources of streaming (tenants, providers).
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public class AstraStreamingClient extends AbstractApiClient {

    /**
     * As immutable object use builder to initiate the object.
     *
     * @param token
     *      authenticated token
     */
    public AstraStreamingClient(String token) {
        super(token);
    }

    /**
     * List tenants in the current instance.
     * 
     * @return
     *      list of tenants.
     */
    public Stream<Tenant> findAll() {
        return JsonUtils
                .unmarshallType(
                        GET(getApiDevopsEndpointTenants()).getBody(),
                        new TypeReference<List<Tenant>>(){})
                .stream();
    }

    /**
     * Find a tenant from ids name.
     *
     * @return
     *      tenant
     */
    public Optional<Tenant> find(String tenantName) {
        return findAll()
                .filter(t -> t.getTenantName().equalsIgnoreCase(tenantName))
                .findFirst();
    }

    /**
     * Assess a tenant exist and retrieve information.
     *
     * @return
     *      tenant reference
     */
    public Tenant get(String tenantName) {
        return find(tenantName).orElseThrow(() -> new TenantNotFoundException(tenantName));
    }

    /**
     * Syntax sugar to help
     *
     * @param ct
     *      creation request for tenant
     */
    public void create(CreateTenant ct) {
        Assert.notNull(ct, "Create Tenant request");
        POST(AstraStreamingClient.getApiDevopsEndpointTenants(), JsonUtils.marshall(ct));
    }

    /**
     * Deleting a tenant and cluster.
     */
    public void delete(String tenantName) {
        Tenant tenant = get(tenantName);
        DELETE(getEndpointCluster(tenant.getTenantName(), tenant.getClusterName()));
    }

    /**
     * Check if a role is present
     *
     * @return
     *      if the tenant exist
     */
    public boolean exist(String tenantName) {
        return HEAD(getEndpointTenant(tenantName)).getCode() == HttpURLConnection.HTTP_OK;
    }

    // ---------------------------------
    // ----   Tenant Specifics      ----
    // ---------------------------------

    /**
     * Access methods for a tenant.
     *
     * @param tenantName
     *      current tenant
     * @return
     *      client for a tenant
     */
    public TenantClient tenant(String tenantName) {
        return new TenantClient(token, tenantName);
    }

    // ---------------------------------
    // ----       Clusters         ----
    // ---------------------------------

    /**
     * Operation on Streaming Clusters.
     *
     * @return
     *      streaming cluster client
     */
    public ClustersClient clusters() {
        return new ClustersClient(token);
    }

    // ---------------------------------
    // ----       Providers         ----
    // ---------------------------------

    /**
     * Operation on Streaming Clusters.
     *
     * @return
     *      streaming cluster client
     */
    public ProvidersClient providers() {
        return new ProvidersClient(token);
    }

    // ---------------------------------
    // ----       Regions           ----
    // ---------------------------------

    /**
     * Operation on Streaming regions.
     *
     * @return
     *      streaming cluster client
     */
    public RegionsClient regions() {
        return new RegionsClient(token);
    }
    
    // ---------------------------------
    // ----       Utilities         ----
    // ---------------------------------

    /**
     * Endpoint to access schema for namespace.
     *
     * @return
     *      endpoint
     */
    public static String getApiDevopsEndpointStreaming() {
        return ApiLocator.getApiDevopsEndpoint() + "/streaming";
    }

    /**
     * Endpoint to access schema for namespace.
     *
     * @return
     *      endpoint
     */
    public static String getApiDevopsEndpointTenants() {
        return getApiDevopsEndpointStreaming() + "/tenants";
    }

    /**
     * Endpoint to access dbs.
     *
     * @return
     *      database endpoint
     */
    public static String getEndpointTenant(String tenantId) {
        return getApiDevopsEndpointTenants() + "/" + tenantId;
    }

    /**
     * Endpoint to access cluster.
     *
     * @param clusterId
     *      identifier for the cluster.
     *
     * @return
     *      database endpoint
     */
    public String getEndpointCluster(String tenantId, String clusterId) {
        return getEndpointTenant(tenantId) + "/clusters/" + clusterId;
    }
}
