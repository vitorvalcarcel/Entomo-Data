package com.vitor.entomodata.helper;

import org.springframework.stereotype.Component;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class CamposHelper {

    public Map<String, String> getTodosCampos() {
        Map<String, String> campos = new LinkedHashMap<>();
        campos.put("cod", "Código (ID)");
        
        campos.put("especie", "Espécie");
        campos.put("sexo", "Sexo");
        campos.put("familia", "Família");
        campos.put("subfamilia", "Subfamília");
        campos.put("tribo", "Tribo");
        campos.put("subtribo", "Subtribo");
        campos.put("genero", "Gênero");
        campos.put("subgenero", "Subgênero");
        campos.put("subespecie", "Subespécie");
        campos.put("autor", "Autor");
        campos.put("determinador", "Determinador");
        campos.put("especieVegetalAssociada", "Planta Assoc.");

        campos.put("gaveta", "Gaveta");
        campos.put("caixa", "Caixa");

        campos.put("pais", "País");
        campos.put("estado", "Estado");
        campos.put("cidade", "Cidade");
        campos.put("localidade", "Localidade");
        campos.put("proprietarioDoLocalDeColeta", "Proprietário");
        campos.put("bioma", "Bioma");
        campos.put("latitude", "Lat");
        campos.put("longitude", "Long");

        campos.put("coletor", "Coletor");
        campos.put("data", "Data");
        campos.put("horarioColeta", "Horário");
        campos.put("metodoDeAquisicao", "Método");
        
        return campos;
    }

    public Map<String, Map<String, String>> getCamposAgrupados() {
        Map<String, Map<String, String>> grupos = new LinkedHashMap<>();

        Map<String, String> taxonomia = new LinkedHashMap<>();
        taxonomia.put("especie", "Espécie");
        taxonomia.put("familia", "Família");
        taxonomia.put("genero", "Gênero");
        taxonomia.put("sexo", "Sexo");
        taxonomia.put("tribo", "Tribo");
        taxonomia.put("subtribo", "Subtribo");
        taxonomia.put("subgenero", "Subgênero");
        taxonomia.put("subespecie", "Subespécie");
        taxonomia.put("autor", "Autor");
        taxonomia.put("determinador", "Determinador");
        taxonomia.put("especieVegetalAssociada", "Planta Associada");
        grupos.put("Taxonomia", taxonomia);

        Map<String, String> armazenamento = new LinkedHashMap<>();
        armazenamento.put("gaveta", "Gaveta");
        armazenamento.put("caixa", "Caixa");
        grupos.put("Armazenamento", armazenamento);

        Map<String, String> local = new LinkedHashMap<>();
        local.put("pais", "País");
        local.put("estado", "Estado");
        local.put("cidade", "Cidade");
        local.put("localidade", "Localidade");
        local.put("proprietarioDoLocalDeColeta", "Proprietário");
        local.put("bioma", "Bioma");
        local.put("latitude", "Latitude");
        local.put("longitude", "Longitude");
        grupos.put("Localização", local);

        Map<String, String> coleta = new LinkedHashMap<>();
        coleta.put("coletor", "Coletor");
        coleta.put("data", "Data");
        coleta.put("horarioColeta", "Horário");
        coleta.put("metodoDeAquisicao", "Método");
        grupos.put("Coleta", coleta);

        return grupos;
    }
}