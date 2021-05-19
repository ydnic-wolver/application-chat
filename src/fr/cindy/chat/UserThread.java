package fr.cindy.chat;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Hashtable;
import java.util.StringTokenizer;

/**
 * La classe UserThread est responsable de la lecture des messages envoyes par le client et de la diffusion des messages a tous les autres clients. 
 *  Elle envoie d'abord une liste d'utilisateurs en ligne au nouvel utilisateur. 
 *  Ensuite, elle lit le nom d'utilisateur et informe les autres utilisateurs de l'existence du nouvel utilisateur.
 */
public class UserThread extends Thread {


    /**
     * Nom de l'utilisateur
     */
    private String nom;

    /**
     * Reference vers le serveur
     */
    private ServerFrame server;

    /**
     * Reference vers le socket de connexion 
     */
    private Socket socket;

    /**
     * Flux utiliser pour l'envoi de message
     */
    DataOutputStream outputStream;
    
    /** 
     * @return String Nom du client 
     */
    public String getNom() {
        return nom;
    }
    /**
     *  Permet de creer un Thread correspondant a un client
     * @param socket  Socket sur lequel la connexion a etait ouverte
     * @param server Reference vers la classe ServerFrame 
     */
    public UserThread( Socket socket, ServerFrame server ){
        this.socket = socket;
        this.server = server;
    }

    @Override
    public void run() {
      
            try {
                    
                    outputStream = new DataOutputStream( socket.getOutputStream() );
                    
                    DataInputStream inputStream =new DataInputStream( socket.getInputStream());
                    this.nom = inputStream.readUTF();
                    
                    String clientMessage ="";
                    
                        server.addUser(nom, socket); // ajoute l'utilisateur
                        server.generateColor(nom); // genere la couleur pour l'utilisateur
                     
                        server.broadcast("CMD_ONLINE_USERS|"+server.getAllUsers(), null); // Envoi la liste des utilisateurs  mis a jour a 
                        // tous les utilisateurs connectes.

                        StringBuffer str;
                        String serverMessage = "";
                        while (!clientMessage.contains("exit")) {
                        
                            clientMessage = inputStream.readUTF();
                            
                            System.out.println("message read ==> " + clientMessage); 
                            StringTokenizer tokenizer = new StringTokenizer(clientMessage,"|");
                            String cmd = tokenizer.nextToken();
                            switch(cmd){
                                case "CMD_CHAT":
                                     cmd = tokenizer.nextToken();
                                    System.out.println("Server message =>  " + serverMessage );
                                    server.broadcast(this.nom+">"+cmd, this);
                                 break;
                                    case "CMD_PRIVATECHAT": 
                                        String user = tokenizer.nextToken();
                                        cmd = tokenizer.nextToken();
                                        System.out.println("to user " + user + " = message  " + serverMessage );

                                        serverMessage = this.nom+"["+ user + "]$>" + cmd;
                                        server.sendToOneUser(user, serverMessage);
                                    break;
                                default: 
                                    server.broadcast(this.nom+" s'est deconnecte!", this);
                                break;
                            }

                        }
                        
                        server.removeUsers(nom,this);
                        server.broadcast("CMD_ONLINE_USERS|"+server.getAllUsers(), null);
                    
                } catch (IOException ioex) {  
                    ioex.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
       
       
        /**
         * Permet a un client d'envoyer un message a tous les utilisateurs connectes
         * @param message Message a envoyer 
         */
        void sendMessage(String message) {
             try {
                 outputStream.writeUTF(message);
            } catch (IOException e) {
                System.out.println("Erreur lors de l'envoi");
                e.printStackTrace();
            }
        }

}