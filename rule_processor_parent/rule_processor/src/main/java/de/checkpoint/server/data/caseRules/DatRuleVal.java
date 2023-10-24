package de.checkpoint.server.data.caseRules;


import de.checkpoint.ruleGrouper.CriterionEntry;
import de.checkpoint.ruleGrouper.CRGRule;
import de.checkpoint.ruleGrouper.CRGRuleGrouperManager;
import java.util.Objects;


/**
 * <p>Überschrift: Checkpoint DRG</p>
 *
 * <p>Beschreibung: Fallmanagement DRG</p>
 *
 * <p>Copyright: </p>
 *
 * <p>Organisation: </p>
 *
 * @author not attributable
 * @version 2.0
 */
public class DatRuleVal extends DatRuleRoot implements DatRuleTerm
{
	public String m_crit = "";
	public String m_not = "false";
	public String m_op = "";
	public String m_val = "";
        public String m_method = "";
        public String m_parameter = "";
	public boolean m_isMed = false;
	public boolean m_isSole = false;

	public boolean m_isNot = false;
	DatInterval m_interval = new DatInterval();
	public boolean m_hasInterval = false;
        public boolean m_isMethod = false;

	public DatRuleVal(DatRuleElement parent)
	{
		super(parent);
	}

	public DatRuleVal(String crit, String not, String op, String val, DatRuleElement parent)
	{
		super(parent);
		m_crit = crit;
		m_not = not;
		m_op = op;
		m_val = val;
		if(m_not.equals("true")) {
			m_isNot = true;
		} else {
			m_isNot = false;
		}
	}

	public DatRuleVal(String crit, String not, String op, String val, DatRuleElement parent, boolean isMed,
		boolean isSole)
	{
		super(parent);
		m_crit = crit;
		m_not = not;
		m_op = op;
		m_val = val;
		m_isMed = isMed;
		m_isSole = isSole;
		if(m_not.equals("true")) {
			m_isNot = true;
		} else {
			m_isNot = false;
		}
	}

	public DatRuleVal(String crit, String not, String op, String val, DatRuleElement parent, boolean isMed,
		boolean isSole, DatInterval interval, String method, String parameter)
        {
            this(crit, not, op, val, parent, isMed, isSole, interval);
            m_method = method;
            m_parameter = parameter;
            m_isMethod = m_method != null && m_method.length() > 0;
        }
        
        
	public DatRuleVal(String crit, String not, String op, String val, DatRuleElement parent, boolean isMed,
		boolean isSole, DatInterval interval)
	{
		this(crit, not, op, val, parent, isMed, isSole);
		if(interval != null) {
			m_interval = interval;
			m_hasInterval = true;
		}
	}

        @Override
	public String toString()
	{
		String val = this.getCriteriumValue().replaceAll("%", "*");
		String str = "";
                if(m_method.length() == 0){
                    str = m_crit + " " + m_op + " " + CRGRuleGrouperManager.checkWithFormat(m_crit, val);  
                }else{
                    str = m_method + "(" + m_parameter + ")";
                }
		String interval = m_interval.toString();
		if(m_isNot) {
			str = "not (" + str + ")";
		}
		if(m_hasInterval && interval.length() > 0) {
			str += " ( " + interval + " )";
		}
                if(m_mark != null && !m_mark.isEmpty()){
                    str = m_mark + ": " + str; 
                }
		return str;
	}

	public boolean equalsContent(Object obj)
	{
		if((obj == null) || !(obj instanceof DatRuleVal)) {
			return false;
		}
		String val = m_val.replaceAll("%", "*");
		val = val.toUpperCase();
		val = val.replaceAll("'", "");
		String objVal = ((DatRuleVal)obj).m_val.replaceAll("%", "*");
		objVal = objVal.toUpperCase();
		objVal = objVal.replaceAll("'", "");
                String objMethod = ((DatRuleVal)obj).m_method;
                String objParam = ((DatRuleVal)obj).m_parameter;
		return(m_isNot == ((DatRuleVal)obj).m_isNot) && m_crit.equals(((DatRuleVal)obj).m_crit) &&
			(val.equals(objVal) && m_op.equals(((DatRuleVal)obj).m_op) 
                        && m_method.equalsIgnoreCase(objMethod) && m_parameter.equalsIgnoreCase(objParam))&&
			m_interval.equalsContent(((DatRuleVal)obj).m_interval);

	}

	public void setInterval(DatInterval interval)
	{
		m_hasInterval = true;
		m_interval = interval;

	}

	public boolean hasInterval()
	{
		return this.m_hasInterval;
	}

	public DatInterval getInterval()
	{
		return m_interval;
	}

	public void resetInterval()
	{
		m_hasInterval = false;
		m_interval = new DatInterval();
	}

	public boolean hasCritTypes(int flag) throws Exception
	{
//		CriterionEntry entry = CRGRuleGrouperManager.getCriterionByWorkText(m_crit);
//		if(entry == null) {
//			return false;
//		}
//		switch(flag) {
//			case CRGRule.CHECK_CP_ONLY:
//				return CRGRuleGrouperManager_old.isCPEntry(entry);
//			case CRGRule.CHECK_MRSA_ONLY:
//				return CRGRuleGrouperManager_old.isRSAEntry(entry);
//			case CRGRule.CHECK_ACG_ONLY:
//				return CRGRuleGrouperManager_old.isACGEntry(entry);
//			case CRGRule.CHECK_AMBU_MED_ONLY:
//				return CRGRuleGrouperManager_old.isAMBUMedEntry(entry);
//			case CRGRule.CHECK_AMBU_SOLE_ONLY:
//				return CRGRuleGrouperManager_old.isAMBUSoLeEntry(entry);
//			case CRGRule.CHECK_KH_ONLY:
//				return CRGRuleGrouperManager_old.isKHEntry(entry);
//			case CRGRule.CHECK_INS_ONLY:
//				return CRGRuleGrouperManager_old.isInsEntry(entry);
//			case CRGRule.CHECK_CP_MED_ONLY:
//				return CRGRuleGrouperManager_old.isCPMedEntry(entry);
//			case CRGRule.CHECK_AMBU_CARE_ONLY:
//				return CRGRuleGrouperManager_old.isAmbuCareEntry(entry);
//                        case CRGRule.CHECK_NEEDS_GROUPING:
//                            return CRGRuleGrouperManager_old.isNeedsGroupingEntry(entry);
//
//		}
//		return false;
            return true;
	}
        
        public void copyValues(DatRuleVal oldVal)
        {
            m_crit = oldVal.m_crit;
            m_isNot = oldVal.m_isNot;
            m_not = oldVal.m_not;
            m_op = oldVal.m_op;
            m_val = oldVal.m_val;
            m_method = oldVal.m_method;
            m_parameter = oldVal.m_parameter;
            m_hasInterval = oldVal.m_hasInterval;
            m_interval = oldVal.m_interval;
            m_isMethod = oldVal.m_isMethod;
           m_isMed = oldVal.m_isMed;
           m_isSole = oldVal.m_isSole;
        }
        

        @Override
    public int hashCode(){
        return Objects.hash(m_crit, m_not, m_op,m_val);
    }

    @Override
    public String getCriteriumName() {
        return m_crit;
    }

    @Override
    public String getOperation() {
        return m_op;
    }

    @Override
    public String getCriteriumValue() {
        return CRGRuleGrouperManager.checkWithFormat(m_crit, m_val.replaceAll("'", ""));
    }

    @Override
    public String getConditionOperation() {
       return "";
    }

    @Override
    public String getConditionCritValue() {
        return "";
    }

    @Override
    public boolean isValid() {
        return (m_hasInterval && this.m_interval.isValid() || !m_hasInterval)
                &&(!m_crit.isEmpty() && !m_op.isEmpty() && !m_val.isEmpty() // alle befüllt
                || !m_crit.isEmpty() && m_op.isEmpty() && m_val.isEmpty() // nur crit
                || m_crit.isEmpty() && m_op.isEmpty() && !m_val.isEmpty() // nur wert
                );
    }

}
