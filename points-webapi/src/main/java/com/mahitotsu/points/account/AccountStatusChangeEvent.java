package com.mahitotsu.points.account;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.mahitotsu.points.account.AccountStatusChangeEvent.Payload;
import com.mahitotsu.points.event.Event;

import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Value;

@Entity
public class AccountStatusChangeEvent extends Event<Payload> {

    public static enum Status {
        OPENED, CLOSED,
    }

    @Value
    @AllArgsConstructor(onConstructor = @__(@JsonCreator))
    public static class Payload {
        private Status status;
    }

    protected AccountStatusChangeEvent() {
        super();
    }

    public AccountStatusChangeEvent(final Account account, final Status status) {
        super(account, new Payload(status));
    }
}
