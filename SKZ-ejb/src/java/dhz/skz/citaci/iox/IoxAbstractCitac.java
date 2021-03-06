/*
 * Copyright (C) 2016 kraljevic
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package dhz.skz.citaci.iox;

import com.csvreader.CsvReader;
import dhz.skz.aqdb.entity.ProgramMjerenja;
import dhz.skz.aqdb.facades.AbstractFacade;
import dhz.skz.validatori.Validator;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.SocketTimeoutException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author kraljevic
 * @param <T>
 */
public abstract class IoxAbstractCitac<T> {

    protected static final Logger log = Logger.getLogger(IoxAbstractCitac.class.getName());

    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

    private Collection<T> podaci;

    private CsvReader csv;
    private Date aktualnoVrijeme, vrijemeZadnjeg = new Date(0L); //ruzno
    private ProgramMjerenja aktualniProgram;
    private Map<ProgramMjerenja, Validator> validatori;
    private Map<String, ProgramMjerenja> mapa;
    private AbstractFacade dao;
    private IoxKonekcija iocon;
    private Integer period;
    private String dBstr;

    public void setProgramMapa(Map<String, ProgramMjerenja> mapa) {
        this.mapa = mapa;
    }

    public void setValidatoriMapa(Map<ProgramMjerenja, Validator> validatori) {
        this.validatori = validatori;
    }

    public void setDaoFacade(AbstractFacade dao) {
        this.dao = dao;
    }

    public void setVrijemeZadnjeg(Date vrijemeZadnjeg) {
        this.vrijemeZadnjeg = vrijemeZadnjeg;
    }

    public void setKonekcija(IoxKonekcija iocon) {
        this.iocon = iocon;
    }

    public void setPeriod(Integer period) {
        this.period = period;
    }

    public void setdBstr(String dBstr) {
        this.dBstr = dBstr;
    }

    public void dodajPodatak(T ps) {
        podaci.add(ps);
    }

    public Collection<T> getPodaci() {
        return podaci;
    }

    public ProgramMjerenja getAktualniProgram() {
        return aktualniProgram;
    }

    public Date getAktualnoVrijeme() {
        return aktualnoVrijeme;
    }

    public CsvReader getCsv() {
        return csv;
    }

    public Map<ProgramMjerenja, Validator> getValidatori() {
        return validatori;
    }

    public Map<String, ProgramMjerenja> getMapa() {
        return mapa;
    }

    public AbstractFacade getDao() {
        return dao;
    }

    public Date getVrijemeZadnjeg() {
        return vrijemeZadnjeg;
    }

    public void procitaj() {
        odrediVrijemeZadnjegPodatka();

        Date sada = new Date();
        podaci = new ArrayList<>();

        while (vrijemeZadnjeg.before(sada)) {
            
            try (BufferedReader in = new BufferedReader(new InputStreamReader(iocon.getInputStream(period.toString(), dBstr, vrijemeZadnjeg)))) {
                String line = null;
                line = in.readLine();
                line = in.readLine();
                line = in.readLine();
                line = in.readLine();
                line = in.readLine();
                line = in.readLine();
                csv = new CsvReader(in, '\t');
                csv.readHeaders();
                int n = csv.getHeaderCount();
                while (csv.readRecord()) {
                    try {
                        aktualnoVrijeme = sdf.parse(csv.get("Time"));
                        aktualniProgram = getProgram();
                        if (aktualniProgram != null) {
                            parseLinija();
                        }
                    } catch (ParseException ex) {
                        log.log(Level.SEVERE, null, ex);
                    }

                }
                vrijemeZadnjeg = new Date(vrijemeZadnjeg.getTime() + period*3600000);
            } catch (SocketTimeoutException ex){
                log.log(Level.SEVERE, "java.net.SocketTimeoutException: Read timed out at: {0} ", new Object[]{iocon.getUrlPredlozak()});
                break;
            } catch (IOException ex) {
                log.log(Level.SEVERE, null, ex);
                break;
            }
        }
        validiraj();
        spremi();
    }

    protected ProgramMjerenja getProgram() throws IOException {
        ProgramMjerenja pm = mapa.get(csv.get("Device") + "::" + csv.get("Component"));
        if (pm == null) {
            log.log(Level.FINE, "Nema programa za {0} :: {1}", new Object[]{csv.get("Device"), csv.get("Component")});
        }
        return pm;
    }

    private void spremi() {
        for ( T p : podaci) {
            dao.create(p);
        }
    }

    abstract protected void parseLinija() throws IOException;

    abstract protected void validiraj();

    abstract protected void odrediVrijemeZadnjegPodatka();
}
