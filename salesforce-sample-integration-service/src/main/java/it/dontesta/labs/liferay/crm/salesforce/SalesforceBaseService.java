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

package it.dontesta.labs.liferay.crm.salesforce;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.module.configuration.ConfigurationProvider;

import com.sforce.soap.enterprise.Connector;
import com.sforce.soap.enterprise.EnterpriseConnection;
import com.sforce.soap.enterprise.QueryResult;
import com.sforce.soap.enterprise.SaveResult;
import com.sforce.soap.enterprise.sobject.SObject;
import com.sforce.ws.ConnectionException;
import com.sforce.ws.ConnectorConfig;

import it.dontesta.labs.liferay.crm.api.exception.LeadException;
import it.dontesta.labs.liferay.crm.api.model.Lead;
import it.dontesta.labs.liferay.crm.api.service.CRMBaseService;
import it.dontesta.labs.liferay.crm.salesforce.configuration.SalesforceClientGroupConfiguration;

import java.io.FileNotFoundException;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Antonio Musarra
 */
@Component(immediate = true, service = CRMBaseService.class)
public class SalesforceBaseService implements CRMBaseService {

	/**
	 * Create a new Lead on the CRM System
	 *
	 * @param lead The lead object that you want create
	 * @param companyId The Liferay companyId
	 * @return Return the object lead identifier
	 * @throws LeadException In case of error in the creation of the lead
	 */
	@Override
	public String createLead(Lead lead, long companyId) throws LeadException {
		try {
			EnterpriseConnection connection = _getEnterpriseConnection(
				companyId);

			// Create a new Lead and assign various properties

			com.sforce.soap.enterprise.sobject.Lead salesForceLead =
				new com.sforce.soap.enterprise.sobject.Lead();
			salesForceLead.setFirstName(lead.getFirstName());
			salesForceLead.setLastName(lead.getLastName());
			salesForceLead.setCompany(lead.getCompany());
			salesForceLead.setTitle(lead.getTitle());
			salesForceLead.setPhone(lead.getPhone());
			salesForceLead.setMobilePhone(lead.getMobile());
			salesForceLead.setFax(lead.getFax());
			salesForceLead.setEmail(lead.getEmail());
			salesForceLead.setWebsite(lead.getWebSite());
			salesForceLead.setStreet(lead.getStreet());
			salesForceLead.setCity(lead.getCity());
			salesForceLead.setCountry(lead.getCountry());
			salesForceLead.setState(lead.getState());
			salesForceLead.setDescription(lead.getDescription());

			// The lead assignment rule will assign any new leads that
			// have "API" as the LeadSource to a particular user

			salesForceLead.setLeadSource("API");

			// In this sample we will look for a particular rule and if found
			// use the id for the lead assignment. If it is not found we will
			// instruct the call to use the current default rule. You can't use
			// both of these values together.

			QueryResult
				qr = connection.query("SELECT Id FROM AssignmentRule WHERE Name = " +
									"'Mass Mail Campaign' AND SobjectType = 'Lead'");

			if (qr.getSize() == 0) {
				connection.setAssignmentRuleHeader(null, true);
			} else {
				connection.setAssignmentRuleHeader(
					qr.getRecords()[0].getId(), false);
			}

			// Every operation that results in a new or updated lead will
			// use the specified rule until the header is removed from the
			// connection.

			SaveResult[] sr = connection.create(new SObject[] {salesForceLead});

			for (int i = 0; i < sr.length; i++) {
				if (sr[i].isSuccess()) {
					if (_log.isInfoEnabled()) {
						_log.info("Successfully created lead with id of: " +
							sr[i].getId() + ".");
					}

					lead.setLeadId(sr[i].getId());
				}
				else {
					if (_log.isErrorEnabled()) {
						_log.error("Error creating lead: " +
							sr[i].getErrors()[0].getMessage());
					}

					throw new LeadException("Error creating lead: " +
											sr[i].getErrors()[0].getMessage());
				}
			}

			// This call effectively removes the header, the next lead will
			// be assigned to the default lead owner.

			connection.clearAssignmentRuleHeader();

			return lead.getLeadId();
		}
		catch (ConfigurationException ce) {
			if (_log.isErrorEnabled()) {
				_log.error(ce);
			}

			throw new LeadException(ce);
		}
		catch (FileNotFoundException fnfe) {
			if (_log.isErrorEnabled()) {
				_log.error(fnfe);
			}

			throw new LeadException(fnfe);
		}
		catch (ConnectionException ce) {
			if (_log.isErrorEnabled()) {
				_log.error(ce);
			}

			throw new LeadException(ce);
		}
	}

	/**
	 * Create a new Lead on the CRM System
	 *
	 * @param salutation  The salutation string (es.: Mr., Ms., Mrs., Prof.)
	 * @param firstName   The lead first name
	 * @param lastName    The lead last name
	 * @param company     The lead company name
	 * @param title       The lead title
	 * @param leadSource  The lead source (es.: Web, Phone Inquiry, Other)
	 * @param phone       The lead phone
	 * @param mobile      The lead mobile phone number
	 * @param fax         The lead fax number
	 * @param email       The lead email
	 * @param webSite     The lead web site
	 * @param street      The lead street
	 * @param zip         The lead zip postal code
	 * @param city        The lead city
	 * @param state       The lead state
	 * @param country     The lead country
	 * @param description The lead description
	 * @param companyId The Liferay companyId
	 * @return Return the object lead identifier
	 * @throws LeadException In case of error in the creation of the lead
	 */
	@Override
	public String createLead(
			String salutation, String firstName, String lastName,
			String company, String title, String leadSource, String phone,
			String mobile, String fax, String email, String webSite,
			String street, String zip, String city, String state,
			String country, String description, long companyId)
		throws LeadException {

		throw new LeadException("Method not implement");
	}

	/**
	 * Delete the specified lead on the CRM System
	 *
	 * @param leadId    The lead identifier
	 * @param companyId The Liferay companyId
	 * @return Return the object lead identifier
	 */
	@Override
	public String deleteLead(String leadId, long companyId)
		throws LeadException {

		throw new LeadException("Method not implement");
	}

	/**
	 * Retrieve and return the lead object
	 *
	 * @param leadId The lead identifier
	 * @param companyId The Liferay companyId
	 * @return Return the lead object
	 * @throws LeadException In case of error in the get of the lead
	 */
	@Override
	public Lead getLead(String leadId, long companyId) throws LeadException {
		throw new LeadException("Method not implement");
	}

	/**
	 * Update the exits lead object
	 *
	 * @param lead The lead object that you want update
	 * @param companyId The Liferay companyId
	 * @return Return the object lead identifier
	 * @throws LeadException In case of error in the update of the lead
	 */
	@Override
	public String updateLead(Lead lead, long companyId) throws LeadException {
		throw new LeadException("Method not implement");
	}

	/**
	 * Update the exits lead object
	 *
	 * @param leadId      The object lead identifier.
	 * @param salutation  The salutation string (es.: Mr., Ms., Mrs., Prof.)
	 * @param firstName   The lead first name
	 * @param lastName    The lead last name
	 * @param company     The lead company name
	 * @param title       The lead title
	 * @param leadSource  The lead source (es.: Web, Phone Inquiry, Other)
	 * @param phone       The lead phone
	 * @param mobile      The lead mobile phone number
	 * @param fax         The lead fax number
	 * @param email       The lead email
	 * @param webSite     The lead web site
	 * @param street      The lead street
	 * @param zip         The lead zip postal code
	 * @param city        The lead city
	 * @param state       The lead state
	 * @param country     The lead country
	 * @param description The lead description
	 * @param companyId The Liferay companyId
	 * @return Return the object lead identifier
	 * @throws LeadException In case of error in the update of the lead
	 */
	@Override
	public String updateLead(
		String leadId, String salutation, String firstName, String lastName,
		String company, String title, String leadSource, String phone,
		String mobile, String fax, String email, String webSite, String street,
		String zip, String city, String state, String country,
		String description, long companyId) throws LeadException {

		throw new LeadException("Method not implement");
	}

	private EnterpriseConnection _getEnterpriseConnection(long companyId)
		throws ConfigurationException, ConnectionException,
			   FileNotFoundException {

		SalesforceClientGroupConfiguration salesforceClientGroupConfiguration =
			_getSalesforceClientGroupConfiguration(companyId);

		ConnectorConfig config = new ConnectorConfig();

		config.setUsername(salesforceClientGroupConfiguration.username());
		config.setPassword(salesforceClientGroupConfiguration.password());
		config.setAuthEndpoint(
			salesforceClientGroupConfiguration.authEndpointEnterprise());
		config.setTraceFile(
			salesforceClientGroupConfiguration.traceFileEnterprise());
		config.setTraceMessage(
			salesforceClientGroupConfiguration.traceMessage());
		config.setPrettyPrintXml(
			salesforceClientGroupConfiguration.prettyPrintXml());

		return Connector.newConnection(config);
	}

	private SalesforceClientGroupConfiguration _getSalesforceClientGroupConfiguration(
		long companyId) throws
			ConfigurationException {

		return _configurationProvider.getCompanyConfiguration(
			SalesforceClientGroupConfiguration.class, companyId);
	}

	@Reference
	private ConfigurationProvider _configurationProvider;

	private Log _log = LogFactoryUtil.getLog(SalesforceBaseService.class);

}