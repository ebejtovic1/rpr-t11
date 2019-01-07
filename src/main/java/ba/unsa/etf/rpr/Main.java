package ba.unsa.etf.rpr;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.util.ResourceBundle;
import java.util.Scanner;

import static javafx.scene.layout.Region.USE_COMPUTED_SIZE;

public class Main extends Application {
    private static GeografijaDAO baza = GeografijaDAO.getInstance();

    @Override
    public void start(Stage primaryStage) throws Exception {
        ResourceBundle bundle = ResourceBundle.getBundle("Translation");
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/prozor.fxml"), bundle);
        DrzavaModel drzavaModel = new DrzavaModel(baza);
        GradModel gradModel = new GradModel(baza);
        loader.setController(new ProzorController(baza, drzavaModel, gradModel, bundle));
        Parent root = loader.load();
        primaryStage.setTitle(bundle.getString("bazaPodataka"));
        primaryStage.getIcons().add(new Image("icon.png"));
        primaryStage.setResizable(false);
        primaryStage.setScene(new Scene(root, USE_COMPUTED_SIZE, USE_COMPUTED_SIZE));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }

    private static void glavniGrad() {
        Scanner ulaz = new Scanner(System.in);
        String drzava = ulaz.nextLine();
        var grad = baza.glavniGrad(drzava);
        System.out.println("Glavni grad države " + grad.getDrzava().getNazivDrzave() + " je " + grad.getNazivGrad());
    }

    public static String ispisiGradove() {
        var gradovi = baza.gradovi();
        String result = "";
        for (var grad : gradovi)
            result += grad.gradString() + "\n";
        return result;
    }

}
