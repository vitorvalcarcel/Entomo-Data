package com.vitor.entomodata.helper;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.vitor.entomodata.model.ColunaExcelDTO;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ExcelHelper {

    private final DataFormatter dataFormatter = new DataFormatter();

    public List<String> lerCabecalhos(File arquivo) throws IOException {
        List<String> cabecalhos = new ArrayList<>();
        try (FileInputStream is = new FileInputStream(arquivo);
             Workbook workbook = new XSSFWorkbook(is)) {
            Sheet sheet = workbook.getSheetAt(0);
            Row linhaCabecalho = sheet.getRow(0);
            if (linhaCabecalho != null) {
                for (Cell cell : linhaCabecalho) {
                    cabecalhos.add(cell.getStringCellValue());
                }
            }
        }
        return cabecalhos;
    }
    
    public List<String> lerCabecalhos(MultipartFile arquivo) throws IOException {
        List<String> cabecalhos = new ArrayList<>();
        try (InputStream is = arquivo.getInputStream();
             Workbook workbook = new XSSFWorkbook(is)) {
            Sheet sheet = workbook.getSheetAt(0);
            Row linhaCabecalho = sheet.getRow(0);
            if (linhaCabecalho != null) {
                for (Cell cell : linhaCabecalho) {
                    cabecalhos.add(cell.getStringCellValue());
                }
            }
        }
        return cabecalhos;
    }

    public List<ColunaExcelDTO> analisarArquivoComAmostras(MultipartFile arquivo) throws IOException {
        List<ColunaExcelDTO> colunas = new ArrayList<>();

        try (InputStream is = arquivo.getInputStream();
             Workbook workbook = new XSSFWorkbook(is)) {

            Sheet sheet = workbook.getSheetAt(0);
            Row linhaCabecalho = sheet.getRow(0);

            if (linhaCabecalho != null) {
                for (Cell cell : linhaCabecalho) {
                    String nomeColuna = cell.getStringCellValue();
                    int indiceColuna = cell.getColumnIndex();
                    
                    List<String> amostras = new ArrayList<>();
                    int linhasEncontradas = 0;
                    for (int i = 1; i <= sheet.getLastRowNum() && linhasEncontradas < 3 && i < 100; i++) {
                        Row rowData = sheet.getRow(i);
                        if (rowData != null) {
                            String valor = getValorCelula(rowData.getCell(indiceColuna)).trim();
                            if (!valor.isEmpty()) {
                                amostras.add(valor);
                                linhasEncontradas++;
                            }
                        }
                    }
                    colunas.add(new ColunaExcelDTO(nomeColuna, amostras));
                }
            }
        }
        return colunas;
    }

    public String getValorCelula(Cell cell) {
        if (cell == null) return "";
        return dataFormatter.formatCellValue(cell);
    }

    public Map<String, Integer> mapearIndicesColunas(Sheet sheet) {
        Row linhaCabecalho = sheet.getRow(0);
        Map<String, Integer> indices = new HashMap<>();
        if (linhaCabecalho != null) {
            for (Cell cell : linhaCabecalho) {
                indices.put(cell.getStringCellValue(), cell.getColumnIndex());
            }
        }
        return indices;
    }
}