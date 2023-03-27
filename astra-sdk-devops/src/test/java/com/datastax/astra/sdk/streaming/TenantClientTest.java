package com.datastax.astra.sdk.streaming;

import com.datastax.astra.sdk.devops.AbstractDevopsApiTest;
import com.dtsx.astra.sdk.streaming.AstraStreamingClient;
import com.dtsx.astra.sdk.streaming.domain.CreateTenant;
import org.junit.Assert;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

/**
 * Work with tenant.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TenantClientTest extends AbstractDevopsApiTest {

    /** Logger for our Client. */
    private static final Logger LOGGER = LoggerFactory.getLogger(AstraStreamingClientTest.class);

    private static String tmpTenant = "sdk-java-junit-" + UUID.randomUUID().toString().substring(0,7);

    @Test
    @Order(1)
    public void shouldCreateTenant() throws InterruptedException {
        // Given
        AstraStreamingClient sc  = new AstraStreamingClient(getToken());
        Assert.assertFalse(sc.exist(tmpTenant));
        // When
        sc.create(CreateTenant.builder()
                .tenantName(tmpTenant)
                .userEmail("astra-cli@datastax.com").build());
        // Then
        Thread.sleep(1000);
        Assert.assertTrue(sc.exist(tmpTenant));
    }

    @Test
    @Order(2)
    public void shouldDeleteTenant() throws InterruptedException {
        AstraStreamingClient sc  = new AstraStreamingClient(getToken());
        // Giving
        Assert.assertTrue(sc.exist(tmpTenant));
        // When
        sc.delete(tmpTenant);
        Thread.sleep(1000);
        // Then
        Assert.assertFalse(sc.exist(tmpTenant));
    }
}
