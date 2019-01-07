package ba.unsa.etf.rpr;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class GradModel {
    private ObservableList<Grad> gradovi = FXCollections.observableArrayList();
    private ObjectProperty<Grad> trenutniGrad = new SimpleObjectProperty<>();

    public GradModel(GeografijaDAO baza) {
        var gradovi = baza.gradovi();
        this.gradovi.setAll(gradovi);
    }

    public ObjectProperty<Grad> trenutniGradProperty() {
        return trenutniGrad;
    }

    public Grad getTrenutniGrad() {
        return trenutniGrad.get();
    }

    public void setTrenutniGrad(Grad k) {
        trenutniGrad.set(k);
    }

    public ObservableList<Grad> getGradovi() {
        return gradovi;
    }

    public void setGradovi(ObservableList<Grad> gradovi) {
        this.gradovi = gradovi;
    }

    public void ispisiGradovi() {
        System.out.println("Gradovi su:\n" + dajGradovi());
    }

    public String dajGradovi() {
        String rezultat = "";
        for (var Grad : gradovi)
            rezultat += Grad + "\n";
        return rezultat;
    }

    public void deleteGrad() {
        gradovi.remove(getTrenutniGrad());
        setTrenutniGrad(null);
    }

    public void addGrad(Grad Grad) {
        gradovi.add(Grad);
        setTrenutniGrad(Grad);
    }

    public void refresh(GeografijaDAO baza) {
        var gradovi = baza.gradovi();
        this.gradovi.setAll(gradovi);
    }
}
