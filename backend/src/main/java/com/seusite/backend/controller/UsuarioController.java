package com.seusite.backend.controller;

import com.seusite.backend.model.UsuarioModel;
import com.seusite.backend.repository.UsuarioRepository;
import com.seusite.backend.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

    // Cadastro de usuário (pré-pagamento)
    @PostMapping("/cadastrar")
    public ResponseEntity<?> cadastrar(@RequestBody UsuarioModel usuario) {
        if (usuarioRepository.findByEmail(usuario.getEmail()).isPresent()) {
            return ResponseEntity
                    .badRequest()
                    .body("E-mail já cadastrado!");
        }
        // salva com plano inativo; só ativa via webhook após pagamento
        usuario.setPlanoAtivo(false);
        usuarioRepository.save(usuario);
        return ResponseEntity
                .ok("Usuário cadastrado com sucesso! Agora faça o pagamento para ativar o plano.");
    }

    // Login com retorno de token JWT; só quem tem plano ativo consegue entrar
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UsuarioModel usuario) {
        Optional<UsuarioModel> userOpt = usuarioRepository.findByEmail(usuario.getEmail());
        if (userOpt.isEmpty() || !userOpt.get().getSenha().equals(usuario.getSenha())) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("Credenciais inválidas");
        }

        UsuarioModel user = userOpt.get();
        if (!user.isPlanoAtivo()) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body("Plano não ativo. Faça o pagamento para ativar sua conta.");
        }

        String token = jwtUtil.gerarToken(user.getEmail());
        return ResponseEntity.ok(token);
    }

    // Retorna dados do usuário logado com base no token
    @GetMapping("/usuario-logado")
    public ResponseEntity<?> getUsuario(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("Token ausente ou mal formatado");
        }

        String token = authHeader.substring(7);
        String email = jwtUtil.validarToken(token);
        if (email == null) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("Token inválido");
        }

        return usuarioRepository
                .findByEmail(email)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}

