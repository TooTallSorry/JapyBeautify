package cli;

import java.io.IOException;
import outil.*;
import java.io.File;

/**
 * Classe principale de l'application Root.
 *
 * @author CHEBALLAH Jawed
 * @author FWALA Yvon
 */
public class Root {
    /**
     * Methode principale de l'application Root.
     *
     * @param args Les arguments en ligne de commande.
     * @throws IOException Si une exception d'entree/sortie se produit.
     */
    public static void main(String[] args) throws IOException {
        // Affiche le message de démarrage du programme
        System.out.println("\t-#-Le programme démarre-#-");
        String result = "";

        try {
            // Vérifie si l'option -h est spécifiée pour afficher l'aide ou que si il n'y a pas d'option cela affiche l'aide
            if (args.length == 0 || args[0].equalsIgnoreCase("-h")) {
                Help besoinAide = new Help();
                besoinAide.helpMe();
            }
            // Vérifie si l'option -d est spécifiée avec "." pour analyser le répertoire actuel
            else if (args.length >= 2 && args[0].equals("-d") && args[1].equals(".")) {
                // Utilise la fonction parcourirArborescence pour le dossier courant
                result = parcourirArborescence(new File(System.getProperty("user.dir")));

                // Vérifie si l'option --stat est spécifiée en plus de -d .
                if (args.length > 2 && args[2].equals("--stat")) {
                    Statistique stat = new Statistique(System.getProperty("user.dir"));
                    result += "\n" + stat.statRepertoire(); // Ajout de l'analyse statistique
                }

                System.out.println("\t Resultat de l'opération :\n" + result);
            }
            // Vérifie si l'option -f est spécifiée avec un fichier .py et des options supplémentaires
            else if (args.length >= 2 && args[0].equals("-f") && args[1].endsWith(".py")) {
                Verificateur verif = new Verificateur(args[1]);
                AideCorrectionFichier aide = new AideCorrectionFichier(args[1]);

                // Si l'option -f est spécifiée sans autre paramètre, effectuez toutes les analyses
                if (args.length == 2) {
                    result += verif.verifierType() + "\n";
                    result += verif.verifierShebang() + "\n";
                    result += verif.verifierPydoc() + "\n";
                } else {
                    // Sinon, analyse les options spécifiées après -f
                    for (int i = 2; i < args.length; i++) {
                        switch (args[i]) {
                            case "--type":
                                String typeResult = verif.verifierType();
                                if (typeResult.contains("annotations de type")) {
                                    result += typeResult + "\n";
                                }
                                break;
                            case "--head":
                                result += verif.verifierShebang() + "\n";
                                break;
                            case "--pydoc":
                                result += verif.verifierPydoc() + "\n";
                                break;
                            case "--sbutf8":
                                result += aide.ajouterShebang() + "\n";
                                break;
                            case "--comment":
                                // Vérifie s'il y a suffisamment d'arguments pour le nom et la version
                                if (i + 2 < args.length) {
                                    String nom = args[i + 1];
                                    String version = args[i + 2];
                                    aide.ajouterCommentaire(nom, version);
                                    result += "Commentaires PyDoc ajoutés avec succès à toutes les fonctions.\n";
                                } else {
                                    System.out.println("Erreur : L'option --comment nécessite un nom et une version en arguments.");
                                }
                                break;

                            default:
                                System.out.println("Option inconnue : " + args[i]);
                                break;
                        }
                    }
                }

                // Afficher le résultat après avoir effectué les analyses
                System.out.println("\t Resultat de l'opération :\n" + result);
            }
            // Vérifie si l'option -d est spécifiée avec un nom de dossier pour analyser les fichiers .py dans le dossier
            else if (args.length >= 2 && args[0].equals("-d")) {
                String dossierNom = args[1];
                RechercheFichier recherche = new RechercheFichier(dossierNom);
                File dossier = recherche.trouverDossier();

                if (dossier != null) {
                    // Utilisation d'une fonction récursive pour parcourir tous les fichiers Python
                    result = parcourirArborescence(dossier);

                    // Vérifie si l'option --stat est spécifiée
                    if (args.length > 2 && args[2].equals("--stat")) {
                        Statistique stat = new Statistique(dossier.getAbsolutePath());
                        result += "\n" + stat.statRepertoire(); // Ajout de l'analyse statistique
                    }

                    System.out.println("\t Resultat de l'opération :\n" + result);
                } else {
                    System.out.println("\t Aucun dossier trouvé avec le nom '" + dossierNom + "'.");
                }
            }

            // Si aucune option valide n'est spécifiée
            else {
                System.out.println("\t Erreur de paramètre !");
                System.out.println("\t Utilisez l'option -h pour obtenir de l'aide\n");
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("\t Erreur : Arguments insuffisants. Utilisez '-h' pour obtenir de l'aide.\n");
        } catch (Exception e) {
            System.out.println("\t Une erreur inattendue s'est produite : \n" + e.getMessage());
        }

        // Affiche le message de fin du programme
        System.out.println("\t-#-Le programme est terminé-#-");
    }

    /**
     * Parcours recursivement l'arborescence d'un dossier et analyse les fichiers Python (.py).
     *
     * @param dossier Le dossier à parcourir.
     * @return Une chaine de caracteres contenant les resultats des analyses des fichiers Python.
     */
    private static String parcourirArborescence(File dossier) {
        StringBuilder result = new StringBuilder();
        File[] fichiers = dossier.listFiles();

        if (fichiers != null) {
            for (File fichier : fichiers) {
                if (fichier.isDirectory()) {
                    // Appel récursif pour les sous-dossiers
                    result.append(parcourirArborescence(fichier));
                } else if (fichier.getName().endsWith(".py")) {
                    // Analyser le fichier Python
                    Analyseur coibaye = new Analyseur(fichier.getAbsolutePath());
                    result.append(coibaye.analyserFichier()).append("\n");
                }
            }
        }

        // Affiche un message si aucun fichier Python n'est trouvé dans le dossier
        if (result.length() == 0) {
            System.out.println("Aucun fichier Python trouvé dans le dossier '" + dossier.getAbsolutePath() + "'.");
        }

        return result.toString();
    }
}
