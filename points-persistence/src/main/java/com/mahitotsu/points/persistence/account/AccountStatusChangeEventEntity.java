package com.mahitotsu.points.persistence.account;

import java.beans.ConstructorProperties;

import org.hibernate.annotations.Type;

import com.mahitotsu.points.persistence.account.AccountStatusChangeEventEntity.Payload;
import com.mahitotsu.points.persistence.eventstore.EventEntity;

import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.Value;

@Entity(name = "AccountStatusChangeEvent")
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class AccountStatusChangeEventEntity extends EventEntity<Payload> {

    public static Payload payload(final Status status) {
        return new Payload(status);
    }

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

    @Type(JsonType.class)
    @Column(name = "EVENT_PAYLOAD", updatable = false)
    @NotNull
    @Valid
    @Getter(AccessLevel.PUBLIC)
    @Setter(AccessLevel.PROTECTED)
    private Payload eventPayload;
}
