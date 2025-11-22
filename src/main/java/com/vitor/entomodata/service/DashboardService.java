package com.vitor.entomodata.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vitor.entomodata.model.DashboardDTO;
import com.vitor.entomodata.repository.ExemplarRepository;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

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
        dto.setDistribuicaoPorMetodo(converterParaMapa(repository.countByMetodo()));
        dto.setTopEspecies(converterParaMapa(repository.findTop10Especies()));

        // 3. Processar Sazonalidade (Datas) - VERSÃO ROBUSTA
        dto.setSazonalidadePorMes(processarSazonalidade(repository.findAllDatas()));

        // 4. Carregar Mapa
        List<Object[]> rawCoords = repository.findAllCoordenadas();
        List<DashboardDTO.PontoMapa> pontos = new ArrayList<>();
        
        for (Object[] row : rawCoords) {
            try {
                // Remove espaços e troca vírgula por ponto
                String latStr = row[0].toString().trim().replace(",", ".");
                String lngStr = row[1].toString().trim().replace(",", ".");
                
                Double lat = Double.parseDouble(latStr);
                Double lng = Double.parseDouble(lngStr);
                
                // Validação básica de coordenadas terrestres para evitar erros no mapa
                if (lat >= -90 && lat <= 90 && lng >= -180 && lng <= 180) {
                    pontos.add(new DashboardDTO.PontoMapa(lat, lng));
                }
            } catch (Exception e) {
                // Ignora coordenadas inválidas
            }
        }
        dto.setCoordenadas(pontos);

        return dto;
    }

    private Map<String, Long> converterParaMapa(List<Object[]> lista) {
        Map<String, Long> mapa = new LinkedHashMap<>();
        for (Object[] arr : lista) {
            if (arr[0] == null) continue; // Pula chaves nulas
            String chave = (String) arr[0];
            Long valor = ((Number) arr[1]).longValue();
            mapa.put(chave, valor);
        }
        return mapa;
    }

    private Map<String, Long> processarSazonalidade(List<String> datasRaw) {
        // Inicializa o mapa zerado para garantir que todos os meses apareçam no gráfico
        Map<String, Long> contagemMeses = new LinkedHashMap<>();
        String[] meses = {"Jan", "Fev", "Mar", "Abr", "Mai", "Jun", "Jul", "Ago", "Set", "Out", "Nov", "Dez"};
        for (String mes : meses) contagemMeses.put(mes, 0L);

        // Lista de tentativas de formato (do mais comum para o mais raro)
        List<DateTimeFormatter> formatters = Arrays.asList(
            DateTimeFormatter.ofPattern("dd/MM/yyyy"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd"),
            DateTimeFormatter.ofPattern("d/M/yyyy"),     // Excel as vezes remove o zero (1/1/2024)
            DateTimeFormatter.ofPattern("dd-MM-yyyy"),
            DateTimeFormatter.ofPattern("MM/dd/yyyy"),   // Formato americano
            DateTimeFormatter.ofPattern("dd/MM/yy"),     // Ano com 2 dígitos
            DateTimeFormatter.ofPattern("d/M/yy")
        );

        for (String dataStr : datasRaw) {
            if (dataStr == null || dataStr.trim().isEmpty()) continue;

            // Limpeza prévia: Pega só a primeira parte se tiver hora "2024-01-01 10:00"
            String cleanData = dataStr.trim().split(" ")[0]; 
            
            LocalDate dataConvertida = null;
            
            // Tenta converter em todos os formatos conhecidos
            for (DateTimeFormatter fmt : formatters) {
                try {
                    dataConvertida = LocalDate.parse(cleanData, fmt);
                    break; // Sucesso! Para de tentar
                } catch (Exception ignored) {}
            }

            if (dataConvertida != null) {
                int mesIndex = dataConvertida.getMonthValue() - 1; // 0 a 11
                String nomeMes = meses[mesIndex];
                contagemMeses.put(nomeMes, contagemMeses.get(nomeMes) + 1);
            } else {
                // Debug opcional: System.out.println("Data ignorada (formato desconhecido): " + dataStr);
            }
        }
        return contagemMeses;
    }
}