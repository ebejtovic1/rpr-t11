package ba.unsa.etf.rpr;

public class Grad {
    private int idGrad;
    private String nazivGrad;
    private int brojStanovnika;
    private Drzava drzava;

    public Grad() {
    }

    public Grad(int idGrad, String nazivGrad, int brojStanovnika, Drzava drzava) {
        this.idGrad = idGrad;
        this.nazivGrad = nazivGrad;
        this.brojStanovnika = brojStanovnika;
        this.drzava = drzava;
    }

    public int getIdGrad() {
        return idGrad;
    }

    public void setIdGrad(int idGrad) {
        this.idGrad = idGrad;
    }

    public String getNazivGrad() {
        return nazivGrad;
    }

    public void setNazivGrad(String nazivGrad) {
        this.nazivGrad = nazivGrad;
    }

    public int getBrojStanovnika() {
        return brojStanovnika;
    }

    public void setBrojStanovnika(int brojStanovnika) {
        this.brojStanovnika = brojStanovnika;
    }

    public Drzava getDrzava() {
        return drzava;
    }

    public void setDrzava(Drzava drzava) {
        this.drzava = drzava;
    }

    public String gradString() {
        if (getDrzava() == null)
            return getNazivGrad() + "()" + " - " + getBrojStanovnika();
        return getNazivGrad() + " (" + getDrzava().getNazivDrzave() + ")" + " - " + getBrojStanovnika();
    }

    @Override
    public String toString() {
        return String.valueOf(getIdGrad());
    }
}
