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
import com.vitor.entomodata.model.OpcaoConflito;
import com.vitor.entomodata.model.ColunaExcelDTO;
import com.vitor.entomodata.model.Exemplar;
import com.vitor.entomodata.model.AnaliseBancoDTO;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.ArrayList;

@Controller
public class ImportacaoController {

    @Autowired
    private ImportacaoService service;

    private Map<String, String> getCamposAmigaveis() {
        Map<String, String> campos = new LinkedHashMap<>();
        campos.put("cod", "Código (ID)");
        campos.put("especie", "Espécie");
        campos.put("familia", "Família");
        campos.put("subfamilia", "Subfamília");
        campos.put("tribo", "Tribo");
        campos.put("subtribo", "Subtribo");
        campos.put("genero", "Gênero");
        campos.put("subgenero", "Subgênero");
        campos.put("subespecie", "Subespécie");
        campos.put("autor", "Autor");
        campos.put("determinador", "Determinador");
        campos.put("sexo", "Sexo");
        campos.put("especieVegetalAssociada", "Espécie Vegetal Associada");
        campos.put("pais", "País da Coleta");
        campos.put("estado", "Estado da Coleta");
        campos.put("cidade", "Cidade/Município da Coleta");
        campos.put("localidade", "Localidade Específica da Coleta");
        campos.put("proprietarioDoLocalDeColeta", "Proprietário do Local da Coleta");
        campos.put("bioma", "Bioma");
        campos.put("latitude", "Latitude");
        campos.put("longitude", "Longitude");
        campos.put("data", "Data da Coleta");
        campos.put("horarioColeta", "Horário da Coleta");
        campos.put("coletor", "Coletor");
        campos.put("metodoDeAquisicao", "Método de Coleta");
        campos.put("gaveta", "Gaveta");
        campos.put("caixa", "Caixa");
        return campos;
    }

    @GetMapping("/importar")
    public String telaUpload() { return "importar-upload"; }

    @PostMapping("/importar/upload")
    public String processarUpload(@RequestParam("arquivoExcel") MultipartFile arquivo, Model model) {
        try {
            String nomeArquivo = arquivo.getOriginalFilename();
            Path caminhoTemporario = Paths.get(System.getProperty("java.io.tmpdir"), nomeArquivo);
            Files.copy(arquivo.getInputStream(), caminhoTemporario, StandardCopyOption.REPLACE_EXISTING);

            List<ColunaExcelDTO> colunasDoExcel = service.analisarArquivoExcel(arquivo);
            
            model.addAttribute("colunasExcel", colunasDoExcel);
            model.addAttribute("camposSistema", getCamposAmigaveis());
            model.addAttribute("nomeArquivoSalvo", nomeArquivo);
            
            return "importar-mapa"; 
            
        } catch (IOException e) {
            model.addAttribute("erro", "Erro ao processar arquivo: " + e.getMessage());
            return "importar-upload";
        }
    }

    @PostMapping("/importar/finalizar")
    public String finalizarImportacao(@RequestParam Map<String, String> paramsFormulario, Model model) {
        String nomeArquivo = paramsFormulario.get("nomeArquivo");
        paramsFormulario.remove("nomeArquivo");
        Map<String, String> mapaParaService = new LinkedHashMap<>();
        for (Map.Entry<String, String> entry : paramsFormulario.entrySet()) {
            if (entry.getValue() != null && !entry.getValue().isEmpty()) {
                mapaParaService.put(entry.getValue(), entry.getKey());
            }
        }

        try {
            List<Exemplar> lista = service.processarImportacao(nomeArquivo, mapaParaService, true, null);
            
            return encaminharParaAnalise(lista, nomeArquivo, mapaParaService, model);
            
        } catch (DuplicidadeException e) {
            model.addAttribute("duplicatas", e.getDuplicatas());
            model.addAttribute("nomeArquivoSalvo", nomeArquivo);
            model.addAttribute("mapaAnterior", mapaParaService);
            return "importar-conflito";
        } catch (IOException e) {
            return "redirect:/importar?erro=" + e.getMessage();
        }
    }

    @PostMapping("/importar/resolver-conflito")
    public String resolverConflito(@RequestParam String acao, @RequestParam Map<String, String> todosOsParametros, Model model) {
        String nomeArquivo = todosOsParametros.get("nomeArquivo");
        todosOsParametros.remove("nomeArquivo");
        todosOsParametros.remove("acao");

        try {
            if (acao.equals("sobrescrever")) {
                List<Exemplar> lista = service.processarImportacao(nomeArquivo, todosOsParametros, false, null);
                
                return encaminharParaAnalise(lista, nomeArquivo, todosOsParametros, model);
            
            } else if (acao.equals("escolher-manual")) {
                try {
                    service.processarImportacao(nomeArquivo, todosOsParametros, true, null);
                    return "redirect:/?sucesso=true";
                } catch (DuplicidadeException e) {
                    Map<String, List<OpcaoConflito>> detalhes = service.detalharConflitos(nomeArquivo, todosOsParametros, e.getDuplicatas());
                    Map<String, Set<String>> divergencias = service.analisarDivergencias(detalhes);
                    model.addAttribute("conflitos", detalhes);
                    model.addAttribute("divergencias", divergencias);
                    model.addAttribute("nomeArquivoSalvo", nomeArquivo);
                    model.addAttribute("mapaAnterior", todosOsParametros);
                    return "importar-manual";
                }
            
            } else if (acao.equals("smart-merge")) {
                return "redirect:/importar?erro=Funcionalidade_Em_Construcao";
            }
            return "redirect:/importar";

        } catch (IOException e) {
            return "redirect:/importar?erro=" + e.getMessage();
        }
    }

    @PostMapping("/importar/processar-manual")
    public String processarManual(@RequestParam Map<String, String> todosOsParametros, Model model) {
        String nomeArquivo = todosOsParametros.get("nomeArquivo");
        todosOsParametros.remove("nomeArquivo");
        
        Map<String, Integer> linhasParaManter = new HashMap<>();
        Map<String, String> colunasLimpas = new HashMap<>();
        
        for (Map.Entry<String, String> entry : todosOsParametros.entrySet()) {
            if (entry.getKey().startsWith("escolha_")) {
                String codigo = entry.getKey().replace("escolha_", "");
                int linhaEscolhida = Integer.parseInt(entry.getValue());
                linhasParaManter.put(codigo, linhaEscolhida);
            } else if (!entry.getKey().equals("escolhasManual")) {
                colunasLimpas.put(entry.getKey(), entry.getValue());
            }
        }

        try {
            List<Exemplar> lista = service.processarImportacao(nomeArquivo, colunasLimpas, false, linhasParaManter);
            
            return encaminharParaAnalise(lista, nomeArquivo, colunasLimpas, model);
            
        } catch (IOException e) {
            return "redirect:/importar?erro=" + e.getMessage();
        }
    }

    private String encaminharParaAnalise(List<Exemplar> listaProcessada, String nomeArquivo, Map<String, String> mapaColunas, Model model) {
        AnaliseBancoDTO analise = service.analisarConflitosComBanco(listaProcessada);
        
        if (analise.getNovos().isEmpty() && analise.getExistentes().isEmpty()) {
            return "redirect:/?msg=NadaParaImportar";
        }
        
        model.addAttribute("analise", analise);
        model.addAttribute("nomeArquivoSalvo", nomeArquivo);
        model.addAttribute("mapaAnterior", mapaColunas);
        
        return "importar-analise"; 
    }

    @PostMapping("/importar/analise-banco")
    public String processarAnaliseBanco(
            @RequestParam Map<String, String> params,
            @RequestParam(value = "novosSelecionados", required = false) List<String> novosIds,
            @RequestParam(value = "existentesSelecionados", required = false) List<String> existentesIds,
            Model model
    ) {
        String nomeArquivo = params.get("nomeArquivo");
        Map<String, String> colunasLimpas = new LinkedHashMap<>();
        for(Map.Entry<String, String> e : params.entrySet()) {
            if(!e.getKey().equals("nomeArquivo") && !e.getKey().equals("novosSelecionados") && !e.getKey().equals("existentesSelecionados")) {
                colunasLimpas.put(e.getKey(), e.getValue());
            }
        }

        try {
            List<Exemplar> todos = service.processarImportacao(nomeArquivo, colunasLimpas, false, null);
            
            List<Exemplar> paraSalvar = new ArrayList<>();

            if (novosIds != null) {
                for (Exemplar e : todos) {
                    if (novosIds.contains(e.getCod())) {
                        paraSalvar.add(e);
                    }
                }
            }

            service.salvarLista(paraSalvar);

            if (existentesIds != null && !existentesIds.isEmpty()) {
                return "redirect:/?msg=NovosSalvos_EdicaoPendente";
            }

            return "redirect:/?msg=ImportacaoConcluida";

        } catch (IOException e) {
            return "redirect:/importar?erro=" + e.getMessage();
        }
    }
    
    @PostMapping("/importar/processar-smart")
    public String processarSmart(@RequestParam Map<String, String> todosOsParametros) {
        return "redirect:/";
    }
}