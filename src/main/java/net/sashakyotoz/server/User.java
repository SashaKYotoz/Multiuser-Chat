package net.sashakyotoz.server;

public class User {
    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public String getToken() {
        return token;
    }

    private String login;
    private String password;
    private String token;
    private String firstname;
    private String lastname;

    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }
    public User(String login, String password, String token,String firstname, String lastname) {
        this.login = login;
        this.password = password;
        this.token = token;
        this.firstname = firstname;
        this.lastname = lastname;
    }
}
