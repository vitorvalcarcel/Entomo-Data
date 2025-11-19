package com.vitor.entomodata.model;

import java.util.List;

public class ColunaExcelDTO {
    private String nome;
    private List<String> amostras;

    public ColunaExcelDTO(String nome, List<String> amostras) {
        this.nome = nome;
        this.amostras = amostras;
    }

    public String getNome() { return nome; }
    public List<String> getAmostras() { return amostras; }
}