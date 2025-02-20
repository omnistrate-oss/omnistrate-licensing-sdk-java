package com.omnistrate.licensing.common;

public class InvalidLicenseException extends Exception {
    private final String reason;

    public InvalidLicenseException(String reason) {
        super(reason);
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }
}