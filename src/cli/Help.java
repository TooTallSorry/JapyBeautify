package cli;

/**
 * Classe representant l'utilitaire d'aide.
 *
 * @author CHEBALLAH Jawed
 * @author FWALA Yvon
 */
public class Help {

    /**
     * Constructeur par defaut de la classe Help.
     */
    public Help() {
    }

    /**
     * Affiche les informations d'utilisation de l'application.
     */
    public void helpMe() {
        System.out.println("Utilisation: java -jar Root.jar [option] [option2] [option3] (option 3 valable uniquement avec --comment)\n");
        System.out.println("Options 1:");
        System.out.println("  -d, --directory\tAnalyser un répertoire");
        System.out.println("  -f, --file\t\tAnalyser un fichier");
        System.out.println("Options 2:");
        System.out.println("  --type\t\tVérifier les annotations de type d'un fichier .py");
        System.out.println("  --head\t\tVérifier les deux premières lignes de commentaire d'un fichier .py");
        System.out.println("  --pydoc\t\tVérifier les commentaires de fonction au format pydoc d'un fichier .py");
        System.out.println("  --sbutf8\t\tAjouter les deux premières lignes de commentaire manquantes (shebang et utf-8) d'un fichier .py");
        System.out.println("  --comment\t\tAjouter un squelette de commentaire pydoc sur les fonctions sans commentaire d'un fichier .py");
        System.out.println("   avec nom et version Exemple --comment NOM VERSION");
        System.out.println("  --stat\t\tAfficher les statistiques de qualité sur un ensemble de fichiers d'un dossier");
    }
}