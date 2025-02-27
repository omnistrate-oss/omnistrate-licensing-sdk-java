package com.omnistrate.licensing.validation;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;

public class ValidationOptions {

    private boolean skipCertificateValidation;
    private String certificateDomain;
    private ZonedDateTime currentTime;
    private String certPath;
    private String licensePath;
    private String productPlanUniqueID;
    private String organizationID;
    private String instanceID;

    private ValidationOptions(Builder builder) {
        this.skipCertificateValidation = builder.skipCertificateValidation;
        this.certificateDomain = builder.certificateDomain;
        this.currentTime = builder.currentTime;
        this.certPath = builder.certPath;
        this.licensePath = builder.licensePath;
        this.productPlanUniqueID = builder.productPlanUniqueID;
        this.organizationID = builder.organizationID;
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

    public String getProductPlanUniqueID() {
        return productPlanUniqueID;
    }

    public String getOrganizationID() {
        return organizationID;
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
        private String productPlanUniqueID;
        private String organizationID;
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

        public Builder productPlanUniqueID(String productPlanUniqueID) {
            this.productPlanUniqueID = productPlanUniqueID;
            return this;
        }

        public Builder organizationID(String organizationID) {
            this.organizationID = organizationID;
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
