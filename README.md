# Integrating Azure AD v2 into a Java web application

### Overview

This sample demonstrates a Java web application calling a Microsoft Graph that is secured using Azure Active Directory.

1. The Java web application uses the  Gluu’s OpenID Connect libraries to obtain a JWT access token from Azure Active Directory (Azure AD):
2. The access token is used as a bearer token to authenticate the user when calling the Microsoft Graph.

![Topology](./ReadmeFiles/Java-WebApp-Diagram.png)

### Scenario

This sample shows how to build a Java web app(confidential client) that uses OpenID Connect to sign-in users from a single Azure Active Directory (Azure AD) tenant using  Gluu’s OpenID Connect libraries. For more information about how the protocols work in this scenario and other scenarios, see [Gluu’s OpenID Connect libraries](https://github.com/GluuFederation/oxAuth).

## How to run this sample

To run this sample, you'll need:

- Working installation of Java and Maven
- Tomcat or any other J2EE container solution
- An Internet connection
- An Azure Active Directory (Azure AD) tenant. For more information on how to get an Azure AD tenant, see [How to get an Azure AD tenant](https://azure.microsoft.com/en-us/documentation/articles/active-directory-howto-tenant/) 

### Step 1: Download Java (8 and above) for your platform

To successfully use this sample, you need a working installation of [Java](http://www.oracle.com/technetwork/java/javase/downloads/index.html) and [Maven](https://maven.apache.org/).

### Step 2:  Clone or download this repository

### Step 3:  Register the sample with your Azure Active Directory tenant

#### First step: choose the Azure AD tenant where you want to create your applications

As a first step you'll need to:

1. Sign in to the [Azure portal](https://portal.azure.com).
1. On the top bar, click on your account, and then on **Switch Directory**. 
1. Once the *Directory + subscription* pane opens, choose the Active Directory tenant where you wish to register your application, from the *Favorites* or *All Directories* list.
1. Click on **All services** in the left-hand nav, and choose **Azure Active Directory**.

> In the next steps, you might need the tenant name (or directory name) or the tenant ID (or directory ID). These are presented in the **Properties**
of the Azure Active Directory window respectively as *Name* and *Directory ID*

#### Register the app app

1. In the  **Azure Active Directory** pane, click on **App registrations** and choose **New application registration**.
1. Enter a friendly name for the application, for example 'Webapp-Openidconnect' and select 'Web app / API' as the *Application Type*.
1. For the *sign-on URL*, enter the base URL for the sample. By default, this sample uses `https://localhost:8000/`.
1. Click **Create** to create the application.
1. In the succeeding page, Find the *Application ID* value and and record it for later. You'll need it to configure the configuration file for this project.
1. Then click on **Settings**, and choose **Properties**.
1. Click on **Redirect URIs** and set it to for example `https://localhost:8000/websample/secure/aad`
1. From the Settings menu, choose **Keys** and add a new entry in the Password section:

   - Type a key description (of instance `app secret`),
   - Select a key duration of either **In 1 year**, **In 2 years**, or **Never Expires**.
   - When you save this page, the key value will be displayed, copy, and save the value in a safe location.
   - You'll need this key later to configure the project. This key value will not be displayed again, nor retrievable by any other means, so record it as soon as it is visible from the Azure portal.
1. Configure Permissions for your application. To that extent, in the Settings menu, choose the 'Required permissions' section and then,
   click on **Add**, then **Select an API**, and type `Microsoft Graph` in the textbox. Then, click on  **Select Permissions** and select **Read directory data** under **APPLICATION PERMISSIONS**.
   - Note that for **Read directory data** requires the user grating the
     permission to be a tenant administrator. If you created an AzureAD tenant
     as part of the sample, you will be an administrator by default. 

### Step 4:  Configure the sample to use your Azure AD tenant

Open `web.xml` in the webapp/WEB-INF/ folder. Fill in with your tenant and app registration information noted in registration step. Replace '{tenantId}' with the tenant Id or name, 'AppClientId' with the Application Id, 'AppClientSecret' with the key value noted and 'AppRedirectUri' with redirect uri created from above step.

### Step 5: Package and then deploy the war file.
1. mvn package

This will generate a websample.war file in your /targets directory. Deploy this war file using Tomcat or any other J2EE container solution. To deploy on Tomcat container, copy the .war file to the webapps folder under your Tomcat installation and then start the Tomcat server.

This WAR will automatically be hosted at http://<yourserverhost>:<yourserverport>/websample/

Example: http://localhost:8080/websample/

You're done!

Click on "Show users in the tenant" to start the process of logging in

## Help and Support
1. For more information about how OAuth 2.0 protocols work in this scenario and other scenarios, see [Auth code flow](https://docs.microsoft.com/en-us/azure/active-directory/develop/v2-oauth2-auth-code-flow).
1. For more info on GLUU see https://github.com/GluuFederation/oxAuth.

## Credits
This code has been inspired originally https://www.gluu.org/blog/java-openid-connect-servlet-sample/ and has been modified to suit the microsoft v2 implementation.
