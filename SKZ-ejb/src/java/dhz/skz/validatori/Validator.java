/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dhz.skz.validatori;

import dhz.skz.aqdb.entity.PodatakSirovi;

/**
 *
 * @author kraljevic
 */
public interface Validator {

    public void validiraj(PodatakSirovi ps);

    public void setPodaciUmjeravanja(float a, float b, float ldl, float opseg);
    
    public void setTemperatura(float temperatura);

}
