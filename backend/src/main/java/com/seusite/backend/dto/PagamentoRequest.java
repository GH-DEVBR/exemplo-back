package com.seusite.backend.dto;

public class PagamentoRequest {
    private String email;

    public PagamentoRequest() {}
    public PagamentoRequest(String email) { this.email = email; }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
}

