/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dhz.skz.web;

import dhz.skz.DiseminacijaGlavniBeanRemote;
import java.io.Serializable;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.faces.bean.ManagedBean;
import javax.faces.view.ViewScoped;

/**
 *
 * @author kraljevic
 */
@Named(value = "diseminacijaMB")
@ManagedBean
@ViewScoped
public class DiseminacijaMB extends NewClass implements Serializable{
    @EJB
    private DiseminacijaGlavniBeanRemote diseminacijaGlavniBean;

    public DiseminacijaMB() {
        super();
    }
    
    @Override
    protected DiseminacijaGlavniBeanRemote getBean(){
        return diseminacijaGlavniBean;
    }
    
}
