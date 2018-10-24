# Liferay 7.1 The CRM Integration Quick Start
[![Antonio Musarra's Blog](https://img.shields.io/badge/maintainer-Antonio_Musarra's_Blog-purple.svg?colorB=6e60cc)](https://www.dontesta.it)
[![Build Status](https://travis-ci.org/amusarra/liferay-crm-sample-integration.svg?branch=master)](https://travis-ci.org/amusarra/liferay-crm-sample-integration)
[![Twitter Follow](https://img.shields.io/twitter/follow/antonio_musarra.svg?style=social&label=%40antonio_musarra%20on%20Twitter&style=plastic)](https://twitter.com/antonio_musarra)

## 1. Overview
This simple project was born with the aim of providing the basis for a "gym" 
with the aim of creating a system of integration with the most common 
Customer Relationship Management (CRM) systems.

CRM systems are:

* Salesforce.com (SF or SFDC) - https://www.salesforce.com
* SuiteCRM - https://suitecrm.com/
* OpenCRX - http://www.opencrx.org/

The implemented use case is very simple, to then kick off your fantasy. The case 
implemented in this project allows, when a new user is registered, to send it to 
the CRM system, where it will be created as Lead. The figure below shows the 
integration case implemented in this project.

![Figure 1 - Integration scenario Liferay 7.1 and CRM System via API](https://www.dontesta.it/wp-content/uploads/2018/10/LiferayCRMIntegrationSample_1.png)

Figure 1 - Integration scenario Liferay 7.1 and CRM System via API

For integration with Salesforce, this project uses as dependency the 
[salesforce-client-soap](https://search.maven.org/artifact/it.dontesta.labs.liferay.salesforce.client.soap/salesforce-client-soap/1.1.0/jar) 
bundle (last version is 1.1.0) whose implementation is available here 
https://github.com/amusarra/salesforce-client-soap

The **salesforce-client-soap bundle installation** on the Liferay instance is an 
*important requirement* in order to make integration with Salesforce work.

In this project the following topics of the Liferay 7/7.1 platform were 
addressed, which are:

* OSGi and modularity - https://dev.liferay.com/develop/tutorials/-/knowledge_base/7-1/osgi-and-modularity
* OSGi services and dependency injection with declarative services - https://dev.liferay.com/develop/tutorials/-/knowledge_base/7-1/osgi-services-and-dependency-injection-with-declarative-services
* OSGi Configuration - https://dev.liferay.com/develop/tutorials/-/knowledge_base/7-1/making-applications-configurable
* Portal Instance Lifecycle
* Categorizing the configuration - https://dev.liferay.com/develop/tutorials/-/knowledge_base/7-1/categorizing-the-configuration
* Model Listener - https://dev.liferay.com/develop/reference/-/knowledge_base/7-1/model-listener
* Custom Fields - https://dev.liferay.com/discover/portal/-/knowledge_base/7-0/custom-fields
* Message Bus - https://dev.liferay.com/develop/tutorials/-/knowledge_base/7-1/message-bus

**At this time the project includes only complete integration with Salesforce**. 
For the rest of the CRM systems (SuiteCRM and OpenCRX), it is only necessary to 
implement the API layer that communicates with the target system.

This current version of the project has been tested with the Liferay 7.1 
Community Edition GA1.


## 2. Architecture
A scheme is better than a hundred words! The figure shows the macro architecture 
adopted to achieve the integration between Liferay and the most common CRM systems.

The components offered by the Liferay platform used are:

* Model Listener (User listener)
* Message Bus
* OSGi DS Components
* OSGi Configuration

![Figure 2 - Liferay CRM Sample Integration - Architecture](https://www.dontesta.it/wp-content/uploads/2018/10/LiferayCRMIntegrationSample_2.png)

Figure 2 - Liferay CRM Sample Integration - Architecture

In short. For each new user who registers (*Login Form => Create Account*) on 
Liferay, the user model listener is triggered which in turn sends a message 
to a specific destination via the message bus, the listener receives the 
message that once processed sent to the specific CRM system through the APIs.

The figure below shows the diagram of the modules and the relationships between 
them.

![Figure 3 - Liferay CRM Sample Integration - Component diagram](https://www.dontesta.it/wp-content/uploads/2018/10/LiferayCRMIntegrationSample_3.png)

Figure 3 - Liferay CRM Sample Integration - Component diagram

The table below describes the responsibility of each module.

| Name of the module  | Description |
| ------------- | ------------- |
| **crm-sample-integration-api**  | This module defines a subset of APIs representing the operations that can be performed on the CRM system. |
| **crm-user-model-listener** | This module contains the OSGi component that implements the Model Listener of the Liferay user entity. **This module communicates exclusively with the Message Bus and not directly with the CRM systems**. This module also includes the OSGi configuration that specifies which CRM systems to enable for integration. |
| **crm-user-message-bus** | This module contains the definition of the message destination (**one for each CRM system**) and the implementation of the related message listener. Receives and processes messages sent by the *crm-user-model-listener* module. In particular, this module is related to the CRM API module. |
| **crm-user-custom-fields** | This module contains a component of the Portal Instance Lifecycle Listener which is responsible for creating some custom fields on the user entity. |
| **salesforce-sample-integration-service** | This module contains the API implementation defined in the crm-sample-integration-api module for the specific Salesforce CRM system. |
| **suitecrm-sample-integration-service** | This module contains the API implementation defined in the crm-sample-integration-api module for the specific Suite CRM system. |
| **opencrx-sample-integration-service** | This module contains the API implementation defined in the crm-sample-integration-api module for the specific OpenCRX CRM system. |

Each specific implementation of the CRM API also contains the OSGi configuration 
necessary to set up, for example, the service end point, username and password 
of the CRM system.

Only the **salesforce-sample-integration-service** module contains the implementation 
of the CRM API that lets you communicate with Salesforce. For the rest of the 
modules I leave the specific implementation to you.

## 3. Configuration
There are two configurations with scope at the instance (or company) level and which are:

* CRMGroupServiceConfiguration: This configuration allows you to set with which CRM system to enable integration.
* CRMClientGroupConfiguration: For each implementation of the CRM API there is a specific configuration that in this are three.
	* SalesforceClientGroupConfiguration
	* OpenCRXClientGroupConfiguration
	* SuiteCRMClientGroupConfiguration

The following figures show each of the configurations indicated above.

![Figure 4 - Liferay CRM Integration Sample - Enable CRM Service Configuration](https://www.dontesta.it/wp-content/uploads/2018/10/LiferayCRMIntegrationSample_Configuration_1.png)

Figure 4 - Liferay CRM Integration Sample - Enable CRM Service Configuration

![Figure 5 - Liferay CRM Integration Sample - OpenCRX RESTful Client Configuration](https://www.dontesta.it/wp-content/uploads/2018/10/LiferayCRMIntegrationSample_Configuration_2.png)

Figure 5 - Liferay CRM Integration Sample - OpenCRX RESTful Client Configuration

![Figure 6 - Liferay CRM Integration Sample - Salesforce SOAP Client Configuration](https://www.dontesta.it/wp-content/uploads/2018/10/LiferayCRMIntegrationSample_Configuration_3.png)

Figure 7 - Liferay CRM Integration Sample - Salesforce SOAP Client Configuration

![Figure 7 - Liferay CRM Integration Sample - SuiteCRM Rest Client Configuration](https://www.dontesta.it/wp-content/uploads/2018/10/LiferayCRMIntegrationSample_Configuration_4.png)

Figure 7 - Liferay CRM Integration Sample - SuiteCRM Rest Client Configuration

## 4. Integration in action
At this time the available integration is the one with Salesforce. Once you have 
enabled the integration with Salesforce and set the connection parameters to 
your Salesforce instance, you should get the expected result, that is, once 
you have added a new account on Liferay you should have the relative Lead on the 
Salesforce and on Liferay you should see the custom field updated.

![Figure 8 - Liferay CRM Integration Sample - View User Account](https://www.dontesta.it/wp-content/uploads/2018/10/LiferayCRMIntegrationSample_Create_Account_1.png)

Figure 8 - Liferay CRM Integration Sample - View User Account

![Figure 9 - Liferay CRM Integration Sample - View Lead on Salesforce](https://www.dontesta.it/wp-content/uploads/2018/10/LiferayCRMIntegrationSample_Create_Account_2.png)

Figure 9 - Liferay CRM Integration Sample - View Lead on Salesforce

Following is an example of logs that highlight the operations of integration 
with the CRM system.

```
2018-10-23 10:06:46.084 DEBUG [salesforce/send_lead-1][SalesforceUserRequestMessageListerner:42] Action: add
2018-10-23 10:06:46.085 DEBUG [salesforce/send_lead-1][SalesforceUserRequestMessageListerner:43] Receive this payload
[WSC][Thread.run:748]Log file already exists, appending to /tmp/traceSalesforceEnterprise.log
2018-10-23 10:06:46.317 DEBUG [http-nio-8080-exec-3][CRMUserModelListener:60] Get Configuration interface it.dontesta.labs.liferay.crm.listener.user.configuration.CRMGroupServiceConfiguration for companyId: 20099
2018-10-23 10:06:46.318 DEBUG [http-nio-8080-exec-3][CRMUserModelListener:73] CRM Salesforce enabled: true
2018-10-23 10:06:46.319 DEBUG [http-nio-8080-exec-3][CRMUserModelListener:75] CRM SuiteCRM enabled: false
2018-10-23 10:06:46.320 DEBUG [http-nio-8080-exec-3][CRMUserModelListener:77] CRM OpenCRX enabled: false
2018-10-23 10:06:48.606 INFO  [salesforce/send_lead-1][SalesforceBaseService:95] Successfully created lead with id of: 00Q0O000016m5g3UAA.
2018-10-23 10:06:49.222 INFO  [salesforce/send_lead-1][SalesforceUserRequestMessageListerner:95] User custom fields updated for user: antonio.musarra@gmail.com
2018-10-23 10:06:49.223 INFO  [salesforce/send_lead-1][SalesforceUserRequestMessageListerner:75] Lead created on CRM target system with id: 00Q0O000016m5g3UAA
```

Console 1 - Log of CRM Integration components in action

### Resources
If you follow this resources you could see how to use Salesforce SOAP API.

1. [Salesforce - Introducing SOAP API](https://developer.salesforce.com/docs/atlas.en-us.api.meta/api/sforce_api_quickstart_intro.htm)
2. [Salesforce - Cheat Sheets](https://developer.salesforce.com/page/Cheat_Sheets)
3. [Force.com SOAP API Cheatsheet](http://resources.docs.salesforce.com/rel1/doc/en-us/static/pdf/SF_Soap_API_cheatsheet_web.pdf)
4. [Force.com Web Service Connector (WSC)](https://github.com/forcedotcom/wsc)
5. [openCRX 4.1 Documentation](https://sourceforge.net/p/opencrx/wiki/WebHome41/)
6. [SuiteCRM - API Version 8](https://docs.suitecrm.com/developer/api/version-8/)

[![Liferay 7: Demo Salesforce Gogo Shell Command ](https://img.youtube.com/vi/nQXqzKpnxoc/0.jpg)](https://youtu.be/nQXqzKpnxoc)

Video 1 - Liferay 7: Demo Salesforce Gogo Shell Command


### Project License
The MIT License (MIT)

Copyright &copy; 2018 Antonio Musarra's Blog - [https://www.dontesta.it](https://www.dontesta.it "Antonio Musarra's Blog") , 
[antonio.musarra@gmail.com](mailto:antonio.musarra@gmail.com "Antonio Musarra Email")

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

<span style="color:#D83410">
	THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
	IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
	FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
	AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
	LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
	OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
	SOFTWARE.
<span>
