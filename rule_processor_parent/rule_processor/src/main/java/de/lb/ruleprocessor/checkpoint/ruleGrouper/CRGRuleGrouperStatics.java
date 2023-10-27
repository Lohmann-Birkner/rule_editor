package de.lb.ruleprocessor.checkpoint.ruleGrouper;

/**
 * <p>Überschrift: Checkpoint DRG</p>
 *
 * <p>Beschreibung: Fallmanagement DRG</p>
 *
 * <p>Copyright: </p>
 *
 * <p>Organisation: </p>
 *
 * @author unbekannt
 * @version 2.0
 */
public class CRGRuleGrouperStatics
{
	public static final int OP_NO_OPERATION = 0;
	public static final int OP_AND = 1;
	public static final int OP_OR = 2;
	public static final int OP_EQUAL = 3;
	public static final int OP_GT = 4;
	public static final int OP_GT_EQUAL = 5;
	public static final int OP_LT = 6;
	public static final int OP_LT_EQUAL = 7;
	public static final int OP_NOT_EQUAL = 8;
	public static final int OP_PLUS = 9;
	public static final int OP_MINUS = 10;
	public static final int OP_MULTIPL = 11;
	public static final int OP_DIVIDE = 12;
	public static final int OP_IN = 13;
	public static final int OP_NOT_IN = 14;
	public static final int OP_NOT_IN_TABLE = 15;
	public static final int OP_IN_TABLE = 16;
	public static final int OP_IN_FALL = 17;
	public static final int OP_NOT_DOUBLE_IN = 18;
	public static final int OP_NOT_DOUBLE_IN_TABLE = 19;
	public static final int OP_MANY_IN = 20;
	public static final int OP_MANY_IN_TABLE = 21;
	public static final int OP_CONCATENATE = 22;
// operation range
	public static final int OP_RANGE_OR = 1;
	public static final int OP_RANGE_AND = 2;
	public static final int OP_RANGE_COMPARE = 3;
	public static final int OP_RANGE_DASH = 4;
	public static final int OP_RANGE_DOT = 5;

	public static final int COMPUTE_TYPE_BOTH = 0;
	public static final int COMPUTE_TYPE_CRITERIUM = 1;
	public static final int COMPUTE_TYPE_VALUE = 2;
	public static final int COMPUTE_TYPE_OPERATOR = 3;
	public static final int COMPUTE_TYPE_METHOD = 4;

	public static final int TYPE_ELEMENT = 0;
	public static final int TYPE_VALUE = 1;
	public static final int TYPE_OPERATOR = 2;

	public static int MAX_STRING_INDEX = 100;
	public static int MAX_INTEGER_INDEX = 100;
	public static int MAX_DOUBLE_INDEX = 100;
	public static int MAX_DATE_INDEX = 100;
	public static int MAX_LONG_INDEX = 100;
	public static int MAX_ARRAY_STRING_INDEX = 100;
	public static int MAX_ARRAY_INTEGER_INDEX = 100;
	public static int MAX_ARRAY_DOUBLE_INDEX = 100;
	public static int MAX_ARRAY_DATE_INDEX = 100;
	public static int MAX_ARRAY_LONG_INDEX = 100;
	public static int MAX_ARRAY_DEPEND_INDEX = 100;

	public static final int DATATYPE_UNFORMATTED = -1;
	public static final int DATATYPE_STRING = 1;
	public static final int DATATYPE_INTEGER = 2;
	public static final int DATATYPE_DOUBLE = 3;
	public static final int DATATYPE_DATE = 4;
	public static final int DATATYPE_ARRAY_STRING = 5;
	public static final int DATATYPE_ARRAY_INTEGER = 6;
	public static final int DATATYPE_ARRAY_DOUBLE = 7;
	public static final int DATATYPE_ARRAY_DATE = 8;
	public static final int DATATYPE_DAY_TIME = 9;
	public static final int DATATYPE_ARRAY_DAY_TIME = 10;
        public static final int DATATYPE_DATE_WITH_TIME_SHOW = 11;
        public static final int DATATYPE_ALL_OPERATIONS_NESTED = 99; // für Export der Operatotenliste für Termsverknüpfung
        
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// Intervallindizies
    public static final int INDEX_INTERVAL_NO_INTERVAL = -1;
    public static final int INDEX_INTERVAL_DATE = 1;
    public static final int INDEX_INTERVAL_DAYS = 2;
    public static final int INDEX_INTERVAL_MONTHS = 3;
    public static final int INDEX_INTERVAL_QUARTERS = 4;
    public static final int INDEX_INTERVAL_YEARS = 5;
    public static final int INDEX_INTERVAL_ADM_DATE = 6;
    public static final int INDEX_INTERVAL_DIS_DATE = 7;
    public static final int INDEX_INTERVAL_OGVD = 8;
    public static final int INDEX_INTERVAL_CURRENT_CASE = 9;
    public static final int INDEX_INTERVAL_CASE = 10;
    public static final int INDEX_INTERVAL_TIME_STAMP_DATE_1 = 11;
    public static final int INDEX_INTERVAL_TIME_STAMP_DATE_2 = 12;
    public static final int INDEX_INTERVAL_NOW = 13;
    
    //nur für die Auswertung
    public static final int CRITINT_INDEX_NO_CRIT = 999;
    private static int[] m_critEntryIndexSort = null;

//Indizies für die Klasse Depend
	public static final int DEPEND_NO = 0;
    
    public static int[] INTERVAL_NO_VAL = {
        INDEX_INTERVAL_NO_INTERVAL, INDEX_INTERVAL_ADM_DATE,INDEX_INTERVAL_DIS_DATE,
        INDEX_INTERVAL_OGVD,INDEX_INTERVAL_CURRENT_CASE,INDEX_INTERVAL_TIME_STAMP_DATE_1,
        INDEX_INTERVAL_TIME_STAMP_DATE_2, INDEX_INTERVAL_NOW
    };
    
    public static int[] INTERVAL_WITH_VAL = {
        INDEX_INTERVAL_DATE,INDEX_INTERVAL_DAYS , INDEX_INTERVAL_MONTHS,INDEX_INTERVAL_QUARTERS,
        INDEX_INTERVAL_YEARS, INDEX_INTERVAL_CASE
    };

     
	public CRGRuleGrouperStatics()
	{
	}
}
