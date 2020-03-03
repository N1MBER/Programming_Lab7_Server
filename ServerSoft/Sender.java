package ServerSoft;

import Answers.ServerAnswer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Sender extends Thread{

    private DatagramSocket socket;
    //    private ServerAnswer answer;
    private byte[] bytes = new byte[16384];
    private InetAddress clientAddress;
//    private boolean send = false
//    private int port;

    public Sender(DatagramSocket datagramSocket, InetAddress inetAddress){
        this.clientAddress = inetAddress;
        this.socket = datagramSocket;
    }

    @Override
    public void run(){
        while (!isInterrupted()) {

        }
    }

    public void send(ServerAnswer serverAnswer, int port){
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream)) {
            objectOutputStream.writeObject(serverAnswer);
            objectOutputStream.flush();
            bytes = byteArrayOutputStream.toByteArray();
            byteArrayOutputStream.flush();
            DatagramPacket datagramPacket = new DatagramPacket(bytes, bytes.length, clientAddress, port);
            socket.send(datagramPacket);
            System.out.println("----\nПакет отправлен.\n----");
        } catch (IOException e) {
            System.out.println("----\nВозникла ошибка:\n");
            e.printStackTrace();
        }
    }

}
