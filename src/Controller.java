import javax.swing.*;
import javax.swing.text.Element;
import javax.swing.text.Utilities;
import java.awt.event.*;
import java.util.Objects;

//La classe controller sert a gérée les actions possibles par l'utilisateur
//Code réalisé Alex Rompré Beaulieu / Emeric Laberge / Hugo de Sousa

public class Controller extends Model implements ItemListener {
    static String word;

    public Controller() {
        mouseEvents();
        caretEvents();
        keyEvents();
    }

    public void itemStateChanged(ItemEvent e) {

        String motsReplace = Objects.requireNonNull(cinqMots.getSelectedItem()).toString();
    }

    public void actionPerformed(ActionEvent e) {

        //Statement permettant de changer un mot erroné par un des 5 mots les plus proche disponible dans le
        //dictionnaire choisie par l'utilisateur
        if (e.getSource() == this.cinqMots && cinqMots.getSelectedItem().toString() != "Vouliez vous dire") {
            String text = zoneTexte.getText();
            String motsReplace = Objects.requireNonNull(cinqMots.getSelectedItem()).toString();
            text = text.replaceFirst(word, motsReplace);
            zoneTexte.setText(text);
            texteToArray();

        }
        //Statement permettant d'initier la vérification du texte, ssi le dictionnaire est importé
        if (e.getSource() == this.verifier && dicoFirst) {
            this.texteToArray();
        } else if (!dicoFirst) {
            alerte("Veuillez choisir un dictionnaire");
        }

        //Choix du dictionnaire
        if (e.getSource() == this.dicoChoose) {
            dicoFirst = true;
            this.chargerFichier("Dictionnaire");
            this.hashSet();
        }

        //Choix du fichier
        if (e.getSource() == this.optionsFichier) {
            String options = Objects.requireNonNull(optionsFichier.getSelectedItem()).toString();

            if (options.equals("ouvrir")) {
                {
                    this.chargerFichier("Fichier");
                    this.afficher();
                }
            }

            if (options.equals("enregistrer")) {
                this.saveFile();
            }
        }
    }

    //La méthode mouseEvents capte les évênement de la souris servant à identifier le mot et sa position
    private void mouseEvents() {
        zoneTexte.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    // Donne la position du clique dans le texte à partir du clic dans le textArea (View)
                    int offset = zoneTexte.viewToModel2D(e.getPoint());

                    // Donne la position de départ dans le texte du mot cliqué
                    int start = Utilities.getWordStart(zoneTexte, offset);

                    // Donne la position de fin dans le texte du mot cliqué
                    int end = Utilities.getWordEnd(zoneTexte, offset);

                    // Donne la position de début de la rangée dans le texte
                    int rowStart = Utilities.getRowStart(zoneTexte, offset);

                    // Donne la position de fin de la rangée dans le texte
                    int rowEnd = Utilities.getRowEnd(zoneTexte, offset);

                    // Permet de highlight une zone de texte si click droit
                    if (SwingUtilities.isRightMouseButton(e)) {
                        zoneTexte.select(start, end);
                    }
                    // getText() de la position de début et de longueur (fin - début)
                    word = zoneTexte.getText(start, end - start);
                    if (!dicoHash.contains(word)) {
                        setComboBox(start, end, motsProches(word));
                        cinqMots.setVisible(true);
                    } else if (dicoHash.contains(word)) {//si le mot est bien ecrit on affiche pas le combobox
                        cinqMots.setVisible(false);
                    }

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    private void caretEvents() {
        zoneTexte.addCaretListener(e -> {
            // Donne la postion du caret OU la position de début et de fin de la sélection
            int caretPosition = zoneTexte.getCaretPosition();

            Element root = zoneTexte.getDocument().getDefaultRootElement();

            // Donne la ligne du mot cliqué
            int row = root.getElementIndex(caretPosition);

            // Donne la colonne du mot cliqué
            int column = caretPosition - root.getElement(row).getStartOffset();
        });
    }

    //Évênements du clavier, écriture de texte
    private void keyEvents() {
        zoneTexte.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {

                int numberOfLines = zoneTexte.getDocument().getDefaultRootElement().getElementCount();

                textField.setText(String.valueOf(numberOfLines));
            }
        });
    }
}


