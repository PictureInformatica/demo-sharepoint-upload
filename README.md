[![License: GPL v3](https://img.shields.io/badge/License-GPL_v3-blue.svg)](https://www.gnu.org/licenses/gpl-3.0)

## Demo application for Microsoft Graph SDK Sharepoint file upload ##

### Description ###

This sample is a working application to test and demonstrate how to upload files to a sharepoint site using the Graph SDK.

### Requirements ###
* JDK 17
* Registered application on Azure AD tenant (with a shared secret), see [Quickstart: Register an application with the Microsoft identity platform](https://learn.microsoft.com/en-us/azure/active-directory/develop/quickstart-register-app)
* Permissions Files.ReadWrite.All or Sites.Selected

### Configuration ###

The application.conf needs to be populated with information so the application can connect to the mailbox:

* ClientId (aka ApplicationId)
* ClientSecret
* Tenant domain name
* OAuth Scope (preconfigured with https://graph.microsoft.com/.default)
* Sharepoint site (like contoso.sharepoint.com)

### Notes ###

* This demo is using ***client_credentials*** grant type.

### Usage ###

* Clean
    ~~~
    ./gradlew clean
    ~~~

* Run
    ~~~
    ./gradlew run
    ~~~

* Create distribution (on build/install)
    ~~~
    ./gradlew installDist
    ~~~

* Create distribution zip (on build/distributions)
    ~~~
    ./gradlew distZip
    ~~~

* Run from build/install
  ~~~
  ./demo-sharepoint-upload.bat file-to-upload
  ~~~
  or
  ~~~
  java -jar lib/demo-sharepoint-upload-0.1.jar file-to-upload
  ~~~

When the application is executed, if an application.conf doesn't exist, it will be created from an internal template. See [Configuration](#configuration).

### License ###
Copyright © 2023, [Picture Soluções em TI](https://www.picture.com.br)

This demo application is licensed under [GPLv3](https://www.gnu.org/licenses/gpl-3.0), see [LICENSE](LICENSE).