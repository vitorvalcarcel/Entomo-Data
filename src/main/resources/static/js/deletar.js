let inputSenha = document.querySelector('input[name="senhaConfirmacao"]');

if (inputSenha) {
    inputSenha.addEventListener('focus', function() {
        this.style.borderColor = '#c0392b';
    });
    inputSenha.addEventListener('blur', function() {
        this.style.borderColor = '#ccc';
    });
    inputSenha.addEventListener('input', function() {
        this.value = this.value.replace(/[^0-9]/g, '');
    });
}