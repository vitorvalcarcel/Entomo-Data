package com.vitor.entomodata.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.vitor.entomodata.model.Exemplar;
import com.vitor.entomodata.service.ExemplarService;

@Controller
public class ExemplarController {

    @Autowired
    private ExemplarService service;

    @GetMapping("/")
    public String listarExemplares(
            Model model,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size,
            Exemplar filtro
    ) {
        buscarDados(model, page, size, filtro);
        
        return "index";
    }

    @GetMapping("/filtrar")
    public String filtrarExemplares(
            Model model,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "100") int size,
            Exemplar filtro
    ) {
        buscarDados(model, page, size, filtro);
        
        return "index :: tabela-resultados";
    }

    private void buscarDados(Model model, int page, int size, Exemplar filtro) {
        Page<Exemplar> paginaDeAbelhas = service.buscarTodosPaginado(page, size, filtro);
        
        model.addAttribute("listaDeAbelhas", paginaDeAbelhas);
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", size);
        model.addAttribute("totalPages", paginaDeAbelhas.getTotalPages());
        model.addAttribute("totalItems", paginaDeAbelhas.getTotalElements());
        model.addAttribute("filtro", filtro);
    }

    @GetMapping("/novo")
    public String mostrarFormulario(Model model) {
        model.addAttribute("exemplar", new Exemplar());
        return "cadastro";
    }

    @PostMapping("/salvar")
    public String salvarExemplar(Exemplar exemplar) {
        service.salvar(exemplar);
        return "redirect:/";
    }
}