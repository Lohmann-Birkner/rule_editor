/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.lb.ruleprocessor.create_criterien.inoutCrit;

import de.checkpoint.ruleGrouper.CRGRuleGrouperStatics;
import static de.checkpoint.ruleGrouper.CRGRuleGrouperStatics.DATATYPE_ALL_OPERATIONS_NESTED;
import static de.checkpoint.ruleGrouper.CRGRuleGrouperStatics.DATATYPE_ARRAY_DATE;
import static de.checkpoint.ruleGrouper.CRGRuleGrouperStatics.DATATYPE_ARRAY_DAY_TIME;
import static de.checkpoint.ruleGrouper.CRGRuleGrouperStatics.DATATYPE_ARRAY_DOUBLE;
import static de.checkpoint.ruleGrouper.CRGRuleGrouperStatics.DATATYPE_ARRAY_INTEGER;
import static de.checkpoint.ruleGrouper.CRGRuleGrouperStatics.DATATYPE_ARRAY_STRING;
import static de.checkpoint.ruleGrouper.CRGRuleGrouperStatics.DATATYPE_DATE;
import static de.checkpoint.ruleGrouper.CRGRuleGrouperStatics.DATATYPE_DATE_WITH_TIME_SHOW;
import static de.checkpoint.ruleGrouper.CRGRuleGrouperStatics.DATATYPE_DAY_TIME;
import static de.checkpoint.ruleGrouper.CRGRuleGrouperStatics.DATATYPE_DOUBLE;
import static de.checkpoint.ruleGrouper.CRGRuleGrouperStatics.DATATYPE_INTEGER;
import static de.checkpoint.ruleGrouper.CRGRuleGrouperStatics.DATATYPE_STRING;
import static de.checkpoint.ruleGrouper.CRGRuleGrouperStatics.DATATYPE_UNFORMATTED;
import de.checkpoint.server.data.caseRules.DatCaseRuleConstants;
import de.checkpoint.server.data.caseRules.DatRulesCriterion;
import de.checkpoint.server.data.caseRules.DatRulesOperator;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author gerschmann
 */
public class Utils {

    private static final Logger LOG = Logger.getLogger(Utils.class.getName());

    // tag names !!
    public enum TAGS{
        CRITERION_TREE,
        SUPERGROUP,
        GROUP,
        CRITERION,
        RULES_OPERATOR,
        CRITERION_TYPES,
        CTRITERION_TYPE,
        OPERATION,
        OPERATION_GROUPS,
        OPERATIONS,
        TOOLTIP, 
        OPERATION_GROUP, 
        INTERVAL_GROUPS, 
        INTERVAL_GROUP, 
        INTERVAL, 
        INTERVAL_LIMIT, 
        INTERVAL_LIMITS, 
        INTERVAL_RULES, 
        INTERVAL_RELATION, 
        SUGG_ACTION_GROUP, 
        SUGG_ACTION_GROUPS,
        SUGG_ACTION, 
        SUGG_ACTIONS,
        TYPES_AND_OPERATIONS
    };
    
    public enum ATTRIBUTES{
        NAME,
        IDENT,
        DISPLAY_NAME,
        CRITERION_TYPE, 
        CRITERION_INDEX,
        IS_CROSS_CASE,
        IS_DEPENDED,
        DEPEND_INDEX,
        DEPEND_GROUP_INDEX,
        DEPEND_CRIT_CHECKDATE_INDEX,
        IS_CUMMULATIVE,
        CUMMLATE_BASE_INDEX,
        CUMMULATE_WHAT_INDEX,
        CUMMULATE_COUNT_INDEX,
        CUMMULATE_DATE_INDEX,
        VALUE,
        DESCRIPTION,
        CPNAME,
        USAGE, 
        HAS_INTERVAL,
        OPERATION_GROUP, 
        NESTED, 
        SINGLE, 
        INTERVAL_LIMIT, 
        INTERVAL_GROUP,
        DOUBLE_FORMAT,
        INTEGER_FORMAT
    };
    
    public enum USAGE{
	CRIT_RULE_ONLY,
	CRIT_SUGG_ONLY,
	CRIT_RULE_AND_SUGG,
	CRIT_RULE_AND_SUGG_AFTER_FEE_VALIDATION,
	CRIT_RULE_METHOD,
	CRIT_RULE_ASSISTANT_TO_METHOD
    }
    
    public static final Map<String, Integer> DATATYPES = new HashMap()
   {
       {
           put( "DATATYPE_UNFORMATTED", DATATYPE_UNFORMATTED );
           put( "DATATYPE_STRING", DATATYPE_STRING);
           put( "DATATYPE_INTEGER", DATATYPE_INTEGER); 
           put( "DATATYPE_DOUBLE", DATATYPE_DOUBLE);
           put( "DATATYPE_DATE", DATATYPE_DATE);
           put( "DATATYPE_ARRAY_STRING", DATATYPE_ARRAY_STRING);
           put( "DATATYPE_ARRAY_INTEGER", DATATYPE_ARRAY_INTEGER);
           put( "DATATYPE_ARRAY_DOUBLE", DATATYPE_ARRAY_DOUBLE);
           put( "DATATYPE_ARRAY_DATE", DATATYPE_ARRAY_DATE);
           put( "DATATYPE_DAY_TIME", DATATYPE_DAY_TIME);
           put( "DATATYPE_ARRAY_DAY_TIME", DATATYPE_ARRAY_DAY_TIME);
           put( "DATATYPE_DATE_WITH_TIME_SHOW", DATATYPE_DATE_WITH_TIME_SHOW);
           put( "DATATYPE_ALL_OPERATIONS_NESTED", DATATYPE_ALL_OPERATIONS_NESTED);
       }
   };

   
    /**
     * gets the defined operation list to criterion data type
     * @param type
     * @return in DatCaseRuleConstants defined Array
     */
    public static DatRulesOperator[] getOperations2DataType(int type)
    {

        switch(type) {
            case DATATYPE_ARRAY_STRING:
                return DatCaseRuleConstants.m_opList_tables;
            case DATATYPE_STRING:
                return DatCaseRuleConstants.m_opList_equal;
            case DATATYPE_DATE:;
            case DATATYPE_DAY_TIME:;
            case DATATYPE_ARRAY_DATE:;
            case DATATYPE_ARRAY_DAY_TIME:;
                return DatCaseRuleConstants.m_opList_date;
            case DATATYPE_INTEGER:;
            case DATATYPE_DOUBLE:;
                return DatCaseRuleConstants.m_opList_numeric_only;
            case DATATYPE_ARRAY_INTEGER:;
            case DATATYPE_ARRAY_DOUBLE:
                return DatCaseRuleConstants.m_opList_numeric_table; 
            default: 
                return DatCaseRuleConstants.m_opList_no_operation; 
        }
        
    }
    
    /**
     * gets the defined operations to criterion type
     * @param type
     * @return 
     */
    public static String getOperations2type(int type){
        switch(type) {
            case CRGRuleGrouperStatics.DATATYPE_ARRAY_STRING:
               return "opList_tables";
            case CRGRuleGrouperStatics.DATATYPE_STRING:
                return "opList_equal";
            case CRGRuleGrouperStatics.DATATYPE_DATE:;
            case CRGRuleGrouperStatics.DATATYPE_DAY_TIME:;
            case CRGRuleGrouperStatics.DATATYPE_ARRAY_DATE:;
            case CRGRuleGrouperStatics.DATATYPE_ARRAY_DAY_TIME:;

                return "opList_date";
            case CRGRuleGrouperStatics.DATATYPE_INTEGER:;
            case CRGRuleGrouperStatics.DATATYPE_DOUBLE:;
                return "opList_numeric_only";
            case CRGRuleGrouperStatics.DATATYPE_ARRAY_INTEGER:;
            case CRGRuleGrouperStatics.DATATYPE_ARRAY_DOUBLE:
                return "opList_numeric_table";
            case CRGRuleGrouperStatics.DATATYPE_ALL_OPERATIONS_NESTED:
                return "opListNested";
            default: 
                return "opList_no_operation";
        }
        
    }

    /**
     * mapping of type idents to names, for use in xml
     * @return 
     */
    public static boolean getDataTypes(Map critTypes, Map ident2Type, Map type2OperationsGroup) {
        try{
            Field[] fields = CRGRuleGrouperStatics.class.getFields();
            if(fields == null || fields.length == 0){
                return false;
            }
            for(Field field: fields){
                String name = field.getName();
                if(name.startsWith("DATATYPE_")){

                    critTypes.put(name, field.getInt(field));
                    ident2Type.put(field.getInt(field), name);
                    type2OperationsGroup.put(field.getInt(field), Utils.getOperations2type(field.getInt(field)));
                }
            }
        }catch(Exception ex){
            LOG.log(Level.SEVERE, " cannot get datatypes", ex);
            return false;
        }
        return true;
    }
    
    /**
     * gets operations to groups of operations and aoperation names to operation fields
     * @param operationTypes operation type name to array of used operations
     * @param operationNames operation arrays to group name
     * @return 
     */
    public static boolean getOperationGroups(Map operationTypes, Map operationNames){
       try{
            Field[] fields = DatCaseRuleConstants.class.getFields();
            if(fields == null || fields.length == 0){
                return false;
            }
            for(Field field: fields){
                String name = field.getName();
                if(name.startsWith("m_opList_") || name.equals("m_opListNested")){
                    name = name.substring(2);
                    operationTypes.put(name, field.get(field));
                }
                if(name.startsWith("OP_")){
                    operationNames.put( field.get(field), name);
                }
            }
        }catch(Exception ex){
            LOG.log(Level.SEVERE, " cannot get datatypes", ex);
            return false;
        }
        return true;
        
    }
    
    /**
     * mapps interval fields to their names
     * @param intervalTypes
     * @param intervalRules
     * @return 
     */
    public static boolean getIntervalTypes(Map intervalTypes, Map intervalRules){ 
       try{
            Field[] fields = DatCaseRuleConstants.class.getDeclaredFields();
            if(fields == null || fields.length == 0){
                return false;
            }
            Object rules4IntervalsTmp = null;
            Map <Object, String> tmpRules = new HashMap<>();
            for(Field field: fields){
                String name = field.getName();
                if((name.endsWith("Intervals") || name.endsWith("Interval")) && !name.contains("m_ruleForIntervals")){
                    field.setAccessible(true);
                    Object obj = field.get(field);
                    intervalTypes.put(name, obj);
                    tmpRules.put(obj, name);
                }
                if(name.equals("m_ruleForIntervals")){
                    field.setAccessible(true);
                    rules4IntervalsTmp = field.get(field);
                }                
            }
            if(rules4IntervalsTmp != null && rules4IntervalsTmp instanceof Object[][]){
                Object[][] ruleForIntervals = (Object[][])rules4IntervalsTmp;
                for(Object objs[]: ruleForIntervals){
                    DatRulesCriterion crit = (DatRulesCriterion)objs[0];
                    Object obj = objs[1];
                    String groupName = tmpRules.get(objs[1]);
                    if(crit != null && groupName != null){
                        intervalRules.put(crit.getWorkText(), groupName);
                    }
                }
            }
        }catch(Exception ex){
            LOG.log(Level.SEVERE, " cannot get interval types", ex);
            return false;
        }
        return true;
        
    }
    
    /**
     * maps suggestion operation fields to their names
     * @param actionTypes
     * @return 
     */
    public static boolean getSuggActionGroups(Map actionTypes){
        try{
            Field[] fields = DatCaseRuleConstants.class.getFields();
            if(fields == null || fields.length == 0){
                return false;
            }
            for(Field field: fields){
                String name = field.getName();
                if(name.startsWith("m_actList")){

                    actionTypes.put(name, field.get(field));
                }

            }
        }catch(Exception ex){
            LOG.log(Level.SEVERE, " cannot get suggestion action types", ex);
            return false;
        }
        return true;
    }
// TODO: later if required mapping of criterion data types, actions in suggestions
// to operation lists    
//    public static void fillOperationsToCrit(int datatype)
//    {
//
//        try{
//                DatRulesAction action = (DatRulesAction)m_view.m_cbTermin.getSelectedItem();
//                Object[][] critValues = crit.getCritValues();
//                switch(datatype){
//                        case CRGRuleGrouperStatics.DATATYPE_DOUBLE:;
//                        case CRGRuleGrouperStatics.DATATYPE_INTEGER:
//                        if(critValues != null){
//                                m_view.fillOperant(DatCaseRuleMgr.m_opList_equal_sugg);
//                        }
//                        else{
//                                m_view.fillOperant(DatCaseRuleMgr.m_opList_numeric_sugg);
//                        }
//                        break;
//                case CRGRuleGrouperStatics.DATATYPE_ARRAY_STRING:
//                        int whatAction = action != null? action.m_id:2;
//                        if(whatAction == 0) {//löschen
//                                m_view.fillOperant(DatCaseRuleMgr.m_opList_tables_suggs);
//                                ((DsrSuggEditView)m_view).fillConditionOperant(DatCaseRuleMgr.m_opList_tables_suggs);
//                                ((DsrSuggEditView)m_view).enableConditionOperant(true);
//                        }
//                        else if(whatAction == 1) {// hinzufügen
//                                m_view.fillOperant(DatCaseRuleMgr.m_opList_tables_InEqualOnly_suggs);
//                        }
//                        else{ // ändern mit bedingung
//                                m_view.fillOperant(DatCaseRuleMgr.m_opList_equal_sugg);
//                                ((DsrSuggEditView)m_view).fillConditionOperant(DatCaseRuleMgr.m_opList_tables_suggs);
//                                ((DsrSuggEditView)m_view).enableConditionOperant(true);
//                        }
//
//                        break;
//                case CRGRuleGrouperStatics.DATATYPE_STRING: // eigentlich darf nur ändern sein, aber es gibt viel Regeln mit löschen, zufügen der Hauptdiagnose
//                        if(action != null && action.m_id == 0) //löschen
//                                m_view.fillOperant(DatCaseRuleMgr.m_opList_tables_suggs);
//                        else
//                                m_view.fillOperant(DatCaseRuleMgr.m_opList_equal_sugg);
//                        break;
//                case CRGRuleGrouperStatics.DATATYPE_DAY_TIME:
//                case CRGRuleGrouperStatics.DATATYPE_ARRAY_DAY_TIME:
//                case CRGRuleGrouperStatics.DATATYPE_DATE:
//                case CRGRuleGrouperStatics.DATATYPE_ARRAY_DATE:
//                        m_view.fillOperant(DatCaseRuleMgr.m_opList_equal_sugg);
//                        break;
//                case CRGRuleGrouperStatics.DATATYPE_ARRAY_INTEGER:
//                case CRGRuleGrouperStatics.DATATYPE_ARRAY_DOUBLE:
//                        m_view.fillOperant(DatCaseRuleMgr.m_opList_equal_sugg);
//                        break;
//                default:
//                        m_view.fillOperant(DatCaseRuleMgr.m_opList_no_operation);
//                        m_view.m_cbOperant.setEnabled(false);
//                        break;
//                }
//        }catch(Exception e){
//                ExcException.createException(e, "Edit Suggestion");
//        }
//    }
}
