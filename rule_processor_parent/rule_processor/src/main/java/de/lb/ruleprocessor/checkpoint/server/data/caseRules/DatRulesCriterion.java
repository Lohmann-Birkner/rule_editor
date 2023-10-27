package de.lb.ruleprocessor.checkpoint.server.data.caseRules;

public class DatRulesCriterion extends RulesCriterion {


	public Class m_dataType = null;

	public boolean m_isSingle = true;

	public boolean m_isNested = true;
	public int m_operationType = 0;
	public int m_suggOperationType = 0;
	public String m_tooltipText = "";
	public String m_tooltipSugText = "";
	int toolTipLen = 350;

	public DatRulesCriterion(int opType, String displayText, String workText, Class dataType, String TooltipText) {
		m_displayText = displayText;
		m_name = workText;
		m_dataType = dataType;
		m_operationType = opType;
		m_suggOperationType = opType;
		m_tooltipText = de.lb.ruleprocessor.checkpoint.installer.IAActions.CommonOperations.stringAsHTML(TooltipText,
						toolTipLen);
		m_tooltipSugText = m_tooltipText;

	}

	public DatRulesCriterion(int opType, String displayText, String workText, Class dataType, String []TooltipText) {
		m_displayText = displayText;
		m_name = workText;
		m_dataType = dataType;
		m_operationType = opType;
		m_suggOperationType = opType;
					m_tooltipText = de.lb.ruleprocessor.checkpoint.installer.IAActions.CommonOperations.stringArrayAsHTML(TooltipText,
									toolTipLen);

	}

	public DatRulesCriterion(int opType, String displayText, String workText, Class dataType, boolean isSugg, String []TooltipText) {
		m_displayText = displayText;
		m_name = workText;
		m_dataType = dataType;
		m_operationType = opType;
		m_suggOperationType = opType;
		m_isNested = isSugg;
		m_tooltipText = de.lb.ruleprocessor.checkpoint.installer.IAActions.CommonOperations.stringArrayAsHTML(TooltipText,
									toolTipLen);

	}


	public DatRulesCriterion(String displayText, String workText, Class dataType, String TooltipText) {
		m_displayText = displayText;
		m_name = workText;
		m_dataType = dataType;
		m_tooltipText = de.lb.ruleprocessor.checkpoint.installer.IAActions.CommonOperations.stringAsHTML(TooltipText, toolTipLen);
		m_tooltipSugText = m_tooltipText;
	}

	public DatRulesCriterion(int opType, String displayText, String workText, Class dataType, boolean isSuggestion, String TooltipText) {
		m_displayText = displayText;
		m_name = workText;
		m_dataType = dataType;
		m_isNested = isSuggestion;
		m_operationType = opType;
		m_suggOperationType = opType;
		m_tooltipText = de.lb.ruleprocessor.checkpoint.installer.IAActions.CommonOperations.stringAsHTML(TooltipText, toolTipLen);
		m_tooltipSugText = m_tooltipText;
	}

	public DatRulesCriterion(int opType, String displayText, String workText, Class dataType, boolean isSuggestion, String TooltipText, String SuggToolTipText) {
		m_displayText = displayText;
		m_name = workText;
		m_dataType = dataType;
		m_isNested = isSuggestion;
		m_operationType = opType;
		m_suggOperationType = opType;
		m_tooltipText = de.lb.ruleprocessor.checkpoint.installer.IAActions.CommonOperations.stringAsHTML(TooltipText, toolTipLen);
		m_tooltipSugText = de.lb.ruleprocessor.checkpoint.installer.IAActions.CommonOperations.stringAsHTML(SuggToolTipText, toolTipLen);
	}

	public DatRulesCriterion(int opType, String displayText, String workText, boolean isSingle, Class dataType, boolean isSuggestion, String TooltipText) {
		m_displayText = displayText;
		m_name = workText;
		m_dataType = dataType;
		m_isNested = isSuggestion;
		m_isSingle = isSingle;
		m_operationType = opType;
		m_suggOperationType = opType;
		m_tooltipText = de.lb.ruleprocessor.checkpoint.installer.IAActions.CommonOperations.stringAsHTML(TooltipText, toolTipLen);
		m_tooltipSugText = m_tooltipText;

	}

	public DatRulesCriterion(int opType, String workText, Class dataType, String TooltipText) {
		m_displayText = workText;
		m_name = workText;
		m_dataType = dataType;
		m_operationType = opType;
		m_suggOperationType = opType;
		m_tooltipText = de.lb.ruleprocessor.checkpoint.installer.IAActions.CommonOperations.stringAsHTML(TooltipText, toolTipLen);
		m_tooltipSugText = m_tooltipText;
	}

	public DatRulesCriterion(String workText, Class dataType, String TooltipText) {
		m_displayText = workText;
		m_name = workText;
		m_dataType = dataType;
		m_tooltipText = de.lb.ruleprocessor.checkpoint.installer.IAActions.CommonOperations.stringAsHTML(TooltipText, toolTipLen);
		m_tooltipSugText = m_tooltipText;
	}

	public DatRulesCriterion(int opType, String workText, Class dataType, boolean isSuggestion, String TooltipText) {
		m_displayText = workText;
		m_name = workText;
		m_dataType = dataType;
		m_isNested = isSuggestion;
		m_operationType = opType;
		m_suggOperationType = opType;
		m_tooltipText = TooltipText;
		m_tooltipText = de.lb.ruleprocessor.checkpoint.installer.IAActions.CommonOperations.stringAsHTML(TooltipText, toolTipLen);
		m_tooltipSugText = m_tooltipText;
	}

	public DatRulesCriterion(int opType, String workText, boolean isSingle, Class dataType, String TooltipText) {
		m_isSingle = isSingle;
		m_displayText = workText;
		m_name = workText;
		m_dataType = dataType;
		m_operationType = opType;
		m_suggOperationType = opType;
		m_tooltipText = de.lb.ruleprocessor.checkpoint.installer.IAActions.CommonOperations.stringAsHTML(TooltipText, toolTipLen);
		m_tooltipSugText = m_tooltipText;
	}

	public DatRulesCriterion(int opType, int suggType, String workText, boolean isSingle, Class dataType, String TooltipText) {
		m_isSingle = isSingle;
		m_displayText = workText;
		m_name = workText;
		m_dataType = dataType;
		m_operationType = opType;
		m_suggOperationType = suggType;
		m_tooltipText = de.lb.ruleprocessor.checkpoint.installer.IAActions.CommonOperations.stringAsHTML(TooltipText, toolTipLen);
		m_tooltipSugText = m_tooltipText;
	}

	public DatRulesCriterion(int opType, String workText, boolean isSingle, Class dataType, boolean isSuggestion, String TooltipText) {
		m_isSingle = isSingle;
		m_displayText = workText;
		m_name = workText;
		m_dataType = dataType;
		m_isNested = isSuggestion;
		m_operationType = opType;
		m_suggOperationType = opType;
		m_tooltipText = de.lb.ruleprocessor.checkpoint.installer.IAActions.CommonOperations.stringAsHTML(TooltipText, toolTipLen);
		m_tooltipSugText = m_tooltipText;
	}

	public String toString(){
		return m_displayText;
	}

	public DatRulesCriterion createCopy(){
		return new DatRulesCriterion(m_operationType, m_displayText, m_name, m_dataType, m_isNested, m_tooltipText, m_tooltipSugText);
	}
	public boolean equals(Object obj)
{
    /* 3.9.5 2015-09-01 DNi: #FINDBUGS - Potenzielle NP-Exception, weil obj != null nicht abgefragt wird! */
	return this.toString().equals(obj.toString());
}

}
