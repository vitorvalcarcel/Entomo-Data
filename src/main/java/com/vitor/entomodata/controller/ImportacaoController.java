package com.vitor.entomodata.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import com.vitor.entomodata.service.ImportacaoService;
import com.vitor.entomodata.exception.DuplicidadeException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Controller
public class ImportacaoController {

    @Autowired
    private ImportacaoService service;

    @GetMapping("/importar")
    public String telaUpload() {
        return "importar-upload";
    }

    @PostMapping("/importar/upload")
    public String processarUpload(@RequestParam("arquivoExcel") MultipartFile arquivo, Model model) {
        try {
            String nomeArquivo = arquivo.getOriginalFilename();
            Path caminhoTemporario = Paths.get(System.getProperty("java.io.tmpdir"), nomeArquivo);
            Files.copy(arquivo.getInputStream(), caminhoTemporario, StandardCopyOption.REPLACE_EXISTING);

            List<String> colunasDoExcel = service.lerCabecalhos(arquivo);
            
            List<String> camposDoSistema = Arrays.asList(
                "cod", 
                "familia", "subfamilia", "tribo", "subtribo", 
                "genero", "subgenero", "especie", "subespecie", 
                "autor", "determinador", "sexo", "especieVegetalAssociada",
                "gaveta", "caixa",
                "pais", "estado", "cidade", "localidade", "proprietarioDoLocalDeColeta",
                "bioma", "latitude", "longitude",
                "coletor", "metodoDeAquisicao", "data", "horarioColeta"
            );

            model.addAttribute("colunasExcel", colunasDoExcel);
            model.addAttribute("camposSistema", camposDoSistema);
            model.addAttribute("nomeArquivoSalvo", nomeArquivo);
            
            return "importar-mapa"; 
            
        } catch (IOException e) {
            model.addAttribute("erro", "Erro ao processar arquivo: " + e.getMessage());
            return "importar-upload";
        }
    }

    @PostMapping("/importar/finalizar")
    public String finalizarImportacao(@RequestParam Map<String, String> todosOsParametros, Model model) {
        String nomeArquivo = todosOsParametros.get("nomeArquivo");
        todosOsParametros.remove("nomeArquivo");
        
        try {
            // Tenta processar COM validação de duplicidade (true)
            service.processarImportacao(nomeArquivo, todosOsParametros, true);
            return "redirect:/?sucesso=true";
            
        } catch (DuplicidadeException e) {
            // Se achou duplicatas, vai para a tela de decisão
            model.addAttribute("duplicatas", e.getDuplicatas());
            model.addAttribute("nomeArquivoSalvo", nomeArquivo);
            model.addAttribute("mapaAnterior", todosOsParametros); // Passa o mapa para não perder
            return "importar-conflito";
            
        } catch (IOException e) {
            return "redirect:/importar?erro=" + e.getMessage();
        }
    }

    @PostMapping("/importar/resolver-conflito")
    public String resolverConflito(
            @RequestParam String acao,
            @RequestParam Map<String, String> todosOsParametros,
            Model model
    ) {
        String nomeArquivo = todosOsParametros.get("nomeArquivo");
        todosOsParametros.remove("nomeArquivo");
        todosOsParametros.remove("acao");

        try {
            if (acao.equals("sobrescrever")) {
                service.processarImportacao(nomeArquivo, todosOsParametros, false);
                return "redirect:/?sucesso=true";
            
            } else if (acao.equals("escolher-manual")) {
                return "redirect:/importar?erro=Funcionalidade_Em_Construcao";
            
            } else if (acao.equals("smart-merge")) {
                return "redirect:/importar?erro=Funcionalidade_Em_Construcao";
            }
            
            return "redirect:/importar";

        } catch (IOException e) {
            return "redirect:/importar?erro=" + e.getMessage();
        }
    }
}