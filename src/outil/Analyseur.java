package outil;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Pattern;
import java.io.File;
/**
 * La classe Analyseur permet d'analyser des fichiers Python specifies ou tout un repertoire de fichiers Python.
 * Elle peut verifier le shebang, les commentaires pydoc, les types de retour des fonctions, et compter le nombre de fonctions
 * dans les fichiers Python.
 *
 *
 * @author CHEBALLAH Jawed
 * @author FWALA Yvon
 */
public class Analyseur {
    private String path;

    /**
     * Constructeur de la classe Analyseur.
     *
     * @param path Le chemin du fichier ou du repertoire à analyser.
     */
    public Analyseur(String path) {
        this.path = path;
    }

    /**
     * Analyse le fichier Python specifie.
     *
     * @return Un rapport d'analyse du fichier.
     */
    public String analyserFichier() {
        StringBuilder result = new StringBuilder();
        Verificateur verif = new Verificateur(path);
        result.append("Nom du fichier : ").append(new File(path).getName()).append("\n");
        result.append(verif.verifierShebang());
        result.append(verif.verifierPydoc());
        result.append(verif.verifierType());
        result.append(compteurDeFonctions());

        return result.toString();
    }

    /**
     * Analyse tous les fichiers Python dans le repertoire specifie.
     *
     * @return Un rapport d'analyse de tous les fichiers du repertoire.
     */
    public String analyserRepertoire() {
        StringBuilder result = new StringBuilder();
        File repertoire = new File(path);

        // Liste tous les fichiers Python dans le répertoire
        File[] fichiers = repertoire.listFiles((dir, nom) -> nom.endsWith(".py"));

        if (fichiers != null && fichiers.length > 0) {
            for (File fichier : fichiers) {
                Verificateur verif = new Verificateur(fichier.getAbsolutePath());
                Analyseur analyseur = new Analyseur(fichier.getAbsolutePath());
                result.append("Analyse du fichier ").append(fichier.getName()).append(" :\n");
                result.append(verif.verifierShebang());
                result.append(verif.verifierPydoc());
                result.append(verif.verifierType());
                result.append(analyseur.compteurDeFonctions());
                result.append("\n");
            }
        } else {
            result.append("\t Aucun fichier Python trouvé dans le répertoire ").append(repertoire).append(".\n");
        }

        return result.toString();
    }

    /**
     * Compte le nombre de fonctions dans le fichier Python specifie.
     *
     * @return Le nombre de fonctions dans le fichier.
     */
    public String compteurDeFonctions() {
        File fichier = new File(path);
        StringBuilder result = new StringBuilder();

        try (BufferedReader br = new BufferedReader(new FileReader(fichier))) {
            String ligne;
            int nbFonctions = 0;

            while ((ligne = br.readLine()) != null) {
                // Vérifie si la ligne correspond à une déclaration de fonction
                if (Pattern.matches("\\s*def\\s+\\w+\\([^)]*\\):", ligne) || Pattern.matches(("\\s*def\\s+\\w+\\([^)]*\\)(\\s*->\\s*\\w+)?\\s*:"), ligne)) {
                    nbFonctions++;
                }
            }
            result.append("\t Nombre de fonctions : ").append(nbFonctions).append("\n");

        } catch (IOException e) {
            result.append("\t Erreur lors de la lecture du fichier ").append(fichier.getName()).append(": ").append(e.getMessage()).append("\n");
        }

        return result.toString();
    }
}
