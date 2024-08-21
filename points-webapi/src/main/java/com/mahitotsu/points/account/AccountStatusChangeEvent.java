package com.mahitotsu.points.account;

import java.beans.ConstructorProperties;
import java.util.UUID;

import com.mahitotsu.points.account.AccountStatusChangeEvent.Payload;
import com.mahitotsu.points.eventstore.Event;

import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.Value;

@Entity
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class AccountStatusChangeEvent extends Event<Payload> {

    public static enum Status {
        OPENED, CLOSED,
    }

    @Value
    @EqualsAndHashCode
    @ToString
    @AllArgsConstructor(onConstructor = @__(@ConstructorProperties({ "status" })))
    public static class Payload {
        private Status status;
    }

    protected AccountStatusChangeEvent() {
        super();
    }

    public AccountStatusChangeEvent(final Status status, final UUID targetId) {
        super(new Payload(status), targetId, "Account");
    }
}
