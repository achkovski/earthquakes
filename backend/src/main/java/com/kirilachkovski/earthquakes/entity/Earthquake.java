package com.kirilachkovski.earthquakes.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "earthquakes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Earthquake {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "external_id", nullable = false, unique = true, length = 100)
    private String externalId;

    @Column(nullable = false, precision = 4, scale = 2)
    private BigDecimal magnitude;

    @Column(name = "mag_type", length = 20)
    private String magType;

    @Column(length = 255)
    private String place;

    @Column(length = 255)
    private String title;

    @Column(name = "event_time", nullable = false)
    private Instant eventTime;

    @Column(precision = 9, scale = 4)
    private BigDecimal longitude;

    @Column(precision = 9, scale = 4)
    private BigDecimal latitude;

    @Column(precision = 7, scale = 2)
    private BigDecimal depth;
}
