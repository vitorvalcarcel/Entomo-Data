package com.vitor.entomodata.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.vitor.entomodata.service.DashboardService;
import com.vitor.entomodata.model.DashboardDTO;
import com.vitor.entomodata.helper.CamposHelper;

@Controller
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;
    
    @Autowired
    private CamposHelper camposHelper;

    @GetMapping("/")
    public String exibirDashboard(Model model) {
        DashboardDTO dados = dashboardService.gerarDashboard();
        model.addAttribute("dashboard", dados);
        model.addAttribute("camposHelper", camposHelper); // Para manter o menu consistente se usar fragments
        return "dashboard";
    }
}