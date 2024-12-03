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
@Table(
    name = "warehouse_goods",
    uniqueConstraints = @UniqueConstraint(columnNames = {"goods_id", "warehouse_id"}),
    indexes = @Index(name = "goods_id_cln_index", columnList = "goods_id")
)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class WarehouseGoods {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "warehouse_goods_id")
    Long warehouseGoodsId;

    @ManyToOne
    @JoinColumn(name = "goods_id", nullable = false, referencedColumnName = "goods_id")
    Goods goods;

    @ManyToOne
    @JoinColumn(name = "warehouse_id", nullable = false, referencedColumnName = "warehouse_id")
    Warehouse warehouse;

    @Column(name = "current_quantity", nullable = false)
    Long currentQuantity;

    @Version
    Long version;
}
