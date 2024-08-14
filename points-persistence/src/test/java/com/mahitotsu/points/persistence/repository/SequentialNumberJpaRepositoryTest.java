package com.mahitotsu.points.persistence.repository;

import static org.junit.Assert.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.mahitotsu.points.persistence.entity.SequentialNumberEntity;

@DataJpaTest(properties = "spring.jpa.generate-ddl=true")
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class SequentialNumberJpaRepositoryTest {

    private static final String TEST_SEQUENCE_1_NAME = "test-seqnum-1";
    private static final String TEST_SEQUENCE_2_NAME = "test-seqnum-2";

    @Autowired
    private SequentialNumberJpaRepository repository;

    @BeforeEach
    public void prepareTestData() {
        this.repository.save(new SequentialNumberEntity(TEST_SEQUENCE_1_NAME, 0));
        this.repository.save(new SequentialNumberEntity(TEST_SEQUENCE_2_NAME, 0));
    }

    @Test
    public void testGetNextValue() {
        assertEquals(1, this.repository.getByName(TEST_SEQUENCE_1_NAME).increaseAndGet());
        assertEquals(2, this.repository.getByName(TEST_SEQUENCE_1_NAME).increaseAndGet());
        assertEquals(1, this.repository.getByName(TEST_SEQUENCE_2_NAME).increaseAndGet());
        assertEquals(3, this.repository.getByName(TEST_SEQUENCE_1_NAME).increaseAndGet());
    }
}
