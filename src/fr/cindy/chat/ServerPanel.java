package fr.cindy.chat;

import java.awt.Color;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;



/**
 * Classe responsable de l'interface graphique du serveur
 */
public class ServerPanel extends JPanel {

    
    /**
     * Frame
     */
    private JFrame frame; 

    /**
     * Variable pour le tableau des messages du serveur sur l'interface
     */
    private JTextArea serverMessageBoard; 

    /**
     * Liste de nom des utilisateurs 
     */
    private JList<String> allUserNameList;  

    /**
     * Liste des clients actifs 
     */
    private JList<String> activeClientList; 

    /**
     * Conserve la liste des utilisateurs actifs pour l'afficher sur l'interface utilisateur
     */
    private DefaultListModel<String> activeDlm = new DefaultListModel<String>(); 

    /**
     * Conserve la liste de tous les utilisateurs pour l'afficher sur l'IU
     */
    private DefaultListModel<String> allDlm = new DefaultListModel<String>(); 

    /**
     * Construction du ServerPanel
     * @param frame  Reference vers la frame
     */
    public ServerPanel(JFrame frame){
        this.frame = frame;
        initialize();  
    }

    /**
     * Initialisez le contenu du panel
     */
    private void initialize() { //here components of Swing App UI are initilized
        
        serverMessageBoard = new JTextArea();
        serverMessageBoard.setEditable(false);
        serverMessageBoard.setBounds(12, 29, 489, 435);
        frame.getContentPane().add(serverMessageBoard);
        serverMessageBoard.setText("Demarrage du serveur...\n");

        allUserNameList = new JList();
        allUserNameList.setBounds(526, 324, 218, 140);
        frame.getContentPane().add(allUserNameList);

        activeClientList = new JList();
        activeClientList.setBounds(526, 78, 218, 156);
        frame.getContentPane().add(activeClientList);

        JLabel lblNewLabel = new JLabel("Noms utilisateurs");
        lblNewLabel.setHorizontalAlignment(SwingConstants.LEFT);
        lblNewLabel.setBounds(530, 295, 127, 16);
        frame.getContentPane().add(lblNewLabel);

        JLabel lblNewLabel_1 = new JLabel("Utilisateurs actifs");
        lblNewLabel_1.setBounds(526, 53, 98, 23);
        frame.getContentPane().add(lblNewLabel_1);
        
        
        
    }

    /**
     * Demande au tableau de message d'ajouter le message
     * @param message Message a afficher
     */
    public void setMessageBoard(String message){
        serverMessageBoard.append(message);
    }

    /**
     * Met a jour la liste UI des utilisateurs connecte
     * Supprime la chaine passe en parametre du model
     * @param userName Nom de l'utilisateur
     */
	public void updateUsersList(String userName) {
        activeDlm.removeElement(userName); // Supprime le client de la JList du serveur
        activeClientList.setModel(activeDlm); //Met a jour la liste actif des utilisateur
	}

	
    /** 
     * Ajoute un client dans la liste
     * @param userName Nom a ajouter
     */
    public void addUserList(String userName) {
        
        activeDlm.addElement(userName); // Ajoute l'utilisateur a la liste d'actif
        if (!allDlm.contains(userName)) // si le nom d'utilisateur a ete pris precedemment,on ne l'ajoute pas pas a allUser JList ;
            allDlm.addElement(userName);
        
        activeClientList.setModel(activeDlm); // show the active and allUser List to the swing app in JList
        allUserNameList.setModel(allDlm);
	}

}

