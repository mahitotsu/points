package com.mahitotsu.points.webapi.eventhub.repository;

import java.util.UUID;
import java.util.stream.Stream;

public interface EventRepository {

    Stream<EventEntity> fetchEvents(String targetEntityName, String targetEntityId,
            String eventType, long fromTime, long toTime, int maxResult);

    UUID putEvent(String targetEntityName, String targetEntityId, String eventType,
            Object paylaod);

}