package com.mahitotsu.points.webapi.eventhub.repository;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@JsonTypeInfo(use = Id.CLASS, include = As.PROPERTY, property = "class")
@Data
@ToString
@EqualsAndHashCode
public abstract class Event {

}
