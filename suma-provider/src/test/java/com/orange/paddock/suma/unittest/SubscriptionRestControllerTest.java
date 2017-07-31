package com.orange.paddock.suma.unittest;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Date;
import java.util.UUID;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import com.orange.paddock.commons.http.PdkHeader;
import com.orange.paddock.suma.AbstractControllerTest;
import com.orange.paddock.suma.business.exception.SumaAlreadyRevokedSubException;
import com.orange.paddock.suma.business.exception.SumaUnknownSubscriptionIdException;
import com.orange.paddock.suma.business.model.SubscriptionDto;
import com.orange.paddock.suma.business.model.SubscriptionResponse;


public class SubscriptionRestControllerTest extends AbstractControllerTest {

	@Test
	public void subscribeTest() throws Exception {

		String content = readResourceFile("request/subscription_request.json");

		SubscriptionResponse response = new SubscriptionResponse();
		response.setCcgwSubscriptionId(UUID.randomUUID().toString());
		given(manager.subscribe(any(SubscriptionDto.class), any(String.class), any(String.class))).willReturn(response);

		MvcResult result = mockMvc
				.perform(post(SUMA_ENDPOINT_SUBSCRIPTION).accept(MediaType.APPLICATION_JSON)
						.contentType(MediaType.APPLICATION_JSON).content(content))
				.andDo(print()).andExpect(status().isCreated()).andReturn();

		Assert.assertEquals("subscription/v1/subscriptions/" + response.getCcgwSubscriptionId(),
				result.getResponse().getHeader("Location"));
	}

	@Test
	public void subscribeWithOatHeaderTest() throws Exception {

		String content = readResourceFile("request/subscription_oat_request.json");
		String headerValue = "OAT";

		given(manager.subscribe(any(SubscriptionDto.class), any(String.class), any(String.class)))
				.willReturn(new SubscriptionResponse());

		mockMvc.perform(post(SUMA_ENDPOINT_SUBSCRIPTION).header(PdkHeader.ORANGE_API_TOKEN, headerValue)
				.accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON).content(content))
				.andDo(print()).andExpect(status().isCreated()).andReturn();

	}

	@Test
	public void subscribeWithIse2HeaderTest() throws Exception {

		String content = String.format(readResourceFile("request/subscription_ise2_request.json"));
		String headerValue = "ISE2";
		String mco = "OFR";

		given(manager.subscribe(any(SubscriptionDto.class), any(String.class), any(String.class)))
				.willReturn(new SubscriptionResponse());

		mockMvc.perform(post(SUMA_ENDPOINT_SUBSCRIPTION).header(PdkHeader.ORANGE_ISE2, headerValue)
				.header(PdkHeader.ORANGE_MCO, mco).accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON).content(content)).andDo(print())
				.andExpect(status().isCreated()).andReturn();
	}

	@Test
	public void unsubscribeTest() throws Exception {

		mockMvc.perform(delete(SUMA_ENDPOINT_UNSUBSCRIPTION + "test").contentType(MediaType.APPLICATION_JSON))
				.andDo(print()).andExpect(status().isNoContent()).andReturn();
	}

	@Test
	public void unsubscribeWithUnknownSubscriptionId() throws Exception {

		String unknownSubscriptionId = "unknown_subscription_id";

		given(this.manager.unsubscribe(unknownSubscriptionId))
				.willThrow(new SumaUnknownSubscriptionIdException(unknownSubscriptionId));

		mockMvc.perform(
				delete(SUMA_ENDPOINT_UNSUBSCRIPTION + unknownSubscriptionId).contentType(MediaType.APPLICATION_JSON))
				.andDo(print()).andExpect(status().isNotFound()).andExpect(jsonPath("code").value("00004"))
				.andExpect(jsonPath("message").value("Unknown subscription identifier"))
				.andExpect(jsonPath("description").value("Unknown subscription: " + unknownSubscriptionId));
	}

	@Test
	public void unsubscribeWithRevokedSubscriptionId() throws Exception {
		String revokedSubscriptionId = "revoked_subscription_id";

		given(this.manager.unsubscribe(revokedSubscriptionId))
				.willThrow(new SumaAlreadyRevokedSubException(revokedSubscriptionId));

		mockMvc.perform(
				delete(SUMA_ENDPOINT_UNSUBSCRIPTION + revokedSubscriptionId).contentType(MediaType.APPLICATION_JSON))
				.andDo(print()).andExpect(status().isNotFound()).andExpect(jsonPath("code").value("00004"))
				.andExpect(jsonPath("message").value("Subscription already revoked"))
				.andExpect(jsonPath("description").value("Subscription already revoked: " + revokedSubscriptionId));
	}

	@Test
	public void getSubscriptionStatusTest() throws Exception {

		SubscriptionDto subscriptionDto = new SubscriptionDto();
		// subscriptionDto.setSubscriptionId("subscriptionId");
		subscriptionDto.setTransactionId("transactionId");
		subscriptionDto.setServiceId(serviceId);
		subscriptionDto.setOnBehalfOf(onBehalfOf);
		subscriptionDto.setEndUserId(endUserid);
		subscriptionDto.setDescription(description);
		subscriptionDto.setCategoryCode(categoryCode);
		subscriptionDto.setAmount(amount);
		subscriptionDto.setTaxedAmount(taxedAmount);
		subscriptionDto.setCurrency(currency);
		subscriptionDto.setCreationDate(new Date());
		// subscriptionDto.isAdult();
		subscriptionDto.setStatus("ACTIVE");

		given(manager.getSubscriptionStatus("subscriptionId")).willReturn(subscriptionDto);

		mockMvc.perform(get(SUMA_ENDPOINT_SUBSCRIPTION + "subscriptionId").contentType(MediaType.APPLICATION_JSON))
				.andDo(print()).andExpect(status().isOk())
				.andExpect(jsonPath("transactionId").value(subscriptionDto.getTransactionId()))
				.andExpect(jsonPath("serviceId").value("Fortumo"))
				.andExpect(jsonPath("onBehalfOf").value("Marc Dorcel"))
				.andExpect(jsonPath("endUserId").value("tel:+33123456789"))
				.andExpect(jsonPath("description").value("XXX Content"))
				.andExpect(jsonPath("categoryCode").value("XXX")).andExpect(jsonPath("amount").value("100"))
				.andExpect(jsonPath("taxedAmount").value("120")).andExpect(jsonPath("currency").value("PLN"))
				.andExpect(jsonPath("status").value("ACTIVE"));
	}

	@Test
	public void subscribeWithOatHeaderAndEmptyValueTest() throws Exception {

		String content = readResourceFile("request/subscription_oat_request.json");
		String headerValue = "";

		mockMvc.perform(post(SUMA_ENDPOINT_SUBSCRIPTION).header(PdkHeader.ORANGE_API_TOKEN, headerValue)
				.accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON).content(content))
				.andDo(print()).andExpect(status().isBadRequest()).andExpect(jsonPath("code").value("00003"))
				.andExpect(jsonPath("message").value("Bad request")).andExpect(jsonPath("description")
						.value("Invalid or missing parameter :Missing header Orange API Token."));
	}

	@Test
	public void subscribeWithIse2HeaderAndEmptyValueTest() throws Exception {

		String content = String.format(readResourceFile("request/subscription_ise2_request.json"));
		String headerValue = "";
		String mco = "OFR";

		mockMvc.perform(post(SUMA_ENDPOINT_SUBSCRIPTION).header(PdkHeader.ORANGE_ISE2, headerValue)
				.accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON).content(content))
				.andDo(print()).andExpect(status().isBadRequest()).andExpect(jsonPath("code").value("00003"))
				.andExpect(jsonPath("message").value("Bad request"))
				.andExpect(jsonPath("description").value("Invalid or missing parameter :Missing header ISE2."));
	}

	@Test
	public void subscribeWithIse2HeaderWithoutMcoTest() throws Exception {

		String content = String.format(readResourceFile("request/subscription_ise2_request.json"));
		String headerValue = "ISE2";

		mockMvc.perform(post(SUMA_ENDPOINT_SUBSCRIPTION).header(PdkHeader.ORANGE_ISE2, headerValue)
				.accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON).content(content))
				.andDo(print()).andExpect(status().isBadRequest()).andExpect(jsonPath("code").value("00003"))
				.andExpect(jsonPath("message").value("Bad request"))
				.andExpect(jsonPath("description").value("Invalid or missing parameter :Missing required header MCO."));
	}

	@Test
	public void subscribeInvalidEndUserIdFormatTest() throws Exception {

		String content = String.format(readResourceFile("request/subscription_oat_invalid_parameter_request.json"));
		String headerValue = "OAT";

		mockMvc.perform(post(SUMA_ENDPOINT_SUBSCRIPTION).header(PdkHeader.ORANGE_API_TOKEN, headerValue)
				.accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON).content(content))
				.andDo(print()).andExpect(status().isBadRequest()).andExpect(jsonPath("code").value("00003"))
				.andExpect(jsonPath("message").value("Bad request"))
				.andExpect(jsonPath("description").value("Invalid or missing parameter :endUserId format"));
	}

	@Test
	public void subscribeMissingServiceIdTest() throws Exception {

		String content = String.format(readResourceFile("request/subscription_oat_missing_serviceId_request.json"));
		String headerValue = "OAT";

		mockMvc.perform(post(SUMA_ENDPOINT_SUBSCRIPTION).header(PdkHeader.ORANGE_API_TOKEN, headerValue)
				.accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON).content(content))
				.andDo(print()).andExpect(status().isBadRequest()).andExpect(jsonPath("code").value("00003"))
				.andExpect(jsonPath("message").value("Bad request"))
				.andExpect(jsonPath("description").value("Invalid or missing parameter :serviceId"));
	}

	@Test
	public void subscribeMissingOnBehaldOfTest() throws Exception {

		String content = String.format(readResourceFile("request/subscription_ise2_missing_onBehalfOf_request.json"));
		String headerValue = "ise";
		String mco = "FRA";

		mockMvc.perform(post(SUMA_ENDPOINT_SUBSCRIPTION).header(PdkHeader.ORANGE_ISE2, headerValue)
				.header(PdkHeader.ORANGE_MCO, mco).accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON).content(content)).andDo(print())
				.andExpect(status().isBadRequest()).andExpect(jsonPath("code").value("00003"))
				.andExpect(jsonPath("message").value("Bad request"))
				.andExpect(jsonPath("description").value("Invalid or missing parameter :onBehalfOf"));
	}

	@Test
	public void subscribeMissingDescriptionTest() throws Exception {

		String content = String.format(readResourceFile("request/subscription_oat_missing_description_request.json"));
		String headerValue = "OAT";

		mockMvc.perform(post(SUMA_ENDPOINT_SUBSCRIPTION).header(PdkHeader.ORANGE_API_TOKEN, headerValue)
				.accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON).content(content))
				.andDo(print()).andExpect(status().isBadRequest()).andExpect(jsonPath("code").value("00003"))
				.andExpect(jsonPath("message").value("Bad request"))
				.andExpect(jsonPath("description").value("Invalid or missing parameter :description"));
	}

	@Test
	public void subscribeMissingCategoryCodeTest() throws Exception {

		String content = String.format(readResourceFile("request/subscription_oat_missing_categoryCode_request.json"));
		String headerValue = "OAT";

		mockMvc.perform(post(SUMA_ENDPOINT_SUBSCRIPTION).header(PdkHeader.ORANGE_API_TOKEN, headerValue)
				.accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON).content(content))
				.andDo(print()).andExpect(status().isBadRequest()).andExpect(jsonPath("code").value("00003"))
				.andExpect(jsonPath("message").value("Bad request"))
				.andExpect(jsonPath("description").value("Invalid or missing parameter :categoryCode"));
	}

	@Test
	public void subscribeMissingAmountTest() throws Exception {

		String content = String.format(readResourceFile("request/subscription_oat_missing_amount_request.json"));
		String headerValue = "OAT";

		mockMvc.perform(post(SUMA_ENDPOINT_SUBSCRIPTION).header(PdkHeader.ORANGE_API_TOKEN, headerValue)
				.accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON).content(content))
				.andDo(print()).andExpect(status().isBadRequest()).andExpect(jsonPath("code").value("00003"))
				.andExpect(jsonPath("message").value("Bad request"))
				.andExpect(jsonPath("description").value("Invalid or missing parameter :amount"));
	}

	@Test
	public void subscribeMissingTaxedAmountTest() throws Exception {

		String content = String.format(readResourceFile("request/subscription_oat_missing_taxAmount_request.json"));
		String headerValue = "OAT";

		mockMvc.perform(post(SUMA_ENDPOINT_SUBSCRIPTION).header(PdkHeader.ORANGE_API_TOKEN, headerValue)
				.accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON).content(content))
				.andDo(print()).andExpect(status().isBadRequest()).andExpect(jsonPath("code").value("00003"))
				.andExpect(jsonPath("message").value("Bad request"))
				.andExpect(jsonPath("description").value("Invalid or missing parameter :taxedAmount"));
	}

	@Test
	public void subscribeMissingCurrencyTest() throws Exception {

		String content = String.format(readResourceFile("request/subscription_oat_missing_currency_request.json"));
		String headerValue = "OAT";

		mockMvc.perform(post(SUMA_ENDPOINT_SUBSCRIPTION).header(PdkHeader.ORANGE_API_TOKEN, headerValue)
				.accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON).content(content))
				.andDo(print()).andExpect(status().isBadRequest()).andExpect(jsonPath("code").value("00003"))
				.andExpect(jsonPath("message").value("Bad request"))
				.andExpect(jsonPath("description").value("Invalid or missing parameter :currency"));
	}

	@Test
	public void subscribeMissingIsAdultTest() throws Exception {

		String content = String.format(readResourceFile("request/subscription_oat_missing_isAdult_request.json"));
		String headerValue = "OAT";

		mockMvc.perform(post(SUMA_ENDPOINT_SUBSCRIPTION).header(PdkHeader.ORANGE_API_TOKEN, headerValue)
				.accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON).content(content))
				.andDo(print()).andExpect(status().isBadRequest()).andExpect(jsonPath("code").value("00003"))
				.andExpect(jsonPath("message").value("Bad request"))
				.andExpect(jsonPath("description").value("Invalid or missing parameter :isAdult"));
	}

	// @Test
	// public void subscribeMissingAllPinputsTest() throws Exception {
	//
	// String content =
	// String.format(readResourceFile("request/subscription_oat_missing_all_inputs_request.json"));
	// String headerValue = "OAT";
	// String test = null;
	//
	// mockMvc.perform(
	// post(SUMA_ENDPOINT_SUBSCRIPTION).header(PdkHeader.ORANGE_API_TOKEN,
	// headerValue)
	// .accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON).content(test))
	// .andDo(print())
	// .andExpect(status().isBadRequest())
	// .andExpect(jsonPath("internalErrorCode").value("PDK_SUMA_0001"))
	// .andExpect(jsonPath("errorCode").value("00003"))
	// .andExpect(jsonPath("errorDescription").value("Invalid or missing
	// parameter :All mandatory fields"))
	// .andExpect(jsonPath("httpStatusCode").value(400));
	// }
	//

}
