package ba.unsa.etf.rpr;

import java.sql.*;
import java.util.ArrayList;

public class GeografijaDAO {
    private static GeografijaDAO instance = null;
    private static Connection conn;
    private PreparedStatement upit;

    public Connection getConn() {
        return conn;
    }

    private static void initialize() {
        instance = new GeografijaDAO();
    }

    private void napuniPodacima(ArrayList<Grad> gradovi, ArrayList<Drzava> drzave) {
        Grad pariz = new Grad(1, "Pariz", 2206488, null);
        Grad london = new Grad(2, "London", 8825000, null);
        Grad bech = new Grad(3, "Beč", 1899055, null);
        Grad manchester = new Grad(4, "Manchester", 545500, null);
        Grad graz = new Grad(5, "Graz", 280200, null);
        Drzava francuska = new Drzava(1, "Francuska", pariz);
        Drzava engleska = new Drzava(2, "Velika Britanija", london);
        Drzava austrija = new Drzava(3, "Austrija", bech);
        pariz.setDrzava(francuska);
        london.setDrzava(engleska);
        bech.setDrzava(austrija);
        manchester.setDrzava(engleska);
        graz.setDrzava(austrija);
        gradovi.add(pariz);
        gradovi.add(london);
        gradovi.add(bech);
        gradovi.add(manchester);
        gradovi.add(graz);
        drzave.add(francuska);
        drzave.add(engleska);
        drzave.add(austrija);
    }

    private GeografijaDAO() {
        ArrayList<Grad> gradovi = new ArrayList<>();
        ArrayList<Drzava> drzave = new ArrayList<>();
        napuniPodacima(gradovi, drzave);
        //Class.forName("com.mysql.jdbc.Driver");
        try {
            //String url = "jdbc:sqlite:" + System.getProperty("user.dir") + "\\baza.db";
            String url = "jdbc:sqlite:baza.db";
            //String url = "jdbc:oracle:thin:@ora.db.lab.ri.etf.unsa.ba:1521:ETFLAB";
            //String username = "FP18120";
            //String password = "dsmLP88q";
            conn = DriverManager.getConnection(url);
            upit = conn.prepareStatement("PRAGMA foreign_keys = ON");
            upit.executeUpdate();
            //Izbrisi sve iz baze
            boolean gradTabelaPostoji = true, drzavaTabelaPostoji = true;
            try {
                upit = conn.prepareStatement("SELECT 'x' FROM grad");
                upit.executeQuery();
            } catch (SQLException ignored) {
                gradTabelaPostoji = false;
            }
            try {
                upit = conn.prepareStatement("SELECT 'x' FROM drzava");
                upit.executeQuery();
            } catch (SQLException ignored) {
                drzavaTabelaPostoji = false;
            }

            if (!gradTabelaPostoji || !drzavaTabelaPostoji) {
                //Tabela gradova vjerovatno ne postoji
                upit = conn.prepareStatement("create table grad (id int constraint grad_id_pk primary key, naziv varchar(50), broj_stanovnika int, drzava int constraint drzava_fk references drzava(id) on delete cascade)");
                upit.execute();
                //Tabela drzava vjerovatno ne postoji
                upit = conn.prepareStatement("create table drzava (id int constraint drzava_id_pk primary key, naziv varchar(50), glavni_grad int constraint gl_grad_fk references grad(id) on delete cascade)");
                upit.execute();

                upit = conn.prepareStatement("INSERT INTO grad VALUES (?, ?, ?, NULL)");
                for (var grad : gradovi) {
                    try {
                        upit.setInt(1, grad.getIdGrad());
                        upit.setString(2, grad.getNazivGrad());
                        upit.setInt(3, grad.getBrojStanovnika());
                        upit.executeUpdate();
                    } catch (SQLException ignored) {
                    }
                }
                upit = conn.prepareStatement("INSERT  INTO drzava VALUES(?, ?, ?)");
                for (var drzava : drzave) {
                    try {
                        upit.setInt(1, drzava.getIdDrzava());
                        upit.setString(2, drzava.getNazivDrzave());
                        upit.setInt(3, drzava.getGlavniGrad().getIdGrad());
                        upit.executeUpdate();
                    } catch (SQLException ignored) {
                    }
                }
                upit = conn.prepareStatement("UPDATE grad SET drzava = ? WHERE id = ?");
                for (var grad : gradovi) {
                    try {
                        upit.setInt(1, grad.getDrzava().getIdDrzava());
                        upit.setInt(2, grad.getIdGrad());
                        upit.executeUpdate();
                    } catch (SQLException ignored) {
                    }
                }
            }
        } catch (SQLException greska) {
            System.out.println(greska.getMessage());
        }
    }

    public static void removeInstance() {
        try {
            if (conn != null)
                conn.close();
        } catch (SQLException ignored) {

        }
        instance = null;
    }

    public static GeografijaDAO getInstance() {
        if (instance == null)
            initialize();
        return instance;
    }

    public void obrisiDrzavu(String drzava) {
        try {
            upit = conn.prepareStatement("DELETE FROM drzava WHERE naziv = ?");
            upit.setString(1, drzava);
            upit.executeUpdate();
        } catch (SQLException greska) {
            System.out.println(greska.getMessage());
        }
    }

    public void obrisiGrad(String grad) {
        try {
            upit = conn.prepareStatement("DELETE FROM grad WHERE naziv = ?");
            upit.setString(1, grad);
            upit.executeUpdate();
        } catch (SQLException greska) {
            System.out.println(greska.getMessage());
        }
    }

    public ArrayList<Grad> gradovi() {
        ArrayList<Grad> gradovi = new ArrayList<>();
        try {
            upit = conn.prepareStatement("SELECT * FROM grad ORDER BY broj_stanovnika DESC");
            ResultSet resultGradovi = upit.executeQuery();
            while (resultGradovi.next()) {
                Grad grad = new Grad();
                int idGrad = resultGradovi.getInt(1);
                grad.setIdGrad(idGrad);
                String nazivGrad = resultGradovi.getString(2);
                grad.setNazivGrad(nazivGrad);
                int brojStanovnika = resultGradovi.getInt(3);
                grad.setBrojStanovnika(brojStanovnika);
                int drzavaId = resultGradovi.getInt(4);
                grad.setDrzava(new Drzava(drzavaId, "", null));
                gradovi.add(grad);
            }
            upit = conn.prepareStatement("SELECT * FROM drzava");
            ResultSet resultDrzave = upit.executeQuery();
            while (resultDrzave.next()) {
                Drzava drzava = new Drzava();
                int idDrzava = resultDrzave.getInt(1);
                drzava.setIdDrzava(idDrzava);
                String nazivDrzave = resultDrzave.getString(2);
                drzava.setNazivDrzave(nazivDrzave);
                int glavniGradId = resultDrzave.getInt(3);
                for (var grad : gradovi) {
                    if (grad.getDrzava().getIdDrzava() == drzava.getIdDrzava()) {
                        grad.setDrzava(drzava);
                    }
                    if (glavniGradId == grad.getIdGrad())
                        drzava.setGlavniGrad(grad);
                }
            }
        } catch (SQLException greska) {
            System.out.println(greska.getMessage());
        }
        return gradovi;
    }

    public ArrayList<Drzava> drzave() {
        ArrayList<Drzava> drzave = new ArrayList<>();
        try {
            upit = conn.prepareStatement("SELECT d.id, d.naziv, d.glavni_grad FROM drzava d, grad g WHERE d.glavni_grad = g.id ORDER BY broj_stanovnika DESC");
            ResultSet resultDrzave = upit.executeQuery();
            while (resultDrzave.next()) {
                Drzava drzava = new Drzava();
                int idDrzava = resultDrzave.getInt(1);
                drzava.setIdDrzava(idDrzava);
                String nazivDrzave = resultDrzave.getString(2);
                drzava.setNazivDrzave(nazivDrzave);
                int gradId = resultDrzave.getInt(3);
                PreparedStatement podUpit = conn.prepareStatement("SELECT * FROM grad WHERE id = ?");
                podUpit.setInt(1, gradId);
                ResultSet resultGradovi = podUpit.executeQuery();
                Grad grad = new Grad();
                while (resultGradovi.next()) {
                    int idGrad = resultGradovi.getInt(1);
                    grad.setIdGrad(idGrad);
                    String nazivGrad = resultGradovi.getString(2);
                    grad.setNazivGrad(nazivGrad);
                    int brojStanovika = resultGradovi.getInt(3);
                    grad.setBrojStanovnika(brojStanovika);
                    grad.setDrzava(drzava);
                }
                drzava.setGlavniGrad(grad);
                drzave.add(drzava);
            }
        } catch (SQLException greska) {
            System.out.println(greska.getMessage());
        }
        return drzave;
    }

    public Grad glavniGrad(String drzava) {
        Grad grad = new Grad();
        try {
            upit = conn.prepareStatement("SELECT g.id, g.naziv, g.broj_stanovnika, d.id, d.naziv FROM grad g, drzava d WHERE d.glavni_grad = g.id AND d.naziv = ?");
            upit.setString(1, drzava);
            ResultSet result = upit.executeQuery();
            Drzava drzavaFk = new Drzava();
            grad.setDrzava(drzavaFk);
            drzavaFk.setGlavniGrad(grad);
            int brojac = 0;
            while (result.next()) {
                int idGrad = result.getInt(1);
                grad.setIdGrad(idGrad);
                String nazivGrad = result.getString(2);
                grad.setNazivGrad(nazivGrad);
                int brojStanovnika = result.getInt(3);
                grad.setBrojStanovnika(brojStanovnika);
                int idDrzava = result.getInt(4);
                drzavaFk.setIdDrzava(idDrzava);
                String nazivDrzave = result.getString(5);
                drzavaFk.setNazivDrzave(nazivDrzave);
                brojac++;
            }
            if (brojac == 0) {
                System.out.println("Data drzava ne postoji");
                return null;
            }
        } catch (SQLException greska) {
            System.out.println(greska.getMessage());
        }
        return grad;
    }

    public Drzava nadjiDrzavu(String drzava) {
        Drzava drzavaResult = new Drzava();
        try {
            upit = conn.prepareStatement("SELECT d.id, d.naziv, g.id, g.naziv, g.broj_stanovnika FROM drzava d, grad g WHERE d.glavni_grad = g.id AND d.naziv = ?");
            upit.setString(1, drzava);
            ResultSet result = upit.executeQuery();
            Grad glavniGrad = new Grad();
            drzavaResult.setGlavniGrad(glavniGrad);
            glavniGrad.setDrzava(drzavaResult);
            while (result.next()) {
                int idDrzava = result.getInt(1);
                drzavaResult.setIdDrzava(idDrzava);
                String nazivDrzave = result.getString(2);
                drzavaResult.setNazivDrzave(nazivDrzave);
                int idGrad = result.getInt(3);
                glavniGrad.setIdGrad(idGrad);
                String nazivGrad = result.getString(4);
                glavniGrad.setNazivGrad(nazivGrad);
                int brojStanovnika = result.getInt(5);
                glavniGrad.setBrojStanovnika(brojStanovnika);
            }
        } catch (SQLException greska) {
            System.out.println(greska.getMessage());
            return null;
        }
        return drzavaResult;
    }

    private int dajSljedeciID(String nazivTabele) throws SQLException {
        //upit = conn.prepareStatement("SELECT id FROM " + nazivTabele + " WHERE ROWNUM = 1 ORDER BY id DESC");
        upit = conn.prepareStatement("SELECT id FROM " + nazivTabele + " ORDER BY id DESC LIMIT 1");
        var result = upit.executeQuery();
        int id = 0;
        while (result.next())
            id = result.getInt(1);
        return id + 1;
    }

    private int dajGradIDAkoPostoji(String naziv) throws SQLException {
        upit = conn.prepareStatement("SELECT id FROM grad WHERE naziv = ? AND broj_stanovnika IS NULL");
        upit.setString(1, naziv);
        var result = upit.executeQuery();
        int id = -1;
        while (result.next())
            id = result.getInt(1);
        return id;
    }

    public void dodajGrad(Grad grad) {
        try {
            //Provjera da li je dati grad vec u bazi, pri cemu je broj_stanovnika = NULL
            int idAkoPostoji = dajGradIDAkoPostoji(grad.getNazivGrad());
            if (idAkoPostoji != -1) {
                grad.setIdGrad(idAkoPostoji);
                upit = conn.prepareStatement("SELECT id FROM drzava WHERE glavni_grad = ?");
                upit.setInt(1, idAkoPostoji);
                var result = upit.executeQuery();
                int id = -1;
                while (result.next())
                    id = result.getInt(1);
                Drzava temp = new Drzava();
                temp.setIdDrzava(id);
                grad.setDrzava(temp);
                izmijeniGrad(grad);
                return;
            }
            //Provjerava da li se drzava nalazi u tabeli drzava
            upit = conn.prepareStatement("SELECT id FROM drzava WHERE naziv = ?");
            upit.setString(1, grad.getDrzava().getNazivDrzave());
            ResultSet result = upit.executeQuery();
            int brojac = 0;
            int idDrzave = 0;
            while (result.next()) {
                idDrzave = result.getInt(1);
                brojac++;
            }

            //Unos novog grada
            int sljedeciIDGrad = dajSljedeciID("grad");
            upit = conn.prepareStatement("INSERT INTO grad VALUES (?, ?, ?, ?)");
            upit.setInt(1, sljedeciIDGrad);
            upit.setString(2, grad.getNazivGrad());
            upit.setInt(3, grad.getBrojStanovnika());
            if (brojac == 0)
                upit.setNull(4, Types.INTEGER);
            else
                upit.setInt(4, idDrzave);
            upit.executeUpdate();

            //Ako nema drzave brojac = 0
            if (brojac == 0) {
                int sljedeciIDDrzava = dajSljedeciID("drzava");
                //Dodaj novu drzavu
                upit = conn.prepareStatement("INSERT INTO drzava VALUES (?, ?, ?)");
                upit.setInt(1, sljedeciIDDrzava);
                upit.setString(2, grad.getDrzava().getNazivDrzave());
                upit.setInt(3, sljedeciIDGrad);
                upit.executeUpdate();
                //Postavi odgovarajuci drzava id za grad
                upit = conn.prepareStatement("UPDATE grad SET drzava = ? WHERE id = ?");
                upit.setInt(1, sljedeciIDDrzava);
                upit.setInt(2, sljedeciIDGrad);
                upit.executeUpdate();
            }
        } catch (SQLException greska) {
            System.out.println(greska.getMessage());
        }
    }

    public void dodajDrzavu(Drzava drzava) {
        try {
            //Provjera da li se glavni grad nalazi u tabeli gradova
            upit = conn.prepareStatement("SELECT id FROM grad WHERE naziv = ?");
            upit.setString(1, drzava.getGlavniGrad().getNazivGrad());
            ResultSet result = upit.executeQuery();
            int brojac = 0;
            int idGrada = 0;
            while (result.next()) {
                idGrada = result.getInt(1);
                brojac++;
            }
            int sljedeciIDDrzava = dajSljedeciID("drzava");
            //Unos nove drzave
            upit = conn.prepareStatement("INSERT INTO drzava VALUES (?, ?, ?)");
            upit.setInt(1, sljedeciIDDrzava);
            upit.setString(2, drzava.getNazivDrzave());
            if (brojac == 0)
                upit.setNull(3, Types.INTEGER);
            else
                upit.setInt(3, idGrada);
            upit.executeUpdate();
            //Ako nema glavnog grada brojac = 0
            if (brojac == 0) {
                //Dodaj novi grad
                int sljedeciIDGrad = dajSljedeciID("grad");
                upit = conn.prepareStatement("INSERT INTO grad VALUES (?, ?, NULL, ?)");
                upit.setInt(1, sljedeciIDGrad);
                upit.setString(2, drzava.getGlavniGrad().getNazivGrad());
                upit.setInt(3, sljedeciIDDrzava);
                upit.executeUpdate();

                //Postavi glavni grad u drzavi
                upit = conn.prepareStatement("UPDATE drzava SET glavni_grad = ? WHERE id = ?");
                upit.setInt(1, sljedeciIDGrad);
                upit.setInt(2, sljedeciIDDrzava);
                upit.executeUpdate();
            }
        } catch (SQLException greska) {
            System.out.println(greska.getMessage());
        }
    }

    public void izmijeniGrad(Grad grad) {
        try {
            upit = conn.prepareStatement("UPDATE grad SET naziv = ?, broj_stanovnika = ?, drzava = ? WHERE id = ?");
            upit.setString(1, grad.getNazivGrad());
            upit.setInt(2, grad.getBrojStanovnika());
            upit.setInt(3, grad.getDrzava().getIdDrzava());
            upit.setInt(4, grad.getIdGrad());

            int broj = upit.executeUpdate();
            System.out.println("Uspjesno izmjenjen " + broj + " red");
        } catch (SQLException greska) {
            System.out.println(greska.getMessage());
        }
    }

    public void izmijeniDrzava(Drzava drzava) {
        try {
            upit = conn.prepareStatement("UPDATE drzava SET naziv = ?, glavni_grad = ? WHERE id = ?");
            upit.setString(1, drzava.getNazivDrzave());
            upit.setInt(2, drzava.getGlavniGrad().getIdGrad());
            upit.setInt(3, drzava.getIdDrzava());

            int broj = upit.executeUpdate();
            System.out.println("Uspjesno izmjenjen " + broj + " red");
        } catch (SQLException greska) {
            System.out.println(greska.getMessage());
        }
    }
}
