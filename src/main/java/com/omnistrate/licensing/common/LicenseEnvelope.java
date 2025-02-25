package com.omnistrate.licensing.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.DeserializationFeature;

import java.util.Base64;
import java.time.ZonedDateTime;
import java.time.ZoneOffset;

public class LicenseEnvelope {

    private static final ObjectMapper mapper = new ObjectMapper()
        .setPropertyNamingStrategy(PropertyNamingStrategies.LOWER_CAMEL_CASE)
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    @JsonProperty("license")
    private License license;

    @JsonProperty("signature")
    private byte[] signature;

    public LicenseEnvelope() {
    }

    public LicenseEnvelope(License license, byte[] signature) {
        this.license = license;
        this.signature = signature;
    }

    public License getLicense() {
        return license;
    }

    public byte[] getSignature() {
        return signature;
    }

    public boolean isValid() {
        return license != null && signature != null && signature.length > 0;
    }

    public boolean isExpired() {
        return isExpiredAt(ZonedDateTime.now(ZoneOffset.UTC));
    }

    public boolean isExpiredAt(ZonedDateTime currentTime) {
        return license.isExpiredAt(currentTime);
    }

    public byte[] toBytes() throws Exception {
        return toString().getBytes("UTF-8");
    }

    @Override
    public String toString() {
        return String.format("{\"license\":%s,\"signature\":\"%s\"}", license.toString(), Base64.getEncoder().encodeToString(signature));
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof LicenseEnvelope)) {
            return false;
        }
        LicenseEnvelope other = (LicenseEnvelope) obj;
        return license.equals(other.license) && signature.equals(other.signature);
    }

    public String toBase64() {
        try {
            return Base64.getEncoder().encodeToString(toBytes());
        } catch (Exception e) {
            return null;
        }
    }

    public static LicenseEnvelope parseBytes(byte[] data) throws InvalidLicenseException {
        try {
            return mapper.readValue(data, LicenseEnvelope.class);
        } catch (Exception e) {
            throw new InvalidLicenseException("Failed to parse license byte[] envelope", e);
        }
    }

    public static LicenseEnvelope parse(String data) throws InvalidLicenseException {
        byte[] decoded;
        try {
            decoded = data.getBytes("UTF-8");
        } catch (Exception e) {
            throw new InvalidLicenseException("Failed to decode license string envelope", e);
        }
        return parseBytes(decoded);
    }

    public static LicenseEnvelope parseBase64(String data) throws InvalidLicenseException {
        byte[] decoded = Base64.getDecoder().decode(data);
        return parseBytes(decoded);
    }
}
