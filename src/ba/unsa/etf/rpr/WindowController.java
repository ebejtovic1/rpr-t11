package ba.unsa.etf.rpr;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import net.sf.jasperreports.engine.JRException;

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

public class WindowController {
    private static GeografijaDAO baza = null;
    public Button izmjeniDrzavuButton;
    public Button dodajDrzavuButton;
    public Button izmjeniGradButton;
    public Button dodajGradButton;
    public TableView<Drzava> tabelaDrzava;
    public TableView<Grad> tabelaGradova;
    public TextField prvoPolje;
    public TextField drugoPolje;
    public TextField trecePolje;
    public TextField cetvrtoPolje;
    public Button clearButton;
    public Button obrisiDrzavuButton;
    public Button obrisiGradButton;
    public Button refreshButton;
    public Button izvjetajButton;
    public ChoiceBox<String> jezikChoiceBox;
    private DrzavaModel modelDrzava;
    private GradModel modelGradova;
    private ResourceBundle bundle;

    public TableColumn<Drzava, Integer> idDrzavaKolona;
    public TableColumn<Drzava, String> nazivDrzaveKolona;
    public TableColumn<Drzava, Grad> glavniGradKolona;

    public TableColumn<Grad, Integer> idGradKolona;
    public TableColumn<Grad, String> nazivGradKolona;
    public TableColumn<Grad, Integer> brojStanovnikaKolona;
    public TableColumn<Grad, Drzava> drzavaIdKolona;

    public WindowController(GeografijaDAO baza, DrzavaModel drzavaModel, GradModel gradModel, ResourceBundle bundle) {
        WindowController.baza = baza;
        modelDrzava = drzavaModel;
        modelGradova = gradModel;
        this.bundle = bundle;
    }

    private void disableButtona(boolean cetvrtoPoljeBool) {
        dodajDrzavuButton.setDisable(true);
        dodajGradButton.setDisable(true);
        izmjeniGradButton.setDisable(cetvrtoPoljeBool);
        izmjeniDrzavuButton.setDisable(!cetvrtoPoljeBool);
        cetvrtoPolje.setDisable(cetvrtoPoljeBool);
    }

    private void reloadScene() {
        bundle = ResourceBundle.getBundle("Translation");
        Scene scene = tabelaDrzava.getScene();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("window.fxml"), bundle);
        loader.setController(this);
        try {
            scene.setRoot(loader.load());
        } catch (IOException ignored) {

        }
    }

    @FXML
    public void initialize() {
        jezikChoiceBox.setItems(FXCollections.observableArrayList("Bosanski jezik", "Engleski jezik", "Njemacki jezik", "Francsuki jezik"));
        switch (Locale.getDefault().getCountry()) {
            case "BA":
                jezikChoiceBox.setValue("Bosanski jezik");
                break;
            case "US":
                jezikChoiceBox.setValue("Engleski jezik");
                break;
            case "DE":
                jezikChoiceBox.setValue("Njemacki jezik");
                break;
            case "FR":
                jezikChoiceBox.setValue("Francsuki jezik");
                break;
        }
        jezikChoiceBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            switch (newValue) {
                case "Bosanski jezik":
                    Locale.setDefault(new Locale("bs", "BA"));
                    reloadScene();
                    break;
                case "Engleski jezik":
                    Locale.setDefault(new Locale("en", "US"));
                    reloadScene();
                    break;
                case "Njemacki jezik":
                    Locale.setDefault(new Locale("de", "DE"));
                    reloadScene();
                    break;
                case "Francsuki jezik":
                    Locale.setDefault(new Locale("fr", "FR"));
                    reloadScene();
                    break;
            }
        });
        prvoPolje.setDisable(true);
        clearButtonClick(null);
        tabelaDrzava.setItems(modelDrzava.getDrzave());
        tabelaGradova.setItems(modelGradova.getGradovi());


        tabelaDrzava.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                tabelaGradova.getSelectionModel().clearSelection();
                modelGradova.setTrenutniGrad(null);
                disableButtona(true);
                modelDrzava.setTrenutnaDrzava(newValue);
                prvoPolje.setText(String.valueOf(newValue.getIdDrzava()));
                drugoPolje.setText(newValue.getNazivDrzave());
                trecePolje.setText(String.valueOf(newValue.getGlavniGrad().getIdGrad()));
            }
        });

        tabelaDrzava.setRowFactory(tv -> {
            TableRow<Drzava> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    izmjeniDrzavuClick(null);
                }
            });
            return row;
        });

        tabelaGradova.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                tabelaDrzava.getSelectionModel().clearSelection();
                modelDrzava.setTrenutnaDrzava(null);
                disableButtona(false);
                modelGradova.setTrenutniGrad(newValue);
                prvoPolje.setText(String.valueOf(newValue.getIdGrad()));
                drugoPolje.setText(newValue.getNazivGrad());
                trecePolje.setText(String.valueOf(newValue.getBrojStanovnika()));
                cetvrtoPolje.setText(String.valueOf(newValue.getDrzava().getIdDrzava()));
            }
        });

        tabelaGradova.setRowFactory(tv -> {
            TableRow<Grad> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    izmjeniGradClick(null);
                }
            });
            return row;
        });

        idDrzavaKolona.setCellValueFactory(new PropertyValueFactory<>("idDrzava"));
        nazivDrzaveKolona.setCellValueFactory(new PropertyValueFactory<>("nazivDrzave"));
        glavniGradKolona.setCellValueFactory(new PropertyValueFactory<>("glavniGrad"));

        idGradKolona.setCellValueFactory(new PropertyValueFactory<>("idGrad"));
        nazivGradKolona.setCellValueFactory(new PropertyValueFactory<>("nazivGrad"));
        brojStanovnikaKolona.setCellValueFactory(new PropertyValueFactory<>("brojStanovnika"));
        drzavaIdKolona.setCellValueFactory(new PropertyValueFactory<>("drzava"));
    }

    private void prikaziAlert(String title, String headerText) {
        Alert error = new Alert(Alert.AlertType.INFORMATION);
        error.setTitle(title);
        error.setHeaderText(headerText);
        error.show();
    }

    public void izmjeniDrzavuClick(ActionEvent actionEvent) {
        if (modelDrzava.getTrenutnaDrzava() == null)
            return;
        if (cetvrtoPolje.isDisabled()) {
            int id = Integer.parseInt(prvoPolje.getText());
            String naziv = drugoPolje.getText();
            int glavniGradId = Integer.parseInt(trecePolje.getText());
            baza.izmijeniDrzava(new Drzava(id, naziv, new Grad(glavniGradId, "", 0, null)));
            prikaziAlert(bundle.getString("uspjeh"), bundle.getString("uspjesnoIzmjenjenaDrzava"));
        }
    }

    public void dodajDrzavuClick(ActionEvent actionEvent) {
        if (drugoPolje.getText().isEmpty() || cetvrtoPolje.getText().isEmpty())
            return;
        String naziv = drugoPolje.getText();
        String nazivGrada = cetvrtoPolje.getText();
        baza.dodajDrzavu(new Drzava(0, naziv, new Grad(0, nazivGrada, 0, null)));
        prikaziAlert(bundle.getString("uspjeh"), bundle.getString("uspjesnoDodataDrzava"));

    }

    public void izmjeniGradClick(ActionEvent actionEvent) {
        if (modelGradova.getTrenutniGrad() == null)
            return;
        if (!cetvrtoPolje.isDisabled()) {
            int id = Integer.parseInt(prvoPolje.getText());
            String naziv = drugoPolje.getText();
            int brojStanovnika = Integer.parseInt(trecePolje.getText());
            int drzavaId = Integer.parseInt(cetvrtoPolje.getText());
            baza.izmijeniGrad(new Grad(id, naziv, brojStanovnika, new Drzava(drzavaId, "", null)));
            prikaziAlert(bundle.getString("uspjeh"), bundle.getString("uspjesnoIzmjenjenGrad"));
        }
    }

    public void dodajGradClick(ActionEvent actionEvent) {
        if (drugoPolje.getText().isEmpty() || cetvrtoPolje.getText().isEmpty())
            return;
        String naziv = drugoPolje.getText();
        int brojStanovnika = Integer.parseInt(trecePolje.getText());
        String drzavaNaziv = cetvrtoPolje.getText();
        baza.dodajGrad(new Grad(0, naziv, brojStanovnika, new Drzava(0, drzavaNaziv, null)));
        prikaziAlert(bundle.getString("uspjeh"), bundle.getString("uspjesnoDodatGrad"));
    }

    public void clearButtonClick(ActionEvent actionEvent) {
        prvoPolje.clear();
        drugoPolje.clear();
        trecePolje.clear();
        cetvrtoPolje.clear();
        cetvrtoPolje.setDisable(false);
        izmjeniGradButton.setDisable(true);
        izmjeniDrzavuButton.setDisable(true);
        dodajDrzavuButton.setDisable(false);
        dodajGradButton.setDisable(false);
        tabelaDrzava.getSelectionModel().clearSelection();
        tabelaGradova.getSelectionModel().clearSelection();
        prvoPolje.setPromptText(bundle.getString("id"));
        drugoPolje.setPromptText(bundle.getString("naziv"));
        trecePolje.setPromptText(bundle.getString("brojStanovnika") + " (" + bundle.getString("grad") + ")");
        cetvrtoPolje.setPromptText(bundle.getString("nazivGradaDrzave"));
    }


    public void izvjestajClick(ActionEvent actionEvent) {
        try {
            new GradoviReport().showReport(baza.getConn(), modelDrzava.getTrenutnaDrzava());
        } catch (JRException greska) {
            System.out.println(greska.getMessage());
        }
    }
    public void doSaveAs(ActionEvent actionEvent) {
        return;
    }



}
