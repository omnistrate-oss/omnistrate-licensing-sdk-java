package com.omnistrate.licensing.common;

public class InvalidSignatureException extends Exception {
    private final String reason;

    public InvalidSignatureException(String reason) {
        super(reason);
        this.reason = reason;
    }
    
    public InvalidSignatureException(String reason, Throwable cause) {
        super(reason, cause);
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }
}