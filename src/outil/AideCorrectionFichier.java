package outil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.regex.Pattern;
/**
 * La classe AideCorrectionFichier permet d'effectuer des corrections et des ajouts dans un fichier Python.
 * Elle peut ajouter un shebang au debut du fichier s'il est manquant et ajouter des commentaires pydoc
 * pour chaque fonction qui n'en a pas deja un.
 *
 *
 * @author CHEBALLAH Jawed
 * @author FWALA Yvon
 */
public class AideCorrectionFichier {
    String path;

    /**
     * Constructeur de la classe AideCorrectionFichier.
     *
     * @param path Le chemin du fichier à corriger.
     */
    public AideCorrectionFichier(String path) {
        this.path = path;
    }

    /**
     * Ajoute le shebang au début du fichier s'il est manquant.
     *
     * @return Un message decrivant l'action effectuee.
     */
    public String ajouterShebang() {
        File fichier = new File(path);
        StringBuilder result = new StringBuilder();

        // Verifier si les deux premieres lignes sont presentes
        String shebang = "#!/usr/bin/env python";
        String utf8 = "# -*- coding: utf-8 -*-";

        try (BufferedReader br = new BufferedReader(new FileReader(fichier))) {
            String ligne1 = br.readLine();
            String ligne2 = br.readLine();

            if (ligne1 != null && ligne2 != null && ligne1.equals(shebang) && ligne2.equals(utf8)) {
                result.append("Les deux premières lignes de commentaire sont déjà présentes.\n");
            } else {
                result.append("Ajout des deux premières lignes de commentaire.\n");

                // Sauvegarder le contenu actuel du fichier
                StringBuilder contenuActuel = new StringBuilder();
                contenuActuel.append(ligne1).append("\n").append(ligne2).append("\n");

                // Lire le reste du fichier et l'ajouter à contenuActuel
                String ligne;
                while ((ligne = br.readLine()) != null) {
                    contenuActuel.append(ligne).append("\n");
                }

                // Réécrire le fichier avec les deux premières lignes ajoutées
                try (FileWriter writer = new FileWriter(fichier)) {
                    writer.write(shebang + "\n" + utf8 + "\n" + contenuActuel.toString());
                } catch (IOException e) {
                    result.append("Erreur lors de l'écriture du fichier ").append(fichier.getName()).append(": ").append(e.getMessage()).append("\n");
                }
            }
        } catch (IOException e) {
            result.append("Erreur lors de la lecture du fichier ").append(fichier.getName()).append(": ").append(e.getMessage()).append("\n");
        }

        return result.toString();
    }

    /**
     * Ajoute un commentaire pydoc pour chaque fonction qui n'en a pas deja un.
     *
     * @param nom     Le nom de l'auteur du commentaire pydoc.
     * @param version La version du commentaire pydoc.
     * @return Un message decrivant les actions effectuees.
     */
    public String ajouterCommentaire(String nom, String version) {
        File fichier = new File(path);
        StringBuilder result = new StringBuilder();

        try (BufferedReader br = new BufferedReader(new FileReader(fichier))) {
            String ligne;
            StringBuilder nouveauContenu = new StringBuilder();

            while ((ligne = br.readLine()) != null) {
                if (Pattern.matches("\\s*def\\s+\\w+\\([^)]*\\):", ligne) || Pattern.matches("\\s*def\\s+\\w+\\([^)]*\\)(\\s*->\\s*\\w+)?\\s*:", ligne)) {
                    // Ajoute toujours un squelette de commentaire Pydoc
                    result.append("\t Ajout d'un squelette de commentaire pydoc pour la fonction : ").append(ligne.trim()).append("\n");
                    nouveauContenu.append(ligne).append("\n");
                    nouveauContenu.append("\t\"\"\"\n\t @" + nom + "  @version" + version + "\n\t\"\"\"\n");
                } else {
                    nouveauContenu.append(ligne).append("\n");
                }
            }

            // Réécrire le fichier avec les commentaires ajoutés
            try (FileWriter writer = new FileWriter(fichier)) {
                writer.write(nouveauContenu.toString());
            } catch (IOException e) {
                result.append("\t Erreur lors de l'écriture du fichier ").append(fichier.getName()).append(": ").append(e.getMessage()).append("\n");
            }

        } catch (IOException e) {
            result.append("\t Erreur lors de la lecture du fichier ").append(fichier.getName()).append(": ").append(e.getMessage()).append("\n");
        }

        System.out.println(result.toString());
        return result.toString();
    }
}
