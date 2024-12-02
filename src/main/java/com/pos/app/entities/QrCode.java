package com.pos.app.entities;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.type.format.jakartajson.JakartaJsonIntegration;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "qr_code")
public class QrCode {

    @Id
    private String id;

    @Column(name = "code")
    private String code;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne
    @JoinColumn(name = "client_id")
    private Client client;

    @Column(name = "created_date")
    private Long createdDate;

    @Column(name = "updated_date")
    private Long updatedDate;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "updated_by")
    private String updatedBy;

}
