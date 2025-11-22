package com.vitor.entomodata.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.vitor.entomodata.model.Exemplar;

import java.util.List;

@Repository
public interface ExemplarRepository extends JpaRepository<Exemplar, String> {
    
    // Contagem distinta (Biodiversidade)
    @Query("SELECT COUNT(DISTINCT e.especie) FROM Exemplar e WHERE e.especie IS NOT NULL AND e.especie <> ''")
    long countDistinctEspecies();

    @Query("SELECT COUNT(DISTINCT e.genero) FROM Exemplar e WHERE e.genero IS NOT NULL AND e.genero <> ''")
    long countDistinctGeneros();

    @Query("SELECT COUNT(DISTINCT e.familia) FROM Exemplar e WHERE e.familia IS NOT NULL AND e.familia <> ''")
    long countDistinctFamilias();
    
    // Itens sem identificação (Ex: Espécie vazia ou contendo 'sp')
    @Query("SELECT COUNT(e) FROM Exemplar e WHERE e.especie IS NULL OR e.especie = '' OR LOWER(e.especie) LIKE '%sp.%'")
    long countSemIdentificacao();

    // Agrupamentos para Gráficos
    @Query("SELECT e.familia, COUNT(e) FROM Exemplar e WHERE e.familia IS NOT NULL AND e.familia <> '' GROUP BY e.familia ORDER BY COUNT(e) DESC")
    List<Object[]> countByFamilia();

    @Query("SELECT e.bioma, COUNT(e) FROM Exemplar e WHERE e.bioma IS NOT NULL AND e.bioma <> '' GROUP BY e.bioma ORDER BY COUNT(e) DESC")
    List<Object[]> countByBioma();

    @Query("SELECT e.metodoDeAquisicao, COUNT(e) FROM Exemplar e WHERE e.metodoDeAquisicao IS NOT NULL AND e.metodoDeAquisicao <> '' GROUP BY e.metodoDeAquisicao ORDER BY COUNT(e) DESC")
    List<Object[]> countByMetodo();
    
    // Top 10 espécies
    @Query("SELECT e.especie, COUNT(e) FROM Exemplar e WHERE e.especie IS NOT NULL AND e.especie <> '' GROUP BY e.especie ORDER BY COUNT(e) DESC LIMIT 10")
    List<Object[]> findTop10Especies();

    // Buscar apenas Lat/Long para o mapa
    @Query("SELECT e.latitude, e.longitude FROM Exemplar e WHERE e.latitude IS NOT NULL AND e.longitude IS NOT NULL AND e.latitude <> '' AND e.longitude <> ''")
    List<Object[]> findAllCoordenadas();

    // Buscar todas as datas para processamento de sazonalidade em memória
    @Query("SELECT e.data FROM Exemplar e WHERE e.data IS NOT NULL AND e.data <> ''")
    List<String> findAllDatas();
}