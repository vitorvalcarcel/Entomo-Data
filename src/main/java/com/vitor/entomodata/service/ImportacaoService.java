package com.vitor.entomodata.service;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
public class ImportacaoService {

    // Abre o Excel e devolve uma lista com os nomes das colunas (Cabeçalho)
    public List<String> lerCabecalhos(MultipartFile arquivo) throws IOException {
        List<String> cabecalhos = new ArrayList<>();

        try (InputStream is = arquivo.getInputStream();
             Workbook workbook = new XSSFWorkbook(is)) {

            // Pega a primeira aba (Sheet 0)
            Sheet sheet = workbook.getSheetAt(0);
            
            // Pega a primeira linha (Row 0)
            Row linhaCabecalho = sheet.getRow(0);

            if (linhaCabecalho != null) {
                // Varre todas as células da primeira linha para pegar os nomes
                for (Cell cell : linhaCabecalho) {
                    cabecalhos.add(cell.getStringCellValue());
                }
            }
        }
        return cabecalhos;
    }
}