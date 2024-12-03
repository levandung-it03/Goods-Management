package com.distributionsys.backend.entities.sql.relationships;

import com.distributionsys.backend.entities.sql.ExportBill;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "export_bill_warehouse_goods",
    uniqueConstraints = {@UniqueConstraint(columnNames = {"warehouse_goods_id", "export_bill_id"})},
    indexes = @Index(name = "bill_id_cln_index", columnList = "export_bill_id"))
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ExportBillWarehouseGoods {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "export_bill_warehouse_goods_id")
    Long exportBillWarehouseGoodsId;

    @ManyToOne
    @JoinColumn(name = "warehouse_goods_id", nullable = false, referencedColumnName = "warehouse_goods_id")
    WarehouseGoods warehouseGoods;

    @ManyToOne
    @JoinColumn(name = "export_bill_id", nullable = false, referencedColumnName = "export_bill_id")
    ExportBill exportBill;

    @Column(name = "goods_quantity", nullable = false)
    Long goodsQuantity;
}
