package com.mahitotsu.points.webapi.domainobj;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.UUID;
import java.util.function.Consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.interceptor.DefaultTransactionAttribute;
import org.springframework.transaction.support.TransactionTemplate;

import com.mahitotsu.points.webapi.eventhub.EventEntity;
import com.mahitotsu.points.webapi.eventhub.EventRepository;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode
public abstract class DomainObject {

    public DomainObject(final UUID id) {
        this.id = id;
    }

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private PlatformTransactionManager transactionManager;

    private final UUID id;

    private final Map<Class<? extends EventEntity.Payload>, Long> lastEventTimes = new HashMap<>();

    protected void putEvent(final EventEntity.Payload payload) {
        this.eventRepository.putEvent(this.getClass(), this.id, payload);
    }

    protected void doInROTx(final Consumer<TransactionStatus> callback) {

        final DefaultTransactionAttribute txDef = new DefaultTransactionAttribute();
        txDef.setReadOnly(true);

        new TransactionTemplate(this.transactionManager, txDef).executeWithoutResult(callback);
    }

    protected <P extends EventEntity.Payload> void processLastEvent(final Class<P> payloadType, final long toTime,
            final Consumer<? super P> processor) {
        if (processor == null) {
            return;
        }

        this.doInROTx(tx -> {
            final long now = System.currentTimeMillis();
            final EventEntity lastEvent = this.eventRepository
                    .fetchLastEvent(this.getClass(), this.id, payloadType, toTime).orElse(null);
            if (lastEvent == null) {
                return;
            }

            processor.accept(payloadType.cast(lastEvent.getPayload()));
            this.lastEventTimes.put(payloadType, now);
        });
    }

    protected void processEventStream(final Set<Class<? extends EventEntity.Payload>> payloadTypes, final long toTime,
            final Consumer<? super EventEntity.Payload> processor) {

        if (processor == null) {
            return;
        }

        this.doInROTx(tx -> {
            final SortedMap<Long, Set<Class<? extends EventEntity.Payload>>> tasks = new TreeMap<>();
            payloadTypes.forEach(pt -> {
                final Long currentTime = this.lastEventTimes.getOrDefault(pt, Long.valueOf(0));
                tasks.computeIfAbsent(currentTime, key -> new HashSet<Class<? extends EventEntity.Payload>>()).add(pt);
            });

            final long now = System.currentTimeMillis();
            long startTime = -1;
            long stopTime = -1;
            final Set<Class<? extends EventEntity.Payload>> types = new HashSet<>();
            for (Iterator<Map.Entry<Long, Set<Class<? extends EventEntity.Payload>>>> i = tasks.sequencedEntrySet()
                    .iterator(); i.hasNext() && startTime < toTime;) {
                final Map.Entry<Long, Set<Class<? extends EventEntity.Payload>>> entry = i.next();
                if (startTime < 0) {
                    startTime = entry.getKey();
                    types.addAll(entry.getValue());
                } else {
                    stopTime = entry.getKey();
                    this.eventRepository
                            .fetchEvents(this.getClass(), this.id, types, startTime, Math.max(stopTime, toTime),
                                    Integer.MAX_VALUE)
                            .forEach(e -> processor.accept(e.getPayload()));
                    startTime = stopTime;
                    types.addAll(entry.getValue());
                }
                if (i.hasNext() == false && startTime < toTime) {
                    this.eventRepository
                            .fetchEvents(this.getClass(), this.id, types, startTime, toTime, Integer.MAX_VALUE)
                            .forEach(e -> processor.accept(e.getPayload()));
                }
            }
            payloadTypes.forEach(pt -> this.lastEventTimes.put(pt, now));
        });
    }
}
