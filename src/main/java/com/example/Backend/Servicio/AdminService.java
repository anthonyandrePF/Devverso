package com.example.Backend.Servicio;

import com.example.Backend.Entidad.Curso;
import com.example.Backend.Entidad.Compra;
import com.example.Backend.Repositorio.CompraRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class AdminService {

    @Autowired
    private CursoService cursoService;

    @Autowired
    private CompraRepository compraRepository;

    public List<Curso> obtenerCursos() {
        return cursoService.findAll();
    }

    public List<Compra> obtenerVentas() {
        return compraRepository.findAllByOrderByFechaCompraDesc();
    }
}
