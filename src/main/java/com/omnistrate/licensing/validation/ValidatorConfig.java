package com.omnistrate.licensing.validation;

import com.omnistrate.licensing.common.Utils;

public class ValidatorConfig {

    public static final String DEFAULT_CERT_PATH = "/var/subscription/license.crt";
    public static final String DEFAULT_LICENSE_PATH = "/var/subscription/license.lic";
    public static final String INSTANCE_ID_ENV = "INSTANCE_ID";
    public static final String CERT_PATH_ENV = "SERVICE_PLAN_SUBSCRIPTION_LICENSE_CERT_PATH";
    public static final String LICENSE_PATH_ENV = "SERVICE_PLAN_SUBSCRIPTION_LICENSE_FILE_PATH";

    private String certPath;
    private String licensePath;
    private String instanceID;

    public ValidatorConfig(String instanceID, String certPath, String licensePath) {
        if (Utils.isNullOrEmpty(instanceID)) {
           this.instanceID = Utils.getEnvOrDefault(INSTANCE_ID_ENV, "");
        } else {
            this.instanceID = instanceID;
        }
        if (Utils.isNullOrEmpty(certPath)) {
            this.certPath = Utils.getEnvOrDefault(CERT_PATH_ENV, DEFAULT_CERT_PATH);
        } else {
            this.certPath = certPath;
        }
        if (Utils.isNullOrEmpty(licensePath)) {
            this.licensePath = Utils.getEnvOrDefault(LICENSE_PATH_ENV, DEFAULT_LICENSE_PATH);
        } else {
            this.licensePath = licensePath;
        }
    }

    public ValidatorConfig() {
        this("", "", "");
    }

    public boolean isValid() {
        if (Utils.isNullOrEmpty(certPath) || Utils.isNullOrEmpty(licensePath)) {
            return false;
        }
        return true;
    }

    public String getCertPath() {
        return certPath;
    }

    public String getLicensePath() {
        return licensePath;
    }

    public String getInstanceID() {
        return instanceID;
    }

}
