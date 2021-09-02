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

public class ClientHandler extends Thread implements Runnable {

    private Socket client;

    private ObjectInputStream in;
    private ObjectOutputStream out;

    public ClientHandler(Socket clientSocket) {
        try {
            this.client = clientSocket;
            this.out = new ObjectOutputStream(client.getOutputStream());
            this.in = new ObjectInputStream(client.getInputStream());
            out.writeObject("Succesfully created client thread.");

        } catch (IOException e) {
            System.out.println("Error creating client thread: " + e.getMessage());
        }
    }

    public void broadcast(Object message) {
        for (ClientHandler clientHandler : Server.clients) {
            try {
                clientHandler.getObjectWriter().writeObject(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public ObjectOutputStream getObjectWriter() throws IOException {
        return out;
    }

    public void checkClientCount() {
        if (Server.clients.size() < 2) {
            broadcast("wait");
        } else {
            Server.playersConnected = true;
            broadcast("playersConnected");
        }
    }

    public void checkPlayerCount() {
        if (Server.players.size() < 2) {
            broadcast("wait");
        } else {
            broadcast("playersReady");
            Server.gameStarted = true;

            // The first player to fill their field starts first.
            Server.currentTurn = Server.players.get(0);
            Message msg = new Message();
            msg.setNextTurn(Server.players.get(0).getPlayerName());
            msg.setShot(null);
            msg.setShip(null);
            broadcast(msg);
        }
    }

    public void run() {
        if (!Server.playersConnected) {
            checkClientCount();
        }
        try {
            // Waiting for command from client
            while (true) {
                Object recievedObject = in.readObject();

                if (!Server.gameStarted) {

                    String request = (String) recievedObject;

                    if (Server.players.size() < 2) {
                        if (request.contains("nextPlayer")) {
                            try {
                                Player currentPlayer = (Player) in.readObject();
                                Server.players.add(currentPlayer);
                                System.out.println("New player: " + currentPlayer.getPlayerName());
                                checkPlayerCount();
                            } catch (ClassNotFoundException e) {
                                System.out.println("Error reading player object on serverside");
                                e.printStackTrace();
                            }
                        }
                    }
                } else {
                    Shot shot = (Shot) recievedObject;
                    System.out.println("New shot: (" + shot.getX() + ", " + shot.getY() + ")");

                    Message response = Server.processShot(shot);
                    broadcast(response);

                    if (response.isVictory()) {
                        in.close();
                        out.close();
                        client.close();
                        System.exit(0);
                    }
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error parsing request " + e.getMessage());
        } finally {
            // If the server is down, the connection is closed
            try {
                in.close();
                out.close();
                client.close();
            } catch (IOException e) {
                System.out.println("Error closing clientHandler ports " + e.getMessage());
            }
        }
    }
}
