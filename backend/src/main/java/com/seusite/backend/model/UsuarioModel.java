package com.seusite.backend.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
public class UsuarioModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;
    private String senha;
    private boolean planoAtivo;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL)
    private List<SiteModel> sites;

    // Getters e setters


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<SiteModel> getSites() {
        return sites;
    }

    public void setSites(List<SiteModel> sites) {
        this.sites = sites;
    }

    public boolean isPlanoAtivo() {
        return planoAtivo;
    }

    public void setPlanoAtivo(boolean planoAtivo) {
        this.planoAtivo = planoAtivo;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}






