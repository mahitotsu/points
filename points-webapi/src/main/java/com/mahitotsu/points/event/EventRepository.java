package com.mahitotsu.points.event;

import java.util.Map;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.mahitotsu.points.jpa.EntityBase;
import com.mahitotsu.points.jpa.EntityRepository;

import jakarta.persistence.LockModeType;

@Repository
public class EventRepository extends EntityRepository {

    @Transactional(readOnly = true)
    public <E extends Event<?>> E findLastEvent(final EntityBase target, final Class<E> eventType) {

        return this.processSingleEntity("""
                SELECT e FROM Event
                WHERE e.target = :target
                  AND e.name := eventName
                ORDER BY e.tm desc, e.tx desc
                """, eventType,
                Map.ofEntries(
                        Map.entry("target", target),
                        Map.entry("eventName", eventType.getSimpleName()) //
                ),
                LockModeType.NONE,
                (event -> event));
    }
}
