package com.datastax.stargate.sdk.test;

import org.junit.AfterClass;
import org.junit.jupiter.api.BeforeAll;

import com.datastax.stargate.sdk.doc.test.ApiDocumentCollectionsTest;

/**
 * Execute some unit tests agains collections.
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public class StargateApiDocumentCollectionsTest extends ApiDocumentCollectionsTest {
     
    /**
     * Init
     */
    @BeforeAll
    public static void init() {
        stargateClient = ApiStargateTestFactory.createStargateClient();
        nsClient = stargateClient.apiDocument().namespace(TEST_NAMESPACE);
        // We need the namespace
        if (!nsClient.exist()) {
            nsClient.createSimple(1);
        }
    }
    
    /**
     * Close connections when ending
     */
    @AfterClass
    public static void closing() {
        if (stargateClient != null) {
            stargateClient.close();
        }
    }

}
