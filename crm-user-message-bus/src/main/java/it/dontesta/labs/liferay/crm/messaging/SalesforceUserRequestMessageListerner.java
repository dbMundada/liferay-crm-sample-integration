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

package it.dontesta.labs.liferay.crm.messaging;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.messaging.BaseMessageListener;
import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.messaging.MessageListener;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.WebKeys;

import it.dontesta.labs.liferay.crm.api.model.Lead;
import it.dontesta.labs.liferay.crm.api.service.CRMBaseService;
import it.dontesta.labs.liferay.crm.messaging.constants.CRMMessageDestinationNames;
import it.dontesta.labs.liferay.crm.users.constants.CRMUserExtend;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferencePolicyOption;

/**
 * @author Antonio Musarra
 */
@Component(
	immediate = true,
	property = "destination.name=" +
		CRMMessageDestinationNames.SALESFORCE_SEND_LEAD,
	service = MessageListener.class
)
public class SalesforceUserRequestMessageListerner extends BaseMessageListener {

	@Override
	protected void doReceive(Message message) throws Exception {
		Lead lead = (Lead)message.getPayload();

		String leadId = null;
		String actionCommand = message.getString(Constants.CMD);

		if (_log.isDebugEnabled()) {
			_log.debug("Action: " + actionCommand);
			_log.debug("Receive this payload: " + lead.toString());
		}

		switch (actionCommand) {
			case Constants.ADD:
				leadId = _crmBaseService.createLead(
					lead, (long)message.get(WebKeys.COMPANY_ID));
				lead.setLeadId(leadId);

				_updateUserCustomFields(message, lead);

				break;

			case Constants.UPDATE:
				leadId = _crmBaseService.updateLead(
					lead, (long)message.get(WebKeys.COMPANY_ID));
				lead.setLeadId(leadId);

				_updateUserCustomFields(message, lead);

				break;

			case Constants.DELETE:
				leadId = _crmBaseService.deleteLead(
					lead.getLeadId(), (long)message.get(WebKeys.COMPANY_ID));

				break;
		}

		_updateUserCustomFields(message, lead);

		if (_log.isInfoEnabled()) {
			_log.info("Lead created on CRM target system with id: " + leadId);
		}
	}

	private void _updateUserCustomFields(Message message, Lead lead)
		throws PortalException {

		User user = _userLocalService.getUserByEmailAddress(
			(long)message.get(WebKeys.COMPANY_ID), lead.getEmail());

		user.getExpandoBridge().setAttribute(
			CRMUserExtend.CRM_SYSTEM_NAME, _CRM_SYSTEM_NAME, false);

		user.getExpandoBridge().setAttribute(
			CRMUserExtend.CRM_ENTITY_TYPE, "lead", false);

		user.getExpandoBridge().setAttribute(
			CRMUserExtend.CRM_ENTITY_OBJECT_KEY, lead.getLeadId(), false);

		if (_log.isInfoEnabled()) {
			_log.info("User custom fields updated for user: " +
				user.getEmailAddress());
		}
	}

	private static final String _CRM_SYSTEM_NAME = "SALESFORCE";

	@Reference(
		policyOption = ReferencePolicyOption.GREEDY,
		target = "(component.name=it.dontesta.labs.liferay.crm.salesforce.SalesforceBaseService)"
	)
	private CRMBaseService _crmBaseService;

	private Log _log = LogFactoryUtil.getLog(
		SalesforceUserRequestMessageListerner.class);

	@Reference
	private UserLocalService _userLocalService;

}