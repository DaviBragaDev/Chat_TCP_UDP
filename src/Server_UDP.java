
import java.io.*;
import java.net.*;
public class Server_UDP {
    private static final int PORT = 12345;

    public static void main(String[] args) {
        try (DatagramSocket serverSocket = new DatagramSocket(PORT)) {
            System.out.println("Server running on port " + PORT);
            byte[] receiveData = new byte[1024];

            while (true) {
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                serverSocket.receive(receivePacket);
                String message = new String(receivePacket.getData(), 0, receivePacket.getLength());
                System.out.println("Received message from client: " + message);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
