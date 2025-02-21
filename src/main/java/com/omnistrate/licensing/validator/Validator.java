package com.omnistrate.licensing.validator;

import com.omnistrate.licensing.certificate.CertificateUtils;
import com.omnistrate.licensing.common.InvalidCertificateException;
import com.omnistrate.licensing.common.InvalidLicenseException;
import com.omnistrate.licensing.common.License;
import com.omnistrate.licensing.common.LicenseEnvelope;
import com.omnistrate.licensing.common.Utils;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.cert.X509Certificate;
import java.time.ZonedDateTime;

public class Validator {

    private X509Certificate cert;

    public Validator(X509Certificate cert) {
        this.cert = cert;
    }

    public Validator(byte[] certPEM) throws InvalidCertificateException {
        this(CertificateUtils.loadCertificateFromBytes(certPEM));
    }

    public Validator(String certPath) throws InvalidCertificateException {
        this(CertificateUtils.loadCertificate(certPath));
    }
    
    public Validator(ValidatorConfig config) throws InvalidCertificateException {
        this(config.getCertPath());
    }
    
    public boolean validateLicense(LicenseEnvelope envelope, String sku, String instanceID, ZonedDateTime currentTime) throws InvalidLicenseException, Exception {
        if (cert == null) {
            throw new InvalidLicenseException("signingCertificate is required to validate a license");
        }

        if (envelope == null) {
            throw new InvalidLicenseException("envelope is required");
        }

        if (!envelope.isValid()) {
            throw new InvalidLicenseException("envelope is invalid");
        }

        // Extract the signature
        byte[] signature = envelope.getSignature();

        // Extract the license
        License license = envelope.getLicense();
        byte[] licenseBytes = license.toBytes();

        // Check if the license is valid
        if (!license.isValid(sku, instanceID)) {
            throw new InvalidLicenseException("license is invalid");
        }

        // Check if the license is expired
        if (license.isExpiredAt(currentTime)) {
            throw new InvalidLicenseException("license is expired");
        }

        // Verify the signature
        if (!CertificateUtils.verifySignature(cert, signature, licenseBytes)) {
            throw new InvalidLicenseException("failed to verify signature");
        }

        return true;
    }

    public boolean validateLicenseBase64(String envelopeBase64, String sku, String instanceID, ZonedDateTime currentTime) throws InvalidLicenseException, Exception {
        // Decode the license envelope
        LicenseEnvelope envelope = LicenseEnvelope.parseBase64(envelopeBase64);

        // Validate the license
        return validateLicense(envelope, sku, instanceID, currentTime);
    }

    public boolean validateLicenseString(String envelopeJson, String sku, String instanceID, ZonedDateTime currentTime) throws InvalidLicenseException, Exception {
        // Decode the license envelope
        LicenseEnvelope envelope = LicenseEnvelope.parse(envelopeJson);

        // Validate the license
        return validateLicense(envelope, sku, instanceID, currentTime);
    }

    public boolean validateLicenseBytes(byte[] envelopeBytes, String sku, String instanceID, ZonedDateTime currentTime) throws InvalidLicenseException, Exception {
        // Decode the license envelope
        LicenseEnvelope envelope = LicenseEnvelope.parseBytes(envelopeBytes);

        // Validate the license
        return validateLicense(envelope, sku, instanceID, currentTime);
    }

    public boolean validateCertificate(String certificateDomain, ZonedDateTime currentTime) throws InvalidCertificateException {
        if (cert == null) {
            throw new InvalidCertificateException("signingCertificate is required to validate a certificate");
        }

        // Validate the certificate
        return CertificateUtils.verifyCertificate(cert, certificateDomain, currentTime);
    }

    public static boolean validateLicense() throws Exception {
        return validateLicenseWithOptions(new ValidationOptions.Builder().build());
    }

    public static boolean validateLicenseForProduct(String sku) throws Exception {
        ValidationOptions options = new ValidationOptions.Builder().sku(sku).build();
        return validateLicenseWithOptions(options);
    }

    public static boolean validateLicenseWithOptions(ValidationOptions options) throws Exception {
        ValidatorConfig config = new ValidatorConfig();

        if (!Utils.isNullOrEmpty(options.getCertPath())) {
            config = new ValidatorConfig(config.getInstanceID(), options.getCertPath(), config.getLicensePath());
        }

        if (!Utils.isNullOrEmpty(options.getLicensePath())) {
            config = new ValidatorConfig(config.getInstanceID(), config.getCertPath(), options.getLicensePath());
        }

        byte[] licenseBytes = Files.readAllBytes(Paths.get(config.getLicensePath()));

        Validator validator = new Validator(config);

        ZonedDateTime currentTime = ZonedDateTime.now();
        if (options.getCurrentTime() != null) {
            currentTime = options.getCurrentTime();
        }

        if (!options.isSkipCertificateValidation()) {
            String certificateDomain = options.getCertificateDomain();
            if (certificateDomain == null || certificateDomain.isEmpty()) {
                certificateDomain = "signingCertificateValidDnsName";
            }

            if (!validator.validateCertificate(certificateDomain, currentTime)) {
                return false;
            }
        }

        String sku = options.getSku();
        String instanceID = options.getInstanceID();
        if (instanceID == null || instanceID.isEmpty()) {
            instanceID = config.getInstanceID();
        }

        return validator.validateLicenseBytes(licenseBytes, sku, instanceID, currentTime);
    }

    
}
