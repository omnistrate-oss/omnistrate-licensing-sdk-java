package com.omnistrate.licensing.common;

public class InvalidPrivateKeyException extends Exception {
    private final String reason;

    public InvalidPrivateKeyException(String reason) {
        super(reason);
        this.reason = reason;
    }
    
    public InvalidPrivateKeyException(String reason, Throwable cause) {
        super(reason, cause);
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }
}