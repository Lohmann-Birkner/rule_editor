package de.lb.ruleprocessor.checkpoint.ruleGrouper;

import de.lb.ruleprocessor.checkpoint.server.exceptions.ExcException;
import de.lb.ruleprocessor.checkpoint.utils.UtlDateTimeConverter;
import java.util.Date;
import java.io.Serializable;
import java.util.ArrayList;

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
public class CRGRuleElement implements Serializable
{
        public String m_mark = null; // fuer CheckpointX eindeutige markierung eines elements fuer auswertung einzelnen termen der regel
	/**
	 * Type des Elementes
	 * 	public static final int TYPE_ELEMENT = 0;
	 *	public static final int TYPE_VALUE = 1;
	 *  public static final int TYPE_OPERATOR = 2;
	 */
	public int m_type = 0;

	/**
	 * Anzahl der Unterknoten
	 */
	public int m_childCount = 0;

	/**
	 * Gibt an, ob es Unterknoten gibt
	 */
	public boolean m_hasChilds = false;

	/**
	 * Gint an, ob eine Verschachtelung vorliegt
	 */
	public boolean m_isNested = false;

	public boolean m_isNot = false; // Verneinung vor dem Element

	/**
	 * Gibt an, ob eine Abhängigkeit von anderen Kriterien besteht
	 */
	public boolean m_isDepended = false;

	/**
	 * Index des Kriteriums mit der Abhängigkeit
	 */
	public int m_depend = 0;

	/**
	 * Ist Fallübergreifend
	 */
	public boolean m_isCrossCase = false;

	/**
	 * Flag, der wird auf true gesetzt, wenn das Kriterium ein summierendes Kriterium ist
	 */
	public boolean m_isCummulative = false;
	/**
	 * bei kummulierten Kriterien index eines Kriteriens, für den die Werte für Intervall gesammelt werden z.B. für
	 * das Kriterium GesamtPreisFuerPZN m_cummulateBase - Index des Kriteriens PZN
	 */
	public int m_indCummulateBase = -1;
	/**
	 * bei kummulierten Kriterien index eines Kriteriens, dessen Werte werden gesammelt. Z.B. für
	 * das Kriterium GesamtPreisFuerPZN m_cummulateWhat - Index des Kriteriens Einzellpreis
	 */
	public int m_indCummulateWhat = -1;
	/**
	 * bei kummulierten Kriterien index eines Kriteriens, dessen Wert mit dem entsprechenden Wert auf
	 * m_cummulateWhat multipliziert wird. Z.B. für
	 * das Kriterium GesamtPreisFuerPZN m_cummulateNumber - Index des Kriteriens Anzahl für PZN
	 */
	public int m_indCummulateNumber = -1;
	/**
	 * bei kummulierten Kriterien index eines Kriteriens, der speichert das Datum des entsprechenden Eintrages. Z.B für
	 * das Kriterium GesamtPreisFuerPZN m_indCummulateDate ist Index des Kriteriens Verschreibungsdatum
	 */
	int m_indCummulateDate = -1;

	/**
	 * Unterlemente
	 */
	public CRGRuleElement[] m_childElements = null;

	/**
	 * Datentyp des ersten Elementes
	 */
	public int m_firstChildType = 0;

	/**
	 * Typ des nächsten Operator
	 */
	public int m_nextOperantType = 0;

	/**
	 * Berechnungs-Typ
	 * 	public static final int COMPUTE_TYPE_BOTH = 0;
	 *	public static final int COMPUTE_TYPE_CRITERIUM = 1;
	 *  public static final int COMPUTE_TYPE_VALUE = 2;
	 *  public static final int COMPUTE_TYPE_OPERATOR = 3;
	 */
	public int m_computeType = 0;
 	/**
	 * Datentyp des Kriteriums
	 * public static final int DATATYPE_UNFORMATTED = -1;
	 * public static final int DATATYPE_STRING = 1;
	 * public static final int DATATYPE_INTEGER = 2;
	 * public static final int DATATYPE_DOUBLE = 3;
	 * public static final int DATATYPE_DATE = 4;
	 * public static final int DATATYPE_ARRAY_STRING = 5;
	 * public static final int DATATYPE_ARRAY_INTEGER = 6;
	 * public static final int DATATYPE_ARRAY_DOUBLE = 7;
	 * public static final int DATATYPE_ARRAY_DATE = 8;
	 * public static final int DATATYPE_DAY_TIME = 9;
	 * public static final int DATATYPE_ARRAY_DAY_TIME = 10;
	 */
	public int m_criterionType = -1;

	/**
	 * Index des Kriteriums
	 */
	public int m_criterionIndex = -1;
	public int m_criterionDependIndex = -1;
	public int m_criterionCheckIndex = -1;
	public int m_criterionDependGroupIndex = -1;

	/**
	 * Kennzeichen für vorangestellten NOT-Operator
	 */
	public boolean m_notElement = false;

	/**
	 * Typ des Operators
	 * public static final int OP_NO_OPERATION = 0;
	 * public static final int OP_AND = 1;
	 * public static final int OP_OR = 2;
	 * public static final int OP_EQUAL = 3;
	 * public static final int OP_GT = 4;
	 * public static final int OP_GT_EQUAL = 5;
	 * public static final int OP_LT = 6;
	 * public static final int OP_LT_EQUAL = 7;
	 * public static final int OP_NOT_EQUAL = 8;
	 * public static final int OP_PLUS = 9;
	 * public static final int OP_MINUS = 10;
	 * public static final int OP_MULTIPL = 11;
	 * public static final int OP_DIVIDE = 12;
	 * public static final int OP_IN = 13;
	 * public static final int OP_NOT_IN = 14;
	 * public static final int OP_NOT_IN_TABLE = 15;
	 * public static final int OP_IN_TABLE = 16;
	 * public static final int OP_IN_FALL = 17;
	 * public static final int OP_NOT_DOUBLE_IN = 18;
	 * public static final int OP_NOT_DOUBLE_IN_TABLE = 19;
	 * public static final int OP_MANY_IN = 20;
	 * public static final int OP_MANY_IN_TABLE = 21;
	 * public static final int OP_CONCATENATE = 22;
	 */
	public int m_operantType = -1;

	/**
	 * Kennzeichnet Kriterien ohne Wert
	 */
	public boolean m_operantCompute = false;

	/**
	 * Datentyp des Wertes
	 */
	public int m_valueType = -1;

 /**
  *     public static final int METHOD_TYPE_MAX_INTERVAL_PROPERTY = 1;
        public static final int METHOD_TYPE_MAX_INTERVAL_TABLE = 2;

  */       
        public int m_methodType = 0;
        /**
         * parameter - Felder für definierten Method
         */
        public Object[] m_parameter = null;
// für MethodCriterionEntry wird z.B. das Datums - Array zu dem OPS - Array in Verbindung gesetzt
        public int m_method_dependency_crit = -1;
        public int m_method_dependency_type = -1;
        
        
        
	/**
	 * Gibt an, ob das Regel-Element ein Intervall hat
	 */
	public boolean hasInterval = false;

	public long intervalStart = 0;
	public long intervalEnd = 0;

	public CRGIntervalEntry interval = null;

	public String m_strValue=null;
	public int m_intValue=-1;
	public double m_doubleValue=-1;
	public java.util.Date m_dateValue=null;
	public long m_longValue=-1;
	public java.util.Date m_datetimeValue=null;
	public long m_longtimeValue=-1;
	public String[] m_strArrayValue=null;
	public int[] m_intArrayValue=null;
	public double[] m_doubleArrayValue;
	public java.util.Date[] m_dayTimeArrayValue=null;
	public long[] m_longtimeArrayValue=null;
	public java.util.Date[] m_dateArrayValue=null;
	public long[] m_longArrayValue=null;
	public int m_length=-1;
	public int m_arrayLength=-1;
	public int m_level = 0;
      // für den Fall, dass das Kriterium eine Tabelleeinthält, wird die Tabellenname gespeichert.
      // Benutzt wird nur in toString() anweisung
        public String m_tableName = null;

	public CriterionDepend[] m_criterionDepend;
	public int m_WildCard=0;
//	public int[] m_WildCardArray=null;

	private CRGRuleElement m_parent = null;
// für Datumskriterien, die als Tage, Stunden und s.w. in den Regeln eingetragen sind um in toString den richtigen wert zu teigen       
        public String m_helpDateUnformat = null;
        public String m_helpStringListValue = null;
        public String m_helpStringDoubleValue = null;
        public String m_helpStringIntValue = null;
        public String m_helpStringDateValue = null;
        public String m_helpStringDateTimeValue = null;


	public CRGRuleElement(CRGRuleElement parent)
	{
		m_parent = parent;
		if(parent != null)
			m_level = parent.m_level + 1;
	}

	public String toString()
	{

		StringBuffer buffer = new StringBuffer();
                if(m_mark != null && !m_mark.isEmpty()){
                    buffer.append(" " + m_mark + ": ");
                }
		switch(m_type){
			case CRGRuleGrouperStatics.TYPE_ELEMENT:
				buffer.append("(");
				for(int i = 0; i < m_childElements.length; i++){
					buffer.append(m_childElements[i].toString());
				}
				buffer.append(")");
				return buffer.toString();
			case CRGRuleGrouperStatics.TYPE_OPERATOR:
                            if(m_operantType > 0){
				 buffer.append(" ").append(CRGRuleGrouperManager.getOperatorText(m_operantType)).append(" ");
                                 return buffer.toString();
                            }else return "";
			case CRGRuleGrouperStatics.TYPE_VALUE:{
				buffer.append( CRGRuleGrouperManager.getCriterionTextByIndex(m_criterionType, m_criterionIndex));
				buffer.append(" ").append(CRGRuleGrouperManager.getOperatorText(m_operantType)).append(" ");
                                if( m_criterionIndex == CRGRuleGrouperStatics.CRITINT_INDEX_NO_CRIT || m_operantType > 0){
                                    buffer.append( getValuesAsString());
                                }
                                if(hasInterval){
                                    buffer.append(" ").append(interval == null?"":interval.toString());
                                }
				if(m_notElement){
					buffer.append(")");
					buffer.insert(0, "not(");
				}
				return buffer.toString();
			}
		}
		return "";
	}

	private String getValuesAsString()
	{
		switch(m_valueType){
			case CRGRuleGrouperStatics.DATATYPE_INTEGER:
				return String.valueOf(m_intValue);
			case CRGRuleGrouperStatics.DATATYPE_DOUBLE:
				String ret = String.valueOf(m_doubleValue);
                                ret = ret == null?"":ret.replace(".", ",");
                                return ret;
			case CRGRuleGrouperStatics.DATATYPE_STRING:
				return m_strValue;
			case CRGRuleGrouperStatics.DATATYPE_DAY_TIME:
                            if(m_helpDateUnformat != null){
                                return m_helpDateUnformat;
                            }
				return "'hh:mm'";
			case CRGRuleGrouperStatics.DATATYPE_DATE:
                            if(m_helpDateUnformat != null){
                                return m_helpDateUnformat;
                            }
                            if(m_dateValue != null){
                                return UtlDateTimeConverter.converter().convertDateToExportString(m_dateValue, false);
                            }
                            if(m_longValue > 0L){
                                return UtlDateTimeConverter.converter().convertDateToExportString(new Date(m_longValue), false);
                            }
                            return "'jjjjmmdd'";
			case CRGRuleGrouperStatics.DATATYPE_ARRAY_STRING:
				return m_tableName == null?(this.m_helpStringListValue == null?"strArray": m_helpStringListValue ):m_tableName;
			case CRGRuleGrouperStatics.DATATYPE_ARRAY_INTEGER:
				return m_tableName == null?(m_helpStringIntValue == null?"intArray":m_helpStringIntValue):m_tableName;
			case CRGRuleGrouperStatics.DATATYPE_ARRAY_DOUBLE:
				return m_tableName == null?(m_helpStringDoubleValue == null?"doubleArray":m_helpStringDoubleValue + ""):m_tableName;
			case CRGRuleGrouperStatics.DATATYPE_ARRAY_DAY_TIME:
				return m_tableName == null?(m_helpStringDateTimeValue == null?"dateTimeArray":m_helpStringDateTimeValue  ):m_tableName;
			case CRGRuleGrouperStatics.DATATYPE_ARRAY_DATE:
				return m_tableName == null?(m_helpStringDateValue == null?"dateArray":m_helpStringDateValue ):m_tableName;
				default: return m_strValue == null?"":m_strValue;
		}
	}

        
        // Ist für Method, mache ich nicht
        /**
//         * setzt die Parameter - Felder für den MaxInrevalEigenschaft - Method in den CRGRuleElement
//         * @param ruleEle
//         * @param parameter 
//         */
//        public void setParameter4MaxIntervalPropertyMethod(  String parameter)
//        {
//       try{
//           m_parameter = new Object[4];
//           
//            String properties = parameter.substring(parameter.indexOf("[") + 1, parameter.indexOf("]"));
//
//            m_parameter[0] = getParameterIndexes(properties);
//            String nums = "";
//           parameter = parameter.substring(parameter.indexOf("]") + 2);
//            if(parameter.indexOf("[") >= 0 && parameter.indexOf("]") >= 0){
//                properties = parameter.substring(parameter.indexOf("[") + 1, parameter.indexOf("]"));
//                 m_parameter[1] = getParameterIndexes(properties);
//                nums = parameter.substring(parameter.indexOf("]") + 2);
//            } else{
//              nums = parameter;
//               m_parameter[1] = null;
//            }
//            String[] nn = nums.split(",");
//            if(nn.length > 1){
//                try{
//                    m_parameter[2] = new Integer(Integer.valueOf(nn[0].trim()));
//                }catch(NumberFormatException n1){
//                    ExcException.createException(n1);
//                }
//                try{
//                    m_parameter[3] = new Integer(Integer.valueOf(nn[1].trim()));
//                }catch(NumberFormatException n2){
//                    ExcException.createException(n2);
//                }
//               
//            }
//            
//        }catch(Exception e){
//            ExcException.createException(e);
//        }
//        }
        
        // Ist für Method, mache ich nicht
//        
//        private ArrayList getParameterIndexes(String properties)
//        {
//            String props[] = properties.split(",");
//           ArrayList<Integer> asss = new ArrayList<Integer>();
//            for(int i = 0, n = props.length; i < n; i++){
//                AssistantCriterionEntry ass = CRGRuleGrouperManager.getCrit2PropertyDisplayText(props[i].trim());
//                if(ass != null)
//                    asss.add(new Integer(ass.m_index));
//            }
//           return asss;
//        }

       /**
         * setzt die Parameter - Felder für den MaxInrevalEigenschaft - Method in den CRGRuleElement
         * @param parameter string
         */
        public void setParameter4MaxIntervalTableMethod(  String parameter)
        {
       try{
            m_parameter = new Object[3];
          
            String[] nn = parameter.split(",");
            if(nn.length > 3){
                try{
                    m_parameter[0] = nn[1].trim();
                    m_parameter[1] = new Integer(Integer.valueOf(nn[2].trim()));
                }catch(NumberFormatException n1){
                    ExcException.createException(n1);
                }
                try{
                   m_parameter[2] = new Integer(Integer.valueOf(nn[3].trim()));
                }catch(NumberFormatException n2){
                    ExcException.createException(n2);
                }
               
            }
            
        }catch(Exception e){
            ExcException.createException(e);
        }        
     }
        
       public Object getMethodParameter(int index)
       {
           if(m_parameter == null || m_parameter.length <= index)
               return null;
           return m_parameter[index];
       }

}
