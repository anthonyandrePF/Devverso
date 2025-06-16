package com.example.Backend.Controlador;

import com.example.Backend.Entidad.Curso;
import com.example.Backend.Entidad.Usuario;
import com.example.Backend.Servicio.CursoService;
import com.example.Backend.Servicio.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.util.Optional;

@Controller
@RequestMapping("/admin/cursos")
public class AdminCursoController {
    
    @Autowired
    private CursoService cursoService;
    
    @Autowired
    private UsuarioService usuarioService;

    // Mostrar formulario para agregar un curso
    @GetMapping("/agregar")
    public String mostrarFormularioAgregar(Model model, HttpSession session) {
        String email = (String) session.getAttribute("usuarioActual");
        if (email == null) {
            return "redirect:/?loginRequired=true";
        }
        
        Optional<Usuario> usuarioOpt = usuarioService.getByEmail(email);
        if (usuarioOpt.isEmpty() || !"ROLE_ADMIN".equals(usuarioOpt.get().getRole())) {
            return "redirect:/perfil?accessDenied=true";
        }
        
        model.addAttribute("curso", new Curso());
        return "admin/formulario-curso";
    }

    // Guardar un curso nuevo
    @PostMapping("/guardar")
    public String guardarCurso(@ModelAttribute Curso curso) {
        cursoService.save(curso);
        return "redirect:/perfil-admin";
    }

    // Mostrar formulario para editar un curso
    @GetMapping("/edit/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model) {
        Curso curso = cursoService.findById(id);
        model.addAttribute("curso", curso);
        return "admin/formulario-curso"; // misma vista para agregar y editar
    }

    // Actualizar un curso editado
    @PostMapping("/update")
    public String actualizarCurso(@ModelAttribute Curso curso) {
        cursoService.save(curso);
        return "redirect:/perfil-admin";
    }

    // Eliminar un curso
    @GetMapping("/delete/{id}")
    public String eliminarCurso(@PathVariable Long id) {
        cursoService.deleteById(id);
        return "redirect:/perfil-admin";
    }
}

