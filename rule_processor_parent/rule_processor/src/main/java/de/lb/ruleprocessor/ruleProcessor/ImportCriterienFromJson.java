/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.lb.ruleprocessor.ruleProcessor;

import de.checkpoint.ruleGrouper.CRGRuleGrouperManager;
import de.checkpoint.ruleGrouper.CriterionEntry;
import de.checkpoint.ruleGrouper.GroupCriterionEntry;
import de.lb.ruleprocessor.create_criterien.inoutCrit.Criterium;
import de.lb.ruleprocessor.create_criterien.inoutCrit.CriteriumContainer;
import de.lb.ruleprocessor.create_criterien.inoutCrit.Utils;
import de.lb.ruleprocessor.json_processor.JsonFileReader;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author gerschmann
 */
public class ImportCriterienFromJson {

    private static final Logger LOG = Logger.getLogger(ImportCriterienFromJson.class.getName());
    
    
    
    public static void main(String args[]){
         if(args.length == 0){
            LOG.log(Level.WARNING, "usage: path to criterien.json");
            System.exit(0);
        }else{
            ImportCriterienFromJson importTree = new ImportCriterienFromJson();
            try {
                if(importTree.doImport(args[0])){
                    System.exit(0);
                }else{
                    System.exit(-1);
                }
            } catch (Exception ex) {
                Logger.getLogger(ImportCriterienFromJson.class.getName()).log(Level.SEVERE, "could not read crits", ex);
            }

        }
    }

    private boolean doImport(String pJsonPath) throws Exception{
        JsonFileReader reader = new JsonFileReader(pJsonPath);
        reader.openFile();

        CriteriumContainer container = reader.readFile();
        Map <String, List<Criterium>> criterien = container.getCriterien();
        if(criterien != null){
            List<GroupCriterionEntry> groups = new ArrayList<>();
            Map<String, CriterionEntry> name2crit = new HashMap<>();
            Map<Integer, Map<String, CriterionEntry>> type2criterien = new HashMap<>();
            Map<Integer, Map<Integer, CriterionEntry>> type2criterienIndex = new HashMap<>();
            Set<String> groupNames = criterien.keySet();
            Iterator<String> itr = groupNames.iterator();
            int groupInd = 0;
            while(itr.hasNext()){
                String groupName = itr.next();
                GroupCriterionEntry group = new GroupCriterionEntry(groupInd, groupName, "");
                groups.add(group);
                List<Criterium> crits = criterien.get(groupName);
                if(crits != null){
                    for(Criterium crit: crits){
                        CriterionEntry entry = new CriterionEntry(groupInd,
                        crit.getName().toLowerCase(),
                        crit.getName(),
                        crit.getDescription(),
                        Utils.DATATYPES.get(crit.getType()), 
                        CRGRuleGrouperManager.instance().getCritIndex2type(crit.getType()),
                         Utils.USAGE.CRIT_RULE_ONLY.ordinal()
                        );
                        Map <String, CriterionEntry> typeMap = type2criterien.get(Utils.DATATYPES.get(crit.getType()));
                        if(typeMap == null){
                            typeMap = new HashMap<>();
                            type2criterien.put(Utils.DATATYPES.get(crit.getType()), typeMap);
                        }
                        Map <Integer, CriterionEntry> typeMapInd = type2criterienIndex.get(Utils.DATATYPES.get(crit.getType()));
                        if(typeMapInd == null){
                            typeMapInd = new HashMap<>();
                            type2criterienIndex.put(Utils.DATATYPES.get(crit.getType()), typeMapInd);
                        }
                        typeMapInd.put(entry.getM_index(), entry);
                        name2crit.put( crit.getName(), entry);

                    }
                }
            }
            CRGRuleGrouperManager.instance().setCriterionTree(null, groups,  name2crit, type2criterien, type2criterienIndex); 
            return true;
        }
        return false;
    }
   
}
