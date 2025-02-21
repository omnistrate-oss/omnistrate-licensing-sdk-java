package com.omnistrate.licensing.certificate;

import org.junit.jupiter.api.Test;

import java.security.cert.X509Certificate;

import static org.junit.jupiter.api.Assertions.*;

public class CertificateUtilsTest {

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
}
