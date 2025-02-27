package com.omnistrate.licensing.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.DeserializationFeature;

import java.time.ZonedDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.UUID;

public class License {

    private static final ObjectMapper mapper = new ObjectMapper()
        .setPropertyNamingStrategy(PropertyNamingStrategies.UPPER_CAMEL_CASE) 
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    
    @JsonProperty("ID")
    private String id;

    @JsonProperty("CreationTime")
    private String creationTime;

    @JsonProperty("ExpirationTime")
    private String expirationTime;

    @JsonProperty("Description")
    private String description;

    @JsonProperty("InstanceID")
    private String instanceID;

    @JsonProperty("SubscriptionID")
    private String subscriptionID;

    @JsonProperty("ProductPlanUniqueID")
    private String productPlanUniqueID;
    
    @JsonProperty("Version")
    private long version;

    @JsonProperty("OrganizationID")
    private String organizationID;

    public License() {
    }

    public License(String organizationID, String productPlanUniqueID, String instanceID, String subscriptionID, String description, ZonedDateTime creationTime, ZonedDateTime expirationTime) {
        this.id = UUID.randomUUID().toString();
        this.creationTime = formatDate(creationTime);
        this.expirationTime = formatDate(expirationTime);
        this.instanceID = instanceID;
        this.subscriptionID = subscriptionID;
        this.description = description;
        this.productPlanUniqueID = productPlanUniqueID;
        this.organizationID = organizationID;
        this.version = 1;
    }

    public String getId() {
        return id;
    }

    public String getProductPlanUniqueID() {
        return productPlanUniqueID;
    }

    public String getOrganizationID() {
        return organizationID;
    }

    public String getDescription() {
        return description;
    }

    public String getInstanceID() {
        return instanceID;
    }

    public String getSubscriptionID() {
        return subscriptionID;
    }

    public long getVersion() {
        return version;
    }

    private String formatDate(ZonedDateTime date) {
        return date.format(DateTimeFormatter.ISO_ZONED_DATE_TIME);
    }

    private ZonedDateTime parseDate(String date) throws DateTimeParseException {
        return ZonedDateTime.parse(date, DateTimeFormatter.ISO_ZONED_DATE_TIME);
    }

    public ZonedDateTime getExpirationTime() throws DateTimeParseException {
        return parseDate(expirationTime);
    }

    public ZonedDateTime getCreationTime() throws DateTimeParseException {
        return parseDate(creationTime);
    }

    public boolean isValid(String organizationID, String productPlanUniqueID, String instanceID) throws InvalidLicenseException {
        if (Utils.isNullOrEmpty(id) || Utils.isNullOrEmpty(creationTime) || Utils.isNullOrEmpty(expirationTime)) {
            throw new InvalidLicenseException("Missing required fields");
        }

        if (!Utils.isNullOrEmpty(organizationID)) {
            if (!organizationID.equals(this.organizationID)) {
                throw new InvalidLicenseException("Invalid organization ID expected " + this.organizationID + " got " + organizationID);
            }
        }

        if (!Utils.isNullOrEmpty(productPlanUniqueID)) {
            if (!productPlanUniqueID.equals("" + this.productPlanUniqueID)) {
                throw new InvalidLicenseException("Invalid product plan unique id expected " + this.productPlanUniqueID + " got " + productPlanUniqueID);
            }
        }

        if (!Utils.isNullOrEmpty(instanceID)) {
            if (!instanceID.equals("" + this.instanceID)) {
                throw new InvalidLicenseException("Invalid instance ID expected " + this.instanceID + " got " + instanceID);
            }
        }

        try {
            getCreationTime();
        } catch (DateTimeParseException e) {
            throw new InvalidLicenseException("Invalid creation time");
        }
        try {
            getExpirationTime();
        } catch (DateTimeParseException e) {
            throw new InvalidLicenseException("Invalid expiration time");
        }
        return true;
    }

    public boolean isExpired() {
        return isExpiredAt(ZonedDateTime.now(ZoneOffset.UTC));
    }

    public boolean isExpiredAt(ZonedDateTime currentTime) {
        try {
            return currentTime.isAfter(getExpirationTime());
        } catch (DateTimeParseException e) {
            return true;
        }
    }

    public void renew(ZonedDateTime expirationTime) {
        this.creationTime = formatDate(ZonedDateTime.now(ZoneOffset.UTC));
        this.expirationTime = formatDate(expirationTime);
        this.version++;
    }

    public byte[] toBytes() {
        try {
            return toString().getBytes("UTF-8");
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public String toString() {
        return String.format("{\"ID\":\"%s\",\"CreationTime\":\"%s\",\"ExpirationTime\":\"%s\",\"Description\":\"%s\",\"InstanceID\":\"%s\",\"SubscriptionID\":\"%s\",\"ProductPlanUniqueID\":\"%s\",\"OrganizationID\":\"%s\",\"Version\":%d}",
                id, creationTime, expirationTime, description, instanceID, subscriptionID, productPlanUniqueID, organizationID, version);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof License)) {
            return false;
        }
        License other = (License) obj;
        return id.equals(other.id) && creationTime.equals(other.creationTime) && expirationTime.equals(other.expirationTime) && description.equals(other.description) && instanceID.equals(other.instanceID) && subscriptionID.equals(other.subscriptionID) && productPlanUniqueID.equals(other.productPlanUniqueID) && organizationID.equals(other.organizationID) && version == other.version;
    }

    public static License parse(String s) throws Exception {
        return mapper.readValue(s, License.class);
    }
}