package com.distributionsys.backend.entities.sql.relationships;

import com.distributionsys.backend.entities.sql.ImportBill;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "import_bill_warehouse_goods",
    uniqueConstraints = {@UniqueConstraint(columnNames = {"warehouse_goods_id", "import_bill_id"})},
    indexes = @Index(name = "bill_id_cln_index", columnList = "import_bill_id"))
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ImportBillWarehouseGoods {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne
    @JoinColumn(name = "warehouse_goods_id", nullable = false, referencedColumnName = "warehouse_goods_id")
    WarehouseGoods warehouseGoods;

    @ManyToOne
    @JoinColumn(name = "import_bill_id", nullable = false, referencedColumnName = "import_bill_id")
    ImportBill importBill;

    @Column(name = "goods_quantity", nullable = false)
    Long goodsQuantity;
}
