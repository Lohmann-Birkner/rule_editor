package de.lb.ruleprocessor.checkpoint.ruleGrouper;

import de.lb.ruleprocessor.checkpoint.server.db.OSObject;
import java.io.Serializable;
import java.util.Date;
import java.util.ArrayList;

/**
 * <p>Überschrift: Checkpoint DRG</p>
 *
 * <p>Beschreibung: Vorschläge einer Regel</p>
 *
 * <p>Copyright: Lohmann & Birkner Health Care Consulting GmbH </p>
 *
 * <p>Organisation: Lohmann & Birkner Health Care Consulting GmbH </p>
 *
 * @author unbekannt
 * @version 2.0
 */
public class CRGSuggestion extends Object implements Serializable
{
	int m_criterionType = -1;
	int m_criterionIndex = -1;

	/** übernimmt aus dem CriterumEntry useInSgg - Flag, um zu gestimmen,
	 * 	ob dieser Vorschlag vor oder nach der Entgeltvalidierung anzuwenden ist
	 */
	int m_suggType = 0;

	int m_actionType = -1;

	int m_operantType = -1;
	boolean m_operantCompute = false;
	int m_computeType = 0;

	int m_valueType = -1;
	int m_length = -1;
	int m_arrayLength=-1;

	int m_WildCard=0;
//	int[] m_WildCardArray=null;

	String m_strValue=null;
	int m_intValue=-1;
	double m_doubleValue=-1;
	java.util.Date m_dateValue=null;
	long m_longValue = -1;
	java.util.Date m_datetimeValue=null;
	long m_longtimeValue = -1;
	String[] m_strArrayValue=null;
	int[] m_intArrayValue=null;
	double[] m_doubleArrayValue;
	java.util.Date[] m_dateArrayValue=null;
	long[] m_longArrayValue = null;
	int m_conditionValueType = -1;
	int m_conditionOperantType = -1;
	ArrayList<String>m_strConditionValues = null;

	/** für Kriterien ohne Werte
	 * bezieht sich z.Z. auf MRSA - Kriterien alle Diagnose aus dem Datajahr zufügen
	 * und alle Medikamente löschen
	 */

	int [] m_critToAffectIds = null;
	int[] m_critsToAffectTypes = null;

	String m_criterionText = "";
	String m_operantText = "";
	String m_valueText = "";
	String m_conditionOperantText = "";
	String m_conditionValueText = "";

	/** Array der Kriterienindizies, die bei der Vorschlagsbearbeitung  - Zufügen - müssen überprüft werden
	 * z.B.
	 * bei der Bearbeitung des Entgeltvorschlags, wenn in ein Entgeltart Kriterium für Zufügen
	 * vorgeschlagen wurde, müssen die darauffolgenden Vorschläge auf Zufügen der Entgeltanzahl
	 * und Entgelteinzelbetrags überprüft werden, die als Gruppe vor der weireren Verschlagsauswertung
	 * zu betrachten sind Dieser Array soll in dem führenden Kriterium definiert werden, der
	 * m_indexDepend == 0 hat, die eigen Eigenschaften stehen in m_suggCheckDepend[0];
	 * m_suggCheckDepend [i][0]  - Kriteriumstyp
	 * m_suggCheckDepend [i][1] - Kriteriumsindex
	 * m_suggCheckDepend [i][2] - Defaultwert
	 */
	int[][] m_suggCheckDepend = null;

	String m_suggCheckMethod = null;

	/**
	 * Konstruktor
	 */
	public CRGSuggestion()
	{
	}

	void setCritIdsToAffect(CriterionDepend[] critsToAffect)
	{
		if(critsToAffect == null || critsToAffect.length == 0)
			return;
		int len = critsToAffect.length;
		m_critToAffectIds = new int[len];
		m_critsToAffectTypes = new int[len];
		for(int i = 0; i < len; i++){
			m_critToAffectIds[i] = critsToAffect[i].getCritId();
			m_critsToAffectTypes[i] = critsToAffect[i].getCritType();
		}
	}

	/**
	 * Gibt den Typ der Aktion zurück, welcher durchgeführt werden soll.
	 * <p>
	 * 0 = Löschen
	 * 1 = Hinzufügen
	 * 2 = Ändern
	 * @return int : Type der Aktion
	 */
	public int getActionType(){
		return m_actionType;
	}

	/**
	 * Gibt die Definition des Vorschlages als Text zurück.
	 * <p>
	 * z.B. 'Hauptdiagnose == K52.1'
	 * @return String : Definition des Vorschlages
	 */
	public String getDefinitionText(){
		return (m_criterionText!=null ? m_criterionText+ " " : "")
			 + (m_operantText!=null ? m_operantText+ " " : "")
			 + (m_valueText!=null ? m_valueText : "");
	}

	public Object getSimpleSuggValue()
	{
		switch(m_criterionType){
			case CRGRuleGrouperStatics.DATATYPE_STRING:;
			case CRGRuleGrouperStatics.DATATYPE_ARRAY_STRING: return this.m_strValue;
			case CRGRuleGrouperStatics.DATATYPE_INTEGER:;
			case CRGRuleGrouperStatics.DATATYPE_ARRAY_INTEGER: return this.m_intValue;
			case CRGRuleGrouperStatics.DATATYPE_DOUBLE:;
			case CRGRuleGrouperStatics.DATATYPE_ARRAY_DOUBLE: return this.m_doubleValue;
			case CRGRuleGrouperStatics.DATATYPE_DATE:;
			case CRGRuleGrouperStatics.DATATYPE_ARRAY_DATE: return this.m_dateValue;
			case CRGRuleGrouperStatics.DATATYPE_DAY_TIME:;
			case CRGRuleGrouperStatics.DATATYPE_ARRAY_DAY_TIME: return this.m_datetimeValue;

		}
		return null;
	}

}
