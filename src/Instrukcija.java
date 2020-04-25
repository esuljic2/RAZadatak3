import java.util.ArrayList;
import java.util.Arrays;

public class Instrukcija {
    private String ime;
    private String parametri;
    private String labela;

    public Instrukcija(String ime, String parametri) {
        this.ime = ime.toUpperCase();
        this.parametri = parametri.toUpperCase();
    }

    public Instrukcija(String labela, String ime, String parametri) {
        this.labela = labela.toLowerCase();
        this.ime = ime.toUpperCase();
        this.parametri = parametri.toUpperCase();
    }

    public boolean jeLiBranchInstrukcija() {
        return ime.equals("BNE") || ime.equals("BEQ");
    }

    public String dajLabeluBrancha() {
        return parametri.substring(parametri.lastIndexOf(',') + 1).toLowerCase();
    }

    public String dajOdredisniRegistar() {
        if (ime.equals("SW") || ime.equals("SH") || ime.equals("SB"))
            //Ukoliko je instrukcija STORE instrukcija, ona nema odredisni registar
            return null;

        if (jeLiBranchInstrukcija())
            //Branch instrukcije nemaju odredisni registar
            return null;

        return parametri.split(",")[0];
    }

    public boolean daLiKoristiRegistar(String imeRegistra) {
        ArrayList registri = new ArrayList<>(Arrays.asList(parametri.split(",")));

        if (jeLiBranchInstrukcija()) {
            return registri.get(0).equals(imeRegistra) || registri.get(1).equals(imeRegistra);
        } else {
            return registri.get(1).equals(imeRegistra) || registri.get(2).equals(imeRegistra);
        }
    }

    @Override
    public String toString() {
        return ime + " " + parametri;
    }

    public String getLabela() {
        return labela;
    }
}
