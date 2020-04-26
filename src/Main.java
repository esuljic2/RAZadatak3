import javafx.util.Pair;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class Main {

    public static void main(String... args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Unesite putanju datoteke sa instrukcijama (ili pritisnite enter da koristite zadanu)");
        String putanja = scanner.nextLine();

        if (putanja.isEmpty()) putanja = "instrukcije.txt";

        ArrayList<Instrukcija> instrukcije = ucitajInstrukcije(putanja);

        ArrayList<Pair<Instrukcija, Instrukcija>> instrukcijeSaZadrskama = dajInstrukcijeZadrske(instrukcije);

        if (instrukcijeSaZadrskama.size() == 0) {
            System.out.println("Nisu potrebne instrukcije zadrske za ovu sekvencu instrukcija");
            return;
        }

        String odrediste = putanja.substring(0, putanja.lastIndexOf(".")) + "New" + putanja.substring(putanja.lastIndexOf("."));

        zapisiInstrukcije(instrukcijeSaZadrskama, odrediste);

        System.out.println("Datoteka sa instrukcijama zadrske: " + odrediste);
    }

    public static void zapisiInstrukcije(ArrayList<Pair<Instrukcija, Instrukcija>> instrukcijeSaZadrskama, String putanja) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(putanja));
            for (Pair<Instrukcija, Instrukcija> par : instrukcijeSaZadrskama) {
                writer.write(par.getKey().toString());
                if (par.getValue() == null)
                    writer.write(" || nije moguce pronaci instrukciju zadrske");
                else
                    writer.write(" || a njena instrukcija zadrske je " + par.getValue().toString());
                writer.newLine();
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<Pair<Instrukcija, Instrukcija>> dajInstrukcijeZadrske(List<Instrukcija> instrukcije) {
        ArrayList<Pair<Instrukcija, Instrukcija>> instrukcijeSaZadrskama = new ArrayList<>();

        for (int i = 0; i < instrukcije.size(); i++) {
            Instrukcija instrukcija = instrukcije.get(i);

            if (instrukcija.jeLiBranchInstrukcija()) {
                String labelaBrancha = instrukcija.dajLabeluBrancha();

                //Prvo trazimo slucaj a, dakle da li je instrukcija koja je prije trenutne
                //validna da bude instrukcija zadrske
                if (i != 0) {
                    Instrukcija instrukcijaPrije = instrukcije.get(i - 1);
                    if (!instrukcijaPrije.jeLiBranchInstrukcija()) {
                        String odredisniRegistar = instrukcijaPrije.dajOdredisniRegistar();
                        //Ona moze biti instrukcija zadrske ukoliko ne mijenja registre
                        //ili ako njen rezultat nije bitan za branch instrukciju
                        if (odredisniRegistar == null || !instrukcija.daLiKoristiRegistar(odredisniRegistar)) {
                            instrukcijeSaZadrskama.add(new Pair<>(instrukcija, instrukcijaPrije));
                            continue;
                        }
                    }
                }

                //Slucaj b, instrukcija zadrske je ona na koju se skace ukoliko je uslov
                //brancha zadovoljen
                Optional<Instrukcija> odredisnaInstrukcija = instrukcije.stream().filter(instrukcija1 -> instrukcija1.getLabela() != null && instrukcija1.getLabela().equals(labelaBrancha)).findFirst();
                if (odredisnaInstrukcija.isPresent()) {
                    String odredisniRegistar = odredisnaInstrukcija.get().dajOdredisniRegistar();
                    //Ona moze biti instrukcija zadrske ukoliko ne mijenja registre
                    //ili ako njen rezultat nije bitan za branch instrukciju
                    if (odredisniRegistar == null || !instrukcija.daLiKoristiRegistar(odredisniRegistar)) {
                        if (!odredisnaInstrukcija.get().jeLiBranchInstrukcija()) {
                            instrukcijeSaZadrskama.add(new Pair<>(instrukcija, odredisnaInstrukcija.get()));
                            continue;
                        }
                    }
                }

                //Slucaj c, instrukcija zadrske je ona koja je poslije trenutne, to jest ona koja
                //ce se izvrsiti ukoliko uslov grananja nije zadovoljen
                if (i + 1 < instrukcije.size()) {
                    Instrukcija sljedecaInstrukcija = instrukcije.get(i + 1);
                    String odredisniRegistar = sljedecaInstrukcija.dajOdredisniRegistar();
                    //Ona moze biti instrukcija zadrske ukoliko ne mijenja registre
                    //ili ako njen rezultat nije bitan za branch instrukciju
                    if (odredisniRegistar == null || !instrukcija.daLiKoristiRegistar(odredisniRegistar)) {
                        if (!sljedecaInstrukcija.jeLiBranchInstrukcija()) {
                            instrukcijeSaZadrskama.add(new Pair<>(instrukcija, sljedecaInstrukcija));
                            continue;
                        }
                    }
                }

                instrukcijeSaZadrskama.add(new Pair<>(instrukcija, null));
            }
        }

        return instrukcijeSaZadrskama;
    }

    public static ArrayList<Instrukcija> ucitajInstrukcije(String putanja) {
        ArrayList<Instrukcija> ret = new ArrayList<>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(putanja));
            String line = reader.readLine();
            while (line != null) {
                String[] values = line.split(" ");
                //Ukoliko values ima 2 elementa, prvi je ime instrukcije, a drugi su parametri
                //a ukoliko ima 3, prvi je naziv labele

                if (values.length == 2)
                    ret.add(new Instrukcija(values[0], values[1]));
                else
                    ret.add(new Instrukcija(values[0], values[1], values[2]));

                line = reader.readLine();
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ret;
    }
}
