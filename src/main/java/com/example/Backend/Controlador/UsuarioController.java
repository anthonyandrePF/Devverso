package com.example.Backend.Controlador;

import com.example.Backend.Entidad.Usuario;
import com.example.Backend.Repositorio.UsuarioRepository;
import com.example.Backend.Servicio.UsuarioService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Controller
public class UsuarioController {

    private final UsuarioService userService;

    @Autowired
    private PasswordEncoder passwordEncoder; // ✅ Agregado para hashear

    public UsuarioController(UsuarioService userService) {
        this.userService = userService;
    }

    private String buildRedirectUrl(String referer, String param) {
        return referer.contains("?") ? referer + "&" + param + "=true" : referer + "?" + param + "=true";
    }

    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }


    @GetMapping("/register")
    public String showRegisterForm() {
        return "register";
    }

    @PostMapping("/register")
    public String register(@RequestParam String nombre,
                           @RequestParam String email,
                           @RequestParam String password,
                           HttpSession session,
                           @RequestHeader("Referer") String referer) {

        if (userService.existsByEmail(email)) {
            return "redirect:" + buildRedirectUrl(referer, "emailExists");
        }

        Usuario newUser = Usuario.builder()
                .nombre(nombre)
                .email(email)
                .password(passwordEncoder.encode(password)) // ✅ Hashea la contraseña aquí
                .build();

        userService.save(newUser);  
        return "redirect:" + buildRedirectUrl(referer, "registerSuccess");
    }

    @GetMapping("/logout")
    public String logoutGet(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

    @RestController
    public static class SesionController {
        @Autowired
        private UsuarioRepository usuarioRepository;

        @GetMapping("/api/usuario/autenticado")
        public ResponseEntity<Map<String, Object>> verificarSesion(HttpSession session) {
            Map<String, Object> response = new HashMap<>();
            Object email = session.getAttribute("usuarioActual");
            if(email != null) {
                response.put("autenticado", true);
                Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(email.toString());
                if(usuarioOpt.isPresent()){
                    Usuario usuario = usuarioOpt.get();
                    response.put("usuario", usuario.getNombre());
                    response.put("email", usuario.getEmail());
                    response.put("rol", usuario.getRole());
                }
            } else {
                response.put("autenticado", false);
            }
            return ResponseEntity.ok(response);
        }
    }

    @PostMapping("/perfil/actualizar")
    @ResponseBody
    public ResponseEntity<?> actualizarPerfil(@RequestBody Usuario usuario, HttpSession session) {
        Object emailObj = session.getAttribute("usuarioActual");
        if (emailObj == null) {
            return ResponseEntity.status(401).body("No autenticado");
        }
        String email = emailObj.toString();
        Optional<Usuario> userOpt = userService.getByEmail(email);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(404).body("Usuario no encontrado");
        }
        Usuario user = userOpt.get();
        user.setNombre(usuario.getNombre());
        user.setEmail(usuario.getEmail());
        // No actualices el rol ni el password aquí por seguridad
        userService.save(user);
        // Actualiza el email en sesión si cambió
        session.setAttribute("usuarioActual", user.getEmail());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/perfil/eliminar")
    @ResponseBody
    public ResponseEntity<?> eliminarCuenta(HttpSession session) {
        Object emailObj = session.getAttribute("usuarioActual");
        if (emailObj == null) {
            return ResponseEntity.status(401).body("No autenticado");
        }
        String email = emailObj.toString();
        Optional<Usuario> userOpt = userService.getByEmail(email);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(404).body("Usuario no encontrado");
        }
        userService.deleteById(userOpt.get().getId());
        session.invalidate(); // Cierra la sesión
        Map<String, String> resp = new HashMap<>();
        resp.put("redirect", "/");
        return ResponseEntity.ok(resp);
    }
}
