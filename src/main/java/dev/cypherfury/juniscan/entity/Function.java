package dev.cypherfury.juniscan.entity;

import jakarta.persistence.*;
import lombok.*;

@Data
@Table
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Function {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "module_id", nullable = false)
    private Module module;

    @Column(nullable = false)
    private int callIndex;

    @Column(nullable = false)
    private String name;

    private String description;

}
