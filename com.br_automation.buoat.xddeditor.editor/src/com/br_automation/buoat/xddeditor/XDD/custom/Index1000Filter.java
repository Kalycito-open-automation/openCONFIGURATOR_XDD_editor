package com.br_automation.buoat.xddeditor.XDD.custom;

import java.math.BigInteger;

import org.eclipse.jface.viewers.IFilter;

import com.br_automation.buoat.xddeditor.XDD.TObject;

/**
 * @author Joris L�ckenga
 * @brief Filters for TObjects with Index set to "1000"
 * @since 19.3.2013
 * */
public class Index1000Filter implements IFilter {
    @Override
    public boolean select(Object toTest) {
        if (toTest instanceof TObject) {
            TObject tobject = (TObject) toTest;
            String result;

            if (tobject.getIndex() != null) {
                result = new BigInteger(1, tobject.getIndex()).toString(16);
                if (result.contentEquals("1000"))
                    return true;
            }
        }
        return false;
    } //select
} //Index1F82Filter
