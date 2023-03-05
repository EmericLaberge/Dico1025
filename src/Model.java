import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.*;

//La classe Model est la structure du code
//Code réalisé Alex Rompré Beaulieu / Emeric Laberge / Hugo de Sousa

public class Model extends JFrame implements ActionListener, ItemListener {

    public final JFileChooser fc;
    public final JTextArea zoneTexte;
    public ArrayList<String> texte;
    public ArrayList<String> dico;
    public HashSet<String> dicoHash;
    public JComboBox<String> optionsFichier;
    public JComboBox<String> cinqMots;
    public String option[] = {"fichier", "ouvrir", "enregistrer"};
    public String proposition[] = {"Vouliez vous dire"};
    public JTextField textField;
    protected JButton dicoChoose;
    protected JButton verifier;
    boolean dicoFirst = false;

    // An instance of the private subclass of the default highlight painter
    Highlighter.HighlightPainter myHighlightPainter = new MyHighlightPainter(Color.red);

    //Le constructeur de Model nous permet d'établir les components de la fenêtre et d'ajouter leur Listener
    public Model() {
        JPanel haut = new JPanel();
        JPanel bas = new JPanel();
        textField = new JTextField(30);

        this.add(haut, "North");
        this.add(bas, "Center");

        this.zoneTexte = new JTextArea(20, 25);
        bas.add(this.zoneTexte);

        this.optionsFichier = new JComboBox<>(option);
        optionsFichier.addActionListener(this);
        haut.add(optionsFichier);

        this.cinqMots = new JComboBox<>(proposition);
        cinqMots.setVisible(false);
        bas.add(this.cinqMots);
        cinqMots.addActionListener(this);
        cinqMots.addItemListener(this);

        this.dicoChoose = new JButton("Dictionnaire");
        haut.add(this.dicoChoose);

        this.verifier = new JButton("Vérifier");
        haut.add(this.verifier);

        this.fc = new JFileChooser();
        this.texte = new ArrayList();
        this.dico = new ArrayList();
        this.dicoHash = new HashSet<>();
        this.verifier.addActionListener(this);
        this.dicoChoose.addActionListener(this);

    }

    //La méthode distance prend comme paramètre le mot erroné dans le textArea
    //et les mots dans le dictionnaire et va être utiliser pour retourner les mots
    //avec une différence minimal de caractère
    public static int distance(String s1, String s2) {
        int edits[][] = new int[s1.length() + 1][s2.length() + 1];
        for (int i = 0; i <= s1.length(); i++)
            edits[i][0] = i;
        for (int j = 1; j <= s2.length(); j++)
            edits[0][j] = j;
        for (int i = 1; i <= s1.length(); i++) {
            for (int j = 1; j <= s2.length(); j++) {
                int u = (s1.charAt(i - 1) == s2.charAt(j - 1) ? 0 : 1);
                edits[i][j] = Math.min(
                        edits[i - 1][j] + 1,
                        Math.min(
                                edits[i][j - 1] + 1,
                                edits[i - 1][j - 1] + u
                        )
                );
            }
        }
        return edits[s1.length()][s2.length()];
    }

    //La méthode chargerFichier se faire appeler lorsqu'on importe un dictionnaire ou un fichier texte
    public void chargerFichier(String fileType) {
        ArrayList<String> file = new ArrayList<>();

        if (fileType == "Fichier") {
            file = texte;
        } else if (fileType == "Dictionnaire") {
            file = dico;
        }

        int val = this.fc.showOpenDialog(this);

        try {
            if (val == 0) {
                BufferedReader r = new BufferedReader(new FileReader(this.fc.getSelectedFile()));
                String line = null;

                while ((line = r.readLine()) != null) {
                    file.add(line);
                }
                r.close();
            }
        } catch (Exception var4) {
            var4.printStackTrace();
        }

    }

    //Méthode hashSet prend un fichier servant de dictionnaire et le transforme en HashSet
    public void hashSet() {
        for (String s : dico) {
            dicoHash.add(s);
        }
    }

    //La méthode verif est appelée par le bouton vérifier et va traverser
    //le tableau des mots dans le textArea et les comparer à ceux du dictionnaire.
    //Si un mot n'est pas dans le dictionnaire, on appelle la fonction highlight avec le mot en question.
    public void verif(String[] arr) {
        removeHighlights(zoneTexte);
        for (int i = 0; i < arr.length; i++) {
            if (dicoHash.contains(arr[i]) == false) {
                highlight(arr[i]);
            }
        }
    }

    //La méthode afficher prend le texte contenu dans le fichier que
    //l'utilisateur a ouvert et l'affiche dans le textArea
    public void afficher() {
        if (this.texte != null) {
            Iterator var1 = this.texte.iterator();
            while (var1.hasNext()) {
                String s = (String) var1.next();
                this.zoneTexte.append(s + "\n");
            }
        }

    }

    //La méthode texteToArray prend le textArea et nous le transforme en tableau valide pour vérification
    public void texteToArray() {
        String txt = zoneTexte.getText().replace(",", "").toLowerCase();
        String[] arr = txt.split("[ !\"\\#$%&()*+,-./:;<=>?@\\[\\]^_{|}~]");
        verif(arr);
    }

    //La méthode saveFile prend le texte du textArea et ouvre le dialog permettant de sauvegarder le fichier
    public void saveFile() {
        int val = this.fc.showOpenDialog(this);
        String areaOfText = zoneTexte.getText();
        try {
            if (val == 0) {
                BufferedWriter r = new BufferedWriter(new FileWriter(this.fc.getSelectedFile()));
                r.write(areaOfText);
                r.close();
            }
        } catch (Exception var4) {
            var4.printStackTrace();
        }
    }

    public void actionPerformed(ActionEvent e) {
    }

    public void itemStateChanged(ItemEvent e) {
        String motsReplace = Objects.requireNonNull(cinqMots.getSelectedItem()).toString();
    }

    // SetComboBox sert à créer actualiser la liste de 5 mots proposé et l'afficher
    public void setComboBox(int x, int y, ArrayList<String> suggestionsMots) {
        cinqMots.removeAllItems();
        cinqMots.addItem("Vouliez vous dire");
        for (int i = 0; i < suggestionsMots.size(); i++) {
            cinqMots.addItem(suggestionsMots.get(i));
        }
        cinqMots.setLocation(x, y);
        cinqMots.setVisible(true);
    }

    //La méthode motsProches crée un ArrayList des 5 mots les plus proches
    public ArrayList<String> motsProches(String word) {
        ArrayList<String> listMots = new ArrayList<String>();
        int i = 0;
        while (listMots.size() < 5) {
            for (String mot : dicoHash) {
                if (listMots.size() < 5) {
                    if (distance(word, mot) == i) {
                        listMots.add(mot);
                    }
                }
            }
            i += 1;
        }
        return listMots;
    }


    // Creates highlights around all occurrences of pattern in textComp
    public void highlight(String pattern) {

        try {
            Highlighter hilite = zoneTexte.getHighlighter();
            Document doc = zoneTexte.getDocument();
            String text = doc.getText(0, doc.getLength());
            int pos = 0;

            // Search for pattern
            while ((pos = text.indexOf(pattern, pos)) >= 0) {
                // Create highlighter using private painter and apply around pattern
                hilite.addHighlight(pos, pos + pattern.length(), myHighlightPainter);
                pos += pattern.length();
            }
        } catch (BadLocationException e) {
        }
    }

    // Removes only our private highlights
    public void removeHighlights(JTextComponent ta) {
        Highlighter hilite = ta.getHighlighter();
        Highlighter.Highlight[] hilites = hilite.getHighlights();

        for (int i = 0; i < hilites.length; i++) {
            if (hilites[i].getPainter() instanceof MyHighlightPainter) {
                hilite.removeHighlight(hilites[i]);
            }
        }
    }
    
    void alerte(String s) {
        JOptionPane.showMessageDialog(null, s);
    }

    // A private subclass of the default highlight painter
    class MyHighlightPainter extends DefaultHighlighter.DefaultHighlightPainter {
        public MyHighlightPainter(Color color) {
            super(color);
        }
    }

}


