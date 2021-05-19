package fr.cindy.chat;

import java.awt.Color;

import javax.swing.JTextPane;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;


/**
 * Classe responsable de la personnalisation d'un JTextPane
 */
public class JColorTextPane extends JTextPane {

    /**
     *  serial par defaut
     */
    private static final long serialVersionUID = 2713154197866902157L;

    /**
     *  Permet d'ajouter du texte a une JTextpane
     * @param msg Message a ajouter
     * @param c Couleur du texte
     * @param align Parametre d'alignement
     */
    public void append(String msg, Color c, boolean align) {

        
        StyledDocument doc = this.getStyledDocument();
        SimpleAttributeSet aset = new SimpleAttributeSet();

        
        StyleConstants.setBold(aset, true); /** Met le texte en gras */
        /** Met le texte en couleur */
        StyleConstants.setForeground(aset, c);
        StyleConstants.setAlignment(aset, StyleConstants.ALIGN_LEFT);
     
        try {
            doc.insertString(doc.getLength(), msg, aset);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
        setDocument(doc);
       
    }

    
     
}