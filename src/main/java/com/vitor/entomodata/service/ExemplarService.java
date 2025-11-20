package com.vitor.entomodata.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.vitor.entomodata.model.Exemplar;
import com.vitor.entomodata.repository.ExemplarRepository;

import java.util.List;

@Service
public class ExemplarService {

    @Autowired
    private ExemplarRepository repository;

    public Page<Exemplar> buscarTodosPaginado(int numeroPagina, int tamanhoPagina, String sortField, String sortDir, Exemplar filtro) {
        Sort sort = sortDir.equalsIgnoreCase("asc") 
                ? Sort.by(sortField).ascending() 
                : Sort.by(sortField).descending();

        Pageable paginacao = PageRequest.of(numeroPagina, tamanhoPagina, sort);

        ExampleMatcher matcher = ExampleMatcher.matching()
                .withIgnoreCase()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING);

        Example<Exemplar> example = Example.of(filtro, matcher);

        return repository.findAll(example, paginacao);
    }

    public List<Exemplar> buscarTodos() {
        return repository.findAll();
    }

    public void salvar(Exemplar exemplar) {
        repository.save(exemplar);
    }
    
    public Exemplar buscarPorId(String cod) {
        return repository.findById(cod).orElse(null);
    }

    public List<Exemplar> buscarPorListaDeCodigos(List<String> codigos) {
        return repository.findAllById(codigos);
    }

    public void deletarPorListaDeCodigos(List<String> codigos) {
        repository.deleteAllById(codigos);
    }
}