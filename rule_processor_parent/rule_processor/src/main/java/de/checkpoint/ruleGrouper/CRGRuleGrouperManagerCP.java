/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.checkpoint.ruleGrouper;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

/**
 *
 * @author gerschmann
 */
public class CRGRuleGrouperManagerCP extends CRGRuleGrouperManager{

    /**
     * liefert kriterien für das Anzeigen in einem Kriterienbaum, geordnet nach
     * Kriterienguppen
     *
     * @param addAll boolean
     * @return Object[][]
     */
    /**
     * liefert kriterien für das Anzeigen in einem Kriterienbaum, geordnet nach
     * Kriterienguppen
     *
     * @param addAll boolean
     * @return Object[][]
     */
    public static Object[][] getTreeRulesCriterion(boolean addAll) {
        return getTreeRulesCriterion(addAll, false);
    }

    /**
     * liefert kriterien für das Anzeigen in einem Kriterienbaum begrenzt auf
     * Vorschlagskriterien, geordnet nach Kriterienguppen
     *
     * @param addAll boolean
     * @return Object[][]
     */
    public static Object[][] getTreeSuggsCriterion(boolean addAll) {
        return getTreeRulesCriterion(addAll, true);
    }

    private static Object[][] getTreeRulesCriterion(boolean addAll, boolean checkSugg) {



            return getFullTreeStruct(comp, checkSugg);

    }

    static Comparator comp = new Comparator() {
            public int compare(Object o1, Object o2) {
                CriterionEntry sr1 = (CriterionEntry) o1;
                CriterionEntry sr2 = (CriterionEntry) o2;
                return sr1.getDisplayText().toUpperCase().compareTo(sr2.getDisplayText().toUpperCase());
            }

            public boolean equals(Object obj) {
                return true;
            }
    };

    public static Object[][] getGroupAsTree(String groupName){
        if(groupName == null){
            return getTreeRulesCriterion(false);
        }
        GroupCriterionEntry retSuperGroup = null;
        Map<GroupCriterionEntry, Vector<CriterionEntry> > crits = new HashMap<>();
        for (GroupCriterionEntry m_superGroup : m_superGroups) {   
            if(m_superGroup.getDescriptionText().equalsIgnoreCase(groupName) || m_superGroup.getWorkText().equalsIgnoreCase(groupName)){
                for (GroupCriterionEntry m_group : m_groups) {
                    
                    if(m_group.getStructGroupIndex() == m_superGroup.m_groupCPIndex){
                        getSimpleGroup(m_group.getWorkText(), crits);
                    }
                }
                retSuperGroup = m_superGroup;
                break;
            }
        }
        if(retSuperGroup != null){
            Object[][] retSuperArray = new Object[1][2];
            retSuperArray[0][0] = (Object)retSuperGroup;
            retSuperArray[0][1] = (Object)crits;
            return retSuperArray;
        }else {
             getSimpleGroup(groupName, crits);
        }
        if(!crits.isEmpty()){
            Object[][] retArray = new Object[crits.size()][2];
            Set e = crits.keySet();

            Iterator itr = e.iterator();
            int i = 0;
            while (itr.hasNext()) {
                Object key = itr.next();
                Object obj = crits.get(key);
                if (obj != null) {
                    retArray[i][0] = key;
                    retArray[i][1] = obj;
                    i++;
                }
            } 
            return retArray;
        }

        return null;
        
    }
    
     private static  void getSimpleGroup(String groupName, Map<GroupCriterionEntry, Vector<CriterionEntry> > crits){
        Vector<CriterionEntry>  v = new Vector<>();
        for (GroupCriterionEntry m_group : m_groups) {

            if(m_group.getDescriptionText().equalsIgnoreCase(groupName) || m_group.getWorkText().equalsIgnoreCase(groupName)){
                int critGroupInd = m_group.getM_groupCPIndex();
               
                for (CriterionEntry m_criterion1 : m_criterion) {
                    CriterionEntry crit = (CriterionEntry) m_criterion1;
                    if (critGroupInd == crit.m_groupStructIndex)  {
                        
                        v.add(crit);
                        
                    }
                }
                
                if (v.size() > 0) {
                    Collections.sort(v, comp);

                }
                crits.put(m_group, v);
                return;
            }
        }
        
    }
    
    private static Object[][] getFullTreeStruct(Comparator comp,  boolean checkSugg) {
        boolean isRuleTest = true;
        LinkedHashMap all = new LinkedHashMap();
        Vector allSorted = new Vector();
        boolean showMethods = false;
        boolean eskaSole = false;
        boolean isIskv = false;
        for (int i = 0; i < m_superGroups.length; i++) {
            int index = m_superGroups[i].m_groupCPIndex;
            GroupCriterionEntry superGroup = m_superGroups[i];
            HashMap crits = new HashMap();
            for (int j = 0; j < m_groups.length; j++) {
                GroupCriterionEntry group = (GroupCriterionEntry) m_groups[j];
                if (index == group.m_groupStructIndex) {
                    int critGroupInd = group.m_groupCPIndex;
  
                    Vector v = new Vector();
                    for (int k = 0; k < m_criterion.length; k++) {
                        CriterionEntry crit = (CriterionEntry) m_criterion[k];

                        v.add(crit);

                    }
                        
                    if (v.size() > 0) {
                        Collections.sort(v, comp);
                        crits.put(group, v);
                    }
                }
                if (crits.size() > 0) {
                    if (index == group.m_groupCPIndex) {
                        all.put(group, crits.get(group));
                        break;
                    } else {
                        all.put(superGroup, crits);
                    }
                }
            }
        }

        Object[][] crits = new Object[all.size()][2];
        Set e = all.keySet();
        /*			try{
        TreeSet tmpSet = new TreeSet(comp);
        tmpSet.addAll(e);
        e = tmpSet;
        }catch(Exception ex){
        ex.printStackTrace();
        }*/
        Iterator itr = e.iterator();
        int i = 0;
        while (itr.hasNext()) {
            Object key = itr.next();
            Object obj = all.get(key);
            if (obj != null) {
                crits[i][0] = key;
                crits[i][1] = obj;
                i++;
            }
        }
        return crits;
    }

     public static boolean isCurrency(int m_index, int type) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    protected CRGRuleGrouperManagerCP(){
        
    }
    
}
