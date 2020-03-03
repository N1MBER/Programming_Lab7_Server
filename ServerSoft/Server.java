package ServerSoft;

import java.io.IOException;

public class Server {
    public static void main(String[] args){
        try {
            ConsoleReader consoleReader = new ConsoleReader();
            consoleReader.work();
        }catch (IOException |InterruptedException e){
            System.out.println("----\nВозникла ошибка:\n");
            e.printStackTrace();
        }
    }
}
