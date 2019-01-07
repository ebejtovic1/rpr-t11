package ba.unsa.etf.rpr;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class DrzavaModel {
    private ObservableList<Drzava> drzave = FXCollections.observableArrayList();
    private ObjectProperty<Drzava> trenutnaDrzava = new SimpleObjectProperty<>();

    public DrzavaModel(GeografijaDAO baza) {
        var drzave = baza.drzave();
        this.drzave.setAll(drzave);
    }

    public ObjectProperty<Drzava> trenutnaDrzavaProperty() {
        return trenutnaDrzava;
    }

    public Drzava getTrenutnaDrzava() {
        return trenutnaDrzava.get();
    }

    public void setTrenutnaDrzava(Drzava k) {
        trenutnaDrzava.set(k);
    }

    public ObservableList<Drzava> getDrzave() {
        return drzave;
    }

    public void setDrzave(ObservableList<Drzava> drzave) {
        this.drzave = drzave;
    }

    public void ispisiDrzave() {
        System.out.println("Drzave su:\n" + dajDrzave());
    }

    public String dajDrzave() {
        String rezultat = "";
        for (var Drzava : drzave)
            rezultat += Drzava + "\n";
        return rezultat;
    }

    public void deleteDrzava() {
        drzave.remove(getTrenutnaDrzava());
        setTrenutnaDrzava(null);
    }

    public void addDrzava(Drzava Drzava) {
        drzave.add(Drzava);
        setTrenutnaDrzava(Drzava);
    }

    public void refresh(GeografijaDAO baza) {
        var drzave = baza.drzave();
        this.drzave.setAll(drzave);
    }
}
