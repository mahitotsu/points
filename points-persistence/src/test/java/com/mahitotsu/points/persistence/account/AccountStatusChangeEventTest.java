package com.mahitotsu.points.persistence.account;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.mahitotsu.points.persistence.TestBase;
import com.mahitotsu.points.persistence.account.AccountStatusChangeEventEntity.Payload;
import com.mahitotsu.points.persistence.account.AccountStatusChangeEventEntity.Status;
import com.mahitotsu.points.persistence.eventstore.EventEntity;
import com.mahitotsu.points.persistence.eventstore.EventRepository;
import com.mahitotsu.points.persistence.utils.UUIDUtils;

import jakarta.validation.ConstraintViolationException;

public class AccountStatusChangeEventTest extends TestBase {

    @Autowired
    private EventRepository eventRepository;

    @Test
    public void testPutAndGetEvent() {

        final Payload payload = new AccountStatusChangeEventEntity.Payload(Status.OPEND);
        final String targetType = "TestType";
        final UUID targetId = UUID.randomUUID();

        final AccountStatusChangeEventEntity event = new AccountStatusChangeEventEntity();
        event.init(payload, targetType, targetId);
        assertEquals(this.eventRepository.getEventType(event.getClass()), event.getEventType());
        assertNull(event.getEventId());

        final long eventTime = this.eventRepository.putEvent(event);
        assertNotNull(event.getEventId());
        assertEquals(this.eventRepository.extractEpochMillis(event), eventTime);

        final EventEntity<?> stored = this.eventRepository.findEventById(event.getEventId()).orElse(null);
        assertNotNull(stored);
        assertInstanceOf(AccountStatusChangeEventEntity.class, stored);
        assertEquals(event, stored);
    }

    @Test
    public void testTryToStoreNotInitializedEvent() {

        final AccountStatusChangeEventEntity event = new AccountStatusChangeEventEntity();
        assertThrows(ConstraintViolationException.class,
                () -> this.eventRepository.putEvent(event));
    }

    @Test
    public void testTryToStoreInvalidPayload() {

        final Payload payload = new AccountStatusChangeEventEntity.Payload(null);
        final String targetType = "TestType";
        final UUID targetId = UUID.randomUUID();

        final AccountStatusChangeEventEntity event = new AccountStatusChangeEventEntity();
        event.init(payload, targetType, targetId);
        assertThrows(ConstraintViolationException.class,
                () -> this.eventRepository.putEvent(event));
    }

    @Test
    public void testFindLastEvent() {

        final String targetType = "TestType";
        final UUID targetId = UUID.randomUUID();
        final Payload open = new AccountStatusChangeEventEntity.Payload(Status.OPEND);
        final Payload close = new AccountStatusChangeEventEntity.Payload(Status.CLOSED);

        final AccountStatusChangeEventEntity first = new AccountStatusChangeEventEntity();
        first.init(open, targetType, targetId);
        this.eventRepository.putEvent(first);

        final AccountStatusChangeEventEntity second = new AccountStatusChangeEventEntity();
        second.init(close, targetType, targetId);
        this.eventRepository.putEvent(second);

        final EventEntity<?> last = this.eventRepository
                .findLastEvent(this.eventRepository.getEventType(AccountStatusChangeEventEntity.class),
                        targetType, targetId)
                .orElse(null);
        assertNotNull(last);
        assertEquals(second, last);
    }

    @Test
    public void testStreamEventAscByTime() {

        final String targetType = "TestType";
        final UUID targetId = UUID.randomUUID();
        final Payload open = new AccountStatusChangeEventEntity.Payload(Status.OPEND);
        final Payload close = new AccountStatusChangeEventEntity.Payload(Status.CLOSED);

        final AccountStatusChangeEventEntity first = new AccountStatusChangeEventEntity();
        first.init(open, targetType, targetId);
        final long firstTime = this.eventRepository.putEvent(first);

        final AccountStatusChangeEventEntity second = new AccountStatusChangeEventEntity();
        second.init(close, targetType, targetId);
        final long secondTime = this.eventRepository.putEvent(second);

        final String eventType = this.eventRepository.getEventType(AccountStatusChangeEventEntity.class);
        final List<EventEntity<?>> list = this.doWithTransaction((tx) -> this.eventRepository
                .streamEvents(eventType, targetType, targetId, firstTime, secondTime + 1).collect(Collectors.toList()));
        assertEquals(2, list.size());
        assertEquals(first, list.get(0));
        assertEquals(second, list.get(1));
    }

    @Test
    public void testStreamEventDescByTime() {

        final String targetType = "TestType";
        final UUID targetId = UUID.randomUUID();
        final Payload open = new AccountStatusChangeEventEntity.Payload(Status.OPEND);
        final Payload close = new AccountStatusChangeEventEntity.Payload(Status.CLOSED);

        final AccountStatusChangeEventEntity first = new AccountStatusChangeEventEntity();
        first.init(open, targetType, targetId);
        final long firstTime = this.eventRepository.putEvent(first);

        final AccountStatusChangeEventEntity second = new AccountStatusChangeEventEntity();
        second.init(close, targetType, targetId);
        final long secondTime = this.eventRepository.putEvent(second);

        final String eventType = this.eventRepository.getEventType(AccountStatusChangeEventEntity.class);
        final List<EventEntity<?>> list = this.doWithTransaction((tx) -> this.eventRepository
                .streamEvents(eventType, targetType, targetId, secondTime, firstTime - 1).collect(Collectors.toList()));
        assertEquals(2, list.size());
        assertEquals(first, list.get(1));
        assertEquals(second, list.get(0));
    }

    @Test
    public void testStreamEventAscById() {

        final String targetType = "TestType";
        final UUID targetId = UUID.randomUUID();
        final Payload open = new AccountStatusChangeEventEntity.Payload(Status.OPEND);
        final Payload close = new AccountStatusChangeEventEntity.Payload(Status.CLOSED);

        final AccountStatusChangeEventEntity first = new AccountStatusChangeEventEntity();
        first.init(open, targetType, targetId);
        this.eventRepository.putEvent(first);

        final AccountStatusChangeEventEntity second = new AccountStatusChangeEventEntity();
        second.init(close, targetType, targetId);
        this.eventRepository.putEvent(second);

        final String eventType = this.eventRepository.getEventType(AccountStatusChangeEventEntity.class);
        final List<EventEntity<?>> list = this.doWithTransaction((tx) -> this.eventRepository
                .streamEvents(eventType, targetType, targetId, first.getEventId(), UUIDUtils.next(second.getEventId()))
                .collect(Collectors.toList()));
        assertEquals(2, list.size());
        assertEquals(first, list.get(0));
        assertEquals(second, list.get(1));
    }

    @Test
    public void testStreamEventDescById() {

        final String targetType = "TestType";
        final UUID targetId = UUID.randomUUID();
        final Payload open = new AccountStatusChangeEventEntity.Payload(Status.OPEND);
        final Payload close = new AccountStatusChangeEventEntity.Payload(Status.CLOSED);

        final AccountStatusChangeEventEntity first = new AccountStatusChangeEventEntity();
        first.init(open, targetType, targetId);
        this.eventRepository.putEvent(first);

        final AccountStatusChangeEventEntity second = new AccountStatusChangeEventEntity();
        second.init(close, targetType, targetId);
        this.eventRepository.putEvent(second);

        final String eventType = this.eventRepository.getEventType(AccountStatusChangeEventEntity.class);
        final List<EventEntity<?>> list = this.doWithTransaction((tx) -> this.eventRepository
                .streamEvents(eventType, targetType, targetId, second.getEventId(),
                        UUIDUtils.previous(first.getEventId()))
                .collect(Collectors.toList()));
        assertEquals(2, list.size());
        assertEquals(first, list.get(1));
        assertEquals(second, list.get(0));
    }
}
