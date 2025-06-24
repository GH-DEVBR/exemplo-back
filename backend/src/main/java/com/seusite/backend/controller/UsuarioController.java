package com.seusite.backend.controller;

import com.seusite.backend.model.UsuarioModel;
import com.seusite.backend.repository.UsuarioRepository;
import com.seusite.backend.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@CrossOrigin(origins = "*")
public class UsuarioController {

    private final UsuarioRepository usuarioRepository;
    private final JwtUtil jwtUtil;

    @Autowired
    public UsuarioController(UsuarioRepository usuarioRepository, JwtUtil jwtUtil) {
        this.usuarioRepository = usuarioRepository;
        this.jwtUtil = jwtUtil;
    }

    // Cadastro de usuário
    @PostMapping("/cadastrar")
    public ResponseEntity<?> cadastrar(@RequestBody UsuarioModel usuario) {
        if (usuarioRepository.findByEmail(usuario.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("E-mail já cadastrado!");
        }

        usuario.setPlanoAtivo(false); // Começa sem plano
        usuarioRepository.save(usuario);
        return ResponseEntity.ok("Usuário cadastrado com sucesso!");
    }

    // Login com retorno de token JWT
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UsuarioModel usuario) {
        Optional<UsuarioModel> user = usuarioRepository.findByEmail(usuario.getEmail());

        if (user.isEmpty() || !user.get().getSenha().equals(usuario.getSenha())) {
            return ResponseEntity.status(401).body("Credenciais inválidas");
        }

        String token = jwtUtil.gerarToken(usuario.getEmail());
        return ResponseEntity.ok(token);
    }

    // Retorna dados do usuário logado com base no token
    @GetMapping("/usuario-logado")
    public ResponseEntity<?> getUsuario(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body("Token ausente");
        }

        String token = authHeader.substring(7);
        String email = jwtUtil.validarToken(token);

        if (email == null) {
            return ResponseEntity.status(401).body("Token inválido");
        }

        Optional<UsuarioModel> usuario = usuarioRepository.findByEmail(email);
        return usuario.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
}
