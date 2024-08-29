package com.mahitotsu.points.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.ColumnDefault;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@MappedSuperclass
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter(AccessLevel.PUBLIC)
@Setter(AccessLevel.NONE)
@ToString
@EqualsAndHashCode
public abstract class EntityBase {

    @Id
    @GeneratedValue
    @Column(name = "id", insertable = false, updatable = false)
    @ColumnDefault("gen_random_uuid()")
    private UUID id;

    @Column(name = "ts", insertable = false, updatable = false)
    @ColumnDefault("statement_timestamp()")
    private LocalDateTime ts;

    @Column(name = "tx", insertable = false, updatable = false)
    @ColumnDefault("txid_current()")
    private Long tx;

    @Column(name = "sq", insertable = false, updatable = false)
    @ColumnDefault("nextval_tempseq()")
    private Long sq;

    @Column(name = "nm", insertable = false, updatable = false)
    private String nm;
}
