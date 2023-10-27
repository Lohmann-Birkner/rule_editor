/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.lb.ruleprocessor.ruleProcessor;

import de.lb.ruleprocessor.checkpoint.ruleGrouper.CRGFileRuleManager;
import de.lb.ruleprocessor.checkpoint.ruleGrouper.CRGRuleGrouperManager;
import de.lb.ruleprocessor.checkpoint.ruleGrouper.CriterionEntry;
import de.lb.ruleprocessor.checkpoint.ruleGrouper.GroupCriterionEntry;
import de.lb.ruleprocessor.create_criterien.inoutCrit.Utils;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 *
 * @author gerschmann
 */
public class ImportCriterienTreeFromXml {

    private static final Logger LOG = Logger.getLogger(ImportCriterienTreeFromXml.class.getName());



    public ImportCriterienTreeFromXml() {
    }
    
    public boolean doImport(String pPath) throws Exception {
        LOG.log(Level.INFO, "We read criterien tree from path {0}", pPath);
        File file = checkFile(pPath);
        if(file == null){
            return false;
        }
        Document doc = CRGFileRuleManager.getXMLDocument(file);
        if(doc == null){
            LOG.log(Level.WARNING, "could not extract xml from file {0}", pPath);
            return false;
        }
        NodeList tree = doc.getElementsByTagName(Utils.TAGS.CRITERION_TREE.name().toLowerCase());
        if(tree == null || tree.getLength() != 1){
            LOG.log(Level.WARNING, "could not extract xml from file {0}", pPath);
            return false;
        }
        NodeList sGroups = ((Element)tree.item(0)).getElementsByTagName(Utils.TAGS.SUPERGROUP.name().toLowerCase());
        if(sGroups == null || sGroups.getLength() == 0){
            LOG.log(Level.WARNING, "file does not contain supergroup - Tag");
            return false;
        }
        List<GroupCriterionEntry> superGroups = new ArrayList<>();
        List<GroupCriterionEntry> groups = new ArrayList<>();
        Map<String, CriterionEntry> name2crit = new HashMap<>();
        Map<Integer, Map<String, CriterionEntry>> type2criterien = new HashMap<>();
        Map<Integer, Map<Integer, CriterionEntry>> type2criterienIndex = new HashMap<>();
        
        int groupInd = 0;
        for(int superGroupIndex = 0, nlCount = sGroups.getLength(); superGroupIndex < nlCount; superGroupIndex++){
            Element sEle = (Element)sGroups.item(superGroupIndex);
            String name = sEle.getAttribute(Utils.ATTRIBUTES.CPNAME.name().toLowerCase());
            if(name != null){
            // get Tooltips for supergroup
                GroupCriterionEntry superGroup = new GroupCriterionEntry(superGroupIndex, name, getToolTip4Element(sEle));
                superGroups.add(superGroup);
                // get groups for supergroup
                NodeList grps = sEle.getElementsByTagName(Utils.TAGS.GROUP.name().toLowerCase());
                if(grps == null || grps.getLength() == 0){
                    continue;
                }
                for(int j = 0, grLen = grps.getLength(); j < grLen; j++, groupInd++){
                    Element grp = (Element)grps.item(j);
                    name = grp.getAttribute(Utils.ATTRIBUTES.CPNAME.name().toLowerCase());
                    if(name != null){
                        GroupCriterionEntry group = new GroupCriterionEntry(groupInd, superGroupIndex, name, getToolTip4Element(grp));
                        groups.add(group);
                        createCriterien4Group(grp, groupInd, name2crit, type2criterien, type2criterienIndex);
                    }
                }
            }
        }
        CRGRuleGrouperManager.instance().setCriterionTree(superGroups, groups,  name2crit, type2criterien, type2criterienIndex); 
        return true;
    }

    private  void createCriterien4Group(Element grp, int groupInd, Map<String, CriterionEntry> name2crit, 
            Map<Integer, Map<String, CriterionEntry>> type2criterien, Map<Integer, Map<Integer, CriterionEntry>> type2criterienIndex) throws Exception{

        NodeList crits = grp.getElementsByTagName(Utils.TAGS.CRITERION.name().toLowerCase());
        if(crits != null && crits.getLength() > 0){
            for(int i = 0, len = crits.getLength(); i < len; i++){
                Element crit = (Element)crits.item(i);
                String cpName = crit.getAttribute(Utils.ATTRIBUTES.CPNAME.name().toLowerCase());
//                String critIndex = crit.getAttribute(Utils.ATTRIBUTES.CRITERION_INDEX.name().toLowerCase());
                String criterionType = crit.getAttribute(Utils.ATTRIBUTES.CRITERION_TYPE.name().toLowerCase());
                String displayName = crit.getAttribute(Utils.ATTRIBUTES.DISPLAY_NAME.name().toLowerCase());
                String usage = crit.getAttribute(Utils.ATTRIBUTES.USAGE.name().toLowerCase());
                String description = getToolTip4Element(crit);
                CriterionEntry cpCrit = new CriterionEntry(groupInd, 
                        cpName, 
                        displayName == null?cpName:displayName, 
                        description, 
                        Utils.DATATYPES.get(criterionType),
                        CRGRuleGrouperManager.instance().getCritIndex2type(criterionType), 
                        Utils.USAGE.valueOf(usage).ordinal()
                );
                Map <String, CriterionEntry> typeMap = type2criterien.get(Utils.DATATYPES.get(criterionType));
                if(typeMap == null){
                    typeMap = new HashMap<>();
                    type2criterien.put(Utils.DATATYPES.get(criterionType), typeMap);
                }
                Map <Integer, CriterionEntry> typeMapInd = type2criterienIndex.get(Utils.DATATYPES.get(criterionType));
                if(typeMapInd == null){
                    typeMapInd = new HashMap<>();
                    type2criterienIndex.put(Utils.DATATYPES.get(criterionType), typeMapInd);
                }
                typeMapInd.put(cpCrit.getM_index(), cpCrit);
                name2crit.put(cpName, cpCrit);
            }
        }

    }
    
    private String getToolTip4Element(Element pElement) throws Exception{
            StringBuilder sToolTip = new StringBuilder();
            String name = pElement.getAttribute(Utils.ATTRIBUTES.CPNAME.name().toLowerCase());
            
            NodeList toolTips = pElement.getElementsByTagName(Utils.TAGS.TOOLTIP.name().toLowerCase());
            if(toolTips != null && toolTips.getLength() > 0){
                sToolTip.append(name == null?"":name);
                if(toolTips.getLength() > 1){
                    sToolTip.append(":<ul><li>");
                }
                for(int i = 0, len =toolTips.getLength(); i < len; i++){
                    Element toolTip = (Element)toolTips.item(i);
                    name = toolTip.getAttribute(Utils.ATTRIBUTES.CPNAME.name().toLowerCase());
                    sToolTip.append(name == null?"":name).append("</li><li>");
                }
                if(toolTips.getLength() > 1){
                    sToolTip.append("</li></ul>");
                }
            }
        return sToolTip.toString();
    }
/**
 * checks, whether file on Path exists
 * @param pPath
 * @return 
 */
    private File checkFile(String pPath) {
       File file = new File(pPath);
       if( file.exists() && file.isFile() && file.canRead()){
           return file;
       }
       LOG.log(Level.WARNING, "file path {0} is not valid", pPath);
       return null;
    }
    
    public static void main(String args[]){
        if(args.length == 0){
            LOG.log(Level.WARNING, "usage: path to criterien.xml");
            System.exit(0);
        }else{
            ImportCriterienTreeFromXml importTree = new ImportCriterienTreeFromXml();
            try {
                if(importTree.doImport(args[0])){
                    System.exit(0);
                }else{
                    System.exit(-1);
                }
            } catch (Exception ex) {
                Logger.getLogger(ImportCriterienTreeFromXml.class.getName()).log(Level.SEVERE, "could not read crits", ex);
            }

        }
    }
}
