package com.mahitotsu.points.webapi.eventhub.repository;

import static org.junit.Assert.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.mahitotsu.points.webapi.TestBase;

import lombok.Value;

public class EventRepositoryTest extends TestBase {

    @Value
    private class PayloadBean {
        private final String key;
    }

    @Autowired
    private EventRepository eventRepository;

    @Test
    public void testPutAndFetchEvents() {

        final int size = 10;
        final String targetEntityName = "TestEntity";
        final String targetEntityId = "TE001";
        final String eventType = "TestEvent";
        final String payload = "{\"key\":\"value\"}";

        final List<String[]> paramsList = IntStream.range(0, size)
                .mapToObj(i -> new String[] { targetEntityName, targetEntityId, eventType, payload })
                .collect(Collectors.toList());

        final long startTime = System.currentTimeMillis();
        paramsList.forEach(params -> this.eventRepository.putEvent(params[0], params[1], params[2], params[3]));
        final long stopTime = System.currentTimeMillis() + 1;

        final List<EventEntity> eventList = this.doInROTx(() -> this.eventRepository
                .fetchEventHistory(targetEntityName, targetEntityId, eventType, startTime, stopTime, -1)
                .collect(Collectors.toList()));
        assertEquals(size, eventList.size());

        UUID id = null;
        long eventTime = startTime;
        for (final EventEntity event : eventList) {
            assertTrue(id == null || id.compareTo(event.getId()) < 0);
            assertTrue(eventTime - event.getEventTime() <= 0);
            id = event.getId();
            eventTime = event.getEventTime();
        }
        assertTrue(eventTime - stopTime <= 0);
    }

    @Test
    public void testJsonStringPayload() {

        final String targetEntityName = "TestEntity";
        final String targetEntityId = "TE001";
        final String eventType = "TestEvent";
        final String payload = "{\"key\":\"value\"}";

        final long startTime = System.currentTimeMillis();
        this.eventRepository.putEvent(targetEntityName, targetEntityId, eventType, payload);
        final long stopTime = System.currentTimeMillis() + 1;

        final EventEntity event = this.doInROTx(() -> this.eventRepository.fetchEventHistory(targetEntityName,
                targetEntityId, eventType, startTime, stopTime, -1).findFirst().get());

        final Object actual = event.getPayload();
        assertTrue(Map.class.isInstance(actual));
        assertEquals("value", Map.class.cast(actual).get("key"));
    }

    @Test
    public void testMapPayload() {

        final String targetEntityName = "TestEntity";
        final String targetEntityId = "TE001";
        final String eventType = "TestEvent";
        final Map<?, ?> payload = Map.of("key", "value");

        final long startTime = System.currentTimeMillis();
        this.eventRepository.putEvent(targetEntityName, targetEntityId, eventType, payload);
        final long stopTime = System.currentTimeMillis() + 1;

        final EventEntity event = this.doInROTx(() -> this.eventRepository.fetchEventHistory(targetEntityName,
                targetEntityId, eventType, startTime, stopTime, -1).findFirst().get());

        final Object actual = event.getPayload();
        assertTrue(Map.class.isInstance(actual));
        assertEquals("value", Map.class.cast(actual).get("key"));
    }

    @Test
    public void testPOJOPayload() {

        final String targetEntityName = "TestEntity";
        final String targetEntityId = "TE001";
        final String eventType = "TestEvent";
        final PayloadBean payload = new PayloadBean("value");

        final long startTime = System.currentTimeMillis();
        this.eventRepository.putEvent(targetEntityName, targetEntityId, eventType, payload);
        final long stopTime = System.currentTimeMillis() + 1;

        final EventEntity event = this.doInROTx(() -> this.eventRepository.fetchEventHistory(targetEntityName,
                targetEntityId, eventType, startTime, stopTime, -1).findFirst().get());

        final Object actual = event.getPayload();
        assertTrue(Map.class.isInstance(actual));
        assertEquals("value", Map.class.cast(actual).get("key"));
    }
}
