import java.io.*;
import java.net.*;

public class Client_TCP {
    private static final String SERVER_IP = "127.0.0.1"; // IP do servidor
    private static final int SERVER_PORT = 12345; // Porta do servidor

    public static void main(String[] args) {
        try (Socket clientSocket = new Socket(SERVER_IP, SERVER_PORT)) {
            long startTime = System.nanoTime();

            int tamanho = 1024 * 1
                    ;
            byte[] sendData = new byte[tamanho];
            OutputStream outputStream = clientSocket.getOutputStream();
            outputStream.write(sendData);
            outputStream.flush();

            long endTime = System.nanoTime();
            double elapsedTimeSeconds = (endTime - startTime) / 1e9;
            System.out.println("Tempo necessário para enviar a mensagem via TCP: " + tamanho / elapsedTimeSeconds + " KB/S");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
