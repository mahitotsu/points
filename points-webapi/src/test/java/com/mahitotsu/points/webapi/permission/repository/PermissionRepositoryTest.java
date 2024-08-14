package com.mahitotsu.points.webapi.permission.repository;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.stereotype.Repository;

@DataJpaTest(includeFilters = {
        @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = { Repository.class })
})
public class PermissionRepositoryTest {

    @Autowired
    private PermissionRepository permissionRepository;

    @Test
    public void testCRUDPermission() {

        final String serviceName = "Testing";

        assertFalse(this.permissionRepository.existsPermission(serviceName));

        this.permissionRepository.registerPermission(serviceName);
        assertTrue(this.permissionRepository.existsPermission(serviceName));
    }
}
