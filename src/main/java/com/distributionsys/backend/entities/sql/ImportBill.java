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
@Table(name = "import_bill", indexes = @Index(name = "created_time_index", columnList = "createdTime"))
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ImportBill {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "import_bill_id")
    Long importBillId;

    @ManyToOne
    @JoinColumn(name = "client_info_id", referencedColumnName = "client_info_id")
    ClientInfo clientInfo;

    @DateTimeFormat
    @Column(name = "created_time", nullable = false, updatable = false,
        columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    LocalDateTime createdTime;
}
