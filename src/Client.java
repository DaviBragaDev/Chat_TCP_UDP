import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;



public class Client implements  Runnable {


    private Socket client;
    private BufferedReader in;
    private PrintWriter out;

    private boolean done;



    @Override
    public void run(){
        try {
            client = new Socket("127.0.0.1",9991);
            out = new PrintWriter(client.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(client.getInputStream()));


            InputHandler inHandler  = new InputHandler();
            Thread t = new Thread(inHandler);
            t.start();

            String  inMessage;

            while((inMessage = in.readLine()) != null){
                System.out.println(inMessage);
            }
        } catch (IOException e) {

            shutdown();

        }
    }

    public void shutdown(){


        done = true;
        try{
            in.close();
            out.close();
            if(!client.isClosed()){
                client.close();
            }
        }catch (IOException e){
            // ignore
            throw new RuntimeException(e);
        }
    }

    class InputHandler implements Runnable {
        @Override
        public void run() {
            try {
                BufferedReader inReader = new BufferedReader(new InputStreamReader(System.in));
                while (!done) {
                    String message = inReader.readLine();
                    if (message.equals("/quit")) {
                        inReader.close();
                        shutdown();
                    } else {
                        long startTime = System.currentTimeMillis(); // Tempo inicial
                        out.println(message); // Envia a mensagem
                        long endTime = System.currentTimeMillis(); // Tempo final
                        long elapsedTime = endTime - startTime; // Calcula o tempo de envio
                        System.out.println("Tempo de envio: " + elapsedTime + " ms");
                    }
                }
            } catch (IOException e) {
                shutdown();
            }
        }
    }

    public static void main(String[] args) {
        Client client = new Client();
        System.out.println("Criei");
        client.run();
    }
}






