/*
 * Copyright (c) 2018 Antonio Musarra's Blog - https://www.dontesta.it
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package it.dontesta.labs.liferay.crm.salesforce.configuration;

import aQute.bnd.annotation.metatype.Meta;

import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

/**
 * @author Antonio Musarra
 *
 */
@ExtendedObjectClassDefinition(
	category = "crm-services", scope = ExtendedObjectClassDefinition.Scope.COMPANY
)
@Meta.OCD(
	id = "it.dontesta.labs.liferay.crm.salesforce.configuration.SalesforceClientGroupConfiguration",
	localization = "content/Language",
	name = "salesforce-client-configuration-name"
)
public interface SalesforceClientGroupConfiguration {

	@Meta.AD(
		deflt = "", name = "username", description = "username-description",
		required = false
	)
	public String username();

	@Meta.AD(
		deflt = "", name = "password", description = "password-description",
		required = false, type = Meta.Type.Password
	)
	public String password();

	@Meta.AD(
		deflt = "https://login.salesforce.com/services/Soap/u/43.0",
		name = "auth-end-point", description = "auth-end-point-description",
		required = false
	)
	public String authEndpoint();

	@Meta.AD(
		deflt = "https://login.salesforce.com/services/Soap/c/43.0",
		name = "auth-end-point-enterprise",
		description = "auth-end-point-enterprise-description", required = false
	)
	public String authEndpointEnterprise();

	@Meta.AD(
		deflt = "/tmp/traceSalesforcePartner.log", name = "trace-file",
		description = "trace-file-description", required = false
	)
	public String traceFile();

	@Meta.AD(
		deflt = "/tmp/traceSalesforceEnterprise.log",
		name = "trace-file-enterprise",
		description = "trace-file-enterprise-description", required = false
	)
	public String traceFileEnterprise();

	@Meta.AD(
		deflt = "true", name = "trace-message",
		description = "trace-message-description", required = false
	)
	public boolean traceMessage();

	@Meta.AD(
		deflt = "true", name = "pretty-print-xml",
		description = "pretty-print-xml-description", required = false
	)
	public boolean prettyPrintXml();

}