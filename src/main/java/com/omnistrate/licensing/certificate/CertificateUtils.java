package com.omnistrate.licensing.certificate;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.cert.PKIXParameters;
import java.security.cert.TrustAnchor;
import java.security.cert.X509CertSelector;
import java.security.cert.CertPathValidator;
import java.security.cert.CertPathValidatorException;
import java.security.cert.PKIXBuilderParameters;
import java.security.cert.CertStore;
import java.security.cert.CertificateException;
import java.security.cert.CollectionCertStoreParameters;
import java.security.cert.CertPath;
import java.security.cert.CertPathBuilder;
import java.security.cert.CertPathBuilderException;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;

import com.omnistrate.licensing.common.InvalidCertificateException;

public class CertificateUtils {

    public static X509Certificate loadCertificate(String certPath) throws InvalidCertificateException {
        try (FileInputStream fis = new FileInputStream(new File(certPath))) {
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            return (X509Certificate) cf.generateCertificate(fis);
        } catch (FileNotFoundException e) {
            throw new InvalidCertificateException("Certificate file not found: " + certPath);
        } catch (CertificateException e) {
            throw new InvalidCertificateException("Failed to load certificate from file: " + certPath, e);
        } catch (IOException e) {
            throw new InvalidCertificateException("Failed to read certificate file: " + certPath, e);
        }
    }

    public static X509Certificate loadCertificateFromBytes(byte[] certBytes) throws InvalidCertificateException {
        try {
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            return (X509Certificate) cf.generateCertificate(new ByteArrayInputStream(certBytes));
        } catch (CertificateException e) {
            throw new InvalidCertificateException("Failed to load certificate from bytes", e);
        }
    }

    public static X509Certificate loadCertificateFromString(String certString) throws InvalidCertificateException {
        return loadCertificateFromBytes(certString.getBytes());
    }

    public static PrivateKey loadPrivateKeyFromBytes(byte[] keyBytes) throws NoSuchAlgorithmException, InvalidKeySpecException {
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePrivate(keySpec);
    }

    public static PrivateKey loadPrivateKeyFromString(String keyString) throws Exception {
        return loadPrivateKeyFromBytes(keyString.getBytes());
    }

    public static byte[] sign(PrivateKey key, byte[] data) throws Exception {
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(key);
        signature.update(data);
        return signature.sign();
    }

    public static boolean verifySignature(X509Certificate cert, byte[] signature, byte[] data) throws SignatureException, InvalidKeyException, NoSuchAlgorithmException {
        PublicKey publicKey = cert.getPublicKey();
        Signature sig = Signature.getInstance("SHA256withRSA");
        sig.initVerify(publicKey);
        sig.update(data);
        return sig.verify(signature);
    }

    public static void verifyCertificate(X509Certificate cert, String dnsName, ZonedDateTime currentTime) throws Exception {
        cert.checkValidity(java.util.Date.from(currentTime.toInstant()));

        Set<TrustAnchor> trustAnchors = new HashSet<>();
        // Add CA and intermediate certificates to trustAnchors
        X509Certificate caCert = loadCertificateFromString(Certificates.ISRGROOTX1);
        trustAnchors.add(new TrustAnchor(caCert, null));
        X509Certificate intermediateCert1 = loadCertificateFromString(Certificates.R10);
        trustAnchors.add(new TrustAnchor(intermediateCert1, null));
        X509Certificate intermediateCert2 = loadCertificateFromString(Certificates.R11);
        trustAnchors.add(new TrustAnchor(intermediateCert2, null));

        PKIXParameters params = new PKIXParameters(trustAnchors);
        params.setRevocationEnabled(false); // Disable CRL checking to support offline verification

        X509CertSelector certSelector = new X509CertSelector();
        certSelector.setCertificate(cert);

        CertPathBuilder certPathBuilder = CertPathBuilder.getInstance("PKIX");
        PKIXBuilderParameters builderParams = new PKIXBuilderParameters(trustAnchors, certSelector);
        builderParams.setRevocationEnabled(false); // Disable CRL checking to support offline verification

        CertStore certStore = CertStore.getInstance("Collection", new CollectionCertStoreParameters(trustAnchors));
        builderParams.addCertStore(certStore);

        CertPath certPath;
        try {
            certPath = certPathBuilder.build(builderParams).getCertPath();
        } catch (CertPathBuilderException e) {
            throw new Exception("Failed to build certification path", e);
        }

        // Set the current time for the validation
        params.setDate(java.util.Date.from(currentTime.toInstant()));

        CertPathValidator certPathValidator = CertPathValidator.getInstance("PKIX");
        try {
            certPathValidator.validate(certPath, params);
        } catch (CertPathValidatorException e) {
            throw new Exception("Certificate path validation failed", e);
        }
    }

}
