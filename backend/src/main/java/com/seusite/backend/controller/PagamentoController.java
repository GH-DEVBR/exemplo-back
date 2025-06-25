package com.seusite.backend.controller;

import com.seusite.backend.dto.PagamentoRequest;
import com.seusite.backend.model.UsuarioModel;
import com.seusite.backend.repository.UsuarioRepository;
import com.stripe.Stripe;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import jakarta.servlet.http.HttpServletRequest;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins = "*")
public class PagamentoController {

    @Value("${stripe.api.secret.key}")
    private String stripeSecretKey;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @PostMapping("/criar-checkout")
    public String criarCheckout(@RequestBody PagamentoRequest request) throws Exception {
        Stripe.apiKey = stripeSecretKey;

        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.SUBSCRIPTION)
                .setSuccessUrl("http://localhost:5500/sucesso.html")
                .setCancelUrl("http://localhost:5500/cancelado.html")
                .setCustomerEmail(request.getEmail()) // usa o getter
                .addLineItem(
                        SessionCreateParams.LineItem.builder()
                                .setQuantity(1L)
                                .setPrice("price_1RdXDSRoHCCGe8LdBi5T6xkd")
                                .build()
                )
                .build();

        Session session = Session.create(params);
        return session.getUrl();
    }

    @PostMapping("/webhook")
    public ResponseEntity<String> stripeWebhook(HttpServletRequest servletRequest) {
        try {
            String payload = servletRequest.getReader().lines().collect(Collectors.joining());
            JSONObject evento = new JSONObject(payload);

            if ("checkout.session.completed".equals(evento.getString("type"))) {
                JSONObject sess = evento.getJSONObject("data").getJSONObject("object");
                String email = sess.getString("customer_email");

                // Ativar plano no banco
                Optional<UsuarioModel> opt = usuarioRepository.findByEmail(email);
                if (opt.isPresent()) {
                    UsuarioModel usuario = opt.get();
                    usuario.setPlanoAtivo(true);           // usa o nome correto do setter
                    usuarioRepository.save(usuario);
                    System.out.println("✅ Plano ativado para: " + email);
                }
            }
            return ResponseEntity.ok("Webhook recebido com sucesso");
        } catch (Exception e) {
            System.out.println("❌ Erro ao processar webhook: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erro no webhook");
        }
    }
}





