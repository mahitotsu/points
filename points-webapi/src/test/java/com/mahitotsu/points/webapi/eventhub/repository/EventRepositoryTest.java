package com.mahitotsu.points.webapi.eventhub.repository;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.mahitotsu.points.webapi.TestBase;

public class EventRepositoryTest extends TestBase {

    @Autowired
    private EventRepository eventRepository;

    @Test
    public void putAndFetchEvents() {

        final int size = 10;
        final String targetEntityName = "TestEntity";
        final String eventType = "TestEvent";

        final List<String[]> paramsList = IntStream.range(0, size)
                .mapToObj(i -> new String[] { targetEntityName, String.format("TE%02d", i), eventType })
                .collect(Collectors.toList());
        paramsList.forEach(params -> this.eventRepository.putEvent(params[0], params[1], params[2]));
    }
}
