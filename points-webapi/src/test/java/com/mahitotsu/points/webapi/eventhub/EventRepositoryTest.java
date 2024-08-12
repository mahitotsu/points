package com.mahitotsu.points.webapi.eventhub;

import static org.junit.Assert.*;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.mahitotsu.points.webapi.TestBase;
import com.mahitotsu.points.webapi.domainobj.DomainObject;
import com.mahitotsu.points.webapi.eventhub.EventEntity.Payload;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

public class EventRepositoryTest extends TestBase {

    @ToString(callSuper = true)
    @EqualsAndHashCode(callSuper = true)
    public static class TestObject extends DomainObject {
        public TestObject(final UUID id) {
            super(id);
        }
    }

    @SuperBuilder
    @Getter
    @ToString(callSuper = true)
    @EqualsAndHashCode(callSuper = true)
    @Jacksonized
    public static class StringPayload extends Payload {
        private String text;
    }

    @SuperBuilder
    @Getter
    @ToString(callSuper = true)
    @EqualsAndHashCode(callSuper = true)
    @Jacksonized
    public static class PrimitivePayload extends Payload {
        private int count;
        private boolean active;
    }

    @SuperBuilder
    @Getter
    @ToString(callSuper = true)
    @EqualsAndHashCode(callSuper = true)
    @Jacksonized
    public static class ArrayPayload extends Payload {
        private String[] items;
    }

    @Autowired
    private EventRepository eventRepository;

    @Test
    public void testPutAndFetchEvents() {

        final int size = 10;
        final Class<? extends DomainObject> targetObjectType = TestObject.class;
        final UUID targetObjectId = UUID.randomUUID();

        final long beforeTime = System.currentTimeMillis();
        IntStream.range(0, size)
                .forEach(i -> this.eventRepository.putEvent(targetObjectType, targetObjectId,
                        StringPayload.builder().text(String.format("%02d", i)).build()));
        final long afterTime = System.currentTimeMillis();

        List<EventEntity> eventList;
        UUID id;
        long eventTime;

        // fetch from first to last
        eventList = this.doInROTx(() -> this.eventRepository
                .fetchEvents(targetObjectType, targetObjectId, beforeTime, afterTime + 1,
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
                .fetchEvents(targetObjectType, targetObjectId, afterTime, beforeTime - 1,
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
    public void testFetchAllPayloadTypes() {

        final Class<? extends DomainObject> targetObjectType = TestObject.class;
        final UUID targetObjectId = UUID.randomUUID();
        final PrimitivePayload payload1 = PrimitivePayload.builder().count(1).active(true).build();
        final ArrayPayload payload2 = ArrayPayload.builder().items(new String[] { "A", "b" }).build();

        final long startTime = System.currentTimeMillis();
        this.eventRepository.putEvent(targetObjectType, targetObjectId, payload1);
        this.eventRepository.putEvent(targetObjectType, targetObjectId, payload2);
        final long stopTime = System.currentTimeMillis() + 1;

        final List<EventEntity> eventList = this.doInROTx(() -> this.eventRepository
                .fetchEvents(targetObjectType,
                        targetObjectId, startTime, stopTime, Integer.MAX_VALUE)
                .collect(Collectors.toList()));
        assertEquals(2, eventList.size());

        final EventEntity first = eventList.get(0);
        assertEquals(PrimitivePayload.class, first.getPayloadType());
        assertEquals(payload1, first.getPayload());

        final EventEntity second = eventList.get(1);
        assertEquals(ArrayPayload.class, second.getPayloadType());
        assertEquals(payload2, second.getPayload());
    }

    @Test
    public void testFetchBothEventTypes() {

        final Class<? extends DomainObject> targetObjectType = TestObject.class;
        final UUID targetObjectId = UUID.randomUUID();
        final PrimitivePayload payload1 = PrimitivePayload.builder().count(1).active(true).build();
        final ArrayPayload payload2 = ArrayPayload.builder().items(new String[] { "A", "b" }).build();

        final long startTime = System.currentTimeMillis();
        this.eventRepository.putEvent(targetObjectType, targetObjectId, payload1);
        this.eventRepository.putEvent(targetObjectType, targetObjectId, payload2);
        final long stopTime = System.currentTimeMillis() + 1;

        final List<EventEntity> eventList = this.doInROTx(() -> this.eventRepository
                .fetchEvents(targetObjectType,
                        targetObjectId, Set.of(PrimitivePayload.class, ArrayPayload.class),
                        startTime, stopTime, Integer.MAX_VALUE)
                .collect(Collectors.toList()));
        assertEquals(2, eventList.size());

        final EventEntity first = eventList.get(0);
        assertEquals(PrimitivePayload.class, first.getPayloadType());
        assertEquals(payload1, first.getPayload());

        final EventEntity second = eventList.get(1);
        assertEquals(ArrayPayload.class, second.getPayloadType());
        assertEquals(payload2, second.getPayload());
    }

    @Test
    public void testFetchSpecifiedEventTypeOnly() {

        final Class<? extends DomainObject> targetObjectType = TestObject.class;
        final UUID targetObjectId = UUID.randomUUID();
        final PrimitivePayload payload1 = PrimitivePayload.builder().count(1).active(true).build();
        final ArrayPayload payload2 = ArrayPayload.builder().items(new String[] { "A", "b" }).build();

        final long startTime = System.currentTimeMillis();
        this.eventRepository.putEvent(targetObjectType, targetObjectId, payload1);
        this.eventRepository.putEvent(targetObjectType, targetObjectId, payload2);
        final long stopTime = System.currentTimeMillis() + 1;

        final List<EventEntity> firstList = this.doInROTx(() -> this.eventRepository
                .fetchEvents(targetObjectType,
                        targetObjectId, PrimitivePayload.class, startTime, stopTime,
                        Integer.MAX_VALUE)
                .collect(Collectors.toList()));
        assertEquals(1, firstList.size());

        final EventEntity first = firstList.get(0);
        assertEquals(PrimitivePayload.class, first.getPayloadType());
        assertEquals(payload1, first.getPayload());

        final List<EventEntity> secondList = this.doInROTx(() -> this.eventRepository
                .fetchEvents(targetObjectType,
                        targetObjectId, ArrayPayload.class, startTime, stopTime,
                        Integer.MAX_VALUE)
                .collect(Collectors.toList()));
        assertEquals(1, secondList.size());

        final EventEntity second = secondList.get(0);
        assertEquals(ArrayPayload.class, second.getPayloadType());
        assertEquals(payload2, second.getPayload());
    }

    @Test
    public void testFetchLastEvent() {

        final int size = 10;
        final Class<? extends DomainObject> targetObjectType = TestObject.class;
        final UUID targetObjectId = UUID.randomUUID();

        final List<UUID> eventIdList = IntStream.range(0, size)
                .mapToObj(i -> this.eventRepository.putEvent(targetObjectType, targetObjectId,
                        StringPayload.builder().text(String.format("%02d", i)).build()))
                .collect(Collectors.toList());

        final EventEntity lastEvent = this.doInROTx(() -> this.eventRepository
                .fetchLastEvent(targetObjectType, targetObjectId, StringPayload.class,
                        System.currentTimeMillis())
                .orElseGet(null));
        assertNotNull(lastEvent);
        assertEquals(eventIdList.get(size - 1), lastEvent.getId());
        assertEquals(String.format("%02d", size - 1),
                StringPayload.class.cast(lastEvent.getPayload()).getText());
    }
}