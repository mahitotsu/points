package com.mahitotsu.points.persistence.account;

import java.beans.ConstructorProperties;

import com.mahitotsu.points.persistence.eventstore.EventEntity;

import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.Value;

@Entity(name = "AccountStatusChangeEvent")
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class AccountStatusChangeEventEntity extends EventEntity<AccountStatusChangeEventEntity.Payload> {

    public static enum Status {
        OPEND, CLOSED,
    }

    @Value
    @ToString
    @EqualsAndHashCode
    public static class Payload {

        @ConstructorProperties({ "status" })
        public Payload(final Status status) {
            this.status = status;
        }

        @NotNull
        private Status status;
    }
}
