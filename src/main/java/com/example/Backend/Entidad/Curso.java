package com.example.Backend.Entidad;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "curso")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Curso {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String descripcion;


    private String fecha;

    private String duracion;
    private String nivel;
    private Double precio;
    private String imagen;


    @ManyToOne
    @JoinColumn(name = "categoria_id")
    private Categoria categoria;


    private String profesor_nombre;


    @Column(columnDefinition = "JSON")
    private String temario; 

    @Column(columnDefinition = "JSON")
    private String aprendizajes;  

    @Column(columnDefinition = "JSON")
    private String publico;  
}

