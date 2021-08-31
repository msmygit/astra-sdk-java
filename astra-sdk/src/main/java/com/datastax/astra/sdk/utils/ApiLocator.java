package com.datastax.astra.sdk.utils;

import com.datastax.stargate.sdk.utils.Assert;

/**
 * Utility class to find endpoints of the Astra APIs.
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public class ApiLocator {
    
    /** DEVOPS. */
    public static final String ASTRA_ENDPOINT_DEVOPS = "https://api.astra.datastax.com/v2";
    
    /** Building Astra base URL. */
    public static final String ASTRA_ENDPOINT_PREFIX  = "https://";
    
    /** Building Astra base URL. */
    public static final String ASTRA_ENDPOINT_SUFFIX  = ".apps.astra.datastax.com";
    
    /**
     * Hide constructor.
     */
    private ApiLocator() {}
    
    /**
     * Get the Devops endpoint.
     * 
     * @return
     *      the devops URL.
     */
    public static final String getApiDevopsEndpoint() {
        return ASTRA_ENDPOINT_DEVOPS;
    } 
   
   
    /**
     * REST and DOCUMENT endpoint for a database and region.
     *
     * @param dbId
     *      database identifier
     * @param dbRegion
     *      region identifier
     * @return
     *      the url to invoke
     */
    public static final String getApiRestEndpoint(String dbId, String dbRegion) {
        Assert.hasLength(dbId, "dbId");
        Assert.hasLength(dbRegion, "dbRegion");
        return new StringBuilder(ASTRA_ENDPOINT_PREFIX)
                .append(dbId).append("-").append(dbRegion)
                .append(ASTRA_ENDPOINT_SUFFIX)
                .append("/api/rest")
                .toString();
    }

    /**
     * GRAPHQL endpoint for a database and region working with Schema definition
     *
     * @param dbId
     *      database identifier
     * @param dbRegion
     *      region identifier
     * @return
     *      the url to invoke
     */
    public static final String getApiGraphQLEndPoint(String dbId, String dbRegion) {
        Assert.hasLength(dbId, "dbId");
        Assert.hasLength(dbRegion, "dbRegion");
        return new StringBuilder(ASTRA_ENDPOINT_PREFIX)
                .append(dbId).append("-").append(dbRegion)
                .append(ASTRA_ENDPOINT_SUFFIX)
                .append("/api/graphql")
                .toString();
    }
    /**
     * GRAPHQL endpoint for a database and region working with Schema definition
     *
     * @param dbId
     *      database identifier
     * @param dbRegion
     *      region identifier
     * @return
     *      the url to invoke
     */
    public static final String getApiGraphQLSchemaEndPoint(String dbId, String dbRegion) {
        return getApiGraphQLEndPoint(dbId, dbRegion) + "-schema";
    }
    
    /**
     * GRAPHQL endpoint for a database and region working with Schema definition
     *
     * @param dbId
     *      database identifier
     * @param dbRegion
     *      region identifier
     * @param keyspace
     *      keyspace identifier     
     * @return
     *      the url to invoke
     */public static final String getApiGraphQLEndPoint(String dbId, String dbRegion, String keyspace) {
         return getApiGraphQLEndPoint(dbId, dbRegion) + "/" + keyspace;
    }

}
