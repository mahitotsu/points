package com.mahitotsu.points.event;

import com.mahitotsu.points.jpa.EntityBase;

import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class Event extends EntityBase {

}
