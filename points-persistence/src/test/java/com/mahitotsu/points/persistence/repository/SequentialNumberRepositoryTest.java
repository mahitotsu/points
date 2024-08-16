package com.mahitotsu.points.persistence.repository;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

@Sql({ "/test-queries/999-create-test-sequences.sql" })
public class SequentialNumberRepositoryTest extends RepositoryTestBase {

    public static final String TEST_SEQ_1 = "testseq_1";
    public static final String TEST_SEQ_2 = "testseq_2";

    @Autowired
    private SequentialNumberRepository sequentialNumberRepository;

    @Test
    public void testIncrementAndGet() {

        final long val1 = this.sequentialNumberRepository.incrementAndGet(TEST_SEQ_1);
        assertEquals(1, val1);

        final long val2 = this.sequentialNumberRepository.incrementAndGet(TEST_SEQ_1);
        assertEquals(2, val2);

        final long val3 = this.sequentialNumberRepository.incrementAndGet(TEST_SEQ_2);
        assertEquals(1, val3);
    }
}
