package com.mahitotsu.points.webapi;

import static io.restassured.RestAssured.*;

import org.junit.jupiter.api.Test;

public class ApiHealthTest extends TestBase {

    @Test
    public void testHealth() throws Exception {
        given()
                .when().get("/actuator/health")
                .then().statusCode(200);
    }
}
