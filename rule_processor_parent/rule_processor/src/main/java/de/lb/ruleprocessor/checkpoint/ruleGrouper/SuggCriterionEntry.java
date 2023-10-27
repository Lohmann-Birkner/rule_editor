package de.lb.ruleprocessor.checkpoint.ruleGrouper;

public class SuggCriterionEntry extends CriterionEntry
{
	CriterionDepend[] m_critsToAffect = null;

	public SuggCriterionEntry(int groupIndex, String workText, String displayText, String descrText,
		 CriterionDepend[]critToAffect )
	{
		super(groupIndex, workText, displayText, descrText, CriterionEntry.CRIT_SUGG_ONLY, CRGRuleGrouperStatics.DATATYPE_UNFORMATTED, CRGRuleGrouperStatics.DATATYPE_UNFORMATTED, false, CRGRuleGrouperStatics.DEPEND_NO);
		m_critsToAffect = critToAffect;
	}


	public SuggCriterionEntry(int groupIndex,  String workText, String displayText, String descrText,
		 int type, int index, CriterionDepend[]critToAffect)
	{
		super(groupIndex,  workText, displayText, descrText,	CriterionEntry.CRIT_SUGG_ONLY, type, index, false,  CRGRuleGrouperStatics.DEPEND_NO);
		m_critsToAffect = critToAffect;
	}

	/**
	 * Dieses Kriterium verlagt keine Operation um in den Vorschlag aufgenommen zu werden.
	 * @return boolean
	 */
	public boolean isNoOperation()
	{
		return true;
	}

	public CriterionDepend[] getM_critsToAffect()
	{
		return m_critsToAffect;
	}
}
