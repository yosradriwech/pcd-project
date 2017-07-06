package com.orange.paddock.suma.unittest;

import org.junit.Assert;
import org.junit.Test;

import com.orange.paddock.suma.AbstractControllerTest;
import com.orange.paddock.suma.provider.soap.model.Status;
import com.orange.paddock.suma.provider.soap.model.SubscriptionNotificationRequestType;
import com.orange.paddock.suma.provider.soap.model.SubscriptionNotificationResponseType;
import com.orange.paddock.suma.provider.soap.model.UnsubscriptionNotificationRequestType;
import com.orange.paddock.suma.provider.soap.model.UnsubscriptionNotificationResponseType;

public class NotificationSoapControllerTest extends AbstractControllerTest {

	@Test
	public void notificationSubscriptionTest() {

		SubscriptionNotificationResponseType expected = new SubscriptionNotificationResponseType();
		Status status = new Status();
		status.setSuccess(true);
		expected.setStatus(status);

		SubscriptionNotificationRequestType parameters = new SubscriptionNotificationRequestType();
		parameters.setSubscriptionId("subId");
		parameters.setRequestId("requestid");
		parameters.setSubscriber("subscriber");
		parameters.setAssentForActivation(true);
		
		SubscriptionNotificationResponseType response = soapController.subscriptionNotification(parameters);

		Assert.assertEquals(expected.getStatus().isSuccess(), response.getStatus().isSuccess());

	}
	
	@Test
	public void notificationUnsubscribeTest() throws Exception{
		
		UnsubscriptionNotificationResponseType expected = new UnsubscriptionNotificationResponseType();
		Status status = new Status();
		status.setSuccess(true);
		expected.setStatus(status);
		
		UnsubscriptionNotificationRequestType parameters = new UnsubscriptionNotificationRequestType();
		parameters.setSubscriptionId("subId");
		parameters.setRequestId("requestid");
		parameters.setSubscriber("subscriber");

		UnsubscriptionNotificationResponseType response = soapController.unsubscriptionNotification(parameters);
		
		Assert.assertEquals(expected.getStatus().isSuccess(), response.getStatus().isSuccess());
		
	}
	

}
