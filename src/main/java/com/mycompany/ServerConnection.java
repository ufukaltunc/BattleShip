/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import com.mycompany.Message;
import com.mycompany.Player;
import com.mycompany.Shot;

public class ServerConnection extends Thread {

    private Socket server;
    private Client client;
    private Player player;
    private Shot shot;

    private ObjectOutputStream out;
    private ObjectInputStream in;
    private Message message;

    private boolean playersConnected = false;
    private boolean playersReady = false;
    private boolean ready = false;
    private boolean gameStarted = false;

    public ServerConnection(Socket server, Player player, Client client) throws IOException {
        this.server = server;
        this.client = client;
        this.player = player;
        this.out = new ObjectOutputStream(server.getOutputStream());
        this.in = new ObjectInputStream(server.getInputStream());
    }

    public void setReady(boolean ready) {
        this.ready = ready;
    }

    public void sendShot(Shot shot) {
        this.shot = shot;
        try {
            out.writeObject(this.shot);
        } catch (IOException e) {
            System.out.println("Error sending shot: " + e.getMessage());
        }
    }
    public void sendMessage(String text){
        System.out.println(text);
        /*message.setMssg(text);
        try {
            out.writeObject(this.message);
        } catch (IOException e) {
            System.out.println("Error sending message: " + e.getMessage());
        }*/
    }

    public void closeConnection() {
        try {
            in.close();
            out.close();
            server.close();
        } catch (IOException e) {
            System.out.println("Error closing connection: " + e.getMessage());
        }
    }

    public void sendPlayerObject() {
        try {
            out.writeObject("nextPlayer");
            out.writeObject(player);
        } catch (IOException e) {
            System.out.println("Error sending Player object: " + e.getMessage());
        }
    }

    public void run() {
        try {
            while (true) {
                Object recievedObject = in.readObject();
                if (!gameStarted) {
                    String serverResponse = (String) recievedObject;
                    System.out.println("[SERVER]: " + serverResponse);

                    if (!playersConnected) {
                        if (serverResponse.equals("wait")) {
                            client.openWaitingScreen();
                        } else if (serverResponse.equals("playersConnected")) {
                            client.closeWaitingScreen();
                            playersConnected = true;

                        }
                    }

                    if (playersConnected && !playersReady && ready) {
                        if (serverResponse.equals("wait")) {
                            client.openWaitingScreen();
                        } else if (serverResponse.equals("playersReady")) {
                            client.closeWaitingScreen();
                            playersReady = true;
                            gameStarted = true;
                        }
                    }
                } else {
                    message = (Message) recievedObject;
                    client.processMessage(message);
                }

            }
        } catch (IOException e) {
            System.out.println("Error retrieving message: " + e.getMessage());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                in.close();
                out.close();
                server.close();
            } catch (IOException e) {
                System.out.println("Error closing socket: " + e.getMessage());
            }
        }
    }
}
