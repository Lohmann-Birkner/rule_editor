package de.lb.ruleprocessor.checkpoint.server.data.caseRules;

import de.lb.ruleprocessor.checkpoint.ruleGrouper.CRGRuleGrouperManagerCP;
import de.lb.ruleprocessor.checkpoint.ruleGrouper.CRGRuleGrouperStatics;
import de.lb.ruleprocessor.checkpoint.server.appServer.AppResourceBundle;
import de.lb.ruleprocessor.checkpoint.server.appServer.AppResources;
import java.util.Vector;

public class DatCaseRuleConstants {

    public static DatRulesOperator[] m_opListNested = null;
    public static DatRulesOperator[] m_opListVal = null;
    public static DatRulesOperator[] m_opListLinks = null;
    public static final String OP_NO_OPERATION = AppResources.getResource(AppResourceBundle.TXT_OP_NO_OPERATION, "keine Operation"); 
    public static final String OP_AND = AppResources.getResource(AppResourceBundle.TXT_OP_AND, "und ( && )");
    public static final String OP_OR = AppResources.getResource(AppResourceBundle.TXT_OP_OR, "oder ( || )");
    public static final String OP_EQUAL = AppResources.getResource(AppResourceBundle.TXT_OP_EQUAL, "gleich ( == )");
    public static final String OP_GT = AppResources.getResource(AppResourceBundle.TXT_OP_GT, "gr\u00f6\u00dfer ( > )");
    public static final String OP_GT_EQUAL = AppResources.getResource(AppResourceBundle.TXT_OP_GT_EQUAL, "gr\\u00f6\\u00dfer gleich ( >= )");
    public static final String OP_LT = AppResources.getResource(AppResourceBundle.TXT_OP_LT, "kleiner ( < )");
    public static final String OP_LT_EQUAL = AppResources.getResource(AppResourceBundle.TXT_OP_LT_EQUAL, "kleiner gleich ( <= )");
    public static final String OP_NOT_EQUAL = AppResources.getResource(AppResourceBundle.TXT_OP_NOT_EQUAL, "ungleich ( != )");
    public static final String OP_PLUS = AppResources.getResource(AppResourceBundle.TXT_OP_PLUS, "plus ( + )");
    public static final String OP_MINUS = AppResources.getResource(AppResourceBundle.TXT_OP_MINUS, "minus ( - )");
    public static final String OP_MULTIPL = AppResources.getResource(AppResourceBundle.TXT_OP_MULTIPL, "multipliziert ( * )");
    public static final String OP_DIVIDE = AppResources.getResource(AppResourceBundle.TXT_OP_DIVIDE, "geteilt durch ( / )");
    public static final String OP_IN = AppResources.getResource(AppResourceBundle.TXT_OP_IN, "enthalten ( IN  )");
    public static final String OP_NOT_IN = AppResources.getResource(AppResourceBundle.TXT_OP_NOT_IN, "keine enthalten ( NOT IN )");
    public static final String OP_NOT_IN_TABLE = AppResources.getResource(AppResourceBundle.TXT_OP_NOT_IN_TABLE, "keine enthalten in Tabelle ( NOT IN @ )");
    public static final String OP_IN_TABLE = AppResources.getResource(AppResourceBundle.TXT_OP_IN_TABLE, "in Tabelle ( @ )");
    public static final String OP_IN_FALL = AppResources.getResource(AppResourceBundle.TXT_OP_IN_FALL, "in Fall ( ~ )");
    public static final String OP_NOT_DOUBLE_IN = AppResources.getResource(AppResourceBundle.TXT_OP_NOT_DOUBLE_IN, "nicht doppelt in ( !! )");
    public static final String OP_NOT_DOUBLE_IN_TABLE = AppResources.getResource(AppResourceBundle.TXT_OP_NOT_DOUBLE_IN_TABLE, "nicht doppelt in Tabelle ( !! @ )");
    public static final String OP_MANY_IN = AppResources.getResource(AppResourceBundle.TXT_OP_MANY_IN, "mehrere in ( ## )");
    public static final String OP_MANY_IN_TABLE = AppResources.getResource(AppResourceBundle.TXT_OP_MANY_IN_TABLE, "mehrere in Tabelle ( #@ )");
    public static final String OP_CONCATENATE = AppResources.getResource(AppResourceBundle.TXT_OP_CONCATENATE, "verketten ( | )");
    public static DatRulesOperator[] m_opList = new DatRulesOperator[]{
        new DatRulesOperator(OP_NO_OPERATION, "", true),
        new DatRulesOperator(OP_AND, "&&", true),
        new DatRulesOperator(OP_OR, "||", true),
        new DatRulesOperator(OP_EQUAL, "==", true),
        new DatRulesOperator(OP_GT, ">", true),
        new DatRulesOperator(OP_GT_EQUAL, ">=", true),
        new DatRulesOperator(OP_LT, "<", true),
        new DatRulesOperator(OP_LT_EQUAL, "<=", true),
        new DatRulesOperator(OP_NOT_EQUAL, "!=", true),
        new DatRulesOperator(OP_PLUS, "+", true),
        new DatRulesOperator(OP_MINUS, "-", true),
        new DatRulesOperator(OP_MULTIPL, "*", true),
        new DatRulesOperator(OP_DIVIDE, "/", true),
        new DatRulesOperator(OP_CONCATENATE, "|", true),
        new DatRulesOperator(OP_IN, "IN", false),
        new DatRulesOperator(OP_NOT_IN, "NOT IN", false),
        new DatRulesOperator(OP_NOT_IN_TABLE, "NOT IN @", false),
        new DatRulesOperator(OP_IN_TABLE, "@", false),
        new DatRulesOperator(OP_NOT_DOUBLE_IN, "!!", false),
        new DatRulesOperator(OP_NOT_DOUBLE_IN_TABLE, "!!@", false),
        new DatRulesOperator(OP_MANY_IN, "##", false),
        new DatRulesOperator(OP_MANY_IN_TABLE, "#@", false)
    };
    public static DatRulesOperator[] m_opList_numeric = new DatRulesOperator[]{
        new DatRulesOperator(OP_NO_OPERATION, "", true),
        new DatRulesOperator(OP_EQUAL, "==", true),
        new DatRulesOperator(OP_GT, ">", true),
        new DatRulesOperator(OP_GT_EQUAL, ">=", true),
        new DatRulesOperator(OP_LT, "<", true),
        new DatRulesOperator(OP_LT_EQUAL, "<=", true),
        new DatRulesOperator(OP_NOT_EQUAL, "!=", true),
        new DatRulesOperator(OP_PLUS, "+", true),
        new DatRulesOperator(OP_MINUS, "-", true),
        new DatRulesOperator(OP_MULTIPL, "*", true),
        new DatRulesOperator(OP_DIVIDE, "/", true),
        //        new DatRulesOperator("^", false),
        new DatRulesOperator(OP_IN, "IN", false),
        new DatRulesOperator(OP_NOT_IN, "NOT IN", false),
        new DatRulesOperator(OP_NOT_IN_TABLE, "NOT IN @", false),
        new DatRulesOperator(OP_IN_TABLE, "@", false),};
    public static DatRulesOperator[] m_opList_numeric_table = new DatRulesOperator[]{
        new DatRulesOperator(OP_NO_OPERATION, "", true),
        new DatRulesOperator(OP_EQUAL, "==", true),
        new DatRulesOperator(OP_GT, ">", true),
        new DatRulesOperator(OP_GT_EQUAL, ">=", true),
        new DatRulesOperator(OP_LT, "<", true),
        new DatRulesOperator(OP_LT_EQUAL, "<=", true),
        new DatRulesOperator(OP_NOT_EQUAL, "!=", true),
        new DatRulesOperator(OP_PLUS, "+", true),
        new DatRulesOperator(OP_MINUS, "-", true),
        new DatRulesOperator(OP_MULTIPL, "*", true),
        new DatRulesOperator(OP_DIVIDE, "/", true),
        new DatRulesOperator(OP_IN, "IN", false),
        new DatRulesOperator(OP_NOT_IN, "NOT IN", false),
        new DatRulesOperator(OP_NOT_IN_TABLE, "NOT IN @", false),
        new DatRulesOperator(OP_IN_TABLE, "@", false),
        new DatRulesOperator(OP_NOT_DOUBLE_IN, "!!", false),
        new DatRulesOperator(OP_NOT_DOUBLE_IN_TABLE, "!!@", false),
        new DatRulesOperator(OP_MANY_IN, "##", false),
        new DatRulesOperator(OP_MANY_IN_TABLE, "#@", false)
    };
    public static DatRulesOperator[] m_opList_numeric_only = new DatRulesOperator[]{
        new DatRulesOperator(OP_NO_OPERATION, "", true),
        new DatRulesOperator(OP_EQUAL, "==", true),
        new DatRulesOperator(OP_GT, ">", true),
        new DatRulesOperator(OP_GT_EQUAL, ">=", true),
        new DatRulesOperator(OP_LT, "<", true),
        new DatRulesOperator(OP_LT_EQUAL, "<=", true),
        new DatRulesOperator(OP_NOT_EQUAL, "!=", true),
        new DatRulesOperator(OP_PLUS, "+", true),
        new DatRulesOperator(OP_MINUS, "-", true),
        new DatRulesOperator(OP_MULTIPL, "*", true),
        new DatRulesOperator(OP_DIVIDE, "/", true),};
    public static DatRulesOperator[] m_opList_equal_only = new DatRulesOperator[]{
        new DatRulesOperator(OP_NO_OPERATION, "", true),
        new DatRulesOperator(OP_EQUAL, "==", true),
        new DatRulesOperator(OP_NOT_EQUAL, "!=", true),
        new DatRulesOperator(OP_IN, "IN", false),
        new DatRulesOperator(OP_NOT_IN, "NOT IN", false),};
    public static DatRulesOperator[] m_opList_equal = new DatRulesOperator[]{
        new DatRulesOperator(OP_NO_OPERATION, "", true),
        new DatRulesOperator(OP_EQUAL, "==", true),
        new DatRulesOperator(OP_NOT_EQUAL, "!=", true),
        new DatRulesOperator(OP_IN, "IN", false),
        new DatRulesOperator(OP_NOT_IN, "NOT IN", false),
        new DatRulesOperator(OP_NOT_IN_TABLE, "NOT IN @", false),
        new DatRulesOperator(OP_IN_TABLE, "@", false),};
    public static DatRulesOperator[] m_opList_equal_multiChoice = new DatRulesOperator[]{
        new DatRulesOperator(OP_NO_OPERATION, "", true),
        new DatRulesOperator(OP_EQUAL, "==", true),
        new DatRulesOperator(OP_NOT_EQUAL, "!=", true),
        new DatRulesOperator(OP_IN, "IN", false),
        new DatRulesOperator(OP_NOT_IN, "NOT IN", false),
        new DatRulesOperator(OP_NOT_IN_TABLE, "NOT IN @", false),
        new DatRulesOperator(OP_IN_TABLE, "@", false),
        new DatRulesOperator(OP_NOT_DOUBLE_IN, "!!", false),
        new DatRulesOperator(OP_NOT_DOUBLE_IN_TABLE, "!!@", false),
        new DatRulesOperator(OP_MANY_IN, "##", false),
        new DatRulesOperator(OP_MANY_IN_TABLE, "#@", false)
    };
    public static DatRulesOperator[] m_opList_compare_only = new DatRulesOperator[]{
        new DatRulesOperator(OP_NO_OPERATION, "", true),
        new DatRulesOperator(OP_EQUAL, "==", true),
        new DatRulesOperator(OP_GT, ">", true),
        new DatRulesOperator(OP_GT_EQUAL, ">=", true),
        new DatRulesOperator(OP_LT, "<", true),
        new DatRulesOperator(OP_LT_EQUAL, "<=", true),
        new DatRulesOperator(OP_NOT_EQUAL, "!=", true),};
    public static DatRulesOperator[] m_opList_compare = new DatRulesOperator[]{
        new DatRulesOperator(OP_NO_OPERATION, "", true),
        new DatRulesOperator(OP_EQUAL, "==", true),
        new DatRulesOperator(OP_GT, ">", true),
        new DatRulesOperator(OP_GT_EQUAL, ">=", true),
        new DatRulesOperator(OP_LT, "<", true),
        new DatRulesOperator(OP_LT_EQUAL, "<=", true),
        new DatRulesOperator(OP_NOT_EQUAL, "!=", true),
        new DatRulesOperator(OP_IN, "IN", false),
        new DatRulesOperator(OP_NOT_IN, "NOT IN", false),
        new DatRulesOperator(OP_NOT_IN_TABLE, "NOT IN @", false),
        new DatRulesOperator(OP_IN_TABLE, "@", false),};
    public static DatRulesOperator[] m_opList_compare_table = new DatRulesOperator[]{
        new DatRulesOperator(OP_NO_OPERATION, "", true),
        new DatRulesOperator(OP_EQUAL, "==", true),
        new DatRulesOperator(OP_GT, ">", true),
        new DatRulesOperator(OP_GT_EQUAL, ">=", true),
        new DatRulesOperator(OP_LT, "<", true),
        new DatRulesOperator(OP_LT_EQUAL, "<=", true),
        new DatRulesOperator(OP_NOT_EQUAL, "!=", true),
        new DatRulesOperator(OP_NOT_IN_TABLE, "NOT IN @", false),
        new DatRulesOperator(OP_IN_TABLE, "@", false),};
    public static DatRulesOperator[] m_opList_tables = new DatRulesOperator[]{
        new DatRulesOperator(OP_NO_OPERATION, "", true),
        new DatRulesOperator(OP_EQUAL, "==", true),
        new DatRulesOperator(OP_NOT_EQUAL, "!=", true),
        new DatRulesOperator(OP_IN, "IN", false),
        new DatRulesOperator(OP_NOT_IN, "NOT IN", false),
        new DatRulesOperator(OP_NOT_IN_TABLE, "NOT IN @", false),
        new DatRulesOperator(OP_IN_TABLE, "@", false),
        new DatRulesOperator(OP_NOT_DOUBLE_IN, "!!", false),
        new DatRulesOperator(OP_NOT_DOUBLE_IN_TABLE, "!!@", false),
        new DatRulesOperator(OP_MANY_IN, "##", false),
        new DatRulesOperator(OP_MANY_IN_TABLE, "#@", false)
    };
    public static DatRulesOperator[] m_opList_date = new DatRulesOperator[]{
        new DatRulesOperator(OP_NO_OPERATION, "", true),
        new DatRulesOperator(OP_EQUAL, "==", true),
        new DatRulesOperator(OP_GT, ">", true),
        new DatRulesOperator(OP_GT_EQUAL, ">=", true),
        new DatRulesOperator(OP_LT, "<", true),
        new DatRulesOperator(OP_LT_EQUAL, "<=", true),
        new DatRulesOperator(OP_NOT_EQUAL, "!=", true), /*		new DatRulesOperator(OP_PLUS, "+", true),
     new DatRulesOperator(OP_MINUS, "-", true),*/};
    public static DatRulesOperator[] m_opList_tables_suggs = new DatRulesOperator[]{
        new DatRulesOperator(OP_NO_OPERATION, "", true),
        new DatRulesOperator(OP_EQUAL, "==", true),
        new DatRulesOperator(OP_NOT_EQUAL, "!=", true),
        new DatRulesOperator(OP_IN, "IN", false),
        new DatRulesOperator(OP_NOT_IN, "NOT IN", false),
        new DatRulesOperator(OP_NOT_IN_TABLE, "NOT IN @", false),
        new DatRulesOperator(OP_IN_TABLE, "@", false),};
    public static DatRulesOperator[] m_opList_tables_InEqualOnly_suggs = new DatRulesOperator[]{
        new DatRulesOperator(OP_NO_OPERATION, "", true),
        new DatRulesOperator(OP_EQUAL, "==", true),
        new DatRulesOperator(OP_IN, "IN", false),
        new DatRulesOperator(OP_IN_TABLE, "@", false),};
    public static DatRulesOperator[] m_opList_tables_auxdiag_suggs = new DatRulesOperator[]{
        new DatRulesOperator(OP_NO_OPERATION, "", true),
        new DatRulesOperator(OP_EQUAL, "==", true),
        new DatRulesOperator(OP_NOT_EQUAL, "!=", true),
        new DatRulesOperator(OP_IN, "IN", false),
        new DatRulesOperator(OP_NOT_IN, "NOT IN", false),
        new DatRulesOperator(OP_NOT_IN_TABLE, "NOT IN @", false),
        new DatRulesOperator(OP_IN_TABLE, "@", false),
        new DatRulesOperator(OP_IN_FALL, "~", false),};
    public static DatRulesOperator[] m_opList_equal_sugg = new DatRulesOperator[]{
        new DatRulesOperator(OP_NO_OPERATION, "", true),
        new DatRulesOperator(OP_EQUAL, "==", true),};
    public static DatRulesOperator[] m_opList_equal_not_equal = new DatRulesOperator[]{
        new DatRulesOperator(OP_NO_OPERATION, "", true),
        new DatRulesOperator(OP_EQUAL, "==", true),
        new DatRulesOperator(OP_NOT_EQUAL, "!=", true),};
    public static DatRulesOperator[] m_opList_numeric_sugg = new DatRulesOperator[]{
        new DatRulesOperator(OP_NO_OPERATION, "", true),
        new DatRulesOperator(OP_EQUAL, "==", true),
        new DatRulesOperator(OP_PLUS, "+", true),
        new DatRulesOperator(OP_MINUS, "-", true), 
        new DatRulesOperator(OP_MULTIPL, "*", true),
 /*    new DatRulesOperator(OP_DIVIDE, "/", true),*/};
    public static DatRulesOperator[] m_opList_no_operation = new DatRulesOperator[]{
        new DatRulesOperator(OP_NO_OPERATION, "", true),};
    private static DatRulesCriterion[] m_sysdateIntervals = new DatRulesCriterion[]{
        new DatRulesCriterion(0, AppResources.getResource(AppResourceBundle.TXT_NO_INTERVAL), DatCaseRuleAttributes.NOTHING, false, Integer.class, false, ""),
        new DatRulesCriterion(0, AppResources.getResource(AppResourceBundle.TXT_INTERVAL_DATE), "Date", false, Integer.class, false, ""),
        new DatRulesCriterion(0, AppResources.getResource(AppResourceBundle.TXT_INTERVAL_DAYS), "days", false, Integer.class, false, ""),
        new DatRulesCriterion(0, AppResources.getResource(AppResourceBundle.TXT_INTERVAL_MONTHS), "months", false, Integer.class, false, ""),
        new DatRulesCriterion(0, AppResources.getResource(AppResourceBundle.TXT_INTERVAL_QUARTERS), "quater", false, Integer.class, false, ""),
        new DatRulesCriterion(0, AppResources.getResource(AppResourceBundle.TXT_INTERVAL_YEARS), "years", false, Integer.class, false, ""),
        new DatRulesCriterion(0, AppResources.getResource(AppResourceBundle.TXT_INTERVAL_ADM_DATE), "admDate", true, Integer.class, false, ""),
        new DatRulesCriterion(0, AppResources.getResource(AppResourceBundle.TXT_INTERVAL_DIS_DATE), "disDate", true, Integer.class, false, ""),};
    private static DatRulesCriterion[] m_dateIntervals = new DatRulesCriterion[]{
        new DatRulesCriterion(0, AppResources.getResource(AppResourceBundle.TXT_NO_INTERVAL), DatCaseRuleAttributes.NOTHING, false, Integer.class, false, ""),
        new DatRulesCriterion(0, AppResources.getResource(AppResourceBundle.TXT_INTERVAL_NOW), "sysDate", true, Integer.class, false, ""),
        new DatRulesCriterion(0, AppResources.getResource(AppResourceBundle.TXT_INTERVAL_DATE), "Date", false, Integer.class, false, ""),
        new DatRulesCriterion(0, AppResources.getResource(AppResourceBundle.TXT_INTERVAL_DAYS), "days", false, Integer.class, false, ""),
        new DatRulesCriterion(0, AppResources.getResource(AppResourceBundle.TXT_INTERVAL_MONTHS), "months", false, Integer.class, false, ""),
        new DatRulesCriterion(0, AppResources.getResource(AppResourceBundle.TXT_INTERVAL_QUARTERS), "quater", false, Integer.class, false, ""),
        new DatRulesCriterion(0, AppResources.getResource(AppResourceBundle.TXT_INTERVAL_YEARS), "years", false, Integer.class, false, ""),
        new DatRulesCriterion(0, AppResources.getResource(AppResourceBundle.TXT_INTERVAL_ADM_DATE), "admDate", true, Integer.class, false, ""),
        new DatRulesCriterion(0, AppResources.getResource(AppResourceBundle.TXT_INTERVAL_DIS_DATE), "disDate", true, Integer.class, false, ""),};
    private static DatRulesCriterion[] m_dateGKIntervals = new DatRulesCriterion[]{
        new DatRulesCriterion(0, AppResources.getResource(AppResourceBundle.TXT_NO_INTERVAL), DatCaseRuleAttributes.NOTHING, false, Integer.class, false, ""),
        new DatRulesCriterion(0, AppResources.getResource(AppResourceBundle.TXT_INTERVAL_NOW), "sysDate", true, Integer.class, false, ""),
        new DatRulesCriterion(0, AppResources.getResource(AppResourceBundle.TXT_INTERVAL_DATE), "Date", false, Integer.class, false, ""),
        new DatRulesCriterion(0, AppResources.getResource(AppResourceBundle.TXT_INTERVAL_DAYS), "days", false, Integer.class, false, ""),
        new DatRulesCriterion(0, AppResources.getResource(AppResourceBundle.TXT_INTERVAL_MONTHS), "months", false, Integer.class, false, ""),
        new DatRulesCriterion(0, AppResources.getResource(AppResourceBundle.TXT_INTERVAL_QUARTERS), "quater", false, Integer.class, false, ""),
        new DatRulesCriterion(0, AppResources.getResource(AppResourceBundle.TXT_INTERVAL_YEARS), "years", false, Integer.class, false, ""),
        new DatRulesCriterion(0, AppResources.getResource(AppResourceBundle.TXT_INTERVAL_TIME_STAMP_DATE)
        + "1 (" + AppResources.getResource(AppResourceBundle.TXT_INTERVAL_ADM_DATE) + ")",
        "timeStamp1", true, Integer.class, false, ""),
        new DatRulesCriterion(0, AppResources.getResource(AppResourceBundle.TXT_INTERVAL_TIME_STAMP_DATE)
        + "2 (" + AppResources.getResource(AppResourceBundle.TXT_INTERVAL_DIS_DATE) + ")",
        "timeStamp2", true, Integer.class, false, ""),};
    private static DatRulesCriterion[] m_sysDateGKIntervals = new DatRulesCriterion[]{
        new DatRulesCriterion(0, AppResources.getResource(AppResourceBundle.TXT_NO_INTERVAL), DatCaseRuleAttributes.NOTHING, false, Integer.class, false, ""),
        new DatRulesCriterion(0, AppResources.getResource(AppResourceBundle.TXT_INTERVAL_DATE), "Date", false, Integer.class, false, ""),
        new DatRulesCriterion(0, AppResources.getResource(AppResourceBundle.TXT_INTERVAL_DAYS), "days", false, Integer.class, false, ""),
        new DatRulesCriterion(0, AppResources.getResource(AppResourceBundle.TXT_INTERVAL_MONTHS), "months", false, Integer.class, false, ""),
        new DatRulesCriterion(0, AppResources.getResource(AppResourceBundle.TXT_INTERVAL_QUARTERS), "quater", false, Integer.class, false, ""),
        new DatRulesCriterion(0, AppResources.getResource(AppResourceBundle.TXT_INTERVAL_YEARS), "years", false, Integer.class, false, ""),
        new DatRulesCriterion(0, AppResources.getResource(AppResourceBundle.TXT_INTERVAL_TIME_STAMP_DATE)
        + "1 (" + AppResources.getResource(AppResourceBundle.TXT_INTERVAL_ADM_DATE) + ")",
        "timeStamp1", true, Integer.class, false, ""),
        new DatRulesCriterion(0, AppResources.getResource(AppResourceBundle.TXT_INTERVAL_TIME_STAMP_DATE)
        + "2 (" + AppResources.getResource(AppResourceBundle.TXT_INTERVAL_DIS_DATE) + ")",
        "timeStamp2", true, Integer.class, false, ""),};
    private static DatRulesCriterion[] m_admDateIntervals = new DatRulesCriterion[]{
        new DatRulesCriterion(0, AppResources.getResource(AppResourceBundle.TXT_NO_INTERVAL), DatCaseRuleAttributes.NOTHING, false, Integer.class, false, ""),
        new DatRulesCriterion(0, AppResources.getResource(AppResourceBundle.TXT_INTERVAL_NOW), "sysDate", true, Integer.class, false, ""),
        new DatRulesCriterion(0, AppResources.getResource(AppResourceBundle.TXT_INTERVAL_DATE), "Date", false, Integer.class, false, ""),
        new DatRulesCriterion(0, AppResources.getResource(AppResourceBundle.TXT_INTERVAL_DAYS), "days", false, Integer.class, false, ""),
        new DatRulesCriterion(0, AppResources.getResource(AppResourceBundle.TXT_INTERVAL_MONTHS), "months", false, Integer.class, false, ""),
        new DatRulesCriterion(0, AppResources.getResource(AppResourceBundle.TXT_INTERVAL_QUARTERS), "quater", false, Integer.class, false, ""),
        new DatRulesCriterion(0, AppResources.getResource(AppResourceBundle.TXT_INTERVAL_YEARS), "years", false, Integer.class, false, ""),
        new DatRulesCriterion(0, AppResources.getResource(AppResourceBundle.TXT_INTERVAL_DIS_DATE), "disDate", true, Integer.class, false, ""),
        new DatRulesCriterion(0, AppResources.getResource(AppResourceBundle.TXT_INTERVAL_OGVD), "ogvd", true, Integer.class, false, ""),};
    private static DatRulesCriterion[] m_admDateGKIntervals = new DatRulesCriterion[]{
        new DatRulesCriterion(0, AppResources.getResource(AppResourceBundle.TXT_NO_INTERVAL), DatCaseRuleAttributes.NOTHING, false, Integer.class, false, ""),
        new DatRulesCriterion(0, AppResources.getResource(AppResourceBundle.TXT_INTERVAL_NOW), "sysDate", true, Integer.class, false, ""),
        new DatRulesCriterion(0, AppResources.getResource(AppResourceBundle.TXT_INTERVAL_DATE), "Date", false, Integer.class, false, ""),
        new DatRulesCriterion(0, AppResources.getResource(AppResourceBundle.TXT_INTERVAL_DAYS), "days", false, Integer.class, false, ""),
        new DatRulesCriterion(0, AppResources.getResource(AppResourceBundle.TXT_INTERVAL_MONTHS), "months", false, Integer.class, false, ""),
        new DatRulesCriterion(0, AppResources.getResource(AppResourceBundle.TXT_INTERVAL_QUARTERS), "quater", false, Integer.class, false, ""),
        new DatRulesCriterion(0, AppResources.getResource(AppResourceBundle.TXT_INTERVAL_YEARS), "years", false, Integer.class, false, ""),};
    private static DatRulesCriterion[] m_disDateIntervals = new DatRulesCriterion[]{
        new DatRulesCriterion(0, AppResources.getResource(AppResourceBundle.TXT_NO_INTERVAL), DatCaseRuleAttributes.NOTHING, false, Integer.class, false, ""),
        new DatRulesCriterion(0, AppResources.getResource(AppResourceBundle.TXT_INTERVAL_NOW), "sysDate", true, Integer.class, false, ""),
        new DatRulesCriterion(0, AppResources.getResource(AppResourceBundle.TXT_INTERVAL_DATE), "Date", false, Integer.class, false, ""),
        new DatRulesCriterion(0, AppResources.getResource(AppResourceBundle.TXT_INTERVAL_DAYS), "days", false, Integer.class, false, ""),
        new DatRulesCriterion(0, AppResources.getResource(AppResourceBundle.TXT_INTERVAL_MONTHS), "months", false, Integer.class, false, ""),
        new DatRulesCriterion(0, AppResources.getResource(AppResourceBundle.TXT_INTERVAL_QUARTERS), "quater", false, Integer.class, false, ""),
        new DatRulesCriterion(0, AppResources.getResource(AppResourceBundle.TXT_INTERVAL_YEARS), "years", false, Integer.class, false, ""),
        new DatRulesCriterion(0, AppResources.getResource(AppResourceBundle.TXT_INTERVAL_ADM_DATE), "admDate", true, Integer.class, false, ""),};
    private static DatRulesCriterion[] m_disDateGKIntervals = new DatRulesCriterion[]{
        new DatRulesCriterion(0, AppResources.getResource(AppResourceBundle.TXT_NO_INTERVAL), DatCaseRuleAttributes.NOTHING, false, Integer.class, false, ""),
        new DatRulesCriterion(0, AppResources.getResource(AppResourceBundle.TXT_INTERVAL_NOW), "sysDate", true, Integer.class, false, ""),
        new DatRulesCriterion(0, AppResources.getResource(AppResourceBundle.TXT_INTERVAL_DATE), "Date", false, Integer.class, false, ""),
        new DatRulesCriterion(0, AppResources.getResource(AppResourceBundle.TXT_INTERVAL_DAYS), "days", false, Integer.class, false, ""),
        new DatRulesCriterion(0, AppResources.getResource(AppResourceBundle.TXT_INTERVAL_MONTHS), "months", false, Integer.class, false, ""),
        new DatRulesCriterion(0, AppResources.getResource(AppResourceBundle.TXT_INTERVAL_QUARTERS), "quater", false, Integer.class, false, ""),
        new DatRulesCriterion(0, AppResources.getResource(AppResourceBundle.TXT_INTERVAL_YEARS), "years", false, Integer.class, false, ""),};
    private static DatRulesCriterion[] m_actCaseIntervals = new DatRulesCriterion[]{
        new DatRulesCriterion(0, AppResources.getResource(AppResourceBundle.TXT_NO_INTERVAL), DatCaseRuleAttributes.NOTHING, false, Integer.class, false, ""),
        new DatRulesCriterion(0, AppResources.getResource(AppResourceBundle.TXT_INTERVAL_CASE), "case", false, Integer.class, false, ""),};
    private static DatRulesCriterion[] m_caseIntervals = new DatRulesCriterion[]{
        new DatRulesCriterion(0, AppResources.getResource(AppResourceBundle.TXT_NO_INTERVAL), DatCaseRuleAttributes.NOTHING, false, Integer.class, false, ""),
        new DatRulesCriterion(0, AppResources.getResource(AppResourceBundle.TXT_INTERVAL_CUR_CASE), "actCase", false, Integer.class, false, ""),};
    public static DatRulesCriterion[] m_disTimeStampIntervals = new DatRulesCriterion[]{
        new DatRulesCriterion(0, AppResources.getResource(AppResourceBundle.TXT_NO_INTERVAL), DatCaseRuleAttributes.NOTHING, false, Integer.class, false, ""),
        new DatRulesCriterion(0, AppResources.getResource(AppResourceBundle.TXT_INTERVAL_NOW), "sysDate", true, Integer.class, false, ""),
        new DatRulesCriterion(0, AppResources.getResource(AppResourceBundle.TXT_INTERVAL_TIME_STAMP_DATE)
        + "1 (" + AppResources.getResource(AppResourceBundle.TXT_INTERVAL_ADM_DATE) + ")",
        "timeStamp1", true, Integer.class, false, ""),
        new DatRulesCriterion(0, AppResources.getResource(AppResourceBundle.TXT_INTERVAL_TIME_STAMP_DATE)
        + "2 (" + AppResources.getResource(AppResourceBundle.TXT_INTERVAL_DIS_DATE) + ")",
        "timeStamp2", true, Integer.class, false, ""),
        new DatRulesCriterion(0, AppResources.getResource(AppResourceBundle.TXT_INTERVAL_DATE), "Date", false, Integer.class, false, ""),
        new DatRulesCriterion(0, AppResources.getResource(AppResourceBundle.TXT_INTERVAL_DAYS), "days", false, Integer.class, false, ""),
        new DatRulesCriterion(0, AppResources.getResource(AppResourceBundle.TXT_INTERVAL_MONTHS), "months", false, Integer.class, false, ""),
        new DatRulesCriterion(0, AppResources.getResource(AppResourceBundle.TXT_INTERVAL_QUARTERS), "quater", false, Integer.class, false, ""),
        new DatRulesCriterion(0, AppResources.getResource(AppResourceBundle.TXT_INTERVAL_YEARS), "years", false, Integer.class, false, ""),
        new DatRulesCriterion(0, AppResources.getResource(AppResourceBundle.TXT_INTERVAL_CUR_CASE), "actCase", true, Integer.class, false, ""),};
    private static DatRulesCriterion[] m_noInterval = new DatRulesCriterion[]{
        new DatRulesCriterion(0, AppResources.getResource(AppResourceBundle.TXT_NO_INTERVAL), DatCaseRuleAttributes.NOTHING, false, Integer.class, false, ""),};
    private static Object[][] m_ruleForIntervals = new Object[][]{
        {new DatRulesCriterion(0, AppResources.getResource(AppResourceBundle.TXT_NO_INTERVAL), DatCaseRuleAttributes.NOTHING, false, Integer.class, false, ""), m_noInterval},
        {new DatRulesCriterion(0, AppResources.getResource(AppResourceBundle.TXT_INTERVAL_NOW), "sysDate", true, Integer.class, false, ""), m_sysdateIntervals},
        {new DatRulesCriterion(0, AppResources.getResource(AppResourceBundle.TXT_INTERVAL_DATE), "Date", false, Integer.class, false, ""), m_dateIntervals},
        {new DatRulesCriterion(CRGRuleGrouperStatics.INDEX_INTERVAL_DATE, AppResources.getResource(AppResourceBundle.TXT_INTERVAL_DATE), "Date", false, Integer.class, false, ""), m_dateGKIntervals},
        {new DatRulesCriterion(0, AppResources.getResource(AppResourceBundle.TXT_INTERVAL_ADM_DATE), "admDate", true, Integer.class, false, ""), m_admDateIntervals},
        {new DatRulesCriterion(0, AppResources.getResource(AppResourceBundle.TXT_INTERVAL_DIS_DATE), "disDate", true, Integer.class, false, ""), m_disDateIntervals},
        {new DatRulesCriterion(CRGRuleGrouperStatics.INDEX_INTERVAL_NO_INTERVAL, AppResources.getResource(AppResourceBundle.TXT_NO_INTERVAL), DatCaseRuleAttributes.NOTHING, false, Integer.class, false, ""), m_noInterval},
        {new DatRulesCriterion(CRGRuleGrouperStatics.INDEX_INTERVAL_TIME_STAMP_DATE_1, AppResources.getResource(AppResourceBundle.TXT_INTERVAL_TIME_STAMP_DATE)
            + "1 (" + AppResources.getResource(AppResourceBundle.TXT_INTERVAL_ADM_DATE) + ")",
            "timeStamp1", true, Integer.class, false, ""), m_admDateGKIntervals},
        {new DatRulesCriterion(CRGRuleGrouperStatics.INDEX_INTERVAL_TIME_STAMP_DATE_2, AppResources.getResource(AppResourceBundle.TXT_INTERVAL_TIME_STAMP_DATE)
            + "2 (" + AppResources.getResource(AppResourceBundle.TXT_INTERVAL_DIS_DATE) + ")",
            "timeStamp2", true, Integer.class, false, ""), m_disDateGKIntervals},
        {new DatRulesCriterion(CRGRuleGrouperStatics.INDEX_INTERVAL_CASE, AppResources.getResource(AppResourceBundle.TXT_INTERVAL_CASE), "case",
            false, Integer.class, false, ""), m_actCaseIntervals},
        //		{new DatRulesCriterion(0, AppResources.getResource(AppResourceBundle.TXT_INTERVAL_CUR_CASE), "actCase", true, Integer.class, false, ""), 0},
        {new DatRulesCriterion(CRGRuleGrouperStatics.INDEX_INTERVAL_DAYS, AppResources.getResource(AppResourceBundle.TXT_INTERVAL_DAYS), "days", false, Integer.class, false, ""), m_dateGKIntervals},
        {new DatRulesCriterion(CRGRuleGrouperStatics.INDEX_INTERVAL_MONTHS, AppResources.getResource(AppResourceBundle.TXT_INTERVAL_MONTHS), "months", false, Integer.class, false, ""), m_dateGKIntervals},
        {new DatRulesCriterion(CRGRuleGrouperStatics.INDEX_INTERVAL_QUARTERS, AppResources.getResource(AppResourceBundle.TXT_INTERVAL_QUARTERS), "quater", false, Integer.class, false, ""), m_dateGKIntervals},
        {new DatRulesCriterion(CRGRuleGrouperStatics.INDEX_INTERVAL_YEARS, AppResources.getResource(AppResourceBundle.TXT_INTERVAL_YEARS), "years", false, Integer.class, false, ""), m_dateGKIntervals},
        {new DatRulesCriterion(CRGRuleGrouperStatics.INDEX_INTERVAL_NOW, AppResources.getResource(AppResourceBundle.TXT_INTERVAL_NOW), "sysDate", true, Integer.class, false, ""), m_sysDateGKIntervals},};
    private static DatRulesCriterion[] m_ruleAllIntervals = new DatRulesCriterion[]{
        new DatRulesCriterion(CRGRuleGrouperStatics.INDEX_INTERVAL_NO_INTERVAL, AppResources.getResource(AppResourceBundle.TXT_NO_INTERVAL), DatCaseRuleAttributes.NOTHING, false, Integer.class, false, ""),
        new DatRulesCriterion(CRGRuleGrouperStatics.INDEX_INTERVAL_NOW, AppResources.getResource(AppResourceBundle.TXT_INTERVAL_NOW), "sysDate", true, Integer.class, false, ""),
        new DatRulesCriterion(CRGRuleGrouperStatics.INDEX_INTERVAL_DATE, AppResources.getResource(AppResourceBundle.TXT_INTERVAL_DATE), "Date", false, Integer.class, false, ""),
        new DatRulesCriterion(CRGRuleGrouperStatics.INDEX_INTERVAL_DAYS, AppResources.getResource(AppResourceBundle.TXT_INTERVAL_DAYS), "days", false, Integer.class, false, ""),
        new DatRulesCriterion(CRGRuleGrouperStatics.INDEX_INTERVAL_MONTHS, AppResources.getResource(AppResourceBundle.TXT_INTERVAL_MONTHS), "months", false, Integer.class, false, ""),
        new DatRulesCriterion(CRGRuleGrouperStatics.INDEX_INTERVAL_QUARTERS, AppResources.getResource(AppResourceBundle.TXT_INTERVAL_QUARTERS), "quater", false, Integer.class, false, ""),
        new DatRulesCriterion(CRGRuleGrouperStatics.INDEX_INTERVAL_YEARS, AppResources.getResource(AppResourceBundle.TXT_INTERVAL_YEARS), "years", false, Integer.class, false, ""),
        new DatRulesCriterion(CRGRuleGrouperStatics.INDEX_INTERVAL_ADM_DATE, AppResources.getResource(AppResourceBundle.TXT_INTERVAL_ADM_DATE), "admDate", true, Integer.class, false, ""),
        new DatRulesCriterion(CRGRuleGrouperStatics.INDEX_INTERVAL_DIS_DATE, AppResources.getResource(AppResourceBundle.TXT_INTERVAL_DIS_DATE), "disDate", true, Integer.class, false, ""),
        new DatRulesCriterion(CRGRuleGrouperStatics.INDEX_INTERVAL_OGVD, AppResources.getResource(AppResourceBundle.TXT_INTERVAL_OGVD), "ogvd", true, Integer.class, false, ""),
        new DatRulesCriterion(CRGRuleGrouperStatics.INDEX_INTERVAL_TIME_STAMP_DATE_1,
        AppResources.getResource(AppResourceBundle.TXT_INTERVAL_TIME_STAMP_DATE)
        + "1 (" + AppResources.getResource(AppResourceBundle.TXT_INTERVAL_ADM_DATE) + ")",
        "timeStamp1", true, Integer.class, false, ""),
        new DatRulesCriterion(CRGRuleGrouperStatics.INDEX_INTERVAL_TIME_STAMP_DATE_2,
        AppResources.getResource(AppResourceBundle.TXT_INTERVAL_TIME_STAMP_DATE)
        + "2 (" + AppResources.getResource(AppResourceBundle.TXT_INTERVAL_DIS_DATE) + ")",
        "timeStamp2", true, Integer.class, false, ""),
        new DatRulesCriterion(CRGRuleGrouperStatics.INDEX_INTERVAL_CURRENT_CASE, AppResources.getResource(AppResourceBundle.TXT_INTERVAL_CUR_CASE), "actCase", true, Integer.class, false, ""),
        new DatRulesCriterion(CRGRuleGrouperStatics.INDEX_INTERVAL_CASE, AppResources.getResource(AppResourceBundle.TXT_INTERVAL_CASE), "case", false, Integer.class, false, ""),};
    private static Object[][] actualCase = {{new DatRulesCriterion(0, AppResources.getResource(AppResourceBundle.TXT_INTERVAL_CUR_CASE), "actCase", true, Integer.class, false, ""), m_actCaseIntervals},};

// Methods
    private static DatRulesMethod[] m_methodList = new DatRulesMethod[]{
        new DatRulesMethod(AppResources.getResource(AppResourceBundle.TXT_METHOD_MAX_INTERVAL_PROPERTY_DIS, "MaxIntervallEigenschaft"),
            AppResources.getResource(AppResourceBundle.TXT_METHOD_MAX_INTERVAL_PROPERTY, "MaxIntervalProperty"), new Class[]{String.class, int.class, int.class}),
        new DatRulesMethod(AppResources.getResource(AppResourceBundle.TXT_METHOD_MAX_INTERVAL_TABLE_DIS,
            "MaxIntervallTabelle"), AppResources.getResource(AppResourceBundle.TXT_METHOD_MAX_INTERVAL_TABLE, "MaxIntervalTable"),  new Class[]{String.class, int.class, int.class}),
    };
// Eigenschaften zu dem   MaxIntervalProperty Method
    private static DatProperty[] m_properties = new DatProperty[]{
        new DatProperty(AppResources.getResource(AppResourceBundle.TXT_NO_PROPERTY_DIS, "keine Eigenschaft"), 
            ""),
        new DatProperty(AppResources.getResource(AppResourceBundle.TXT_PROPERTY_VALIDITY_GROUP_DIS, "Berufsgruppe"), 
            AppResources.getResource(AppResourceBundle.TXT_PROPERTY_VALIDITY_GROUP, "Validity-Group")), 
       new DatProperty(AppResources.getResource(AppResourceBundle.TXT_PROPERTY_TREATMENT_TYPE_DIS, "Behandlungsart"), 
            AppResources.getResource(AppResourceBundle.TXT_PROPERTY_TREATMENT_TYPE, "Treatment-Type")), 
       new DatProperty(AppResources.getResource(AppResourceBundle.TXT_PROPERTY_VALIDITY_SET_DIS, "Mengenbezeichnung"), 
            AppResources.getResource(AppResourceBundle.TXT_PROPERTY_VALIDITY_SET, "Validity-Set")), 
        
    };
    
    public static Object[] getFromIntervalCriterion() {
        int size = m_ruleForIntervals.length;
//        if (CMDefaults.defaultsMgr().isCrossCaseModelAllowed()) {
//            size++;
//        }
        Vector v = new Vector();
        int i = 0;
        for (i = 0; i < m_ruleForIntervals.length; i++) {
            if (((DatRulesCriterion) m_ruleForIntervals[i][0]).m_operationType != 0) {
                v.addElement(m_ruleForIntervals[i][0]);
            }
        }
//        if (CMDefaults.defaultsMgr().isCrossCaseModelAllowed()) {
//            v.addElement(actualCase[0][0]);
//        }

        return v.toArray();
    }

    public DatCaseRuleConstants() {
    }

    public static int getTypKey(String rulesType) {
        switch (rulesType) {
            case DatCaseRuleAttributes.RULES_TYPE_ERROR:
                return DatCaseRuleAttributes.STATE_ERROR;
            case DatCaseRuleAttributes.RULES_TYPE_WARNING:
                return DatCaseRuleAttributes.STATE_WARNING;
            case DatCaseRuleAttributes.RULES_TYPE_SUGGESTION:
                return DatCaseRuleAttributes.STATE_SUGG;
            default:
                return DatCaseRuleAttributes.STATE_NO;
        }
    }

    public static DatRulesAction getActionByWorkID(String actID) {
        int aid = 0;
        try {
            aid = Integer.parseInt(actID);
        } catch (Exception ex) {
        }
        for (int i = 0; i < m_actList.length; i++) {
            DatRulesAction crit = m_actList[i];
            if (crit.m_id == aid) {
                return crit;
            }
        }
        return null;
    }
    public static final DatRulesAction[] m_actList0 = new DatRulesAction[]{
        new DatRulesAction(DatCaseRuleAttributes.SUGG_NULL,
                AppResources.getResource(AppResourceBundle.TXT_LIST_NOTHING),
                AppResources.getResource(AppResourceBundle.TXT_LIST_DO_NOTHING))};
    public static final DatRulesAction[] m_actListAddOnly = new DatRulesAction[]{
        new DatRulesAction(DatCaseRuleAttributes.SUGG_ADD, "add", 
                AppResources.getResource(AppResourceBundle.TXT_LIST_ADD))
    };
    public static final DatRulesAction[] m_actListChangeOnly = new DatRulesAction[]{
        new DatRulesAction(DatCaseRuleAttributes.SUGG_CHANGE, "change", AppResources.getResource(AppResourceBundle.TXT_LIST_CHANGE))
    };
    public static final DatRulesAction[] m_actList = new DatRulesAction[]{
        new DatRulesAction(DatCaseRuleAttributes.SUGG_DELETE, "delete", AppResources.getResource(AppResourceBundle.TXT_LIST_DELETE)),
        new DatRulesAction(DatCaseRuleAttributes.SUGG_ADD, "add", AppResources.getResource(AppResourceBundle.TXT_LIST_ADD)),
        new DatRulesAction(DatCaseRuleAttributes.SUGG_CHANGE, "change", AppResources.getResource(AppResourceBundle.TXT_LIST_CHANGE))
    };

    public static DatRulesOperator getOperatorByWorkText(String text) {
        for (int i = 0; i < getOperator().length; i++) {
            DatRulesOperator crit = m_opList[i];
            if (crit.getWorkText().equals(text)) {
                return crit;
            }
        }
        return null;
    }

    public static DatRulesMethod getMethodByWorkText(String text) {
        for ( DatRulesMethod crit:m_methodList) {
           
            if (crit.getWorkText().equals(text)) {
                return crit;
            }
        }
        return null;
    }

    public static DatRulesMethod getMethodByDisplayText(String text) {
        for ( DatRulesMethod crit:m_methodList) {
           
            if (crit.getDisplayText().equals(text)) {
                return crit;
            }
        }
        return null;
    }

    public static DatRulesOperator[] getOperator() {
        return m_opList;
    }

    public static DatRulesOperator[] getNestedOperator() {
        /* 3.9.5 2015-09-01 DNi: #FINDBUGS - Wegen der Initialisierung des statischen Feldes m_opListNested sollte die Methode synchronized sein. */
        if (m_opListNested == null) {
            Vector lst = new Vector();
            for (int i = 0; i < m_opList.length; i++) {
                if (m_opList[i].m_isNested) {
                    lst.add(m_opList[i]);
                }
            }
            m_opListNested = new DatRulesOperator[lst.size()];
            for (int i = 0; i < lst.size(); i++) {
                m_opListNested[i] = (DatRulesOperator) lst.get(i);
            }

        }
        return m_opListNested;
    }

    public static Object getFromIntervalCriterionByWorkText(String workText) {
        for (int i = 0; i < m_ruleForIntervals.length; i++) {
            try {
                if (((DatRulesCriterion) m_ruleForIntervals[i][0]).getWorkText().equalsIgnoreCase(workText)) {
                    return m_ruleForIntervals[i][0];
                }
                ;
            } catch (Exception e) {
                continue;
            }
        }
//        if (CMDefaults.defaultsMgr().isCrossCaseModelAllowed()) {
//            if (((DatRulesCriterion) actualCase[0][0]).getWorkText().equalsIgnoreCase(workText)) {
//                return actualCase[0][0];
//            }
//        }
        return m_ruleForIntervals[0][0];
    }

    public static Object getIntervalCriterionByWorkText(String workText) {
        for (int i = 0; i < m_ruleAllIntervals.length; i++) {
            try {
                if (((DatRulesCriterion) m_ruleAllIntervals[i]).getWorkText().equalsIgnoreCase(workText)) {
                    return m_ruleAllIntervals[i];
                }
                ;
            } catch (Exception e) {
                continue;
            }
        }
        return m_ruleAllIntervals[0];
    }

    public static Object getToIntervalCriterionByWorkText(String fromWorkText, String toWorkText) {
        Object[] toArray = getToIntervalCriterions(fromWorkText);
        if (toArray == null) {
            return null;
        }
        for (int i = 0; i < toArray.length; i++) {
            try {
                if (((DatRulesCriterion) toArray[i]).getWorkText().equalsIgnoreCase(toWorkText)) {
                    return toArray[i];
                }
                ;
            } catch (Exception e) {
                continue;
            }
        }

        return toArray[0];
    }

    public static Object[] getToIntervalCriterions(String workText) {
        for (int i = 0; i < m_ruleForIntervals.length; i++) {
            try {
                if (((DatRulesCriterion) m_ruleForIntervals[i][0]).getWorkText().equalsIgnoreCase(workText)) {
                    if (((DatRulesCriterion) m_ruleForIntervals[i][0]).m_operationType != 0) {
                        return (Object[]) m_ruleForIntervals[i][1];
                    }
                    continue;
                }
                ;
            } catch (Exception e) {
                continue;
            }
        }
//        if (CMDefaults.defaultsMgr().isCrossCaseModelAllowed()) {
//            if (((DatRulesCriterion) actualCase[0][0]).getWorkText().equalsIgnoreCase(workText)) {
//                return ((Object[]) actualCase[0][1]);
//            }
//        }
        return null;

    }
    
    public static DatProperty[] getMethodProperties()
    {
        return m_properties;
    }
}
