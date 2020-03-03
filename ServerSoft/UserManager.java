package ServerSoft;

import Answers.ClientAnswer;
import Answers.ServerAnswer;

import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Random;
//import javax.*;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class UserManager {

    private static String URL = "jdbc:postgresql://localhost:5432/lab7";
    private static String USER = "n1mber";
    private static String PASSWORD = "SocialKmax2512";

    private ConsoleReader consoleReader;

    public UserManager(ConsoleReader consoleReader){
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("PostgreSQL JDBC Driver is not found. Include it in your library path ");
            e.printStackTrace();
        }
        this.consoleReader = consoleReader;
    }

//    public String register(ClientAnswer clientAnswer){
//        String query ="";
//        try(Connection connection)
//    }

    public void sendFromMail(String to, String newPass) {
        try {
            final Properties properties = new Properties();
            properties.load(UserManager.class.getClassLoader().getResourceAsStream("Mail.properties"));
            Session session = Session.getDefaultInstance(properties);
            MimeMessage mimeMessage = new MimeMessage(session);
            mimeMessage.setFrom(new InternetAddress("testmaxlab7@gmail.com"));
            mimeMessage.addRecipients(Message.RecipientType.TO , to);
            mimeMessage.setSubject("Hello! Here your password");
            mimeMessage.setText("----\nВаш пароль:" + newPass);

            Transport transport = session.getTransport();
            transport.connect(null, "dofxu1-koqxYt-qizmuv");
            transport.sendMessage(mimeMessage,mimeMessage.getAllRecipients());
            transport.close();
        }
        catch (MessagingException | IOException e){
            System.out.println("----\nВозникла ошибка:");
            e.printStackTrace();
        }
    }

    private String passwordGenerator() {
        Random random = new Random();
        String passwordAlphabet = "QWERTYUIOPASDFGHJKLZXCVBNMqwertyuiopasdfghjklzxcvbnm1234567890";
        char[] resultArr = new char[6];
        for (int i = 0; i < resultArr.length; i++)
            resultArr[i] = passwordAlphabet.charAt(random.nextInt(passwordAlphabet.length()));

        return new String(resultArr);
    }


    public void addUser(ClientAnswer clientAnswer,int port){
        String query = "SELECT * from users;";
        String query1 = "INSERT INTO users (id, login, password, usersport) VALUES (?,?,?,?)";
        int id = (int)clientAnswer.hashCode()/10000;
        boolean miss = false;
        try(Connection connection = DriverManager.getConnection(URL,USER,PASSWORD)){
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()){
                if (resultSet.getString("login").equals(clientAnswer.getLogin())) {
                    consoleReader.send(new ServerAnswer("----\nПодобный логин уже зарегестрирован,\n" +
                            "введите новый логин или введите пароль от данного логина.\n----", "LOGINMISS"),port);
                    miss = true;
                }
                else {
                    if (id == resultSet.getInt("id"))
                        id = (int) Math.random() * 100000;
                    System.out.println("sis");
                }
            }
            if (!miss) {
                String password = passwordGenerator();
                sendFromMail(clientAnswer.getLogin(),password);
                PreparedStatement preparedStatement1 = connection.prepareStatement(query1);
                preparedStatement1.setInt(1, id);
                preparedStatement1.setString(2, clientAnswer.getLogin());
                preparedStatement1.setString(3, SHA512(password));
                preparedStatement1.setInt(4,port);
                preparedStatement1.executeUpdate();
                consoleReader.send(new ServerAnswer("----\nУчётная запись успешно добавлена.\n----","LOGINHIT"),port);
            }

        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    public int getPort(String login, String password){
        String query = "SELECT * FROM users WHERE login=? AND password=?;";
        int port = 0;
        try (Connection connection = DriverManager.getConnection(URL,USER,PASSWORD)){
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1,login);
            preparedStatement.setString(2,SHA512(password));
            ResultSet resultSet = preparedStatement.executeQuery();
            port = resultSet.getInt("usersport");
        }catch (SQLException e){
            e.printStackTrace();
        }
        return port;
    }

    public void setPort(ClientAnswer clientAnswer,int port){
        String query = "UPDATE users SET usersport=? WHERE login=? AND password=?;";
        try(Connection connection = DriverManager.getConnection(URL,USER,PASSWORD)){
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1,port);
            preparedStatement.setString(2,clientAnswer.getLogin());
            preparedStatement.setString(3, SHA512(clientAnswer.getPassword()));
            preparedStatement.executeUpdate();
        }catch (SQLException e){
            System.out.println("----\nВозникла ошибка:");
            e.printStackTrace();
        }
    }

    public int getUserID(String login, String password){
        String query = "SELECT * FROM users WHERE login=? AND password=?;";
        int id = 0;
        try(Connection connection = DriverManager.getConnection(URL,USER,PASSWORD)){
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1,login);
            preparedStatement.setString(2,SHA512(password));
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next())
                id = resultSet.getInt("id");
        }catch (SQLException e){
            System.out.println("----\nВозникла ошибка:");
            e.printStackTrace();
        }
        return id;
    }

    public String SHA512(String pass){
        String answer = pass;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            byte[] messageDigest = md.digest(pass.getBytes());
            BigInteger no = new BigInteger(1, messageDigest);
            String hashtext = no.toString(16);

            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }
            return hashtext;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }catch (NullPointerException e){
        }
        return answer;
    }
}
