/*
 * Copyright (C) 2015 kraljevic
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
package dhz.skz.citaci.mlu;

import dhz.skz.aqdb.facades.ProgramMjerenjaFacade;
import dhz.skz.aqdb.facades.ZeroSpanFacade;
import dhz.skz.aqdb.entity.IzvorPodataka;
import dhz.skz.aqdb.entity.Postaja;
import dhz.skz.aqdb.entity.ProgramMjerenja;
import dhz.skz.aqdb.entity.ZeroSpan;
import dhz.skz.webservis.omotnica.CsvOmotnica;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author kraljevic
 */
public class ZeroSpanPrihvat implements OmotnicaPrihvat{
    private static final Logger log = Logger.getLogger(ZeroSpanPrihvat.class.getName());

    private final ZeroSpanFacade zeroSpanFacade;
    private final ProgramMjerenjaFacade programMjerenjaFacade;
    private Map<Integer, ProgramMjerenja> mapa;
    private Map<Integer, String> modMapa;

    private CsvOmotnica omotnica;
    private Postaja postaja;
    private IzvorPodataka izvor;

    public ZeroSpanPrihvat(ProgramMjerenjaFacade programMjerenjaFacade, ZeroSpanFacade zeroSpanFacade) {
        this.programMjerenjaFacade = programMjerenjaFacade;
        this.zeroSpanFacade = zeroSpanFacade;
    }
    
    @Override
    public void prihvati(CsvOmotnica omotnica, Postaja postaja, IzvorPodataka izvor){
        this.omotnica = omotnica;
        this.postaja = postaja;
        this.izvor = izvor;
        this.mapa = new HashMap<>();
        this.modMapa = new HashMap<>();

        Collection<ZeroSpan> podaci = new ArrayList<>();

        parseHeaders(omotnica.getHeaderi());

        Iterator<Long> it = omotnica.getVremena().iterator();
        for (String[] linija : omotnica.getLinije()) {
            parseLinija(linija, it.next(), podaci);
        }
        log.log(Level.INFO, "BROJ ZS: {0} {1} {2}", new Object[]{omotnica.getVremena().size(), omotnica.getLinije().size(), podaci.size()});

        zeroSpanFacade.spremi(podaci);
    }

    private void parseHeaders(String[] headeri) {
        String datoteka = omotnica.getDatoteka();
        for (Integer i = 1; i < headeri.length; i++) {
            String str = headeri[i];
            if (str.length() > 5) {
                String kraj = str.substring(str.length() - 5);
                String pocetak = str.substring(0, str.length() - 5);
                ProgramMjerenja pm = programMjerenjaFacade.find(postaja, izvor, pocetak, datoteka);
                if (pm != null) {
                    mapa.put(i, pm);
                    if (kraj.compareToIgnoreCase("_Span") == 0) {
                        modMapa.put(i, "AS");
                    } else if (kraj.compareToIgnoreCase("_Zero") == 0) {
                        modMapa.put(i, "AZ");
                    } else {
                        log.log(Level.INFO, "Los header {0} na poziciji {1}", new Object[]{str, i});
                    }
                } else {
                    log.log(Level.SEVERE, "NEMA PROGRAMA");
                }
            } else {
                log.log(Level.INFO, "Los header {0} na poziciji {1}", new Object[]{str, i});
            }
        }
    }

    private void parseLinija(String[] linija, Long next, Collection<ZeroSpan> podaci) {
        Date vrijeme = new Date(next);
        for (Integer i : mapa.keySet()) {
            ProgramMjerenja pm = mapa.get(i);
            
            if (!linija[i].equalsIgnoreCase("null")) {
                String ll = linija[i];
                try {
                    DecimalFormatSymbols symbols = new DecimalFormatSymbols();
                    if(linija[i].contains(",")) { 
                        symbols.setDecimalSeparator(',');
                    } else {
                        symbols.setDecimalSeparator('.');
                    }
                    DecimalFormat format = new DecimalFormat("#.#");
                    format.setDecimalFormatSymbols(symbols);
                    Double vrijednost;
                    if ("-9999".equals(linija[i])) {
                        vrijednost = -999.;
                    } else {
                        vrijednost = format.parse(linija[i]).doubleValue();
                    }
                    ZeroSpan ps = new ZeroSpan();
                    ps.setProgramMjerenjaId(pm);
                    ps.setVrijeme(vrijeme);
                    ps.setVrijednost(vrijednost);
                    ps.setVrsta(modMapa.get(i));
                    podaci.add(ps);
                } catch (NumberFormatException | ParseException ex) {
                    log.log(Level.SEVERE, null, ex);
                }
            }
        }
    }
}
