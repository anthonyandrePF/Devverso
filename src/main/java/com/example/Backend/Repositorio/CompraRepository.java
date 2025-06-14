package com.example.Backend.Repositorio;

import com.example.Backend.Entidad.Compra;
import com.example.Backend.Entidad.Curso;
import com.example.Backend.Entidad.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CompraRepository extends JpaRepository<Compra, Long> {
    List<Compra> findByUsuario(Usuario usuario);
    boolean existsByUsuarioAndCurso(Usuario usuario, Curso curso);
}
