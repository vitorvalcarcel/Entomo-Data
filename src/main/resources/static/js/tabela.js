let delayTimer;

function filtrar(pagina) {
    clearTimeout(delayTimer);
    delayTimer = setTimeout(function() {
        enviarRequisicao(pagina);
    }, 300);
}

function ordenar(elemento) {
    let campo = elemento.getAttribute('data-field');
    let currentField = document.getElementById('sortField').value;
    let currentDir = document.getElementById('sortDir').value;
    
    let newDir = 'asc';
    if (campo === currentField) {
        newDir = (currentDir === 'asc') ? 'desc' : 'asc';
    }
    
    document.getElementById('sortField').value = campo;
    document.getElementById('sortDir').value = newDir;
    
    filtrar(0);
}

function enviarRequisicao(pagina) {
    let size = document.getElementById('sizeSelect').value;
    let sort = document.getElementById('sortField').value;
    let dir = document.getElementById('sortDir').value;
    
    let inputs = document.querySelectorAll('#painelFiltros .filtro-input');
    let params = new URLSearchParams();
    params.append('page', pagina);
    params.append('size', size);
    params.append('sort', sort);
    params.append('dir', dir);

    for (let input of inputs) {
        if (input.value) {
            params.append(input.name, input.value);
        }
    }

    fetch('/filtrar?' + params.toString())
        .then(response => response.text())
        .then(html => {
            let areaTabela = document.getElementById('area-tabela');
            if (areaTabela) {
                areaTabela.outerHTML = html;
                sincronizarRolagem();
            }
        });
}

function mudarTamanho() {
    filtrar(0);
}

function toggleFiltros() {
    let painel = document.getElementById('painelFiltros');
    let btn = document.getElementById('btnFiltros');
    if (painel.style.display === 'none' || painel.style.display === '') {
        painel.style.display = 'block';
        btn.classList.add('ativo');
        btn.innerHTML = '<span>▼</span> Ocultar Filtros';
    } else {
        painel.style.display = 'none';
        btn.classList.remove('ativo');
        btn.innerHTML = '<span>🔍</span> Filtrar Dados';
    }
}

function toggleInput(checkbox) {
    let container = checkbox.closest('.item-filtro');
    let input = container.querySelector('.filtro-input');
    if (checkbox.checked) {
        input.style.display = 'block';
        input.focus();
    } else {
        input.style.display = 'none';
        input.value = '';
        filtrar(0);
    }
}

function sincronizarRolagem() {
    let topo = document.getElementById('scrollTopWrapper');
    let conteudoTopo = document.getElementById('scrollTopContent');
    let tabelaContainer = document.querySelector('.tabela-container');
    let tabela = document.querySelector('table');

    if (!topo || !tabelaContainer || !tabela) return;
    conteudoTopo.style.width = tabela.offsetWidth + 'px';
    topo.onscroll = function() { tabelaContainer.scrollLeft = topo.scrollLeft; };
    tabelaContainer.onscroll = function() { topo.scrollLeft = tabelaContainer.scrollLeft; };
}

function abrirModalExclusao(cod) {
    let modal = document.getElementById('modalExclusaoIndividual');
    let spanCod = document.getElementById('codExclusao');
    let btnConfirmar = document.getElementById('btnConfirmarExclusao');
    
    spanCod.innerText = cod;
    btnConfirmar.href = "/deletar/" + cod;
    
    modal.classList.add('ativo');
}

function fecharModalExclusao() {
    document.getElementById('modalExclusaoIndividual').classList.remove('ativo');
}

window.onload = function() {
    sincronizarRolagem();
    let inputs = document.querySelectorAll('.filtro-input');
    inputs.forEach(input => {
        if (input.value.trim() !== '') {
            input.style.display = 'block';
            let checkbox = input.parentElement.querySelector('input[type="checkbox"]');
            if(checkbox) checkbox.checked = true;
            document.getElementById('painelFiltros').style.display = 'block';
            document.getElementById('btnFiltros').classList.add('ativo');
        }
    });
};