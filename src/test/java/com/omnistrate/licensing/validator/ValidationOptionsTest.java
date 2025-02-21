package com.omnistrate.licensing.validator;

import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class ValidationOptionsTest {

    @Test
    public void testBuilder() {
        ZonedDateTime currentTime = ZonedDateTime.now();
        ValidationOptions options = new ValidationOptions.Builder()
            .skipCertificateValidation(true)
            .certificateDomain("example.com")
            .currentTime(currentTime)
            .certPath("/path/to/cert")
            .licensePath("/path/to/license")
            .sku("SKU123")
            .instanceID("instance-id")
            .build();

        assertTrue(options.isSkipCertificateValidation());
        assertEquals("example.com", options.getCertificateDomain());
        assertEquals(currentTime, options.getCurrentTime());
        assertEquals("/path/to/cert", options.getCertPath());
        assertEquals("/path/to/license", options.getLicensePath());
        assertEquals("SKU123", options.getSku());
        assertEquals("instance-id", options.getInstanceID());
    }
}
