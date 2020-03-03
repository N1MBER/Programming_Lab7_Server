package ServerSoft;

import Answers.ClientAnswer;
import Answers.ServerAnswer;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class Receiver extends Thread {

    private DatagramSocket socket;
    private ConsoleReader reader;

    public Receiver(DatagramSocket datagramSocket, ConsoleReader consoleReader) {
        this.socket = datagramSocket;
        this.reader = consoleReader;
    }

    @Override
    public void run() {
        while (!isInterrupted()) {
            try {
                byte[] bytes = new byte[16384];
                DatagramPacket datagramPacket = new DatagramPacket(bytes, bytes.length);
                socket.receive(datagramPacket);
//                System.out.println(datagramPacket.getPort());
                ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
                ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
                ClientAnswer clientAnswer = (ClientAnswer) objectInputStream.readObject();
                System.out.println(clientAnswer.getAnswer());
                int port = datagramPacket.getPort();
                new Analyzator(reader, clientAnswer,port);
                byteArrayInputStream.close();
                objectInputStream.close();

            } catch (IOException | ClassNotFoundException e) {
                System.out.println("----\nВозникла ошибка:\n");
                e.printStackTrace();
            }
        }
    }
}
