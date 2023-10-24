package de.checkpoint.server.data.caseRules;

import java.text.*;
import java.util.*;

import org.w3c.dom.*;
import de.checkpoint.drg.RulesMgr;
import de.checkpoint.server.exceptions.ExcException;
import de.checkpoint.server.db.OSObject;
import de.checkpoint.ruleGrouper.CRGRule;
import de.checkpoint.ruleGrouper.CRGRuleGrouperManager;



public class DsrRule extends OSObject implements Comparable
{
  /* 3.9.5 2015-09-02 DNi: #FINDBUGS - Attribut m_sdf ist nicht threadsafe! */
	protected static SimpleDateFormat m_sdf = new SimpleDateFormat("dd.MM.yyyy");
//	protected static SimpleDateFormat m_sdf1 = new SimpleDateFormat("dd.MM.yyyy");

//	public Element m_element = null;

	public DatRuleElement m_rootElement = null;

	public String m_ruleNumber = "";
	public String m_rid = "";
	public String m_number = "";
	public String m_caption = "";
	public String m_text = "";
	public String m_suggestion = "";
	public boolean m_isActive = true;
	public boolean m_isVisible = false;
	public boolean m_isEntgelt = true;
	public boolean m_isUnchanged = true;
	public String m_isActiveText = "";
	public String m_notice = "";
	public int m_unchangedType = 0;

	public java.util.Date m_adt = null;
	public java.util.Date m_ddt = null;

	public long m_validFromTime = -1;
	public long m_validToTime = -1;

	public int m_type = 0;
	public String m_typeText = "";
	public String m_errorTypeText = "";
	public DatRulerErrorType m_errorType = null;
	public Vector m_lstSuggs = null;
	public Vector m_lstSuggItems = new Vector();

	public Vector m_tableNames = new Vector();
        
        public HashMap<Integer, DatRuleVal>  m_simpleTerms = new HashMap<>();

	public String m_ruleText = "";
	public long[] m_roles = null;
	public long[] m_rolesVisible = null;

	public long[] m_rolesNoVisible = null;

	public long[] m_rolesVisibleBasic = null; // die Kopie von Visibles wenn die Regel angelegt wird

	public boolean m_rolesVisibleChanged = false;

	public boolean m_hasSuggestions = false;
        public String m_massNumber = "0";
        public String m_medType = "S";
        public float m_profit = 0.0f;

	public int sugg_breathing = -1;
	public int sugg_age_years = -1;
	public int sugg_age_months = -1;
	public String sugg_sex = null;
	public int sugg_weight = -1;
	public int sugg_vwd = -1;
	public int sugg_vwd_op = 0;

	public Vector sugg_princ_diag = null;
	public Vector sugg_aux_diag = null;
	public Vector sugg_diag = null;
	public Vector sugg_proc = null;

	public int sugg_adm_type = -1;
	public int sugg_dis_type = -1;
	public int sugg_adm_cause = -1;

	public double sugg_dCW = 0d;

	public boolean hasInterval = false;
	public String intervalFromCrit = null;
	public String intervalFromVal = null;
	public String intervalToCrit = null;
	public String intervalToVal = null;

	public boolean m_isCorrupt = false;


	public int m_year = -1;

	/* 3.9.5 2015-09-02 DNi: #FINDBUGS - Attribut m_cal ist nicht threadsafe! */
	protected static Calendar m_cal = new GregorianCalendar();
	List m_types = null;
    protected boolean m_isCheckpoint = true; // temporäre Abhilfe um den Zugriff auf einigen Klassen zu vermeiden bei CheckpointGrouper ohne CheckpoinDRG

	public DsrRule()
	{
		this(null, 0);
	}

	public DsrRule(int year)
	{
		this(null, year);
	}

	public DsrRule(Element ele, int year)
	{
		super("", false);
		m_year = year;
		init(ele, null);
	}

	public DsrRule(String tableName, boolean isDetailed)
	{
		super(tableName, isDetailed);
	}

	public DsrRule(Element ele, int year, List ruleTypes)
	{
		super("", false);
		m_year = year;
		init(ele, ruleTypes);
	}

	protected void init(Element ele, List ruleTypes)
	{
		var_suff = "";
		m_types = ruleTypes;
		if(ele != null) {
			m_rid = ele.getAttribute(DatCaseRuleAttributes.ATT_RULES_ID); 
			setValuesByElement(ele, ruleTypes); 
		}
	}

	public void setNumber(String number)
	{
		m_ruleNumber = number;
/*		if(this.m_element != null) {
			m_element.setAttribute(DatCaseRuleAttributes.ATT_RULES_NUMBER, number);
		}*/
	}

	public void setRuleID(String rid)
	{
		this.m_rid = rid;
/*		if(this.m_element != null) {
			m_element.setAttribute(DatCaseRuleAttributes.ATT_RULES_ID, rid);
		}*/
	}

	public void setValuesByElement(Element ele, List ruleTypes)
	{
		if(ele != null) {
//			m_element = ele;
			setBasics(ele, ruleTypes);
			setInterval(ele);
			setDates(ele);
			setRule(ele);
			setSuggestion(ele);
			setRoles(ele);
			setRolesVisible(ele);
		}
	}

	public Element getValuesByElement(Element ele, Document doc)
	{
		getBasics(ele);
		getInterval(ele, hasInterval, new DatInterval(intervalFromCrit, this.intervalFromVal, this.intervalToCrit,
			this.intervalToVal));
		getDates(ele);
		getRule(ele, doc);
		getSuggestion(ele, doc);
		getRoles(ele);
		getFeeGroups(ele);
		getRolesVisible(ele);
//		this.m_element = ele;
		return ele;
	}

	protected void getFeeGroups(Element ele)
	{}

	private void getInterval(Element ele, boolean hasInterval, DatInterval interval)
	{
		if(hasInterval) {
			ele.setAttribute(DatCaseRuleAttributes.ATT_RULES_HAS_INTERVAL, "true");
		} else {
			ele.setAttribute(DatCaseRuleAttributes.ATT_RULES_HAS_INTERVAL, "false");
			ele.setAttribute(DatCaseRuleAttributes.ATT_RULES_INTERVAL_FROM, "");
			ele.setAttribute(DatCaseRuleAttributes.ATT_RULES_INTERVAL_TO, "");
			return;
		}
		String crit = interval.getIntervalFromCritText();
		if(crit == null) {
			ele.setAttribute(DatCaseRuleAttributes.ATT_RULES_INTERVAL_FROM, "");
		} else {
			String val = getXMLText(crit);
			String intervalFromVal = interval.getIntervalFromValue();
			if(val != null && intervalFromVal != null && intervalFromVal.length() > 0) {
				val += ":" + intervalFromVal;
			}
			ele.setAttribute(DatCaseRuleAttributes.ATT_RULES_INTERVAL_FROM, val);
		}
		crit = interval.getIntervalToCritText();
		if(crit == null) {
			ele.setAttribute(DatCaseRuleAttributes.ATT_RULES_INTERVAL_TO, "");
		} else {
			String val = getXMLText(crit);
			String intervalToVal = interval.getIntervalToValue();
			if(intervalToVal != null && intervalToVal.length() > 0) {
				val += ":" + intervalToVal;
			}
			ele.setAttribute(DatCaseRuleAttributes.ATT_RULES_INTERVAL_TO, val);
		}
	}

	protected void setInterval(Element ele)
	{
		String val = ele.getAttribute(DatCaseRuleAttributes.ATT_RULES_HAS_INTERVAL);
		if(val != null && val.equals("true")) {
			hasInterval = true;
		} else {
			hasInterval = false;
		}
		val = getDisplayText(ele.getAttribute(DatCaseRuleAttributes.ATT_RULES_INTERVAL_FROM));
		if(val != null) {
			String[] vals = val.split(":");
			if(vals.length > 0) {
				intervalFromCrit = vals[0];
			} else {
				intervalFromCrit = "";
			}
			if(vals.length > 1) {
				intervalFromVal = vals[1];
			} else {
				intervalFromVal = "";
			}
		} else {
			this.intervalFromCrit = "";
			this.intervalFromVal = "";
		}
		val = getDisplayText(ele.getAttribute(DatCaseRuleAttributes.ATT_RULES_INTERVAL_TO));
		if(val != null) {
			String[] vals = val.split(":");
			if(vals.length > 0) {
				intervalToCrit = vals[0];
			} else {
				intervalToCrit = "";
			}
			if(vals.length > 1) {
				intervalToVal = vals[1];
			} else {
				intervalToVal = "";
			}
		} else {
			this.intervalToCrit = "";
			this.intervalToVal = "";
		}
	}

	private Element getBasics(Element ele)
	{
		ele.setAttribute(DatCaseRuleAttributes.ATT_CAPTION_CAPTION, getXMLText(m_caption));
		ele.setAttribute(DatCaseRuleAttributes.ATT_CAPTION_NUMBER, getXMLText(m_number));
		ele.setAttribute(DatCaseRuleAttributes.ATT_CAPTION_TEXT, getXMLText(m_text));
		ele.setAttribute(DatCaseRuleAttributes.ATT_CAPTION_TYPE, getXMLText(m_typeText));
		ele.setAttribute(DatCaseRuleAttributes.ATT_RULES_NUMBER, getXMLText(m_ruleNumber));
		ele.setAttribute(DatCaseRuleAttributes.ATT_RULES_NOTICE, getXMLText(m_notice));
                
                ele.setAttribute(DatCaseRuleAttributes.ATT_RULES_MASSNUMBER, getXMLText(m_massNumber));
                ele.setAttribute(DatCaseRuleAttributes.ATT_RULES_MEDTYPE, getXMLText(m_medType));
                ele.setAttribute(DatCaseRuleAttributes.ATT_RULES_PROFIT, getXMLText(String.valueOf(m_profit)));
                
		if(m_isEntgelt) {
			ele.setAttribute(DatCaseRuleAttributes.ATT_CAPTION_ENTGELT, "true");
		} else {
			ele.setAttribute(DatCaseRuleAttributes.ATT_CAPTION_ENTGELT, "false");
		}

		if(!m_isActive) {
			ele.setAttribute(DatCaseRuleAttributes.ATT_CAPTION_USED, "false");
		} else {
			ele.setAttribute(DatCaseRuleAttributes.ATT_CAPTION_USED, "true");
		}

		ele.setAttribute(DatCaseRuleAttributes.ATT_CAPTION_UNCHANGE, String.valueOf(m_unchangedType));
		ele.setAttribute(DatCaseRuleAttributes.ATT_CAPTION_ERROR_TYPE, getXMLText(m_errorTypeText));
		return ele;
	}

	protected void setBasics(Element ele, List ruleTypes)
	{
		try {
			m_caption = getDisplayText(ele.getAttribute(DatCaseRuleAttributes.ATT_CAPTION_CAPTION));
			m_number = getDisplayText(ele.getAttribute(DatCaseRuleAttributes.ATT_CAPTION_NUMBER));
			m_text = getDisplayText(ele.getAttribute(DatCaseRuleAttributes.ATT_CAPTION_TEXT));
			m_typeText = getDisplayText(ele.getAttribute(DatCaseRuleAttributes.ATT_CAPTION_TYPE));
			m_type = DatCaseRuleConstants.getTypKey(m_typeText);
			m_ruleNumber = getDisplayText(ele.getAttribute(DatCaseRuleAttributes.ATT_RULES_NUMBER));
			m_notice = getDisplayText(ele.getAttribute(DatCaseRuleAttributes.ATT_RULES_NOTICE));
                        m_massNumber = getDisplayText(ele.getAttribute(DatCaseRuleAttributes.ATT_RULES_MASSNUMBER));
                        m_medType = getDisplayText(ele.getAttribute(DatCaseRuleAttributes.ATT_RULES_MEDTYPE));
                        try{
                            m_profit = Float.valueOf(getDisplayText(ele.getAttribute(DatCaseRuleAttributes.ATT_RULES_PROFIT)));
                        }catch(Exception e){
                            m_profit = 0;
                        }
                        
			String err = getDisplayText(ele.getAttribute(DatCaseRuleAttributes.ATT_CAPTION_ENTGELT));
			if(err.equals("true")) {
				m_isEntgelt = true;
			} else {
				m_isEntgelt = false;
			}
			m_isActiveText = getDisplayText(ele.getAttribute(DatCaseRuleAttributes.ATT_CAPTION_USED));
			if(m_isActiveText.equals("false")) {
				m_isActive = false;
			} else {
				m_isActive = true;
			}
			err = getDisplayText(ele.getAttribute(DatCaseRuleAttributes.ATT_CAPTION_UNCHANGE));
			try {
				m_unchangedType = Integer.parseInt(err.trim());
			} catch(Exception ex) {
				m_unchangedType = 0;
			}
			m_errorTypeText = getDisplayText(ele.getAttribute(DatCaseRuleAttributes.ATT_CAPTION_ERROR_TYPE).trim());
			if(ruleTypes == null) {
				m_errorType = RulesMgr.getRulesManager().getRulesErrorTypeByText(m_errorTypeText);
			} else {
				m_errorType = RulesMgr.getRulesManager().getRulesErrorTypeByText(m_errorTypeText, ruleTypes);
			}
		} catch(Exception ex) {
			ExcException.createException(ex);
		}
	}

	public String getEndDate()
	{
		try {
			return m_sdf.format(m_ddt);
		} catch(Exception ex) {
			return "";
		}
	}

	protected void setDates(Element ele)
	{
            String adm= "";
            String ddt = "";
            
		try {
                        adm = ele.getAttribute(DatCaseRuleAttributes.ATT_CAPTION_FROM);
			m_adt = null;
			if(adm != null && adm.trim().trim().length() == 10) {
				m_adt = new SimpleDateFormat("dd.MM.yyyy").parse(adm);
				m_validFromTime = m_adt.getTime();
			} else {
				m_validFromTime = 0;
			}
			ddt = ele.getAttribute(DatCaseRuleAttributes.ATT_CAPTION_TO);
			m_ddt = null;
			if(ddt != null && ddt.trim().length() == 10) {
				m_ddt = new SimpleDateFormat("dd.MM.yyyy").parse(ddt);
				m_validToTime = m_ddt.getTime();
			} else {
				m_validToTime = 0;
			}
			try {
				m_year = Integer.parseInt(getDisplayText(ele.getAttribute(DatCaseRuleAttributes.ATT_CAPTION_RULE_YEAR)));
			} catch(Exception e) {
				m_year = getYear();
			}
			;
		} catch(Exception ex) {
                    Exception e = new Exception("rule = " + m_number + " adm= " + adm + " ddt = " + ddt , ex);
			ExcException.createException(e);
		}
	}

	public int getYear()
	{
		if(m_year >= 0) {
			return m_year;
		}
		if(m_year < 0 && m_adt != null) {
			m_year = getYearFromDate(m_adt);
		} else {
			m_year = 0;
		}
		return m_year;
	}

	private synchronized int getYearFromDate(Date dt)
	{
		m_cal.setTime(dt);
		return m_cal.get(Calendar.YEAR);
	}

	private Element getDates(Element ele)
	{
		try {
			if(m_adt != null) {
				String adm = m_sdf.format(m_adt);
				if(adm != null) {
					ele.setAttribute(DatCaseRuleAttributes.ATT_CAPTION_FROM, adm);
				}
			} else {
				ele.setAttribute(DatCaseRuleAttributes.ATT_CAPTION_FROM, "");
			}
			if(m_ddt != null) {
				String ddt = m_sdf.format(m_ddt);
				if(ddt != null) {
					ele.setAttribute(DatCaseRuleAttributes.ATT_CAPTION_TO, ddt);
				}
			} else {
				ele.setAttribute(DatCaseRuleAttributes.ATT_CAPTION_TO, "");
			}
			if(m_year >= 0) {
				ele.setAttribute(DatCaseRuleAttributes.ATT_CAPTION_RULE_YEAR, String.valueOf(m_year));
			} else {
				ele.setAttribute(DatCaseRuleAttributes.ATT_CAPTION_RULE_YEAR, "");
			}
		} catch(Exception ex) {
			ExcException.createException(ex);
		}
		return ele;
	}

	protected void setRoles(Element ele)
	{
		try {
			m_roles = DatCaseRuleMgr.getRolesFromElement(ele);
		} catch(Exception ex) {
			ExcException.createException(ex);
		}
	}

	protected void setRolesVisible(Element ele)
	{
		try {
			m_rolesVisible = DatCaseRuleMgr.geValuesFromElement(ele, DatCaseRuleAttributes.ATT_CAPTION_VISIBLE);
			m_rolesVisibleBasic = m_rolesVisible;

		} catch(Exception ex) {
			ExcException.createException(ex);
		}
	}

	public void setRolesVisible(long roleid, boolean isVisible)
	{
		try {
			Vector v = new Vector();
			if(m_rolesVisible != null) {
				for(int i = 0; i < m_rolesVisible.length; i++) {
					if(m_rolesVisible[i] != roleid) {
						v.add(m_rolesVisible[i]);
					}
				}
				if(isVisible) {
					v.add(roleid);
				}
			}
			int n = v.size();
			m_rolesVisible = new long[n];
			for(int i = 0; i < n; i++) {
				m_rolesVisible[i] = (Long)v.get(i);
			}
		} catch(Exception ex) {
			ExcException.createException(ex);
		}
	}


	protected boolean isInArraySorted(long roleid, long[] roleids)
	{
		int high, low = -1, probe;
		if(roleids != null && roleids.length > 0) {
			high = roleids.length;
			while(high - low > 1) {
				probe = (high + low) / 2;
				if(roleid < roleids[probe] )
					high = probe;
				else if(roleid == roleids[probe] )
					return true;
				else
					low = probe;
			}
		}
		return false;
	}


	protected boolean isInArray(long roleid, long[] roleids)
	{
		if(roleids == null) {
			return false;
		}
		for(int i = 0; i < roleids.length; i++) {
			if(roleid == roleids[i]) {
				return true;
			}
		}
		return false;
	}

	public void setRolesVisible(long[] roleids, boolean isVisible)
	{
		int n = roleids != null ? roleids.length : 0;
		if(n > 0) {
			try {
				Vector v = new Vector();
				int m = m_rolesVisible != null ? m_rolesVisible.length : 0;
				for(int i = 0; i < m; i++) {
					if(!isInArray(m_rolesVisible[i], roleids)) {
						v.add(m_rolesVisible[i]);
					}
				}
				if(isVisible) {
					for(int i = 0; i < n; i++) {
						v.add(roleids[i]);
					}
				} else { //falls isVisible false ist, ist die Regel für diese Rollen nicht mehr sichtbar
					for(int i = 0; i < n; i++) {
						v.remove(roleids[i]);
					}
				}
				n = v.size();
				m_rolesVisible = new long[n];
				for(int i = 0; i < n; i++) {
					m_rolesVisible[i] = (Long)v.get(i);
				}
				m_rolesVisibleChanged = true;
			} catch(Exception ex) {
				ExcException.createException(ex);
			}
		}
	}

	private Element getRoles(Element ele)
	{
		try {
			ele.setAttribute(DatCaseRuleAttributes.ATT_CAPTION_ROLE, getStringFromLongArray(m_roles));
		} catch(Exception ex) {
			ExcException.createException(ex);
		}
		return ele;
	}

	protected String getStringFromLongArray(long[] arr)
	{
		String tt = "";
		try {
			if(arr != null) {
				Arrays.sort(arr);
				for(int i = 0; i < arr.length; i++) {
					if(arr[i] > 0) {
						tt += String.valueOf(arr[i]) + ",";
					}
				}
				if(tt.endsWith(",")) {
					tt = tt.substring(0, tt.length() - 1);
				}
			}

		} catch(Exception ex) {
			ExcException.createException(ex);
		}
		return tt;
	}

	private Element getRolesVisible(Element ele)
	{
		try {
			ele.setAttribute(DatCaseRuleAttributes.ATT_CAPTION_VISIBLE, getStringFromLongArray(m_rolesVisible));
		} catch(Exception ex) {
			ExcException.createException(ex);
		}
		return ele;
	}

	public boolean isForRole(long roleID)
	{
		return roleID == -1 || isInArray(roleID, m_roles);//DatCaseRuleAttributes.isForRole(roleID, m_roles); // -1 ist für die 'alle' Rolle !
	}

	public boolean isForRoleSorted(long[] roleIDs)
	{
		int n = roleIDs != null ? roleIDs.length : 0;
		for(int i = 0; i < n; i++) {
			if(this.isInArraySorted(roleIDs[i], m_roles)) {
				return true;
			}
		}
		return false;
	}

	public boolean isForRole(long[] roleIDs)
	{
		int n = roleIDs != null ? roleIDs.length : 0;
		for(int i = 0; i < n; i++) {
			if(this.isInArray(roleIDs[i], m_roles)) {
				return true;
			}
		}
		return false;
	}

	public boolean isVisibleForRole(long roleID)
	{
		return roleID == -1 || isInArray(roleID, m_rolesVisible);// DatCaseRuleAttributes.isForRole(roleID, m_rolesVisible); // -1 ist für die 'alle' Rolle !
	}

	public boolean isVisibleForRole(long[] roleIDs)
	{
		int n = roleIDs != null ? roleIDs.length : 0;
		for(int i = 0; i < n; i++) {
			if(this.isInArray(roleIDs[i], m_rolesVisible)) {
				return true;
			}
		}
		return false;
	}

	private Element getSuggestion(Element ele, Document doc)
	{
		try {
			NodeList nl = ele.getElementsByTagName(DatCaseRuleAttributes.
						  ELEMENT_RULES_SUGGESTIONS);
			int n = nl.getLength();
			for(int i = n; i >= 0; i--) {
				Node node = nl.item(i);
				if(node != null) {
					try {
						ele.removeChild(node);
					} catch(org.w3c.dom.DOMException ex) {
						ex.printStackTrace();
					}
				}
			}
			Element suggs = doc.createElement(DatCaseRuleAttributes.ELEMENT_RULES_SUGGESTIONS);
			suggs.setAttribute(DatCaseRuleAttributes.ATT_SUGG_TEXT, getXMLText(m_suggestion));
			for(int i = 0, m = m_lstSuggItems.size(); i < m; i++) {
				DatRuleSuggestion suggDat = (DatRuleSuggestion)m_lstSuggItems.get(i);
				Element sugg = doc.createElement(DatCaseRuleAttributes.ELEMENT_RULES_SUGG);
				if(suggDat.m_act != null) {
					sugg.setAttribute(DatCaseRuleAttributes.ATT_SUGG_ACTION_ID, String.valueOf(suggDat.m_act.m_id));
				} else {
					sugg.setAttribute(DatCaseRuleAttributes.ATT_SUGG_ACTION_ID, String.valueOf(DatCaseRuleAttributes.SUGG_CHANGE));
				}
				if(suggDat.getCriterion() != null) {
					sugg.setAttribute(DatCaseRuleAttributes.ATT_SUGG_CRIT, getXMLText(suggDat.getWorkText()));
				}
				sugg.setAttribute(DatCaseRuleAttributes.ATT_SUGG_OP, getXMLText(suggDat.m_op));
				sugg.setAttribute(DatCaseRuleAttributes.ATT_SUGG_VALUE,
                                        CRGRuleGrouperManager.checkWithFormat(suggDat.getWorkText(), getXMLText(suggDat.getValue(suggDat.m_val))));
				if(suggDat.m_op != null && (suggDat.m_op.indexOf('@') >= 0)) {
					this.addTableName(suggDat.m_val);
				}
				sugg.setAttribute(DatCaseRuleAttributes.ATT_SUGG_CONDITION_OP, getXMLText(suggDat.m_condition_op));
				sugg.setAttribute(DatCaseRuleAttributes.ATT_SUGG_CONDITION_VALUE, getXMLText(suggDat.getValue(suggDat.m_condtion_val)));
				if(suggDat.m_condition_op != null && (suggDat.m_condition_op.indexOf('@') >= 0)) {
					this.addTableName(suggDat.m_condtion_val);
				}
				suggs.appendChild(sugg);
			}
			ele.appendChild(suggs);
		} catch(Exception ex) {
			ExcException.createException(ex, "Regel ID " + this.m_rid + " Nummer " + this.m_ruleNumber);
		}
		return ele;
	}

	protected int getSuggestionLength(NodeList snl)
	{
		return snl != null ? snl.getLength() : 0; 
	}

	public void setSuggestion(Element rule)
	{
		try {
			this.resetSuggestion();
			m_lstSuggItems = new Vector();
			NodeList nl = rule.getElementsByTagName(DatCaseRuleAttributes.
						  ELEMENT_RULES_SUGGESTIONS);
			if(nl.getLength() > 0) {
				Element suggs = (Element)nl.item(0);
				this.m_suggestion = getDisplayText(suggs.getAttribute(DatCaseRuleAttributes.
									ATT_SUGG_TEXT));
				NodeList snl = suggs.getElementsByTagName(DatCaseRuleAttributes.
							   ELEMENT_RULES_SUGG);
				m_lstSuggs = new Vector();
				int n = getSuggestionLength(snl);
				for(int i = 0; i < n; i++) {
					m_hasSuggestions = true;
					Element sugg = (Element)snl.item(i);
					String tt = "";
					String actID = getDisplayText(sugg.getAttribute(DatCaseRuleAttributes.ATT_SUGG_ACTION_ID));
					String crit = getDisplayText(sugg.getAttribute(DatCaseRuleAttributes.ATT_SUGG_CRIT));
					String op = getDisplayText(DatCaseRuleMgr.getDisplayText(sugg.
								getAttribute(DatCaseRuleAttributes.ATT_SUGG_OP)));
					String val = getDisplayText(sugg.getAttribute(DatCaseRuleAttributes.ATT_SUGG_VALUE));
					String condition_op = getDisplayText(DatCaseRuleMgr.getDisplayText(sugg.
								getAttribute(DatCaseRuleAttributes.ATT_SUGG_CONDITION_OP)));
					String condition_val = getDisplayText(sugg.getAttribute(DatCaseRuleAttributes.ATT_SUGG_CONDITION_VALUE));

					val = val.replace('*', '%');
					String value = val.replaceAll("'", "");
					addSuggItem(actID, crit, op, value, condition_op, condition_val);
					setSuggestionValues(actID, crit, op, value, i, condition_op, condition_val);
					tt = crit;
					tt = tt + " " + op;
					tt = tt + " " + val;
					m_lstSuggs.add(tt);
					if(op.indexOf("@") >= 0) {
						addTableName(value);
					}
                                        if(condition_op != null && condition_op.contains("@")){
                                            addTableName(condition_val);
                                        }
				}

			}
		} catch(Exception ex) {
			ExcException.createException(ex);
		}
	}

	private void resetSuggestion()
	{
		m_hasSuggestions = false;

		sugg_breathing = -1;
		sugg_age_years = -1;
		sugg_age_months = -1;
		sugg_sex = null;
		sugg_weight = -1;
		sugg_vwd = -1;
		sugg_vwd_op = 0;

		sugg_princ_diag = null;
		sugg_aux_diag = null;
		sugg_diag = null;
		sugg_proc = null;

		sugg_adm_type = -1;
		sugg_dis_type = -1;
		sugg_adm_cause = -1;
	}

	public int getRuleDescrTypeID()
	{
		if(this.m_errorType != null) {
			return m_errorType.getIdent();
		} else {
			return -1;
		}
	}

	protected void setRule(Element ele)
	{
		try {
			this.m_tableNames.clear();
			m_rootElement = new DatRuleElement(null);
			updateRuleText(ele);
			setRuleDefinition(m_rootElement);
		} catch(Exception ex) {
			ExcException.createException(ex);
			m_ruleText = "Fehler beim Setzen des Strings";
		}
	}

	protected void setRuleDefinition(DatRuleElement element) throws Exception
	{

	}

	private Element getRule(Element ele, Document doc)
	{
		try {
			NodeList nl = ele.getElementsByTagName(DatCaseRuleAttributes.ELEMENT_RULES_ELEMENT);  
			int n = nl.getLength();
			for(int i = n; i >= 0; i--) {
				Node node = nl.item(i);   
				if(node != null) {
					try {
						ele.removeChild(node);
					} catch(org.w3c.dom.DOMException ex) {
						ex.printStackTrace();
					}
				}
			}
			m_tableNames.clear();
			addRulesElement(ele, m_rootElement, doc, false);
		} catch(Exception ex) {
			ExcException.createException(ex);
			m_ruleText = "Fehler beim Setzen des Strings";
		}
		return ele;
	}

	private void addRulesElement(Element parent, DatRuleElement dat, Document doc, boolean setNested)
	{
		Element ele = doc.createElement(DatCaseRuleAttributes.ELEMENT_RULES_ELEMENT);
		if(setNested) {
			ele.setAttribute(DatCaseRuleAttributes.ATTR_NESTED, "true");
		} else {
			ele.setAttribute(DatCaseRuleAttributes.ATTR_NESTED, "false");
		}
		if(dat.m_isNot){
			ele.setAttribute(DatCaseRuleAttributes.ATT_RULES_VALUE_NOT, dat.m_not);
		}
		Vector childs = dat.getElements();
		if(childs != null) {
			for(int i = 0, n = childs.size(); i < n; i++) {
				DatRuleRoot child = (DatRuleRoot)childs.get(i);
				if(child instanceof DatRuleVal) {
					DatRuleVal datVal = (DatRuleVal)child;
					Element val = doc.createElement(DatCaseRuleAttributes.ELEMENT_RULES_VALUES);
					String op = datVal.m_op;
					val.setAttribute(DatCaseRuleAttributes.ATT_RULES_VALUE_NOT, getXMLText(datVal.m_not));
					val.setAttribute(DatCaseRuleAttributes.ATT_RULES_TYPE, getXMLText(datVal.m_crit));
                                        val.setAttribute(DatCaseRuleAttributes.ATT_RULES_METHOD, datVal.m_method);
                                        val.setAttribute(DatCaseRuleAttributes.ATT_RULES_PARAMETER, datVal.m_parameter);
					val.setAttribute(DatCaseRuleAttributes.ATT_RULES_OPERATOR, getXMLText(op));
					val.setAttribute(DatCaseRuleAttributes.ATT_RULES_VALUE, getXMLText(
                                                this.m_isCheckpoint?datVal.m_val:datVal.getCriteriumValue()));
					if(op != null && (op.equals("NOT IN @")
						|| op.equals("@")
						|| op.equals("!!@")
						|| op.equals("#@"))) {
						this.addTableName(m_isCheckpoint?datVal.m_val:datVal.getCriteriumValue());
					}
					ele.appendChild(val);
// intervalwerte
					getInterval(val, datVal.m_hasInterval, datVal.getInterval());
				} else if(child instanceof DatRuleOp) {
					DatRuleOp datOp = (DatRuleOp)child;
					Element op = doc.createElement(DatCaseRuleAttributes.ELEMENT_RULES_OPERATOR);
					op.setAttribute(DatCaseRuleAttributes.ATT_OPERATOR_TYPE, getXMLText(datOp.m_op));
					ele.appendChild(op);
				} else if(child instanceof DatRuleElement) {
					addRulesElement(ele, (DatRuleElement)child, doc, dat.hasNestedElements());
				}
			}
		}
		m_ruleText = RulesMgr.getRulesManager().getElementString(ele, null, null, this.m_simpleTerms); 
		parent.appendChild(ele);
	}

	protected void addSuggItem(String actID, String crit, String op, String val, String condition_op, String condition_val)
	{
		try {
			DatRulesCriterion criter = DatCaseRuleMgr.getCriterionByWorkText(crit);
			DatRulesAction ract = DatCaseRuleConstants.getActionByWorkID(actID);
			DatRuleSuggestion suggItem = new DatRuleSuggestion(criter, op, val, ract, condition_op, condition_val);
			m_lstSuggItems.add(suggItem);
		} catch(Exception ex) {
			ExcException.createException(ex);
		}
	}

	private int getInteger(String val)
	{
		if(val != null && val.length() > 0) {
			try {
				return Integer.parseInt(val.trim());
			} catch(Exception e) {
			}
		}
		return 0;
	}

	protected void setSuggestionValues(String actID, String crit, String op, String val, int index, String condition_op, String condition_val) throws Exception
	{
		try {
			if(op == null) {
				op = "";
			}
			if(crit.equals(DatCaseRuleMgr.CRIT_BREATHING)) {
				this.sugg_breathing = getInteger(val);
			} else if(crit.equals(DatCaseRuleMgr.CRIT_AGE_YEARS)) {
				this.sugg_age_years = getInteger(val);
			} else if(crit.equals(DatCaseRuleMgr.CRIT_AGE_MONTHS)) {
				this.sugg_age_months = getInteger(val);
			} else if(crit.equals(DatCaseRuleMgr.CRIT_GENDER)) {
				sugg_sex = val.trim();
			} else if(crit.equals(DatCaseRuleMgr.CRIT_WEIGHT)) {
				this.sugg_weight = getInteger(val);
			} else if(crit.equals(DatCaseRuleMgr.CRIT_VWD)) {
				sugg_vwd = getInteger(val);
				if(op.equals("==")) {
					sugg_vwd_op = DatCaseRuleAttributes.SUGG_OP_SAME;
				} else if(op.equals("+")) {
					sugg_vwd_op = DatCaseRuleAttributes.SUGG_OP_PLUS;
				} else if(op.equals("-")) {
					sugg_vwd_op = DatCaseRuleAttributes.SUGG_OP_MINUS;
				}
			} else if(crit.equals(DatCaseRuleMgr.CRIT_PRINC_DIAG)) {
				addSuggMainDiag(actID, op, val);
			} else if(crit.equals(DatCaseRuleMgr.CRIT_AUX_DIAG)
				|| crit.equals(DatCaseRuleMgr.CRIT_DIAG)) {
				addSuggDiag(actID, op, val);
			} else if(crit.equals(DatCaseRuleMgr.CRIT_PROC)) {
				addSuggProc(actID, op, val);
			} else if(crit.equals(DatCaseRuleMgr.CRIT_ADM_CAUSE)) {
				this.sugg_adm_cause = getInteger(val);
			} else if(crit.equals(DatCaseRuleMgr.CRIT_ADM_TYPE)) {
				this.sugg_adm_type = getInteger(val);
			} else if(crit.equals(DatCaseRuleMgr.CRIT_DIS_TYPE)) {
				this.sugg_dis_type = getInteger(val);
			}
		} catch(Exception ex) {
			ExcException.createException(ex);
		}
	}

	private void addOpValue(Object[] opVal, String op, String val)
	{
		try {
			opVal[0] = op;
			opVal[1] = new Integer(val.trim());
		} catch(Exception ex) {
			ExcException.createException(ex);
		}
	}

	private void addSuggMainDiag(String actID, String op, String val)
	{
		if(sugg_princ_diag == null) {
			sugg_princ_diag = new Vector();
		}
		addSuggCodes(sugg_princ_diag, actID, op, val);
	}

	private void addSuggDiag(String actID, String op, String val)
	{
		if(this.sugg_diag == null) {
			sugg_diag = new Vector();
		}
		addSuggCodes(sugg_diag, actID, op, val);
	}

	private void addSuggProc(String actID, String op, String val)
	{
		if(this.sugg_proc == null) {
			sugg_proc = new Vector();
		}
		addSuggCodes(sugg_proc, actID, op, val);
	}

	private void addSuggCodes(java.util.List lst, String actID, String op, String val)
	{
		String[] vals = null;
		int not = 0;
		if(op.endsWith("IN")) {
			vals = getSuggListStrings(val.toUpperCase());
		} else if(op.endsWith("@")) {
			vals = getSuggTableStrings(val);
		} else {
			vals = new String[] {val.toUpperCase()};
		}
		if(op.startsWith("NOT")) {
			not = 1;
		}
		Integer action = null;
		if(op.equals("!=")) {
			action = new Integer(DatCaseRuleAttributes.SUGG_DELETE);
		} else {
			action = new Integer(actID);
		}
		if(vals != null && vals.length > 0) {
			for(int i = 0; i < vals.length; i++) {
				lst.add(new Object[] {vals[i], action, new Integer(not)});
			}
		}
	}

	private String[] getSuggListStrings(String val)
	{
		val = val.replaceAll("'", "");
		return val.split(",");
	}

	protected int getIdent()
	{
		return m_year;
	}

	//hier sollte kein Apostroph merh enthalten sein. volker
	private String[] getSuggTableStrings(String val)
	{
//		try {
//			java.util.List lst = RulePermissionMgr.rulePermissionMgr().getTableValue(val, getIdent());
//			if(lst != null) {
//				String[] vals = new String[lst.size()];
//				for(int i = 0; i < lst.size(); i++) {
//					String cd = (String)lst.get(i);
//					vals[i] = cd.replaceAll("'", "");
//				}
//				return vals;
//			} else {
//				throw new RuntimeException("table " + val + " not found !");
//			}
//		} catch(Exception ex) {
//			ExcException.createException(ex);
//		}
		return new String[0];
	}

/*	public Element getElement()
	{
		return m_element;
	}*/

/*	public void resetElement()
	{
		m_element = null;
	}*/

        @Override
	public String toString()
	{
		return m_ruleText;
	}

	public String toShortString()
	{
		return m_caption;
	}

	public static boolean isEqualCode(String suggCD, String cd)
	{
		while(suggCD.indexOf("'") >= 0) {
			suggCD = suggCD.replaceAll("'", "");
		}
		if(suggCD.indexOf("%") >= 0) {
			suggCD = suggCD.replaceAll("%", "");
			int ind = cd.indexOf(suggCD);
			return ind < 0 ? false : true;
		} else if(suggCD.indexOf("*") >= 0) {
		  /* 3.9.5 2015-09-01 DNi: #FINDBUGS - replaceAll verarbeitet Regular Expressions und * ist ein Wildcard. Es müsste \\* abgefragt werden, sonst gibt es ein anderes Ergebnisse! */
			suggCD = suggCD.replaceAll("*", "");
			int ind = cd.indexOf(suggCD);
			return ind < 0 ? false : true;
		} else {
			return suggCD.equals(cd);
		}
	}


	public static boolean containsCode(Vector suggs, String cd)
	{
		for(Iterator it = suggs.iterator(); it.hasNext(); ) {
			String sugg = (String)it.next();
			if(isEqualCode(sugg, cd)) {
				return true;
			}

		}
		return false;
	}

	public DatRuleElement getRootElement()
	{
		if(m_rootElement == null) {
			m_rootElement = new DatRuleElement(null);
		}
		return this.m_rootElement;
	}

	public int getRootElementSize()
	{
		DatRuleElement rele = getRootElement();
		Vector v = rele.getElements();
		return v.size();
	}

	public Vector getTableNames()
	{
		return this.m_tableNames;
	}

	public void addTableName(String tblName)
	{
		tblName = tblName.replace("'", "");
		if(m_tableNames == null) {
			m_tableNames = new Vector();
		}
		if(!m_tableNames.contains(tblName)) {
			m_tableNames.add(tblName);
		}
	}

	public void removeTableName(String tblName)
	{
		tblName = tblName.replace("'", "");
		m_tableNames.remove(tblName);
	}

	public static String getXMLText(String text)
	{
		if(text == null) {
			return "";
		}
		text = text.replaceAll("<", "&lt;");
		text = text.replaceAll(">", "&gt;");
		return text.replaceAll("\n", "&#013");
	}

	public static String getDisplayText(String text)
	{
		text = text.replaceAll("&lt;", "<");
		text = text.replaceAll("&gt;", ">");
		if(text.length() >= 2) {
			if((text.charAt(0) == '>' || text.charAt(0) == '<')
				&& text.charAt(1) == '&') {
				text = text.replaceAll("&", "");
			}
		}
		return text.replaceAll("&#013", "\n");
	}

	public int compareTo(Object o)
	{
		if(o != null && o instanceof DsrRule) {
			DsrRule rule = (DsrRule)o;
			//if (this.m_number.length()>0 && rule.m_number.length()>0)
			return m_number.compareTo(rule.m_number);
			//else
			//return this.m_caption.compareTo(rule.m_caption);
		}
		return 0;
	}

	public DatRuleElement copyRootElement()
	{
		if(this.m_rootElement != null) {
			return m_rootElement.copyObject(null);
		}
		return null;
	}

	public Vector getContainedVals()
	{
		Vector v = new Vector();
		if(this.m_rootElement != null) {
			return getElementsValues(m_rootElement, v);
		}
		return v;
	}

	private Vector getElementsValues(DatRuleElement ele, Vector v)
	{
		Vector eles = ele.getElements();
		if(eles != null) {
			for(int i = 0, n = eles.size(); i < n; i++) {
				Object obj = eles.get(i);
				if(obj instanceof DatRuleElement) {
					getElementsValues((DatRuleElement)obj, v);
				} else if(obj instanceof DatRuleVal) {
					v.add(((DatRuleVal)obj).m_val);
				}
			}
		}
		return v;
	}

	public OSObject copyObject()
	{
		DsrRule ret = (DsrRule)super.copyObject();
		ret.m_rootElement = ret.copyRootElement();
		return ret;

	}

	public OSObject copyObject(boolean status)
	{
		CRGRule ret = (CRGRule)super.copyObject();
		return ret;

	}

	public long getValidFromTime()
	{
		return m_validFromTime;
	}

	public long getValidToTime()
	{
		return m_validToTime;
	}

	public String getNumber()
	{
		return m_number;
	}

	public Vector getRuleTerms()
	{
		return m_rootElement.getTerms();
	}
        
        public void reArrangeMarks(){
            ArrayList<DatRuleRoot> members = new ArrayList<>();
            
            HashMap<String, DatRuleRoot> marks = new HashMap<>();
            
            m_rootElement.getAllRuleMembers(m_rootElement, members,  marks);
            if(marks.keySet() == null ||  marks.keySet() != null && members.size() != marks.keySet().size()){
                m_rootElement.rearrangeMarks(members);
            }
        }

	public DsrRule clone()
	{
//		DsrRule rule = new DsrRule(this.m_element, this.m_year);
	  /* 3.9.5 2015-09-02 DNi: #FINDBUGS - clone()-Methode wird verwendet, aber das Interface Cloneable wird gar nicht implementiert! */
		return (DsrRule)this.copyObject();
	}

	public List getRuleTypes()
	{
		return m_types;
	}

	public String updateRuleText(Element ele)
	{
		NodeList nl = ele.getElementsByTagName(DatCaseRuleAttributes.ELEMENT_RULES_ELEMENT);
		if(nl != null && nl.getLength() > 0) {
			m_ruleText = RulesMgr.getRulesManager().getElementString((Element)nl.item(0), m_rootElement, m_tableNames, m_simpleTerms);
		} else {
			m_ruleText = "";
		}
		return m_ruleText;
	}

	public void setRuleValues(DsrRule rule)
	{
		m_rootElement = rule.copyRootElement();
		this.hasInterval = rule.hasInterval;
		this.intervalFromCrit = rule.intervalFromCrit;
		this.intervalFromVal = rule.intervalFromVal;
		this.intervalToCrit = rule.intervalToCrit;
		this.intervalToVal = rule.intervalToVal;

		this.m_ruleNumber = rule.m_ruleNumber;
		this.m_rid = rule.m_rid;
		this.m_number = rule.m_number;
		this.m_caption = rule.m_caption;
		this.m_text = rule.m_text;
		this.m_suggestion = rule.m_suggestion;
		this.m_isActive = rule.m_isActive;
		this.m_isVisible = rule.m_isVisible;
		this.m_isEntgelt = rule.m_isEntgelt;
		this.m_isUnchanged = rule.m_isUnchanged;
		this.m_isActiveText = rule.m_isActiveText;
		this.m_notice = rule.m_notice;
		this.m_unchangedType = rule.m_unchangedType;

		this.m_adt = rule.m_adt;
		this.m_ddt = rule.m_ddt;

		this.m_validFromTime = rule.m_validFromTime;
		this.m_validToTime = rule.m_validToTime;

		this.m_type = rule.m_type;
		this.m_typeText = rule.m_typeText;
		this.m_errorTypeText = rule.m_errorTypeText;
		this.m_errorType = rule.m_errorType;
		if(rule.m_lstSuggs != null) {
			this.m_lstSuggs = new Vector();
			try {
				for(int i = 0; i < rule.m_lstSuggs.size(); i++) {

					this.m_lstSuggs.addElement(new String((String)rule.m_lstSuggs.elementAt(i)));
				}

			} catch(Exception ex) {
				ExcException.createException(ex);

			}
		}
		this.m_lstSuggItems = new Vector();
		if(rule.m_lstSuggItems.size() > 0) {
			try {
				for(int i = 0; i < rule.m_lstSuggItems.size(); i++) {
					DatRuleSuggestion sugg = ((DatRuleSuggestion)rule.m_lstSuggItems.get(i));
					this.m_lstSuggItems.addElement(new DatRuleSuggestion(sugg.getCriterion(), sugg.m_op, sugg.m_val,
						sugg.m_act, sugg.m_condition_op, sugg.m_condtion_val));
				}

			} catch(Exception ex) {
				ExcException.createException(ex);
			}

		}
		this.m_tableNames = new Vector();
		Vector v = rule.getTableNames();
		if(v != null && v.size() > 0) {
			try {

				for(int i = 0; i < v.size(); i++) {
					String r = (String)v.elementAt(i);
					this.addTableName(new String(r));
				}
			} catch(Exception ex) {
				ExcException.createException(ex);
			}

		}
		this.m_ruleText = rule.m_ruleText;
		if(rule.m_roles != null) {
			this.m_roles = new long[rule.m_roles.length];
			for(int i = 0; i < m_roles.length; i++) {
				this.m_roles[i] = rule.m_roles[i];
			}

		}
		if(rule.m_rolesVisible != null) {
			this.m_rolesVisible = new long[rule.m_rolesVisible.length];
			for(int i = 0; i < m_rolesVisible.length; i++) {
				this.m_rolesVisible[i] = rule.m_rolesVisible[i];
			}
		}
		this.m_hasSuggestions = rule.m_hasSuggestions;

		this.m_isCorrupt = rule.m_isCorrupt;

		this.m_year = rule.m_year;
	}

	public void setTableNames(Vector v)
	{
		this.m_tableNames.clear();
		this.m_tableNames = v;
	}

	private Vector getRuleTablesList(DatRuleRoot elem, Vector v)
	{
		if(elem instanceof DatRuleVal) {
			DatRuleVal val = (DatRuleVal)elem;
			if(val.m_op.indexOf("@") >= 0) {
				String s = val.m_val.replaceAll("'", "");
				if(!v.contains(s)) {
					v.addElement(s);
				}

			}
			return v;
		}

		else if(elem instanceof DatRuleElement) {
			DatRuleElement el = (DatRuleElement)elem;
			Vector vv = el.getTerms();
			for(int i = 0; i < vv.size(); i++) {
				Object obj = vv.elementAt(i);
				if(obj instanceof DatRuleVal){
					DatRuleVal val = (DatRuleVal)obj;
					if(val.m_op.indexOf("@") >= 0) {
						String s = val.m_val.replaceAll("'", "");
						if(!v.contains(s)) {
							v.addElement(s);
						}

					}
				}
			}
			return v;

		}

		return v;
	}

	/**
	 * liefert alle in einem Regel benutzten Tabellen
	 * @return Vector
	 */
	public Vector getAllRuleTables()
	{
		Vector v = getSuggTableNames();
		return getRuleTablesList(this.m_rootElement, v);
	}

	public boolean sameRoles(long[] roles)
	{
		return compareRoleArrays(m_roles, roles);
	}

	protected boolean compareRoleArrays(long local[], long toCompare[])
{
		boolean found = false;
		if(local == null) {
			return toCompare == null || toCompare.length == 0;
		}
		if(toCompare == null)
			toCompare = new long[0];
		if(local.length != toCompare.length) {
			return false;
		}
		for(int i = 0; i < local.length; i++) {
			found = false;
			long role = local[i];
			for(int j = 0; j < toCompare.length; j++) {
				if(role == toCompare[j]) {
					found = true;
					break;
				}
			}
			if(!found) {
				return false;
			}
		}
		return true;
	}

	public boolean sameVisibleRoles(long[] roles)
	{
		return compareRoleArrays(m_rolesVisible, roles);
	}

	public Vector getSuggTableNames()
	{
		Vector v = new Vector();
		if(this.m_lstSuggItems != null) {
			Iterator it = m_lstSuggItems.iterator();
			while(it.hasNext()) {
				DatRuleSuggestion sugg = (DatRuleSuggestion)it.next();
				if(sugg.m_op.contains("@")) {
					if(!v.contains(sugg.m_val)) {
						v.addElement(sugg.m_val);
					}
				}
                                String op = sugg.getConditionOp();
                                if(op != null && op.contains("@")){
                                    if(!v.contains(sugg.getConditionValue())) {
						v.addElement(sugg.getConditionValue());
					}
                                }
			}
		}
		return v;
	}

    public void setIsCheckpoint(boolean m_isCheckpoint) {
        this.m_isCheckpoint = m_isCheckpoint;
    }

}
