package com.mahitotsu.points.persistence;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class EventRepositoryTest extends TestBase {

    @Autowired
    private EventRepository eventRepository;

    @Test
    public void testPutEventWithoutPayload() {

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
        assertNull(events.get(0).get("EVENT_PAYLOAD"));
    }

    @Test
    public void testPutEventWithPayload() {

        final String targetType = "TestEntity";
        final UUID targetId = UUID.randomUUID();
        final String eventType = "TestEvent";
        final Map<String, Object> eventPayload = Map.of("item", "value");

        final UUID eventId = this.eventRepository.putEvent(targetType, targetId, eventType, eventPayload);
        assertNotNull(eventId);

        final Long eventTime = this.eventRepository.extractEventTime(eventId);
        assertNotNull(eventTime);

        final List<Map<String, Object>> events = this.eventRepository.listEvents(eventTime, eventTime + 1, eventType,
                targetId);
        assertEquals(1, events.size());
        System.out.println(events.get(0).get("EVENT_PAYLOAD").getClass());
        assertEquals(targetType, events.get(0).get("TARGET_TYPE"));
        assertEquals(targetId, events.get(0).get("TARGET_ID"));
        assertEquals(eventType, events.get(0).get("EVENT_TYPE"));
        assertEquals(eventId, events.get(0).get("EVENT_ID"));
        assertEquals(eventPayload, events.get(0).get("EVENT_PAYLOAD"));
    }
}
