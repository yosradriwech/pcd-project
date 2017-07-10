package com.orange.paddock.suma.unittest;

import java.util.Date;
import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import com.orange.paddock.suma.AbstractControllerTest;
import com.orange.paddock.suma.business.manager.SubscriptionStatusUtils;
import com.orange.paddock.suma.dao.mongodb.document.Subscription;
import com.orange.paddock.suma.provider.soap.model.Status;
import com.orange.paddock.suma.provider.soap.model.SubscriptionNotificationRequestType;
import com.orange.paddock.suma.provider.soap.model.SubscriptionNotificationResponseType;
import com.orange.paddock.suma.provider.soap.model.UnsubscriptionNotificationRequestType;
import com.orange.paddock.suma.provider.soap.model.UnsubscriptionNotificationResponseType;

public class NotificationSoapControllerTest extends AbstractControllerTest {

	@Test
	public void notificationSubscriptionTest() throws DatatypeConfigurationException {
		
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.setTime(new Date());
		
		SubscriptionNotificationResponseType expected = new SubscriptionNotificationResponseType();
		Status status = new Status();
		status.setSuccess(true);
		expected.setStatus(status);

		SubscriptionNotificationRequestType parameters = new SubscriptionNotificationRequestType();
		parameters.setSubscriptionId("subId");
		parameters.setRequestId("requestid");
		parameters.setSubscriber("subscriber");
		parameters.setActivationDate(DatatypeFactory.newInstance().newXMLGregorianCalendar(calendar));
		parameters.setAssentForActivation(true);
		
		SubscriptionNotificationResponseType response = soapController.subscriptionNotification(parameters);

		Assert.assertEquals(expected.getStatus().isSuccess(), response.getStatus().isSuccess());

	}
	
	@Test
	public void notificationUnsubscribeTest() throws Exception{
		
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.setTime(new Date());
		
		UnsubscriptionNotificationResponseType expected = new UnsubscriptionNotificationResponseType();
		Status status = new Status();
		status.setSuccess(true);
		expected.setStatus(status);
		
		UnsubscriptionNotificationRequestType parameters = new UnsubscriptionNotificationRequestType();
		parameters.setSubscriptionId("subId");
		parameters.setRequestId("requestid");
		parameters.setActivationDate(DatatypeFactory.newInstance().newXMLGregorianCalendar(calendar));
		parameters.setSubscriber("subscriber");
		parameters.setAssentForActivation(true);

		UnsubscriptionNotificationResponseType response = soapController.unsubscriptionNotification(parameters);
		
		Assert.assertEquals(expected.getStatus().isSuccess(), response.getStatus().isSuccess());
		
	}
	

}
