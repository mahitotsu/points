package com.mahitotsu.points.persistence;

import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
        "spring.sql.init.mode=always",
        "spring.jpa.open-in-vew=false",
        "spring.jpa.show-sql=false",
        "spring.jpa.generate-ddl=false"
})
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class TestBase {
}