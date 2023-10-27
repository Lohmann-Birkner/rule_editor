package de.lb.ruleprocessor.checkpoint.drg;

import java.util.*;



public class RulesCheckOut implements java.io.Serializable
{
	public java.util.List m_checkList = new ArrayList();
	public java.util.List m_checkLinkList = new ArrayList();
	public java.util.List m_cwList = new ArrayList();
	public java.util.List m_careCwList = new ArrayList();
	public java.util.List m_dFeeList = new ArrayList();
	public java.util.List m_cwRulesList = new ArrayList();
	public java.util.List m_refList = new ArrayList();
	public HashMap m_encounterVals = new HashMap();

	public HashMap m_hmCWRuleList = new HashMap(0);
	public HashMap m_hmFeeRuleList = new HashMap(0);
	public HashMap m_hmCareCwRuleList = new HashMap(0);
	public HashMap<String, String> m_hmDRGRuleList = new HashMap<String, String>(0);



	public int errorid = 0;
        public String beschreibung = "";
        public int m_vwd = 0;
	public boolean m_aufenthaltKleiner24h = false;
	public double m_cwEncounter = 0d;

	public boolean m_getResultsFromCase = false;
	// für Entgelte
	public String[] feeKeys = null;
	public double[] feeValues = null;
	public int[] feeCount = null;
	public java.util.Date[] feeFrom = null;
	public java.util.Date[] feeTo = null;;
	public int[] daysOff = null;
	public String kasse = null;
        public int m_reduceLos = 0;


	public RulesCheckOut()
	{
		this(false);
	}

	public RulesCheckOut(boolean getResultsFromCase)
	{
		m_getResultsFromCase = getResultsFromCase;
	}
}
