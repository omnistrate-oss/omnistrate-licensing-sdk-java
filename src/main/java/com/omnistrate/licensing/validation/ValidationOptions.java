package com.omnistrate.licensing.validation;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;

public class ValidationOptions {

    private boolean skipCertificateValidation;
    private String certificateDomain;
    private ZonedDateTime currentTime;
    private String certPath;
    private String licensePath;
    private String sku;
    private String instanceID;

    private ValidationOptions(Builder builder) {
        this.skipCertificateValidation = builder.skipCertificateValidation;
        this.certificateDomain = builder.certificateDomain;
        this.currentTime = builder.currentTime;
        this.certPath = builder.certPath;
        this.licensePath = builder.licensePath;
        this.sku = builder.sku;
        this.instanceID = builder.instanceID;
    }

    public boolean isSkipCertificateValidation() {
        return skipCertificateValidation;
    }

    public String getCertificateDomain() {
        return certificateDomain;
    }

    public ZonedDateTime getCurrentTime() {
        return currentTime;
    }

    public String getCertPath() {
        return certPath;
    }

    public String getLicensePath() {
        return licensePath;
    }

    public String getSku() {
        return sku;
    }

    public String getInstanceID() {
        return instanceID;
    }

    public static class Builder {

        private final static String SIGNING_CERTIFICATE_VALID_DNS_NAME = "licensing.omnistrate.cloud";

        private boolean skipCertificateValidation = false;
        private String certificateDomain = SIGNING_CERTIFICATE_VALID_DNS_NAME;
        private ZonedDateTime currentTime = ZonedDateTime.now(ZoneOffset.UTC);
        private String certPath;
        private String licensePath;
        private String sku;
        private String instanceID;

        public Builder skipCertificateValidation(boolean skipCertificateValidation) {
            this.skipCertificateValidation = skipCertificateValidation;
            return this;
        }

        public Builder certificateDomain(String certificateDomain) {
            this.certificateDomain = certificateDomain;
            return this;
        }

        public Builder currentTime(ZonedDateTime currentTime) {
            this.currentTime = currentTime;
            return this;
        }

        public Builder certPath(String certPath) {
            this.certPath = certPath;
            return this;
        }

        public Builder licensePath(String licensePath) {
            this.licensePath = licensePath;
            return this;
        }

        public Builder sku(String sku) {
            this.sku = sku;
            return this;
        }

        public Builder instanceID(String instanceID) {
            this.instanceID = instanceID;
            return this;
        }

        public ValidationOptions build() {
            return new ValidationOptions(this);
        }
    }
}
