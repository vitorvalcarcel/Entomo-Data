package com.vitor.entomodata.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.concurrent.atomic.AtomicLong;

@Controller
public class SystemController {

    @Autowired
    private ApplicationContext context;

    // Inicializa com o tempo atual
    private final AtomicLong lastHeartbeat = new AtomicLong(System.currentTimeMillis());
    
    // Tempo de tolerância (Use 60000 para produção, 10000 só para teste rápido)
    private static final long TIMEOUT_MS = 15000; 

    @GetMapping("/sistema/sair")
    public String confirmarSaida() {
        return "sistema-sair";
    }

    @PostMapping("/sistema/desligar")
    public String desligarSistema() {
        executarDesligamento(1000); 
        return "sistema-desligado";
    }

    // --- HEARTBEAT ---

    @PostMapping("/sistema/alive")
    @ResponseBody
    public void receberSinalDeVida() {
        // DEBUG: Comente esta linha em produção
        System.out.println("❤️ [BACKEND] Tum-tum! Sinal recebido às " + java.time.LocalTime.now());
        
        lastHeartbeat.set(System.currentTimeMillis());
    }

    // Roda a cada 5 segundos, espera 20s para começar
    @Scheduled(fixedRate = 5000, initialDelay = 20000)
    public void verificarInatividade() {
        long agora = System.currentTimeMillis();
        long ultimoSinal = lastHeartbeat.get();
        long silencio = agora - ultimoSinal;

        System.out.println("⏱️ [CHECK] Tempo de silêncio: " + (silencio/1000) + "s (Limite: " + (TIMEOUT_MS/1000) + "s)");

        if (silencio > TIMEOUT_MS) {
            System.out.println("⚠️ [SHUTDOWN] Inatividade crítica detectada. Encerrando...");
            executarDesligamento(0);
        }
    }

    private void executarDesligamento(long delay) {
        new Thread(() -> {
            try {
                if (delay > 0) Thread.sleep(delay);
                SpringApplication.exit(context, () -> 0);
                System.exit(0);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
    }
}