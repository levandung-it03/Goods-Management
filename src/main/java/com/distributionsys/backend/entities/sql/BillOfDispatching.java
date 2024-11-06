package com.distributionsys.backend.entities.sql;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "bill_of_dispatching")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BillOfDispatching {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bill_of_dispatching_id")
    Long billOfDispatchingId;

    @ManyToOne
    @JoinColumn(name = "bill_of_dispatching_id", referencedColumnName = "client_info_id")
    ClientInfo clientInfo;

    @DateTimeFormat
    @Column(name = "created_time", nullable = false, updatable = false,
        columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    LocalDateTime createdTime;
}
