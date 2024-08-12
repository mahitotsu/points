package com.mahitotsu.points.webapi.domainobj;

import java.util.Random;
import java.util.UUID;

import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DomainRepository {

    private static final Random SEED = new Random();

    @Autowired
    private ListableBeanFactory beanFactory;

    public <O extends DomainObject> O newObject(final Class<O> objectType) {
        return this.loadObject(objectType, new UUID(System.currentTimeMillis(), SEED.nextLong()));
    }

    public <O extends DomainObject> O loadObject(final Class<O> objectType, final UUID id) {
        return this.beanFactory.getBean(objectType, id);
    }
}
