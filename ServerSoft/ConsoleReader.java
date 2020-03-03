package ServerSoft;

import Answers.ServerAnswer;
import Parser.JSONParser;
import PlantsInfo.Plants;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;

public class ConsoleReader {

    private int PORT_CLIENT = 3333;
    private int PORT_SERVER;
    private Sender sender;
    private InetAddress inetAddress;
    private Scanner scanner;
    private DatagramSocket datagramSocket;
    private InetAddress clientAdress;
    private boolean connected = false;
    private boolean portSet = false;

    public ConsoleReader() throws IOException{
        scanner = new Scanner(System.in);
        inetAddress = InetAddress.getByName("localhost");
    }

    public void setConnected(boolean connected){
        this.connected = connected;
    }

    protected void setPort(){
        portSet = false;
        System.out.println("----\nУкажите порт для подключения к серверу\n----");
        while (!portSet){
            try {
                String numb = scanner.nextLine();
                System.out.println("----");
                if (numb.matches("[0-9]+")) {
                    if (Integer.parseInt(numb) < 65535) {
                        PORT_SERVER = Integer.parseInt(numb);
                        datagramSocket = new DatagramSocket(PORT_SERVER);
                        clientAdress = InetAddress.getByName("localhost");
                        portSet = true;
                    }else {
                        System.out.println("----\nНедопустимый номер порта, введите снова\n----");
                        continue;
                    }
                }else {
                    System.out.println("----\nНедопустимый номер порта, введите снова\n----");
                    continue;
                }
            }catch (IOException e){
                System.out.println("----\nНедопустимый номер порта, введите снова\n----");
                continue;
            }
        }

    }


    private void shootDownHook(){
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("I'll be back");
        }));
    }

    public void send(ServerAnswer serverAnswer,int port){
        sender.send(serverAnswer,port);
    }

    public void work() throws InterruptedException{
        System.out.println("----\nСтарт работы.\n----");
        setPort();
        sender = new Sender(datagramSocket,clientAdress);
        sender.start();
        Receiver receiver = new Receiver(datagramSocket,this);
        receiver.setDaemon(true);
        receiver.start();
        shootDownHook();
        while (true){
        }
    }

    private int getBracket(String str,char bracket){
        int count = 0;
        for(char c : str.toCharArray()){
            if (c == bracket)
                count++;
        }
        return count;
    }

    public void setPORT_CLIENT(int port) {
        this.PORT_CLIENT = port;
    }
}
