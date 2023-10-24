package de.checkpoint.server.data.caseRules;

import de.checkpoint.ruleGrouper.CRGRuleGrouperStatics;





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
public class DatInterval extends DatRuleRoot
{
	protected DatRulesCriterion m_intervalFromCrit = null;
	protected String m_intervalFromVal = null;
	protected DatRulesCriterion m_intervalToCrit = null;
	protected String m_intervalToVal = null;
        protected String m_intervalAsString = null;

	public DatInterval()
	{
		super(null);

	}


	/**
	 * getFromCrit
	 *
	 * @return String
	 */
	public DatRulesCriterion getFromCrit()
	{
		return m_intervalFromCrit;
	}

	public String getFromVal()
		{
			return m_intervalFromVal;
		}
		public DatRulesCriterion getToCrit(){
			return m_intervalToCrit;
		}
		public String getToVal(){
			return m_intervalToVal;
		}
	public DatInterval(String from, String to)
	{
		this();
		String intervalFromCrit = "";
		String intervalToCrit = "";
		if(from != null && from.length() != 0) {
			String[] vals = from.split(":");
			if(vals.length > 0)
				intervalFromCrit = vals[0];
			else
				intervalFromCrit = "";
			if(vals.length > 1)
				m_intervalFromVal = vals[1];
			else
				m_intervalFromVal = "";
		} else {
			intervalFromCrit = "";
			m_intervalFromVal = "";
		}
		if(to != null && to.length() > 0) {
			String[] vals = to.split(":");
			if(vals.length > 0)
				intervalToCrit = vals[0];
			else
				intervalToCrit = "";
			if(vals.length > 1)
				m_intervalToVal = vals[1];
			else
				m_intervalToVal = "";
		}
		m_intervalFromCrit = (DatRulesCriterion)DatCaseRuleMgr.getIntervalCriterionByWorkText(
							 intervalFromCrit);
		m_intervalToCrit = ((DatRulesCriterion)DatCaseRuleMgr.getIntervalCriterionByWorkText(
						    intervalToCrit));
	}

	public DatInterval(DatRulesCriterion fromCrit, String fromVal, DatRulesCriterion toCrit, String toVal)
	{
		this();
		m_intervalFromCrit = fromCrit;
		m_intervalToCrit = toCrit;
		m_intervalToVal = toVal;
		m_intervalFromVal = fromVal;
	}

	public DatInterval(String fromCrit, String fromVal, String toCrit, String toVal)
	{
		this();
		m_intervalFromCrit = (DatRulesCriterion)DatCaseRuleMgr.getIntervalCriterionByWorkText(
							 fromCrit);
		m_intervalToCrit = ((DatRulesCriterion)DatCaseRuleMgr.getIntervalCriterionByWorkText(
						   toCrit));
		m_intervalToVal = toVal;
		m_intervalFromVal = fromVal;
	}

	public void setIntervalValues(DsrRule rule)
	{
		if(rule != null && rule.hasInterval) {
			m_intervalFromCrit = (DatRulesCriterion)DatCaseRuleMgr.getIntervalCriterionByWorkText(rule.
								 intervalFromCrit);
			m_intervalFromVal = rule.intervalFromVal;
			m_intervalToCrit = ((DatRulesCriterion)DatCaseRuleMgr.getIntervalCriterionByWorkText(
							   rule.intervalToCrit));
			m_intervalToVal = rule.intervalToVal;
		}
	}


	public DatRulesCriterion getIntervalFromCrit()
		{
			return m_intervalFromCrit;
		}

	public String getIntervalFromCritText()
	{
		if(m_intervalFromCrit != null)
			return m_intervalFromCrit.getWorkText();
		else return null;
	}

	public String getIntervalToCritText()
	{
		if(m_intervalToCrit != null)
			return m_intervalToCrit.getWorkText();
		else return null;
	}

	public String getIntervalFromValue()
		{
			return m_intervalFromVal;
		}
	public  DatRulesCriterion getIntervalToCrit()
	{
		return m_intervalToCrit;
	}
	public String getIntervalToValue()
		{
			return m_intervalToVal;
		}

	public String toString(){


	if(m_intervalFromCrit != null && m_intervalFromVal != null && m_intervalToVal !=null) {
		StringBuffer str = new StringBuffer(DatCaseRuleMgr.INTERVAL);
		str.append(" von ");
		try{
			str.append((m_intervalFromCrit.m_displayText==null || m_intervalFromCrit.m_displayText.isEmpty())?m_intervalFromCrit.getWorkText():m_intervalFromCrit.m_displayText);
		}catch(Exception e){
		  /* 3.9.5 2015-09-02 DNi: #FINDBUGS - Exception wird unterdrückt! */
		}
		if(m_intervalFromVal != null && m_intervalFromVal.trim().length() > 0) {
			str.append(": ");
			str.append(m_intervalFromVal);
		}
		str.append(" bis ");
		try {

			str.append((m_intervalToCrit.m_displayText== null||m_intervalToCrit.m_displayText.isEmpty())?m_intervalToCrit.getWorkText():m_intervalToCrit.m_displayText);
		} catch(Exception e) {}
		if(m_intervalToVal != null && m_intervalToVal.trim().length() > 0) {
			str.append(": ");
			str.append(m_intervalToVal);

		}
		return str.toString();

	}
	return "";
}

		public boolean equalsContent(Object obj)
		{
			try{
				return this.toString().equals(((DatInterval)obj).toString());
			}catch(Exception e){
				return false;
			}
		}

	public boolean equals(Object obj)
{
	return equalsContent(obj);
}

        public boolean isValid(){
            return checkIntervalValid(this.getFromCrit(), this.getFromVal())
                    && checkIntervalValid(this.getToCrit(), getToVal());

        }
        private boolean checkIntervalValid(DatRulesCriterion crit, String value){
            if(crit == null){
                if(value == null || value.isEmpty()){
                    return true;
                }else{
                    return false;
                }
            }
            // criterium does not need any value
            if(crit.m_operationType >= CRGRuleGrouperStatics.INDEX_INTERVAL_ADM_DATE 
                    && crit.m_operationType <=CRGRuleGrouperStatics.INDEX_INTERVAL_CURRENT_CASE
                    || crit.m_operationType >= CRGRuleGrouperStatics.INDEX_INTERVAL_TIME_STAMP_DATE_1
                    && crit.m_operationType <= CRGRuleGrouperStatics.INDEX_INTERVAL_NOW) {
                return value == null || value.isEmpty();
            }else{
                // criterium needs value
                return value != null && !value.isEmpty();
            }
        }


        }
