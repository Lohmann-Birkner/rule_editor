/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.lb.ruleprocessor.create_criterien.inoutCrit;

import org.jetbrains.annotations.NotNull;

/**
 *
 * @author gerschmann
 */
public class Tooltip implements Comparable{
    private final String value;
    private final String description;
    private final TOOLTIP_TYPE type;

    public String getFullDescription() {
        if(type.equals(TOOLTIP_TYPE.VALUE)){
            return value + " - " + description;
        }
        return description;
    }

    public enum TOOLTIP_TYPE{
        TEXT,
        VALUE
    }    
    
    
    public Tooltip(@NotNull String pDescription, String pValue){
        value = pValue;
        description = pDescription;
        type = value == null?TOOLTIP_TYPE.TEXT:TOOLTIP_TYPE.VALUE;
    }
    
    @Override
    public int compareTo(Object toCompare) {
        if(toCompare instanceof Tooltip){
            if(type.equals(((Tooltip)toCompare).getType()) ){
                if(type.equals(TOOLTIP_TYPE.VALUE)){
                    return value.compareTo(((Tooltip)toCompare).getValue());
                }
                
            }
        }
        return -1;
    }

    public TOOLTIP_TYPE getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    public String getDescription() {
        return description;
    }
    
    
}
