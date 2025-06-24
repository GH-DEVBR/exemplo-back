package com.seusite.backend.controller;

import com.seusite.backend.dto.PagamentoRequest;
import com.stripe.Stripe;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import jakarta.servlet.http.HttpServletRequest;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins = "*")
public class PagamentoController {

    @Value("${stripe.api.secret.key}")
    private String stripeSecretKey;

    @PostMapping("/criar-checkout")
    public String criarCheckout(@RequestBody PagamentoRequest request) throws Exception {
        Stripe.apiKey = stripeSecretKey;

        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.SUBSCRIPTION)
                .setSuccessUrl("http://localhost:5500/sucesso.html")
                .setCancelUrl("http://localhost:5500/cancelado.html")
                .setCustomerEmail(request.email)
                .addLineItem(
                        SessionCreateParams.LineItem.builder()
                                .setQuantity(1L)
                                .setPrice("price_1RdXDSRoHCCGe8LdBi5T6xkd") // seu price_id
                                .build()
                )
                .build();

        Session session = Session.create(params);
        return session.getUrl(); // Redirecionar usuário
    }

    @PostMapping("/webhook")
    public ResponseEntity<String> stripeWebhook(HttpServletRequest request) {
        try {
            String payload = request.getReader().lines().collect(Collectors.joining());
            JSONObject evento = new JSONObject(payload);

            if ("checkout.session.completed".equals(evento.getString("type"))) {
                JSONObject session = evento.getJSONObject("data").getJSONObject("object");
                String email = session.getString("customer_email");

                // Aqui você pode ativar o plano do usuário no banco
                System.out.println("✅ Assinatura ativada para: " + email);
                // TODO: buscar o usuário pelo e-mail e setar planoAtivo = true
            }

            return ResponseEntity.ok("Webhook recebido com sucesso");
        } catch (Exception e) {
            System.out.println("❌ Erro ao processar webhook: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erro no webhook");
        }
    }
}


