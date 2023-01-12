package client1;
import java.io.*;
import java.net.*;
public class ChatClient1 {
    public final static int port = 4445;
    public final static String server = "localhost";
    public static String username;

    public static void main(String[] args) throws Exception {
        // créer un buffer pour lire les entées d'un client
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

        // demander au client à entrer son identifiant
        System.out.println("Entrer votre CNE: ");
        username = in.readLine();

        // vérifier si l'identifiant existe à la BD
        boolean uniqueUsername = false;
        while (!uniqueUsername) {
            // envoyer un message au serveur pour vérifier si l'identifiant existe à la BD
            String message = "CHECK_USERNAME:" + username;
            byte[] buffer = message.getBytes();
            InetAddress address = InetAddress.getByName(server);
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, port);
            DatagramSocket socket = new DatagramSocket();
            socket.send(packet);

            // créer un buffer pour recevoir un packet
            byte[] receiveBuffer = new byte[1024];
            DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);

            // recevoir le packet
            socket.receive(receivePacket);

            // avoir le message de packet
            String response = new String(receiveBuffer, 0, receivePacket.getLength());

            // vérifier si l'identifiant existe à la BD
            if (response.equals("EXISTE_BD")) {
                uniqueUsername = true;

                System.out.println("------------------------------------------------------------------------------");
                System.out.println("Bienvenue !! Vous pouvez communiquer et entrer des messages: ");

            } else {
                // demander au client d'entrer un CNE valable

                System.out.println("------------------------------------------------------------------------------");
                System.out.println("CNE n'est pas d'un étudiant ingénieur de la filière IRISI. Essayer une autre fois: ");
                username = in.readLine();
            }
        }

        // créer un DatagramSocket pour recevoir et envoyer des messages
        DatagramSocket socket = new DatagramSocket();

        // un thread pour recevoir des messages
        new Thread() {
            @Override
            public void run() {
                while (true) {
                    try {
                        // créer un buffer pour recevoir un packet
                        byte[] buffer = new byte[1024];
                        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

                        // recevoir le packet
                        socket.receive(packet);

                        // avoir le message de packet
                        String message = new String(buffer, 0, packet.getLength());

                        // afficher le message reçu
                        System.out.println(message);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();

        // thread pour envoyer des messages
        new Thread() {
            @Override
            public void run() {
                while (true) {
                    try {
                        // demander au client à entrer l'identifiant d'un destinataire
                        System.out.println("Entrer le CNE du destinataire: ");
                        String recipient = in.readLine();

                        // demander au client à entrer le message à envoyer
                        System.out.println("Entrer un message: ");
                        String message = in.readLine();

                        // inclure le nom d'utilisateur et le destinataire dans le message
                        String messageWithUsername = recipient + ":" + username + ":" + message;

                        // créer un buffer pour envoyer un packet
                        byte[] buffer = messageWithUsername.getBytes();

                        // créer un packet pour envoyer un message
                        InetAddress address = InetAddress.getByName(server);
                        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, port);

                        // envoyer le packet
                        socket.send(packet);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }
}


