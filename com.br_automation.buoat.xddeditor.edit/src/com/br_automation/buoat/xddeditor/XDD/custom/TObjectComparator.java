/**
 * @since 16.4.2013
 * @author Joris L�ckenga, Bernecker + Rainer Industrie Elektronik Ges.m.b.H.
 */

package com.br_automation.buoat.xddeditor.XDD.custom;

import java.math.BigInteger;
import java.util.Comparator;

import com.br_automation.buoat.xddeditor.XDD.TObject;

/**
 *
 * @brief A comparator for objects of type TObject.
 *
 *        Enables sorting of an XDD's TObject-Elements.
 *
 * @author Joris L�ckenga
 */
public class TObjectComparator implements Comparator<TObject> {

    /**
     * @see Comparator#compare(Object, Object)
     */
    @Override
    public int compare(TObject o1, TObject o2) {
        return (new BigInteger(o1.getIndex()).intValue()) - (new BigInteger(o2.getIndex()).intValue());
    }

}
