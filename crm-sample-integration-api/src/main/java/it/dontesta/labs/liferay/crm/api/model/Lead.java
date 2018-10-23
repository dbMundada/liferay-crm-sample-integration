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

package it.dontesta.labs.liferay.crm.api.model;

import java.io.Serializable;

import java.util.Date;

/**
 * @author Antonio Musarra
 */
public class Lead implements Serializable {

	public Lead() {
	}

	public Lead(
		String salutation, String firstName, String lastName, String company,
		String title, String leadSource, String phone, String mobile,
		String fax, String email, String webSite, String street, String zip,
		String city, String state, String country, String description) {

		_salutation = salutation;
		_firstName = firstName;
		_lastName = lastName;
		_company = company;
		_title = title;
		_leadSource = leadSource;
		_phone = phone;
		_mobile = mobile;
		_fax = fax;
		_email = email;
		_webSite = webSite;
		_street = street;
		_zip = zip;
		_city = city;
		_state = state;
		_country = country;
		_description = description;
	}

	public String getCity() {
		return _city;
	}

	public String getCompany() {
		return _company;
	}

	public String getCountry() {
		return _country;
	}

	public Date getCreateDate() {
		return _createDate;
	}

	public String getDescription() {
		return _description;
	}

	public String getEmail() {
		return _email;
	}

	public String getFax() {
		return _fax;
	}

	public String getFirstName() {
		return _firstName;
	}

	public String getLastName() {
		return _lastName;
	}

	public String getLeadId() {
		return _leadId;
	}

	public String getLeadSource() {
		return _leadSource;
	}

	public String getMobile() {
		return _mobile;
	}

	public Date getModifiedDate() {
		return _modifiedDate;
	}

	public String getPhone() {
		return _phone;
	}

	public String getSalutation() {
		return _salutation;
	}

	public String getState() {
		return _state;
	}

	public String getStreet() {
		return _street;
	}

	public String getTitle() {
		return _title;
	}

	public String getWebSite() {
		return _webSite;
	}

	public String getZip() {
		return _zip;
	}

	public void setCity(String city) {
		_city = city;
	}

	public void setCompany(String company) {
		_company = company;
	}

	public void setCountry(String country) {
		_country = country;
	}

	public void setCreateDate(Date createDate) {
		_createDate = createDate;
	}

	public void setDescription(String description) {
		_description = description;
	}

	public void setEmail(String email) {
		_email = email;
	}

	public void setFax(String fax) {
		_fax = fax;
	}

	public void setFirstName(String firstName) {
		_firstName = firstName;
	}

	public void setLastName(String lastName) {
		_lastName = lastName;
	}

	public void setLeadId(String leadId) {
		_leadId = leadId;
	}

	public void setLeadSource(String leadSource) {
		_leadSource = leadSource;
	}

	public void setMobile(String mobile) {
		_mobile = mobile;
	}

	public void setModifiedDate(Date modifiedDate) {
		_modifiedDate = modifiedDate;
	}

	public void setPhone(String phone) {
		_phone = phone;
	}

	public void setSalutation(String salutation) {
		_salutation = salutation;
	}

	public void setState(String state) {
		_state = state;
	}

	public void setStreet(String street) {
		_street = street;
	}

	public void setTitle(String title) {
		_title = title;
	}

	public void setWebSite(String webSite) {
		_webSite = webSite;
	}

	public void setZip(String zip) {
		_zip = zip;
	}

	@Override
	public String toString() {
		final StringBuffer sb = new StringBuffer("Lead{");
		sb.append("_city='").append(_city).append('\'');
		sb.append(", _company='").append(_company).append('\'');
		sb.append(", _country='").append(_country).append('\'');
		sb.append(", _createDate=").append(_createDate);
		sb.append(", _description='").append(_description).append('\'');
		sb.append(", _email='").append(_email).append('\'');
		sb.append(", _fax='").append(_fax).append('\'');
		sb.append(", _firstName='").append(_firstName).append('\'');
		sb.append(", _lastName='").append(_lastName).append('\'');
		sb.append(", _leadId='").append(_leadId).append('\'');
		sb.append(", _leadSource='").append(_leadSource).append('\'');
		sb.append(", _mobile='").append(_mobile).append('\'');
		sb.append(", _modifiedDate=").append(_modifiedDate);
		sb.append(", _phone='").append(_phone).append('\'');
		sb.append(", _salutation='").append(_salutation).append('\'');
		sb.append(", _state='").append(_state).append('\'');
		sb.append(", _street='").append(_street).append('\'');
		sb.append(", _title='").append(_title).append('\'');
		sb.append(", _webSite='").append(_webSite).append('\'');
		sb.append(", _zip='").append(_zip).append('\'');
		sb.append('}');

		return sb.toString();
	}

	private String _city;
	private String _company;
	private String _country;
	private Date _createDate;
	private String _description;
	private String _email;
	private String _fax;
	private String _firstName;
	private String _lastName;
	private String _leadId;
	private String _leadSource;
	private String _mobile;
	private Date _modifiedDate;
	private String _phone;
	private String _salutation;
	private String _state;
	private String _street;
	private String _title;
	private String _webSite;
	private String _zip;

}