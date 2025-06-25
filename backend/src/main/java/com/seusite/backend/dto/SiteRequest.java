package com.seusite.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class SiteRequest {

    @NotBlank(message = "O nome não pode ficar em branco")
    @Size(max = 100, message = "O nome pode ter no máximo 100 caracteres")
    private String nome;

    @NotBlank(message = "A profissão não pode ficar em branco")
    @Size(max = 100, message = "A profissão pode ter no máximo 100 caracteres")
    private String profissao;

    @NotBlank(message = "A descrição não pode ficar em branco")
    @Size(max = 1000, message = "A descrição pode ter no máximo 1000 caracteres")
    private String descricao;

    @Size(max = 50, message = "Instagram pode ter no máximo 50 caracteres")
    private String instagram;

    @Size(max = 20, message = "WhatsApp pode ter no máximo 20 caracteres")
    private String whatsapp;

    @Email(message = "E-mail inválido")
    @NotBlank(message = "O e-mail é obrigatório")
    private String email;

    @NotBlank(message = "Escolha um template")
    @Pattern(regexp = "minimal|moderno|elegante",
            message = "Template deve ser 'minimal', 'moderno' ou 'elegante'")
    private String template;

    public SiteRequest() { }

    public SiteRequest(String nome, String profissao, String descricao,
                       String instagram, String whatsapp,
                       String email, String template) {
        this.nome = nome;
        this.profissao = profissao;
        this.descricao = descricao;
        this.instagram = instagram;
        this.whatsapp = whatsapp;
        this.email = email;
        this.template = template;
    }

    public String getNome() {
        return nome;
    }
    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getProfissao() {
        return profissao;
    }
    public void setProfissao(String profissao) {
        this.profissao = profissao;
    }

    public String getDescricao() {
        return descricao;
    }
    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getInstagram() {
        return instagram;
    }
    public void setInstagram(String instagram) {
        this.instagram = instagram;
    }

    public String getWhatsapp() {
        return whatsapp;
    }
    public void setWhatsapp(String whatsapp) {
        this.whatsapp = whatsapp;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getTemplate() {
        return template;
    }
    public void setTemplate(String template) {
        this.template = template;
    }
}
