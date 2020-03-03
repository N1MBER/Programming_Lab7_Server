package ServerSoft;

import Answers.ClientAnswer;
import Answers.ServerAnswer;

public class Analyzator {

    private ClientAnswer answer;
    private int clientport;
    private ConsoleReader reader;

    public Analyzator(ConsoleReader consoleReader,ClientAnswer clientAnswer, int port){
        this.answer = clientAnswer;
        this.reader = consoleReader;
        this.clientport = port;
        analyz(reader,answer);
    }

    public void analyz(ConsoleReader reader, ClientAnswer answer){
        try {
            switch (answer.getAnswer()){
                case "CONNECT":
                    reader.setConnected(true);
                    reader.send(new ServerAnswer("", "CONNECT"),clientport);
                    Thread.sleep(1000);
                    reader.send(new ServerAnswer(new DataBaseManager(reader).help(), "HELP"),clientport);
                    break;
                case "ADD":
                    reader.send(new ServerAnswer(new DataBaseManager(reader).add(answer), "ADD"),clientport);
                    new UserManager(reader).setPort(answer,clientport);
                    break;
                case "ADD_IF_MAX":
                    reader.send(new ServerAnswer(new DataBaseManager(reader).add_if_max(answer), "ADD_IF_MAX"),clientport);
                    new UserManager(reader).setPort(answer,clientport);
                    break;
                case "REMOVE":
                    reader.send(new ServerAnswer(new DataBaseManager(reader).remove(answer), "REMOVE"),clientport);
                    new UserManager(reader).setPort(answer,clientport);
                    break;
                case "IMPORT":
                    reader.send(new ServerAnswer(new DataBaseManager(reader).importCollection(answer),"IMPORT"),clientport);
                    new UserManager(reader).setPort(answer,clientport);
                    break;
                case "REMOVE_LOWER":
                    reader.send(new ServerAnswer(new DataBaseManager(reader).remove_lower(answer), "REMOVE_LOWER"),clientport);
                    new UserManager(reader).setPort(answer,clientport);
                    break;
                case "REMOVE_GREATER":
                    reader.send(new ServerAnswer(new DataBaseManager(reader).remove_greater(answer), "REMOVE_GREATER"),clientport);
                    new UserManager(reader).setPort(answer,clientport);
                    break;
                case "INFO":
                    reader.send(new ServerAnswer(new DataBaseManager(reader).info(answer), "INFO"),clientport);
                    new UserManager(reader).setPort(answer,clientport);
                    break;
                case "SHOW":
                    reader.send(new ServerAnswer(new DataBaseManager(reader).show(answer), "SHOW"),clientport);
                    new UserManager(reader).setPort(answer,clientport);
                    break;
                case "REGISTER":
                    new UserManager(reader).addUser(answer,clientport);
                    break;
                case "HELP":
                    new UserManager(reader).setPort(answer,clientport);
                    reader.send(new ServerAnswer(new DataBaseManager(reader).help(), "HELP"),clientport);
                    break;
                default:
                    System.out.println("----\nНераспознанный ответ.\n----");
                    break;
            }
        }catch (InterruptedException e) {
            System.out.println("----\nВозникла ошибка:");
            e.printStackTrace();
        }
    }
}
