package com.omnistrate.licensing.validator;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ValidatorConfigTest {

    private static final String DEFAULT_CERT_PATH = ValidatorConfig.DEFAULT_CERT_PATH;
    private static final String DEFAULT_LICENSE_PATH = ValidatorConfig.DEFAULT_LICENSE_PATH;

    @Test
    public void testNewValidatorConfigDefault() {
        ValidatorConfig config = new ValidatorConfig();
        assertEquals(DEFAULT_CERT_PATH, config.getCertPath());
        assertEquals(DEFAULT_LICENSE_PATH, config.getLicensePath());
        assertEquals("", config.getInstanceID());

        config = new ValidatorConfig("", "", "");
        assertEquals(DEFAULT_CERT_PATH, config.getCertPath());
        assertEquals(DEFAULT_LICENSE_PATH, config.getLicensePath());
        assertEquals("", config.getInstanceID());


        config = new ValidatorConfig("instance-id", "", "");
        assertEquals(DEFAULT_CERT_PATH, config.getCertPath());
        assertEquals(DEFAULT_LICENSE_PATH, config.getLicensePath());
        assertEquals("instance-id", config.getInstanceID());
    }

    @Test
    public void testNewValidatorConfig() {
        ValidatorConfig config2 = new ValidatorConfig("instance-id", "/custom/cert/path", "/custom/license/path");
        assertEquals("/custom/cert/path", config2.getCertPath());
        assertEquals("/custom/license/path", config2.getLicensePath());
        assertEquals("instance-id", config2.getInstanceID());
    }

    @Test
    public void testValidatorConfigIsValid() {
        ValidatorConfig config = new ValidatorConfig("", "", "");
        assertTrue(config.isValid());

        config = new ValidatorConfig("", "/custom/cert/path", "");
        assertTrue(config.isValid());

        config = new ValidatorConfig("instance-id", "", "");
        assertTrue(config.isValid());

        config = new ValidatorConfig("instance-id", "/custom/cert/path", "");
        assertTrue(config.isValid());

        config = new ValidatorConfig("instance-id", "/custom/cert/path", "/custom/license/path");
        assertTrue(config.isValid());
    }
}
