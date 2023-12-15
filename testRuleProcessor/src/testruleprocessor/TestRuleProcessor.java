/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testruleprocessor;

import de.lb.ruleprocessor.checkpoint.ruleGrouper.CRGRule;
import de.lb.ruleprocessor.create_criterien.inoutCrit.Criterium;
import de.lb.ruleprocessor.create_criterien.inoutCrit.CriteriumContainer;
import de.lb.ruleprocessor.model.TransferRuleResult;
import de.lb.ruleprocessor.ruleProcessor.CreateXmlFromExcel;
import de.lb.ruleprocessor.ruleProcessor.RuleProcessor;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author gerschmann
 */
public class TestRuleProcessor {

    private static final Logger LOG = Logger.getLogger(TestRuleProcessor.class.getName());


    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
       if(args.length < 3){
           LOG.log(Level.INFO, "Usage path to excel> <path to  ctritrion> <path to rules>");
           System.exit(0);
       }
                CreateXmlFromExcel coverter = new CreateXmlFromExcel(args[0], args[1]);   
        try {
            CriteriumContainer container = coverter.doExecute();
            coverter.write2xml(container);
            Map<String, List<Criterium>> criteria = container.getCriterien();
            if(criteria.isEmpty()){
                LOG.log(Level.INFO, "no criteria found");
                System.exit(0);
            }
            LOG.log(Level.INFO, criteria.toString());
        } catch (Exception ex) {
            Logger.getLogger(CreateXmlFromExcel.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(-1);
        }
        
       RuleProcessor processor = new RuleProcessor(args[1], args[2]);
       
       Map<String, Object> pCriteriaValues = createTestMap();
        try {
            CRGRule[] allRules = processor.getAllRulesAsList(); 
            printRules(allRules);
            LOG.log(Level.INFO, "All rules read from path {}", args[2]);
            CRGRule[] rules = processor.checkWithRules(pCriteriaValues);  
            LOG.log(Level.INFO, "Values set: {0}", processor.getDump()); 
            LOG.log(Level.INFO, "Postive rule results: {0}", rules== null||rules.length == 0?"not found":rules.length);
            printRules(rules);
            checkRules(processor, rules);
        } catch (Exception ex) {
            Logger.getLogger(RuleProcessor.class.getName()).log(Level.SEVERE, null, ex);
        }
       System.exit(0);
    }

    private static Map<String, Object> createTestMap(){
        Map<String, Object> map = new HashMap<>();
        map.put("first_name", "first_name");
        Calendar cal = Calendar.getInstance();
        cal.set(2022,03,03);
        map.put("admission_date", cal.getTime());
        map.put("status", "draft");
        return map;
    }    
    
    private static void printRules(CRGRule[] rules){
             if(rules == null || rules.length == 0){
               return;
            }
            else{
                for(CRGRule rule:rules){
                    LOG.log(Level.INFO, rule.toString());
                }
            }
       
    }

    private static void checkRules(RuleProcessor processor, CRGRule[] rules) throws Exception{
         if(rules == null || rules.length == 0){
               return;
        }
        for(CRGRule rule: rules){
            TransferRuleResult result = processor.analyseRule(rule);
            LOG.log(Level.INFO, "result for rule {0}: ", rule.toShortString());
            LOG.log(Level.INFO, result.toString());
        } 
    }
}
