/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dhz.skz.aqdb.entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author kraljevic
 */
@Entity
@Table(name = "primatelj_program_kljucevi_map")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "PrimateljProgramKljuceviMap.findAll", query = "SELECT p FROM PrimateljProgramKljuceviMap p"),
    @NamedQuery(name = "PrimateljProgramKljuceviMap.findByProgramMjerenjaId", query = "SELECT p FROM PrimateljProgramKljuceviMap p WHERE p.primateljProgramKljuceviMapPK.programMjerenjaId = :programMjerenjaId"),
    @NamedQuery(name = "PrimateljProgramKljuceviMap.findByPKljuc", query = "SELECT p FROM PrimateljProgramKljuceviMap p WHERE p.pKljuc = :pKljuc"),
    @NamedQuery(name = "PrimateljProgramKljuceviMap.findByKKljuc", query = "SELECT p FROM PrimateljProgramKljuceviMap p WHERE p.kKljuc = :kKljuc"),
    @NamedQuery(name = "PrimateljProgramKljuceviMap.findByUKljuc", query = "SELECT p FROM PrimateljProgramKljuceviMap p WHERE p.uKljuc = :uKljuc"),
    @NamedQuery(name = "PrimateljProgramKljuceviMap.findByNKljuc", query = "SELECT p FROM PrimateljProgramKljuceviMap p WHERE p.nKljuc = :nKljuc"),
    @NamedQuery(name = "PrimateljProgramKljuceviMap.findByPrimateljId", query = "SELECT p FROM PrimateljProgramKljuceviMap p WHERE p.primateljProgramKljuceviMapPK.primateljId = :primateljId"),
    @NamedQuery(name = "PrimateljProgramKljuceviMap.findByAktivan", query = "SELECT p FROM PrimateljProgramKljuceviMap p WHERE p.aktivan = :aktivan")})
public class PrimateljProgramKljuceviMap implements Serializable {
    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected PrimateljProgramKljuceviMapPK primateljProgramKljuceviMapPK;
    @Size(max = 90)
    @Column(name = "p_kljuc")
    private String pKljuc;
    @Size(max = 90)
    @Column(name = "k_kljuc")
    private String kKljuc;
    @Size(max = 90)
    @Column(name = "u_kljuc")
    private String uKljuc;
    @Size(max = 90)
    @Column(name = "n_kljuc")
    private String nKljuc;
    private Integer aktivan;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "zadnji_poslani")
    private Date zadnjiPoslani;
    @JoinColumn(name = "primatelj_id", referencedColumnName = "id", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private PrimateljiPodataka primateljiPodataka;
    @JoinColumn(name = "program_mjerenja_id", referencedColumnName = "id", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private ProgramMjerenja programMjerenja;

    public PrimateljProgramKljuceviMap() {
    }

    public PrimateljProgramKljuceviMap(PrimateljProgramKljuceviMapPK primateljProgramKljuceviMapPK) {
        this.primateljProgramKljuceviMapPK = primateljProgramKljuceviMapPK;
    }

    public PrimateljProgramKljuceviMap(int programMjerenjaId, int primateljId) {
        this.primateljProgramKljuceviMapPK = new PrimateljProgramKljuceviMapPK(programMjerenjaId, primateljId);
    }

    public PrimateljProgramKljuceviMapPK getPrimateljProgramKljuceviMapPK() {
        return primateljProgramKljuceviMapPK;
    }

    public void setPrimateljProgramKljuceviMapPK(PrimateljProgramKljuceviMapPK primateljProgramKljuceviMapPK) {
        this.primateljProgramKljuceviMapPK = primateljProgramKljuceviMapPK;
    }

    public String getPKljuc() {
        return pKljuc;
    }

    public void setPKljuc(String pKljuc) {
        this.pKljuc = pKljuc;
    }

    public String getKKljuc() {
        return kKljuc;
    }

    public void setKKljuc(String kKljuc) {
        this.kKljuc = kKljuc;
    }

    public String getUKljuc() {
        return uKljuc;
    }

    public void setUKljuc(String uKljuc) {
        this.uKljuc = uKljuc;
    }

    public String getNKljuc() {
        return nKljuc;
    }

    public void setNKljuc(String nKljuc) {
        this.nKljuc = nKljuc;
    }

    public Integer getAktivan() {
        return aktivan;
    }

    public void setAktivan(Integer aktivan) {
        this.aktivan = aktivan;
    }

    public PrimateljiPodataka getPrimateljiPodataka() {
        return primateljiPodataka;
    }

    public void setPrimateljiPodataka(PrimateljiPodataka primateljiPodataka) {
        this.primateljiPodataka = primateljiPodataka;
    }

    public ProgramMjerenja getProgramMjerenja() {
        return programMjerenja;
    }

    public void setProgramMjerenja(ProgramMjerenja programMjerenja) {
        this.programMjerenja = programMjerenja;
    }

    public Date getZadnjiPoslani() {
        return zadnjiPoslani;
    }

    public void setZadnjiPoslani(Date zadnjiPoslani) {
        this.zadnjiPoslani = zadnjiPoslani;
    }
    

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (primateljProgramKljuceviMapPK != null ? primateljProgramKljuceviMapPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof PrimateljProgramKljuceviMap)) {
            return false;
        }
        PrimateljProgramKljuceviMap other = (PrimateljProgramKljuceviMap) object;
        if ((this.primateljProgramKljuceviMapPK == null && other.primateljProgramKljuceviMapPK != null) || (this.primateljProgramKljuceviMapPK != null && !this.primateljProgramKljuceviMapPK.equals(other.primateljProgramKljuceviMapPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "dhz.skz.aqdb.entity.PrimateljProgramKljuceviMap[ primateljProgramKljuceviMapPK=" + primateljProgramKljuceviMapPK + " ]";
    }
    
}
