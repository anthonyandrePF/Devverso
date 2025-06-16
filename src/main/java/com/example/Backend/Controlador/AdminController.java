package com.example.Backend.Controlador;

import com.example.Backend.Servicio.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminController {
    
    @Autowired
    private AdminService adminService;
    
    @GetMapping("/perfil-admin")
    public String mostrarPerfilAdmin(Model model) {
        model.addAttribute("cursos", adminService.obtenerCursos());
        model.addAttribute("ventas", adminService.obtenerVentas());
        return "perfil-admin";
    }
}
