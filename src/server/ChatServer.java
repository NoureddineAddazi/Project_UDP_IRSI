package server;

import java.io.*;
import java.net.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import java.sql.*;
public class ChatServer {
    public final static int port = 4445;
    public static DatagramSocket socket = null;
    // liste de type Map contient les adresses des utilisateurs connectés
    public static Map<String, InetSocketAddress> clients = new ConcurrentHashMap<>();

    public static void main(String[] args) throws Exception {
        System.out.println("Server is available");
        System.out.println("Port: " + port);
        // créer un DatagramSocket pour recevoir et envoyer des paquets
        socket = new DatagramSocket(port);

        // un thread pour recevoir des messages
        new Thread() {
            @Override
            public void run() {
                while (true) {
                    try {
                        // Créer un buffer pour recevoir un packet
                        byte[] buffer = new byte[1024];
                        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

                        // recevoir le packet
                        socket.receive(packet);

                        // Avoir l'adresse du client
                        InetAddress clientAddress = packet.getAddress();
                        int clientPort = packet.getPort();
                        InetSocketAddress clientSocketAddress = new InetSocketAddress(clientAddress, clientPort);

                        // Avoir le message de packet
                        String message = new String(buffer, 0, packet.getLength());

                        // Vérifier si le message est "CHECK_USERNAME"
                        if (message.startsWith("CHECK_USERNAME:")) {
                            // extraire l'identifiant qui est dans le message
                            String username = message.substring("CHECK_USERNAME:".length());
                            try
                            {
                                //étape1: charger la classe de driver
                                Class.forName("com.mysql.cj.jdbc.Driver");

                                //étape2: créer l'objet de connexion
                                Connection conn = DriverManager.getConnection(
                                        "jdbc:mysql://localhost:3306/etudiants", "root", "");

                                //étape3: créer l'objet statement

                                PreparedStatement preparedStatement = conn.prepareStatement("SELECT nom,prenom FROM etudiant WHERE cne=?");
                                preparedStatement.setString(1,username);
                                ResultSet res = preparedStatement.executeQuery();
                                if(!res.isBeforeFirst()){
                                    // envoyer un message au client pour l'informer que son identifiant n'existe pas à la BD.
                                    String response = "NON_EXISTE_BD";
                                    byte[] data = response.getBytes();
                                    DatagramPacket responsePacket = new DatagramPacket(data, data.length, clientAddress, clientPort);
                                    socket.send(responsePacket);


                                }
                                else{
                                    // envoyer un message au client pour l'informer que son identifiant existe à la BD.
                                    String response = "EXISTE_BD";
                                    clients.put(username, clientSocketAddress);
                                    byte[] data = response.getBytes();
                                    DatagramPacket responsePacket = new DatagramPacket(data, data.length, clientAddress, clientPort);
                                    socket.send(responsePacket);
                                }
                                res.close();


                            }
                            catch(Exception e){
                                System.out.println(e);
                            }
                        } else {
                            // diviser le message pour extraire le client, destinataire et le actualMessage
                            String[] parts = message.split(":");
                            String recipient = parts[0];
                            String username = parts[1];
                            String actualMessage = parts[2];
                            // vérifier si le destinataire est connecté au serveur
                            if (clients.containsKey(recipient)) {
                                // avoir l'adresse de destinataire à partir de la liste Map des clients (destinataire aussi est un client)
                                InetSocketAddress recipientAddress = clients.get(recipient);


                                // envoyer le message au destinateur
                                byte[] data = actualMessage.getBytes();
                                DatagramPacket messagePacket = new DatagramPacket(data, data.length, recipientAddress);
                                socket.send(messagePacket);
                                System.out.println("Message est envoyé par: " +username+" à: "+recipient+" :::: " + actualMessage);
                            } else {
                                // si le destinataire est non connecté , le client va recevoir un message que le destinataire est hors ligne
                                String response = "Recipient is offline";
                                byte[] data = response.getBytes();
                                DatagramPacket responsePacket = new DatagramPacket(data, data.length, clientAddress, clientPort);
                                socket.send(responsePacket);
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }
}