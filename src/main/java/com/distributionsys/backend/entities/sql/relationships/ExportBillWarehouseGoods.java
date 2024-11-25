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
    indexes = @Index(name = "bill_id_cln_index", columnList = "export_bill_id"))
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ExportBillWarehouseGoods {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne
    @JoinColumn(name = "warehouse_goods_id", referencedColumnName = "warehouse_goods_id")
    WarehouseGoods warehouseGoods;

    @ManyToOne
    @JoinColumn(name = "export_bill_id", referencedColumnName = "export_bill_id")
    ExportBill exportBill;

    @Column(name = "goods_quantity", nullable = false)
    Long goodsQuantity;
}
