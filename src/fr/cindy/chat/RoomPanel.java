package fr.cindy.chat;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;



/**
 *  Cette classe est responsable de tous les composants graphiques
 */
public class RoomPanel extends JPanel{

    
    /**
     * Liste des utilisateurs actifs
     */
    private JColorTextPane clientActiveUsersList;
    
    /**
     * Espace de saisie utilisateur 
     */
    private JTextArea clientTypingBoard;

     /**
     * Affichage des messages 
     */
    private JColorTextPane clientMessageBoard;

    /**
     * Button d'envoi de message
     */
    private JButton clientSendMsgBtn;

     /**
     * Gere la liste des utilisateurs pouvant recevoir un message
     */
    JComboBox<String> privateChat;

    /**
     * Gestion de composant sous format html
     */
    HTMLEditorKit htmlKit;

    /**
     * Document HTML a gerer
     */
    HTMLDocument htmlDoc; 


    /**
     * Creation du panneau de discussion
     */
    public RoomPanel(){
        initComponents();
    }

    
    /** 
     * Getter private chat
     * @return JComboBox 
     */
    public JComboBox<String> getPrivateChat() {
        return privateChat;
    }
    
    private void initComponents() {
        
        privateChat = new JComboBox<>();
        privateChat.addItem("Tous le monde");
      
        clientActiveUsersList = new JColorTextPane();
        clientActiveUsersList.setToolTipText("Utilisateurs actif");
        clientActiveUsersList.setPreferredSize(new Dimension(120, 200));
        clientActiveUsersList.setEditable(false);
 
        clientMessageBoard = new JColorTextPane();
        htmlKit = new HTMLEditorKit();
        htmlDoc = new HTMLDocument();
        clientMessageBoard.setEditorKit(htmlKit);
        clientMessageBoard.setDocument(htmlDoc);

        //clientMessageBoard.setBackground( new Color(54,57,63));
        clientMessageBoard.setEditable(false);

        clientTypingBoard = new JTextArea(5,20);
        clientSendMsgBtn = new JButton("Envoyer");


        // Defini des bordures vides autours du container
        this.setBorder(new EmptyBorder(10, 10, 10, 10));
        this.setLayout(new BorderLayout(0, 0));

        JPanel rightView = new JPanel();
        rightView.setBorder(new EmptyBorder(0, 20, 0, 0));
        rightView.setLayout(new BorderLayout(0, 0));

        JPanel center_content = new JPanel();
        center_content.setLayout(new BorderLayout());


        JPanel discussionView = new JPanel();
        center_content.add(discussionView,BorderLayout.CENTER);
        discussionView.setLayout(new BorderLayout(0, 0));
        discussionView.setBackground(new Color(54,57,63));

        JLabel dis = new JLabel("Discussion");
        dis.setHorizontalAlignment(SwingConstants.CENTER);
        discussionView.add(dis, BorderLayout.NORTH);

        JScrollPane jscroll = new JScrollPane(clientMessageBoard);
        jscroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        discussionView.add(jscroll, BorderLayout.CENTER);
        

        JPanel messageView = new JPanel();
        messageView.setBorder(new EmptyBorder(0, 0, 20, 0));
        messageView.setLayout(new BorderLayout(0, 0));
        center_content.add(messageView, BorderLayout.SOUTH);
     
        JPanel privatePanel = new JPanel();
        privatePanel.setLayout(new FlowLayout(FlowLayout.TRAILING,5,6));
        privatePanel.setBorder(new EmptyBorder(0, 0, 20, 0));

        privatePanel.add(new JLabel("Message prive"));
        privateChat.setPreferredSize(new Dimension(200,20));
        privatePanel.add(privateChat);

        privatePanel.add(privateChat, BorderLayout.CENTER);
        messageView.add( privatePanel, BorderLayout.NORTH );
        messageView.add(clientTypingBoard, BorderLayout.SOUTH);
       


        /*
         * Ajout des composants du container contenant les elements se situant a droite
         */
        rightView.add(center_content, BorderLayout.CENTER);
        rightView.add(clientSendMsgBtn, BorderLayout.SOUTH);

        JPanel leftView = new JPanel();
        leftView.setLayout(new BorderLayout(0, 0));

        JLabel lblNewLabel = new JLabel("Connect\u00E9s");
        lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
        leftView.add(lblNewLabel, BorderLayout.NORTH);

        //
        leftView.add(clientActiveUsersList, BorderLayout.CENTER);

        /**
         * Ajout des containers droit & gauches dans le panel
         */
        add(leftView, BorderLayout.WEST);
        add(rightView, BorderLayout.CENTER);

        setVisible(false);
    }
    /**
     * Nettoie la liste des utilisateurs 
     */
    public void clearList(){
        clientActiveUsersList.setText("");
        privateChat.removeAllItems();
        privateChat.addItem("Tous le monde");
      
    }

    
    /** 
     * @return String InputText
     */
    public String getInputText(){ 
        return  clientTypingBoard.getText();
    }

    
    /** 
     * @return JButton Bouton envoye
     */
    public JButton getBtnSend() {
        return clientSendMsgBtn;
    }

    
    
    /** 
     * @return JColorTextPane Liste d'utilisateurs actifs
     */
    public JColorTextPane getClientActiveUsersList() {
        return clientActiveUsersList;
    }


    
    /** 
     * @return JColorTextPane Message Board
     */
    public JColorTextPane getClientMessageBoard() {
        return clientMessageBoard;
    }

    
    /** 
     * @return JTextArea Typing Board
     */
    public JTextArea getClientTypingBoard() {
        return clientTypingBoard;
    }

    
    /** 
     * Affiche le message a gauche
     * @param msg Message a afficher
     * @param color Couleur du message ( lie a celle de l'utilisateur ) 
     */
    public void appendMessage_Left(String msg, String color) {   
        try {
            htmlKit.insertHTML(htmlDoc, htmlDoc.getLength(), "<p style=\"color:rgb("+color+"); padding: 3px; margin-top: 4px; text-align:left; font:normal 12px Tahoma;\"><span style=\" -webkit-border-radius: 10px;\">" + msg + "</span></p>", 0, 0, null);

        } catch (BadLocationException | IOException ex) {
            Logger.getLogger(RoomPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
        clientMessageBoard.setCaretPosition(clientMessageBoard.getDocument().getLength());
    }
     
    
    /** 
     * Affiche le message a droit
     * @param msg Message a afficher
     * @param color Couleur du message ( lie a celle de l'utilisateur ) 
     */
    public void appendMessage_Right(String msg, String color) {    
        try { 
            htmlKit.insertHTML(htmlDoc, htmlDoc.getLength(), "<p style=\"color:rgb("+color+"); padding: 3px; margin-top: 4px; text-align:right; font:normal 12px Tahoma;\"><span style=\" -webkit-border-radius: 10px;\">" + msg + "</span></p>", 0, 0, null);
        } catch (BadLocationException | IOException ex) {
            Logger.getLogger(RoomPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
        clientMessageBoard.setCaretPosition(clientMessageBoard.getDocument().getLength());
    }

    
}
