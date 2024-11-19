package com.distributionsys.backend.entities.sql.relationships;

import com.distributionsys.backend.entities.sql.Goods;
import com.distributionsys.backend.entities.sql.Warehouse;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "warehouse_goods")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class WarehouseGoods {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "warehouse_goods_id")
    Long id;

    @ManyToOne
    @JoinColumn(name = "goods_id", referencedColumnName = "goods_id")
    Goods goods;

    @ManyToOne
    @JoinColumn(name = "warehouse_id", referencedColumnName = "warehouse_id")
    Warehouse warehouse;

    @Column(name = "current_quantity", nullable = false)
    Long currentQuantity;

    @Version
    Long version;
}
