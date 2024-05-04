package outil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * La classe Verificateur permet de vérifier differents aspects d'un fichier Python, tels que la presence de la premiere
 * ligne de shebang, de commentaires pydoc dans les fonctions, et d'annotations de type.
 *
 *
 * @author CHEBALLAH Jawed
 * @author FWALA Yvon
 */
public class Verificateur {
    private String path;

    /**
     * Constructeur de la classe Verificateur.
     *
     * @param path Le chemin vers le fichier Python à verifier.
     */
    public Verificateur(String path) {
        this.path = path;
    }

    /**
     * Vérifie la presence de la premiere ligne de shebang dans le fichier.
     *
     * @return Un message indiquant si la premiere ligne de shebang est presente ou manquante.
     */
    public String verifierShebang() {
        File fichier = new File(path);
        StringBuilder result = new StringBuilder();

        try (BufferedReader br = new BufferedReader(new FileReader(fichier))) {
            String ligne;
            while ((ligne = br.readLine()) != null) {
                // Vérifie si la ligne commence par #! ou est une déclaration d'encodage
                if (ligne.trim().startsWith("#!") || ligne.trim().equals("# -*- coding: utf-8 -*-")) {
                    result.append("La première ligne de shebang est présente.");
                    return result.toString();
                }
            }
            result.append("\t La première ligne de shebang est manquante.");
        } catch (IOException e) {
            result.append("\t Erreur lors de la lecture du fichier ").append(fichier.getName()).append(": ").append(e.getMessage()).append("\n");
        }

        return result.toString();
    }
    

    /**
     * Verifie la presence de commentaires pydoc dans les fonctions du fichier.
     *
     * @return Un message indiquant le nombre de fonctions avec des commentaires pydoc.
     */
    public String verifierPydoc() {
        File fichier = new File(path);
        StringBuilder result = new StringBuilder();

        try (BufferedReader br = new BufferedReader(new FileReader(fichier))) {
            String ligne;
            boolean fonctionTrouvee = false;
            int fonctionsAvecCommentaires = 0;
            boolean enCommentairePydoc = false;

            while ((ligne = br.readLine()) != null) {
                // Vérifie si la ligne correspond à la signature d'une fonction
                if (Pattern.matches("\\s*def\\s+\\w+\\([^)]*\\):", ligne) || Pattern.matches("\\s*def\\s+\\w+\\([^)]*\\)(\\s*->\\s*\\w+)?\\s*:", ligne)) {
                    fonctionTrouvee = true;
                }
                // Si une fonction est trouvée, vérifie si elle a des commentaires pydoc
                if (fonctionTrouvee) {
                    // Vérifie si la ligne commence par """ (début de commentaire pydoc)
                    if (Pattern.matches("\\s*\"\"\".*", ligne)) {
                        enCommentairePydoc = true;
                    }

                    // Si nous sommes déjà dans un commentaire pydoc, vérifie la fin du commentaire
                    if (enCommentairePydoc) {
                        if (Pattern.matches(".*\"\"\".*", ligne)) {
                            enCommentairePydoc = false;
                            fonctionsAvecCommentaires++;
                            fonctionTrouvee = false;
                        } else {
                            // La ligne fait partie du commentaire pydoc sur plusieurs lignes
                        }
                    }
                }
            }

            // Ajoute le résultat final à la chaîne de résultat
            result.append("\t Nombre de fonctions avec commentaires pydoc :").append(fonctionsAvecCommentaires).append("\n");
        } catch (IOException e) {
            result.append("\t Erreur lors de la lecture du fichier ").append(fichier.getName()).append(": ").append(e.getMessage()).append("\n");
        }

        return result.toString();
    }


    /**
     * Verifie la présence d'annotations de type dans les fonctions du fichier.
     *
     * @return Un message indiquant le nombre de fonctions avec des annotations de type.
     */
    public String verifierType() {
        File fichier = new File(path);
        StringBuilder result = new StringBuilder();
        int fonctionsAvecAnnotations = 0; // Nouvelle variable pour compter les fonctions avec annotations

        try (BufferedReader br = new BufferedReader(new FileReader(fichier))) {
            String ligne;

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

            // Ajoute le résultat final à la chaîne de résultat
            result.append("\t Nombre de fonctions avec annotations de type :").append(fonctionsAvecAnnotations);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return result.toString();
    }

    // Fonction pour vérifier si une chaîne de caractères contient des annotations de type
    private boolean contientAnnotations(String text) {
        return text.matches(".*:\\s*\\w+.*");
    }

}

