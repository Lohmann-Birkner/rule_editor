/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.lb.ruleprocessor.ruleProcessor;

import de.lb.ruleprocessor.checkpoint.ruleGrouper.CRGFileRuleManager;
import de.lb.ruleprocessor.checkpoint.ruleGrouper.CRGInputOutputBasic;
import de.lb.ruleprocessor.checkpoint.ruleGrouper.CRGRule;
import de.lb.ruleprocessor.checkpoint.ruleGrouper.CRGRuleGrouperManager;
import de.lb.ruleprocessor.checkpoint.ruleGrouper.CRGRuleManager;
import de.lb.ruleprocessor.checkpoint.ruleGrouper.CheckpointRuleGrouper;
import de.lb.ruleprocessor.checkpoint.ruleGrouper.CriterionEntry;


import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

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
            m_ruler = new CheckpointRuleGrouper();
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
    

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
       if(args.length < 2){
           LOG.log(Level.INFO, "Usage <path to  ctritrion> <path to rules>");
           System.exit(0);
       }
       RuleProcessor processor = new RuleProcessor(args[0], args[1]);
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

 }
