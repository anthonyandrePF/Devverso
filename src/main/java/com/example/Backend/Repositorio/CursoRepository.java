package com.example.Backend.Repositorio;

import com.example.Backend.Entidad.Curso;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CursoRepository extends JpaRepository<Curso, Long> {
}