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
@Table(name = "bill_of_receiving")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BillOfReceiving {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bill_of_receiving_id")
    Long billOfReceivingId;

    @ManyToOne
    @JoinColumn(name = "client_info_id", referencedColumnName = "client_info_id")
    ClientInfo clientInfo;

    @Column(name = "receiver_name", nullable = false, length = 100)
    String receiverName;

    @DateTimeFormat
    @Column(name = "created_time", nullable = false, updatable = false,
        columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    LocalDateTime createdTime;
}
