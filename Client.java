package chat;

import chat.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String nom;

    public Client(Socket socket, String nom) {
        try {
            this.socket = socket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.nom = nom;
        } catch (IOException e) {
            //TODO: handle exception
            toutFermer(socket, bufferedReader, bufferedWriter);
        }
    }

    public void envoiMessage() {
        try {
            bufferedWriter.write(nom);
            bufferedWriter.newLine();
            bufferedWriter.flush();
            Scanner scanner = new Scanner(System.in);
            while (socket.isConnected()) {
                String messageEnvoyer = scanner.nextLine();
                bufferedWriter.write(nom + " : " + messageEnvoyer);
                bufferedWriter.newLine();
                bufferedWriter.flush();
            }
        } catch (IOException e) {
            //TODO: handle exception
            toutFermer(socket, bufferedReader, bufferedWriter);
        }
    }

    public void recuMessage() {
        new Thread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                String messagesDuGroupe;
                while (socket.isConnected()) {
                    try {
                        messagesDuGroupe = bufferedReader.readLine();
                        System.out.println(messagesDuGroupe);
                    } catch (IOException e) {
                        //TODO: handle exception
                        toutFermer(socket, bufferedReader, bufferedWriter);
                    }
                }
            }
            
        }).start();
    }

    public void toutFermer(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
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

    public static void main(String[] args) throws IOException{
        Scanner scanner = new Scanner(System.in);
        System.out.println("Entrez votre nom : ");
        String nom = scanner.nextLine();
        Socket socket = new Socket("Localhost", 6666);
        Client client = new Client(socket, nom);
        client.recuMessage();
        client.envoiMessage();
    }

}