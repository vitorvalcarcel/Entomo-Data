package com.vitor.entomodata.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.vitor.entomodata.model.Exemplar;

@Repository
public interface ExemplarRepository extends JpaRepository<Exemplar, String> {
    
}