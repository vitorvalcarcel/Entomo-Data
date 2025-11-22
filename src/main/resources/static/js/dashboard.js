document.addEventListener('DOMContentLoaded', function() {
    
    // --- 1. GRÁFICOS (Chart.js) ---

    // Gráfico de FAMÍLIA (Doughnut)
    criarGrafico('chartFamilia', 'doughnut', dashboardData.distribuicaoPorFamilia, {
        colors: ['#3498db', '#e74c3c', '#f1c40f', '#2ecc71', '#9b59b6', '#34495e', '#e67e22'],
        legend: true
    });

    // Gráfico de BIOMA (Barra)
    criarGrafico('chartBioma', 'bar', dashboardData.distribuicaoPorBioma, {
        colors: '#2ecc71',
        label: 'Exemplares'
    });

    // Gráfico de MÉTODO DE COLETA (Pie)
    criarGrafico('chartMetodo', 'pie', dashboardData.distribuicaoPorMetodo, {
        colors: ['#1abc9c', '#16a085', '#27ae60', '#2980b9', '#8e44ad', '#f39c12'],
        legend: true
    });

    // Gráfico de SAZONALIDADE (Linha)
    criarGrafico('chartSazonalidade', 'line', dashboardData.sazonalidadePorMes, {
        colors: '#e74c3c',
        label: 'Coletas',
        fill: true,
        tension: 0.3
    });

    // --- FUNÇÃO HELPER PARA CRIAR GRÁFICOS ---
    function criarGrafico(idElemento, tipo, dadosObj, opcoes) {
        const ctx = document.getElementById(idElemento);
        if (!ctx || !dadosObj) return;

        const labels = Object.keys(dadosObj);
        const data = Object.values(dadosObj);
        const bgColors = opcoes.colors || '#3498db';

        new Chart(ctx, {
            type: tipo,
            data: {
                labels: labels,
                datasets: [{
                    label: opcoes.label || 'Quantidade',
                    data: data,
                    backgroundColor: bgColors,
                    borderColor: tipo === 'line' ? bgColors : '#fff',
                    borderWidth: 1,
                    fill: opcoes.fill || false,
                    tension: opcoes.tension || 0
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: { 
                        display: !!opcoes.legend,
                        position: 'right'
                    }
                },
                scales: (tipo === 'bar' || tipo === 'line') ? { y: { beginAtZero: true } } : {}
            }
        });
    }

    // --- 2. MAPA DE CALOR (Leaflet) ---
    
    const mapDiv = document.getElementById('mapaCalor');
    
    if (mapDiv && dashboardData.coordenadas && dashboardData.coordenadas.length > 0) {
        
        // Intensidade 3 para os dados (base)
        const heatData = dashboardData.coordenadas.map(p => [p.lat, p.lng, 3]);

        const startLat = heatData[0][0] || -14.235;
        const startLng = heatData[0][1] || -51.925;

        // --- AQUI ESTÁ A TRAVA DE ZOOM ---
        const map = L.map('mapaCalor', {
            minZoom: 3, // Impede o zoom out excessivo (não mostra o mundo repetido)
            maxZoom: 18 // Zoom máximo permitido (opcional)
        }).setView([startLat, startLng], 4);

        L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
            attribution: '&copy; OpenStreetMap',
            maxZoom: 18
        }).addTo(map);

        // Configuração Visual que você gostou
        L.heatLayer(heatData, {
            radius: 45, 
            blur: 40,   
            maxZoom: 9, 
            gradient: {0.2: 'blue', 0.4: 'lime', 0.6: 'yellow', 1: 'red'}
        }).addTo(map);
        
        if (heatData.length > 1) {
             const bounds = L.latLngBounds(heatData.map(p => [p[0], p[1]]));
             map.fitBounds(bounds);
        }

    } else if (mapDiv) {
        mapDiv.innerHTML = '<div style="display:flex; justify-content:center; align-items:center; height:100%; color:#999;">Sem coordenadas para exibir.</div>';
    }
});