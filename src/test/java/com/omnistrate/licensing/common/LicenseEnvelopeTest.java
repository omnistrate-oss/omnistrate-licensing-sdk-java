package com.omnistrate.licensing.common;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.time.ZonedDateTime;
import java.util.Base64;

public class LicenseEnvelopeTest {

    @Test
    public void testNewLicenseEnvelope() {
        License license = new License("sku", "instanceID", "subscriptionID", "description", ZonedDateTime.now(), ZonedDateTime.now().plusDays(30));
        byte[] signature = "test-signature".getBytes();
        LicenseEnvelope le = new LicenseEnvelope(license, signature);

        assertNotNull(le);
        assertEquals(license, le.getLicense());
        assertArrayEquals(signature, le.getSignature());
    }

    @Test
    public void testIsValid() {
        License license = new License("sku", "instanceID", "subscriptionID", "description", ZonedDateTime.now(), ZonedDateTime.now().plusDays(30));
        byte[] signature = "test-signature".getBytes();
        LicenseEnvelope le = new LicenseEnvelope(license, signature);

        assertTrue(le.isValid());

        le = new LicenseEnvelope(license, new byte[0]);
        assertFalse(le.isValid());

        le = new LicenseEnvelope(null, signature);
        assertFalse(le.isValid());
    }

    @Test
    public void testIsExpired() {
        License license = new License("sku", "instanceID", "subscriptionID", "description", ZonedDateTime.now(), ZonedDateTime.now().plusDays(30));
        byte[] signature = "test-signature".getBytes();
        LicenseEnvelope le = new LicenseEnvelope(license, signature);

        assertFalse(le.isExpired());

        license = new License("sku", "instanceID", "subscriptionID", "description", ZonedDateTime.now(), ZonedDateTime.now().minusDays(1));
        le = new LicenseEnvelope(license, signature);
        assertTrue(le.isExpired());
    }

    @Test
    public void testToBytes() throws Exception {
        License license = new License("sku", "instanceID", "subscriptionID", "description", ZonedDateTime.now(), ZonedDateTime.now().plusDays(30));
        byte[] signature = "test-signature".getBytes();
        LicenseEnvelope le = new LicenseEnvelope(license, signature);

        byte[] bytes = le.toBytes();
        assertNotNull(bytes);
    }

    @Test
    public void testToString() {
        License license = new License("sku", "instanceID", "subscriptionID", "description", ZonedDateTime.now(), ZonedDateTime.now().plusDays(30));
        byte[] signature = "test-signature".getBytes();
        LicenseEnvelope le = new LicenseEnvelope(license, signature);

        String str = le.toString();
        assertNotNull(str);
    }

    @Test
    public void testEncodeBase64() {
        License license = new License("sku", "instanceID", "subscriptionID", "description", ZonedDateTime.now(), ZonedDateTime.now().plusDays(30));
        byte[] signature = "test-signature".getBytes();
        LicenseEnvelope le = new LicenseEnvelope(license, signature);

        String encoded = le.encodeBase64();
        assertNotNull(encoded);

        byte[] decoded = Base64.getDecoder().decode(encoded);
        assertNotNull(decoded);
    }

    @Test
    public void testParseBytes() throws Exception {
        License license = new License("sku", "instanceID", "subscriptionID", "description", ZonedDateTime.now(), ZonedDateTime.now().plusDays(30));
        byte[] signature = "test-signature".getBytes();
        LicenseEnvelope le = new LicenseEnvelope(license, signature);

        byte[] bytes = le.toBytes();
        LicenseEnvelope decodedLe = LicenseEnvelope.parseBytes(bytes);
        assertNotNull(decodedLe);
        assertEquals(le.getLicense(), decodedLe.getLicense());
        assertArrayEquals(le.getSignature(), decodedLe.getSignature());
    }

    @Test
    public void testParseString() throws Exception {
        License license = new License("sku", "instanceID", "subscriptionID", "description", ZonedDateTime.now(), ZonedDateTime.now().plusDays(30));
        byte[] signature = "test-signature".getBytes();
        LicenseEnvelope le = new LicenseEnvelope(license, signature);

        String str = le.toString();
        LicenseEnvelope decodedLe = LicenseEnvelope.parse(str);
        assertNotNull(decodedLe);
        assertEquals(le.getLicense(), decodedLe.getLicense());
        assertArrayEquals(le.getSignature(), decodedLe.getSignature());
    }

    @Test
    public void testParseBase64() throws Exception {
        License license = new License("sku", "instanceID", "subscriptionID", "description", ZonedDateTime.now(), ZonedDateTime.now().plusDays(30));
        byte[] signature = "test-signature".getBytes();
        LicenseEnvelope le = new LicenseEnvelope(license, signature);

        String encoded = le.encodeBase64();
        LicenseEnvelope decodedLe = LicenseEnvelope.parseBase64(encoded);
        assertNotNull(decodedLe);
        assertEquals(le.getLicense(), decodedLe.getLicense());
        assertArrayEquals(le.getSignature(), decodedLe.getSignature());
    }
}
