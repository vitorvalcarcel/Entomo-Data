let botaoAtivo = null; 

function abrirModal(btn) {
    botaoAtivo = btn;
    let modal = document.getElementById('modalSelecao');
    atualizarEstadoModal();
    modal.classList.add('ativo');
}

function fecharModal(event) {
    if (!event || event.target.classList.contains('modal-overlay') || event.target.classList.contains('btn-fechar')) {
        document.getElementById('modalSelecao').classList.remove('ativo');
    }
}

function selecionarCampo(valor, nomeAmigavel) {
    if (!botaoAtivo) return;

    let card = botaoAtivo.closest('.card-coluna');
    let inputHidden = card.querySelector('.input-valor-mapeado');

    inputHidden.value = valor;

    if (valor === "") {
        botaoAtivo.innerText = "Selecionar...";
        botaoAtivo.classList.remove('preenchido');
        card.classList.remove('mapeado');
    } else {
        botaoAtivo.innerText = nomeAmigavel;
        botaoAtivo.classList.add('preenchido');
        card.classList.add('mapeado');
    }

    document.getElementById('modalSelecao').classList.remove('ativo');
}

function atualizarEstadoModal() {
    let inputs = document.querySelectorAll('.input-valor-mapeado');
    let valoresUsados = new Set();
    
    inputs.forEach(inp => {
        if (inp.value !== "") {
            valoresUsados.add(inp.value);
        }
    });

    let inputAtual = botaoAtivo.closest('.card-coluna').querySelector('.input-valor-mapeado');
    let meuValor = inputAtual.value;

    let botoesModal = document.querySelectorAll('.btn-opcao');
    
    botoesModal.forEach(btn => {
        let match = btn.getAttribute('onclick').match(/'([^']*)'/);
        if (match) {
            let valorBotao = match[1];
            
            if (valorBotao !== "" && valoresUsados.has(valorBotao) && valorBotao !== meuValor) {
                btn.classList.add('indisponivel'); 
            } else {
                btn.classList.remove('indisponivel');
            }

            if (valorBotao === meuValor && valorBotao !== "") {
                btn.classList.add('selecionado');
            } else {
                btn.classList.remove('selecionado');
            }
        }
    });
}