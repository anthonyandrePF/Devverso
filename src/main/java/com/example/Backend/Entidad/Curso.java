package com.example.Backend.Entidad;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.Date;


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

    @Column(name = "fecha")
    private Date fecha;

    private String duracion;
    private String nivel;
    private BigDecimal precio;
    private String imagen;

    @Column(name = "categoria_id")
    private Long categoriaId;

    @Column(name = "profesor_nombre")
    private String profesorNombre;

    @Column(columnDefinition = "json")
    private String temario;

    @Column(columnDefinition = "json")
    private String aprendizajes;

    @Column(columnDefinition = "json")
    private String publico;
}

