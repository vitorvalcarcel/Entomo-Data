(function() {
    console.log("❤️ Script de Heartbeat carregado e iniciando...");

    setInterval(function() {
        console.log("❤️ Enviando pulso..."); // Log antes de enviar
        
        fetch('/sistema/alive', { 
            method: 'POST',
            keepalive: true,
            headers: {
                'Content-Type': 'application/json'
            }
        })
        .then(response => {
            if (response.ok) {
                console.log("✅ Pulso recebido pelo servidor (200 OK)");
            } else {
                console.error("💔 Servidor rejeitou o pulso: " + response.status);
            }
        })
        .catch(e => {
             console.error("💀 Erro de conexão no Heartbeat (Servidor caiu?):", e);
        });
    }, 2000); // A cada 2 segundos
})();