package com.omnistrate.licensing.validator;

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
        this.instanceID = instanceID;
        this.certPath = certPath;
        this.licensePath = licensePath;
    }

    public ValidatorConfig() {
        this.instanceID = getEnvOrDefault(INSTANCE_ID_ENV, "");
        this.certPath = getEnvOrDefault(CERT_PATH_ENV, DEFAULT_CERT_PATH);
        this.licensePath = getEnvOrDefault(LICENSE_PATH_ENV, DEFAULT_LICENSE_PATH);
    }

    public boolean isValid() {
        if (isNullOrEmpty(certPath) || isNullOrEmpty(licensePath)) {
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

    private static String getEnvOrDefault(String key, String defaultValue) {
        String envValue = System.getenv(key);
        if (isNullOrEmpty(envValue)) {
            return defaultValue;
        }
        return envValue;
    }

    private static boolean isNullOrEmpty(String value) {
        return value == null || value.isEmpty();
    }
}
