package chat;

import chat.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHandler implements Runnable{

    public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String clientName;

    public ClientHandler(Socket socket) {
        try {
            this.socket = socket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.clientName =  bufferedReader.readLine();
            clientHandlers.add(this);
            messageAfficher("SERVER : " + clientName + " participe a la discussion de groupe");
        } catch (IOException e) {
            //TODO: handle exception
            toutFermer(socket, bufferedReader, bufferedWriter);
        }
    }

    @Override
    public void run() {
        // TODO Auto-generated method stub
        String messageFromClient;
        while (socket.isConnected()) {
            try {
                messageFromClient = bufferedReader.readLine();
                messageAfficher(messageFromClient);
            } catch (Exception e) {
                //TODO: handle exception
                toutFermer(socket, bufferedReader, bufferedWriter);
                break;
            }
        }
    }

    public void messageAfficher(String messageEnvoyer) {
        for(ClientHandler clientHandler : clientHandlers) {
            try {
                if (!clientHandler.clientName.equals(clientName)) {
                    clientHandler.bufferedWriter.write(messageEnvoyer);
                    clientHandler.bufferedWriter.newLine();
                    clientHandler.bufferedWriter.flush();

                }
            } catch (IOException e) {
                //TODO: handle exception
                toutFermer(socket, bufferedReader, bufferedWriter);
            }
        }
    }

    public void enleverClient() {
        clientHandlers.remove(this);
        messageAfficher("SERVER : " + clientName + " a quiter la discussion de groupe");
    }

    public void toutFermer(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
        enleverClient();
        try {
            if (socket != null) {
                socket.close();
            }
            if (bufferedReader != null) {
                bufferedReader.close();
            }
            if (bufferedWriter != null) {
                bufferedWriter.close();
            }
        } catch (IOException e) {
            //TODO: handle exception
            e.printStackTrace();
        }
    }    
}
