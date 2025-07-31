package com.omnistrate.licensing.certificate;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
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
import java.security.cert.PKIXBuilderParameters;
import java.security.cert.CertStore;
import java.security.cert.CertificateException;
import java.security.cert.CollectionCertStoreParameters;
import java.security.cert.CertPath;
import java.security.cert.CertPathBuilder;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.CertificateParsingException;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.omnistrate.licensing.common.InvalidCertificateException;
import com.omnistrate.licensing.common.InvalidPrivateKeyException;
import com.omnistrate.licensing.common.InvalidSignatureException;

public class CertificateUtils {

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

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

    public static PrivateKey loadPrivateKeyFromBytes(byte[] pemContent) throws InvalidPrivateKeyException {
        try {
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(pemContent);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA", "BC");
            return keyFactory.generatePrivate(keySpec);
        } catch (NoSuchAlgorithmException ex) {
            throw new InvalidPrivateKeyException("Failed to load private key: RSA algorithm not found", ex);
        } catch (NoSuchProviderException ex) {
            throw new InvalidPrivateKeyException("Failed to load private key: Bouncy Castle provider not found", ex);
        } catch (InvalidKeySpecException ex) {
            throw new InvalidPrivateKeyException("Failed to load private key: Invalid key spec", ex);
        }
    }

    public static PrivateKey loadPrivateKeyFromString(String keyString) throws InvalidPrivateKeyException {
        try {
            PemReader pemReader = new PemReader(new StringReader(keyString));
            PemObject pemObject = pemReader.readPemObject();
            byte[] pemContent = pemObject.getContent();
            return loadPrivateKeyFromBytes(pemContent);
        } catch (IOException e) {
            throw new InvalidPrivateKeyException("Failed to load private key from string", e);
        }
    }

    public static byte[] sign(PrivateKey key, byte[] data) throws Exception {
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(key);
        signature.update(data);
        return signature.sign();
    }

    public static boolean verifySignature(X509Certificate cert, byte[] signature, byte[] data) throws InvalidSignatureException {
        try {
            PublicKey publicKey = cert.getPublicKey();
            Signature sig = Signature.getInstance("SHA256withRSA");
            sig.initVerify(publicKey);
            sig.update(data);
            return sig.verify(signature);
        } catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException e) {
            throw new InvalidSignatureException("Failed to verify signature", e);
        }
    }

   public static boolean verifyCertificate(X509Certificate cert, String dnsName, ZonedDateTime currentTime) throws InvalidCertificateException {
        // Check certificate validity date
        try {
            cert.checkValidity(java.util.Date.from(currentTime.toInstant()));
        } catch (CertificateExpiredException | CertificateNotYetValidException e) {
            throw new InvalidCertificateException("Certificate is not valid at " + currentTime, e);
        }

        try {
            // Verify certificate against trust anchors
            Set<TrustAnchor> trustAnchors = new HashSet<>();
            // Load CA and intermediate certificates from resources
            X509Certificate caCert = loadCertificateFromString(Certificates.ISRGROOTX1);
            trustAnchors.add(new TrustAnchor(caCert, null));
            X509Certificate intermediateCert1 = loadCertificateFromString(Certificates.R10);
            trustAnchors.add(new TrustAnchor(intermediateCert1, null));
            X509Certificate intermediateCert2 = loadCertificateFromString(Certificates.R11);
            trustAnchors.add(new TrustAnchor(intermediateCert2, null));

            PKIXParameters params = new PKIXParameters(trustAnchors);
            params.setRevocationEnabled(false); // Disable revocation checking

            X509CertSelector certSelector = new X509CertSelector();
            certSelector.setCertificate(cert);

            CertPathBuilder certPathBuilder = CertPathBuilder.getInstance("PKIX");
            PKIXBuilderParameters builderParams = new PKIXBuilderParameters(trustAnchors, certSelector);
            builderParams.setRevocationEnabled(false); // Disable revocation checking

            CertStore certStore = CertStore.getInstance("Collection", new CollectionCertStoreParameters(trustAnchors));
            builderParams.addCertStore(certStore);

            builderParams.setDate(java.util.Date.from(currentTime.toInstant()));

            CertPath certPath = certPathBuilder.build(builderParams).getCertPath();
            
            CertPathValidator certPathValidator = CertPathValidator.getInstance("PKIX");
            params.setDate(java.util.Date.from(currentTime.toInstant()));
            certPathValidator.validate(certPath, params);
        } catch (Exception e) {
            throw new InvalidCertificateException("Failed to validate certificate", e);
        }

        // Validate the certificate's domain in alt names or CN
        boolean domainValid = false;
        try {
            Collection<List<?>> altNames = cert.getSubjectAlternativeNames();
            if (altNames != null) {
                for (List<?> altName : altNames) {
                    if (altName.get(0).equals(2) && altName.get(1).equals(dnsName)) {
                        domainValid = true;
                        break;
                    }
                }
            }
        } catch (CertificateParsingException e) {
            throw new InvalidCertificateException("Failed to parse certificate subject alternative names", e);
        }

        if (!domainValid) {
            String subjectDN = cert.getSubjectX500Principal().getName();
            if (subjectDN.contains("CN=" + dnsName)) {
                domainValid = true;
            }
        }

        if (!domainValid) {
            throw new InvalidCertificateException("Certificate is not valid for the specified domain: " + dnsName);
        }

        return true;
    }
}
