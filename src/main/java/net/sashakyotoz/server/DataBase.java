package net.sashakyotoz.server;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class DataBase {
    private final String URL = "jdbc:postgresql://localhost:5432/webchat";
    private final String USER = "postgres";
    private final String PASS = "Vdacha2023";

    public void addNewUser(User user) {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASS)) {
            Statement statement = connection.createStatement();
            String sql = String.format(
                    "INSERT INTO users (login, password, token, firstname, lastname) VALUES ('%s', '%s', '%s', '%s', '%s')",
                    user.getLogin(),
                    user.getPassword(),
                    user.getToken(),
                    user.getFirstname(),
                    user.getLastname()
            );
            statement.execute(sql);
            System.out.println("User was added to database");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<User> getAllUsers() {
        ArrayList<User> users = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(URL, USER, PASS)) {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM users");
            while(resultSet.next()) {
                users.add(new User(
                        resultSet.getString(2),
                        resultSet.getString(3),
                        resultSet.getString(4),
                        resultSet.getString(5),
                        resultSet.getString(6)
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    public User getUserByToken(String token) {
        User user = null;
        try (Connection connection = DriverManager.getConnection(URL, USER, PASS)) {
            Statement statement = connection.createStatement();
            String sql = String.format("SELECT * FROM users WHERE token = '%s'", token);
            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                user = new User(
                        resultSet.getString("login"),
                        resultSet.getString("password"),
                        resultSet.getString("token"),
                        resultSet.getString("firstname"),
                        resultSet.getString("lastname")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }

    public ArrayList<Message> getAllMessages() {
        ArrayList<Message> result = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(URL, USER, PASS)) {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM messages");
            while (resultSet.next()) {
                result.add(
                        new Message(
                                resultSet.getString("token"),
                                resultSet.getString("text"),
                                new SimpleDateFormat("dd-MM-yyyy hh:mm:ss").parse(resultSet.getString("date"))
                        )
                );
            }
        } catch (SQLException | ParseException e) {
            e.printStackTrace();
        }
        return result;
    }

    public void addNewMessage(Message message) {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASS)) {
            Statement statement = connection.createStatement();
            String dateStr = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss").format(message.getDate());
            String sql = String.format(
                    "INSERT INTO messages (text, token, date) VALUES ('%s', '%s', '%s')",
                    message.getText(),
                    message.getToken(),
                    dateStr
            );
            statement.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}