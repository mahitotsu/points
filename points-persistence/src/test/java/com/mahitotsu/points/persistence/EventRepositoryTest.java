package com.mahitotsu.points.persistence;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class EventRepositoryTest extends TestBase {

    @Autowired
    private EventRepository eventRepository;

    @Test
    public void testPutEvent() {

        final String targetType = "TestEntity";
        final UUID targetId = UUID.randomUUID();
        final String eventType = "TestEvent";

        final UUID eventId = this.eventRepository.putEvent(targetType, targetId, eventType, null);
        assertNotNull(eventId);

        final Long eventTime = this.eventRepository.extractEventTime(eventId);
        assertNotNull(eventTime);

        final List<Map<String, Object>> events = this.eventRepository.listEvents(eventTime, eventTime + 1, eventType,
                targetId);
        assertEquals(1, events.size());
        assertEquals(targetType, events.get(0).get("TARGET_TYPE"));
        assertEquals(targetId, events.get(0).get("TARGET_ID"));
        assertEquals(eventType, events.get(0).get("EVENT_TYPE"));
        assertEquals(eventId, events.get(0).get("EVENT_ID"));
    }
}
