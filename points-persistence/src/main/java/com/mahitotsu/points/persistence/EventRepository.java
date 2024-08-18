package com.mahitotsu.points.persistence;

import java.io.IOException;
import java.sql.Types;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.shaded.com.fasterxml.jackson.core.JsonProcessingException;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.annotation.PostConstruct;

@Repository
public class EventRepository {

    private static final DateFormat DATETIME_FORMAT = new SimpleDateFormat("yyyyMMddHHmmssSSS");

    @Autowired
    private DataSource dataSource;

    private ObjectMapper objectMapper;

    @PostConstruct
    public void setup() {
        final ObjectMapper objectMapper = new ObjectMapper();
        this.objectMapper = objectMapper;
    }

    @Transactional
    public UUID putEvent(final String targetType, final UUID targetId, final String eventType,
            final Object eventPayload) {

        final KeyHolder keyHolder = new GeneratedKeyHolder();
        new NamedParameterJdbcTemplate(this.dataSource).update("""
                insert into EVENT_STORE (
                    TARGET_TYPE, TARGET_ID, EVENT_TYPE, EVENT_PAYLOAD
                ) values (
                    :targetType, :targetId, :eventType, :eventPayload
                )
                """,
                new MapSqlParameterSource()
                        .addValue("targetType", targetType)
                        .addValue("targetId", targetId)
                        .addValue("eventType", eventType)
                        .addValue("eventPayload", Optional.ofNullable(eventPayload).map(p -> {
                            try {
                                return this.objectMapper.writeValueAsString(p);
                            } catch (JsonProcessingException e) {
                                throw new IllegalStateException(e);
                            }
                        }).orElse(null), Types.OTHER, "jsonb"),
                keyHolder, new String[] { "event_id" });

        return keyHolder.getKeyAs(UUID.class);
    }

    public Long extractEventTime(final UUID eventId) {

        return Optional.of(eventId.toString().split("-")).map(i -> i[0] + i[1] + i[2].substring(2) + i[3].substring(1))
                .map(s -> {
                    try {
                        return Long.valueOf(DATETIME_FORMAT.parse(s).getTime());
                    } catch (ParseException e) {
                        return null;
                    }
                }).orElse(null);
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> listEvents(final long fromMills, final long toMillis, final String eventType,
            final UUID targetId, final Class<?> payloadType) {

        final String where = """
                e.EVENT_ID >= :fromEventId and
                e.EVENT_ID < :toEventId and
                e.EVENT_TYPE = :eventType and
                e.TARGET_ID = :targetId
                """;
        final SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("fromEventId", this.buildUUID(fromMills))
                .addValue("toEventId", this.buildUUID(toMillis))
                .addValue("targetId", targetId)
                .addValue("eventType", eventType);

        return this.listEvents(where, parameters, payloadType);
    }

    private List<Map<String, Object>> listEvents(final String where, final SqlParameterSource parameters,
            final Class<?> payloadType) {

        return new NamedParameterJdbcTemplate(this.dataSource).query("""
                select
                    e.EVENT_ID,
                    e.EVENT_TYPE,
                    e.TARGET_TYPE,
                    e.TARGET_ID,
                    e.EVENT_PAYLOAD::varchar
                from
                    EVENT_STORE e
                where
                """ + where,
                parameters,
                (rs, index) -> {
                    final Map<String, Object> map = new HashMap<>();
                    map.put("EVENT_ID", rs.getObject(1, UUID.class));
                    map.put("EVENT_TYPE", rs.getString(2));
                    map.put("TARGET_TYPE", rs.getString(3));
                    map.put("TARGET_ID", rs.getObject(4, UUID.class));
                    map.put("EVENT_PAYLOAD", Optional.ofNullable(rs.getString(5)).map(s -> {
                        try {
                            return payloadType == null ? s : this.objectMapper.readValue(s, payloadType);
                        } catch (IOException e) {
                            throw new IllegalStateException(e);
                        }
                    }).orElse(null));
                    return map;
                });
    }

    private UUID buildUUID(final long epochTime) {

        return Optional.of(DATETIME_FORMAT.format(epochTime)).map(t -> UUID.fromString(
                String.join("-",
                        new String[] { t.substring(0, 8), t.substring(8, 12), "00" + t.substring(12, 14),
                                "0" + t.substring(14, 17),
                                "000000000000"
                        })))
                .get();
    }
}
