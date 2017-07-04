package com.orange.paddock.suma.business.mapper;

import org.springframework.stereotype.Component;

import com.orange.paddock.suma.business.model.SubscriptionDto;
import com.orange.paddock.suma.consumer.ccgw.model.SumaSubscriptionRequest;
import com.orange.paddock.suma.consumer.ccgw.model.SumaUnsubscriptionRequest;
import com.orange.paddock.suma.dao.mongodb.document.Subscription;

import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.ConfigurableMapper;

@Component
public class SubscriptionDtoMapper extends ConfigurableMapper {
	@Override
	protected void configure(MapperFactory factory) {

		factory.classMap(SubscriptionDto.class, Subscription.class).exclude("creationDate").byDefault().register();
		
		factory.classMap(Subscription.class, SubscriptionDto.class).byDefault().register();
		
		factory.classMap(SubscriptionDto.class, SumaUnsubscriptionRequest.class).byDefault().register();
		
		//CCGW requests Mapping
		factory.classMap(SubscriptionDto.class, SumaSubscriptionRequest.class)
		.field("serviceId", "providerId")
		.field("onBehalfOf", "saleProviderId")
		.field("description", "contentName")
		.field("categoryCode", "contentType")
		.field("isAdult", "adultFlag")
		//.field("subscriber", "endUserId")
		.byDefault().register();

		
		factory.classMap(SubscriptionDto.class, SumaUnsubscriptionRequest.class)
		.field("serviceId", "providerId")
		//.field("subscriber", "endUserId")
		.byDefault().register();
		
	}

}
