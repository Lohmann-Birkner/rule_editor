package de.checkpoint.server.data.caseRules;




import de.checkpoint.server.db.*;
import de.checkpoint.ruleGrouper.CRGRuleGrouperManager; 

public class DatRuleSuggestion extends OSObject implements DatRuleTerm
{
//	public DatRulesCriterion m_crit = null;
	protected RulesCriterion m_crit = null;
	public String m_val = "";
	public String m_op = "";
	public String m_condition_op = "";
	public String m_condtion_val = "";
	public DatRulesAction m_act = null;

	public DatRuleSuggestion(RulesCriterion crit, String op, String val, DatRulesAction act, String conditionOp, String conditionVal)
	{
		this(crit, op, val, act);
		setCondition(conditionOp, conditionVal);
	}

	public DatRuleSuggestion(RulesCriterion crit, String op, String val, DatRulesAction act)
	{
		super("", false);
		m_crit = crit;
		m_op = op;
		m_val = setValue(val.toUpperCase());
		m_act = act;
	}

	private String setValue(String val)
	{
		String ret_val = "";
		for (int i=0; i<val.length(); i++){
			char c = val.charAt(i);
			if (c == '%')
				ret_val += '*';
			else
				ret_val += c;
		}
		return ret_val;
	}

	public void setCondition(String op, String val)
	{
		m_condition_op = op;
		m_condtion_val = setValue(val.toUpperCase());
	}


        @Override
	public String toString()
	{
		String ret = m_crit == null ? "" :
			(m_act!=null ? m_act.m_displayText : "Ändern")+ " "  + getDisplayText() + " " + m_op + " " + CRGRuleGrouperManager.checkWithFormat(getWorkText(), m_val.replaceAll("'", "")); 
		if(
//                        (ret.contains("Ändern") || ret.contains("Löschen")) &&
                        m_condition_op.length() > 0 || m_condtion_val.length() > 0
                        ){
			ret += " (wenn " + getDisplayText() + " "+ m_condition_op + " " + m_condtion_val.replaceAll("'", "") + ")";
		}
		return ret;
	}

	public String getCitText()
	{
		if (m_crit != null)
			return m_crit.getWorkText();
		else
			return "";
	}

	public String getValue()
	{
		return getValue(m_val);
	}

	public String getValue(String value)
	{
		String val = "";
		for (int i=0; i<value.length(); i++){
			char c = value.charAt(i);
			if (c == '*')
				val += '%';
			else
				val += c;
		}
		return val;
	}

	public String getConditionValue()
	{
		return getValue(m_condtion_val);
	}
        
        public String getConditionOp()
        {   
            return m_condition_op;
        }

	public String getActionText()
	{
		if (this.m_act!=null)
			return m_act.m_workText;
		else
			return "";
	}

	public int getActionID()
	{
		if (this.m_act!=null){
			return m_act.m_id;
		}
		else
			return 0;
	}

	public RulesCriterion getCriterion()
		{
			return m_crit;
		}

	public void setCriterion(RulesCriterion obj)
		{
				m_crit = obj;
		}

    @Override
    public String getCriteriumName() {
        return getWorkText();
    }

    @Override
    public String getOperation() {
        return m_op;
    }

    @Override
    public String getConditionOperation() {
        return this.m_condition_op;
    }

    @Override
    public String getCriteriumValue() {
        return this.getValue().replaceAll("'", "");
    }

    @Override
    public String getConditionCritValue() {
        return this.getConditionValue().replaceAll("'", "");
    }

    @Override
    public boolean isValid() {

        return (!(this.m_act == null

                ||this.m_val.isEmpty()
                ||this.m_crit== null
                ||this.m_op.isEmpty()
                ||(this.m_condition_op.isEmpty() && !this.m_condtion_val.isEmpty())
                ||(!this.m_condition_op.isEmpty() && this.m_condtion_val.isEmpty())))
                || 
                (// Syntax ausnahme: löschen hauptdiagnose braucht keine weiteren op und wert
                    m_act.m_id == 0
                    && getWorkText().equalsIgnoreCase("Hauptdiagnose")
                    && m_val.isEmpty() 
                    && this.m_op.isEmpty() 
                    && (m_condition_op.isEmpty() && m_condtion_val.isEmpty()
                        ||!m_condition_op.isEmpty() && !this.m_condtion_val.isEmpty())
                )
                ;
    }
    
	public String getDisplayText()
	{
		return m_crit == null?"":m_crit.getDisplayText();
	}

	public String getWorkText()
	{
		return m_crit == null?"":m_crit.getWorkText();
	}
    
    
}
