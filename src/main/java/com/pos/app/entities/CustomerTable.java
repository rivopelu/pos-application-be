package com.pos.app.entities;


import jakarta.persistence.*;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "customer_table")
public class CustomerTable extends BaseEntity {

    @Column(name = "table_number")
    private Integer tableNumber;

    @Column(name = "customer_name")
    private String customerName;

    @JoinColumn(name = "merchant_id")
    @ManyToOne
    private Merchant merchant;

    @JoinColumn(name = "client_id")
    @ManyToOne
    private Client client;

}
