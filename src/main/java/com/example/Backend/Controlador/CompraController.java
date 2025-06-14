package com.example.Backend.Controlador;

import com.example.Backend.Entidad.Compra;
import com.example.Backend.Entidad.Curso;
import com.example.Backend.Entidad.Usuario;
import com.example.Backend.Repositorio.CompraRepository;
import com.example.Backend.Repositorio.CursoRepository;
import com.example.Backend.Repositorio.UsuarioRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Optional;

@RestController
@RequestMapping("/api/compra")
public class CompraController {

    @Autowired
    private CompraRepository compraRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private CursoRepository cursoRepository;

    @PostMapping("/realizar")
    public String realizarCompra(@RequestParam Long cursoId, HttpSession session) {
        String email = (String) session.getAttribute("usuarioActual");
        if (email == null) {
            return "Usuario no autenticado";
        }
        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(email);
        Optional<Curso> cursoOpt = cursoRepository.findById(cursoId);

        if (usuarioOpt.isPresent() && cursoOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            Curso curso = cursoOpt.get();

            // Verificar si ya existe una compra para este usuario y curso
            boolean yaComprado = compraRepository.existsByUsuarioAndCurso(usuario, curso);
            if (yaComprado) {
                return "Ya has comprado este curso";
            }

            Compra compra = Compra.builder()
                    .usuario(usuario)
                    .curso(curso)
                    .fechaCompra(LocalDate.now())
                    .estado("completado")
                    .build();
            compraRepository.save(compra);
            return "Compra registrada";
        } else {
            return "Usuario o curso no encontrado";
        }
    }
}

