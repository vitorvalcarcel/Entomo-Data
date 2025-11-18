package com.vitor.entomodata.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "exemplares")
public class Exemplar {

    @Id
    private String cod;

    // Dados do indivíduo
    private String familia;
    private String subfamilia;
    private String tribo;
    private String subtribo;
    private String genero;
    private String subgenero;
    private String especie;
    private String subespecie;
    private String autor;
    private String determinador;
    private String sexo;
    private String gaveta;
    private String caixa;
    private String especieVegetalAssociada;

    // Dados da coleta
    private String pais;
    private String estado;
    private String cidade;
    private String proprietarioDoLocalDeColeta;
    private String latitude;
    private String longitude;
    private String localidade;
    private String bioma;
    private String coletor;
    private String metodoDeAquisicao;
    private String horarioColeta;
    private String data;

    // Getters
    public String getCod() { return cod; }
    public String getFamilia() { return familia; }
    public String getSubfamilia() { return subfamilia; }
    public String getTribo() { return tribo; }
    public String getSubtribo() { return subtribo; }
    public String getGenero() { return genero; }
    public String getSubgenero() { return subgenero; }
    public String getEspecie() { return especie; }
    public String getSubespecie() { return subespecie; }
    public String getAutor() { return autor; }
    public String getDeterminador() { return determinador; }
    public String getSexo() { return sexo; }
    public String getGaveta() { return gaveta; }
    public String getCaixa() { return caixa; }
    public String getEspecieVegetalAssociada() { return especieVegetalAssociada; }
    public String getPais() { return pais; }
    public String getEstado() { return estado; }
    public String getCidade() { return cidade; }
    public String getProprietarioDoLocalDeColeta() { return proprietarioDoLocalDeColeta; }
    public String getLatitude() { return latitude; }
    public String getLongitude() { return longitude; }
    public String getLocalidade() { return localidade; }
    public String getBioma() { return bioma; }
    public String getColetor() { return coletor; }
    public String getMetodoDeAquisicao() { return metodoDeAquisicao; }
    public String getHorarioColeta() { return horarioColeta; }
    public String getData() { return data; }

    // Setters
    public void setCod(String cod) { this.cod = cod; }
    public void setFamilia(String familia) { this.familia = familia; }
    public void setSubfamilia(String subfamilia) { this.subfamilia = subfamilia; }
    public void setTribo(String tribo) { this.tribo = tribo; }
    public void setSubtribo(String subtribo) { this.subtribo = subtribo; }
    public void setGenero(String genero) { this.genero = genero; }
    public void setSubgenero(String subgenero) { this.subgenero = subgenero; }
    public void setEspecie(String especie) { this.especie = especie; }
    public void setSubespecie(String subespecie) { this.subespecie = subespecie; }
    public void setAutor(String autor) { this.autor = autor; }
    public void setDeterminador(String determinador) { this.determinador = determinador; }
    public void setSexo(String sexo) { this.sexo = sexo; }
    public void setGaveta(String gaveta) { this.gaveta = gaveta; }
    public void setCaixa(String caixa) { this.caixa = caixa; }
    public void setEspecieVegetalAssociada(String especieVegetalAssociada) { this.especieVegetalAssociada = especieVegetalAssociada; }
    public void setPais(String pais) { this.pais = pais; }
    public void setEstado(String estado) { this.estado = estado; }
    public void setCidade(String cidade) { this.cidade = cidade; }
    public void setProprietarioDoLocalDeColeta(String proprietarioDoLocalDeColeta) { this.proprietarioDoLocalDeColeta = proprietarioDoLocalDeColeta; }
    public void setLatitude(String latitude) { this.latitude = latitude; }
    public void setLongitude(String longitude) { this.longitude = longitude; }
    public void setLocalidade(String localidade) { this.localidade = localidade; }
    public void setBioma(String bioma) { this.bioma = bioma; }
    public void setColetor(String coletor) { this.coletor = coletor; }
    public void setMetodoDeAquisicao(String metodoDeAquisicao) { this.metodoDeAquisicao = metodoDeAquisicao; }
    public void setHorarioColeta(String horarioColeta) { this.horarioColeta = horarioColeta; }
    public void setData(String data) { this.data = data; }

}