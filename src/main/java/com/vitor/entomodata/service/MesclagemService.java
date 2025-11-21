package com.vitor.entomodata.service;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vitor.entomodata.helper.ExcelHelper;
import com.vitor.entomodata.model.Exemplar;
import com.vitor.entomodata.model.OpcaoConflito;
import com.vitor.entomodata.repository.ExemplarRepository;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MesclagemService {

    @Autowired
    private ExemplarRepository exemplarRepository;

    @Autowired
    private ExemplarService exemplarService;

    @Autowired
    private ExcelHelper excelHelper;

    @Autowired
    private MapeamentoService mapeamentoService;

    public Map<String, Map<String, Set<String>>> executarMesclagemInteligente(String nomeArquivo, Map<String, String> mapaDeColunas, Map<String, List<Integer>> duplicatas) throws IOException {
        Set<Integer> linhasParaIgnorar = new HashSet<>();
        for (List<Integer> linhas : duplicatas.values()) linhasParaIgnorar.addAll(linhas);
        
        // Salva o que não é duplicado primeiro
        salvarLinhasNaoDuplicadas(nomeArquivo, mapaDeColunas, linhasParaIgnorar);

        Map<String, List<OpcaoConflito>> dadosBrutos = detalharConflitos(nomeArquivo, mapaDeColunas, duplicatas);
        Map<String, Map<String, Set<String>>> conflitosReais = new HashMap<>();

        for (Map.Entry<String, List<OpcaoConflito>> entry : dadosBrutos.entrySet()) {
            String codigo = entry.getKey();
            List<OpcaoConflito> linhas = entry.getValue();
            
            Optional<Exemplar> existente = exemplarRepository.findById(codigo);
            Exemplar exemplarFinal = existente.orElse(new Exemplar());
            exemplarFinal.setCod(codigo);
            
            Map<String, Set<String>> conflitosDesteCodigo = new HashMap<>();
            boolean temConflitoGeral = false;
            Set<String> todosCampos = new HashSet<>();
            for(OpcaoConflito op : linhas) todosCampos.addAll(op.getDados().keySet());

            for (String campo : todosCampos) {
                Set<String> valoresDistintos = new HashSet<>();
                for (OpcaoConflito linha : linhas) {
                    String val = linha.getDados().get(campo);
                    if (val != null && !val.trim().isEmpty()) valoresDistintos.add(val.trim());
                }
                if (valoresDistintos.isEmpty()) {
                    // Nada a fazer
                } else if (valoresDistintos.size() == 1) {
                    mapeamentoService.preencherCampo(exemplarFinal, campo, valoresDistintos.iterator().next());
                } else {
                    conflitosDesteCodigo.put(campo, valoresDistintos);
                    temConflitoGeral = true;
                }
            }

            if (!temConflitoGeral) {
                exemplarService.salvar(exemplarFinal);
            } else {
                conflitosReais.put(codigo, conflitosDesteCodigo);
            }
        }
        return conflitosReais;
    }

    public void aplicarMesclagemFinal(String nomeArquivo, Map<String, String> mapaDeColunas, Map<String, Map<String, String>> decisoes) throws IOException {
        Map<String, List<Integer>> duplicatas = getMapaOcorrencias(nomeArquivo, mapaDeColunas);
        Map<String, List<OpcaoConflito>> dadosBrutos = detalharConflitos(nomeArquivo, mapaDeColunas, duplicatas);

        for (Map.Entry<String, List<OpcaoConflito>> entry : dadosBrutos.entrySet()) {
            String codigo = entry.getKey();
            List<OpcaoConflito> linhas = entry.getValue();
            
            if (decisoes.containsKey(codigo)) {
                Optional<Exemplar> existente = exemplarRepository.findById(codigo);
                Exemplar exemplarFinal = existente.orElse(new Exemplar());
                exemplarFinal.setCod(codigo);
                
                Map<String, String> decisoesDesteCodigo = decisoes.get(codigo);
                Set<String> todosCampos = new HashSet<>();
                for(OpcaoConflito op : linhas) todosCampos.addAll(op.getDados().keySet());

                for (String campo : todosCampos) {
                    if (decisoesDesteCodigo.containsKey(campo)) {
                         mapeamentoService.preencherCampo(exemplarFinal, campo, decisoesDesteCodigo.get(campo));
                    } else {
                        for (OpcaoConflito linha : linhas) {
                            String val = linha.getDados().get(campo);
                            if (val != null && !val.trim().isEmpty()) {
                                mapeamentoService.preencherCampo(exemplarFinal, campo, val);
                                break; 
                            }
                        }
                    }
                }
                exemplarService.salvar(exemplarFinal);
            }
        }
    }

    public Map<String, List<OpcaoConflito>> detalharConflitos(String nomeArquivo, Map<String, String> mapaDeColunas, Map<String, List<Integer>> duplicatas) throws IOException {
        File arquivo = new File(System.getProperty("java.io.tmpdir"), nomeArquivo);
        Map<String, List<OpcaoConflito>> detalhes = new LinkedHashMap<>();
        try (FileInputStream is = new FileInputStream(arquivo); Workbook workbook = new XSSFWorkbook(is)) {
            Sheet sheet = workbook.getSheetAt(0);
            Map<String, Integer> indiceDasColunas = excelHelper.mapearIndicesColunas(sheet);

            for (Map.Entry<String, List<Integer>> entry : duplicatas.entrySet()) {
                String codigo = entry.getKey();
                List<Integer> linhas = entry.getValue();
                List<OpcaoConflito> opcoes = new ArrayList<>();

                for (Integer numeroLinha : linhas) {
                    Row row = sheet.getRow(numeroLinha - 1);
                    Map<String, String> dadosDaLinha = new LinkedHashMap<>();
                    for (Map.Entry<String, String> colEntry : mapaDeColunas.entrySet()) {
                        String campoSistema = colEntry.getKey();
                        String nomeColExcel = colEntry.getValue();
                        if (indiceDasColunas.containsKey(nomeColExcel)) {
                             String val = excelHelper.getValorCelula(row.getCell(indiceDasColunas.get(nomeColExcel)));
                             if(!val.isEmpty()) dadosDaLinha.put(campoSistema, val);
                        }
                    }
                    opcoes.add(new OpcaoConflito(numeroLinha, dadosDaLinha));
                }
                detalhes.put(codigo, opcoes);
            }
        }
        return detalhes;
    }

    public Map<String, Set<String>> analisarDivergencias(Map<String, List<OpcaoConflito>> detalhes) {
        Map<String, Set<String>> divergencias = new HashMap<>();
        for (Map.Entry<String, List<OpcaoConflito>> entry : detalhes.entrySet()) {
            String codigo = entry.getKey();
            List<OpcaoConflito> opcoes = entry.getValue();
            Set<String> camposDivergentes = new HashSet<>();
            if (opcoes.size() <= 1) { divergencias.put(codigo, camposDivergentes); continue; }
            Set<String> todasAsChaves = opcoes.get(0).getDados().keySet();
            for (String chave : todasAsChaves) {
                String valorBase = opcoes.get(0).getDados().get(chave);
                for (int i = 1; i < opcoes.size(); i++) {
                    String valorAtual = opcoes.get(i).getDados().get(chave);
                    if ((valorBase == null && valorAtual != null) || (valorBase != null && !valorBase.equals(valorAtual))) {
                        camposDivergentes.add(chave); break;
                    }
                }
            }
            divergencias.put(codigo, camposDivergentes);
        }
        return divergencias;
    }

    private void salvarLinhasNaoDuplicadas(String nomeArquivo, Map<String, String> mapaDeColunas, Set<Integer> linhasParaIgnorar) throws IOException {
        File arquivo = new File(System.getProperty("java.io.tmpdir"), nomeArquivo);
        try (FileInputStream is = new FileInputStream(arquivo); Workbook workbook = new XSSFWorkbook(is)) {
            Sheet sheet = workbook.getSheetAt(0);
            Map<String, Integer> indiceDasColunas = excelHelper.mapearIndicesColunas(sheet);
            String nomeColunaCodigo = mapaDeColunas.get("cod");
            Integer idxCodigo = indiceDasColunas.get(nomeColunaCodigo);

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                if (linhasParaIgnorar.contains(i + 1)) continue;
                Row row = sheet.getRow(i);
                if (row == null) continue;
                String codigoNoExcel = null;
                if(idxCodigo != null) codigoNoExcel = excelHelper.getValorCelula(row.getCell(idxCodigo)).trim();
                if (codigoNoExcel == null || codigoNoExcel.isEmpty()) continue;

                Exemplar exemplar = new Exemplar();
                exemplar.setCod(codigoNoExcel);
                mapeamentoService.preencherExemplarComLinha(exemplar, row, mapaDeColunas, indiceDasColunas);
                exemplarService.salvar(exemplar);
            }
        }
    }

    private Map<String, List<Integer>> getMapaOcorrencias(String nomeArquivo, Map<String, String> mapaDeColunas) throws IOException {
        File arquivo = new File(System.getProperty("java.io.tmpdir"), nomeArquivo);
        String nomeColunaCodigo = mapaDeColunas.get("cod");
        Map<String, List<Integer>> mapa = new HashMap<>();
        try (FileInputStream is = new FileInputStream(arquivo); Workbook workbook = new XSSFWorkbook(is)) {
            Sheet sheet = workbook.getSheetAt(0);
            Map<String, Integer> indiceDasColunas = excelHelper.mapearIndicesColunas(sheet);
            Integer idxCodigo = indiceDasColunas.get(nomeColunaCodigo);
            if (idxCodigo == null) return mapa;
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;
                String cod = excelHelper.getValorCelula(row.getCell(idxCodigo)).trim();
                if (!cod.isEmpty()) mapa.computeIfAbsent(cod, k -> new ArrayList<>()).add(i + 1);
            }
        }
        return mapa.entrySet().stream().filter(e -> e.getValue().size() > 1).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}