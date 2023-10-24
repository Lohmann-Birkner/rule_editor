/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.checkpoint.server.data.caseRules;

/**
 *
 * @author gerschmann
 */
public class DatCaseRuleAttributes {
    
    public static final int SUGG_NULL = -1;
    public static final int SUGG_DELETE = 0;
    public static final int SUGG_ADD = 1;
    public static final int SUGG_CHANGE = 2;
    public static final int STATE_NO = 0;
    public static final int STATE_WARNING = 1;
    public static final int STATE_ERROR = 2;
    public static final int STATE_SUGG = 3;
    public static final int SUGG_OP_SAME = 0;
    public static final int SUGG_OP_PLUS = 1;
    public static final int SUGG_OP_MINUS = 2;
    public static String ELEMENT_CASE_RULES = "caseRules";
    public static String ELEMENT_RULES = "rule";
    public static String ELEMENT_RULES_ELEMENT = "rules_element";
    public static String ELEMENT_RULES_SUGGESTIONS = "suggestions";
    public static String ELEMENT_RULES_SUGG = "sugg";
    public static String ELEMENT_RULES_VALUES = "rules_value";
    public static String ELEMENT_RULES_OPERATOR = "rules_operator";
    public static String ELEMENT_EXPORT_TABLE = "export_table";
    public static String ELEMENT_EXPORT_VAL = "export_val";
    public static String ELEMENT_RULES_TYPES = "rules_types";
    public static String ELEMENT_RULES_TYPE = "rules_type";
    public static String ELEMENT_RISK = "risk";
    public static String ELEMENT_RISK_AREA = "risk_area";
    public static String ATTR_NESTED = "nested";
    public static String ATT_NAME = "rules_name";
    public static String ATT_CASE_RULES_MAX = "maxrules";
    public static String ATT_RULES_TYPE = "kriterium";
    public static String ATT_RULES_ID = "rid";
    public static String ATT_RULES_OPERATOR = "operator";
    public static String ATT_RULES_VALUE = "wert";
    public static String ATT_RULES_MARK = "mark";
    public static String ATT_RULES_VALUE_NOT = "not";
    public static String ATT_RULES_METHOD = "method";
    public static String ATT_RULES_PARAMETER = "parameter";
    public static String ATT_OPERATOR_TYPE = "op_type";
    public static String ATT_CAPTION_CAPTION = "caption";
    public static String ATT_CAPTION_NUMBER = "number";
    public static String ATT_CAPTION_TEXT = "text";
    public static String ATT_CAPTION_TYPE = "typ";
    public static String ATT_CAPTION_USED = "used";
    public static String ATT_CAPTION_VISIBLE = "visible";
    public static String ATT_CAPTION_ENTGELT = "entgelt";
    public static String ATT_CAPTION_UNCHANGE = "unchange";
    public static String ATT_CAPTION_ROLE = "role";
    public static String ATT_CAPTION_FROM = "from";
    public static String ATT_CAPTION_TO = "to";
    public static String ATT_CAPTION_ERROR_TYPE = "errror_type";
    public static String ATT_CAPTION_RULE_YEAR = "rules_year";
    public static String ATT_RULES_NUMBER = "rules_number";
    public static String ATT_RULES_NOTICE = "rules_notice";
    public static String ATT_SUGG_TEXT = "suggtext";
    public static String ATT_SUGG_CRIT = "crit";
    public static String ATT_SUGG_VALUE = "value";
    public static String ATT_SUGG_ACTION = "action";
    public static String ATT_SUGG_ACTION_ID = "actionid";
    public static String ATT_SUGG_OP = "op";
    public static String ATT_SUGG_CONDITION_OP = "condition_op";
    public static String ATT_SUGG_CONDITION_VALUE = "condition_value";
    public static String ATT_EXPORT_TABLE_NAME = "table_name";
    public static String ATT_EXPORT_TABLE_COMMENT = "table_comment";
    public static String ATT_EXPORT_TABLE_CATEGORY = "table_category";
    public static String ATT_EXPORT_TABLE_YEAR = "table_year";
    public static String ATT_RULES_FEE_GROUPS = "feegroup";
    public static String ATT_RULES_TYPES_MAXID = "rtypes_maxi";
    public static String ATT_RULES_TYPE_IDENT = "rtype_ident";
    public static String ATT_RULES_TYPE_CREATION_DATE = "creation_date";
    public static String ATT_RULES_TYPE_SHORT_TEXT = "short_text";
    public static String ATT_RULES_TYPE_DISPLAY_TEXT = "display_text";
    public static String ATT_RULES_TYPE_ID = "rtype_id";
    public static String ATT_RULES_TYPE_ROLE = "role_id";
    public static String ATT_RULES_TYPE_USER = "user_id";
    public static String ATT_RULES_TYPE_ORGID = "rtype_orgid";
    public static String ATT_RULES_HAS_INTERVAL = "hasinterval";
    public static String ATT_RULES_INTERVAL_FROM = "interval_from";
    public static String ATT_RULES_INTERVAL_TO = "interval_to";
    public static final String RULES_TYPE_ERROR = "error";
    public static final String RULES_TYPE_WARNING = "warning";
    public static final String RULES_TYPE_SUGGESTION = "suggestion";
    public static final String NOTHING = "nothing";
    public static String ATT_RULES_MASSNUMBER = "massnumber";
    public static String ATT_RULES_MEDTYPE = "medtype";
    public static String ATT_RULES_PROFIT = "profit";
    public static String ATT_RISK_AREA_NAME = "risk_area_name";
    public static String ATT_RISK_WASTE_PERCENT_VALUE = "risk_waste_percent_value";
    public static String ATT_RISK_AUDIT_PERCENT_VALUE = "risk_audit_percent_value";
    public static String ATT_RISK_DEFAULT_WASTE_VALUE = "risk_default_waste_value";
    public static String ATT_RISK_COMMENT = "risk_comment";
}
