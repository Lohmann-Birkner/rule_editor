package de.lb.ruleprocessor.create_criterien.inoutCrit.tree.export;




import de.checkpoint.ruleGrouper.CRGRuleGrouperManager;
import de.checkpoint.ruleGrouper.CRGRuleGrouperManagerCP;
import de.checkpoint.ruleGrouper.CRGRuleGrouperStatics;
import de.checkpoint.ruleGrouper.CriterionEntry;
import de.checkpoint.server.appServer.AppResourceBundle;
import de.checkpoint.server.appServer.AppResources;
import de.checkpoint.server.data.caseRules.DatCaseRuleConstants;
import de.checkpoint.server.data.caseRules.DatRulesAction;
import de.checkpoint.server.data.caseRules.DatRulesCriterion;
import de.checkpoint.server.data.caseRules.DatRulesOperator;
import de.checkpoint.server.data.caseRules.DsrRule;
import de.checkpoint.server.xml.XMLDOMWriter;

import de.lb.ruleprocessor.create_criterien.inoutCrit.Utils;
import de.lb.ruleprocessor.create_criterien.inoutCrit.Utils.ATTRIBUTES;
import de.lb.ruleprocessor.create_criterien.inoutCrit.Utils.TAGS;
import de.lb.ruleprocessor.create_criterien.inoutCrit.Utils.USAGE;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * for simplisity, because this Program will be used not often
 * the Checkpoint ApplicationServer has to be on
 * Exportiert alle Regelkriterien in For des vordefinierten XML Baumes
 * für CPX - Regeleditor
 * @author gerschmann
 */
public class ExportCriterienTreeAsXml {

    static final String CURRENCY_DOUBLE_FORMAT = "#0.00";
    static final String NONMONETARY_DOUBLE_FORMAT = "#0.000";
    static final String LABOR_DOUBLE_FORMAT = "#0.0000";
    static final String ALOS_DOUBLE_FORMAT = "#0.0";
    static final String INTEGER_FORMAT_2 = "00";
    
    static final Logger LOG = Logger.getLogger(ExportCriterienTreeAsXml.class.getName());
    private final Map <String, String> resources_de = new HashMap<>();//<text from resource, key>
    private final Map<String, String> cpx_resources = new HashMap<>();// <key, resource>
    private final Map<String, Integer> critTypes = new HashMap<>(); // <name, ident>
    private final Map<Integer, String> ident2Type = new HashMap<>(); // <ident, name>
    private final Map<Integer, String> type2OperationsGroup = new HashMap<>(); // <ident, name>
    private final Map <String,DatRulesOperator[]> operationGroups = new HashMap<> ();
    private final Map <String,DatRulesCriterion[]> intervalGroups = new HashMap<> ();
    private final Map<String, String> operationNames = new HashMap<>();// <displayname, fieldname>
    private int tmpCount = 0;// for long strings wenn key not found will generate rule.tmp.nnn
    private static final String SOLE_NEW_PATTERN = "Hilfs";
    private static final String SOLE_NEW_PATTERN_1 = AppResources.getResource(AppResourceBundle.TXT_HPM_POS_NR);
    
    public static void main(String args[]){

        if(args == null || args.length < 3){
            LOG.log(Level.INFO, " usage: Exportpath, resources_de.properties - Path, result properties, group - optional");
            System.exit(0);
        }
         ExportCriterienTreeAsXml xml = new ExportCriterienTreeAsXml();
        if(args.length > 3){
       
            xml.doExport(args[0], args[1], args[2], args[3]);
        }else{
            xml.doExport(args[0], args[1], args[2]);
        }
    }

    private void doExport(String outputPath, String inResource, String outResource) {
        doExport(outputPath, inResource, outResource, null);
    }
    private void doExport(String outputPath, String inResource, String outResource, String groupName) {

        if(! Utils.getDataTypes(critTypes, ident2Type, type2OperationsGroup)){
            LOG.log(Level.INFO, "The execution was aborted because of error by collecting of data types");
            
        }
        if(readResources(inResource)){
            HashMap<String, Document> results = createFullTree(groupName);
            if(results != null){
                Set <String> names = results.keySet();
                for(String name :names){
                    if(!writeXMLDocument(results.get(name), outputPath + File.separator + name + ".xml")){
                        LOG.log(Level.INFO, " Error on creating of the xml file");
                    }
                }
                if(!writeResources4Cpx(outResource)){
                   LOG.log(Level.INFO, " Error on save CPX resorce file");
               }
           }else{
                LOG.log(Level.INFO, " Error on creating of the xml document");
            }
            
        }else{
            LOG.log(Level.INFO, "The execution was aborted because of error by reading of {0}", inResource);
        }
    }
    
    /**
     * creates DOM Objecte from the criterion tree für jede Supergroup und s.w.
     * @return 
     */
    private HashMap<String, Document> createFullTree(String groupName){
        HashMap<String, Document> docs = new HashMap<>();
        try{
//  criterion tree            
            Object[][] fullTree = groupName == null? CRGRuleGrouperManagerCP.getTreeRulesCriterion(false):CRGRuleGrouperManagerCP.getGroupAsTree(groupName);
            for(Object[] oneGroup: fullTree){
                // supergroup 
                CriterionEntry superGroup = (CriterionEntry)oneGroup[0];//supergroup
                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                DocumentBuilder db = dbf.newDocumentBuilder();
                Document doc = db.newDocument();
                Element root = doc.createElement(TAGS.CRITERION_TREE.name().toLowerCase());
                doc.appendChild(root);
                Element eSuperGroup = getCriterionAsDOMObject(superGroup, root, doc, TAGS.SUPERGROUP);
                if(oneGroup[1] instanceof HashMap){
                    HashMap<CriterionEntry, Vector<CriterionEntry>> groups = (HashMap)oneGroup[1]; // HashMap<>
                    Set<CriterionEntry> sGroups = groups.keySet();
                    for(CriterionEntry group: sGroups){
                        Element eGroup = getCriterionAsDOMObject(group, eSuperGroup, doc, TAGS.GROUP);
                         Vector<CriterionEntry> entries = groups.get(group);
                         for(CriterionEntry entry: entries){
                            getCriterionAsDOMObject(entry, eGroup, doc, TAGS.CRITERION); 
                         }
                    }
                }else if (oneGroup[1] instanceof Vector){
                    Vector<CriterionEntry> group = (Vector)oneGroup[1];
                    Element eGroup = getCriterionAsDOMObject(superGroup, eSuperGroup, doc, TAGS.GROUP);
                   for(CriterionEntry entry: group){
                       getCriterionAsDOMObject(entry, eGroup, doc, TAGS.CRITERION);

                    }
                }
                docs.put(getResourceKey2Description(superGroup.getWorkText()), doc);
            }
// data types            
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.newDocument();
            Element root = doc.createElement(TAGS.TYPES_AND_OPERATIONS.name().toLowerCase());
            doc.appendChild(root);
            if(!addCriterienTypes(doc, root)){
                LOG.log(Level.WARNING, "no data types elements created, can't be right, generated document is not complete");               
            }
            docs.put(TAGS.CRITERION_TYPES.name(), doc);
//operator groups
            if(!addOperatorGroups(doc, root)){
                 LOG.log(Level.WARNING, "error on creating of the operation groups");
            }
// intervals     
            if(!addIntervalGroups(doc, root)){
                  LOG.log(Level.WARNING, "error on creating of the interval groups");               
            }
// sugggestion actions
            if(!addSuggestionActions(doc, root)){
                  LOG.log(Level.WARNING, "error on creating of group of the suggestion actions");               
            };
            return docs;
        }catch(ParserConfigurationException | DOMException ex){
            LOG.log(Level.SEVERE, " Error on creating of the xml document", ex);
            return null;
        }
    }

    /**
     * reads the whole resources_de.properties in the resources_de HashMap
     * so that the definitoins, which are used in any criterium can be
     * selected into the result resources
     * @param inResource
     * @return 
     */
    private boolean readResources(String inResource) {
        File file = new File(inResource);
        if(file.exists()){
            Properties props = new Properties();
            try{
                
                BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
                 props.load(bis);
                 bis.close();  
            }catch(IOException ex){
                LOG.log(Level.SEVERE, " error on reading " + inResource + " file", ex);
                return false;
            }
            Enumeration<?> en = props.propertyNames();
            while(en.hasMoreElements()){
                String key = (String)en.nextElement();
                String value = props.getProperty(key);
                resources_de.put(value, key);
            }
            return true;
        }
        return false;
    }
    
    
    
    /**
     * creates one Eelement to CriterionEntry
     * @param entry
     * @param parent
     * @param doc
     * @return 
     */
    private Element getCriterionAsDOMObject(CriterionEntry entry, Element parent,  Document doc, TAGS tag)
    {
	Element ele = doc.createElement(tag.name().toLowerCase());
        ele.setAttribute(ATTRIBUTES.NAME.name().toLowerCase(), getResourceKey2Description(entry.getWorkText()));
        ele.setAttribute(ATTRIBUTES.DISPLAY_NAME.name().toLowerCase(), getResourceKey2Description(entry.getDisplayText(), entry.getWorkText()));
        ele.setAttribute(ATTRIBUTES.CPNAME.name().toLowerCase(), entry.getWorkText());
// interval allowed
        if(tag.equals(TAGS.GROUP)) { 
            if(CRGRuleGrouperManager.isAnyIntervalCrit(entry.getM_groupCPIndex())){
                ele.setAttribute(ATTRIBUTES.HAS_INTERVAL.name().toLowerCase(), "true");
            }
        }
        if(tag.equals(TAGS.CRITERION)){
// data type            
            ele.setAttribute(ATTRIBUTES.CRITERION_TYPE.name().toLowerCase(), getType2Ident(entry.getType()));
// usage
            ele.setAttribute(ATTRIBUTES.USAGE.name().toLowerCase(), this.getUsage2Ident(entry.getM_usedInSugg()));
// criterien index
        ele.setAttribute(ATTRIBUTES.CRITERION_INDEX.name().toLowerCase(), String.valueOf(entry.getM_index()));
        if(entry.isM_isDepended()){
            ele.setAttribute(ATTRIBUTES.IS_DEPENDED.name().toLowerCase(), "true");
            ele.setAttribute(ATTRIBUTES.DEPEND_INDEX.name().toLowerCase(), String.valueOf(entry.getM_depend()));
            ele.setAttribute(ATTRIBUTES.DEPEND_GROUP_INDEX.name().toLowerCase(), String.valueOf(entry.getM_dependGroupIndex()));
            ele.setAttribute(ATTRIBUTES.DEPEND_CRIT_CHECKDATE_INDEX.name().toLowerCase(), String.valueOf(entry.getM_dependCritCheckIndex()));
            ele.setAttribute(ATTRIBUTES.IS_CROSS_CASE.name().toLowerCase(), String.valueOf(entry.isM_isCrossCase()));
        }
        if(entry.isM_isCummulative()){
           ele.setAttribute(ATTRIBUTES.IS_CUMMULATIVE.name().toLowerCase(), "true");
            ele.setAttribute(ATTRIBUTES.CUMMLATE_BASE_INDEX.name().toLowerCase(), String.valueOf(entry.getM_indCummulateBase()));
            ele.setAttribute(ATTRIBUTES.CUMMULATE_COUNT_INDEX.name().toLowerCase(), String.valueOf(entry.getM_indCummulateNumber()));
            ele.setAttribute(ATTRIBUTES.CUMMULATE_WHAT_INDEX.name().toLowerCase(), String.valueOf(entry.getM_indCummulateWhat()));
            ele.setAttribute(ATTRIBUTES.CUMMULATE_DATE_INDEX.name().toLowerCase(), String.valueOf(entry.getM_indCummulateDate()));
            
        }
// nachkommastellen für double            
            if(entry.getType() == CRGRuleGrouperStatics.DATATYPE_ARRAY_DOUBLE 
                    || entry.getType() == CRGRuleGrouperStatics.DATATYPE_DOUBLE){
                
                if(CRGRuleGrouperManagerCP.isCurrency(entry.getM_index(),entry.getType())){
                    ele.setAttribute(ATTRIBUTES.DOUBLE_FORMAT.name().toLowerCase(), CURRENCY_DOUBLE_FORMAT);
                }
                else{
                    ele.setAttribute(ATTRIBUTES.DOUBLE_FORMAT.name().toLowerCase(), NONMONETARY_DOUBLE_FORMAT);
                   
                }
            }
//            else 
//                if (entry.getWorkText().equals(CRGRuleGrouperManager_old.CRIT_CLINIC_ADMISSION_REASON)
//                    ||entry.getWorkText().equals(CRGRuleGrouperManager_old.CRIT_CLINIC_DISCHARGE_REASON)
//                    ||entry.getWorkText().equals(CRGRuleGrouperManager_old.CRIT_ADM_TYPE)
//                    ||entry.getWorkText().equals(CRGRuleGrouperManager_old.CRIT_ADM2_TYPE)
//                    ||entry.getWorkText().equals(CRGRuleGrouperManager_old.CRIT_DIS_TYPE)
//                    ){
//
//                     ele.setAttribute(ATTRIBUTES.INTEGER_FORMAT.name().toLowerCase(), INTEGER_FORMAT_2);
//                    
//                
//                
//            }
        }
            Object[][] objs = entry.getM_critValuesBasic(); 

            if(objs != null ){
                if(entry.isM_addedAdditional()){
                    objs = entry.getCritValues();
                }
                for(Object[] values: objs){
                    Element tooltip = doc.createElement(TAGS.TOOLTIP.name().toLowerCase());
                    
                    tooltip.setAttribute(ATTRIBUTES.VALUE.name().toLowerCase(), String.valueOf(values[0]).replaceAll("'", ""));
                    
                    tooltip.setAttribute(ATTRIBUTES.DESCRIPTION.name().toLowerCase(), getResourceKey2Description(String.valueOf(values[1])));
                    ele.appendChild(tooltip);
                }
            }else{
                String[] toolTips = entry.getM_descriptionTextBase();
                if(toolTips != null){
                    for(String tt:toolTips){
// for SOLE_NEW_PATTERN
                        if(tt.contains("$PATT$")){
                            tt = tt.replace("$PATT$", SOLE_NEW_PATTERN);
                        }
                        if(tt.contains("$PATT1$")){
                            tt = tt.replace("$PATT$", SOLE_NEW_PATTERN_1);
                        }
                        Element tooltip = doc.createElement(TAGS.TOOLTIP.name().toLowerCase());
                        tooltip.setAttribute(ATTRIBUTES.DESCRIPTION.name().toLowerCase(), getResourceKey2Description(String.valueOf(tt), entry.getWorkText()));
                        tooltip.setAttribute(ATTRIBUTES.CPNAME.name().toLowerCase(), tt);
                         ele.appendChild(tooltip);   
                    }
                }
            }

        parent.appendChild(ele);
        return ele;
    }
    
    private String getResourceKey2Description(String descr){
        return getResourceKey2Description(descr, null);
    }
    
    private String getResourceKey2Description(String descr, String workText){
        String resourceKey = resources_de.get(descr);
        if(resourceKey == null){
            resourceKey = createTmpResourceKey(descr, workText);
            LOG.log(Level.WARNING, "for {0} no entry in resources_de found, created: rules.{1}", new Object[]{descr, resourceKey});

        }
        resourceKey = "rules." + resourceKey;
        this.cpx_resources.put(resourceKey, descr);
        return resourceKey;
    }
    
    private String createTmpResourceKey(String text, String workText){
// max length 100
        if(workText == null){
                
                text = allReplaces(text);
                if(text.length() > 100){
                    text = text.substring(0, 100);

                }
                return text;
        }else{
            // workText is never too long and does not have blanks
                workText = allReplaces(workText);
                tmpCount++;
            return "temp."+ tmpCount + "." + workText;
        }
    }
    
    private String allReplaces(String text){
        text = text.toLowerCase();
        text = text.replaceAll("_", " ");
        text = text.replaceAll("-", " ");
        text = text.replaceAll("   ", " ");
        text = text.replaceAll("  ", " ");
        text = text.replaceAll(" ", ".");
        text = text.replaceAll("ü", "ue");
        text = text.replaceAll("ä", "ae");
        text = text.replaceAll("ö", "oe");
        text = text.replaceAll("ß", "ss");
        text = text.replaceAll("/", "");
        text = text.replaceAll("\\\\", "");
        text = text.replaceAll(":", "");
        text = text.replaceAll("$", "");
        text = text.replaceAll("§", "");
        text = text.replaceAll("\\(", "");
        text = text.replaceAll("\\)", "");
        text = text.replaceAll("\\+", "");
        text = text.replaceAll("\\.\\.", "\\.");
        return text;
    }


    
    private String getType2Ident(int ident){
        String ret = ident2Type.get(ident);
        if(ret == null){
            return "unknown";
        }
        return ret;
    }
    
    private String getUsage2Ident(int ident){
        USAGE use[] = USAGE.values();
        for(USAGE u: use){
            if(u.ordinal() == ident){
                return u.name();
            }
        }
        return USAGE.CRIT_RULE_ONLY.name();
    }
    
    private boolean writeXMLDocument(Document doc, String path)
    {
        try {
            File xmlFile = null;
            OutputStream os = new ByteArrayOutputStream();
            PrintWriter pw = new PrintWriter(new OutputStreamWriter(os,
                                             "UTF-16"));

            if(path != null) {
                    xmlFile = new File(path);
            } else {
                    xmlFile = File.createTempFile("test", ".xml");
                    xmlFile.deleteOnExit();
            }
            FileOutputStream fos = new FileOutputStream(xmlFile);
            DataOutputStream output = new DataOutputStream(fos);

            XMLDOMWriter.setWriterEncoding("UTF-16");
            XMLDOMWriter.print(doc, pw, false);
            output.writeBytes(os.toString());
            output.flush();
            output.close();
            if(fos != null) {
                fos.flush();
                fos.close();
            }
        } catch(Exception ex) {
            LOG.log(Level.SEVERE, " Error on creating of the xml file", ex);
            return false;
        }

        return true;
    }
/**
 * writes resource which are used in criterion tree into file
 * @param outResource
 * @return 
 */
    private boolean writeResources4Cpx(String outResource) {
        if(cpx_resources.isEmpty()){
            LOG.log(Level.SEVERE, "no resources found, cann not be right");
            return false;        
        }

        try{
            java.io.BufferedWriter out_result = new java.io.BufferedWriter(new
                   java.io.FileWriter(outResource));
            out_result.append("#generated from Checkpoint resources");
            out_result.newLine();
            Set<String> keys = cpx_resources.keySet();
            ArrayList<String>list = new ArrayList(keys);
            Collections.sort(list);
            for(String key: list){

                String testkey = key.replace(".-.", ".");
                out_result.append(testkey).append("=").append(cpx_resources.get(key));
                out_result.newLine();
            }
            out_result.flush();
            out_result.close();

            return true;
        }catch (IOException ex) {
            LOG.log(Level.SEVERE, "error on writing cpx - resources", ex);
            return false;   
        }  
    }
/**
 * adds the criterion_types group to the export document
 * @param result
 * @return 
 */
    private boolean addCriterienTypes(Document doc, Element root) {
        Element allTypes =  doc.createElement(TAGS.CRITERION_TYPES.name().toLowerCase());
        root.appendChild(allTypes);
        if(critTypes == null || critTypes.isEmpty()){
            LOG.log(Level.SEVERE, "no data types elements created, can't be right, generated document is not complete");
            return false;
        }
        Set <String> types = critTypes.keySet();
        for(String type: types){
            Element eType = doc.createElement(TAGS.CTRITERION_TYPE.name().toLowerCase());
            eType.setAttribute(ATTRIBUTES.NAME.name().toLowerCase(), type);
            eType.setAttribute(ATTRIBUTES.IDENT.name().toLowerCase(), String.valueOf(critTypes.get(type).intValue()));

            eType.setAttribute(ATTRIBUTES.OPERATION_GROUP.name().toLowerCase(), type2OperationsGroup.get(critTypes.get(type)));
            allTypes.appendChild(eType);
        }
        return true;
    }

/**
 * adds groups of operations
 * @param doc
 * @param root
 * @return 
 */
    private boolean addOperatorGroups(Document doc, Element root) {
        try{
// all used operations with their display texts
            Element operations = doc.createElement(TAGS.OPERATION_GROUPS.name().toLowerCase());
            root.appendChild(operations);
            if(! Utils.getOperationGroups(operationGroups, operationNames)){
                 LOG.log(Level.SEVERE, "error on creating of the operation groups");
                return false;
            }
            HashMap<String, DatRulesOperator> allOperations = new HashMap<>();
            Set<String>groupNames = operationGroups.keySet();
            for(String group:groupNames){                
                Element eGroup = doc.createElement(TAGS.OPERATION_GROUP.name().toLowerCase());
                operations.appendChild(eGroup);
                DatRulesOperator[] ops = operationGroups.get(group);
                if(ops == null){
                    if(group.equals("opListNested")){
                        ops = DatCaseRuleConstants.getNestedOperator();
                    }else{
                        continue;
                    }
                }
                eGroup.setAttribute(ATTRIBUTES.NAME.name().toLowerCase(), group);
                for(DatRulesOperator op:ops){
                    Element eOp = doc.createElement(TAGS.OPERATION.name().toLowerCase());
                    String workName =  operationNames.get(op.toString());
                    eOp.setAttribute(ATTRIBUTES.NAME.name().toLowerCase(), workName);
//                    if(!allOperations.containsKey(workName)){
//                        allOperations.put(workName, op);
//                    }
                    eOp.setAttribute(ATTRIBUTES.DISPLAY_NAME.name().toLowerCase(), getResourceKey2Description(op.toString()));
                    eOp.setAttribute(ATTRIBUTES.NAME.name().toLowerCase(), DsrRule.getXMLText(op.getWorkText()));
                    eOp.setAttribute(ATTRIBUTES.NESTED.name().toLowerCase(), String.valueOf(op.m_isNested));
                    eGroup.appendChild(eOp);
                }
            }
//            Set<String>ops = allOperations.keySet();
//            operations = doc.createElement(TAGS.OPERATIONS.name().toLowerCase());
//            root.appendChild(operations);
//            for(String op:ops){
//                DatRulesOperator dOp = allOperations.get(op);
//                Element eOp = doc.createElement(TAGS.OPERATION.name().toLowerCase());
//                eOp.setAttribute(ATTRIBUTES.NAME.name().toLowerCase(), dOp.getWorkText());
//                eOp.setAttribute(ATTRIBUTES.DISPLAY_NAME.name().toLowerCase(), getResourceKey2Description(dOp.toString()));
//                eOp.setAttribute(ATTRIBUTES.NESTED.name().toLowerCase(), String.valueOf(dOp.m_isNested));
//                operations.appendChild(eOp);
//            }
        }catch(DOMException ex){
            LOG.log(Level.SEVERE, "error on creating of the opertion groups", ex);
            return false;
            
        }
        return true;
    }

/**
 * adds Groups of intervals
 * @param doc
 * @param root
 * @return 
 */    
    private boolean addIntervalGroups(Document doc, Element root) {
        try{
// all used operations with their display texts
            Element intervals = doc.createElement(TAGS.INTERVAL_GROUPS.name().toLowerCase());
            root.appendChild(intervals);
            Map <String, String> intervalRules = new HashMap<>();
            if(! Utils.getIntervalTypes(intervalGroups, intervalRules)){ 
                 LOG.log(Level.SEVERE, "error on creating of the operation groups");
                return false;
            }
            HashMap<String, DatRulesCriterion> allIntervalLimits = new HashMap<>();
            Set<String>groupNames = intervalGroups.keySet();
            for(String group:groupNames){                
                Element eGroup = doc.createElement(TAGS.INTERVAL_GROUP.name().toLowerCase());
                intervals.appendChild(eGroup);
                DatRulesCriterion[] ints = intervalGroups.get(group);
                eGroup.setAttribute(ATTRIBUTES.NAME.name().toLowerCase(), group);
                for(DatRulesCriterion interv:ints){
                    Element eOp = doc.createElement(TAGS.INTERVAL_LIMIT.name().toLowerCase());
                    eOp.setAttribute(ATTRIBUTES.NAME.name().toLowerCase(), interv.getWorkText());
                    if(!allIntervalLimits.containsKey(interv.getWorkText())){
                        allIntervalLimits.put(interv.getWorkText(), interv);
                    }
                    eGroup.appendChild(eOp);
                }
            }
            Set<String>interv = allIntervalLimits.keySet();
            intervals = doc.createElement(TAGS.INTERVAL_LIMITS.name().toLowerCase());
            root.appendChild(intervals);
            for(String oneInt:interv){
                DatRulesCriterion dInt = allIntervalLimits.get(oneInt);
                Element eOp = doc.createElement(TAGS.INTERVAL_LIMIT.name().toLowerCase());
                eOp.setAttribute(ATTRIBUTES.NAME.name().toLowerCase(), dInt.getWorkText());
                eOp.setAttribute(ATTRIBUTES.DISPLAY_NAME.name().toLowerCase(), getResourceKey2Description(dInt.toString()));
                eOp.setAttribute(ATTRIBUTES.SINGLE.name().toLowerCase(), String.valueOf(dInt.m_isSingle));
                intervals.appendChild(eOp);
            }
// rules for  relationships of interval values inteval_limit-> interval_group          
            Element intRules = doc.createElement(TAGS.INTERVAL_RULES.name().toLowerCase());
            Set<String> rules = intervalRules.keySet();
            for(String rule: rules){
                Element eRule = doc.createElement(TAGS.INTERVAL_RELATION.name().toLowerCase());
                eRule.setAttribute(ATTRIBUTES.INTERVAL_LIMIT.name().toLowerCase(), rule);
                eRule.setAttribute(ATTRIBUTES.INTERVAL_GROUP.name().toLowerCase(), intervalRules.get(rule));
                intRules.appendChild(eRule);
            }
            root.appendChild(intRules);
        }catch(DOMException ex){
            LOG.log(Level.SEVERE, "error on creating of the opertion groups", ex);
            return false;
            
        }
        return true;
    }

    private boolean addSuggestionActions(Document doc, Element root) {
        Element actGroup = doc.createElement(TAGS.SUGG_ACTION_GROUPS.name().toLowerCase());
        root.appendChild(actGroup);
        Map<String, DatRulesAction[]> actionGroups = new HashMap<>();
        if(!Utils.getSuggActionGroups(actionGroups)){
            LOG.log(Level.SEVERE, "error on creating of the suggsetion action groups");
            return false;
        }
        Map<String, DatRulesAction> actions = new HashMap<>();
        Set<String>keys = actionGroups.keySet();
        for(String key:keys){
            Element eGroup = doc.createElement(TAGS.SUGG_ACTION_GROUP.name().toLowerCase());
            eGroup.setAttribute(ATTRIBUTES.NAME.name().toLowerCase(), key);
            actGroup.appendChild(eGroup);
            DatRulesAction[] actList = actionGroups.get(key);
            if(actList == null){
                LOG.log(Level.SEVERE, " error on create suggestion group list");
                continue;
            }
            for(DatRulesAction act:actList){
                Element eAct = doc.createElement(TAGS.SUGG_ACTION.name().toLowerCase());
                eAct.setAttribute(ATTRIBUTES.NAME.name().toLowerCase(), act.m_workText);
                actGroup.appendChild(eAct);
                actions.put(act.m_workText, act);
            }
        }
// actions
        actGroup = doc.createElement(TAGS.SUGG_ACTIONS.name().toLowerCase());
        root.appendChild(actGroup);
        Set<Entry<String, DatRulesAction>>acts = actions.entrySet();
        for(Entry act:acts){
            DatRulesAction dAct = (DatRulesAction)act.getValue();
            Element eAct = doc.createElement(TAGS.SUGG_ACTION.name().toLowerCase());
            eAct.setAttribute(ATTRIBUTES.NAME.name().toLowerCase(), dAct.m_workText);
            eAct.setAttribute(ATTRIBUTES.DISPLAY_NAME.name().toLowerCase(), getResourceKey2Description(dAct.m_displayText));
            eAct.setAttribute(ATTRIBUTES.IDENT.name().toLowerCase(), String.valueOf(dAct.m_id));
            actGroup.appendChild(eAct);
        }
        return true;
    }


}
