package com.pos.app.entities;

import jakarta.persistence.*;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "merchant")
public class Merchant extends BaseEntity {

    @Column(name = "name")
    private String name;
    @Column(name = "address")
    private String address;
    @Column(name = "note")
    private String note;

    @JoinColumn(name = "client_id")
    @ManyToOne
    private Client client;




}
