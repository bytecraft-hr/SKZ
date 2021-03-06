/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dhz.skz.rest;

import dhz.skz.rest.dto.ZadnjiDTO;
import dhz.skz.aqdb.entity.PodatakSirovi;
import dhz.skz.aqdb.entity.ProgramMjerenja;
import dhz.skz.aqdb.facades.PodatakFacade;
import dhz.skz.aqdb.facades.PodatakSiroviFacade;
import dhz.skz.aqdb.facades.ProgramMjerenjaFacade;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import javax.ejb.EJB;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

/**
 *
 * @author kraljevic
 */
@javax.enterprise.context.RequestScoped
@Path("dhz.skz.rs.programmjerenja")
public class ProgramMjerenjaFacadeREST  {
    @EJB
    private PodatakSiroviFacade podatakSiroviFacade;
    @EJB
    private PodatakFacade podatakFacade;
    @EJB
    private ProgramMjerenjaFacade programMjerenjaFacade;
    

    public ProgramMjerenjaFacadeREST() {
    }

    @GET
    @Produces({"application/xml", "application/json"})
    public List<ProgramMjerenja> findAll(@DefaultValue("0") @QueryParam("neaktivni_ukljuceni") Integer neaktivni) {
        List<ProgramMjerenja> lista = programMjerenjaFacade.findAll();
        List<ProgramMjerenja> tmp = new ArrayList<ProgramMjerenja>(lista.size());
        for ( ProgramMjerenja pm : lista) {
            if ( pm.getAktivan() || (neaktivni > 0))
                tmp.add(pm);
        }
        return tmp;
    }
    
    @GET
    @Path("zakljucani")
    @Produces({"application/xml", "application/json"})
    public List<ProgramMjerenja> findAllTajni(@DefaultValue("0") @QueryParam("neaktivni_ukljuceni") Integer neaktivni) {
        List<ProgramMjerenja> lista = programMjerenjaFacade.findAll();
        List<ProgramMjerenja> tmp = new ArrayList<ProgramMjerenja>(lista.size());
        for ( ProgramMjerenja pm : lista) {
            if ( pm.getAktivan() || (neaktivni > 0))
                tmp.add(pm);
        }
        return tmp;
    }

    
    @GET
    @Path("zadnji")
    @Produces({"application/xml", "application/json"})
    public List<ZadnjiDTO> getVrijemeZadnjegPodatkaPoProgramu(){
        List<ZadnjiDTO> lista = new ArrayList<>();
        for ( ProgramMjerenja pm : programMjerenjaFacade.findAll()) {
            Date zadnjiSatni = podatakFacade.getVrijemeZadnjeg(pm, 0);
            PodatakSirovi zadnji = podatakSiroviFacade.getZadnji(pm);
            Date zadnjiSirovi; 
            if ( zadnji != null ) 
                zadnjiSirovi = zadnji.getVrijeme();
            else
                zadnjiSirovi = null;
            lista.add(new ZadnjiDTO(pm.getId(), zadnjiSatni, zadnjiSirovi));
        }
        return lista;
    }

}
