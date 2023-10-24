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
import java.util.*;

//import de.checkpoint.client.clientManager.*;

import de.checkpoint.utils.UtlDateTimeConverter;
//import de.checkpoint.server.data.caseRules.DatCaseRuleMgr;
/*import de.checkpoint.server.data.caseRules.*;
import de.checkpoint.server.exceptions.*;
import de.checkpoint.server.rmServer.medicineManager.RmmSole;*/

/**
 * <p>�berschrift: Checkpoint DRG</p>
 *
 * <p>Beschreibung: Ein-/Ausgabe-Objekt des Regelkerns</p>
 *
 * <p>Copyright: Lohmann & Birkner Health Care Consulting GmbH </p>
 *
 * <p>Organisation: Lohmann & Birkner Health Care Consulting GmbH</p>
 *
 * @author unbekannt
 * @version 2.0
 */
public class CRGInputOutputBasic 
{
	public static final int ONE_SECOND = 1000;
	public static final int ONE_MINUTE = 60 * ONE_SECOND;
	public static final int ONE_HOUR = 60 * ONE_MINUTE;
	public static final long ONE_DAY = 24 * ONE_HOUR;
	public static final long HALF_DAY = 12 * ONE_HOUR;
	public static final long ONE_WEEK = 7 * ONE_DAY;

	private static TimeZone m_timeZone = null;
	private static Calendar m_calendar = null;
	protected String[] m_stringValues = null;
	protected int[] m_integerValues = null;
	protected double[] m_doubleValues = null;
	protected java.util.Date[] m_dateValues = null;
	protected long[] m_longValues = null;
	protected String[][] m_arrayStringValues = null;
    //protected ArrayList<String>[] m_arrayStringValues = null;
	protected int[][] m_arrayIntegerValues = null;
	protected double[][] m_arrayDoubleValues = null;
	protected java.util.Date[][] m_arrayDateValues = null;
	//protected ArrayList<Date>[] m_arrayDateValues = null;
	protected long[][] m_arrayLongValues = null;
	protected java.util.Date[] m_datetimeValues = null;
	protected long[] m_longtimeValues = null;
	protected java.util.Date[][] m_arrayDatetimeValues = null;
	protected long[][] m_arrayLongtimeValues = null;

	protected long[][] m_checkDateValues = null;


//	protected int m_checkYear = 0;
//	protected long m_checkDate = 0;
//	protected long m_admissionDate = 0;
//	protected long m_dischargeDate = 0;
//	protected long m_admissionTime = 0;
//	protected long m_dischargeTime = 0;
//	protected long m_transferTime = 0;

	protected static final int DEFAULT_INT_VALUE = -Integer.MAX_VALUE;
	protected static final double DEFAULT_DOUBLE_VALUE = -Double.MAX_VALUE;
	protected static final long DEFAULT_LONG_VALUE = -Long.MAX_VALUE;


	private double m_dCW = 0d;
        public static final long END_OF_2014 = getDateWithoutTimeAsLong(UtlDateTimeConverter.converter().getEndDateForYear(2014));

	/**
	 * Konstruktor
	 * - Objekt muss f�r DRG-Groupen aus CheckpointRuleGrouper geladen werden
	 */
	public CRGInputOutputBasic()
	{

	}

        public void setAnyValue(CriterionEntry pCrit, Object pFieldValue) throws Exception{
            if(pCrit != null){
                int index = pCrit.getM_index();
                switch(pCrit.getType()){
                    case DATATYPE_STRING:
                        if(pFieldValue instanceof String){
                            this.setStringValue((String)pFieldValue, index);
                        }
                        break;
                    case DATATYPE_INTEGER:
                        if(pFieldValue instanceof Integer){
                            this.setIntegerValue((Integer)pFieldValue, index);
                        }
                        break;
                    case DATATYPE_DOUBLE:
                        if(pFieldValue instanceof Double){
                            this.setDoubleValue((Double)pFieldValue, index);
                        }
                        break;
                    case DATATYPE_DATE:
                        if(pFieldValue instanceof Date){
                            this.setDateValue((Date)pFieldValue, index);
                            this.setLongValue(getDateWithoutTimeAsLong((Date) pFieldValue), index);
                        }
                        break;
                    case DATATYPE_ARRAY_STRING:
                        if(pFieldValue instanceof String){
                            this.setArrayStringValue((String)pFieldValue, index);
                        }
                        break;
                    case DATATYPE_ARRAY_INTEGER:
                        if(pFieldValue instanceof Integer){
                            this.setArrayIntegerValue((Integer)pFieldValue, index);
                        }
                        break;
                    case DATATYPE_ARRAY_DOUBLE:
                        if(pFieldValue instanceof Double){
                            this.setArrayDoubleValue((Double)pFieldValue, index);
                        }
                        break;
                    case DATATYPE_ARRAY_DATE:
                         if(pFieldValue instanceof Date){
                            this.setArrayDateValue((Date)pFieldValue, index);
                            this.setArrayLongValue(getDateWithoutTimeAsLong((Date) pFieldValue), index);
                        }
                       break;
                    case DATATYPE_DAY_TIME:
                        if(pFieldValue instanceof Date){
                             int timeOfDay = getTimeFromDate((Date)pFieldValue);
                            this.setLongValue(getDateWithoutTimeAsLong((Date) pFieldValue), index);
                        }
                       
                        break;
                    case DATATYPE_ARRAY_DAY_TIME:
                        if(pFieldValue instanceof Date){
                             int timeOfDay = getTimeFromDate((Date)pFieldValue);
                            this.setArrayLongValue(getDateWithoutTimeAsLong((Date) pFieldValue), index);
                        }
                       
                        break;

                }
            }
            
        }
	/**
	 * Gibt alle vorhandenen Parameter zur�ck,
	 * die als Datentyp String-Array definiert sind.
	 * @return String[]
	 */
	public String[] getStringValues()
	{
		return this.m_stringValues;
	}

	/**
	 * Gibt alle vorhandenen Parameter zur�ck,
	 * die als Datentyp int-Array definiert sind.
	 * @return int[]
	 */
	public int[] getIntegerValues()
	{
		return this.m_integerValues;
	}



	/**
	 * Gibt alle vorhandenen Parameter zur�ck,
	 * die als Datentyp double-Array definiert sind.
	 * @return double[]
	 */
	public double[] getDoubleValues()
	{
		return this.m_doubleValues;
	}

	/**
	 * Gibt alle vorhandenen Parameter zur�ck,
	 * die als Datentyp Date-Array definiert sind.
	 * @return Date[]
	 */
	public Date[] getDateValues()
	{
		return this.m_dateValues;
	}

	/**
	 * Gibt alle vorhandenen Parameter zur�ck,
	 * die als Datentyp long-Array definiert sind.
	 * @return Date[]
	 */
	public long[] getLongValues()
	{
		return this.m_longValues;
	}

	/**
	 * Gibt alle vorhandenen Parameter zur�ck,
	 * die als Datentyp Datetime-Array definiert sind.
	 * @return Date[]
	 */
	public Date[] getDateTimeValues()
	{
		return this.m_datetimeValues;
	}

	/**
	 * Gibt alle vorhandenen Parameter zur�ck,
	 * die als Datentyp long-Array (Datum/Zeit als long-Wert) definiert sind.
	 * @return Date[]
	 */
	public long[] getLongTimeValues()
	{
		return this.m_longtimeValues;
	}


        
	protected static TimeZone getTimeZone()
        {
 /*           if(m_timeZone == null) {
			TimeZone zone = TimeZone.getDefault();
// damit wird der �bergang Sommer/Winterzeit resetet
                     m_timeZone = new SimpleTimeZone(zone.getRawOffset(),
                     zone.getDisplayName());
		}*/
            if(m_timeZone == null) {
                m_timeZone =  TimeZone.getDefault();//getTimeZone("CET");
  //                  m_timeZone = new SimpleTimeZone(m_timeZone.getRawOffset(),m_timeZone.getDisplayName());
            }
            
            return m_timeZone;
        }

	protected static int getTimeZoneOffset(long ldt)
	{
		
		return getTimeZone().getOffset(ldt); 
	}

	protected static Calendar getCalendar()
	{
		if(m_calendar == null) {
			m_calendar = Calendar.getInstance(); 
		}
                
		return m_calendar;
	}

	public static int getTimeFromDate(java.util.Date date)
	{
		return getTimeFromDate(date.getTime());
	}

	protected static int getTimeFromDate(long ldt)
	{
		int timeOfDay = getTimeZoneOffset(ldt);
		timeOfDay = timeOfDay % (int)ONE_DAY;
		timeOfDay += (int)(ldt % ONE_DAY);
		if(timeOfDay >= ONE_DAY) {
			timeOfDay -= ONE_DAY;
		}
		return timeOfDay;
	}
        
        public static synchronized Date getDateWithoutTime(java.util.Date date)
        {
            //f�r den Fall, dass m_calendar noch nicht initiirt ist
            if(date == null){
                return null;
            }
            getCalendar().setTime(date);
            /* 3.9.5 2015-09-02 DNi: #FINDBUGS - Ver�nderung des statischen Attributs m_calendar kann zu Problemen bei mehreren Threads f�hren. */
            m_calendar.set(Calendar.HOUR_OF_DAY, 0);
            m_calendar.set(Calendar.MINUTE, 0);
            m_calendar.set(Calendar.SECOND, 0);
            m_calendar.set(Calendar.MILLISECOND, 0);
            return m_calendar.getTime();
        }
        
        public static long getDateWithoutTimeAsLong(java.util.Date date)
        {
            long ret = getDateWithoutTime(date).getTime();
           long l = (ret + HALF_DAY)/ONE_DAY * ONE_DAY; // runden auf vollen tag, damit sommer/winterzeit nicht ber�cksichtigt wird
           return l;
        }
        
        

	/**
	 * Ermittelt den long-Wert des �bergebenden Datums ohne Zeit.
	 * @param dt Date : Datum
	 * @return long
	 */
	public static long getDateLong(java.util.Date dt)
	{
		if(dt != null) {
			return getDateWithoutTimeAsLong(dt);
		} else {
			return -1;
		}
	}


	/**
	 * Mapping auf CP Lokalisation.
	 * @param value String
	 * @return int
	 * @throws Exception
	 */
	protected int getLocalisation(String value)
	{
		try {
			value = value.toLowerCase();
			char c = value.charAt(0);
			switch(c) {
				case 'r':
				case '1':
					return 1;
				case 'l':
				case '2':
					return 2;
				case 'b':
				case '3':
					return 3;
				default:
					return 0;
			}
		} catch(Exception ex) {
			return 0;
		}
	}
        
        public static String getStringLocalisation(int loc)
{
	switch(loc){
		case 1: return "L";
		case 2: return "R";
		case 3: return "B";
		default: return "";
	}
}


	/**
	 * Setzt ein Kriterium in ein String Array.
	 * @param value String : Wert
	 * @param index int : Index des Wertes
	 * @throws Exception
	 */
	protected void setStringValue(String value, int index) throws Exception
	{
		if (value != null) {
			value = value.trim().toUpperCase();
			value = value.replaceAll(" ", "");
		}

		setStringValueDirect(value, index);
	}

	protected void setStringValueDirect(String value, int index) throws Exception
	{
		if(m_stringValues == null) {
			m_stringValues = new String[CRGRuleGrouperStatics.MAX_STRING_INDEX]; 
		}
		if(m_stringValues.length < index + 1) {
			m_stringValues = getNewStringValue(m_stringValues, value, index);
		} else {
			m_stringValues[index] = value;
		}

	}

	protected String[] getNewStringValue(String[] m_strValue, String value, int index) throws Exception
	{
		String[] strValue = new String[index + 1];
/*		for(int i = 0; i < m_strValue.length; i++) {
			strValue[i] = m_strValue[i];
		}*/
		System.arraycopy(m_strValue, 0, strValue, 0, m_strValue.length);
		strValue[index] = value;
		return strValue;
	}

	protected String[] getNewStringValue(String[] m_strValue, int index) throws Exception
	{
		String[] strValue = new String[index + 1];
/*		for(int i = 0; i < m_strValue.length; i++) {
			strValue[i] = m_strValue[i];
		}*/
		System.arraycopy(m_strValue, 0, strValue, 0, m_strValue.length);
		return strValue;
	}

	/**
	 * Gibt den String-Wert aus dem String-Array zur�ck.
	 * @param index int
	 * @return String
	 * @throws Exception
	 */
	public String getStringValue(int index) throws Exception
	{
		if(m_stringValues == null) {
			return "";
		}
		if(m_stringValues.length < index + 1) {
			m_stringValues = getNewStringValue(m_stringValues, index);
		}
		return m_stringValues[index];
	}

	/**
	 * Setzt ein Kriterium in ein Integer-Array.
	 * @param value int : Wert
	 * @param index int : Index des Wertes
	 * @throws Exception
	 */
	protected void setIntegerValue(int value, int index) throws Exception
	{
		if(m_integerValues == null)
			m_integerValues = new int[CRGRuleGrouperStatics.MAX_INTEGER_INDEX];
		if(m_integerValues.length < index + 1) {
			m_integerValues = getNewIntegerValue(m_integerValues, value, index);
		} else {
			m_integerValues[index] = value;
		}

	}

	protected int[] getNewIntegerValue(int[] m_intValue, int value, int index) throws Exception
	{
		int[] intValue = new int[index + 1];
/*		for(int i = 0; i < m_intValue.length; i++) {
			intValue[i] = m_intValue[i];
		}*/
		System.arraycopy(m_intValue, 0, intValue, 0, m_intValue.length);
		intValue[index] = value;
		return intValue;
	}

	protected int[] getNewIntegerValue(int[] m_intValue, int index) throws Exception
	{
		int[] intValue = new int[index + 1];
/*		for(int i = 0; i < m_intValue.length; i++) {
			intValue[i] = m_intValue[i];
		}*/
		System.arraycopy(m_intValue, 0, intValue, 0, m_intValue.length);
		return intValue;
	}

	/**
	 * Gibt den Integer-Wert aus dem Register des Regelkerns zur�ck.
	 * @param index int
	 * @return int
	 * @throws Exception
	 */
	public int getIntegerValue(int index) throws Exception
	{
		if(m_integerValues.length < index + 1) {
			m_integerValues = getNewIntegerValue(m_integerValues, index);
		}
		return m_integerValues[index];
	}

	/**
	 * Setzt ein Kriterium in ein Double-Array.
	 * @param value double : Wert
	 * @param index int :  Index des Wertes
	 * @throws Exception
	 */
	protected void setDoubleValue(double value, int index) throws Exception
	{
		if(m_doubleValues == null)
			m_doubleValues = new double[CRGRuleGrouperStatics.MAX_DOUBLE_INDEX];
		if(m_doubleValues.length < index + 1) {
			m_doubleValues = getNewDoubleValue(m_doubleValues, value, index);
		} else {
			m_doubleValues[index] = value;
		}

	}

	protected double[] getNewDoubleValue(double[] m_doubleValue, double value, int index) throws Exception
	{
		double[] doubleValue = new double[index + 1];
/*		for(int i = 0; i < m_doubleValue.length; i++) {
			doubleValue[i] = m_doubleValue[i];
		}*/
		System.arraycopy(m_doubleValue, 0, doubleValue, 0, m_doubleValue.length);
		doubleValue[index] = value;
		return doubleValue;
	}

	protected double[] getNewDoubleValue(double[] m_doubleValue, int index) throws Exception
	{
		double[] doubleValue = new double[index + 1];
/*		for(int i = 0; i < m_doubleValue.length; i++) {
			doubleValue[i] = m_doubleValue[i];
		}*/
			System.arraycopy(m_doubleValue, 0, doubleValue, 0, m_doubleValue.length);
		return doubleValue;
	}

	/**
	 * Gibt den Double-Wert aus dem Register des Regelkerns zur�ck.
	 * @param index int
	 * @return double
	 * @throws Exception
	 */
	public double getDoubleValue(int index) throws Exception
	{
		if(m_doubleValues.length < index + 1) {
			m_doubleValues = getNewDoubleValue(m_doubleValues, index);
		}
		return m_doubleValues[index];
	}

	/**
	 *
	 * @param value Date
	 * @param index int
	 * @throws Exception
	 */
	protected void setDateValue(java.util.Date value, int index) throws Exception
	{
		if(m_dateValues == null)
			m_dateValues = new java.util.Date[CRGRuleGrouperStatics.MAX_DATE_INDEX];
		if(m_dateValues.length < index + 1) {
			m_dateValues = getNewDateValue(m_dateValues, value, index);
		} else {
			m_dateValues[index] = value;
		}

	}

	/**
	 *
	 * @param value DateTime
	 * @param index int
	 * @throws Exception
	 */
	protected void setDateTimeValue(java.util.Date value, int index) throws Exception
	{
		if(m_datetimeValues == null) {
			m_datetimeValues = new java.util.Date[index + 1];
			m_datetimeValues[index] = value;
		} else {
			if(m_datetimeValues.length < index + 1) {
				m_datetimeValues = getNewDateTimeValue(m_datetimeValues, value, index);
			} else {
				m_datetimeValues[index] = value;
			}
		}
	}

	protected java.util.Date[] getNewDateValue(java.util.Date[] m_dateValue, java.util.Date value,
		int index) throws Exception
	{
		java.util.Date[] dateValue = new java.util.Date[index + 1];
/*		for(int i = 0; i < m_dateValue.length; i++) {
			dateValue[i] = m_dateValue[i];
		}*/
		System.arraycopy(m_dateValue, 0, dateValue, 0, m_dateValue.length);
		dateValue[index] = value;
		return dateValue;
	}

	private java.util.Date[] getNewDateTimeValue(java.util.Date[] m_datetimeValue, java.util.Date value,
		int index) throws Exception
	{
		java.util.Date[] datetimeValue = new java.util.Date[index + 1];
/*		for(int i = 0; i < m_datetimeValue.length; i++) {
			datetimeValue[i] = m_datetimeValue[i];
		}*/
			System.arraycopy(m_datetimeValue, 0, datetimeValue, 0, m_datetimeValue.length);
			datetimeValue[index] = value;
		return datetimeValue;
	}

	protected java.util.Date[] getNewDateValue(java.util.Date[] m_dateValue, int index) throws Exception
	{
		java.util.Date[] dateValue = new java.util.Date[index + 1];
/*		for(int i = 0; i < m_dateValue.length; i++) {
			dateValue[i] = m_dateValue[i];
		}*/
			System.arraycopy(m_dateValue, 0, dateValue, 0, m_dateValue.length);
		return dateValue;
	}

	protected java.util.Date[] getNewDateTimeValue(java.util.Date[] m_datetimeValue, int index) throws Exception
	{
		java.util.Date[] datetimeValue = new java.util.Date[index + 1];
/*		for(int i = 0; i < m_datetimeValue.length; i++) {
			datetimeValue[i] = m_datetimeValue[i];
		}*/
		System.arraycopy(m_datetimeValue, 0, datetimeValue, 0, m_datetimeValue.length);
		return datetimeValue;
	}

	/**
	 * Gibt das Datum aus dem Register des Regelkerns zur�ck.
	 * @param index int
	 * @return Date
	 * @throws Exception
	 */
	public java.util.Date getDateValue(int index) throws Exception
	{
		if(m_dateValues.length < index + 1) {
			m_dateValues = getNewDateValue(m_dateValues, index);
		}
		return m_dateValues[index];
	}

	protected java.util.Date getDateTimeValue(int index) throws Exception
	{
		if(m_datetimeValues.length < index + 1) {
			m_datetimeValues = getNewDateTimeValue(m_datetimeValues, index);
		}
		return m_datetimeValues[index];
	}

	protected void setCheckLongDateValue(long value, int index) throws Exception
	{
		setCheckLongDateValue(value, index, 1, -1);
	}

	protected void setCheckLongDateValue(long value, int index, int maxInd, int ind) throws Exception
	{
		if(m_checkDateValues == null) {
			m_checkDateValues = new long[index + 1][];
		} else {
			if(m_checkDateValues.length < index + 1) {
				long[][] longNewValue =  new long[index + 1][];
				System.arraycopy(m_checkDateValues, 0, longNewValue, 0, m_checkDateValues.length);
				m_checkDateValues = longNewValue;

			}
		}
		if(m_checkDateValues[index] == null) {
			m_checkDateValues[index] = new long[maxInd];
			m_checkDateValues[index][0] = value;
		} else {
			if(ind > 0 && m_checkDateValues[index].length > ind)
				m_checkDateValues[index][ind] = value;
			else
				m_checkDateValues[index] = getNewLongValue(m_checkDateValues[index], value, m_checkDateValues[index].length);
		}
	}

	/**
	 * Gibt das Datum als Long-Wert aus dem Register des Regelkerns zur�ck.
	 * @param index int
	 * @return long[]
	 * @throws Exception
	 */
	public long[] getCheckLongDateValue(int index) throws Exception
	{
		if(index < 0) {
			throw new CRGRuleGroupException("index of long-Array must greater than -1 " + index);
		}
		if(m_checkDateValues == null) {
			m_checkDateValues = new long[index + 1][];
		} else {
			if(m_checkDateValues.length < index + 1) {
				long[][] longOldValue = m_checkDateValues;
				long[][] longValue = new long[index + 1][];
				m_checkDateValues = longValue;
/*				for(int i = 0; i < longOldValue.length; i++) {
					m_checkDateValues[i] = longOldValue[i];
				}*/
				System.arraycopy(longOldValue, 0, m_checkDateValues, 0, longOldValue.length);
			}
		}
		if(m_checkDateValues[index] == null) {
			m_checkDateValues[index] = new long[1];
		}
		return m_checkDateValues[index];
	}

	/**
	 *
	 * @param value long
	 * @param index int
	 * @throws Exception
	 */
	protected void setLongValue(long value, int index) throws Exception
	{
		if(m_longValues == null)
			m_longValues = new long[CRGRuleGrouperStatics.MAX_DATE_INDEX];
		if(m_longValues.length < index + 1) {
			m_longValues = getNewLongValue(m_longValues, value, index);
		} else {
			m_longValues[index] = value;
		}

	}

	protected long[] getNewLongValue(long[] m_longValue, long value, int index) throws Exception
	{
		long[] longValue = new long[index + 1];
/*		for(int i = 0; i < m_longValue.length; i++) {
			longValue[i] = m_longValue[i];
		}*/
		System.arraycopy(m_longValue, 0, longValue, 0, m_longValue.length);
		longValue[index] = value;
		return longValue;
	}

	protected long[] getNewLongValue(long[] m_longValue, int index) throws Exception
	{
		long[] longValue = new long[index + 1];
/*		for(int i = 0; i < m_longValue.length; i++) {
			longValue[i] = m_longValue[i];
		}*/
		System.arraycopy(m_longValue, 0, longValue, 0, m_longValue.length);
		return longValue;
	}

	/**
	 * Gibt den Long-Wert aus dem Register des Regelkerns zur�ck.
	 * @param index int
	 * @return long
	 * @throws Exception
	 */
	public long getLongValue(int index) throws Exception
	{
		if(m_longValues.length < index + 1) {
			m_longValues = getNewLongValue(m_longValues, index);
		}
		return m_longValues[index];
	}

	/**
	 *
	 * @param value longTime
	 * @param index int
	 * @throws Exception
	 */
	protected void setLongTimeValue(long value, int index) throws Exception
	{
		if(m_longtimeValues == null) {
			m_longtimeValues = new long[index + 1];
			m_longtimeValues[index] = value;
		} else {
			if(m_longtimeValues.length < index + 1) {
				m_longtimeValues = getNewLongTimeValue(m_longtimeValues, value, index);
			} else {
				m_longtimeValues[index] = value;
			}
		}
	}

	protected long[] getNewLongTimeValue(long[] m_longtimeValue, long value, int index) throws Exception
	{
		long[] longtimeValue = new long[index + 1];
/*		for(int i = 0; i < m_longtimeValue.length; i++) {
			longtimeValue[i] = m_longtimeValue[i];
		}*/
		System.arraycopy(m_longtimeValue, 0, longtimeValue, 0, m_longtimeValue.length);
		longtimeValue[index] = value;
		return longtimeValue;
	}

	protected long[] getNewLongTimeValue(long[] m_longtimeValue, int index) throws Exception
	{
		long[] longtimeValue = new long[index + 1];
/*		for(int i = 0; i < m_longtimeValue.length; i++) {
			longtimeValue[i] = m_longtimeValue[i];
		}*/
		System.arraycopy(m_longtimeValue, 0, longtimeValue, 0, m_longtimeValue.length);
		return longtimeValue;
	}

	/**
	 * Gibt den Long-Wert eines Datums aus dem Register des Regelkerns zur�ck.
	 * @param index int
	 * @return long
	 * @throws Exception
	 */
	public long getLongTimeValue(int index) throws Exception
	{
		if(m_longtimeValues.length < index + 1) {
			m_longtimeValues = getNewLongTimeValue(m_longtimeValues, index);
		}
		return m_longtimeValues[index];
	}

	/**
	 * Setzt einen Wert innerhalb eines String-Array.
	 * @param value String : Wert
	 * @param index int : Index des Pr�fparameters
	 * @throws Exception
	 */
	protected void setArrayStringValue(String value, int index) throws Exception
	{
		if(value != null) {
			value = value.trim().toUpperCase();
			value = value.replaceAll(" ", "");
		}
		setArrayStringValue(value, index, 1, -1);
	}
    protected void setArrayStringValueDirect(String value, int index) throws Exception
    {
        setArrayStringValue(value, index, 1, -1);
    }
		/**
		 * Setzt einen Wert innerhalb eines String-Array.
		 * @param value String : Wert
		 * @param index int : Index des Pr�fparameters
		 * @param maxIndex : groesse des Arrays zu anlegen auf der index - Stelle
		 * @throws Exception
		 */
	/*protected void setArrayStringValue(String value, int index, int maxIndex, int ind) throws Exception
	{
		//zweidimensionales Array
		if(m_arrayStringValues == null) {
//			m_arrayStringValues = new String[index + 1][];
            m_arrayStringValues = new ArrayList[index + 1];
		} else {
			if(m_arrayStringValues.length < index + 1) {
				ArrayList[] strNewValue =  new ArrayList[index + 1];
				System.arraycopy(m_arrayStringValues, 0, strNewValue, 0, m_arrayStringValues.length);
				m_arrayStringValues = strNewValue;
			}
		}
		if(m_arrayStringValues[index] == null) {
			m_arrayStringValues[index] = new ArrayList();
		}
        m_arrayStringValues[index].add(value);

	}
*/
	protected void setArrayStringValue(String value, int index, int maxIndex, int ind) throws Exception
	{
		//zweidimensionales Array
		if(m_arrayStringValues == null) {
			m_arrayStringValues = new String[index + 1][];
		} else {
			if(m_arrayStringValues.length < index + 1) {
				String[][] strNewValue =  new String[index + 1][];
				System.arraycopy(m_arrayStringValues, 0, strNewValue, 0, m_arrayStringValues.length);
				m_arrayStringValues = strNewValue;
/*				for(int i = 0; i < strOldValue.length; i++) {
					m_arrayStringValues[i] = strOldValue[i];
				}*/
			}
		}
		if(m_arrayStringValues[index] == null) {
			m_arrayStringValues[index] = new String[maxIndex];
			m_arrayStringValues[index][0] = value;
		} else {
			if((m_arrayStringValues[index].length == 1) && (m_arrayStringValues[index][0] == null)) {
				m_arrayStringValues[index][0] = value;
			} else {
				if(ind > 0 && m_arrayStringValues[index].length > ind){
					m_arrayStringValues[index][ind] = value;
				}else
					m_arrayStringValues[index] = getNewStringValue(m_arrayStringValues[index], value,
											 m_arrayStringValues[index].length);
			}
		}
	}

	/**
	 * entfernt ein Stringarray f�r den index
	 * @param index int
	 * @throws Exception
	 */
	protected void removeArrayStringValue(int index) throws Exception
	{
		if(m_arrayStringValues == null || (m_arrayStringValues.length < index + 1)) {
			return;
		}
		m_arrayStringValues[index] = null;

	}

	/**
	 * entfernt ein Intarray f�r den index
	 * @param index int
	 * @throws Exception
	 */
	protected void removeArrayIntValue(int index) throws Exception
	{
		if(m_arrayIntegerValues == null || (m_arrayIntegerValues.length < index + 1)) {
			return;
		}
		m_arrayIntegerValues[index] = null;

	}

	/**
	 * entfernt ein Longarray f�r den index
	 * @param index int
	 * @throws Exception
	 */
	protected void removeArrayLongValue(int index) throws Exception
	{
		if(m_arrayLongValues == null || (m_arrayLongValues.length < index + 1)) {
			return;
		}
		m_arrayLongValues[index] = null;

	}

	/**
	 * entfernt ein Longarray f�r den index
	 * @param index int
	 * @throws Exception
	 */
	protected void removeArrayCheckDateValue(int index) throws Exception
	{
		if(m_checkDateValues == null || (m_checkDateValues.length < index + 1)) {
			return;
		}
		m_checkDateValues[index] = null;

	}

	/**
	 * entfernt ein Doublearray f�r den index
	 * @param index int
	 * @throws Exception
	 */
	protected void removeArrayDoubleValue(int index) throws Exception
	{
		if(m_arrayDoubleValues == null || (m_arrayDoubleValues.length < index + 1)) {
			return;
		}
		m_arrayDoubleValues[index] = null;

	}

	/**
	 * entfernt ein Datearray f�r den index
	 * @param index int
	 * @throws Exception
	 */
	protected void removeArrayDateValue(int index) throws Exception
	{
		if(m_arrayDateValues == null || (m_arrayDateValues.length < index + 1)) {
			return;
		}
		m_arrayDateValues[index] = null;

	}

	/**
	 * Gibt einen String-Array-Wert aus dem Register des Regelkerns zur�ck.
	 * @param index int
	 * @return String[]
	 * @throws Exception
	 */
	/*public String[] getArrayStringValue(int index) throws Exception
	{
		if(index < 0) {
			throw new CRGRuleGroupException("index of String-Array must greater than -1 " + index);
		}
		if(m_arrayStringValues == null) {
			m_arrayStringValues = new ArrayList[index + 1];
		} else {
			if(m_arrayStringValues.length < index + 1) {
				ArrayList[]strNewValue =  new ArrayList[index + 1];
				System.arraycopy(m_arrayStringValues, 0, strNewValue, 0, m_arrayStringValues.length);
				m_arrayStringValues = strNewValue;
			}
		}
		// muss der index <> null sein???
		if(m_arrayStringValues[index] == null) {
			m_arrayStringValues[index] = new ArrayList();
		}
        String[] ret = new String[m_arrayStringValues[index].size()];
        m_arrayStringValues[index].toArray(ret);
		return ret;
	}
*/
	public String[] getArrayStringValue(int index) throws Exception
	{
		if(index < 0) {
			throw new CRGRuleGroupException("index of String-Array must greater than -1 " + index);
		}
		if(m_arrayStringValues == null) {
			m_arrayStringValues = new String[index + 1][];
		} else {
			if(m_arrayStringValues.length < index + 1) {
				String[][] strNewValue =  new String[index + 1][];
				System.arraycopy(m_arrayStringValues, 0, strNewValue, 0, m_arrayStringValues.length);
				m_arrayStringValues = strNewValue;
			}
		}
		// muss der index <> null sein???
		if(m_arrayStringValues[index] == null) {
			m_arrayStringValues[index] = new String[0];
		}
		return m_arrayStringValues[index];
	}

	/**
	 * Setzt einen Wert innerhalb eines Integer-Array.
	 * @param value int : Wert
	 * @param index int : Index des Pr�fparameters
	 * @throws Exception
	 */
	protected void setArrayIntegerValue(int value, int index) throws Exception
	{
		setArrayIntegerValue(value, index, 1, -1);
	}
		/**
		 * Setzt einen Wert innerhalb eines Integer-Array.
		 * @param value int : Wert
		 * @param index int : Index des Pr�fparameters
		 * maxInd
		 * @throws Exception
		 */
	protected void setArrayIntegerValue(int value, int index, int maxInd, int ind) throws Exception
	{
		//zweidimensionales Array
		if(m_arrayIntegerValues == null) {
			m_arrayIntegerValues = new int[index + 1][];
		} else {
			if(m_arrayIntegerValues.length < index + 1) {
				int[][] intNewValue = new int[index + 1][];
				System.arraycopy(m_arrayIntegerValues, 0, intNewValue, 0, m_arrayIntegerValues.length);
				m_arrayIntegerValues = intNewValue;
			}
		}
		if(m_arrayIntegerValues[index] == null) {
			m_arrayIntegerValues[index] = new int[maxInd];
			m_arrayIntegerValues[index][0] = value;
		} else {
			if(ind > 0 && m_arrayIntegerValues[index].length > ind)
				m_arrayIntegerValues[index][ind] = value;
			else
				m_arrayIntegerValues[index] = getNewIntegerValue(m_arrayIntegerValues[index], value,
										  m_arrayIntegerValues[index].length);
		}
	}

	protected void setArrayInteger(int[] values, int index) throws Exception
	{
		//zweidimensionales Array
		if(m_arrayIntegerValues == null) {
			m_arrayIntegerValues = new int[index + 1][];
		} else {
			if(m_arrayIntegerValues.length < index + 1) {
				int[][] intNewValue = new int[index + 1][];
				System.arraycopy(m_arrayIntegerValues, 0, intNewValue, 0, m_arrayIntegerValues.length);
				m_arrayIntegerValues = intNewValue;
			}
		}
		m_arrayIntegerValues[index] = values;
	}

	protected void setArrayLong(long[] values, int index) throws Exception
	{
		//zweidimensionales Array
		if(m_arrayLongValues == null) {
			m_arrayLongValues = new long[index + 1][];
		} else {
			if(m_arrayLongValues.length < index + 1) {
				long[][] newLongValue = new long[index + 1][];
				System.arraycopy(m_arrayLongValues, 0, newLongValue, 0, m_arrayLongValues.length);
				m_arrayLongValues = newLongValue;
			}
		}
		m_arrayLongValues[index] = values;
	}

	protected void setArrayDate(Date[] values, int index) throws Exception
	{
		//zweidimensionales Array
		if(m_arrayDateValues == null) {
			m_arrayDateValues = new Date[index + 1][];
		} else {
			if(m_arrayDateValues.length < index + 1) {
				Date[][] dateNewValue = new Date[index + 1][];
				System.arraycopy(m_arrayDateValues, 0, dateNewValue, 0, m_arrayDateValues.length);
				m_arrayDateValues = dateNewValue;
			}
		}
		m_arrayDateValues[index] = values;
	}

	protected void setArrayDouble(double[] values, int index) throws Exception
	{
		//zweidimensionales Array
		if(m_arrayDoubleValues == null) {
			m_arrayDoubleValues = new double[index + 1][];
		} else {
			if(m_arrayDoubleValues.length < index + 1) {
				double[][] doubleNewValue = new double[index + 1][];
				System.arraycopy(m_arrayDoubleValues, 0, doubleNewValue, 0, m_arrayDoubleValues.length);
				m_arrayDoubleValues = doubleNewValue;
			}
		}
		m_arrayDoubleValues[index] = values;
	}

	protected void setArrayString(String[] values, int index) throws Exception
	{
		//zweidimensionales Array
		if(m_arrayStringValues == null) {
			m_arrayStringValues = new String[index + 1][];
		} else {
			if(m_arrayStringValues.length < index + 1) {
				String[][] strNewValue = new String[index + 1][];
				System.arraycopy(m_arrayStringValues, 0, strNewValue, 0, m_arrayStringValues.length);
				m_arrayStringValues = strNewValue;
			}
		}
		m_arrayStringValues[index] = values;
	}

	/**
	 * Gibt einen Integer-Array-Wert aus dem Register des Regelkerns zur�ck.
	 * @param index int
	 * @return int[]
	 * @throws Exception
	 */
	public int[] getArrayIntegerValue(int index) throws Exception
	{
		if(index < 0) {
			throw new CRGRuleGroupException("index of Integer-Array must greater than -1 " + index);
		}
		if(m_arrayIntegerValues == null) {
			m_arrayIntegerValues = new int[index + 1][];
		} else {
			if(m_arrayIntegerValues.length < index + 1) {
				int[][] intOldValue = m_arrayIntegerValues;
				int[][] intValue = new int[index + 1][];
				m_arrayIntegerValues = intValue;
				for(int i = 0; i < intOldValue.length; i++) {
					m_arrayIntegerValues[i] = intOldValue[i];
				}
			}
		}
		if(m_arrayIntegerValues[index] == null) {
			m_arrayIntegerValues[index] = new int[0];
		}
		return m_arrayIntegerValues[index];
	}

	private void resetArrayIntegerValue(int index) throws Exception
	{
		if(m_arrayIntegerValues != null && m_arrayIntegerValues.length > index) {
			if(m_arrayIntegerValues[index] != null) {
				m_arrayIntegerValues[index] = null;
			}
		}
	}


	/**
	 * Setzt einen Wert innerhalb eines Double-Array.
	 * @param value double : Wert
	 * @param index int : Index des Pr�fparameters
	 * @throws Exception
	 */
	protected void setArrayDoubleValue(double value, int index) throws Exception
	{
		setArrayDoubleValue(value, index, 1, -1);
	}
		/**
		 * Setzt einen Wert innerhalb eines Double-Array.
		 * @param value double : Wert
		 * @param index int : Index des Pr�fparameters
		 * @param maxInd int : max. Anzahl der Elemente auf index - Stelle
		 * @throws Exception
		 */
	protected void setArrayDoubleValue(double value, int index, int maxInd, int ind) throws Exception
		{
		//zweidimensionales Array
		if(m_arrayDoubleValues == null) {
			m_arrayDoubleValues = new double[index + 1][];
		} else {
			if(m_arrayDoubleValues.length < index + 1) {
				double[][] doubleNewValue = new double[index + 1][];
				System.arraycopy(m_arrayDoubleValues, 0, doubleNewValue, 0,m_arrayDoubleValues.length);
				m_arrayDoubleValues = doubleNewValue;
			}
		}
		if(m_arrayDoubleValues[index] == null) {
			m_arrayDoubleValues[index] = new double[maxInd];
			m_arrayDoubleValues[index][0] = value;
		} else {
			if(ind > 0 && m_arrayDoubleValues[index].length > ind)
				m_arrayDoubleValues[index][ind] = value;
			else
				m_arrayDoubleValues[index] = getNewDoubleValue(m_arrayDoubleValues[index], value,
										 m_arrayDoubleValues[index].length);
		}
	}

	/**
	 * Gibt einen Double-Array-Wert aus dem Register des Regelkerns zur�ck.
	 * @param index int
	 * @return double[]
	 * @throws Exception
	 */
	public double[] getArrayDoubleValue(int index) throws Exception
	{
		if(index < 0) {
			throw new CRGRuleGroupException("index of double-Array must greater than -1 " + index);
		}
		if(m_arrayDoubleValues == null) {
			m_arrayDoubleValues = new double[index + 1][];
		} else {
			if(m_arrayDoubleValues.length < index + 1) {
				double[][] doubleNewValue =  new double[index + 1][];
				System.arraycopy(m_arrayDoubleValues, 0, doubleNewValue, 0, m_arrayDoubleValues.length);
				m_arrayDoubleValues = doubleNewValue;
			}
		}
		if(m_arrayDoubleValues[index] == null) {
			m_arrayDoubleValues[index] = new double[0];
		}
		return m_arrayDoubleValues[index];
	}

	/**
	 * Setzt einen Wert innerhalb eines Date-Arrays.
	 * @param value date : Wert
	 * @param index int : Index des Pr�fparameters
	 * @throws Exception
	 */
	protected void setArrayDateValue(java.util.Date value, int index) throws Exception
	{
		setArrayDateValue(value, index, 1, -1);
	}
		/**
		 * Setzt einen Wert innerhalb eines Date-Arrays.
		 * @param value date : Wert
		 * @param index int : Index des Pr�fparameters
		 * @throws Exception
		 */
	protected void setArrayDateValue(java.util.Date value, int index, int maxInd, int ind) throws Exception
	{
		//zweidimensionales Array
		if(m_arrayDateValues == null) {
			m_arrayDateValues = new java.util.Date[index + 1][];
		} else {
			if(m_arrayDateValues.length < index + 1) {
				java.util.Date[][] dateNewValue = new java.util.Date[index + 1][];
				System.arraycopy(m_arrayDateValues, 0, dateNewValue, 0, m_arrayDateValues.length);
				m_arrayDateValues = dateNewValue;
			}
		}
		if(m_arrayDateValues[index] == null) {
			m_arrayDateValues[index] = new java.util.Date[maxInd];
			m_arrayDateValues[index][0] = value;
		} else {
			if(ind > 0 && m_arrayDateValues[index].length > ind){
				m_arrayDateValues[index][ind] = value;
			}else
				m_arrayDateValues[index] = getNewDateValue(m_arrayDateValues[index], value, m_arrayDateValues[index].length);
		}
	}

	/**
	 * Setzt einen Wert innerhalb eines Date-Arrays.
	 * @param value date : Wert
	 * @param index int : Index des Pr�fparameters
	 * @throws Exception
	 */
	protected void setArrayDayTimeValue(java.util.Date value, int index) throws Exception
	{
		//zweidimensionales Array
		if(m_arrayDatetimeValues == null) {
			m_arrayDatetimeValues = new java.util.Date[index + 1][];
		} else {
			if(m_arrayDatetimeValues.length < index + 1) {
				java.util.Date[][] dateNewValue = new java.util.Date[index + 1][];
				System.arraycopy(m_arrayDatetimeValues, 0, dateNewValue, 0, m_arrayDatetimeValues.length);
				m_arrayDatetimeValues = dateNewValue;
			}
		}
		if(m_arrayDatetimeValues[index] == null) {
			m_arrayDatetimeValues[index] = new java.util.Date[1];
			m_arrayDatetimeValues[index][0] = value;
		} else {
			m_arrayDatetimeValues[index] = getNewDateTimeValue(m_arrayDatetimeValues[index], value,
										   m_arrayDatetimeValues[index].length);
		}
	}

	/**
	 * Gibt einen Datum-Array-Wert aus dem Register des Regelkerns zur�ck.
	 * @param index int
	 * @return Date[]
	 * @throws Exception
	 */
	public java.util.Date[] getArrayDateValue(int index) throws Exception
	{
		if(index < 0) {
			throw new CRGRuleGroupException("index of date-Array must greater than -1 " + index);
		}
		if(m_arrayDateValues == null) {
			m_arrayDateValues = new java.util.Date[index + 1][];
		} else {
			if(m_arrayDateValues.length < index + 1) {
				java.util.Date[][] dateNewValue = new java.util.Date[index + 1][];
				System.arraycopy(m_arrayDateValues, 0, dateNewValue, 0, m_arrayDateValues.length);
				m_arrayDateValues = dateNewValue;
			}
		}
		if(m_arrayDateValues[index] == null) {
			m_arrayDateValues[index] = new java.util.Date[0];
		}
		return m_arrayDateValues[index];
	}

	/**
	 * Gibt einen Datetime-Array-Wert aus dem Register des Regelkerns zur�ck.
	 * @param index int
	 * @return Date[]
	 * @throws Exception
	 */
	public java.util.Date[] getArrayDateTimeValue(int index) throws Exception
	{
		if(index < 0) {
			throw new CRGRuleGroupException("index of date-Array must greater than -1 " + index);
		}
		if(m_arrayDatetimeValues == null) {
			m_arrayDatetimeValues = new java.util.Date[index + 1][];
		} else {
			if(m_arrayDatetimeValues.length < index + 1) {
				java.util.Date[][] dateNewValue = new java.util.Date[index + 1][];
				System.arraycopy(m_arrayDatetimeValues, 0, dateNewValue, 0, m_arrayDatetimeValues.length);
				m_arrayDatetimeValues = dateNewValue;
			}
		}
		if(m_arrayDatetimeValues[index] == null) {
			m_arrayDatetimeValues[index] = new java.util.Date[0];
		}
		return m_arrayDatetimeValues[index];
	}

	/**
	 * Setzt einen Wert innerhalb eines long-Arrays.
	 * @param value long : Wert
	 * @param index int : Index des Pr�fparameters
	 * @throws Exception
	 */
	protected void setArrayLongValue(long value, int index) throws Exception
	{
		setArrayLongValue(value, index, 1, -1);
	}

	/**
	 * Setzt einen Wert innerhalb eines long-Arrays.
	 * @param value long : Wert
	 * @param index int : Index des Pr�fparameters
	 * @param maxInd : groesse des Arrays auf index - Platz
	 * @throws Exception
	 */
	protected void setArrayLongValue(long value, int index, int maxInd, int ind) throws Exception
	{
		//zweidimensionales Array
		if(m_arrayLongValues == null) {
			m_arrayLongValues = new long[index + 1][];
		} else {
			if(m_arrayLongValues.length < index + 1) {
				long[][] longNewValue =  new long[index + 1][];
				System.arraycopy(m_arrayLongValues, 0, longNewValue, 0, m_arrayLongValues.length);
				m_arrayLongValues = longNewValue;
			}
		}
		if(m_arrayLongValues[index] == null) {
			m_arrayLongValues[index] = new long[maxInd];
			m_arrayLongValues[index][0] = value;
		} else {
			if(ind > 0 && m_arrayLongValues[index].length > ind)
				m_arrayLongValues[index][ind] = value;
			else
				m_arrayLongValues[index] = getNewLongValue(m_arrayLongValues[index], value, m_arrayLongValues[index].length);
		}
	}

	/**
	 * Setzt einen Wert innerhalb eines long-Arrays.
	 * @param value long : Wert
	 * @param index int : Index des Pr�fparameters
	 * @throws Exception
	 */
	protected void setArrayLongtimeValue(long value, int index) throws Exception
	{
		//zweidimensionales Array
		if(m_arrayLongtimeValues == null) {
			m_arrayLongtimeValues = new long[index + 1][];
		} else {
			if(m_arrayLongtimeValues.length < index + 1) {
				long[][] longNewValue = new long[index + 1][];
				System.arraycopy(m_arrayLongtimeValues, 0, longNewValue, 0, m_arrayLongtimeValues.length);
				m_arrayLongtimeValues = longNewValue;
			}
		}
		if(m_arrayLongtimeValues[index] == null) {
			m_arrayLongtimeValues[index] = new long[1];
			m_arrayLongtimeValues[index][0] = value;
		} else {
			m_arrayLongtimeValues[index] = getNewLongValue(m_arrayLongtimeValues[index], value,
										   m_arrayLongtimeValues[index].length);
		}
	}

	/**
	 * Gibt einen Long-Array-Wert (Datum) aus dem Register des Regelkerns zur�ck.
	 * @param index int
	 * @return Date[]
	 * @throws Exception
	 */
	protected long[] getArrayLongValue(int index) throws Exception
	{
		if(index < 0) {
			throw new CRGRuleGroupException("index of long-Array must greater than -1 " + index);
		}
		if(m_arrayLongValues == null) {
			m_arrayLongValues = new long[index + 1][];
		} else {
			if(m_arrayLongValues.length < index + 1) {
				long[][] longNewValue =  new long[index + 1][];
				System.arraycopy(m_arrayLongValues, 0, longNewValue, 0, m_arrayLongValues.length);
				m_arrayLongValues = longNewValue;
			}
		}
		if(m_arrayLongValues[index] == null) {
			m_arrayLongValues[index] = new long[0];
		}
		return m_arrayLongValues[index];
	}

	/**
	 * Gibt einen Long-Array-Wert (Datum) aus dem Register des Regelkerns zur�ck.
	 * @param index int
	 * @return Date[]
	 * @throws Exception
	 */
	protected long[] getArrayLongtimeValue(int index) throws Exception
	{
		if(index < 0) {
			throw new CRGRuleGroupException("index of long-Array must greater than -1 " + index);
		}
		if(m_arrayLongtimeValues == null) {
			m_arrayLongtimeValues = new long[index + 1][];
		} else {
			if(m_arrayLongtimeValues.length < index + 1) {
				long[][] longNewValue = new long[index + 1][];
				System.arraycopy(m_arrayLongtimeValues, 0, longNewValue, 0, m_arrayLongtimeValues.length);
				m_arrayLongtimeValues = longNewValue;
			}
		}
		if(m_arrayLongtimeValues[index] == null) {
			m_arrayLongtimeValues[index] = new long[1];
		}
		return m_arrayLongtimeValues[index];
	}

	/**
	 * Zur�cksetzen aller Felder, bevor die �bergabe neuer Werte erfolgt.
	 */
	public void newCase()
	{
		m_stringValues = null;
		m_integerValues = null;
		m_doubleValues =  null;
		m_dateValues = null;
		m_datetimeValues = null;
		m_arrayStringValues = null;
		m_arrayIntegerValues = null;
		m_arrayDoubleValues =  null;
		m_arrayDateValues =  null;
		m_longValues = null;
		m_longtimeValues =  null;
		m_arrayLongValues =null;
		m_checkDateValues = null;
//		m_admissionDate = 0;
//		m_admissionTime = 0;
//		m_dischargeTime = 0;
//		m_transferTime = 0;
		m_dCW = 0;
		m_arrayDatetimeValues = null;
		m_arrayLongtimeValues = null;
	}

	public void copyCase(CRGInputOutputBasic inout) throws Exception
	{
		newCase();
		int len;
		if(inout.m_stringValues != null) {
			m_stringValues = new String[inout.m_stringValues.length];
/*			for(int i = 0; i < inout.m_stringValues.length; i++) {
				m_stringValues[i] = inout.m_stringValues[i];
			}*/
		 System.arraycopy(inout.m_stringValues, 0, m_stringValues, 0, inout.m_stringValues.length);
		}
		if(inout.m_integerValues != null) {
			m_integerValues = new int[inout.m_integerValues.length];
/*			for(int i = 0; i < inout.m_integerValues.length; i++) {
				m_integerValues[i] = inout.m_integerValues[i];
			}*/
		 System.arraycopy(inout.m_integerValues, 0, m_integerValues, 0, inout.m_integerValues.length);
		}
		if(inout.m_doubleValues != null) {
			m_doubleValues = new double[inout.m_doubleValues.length];
/*			for(int i = 0; i < inout.m_doubleValues.length; i++) {
				m_doubleValues[i] = inout.m_doubleValues[i];
			}*/
		 System.arraycopy(inout.m_doubleValues, 0, m_doubleValues, 0, inout.m_doubleValues.length);
		}
		if(inout.m_dateValues != null) {
			m_dateValues = new java.util.Date[inout.m_dateValues.length];
/*			for(int i = 0; i < inout.m_dateValues.length; i++) {
				m_dateValues[i] = inout.m_dateValues[i];
			}*/
		 System.arraycopy(inout.m_dateValues, 0, m_dateValues, 0, inout.m_dateValues.length);
		}
		if(inout.m_longValues != null) {
			m_longValues = new long[inout.m_longValues.length];
/*			for(int i = 0; i < inout.m_longValues.length; i++) {
				m_longValues[i] = inout.m_longValues[i];
			}*/
		 System.arraycopy(inout.m_longValues, 0, m_longValues, 0, inout.m_longValues.length);
		}
		if(inout.m_datetimeValues != null) {
			m_datetimeValues = new java.util.Date[inout.m_datetimeValues.length];
/*			for(int i = 0; i < inout.m_datetimeValues.length; i++) {
				m_datetimeValues[i] = inout.m_datetimeValues[i];
			}*/
		 System.arraycopy(inout.m_datetimeValues, 0, m_datetimeValues, 0, inout.m_datetimeValues.length);
		}
		if(inout.m_longtimeValues != null) {
			m_longtimeValues = new long[inout.m_longtimeValues.length];
/*			for(int i = 0; i < inout.m_longtimeValues.length; i++) {
				m_longtimeValues[i] = inout.m_longtimeValues[i];
			}*/
			System.arraycopy(inout.m_longtimeValues, 0, m_longtimeValues, 0, inout.m_longtimeValues.length);
		}
		if(inout.m_arrayStringValues != null) {
			m_arrayStringValues = new String[inout.m_arrayStringValues.length][];
			for(int i = 0; i < inout.m_arrayStringValues.length; i++) {
				if(inout.m_arrayStringValues[i] != null) {
					m_arrayStringValues[i] = new String[inout.m_arrayStringValues[i].length];
					System.arraycopy(inout.m_arrayStringValues[i], 0, m_arrayStringValues[i], 0, inout.m_arrayStringValues[i].length);
				}
			}
		}
		if(inout.m_arrayIntegerValues != null) {
			m_arrayIntegerValues = new int[inout.m_arrayIntegerValues.length][];
			for(int i = 0; i < inout.m_arrayIntegerValues.length; i++) {
				if(inout.m_arrayIntegerValues[i] != null) {
					m_arrayIntegerValues[i] = new int[inout.m_arrayIntegerValues[i].length];
					System.arraycopy(inout.m_arrayIntegerValues[i], 0, m_arrayIntegerValues[i], 0, inout.m_arrayIntegerValues[i].length);
				}
			}
		}
		if(inout.m_arrayDoubleValues != null) {
			m_arrayDoubleValues = new double[inout.m_arrayDoubleValues.length][];
			for(int i = 0; i < inout.m_arrayDoubleValues.length; i++) {
				if(inout.m_arrayDoubleValues[i] != null) {
					m_arrayDoubleValues[i] = new double[inout.m_arrayDoubleValues[i].length];
					System.arraycopy(inout.m_arrayDoubleValues[i], 0, m_arrayDoubleValues[i], 0, inout.m_arrayDoubleValues[i].length);
				}
			}
		}
		if(inout.m_arrayDateValues != null) {
			m_arrayDateValues = new java.util.Date[inout.m_arrayDateValues.length][];
			for(int i = 0; i < inout.m_arrayDateValues.length; i++) {
				if(inout.m_arrayDateValues[i] != null) {
					m_arrayDateValues[i] = new java.util.Date[inout.m_arrayDateValues[i].length];
					System.arraycopy(inout.m_arrayDateValues[i], 0, m_arrayDateValues[i], 0, inout.m_arrayDateValues[i].length);
				}
			}
		}
		if(inout.m_arrayDatetimeValues != null) {
			m_arrayDatetimeValues = new java.util.Date[inout.m_arrayDatetimeValues.length][];
			for(int i = 0; i < inout.m_arrayDatetimeValues.length; i++) {
				if(inout.m_arrayDatetimeValues[i] != null) {
					m_arrayDatetimeValues[i] = new java.util.Date[inout.m_arrayDatetimeValues[i].length];
					System.arraycopy(inout.m_arrayDatetimeValues[i], 0, m_arrayDatetimeValues[i], 0, inout.m_arrayDatetimeValues[i].length);
				}
			}
		}
		if(inout.m_arrayLongValues != null) {
			m_arrayLongValues = new long[inout.m_arrayLongValues.length][];
			for(int i = 0; i < inout.m_arrayLongValues.length; i++) {
				if(inout.m_arrayLongValues[i] != null) {
					m_arrayLongValues[i] = new long[inout.m_arrayLongValues[i].length];
					System.arraycopy(inout.m_arrayLongValues[i], 0, m_arrayLongValues[i], 0, inout.m_arrayLongValues[i].length);
				}
			}
		}
		if(inout.m_checkDateValues != null) {
			m_checkDateValues = new long[inout.m_checkDateValues.length][];
			for(int i = 0; i < inout.m_checkDateValues.length; i++) {
				if(inout.m_checkDateValues[i] != null) {
					m_checkDateValues[i] = new long[inout.m_checkDateValues[i].length];
					System.arraycopy(inout.m_checkDateValues[i], 0, m_checkDateValues[i], 0, inout.m_checkDateValues[i].length);
				}
			}
		}
		if(inout.m_arrayLongtimeValues != null) {
			m_arrayLongtimeValues = new long[inout.m_arrayLongtimeValues.length][];
			for(int i = 0; i < inout.m_arrayLongtimeValues.length; i++) {
				if(inout.m_arrayLongtimeValues[i] != null) {
					m_arrayLongtimeValues[i] = new long[inout.m_arrayLongtimeValues[i].length];
					System.arraycopy(inout.m_arrayLongtimeValues[i], 0, m_arrayLongtimeValues[i], 0, inout.m_arrayLongtimeValues[i].length);
				}
			}
		}
//		m_dischargeDate = inout.m_dischargeDate;
//		m_admissionDate = inout.m_admissionDate;
//		m_admissionTime = inout.m_admissionTime;
//		m_dischargeTime = inout.m_dischargeTime;
//		m_transferTime = inout.m_transferTime;
//		m_checkYear = inout.m_checkYear;

	}


	/**
	 * Ausgabe aller Input/Output Variablen.
	 * @return StringBuffer
	 */
	public StringBuffer dumpRecord()
	{
		String tt;
		StringBuffer sb = new StringBuffer();
		if(m_stringValues != null) {
			for(int i = 0; i < m_stringValues.length; i++) {
				tt = "String [" + String.valueOf(i) + "] \t";
				tt += CRGRuleGrouperManager.getCriterionTextByIndex(CRGRuleGrouperStatics.DATATYPE_STRING, i);
				tt += " \t=\t " + m_stringValues[i]; 
				tt += "\n";
				sb.append(tt);
			}
		}
		if(m_integerValues != null) {
			for(int i = 0; i < m_integerValues.length; i++) {
				tt = "Integer [" + String.valueOf(i) + "] \t";
				tt += CRGRuleGrouperManager.getCriterionTextByIndex(CRGRuleGrouperStatics.DATATYPE_INTEGER, i);
				tt += " \t=\t " + m_integerValues[i];
				tt += "\n";
				sb.append(tt);
			}
		}
		if(m_doubleValues != null) {
			for(int i = 0; i < m_doubleValues.length; i++) {
				tt = "Double [" + String.valueOf(i) + "] \t";
				tt += CRGRuleGrouperManager.getCriterionTextByIndex(CRGRuleGrouperStatics.DATATYPE_DOUBLE, i);
				tt += " \t=\t " + m_doubleValues[i];
				tt += "\n";
				sb.append(tt);
			}
		}
		if(m_dateValues != null) {
			for(int i = 0; i < m_dateValues.length; i++) {
				tt = "Date [" + String.valueOf(i) + "] \t";
				tt += CRGRuleGrouperManager.getCriterionTextByIndex(CRGRuleGrouperStatics.DATATYPE_DATE, i);
				tt += " \t=\t " + m_dateValues[i];
				tt += "\n";
				sb.append(tt);
			}
		}
		if(m_longValues != null) {
			for(int i = 0; i < m_longValues.length; i++) {
				tt = "Long [" + String.valueOf(i) + "] \t";
				tt += CRGRuleGrouperManager.getCriterionTextByIndex(CRGRuleGrouperStatics.DATATYPE_DATE, i);
				tt += " \t=\t " + m_longValues[i];
				tt += "\n";
				sb.append(tt);
			}
		}
		if(m_datetimeValues != null) {
			for(int i = 0; i < m_datetimeValues.length; i++) {
				tt = "DateTime [" + String.valueOf(i) + "] \t";
				tt += CRGRuleGrouperManager.getCriterionTextByIndex(CRGRuleGrouperStatics.DATATYPE_DAY_TIME, i);
				tt += " \t=\t " + m_datetimeValues[i];
				tt += "\n";
				sb.append(tt);
			}
		}
		if(m_longtimeValues != null) {
			for(int i = 0; i < m_longtimeValues.length; i++) {
				tt = "Long [" + String.valueOf(i) + "] \t";
				tt += CRGRuleGrouperManager.getCriterionTextByIndex(CRGRuleGrouperStatics.DATATYPE_DAY_TIME, i);
				tt += " \t=\t " + m_longtimeValues[i];
				tt += "\n";
				sb.append(tt);
			}
		}
		if(m_arrayStringValues != null) {
			for(int i = 0; i < m_arrayStringValues.length; i++) {
				String[] vals;
				tt = "String-Array [" + String.valueOf(i) + "] ";
				tt += CRGRuleGrouperManager.getCriterionTextByIndex(CRGRuleGrouperStatics.DATATYPE_ARRAY_STRING, i);
				vals = m_arrayStringValues[i];
				if(vals != null) {
					for(int j = 0; j < vals.length; j++) {
						tt += "\n\tString [" + String.valueOf(j) + "] \t=\t" + vals[j];
					}
				}
				tt += "\n";
				sb.append(tt);
			}
		}
		if(m_arrayIntegerValues != null) {
			for(int i = 0; i < m_arrayIntegerValues.length; i++) {
				int[] vals;
				tt = "Integer-Array [" + String.valueOf(i) + "] ";
				tt += CRGRuleGrouperManager.getCriterionTextByIndex(CRGRuleGrouperStatics.DATATYPE_ARRAY_INTEGER, i);
				vals = m_arrayIntegerValues[i];
				if(vals != null) {
					for(int j = 0; j < vals.length; j++) {
						tt += "\n\tString [" + String.valueOf(j) + "] \t=\t" + vals[j];
					}
				}
				tt += "\n";
				sb.append(tt);
			}
		}
		if(m_arrayDoubleValues != null) {
			for(int i = 0; i < m_arrayDoubleValues.length; i++) {
				double[] vals;
				tt = "Double-Array [" + String.valueOf(i) + "] ";
				tt += CRGRuleGrouperManager.getCriterionTextByIndex(CRGRuleGrouperStatics.DATATYPE_ARRAY_DOUBLE, i);
				vals = m_arrayDoubleValues[i];
				if(vals != null) {
					for(int j = 0; j < vals.length; j++) {
						tt += "\n\tString [" + String.valueOf(j) + "] \t=\t" + vals[j];
					}
				}
				tt += "\n";
				sb.append(tt);
			}
		}
		if(m_arrayDateValues != null) {
			for(int i = 0; i < m_arrayDateValues.length; i++) {
				java.util.Date[] vals;
				tt = "Date-Array [" + String.valueOf(i) + "] ";
				tt += CRGRuleGrouperManager.getCriterionTextByIndex(CRGRuleGrouperStatics.DATATYPE_ARRAY_DATE, i);
				vals = m_arrayDateValues[i];
				if(vals != null) {
					for(int j = 0; j < vals.length; j++) {
						tt += "\n\tString [" + String.valueOf(j) + "] \t=\t" + vals[j];
					}
				}
				tt += "\n";
				sb.append(tt);
			}
		}
		if(m_arrayDatetimeValues != null) {
			for(int i = 0; i < m_arrayDatetimeValues.length; i++) {
				java.util.Date[] vals;
				tt = "Datetime-Array [" + String.valueOf(i) + "] ";
				tt += CRGRuleGrouperManager.getCriterionTextByIndex(CRGRuleGrouperStatics.DATATYPE_ARRAY_DAY_TIME, i);
				vals = m_arrayDatetimeValues[i];
				if(vals != null) {
					for(int j = 0; j < vals.length; j++) {
						tt += "\n\tString [" + String.valueOf(j) + "] \t=\t" + vals[j];
					}
				}
				tt += "\n";
				sb.append(tt);
			}
		}
		if(m_arrayLongValues != null) {
			for(int i = 0; i < m_arrayLongValues.length; i++) {
				long[] vals;
				tt = "Long-Array [" + String.valueOf(i) + "] ";
				tt += CRGRuleGrouperManager.getCriterionTextByIndex(CRGRuleGrouperStatics.DATATYPE_ARRAY_DATE, i);
				vals = m_arrayLongValues[i];
				if(vals != null) {
					for(int j = 0; j < vals.length; j++) {
						tt += "\n\tString [" + String.valueOf(j) + "] \t=\t" + vals[j];
					}
				}
				tt += "\n";
				sb.append(tt);
			}
		}
		if(m_arrayLongtimeValues != null) {
			for(int i = 0; i < m_arrayLongtimeValues.length; i++) {
				long[] vals;
				tt = "Long-Array [" + String.valueOf(i) + "] ";
				tt += CRGRuleGrouperManager.getCriterionTextByIndex(CRGRuleGrouperStatics.DATATYPE_ARRAY_DAY_TIME, i);
				vals = m_arrayLongtimeValues[i];
				if(vals != null) {
					for(int j = 0; j < vals.length; j++) {
						tt += "\n\tString [" + String.valueOf(j) + "] \t=\t" + vals[j];
					}
				}
				tt += "\n";
				sb.append(tt);
			}
		}
		return sb;
	}

    int[] getSortedStartDatesInds() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}

