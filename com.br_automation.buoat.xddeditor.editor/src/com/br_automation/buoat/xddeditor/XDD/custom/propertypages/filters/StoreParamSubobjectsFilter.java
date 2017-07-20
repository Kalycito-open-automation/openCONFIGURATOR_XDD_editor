/**
 * @since 19.3.2013
 * @author Joris L�ckenga, Bernecker + Rainer Industrie Elektronik Ges.m.b.H.
 */

package com.br_automation.buoat.xddeditor.XDD.custom.propertypages.filters;

import java.math.BigInteger;

import org.eclipse.jface.viewers.IFilter;

import com.br_automation.buoat.xddeditor.XDD.SubObjectType;
import com.br_automation.buoat.xddeditor.XDD.TObject;
import com.br_automation.buoat.xddeditor.XDD.custom.EPLGeneralConstants;

/**
 * @brief Filters for subObjects of Object 0x1010 with subIndex != 0.
 *
 * @author Joris L�ckenga
 */
public class StoreParamSubobjectsFilter implements IFilter {

	@Override
	public boolean select(Object toTest) {
		if (toTest instanceof SubObjectType) {
			SubObjectType subObject = (SubObjectType) toTest;
			TObject parentObject = (TObject) subObject.eContainer();

			if (parentObject.getIndex() != null) {
				if (new BigInteger(1, parentObject.getIndex()).intValue() == EPLGeneralConstants.NMT_STOREPARAM_REC) {
					if (subObject.getSubIndex() == null || subObject.getSubIndex().length == 0)
						return true;
					int subIndex = new BigInteger(subObject.getSubIndex()).intValue();
					return (subIndex != 0);
				}
			}
		}
		return false;
	}

} // StoreParamSubobjectsFilter
