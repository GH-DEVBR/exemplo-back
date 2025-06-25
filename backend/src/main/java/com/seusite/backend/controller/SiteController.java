package com.seusite.backend.controller;

import com.seusite.backend.dto.SiteRequest;
import com.seusite.backend.model.SiteModel;
import com.seusite.backend.model.UsuarioModel;
import com.seusite.backend.repository.SiteRepository;
import com.seusite.backend.repository.UsuarioRepository;
import com.seusite.backend.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@Controller
@CrossOrigin(origins = "*")
public class SiteController {

    private final SiteRepository siteRepository;
    private final UsuarioRepository usuarioRepository;
    private final JwtUtil jwtUtil;

    @Autowired
    public SiteController(SiteRepository siteRepository,
                          UsuarioRepository usuarioRepository,
                          JwtUtil jwtUtil) {
        this.siteRepository = siteRepository;
        this.usuarioRepository = usuarioRepository;
        this.jwtUtil = jwtUtil;
    }

    /**
     * Gera um slug único, salva o SiteModel e retorna a URL pública em JSON:
     * { "url": "http://.../site/{slug}" }
     */
    @PostMapping("/gerar-site")
    @ResponseBody
    public ResponseEntity<Map<String,String>> gerarSite(
            @RequestHeader("Authorization") String auth,
            @RequestBody SiteRequest req) {

        if (auth == null || !auth.startsWith("Bearer ")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token ausente ou inválido");
        }
        String email = jwtUtil.validarToken(auth.substring(7));
        if (email == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token inválido");
        }

        UsuarioModel usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuário não encontrado"));

        // Monta o SiteModel
        SiteModel site = new SiteModel();
        site.setNome(req.getNome());
        site.setProfissao(req.getProfissao());
        site.setDescricao(req.getDescricao());
        site.setInstagram(req.getInstagram());
        site.setWhatsapp(req.getWhatsapp());
        site.setTemplate(req.getTemplate()); // "minimal", "moderno" ou "elegante"
        site.setUsuario(usuario);

        // Gera slug amigável + sufixo aleatório
        String base = req.getNome().toLowerCase().replaceAll("[^a-z0-9]+", "-");
        String slug = base + "-" + UUID.randomUUID().toString().substring(0, 6);
        site.setSlug(slug);

        siteRepository.save(site);

        String url = "http://localhost:8080/site/" + slug;
        return ResponseEntity.ok(Collections.singletonMap("url", url));
    }

    /**
     * Retorna a lista de todos os SiteModel do usuário autenticado.
     */
    @GetMapping("/meus-sites")
    @ResponseBody
    public ResponseEntity<List<SiteModel>> meusSites(
            @RequestHeader("Authorization") String auth) {

        if (auth == null || !auth.startsWith("Bearer ")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token ausente");
        }
        String email = jwtUtil.validarToken(auth.substring(7));
        if (email == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token inválido");
        }

        List<SiteModel> lista = siteRepository.findByUsuarioEmail(email);
        return ResponseEntity.ok(lista);
    }

    /**
     * Renderiza o site público com o template escolhido (minimal, moderno ou elegante).
     * Os arquivos devem estar em src/main/resources/templates/{minimal.html, moderno.html, elegante.html}
     */
    @GetMapping("/site/{slug}")
    public String exibirSite(
            @PathVariable String slug,
            Model model) {

        SiteModel site = siteRepository.findBySlug(slug)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Site não encontrado"));

        // Todos os atributos disponíveis no template:
        model.addAttribute("nome",       site.getNome());
        model.addAttribute("profissao",  site.getProfissao());
        model.addAttribute("descricao",  site.getDescricao());
        model.addAttribute("instagram",  site.getInstagram());
        model.addAttribute("whatsapp",   site.getWhatsapp());

        // Escolhe o template Thymeleaf conforme a opção salva
        // (por exemplo, minimal.html, moderno.html ou elegante.html)
        return site.getTemplate();
    }
}






