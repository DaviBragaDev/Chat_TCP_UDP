
import java.io.*;
import java.net.*;
public class Client_UDP {
    private static final String SERVER_IP = "127.0.0.1"; // IP do servidor
    private static final int SERVER_PORT = 12345; // Porta do servidor

    public static void main(String[] args) {
        try (DatagramSocket clientSocket = new DatagramSocket()) {
            long startTime = System.nanoTime();

            int tamanho = 1024 * 60;
            byte[] sendData = new byte[tamanho];
            InetAddress serverAddress = InetAddress.getByName(SERVER_IP);
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, serverAddress, SERVER_PORT);
            clientSocket.send(sendPacket);

            long endTime = System.nanoTime();
            double elapsedTimeSeconds = (endTime - startTime) / 1e9;
            System.out.println("Time taken to send message over UDP: " + elapsedTimeSeconds + " seconds");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

