/**
 * @since 19.3.2013
 * @author Joris L�ckenga, Bernecker + Rainer Industrie Elektronik Ges.m.b.H.
 */

package com.br_automation.buoat.xddeditor.XDD.custom.propertypages.filters;

import java.math.BigInteger;

import org.eclipse.jface.viewers.IFilter;

import com.br_automation.buoat.xddeditor.XDD.SubObjectType;
import com.br_automation.buoat.xddeditor.XDD.TObject;
import com.br_automation.buoat.xddeditor.XDD.custom.ObjectDictionaryEntry;

/**
 * @brief Filters for SubObjectTypes with parent having index of 0x1600 or
 *        0x1A00 and subindex not set to 0x0.
 * 
 * @author Joris L�ckenga
 * */
public class MappingSubobjectsFilter implements IFilter {

    @Override
    public boolean select(Object toTest) {

        if (toTest instanceof SubObjectType) {
            SubObjectType subObject = (SubObjectType) toTest;
            TObject parentObject = (TObject) subObject.eContainer();
            int objectIndex;

            if (parentObject.getIndex() != null) {
                objectIndex = new BigInteger(1, parentObject.getIndex()).intValue();
                if ((objectIndex >= ObjectDictionaryEntry.PDO_RXMAPPPARAM_MIN && objectIndex <= ObjectDictionaryEntry.PDO_RXMAPPPARAM_MAX)
                    || (objectIndex >= ObjectDictionaryEntry.PDO_TXCOMMPARAM_MIN && objectIndex <= ObjectDictionaryEntry.PDO_TXCOMMPARAM_MAX)) {
                    int subIndex = new BigInteger(subObject.getSubIndex()).intValue();
                    return (subIndex != 0); //This statement ignores NumberOfEntries object 
                }
            }
        }
        return false;
    }

} //MappingSubobjectsFilter