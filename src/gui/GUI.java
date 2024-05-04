package gui;

import outil.Analyseur;
import outil.Statistique;
import outil.AideCorrectionFichier;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;

/**
 * Classe representant l'interface graphique de l'application.
 *
 * Cette interface permet d'interagir avec les fonctionnalites de l'application
 * de maniere conviviale. Elle offre des boutons pour parcourir les fichiers
 * Python, analyser des fichiers, ajouter des shebangs, ajouter des commentaires
 * pydoc, generer des statistiques et verifier les annotations de type.
 *
 *
 * @author CHEBALLAH Jawed
 * @author FWALA Yvon
 */

public class GUI extends JFrame {
    private static final long serialVersionUID = 1L;
    protected JTextArea resultatArea = new JTextArea(15, 40);
    protected JTextField champs = new JTextField(30);
    protected JButton parcourir = new JButton("Parcourir");
    protected JButton listerFichiersButton = new JButton("Lister Fichiers Python");
    protected JButton analyserRepertoireButton = new JButton("Analyser Repertoire");
    protected JButton analyserFichierButton = new JButton("Analyser Fichier");
    protected JButton ajouterShebang = new JButton("Ajouter Shebang");
    protected JButton ajouterCommentaire = new JButton("Ajouter Commentaire");
    protected JButton genererStats = new JButton("Generer Statistique");
    protected JButton verifierAnnotations = new JButton("Verifier Annotations");
    protected JButton help = new JButton("Help");
    private Analyseur analyseur;
    private String path = "";
    
    /**
     * Constructeur de la classe GUI.
     */

    public GUI() {
        super("Interface Graphique");

        analyseur = new Analyseur("");
        new AideCorrectionFichier("");

        initActions();
        initLayout();
    }
    /**
     * Initialise les actions des boutons.
     */

    protected void initActions() {
        parcourir.addActionListener(new ParcourirAction());
        listerFichiersButton.addActionListener(new ListerFichiersButtonAction());
        analyserRepertoireButton.addActionListener(new AnalyserRepertoireButtonAction());
        analyserFichierButton.addActionListener(new AnalyserFichierAction());
        ajouterShebang.addActionListener(new AjouterShebangAction());
        ajouterCommentaire.addActionListener(new AjouterCommentaireAction());
        genererStats.addActionListener(new GenererStatistiqueAction());
        help.addActionListener(new HelpAction());
    }
    /**
     * Initialise la mise en page de l'interface graphique.
     */

    protected void initLayout() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        JPanel buttonPanel = new JPanel(new GridLayout(2, 3, 5, 5));
        buttonPanel.add(analyserFichierButton);
        buttonPanel.add(analyserRepertoireButton);
        buttonPanel.add(listerFichiersButton);
        buttonPanel.add(parcourir);
        buttonPanel.add(genererStats);
        buttonPanel.add(ajouterShebang);
        buttonPanel.add(ajouterCommentaire);
        buttonPanel.add(help);

        JPanel textResultPanel = new JPanel();
        textResultPanel.setLayout(new BoxLayout(textResultPanel, BoxLayout.Y_AXIS));
        textResultPanel.add(champs);
        textResultPanel.add(new JScrollPane(resultatArea));

        mainPanel.add(buttonPanel, BorderLayout.CENTER);
        mainPanel.add(textResultPanel, BorderLayout.NORTH);

        Container contentPane = getContentPane();
        contentPane.add(mainPanel);

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(600, 400);
        setResizable(true);
        setVisible(true);
    }

    /**
     * Classe interne pour gerer l'action de parcourir un repertoire.
     */ 
    
    private class ParcourirAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Choisir un repertoire");
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

            int selection = fileChooser.showDialog(GUI.this, "Choisir");

            if (selection == JFileChooser.APPROVE_OPTION) {
                File selectedDirectory = fileChooser.getSelectedFile();
                path = selectedDirectory.getAbsolutePath();
                resultatArea.setText("Repertoire selectionne : " + path);
            } else {
                resultatArea.setText("Aucun repertoire selectionne.");
            }
        }
    }
    /**
     * Classe interne pour gerer l'action d'analyser un repertoire.
     */

    private class AnalyserFichierAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Choisir un fichier Python");
            fileChooser.setFileFilter(new FileNameExtensionFilter("Fichiers Python", "py"));

            int selection = fileChooser.showOpenDialog(GUI.this);

            if (selection == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                String chemin = selectedFile.getAbsolutePath();

                Analyseur analyseur = new Analyseur(chemin);
                String resultat = analyseur.analyserFichier();
                resultatArea.setText(resultat);
            } else {
                resultatArea.setText("Aucun fichier seSlectionner.");
            }
        }
    }
    /**
     * Classe interne pour gerer l'action d'analyser un repertoire.
     */

    private class AnalyserRepertoireButtonAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (!path.isEmpty()) {
                File directory = new File(path);
                if (directory.exists() && directory.isDirectory()) {
                    analyseur = new Analyseur(path);
                    String resultat = analyseur.analyserRepertoire();
                    resultatArea.setText(resultat);
                } else {
                    resultatArea.setText("Le repertoire specifie n'existe pas.");
                }
            } else {
                resultatArea.setText("Veuillez selectionner un repertoire.");
            }
        }
    }
    /**
     * Classe interne pour gerer l'action de lister les fichiers Python dans un repertoire.
     */

    private class ListerFichiersButtonAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            StringBuilder result = new StringBuilder();
            File repertoire = new File(path);

            File[] fichiers = repertoire.listFiles((dir, nom) -> nom.endsWith(".py"));

            if (fichiers != null && fichiers.length > 0) {
                result.append("Liste des fichiers Python dans le repertoire ").append(repertoire).append(" :\n");
                for (File fichier : fichiers) {
                    result.append(fichier.getName()).append("\n");
                }
            } else {
                result.append("Aucun fichier Python trouver dans le repertoire ").append(repertoire).append(".\n");
            }

            resultatArea.setText(result.toString());
        }
    }
    /**
     * Classe interne pour gerer l'action d'ajouter un shebang a un fichier Python.
     */

    private class AjouterShebangAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Choisir un fichier Python");
            fileChooser.setFileFilter(new FileNameExtensionFilter("Fichiers Python", "py"));

            int selection = fileChooser.showOpenDialog(GUI.this);

            if (selection == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                String chemin = selectedFile.getAbsolutePath();

                try (BufferedReader reader = new BufferedReader(new FileReader(chemin))) {
                    // Lire le contenu du fichier
                    StringBuilder fileContent = new StringBuilder();
                    String line;

                    // Lire ligne par ligne et stocker dans fileContent
                    while ((line = reader.readLine()) != null) {
                        fileContent.append(line).append("\n");
                    }

                    // Ajouter le shebang si le fichier est vide ou ne commence pas par un shebang
                    if (fileContent.length() == 0 || !fileContent.toString().trim().startsWith("#!")) {
                        AideCorrectionFichier correcteur = new AideCorrectionFichier(chemin);
                        String resultat = correcteur.ajouterShebang();
                        resultatArea.setText(resultat);
                    } else {
                        resultatArea.setText("Le fichier a déjà un shebang.");
                    }
                } catch (IOException ex) {
                    resultatArea.setText("Erreur lors de la lecture du fichier : " + ex.getMessage());
                }
            } else {
                resultatArea.setText("Veuillez sélectionner un fichier.");
            }
        }
    }
    /**
     * Classe interne pour gerer l'action d'ajouter un commentaire a un fichier Python.
     */  
    
    private class AjouterCommentaireAction implements ActionListener {
    	private boolean commentaireExisteDeja(String contenu) {
            return contenu.contains("\"\"\"");
        }
    	public void actionPerformed(ActionEvent e) {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Choisir un fichier Python");
            fileChooser.setFileFilter(new FileNameExtensionFilter("Fichiers Python", "py"));

            int selection = fileChooser.showOpenDialog(GUI.this);

            if (selection == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                String chemin = selectedFile.getAbsolutePath();

                // Lire le contenu du fichier pour verifier si le commentaire existe deja
                try (BufferedReader br = new BufferedReader(new FileReader(chemin))) {
                    StringBuilder contenu = new StringBuilder();
                    String line;

                    while ((line = br.readLine()) != null) {
                        contenu.append(line).append("\n");
                    }

                    if (commentaireExisteDeja(contenu.toString())) {
                        resultatArea.setText("Le commentaire existe deja dans le fichier.");
                    } else {
                        // Ajout du commentaire s'il n'existe pas deja
                        AideCorrectionFichier aideCorrection = new AideCorrectionFichier(chemin);
                        String resultat = aideCorrection.ajouterCommentaire("DEFAULT", "DEFAULT");

                        if (resultat.equals("Commentaire ajoute avec succes.")) {
                            resultatArea.setText(resultat);
                        } else {
                            resultatArea.setText("Erreur lors de l'ajout du commentaire : " + resultat);
                        }
                    }
                } catch (IOException exception) {
                    resultatArea.setText("Erreur lors de la lecture du fichier : " + exception.getMessage());
                }
            }
        }
    }
    
    /**
     * Classe interne pour gerer l'action de generer des statistiques pour un repertoire.
     */
    private class GenererStatistiqueAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (!path.isEmpty()) {
                Statistique statistiqueRepertoire = new Statistique(path);
                String statistiquesRepertoire = statistiqueRepertoire.statRepertoire();
                resultatArea.setText(statistiquesRepertoire);
            } else {
                resultatArea.setText("Veuillez selectionner un repertoire.");
            }
        }
    }
    /**
     * Classe interne pour afficher l'aide du fonctionnement.
     */
    private class HelpAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            afficherAide();
        }

        private void afficherAide() {
            // Définir le texte d'aide directement ici
            String helpText = "Pour utiliser le GUI de Yvon & Jawed\n\"" +
                              "Actuce 1:\n\"" +
                              "  Pour analyser le repertoire: veuillez cliquer sur Parcourir puis Analyser repertoire\n\"" +
                              "  Pour lister les fichier python: veuillez cliquer sur Parcourir puis Lister Fichier Python\n\"" +
                              "  Pour analyser un fichier python: veuillez cliquer sur Analyser Fichier\n\""+
                              "Actuce 2:\n" +
                              "  Pour ajouter les deux premières lignes de commentaire manquantes: veuillez cliquer sur Ajouter Shebang \n" +
                              "  Pour ajouter un squelette de commentaire pydoc sur les fonctions sans commentaire d'un fichier \n" +
                              "  Pour afficher les statistiques de qualite sur un ensemble de fichiers d'un dossier: veuillez cliquez sur Generer Statistique\n";

            // Afficher une boîte de dialogue ou une nouvelle fenêtre pour afficher le texte d'aide
            JOptionPane.showMessageDialog(GUI.this, helpText, "Aide", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    /**
     * Methode principale pour executer l'application.
     * @param args Arguments de la ligne de commande (non utilises).
     */
    	
    public static void main(String[] args) {
        new GUI();
    }
 }
