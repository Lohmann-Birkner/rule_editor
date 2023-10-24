package de.checkpoint.ruleGrouper;

import de.checkpoint.server.data.caseRules.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class CriterionEntry extends RulesCriterion
{
	public static final int CRIT_RULE_ONLY = 0;
	public static final int CRIT_SUGG_ONLY = 1;
	public static final int CRIT_RULE_AND_SUGG = 2;
	public static final int CRIT_RULE_AND_SUGG_AFTER_FEE_VALIDATION = 3;
	public static final int CRIT_RULE_METHOD = 4;
	public static final int CRIT_RULE_ASSISTANT_TO_METHOD = 5; // diese Kriterien erscheinen nicht in der Regelbaum, sind mit einem Method verbunden
	/**
	 * Flag, der wird auf true gesetzt, wenn das Kriterium ein summierendes Kriterium ist
	 */
	boolean m_isCummulative = false;
	/**
	 * bei kummulierten Kriterien index eines Kriteriens, für den die Werte für Intervall gesammelt werden z.B. für
	 * das Kriterium GesamtPreisFuerPZN m_cummulateBase - Index des Kriteriens PZN
	 */
	int m_indCummulateBase = -1;
	/**
	 * bei kummulierten Kriterien index eines Kriteriens, dessen Werte werden gesammelt. Z.B. für
	 * das Kriterium GesamtPreisFuerPZN m_cummulateWhat - Index des Kriteriens Einzellpreis
	 */
	int m_indCummulateWhat = -1;
	/**
	 * bei kummulierten Kriterien index eines Kriteriens, dessen Wert mit dem entsprechenden Wert auf
	 * m_cummulateWhat multipliziert wird. Z.B. für
	 * das Kriterium GesamtPreisFuerPZN m_cummulateNumber - Index des Kriteriens Anzahl für PZN
	 */
	int m_indCummulateNumber = -1;
	/**
	 * bei kummulierten Kriterien index eines Kriteriens, der speichert das Datum des entsprechenden Eintrages. Z.B für
	 * das Kriterium GesamtPreisFuerPZN m_indCummulateDate ist Index des Kriteriens Verschreibungsdatum
	 */
	int m_indCummulateDate = -1;

/**
	 * Array der Stringkriterientexten
	 */
	//m_isDependde kann überall noch weg?! volker
/**
	 * Typ der Regelausprägung(String, datum und s.w.)
	 */
	int m_type = -1;
        int m_type_show = -1;
/**
	 * Kriterium kann auch in den Regelvorschlägen benutzt werden
	 */
	protected int m_usedInSugg = CRIT_RULE_ONLY;
/**
	 * Erklärungstext
	 */
	String m_descriptionText = "";
        String[] m_descriptionTextBase = null;
/**
	 * index der Kriteriengruppe
	 */
	int m_groupCPIndex = 0;
	boolean m_hastInterval = false;
        String[] m_toolTip = null;
        
        String[] m_toolTipBasic = null;
        Object[][] m_critValuesBasic = null;
        
        protected int m_method_dependency_index = -1; // für MethodCriterionEntry wird z.B. das Datums - Array zu dem OPS - Array in Verbindung gesetzt
        protected int m_method_dependency_type = -1;

/**
	 * index der Kriteriumsgruppe, wenn die Kriteriumsgroupen unreteilt sind(aus Lizens nicht cp und nicht fm)
	 */
	int m_groupStructIndex = 0;
	int m_index;
	// index für TimeStamp bei intervalüberprüfung
	int m_dependCritCheckIndex = -1;
	// index für den indexkriterium bei intervalüberprüfungz.B. bei mehreren Fällen oder Berichten
	int m_dependIndex = -1;
	int m_dependGroupIndex = -1;
	boolean m_isDepended = false;
	int m_depend = -1;
	boolean m_isCrossCase = false;
/*      ein Flag der wird auf true gesetzt, wenn TooltipArray erweitert wurde. Z.z. wird nur einmalige erweiterung erlaubt.
**        Benutzt wird nur bei erweitern des Tooltip für Entlassungsgrund wenn PEPP erlaubt ist
*/
        boolean m_addedAdditional = false; 
/**
	 * Liste der Ausprägungen, in Combobox anzubieten
	 */
	Object[][] critValues = null;
/**
	 * max Länge der Zeile bei Umwandlung des Textes in HTML
	 */
	protected int toolTipLen = 2048; //350;
        
        public CriterionEntry(int groupIndex, String workText, String displayText, String descrText, int type, int index, int usage){
 		m_name = workText;
		m_displayText = displayText;
                m_descriptionTextBase = removeXML(descrText);
		m_descriptionText = de.checkpoint.installer.IAActions.CommonOperations.stringAsHTML(descrText,
							toolTipLen);
		m_type = type;
                m_type_show = m_type;
		m_index = index;           
        }

   public CriterionEntry(int groupIndex, int structIndex, String name, int type, int index, boolean isDepended,
		int depend)
	{
		this(groupIndex, structIndex, name, name, name, type, index, isDepended, depend, -1);
	}

	public CriterionEntry(int groupIndex, String workText, String displayText, String descrText, int type, int index,
		boolean isDepended, int depend)
	{
		this(groupIndex, groupIndex, workText, displayText, descrText, type, index, isDepended, depend, -1);
	}

	public CriterionEntry(int groupIndex, String workText, String displayText, String descrText, int type, int index,
		boolean isDepended, int depend, int checkDateIndex, int indCummulateBase, int indCummulateWhat, int indCummulateNumber, int indCummulateDate)
	{
		this(groupIndex, groupIndex, workText, displayText, descrText, type, index, isDepended, depend, checkDateIndex);
		this.m_isCummulative = true;
		this.m_indCummulateBase = indCummulateBase;
		this.m_indCummulateWhat = indCummulateWhat;
		this.m_indCummulateNumber = indCummulateNumber;
		this.m_indCummulateDate = indCummulateDate;
	}

	public CriterionEntry(int groupIndex, String workText, String displayText, Object[][] descrText, int type, int index,
			boolean isDepended, int depend, int checkDateIndex)
	{
		this(groupIndex, groupIndex, workText, displayText, descrText, type, index, isDepended, depend);
		m_dependCritCheckIndex = checkDateIndex;

	}

	public CriterionEntry(int groupIndex, String workText, String displayText, String descrText, int type, int index,
				boolean isDepended, int depend, int checkDateIndex)
	{
		this(groupIndex, groupIndex, workText, displayText, descrText, type, index, isDepended, depend, checkDateIndex);
	}

	public CriterionEntry(int groupIndex, String workText, String displayText, String descrText, int type, int index,
		boolean isDepended, int depend, int checkDateIndex, int dependIndex, int showType)
        {
            this(groupIndex, workText, displayText, descrText, type, index,
		isDepended, depend, checkDateIndex, dependIndex);
            m_type_show = showType;
        }
        
	public CriterionEntry(int groupIndex, String workText, String displayText, String descrText, int type, int index,
		boolean isDepended, int depend, int checkDateIndex, int dependIndex)
	{
		this(groupIndex, groupIndex, workText, displayText, descrText, type, index, isDepended, depend, checkDateIndex);
		m_dependIndex = dependIndex;
	}

	public CriterionEntry(int groupIndex, int structIndex, String workText, String displayText, String descrText,
		int type, int index, boolean isDepended, int depend, int checkDateIndex)
	{
		m_name = workText;
		m_displayText = displayText;
                m_descriptionTextBase = removeXML(descrText);
		m_descriptionText = de.checkpoint.installer.IAActions.CommonOperations.stringAsHTML(descrText,
							toolTipLen);
		m_type = type;
                m_type_show = m_type;
		m_index = index;
		m_isDepended = isDepended;

                m_depend = depend;
                m_isCrossCase = false;
		
		m_groupCPIndex = groupIndex;
		m_groupStructIndex = structIndex;
		m_dependCritCheckIndex = checkDateIndex;
	}

	public CriterionEntry(int groupIndex, int structIndex, String workText, String displayText, String descrText)
	{
		this(groupIndex, structIndex, workText, displayText, descrText, -1, -1, false, -1, -1);
	}

	public CriterionEntry(int groupIndex, int structIndex, String workText, String displayText, String descrText,
		int useSugg, int type, int index, boolean isDepended, int depend, int dependCheckIndex,
		int indCummulateBase, int indCummulateWhat, int indCummulateNumber, int indCummulateDate)
	{
		this(groupIndex, structIndex, workText, displayText, descrText,	useSugg, type, index, isDepended, depend, dependCheckIndex);
		this.m_isCummulative = true;
		this.m_indCummulateBase = indCummulateBase;
		this.m_indCummulateWhat = indCummulateWhat;
		this.m_indCummulateNumber = indCummulateNumber;
		this.m_indCummulateDate = indCummulateDate;
	}

	public CriterionEntry(int groupIndex, int structIndex, String workText, String displayText, String descrText,
		int useSugg, int type, int index, boolean isDepended, int depend, int dependCheckIndex)
	{
		this(groupIndex, structIndex, workText, displayText, descrText,	useSugg, type, index, isDepended, depend);
		m_dependCritCheckIndex = dependCheckIndex;
	}

	public CriterionEntry(int groupIndex, int structIndex, String workText, String displayText, String descrText,
		int useSugg, int type, int index, boolean isDepended, int depend, int dependCheckIndex, int dependIndex)
	{
		this(groupIndex, structIndex, workText, displayText, descrText,	useSugg, type, index, isDepended, depend);
		m_dependCritCheckIndex = dependCheckIndex;
		m_dependIndex = dependIndex;
	}

	public CriterionEntry(int groupIndex, int structIndex, String workText, String displayText, String descrText,
		int useSugg, int type, int index, boolean isDepended, int depend, int dependCheckIndex, int dependIndex, int dependGroupIndex,
                int showType)
        {
            this(groupIndex, structIndex, workText, displayText, descrText,
		useSugg, type, index, isDepended, depend, dependCheckIndex, dependIndex, dependGroupIndex);
            m_type_show = showType;
        }
        
	public CriterionEntry(int groupIndex, int structIndex, String workText, String displayText, String descrText,
		int useSugg, int type, int index, boolean isDepended, int depend, int dependCheckIndex, int dependIndex, int dependGroupIndex)
	{
		this(groupIndex, structIndex, workText, displayText, descrText,	useSugg, type, index, isDepended, depend);
		m_dependCritCheckIndex = dependCheckIndex;
		m_dependIndex = dependIndex;
		m_dependGroupIndex = dependGroupIndex;
	}

	public CriterionEntry(int groupIndex, int structIndex, String workText, String displayText, String descrText,
		int useSugg, int type, int index, boolean isDepended, int depend)
	{
		this(groupIndex, structIndex, workText, displayText, "", type, index, isDepended, depend, -1);
		m_descriptionText = de.checkpoint.installer.IAActions.CommonOperations.stringAsHTML(descrText,
							toolTipLen);
                m_descriptionTextBase =  removeXML(descrText);
		m_usedInSugg = useSugg;
	}

	public CriterionEntry(int groupIndex, String workText, String displayText, Object[][] descrText,
		int useSugg, int type, int index, boolean isDepended, int depend, int dependCheckIndex)
	{
		this(groupIndex, groupIndex, workText, displayText, descrText, type, index, isDepended, depend);
		m_usedInSugg = useSugg;
		this.m_dependCritCheckIndex = dependCheckIndex;

	}

	public CriterionEntry(int groupIndex, String workText, String displayText, Object[][] descrText,
	int useSugg, int type, int index, boolean isDepended, int depend, int dependCheckIndex, int dependIndex)
	{
		this(groupIndex, workText, displayText, descrText, useSugg, type, index, isDepended, depend, dependCheckIndex);
		m_dependIndex = dependIndex;
	}

	public CriterionEntry(int groupIndex, int structIndex, String workText, String displayText, Object[][] descrText,
		int useSugg, int type, int index, boolean isDepended, int depend)
	{
		this(groupIndex, structIndex, workText, displayText, descrText, type, index, isDepended, depend);
		m_usedInSugg = useSugg;

	}

	public CriterionEntry(int groupIndex, int structIndex, String workText, String displayText, Object[][] descrText,
		int useSugg, int type, int index, boolean isDepended, int depend, int checkIndex, int dependIndex)
	{
		this(groupIndex, structIndex, workText, displayText, descrText, useSugg, type, index, isDepended, depend);
		m_dependCritCheckIndex = checkIndex;
		this.m_dependIndex = dependIndex;
	}

	public CriterionEntry(int groupIndex, int structIndex, String workText, String displayText, Object[][] descrText,
		int useSugg, int type, int index, boolean isDepended, int depend, int checkIndex, int dependIndex, int dependGroupIndex)
	{
		this(groupIndex, structIndex, workText, displayText, descrText, useSugg, type, index, isDepended, depend, checkIndex, dependIndex);
		m_dependGroupIndex = dependGroupIndex;
	}

	public CriterionEntry(int groupIndex, int structIndex, String workText, String displayText, Object[][] descrText,
		int type, int index, boolean isDepended, int depend)
	{
		this(groupIndex, structIndex, workText, displayText, "", type, index, isDepended, depend, -1);
		m_toolTipBasic = new String[descrText.length];
		m_critValuesBasic = new Object[descrText.length - 1][2];
		for(int i = 0; i < descrText.length; i++) {
			m_toolTipBasic[i] = (String)descrText[i][1];
			if(i > 0) {
				m_critValuesBasic[i - 1][0] = descrText[i][0];
				m_critValuesBasic[i - 1][1] = descrText[i][1];
			}
		}
               m_descriptionTextBase = m_toolTipBasic;

               m_toolTip = m_toolTipBasic;
                critValues = m_critValuesBasic;
		m_descriptionText = de.checkpoint.installer.IAActions.CommonOperations.stringArrayAsHTML(m_toolTip,
							toolTipLen);
 	}

	public CriterionEntry(int groupIndex, int structIndex, String workText, String displayText, String[] descrText,
		int type, int index, boolean isDepended, int depend)
	{
		this(groupIndex, structIndex, workText, displayText, "", type, index, isDepended, depend, -1);
                m_descriptionTextBase = descrText;
		m_descriptionText = de.checkpoint.installer.IAActions.CommonOperations.stringArrayAsHTML(descrText,
							toolTipLen);
	}

	public CriterionEntry(int groupIndex, int structIndex, String workText, String displayText, String[] descrText,
		int useSugg, int type, int index, boolean isDepended, int depend)
	{
		this(groupIndex, structIndex, workText, displayText, "", type, index, isDepended, depend, -1);
		m_descriptionText = de.checkpoint.installer.IAActions.CommonOperations.stringArrayAsHTML(descrText,
							toolTipLen);
                m_descriptionTextBase = descrText;
		m_usedInSugg = useSugg;
	}

	public CriterionEntry(int groupIndex, String name, int type, int index, boolean isDepended, int depend)
	{
		this(groupIndex, groupIndex, name, name, name, type, index, isDepended, depend, -1);
	}

	public CriterionEntry(int groupIndex, String workText, String displayText, String descrText)
	{
		this(groupIndex, groupIndex, workText, displayText, descrText);
	}

	public CriterionEntry(int groupIndex, String workText, String displayText, String descrText, int useSugg,
		int type, int index, boolean isDepended, int depend)
	{
		this(groupIndex, groupIndex, workText, displayText, descrText, useSugg, type, index, isDepended, depend);
	}

	public CriterionEntry(int groupIndex, String workText, String displayText, Object[][] descrText, int useSugg,
		int type, int index, boolean isDepended, int depend)
	{
		this(groupIndex, groupIndex, workText, displayText, descrText, useSugg, type, index, isDepended, depend);

	}

	public CriterionEntry(int groupIndex, String workText, String displayText, Object[][] descrText, int type,
		int index, boolean isDepended, int depend)
	{
		this(groupIndex, groupIndex, workText, displayText, descrText, type, index, isDepended, depend);
	}

	public CriterionEntry(int groupIndex, String workText, String displayText, String[] descrText, int type, int index,
		boolean isDepended, int depend)
	{
		this(groupIndex, groupIndex, workText, displayText, descrText, type, index, isDepended, depend);
	}

	public CriterionEntry(int groupIndex, String workText, String displayText, String[] descrText, int useSugg,
		int type, int index, boolean isDepended, int depend)
	{
		this(groupIndex, groupIndex, workText, displayText, descrText, useSugg, type, index, isDepended, depend);
	}

	// Getter Methoden

 
	public String getDescriptionText()
	{
		return m_descriptionText;
	}

	public void setDisplayText(String descr)
	{
		m_displayText = descr;
	}

	public String getDescriptionText(boolean isSugg)
	{
		return m_descriptionText;
	}

	public int getType()
	{
		return m_type;
	}

	public int getShowType()
	{
		return m_type_show;
	}

	public Object[][] getCritValues()
	{
		return critValues;
	}

	public int getM_index()
	{
		return m_index;
	}

	public int getM_usedInSugg()
	{
		return m_usedInSugg;
	}

	public SelectCritValues getCritValueToCritWorkText(String critWorkText)
	{
		if(critValues == null)
			return null;
		Object critValue = null;
		try {
			switch(m_type) {
				case CRGRuleGrouperStatics.DATATYPE_INTEGER:
					critValue = new Integer(Integer.parseInt(critWorkText));
					break;
				default:
					critValue = critWorkText;
					break;
			}
		} catch(Exception e) {
//			return null;
		}

		for(int i = 0; i < critValues.length; i++) {
			if(critValues[i][0].equals(critValue))
				return new SelectCritValues(critValues[i][0], (String)critValues[i][1]);
		}
		return null;
	}

	public boolean isInSuggs()
	{
		return m_usedInSugg !=CRIT_RULE_ONLY;
	}

	public boolean isInRules()
	{
		return m_usedInSugg !=CRIT_SUGG_ONLY;
	}

	public boolean isForAfterFeeValidation()
	{
		return m_usedInSugg == CRIT_RULE_AND_SUGG_AFTER_FEE_VALIDATION;
	}

	public int compareTo(Object sr2)
	{
		if(sr2 instanceof CriterionEntry)
			return this.getDisplayText().toUpperCase().compareTo(((CriterionEntry)sr2).getDisplayText().toUpperCase());
		else
			return 0;
	}

	public int getStructGroupIndex()
	{
		return m_groupStructIndex;
	}

	public boolean isNoOperation()
	{
		return false;
	}

	public void setDescriptionText(String text)
	{
//                m_descriptionTextBase = removeXML(text);
		m_descriptionText = de.checkpoint.installer.IAActions.CommonOperations.stringAsHTML(text,
							toolTipLen);

	}

	public void setDescriptionText(String []text)
{
	m_descriptionText = de.checkpoint.installer.IAActions.CommonOperations.stringArrayAsHTML(text,
						toolTipLen);
        m_descriptionTextBase = text;
}
        public boolean isMethod()
        {
            return m_usedInSugg == CRIT_RULE_METHOD;
        }
        
        /**
         * die zustzlichen Kriteriumsausprägungen werden in die Ausprägungsliste, die bei der erzeugung statischen 
         * Kriterienobjekten angelegt wurde, wird zugefügt. Dies geschieht z.Z. nur bei dem Kriterium Entlassungsgrund, dessen
         * Ausprägungen von der Lizenz abhängig sind
         * @param additionalDescript 
         */
        public void addToolTipsFromArray(Object[][] additionalDescript)
        {
            if(additionalDescript == null || additionalDescript.length == 0)
                return;
            if(m_addedAdditional)
                return;
            m_addedAdditional = true;

                List <Object[]>critValuesList = new ArrayList<>();
                if(m_critValuesBasic != null){
                    for(int i = 0; i < m_critValuesBasic.length; i++){
                        Object obj[] = new Object[2];
                        obj[0] = m_critValuesBasic[i][0];
                        obj[1] = m_critValuesBasic[i][1];
                        critValuesList.add(obj);
                    }
                }
		for(int i = 0; i < additionalDescript.length; i++) {

                        Object obj[] = new Object[2];
                        obj[0] = additionalDescript[i][0];;
                        obj[1] = additionalDescript[i][1];
                        critValuesList.add(obj);
			
		}
                
                Collections.sort(critValuesList, new Comparator(){
                @Override
                public int compare(Object o1, Object o2) {
                    if(o1 instanceof Object[] && o2 instanceof Object[]){
                        Object[]obj1 = (Object[])o1;
                        Object[]obj2 = (Object[])o2;
                        if(obj1[0] instanceof Integer && obj2[0] instanceof Integer){
                            return ( (Integer)obj1[0]).compareTo( (Integer)obj2[0]);
                        }
                        else{
                            return obj1[0].toString().compareTo(obj2[0].toString());
                        }

                    }
                    return 0;
                }
                    
                });
                
                m_toolTip = new String[critValuesList.size()];
                critValues = new Object[critValuesList.size()][2];

                for(int i = 0; i < critValuesList.size(); i++){
                    Object[]obj = critValuesList.get(i);
			m_toolTip[i] = (String)obj[1];
                        critValues[i][0] = obj[0];
                        critValues[ i][1] = obj[1];
                }
		m_descriptionText = de.checkpoint.installer.IAActions.CommonOperations.stringArrayAsHTML(m_toolTip,
							toolTipLen);

        }
        
        public void setBasicToolTipsArray()
        {
            if(m_addedAdditional){
                m_toolTip = m_toolTipBasic;
                critValues = m_critValuesBasic;
		m_descriptionText = de.checkpoint.installer.IAActions.CommonOperations.stringArrayAsHTML(m_toolTip,
							toolTipLen);
                m_addedAdditional = false;
            }
        }

        public Object[][] getM_critValuesBasic(){
            return m_critValuesBasic;
        }
        
    public String[] getM_descriptionTextBase(){
        return m_descriptionTextBase;
    }

    private String[] removeXML(String descrText) {
        if(descrText == null || descrText.length() == 0){
            return null;
        }
        String[] brParts = descrText.split("<br>");
        if(brParts.length > 1){
            return brParts;
        }
        if(descrText.contains(":<ul><li>")){
            ArrayList<String> parts = new ArrayList<>();
            String lines[] = descrText.split(":<ul><li>");
            parts.add(lines[0]);
            if(lines[1].endsWith("</li></ul>")){
                lines[1] = lines[1].substring(0,lines[1].length() - String.valueOf("</li></ul>").length());
            }
            if(lines[1].contains("</li><li>")){
                lines = lines[1].split("</li><li>");
                for(String str: lines){
                    parts.add(str);
                }
                String[] ret = new String[parts.size()];
                parts.toArray(ret);
                return ret;
            }else{
               return lines;
            }
        }else{
            String[] ret = new String[1];
            ret[0] = descrText;
            return ret;
        }
        
    }

    public boolean isM_addedAdditional() {
        return m_addedAdditional;
    }

    public int getM_groupCPIndex() {
        return m_groupCPIndex;
    }

    public boolean isM_isDepended() {
        return m_isDepended;
    }

    public int getM_dependCritCheckIndex() {
        return m_dependCritCheckIndex;
    }

    public int getM_dependIndex() {
        return m_dependIndex;
    }

    public int getM_dependGroupIndex() {
        return m_dependGroupIndex;
    }

    public boolean isM_isCummulative() {
        return m_isCummulative;
    }

    public int getM_indCummulateBase() {
        return m_indCummulateBase;
    }

    public int getM_indCummulateWhat() {
        return m_indCummulateWhat;
    }

    public int getM_indCummulateNumber() {
        return m_indCummulateNumber;
    }

    public int getM_indCummulateDate() {
        return m_indCummulateDate;
    }

    public boolean isM_isCrossCase() {
        return m_isCrossCase;
    }

    public int getM_depend() {
        return m_depend;
    }
    
    
}
