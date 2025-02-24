package com.omnistrate.licensing.validation;

import com.omnistrate.licensing.certificate.CertificateUtils;
import com.omnistrate.licensing.common.InvalidSignatureException;
import com.omnistrate.licensing.common.License;
import com.omnistrate.licensing.common.LicenseEnvelope;

import org.junit.jupiter.api.Test;

import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class ValidatorTest {

    private static final String TEST_CERTIFICATE = "-----BEGIN CERTIFICATE-----\n" +
        "MIIFCjCCA/KgAwIBAgISBIalD5cMH3mevZGYjxxIAFOFMA0GCSqGSIb3DQEBCwUA\n" +
        "MDMxCzAJBgNVBAYTAlVTMRYwFAYDVQQKEw1MZXQncyBFbmNyeXB0MQwwCgYDVQQD\n" +
        "EwNSMTEwHhcNMjUwMjExMTQxMDM5WhcNMjUwNTEyMTQxMDM4WjAoMSYwJAYDVQQD\n" +
        "Ex1saWNlbnNpbmctdGVzdC5vbW5pc3RyYXRlLmRldjCCASIwDQYJKoZIhvcNAQEB\n" +
        "BQADggEPADCCAQoCggEBANrjkfEo81CuqyTdinRso3PtIcvXj9KRIZ/JXG+T1wHQ\n" +
        "/qWEg0bDSq1hsMy+oRhgD4iV0UJZ65iK1RLWvapH9lDp6+VSPjlPLCnk6BTpwDlT\n" +
        "A86em85qD3IjPf1iI/+7FClC2byavFqr/G2zpEvhELb+On/tHgl8oXe6nqSz/kzt\n" +
        "FiPmEh9IyJ4KzARvyEODemfHNRlezfUKOX6YcGfVOOlngsIhtMYfOHv8QxkQ63wX\n" +
        "CM9DiCo7+V0nOTxyC8c3Nakem44saHqe7wLYuXKXm9SMk+feePTIiBmjbQJLz8uO\n" +
        "lwurRuBM2Hsg9w8OP3FrkKIK3XPm11edmeHwQZOS6h8CAwEAAaOCAiEwggIdMA4G\n" +
        "A1UdDwEB/wQEAwIFoDAdBgNVHSUEFjAUBggrBgEFBQcDAQYIKwYBBQUHAwIwDAYD\n" +
        "VR0TAQH/BAIwADAdBgNVHQ4EFgQUOhVq75FYA2NjKPvSkU/tz6VzTukwHwYDVR0j\n" +
        "BBgwFoAUxc9GpOr0w8B6bJXELbBeki8m47kwVwYIKwYBBQUHAQEESzBJMCIGCCsG\n" +
        "AQUFBzABhhZodHRwOi8vcjExLm8ubGVuY3Iub3JnMCMGCCsGAQUFBzAChhdodHRw\n" +
        "Oi8vcjExLmkubGVuY3Iub3JnLzAoBgNVHREEITAfgh1saWNlbnNpbmctdGVzdC5v\n" +
        "bW5pc3RyYXRlLmRldjATBgNVHSAEDDAKMAgGBmeBDAECATCCAQQGCisGAQQB1nkC\n" +
        "BAIEgfUEgfIA8AB1AMz7D2qFcQll/pWbU87psnwi6YVcDZeNtql+VMD+TA2wAAAB\n" +
        "lPWOlQ0AAAQDAEYwRAIgY4ITX6sdDjREvaXIoitgO6EKP2pSSy1noQMJwEnaRnAC\n" +
        "ICkxKoaxpBUChDK0l2olkohMy4BLRBhrZ4aoNswWtbZ0AHcAzxFW7tUufK/zh1vZ\n" +
        "aS6b6RpxZ0qwF+ysAdJbd87MOwgAAAGU9Y6VMgAABAMASDBGAiEAiSPhxVJpmcA/\n" +
        "WLwi+Av87bPwjpY0oEQtHJNNf8blmqMCIQDrbJue/LOb6HfTni/2A74sCU8+V+bO\n" +
        "IdTCbUjAcZG5kzANBgkqhkiG9w0BAQsFAAOCAQEAQ+GRLVEX6+3ZrgK49mOc23Ne\n" +
        "UNTg82Rwh9AerKHBnwNLS0sR6M93GxiYrWW0JH71YwZuLQlHwFYjq8c4zY/xqZs4\n" +
        "/DrPz/cwlSuw2jdtUo3Tt9Mh/xTVuCWsjJVHm42VqRulGYtS5IA2VRjDikCay/1l\n" +
        "qAM2hKAUy8pMQ9+SechHZYWi8YVcp+RgnKB3qtbqFDbFMMbEEDao7Y1DFh4TwXdh\n" +
        "k/akWnY4eLILCSfZ+9zkt4GL2J7wU5k1P4+p6lxRyG8WF/2klTP3MreeGv4NXFNu\n" +
        "1euwTONgE5mpWBAZ4GxR3UFQ37Q9kENFzsks4drS2j7JWZqUMR6OoICtrzqJHw==\n" +
        "-----END CERTIFICATE-----";

    private static final String TEST_PRIVATE_KEY = "-----BEGIN RSA PRIVATE KEY-----\n" +
        "MIIEowIBAAKCAQEA2uOR8SjzUK6rJN2KdGyjc+0hy9eP0pEhn8lcb5PXAdD+pYSD\n" +
        "RsNKrWGwzL6hGGAPiJXRQlnrmIrVEta9qkf2UOnr5VI+OU8sKeToFOnAOVMDzp6b\n" +
        "zmoPciM9/WIj/7sUKULZvJq8Wqv8bbOkS+EQtv46f+0eCXyhd7qepLP+TO0WI+YS\n" +
        "H0jIngrMBG/IQ4N6Z8c1GV7N9Qo5fphwZ9U46WeCwiG0xh84e/xDGRDrfBcIz0OI\n" +
        "Kjv5XSc5PHILxzc1qR6bjixoep7vAti5cpeb1IyT59549MiIGaNtAkvPy46XC6tG\n" +
        "4EzYeyD3Dw4/cWuQogrdc+bXV52Z4fBBk5LqHwIDAQABAoIBAQDZwNzWCcQMtsch\n" +
        "dJehfNYapIQjkn3Mn37Qw/QGOBAECZLavHoQcoWe+Hl3JgmDA0VQvqIfnO7ooOyZ\n" +
        "wjGK7e4Xzq5TQz8NqjqoLsXG0EfffzthWJ5teUuGY5LuuKxRYx4KcivEGjdZCJGl\n" +
        "3LmMlwkW0tEFpLFmBKtpPalG9pz8jmRamaZ/hsNv3ItKHPCMsS+0yKQukT0oMrr0\n" +
        "MXMK+q5hNfhfaqOpUPgGMpqv98v/OHu9ocQkrmcrheGFZlY0aWj9r2qF8l6gD7rg\n" +
        "03/E1GOr463u9iAzGIUVZToO8qKER/JGknswlYMLbnBLeDeE0PB0sdbR5UiE0aSM\n" +
        "jNMVu5kpAoGBAOSVTsuf/gy4H939CPBytxWBLq9gIM2x2VDbyOIuwAVYhyMwqU5W\n" +
        "Nhv47ad/T4a009DllqKg6E0Xs82MCj7DxpUoDprvvri2L4gKnFSGsxurpeTrgfu5\n" +
        "7O6VUr8uR0zZy5YPTvyV7WKwDnzzeEvgvLYFrPsxU9diUkkYr3a6aGNFAoGBAPUk\n" +
        "mOmRDf6AfaMmlunO/kSLx+qEgb8/qVj4pNBjRVf1LNc+j5kUA8qASlDIG0nal1VW\n" +
        "hof09afrw5rTi3mmTKx911Rc9hnWduv80xfijPjAT9VnmG5yDvpfqNQahzCGtMaW\n" +
        "gqlXLLX2GflPHq6jYCYdt+DrEAMWVehx47r6mBwTAoGAJSDPZKm/JSe/HVqdWhj3\n" +
        "/gk2oQvXKDIMH2MjtQFs9TFU+fVMesnsDg0X5Q37x+CVSygPmKjUMrl33hoyC9HW\n" +
        "7qFMBgSNEQmenNVEYUJj00nFL3LFcTLY9kPQ5fbJZAU4WU5xQPvFTDNWlxsg3gpJ\n" +
        "/LLdTwvLF4AD55WX4owSqpUCgYBD73p47/wC1bT7q8wSy4xnjp0+fq4+Q/Q4dd+m\n" +
        "6HvlHkro86tz7roGOntd27bERtG9kpcBwn75pDq3A30Zn16MRuuPs5t4GZknWQ9f\n" +
        "BSewIAt/xo/Vu8Iu3Ke68AcbCZm7lyZqq3/2hVc5YhXMq/YPWhkFOd9cG6p/GmDU\n" +
        "BnJ1BQKBgAS+8ygsHn8L7vMpLcV6ZN5ZPKhpdl+jnxIRcP24bE1s3057NuZ7k4x9\n" +
        "wUWttccGDuiVj6uStbNPtRN0cJdKACOuNXgdYlFL/ABZnHaVS3/o8eUgU0a7Libq\n" +
        "6vXaLbQeJzgKrbMNp3uw52DL+Mp8HIYQjDvnMs2HEoj08vxFZQZN\n" +
        "-----END RSA PRIVATE KEY-----";

    @Test
    public void testNewValidator() {
        try {
            Validator validator = new Validator(TEST_CERTIFICATE.getBytes());
            assertNotNull(validator);
        } catch (Exception e) {
            fail("Exception should not be thrown: " + e.getMessage());
        }
    }

    @Test
    public void testValidateLicense() {
        try {
            ZonedDateTime now = ZonedDateTime.now();
            X509Certificate cert = CertificateUtils.loadCertificateFromString(TEST_CERTIFICATE);
            PrivateKey privateKey = CertificateUtils.loadPrivateKeyFromString(TEST_PRIVATE_KEY);

            License license = new License("SKU", "instance-1", "subs-1", "product a", now, now.plusDays(2));
            byte[] licenseBytes = license.toBytes();
            byte[] signature = CertificateUtils.sign(privateKey, licenseBytes);

            LicenseEnvelope envelope = new LicenseEnvelope(license, signature);

            Validator validator = new Validator(cert);
            assertTrue(validator.validateLicense(envelope, "SKU", "instance-1", now));
        } catch (Exception e) {
            fail("Exception should not be thrown: " + e.getMessage());
        }
    }

    @Test
    public void testValidateLicenseBase64() {
        try {
            ZonedDateTime now = ZonedDateTime.now();
            X509Certificate cert = CertificateUtils.loadCertificateFromString(TEST_CERTIFICATE);
            PrivateKey privateKey = CertificateUtils.loadPrivateKeyFromString(TEST_PRIVATE_KEY);

            License license = new License("SKU", "instance-1", "subs-1", "product a", now, now.plusDays(2));
            byte[] licenseBytes = license.toBytes();
            byte[] signature = CertificateUtils.sign(privateKey, licenseBytes);

            LicenseEnvelope envelope = new LicenseEnvelope(license, signature);
            String envelopeBase64 = envelope.toBase64();

            Validator validator = new Validator(cert);
            assertTrue(validator.validateLicenseBase64(envelopeBase64, "SKU", "instance-1", now));
        } catch (Exception e) {
            fail("Exception should not be thrown: " + e.getMessage());
        }
    }

    @Test
    public void testValidateLicense_Invalid() {
        try {
            ZonedDateTime now = ZonedDateTime.now();
            X509Certificate cert = CertificateUtils.loadCertificateFromString(TEST_CERTIFICATE);

            License license = new License("SKU", "instance-1", "subs-1", "product a", now, now.plusDays(2));
            byte[] invalidSignature = "invalid-signature".getBytes();

            LicenseEnvelope envelope = new LicenseEnvelope(license, invalidSignature);

            Validator validator = new Validator(cert);
            assertThrows(InvalidSignatureException.class, () -> validator.validateLicense(envelope, "SKU", "instance-1", now));
        } catch (Exception e) {
            fail("Exception should not be thrown: " + e.getMessage());
        }
    }
}
