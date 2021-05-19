package fr.cindy.chat;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.JFrame;


/**
 *  La classe ServerFrame est responsable du lancement du serveur
 *
 *  Elle demarre le serveur, ecoute sur un port specifique.
 *  Lorsqu'un nouveau client se connecte, une instance de UserThread est cree
 *  Chaque connection etant traitee dans un thread distinct,le serveur est donc capable
 *  de gerer plusieurs client en même temps.
 *
 */
public class ServerFrame extends JFrame {
    
    
    /**
     *  Serial par defaut
     */
    private static final long serialVersionUID = -5059911418287583133L;


    /**
     * Liste des threads des utilisateurs connectes 
     */
    private final CopyOnWriteArrayList<UserThread> userList = new CopyOnWriteArrayList<>();
    
    /**
     * Cet ensemble garde la trace de tous les utilisateurs actifs
     */
    private final Set<String> activeUserSet = new HashSet<>();

    /**
     * Le numero de port a utiliser
     */
    private static final int DEFAULT_PORT = 8818; 

    /**
     * Reference vers l'UI du serveur
     */
    private ServerPanel serverPanel;
    
    /** Fichier permettant de gerer les couleurs */
    private File file;

    /**
     * Construction du controller lie au Server
     */
    public ServerFrame(){
        initialize();

        addWindowListener(new WindowAdapter(){
            @Override
            public void windowClosed(WindowEvent e) {
                file.delete();
            }
        });
    }

    /**
     * Initialisation des composants
     */
    public void initialize(){

         file = new File("activeList.text");

        if(file.exists()){
            file.delete();
        }  
        this.serverPanel = new ServerPanel(this);
        
        
        setBounds(100, 100, 796, 530);
        getContentPane().setLayout(null);
        setTitle("Server View");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
        setBounds(100, 100, 796, 530);
        
    }


    
   /**
    * Main
    * @param args arguments 
    */
    public static void main(String[] args) {
        new ServerFrame().execute();
    }
    
    /**
     * Gere l'envoi de message entre tous les utilisateurs connectes
     * @param message Message a traiter
     * @param excludeUser Utilisateur a exclure
     */
    void broadcast(String message, UserThread excludeUser) {
        for (UserThread aUser : userList) {
            if (aUser != excludeUser || excludeUser == null ) {
               Objects.requireNonNull(aUser).sendMessage(message);
            }
        }
    }

    /** 
     * Gere l'envoi de message vers un utilisateur
     * @param destinataire Destinataire du message
     * @param message Message a envoyer
     */
    void sendToOneUser(String destinataire, String message){
        for (UserThread aUser : userList) {
            if (aUser.getNom().equalsIgnoreCase(destinataire)) {
               aUser.sendMessage(message);
            }
        }
    }

   


    /**
     * Stocke le nom d'utilisateur du client nouvellement connecte.
     * @param userName Nom de l'utilisateur
     * @param socket Socket de connexion
     */
    public void addUser(String userName, Socket socket){
        
        activeUserSet.add(userName);
       
        serverPanel.addUserList(userName);
        serverPanel.setMessageBoard("Utilisateur " + userName + " connecte...\n"); 
            
    }

    /**
     *  Permet de generer une couleur pour un utilisateur 
     * @param nom Nom de l'utilisateur
     */
    public void generateColor(String nom){
        
        try (BufferedWriter bos = new BufferedWriter(new FileWriter(file, true))){
         
            Random generator = new Random();
            int red = generator.nextInt(255);
            int green = generator.nextInt(255);
            int blue = generator.nextInt(255);
                    
            bos.write(nom+":"+red+","+green+","+blue);
            bos.newLine();

        }catch (IOException ex) {
            ex.printStackTrace();
        }

         
    }

    
    /** 
     * Retourne la liste des utilisateurs connectes 
     * @return String Liste des utilisateurs actifs
     */
    public String getAllUsers() {
        return String.join("|", activeUserSet);
    }


    /**
     * Lorsqu'un client est deconnecte, il supprime le nom d'utilisateur et le fil d'utilisateur associes
     * @param userName Nom de l'utilisateur a supprimer
     * @param aUser reference vers le thread de l'utilisateur a connecter
     */
    void removeUsers(String userName, UserThread aUser) {
        boolean removed = activeUserSet.remove(userName);
        if (removed) {
            // Supprime l'utilisateur de la liste de thread
            userList.remove(aUser);
            // Affichage le message sur le server board
            serverPanel.setMessageBoard(userName + " deconnecte....\n"); 
    
            // Met a jour la liste des utilisateurs actids
            serverPanel.updateUsersList(userName); 
        
        }
    }

    /**
     * Gere l'execution des traitements liees a la creation du serveur
     * 
     */
    void execute(){
        
        // Creation d'un socket pour le serveur
        try (ServerSocket serverSocket = new ServerSocket(DEFAULT_PORT)) {

            serverPanel.setMessageBoard("Le serveur a demarre sur le port: " + DEFAULT_PORT + "\n");
            serverPanel.setMessageBoard("En attente de clients...\n"); // affiche les messages sur le tableau du serveur
            
             while (true) {
                 
                 Socket socket = serverSocket.accept(); // Traitement d'une nouvelle connexion
                 System.out.println("Nouvelle utilisateur connecte");
  
                 UserThread user = new UserThread(socket, this); 
                 userList.add(user);
                 user.start(); // Creation d'un nouveau thread pour le client
  
             }
            
        
         } catch (Exception e) {
             System.out.println("Erreur");
             e.printStackTrace();
         }
    }

   

}
