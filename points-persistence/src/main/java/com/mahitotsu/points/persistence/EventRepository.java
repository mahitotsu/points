package com.mahitotsu.points.persistence;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class EventRepository {

    private static final DateFormat DATETIME_FORMAT = new SimpleDateFormat("yyyyMMddHHmmssSSS");

    @Autowired
    private DataSource dataSource;

    @Transactional
    public UUID putEvent(final String targetType, final UUID targetId, final String eventType,
            final Object eventPayload) {

        final KeyHolder keyHolder = new GeneratedKeyHolder();
        new NamedParameterJdbcTemplate(this.dataSource).update("""
                insert into EVENT_STORE (
                    TARGET_TYPE, TARGET_ID, EVENT_TYPE
                ) values (
                    :targetType, :targetId, :eventType
                )
                """,
                new MapSqlParameterSource()
                        .addValue("targetType", targetType)
                        .addValue("targetId", targetId)
                        .addValue("eventType", eventType),
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
            final UUID targetId) {

        return new NamedParameterJdbcTemplate(this.dataSource).queryForList("""
                select * from EVENT_STORE e
                where e.EVENT_ID >= :fromEventId
                    and e.EVENT_ID < :toEventId
                    and e.EVENT_TYPE = :eventType
                    and e.TARGET_ID = :targetId
                """,
                new MapSqlParameterSource()
                        .addValue("fromEventId", this.buildUUID(fromMills))
                        .addValue("toEventId", this.buildUUID(toMillis))
                        .addValue("targetId", targetId)
                        .addValue("eventType", eventType));
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
