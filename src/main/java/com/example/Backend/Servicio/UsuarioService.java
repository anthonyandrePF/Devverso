package com.example.Backend.Servicio;


import com.example.Backend.Entidad.Usuario;
import com.example.Backend.Repositorio.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UsuarioService {

    private final UsuarioRepository userRepository;

    public UsuarioService(UsuarioRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Optional<Usuario> authenticate(String email, String password) {
        return userRepository.findByEmail(email)
                .filter(Usuario -> Usuario.getPassword().equals(password));
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public void save(Usuario user) {
        userRepository.save(user);
    }

    public Optional<Usuario> getByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }
}

