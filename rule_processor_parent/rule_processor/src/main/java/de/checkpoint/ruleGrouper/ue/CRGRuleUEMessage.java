package de.checkpoint.ruleGrouper.ue;

import de.checkpoint.server.data.caseRules.DatRuleVal;
import java.io.Serializable;

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
public class CRGRuleUEMessage implements Serializable
{
	private DatRuleVal m_ruleVal;
	private CRGUERule m_ruleUE;
	private String m_value;
	private boolean m_isTable;

	public CRGRuleUEMessage(DatRuleVal val, CRGUERule ruleUE, String ueValue, boolean isTable)
	{
		m_ruleVal = val;
		m_ruleUE = ruleUE;
		m_value = ueValue;
		m_isTable = isTable;
	}

	public String toString(){
		return  m_ruleVal.toString()+"("+m_value+")" + ": "+  (m_ruleUE!=null ? m_ruleUE.toString():"");
	}
}
