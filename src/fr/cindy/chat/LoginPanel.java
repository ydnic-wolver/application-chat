package fr.cindy.chat;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;


/**
 * Classe responsable des HUD liees au Login
 */
public class LoginPanel extends JPanel {

    /**
     * Host
     */
    private JTextField tfHost;
    
    /**
     * Nom
     */
    private JTextField tfNickname;
    
    /**
     * Port
     */
    private JTextField tfPort;
    
    /**
     * Bouton de connexion
     */
    private JButton btnConnect;

    /**
     * Creation du panel Login
     */
    public LoginPanel() {
        initComponents();
    }

    /**
     * Initialisation des composants
     */
    public void initComponents() {

        tfHost = new JTextField(10);
        tfNickname = new JTextField(10);
        tfPort = new JTextField(10);

        btnConnect = new JButton("Connexion");
        btnConnect.setEnabled(false);

        setLayout(new GridLayout(2, 2, 0, 0));

        FlowLayout lFlowLayout = new FlowLayout(FlowLayout.CENTER, 5, 5);

        JPanel containerIP = new JPanel(lFlowLayout);
        containerIP.add(new JLabel("IP"));
        containerIP.add(tfHost);

        JPanel containerBtn = new JPanel();
        containerBtn.add(btnConnect);

        JPanel containerPort = new JPanel(lFlowLayout);
        containerPort.add(new JLabel("Port"));
        containerPort.add(tfPort);

        JPanel containerNom = new JPanel(lFlowLayout);
        containerNom.add(new JLabel("Nom"));
        containerNom.add(tfNickname);

        FieldsEmptyListener listener = new FieldsEmptyListener();
        tfNickname.getDocument().addDocumentListener(listener);
        tfHost.getDocument().addDocumentListener(listener);
        tfPort.getDocument().addDocumentListener(listener);

        add(containerNom);
        add(containerBtn);
        add(containerIP);
        add(containerPort);

    }

    /**
     * Methode responsable de la verification des champs 
     */
    public void emptyFields() {
        boolean empty = false;
        if (tfHost.getText().equals("") || tfNickname.getText().equals("") || tfPort.getText().equals("")) {
            empty = true;
        }

        if (!empty)
            btnConnect.setEnabled(true);
        else
            btnConnect.setEnabled(false);
    }

    
    /** 
     * Retourne le bouton de connexion
     * @return JButton Bouton connecte
     */
    public JButton getBtnConnect() {
        return btnConnect;
    }

    
    /** 
     * Retourne le champ Host
     * @return JTextField Host
     */
    public JTextField getTfHost() {
        return tfHost;
    }

    
    /** 
     * Retourne le champ Nom
     * @return JTextField Nom
     */
    public JTextField getTfNickname() {
        return tfNickname;
    }

    
    /** 
     * Retourne le champ Port
     * @return JTextField Port
     */
    public JTextField getTfPort() {
        return tfPort;
    }

    /**
     * Classe interne permettant de gerer les evenements sur le document
     */
    private class FieldsEmptyListener implements DocumentListener {

        @Override
        public void insertUpdate(DocumentEvent e) {
            emptyFields();

        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            emptyFields();

        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            emptyFields();

        }

    }
}
