package com.omnistrate.licensing.certificate;

import org.junit.jupiter.api.Test;

import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CertificateUtilsTest {

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
    public void testLoadCertificateFromString_ISRGROOTX1() {
        try {
            X509Certificate cert = CertificateUtils.loadCertificateFromString(Certificates.ISRGROOTX1);
            assertNotNull(cert);
            assertEquals("CN=ISRG Root X1,O=Internet Security Research Group,C=US", cert.getSubjectX500Principal().getName());
        } catch (Exception e) {
            fail("Exception should not be thrown: " + e.getMessage());
        }
    }

    @Test
    public void testLoadCertificateFromString_R10() {
        try {
            X509Certificate cert = CertificateUtils.loadCertificateFromString(Certificates.R10);
            assertNotNull(cert);
            assertEquals("CN=R10,O=Let's Encrypt,C=US", cert.getSubjectX500Principal().getName());
        } catch (Exception e) {
            fail("Exception should not be thrown: " + e.getMessage());
        }
    }

    @Test
    public void testLoadCertificateFromString_R11() {
        try {
            X509Certificate cert = CertificateUtils.loadCertificateFromString(Certificates.R11);
            assertNotNull(cert);
            assertEquals("CN=R11,O=Let's Encrypt,C=US", cert.getSubjectX500Principal().getName());
        } catch (Exception e) {
            fail("Exception should not be thrown: " + e.getMessage());
        }
    }

    @Test
    public void testLoadCertificateFromString_R12() {
        try {
            X509Certificate cert = CertificateUtils.loadCertificateFromString(Certificates.R12);
            assertNotNull(cert);
            assertEquals("CN=R12,O=Let's Encrypt,C=US", cert.getSubjectX500Principal().getName());
        } catch (Exception e) {
            fail("Exception should not be thrown: " + e.getMessage());
        }
    }

    @Test
    public void testLoadCertificateChainFromString_R12() {
        try {
            List<X509Certificate> certChain = CertificateUtils.loadCertificateChainFromString(Certificates.R12);
            assertNotNull(certChain);
            assertFalse(certChain.isEmpty());
            assertEquals("CN=R12,O=Let's Encrypt,C=US", certChain.get(0).getSubjectX500Principal().getName());
            assertTrue(certChain.size() == 1);
        } catch (Exception e) {
            fail("Exception should not be thrown: " + e.getMessage());
        }
    }

    @Test
    public void testLoadCertificateFromString_R13() {
        try {
            X509Certificate cert = CertificateUtils.loadCertificateFromString(Certificates.R13);
            assertNotNull(cert);
            assertEquals("CN=R13,O=Let's Encrypt,C=US", cert.getSubjectX500Principal().getName());
        } catch (Exception e) {
            fail("Exception should not be thrown: " + e.getMessage());
        }
    }

    @Test
    public void testLoadCertificateChainFromString_R13() {
        try {
            List<X509Certificate> certChain = CertificateUtils.loadCertificateChainFromString(Certificates.R13);
            assertNotNull(certChain);
            assertFalse(certChain.isEmpty());
            assertEquals("CN=R13,O=Let's Encrypt,C=US", certChain.get(0).getSubjectX500Principal().getName());
            assertTrue(certChain.size() == 1);
        } catch (Exception e) {
            fail("Exception should not be thrown: " + e.getMessage());
        }
    }

    @Test
    public void testLoadPrivateKeyFromString() {
        try {
            PrivateKey privateKey = CertificateUtils.loadPrivateKeyFromString(TEST_PRIVATE_KEY);
            assertNotNull(privateKey);
        } catch (Exception e) {
            fail("Exception should not be thrown: " + e.getMessage());
        }
    }

    @Test
    public void testSignAndVerify() {
        try {
            X509Certificate cert = CertificateUtils.loadCertificateFromString(TEST_CERTIFICATE);
            assertNotNull(cert);

            String data = "Test data";
            byte[] dataBytes = data.getBytes();

            // Load private key
            PrivateKey privateKey = CertificateUtils.loadPrivateKeyFromString(TEST_PRIVATE_KEY);

            // Sign data
            byte[] signature = CertificateUtils.sign(privateKey, dataBytes);
            assertNotNull(signature);

            // Verify signature
            boolean isVerified = CertificateUtils.verifySignature(cert, signature, dataBytes);
            assertTrue(isVerified);
        } catch (Exception e) {
            fail("Exception should not be thrown: " + e.getMessage());
        }
    }

    @Test
    public void testSignWithWrongSignature() {
        try {
            X509Certificate cert = CertificateUtils.loadCertificateFromString(TEST_CERTIFICATE);
            assertNotNull(cert);

            String data = "Test data";
            byte[] dataBytes = data.getBytes();

            // Load private key
            PrivateKey privateKey = CertificateUtils.loadPrivateKeyFromString(TEST_PRIVATE_KEY);

            // Sign data
            byte[] signature = CertificateUtils.sign(privateKey, dataBytes);
            assertNotNull(signature);

            // Verify signature with wrong data
            boolean isVerified = CertificateUtils.verifySignature(cert, signature, "Wrong data".getBytes());
            assertFalse(isVerified);
        } catch (Exception e) {
            fail("Exception should not be thrown: " + e.getMessage());
        }
    }

     private static final String TEST_CERTIFICATE_SCALAR = "-----BEGIN CERTIFICATE-----\n" + //
                  "MIIFDzCCA/egAwIBAgISBQ7AxQelP5GuCoG+Mo6+wa9QMA0GCSqGSIb3DQEBCwUA\n" + //
                  "MDMxCzAJBgNVBAYTAlVTMRYwFAYDVQQKEw1MZXQncyBFbmNyeXB0MQwwCgYDVQQD\n" + //
                  "EwNSMTIwHhcNMjUwOTMwMTIzNzQ1WhcNMjUxMjI5MTIzNzQ0WjAlMSMwIQYDVQQD\n" + //
                  "ExpsaWNlbnNpbmcub21uaXN0cmF0ZS5jbG91ZDCCASIwDQYJKoZIhvcNAQEBBQAD\n" + //
                  "ggEPADCCAQoCggEBAMIrmbbPtnNCZvb29sRxImPkELefuIONGQvD3iqi2KMDrG3t\n" + //
                  "w4Ma0g+e7lSZUdr0yBFm2hnHwjEVi9IVrnKih5U8BBdqE0wvcAnPRr3LS7XtrEXE\n" + //
                  "lsYxHdjjX7H+XLMMEMNEw/Nxabu7NdKsMhNeOZMEE1QzhPodNDyZHK3EkcEA2E4N\n" + //
                  "5IczYAI39PL4htU1VUKYbKNz1NZ1kBL6OW1aavKOWcLfo6ZBXQDl/pDSSsGBy3nc\n" + //
                  "WajZICb+a3zSX7URR4/cWVX9/yRJTRpz9w1+FbCGizurbhk/T8q+KhrwMAV9KRS8\n" + //
                  "bw/gcxcYkhKlyL6rYA+qwfta7Ga3wweLcc/hAxkCAwEAAaOCAikwggIlMA4GA1Ud\n" + //
                  "DwEB/wQEAwIFoDAdBgNVHSUEFjAUBggrBgEFBQcDAQYIKwYBBQUHAwIwDAYDVR0T\n" + //
                  "AQH/BAIwADAdBgNVHQ4EFgQUgVaYC+z0u06HB1GVbHuC9qt/PKswHwYDVR0jBBgw\n" + //
                  "FoAUALUp8i2ObzHom0yteD763OkM0dIwMwYIKwYBBQUHAQEEJzAlMCMGCCsGAQUF\n" + //
                  "BzAChhdodHRwOi8vcjEyLmkubGVuY3Iub3JnLzAlBgNVHREEHjAcghpsaWNlbnNp\n" + //
                  "bmcub21uaXN0cmF0ZS5jbG91ZDATBgNVHSAEDDAKMAgGBmeBDAECATAuBgNVHR8E\n" + //
                  "JzAlMCOgIaAfhh1odHRwOi8vcjEyLmMubGVuY3Iub3JnLzgxLmNybDCCAQMGCisG\n" + //
                  "AQQB1nkCBAIEgfQEgfEA7wB2ABLxTjS9U3JMhAYZw48/ehP457Vih4icbTAFhOvl\n" + //
                  "hiY6AAABmZrWjFUAAAQDAEcwRQIhAIUfaABJA0qESd/S1N7Hp+1llRtQQiawBwsa\n" + //
                  "hbY+TMaTAiBkEORg02COWBzx32ZdX/FYSIVUITUk9HZemGR8rB3NZQB1ABmG1Mco\n" + //
                  "qm/+ugNveCpNAZGqzi1yMQ+uzl1wQS0lTMfUAAABmZrWlDEAAAQDAEYwRAIgcIV3\n" + //
                  "JqR62S6OQaGAuJLJhpZIwg3UFp0PhnZP+GNmDSICIDPQiiizYycwkSCYHJQM6YBg\n" + //
                  "bHeBvau4+GnEKQpIxETzMA0GCSqGSIb3DQEBCwUAA4IBAQCeJcdOOYtAzEmPCOKq\n" + //
                  "HA3H4XNg0pmNZ9shz/oB5SbUSjUUJiYWU9mDmphTn7bvgocTF7IDs9ewZTwFglsS\n" + //
                  "c8UO/9DtywzGo3lPX2eLVFBIbgXIOe7NxanxG+ayh0KvE5nAbX2cSCT/g9fiaxN2\n" + //
                  "nXaGB8GOUhe0kbqE4OmxZ3jLQXG/X4xBabMmnDnU460bJTKl1hFKAPou2Ub9jOED\n" + //
                  "KRUCb6MfjjAAG0XgezIsSrivkOqOdRE1Da6xX5qGSINwEgnS6kUr43AvwfDA/oL8\n" + //
                  "LJ7qX3DcTCP+SKhRUqE0EB2GGoIzVPcGSxOOHOBmc/tKfGiXsOV5LSzQrhjCaOV/\n" + //
                  "BivY\n" + //
                  "-----END CERTIFICATE-----\n" + //
                  "-----BEGIN CERTIFICATE-----\n" + //
                  "MIIFBjCCAu6gAwIBAgIRAMISMktwqbSRcdxA9+KFJjwwDQYJKoZIhvcNAQELBQAw\n" + //
                  "TzELMAkGA1UEBhMCVVMxKTAnBgNVBAoTIEludGVybmV0IFNlY3VyaXR5IFJlc2Vh\n" + //
                  "cmNoIEdyb3VwMRUwEwYDVQQDEwxJU1JHIFJvb3QgWDEwHhcNMjQwMzEzMDAwMDAw\n" + //
                  "WhcNMjcwMzEyMjM1OTU5WjAzMQswCQYDVQQGEwJVUzEWMBQGA1UEChMNTGV0J3Mg\n" + //
                  "RW5jcnlwdDEMMAoGA1UEAxMDUjEyMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIB\n" + //
                  "CgKCAQEA2pgodK2+lP474B7i5Ut1qywSf+2nAzJ+Npfs6DGPpRONC5kuHs0BUT1M\n" + //
                  "5ShuCVUxqqUiXXL0LQfCTUA83wEjuXg39RplMjTmhnGdBO+ECFu9AhqZ66YBAJpz\n" + //
                  "kG2Pogeg0JfT2kVhgTU9FPnEwF9q3AuWGrCf4yrqvSrWmMebcas7dA8827JgvlpL\n" + //
                  "Thjp2ypzXIlhZZ7+7Tymy05v5J75AEaz/xlNKmOzjmbGGIVwx1Blbzt05UiDDwhY\n" + //
                  "XS0jnV6j/ujbAKHS9OMZTfLuevYnnuXNnC2i8n+cF63vEzc50bTILEHWhsDp7CH4\n" + //
                  "WRt/uTp8n1wBnWIEwii9Cq08yhDsGwIDAQABo4H4MIH1MA4GA1UdDwEB/wQEAwIB\n" + //
                  "hjAdBgNVHSUEFjAUBggrBgEFBQcDAgYIKwYBBQUHAwEwEgYDVR0TAQH/BAgwBgEB\n" + //
                  "/wIBADAdBgNVHQ4EFgQUALUp8i2ObzHom0yteD763OkM0dIwHwYDVR0jBBgwFoAU\n" + //
                  "ebRZ5nu25eQBc4AIiMgaWPbpm24wMgYIKwYBBQUHAQEEJjAkMCIGCCsGAQUFBzAC\n" + //
                  "hhZodHRwOi8veDEuaS5sZW5jci5vcmcvMBMGA1UdIAQMMAowCAYGZ4EMAQIBMCcG\n" + //
                  "A1UdHwQgMB4wHKAaoBiGFmh0dHA6Ly94MS5jLmxlbmNyLm9yZy8wDQYJKoZIhvcN\n" + //
                  "AQELBQADggIBAI910AnPanZIZTKS3rVEyIV29BWEjAK/duuz8eL5boSoVpHhkkv3\n" + //
                  "4eoAeEiPdZLj5EZ7G2ArIK+gzhTlRQ1q4FKGpPPaFBSpqV/xbUb5UlAXQOnkHn3m\n" + //
                  "FVj+qYv87/WeY+Bm4sN3Ox8BhyaU7UAQ3LeZ7N1X01xxQe4wIAAE3JVLUCiHmZL+\n" + //
                  "qoCUtgYIFPgcg350QMUIWgxPXNGEncT921ne7nluI02V8pLUmClqXOsCwULw+PVO\n" + //
                  "ZCB7qOMxxMBoCUeL2Ll4oMpOSr5pJCpLN3tRA2s6P1KLs9TSrVhOk+7LX28NMUlI\n" + //
                  "usQ/nxLJID0RhAeFtPjyOCOscQBA53+NRjSCak7P4A5jX7ppmkcJECL+S0i3kXVU\n" + //
                  "y5Me5BbrU8973jZNv/ax6+ZK6TM8jWmimL6of6OrX7ZU6E2WqazzsFrLG3o2kySb\n" + //
                  "zlhSgJ81Cl4tv3SbYiYXnJExKQvzf83DYotox3f0fwv7xln1A2ZLplCb0O+l/AK0\n" + //
                  "YE0DS2FPxSAHi0iwMfW2nNHJrXcY3LLHD77gRgje4Eveubi2xxa+Nmk/hmhLdIET\n" + //
                  "iVDFanoCrMVIpQ59XWHkzdFmoHXHBV7oibVjGSO7ULSQ7MJ1Nz51phuDJSgAIU7A\n" + //
                  "0zrLnOrAj/dfrlEWRhCvAgbuwLZX1A2sjNjXoPOHbsPiy+lO1KF8/XY7\n" + //
                  "-----END CERTIFICATE-----";
             

    @Test
    public void testLoadPublicCertificateFromBytesScalar() {
        try {
            byte[] certBytes = TEST_CERTIFICATE_SCALAR.getBytes();
            List<X509Certificate> certChain = CertificateUtils.loadCertificateChainFromBytes(certBytes);
            assertNotNull(certChain);
            assertFalse(certChain.isEmpty());
            
            X509Certificate cert = certChain.get(0); // First cert is the end-entity certificate
            assertNotNull(cert);

            //assertEquals("CN=R11,O=Let's Encrypt,C=US", cert.getIssuerX500Principal().getName());
            assertEquals("CN=licensing.omnistrate.cloud", cert.getSubjectX500Principal().getName());
            assertFalse(cert.getBasicConstraints() != -1);
            assertNotNull(cert.getPublicKey());

            // get a valid time that is within the certificate's validity period
            ZonedDateTime validTime = ZonedDateTime.of(2025, 12, 1, 0, 0, 0, 0, ZoneOffset.UTC);
            
            // Use the intermediate certificates from the chain (all but the first one)
            List<X509Certificate> intermediates = certChain.size() > 1 ? 
                certChain.subList(1, certChain.size()) : java.util.Collections.emptyList();
            assertTrue(CertificateUtils.verifyCertificate(cert, "licensing.omnistrate.cloud", validTime, intermediates));

            // Test without passing intermediates
            assertTrue(CertificateUtils.verifyCertificate(cert, "licensing.omnistrate.cloud", validTime, java.util.Collections.emptyList()));

        } catch (Exception e) {
            fail("Exception should not be thrown: " + e.getMessage());
        }
    }


    @Test
    public void testLoadPublicCertificateFromBytes() {
        try {
            byte[] certBytes = TEST_CERTIFICATE.getBytes();
            List<X509Certificate> certChain = CertificateUtils.loadCertificateChainFromBytes(certBytes);
            assertNotNull(certChain);
            assertFalse(certChain.isEmpty());
            
            X509Certificate cert = certChain.get(0); // First cert is the end-entity certificate
            assertNotNull(cert);

            assertEquals("CN=R11,O=Let's Encrypt,C=US", cert.getIssuerX500Principal().getName());
            assertEquals("CN=licensing-test.omnistrate.dev", cert.getSubjectX500Principal().getName());
            assertFalse(cert.getBasicConstraints() != -1);
            assertNotNull(cert.getPublicKey());

            // get a valid time that is within the certificate's validity period
            ZonedDateTime validTime = ZonedDateTime.of(2025, 5, 1, 0, 0, 0, 0, ZoneOffset.UTC);
            
            // Use the intermediate certificates from the chain (all but the first one)
            List<X509Certificate> intermediates = certChain.size() > 1 ? 
                certChain.subList(1, certChain.size()) : java.util.Collections.emptyList();
            assertTrue(CertificateUtils.verifyCertificate(cert, "licensing-test.omnistrate.dev", validTime, intermediates));
            
            // Test expired certificate
            ZonedDateTime expiredTime =  ZonedDateTime.ofInstant(cert.getNotAfter().toInstant(), ZoneOffset.UTC).plusHours(1);
            assertThrows(Exception.class, () -> CertificateUtils.verifyCertificate(cert, "licensing-test.omnistrate.dev", expiredTime, intermediates));

            assertThrows(Exception.class, () -> CertificateUtils.verifyCertificate(cert, "licensing.omnistrate.dev.invalid", validTime, intermediates));
        } catch (Exception e) {
            fail("Exception should not be thrown: " + e.getMessage());
        }
    }
}
