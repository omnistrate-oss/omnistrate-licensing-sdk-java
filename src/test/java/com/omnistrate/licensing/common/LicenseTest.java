package com.omnistrate.licensing.common;

import org.junit.jupiter.api.Test;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.*;

public class LicenseTest {

    @Test
    public void testNewLicense() {
        ZonedDateTime creationTime = ZonedDateTime.now();
        ZonedDateTime expirationTime = creationTime.plusDays(1);
        License license = new License("SKU", "instance-id", "sub-id", "desc", creationTime, expirationTime);

        assertNotNull(license.getId());
        assertEquals("SKU", license.getSku());
        assertEquals("desc", license.getDescription());
        assertEquals("sub-id", license.getSubscriptionID());
        assertEquals("instance-id", license.getInstanceID());
        assertEquals(creationTime.format(DateTimeFormatter.ISO_ZONED_DATE_TIME), license.getCreationTime().format(DateTimeFormatter.ISO_ZONED_DATE_TIME));
        assertEquals(expirationTime.format(DateTimeFormatter.ISO_ZONED_DATE_TIME), license.getExpirationTime().format(DateTimeFormatter.ISO_ZONED_DATE_TIME));
        assertEquals(1, license.getVersion());
    }

    @Test
    public void testGetExpirationTime() {
        License license = new License("SKU", "instance-id", "sub-id", "desc", ZonedDateTime.now(), ZonedDateTime.now().plusDays(1));
        ZonedDateTime expirationTime = license.getExpirationTime();
        assertNotNull(expirationTime);
    }

    @Test
    public void testRenew() {
        License license = new License("SKU", "instance-id", "sub-id", "desc", ZonedDateTime.now(), ZonedDateTime.now().plusDays(1));
        ZonedDateTime newExpirationTime = ZonedDateTime.now().plusYears(1);
        license.renew(newExpirationTime);

        assertEquals(newExpirationTime.format(DateTimeFormatter.ISO_ZONED_DATE_TIME), license.getExpirationTime().format(DateTimeFormatter.ISO_ZONED_DATE_TIME));
    }

    @Test
    public void testGetCreationTime() {
        License license = new License("SKU", "instance-id", "sub-id", "desc", ZonedDateTime.now(), ZonedDateTime.now().plusDays(1));
        ZonedDateTime creationTime = license.getCreationTime();
        assertNotNull(creationTime);
    }

    @Test
    public void testIsValid() {
        License validLicense = new License("SKU", "instance-id", "sub-id", "desc", ZonedDateTime.now(), ZonedDateTime.now().plusDays(1));
        License invalidLicense = new License(null, "instance-id", "sub-id", "desc", ZonedDateTime.now(), ZonedDateTime.now().plusDays(1));

        try {
            validLicense.isValid("SKU", "instance-id");
        } catch (InvalidLicenseException e) {
            fail("Should not throw an exception");
        }
        try {
            invalidLicense.isValid("SKU", "instance-id");
            fail("Should throw an exception");
        } catch (InvalidLicenseException e) {
            assertEquals("Missing required fields", e.getReason());
        }
    }

    @Test
    public void testIsExpired() {
        License expiredLicense = new License("SKU", "instance-id", "sub-id", "desc", ZonedDateTime.now().minusDays(2), ZonedDateTime.now().minusDays(1));
        License validLicense = new License("SKU", "instance-id", "sub-id", "desc", ZonedDateTime.now(), ZonedDateTime.now().plusDays(1));

        assertTrue(expiredLicense.isExpired());
        assertFalse(validLicense.isExpired());
    }

    @Test
    public void testToBytes() {
        License license = new License("SKU", "instance-id", "sub-id", "desc", ZonedDateTime.now(), ZonedDateTime.now().plusDays(1));
        byte[] bytes = license.toBytes();
        assertNotNull(bytes);
    }

    @Test
    public void testToString() {
        License license = new License("SKU", "instance-id", "sub-id", "desc", ZonedDateTime.now(), ZonedDateTime.now().plusDays(1));
        String jsonString = license.toString();
        assertNotNull(jsonString);
    }

    @Test
    public void testParse() throws Exception {
        String jsonString = "{\"ID\":\"1234\",\"CreationTime\":\"2021-01-01T00:00:00Z\",\"ExpirationTime\":\"2022-02-01T00:00:00Z\",\"Description\":\"desc\",\"InstanceID\":\"instance-id\",\"SubscriptionID\":\"sub-id\",\"SKU\":\"SKU\",\"Version\":1}";
        License license = License.parse(jsonString);

        assertEquals("1234", license.getId());
        assertEquals("2021-01-01T00:00:00Z", license.getCreationTime().format(DateTimeFormatter.ISO_ZONED_DATE_TIME));
        assertEquals("2022-02-01T00:00:00Z", license.getExpirationTime().format(DateTimeFormatter.ISO_ZONED_DATE_TIME));
        assertEquals("desc", license.getDescription());
        assertEquals("instance-id", license.getInstanceID());
        assertEquals("sub-id", license.getSubscriptionID());
        assertEquals("SKU", license.getSku());
        assertEquals(1, license.getVersion());
    }

    @Test   
    public void testSerialize() {
        License license = new License("SKU", "instance-id", "sub-id", "desc", ZonedDateTime.now(), ZonedDateTime.now().plusDays(1));
        String jsonString = license.toString();
        assertNotNull(jsonString);
        try {
            License license2 = License.parse(jsonString);
            assertEquals(license, license2);
        } catch (Exception e) {
            fail("Should not throw an exception");
        }
    }

    @Test
    public void testIsValidWithSKU() {
        License validLicense = new License("SKU", "instance-id", "sub-id", "desc", ZonedDateTime.parse("2021-01-01T00:00:00Z"), ZonedDateTime.parse("2022-01-01T00:00:00Z"));
        License invalidLicense = new License("", "instance-id", "sub-id", "desc", ZonedDateTime.parse("2021-01-01T00:00:00Z"), ZonedDateTime.parse("2022-01-01T00:00:00Z"));

        try {
            validLicense.isValid("", "");
            fail("Should not throw an exception");
        } catch (InvalidLicenseException e) {
            // Expected
        }

        try {
            validLicense.isValid("SKU", "");
            fail("Should not throw an exception");
        } catch (InvalidLicenseException e) {
            // Expected
        }

        try {
            validLicense.isValid("INVALID", "");
            fail("Should throw an exception");
        } catch (InvalidLicenseException e) {
            // Expected
        }

        try {
            invalidLicense.isValid("SKU", "");
            fail("Should throw an exception");
        } catch (InvalidLicenseException e) {
            // Expected
        }
    }

    @Test
    public void testIsValidWithInstanceID() {
        License validLicense = new License("SKU", "instance-id", "sub-id", "desc", ZonedDateTime.parse("2021-01-01T00:00:00Z"), ZonedDateTime.parse("2022-01-01T00:00:00Z"));
        License invalidLicense = new License("", "instance-id", "sub-id", "desc", ZonedDateTime.parse("2021-01-01T00:00:00Z"), ZonedDateTime.parse("2022-01-01T00:00:00Z"));

        try {
            validLicense.isValid("", "");
        } catch (InvalidLicenseException e) {
            fail("Should not throw an exception");
        }

        try {
            validLicense.isValid("SKU", "instance-id");
        } catch (InvalidLicenseException e) {
            fail("Should not throw an exception");
        }

        try {
            invalidLicense.isValid("SKU", "INVALID");
            fail("Should throw an exception");
        } catch (InvalidLicenseException e) {
            // Expected
        }

        try {
            invalidLicense.isValid("SKU", "instance-id");
            fail("Should throw an exception");
        } catch (InvalidLicenseException e) {
            // Expected
        }
    }
}
