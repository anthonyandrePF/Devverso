package com.example.Backend.Controlador;

import com.example.Backend.Servicio.AdminService;
import com.example.Backend.Entidad.Usuario;
import com.example.Backend.Repositorio.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import jakarta.servlet.http.HttpSession;
import java.util.Optional;

@Controller
public class AdminController {
    
    @Autowired
    private AdminService adminService;
    
    @Autowired
    private UsuarioRepository usuarioRepository; // Inyectar repositorio de usuario

    @GetMapping("/perfil-admin")
    public String mostrarPerfilAdmin(Model model, HttpSession session) {
        // Inyectar objeto usuario si existe en sesión
        String email = (String) session.getAttribute("usuarioActual");
        if(email != null) {
            Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(email);
            if(usuarioOpt.isPresent()) {
                model.addAttribute("usuario", usuarioOpt.get());
            }
        }
        model.addAttribute("cursos", adminService.obtenerCursos());
        var ventas = adminService.obtenerVentas();
        model.addAttribute("ventas", ventas);

        // Calcular el total de ventas
        double totalVentas = ventas.stream()
            .mapToDouble(venta -> venta.getCurso().getPrecio().doubleValue())
            .sum();
        model.addAttribute("totalVentas", totalVentas);

        return "perfil-admin";
    }
}
