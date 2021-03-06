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
package dhz.skz.diseminacija.upozorenja;

import dhz.skz.aqdb.entity.Granice;
import dhz.skz.aqdb.entity.KategorijeGranica;
import dhz.skz.aqdb.entity.Podatak;
import dhz.skz.aqdb.entity.PrimateljiPodataka;
import dhz.skz.aqdb.entity.ProgramMjerenja;
import dhz.skz.aqdb.facades.ObavijestiFacade;
import dhz.skz.aqdb.facades.PodatakFacade;
import dhz.skz.aqdb.facades.PrekoracenjaUpozorenjaResultFacade;
import dhz.skz.aqdb.facades.PrimateljiPodatakaFacade;
import dhz.skz.diseminacija.DiseminatorPodataka;
import dhz.skz.diseminacija.upozorenja.slanje.MailUpozorenje;
import dhz.skz.diseminacija.upozorenja.slanje.VrstaUpozorenja;
import dhz.skz.util.OperStatus;
import java.io.IOException;
import java.io.StringWriter;
import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;

/**
 *
 * @author kraljevic
 */
@Stateless
@LocalBean
public class UpozorenjaBean implements DiseminatorPodataka {

    private static final Logger log = Logger.getLogger(UpozorenjaBean.class.getName());

    @EJB
    private PodatakFacade pf;

    @EJB
    private ObavijestiFacade obf;

    @EJB
    private PrekoracenjaUpozorenjaResultFacade prFac;

    @EJB
    private PrimateljiPodatakaFacade primF;

    @Override
    public void salji(PrimateljiPodataka primatelj) {
        log.log(Level.INFO, "UPOZORENJA za {0}:{1}", new Object[]{primatelj.getId(), primatelj.getNaziv()});
        MailUpozorenje suf = new MailUpozorenje();
        for (ProgramMjerenja pm : primatelj.getProgramMjerenjaCollection()) {
            log.log(Level.INFO, "{0}:{1}:{2}", new Object[]{primatelj.getId(), primatelj.getNaziv(), pm.getId()});
            for (Granice g : pm.getKomponentaId().getGraniceCollection()) {
                Integer intervalProcjene = g.getIntervalProcjene();
                double vrijednost = g.getVrijednost();
                String oznaka = g.getKategorijeGranicaId().getOznaka();
                if (oznaka.equals("GO") || oznaka.equals("GU")) {
                    Date kraj = new Date();
                    Date pocetak = new Date(new Date().getTime() - 3600 * 1000);
//                    Date pocetak = new Date(new Date().getTime() - (1 + intervalProcjene) * 3600 * 1000);
                    List<Podatak> podaci = pf.getPodaciOd(pm, pocetak, 0);
                    
                    if (podaci != null && !podaci.isEmpty()
                            && podaci.get(0).getStatus() < (1 << OperStatus.FAULT.ordinal())
                            && podaci.get(0).getVrijednost() > g.getVrijednost()) {
                        log.log(Level.INFO,"POD: {0},{1}", new Object[]{podaci.get(0).getProgramMjerenjaId(), podaci.get(0).getPodatakId()});
                        pocetak = new Date(new Date().getTime() - (1 + intervalProcjene) * 3600 * 1000);
                        podaci = pf.getPodaciOd(pm, pocetak, 0);

                        int brojPrekoracenja = 0;
                        for (Podatak p : podaci) {
                            if ((p.getVrijednost() > g.getVrijednost()) && (p.getStatus() < 16)) {
                                brojPrekoracenja++;
                            }
                        }
                        if (brojPrekoracenja == intervalProcjene) {
                            suf.saljiUpozorenje(primatelj, podaci, g, VrstaUpozorenja.UPOZORENJE);
                        } else {
                            suf.saljiUpozorenje(primatelj, podaci, g, VrstaUpozorenja.OBAVIJEST);
                        }
                    }
                }
            }

//        OffsetDateTime odt_sada = OffsetDateTime.now();
//        SlanjePoruka suf = new SlanjePoruka();
//
//        for (Obavijesti obavijest : obf.findAll(primatelj)) {
//            Integer granicniBroj = obavijest.getGranica().getDozvoljeniBrojPrekoracenja();
//            for (PrekoracenjaUpozorenjaResult pur : prFac.findAll(obavijest, odt_sada, granicniBroj)) {
//                Collection<Podatak> podaci = pokupiPodatke(primatelj, odt_sada);
//                Podatak pod = pf.getPodatakZaSat(pur.getProgramMjerenja(), odt_sada);
//                if (Objects.equals(pur.getBrojPojavljivanja(), granicniBroj)) {
//                    suf.salji(obavijest, pur, pod, podaci, VrstaUpozorenja.UPOZORENJE);
//                } else if (pod != null && (pod.getVrijednost() > obavijest.getGranica().getVrijednost()) && OperStatus.isValid(pod)) {
//                    suf.salji(obavijest, pur, pod, podaci, VrstaUpozorenja.OBAVIJEST);
//                }
//            }
//        }
        }
    }

    private String popuniPredlozak(String template) {
        VelocityContext context = new VelocityContext();
        context.put("mailto", "World");
        context.put("opis", "op");
        context.put("komponenta", "ko");
        context.put("postaja", "po");
        context.put("maksimalna_vrijednost", "mv");

        /* now render the template into a StringWriter */
        StringWriter writer = new StringWriter();
        try {
            Velocity.evaluate(context, writer, "VEL:", template);
        } catch (ParseErrorException ex) {
            Logger.getLogger(UpozorenjaBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MethodInvocationException ex) {
            Logger.getLogger(UpozorenjaBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ResourceNotFoundException ex) {
            Logger.getLogger(UpozorenjaBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(UpozorenjaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return writer.toString();
    }

    @Override
    public void nadoknadi(PrimateljiPodataka primatelj, Collection<ProgramMjerenja> program, Date pocetak, Date kraj) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private Collection<Podatak> pokupiPodatke(PrimateljiPodataka primatelj, OffsetDateTime sada) {
        OffsetDateTime pocetak = sada.minusHours(6);
        return pf.getVrijemePrimatelj(primatelj, pocetak, sada);
    }

    public void testirajSlanje() {
        OffsetDateTime odt_sada = OffsetDateTime.now();
        MailUpozorenje suf = new MailUpozorenje();
        for (PrimateljiPodataka pp : primF.getAktivniPrimateljiUpozorenja()) {
            System.out.println(pp.getNaziv());
        }

        for (PrimateljiPodataka pp : primF.getAktivniPrimateljiUpozorenja()) {
            for (ProgramMjerenja pm : pp.getProgramMjerenjaCollection()) {
                Podatak pod = pf.getPodatakZaSat(pm, odt_sada);
                if (pod != null) {
                    suf.testirajSustav(pp, pod);
                } else {
                    System.out.println("NULL:" + pm.getKomponentaId().getFormula() + ":" + pm.getPostajaId().getNazivPostaje());
                }
            }
        }
    }
}
