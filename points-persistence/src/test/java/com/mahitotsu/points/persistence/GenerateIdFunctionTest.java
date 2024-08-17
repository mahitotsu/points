package com.mahitotsu.points.persistence;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.sql.DataSource;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

public class GenerateIdFunctionTest extends TestBase {

    @Autowired
    private DataSource dataSource;

    @Test
    public void testGenerateIdFormat() {

        final Map<String, Object> resultMap = new JdbcTemplate(this.dataSource)
                .queryForMap("select generate_id() as id, current_timestamp as now");
        assertNotNull(resultMap);
        assertInstanceOf(UUID.class, resultMap.get("id"));
        assertInstanceOf(Timestamp.class, resultMap.get("now"));

        final String[] id = UUID.class.cast(resultMap.get("id")).toString().split("-");
        final Timestamp now = Timestamp.class.cast(resultMap.get("now"));

        final String idTimestamp = id[0] + id[1] + id[2].substring(2) + id[3].substring(1);
        final String nowString = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(now);
        assertTrue(idTimestamp.equals(nowString));
    }

    @Test
    public void testGeneratedIdUniqueness() {

        final int size = 1000;
        final List<Future<UUID>> completions = new ArrayList<>();
        final Set<UUID> idObjSet = new HashSet<>();
        final Set<String> idStrSet = new HashSet<>();

        final CompletionService<UUID> executor = new ExecutorCompletionService<>(Executors.newFixedThreadPool(10));
        final JdbcTemplate jdbcTemplate = new JdbcTemplate(this.dataSource);
        for (int i = 0; i < size; i++) {
            completions.add(executor.submit(() -> jdbcTemplate.queryForObject("select generate_id()", UUID.class)));
        }

        for (int i = 0; i < size; i++) {
            UUID id;
            try {
                id = completions.get(i).get();
                idObjSet.add(id);
                idStrSet.add(id.toString());
            } catch (InterruptedException | ExecutionException e) {
                fail(e);
            }
        }

        assertEquals(size, idObjSet.size());
        assertEquals(size, idStrSet.size());
    }

    @Test
    public void testGeneratedIdOrder() {

        final int size = 100;
        final List<UUID> idList = new ArrayList<>();
        final JdbcTemplate jdbcTemplate = new JdbcTemplate(this.dataSource);

        for (int i = 0; i < size; i++) {
            final UUID id = jdbcTemplate.queryForObject("select generate_id()", UUID.class);
            idList.add(id);
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                fail(e);
            }
        }

        for (int i = 1; i < idList.size(); i++) {
            assertTrue(idList.get(i).toString().compareTo(idList.get(i - 1).toString()) > 0);
        }
    }
}
