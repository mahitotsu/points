package com.mahitotsu.points.persistence.repository;

import static org.junit.Assert.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class SequentialNumberRepositoryTest extends RepositoryTestBase {

    private static final String TEST_SEQUENCE_1_NAME = "test-seqnum-1";
    private static final String TEST_SEQUENCE_2_NAME = "test-seqnum-2";

    @Autowired
    private SequentialNumberRepository repository;

    @BeforeEach
    public void prepareTestData() {
        this.repository.registerSequentialNumberIfNotExists(TEST_SEQUENCE_1_NAME, 0);
        this.repository.registerSequentialNumberIfNotExists(TEST_SEQUENCE_2_NAME, 0);
    }

    @Test
    public void testGetNextValue() {
        assertEquals(1, this.repository.increaseAndGet(TEST_SEQUENCE_1_NAME));
        assertEquals(2, this.repository.increaseAndGet(TEST_SEQUENCE_1_NAME));
        assertEquals(1, this.repository.increaseAndGet(TEST_SEQUENCE_2_NAME));
        assertEquals(3, this.repository.increaseAndGet(TEST_SEQUENCE_1_NAME));
    }

    @Test
    public void testTryToRegisterExistsSequentialNumber() {

        final long current = this.repository.increaseAndGet(TEST_SEQUENCE_1_NAME);
        this.repository.registerSequentialNumberIfNotExists(TEST_SEQUENCE_1_NAME, 0);

        final long next = this.repository.increaseAndGet(TEST_SEQUENCE_1_NAME);
        assertNotEquals(0, next);
        assertNotEquals(current, next);
        assertEquals(current + 1, next);
    }
}
