package com.mahitotsu.points.webapi.domainobj;

import static org.junit.Assert.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.mahitotsu.points.webapi.TestBase;

public class DomainRepositoryTest extends TestBase {

    @Autowired
    private DomainRepository domainRepository;

    @Test
    public void testNewObject() {

        final TestObject obj1 = this.domainRepository.newObject(TestObject.class);
        assertNotNull(obj1);
        assertNotNull(obj1.getId());

        final TestObject obj2 = this.domainRepository.newObject(TestObject.class);
        assertNotNull(obj2);
        assertNotNull(obj2.getId());

        assertFalse(obj1 == obj2);
        assertFalse(obj1.getId().equals(obj2.getId()));
    }
}
