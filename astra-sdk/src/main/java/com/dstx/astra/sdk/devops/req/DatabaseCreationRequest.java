package com.dstx.astra.sdk.devops.req;

import com.dstx.astra.sdk.devops.CloudProviderType;
import com.dstx.astra.sdk.devops.DatabaseTierType;

/**
 * Database creation request
 *
 * @author Cedrick LUNVEN (@clunven)
 *
 */
public class DatabaseCreationRequest {
    
    /** Name of the database--user friendly identifier. */
    private String name;
    
    /** Keyspace name in database */
    private String keyspace;
    
    /** CloudProvider where the database lives. */
    private CloudProviderType cloudProvider;
    
    private DatabaseTierType tier = DatabaseTierType.developer;
    
    /**
     * CapacityUnits is the amount of space available (horizontal scaling) 
     * for the database. For free tier the max CU's is 1, and 100 
     * for CXX/DXX the max is 12 on startup.
     */
    private int capacityUnits = 1;
    
    private String region;
    
    private String user;
    
    private String password;
    
    public DatabaseCreationRequest() {}

    public DatabaseCreationRequest(DatabaseCreationBuilder builder) {
        this.capacityUnits = builder.capacityUnits;
        this.cloudProvider = builder.cloudProvider;
        this.keyspace      = builder.keyspace;
        this.name          = builder.name;
        this.password      = builder.password;
        this.region        = builder.region;
        this.tier          = builder.tier;
        this.user          = builder.user;
    }
    
    public static DatabaseCreationBuilder builder() {
        return new DatabaseCreationBuilder();
    }
    
    /**
     * Getter accessor for attribute 'name'.
     *
     * @return
     *       current value of 'name'
     */
    public String getName() {
        return name;
    }

    /**
     * Getter accessor for attribute 'keyspace'.
     *
     * @return
     *       current value of 'keyspace'
     */
    public String getKeyspace() {
        return keyspace;
    }

    /**
     * Getter accessor for attribute 'cloudProvider'.
     *
     * @return
     *       current value of 'cloudProvider'
     */
    public CloudProviderType getCloudProvider() {
        return cloudProvider;
    }

    /**
     * Getter accessor for attribute 'tier'.
     *
     * @return
     *       current value of 'tier'
     */
    public DatabaseTierType getTier() {
        return tier;
    }
    
    /**
     * Getter accessor for attribute 'capacityUnits'.
     *
     * @return
     *       current value of 'capacityUnits'
     */
    public int getCapacityUnits() {
        return capacityUnits;
    }
    
    /**
     * Getter accessor for attribute 'region'.
     *
     * @return
     *       current value of 'region'
     */
    public String getRegion() {
        return region;
    }

    /**
     * Getter accessor for attribute 'user'.
     *
     * @return
     *       current value of 'user'
     */
    public String getUser() {
        return user;
    }
  
    /**
     * Getter accessor for attribute 'password'.
     *
     * @return
     *       current value of 'password'
     */
    public String getPassword() {
        return password;
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return "DatabaseCreationRequest [name=" + name + ", keyspace=" + keyspace + ", cloudProvider=" + cloudProvider + ", tier="
                + tier + ", capacityUnits=" + capacityUnits + ", region=" + region + ", user=" + user + ", password=" + password
                + "]";
    }

}
