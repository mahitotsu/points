package com.mahitotsu.points.webapi.eventhub.repository.jpa;

import static org.junit.Assert.*;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.mahitotsu.points.webapi.TestBase;

import lombok.Value;

public class EventJpaRepositoryTest extends TestBase {

    @Value
    private class PayloadBean1 {
        private final String key;
    }

    @Value
    private class PayloadBean2 {
        private final String body;
    }

    @Autowired
    private EventJpaRepository eventRepository;

    @Test
    public void testPutAndFetchEvents() {

        final int size = 10;
        final String targetEntityName = "TestEntity";
        final UUID targetEntityId = UUID.randomUUID();
        final String eventType = "TestEvent";
        final String payload = "{\"key\":\"value\"}";

        final long beforeTime = System.currentTimeMillis();
        IntStream.range(0, size)
                .forEach(i -> this.eventRepository.putEvent(targetEntityName, targetEntityId, eventType, payload));
        final long afterTime = System.currentTimeMillis();

        List<EventEntity> eventList;
        UUID id;
        long eventTime;

        // fetch from first to last
        eventList = this.doInROTx(() -> this.eventRepository
                .fetchEvents(targetEntityName, targetEntityId, eventType, beforeTime, afterTime + 1, Integer.MAX_VALUE)
                .collect(Collectors.toList()));
        assertEquals(size, eventList.size());

        id = null;
        eventTime = beforeTime;
        for (final EventEntity event : eventList) {
            assertTrue(id == null || id.compareTo(event.getId()) < 0);
            assertTrue(eventTime - event.getEventTime() <= 0);
            id = event.getId();
            eventTime = event.getEventTime();
        }
        assertTrue(eventTime - afterTime <= 0);

        // fetch from last to first
        eventList = this.doInROTx(() -> this.eventRepository
                .fetchEvents(targetEntityName, targetEntityId, eventType, afterTime, beforeTime - 1, Integer.MAX_VALUE)
                .collect(Collectors.toList()));
        assertEquals(size, eventList.size());

        id = null;
        eventTime = afterTime;
        for (final EventEntity event : eventList) {
            assertTrue(id == null || id.compareTo(event.getId()) > 0);
            assertTrue(eventTime - event.getEventTime() >= 0);
            id = event.getId();
            eventTime = event.getEventTime();
        }
        assertTrue(eventTime - beforeTime >= 0);
    }

    @Test
    public void testJsonStringPayload() {

        final String targetEntityName = "TestEntity";
        final UUID targetEntityId = UUID.randomUUID();
        final String eventType = "TestEvent";
        final String payload = "{\"key\":\"value\"}";

        final long startTime = System.currentTimeMillis();
        this.eventRepository.putEvent(targetEntityName, targetEntityId, eventType, payload);
        final long stopTime = System.currentTimeMillis() + 1;

        final EventEntity event = this.doInROTx(() -> this.eventRepository.fetchEvents(targetEntityName,
                targetEntityId, eventType, startTime, stopTime, Integer.MAX_VALUE).findFirst().get());

        final Object actual = event.getPayload();
        assertTrue(Map.class.isInstance(actual));
        assertEquals("value", Map.class.cast(actual).get("key"));
    }

    @Test
    public void testMapPayload() {

        final String targetEntityName = "TestEntity";
        final UUID targetEntityId = UUID.randomUUID();
        final String eventType = "TestEvent";
        final Map<?, ?> payload = Map.of("key", "value");

        final long startTime = System.currentTimeMillis();
        this.eventRepository.putEvent(targetEntityName, targetEntityId, eventType, payload);
        final long stopTime = System.currentTimeMillis() + 1;

        final EventEntity event = this.doInROTx(() -> this.eventRepository.fetchEvents(targetEntityName,
                targetEntityId, eventType, startTime, stopTime, Integer.MAX_VALUE).findFirst().get());

        final Object actual = event.getPayload();
        assertTrue(Map.class.isInstance(actual));
        assertEquals("value", Map.class.cast(actual).get("key"));
    }

    @Test
    public void testPOJOPayload() {

        final String targetEntityName = "TestEntity";
        final UUID targetEntityId = UUID.randomUUID();
        final String eventType = "TestEvent";
        final PayloadBean1 payload = new PayloadBean1("value");

        final long startTime = System.currentTimeMillis();
        this.eventRepository.putEvent(targetEntityName, targetEntityId, eventType, payload);
        final long stopTime = System.currentTimeMillis() + 1;

        final EventEntity event = this.doInROTx(() -> this.eventRepository.fetchEvents(targetEntityName,
                targetEntityId, eventType, startTime, stopTime, Integer.MAX_VALUE).findFirst().get());

        final Object actual = event.getPayload();
        assertTrue(Map.class.isInstance(actual));
        assertEquals("value", Map.class.cast(actual).get("key"));
    }

    @Test
    public void testFetchAllEventTypes() {

        final String targetEntityName = "TestEntity";
        final UUID targetEntityId = UUID.randomUUID();

        final String eventType1 = "TestEvent1";
        final PayloadBean1 payload1 = new PayloadBean1("value");
        final String eventType2 = "TestEvent2";
        final PayloadBean2 payload2 = new PayloadBean2("hello");

        final long startTime = System.currentTimeMillis();
        this.eventRepository.putEvent(targetEntityName, targetEntityId, eventType1, payload1);
        this.eventRepository.putEvent(targetEntityName, targetEntityId, eventType2, payload2);
        final long stopTime = System.currentTimeMillis() + 1;

        final List<EventEntity> eventList = this.doInROTx(() -> this.eventRepository.fetchEvents(targetEntityName,
                targetEntityId, startTime, stopTime, Integer.MAX_VALUE).collect(Collectors.toList()));
        assertEquals(2, eventList.size());

        final EventEntity first = eventList.get(0);
        assertEquals("TestEvent1", first.getEventType());
        assertEquals("value", Map.class.cast(first.getPayload()).get("key"));

        final EventEntity second = eventList.get(1);
        assertEquals("TestEvent2", second.getEventType());
        assertEquals("hello", Map.class.cast(second.getPayload()).get("body"));
    }

    @Test
    public void testFetchBothEventTypes() {

        final String targetEntityName = "TestEntity";
        final UUID targetEntityId = UUID.randomUUID();

        final String eventType1 = "TestEvent1";
        final PayloadBean1 payload1 = new PayloadBean1("value");
        final String eventType2 = "TestEvent2";
        final PayloadBean2 payload2 = new PayloadBean2("hello");

        final long startTime = System.currentTimeMillis();
        this.eventRepository.putEvent(targetEntityName, targetEntityId, eventType1, payload1);
        this.eventRepository.putEvent(targetEntityName, targetEntityId, eventType2, payload2);
        final long stopTime = System.currentTimeMillis() + 1;

        final List<EventEntity> eventList = this.doInROTx(() -> this.eventRepository.fetchEvents(targetEntityName,
                targetEntityId, Set.of(eventType1, eventType2), startTime, stopTime, Integer.MAX_VALUE)
                .collect(Collectors.toList()));
        assertEquals(2, eventList.size());

        final EventEntity first = eventList.get(0);
        assertEquals("TestEvent1", first.getEventType());
        assertEquals("value", Map.class.cast(first.getPayload()).get("key"));

        final EventEntity second = eventList.get(1);
        assertEquals("TestEvent2", second.getEventType());
        assertEquals("hello", Map.class.cast(second.getPayload()).get("body"));
    }

    @Test
    public void testFetchSpecifiedEventTypeOnly() {

        final String targetEntityName = "TestEntity";
        final UUID targetEntityId = UUID.randomUUID();

        final String eventType1 = "TestEvent1";
        final PayloadBean1 payload1 = new PayloadBean1("value");
        final String eventType2 = "TestEvent2";
        final PayloadBean2 payload2 = new PayloadBean2("hello");

        final long startTime = System.currentTimeMillis();
        this.eventRepository.putEvent(targetEntityName, targetEntityId, eventType1, payload1);
        this.eventRepository.putEvent(targetEntityName, targetEntityId, eventType2, payload2);
        final long stopTime = System.currentTimeMillis() + 1;

        final List<EventEntity> firstList = this.doInROTx(() -> this.eventRepository.fetchEvents(targetEntityName,
                targetEntityId, eventType1, startTime, stopTime, Integer.MAX_VALUE)
                .collect(Collectors.toList()));
        assertEquals(1, firstList.size());

        final EventEntity first = firstList.get(0);
        assertEquals("TestEvent1", first.getEventType());
        assertEquals("value", Map.class.cast(first.getPayload()).get("key"));

        final List<EventEntity> secondList = this.doInROTx(() -> this.eventRepository.fetchEvents(targetEntityName,
                targetEntityId, eventType2, startTime, stopTime, Integer.MAX_VALUE)
                .collect(Collectors.toList()));
        assertEquals(1, secondList.size());

        final EventEntity second = secondList.get(0);
        assertEquals("TestEvent2", second.getEventType());
        assertEquals("hello", Map.class.cast(second.getPayload()).get("body"));
    }
}
