package com.example.Backend.Controlador;

import com.example.Backend.Entidad.Curso;
import com.example.Backend.Entidad.Usuario;
import com.example.Backend.Entidad.Compra;
import com.example.Backend.Servicio.CursoService;
import com.example.Backend.Repositorio.CompraRepository;
import com.example.Backend.Repositorio.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

// import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import jakarta.servlet.http.HttpSession;

@Controller
public class WebController {

    @Autowired
    private CursoService cursoService;

    @Autowired
    private CompraRepository compraRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping("/")
    public String mostrarIndex(Model model) {
        List<Curso> cursos = cursoService.findAll();  
        model.addAttribute("cursos", cursos);
        return "index"; 
    }

    @GetMapping("/cursos")
    public String mostrarCursos(Model model) {
        List<Curso> cursos = cursoService.findAll(); 
        model.addAttribute("cursos", cursos);
        return "cursos";
    }

    @GetMapping("/cursos/detalle/{id}")
    public String detalleCurso(@PathVariable Long id, Model model, HttpSession session) {
        Curso curso = cursoService.findById(id);
        if (curso == null) {
            return "error/404"; 
        }

        try {
            ObjectMapper mapper = new ObjectMapper();

            List<String> aprendizajes = mapper.readValue(
                    curso.getAprendizajes() != null ? curso.getAprendizajes() : "[]",
                    new TypeReference<List<String>>() {}
            );

            List<Map<String, Object>> temario = mapper.readValue(
                    curso.getTemario() != null ? curso.getTemario() : "[]",
                    new TypeReference<List<Map<String, Object>>>() {}
            );

            List<String> publico = mapper.readValue(
                    curso.getPublico() != null ? curso.getPublico() : "[]",
                    new TypeReference<List<String>>() {}
            );

            model.addAttribute("curso", curso);
            model.addAttribute("aprendizajes", aprendizajes);
            model.addAttribute("temario", temario);
            model.addAttribute("publico", publico);

            // --- Lógica para saber si el usuario ha comprado el curso ---
            boolean haComprado = false;
            String email = (String) session.getAttribute("usuarioActual");
            if (email != null) {
                Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(email);
                if (usuarioOpt.isPresent()) {
                    Usuario usuario = usuarioOpt.get();
                    haComprado = compraRepository.existsByUsuarioAndCurso(usuario, curso);
                }
            }
            model.addAttribute("haComprado", haComprado);
            // -----------------------------------------------------------

        } catch (Exception e) {
            e.printStackTrace();

            model.addAttribute("aprendizajes", List.of());
            model.addAttribute("temario", List.of());
            model.addAttribute("publico", List.of());
            model.addAttribute("haComprado", false); // Por si ocurre error
        }

        return "detalle-curso";
    }

    @GetMapping("/nosotros")
    public String mostrarNosotros() {

        return "nosotros";
    }

    @GetMapping("/pago")
    public String mostrarPago() {
        return "pago";
    }

    @GetMapping("/perfil")
    public String mostrarPerfil(Model model, HttpSession session) {
        String email = (String) session.getAttribute("usuarioActual");
        System.out.println("Email en sesión: " + email); // <-- Depuración
        if (email == null) {
            return "redirect:/?loginRequired=true";
        }
        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(email);
        if (usuarioOpt.isEmpty()) {
            return "redirect:/";
        }
        Usuario usuario = usuarioOpt.get();
        List<Compra> compras = compraRepository.findByUsuario(usuario);

        model.addAttribute("usuario", usuario);
        model.addAttribute("compras", compras);
        return "perfil";
    }

    @GetMapping("/curso/{cursoId}/modulo/{moduloIndex}")
    public String verModulo(
            @PathVariable Long cursoId,
            @PathVariable int moduloIndex,
            Model model
    ) {
        Curso curso = cursoService.findById(cursoId);
        if (curso == null) {
            return "error/404";
        }

        try {
            ObjectMapper mapper = new ObjectMapper();
            List<Map<String, Object>> temario = mapper.readValue(
                    curso.getTemario() != null ? curso.getTemario() : "[]",
                    new TypeReference<List<Map<String, Object>>>() {}
            );

            if (moduloIndex < 0 || moduloIndex >= temario.size()) {
                return "error/404";
            }

            Map<String, Object> modulo = temario.get(moduloIndex);

            model.addAttribute("curso", curso);
            model.addAttribute("modulo", modulo);
            model.addAttribute("moduloIndex", moduloIndex);

        } catch (Exception e) {
            e.printStackTrace();
            return "error/500";
        }

        return "modulo-detalle";
    }

    @PostMapping("/logout")
    public String logoutPost(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

    @GetMapping("/mis-cursos")
    public String mostrarMisCursos(Model model, HttpSession session) {
        String email = (String) session.getAttribute("usuarioActual");
        if (email == null) {
             return "redirect:/";
        }
        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(email);
        if (usuarioOpt.isEmpty()) {
             return "redirect:/";
        }
        Usuario usuario = usuarioOpt.get();
        List<Compra> compras = compraRepository.findByUsuario(usuario);
        model.addAttribute("usuario", usuario);
        model.addAttribute("compras", compras);
        return "mis-cursos";
    }

    public String mostrarPerfilAdmin(Model model, HttpSession session) {
        String email = (String) session.getAttribute("usuarioActual");
        if (email == null) {
            return "redirect:/?loginRequired=true";
        }
        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(email);
        if (usuarioOpt.isEmpty()) {
            return "redirect:/";
        }
        Usuario usuario = usuarioOpt.get();
        if (!"ROLE_ADMIN".equals(usuario.getRole())) {
            return "redirect:/perfil?accessDenied=true";
        }
        // ...posibles datos adicionales para el administrador...
        model.addAttribute("usuario", usuario);
        return "perfil-admin"; // Se espera que exista la vista perfil-admin.html
    }
}
