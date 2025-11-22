document.addEventListener('DOMContentLoaded', function() {
    
    // --- 1. GRÁFICO DE FAMÍLIAS (Doughnut) ---
    const ctxFamilia = document.getElementById('chartFamilia');
    
    if (ctxFamilia && dashboardData.distribuicaoPorFamilia) {
        const labelsFam = Object.keys(dashboardData.distribuicaoPorFamilia);
        const dataFam = Object.values(dashboardData.distribuicaoPorFamilia);

        new Chart(ctxFamilia, {
            type: 'doughnut',
            data: {
                labels: labelsFam,
                datasets: [{
                    data: dataFam,
                    backgroundColor: [
                        '#3498db', '#e74c3c', '#f1c40f', '#2ecc71', '#9b59b6', '#34495e', '#e67e22'
                    ],
                    borderWidth: 1
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: { position: 'right' }
                }
            }
        });
    }

    // --- 2. GRÁFICO DE BIOMAS (Barra) ---
    const ctxBioma = document.getElementById('chartBioma');
    
    if (ctxBioma && dashboardData.distribuicaoPorBioma) {
        const labelsBioma = Object.keys(dashboardData.distribuicaoPorBioma);
        const dataBioma = Object.values(dashboardData.distribuicaoPorBioma);

        new Chart(ctxBioma, {
            type: 'bar',
            data: {
                labels: labelsBioma,
                datasets: [{
                    label: 'Exemplares',
                    data: dataBioma,
                    backgroundColor: '#2ecc71',
                    borderRadius: 4
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                scales: {
                    y: { beginAtZero: true }
                }
            }
        });
    }

    // --- 3. MAPA DE CALOR (Leaflet) ---
    const mapDiv = document.getElementById('mapaCalor');
    if (mapDiv && dashboardData.coordenadas && dashboardData.coordenadas.length > 0) {
        
        // Converte os dados do Java para o formato do Leaflet.heat [lat, lng, intensity]
        const heatData = dashboardData.coordenadas.map(p => [p.lat, p.lng, 1]); // Intensidade 1 por ponto

        // Centraliza o mapa no primeiro ponto ou no Brasil (-14, -51)
        const startLat = heatData[0][0] || -14.235;
        const startLng = heatData[0][1] || -51.925;

        const map = L.map('mapaCalor').setView([startLat, startLng], 4);

        // Adiciona camada base (OpenStreetMap)
        // Nota: Para uso 100% offline, você precisaria de tiles locais ou aceitar que o mapa base precisa de internet.
        // O heatmap renderiza por cima.
        L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
            attribution: '&copy; OpenStreetMap contributors',
            maxZoom: 18
        }).addTo(map);

        // Adiciona camada de calor
        L.heatLayer(heatData, {
            radius: 25,
            blur: 15,
            maxZoom: 10,
            gradient: {0.4: 'blue', 0.65: 'lime', 1: 'red'}
        }).addTo(map);
        
        // Ajusta o zoom para caber todos os pontos
        if (heatData.length > 1) {
             const bounds = L.latLngBounds(heatData.map(p => [p[0], p[1]]));
             map.fitBounds(bounds);
        }
    } else if (mapDiv) {
        mapDiv.innerHTML = '<div style="display:flex; justify-content:center; align-items:center; height:100%; color:#999;">Sem coordenadas cadastradas para exibir o mapa.</div>';
    }
});