package com.mahitotsu.points;

import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
        "spring.sql.init.mode=always",
})
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class TestBase {
}