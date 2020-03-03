package ServerSoft;

import Answers.ClientAnswer;
import PlantsInfo.Plants;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.concurrent.ConcurrentSkipListSet;

public class DataBaseManager {

    private static String URL = "jdbc:postgresql://localhost:5432/lab7";
    private static String USER = "n1mber";
    private static String PASSWORD = "SocialKmax2512";

    private ConsoleReader consoleReader;

    public DataBaseManager(ConsoleReader consoleReader){
        this.consoleReader = consoleReader;
    }

    public String add(ClientAnswer clientAnswer){
        String answer ="";
        String query = "INSERT INTO plants (name,character,place,x,y,z,time,userid) VALUES (?,?,?,?,?,?,?,?);";
        int id = new UserManager(consoleReader).getUserID(clientAnswer.getLogin(),clientAnswer.getPassword());
        if (id == 0){
            answer += "----\nНеверно указаны логин или пароль, работа невозможна.\n----";
        }else {
            try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, clientAnswer.getPlant().getName());
                preparedStatement.setString(2, clientAnswer.getPlant().getCharicter());
                preparedStatement.setString(3, clientAnswer.getPlant().getLocation());
                preparedStatement.setInt(4, clientAnswer.getPlant().getX());
                preparedStatement.setInt(5, clientAnswer.getPlant().getY());
                preparedStatement.setInt(6, clientAnswer.getPlant().getZ());
                preparedStatement.setString(7, clientAnswer.getPlant().getTime());
                preparedStatement.setInt(8, id);
                int result = preparedStatement.executeUpdate();
                if (result > 0)
                    answer += "----\nЭлемент успешно добавлен.\n----";
                else
                    answer += "----\nПодобный элемент существует.\n----";
            } catch (SQLException e) {
                e.printStackTrace();
                answer += "----\nВозникла ошибка.\n----";
            }
        }
        return answer;
    }

    public String importCollection(ClientAnswer clientAnswer){
        ConcurrentSkipListSet<Plants> concurrentSkipListSet = clientAnswer.getPlants();
        String answer = "";
        int count = 0;
        String query = "INSERT INTO plants (name,character,place,x,y,z,time,userid) VALUES (?,?,?,?,?,?,?,?);";
        int id = new UserManager(consoleReader).getUserID(clientAnswer.getLogin(),clientAnswer.getPassword());
        if (id == 0){
            answer += "----\nНеверно указаны логин или пароль, работа невозможна.\n----";
        }else {
            try (Connection connection = DriverManager.getConnection(URL,USER,PASSWORD)){
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                Iterator<Plants> iterator = concurrentSkipListSet.iterator();
                while (iterator.hasNext()){
                    preparedStatement.setString(1,iterator.next().getName());
                    preparedStatement.setString(2,iterator.next().getCharicter());
                    preparedStatement.setString(3,iterator.next().getLocation());
                    preparedStatement.setInt(4,iterator.next().getX());
                    preparedStatement.setInt(5,iterator.next().getY());
                    preparedStatement.setInt(6,iterator.next().getZ());
                    preparedStatement.setString(7,iterator.next().getTime());
                    preparedStatement.setInt(8,id);
                    preparedStatement.executeUpdate();
                    count++;
                }
                if (count > 0){
                    answer += ("----\nУспешно добавлено " + count + " элементов.\n----");
                }
            }catch (SQLException e){
                e.printStackTrace();
                answer += "----\nВозникла ошибка.\n----";
            }
        }
        return answer;
    }

    public String remove(ClientAnswer clientAnswer){
        String answer ="";
        String query = "DELETE FROM plants WHERE name=? AND character=? AND place=? AND userid=?;";
        int id = new UserManager(consoleReader).getUserID(clientAnswer.getLogin(),clientAnswer.getPassword());
        if (id == 0){
            answer += "----\nНеверно указаны логин или пароль, работа невозможна.\n----";
        }else{
            try(Connection connection = DriverManager.getConnection(URL,USER,PASSWORD)){
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1,clientAnswer.getPlant().getName());
                preparedStatement.setString(2,clientAnswer.getPlant().getCharicter());
                preparedStatement.setString(3,clientAnswer.getPlant().getLocation());
                preparedStatement.setInt(4, id);
                int result = preparedStatement.executeUpdate();
                if (result == 1)
                    answer += "----\nЭлемент успешно удалён.\n----";
                else
                    if (result == 0)
                        answer += "----\nДанный элемент отсутствует в базе данных.\n----";
                    else
                        answer += "----\nЭлементы успешно удалены.\n----";
            }catch (SQLException e){
                e.printStackTrace();
                answer += "----\nВозникла ошибка.\n----";
            }
        }
        return answer;
    }

    public String remove_lower(ClientAnswer clientAnswer){
        String answer = "";
        String query1 = "SELECT * FROM plants WHERE userid=?;";
        String query2 = "DELETE FROM plants WHERE name=? AND character=? AND place=? AND userid=?;";
        int count = 0;
        int id = new UserManager(consoleReader).getUserID(clientAnswer.getLogin(),clientAnswer.getPassword());
        if(id == 0){
            answer += "----\nНеверно указаны логин или пароль, работа невозможна.\n----";
        }else{
            try(Connection connection = DriverManager.getConnection(URL,USER,PASSWORD)){
                PreparedStatement preparedStatement = connection.prepareStatement(query1);
                preparedStatement.setInt(1,id);
                ResultSet resultSet = preparedStatement.executeQuery();
                while (resultSet.next()){
                    if (!((clientAnswer.getPlant().getName().compareTo(resultSet.getString("name"))== 0) &
                            (clientAnswer.getPlant().getCharicter().compareTo(resultSet.getString("character"))==0)&
                            (clientAnswer.getPlant().getLocation().compareTo(resultSet.getString("place")))==0)){
                        if (clientAnswer.getPlant().getCharicter().compareTo(resultSet.getString("character")) > 0){
                           preparedStatement = connection.prepareStatement(query2);
                           preparedStatement.setString(1 , resultSet.getString("name"));
                           preparedStatement.setString(2, resultSet.getString("character"));
                           preparedStatement.setString(3, resultSet.getString("place"));
                           preparedStatement.executeUpdate();
                           count++;
                        }
                    }
                }
            }catch (SQLException e){
                e.printStackTrace();
                answer += "----\nВозникла ошибка.\n----";
            }
            if(count>0){
                answer += "----\nУдалено " + count +" элементов\n----";
            }
        }
        return answer;
    }

    public String remove_greater(ClientAnswer clientAnswer){
        String answer = "";
        String query1 = "SELECT * FROM plants WHERE userid=?;";
        String query2 = "DELETE FROM plants WHERE name=? AND character=? AND place=? AND userid=?;";
        int count = 0;
        int id = new UserManager(consoleReader).getUserID(clientAnswer.getLogin(),clientAnswer.getPassword());
        if(id == 0){
            answer += "----\nНеверно указаны логин или пароль, работа невозможна.\n----";
        }else{
            try(Connection connection = DriverManager.getConnection(URL,USER,PASSWORD)){
                PreparedStatement preparedStatement = connection.prepareStatement(query1);
                preparedStatement.setInt(1,id);
                ResultSet resultSet = preparedStatement.executeQuery();
                while (resultSet.next()){
                    if (!((clientAnswer.getPlant().getName().compareTo(resultSet.getString("name"))== 0) &
                            (clientAnswer.getPlant().getCharicter().compareTo(resultSet.getString("character"))==0)&
                            (clientAnswer.getPlant().getLocation().compareTo(resultSet.getString("place")))==0)){
                        if (clientAnswer.getPlant().getCharicter().compareTo(resultSet.getString("character")) < 0){
                            preparedStatement = connection.prepareStatement(query2);
                            preparedStatement.setString(1 , resultSet.getString("name"));
                            preparedStatement.setString(2, resultSet.getString("character"));
                            preparedStatement.setString(3, resultSet.getString("place"));
                            preparedStatement.executeUpdate();
                            count++;
                        }
                    }
                }
            }catch (SQLException e){
                e.printStackTrace();
                answer += "----\nВозникла ошибка.\n----";
            }
            if(count>0){
                answer += "----\nУдалено " + count +" элементов\n----";
            }
        }
        return answer;
    }

    public String show(ClientAnswer clientAnswer){
        int id = new UserManager(consoleReader).getUserID(clientAnswer.getLogin(),clientAnswer.getPassword());
        String answer = ("----\nВаш id:" + id + "\n");
        String query = "SELECT * FROM plants;";
        if (id == 0){
            answer = "----\nНеверно указаны логин или пароль, работа невозможна.\n----";
        }else {
            try (Connection connection = DriverManager.getConnection(URL,USER,PASSWORD)){
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                ResultSet resultSet = preparedStatement.executeQuery();
//                if (resultSet.next()) {
                    while (resultSet.next()) {
                        String hit ="";
                        if (resultSet.getInt("userid") == id)
                            hit = "✓";
                        else
                            hit = "-";
                        answer += "\n [" + hit +" id:" + resultSet.getInt("userid") + ", name:" + resultSet.getString("name");
                        answer += "]\n";
                    }
//                }else
//                    answer= "[]";
                answer += "\n----";
            }catch (SQLException e){
                e.printStackTrace();
                answer = "----\nВозникла ошибка.\n----";
            }
        }
        return answer;
    }

    public String info(ClientAnswer clientAnswer){
        String answer = "";
        String query = "SELECT * FROM plants WHERE userid=?;";
        int id = new UserManager(consoleReader).getUserID(clientAnswer.getLogin(),clientAnswer.getPassword());
        int count = 0;
        if (id == 0){
            answer += "----\nНеверно указаны логин или пароль, работа невозможна.\n----";
        }else {
            try(Connection connection = DriverManager.getConnection(URL,USER,PASSWORD)){
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setInt(1,id);
                ResultSet resultSet = preparedStatement.executeQuery();
                while (resultSet.next()){
                    count++;
                }
                answer += ("----\n Информация о базе данных:\n----\n" +
                        "\t Тип: CopyOnWriteArraySet\n" +
                        "\tСодержимое: экземпляры класса Plants\n" +
                        "\tДата: " + LocalDateTime.now().toString() +
                        "\n\tРазмер: " + count +
                        "\n----");
            }catch (SQLException e){
                e.printStackTrace();
                answer = "----\nВозникла ошибка.\n----";
            }
        }
        return answer;
    }

    public String add_if_max(ClientAnswer clientAnswer){
        String answer ="";
        String query1 = "SELECT * FROM plants WHERE userid=?;";
        String query2 = "INSERT INTO plants (name,character,place,x,y,z,time,userid) VALUES (?,?,?,?,?,?,?,?);";
        boolean add = true;
        int id = new UserManager(consoleReader).getUserID(clientAnswer.getLogin(),clientAnswer.getPassword());
        if (id == 0){
            answer += "----\nНеверно указаны логин или пароль, работа невозможна.\n----";
        }else {
            try(Connection connection = DriverManager.getConnection(URL,USER,PASSWORD)){
                PreparedStatement preparedStatement = connection.prepareStatement(query1);
                preparedStatement.setInt(1,id);
                ResultSet resultSet = preparedStatement.executeQuery();
                while (resultSet.next()){
                    if(!((clientAnswer.getPlant().getName().compareTo(resultSet.getString("name"))== 0) &
                            (clientAnswer.getPlant().getCharicter().compareTo(resultSet.getString("character"))==0)&
                            (clientAnswer.getPlant().getLocation().compareTo(resultSet.getString("place")))==0)){
                        if (clientAnswer.getPlant().getCharicter().compareTo(resultSet.getString("character")) <= 0){
                            add = false;
                            break;
                        }
                    }
                }
                if (add){
                    PreparedStatement preparedStatement1 = connection.prepareStatement(query2);
                    preparedStatement1.setString(1, clientAnswer.getPlant().getName());
                    preparedStatement1.setString(2, clientAnswer.getPlant().getCharicter());
                    preparedStatement1.setString(3, clientAnswer.getPlant().getLocation());
                    preparedStatement1.setInt(4, clientAnswer.getPlant().getX());
                    preparedStatement1.setInt(5, clientAnswer.getPlant().getY());
                    preparedStatement1.setInt(6, clientAnswer.getPlant().getZ());
                    preparedStatement1.setString(7, clientAnswer.getPlant().getTime());
                    preparedStatement1.setInt(8, id);
                    preparedStatement1.executeUpdate();
                    answer += "----\nЭлемент успешно добавлен.\n----";
                }
                else
                    answer +="----\nЭлемент не был добавлен.\n----";
            }catch (SQLException e){
                e.printStackTrace();
                answer = "----\nВозникла ошибка.\n----";
            }
        }
        return answer;
    }

    public String help(){
        return ("----\nСписок доступных команд:\n" +
                "1. help: показать доступные команды.\n" +
                "2. remove {element}: удалить элемент из коллекции по его значению.\n" +
                "3. info: вывести в стандартный поток вывода информацию о коллекции (тип, дата инициализации, количество элементов и т.д.), элемент должен быть введен в формате json.\n" +
                "4. remove_lower {element}: удалить из коллекции все элементы, меньше заданного, элемент должен быть введён в формате json.\n" +
                "5. add {element}: добавить новый элемент в коллекцию, элемент должен быть введён в формате json.\n" +
                "6. add_if_max {element}: добавить новый элемент в коллекцию, если он превышает максимальный, элемент должен быть введён в формате json.\n" +
                "7. remove_greater {element}: удалить из коллекции все элементы, превышающие заданный, элемент должен быть введён в формате json.\n" +
                "8. show: вывести в стандартный поток вывода все элементы коллекции в строковом представлении.\n" +
                "9. exit: закончить работу.\n" +
                "10. import: загрузка необходимых данных с клиента.\n" +
                "11. register: регистрация для работы с приложением.\n" +
                "Пример элемента ввода:\n" +
                "{\n" +
                "  \"name\": \"Ромашка\",\n" +
                "  \"characteristic\": \"White\",\n" +
                "  \"location\":{\n" +
                "  \t\"namelocation\": \"Поле\"\n" +
                "  }\n" +
                "}\n" +
                "----");
    }



}
