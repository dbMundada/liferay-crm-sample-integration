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

package it.dontesta.labs.liferay.crm.users.instance.lifecycle;

import com.liferay.expando.kernel.model.ExpandoColumn;
import com.liferay.expando.kernel.model.ExpandoColumnConstants;
import com.liferay.expando.kernel.model.ExpandoTable;
import com.liferay.expando.kernel.model.ExpandoTableConstants;
import com.liferay.expando.kernel.service.ExpandoColumnLocalService;
import com.liferay.expando.kernel.service.ExpandoTableLocalService;
import com.liferay.portal.instance.lifecycle.BasePortalInstanceLifecycleListener;
import com.liferay.portal.instance.lifecycle.PortalInstanceLifecycleListener;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.RoleConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalServiceUtil;
import com.liferay.portal.kernel.util.Validator;

import it.dontesta.labs.liferay.crm.users.constants.CRMUserExtend;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Antonio Musarra
 */
@Component(immediate = true, service = PortalInstanceLifecycleListener.class)
public class CRMPortalInstanceLifecycleListener
	extends BasePortalInstanceLifecycleListener {

	@Override
	public void portalInstanceRegistered(Company company) throws Exception {
		checkUserCustomFields(company);
	}

	protected void checkUserCustomFields(Company company) throws
		PortalException {

		if (_log.isInfoEnabled()) {
			_log.info("Check if CRM User custom fields exists for companyId: " +
				company.getCompanyId() + "...");
		}

		ExpandoTable table = _expandoTableLocalService.fetchDefaultTable(
			company.getCompanyId(), User.class.getName());

		if (Validator.isNull(table)) {
			table = _expandoTableLocalService.addTable(
				company.getCompanyId(), User.class.getName(),
				ExpandoTableConstants.DEFAULT_TABLE_NAME);
		}

		_addExpandoField(
			company.getCompanyId(), CRMUserExtend.CRM_ENTITY_TYPE,
			ExpandoColumnConstants.STRING, table.getTableId());

		_addExpandoField(
			company.getCompanyId(), CRMUserExtend.CRM_ENTITY_OBJECT_KEY,
			ExpandoColumnConstants.STRING, table.getTableId());

		_addExpandoField(
			company.getCompanyId(), CRMUserExtend.CRM_SYSTEM_NAME,
			ExpandoColumnConstants.STRING, table.getTableId());

		if (_log.isInfoEnabled()) {
			_log.info("Check if CRM User custom fields exists for companyId: " +
				company.getCompanyId() + "...[END]");
		}
	}

	private void _addExpandoField(
			long companyId, String name, int type, long tableId)
		throws PortalException {

		_addExpandoField(companyId, name, type, tableId, null);
	}

	private void _addExpandoField(
			long companyId, String name, int type, long tableId,
			Object defaultValue)
		throws PortalException {

		Role userRole = RoleLocalServiceUtil.getRole(
			companyId, RoleConstants.USER);

		ExpandoColumn column = _expandoColumnLocalService.getColumn(
			tableId, name);

		if (Validator.isNull(column)) {
			if (Validator.isNotNull(defaultValue)) {
				column = _expandoColumnLocalService.addColumn(
					tableId, name, type, defaultValue);
			}
			else {
				column = _expandoColumnLocalService.addColumn(
					tableId, name, type);
			}

			column.setTypeSettings(CRMUserExtend.FIELD_TYPE_SETTING);

			_expandoColumnLocalService.updateExpandoColumn(column);

			_resourcePermissionLocalService.setResourcePermissions(
				companyId, ExpandoColumn.class.getName(),
				ResourceConstants.SCOPE_INDIVIDUAL,
				String.valueOf(column.getColumnId()), userRole.getRoleId(),
				new String[] {ActionKeys.VIEW});

			if (_log.isInfoEnabled()) {
				_log.info(
					"Custom field " + name +
					" created and set VIEW permissions to User role");
			}
		}
	}

	private static Log _log = LogFactoryUtil.getLog(
		CRMPortalInstanceLifecycleListener.class);

	@Reference
	private ExpandoColumnLocalService _expandoColumnLocalService;

	@Reference
	private ExpandoTableLocalService _expandoTableLocalService;

	@Reference
	private ResourcePermissionLocalService _resourcePermissionLocalService;

}