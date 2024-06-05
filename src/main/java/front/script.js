let passField = document.getElementById("password");
let icon = document.getElementById("icon");
icon.addEventListener("click", ()=>{
    if(passField.getAttribute("type") == "password"){
        icon.classList.remove("fa-regular");
        icon.classList.add("fa-solid");
        passField.setAttribute("type","text");
    }else{
        icon.classList.add("fa-regular");
        icon.classList.remove("fa-solid");
        passField.setAttribute("type","password");
    }
})

let confirmPasswordField = document.getElementById("cpass");
let icon1 = document.getElementById("icon1");
icon1?.addEventListener("click", ()=>{
    if(confirmPasswordField.getAttribute("type") == "password"){
        icon1.classList.remove("fa-regular");
        icon1.classList.add("fa-solid");
        confirmPasswordField.setAttribute("type","text");
    }else{
        icon1.classList.add("fa-regular");
        icon1.classList.remove("fa-solid");
        confirmPasswordField.setAttribute("type","password");
    }
})
