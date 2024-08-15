package com.mahitotsu.points.persistence.repository;

import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.FilterType;
import org.springframework.stereotype.Repository;

@DataJpaTest(properties = "spring.jpa.generate-ddl=true", includeFilters = {
        @Filter(type = FilterType.ANNOTATION, classes = { Repository.class })
})
@AutoConfigureTestDatabase(replace = Replace.NONE)
public abstract class RepositoryTestBase {

}
