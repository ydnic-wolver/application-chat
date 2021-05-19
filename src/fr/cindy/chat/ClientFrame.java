package fr.cindy.chat;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.FileReader;
import java.io.IOException;
import java.net.Socket;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.text.BadLocationException;

/**
 *  La classe Client est responsable du lancement du client
 *
 *  Elle demarre un client, recupere les informations saisies par l'utilisateur, 
 *  et autorise ou non la connexion au serveur.
 *  Un client peut envoyer un message a tous les utilisateurs ou a un seul seulement
 *
 */
public class ClientFrame extends JFrame {

    /**
     * Reference vers l'UI de discussion 
     */
    private RoomPanel roomPanel;
    
    /**
     * Reference vers l'UI de login 
     */
    private LoginPanel loginPanel;

    /**
     * Liste des couleur utilisateurs
     */
    public static Map<String, String> clientColorList = new ConcurrentHashMap<>();
   
    /**
     * Nom de l'utilisateur
     */
    String nom;
    
    /**
     * Host du serveur
     */
    String host;

    /**
     * Numero du port
     */
    int port;

    /**
     * Socket 
     */
    Socket socket;


    /**
     * Permet de savoir si le client est connecte
     */
    private boolean isConnected;

    /**
     * Flux utiliser pour la lecture de message
     */
    DataInputStream inputStream;
 
    /**
     * Flux utiliser pour l'envoi de message
     */
    DataOutputStream outStream;

    /**
     * Permet de connaître le type de communication
     * Broadcast ou Unicast 
     */
    private String cmd;
    
    /**
     *  
    /*  Cree et initialise les composants liees au Client.
     */
    public ClientFrame() {
        initialize();
       
    }


    /**
     * Creation d'un nouveau Thread pour la lecture des messages
     */
    public class ReadThread extends Thread {

        /**
         * Construction du thread
         */
        public ReadThread(){
        }   

      @Override
        public void run() {


            String response;
            StringTokenizer tokenizer;
            String cmd;

            while ( isConnected  ) {
                try {
                        if( isConnected ){

                             response = inputStream.readUTF();
                            
                            System.out.println("Message recu"  + response);
                            
                            tokenizer  = new StringTokenizer(response, "|");
                            cmd = tokenizer.nextToken();
                           
                                
                            switch(cmd){

                                case "CMD_ONLINE_USERS":
                                    

                                    roomPanel.clearList(); /** Nettoie la liste des utilisateurs actifs */
                                    Color color = null;
                                  
                                    while(tokenizer.hasMoreTokens()) {
                                        cmd = tokenizer.nextToken();
                                        color = findColorFile(cmd); // Cherche la couleur assignee a l'utilisateur 
                                        if( !nom.equalsIgnoreCase(cmd) ) // Ajoute tous les utilisateurs sauf soi-même dans l'onglet "Message privee"
                                        { 
                                            roomPanel.getPrivateChat().addItem(cmd);
                                        }
                                        roomPanel.getClientActiveUsersList().append(cmd+"\n", color, true); /* Affichage de la liste des utilisateurs mis a jour */
                                    }
                                   
                                break;
                            
                                default:
                                    
                                    // Si le message reçu contient le symbole > , cela
                                    // signifie que c'est un message utilisateur
                                    if(response.contains(">")){
                                        String ms[] = response.split(">");
                                        System.out.println("Partie 1" + ms[0]);
                                        String userColor = findColors(ms[0]);
                                        roomPanel.appendMessage_Left(ms[0]+">"+ ms[1], userColor);
                                    }else {
                                        roomPanel.appendMessage_Left(response, "105,105,105");
                                    }
                                  
                                break;
                            }
                    }
                       
                  
                }catch (EOFException ex) {
                    System.out.println("Erreur fin de fichier");
                    return;
                   
                }
                catch (Exception e) {
                    e.printStackTrace();
                    break;
                }
            }
        }
        
       
    }

    

    /**
     * Recupere la couleur contenu dans le fichier genere par le serveur
     * 
     * Le serveur stocke les couleurs sous un format [r,g,b]
     * Le fichier est donc analyse et traiter afin de recuperer les chaînes correspondantes.
     * @param nom Nom de l'utilisateur
     * @return La couleur correspondant au nom 
     */
    public Color findColorFile(String nom){
        try (BufferedReader br = new BufferedReader(new FileReader("activeList.text"))){
            String st;
            int r, g, b;
            Color col = null;
            while( (st = br.readLine()) != null) {
               String[] sm = st.split(":");
               StringTokenizer tok = new StringTokenizer(sm[1],",");
                
               if( sm[0].equalsIgnoreCase(nom)) {
                    try {
                        
                        r=Integer.parseInt(tok.nextToken());
                        g=Integer.parseInt(tok.nextToken());
                        b=Integer.parseInt(tok.nextToken());

                        clientColorList.put(nom,  r+","+g+","+b);
                        col=new Color(r,g,b);
                    } catch (Exception e) {} 
                     
                }
              }
            System.out.println("Lecture ok ok");
            return col;   
        }catch(Exception ex){
            System.out.println("ERREUR SURVENU apres serialisation");
            ex.printStackTrace();
        }
        return null;
    }

    /**
     * Itere sur la HashMap stockant les couleurs des utilisateurs et renvoi celle correspondant 
     * au parametre
     * @param nom Utilisateur a chercher
     * @return Couleur associer a l'utilisateur
     */
    public String findColors(String nom) {

        if( !clientColorList.isEmpty() ){
            for (Map.Entry<String, String> set : 
                clientColorList.entrySet()) { 
                        if( set.getKey().equalsIgnoreCase((nom)) ){
                        return set.getValue();
                }
            }
        }
       
       return null;
    }


    /**
     *  Initialise les composants du Client
     */
    private void initialize() {

        roomPanel = new RoomPanel();
        loginPanel = new LoginPanel();
        
        nom = null;
      
        roomPanel.getBtnSend().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                
                String textAreaMessage = roomPanel.getInputText();  // Recupere le message de la textbox
                
                if (textAreaMessage != null && !textAreaMessage.isEmpty()) { // Verifie si le message est vide
                    try {
                        cmd = (String) roomPanel.getPrivateChat().getSelectedItem();
                        sendToServer( textAreaMessage);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }

        });

        loginPanel.getBtnConnect().addActionListener( e -> {
            if( !isConnected ) {
                openConnection();                    
            }else{
                closeConnection();
            }
        });

        setLayout(new BorderLayout());
        add(loginPanel, BorderLayout.NORTH);
        add( roomPanel, BorderLayout.CENTER );
       
        
        setSize(900, 900);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    /**
     * Permet d'envoyer le message d'un client au serveur
     * 
     * @param msg Message a envoyer
     * @throws IOException Cette methode peut lever une IOException 
     * ce qui correspond a un probleme au niveau de l'envoi du message ( stream ferme par exemple )
     */
    public void sendToServer( String msg) throws IOException {
        
        String message;

         // Concatene le prefix en fonction du type du message envoyee
         // Broadcast ou Unicast
        if( this.cmd.contains("Tous")){
            message = "CMD_CHAT|" + msg + "|";
        }
        else {
            message = "CMD_PRIVATECHAT|" + this.cmd + "|" + msg  + "|";
        }
                        
        // Transmet au serveur le message
        outStream.writeUTF( message );
        
        // Nettoie le champ de texte
        roomPanel.getClientTypingBoard().setText("");
        
        // Cherche la couleur correspondante au nom
        String userColor = findColors(nom);
        System.out.println("Colooour: " + userColor );
       

        roomPanel.appendMessage_Right(msg +"<", userColor);
    }
   
    /**
     *  Pemet d'envoyer une demande de connexion
     */
    public void openConnection(){


        String nom = loginPanel.getTfNickname().getText();
        String host = loginPanel.getTfHost().getText();
        int port = Integer.parseInt(loginPanel.getTfPort().getText());

        this.nom = nom;
        this.host = nom;
        this.port = port;
        
        openClient(host);
    }

    /**
     *  Responsable de la fermeture d'une connexion
     */
    private void closeConnection() {

        try {
           
            loginPanel.getBtnConnect().setText("Connexion");
            roomPanel.setVisible(false);

            // Longue methode responsable de la suppression des messages affiches dans le board
            roomPanel.getClientMessageBoard().getDocument().remove(0, (roomPanel.getClientMessageBoard().getDocument().getLength()));
            isConnected = false;
            clientColorList.remove(nom);
            
            outStream.writeUTF("exit"); // Envoi d'exit afin de notifier le serveur

            loginPanel.getTfNickname().setEnabled(true);
            loginPanel.getTfHost().setEnabled(true);
            loginPanel.getTfPort().setEnabled(true);
            
        } catch (IOException e) {
          
            e.printStackTrace();
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
       
    }

    /**
     * Gere la demande de connexion au serveur
     * @param hostname Nom de l'host
     * @return true ou false en fonction de la reussite de la connexion
     */
    public boolean connectToServer(String hostname){
        
       
        try {
            socket = new Socket(hostname, port);
            inputStream = new DataInputStream(socket.getInputStream());
            outStream = new DataOutputStream(socket.getOutputStream());

            outStream.writeUTF( nom );
  
        } catch (java.net.UnknownHostException e) {
            JOptionPane.showMessageDialog(this, "L'adresse IP est incorrect\nVeuillez reessayez!", "Echec de connexion au serveur", JOptionPane.ERROR_MESSAGE);
            return false;

        } catch (java.net.ConnectException e) {
            JOptionPane.showMessageDialog(this, "Le serveur est injoignable, peut-être que le serveur n'est pas encore ouvert, ou qu'il ne peut pas trouver cet hôte.\nVeuillez reessayez!", "Echec de connexion au serveur", JOptionPane.ERROR_MESSAGE);
            return false;

        } catch(java.net.NoRouteToHostException e) {
            JOptionPane.showMessageDialog(this, "Impossible de trouver cet hôte!\nVeuillez reessayez!", "Echec de connexion au serveur", JOptionPane.ERROR_MESSAGE);
            return false;
        } catch (IOException ex) {
            
            System.out.println("Erreur inconnu");
        }
        return true;
    }

    /**
     * Connecte le client au serveur apres verification
     * que celle-ci est autorise
     * @param hostname L'hôte du serveur
     */
    public void openClient(String hostname){

        if( connectToServer(hostname) ){

            isConnected = true; 
            roomPanel.setVisible(true);
            loginPanel.getTfNickname().setEnabled(false);
            loginPanel.getTfHost().setEnabled(false);
            loginPanel.getTfPort().setEnabled(false);

            loginPanel.getBtnConnect().setText("Deconnexion");
            System.out.println("Connection success");
        
            new ReadThread().start(); // Creation d'un thread responsable de la lecture des messages 
        } 
       
    }

    
   
    // Afin de pouvoir tester le programme 
    // Il faut dans un premier temps lancer le programme 
    // ServerFrame.java
    // puis  ClientFrame.java
    /**
    * Main
    * @param args arguments 
    */
     public static void main(String[] args) {
          
        EventQueue.invokeLater( () -> {
            new ClientFrame();
        });

    }
}
