package com.vitor.entomodata.model;

import java.util.List;
import java.util.Map;

public class DashboardDTO {

    // Cards (KPIs)
    private long totalExemplares;
    private long totalEspecies;
    private long totalGeneros;
    private long totalFamilias;
    private long semIdentificacao;

    // Gráficos (Mapas Chave -> Valor)
    private Map<String, Long> distribuicaoPorFamilia;
    private Map<String, Long> distribuicaoPorBioma;
    private Map<String, Long> topEspecies;
    
    // Mapa (Lista de Coordenadas)
    private List<PontoMapa> coordenadas;

    // Construtor vazio
    public DashboardDTO() {}

    // Getters e Setters
    public long getTotalExemplares() { return totalExemplares; }
    public void setTotalExemplares(long totalExemplares) { this.totalExemplares = totalExemplares; }

    public long getTotalEspecies() { return totalEspecies; }
    public void setTotalEspecies(long totalEspecies) { this.totalEspecies = totalEspecies; }

    public long getTotalGeneros() { return totalGeneros; }
    public void setTotalGeneros(long totalGeneros) { this.totalGeneros = totalGeneros; }

    public long getTotalFamilias() { return totalFamilias; }
    public void setTotalFamilias(long totalFamilias) { this.totalFamilias = totalFamilias; }

    public long getSemIdentificacao() { return semIdentificacao; }
    public void setSemIdentificacao(long semIdentificacao) { this.semIdentificacao = semIdentificacao; }

    public Map<String, Long> getDistribuicaoPorFamilia() { return distribuicaoPorFamilia; }
    public void setDistribuicaoPorFamilia(Map<String, Long> distribuicaoPorFamilia) { this.distribuicaoPorFamilia = distribuicaoPorFamilia; }

    public Map<String, Long> getDistribuicaoPorBioma() { return distribuicaoPorBioma; }
    public void setDistribuicaoPorBioma(Map<String, Long> distribuicaoPorBioma) { this.distribuicaoPorBioma = distribuicaoPorBioma; }

    public Map<String, Long> getTopEspecies() { return topEspecies; }
    public void setTopEspecies(Map<String, Long> topEspecies) { this.topEspecies = topEspecies; }

    public List<PontoMapa> getCoordenadas() { return coordenadas; }
    public void setCoordenadas(List<PontoMapa> coordenadas) { this.coordenadas = coordenadas; }

    // Classe interna para facilitar o transporte de Lat/Long
    public static class PontoMapa {
        private Double lat;
        private Double lng;
        private Long count;

        public PontoMapa(Double lat, Double lng) {
            this.lat = lat;
            this.lng = lng;
            this.count = 1L;
        }
        
        public Double getLat() { return lat; }
        public Double getLng() { return lng; }
        public Long getCount() { return count; }
    }
}