package com.mahitotsu.points.persistence;

import static org.junit.jupiter.api.Assertions.*;

import java.util.UUID;

import org.junit.jupiter.api.Test;

public class EventRepositoryTest extends JdbcTestBase {

    @Test
    public void testGenerateIdFormat() {
        final Object generatedId = this.getSingleObject("select generate_id()");
        assertNotNull(generatedId);
        assertInstanceOf(UUID.class, generatedId);
    }
}
