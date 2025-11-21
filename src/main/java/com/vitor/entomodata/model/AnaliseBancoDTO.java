package com.vitor.entomodata.model;

import java.util.List;

public class AnaliseBancoDTO {
    private List<Exemplar> novos;
    private List<Exemplar> existentes;

    public AnaliseBancoDTO(List<Exemplar> novos, List<Exemplar> existentes) {
        this.novos = novos;
        this.existentes = existentes;
    }

    public List<Exemplar> getNovos() { return novos; }
    public List<Exemplar> getExistentes() { return existentes; }
}