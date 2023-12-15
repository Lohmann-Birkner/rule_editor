/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.lb.ruleprocessor.create_criterien.inoutCrit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author gerschmann
 */
public class CriteriumContainer {
    private final Map <String, List<Criterium>> criterien;
    
    public CriteriumContainer (){
        criterien = new HashMap<>();
    }
    
    public void addCriterium(String pGroup, Criterium pCrit){
        List<Criterium> lst = criterien.get(pGroup);
        if(lst == null){
            lst = new ArrayList<>();
            criterien.put(pGroup, lst);
        }
        lst.add(pCrit);
    }

    public boolean isEmpty() {
        return criterien.isEmpty();
    }

    public Map<String, List<Criterium>> getCriterien() {
        return criterien;
    }
    
    @Override
    public String toString(){
    StringBuffer buffer = new StringBuffer();
    Set<String> groups = criterien.keySet();
       for(String group: groups){
           buffer.append("Group: ").append(group);
           List<Criterium> list = criterien.get(group);
           for(Criterium crit: list){
               buffer.append("\r\n\t").append(crit.toString());
               
           }
           buffer.append("\r\n");
       }
       return buffer.toString();
    }
    
}
