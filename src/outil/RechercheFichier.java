package outil;

import java.io.File;
import java.io.IOException;
/**
 * La classe RechercheFichier permet de trouver un dossier specifie par nom dans le repertoire courant.
 * Elle liste tous les dossiers du repertoire courant et recherche le dossier avec le nom specifie.
 *
 *
 * @author CHEBALLAH Jawed
 * @author FWALA Yvon
 */
public class RechercheFichier {
    private String nomDossier;

    /**
     * Constructeur de la classe RechercheFichier.
     *
     * @param nomDossier Le nom du dossier a rechercher.
     */
    public RechercheFichier(String nomDossier) {
        this.nomDossier = nomDossier;
    }

    /**
     * Trouve le dossier specifie par nomDossier dans le repertoire courant.
     *
     * @return Le dossier trouve ou null s'il n'est pas trouve.
     */
    public File trouverDossier() {
        // Liste tous les dossiers dans le repertoire courant
        File[] dossiers = new File(".").listFiles(File::isDirectory);

        for (File dossier : dossiers) {
            try {
                // Verifie si le nom du dossier correspond a nomDossier
                if (dossier.getName().equals(nomDossier) || dossier.getCanonicalFile().getName().equals(nomDossier)) {
                    return dossier.getCanonicalFile();
                }
            } catch (IOException e) {
                // Gerer les exceptions appropriees ici
                e.printStackTrace();
            }
        }

        return null; // Aucun dossier trouve avec le nom spécifie
    }
}
