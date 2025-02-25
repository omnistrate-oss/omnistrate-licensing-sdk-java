# Omnistrate Licensing SDK for Java

![CI](https://github.com/omnistrate-oss/omnistrate-licensing-sdk-java/actions/workflows/build.yml/badge.svg) 
![CodeQL](https://github.com/omnistrate-oss/omnistrate-licensing-sdk-java/actions/workflows/github-code-scanning/codeql/badge.svg)

## Overview

[omnistrate-licensing-sdk-java](https://github.com/omnistrate-oss/omnistrate-licensing-sdk-java) is an SDK that allows you to validate license files generated by Omnistrate. 

## How to get the SDK

You can get the package from Maven Central:

```xml
<dependency>
    <groupId>om.omnistrate.licensing</groupId>
    <artifactId>omnistrate-licensing-sdk-java</artifactId>
    <version>${LATEST_VERSION}</version>
</dependency>
```

For more details, visit: [Maven Central](https://central.sonatype.com/artifact/com.omnistrate.licensing/omnistrate-licensing-sdk-java)

## Usage Examples

### Pre-requisites

The `Licensing` feature needs to be enabled for the service plan. When enabling the feature, a product identifier can be optionally configured. That value can then be optionally used to validate the license. 

Docker compose example: 
```yaml
x-customer-integrations:
  licensing: 
    licenseExpirationInDays: 7 # optional - defaults to 7 days
    productTierSku: '[product plan unique id]' # optional - identifier (shared secret) that can be used to add extra security on validation
```

When used on a Container-based resource, Omnistrate takes care of mounting the secret and setting the environment variables for verification. 

Service spec configuration:
```yaml
features:
  licensing:
    licenseExpirationInDays: 7 # optional - defaults to 7 days
    productTierSku: '[product plan unique id]' # optional - identifier (shared secret) that can be used to add extra security on validation

```

When using Helm or Operator, the secret `service-plan-subscription-license` generated with the license needs to be mounted on `/var/subscription/`

## Usage

During startup and periodically, implement one of the following options to check for the license validity:

### a. ValidateLicense

The validation of the license can be done on startup or periodically and does not require connection to external services to execute. 

```java
package com.example;

import com.omnistrate.licensing.validation.Validator;

public class Main {
    public static void main(String[] args) {
        try {
            boolean isValid = Validator.validateLicense();
            if (isValid) {
                System.out.println("License validation succeeded");
            } else {
                System.out.println("License validation failed");
            }
        } catch (Exception e) {
            System.out.println("License validation failed: " + e.getMessage());
        }
    }
}
```

### b. ValidateLicenseForProduct

Alternatively, if a value is set for the `productTierSku` field, this method can be used to add extra security, ensuring the license has the same shared secret. 

```java
package com.example;

import com.omnistrate.licensing.validation.Validator;

public class Main {
    public static void main(String[] args) {
        try {
            boolean isValid = Validator.validateLicenseForProduct("[product plan unique id]"); // [SKU]  value is hardcoded, based on the value configured when enabling the feature
            if (isValid) {
                System.out.println("License validation for product succeeded");
            } else {
                System.out.println("License validation for product failed");
            }
        } catch (Exception e) {
            System.out.println("License validation for product failed: " + e.getMessage());
        }
    }
}
```

## Contributing

Want to contribute? Awesome! You can find information about contributing to this
project in the [CONTRIBUTING](/CONTRIBUTING.md) page

## About Omnistrate

[Omnistrate](https://omnistrate.com/) is the operating system for your SaaS,
offering enterprise-grade capabilities: automated provisioning, serverless
capabilities, auto-scaling, billing, monitoring, centralized logging,
self-healing, intelligent patching and much more!
