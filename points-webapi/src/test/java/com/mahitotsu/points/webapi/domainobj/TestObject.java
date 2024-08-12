package com.mahitotsu.points.webapi.domainobj;

import java.util.UUID;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import lombok.Getter;

@Service
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Getter
public class TestObject extends DomainObject {
    public TestObject(final UUID id) {
        super(id);
    }
}
