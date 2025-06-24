package com.seusite.backend.controller;

import com.seusite.backend.model.SiteModel;
import com.seusite.backend.model.UsuarioModel;
import com.seusite.backend.repository.SiteRepository;
import com.seusite.backend.repository.UsuarioRepository;
import com.seusite.backend.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
public class SiteController {

    @Autowired
    private SiteRepository siteRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private JwtUtil jwtUtil;

    // Geração de site com autenticação
    @PostMapping("/gerar-site")
    public ResponseEntity<?> gerarSite(@RequestHeader("Authorization") String auth,
                                       @RequestBody SiteModel siteData) {
        if (auth == null || !auth.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body("Token ausente ou inválido");
        }

        String token = auth.substring(7);
        String email = jwtUtil.validarToken(token);
        UsuarioModel usuario = usuarioRepository.findByEmail(email).orElseThrow();

        siteData.setUsuario(usuario);

        String slug = siteData.getNome().toLowerCase().replace(" ", "-");
        siteData.setSlug(slug);

        siteRepository.save(siteData);

        String html = """
                <!DOCTYPE html>
                <html lang="pt-br">
                <head>
                  <meta charset="UTF-8">
                  <title>%s</title>
                  <style>
                    body { font-family: sans-serif; text-align: center; padding: 50px; background: #f0f0f0; }
                    h1 { color: #333; }
                    p { max-width: 600px; margin: auto; }
                  </style>
                </head>
                <body>
                  <h1>%s</h1>
                  <p><strong>Profissão:</strong> %s</p>
                  <p><strong>Sobre:</strong> %s</p>
                </body>
                </html>
                """.formatted(siteData.getNome(), siteData.getNome(), siteData.getProfissao(), siteData.getDescricao());

        return ResponseEntity.ok(html);
    }

    // Listar sites do usuário autenticado
    @GetMapping("/meus-sites")
    public ResponseEntity<?> meusSites(@RequestHeader("Authorization") String auth) {
        if (auth == null || !auth.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body("Token ausente");
        }

        String token = auth.substring(7);
        String email = jwtUtil.validarToken(token);
        if (email == null) return ResponseEntity.status(401).body("Token inválido");

        List<SiteModel> lista = siteRepository.findByUsuarioEmail(email);
        return ResponseEntity.ok(lista);
    }

    // Acessar site publicamente pelo slug
    @GetMapping("/{slug}")
    public ResponseEntity<String> exibirSitePorSlug(@PathVariable String slug) {
        SiteModel site = siteRepository.findBySlug(slug);

        if (site == null) {
            return ResponseEntity.notFound().build();
        }

        String html = """
                <!DOCTYPE html>
                <html lang="pt-br">
                <head>
                  <meta charset="UTF-8">
                  <title>%s</title>
                  <style>
                    body { font-family: sans-serif; text-align: center; padding: 50px; background: #f9f9f9; }
                    h1 { color: #222; }
                    p { max-width: 700px; margin: 0 auto; line-height: 1.6; }
                  </style>
                </head>
                <body>
                  <h1>%s</h1>
                  <p><strong>Profissão:</strong> %s</p>
                  <p><strong>Descrição:</strong> %s</p>
                </body>
                </html>
                """.formatted(site.getNome(), site.getNome(), site.getProfissao(), site.getDescricao());

        return ResponseEntity.ok(html);
    }
}




