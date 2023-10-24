/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.checkpoint.ruleGrouper;

import static de.checkpoint.ruleGrouper.CRGRuleGrouperStatics.DATATYPE_ARRAY_DATE;
import static de.checkpoint.ruleGrouper.CRGRuleGrouperStatics.DATATYPE_ARRAY_DAY_TIME;
import static de.checkpoint.ruleGrouper.CRGRuleGrouperStatics.DATATYPE_ARRAY_DOUBLE;
import static de.checkpoint.ruleGrouper.CRGRuleGrouperStatics.DATATYPE_ARRAY_INTEGER;
import static de.checkpoint.ruleGrouper.CRGRuleGrouperStatics.DATATYPE_ARRAY_STRING;
import static de.checkpoint.ruleGrouper.CRGRuleGrouperStatics.DATATYPE_DATE;
import static de.checkpoint.ruleGrouper.CRGRuleGrouperStatics.DATATYPE_DAY_TIME;
import static de.checkpoint.ruleGrouper.CRGRuleGrouperStatics.DATATYPE_DOUBLE;
import static de.checkpoint.ruleGrouper.CRGRuleGrouperStatics.DATATYPE_INTEGER;
import static de.checkpoint.ruleGrouper.CRGRuleGrouperStatics.DATATYPE_STRING;
import static de.checkpoint.ruleGrouper.CRGRuleGrouperStatics.OP_AND;
import static de.checkpoint.ruleGrouper.CRGRuleGrouperStatics.OP_CONCATENATE;
import static de.checkpoint.ruleGrouper.CRGRuleGrouperStatics.OP_DIVIDE;
import static de.checkpoint.ruleGrouper.CRGRuleGrouperStatics.OP_EQUAL;
import static de.checkpoint.ruleGrouper.CRGRuleGrouperStatics.OP_GT;
import static de.checkpoint.ruleGrouper.CRGRuleGrouperStatics.OP_GT_EQUAL;
import static de.checkpoint.ruleGrouper.CRGRuleGrouperStatics.OP_IN;
import static de.checkpoint.ruleGrouper.CRGRuleGrouperStatics.OP_IN_TABLE;
import static de.checkpoint.ruleGrouper.CRGRuleGrouperStatics.OP_LT;
import static de.checkpoint.ruleGrouper.CRGRuleGrouperStatics.OP_LT_EQUAL;
import static de.checkpoint.ruleGrouper.CRGRuleGrouperStatics.OP_MANY_IN;
import static de.checkpoint.ruleGrouper.CRGRuleGrouperStatics.OP_MANY_IN_TABLE;
import static de.checkpoint.ruleGrouper.CRGRuleGrouperStatics.OP_MINUS;
import static de.checkpoint.ruleGrouper.CRGRuleGrouperStatics.OP_MULTIPL;
import static de.checkpoint.ruleGrouper.CRGRuleGrouperStatics.OP_NOT_DOUBLE_IN;
import static de.checkpoint.ruleGrouper.CRGRuleGrouperStatics.OP_NOT_DOUBLE_IN_TABLE;
import static de.checkpoint.ruleGrouper.CRGRuleGrouperStatics.OP_NOT_EQUAL;
import static de.checkpoint.ruleGrouper.CRGRuleGrouperStatics.OP_NOT_IN;
import static de.checkpoint.ruleGrouper.CRGRuleGrouperStatics.OP_NOT_IN_TABLE;
import static de.checkpoint.ruleGrouper.CRGRuleGrouperStatics.OP_NO_OPERATION;
import static de.checkpoint.ruleGrouper.CRGRuleGrouperStatics.OP_OR;
import static de.checkpoint.ruleGrouper.CRGRuleGrouperStatics.OP_PLUS;
import static de.checkpoint.ruleGrouper.CRGRuleGrouperStatics.OP_RANGE_AND;
import static de.checkpoint.ruleGrouper.CRGRuleGrouperStatics.OP_RANGE_COMPARE;
import static de.checkpoint.ruleGrouper.CRGRuleGrouperStatics.OP_RANGE_DASH;
import static de.checkpoint.ruleGrouper.CRGRuleGrouperStatics.OP_RANGE_DOT;
import static de.checkpoint.ruleGrouper.CRGRuleGrouperStatics.OP_RANGE_OR;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author gerschmann
 */
public class CRGRuleGrouperManager {
    private static CRGRuleGrouperManager ruleManager;
    private static Integer[] CURRENCY_CRITS_DOUBLE;
    private static Integer[] CURRENCY_CRITS_DOUBLE_ARRAY;
    private static Integer[] INTERVAL_CRIT_GROUPS;
    public static CriterionEntry[] m_criterion;
    private static CriterionDepend[] m_criterionDepend;
    protected static  GroupCriterionEntry[] m_superGroups;
    protected static  GroupCriterionEntry[] m_groups;
    protected static Map<String,CriterionEntry> m_name2criterion; 
    
    private static Map<String, Integer> m_type2maxIndex = new HashMap<>();
    private static Map<Integer, Map<String, CriterionEntry>> m_type2criterienWithName = new HashMap<>();
    private static Map<Integer, Map<Integer, CriterionEntry>> m_type2criterienWithIndex = new HashMap<>();
    private static  Map<String, CriterionEntry> m_name2crit = new HashMap<>();
    public static CRGRuleGrouperManager instance(){
        if(ruleManager == null){
            ruleManager = new CRGRuleGrouperManager();
        }
        return ruleManager;
    }
    
    public static boolean isOperatorArray(int opType) {
        switch (opType) {
            case OP_IN:
            case OP_NOT_IN:
            case OP_NOT_IN_TABLE:
            case OP_IN_TABLE:
            case OP_NOT_DOUBLE_IN:
            case OP_NOT_DOUBLE_IN_TABLE:
            case OP_MANY_IN:
            case OP_MANY_IN_TABLE:
                return true;
            default:
                return false;
        }
    }

    public static boolean isOperatorMany(int opType) {
        switch (opType) {
            case OP_MANY_IN:
            case OP_MANY_IN_TABLE:
                return true;
            default:
                return false;
        }
    }

    public static boolean isOperatorTable(int opType) {
        switch (opType) {
            case OP_NOT_IN_TABLE:
            case OP_IN_TABLE:
            case OP_NOT_DOUBLE_IN_TABLE:
            case OP_MANY_IN_TABLE:
                return true;
            default:
                return false;
        }
    }

    public static synchronized boolean isOperatorCompute(int opType) {
        switch (opType) {
            case OP_MINUS:
            case OP_PLUS:
            case OP_MULTIPL:
            case OP_CONCATENATE:
            case OP_DIVIDE: {
                return true;
            }
            default:
                return false;
        }
    }

    public static synchronized boolean isOperatorCompare(int opType) {
        switch (opType) {
            case OP_EQUAL:
            case OP_GT:
            case OP_GT_EQUAL:
            case OP_LT:
            case OP_LT_EQUAL:
            case OP_NOT_EQUAL: {
                return true;
            }
            default:
                return false;
        }
    }

    public static int getOperationRange(String op) {
        int opInt = getOperatorType(op);
        switch (opInt) {
            case OP_AND:
                return OP_RANGE_AND;
            case OP_OR:
                return OP_RANGE_OR;
            case OP_EQUAL:
                ;
            case OP_GT:
                ;
            case OP_GT_EQUAL:
                ;
            case OP_LT:
                ;
            case OP_LT_EQUAL:
                ;
            case OP_NOT_EQUAL:
                return OP_RANGE_COMPARE;
            case OP_CONCATENATE:
                ;
            case OP_PLUS:
                ;
            case OP_MINUS:
                return OP_RANGE_DASH;
            case OP_MULTIPL:
                ;
            case OP_DIVIDE:
                return OP_RANGE_DOT;
            default:
                return 0;

        }
    }

    public static synchronized int getOperatorType(String op) {
        if (op.equals("&&")) {
            return OP_AND;
        } else if (op.equals("||")) {
            return OP_OR;
        } else if (op.equals("==")) {
            return OP_EQUAL;
        } else if (op.equals(">")) {
            return OP_GT;
        } else if (op.equals(">=")) {
            return OP_GT_EQUAL;
        } else if (op.equals("<")) {
            return OP_LT;
        } else if (op.equals("<=")) {
            return OP_LT_EQUAL;
        } else if (op.equals("!=")) {
            return OP_NOT_EQUAL;
        } else if (op.equals("+")) {
            return OP_PLUS;
        } else if (op.equals("-")) {
            return OP_MINUS;
        } else if (op.equals("*")) {
            return OP_MULTIPL;
        } else if (op.equals("/")) {
            return OP_DIVIDE;
        } else if (op.equals("|")) {
            return OP_CONCATENATE;
        } else if (op.equals("IN")) {
            return OP_IN;
        } else if (op.equals("NOT IN")) {
            return OP_NOT_IN;
        } else if (op.equals("NOT IN @")) {
            return OP_NOT_IN_TABLE;
        } else if (op.equals("@")) {
            return OP_IN_TABLE;
        } else if (op.equals("!!")) {
            return OP_NOT_DOUBLE_IN;
        } else if (op.equals("!!@")) {
            return OP_NOT_DOUBLE_IN_TABLE;
        } else if (op.equals("##")) {
            return OP_MANY_IN;
        } else if (op.equals("#@")) {
            return OP_MANY_IN_TABLE;
        } else if (op.equals("")) {
            return OP_NO_OPERATION;
        } else {
            return -1;
        }

    }
    
    public static int getCritIndex2type(String pCriterionType){
        Integer index = m_type2maxIndex.get(pCriterionType);
        if(index == null){
            m_type2maxIndex.put(pCriterionType, 0);
            return 0;
        }else{
            m_type2maxIndex.put(pCriterionType, ++index);
            return index;
        }
        
    }

    public static synchronized String getOperatorText(int op) {
        switch (op) {
            case OP_AND:
                return "&&";
            case OP_OR:
                return "||";
            case OP_EQUAL:
                return "==";
            case OP_GT:
                return ">";
            case OP_GT_EQUAL:
                return ">=";
            case OP_LT:
                return "<";
            case OP_LT_EQUAL:
                return "<=";
            case OP_NOT_EQUAL:
                return "!=";
            case OP_PLUS:
                return "+";
            case OP_MINUS:
                return "-";
            case OP_MULTIPL:
                return "*";
            case OP_DIVIDE:
                return "/";
            case OP_CONCATENATE:
                return "|";
            case OP_IN:
                return "IN";
            case OP_NOT_IN:
                return "NOT IN";
            case OP_NOT_IN_TABLE:
                return "NOT IN @";
            case OP_IN_TABLE:
                return "@";
            case OP_NOT_DOUBLE_IN:
                return "!!";
            case OP_NOT_DOUBLE_IN_TABLE:
                return "!!@";
            case OP_MANY_IN:
                return "##";
            case OP_MANY_IN_TABLE:
                return "#@";
            case OP_NO_OPERATION:
                return "";
            default:
                return "";

        }
    }
    public static boolean isDashOperation(int opType) {
        return (opType == OP_PLUS || opType == OP_MINUS);
    }

    public static boolean isDotOperation(int opType) {
        return (opType == OP_MULTIPL || opType == OP_DIVIDE);
    }

    public static synchronized CriterionDepend[] getCriterionDepend() {
        return m_criterionDepend;
    }
    

    protected CRGRuleGrouperManager() {
        initRuleProcessor();
    }
    
    private void initRuleProcessor() {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    
    public static String getFormatedCodeText(String val, int datatype) {
        switch (datatype) {
            case DATATYPE_STRING:
                ;
            case DATATYPE_ARRAY_STRING:
                String chk = "";
                val = val.trim();
                for (int i = 0; i < val.length(); i++) {
                    if (i > 0 && i < val.length() - 2
                            && val.charAt(i) == '\''
                            && (val.charAt(i + 1) != '\''
                            || val.charAt(i - 1) != '\'')) {
                        chk = chk + val.charAt(i) + "\'";
                    } else {
                        if (val.charAt(i) == '*') {
                            chk = chk + '%';
                        } else {
                            chk = chk + val.charAt(i);
                        }
                    }
                }
                val = chk.toUpperCase();

                if (val.length() > 0) {
                    if (val.charAt(0) != '\'') {
                        val = "'" + val;
                    }
                    if (val.charAt(val.length() - 1) != '\'') {
                        val = val + "'";
                    }
                }
                break;
            case DATATYPE_INTEGER:
                ;
            case DATATYPE_ARRAY_INTEGER:
                break;
            case DATATYPE_DOUBLE:
                ;
            case DATATYPE_ARRAY_DOUBLE:
                val = val.replaceAll(",", ".");
                break;
            case DATATYPE_DATE:
                ;
            case DATATYPE_ARRAY_DATE:
                ;
                break;
        }
        return val;
    }
    
        public static Class getSimpleClassToCritType(int type) {
        switch (type) {
            case DATATYPE_STRING:
                ;
            case DATATYPE_ARRAY_STRING:
                return String.class;
            case DATATYPE_INTEGER:
                ;
            case DATATYPE_ARRAY_INTEGER:
                return Integer.class;
            case DATATYPE_DOUBLE:
                ;
            case DATATYPE_ARRAY_DOUBLE:
                return Double.class;
            case DATATYPE_DATE:
                ;
            case DATATYPE_ARRAY_DATE:
                ;
            case DATATYPE_DAY_TIME:
                ;
            case DATATYPE_ARRAY_DAY_TIME:
                return Date.class;
        }
        return Object.class;
    }
    

    public static String getDispCriterionTextByWorkText(String critStr) {
        CriterionEntry crit = getCriterionByText(critStr);
        return crit.getDisplayText();
    }

    public static CriterionEntry getCriterionByWorkText(String critStr) {
        return getCriterionByText(critStr);
    }

       public static synchronized CriterionEntry getCriterionByText(String name) {
        return m_name2crit.get(name);
    }


    public static boolean isCurrency(int id, int type) {

        try {
            Integer[] array = CURRENCY_CRITS_DOUBLE;
            if (type == DATATYPE_ARRAY_DOUBLE) {
                array = CURRENCY_CRITS_DOUBLE_ARRAY;
            }
            List list = Arrays.asList(array);
            return list.contains(new Integer(id));
        } catch (Exception e) {
            return false;
        }

    }

    public static boolean isIntervalCrit(int group) {
        try {

            return isAnyIntervalCrit(group);
        } catch (Exception e) {
            return false;
        }
    }
    
    public static boolean isAnyIntervalCrit(int group) {
        List list = Arrays.asList(INTERVAL_CRIT_GROUPS);
        return list.contains(group) ;

    }
    
    public static String getCriterionTextByIndex(int pDataType, int pIndex){
        Map<Integer,CriterionEntry> map =m_type2criterienWithIndex.get(pDataType);
        if(map != null){
            CriterionEntry crt = map.get(pIndex);
            return crt == null?"":crt.getWorkText();
        }
        return "";
    }
    
    public static String  checkWithFormat(String pCritWorkText, String pCritValue){
        return "";
    }

    public void setCriterionTree(List<GroupCriterionEntry> superGroups, 
            List<GroupCriterionEntry> groups, 
            Map<String, CriterionEntry> name2crit,
            Map<Integer, Map<String, CriterionEntry>> type2criterienWithName,
            Map<Integer, Map<Integer, CriterionEntry>> type2criterienWithIndex) {
        if(superGroups != null){
            m_superGroups = new GroupCriterionEntry[superGroups.size()];
            superGroups.toArray(m_superGroups);
        }else{
            m_superGroups = new GroupCriterionEntry[0];
        }
         if(groups != null){
            m_groups = new GroupCriterionEntry[groups.size()];
            groups.toArray(m_groups);
        }else{
            m_groups = new GroupCriterionEntry[0];
        }
         if(name2crit != null){
             m_name2criterion = name2crit;
             Collection< CriterionEntry> crits = name2crit.values();
             m_criterion = new CriterionEntry[crits.size()];
             crits.toArray(m_criterion);
            for (int i = 0; i < m_criterion.length; i++) {
                CriterionEntry entry = m_criterion[i];
                m_name2crit.put(entry.getWorkText(), entry);
            }

         }
         m_type2criterienWithName = type2criterienWithName;
         m_type2criterienWithIndex = type2criterienWithIndex;
   }

    public static CriterionEntry getCriterionByName(String pName){
        return m_name2crit.get(pName);
    }

}
