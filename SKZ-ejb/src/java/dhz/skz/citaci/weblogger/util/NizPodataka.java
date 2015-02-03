/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dhz.skz.citaci.weblogger.util;

import dhz.skz.citaci.weblogger.validatori.Validator;
import dhz.skz.aqdb.entity.ProgramMjerenja;
import dhz.skz.aqdb.entity.ZeroSpan;
import java.util.Date;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.logging.Logger;

/**
 *
 * @author kraljevic
 */
public class NizPodataka {

    private static final Logger log = Logger.getLogger(NizPodataka.class.getName());

    private ProgramMjerenja kljuc;
    private NavigableMap<Date, Validator> validatori;
    private int brojMjerenjaUSatu;
    private NavigableMap<Date, PodatakWl> podaci;
    private NavigableMap<Date, ZeroSpan> zs;

    public NizPodataka(ProgramMjerenja kljuc) {
        this.kljuc = kljuc;
        this.podaci = new TreeMap<>();
        this.zs = new TreeMap<>();
        this.brojMjerenjaUSatu = 60;
    }

    public NizPodataka() {
        this.podaci = new TreeMap<>();
        this.brojMjerenjaUSatu = 60;
        this.zs = new TreeMap<>();

    }

    public NavigableMap<Date, Validator> getValidatori() {
        return validatori;
    }

    public void setValidatori(NavigableMap<Date, Validator> validatori) {
        this.validatori = validatori;
    }

    public void dodajPodatak(Date vrijeme, PodatakWl ocitanje) {
        podaci.put(vrijeme, ocitanje);
    }
    
    public void dodajZeroSpan(Date vrijeme, ZeroSpan ocitanje) {
        zs.put(vrijeme, ocitanje);
    }
    
    public NavigableMap<Date, PodatakWl> getPodaci() {
        return podaci;
    }
    
    public NavigableMap<Date, ZeroSpan> getZeroSpan() {
        return zs;
    }

    public int getBrojMjerenjaUSatu() {
        return brojMjerenjaUSatu;
    }

    public void setBrojMjerenjaUSatu(int brojMjerenjaUSatu) {
        this.brojMjerenjaUSatu = brojMjerenjaUSatu;
    }

    public ProgramMjerenja getKljuc() {
        return kljuc;
    }

    public void setKljuc(ProgramMjerenja kljuc) {
        this.kljuc = kljuc;
    }

    public void setPodaci(NavigableMap<Date, PodatakWl> podaci) {
        this.podaci = podaci;
    }

    public void setZs(NavigableMap<Date, ZeroSpan> zs) {
        this.zs = zs;
    }
}
