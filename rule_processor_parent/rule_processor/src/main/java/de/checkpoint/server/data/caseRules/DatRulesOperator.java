package de.checkpoint.server.data.caseRules;

public class DatRulesOperator extends DatRulesCriterion{


	public DatRulesOperator(String displayText, String workText, boolean isNested) {
		super(displayText, workText, String.class, "");
		m_isNested = isNested;
	}

	public DatRulesOperator(String workText, boolean isNested) {
		super(workText, String.class, "");
		m_isNested = isNested;
	}

	public boolean equals(Object obj)
	{
		if((obj == null) || (!(obj instanceof DatRulesOperator)))
			return false;
		return m_name.equals(((DatRulesOperator)obj).m_name);
	}

	public String toString()
	{
		return m_displayText;
	}

}
