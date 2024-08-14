package com.mahitotsu.points.persistence;

import static org.junit.Assert.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

public class SequentialNumberRepositoryTest extends RepositoryTestBase {

    public static final String TEST_SEQ_1 = "testseq_1";
    public static final String TEST_SEQ_2 = "testseq_2";

    @Autowired
    private SequentialNumberRepository sequentialNumberRepository;

    @Test
    @Sql({ "/test-queries/create-test-sequences.sql" })
    public void testIncrementAndGet() {

        final long val1 = this.sequentialNumberRepository.incrementAndGet(TEST_SEQ_1);
        assertEquals(1, val1);

        final long val2 = this.sequentialNumberRepository.incrementAndGet(TEST_SEQ_1);
        assertEquals(2, val2);

        final long val3 = this.sequentialNumberRepository.incrementAndGet(TEST_SEQ_2);
        assertEquals(1, val3);
    }
}
