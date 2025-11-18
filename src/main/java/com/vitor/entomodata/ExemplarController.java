package com.vitor.entomodata;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ExemplarController {

    @Autowired
    private ExemplarRepository repository;

    @GetMapping("/")
    public String listarExemplares(Model model) {
        var lista = repository.findAll();
        model.addAttribute("listaDeAbelhas", lista);
        return "index";
    }
}