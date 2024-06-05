let loginForm = document.getElementById("login-form");
let registerForm = document.getElementById("register-form");

if (sessionStorage.getItem("token") != null) {
    window.open("chat.html", "__self");
}

loginForm?.addEventListener("submit", (event) => {
    event.preventDefault();
    if (event.target["username"].value && event.target["pass"].value) {
        let user = {
            "type": "login",
            "name": event.target["username"].value,
            "pass": event.target["pass"].value
        }
        let json = JSON.stringify(user);
        connection.send(json);
    } else {
        alertify.error("Write login and password")
    }
})

registerForm?.addEventListener("submit", (event) => {
    event.preventDefault();
    let confirmPassword = event.target["cpass"].value;
    let password = event.target["password"].value;
    let username = event.target["username"].value;
    let firstname = event.target["firstname"].value;
    let lastname = event.target["lastname"].value;
    if (username && password && firstname) {
        if (password != confirmPassword) {
            alertify.error("Passwords not match");
        } else {
            let user = {
                "type": "reg",
                "name": username,
                "pass": password,
                "firstname": firstname,
                "lastname": lastname

            }
            let json = JSON.stringify(user);
            connection.send(json);
        }
    } else {
        alertify.error("Write login and password")
    }
})