let connection = new WebSocket("ws://localhost:5555");

let login = document.getElementById("login");
let register = document.getElementById("register");

login != null ? login.disabled = true : null;
register != null ? register.disabled = true : null;

connection.onopen = () => {
    alertify.success("Connected");
    connection.send("Hello, server");
    login != null ? login.disabled = false : null;
    register != null ? register.disabled = false : null;
}

connection.onerror = (error) => {
    console.log(error);
    alertify.error("Connection time out");
}

connection.onmessage = (e) => {
    let msg = JSON.parse(e.data);
    let token;
    if (msg.type == "reg success") {
        alertify.success(msg.message);
        setTimeout(() => window.open("login.html", "_self"), 2000);
    } else if (msg.type == "reg error") {
        alertify.error(msg.message);
    }
    if (msg.type == "login success") {
        token = msg.token;
        alertify.success(msg.message);
        sessionStorage.setItem("token", token);
        setTimeout(() => window.open("chat.html", "_self"), 2000);
    } else if (msg.type == "login error") {
        alertify.error(msg.message);
    }
}