package com.vitor.entomodata.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import com.vitor.entomodata.service.ImportacaoService;

import java.io.IOException;
import java.util.List;

@Controller
public class ImportacaoController {

    @Autowired
    private ImportacaoService service;

    // Mostra a tela inicial de Upload
    @GetMapping("/importar")
    public String telaUpload() {
        return "importar-upload";
    }

    // Recebe o arquivo e direciona para a tela de Mapeamento (De-Para)
    @PostMapping("/importar/upload")
    public String processarUpload(@RequestParam("arquivoExcel") MultipartFile arquivo, Model model) {
        try {
            // Lê as colunas do Excel do usuário
            List<String> colunasDoExcel = service.lerCabecalhos(arquivo);
            
            // Passa as colunas encontradas para a próxima tela
            model.addAttribute("colunasExcel", colunasDoExcel);
            model.addAttribute("nomeArquivo", arquivo.getOriginalFilename());
            return "importar-mapa"; 
            
        } catch (IOException e) {
            model.addAttribute("erro", "Erro ao ler arquivo: " + e.getMessage());
            return "importar-upload";
        }
    }
}