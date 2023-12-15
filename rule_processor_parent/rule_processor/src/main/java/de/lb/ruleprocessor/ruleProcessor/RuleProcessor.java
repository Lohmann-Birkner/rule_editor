/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.lb.ruleprocessor.ruleProcessor;

import de.lb.ruleprocessor.checkpoint.ruleGrouper.CRGFileRuleManager;
import de.lb.ruleprocessor.checkpoint.ruleGrouper.CRGInputOutputBasic;
import de.lb.ruleprocessor.checkpoint.ruleGrouper.CRGRule;
import de.lb.ruleprocessor.checkpoint.ruleGrouper.CRGRuleElement;
import de.lb.ruleprocessor.checkpoint.ruleGrouper.CRGRuleGrouperManager;
import de.lb.ruleprocessor.checkpoint.ruleGrouper.CRGRuleGrouperStatics;
import de.lb.ruleprocessor.checkpoint.ruleGrouper.CRGRuleManager;
import de.lb.ruleprocessor.checkpoint.ruleGrouper.CheckpointRuleGrouper;
import de.lb.ruleprocessor.checkpoint.ruleGrouper.CriterionEntry;
import de.lb.ruleprocessor.create_criterien.inoutCrit.CriteriumContainer;
import de.lb.ruleprocessor.model.TransferRuleResult;
import java.util.ArrayList;


import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jetbrains.annotations.NotNull;

/**
 *
 * @author gerschmann
 */
public class RuleProcessor {

    private static final Logger LOG = Logger.getLogger(RuleProcessor.class.getName());
    private static String m_path2criterien;
    private static String  m_path2rules;
    private static CRGRuleManager m_ruleManager;
    private static CRGRuleGrouperManager m_ruleGrouperManager;
    private static ImportCriterienTreeFromXml m_importTree;
    protected static CheckpointRuleGrouper m_ruler;
    public RuleProcessor(String path2crit, String path2rules){
        m_path2criterien = path2crit;
        m_path2rules = path2rules;
        init();
    }

       private void init() {
        m_ruleGrouperManager =  CRGRuleGrouperManager.instance();
       // init and read criteien
        m_importTree = new ImportCriterienTreeFromXml();
        try {
            m_importTree.doImport(m_path2criterien);
        } catch (Exception ex) {
            Logger.getLogger(ImportCriterienTreeFromXml.class.getName()).log(Level.SEVERE, "could not read crits", ex);
        }

        //init RuleManager as CRGFileRuleManager
        m_ruleManager = new CRGFileRuleManager(m_path2rules);
        try {
            // read rules through initialisiong onf CheckpointRuleGrouper
            m_ruler = new CheckpointRuleGrouper(m_ruleManager);
            m_ruler.setInout(new CRGInputOutputBasic());

        } catch (Exception ex) {
            Logger.getLogger(RuleProcessor.class.getName()).log(Level.SEVERE, null, ex);
        }
       
    }
       
    public void checkWithRules() throws Exception{
 
        CRGInputOutputBasic inout = m_ruler.newCase();
        fillInoutValues(inout);
        CRGRule[] rules = m_ruler.performCheck();
        for(CRGRule rule:rules){
            LOG.log(Level.INFO, rule.toString());
        }
    }
    
    public TransferRuleResult analyseRule(CRGRule pRule) throws Exception{
        CRGRuleElement root = pRule.getRuleElement();
        CRGInputOutputBasic inout = m_ruler.getInout();
        boolean res = m_ruler.checkTerm4Rule(root,  inout);
        TransferRuleResult oneRes = new TransferRuleResult("0", root.toString(), res, res ? m_ruler.getCodeReferencesForRule(0) : null);
        analyse(root, oneRes, inout);
        return oneRes;
    }
    
     /**
     * Goes thruogh the rule tree and returns true/false result for each term
     * which has "mark" - attribute
     *
     * @param root
     * @param parent
     * @throws Exception
     */
    private void analyse(CRGRuleElement root, TransferRuleResult parent, CRGInputOutputBasic inout) throws Exception {
        if (root == null) {
            return;
        }
        CRGRuleElement[] children = root.m_childElements;
        if (children == null) {
            if (root.m_type == CRGRuleGrouperStatics.TYPE_VALUE) {
                addAnalyseResultValue(root, parent, inout);
            }
            return;
        }
        for (CRGRuleElement child : children) {
            if (child.m_type == CRGRuleGrouperStatics.TYPE_VALUE && child.m_operantType <= 0) {
                parent.addChild(new TransferRuleResult(child.m_mark, child.toString(), parent.isResult()));
                continue;
            }
            CRGRuleElement newCheck = new CRGRuleElement(null);
            String term = child.toString();
            if (child.m_type == CRGRuleGrouperStatics.TYPE_OPERATOR && children.length > 1) {
                parent.addChild(new TransferRuleResult(child.m_mark, child.toString(), parent.isResult()));
                continue;

            } else if (child.m_type == CRGRuleGrouperStatics.TYPE_VALUE) {
// wenn es math operation- übernehmen resultat von parent     
                if (CRGRuleGrouperManager.isDashOperation(child.m_operantType)
                        || CRGRuleGrouperManager.isDotOperation(child.m_operantType)) {
                    TransferRuleResult oneRes = new TransferRuleResult(child.m_mark, child.toString(), parent.isResult());
                    parent.addChild(oneRes);
//                        analyse(child, oneRes, inout);
                    continue;
                }
//                boolean res = ruleGrouper.checkTermRef4Rule(child, inout);
//                parent.addChild(new TransferRuleResult(child.m_mark, child.toString(), res));
                addAnalyseResultValue(child, parent, inout);
                continue;
            } else {
                newCheck = child;
                if (newCheck.m_childElements != null && newCheck.m_childElements.length == 3) {
// check, whether it is a compute term
                    if (CRGRuleGrouperManager.isDashOperation(newCheck.m_childElements[1].m_operantType)
                            || CRGRuleGrouperManager.isDotOperation(newCheck.m_childElements[1].m_operantType)) {
                        TransferRuleResult oneRes = new TransferRuleResult(child.m_mark, child.toString(), parent.isResult());
                        parent.addChild(oneRes);
                        analyse(newCheck, oneRes, inout);
                        continue;

                    } else {
                        boolean res = m_ruler.checkTermRef4Rule(newCheck, inout);
                        TransferRuleResult newCheckRes = new TransferRuleResult(newCheck.m_mark, newCheck.toString(), res, res ? m_ruler.getCodeReferencesForRule(0) : null);
                        parent.addChild(newCheckRes);
                        if (CRGRuleGrouperManager.isOperatorCompare(newCheck.m_childElements[1].m_operantType)) {
// all three children have the same result as the newCheck      
                            TransferRuleResult newCheckChild0 = new TransferRuleResult(newCheck.m_mark, newCheck.m_childElements[0].toString(), res, res ? m_ruler.getCodeReferencesForRule(0) : null);
                            newCheckRes.addChild(newCheckChild0);
                            TransferRuleResult newCheckChild1 = new TransferRuleResult(newCheck.m_mark, newCheck.m_childElements[1].toString(), res, res ? m_ruler.getCodeReferencesForRule(0) : null);
                            newCheckRes.addChild(newCheckChild1);
                            TransferRuleResult newCheckChild2 = new TransferRuleResult(newCheck.m_mark, newCheck.m_childElements[2].toString(), res, res ? m_ruler.getCodeReferencesForRule(0) : null);
                            newCheckRes.addChild(newCheckChild2);

                            continue;
                        } else if (newCheck.m_childElements[1].m_operantType == CRGRuleGrouperStatics.OP_AND
                                || newCheck.m_childElements[1].m_operantType == CRGRuleGrouperStatics.OP_OR) {
// the operation has the same result as newCheck, both member elements are to be checked separately
                            analyse(newCheck.m_childElements[0], newCheckRes, inout);
                            newCheckRes.addChild(new TransferRuleResult(newCheck.m_mark, newCheck.m_childElements[1].toString(), res, res ? m_ruler.getCodeReferencesForRule(0) : null));
                            analyse(newCheck.m_childElements[2], newCheckRes, inout);
                            continue;
                        }
                    }
                }
            }
            boolean res = m_ruler.checkTermRef4Rule(newCheck, inout);

            TransferRuleResult oneRes = new TransferRuleResult(newCheck.m_mark, term, res, res ? m_ruler.getCodeReferencesForRule(0) : null);
            //AWI-20190605: Remove mark check, due to i did not care at this point
            if (newCheck.m_mark != null //                   && !child.m_mark.isEmpty()
                    ) {
                parent.addChild(oneRes);
            } else {
                oneRes = parent;
            }
            analyse(newCheck, oneRes, inout);
        }

    }

    private void addAnalyseResultValue(CRGRuleElement child, TransferRuleResult parent, CRGInputOutputBasic inout) throws Exception {
// wenn es math operation- übernehmen resultat von parent     
//        if(CRGRuleGrouperManager.isDashOperation(child.m_operantType)
//               || CRGRuleGrouperManager.isDotOperation(child.m_operantType)){
//               TransferRuleResult oneRes = new TransferRuleResult(child.m_mark, child.toString(), parent.isResult());
//                parent.addChild(oneRes);
//                analyse(child, oneRes, inout);
//
//        }else{
        CRGRuleElement temp = new CRGRuleElement(null);
        temp.m_childElements = new CRGRuleElement[1];
        temp.m_childElements[0] = child;
        temp.m_childCount = 1;
//            if((CRGRuleGrouperManager.isDashOperation(child.m_operantType)
//                       || CRGRuleGrouperManager.isDotOperation(child.m_operantType) && child.m_criterionIndex > 0){
        boolean res = m_ruler.checkTermRef4Rule(temp, inout);
        parent.addChild(new TransferRuleResult(child.m_mark, child.toString(), res, res ? m_ruler.getCodeReferencesForRule(0) : null));
//        }

    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
       if(args.length < 3){
           LOG.log(Level.INFO, "Usage path to excel> <path to  ctritrion> <path to rules>");
           System.exit(0);
       }
                CreateXmlFromExcel coverter = new CreateXmlFromExcel(args[0], args[1]);   
        try {
            CriteriumContainer container = coverter.doExecute();
            coverter.write2xml(container);
        } catch (Exception ex) {
            Logger.getLogger(CreateXmlFromExcel.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(-1);
        }
        
       RuleProcessor processor = new RuleProcessor(args[1], args[2]);
        try {
            processor.checkWithRules();
        } catch (Exception ex) {
            Logger.getLogger(RuleProcessor.class.getName()).log(Level.SEVERE, null, ex);
        }
       System.exit(0);
    }

    private void fillInoutValues(CRGInputOutputBasic inout)throws Exception {
        //first_name
        CriterionEntry crit = m_ruleGrouperManager.getCriterionByName("first_name");
        inout.setAnyValue(crit, "A1");
        //date
        crit = m_ruleGrouperManager.getCriterionByName("admission_date");
        inout.setAnyValue(crit, new Date());

        // ops
        crit = m_ruleGrouperManager.getCriterionByName("admission_reason12");
        inout.setAnyValue(crit, 1);
        
        crit = m_ruleGrouperManager.getCriterionByName("OPSDatum");
        inout.setAnyValue(crit, new Date());

        crit = m_ruleGrouperManager.getCriterionByName("AnzahlProzeduren");
        inout.setAnyValue(crit, 2);

        LOG.log(Level.INFO, inout.dumpRecord().toString());
    }
    
    public CRGRule[] checkWithRules(@NotNull Map<String, Object> pCriteriaValues) throws Exception{
        setCriteriaValues(pCriteriaValues);
        return m_ruler.performCheck();
    }
    
    private void setCriteriaValues(@NotNull Map<String, Object> pCriteriaValues) throws Exception{
        CRGInputOutputBasic inout = m_ruler.newCase();
        Set<String> crteriaNames = pCriteriaValues.keySet();
        for(String criteriaName: crteriaNames){
            CriterionEntry crit = m_ruleGrouperManager.getCriterionByName(criteriaName);
            if(crit == null){
                LOG.log(Level.INFO, "for {0} no criteria found", criteriaName);
                continue;
            }
            inout.setAnyValue(crit, pCriteriaValues.get(criteriaName));
        }
    }
    
    public String getDump(){
        return m_ruler.getInout().dumpRecord().toString();
    }
    
    public CRGRule[] getAllRulesAsList() throws Exception{
        return m_ruleManager.getAllRules();
    }

 }
