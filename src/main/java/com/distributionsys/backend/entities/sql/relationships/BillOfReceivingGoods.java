package com.distributionsys.backend.entities.sql.relationships;

import com.distributionsys.backend.entities.sql.BillOfReceiving;
import com.distributionsys.backend.entities.sql.Goods;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "bill_of_receiving_goods")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BillOfReceivingGoods {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne
    @JoinColumn(name = "goods_id", referencedColumnName = "goods_id")
    Goods goods;

    @ManyToOne
    @JoinColumn(name = "bill_of_receiving_id", referencedColumnName = "bill_of_receiving_id")
    BillOfReceiving billOfReceiving;

    @Column(name = "goods_quantity", nullable = false)
    Long goodsQuantity;
}
