package com.mahitotsu.points.persistence.account;

import java.beans.ConstructorProperties;

import org.hibernate.annotations.Type;

import com.mahitotsu.points.persistence.account.AccountDepositEventEntity.Payload;
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

@Entity(name = "AccountDepositEvent")
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class AccountDepositEventEntity extends EventEntity<Payload> {

    public static Payload paylod(final int amount) {
        return new Payload(amount);
    }

    @Value
    @ToString
    @EqualsAndHashCode
    public static class Payload {

        @ConstructorProperties({ "amount" })
        public Payload(final int amount) {
            this.amount = amount;
        }

        int amount;
    }

    @Type(JsonType.class)
    @Column(name = "EVENT_PAYLOAD", updatable = false)
    @NotNull
    @Valid
    @Getter(AccessLevel.PUBLIC)
    @Setter(AccessLevel.PROTECTED)
    private Payload eventPayload;
}
