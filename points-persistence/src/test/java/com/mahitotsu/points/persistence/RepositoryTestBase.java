package com.mahitotsu.points.persistence;


import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.stereotype.Repository;

@JdbcTest(includeFilters = {
        @Filter(type = FilterType.ANNOTATION, classes = { Configuration.class }),
        @Filter(type = FilterType.ANNOTATION, classes = { Repository.class }),
})
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class RepositoryTestBase {

}
