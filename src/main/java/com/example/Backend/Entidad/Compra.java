package com.example.Backend.Entidad;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "compra")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Compra {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;


    @ManyToOne
    @JoinColumn(name = "curso_id")
    private Curso curso;

    
    private LocalDate fechaCompra;
    @Builder.Default
    private String estado = "completado";
}

