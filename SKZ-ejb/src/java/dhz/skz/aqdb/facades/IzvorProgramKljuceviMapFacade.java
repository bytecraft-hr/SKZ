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

import dhz.skz.aqdb.entity.IzvorProgramKljuceviMap;
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
public class IzvorProgramKljuceviMapFacade extends AbstractFacade<IzvorProgramKljuceviMap> {
    @PersistenceContext(unitName = "LIKZ-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public IzvorProgramKljuceviMapFacade() {
        super(IzvorProgramKljuceviMap.class);
    }
    
//    public Collection<IzvorProgramKljuceviMap> find(IzvorPodataka izvor) {
//        CriteriaBuilder cb = em.getCriteriaBuilder();
//        CriteriaQuery<IzvorProgramKljuceviMap> cq = cb.createQuery(IzvorProgramKljuceviMap.class);
//        Root<IzvorProgramKljuceviMap> from = cq.from(IzvorProgramKljuceviMap.class);
//        cq.where(cb.equal(from.get(IzvorProgramKljuceviMap_.izvorPodataka), izvor));
//        cq.select(from);
//        return em.createQuery(cq).getResultList();
//    }
    
}
