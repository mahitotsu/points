package com.mahitotsu.points.webapi.eventhub.repository;

import java.util.UUID;
import java.util.stream.Stream;

public interface EventRepository {

    Stream<EventEntity> fetchEventHistory(String targetEntityName, String targetEntityId,
            String eventType, long startTime, long stopTime, int maxResult);

    Stream<EventEntity> fetchFirstEvents(String targetEntityName, String targetEventId, String eventType,
            long startTime, int maxResult);

    Stream<EventEntity> fetchLastEvents(String targetEntityName, String targetEventId, String eventType,
            long stopTime, int maxResult);

    UUID putEvent(String targetEntityName, String targetEntityId, String eventType,
            Object paylaod);

}