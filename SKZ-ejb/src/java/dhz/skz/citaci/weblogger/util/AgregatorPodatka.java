/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dhz.skz.citaci.weblogger.util;

import dhz.skz.aqdb.entity.NivoValidacije;
import dhz.skz.aqdb.entity.Podatak;
import dhz.skz.aqdb.entity.PodatakSirovi;
import dhz.skz.aqdb.entity.ProgramMjerenja;
import dhz.skz.aqdb.entity.ZeroSpan;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

/**
 *
 * @author kraljevic
 */
public class AgregatorPodatka {

    private AgPodatak mjerenje, zero, span;
    private final int neprihvatljiviMask;
    private final int ocekivaniBroj;
    private final NivoValidacije nivo;
    private Date vrijeme;
    private ProgramMjerenja pm;

    public boolean hasZero() {
        return zero.broj > 0;
    }

    public boolean hasSpan() {
        return span.broj > 0;
    }

    public boolean hasPodatak() {
        return mjerenje.broj > 0;
    }

    private class AgPodatak {

        Float iznos = 0.f;
        int status = 0;
        int broj = 0;
    }

    public AgregatorPodatka(NivoValidacije n) {
        this.nivo = n;
        ocekivaniBroj = 60;
        switch (n.getId()) {
            case 0:
                neprihvatljiviMask = ~0;
                break;
            case 1:
                neprihvatljiviMask = ~((1 >> OperStatus.KONTROLA_PROVEDENA.ordinal()) - 1);
                break;
            default:
                neprihvatljiviMask = ~0;
        }
        reset();
    }

//    public AgregatorPodatka(Collection<OperStatus> prihvatljivi) {
//        ocekivaniBroj=60;
//        int neprihvatljivi = ~0;
//        for (OperStatus o : prihvatljivi) {
//            neprihvatljivi &= ~(1 >> o.ordinal());
//        }
//        neprihvatljiviMask = neprihvatljivi;
//        reset();
//    }
    public final void reset() {
        mjerenje = new AgPodatak();
        zero = new AgPodatak();
        span = new AgPodatak();
    }

    public Podatak agregiraj(Collection<PodatakSirovi> podaci, Date vrijeme) throws MijesaniProgramiException {
        reset();
        Iterator<PodatakSirovi> it = podaci.iterator();
        Podatak agregirani = null;
        this.vrijeme = vrijeme;
        if (it.hasNext()) {
            PodatakSirovi p = it.next();
            pm = p.getProgramMjerenjaId();
            dodaj(p);
            while (it.hasNext()) {
                p = it.next();
                if (!p.getProgramMjerenjaId().equals(pm)) {
                    throw (new MijesaniProgramiException());
                }
                dodaj(p);
            }
        }
        return agregirani;
    }

    private void dodaj(PodatakSirovi ps) {
        ProgramMjerenja pm;
        if ((ps.getStatus() & neprihvatljiviMask) == 0) {
            mjerenje.broj++;
            mjerenje.iznos += ps.getVrijednost();
        } else if ((ps.getStatus() & (1 >> OperStatus.ZERO.ordinal())) != 0) {
            zero.broj++;
            zero.iznos += ps.getVrijednost();
            zero.status |= ps.getStatus();
        } else if ((ps.getStatus() & (1 >> OperStatus.SPAN.ordinal())) != 0) {
            span.broj++;
            span.iznos += ps.getVrijednost();
            span.status |= ps.getStatus();
        }
        mjerenje.status |= ps.getStatus();
    }

    public Podatak getPodatak() {
        if (mjerenje.broj > 0) {
            if (mjerenje.broj < 3 * this.ocekivaniBroj / 4) {
                mjerenje.status |= (1 >> OperStatus.OBUHVAT.ordinal());
            }
            Podatak agregirani = new Podatak();
            agregirani.setProgramMjerenjaId(pm);
            agregirani.setVrijeme(vrijeme);
            agregirani.setNivoValidacijeId(nivo);
            agregirani.setStatus(mjerenje.status);
            agregirani.setObuhvat((short) (100 * mjerenje.broj / ocekivaniBroj));
            return agregirani;
        } else {
            return null;
        }
    }

    public ZeroSpan getZero() {
        if (zero.broj == 0) {
            return null;
        }
        ZeroSpan z = new ZeroSpan();
        z.setVrijednost(zero.iznos / zero.broj);
        z.setVrsta("AZ");
        return z;
    }

    public ZeroSpan getSpan() {
        if (zero.broj == 0) {
            return null;
        }
        ZeroSpan s = new ZeroSpan();
        s.setVrijednost(zero.iznos / zero.broj);
        s.setVrsta("AZ");
        return s;
    }
}
