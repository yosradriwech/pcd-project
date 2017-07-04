package com.orange.paddock.suma.business.mapper;

import com.orange.paddock.suma.business.model.SubscriptionDto;
import com.orange.paddock.suma.dao.mongodb.document.Subscription;

import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.ConfigurableMapper;

public class SubscriptionDtoMapper extends ConfigurableMapper {

	@Override
	protected void configure(MapperFactory factory) {
		factory.classMap(SubscriptionDto.class, Subscription.class).byDefault().register();
	}

}
