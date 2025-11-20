package com.vitor.entomodata.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.vitor.entomodata.model.Exemplar;
import com.vitor.entomodata.repository.ExemplarRepository;

@Configuration
public class DadosIniciaisConfig {

    @Bean
    public CommandLineRunner carregarDados(ExemplarRepository repository) {
        return args -> {
            // Verifica se o banco já tem dados (para não duplicar se você mudar para banco real)
            if (repository.count() > 0) {
                return;
            }

            System.out.println("🌱 Semeando banco de dados com 20 exemplares de teste...");

            for (int i = 1; i <= 20; i++) {
                Exemplar e = new Exemplar();
                
                // Gera um código formatado A001, A002...
                String codigo = String.format("A%03d", i);
                e.setCod(codigo);
                
                // Alterna dados para parecer realista
                if (i % 3 == 0) {
                    e.setEspecie("Apis mellifera");
                    e.setFamilia("Apidae");
                    e.setGenero("Apis");
                    e.setLocalidade("Jardim Botânico");
                    e.setEstado("SP");
                    e.setBioma("Mata Atlântica");
                    e.setColetor("João Silva");
                } else if (i % 3 == 1) {
                    e.setEspecie("Bombus morio");
                    e.setFamilia("Apidae");
                    e.setGenero("Bombus");
                    e.setLocalidade("Parque Nacional");
                    e.setEstado("MG");
                    e.setBioma("Cerrado");
                    e.setColetor("Maria Oliveira");
                } else {
                    e.setEspecie("Melipona quadrifasciata");
                    e.setFamilia("Apidae");
                    e.setGenero("Melipona");
                    e.setLocalidade("Reserva Florestal");
                    e.setEstado("PR");
                    e.setBioma("Araucária");
                    e.setColetor("Pedro Santos");
                }
                
                e.setSexo(i % 2 == 0 ? "F" : "M");
                e.setData("2023-10-" + (i < 10 ? "0" + i : i));
                e.setPais("Brasil");
                
                // Preenche alguns campos extras variados
                e.setGaveta("G01");
                e.setCaixa("C" + (i % 5 + 1));
                
                repository.save(e);
            }
            
            System.out.println("✅ Carga inicial concluída!");
        };
    }
}