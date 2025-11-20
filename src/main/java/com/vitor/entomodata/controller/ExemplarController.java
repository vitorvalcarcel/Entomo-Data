package com.vitor.entomodata.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.vitor.entomodata.model.Exemplar;
import com.vitor.entomodata.service.ExemplarService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class ExemplarController {

    @Autowired
    private ExemplarService service;

    // === LISTAGEM ===
    @GetMapping("/")
    public String listarExemplares(
            Model model,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size,
            @RequestParam(defaultValue = "cod") String sort,
            @RequestParam(defaultValue = "asc") String dir,
            Exemplar filtro
    ) {
        buscarDados(model, page, size, sort, dir, filtro);
        return "index";
    }

    @GetMapping("/filtrar")
    public String filtrarExemplares(
            Model model,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size,
            @RequestParam(defaultValue = "cod") String sort,
            @RequestParam(defaultValue = "asc") String dir,
            Exemplar filtro
    ) {
        buscarDados(model, page, size, sort, dir, filtro);
        return "index :: tabela-resultados";
    }

    private void buscarDados(Model model, int page, int size, String sort, String dir, Exemplar filtro) {
        Page<Exemplar> paginaDeAbelhas = service.buscarTodosPaginado(page, size, sort, dir, filtro);
        
        model.addAttribute("listaDeAbelhas", paginaDeAbelhas);
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", size);
        model.addAttribute("totalPages", paginaDeAbelhas.getTotalPages());
        model.addAttribute("totalItems", paginaDeAbelhas.getTotalElements());
        model.addAttribute("filtro", filtro);
        model.addAttribute("sortField", sort);
        model.addAttribute("sortDir", dir);
    }

    // === CADASTRO (NOVO) ===

    @GetMapping("/novo")
    public String mostrarFormulario(Model model) {
        model.addAttribute("exemplar", new Exemplar());
        // Removemos a flag 'edicao' pois agora temos telas separadas
        return "cadastro"; 
    }

    @PostMapping("/salvar")
    public String salvarExemplar(Exemplar exemplar, Model model) {
        // Se tentar criar um código que já existe...
        if (service.buscarPorId(exemplar.getCod()) != null) {
            Exemplar existente = service.buscarPorId(exemplar.getCod());
            
            model.addAttribute("novo", exemplar);
            model.addAttribute("antigo", existente);
            
            return "cadastro-conflito"; // Tela de aviso
        }

        service.salvar(exemplar);
        return "redirect:/";
    }

    // === EDIÇÃO (FLUXO COMPLETO) ===

    // 1. Início: Clicou no botão "Lápis" na tabela
    @GetMapping("/editar/{cod}")
    public String editarExemplar(@PathVariable String cod, Model model) {
        Exemplar original = service.buscarPorId(cod);
        if (original == null) return "redirect:/";
        
        model.addAttribute("exemplar", original); // Form preenchido com dados do banco
        model.addAttribute("original", original); // Original para comparação
        return "editar"; // Abre o editar.html
    }
    
    // 2. Início Alternativo: Veio da tela de Conflito de Cadastro
    @PostMapping("/editar/conflito")
    public String editarConflitoCadastro(Exemplar exemplar, Model model) {
        // Busca o original no banco para servir de base de comparação
        Exemplar original = service.buscarPorId(exemplar.getCod());
        
        // Manda para a tela de edição:
        // 'exemplar': Tem os dados NOVOS que o usuário digitou (para não perder o trabalho)
        // 'original': Tem os dados VELHOS do banco (para saber o que mudou)
        model.addAttribute("exemplar", exemplar);
        model.addAttribute("original", original);
        
        return "editar"; 
    }

    // 3. Meio: Clicou em "Revisar" na tela de edição
    @PostMapping("/editar/revisar")
    public String revisarEdicao(Exemplar exemplar, Model model) {
        Exemplar original = service.buscarPorId(exemplar.getCod());
        
        if (original == null) { 
            // Segurança: se o registro sumiu, salva como novo direto
            service.salvar(exemplar); 
            return "redirect:/";
        }

        model.addAttribute("novo", exemplar);
        model.addAttribute("antigo", original);
        
        return "edicao-confirmar"; // Tela de Antes vs Depois
    }

    // 4. Retorno: Clicou em "Voltar" na tela de revisão
    @PostMapping("/editar/retornar")
    public String retornarParaEdicao(Exemplar exemplar, Model model) {
        Exemplar original = service.buscarPorId(exemplar.getCod());
        if (original == null) original = new Exemplar();
        
        model.addAttribute("exemplar", exemplar); // Mantém o que estava sendo editado
        model.addAttribute("original", original);
        return "editar"; 
    }

    // 5. Fim: Confirmou a revisão
    @PostMapping("/editar/confirmar")
    public String confirmarEdicao(Exemplar exemplar) {
        service.salvar(exemplar);
        return "redirect:/?msg=EdicaoSucesso";
    }

    // === EXCLUSÃO INDIVIDUAL ===

    @GetMapping("/deletar/{cod}")
    public String deletarExemplarIndividual(@PathVariable String cod) {
        service.deletarPorListaDeCodigos(Arrays.asList(cod));
        return "redirect:/";
    }

    // === EXCLUSÃO EM MASSA ===

    @GetMapping("/deletar")
    public String telaDeletar() {
        return "deletar-busca";
    }

    @PostMapping("/deletar/verificar")
    public String verificarExclusao(@RequestParam("codigosRaw") String codigosRaw, Model model) {
        List<String> codigosDigitados = Arrays.stream(codigosRaw.split("[\\r\\n,]+"))
                                              .map(String::trim)
                                              .filter(s -> !s.isEmpty())
                                              .collect(Collectors.toList());

        if (codigosDigitados.isEmpty()) {
            model.addAttribute("erro", "Nenhum código foi informado.");
            return "deletar-busca";
        }

        List<Exemplar> encontrados = service.buscarPorListaDeCodigos(codigosDigitados);
        List<String> idsEncontrados = encontrados.stream().map(Exemplar::getCod).collect(Collectors.toList());
        List<String> naoEncontrados = codigosDigitados.stream()
                                                      .filter(c -> !idsEncontrados.contains(c))
                                                      .collect(Collectors.toList());

        model.addAttribute("encontrados", encontrados);
        model.addAttribute("naoEncontrados", naoEncontrados);
        model.addAttribute("qtdParaApagar", encontrados.size());

        return "deletar-confirma";
    }

    @PostMapping("/deletar/confirmar")
    public String confirmarExclusao(
            @RequestParam("idsParaDeletar") List<String> ids,
            @RequestParam("senhaConfirmacao") int senhaDigitada,
            @RequestParam("qtdReal") int qtdReal,
            Model model
    ) {
        if (senhaDigitada != qtdReal) {
            List<Exemplar> encontrados = service.buscarPorListaDeCodigos(ids);
            model.addAttribute("encontrados", encontrados);
            model.addAttribute("qtdParaApagar", qtdReal);
            model.addAttribute("naoEncontrados", new ArrayList<>());
            model.addAttribute("erro", "Olha, você digitou o número errado. Você sabe o que tá fazendo? Presta atenção!");
            return "deletar-confirma"; 
        }

        service.deletarPorListaDeCodigos(ids);
        return "redirect:/?msg=ExclusaoSucesso";
    }
}