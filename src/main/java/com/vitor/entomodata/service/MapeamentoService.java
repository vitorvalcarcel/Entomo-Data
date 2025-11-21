package com.vitor.entomodata.service;

import org.apache.poi.ss.usermodel.Row;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vitor.entomodata.helper.CamposHelper;
import com.vitor.entomodata.helper.ExcelHelper;
import com.vitor.entomodata.model.CamposAbelha;
import com.vitor.entomodata.model.Exemplar;

import java.util.Map;

@Service
public class MapeamentoService {

    @Autowired
    private ExcelHelper excelHelper;

    @Autowired
    private CamposHelper camposHelper;

    public Map<String, String> getCamposMapeaveis() {
        return camposHelper.getTodosCampos();
    }

    public void preencherExemplarComLinha(Exemplar exemplar, Row row, Map<String, String> mapaDeColunas, Map<String, Integer> indiceDasColunas) {
        for (Map.Entry<String, String> entrada : mapaDeColunas.entrySet()) {
            String campoSistema = entrada.getKey();
            String colunaExcel = entrada.getValue();
            if (indiceDasColunas.containsKey(colunaExcel)) {
                String valor = excelHelper.getValorCelula(row.getCell(indiceDasColunas.get(colunaExcel)));
                if (!valor.isEmpty()) {
                    preencherCampo(exemplar, campoSistema, valor);
                }
            }
        }
    }

    public void preencherCampo(Exemplar e, String campoStr, String valor) {
        if(valor != null) valor = valor.trim();
        
        CamposAbelha campo = CamposAbelha.fromKey(campoStr);
        if (campo == null) return;

        switch (campo) {
            case COD: e.setCod(valor); break;
            case ESPECIE: e.setEspecie(valor); break;
            case FAMILIA: e.setFamilia(valor); break;
            case SUBFAMILIA: e.setSubfamilia(valor); break;
            case TRIBO: e.setTribo(valor); break;
            case SUBTRIBO: e.setSubtribo(valor); break;
            case GENERO: e.setGenero(valor); break;
            case SUBGENERO: e.setSubgenero(valor); break;
            case SUBESPECIE: e.setSubespecie(valor); break;
            case AUTOR: e.setAutor(valor); break;
            case DETERMINADOR: e.setDeterminador(valor); break;
            case SEXO: e.setSexo(valor); break;
            case ESPECIE_VEGETAL: e.setEspecieVegetalAssociada(valor); break;
            case GAVETA: e.setGaveta(valor); break;
            case CAIXA: e.setCaixa(valor); break;
            case PAIS: e.setPais(valor); break;
            case ESTADO: e.setEstado(valor); break;
            case CIDADE: e.setCidade(valor); break;
            case LOCALIDADE: e.setLocalidade(valor); break;
            case PROPRIETARIO: e.setProprietarioDoLocalDeColeta(valor); break;
            case BIOMA: e.setBioma(valor); break;
            case LATITUDE: e.setLatitude(valor); break;
            case LONGITUDE: e.setLongitude(valor); break;
            case COLETOR: e.setColetor(valor); break;
            case METODO: e.setMetodoDeAquisicao(valor); break;
            case DATA: e.setData(valor); break;
            case HORARIO: e.setHorarioColeta(valor); break;
        }
    }
}