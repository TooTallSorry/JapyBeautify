package outil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 * La classe Statistique permet d'effectuer des statistiques sur un repertoire specifie contenant des fichiers Python.
 * Elle analyse les fichiers pour compter les statistiques telles que la presence de shebang, de commentaires pydoc
 * et d'annotations de type.
 *
 *
 * @author CHEBALLAH Jawed
 * @author FWALA Yvon
 */
public class Statistique {
    private String path;

    /**
     * Constructeur de la classe Statistique.
     *
     * @param path Le chemin vers le repertoire ou le fichier a analyser.
     */
    public Statistique(String path) {
        this.path = path;
    }

    /**
     * Effectue des statistiques sur le repertoire spécifie, y compris la presence de shebang, de commentaires pydoc
     * et d'annotations de type dans les fichiers Python.
     *
     * @return Les statistiques sous forme de chaîne de caracteres.
     */
    public String statRepertoire() {
        StringBuilder result = new StringBuilder();
        File repertoire = new File(path);
        int nbShebang = 0;
        int nbStatPydoc = 0;
        int nbStatType = 0;
        int nbTotalFichiers = 0;
        int nbFonctionTotal = 0;

        // Liste tous les fichiers Python dans le répertoire
        File[] fichiers = repertoire.listFiles((dir, nom) -> nom.endsWith(".py"));

        if (fichiers != null && fichiers.length > 0) {
            nbTotalFichiers = fichiers.length;

            for (File fichier : fichiers) {
                Statistique coibaye = new Statistique(fichier.getAbsolutePath());

                if (coibaye.statShebang()) {
                    nbShebang++;
                }

                nbStatPydoc += coibaye.verifierPydoc();
                nbStatType += coibaye.verifierType();
                nbFonctionTotal += coibaye.compteurDeFonctions();
            }

            // Calcul des pourcentages
            double pourcentageShebang = ((double) nbShebang / nbTotalFichiers) * 100;
            double pourcentagePydoc = (nbFonctionTotal > 0) ? ((double) nbStatPydoc / (nbTotalFichiers * nbFonctionTotal)) * 100 : 0;
            double pourcentageType = (nbFonctionTotal > 0) ? ((double) nbStatType / (nbTotalFichiers * nbFonctionTotal)) * 100 : 0;

            result.append("Statistiques sur le répertoire :\n");
            result.append("Pourcentage de fichiers avec shebang : ").append(pourcentageShebang).append("%\n");
            result.append("Pourcentage de fichiers avec pydoc : ").append(pourcentagePydoc).append("%\n");
            result.append("Pourcentage de fichiers avec annotations de type : ").append(pourcentageType).append("%\n");
        } else {
            result.append("Aucun fichier Python trouvé dans le répertoire ").append(repertoire).append(".\n");
        }

        return result.toString();
    }
    /**
     * Verifie la présence de la première ligne de shebang dans le fichier specifie.
     *
     * @return true si la premiere ligne de shebang est présente, sinon false.
     */
    public boolean statShebang() {
        File fichier = new File(path);
        StringBuilder result = new StringBuilder();

        try (BufferedReader br = new BufferedReader(new FileReader(fichier))) {
            String ligne;
            while ((ligne = br.readLine()) != null) {
                if (ligne.trim().startsWith("#!") || ligne.trim().equals("# -*- coding: utf-8 -*-")) {
                    result.append("La première ligne de shebang est présente.\n");
                    return true;
                }
            }
            result.append("La première ligne de shebang est manquante.\n");
        } catch (IOException e) {
            result.append("Erreur lors de la lecture du fichier ").append(fichier.getName()).append(": ").append(e.getMessage()).append("\n");
        }

        return false;
    }

    /**
     * Verifie le nombre de fonctions avec des commentaires pydoc dans le fichier specifie.
     *
     * @return Le nombre de fonctions avec des commentaires pydoc.
     */
    public int verifierPydoc() {
        File fichier = new File(path);
        StringBuilder result = new StringBuilder();

        try (BufferedReader br = new BufferedReader(new FileReader(fichier))) {
            String ligne;
            boolean fonctionTrouvee = false;
            int fonctionsAvecCommentaires = 0;
            boolean enCommentairePydoc = false;

            while ((ligne = br.readLine()) != null) {
                if (Pattern.matches("\\s*def\\s+\\w+\\([^)]*\\):", ligne) || Pattern.matches("\\s*def\\s+\\w+\\([^)]*\\)(\\s*->\\s*\\w+)?\\s*:", ligne)) {
                    fonctionTrouvee = true;
                }

                if (fonctionTrouvee) {
                    if (Pattern.matches("\\s*\"\"\".*", ligne)) {
                        enCommentairePydoc = true;
                    }

                    if (enCommentairePydoc && Pattern.matches(".*\"\"\".*", ligne)) {
                        enCommentairePydoc = false;
                        fonctionsAvecCommentaires++;
                        fonctionTrouvee = false;
                    }
                }
            }

            return fonctionsAvecCommentaires;
        } catch (IOException e) {
            result.append("Erreur lors de la lecture du fichier ").append(fichier.getName()).append(": ").append(e.getMessage()).append("\n");
        }

        return 0;
    }

    /**
     * Verifie le nombre de lignes avec des annotations de type dans le fichier specifie.
     *
     * @return Le nombre de lignes avec des annotations de type.
     */
    public int verifierType() {
        File fichier = new File(path);
        StringBuilder result = new StringBuilder();

        try (BufferedReader br = new BufferedReader(new FileReader(fichier))) {
            String ligne;
            int fonctionsAvecAnnotations = 0; // Nouvelle variable pour compter les fonctions avec annotations

            while ((ligne = br.readLine()) != null) {
                // Vérifie si la ligne contient une déclaration de fonction
                if (ligne.contains("def ")) {
                    Matcher matcher = Pattern.compile("def\\s+(\\w+)\\s*\\((.*)\\)\\s*->\\s*(\\w+)").matcher(ligne);

                    if (matcher.find()) {
                        String functionName = matcher.group(1);
                        String parameters = matcher.group(2);
                        String returnType = matcher.group(3);

                        // Vérifie si les paramètres ou le type de retour contiennent des annotations
                        if (contientAnnotations(parameters) || (!returnType.equals("None") && contientAnnotations(returnType))) {
                            fonctionsAvecAnnotations++;
                            result.append("\t La fonction '").append(functionName).append("' contient des annotations de type.\n");
                        }
                    }
                }
            }
            return fonctionsAvecAnnotations;

        } catch (IOException e) {
            e.printStackTrace();
        }

        return 0;
    }

    /**
     * Compte le nombre de fonctions dans le fichier spécifie.
     *
     * @return Le nombre de fonctions dans le fichier.
     */
    public int compteurDeFonctions() {
        File fichier = new File(path);
        StringBuilder result = new StringBuilder();

        try (BufferedReader br = new BufferedReader(new FileReader(fichier))) {
            String ligne;
            int nbFonctions = 0;

            while ((ligne = br.readLine()) != null) {
                if (Pattern.matches("\\s*def\\s+\\w+\\([^)]*\\):", ligne) || Pattern.matches(("\\s*def\\s+\\w+\\([^)]*\\)(\\s*->\\s*\\w+)?\\s*:"), ligne)) {
                    nbFonctions++;
                }
            }

            return nbFonctions;

        } catch (IOException e) {
            result.append("Erreur lors de la lecture du fichier ").append(fichier.getName()).append(": ").append(e.getMessage()).append("\n");
        }

        return 0;
    }
    private boolean contientAnnotations(String text) {
        return text.matches(".*:\\s*\\w+.*");
    }
}
