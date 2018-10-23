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

package it.dontesta.labs.liferay.crm.listener.user;

import com.liferay.portal.kernel.exception.ModelListenerException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.messaging.MessageBus;
import com.liferay.portal.kernel.model.BaseModelListener;
import com.liferay.portal.kernel.model.ModelListener;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.PredicateFilter;
import com.liferay.portal.kernel.util.WebKeys;

import it.dontesta.labs.liferay.crm.api.model.Lead;
import it.dontesta.labs.liferay.crm.listener.user.configuration.CRMGroupServiceConfiguration;
import it.dontesta.labs.liferay.crm.messaging.constants.CRMMessageDestinationNames;
import it.dontesta.labs.liferay.crm.users.constants.CRMUserExtend;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Antonio Musarra
 */
@Component(immediate = true, service = ModelListener.class)
public class CRMUserModelListener extends BaseModelListener<User> {

	@Override
	public void onAfterCreate(User model) throws ModelListenerException {
		_sendToCRMSystem(model, Constants.ADD);

		super.onAfterUpdate(model);
	}

	@Override
	public void onAfterRemove(User model) throws ModelListenerException {
		_sendToCRMSystem(model, Constants.REMOVE);

		super.onAfterRemove(model);
	}

	@Override
	public void onAfterUpdate(User model) throws ModelListenerException {
		_sendToCRMSystem(model, Constants.UPDATE);

		super.onAfterUpdate(model);
	}

	private CRMGroupServiceConfiguration _getCrmGroupServiceConfiguration(
		long companyId) throws
			ConfigurationException {

		if (_log.isDebugEnabled()) {
			_log.debug(
				"Get Configuration " + CRMGroupServiceConfiguration.class +
				" for companyId: " + companyId);
		}

		return _configurationProvider.getCompanyConfiguration(
			CRMGroupServiceConfiguration.class, companyId);
	}

	private boolean _isConfigurationCorrect(
		CRMGroupServiceConfiguration crmGroupServiceConfiguration) {

		if (_log.isDebugEnabled()) {
			_log.debug("CRM Salesforce enabled: " +
				crmGroupServiceConfiguration.salesforce());
			_log.debug("CRM SuiteCRM enabled: " +
				crmGroupServiceConfiguration.suitecrm());
			_log.debug("CRM OpenCRX enabled: " +
				crmGroupServiceConfiguration.opencrx());
		}

		Boolean crmServicesEnabled[] = {
			crmGroupServiceConfiguration.salesforce(),
			crmGroupServiceConfiguration.suitecrm(),
			crmGroupServiceConfiguration.opencrx()
		};

		PredicateFilter<Boolean> predicateFilter = aBoolean -> {
			if (aBoolean) {
				return true;
			}

			return false;
		};

		if (ArrayUtil.count(crmServicesEnabled, predicateFilter) == 1 ||
			ArrayUtil.count(crmServicesEnabled, predicateFilter) == 0) {

			return true;
		}
		else {
			if (_log.isWarnEnabled()) {
				_log.warn("Check the configuration regarding the " +
						"enabling of CRM services. It is not possible " +
						"to have multiple CRM services enabled by " +
						"system or instance.");
			}

			return false;
		}
	}

	private Message _prepareMessage(User model, String action) {
		Lead lead = new Lead();

		lead.setFirstName(model.getFirstName());
		lead.setLastName(model.getLastName());
		lead.setEmail(model.getEmailAddress());
		lead.setPhone(model.getPhones().toString());
		lead.setWebSite(model.getWebsites().toString());
		lead.setDescription(model.getComments());

		try {
			lead.setCompany(_companyLocalService.getCompany(
				model.getCompanyId()).getName());
		}
		catch (PortalException pe) {
			lead.setCompany("No Company Name");
		}

		if (!model.isNew()) {
			lead.setLeadId((String)model.getExpandoBridge().getAttribute(
				CRMUserExtend.CRM_ENTITY_OBJECT_KEY, false));
		}

		Message message = new Message();

		message.put(WebKeys.COMPANY_ID, model.getCompanyId());
		message.put(Constants.CMD, action);
		message.setPayload(lead);

		return message;
	}

	private void _sendToCRMSystem(User model, String actionCommand) {
		try {
			long companyId = model.getCompanyId();

			CRMGroupServiceConfiguration crmGroupServiceConfiguration =
				_getCrmGroupServiceConfiguration(companyId);

			if (_isConfigurationCorrect(crmGroupServiceConfiguration)) {
				if (crmGroupServiceConfiguration.salesforce()) {
					Message message = _prepareMessage(model, actionCommand);

					_messageBus.sendMessage(
						CRMMessageDestinationNames.SALESFORCE_SEND_LEAD,
						message);
				}

				if (crmGroupServiceConfiguration.suitecrm()) {
					Message message = _prepareMessage(model, actionCommand);

					_messageBus.sendMessage(
						CRMMessageDestinationNames.SUITECRM_SEND_LEAD, message);
				}

				if (crmGroupServiceConfiguration.opencrx()) {
					Message message = _prepareMessage(model, actionCommand);

					_messageBus.sendMessage(
						CRMMessageDestinationNames.OPENCRX_SEND_LEAD, message);
				}
			}
		}
		catch (ConfigurationException ce) {
			if (_log.isErrorEnabled()) {
				_log.error(ce);
			}
		}
	}

	@Reference
	private CompanyLocalService _companyLocalService;

	@Reference
	private ConfigurationProvider _configurationProvider;

	private Log _log = LogFactoryUtil.getLog(CRMUserModelListener.class);

	@Reference
	private MessageBus _messageBus;

}