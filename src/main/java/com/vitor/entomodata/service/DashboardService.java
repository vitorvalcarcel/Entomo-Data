package com.vitor.entomodata.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vitor.entomodata.model.DashboardDTO;
import com.vitor.entomodata.repository.ExemplarRepository;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class DashboardService {

    @Autowired
    private ExemplarRepository repository;

    public DashboardDTO gerarDashboard() {
        DashboardDTO dto = new DashboardDTO();

        // 1. Carregar KPIs (Cards)
        dto.setTotalExemplares(repository.count());
        dto.setTotalEspecies(repository.countDistinctEspecies());
        dto.setTotalGeneros(repository.countDistinctGeneros());
        dto.setTotalFamilias(repository.countDistinctFamilias());
        dto.setSemIdentificacao(repository.countSemIdentificacao());

        // 2. Carregar Gráficos
        dto.setDistribuicaoPorFamilia(converterParaMapa(repository.countByFamilia()));
        dto.setDistribuicaoPorBioma(converterParaMapa(repository.countByBioma()));
        dto.setTopEspecies(converterParaMapa(repository.findTop10Especies()));

        // 3. Carregar Mapa
        List<Object[]> rawCoords = repository.findAllCoordenadas();
        List<DashboardDTO.PontoMapa> pontos = new ArrayList<>();
        
        for (Object[] row : rawCoords) {
            try {
                // Tenta converter String para Double (já que no banco pode estar como texto dependendo da importação)
                String latStr = row[0].toString().replace(",", ".");
                String lngStr = row[1].toString().replace(",", ".");
                
                Double lat = Double.parseDouble(latStr);
                Double lng = Double.parseDouble(lngStr);
                
                pontos.add(new DashboardDTO.PontoMapa(lat, lng));
            } catch (NumberFormatException e) {
                // Ignora coordenadas inválidas/não numéricas
            }
        }
        dto.setCoordenadas(pontos);

        return dto;
    }

    // Helper para transformar List<Object[]> do JPA em Map<String, Long>
    private Map<String, Long> converterParaMapa(List<Object[]> lista) {
        Map<String, Long> mapa = new LinkedHashMap<>();
        for (Object[] arr : lista) {
            String chave = (String) arr[0];
            Long valor = ((Number) arr[1]).longValue();
            mapa.put(chave, valor);
        }
        return mapa;
    }
}