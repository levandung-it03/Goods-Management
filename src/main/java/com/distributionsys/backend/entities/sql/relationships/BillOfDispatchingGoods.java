package com.distributionsys.backend.entities.sql.relationships;

import com.distributionsys.backend.entities.sql.BillOfDispatching;
import com.distributionsys.backend.entities.sql.Goods;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "bill_of_dispatching_goods")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BillOfDispatchingGoods {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne
    @JoinColumn(name = "goods_id", referencedColumnName = "goods_id")
    Goods goods;

    @ManyToOne
    @JoinColumn(name = "bill_of_dispatching_id", referencedColumnName = "bill_of_dispatching_id")
    BillOfDispatching billOfDispatching;

    @Column(name = "goods_quantity", nullable = false)
    Long goodsQuantity;
}
