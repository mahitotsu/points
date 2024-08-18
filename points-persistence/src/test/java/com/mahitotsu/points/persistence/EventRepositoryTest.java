package com.mahitotsu.points.persistence;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import lombok.Data;

public class EventRepositoryTest extends TestBase {

    @Data
    public static class PayloadBean {
        private String text;
        private int integer;
        private String[] array;
        private PayloadBean child;
    }

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
                targetId, null);
        assertEquals(1, events.size());
        assertEquals(targetType, events.get(0).get("TARGET_TYPE"));
        assertEquals(targetId, events.get(0).get("TARGET_ID"));
        assertEquals(eventType, events.get(0).get("EVENT_TYPE"));
        assertEquals(eventId, events.get(0).get("EVENT_ID"));
        assertNull(events.get(0).get("EVENT_PAYLOAD"));
    }

    @Test
    public void testPutEventWithMapPayload() {

        final String targetType = "TestEntity";
        final UUID targetId = UUID.randomUUID();
        final String eventType = "TestEvent";
        final Map<String, Object> eventPayload = Map.of("item", "value");

        final UUID eventId = this.eventRepository.putEvent(targetType, targetId, eventType, eventPayload);
        assertNotNull(eventId);

        final Long eventTime = this.eventRepository.extractEventTime(eventId);
        assertNotNull(eventTime);

        final List<Map<String, Object>> events = this.eventRepository.listEvents(eventTime, eventTime + 1, eventType,
                targetId, HashMap.class);
        assertEquals(1, events.size());
        assertEquals(targetType, events.get(0).get("TARGET_TYPE"));
        assertEquals(targetId, events.get(0).get("TARGET_ID"));
        assertEquals(eventType, events.get(0).get("EVENT_TYPE"));
        assertEquals(eventId, events.get(0).get("EVENT_ID"));

        final Object actualPayload = events.get(0).get("EVENT_PAYLOAD");
        assertInstanceOf(HashMap.class, actualPayload);
        assertEquals(eventPayload, actualPayload);
    }

    @Test
    public void testPutEventWithPojoPayload() {

        final String targetType = "TestEntity";
        final UUID targetId = UUID.randomUUID();
        final String eventType = "TestEvent";

        final PayloadBean eventPayload = new PayloadBean();
        eventPayload.setText("parent");
        eventPayload.setInteger(3);
        eventPayload.setArray(new String[] { "A", "b", "cDe" });
        final PayloadBean child = new PayloadBean();
        child.setText("child");
        eventPayload.setChild(child);

        final UUID eventId = this.eventRepository.putEvent(targetType, targetId, eventType, eventPayload);
        assertNotNull(eventId);

        final Long eventTime = this.eventRepository.extractEventTime(eventId);
        assertNotNull(eventTime);

        final List<Map<String, Object>> events = this.eventRepository.listEvents(eventTime, eventTime + 1, eventType,
                targetId, PayloadBean.class);
        assertEquals(1, events.size());
        assertEquals(targetType, events.get(0).get("TARGET_TYPE"));
        assertEquals(targetId, events.get(0).get("TARGET_ID"));
        assertEquals(eventType, events.get(0).get("EVENT_TYPE"));
        assertEquals(eventId, events.get(0).get("EVENT_ID"));

        final Object actualPayload = events.get(0).get("EVENT_PAYLOAD");
        assertInstanceOf(PayloadBean.class, actualPayload);
        assertEquals(eventPayload, actualPayload);
    }
}
