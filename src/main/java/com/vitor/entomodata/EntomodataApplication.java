package com.vitor.entomodata;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class EntomodataApplication {

	public static void main(String[] args) {
		SpringApplication.run(EntomodataApplication.class, args);
	}

	// Isso roda ao iniciar o programa
    @Bean
    public CommandLineRunner demo(ExemplarRepository repository) {
        return (args) -> {
            // Criando um exemplar de teste
            Exemplar e1 = new Exemplar();
            e1.setCod("A-0001");
            e1.setEspecie("Apis mellifera");
            e1.setLocalidade("Jardim Botânico");
            e1.setColetor("Vitor");
            e1.setData("2023-11-18");
            
            // Salvando no banco
            repository.save(e1);
            
            System.out.println("Dados de teste carregados!");
        };
    }
}
