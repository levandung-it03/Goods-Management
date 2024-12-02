package com.distributionsys.backend.entities.sql;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Collection;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(
    name = "goods",
    uniqueConstraints = {@UniqueConstraint(columnNames = {"goods_id", "supplier_id"})},
    indexes = @Index(name = "name_cln_index", columnList = "goods_name"))
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Goods {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "goods_id")
    Long goodsId;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "warehouse_goods",
        joinColumns = @JoinColumn(name = "goods_id", referencedColumnName = "goods_id"),
        inverseJoinColumns = @JoinColumn(name = "warehouse_id", referencedColumnName = "warehouse_id")
    )
    Collection<Warehouse> warehouses;

    @ManyToOne
    @JoinColumn(name = "supplier_id", nullable = false, referencedColumnName = "supplier_id")
    Supplier supplier;

    @Column(name = "goods_name", nullable = false, length = 100)
    String goodsName;

    @Column(name = "unit_price", nullable = false)
    Float unitPrice;
}
