import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server implements Runnable {
    private ArrayList<ConnectionHandler> connections;
    private ServerSocket server;
    private boolean chatFechado;
    private ExecutorService pool;

    public Server(){
        connections = new ArrayList<>();
        chatFechado = false;
    }

    @Override
    public void run(){
        try{
            ServerSocket server = new ServerSocket(9991);
            pool = Executors.newCachedThreadPool();
            while(!chatFechado) {
                Socket client = server.accept();
                ConnectionHandler handler = new ConnectionHandler(client);
                connections.add(handler);
                pool.execute(handler);
            }
        }catch (IOException e) {
            desligar();
        }
    }

    public void broadCast(String mensagem){
        for (ConnectionHandler ch : connections){
            if (ch != null){
                ch.enviarMensagem(mensagem);
            }
        }
    }

    public void desligar(){
        try {
            chatFechado = true;
            pool.shutdown();
            if (!server.isClosed()) {
                server.close();
            }
            for (ConnectionHandler ch:connections){
                ch.desligar();
            }
        }catch (IOException e){
            // ignore
        }
    }

    class ConnectionHandler implements Runnable{
        private Socket client;
        private BufferedReader in;
        private PrintWriter out;
        private String nickname;
        public ConnectionHandler(Socket client){
            this.client = client;
        }

        @Override
        public void run() {

            try{
                String mensagem;
                long endTime;
                long startTime;

                out = new PrintWriter(client.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                out.println("Digite o seu nickname:");
                nickname = in.readLine();
                System.out.println(nickname + " se conectou ao servidor!");
                broadCast(nickname + " entrou no chat!");

                while((mensagem = in.readLine()) != null){
                    if (mensagem.startsWith("/nick ")){
                        String[] mensagemDividir = mensagem.split(" ", 2);
                        broadCast(nickname);
                        if (mensagemDividir.length == 2){
                            broadCast(nickname + " renomeou para " + mensagemDividir[1]);
                            System.out.println(nickname + " renomeou para " + mensagemDividir[1]);
                            nickname = mensagemDividir[1];
                            out.println("Sucesso! Voce mudou o nickname para " + nickname);
                        } else {
                            out.println("Sem nickmane fornecido!");
                        }

                        //TODO: alterar nickname
                    }else if(mensagem.startsWith("/quit")){
                        broadCast(nickname + " saiu do chat.");
                        desligar();
                    }  else if (mensagem.startsWith("/sendKB ")) {
                        startTime = System.currentTimeMillis(); // Tempo de início da transmissão
                        // Processamento para enviar uma mensagem de KB específico
                        String[] parts = mensagem.split(" ");
                        if (parts.length == 2) {
                            int kbSize = Integer.parseInt(parts[1]);
                            String message = generateMessageWithKBSize2(kbSize);

                            broadCast(nickname + ": " + message);
                            endTime = System.currentTimeMillis(); // Tempo de término da transmissão

                            long elapsedTime = endTime - startTime; // Calcula o tempo de transmissão da resposta
                            double elapsedTimeInSeconds = elapsedTime / 1000.0; // Tempo decorrido em segundos
                            byte[] bytes = message.getBytes();

                            double dataSizeInKB = bytes.length / 1024.0; // Tamanho da mensagem em KB
                            double transferRateInKBps = dataSizeInKB / elapsedTimeInSeconds; // Taxa de transferência em KB/s

                            System.out.println("Taxa de transferência: " + transferRateInKBps + " KB/s");
                            System.out.println("Tamanho da mensagem: " + dataSizeInKB + " KB");
                        } else {
                            out.println("Uso correto: /sendKB <tamanho em KB>");
                        }
                    }else{
                        broadCast(nickname + ": " + mensagem);
                    }

                }
            }catch(IOException e){
                desligar();
            }

        }


        private String generateMessageWithKBSize2(int kbSize){
            StringBuilder message = new StringBuilder();
            int rastreador = 0;

            String loreIpsum = "L";

            while(rastreador < kbSize*1024) {
                message.append(loreIpsum);
                rastreador += loreIpsum.length();
            }

            return message.toString().substring(0,kbSize*1024);
        }

        public void enviarMensagem(String mensagem){
            out.println(mensagem);
        }

        public void desligar(){
            try{
                in.close();
                out.close();
                if(!client.isClosed()){
                    client.close();
                }
            }catch (IOException e){
                //ignore
            }
        }
    }

    public static void main(String[] args) {
        Server server = new Server();
        server.run();
    }

}
