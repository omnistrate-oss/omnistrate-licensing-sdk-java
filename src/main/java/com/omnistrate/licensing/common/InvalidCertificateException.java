package com.omnistrate.licensing.common;

public class InvalidCertificateException extends Exception {
    private final String reason;

    public InvalidCertificateException(String reason) {
        super(reason);
        this.reason = reason;
    }
    public InvalidCertificateException(String reason, Throwable cause) {
        super(reason, cause);
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }
}