package ba.unsa.etf.rpr;

public class Drzava {
    private int idDrzava;
    private String nazivDrzave;
    private Grad glavniGrad;

    public Drzava() {
    }

    public Drzava(int idDrzava, String nazivDrzave, Grad glavniGrad) {
        this.idDrzava = idDrzava;
        this.nazivDrzave = nazivDrzave;
        this.glavniGrad = glavniGrad;
    }

    public int getIdDrzava() {
        return idDrzava;
    }

    public void setIdDrzava(int idDrzava) {
        this.idDrzava = idDrzava;
    }

    public String getNazivDrzave() {
        return nazivDrzave;
    }

    public void setNazivDrzave(String nazivDrzave) {
        this.nazivDrzave = nazivDrzave;
    }

    public Grad getGlavniGrad() {
        return glavniGrad;
    }


    public void setGlavniGrad(Grad glavniGrad) {
        this.glavniGrad = glavniGrad;
    }

    public String drzavaString() {
        return getNazivDrzave();
    }

    @Override
    public String toString() {
        return String.valueOf(getIdDrzava());
    }
}
