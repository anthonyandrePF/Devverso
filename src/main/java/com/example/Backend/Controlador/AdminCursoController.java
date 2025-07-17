package com.example.Backend.Controlador;

import com.example.Backend.Entidad.Curso;
import com.example.Backend.Entidad.Usuario;
import com.example.Backend.Entidad.Categoria;
import com.example.Backend.Servicio.CursoService;
import com.example.Backend.Servicio.UsuarioService;
import com.example.Backend.Repositorio.CategoriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.util.StringUtils;

import jakarta.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.util.Optional;

@Controller
@RequestMapping("/admin/cursos")
public class AdminCursoController {
    
    @Autowired
    private CursoService cursoService;
    
    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private CategoriaRepository categoriaRepository;

    // Mostrar formulario para agregar un curso
    @GetMapping("/agregar")
public String mostrarFormularioAgregar(Model model, HttpSession session) {
    String email = (String) session.getAttribute("usuarioActual");
    if (email == null) return "redirect:/?loginRequired=true";

    Optional<Usuario> usuarioOpt = usuarioService.getByEmail(email);
    if (usuarioOpt.isEmpty() || !"ROLE_ADMIN".equals(usuarioOpt.get().getRole())) {
        return "redirect:/perfil?accessDenied=true";
    }

    Curso nuevoCurso = new Curso();
    nuevoCurso.setTemario("[]");
    nuevoCurso.setAprendizajes("[]");
    nuevoCurso.setPublico("[]");

    model.addAttribute("curso", nuevoCurso);
    model.addAttribute("categorias", categoriaRepository.findAll()); // Agregar categorías al modelo
    return "admin/formulario-curso";
}

    // Guardar un curso nuevo
    @PostMapping("/guardar")
public String guardarCurso(@ModelAttribute Curso curso,
                           @RequestParam(value = "imagenFile", required = false) MultipartFile imagenFile) {

    // Inicializar campos tipo JSON si vienen vacíos
    if (curso.getTemario() == null || curso.getTemario().isBlank()) curso.setTemario("[]");
    if (curso.getAprendizajes() == null || curso.getAprendizajes().isBlank()) curso.setAprendizajes("[]");
    if (curso.getPublico() == null || curso.getPublico().isBlank()) curso.setPublico("[]");

    // Guardar imagen si se envió
    if (imagenFile != null && !imagenFile.isEmpty()) {
        String nombreArchivo = StringUtils.cleanPath(imagenFile.getOriginalFilename());
        String rutaDestino = "C:/Devverso/static/img/" + nombreArchivo; // Ajusta según tu entorno
        try {
            File archivoDestino = new File(rutaDestino);
            File directorio = archivoDestino.getParentFile();
            if (!directorio.exists()) {
                directorio.mkdirs();
            }
            imagenFile.transferTo(archivoDestino);
            curso.setImagen("/img/" + nombreArchivo);
        } catch (IOException e) {
            System.out.println("Error al guardar imagen: " + e.getMessage());
        }
    }

    Optional<Categoria> categoriaOpt = categoriaRepository.findById(curso.getCategoria().getId());
    if (categoriaOpt.isPresent()) {
        curso.setCategoria(categoriaOpt.get());
    } else {
        System.out.println("Categoría no encontrada con ID: " + curso.getCategoria().getId());
        return "redirect:/perfil-admin";
    }
    cursoService.save(curso);
    return "redirect:/perfil-admin";
}


    // Mostrar formulario para editar un curso
    @GetMapping("/edit/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model, HttpSession session) {
        String email = (String) session.getAttribute("usuarioActual");
        if (email == null) return "redirect:/?loginRequired=true";

        Optional<Usuario> usuarioOpt = usuarioService.getByEmail(email);
        if (usuarioOpt.isEmpty() || !"ROLE_ADMIN".equals(usuarioOpt.get().getRole())) {
            return "redirect:/perfil?accessDenied=true";
        }

        Curso curso = cursoService.findById(id);
        if (curso == null) {
            return "redirect:/perfil-admin";
        }

        model.addAttribute("curso", curso);
        model.addAttribute("categorias", categoriaRepository.findAll());
        return "admin/formulario-curso";
    }

    // Actualizar un curso editado
    @PostMapping("/update")
    public String actualizarCurso(@ModelAttribute Curso curso, @RequestParam(value = "imagenFile", required = false) MultipartFile imagenFile) {
        Curso cursoExistente = cursoService.findById(curso.getId());
        if (cursoExistente == null) {
            System.out.println("Curso no encontrado con ID: " + curso.getId());
            return "redirect:/perfil-admin";
        }

        // Mantener la imagen existente si no se sube una nueva
        String imagenAnterior = cursoExistente.getImagen();
        curso.setImagen(imagenAnterior);

        if (imagenFile != null && !imagenFile.isEmpty()) {
            String nombreArchivo = StringUtils.cleanPath(imagenFile.getOriginalFilename());
            // Usar una ruta absoluta para guardar las imágenes
            String rutaDestino = "C:/Devverso/static/img/" + nombreArchivo;

            try {
                File archivoDestino = new File(rutaDestino);

                // Crear el directorio si no existe
                File directorio = archivoDestino.getParentFile();
                if (!directorio.exists()) {
                    directorio.mkdirs();
                }

                imagenFile.transferTo(archivoDestino);

                if (archivoDestino.exists()) {
                    // Actualizar la ruta en el objeto curso
                    curso.setImagen("/img/" + nombreArchivo);
                    System.out.println("Nueva imagen guardada: " + curso.getImagen());
                } else {
                    System.out.println("Error: No se pudo guardar la imagen");
                    curso.setImagen(imagenAnterior); // Reestablecer la imagen anterior
                }
            } catch (IOException e) {
                System.out.println("Error al guardar la imagen: " + e.getMessage());
                curso.setImagen(imagenAnterior); // Reestablecer la imagen anterior
            }
        }

        System.out.println("Curso antes de guardar: " + curso);
        cursoService.save(curso);
        System.out.println("Curso actualizado: " + curso);
        return "redirect:/perfil-admin";
    }

    // Eliminar un curso
    @GetMapping("/delete/{id}")
    public String eliminarCurso(@PathVariable Long id) {
        cursoService.deleteById(id);
        return "redirect:/perfil-admin";
    }

    // Mostrar detalles del curso
    @GetMapping("/detalle/{id}")
public String mostrarDetalleCurso(@PathVariable Long id, Model model) {
    Curso curso = cursoService.findById(id);
    if (curso == null) {
        return "redirect:/cursos";
    }

    // Obtener el nombre de la categoría
    Optional<Categoria> categoriaOpt = categoriaRepository.findById(curso.getCategoria().getId());
    String nombreCategoria = categoriaOpt.map(Categoria::getNombre).orElse("Categoría no encontrada");

    model.addAttribute("curso", curso);
    model.addAttribute("nombreCategoria", nombreCategoria);
    return "detalle-curso";
}

    
}


