package net.sashakyotoz.server;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.mindrot.jbcrypt.BCrypt;

import java.net.InetSocketAddress;
import java.text.SimpleDateFormat;
import java.util.*;

public class Server extends WebSocketServer {
    private static final int PORT = 5555;
    private Set<WebSocket> cons;
    private DataBase db;
    private ArrayList<User> users;
    private ArrayList<Message> messages;

    public Server() {
        super(new InetSocketAddress(PORT));
        cons = new HashSet<>();
        db = new DataBase();
        users = db.getAllUsers();
        messages = db.getAllMessages();
    }

    @Override
    public void onOpen(WebSocket webSocket, ClientHandshake clientHandshake) {
        System.out.println(messages);
        cons.add(webSocket);
        System.out.println(
                "New connection from " +
                        webSocket
                                .getLocalSocketAddress()
                                .getAddress()
                                .getHostAddress()
        );
    }

    @Override
    public void onClose(WebSocket webSocket, int i, String s, boolean b) {
        cons.remove(webSocket);
        System.out.println(
                "Closed connection from " +
                        webSocket
                                .getLocalSocketAddress()
                                .getAddress()
                                .getHostAddress()
        );
    }

    @Override
    public void onMessage(WebSocket webSocket, String s) {
        JSONObject object = JSON.parseObject(s);
        String type = object.getString("type");
        String name = object.getString("name");
        String firstname = object.getString("firstname");
        String lastname = object.getString("lastname");
        String pass = object.getString("pass");
        JSONObject answer = new JSONObject();
        switch (type) {
            case "reg" -> {
                if (getUserByLogin(name) == null) {
                    String token = generateToken();
                    String hashPass = hashPassword(pass);
                    User user = new User(name, hashPass, token,firstname,lastname);
                    users.add(user);
                    db.addNewUser(user);

                    answer.put("type", "reg success");
                    answer.put("message", "Success. Please, log in now");
                } else {
                    answer.put("type", "reg error");
                    answer.put("message", "User already exists");
                }
                webSocket.send(answer.toString());
            }
            case "login" -> {
                User user = getUserByLogin(name);
                if (user != null) {
                    if (verifyPassword(pass, user.getPassword())) {
                        answer.put("type", "login success");
                        answer.put("message", "Login completed successfully");
                        answer.put("token", user.getToken());
                    } else {
                        answer.put("type", "login error");
                        answer.put("message", "Password invalid");
                    }
                } else {
                    answer.put("type", "login error");
                    answer.put("message", "Username invalid");
                }
                webSocket.send(answer.toString());
            }
            case "token_auth" -> {
                String token = object.getString("token");
                User user = db.getUserByToken(token);
                if (user != null){
                    answer.put("type", "token_auth");
                    answer.put("name", user.getLogin());
                }else{
                    answer.put("type", "token_error");
                }
                webSocket.send(answer.toString());
                String allMessagesStr = createJSONFromAllMessages(token);
                webSocket.send(allMessagesStr);
            }
            case "chat_message" -> {
                String text = object.getString("text");
                String token = object.getString("token");

                User user = db.getUserByToken(token);
                if (user == null) return;

                Message msg = new Message(token, text, new Date());
                messages.add(msg);
                db.addNewMessage(msg);

                JSONObject sendMessage = new JSONObject();
                sendMessage.put("type", "chat_message");
                sendMessage.put("own_msg", false);
                sendMessage.put("text", text);
                sendMessage.put("sender", user.getFirstname() + " "  + user.getLastname());
                sendMessage.put("date", new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(msg.getDate()));
                for (WebSocket socket : cons) {
                    if (!socket.equals(webSocket)) {
                        socket.send(sendMessage.toString());
                    }
                }
                answer.put("type", "sending_result");
                answer.put("result", "Success");
                webSocket.send(answer.toString());
            }
        }
    }

    public String hashPassword(String plainPassword) {
        String salt = BCrypt.gensalt();
        return BCrypt.hashpw(plainPassword, salt);
    }

    public boolean verifyPassword(String plainPassword, String hashPassword) {
        return BCrypt.checkpw(plainPassword, hashPassword);
    }

    public String createJSONFromAllMessages(String token) {
        JSONObject tempObj = new JSONObject();
        tempObj.put("type", "chat_history");
        JSONArray array = new JSONArray();
        for (Message msg : messages) {
            JSONObject temp = new JSONObject();
            if (token.equals(msg.getToken())) {
                temp.put("own_msg", true);
            } else {
                temp.put("own_msg", false);
            }
            temp.put("text", msg.getText());
            User user = db.getUserByToken(msg.getToken());
            temp.put("sender", user.getFirstname() + " "  + user.getLastname());
            temp.put("date", new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(msg.getDate()));
            array.add(temp);
        }
        tempObj.put("messages", array);
        return tempObj.toString();
    }

    public User getUserByLogin(String login) {
        for (User user : users) {
            if (user.getLogin().equals(login)) return user;
        }
        return null;
    }

    public String generateToken() {
        StringBuilder sb = new StringBuilder();
        String symbols = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        Random random = new Random();
        for (int i = 0; i < 16; i++) {
            sb.append(symbols.charAt(random.nextInt(symbols.length())));
        }
        return sb.toString();
//        return new Random().ints(16, 0, 62)
//                .mapToObj("0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"::charAt)
//                .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
//                .toString();
    }

    @Override
    public void onError(WebSocket webSocket, Exception e) {
    }

    @Override
    public void onStart() {
    }
}