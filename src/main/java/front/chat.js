let token = sessionStorage.getItem("token");

if (token == null) {
    window.open("login.html", "_self");
}

function logout() {
    sessionStorage.clear();
    window.open("login.html", "_self");
}

let connection = new WebSocket("ws://127.0.0.1:5555/chat");

connection.onopen = () => {
    let tokenAuth = {
        type: "token_auth",
        token: token
    }
    let json = JSON.stringify(tokenAuth);
    connection.send(json);
}

connection.onmessage = (e) => {
    let msg = JSON.parse(e.data);
    console.log(msg);
    if (msg.type == "token_auth") {
        alertify.success(`Hello, ${msg.name}!`);
    } else if (msg.type == "chat_history") {
        let array = msg.messages;
        for (let i = 0; i < array.length; i++) {
            if (array[i].own_msg) {
                createMyMessage(array[i].text,array[i].date);
            } else {
                createMessage(array[i].text,array[i].date,array[i].sender);
            }
        }
        window.scrollTo(0,document.body.scrollHeight);
    }else if(msg.type == "token_error"){
        window.open("login.html", "_self");
    }else if(msg.type == "chat_message"){
        if (msg.own_msg) {
            createMyMessage(msg.text,msg.date);
        } else {
            createMessage(msg.text,msg.date,msg.sender);
        }
    }
}

function sendMessage() {
    let msg = $("#inputMsg").val();
    if (msg == "") return;
    let message = {
        type: "chat_message",
        token: token,
        text: msg
    }

    let json = JSON.stringify(message);
    connection.send(json);
    $("#inputMsg").val("");
    createMyMessage(msg);
}

$("#inputMsg").keydown(function (e) {
    if (e.keyCode == 13) {
        sendMessage();
    }
})

function createMyMessage(text,date) {
    let timeStr;
    let time;
    if (date) {
        timeStr = date.split(" ")[1];
        time = timeStr.split(":")[0] + ":" + timeStr.split(":")[1]
    } else {
        let date = new Date();
        time = String(date.getHours()).padStart(2, "0") + ":" + String(date.getMinutes()).padStart(2, "0");
    }
    let messageContainer = `<li class="message own"><p class="message-text">${text}</p><span class="time">${time}</span></li>`;
    $(".messages-list").append(messageContainer);
    $(".messages-list").scrollTop(99999);
}

function createMessage(text,date,name){
    let message = document.createElement("li");
    message.classList.add("other");
    message.classList.add("message");
    let avatar = document.createElement("div");
    avatar.classList.add("message-avatar");
    let content = document.createElement("div");
    content.classList.add("content");
    let sender = document.createElement("span");
    sender.classList.add("sender");
    let msg = document.createElement("p");
    msg.classList.add("message-text");
    let time = document.createElement("span");
    time.classList.add("time");
    let timeStr = date.split(" ")[1];
    time.textContent = timeStr.split(":")[0] + ":" + timeStr.split(":")[1];
    let nameSplit = name.split(" ");
    if(nameSplit[1] != "null"){
        sender.textContent = name;
    } else{
        sender.textContent = nameSplit[0];
    }
    msg.textContent = text;
    content.appendChild(sender);
    content.appendChild(msg);
    content.appendChild(time);
    if(nameSplit[1] != "null"){
        avatar.textContent = nameSplit[0].charAt(0) + nameSplit[1].charAt(0);
    } else{
        avatar.textContent = nameSplit[0].charAt(0);
    }
    avatar.textContent = name.charAt(0);
    message.appendChild(avatar);
    message.appendChild(content);
    $(".messages-list").append(message);
}