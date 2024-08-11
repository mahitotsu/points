package com.mahitotsu.points.webapi.eventhub.repository.jpa;

import static org.junit.Assert.*;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.mahitotsu.points.webapi.TestBase;
import com.mahitotsu.points.webapi.eventhub.repository.Event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

public class EventJpaRepositoryTest extends TestBase {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString(callSuper = true)
    @EqualsAndHashCode(callSuper = true)
    public static class TestEvent extends Event {
        private String payload;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString(callSuper = true)
    @EqualsAndHashCode(callSuper = true)
    public static class TestEvent1 extends Event {
        private int count;
        private boolean active;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString(callSuper = true)
    @EqualsAndHashCode(callSuper = true)
    public static class TestEvent2 extends Event {
        private String[] items;
    }

    @Autowired
    private EventJpaRepository eventRepository;

    @Test
    public void testPutAndFetchEvents() {

        final int size = 10;
        final String targetEntityName = "TestEntity";
        final UUID targetEntityId = UUID.randomUUID();

        final long beforeTime = System.currentTimeMillis();
        IntStream.range(0, size)
                .forEach(i -> this.eventRepository.putEvent(targetEntityName, targetEntityId,
                        new TestEvent(String.format("%02d", i))));
        final long afterTime = System.currentTimeMillis();

        List<EventEntity> eventList;
        UUID id;
        long eventTime;

        // fetch from first to last
        eventList = this.doInROTx(() -> this.eventRepository
                .fetchEvents(targetEntityName, targetEntityId, beforeTime, afterTime + 1,
                        Integer.MAX_VALUE)
                .collect(Collectors.toList()));
        assertEquals(size, eventList.size());

        id = null;
        eventTime = beforeTime;
        for (final EventEntity entity : eventList) {
            assertTrue(id == null || id.compareTo(entity.getId()) < 0);
            assertTrue(eventTime - entity.getEventTime() <= 0);
            id = entity.getId();
            eventTime = entity.getEventTime();
        }
        assertTrue(eventTime - afterTime <= 0);

        // fetch from last to first
        eventList = this.doInROTx(() -> this.eventRepository
                .fetchEvents(targetEntityName, targetEntityId, afterTime, beforeTime - 1,
                        Integer.MAX_VALUE)
                .collect(Collectors.toList()));
        assertEquals(size, eventList.size());

        id = null;
        eventTime = afterTime;
        for (final EventEntity entity : eventList) {
            assertTrue(id == null || id.compareTo(entity.getId()) > 0);
            assertTrue(eventTime - entity.getEventTime() >= 0);
            id = entity.getId();
            eventTime = entity.getEventTime();
        }
        assertTrue(eventTime - beforeTime >= 0);
    }

    @Test
    public void testFetchAllEventTypes() {

        final String targetEntityName = "TestEntity";
        final UUID targetEntityId = UUID.randomUUID();
        final TestEvent1 event1 = new TestEvent1(1, true);
        final TestEvent2 event2 = new TestEvent2(new String[] { "A", "b" });

        final long startTime = System.currentTimeMillis();
        this.eventRepository.putEvent(targetEntityName, targetEntityId, event1);
        this.eventRepository.putEvent(targetEntityName, targetEntityId, event2);
        final long stopTime = System.currentTimeMillis() + 1;

        final List<EventEntity> eventList = this.doInROTx(() -> this.eventRepository.fetchEvents(targetEntityName,
                targetEntityId, startTime, stopTime, Integer.MAX_VALUE).collect(Collectors.toList()));
        assertEquals(2, eventList.size());

        final EventEntity first = eventList.get(0);
        assertEquals(TestEvent1.class, first.getEventType());
        assertEquals(event1, first.getEvent());

        final EventEntity second = eventList.get(1);
        assertEquals(TestEvent2.class, second.getEventType());
        assertEquals(event2, second.getEvent());
    }

    @Test
    public void testFetchBothEventTypes() {

        final String targetEntityName = "TestEntity";
        final UUID targetEntityId = UUID.randomUUID();
        final TestEvent1 event1 = new TestEvent1(1, true);
        final TestEvent2 event2 = new TestEvent2(new String[] { "A", "b" });

        final long startTime = System.currentTimeMillis();
        this.eventRepository.putEvent(targetEntityName, targetEntityId, event1);
        this.eventRepository.putEvent(targetEntityName, targetEntityId, event2);
        final long stopTime = System.currentTimeMillis() + 1;

        final List<EventEntity> eventList = this.doInROTx(() -> this.eventRepository.fetchEvents(targetEntityName,
                targetEntityId, Set.of(TestEvent1.class, TestEvent2.class), startTime, stopTime, Integer.MAX_VALUE)
                .collect(Collectors.toList()));
        assertEquals(2, eventList.size());

        final EventEntity first = eventList.get(0);
        assertEquals(TestEvent1.class, first.getEventType());
        assertEquals(event1, first.getEvent());

        final EventEntity second = eventList.get(1);
        assertEquals(TestEvent2.class, second.getEventType());
        assertEquals(event2, second.getEvent());
    }

    @Test
    public void testFetchSpecifiedEventTypeOnly() {

        final String targetEntityName = "TestEntity";
        final UUID targetEntityId = UUID.randomUUID();
        final TestEvent1 event1 = new TestEvent1(1, true);
        final TestEvent2 event2 = new TestEvent2(new String[] { "A", "b" });

        final long startTime = System.currentTimeMillis();
        this.eventRepository.putEvent(targetEntityName, targetEntityId, event1);
        this.eventRepository.putEvent(targetEntityName, targetEntityId, event2);
        final long stopTime = System.currentTimeMillis() + 1;

        final List<EventEntity> firstList = this.doInROTx(() -> this.eventRepository.fetchEvents(targetEntityName,
                targetEntityId, TestEvent1.class, startTime, stopTime, Integer.MAX_VALUE)
                .collect(Collectors.toList()));
        assertEquals(1, firstList.size());

        final EventEntity first = firstList.get(0);
        assertEquals(TestEvent1.class, first.getEventType());
        assertEquals(event1, first.getEvent());

        final List<EventEntity> secondList = this.doInROTx(() -> this.eventRepository.fetchEvents(targetEntityName,
                targetEntityId, TestEvent2.class, startTime, stopTime, Integer.MAX_VALUE)
                .collect(Collectors.toList()));
        assertEquals(1, secondList.size());

        final EventEntity second = secondList.get(0);
        assertEquals(TestEvent2.class, second.getEventType());
        assertEquals(event2, second.getEvent());
    }

    @Test
    public void testFetchLastEvent() {

        final int size = 10;
        final String targetEntityName = "TestEntity";
        final UUID targetEntityId = UUID.randomUUID();

        final List<UUID> eventIdList = IntStream.range(0, size)
                .mapToObj(i -> this.eventRepository.putEvent(targetEntityName, targetEntityId,
                        new TestEvent(String.format("%02d", i))))
                .collect(Collectors.toList());

        final EventEntity lastEvent = this.doInROTx(() -> this.eventRepository
                .fetchLastEvent(targetEntityName, targetEntityId, TestEvent.class, System.currentTimeMillis())
                .orElseGet(null));
        assertNotNull(lastEvent);
        assertEquals(eventIdList.get(size - 1), lastEvent.getId());
        assertEquals(String.format("%02d", size - 1), TestEvent.class.cast(lastEvent.getEvent()).getPayload());
    }
}