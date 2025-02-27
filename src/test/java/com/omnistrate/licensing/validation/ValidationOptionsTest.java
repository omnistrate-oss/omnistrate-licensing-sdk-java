package com.omnistrate.licensing.validation;

import org.junit.jupiter.api.Test;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class ValidationOptionsTest {

    @Test
    public void testBuilder() {
        ZonedDateTime currentTime = ZonedDateTime.now(ZoneOffset.UTC);
        ValidationOptions options = new ValidationOptions.Builder()
            .skipCertificateValidation(true)
            .certificateDomain("example.com")
            .currentTime(currentTime)
            .certPath("/path/to/cert")
            .licensePath("/path/to/license")
            .productPlanUniqueID("product-plan-id")
            .organizationID("org-id")
            .instanceID("instance-id")
            .build();

        assertTrue(options.isSkipCertificateValidation());
        assertEquals("example.com", options.getCertificateDomain());
        assertEquals(currentTime, options.getCurrentTime());
        assertEquals("/path/to/cert", options.getCertPath());
        assertEquals("/path/to/license", options.getLicensePath());
        assertEquals("instance-id", options.getInstanceID());
        assertEquals("product-plan-id", options.getProductPlanUniqueID());
        assertEquals("org-id", options.getOrganizationID());
    }

    @Test
    public void testBuilderDefaults() {
        ValidationOptions options = new ValidationOptions.Builder().build();

        assertFalse(options.isSkipCertificateValidation());
        assertEquals(options.getCertificateDomain(), "licensing.omnistrate.cloud");
        assertNotNull(options.getCurrentTime());
        assertNull(options.getCertPath());
        assertNull(options.getLicensePath());
        assertNull(options.getProductPlanUniqueID());
        assertNull(options.getOrganizationID());
        assertNull(options.getInstanceID());
    }
}
