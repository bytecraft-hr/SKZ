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
package dhz.skz.aqdb.facades;

import dhz.skz.aqdb.entity.Postaja;
import dhz.skz.aqdb.entity.PostajaUredjajLink;
import java.util.Date;
import java.util.List;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author kraljevic
 */
@Stateless
@LocalBean
public class PostajaUredjajLinkFacade extends AbstractFacade<PostajaUredjajLink> {
    @PersistenceContext(unitName = "LIKZ-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public PostajaUredjajLinkFacade() {
        super(PostajaUredjajLink.class);
    }
    
    public PostajaUredjajLink findByPostajaUsporednoVrijeme(Postaja po, int usporedno, Date vrijeme) {
        List<PostajaUredjajLink> resultList = em.createNamedQuery("PostajaUredjajLink.findByPostajaUsporednoVrijeme", PostajaUredjajLink.class)
                .setParameter("postaja", po).setParameter("usporedno", usporedno).setParameter("vrijeme", vrijeme).getResultList();
        if ( resultList.isEmpty() ) return null;
        return resultList.get(0);
    }
    
}
