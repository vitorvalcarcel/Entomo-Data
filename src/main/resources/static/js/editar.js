function toggleEdit(checkbox) {
    let inputId = checkbox.getAttribute('data-target-id');
    let input = document.getElementById(inputId);
    
    if (checkbox.checked) {
        input.readOnly = false;
        input.classList.remove('input-travado');
        input.classList.add('input-editavel');
        input.focus();
    } else {
        input.readOnly = true;
        input.classList.remove('input-editavel');
        input.classList.add('input-travado');
    }
}