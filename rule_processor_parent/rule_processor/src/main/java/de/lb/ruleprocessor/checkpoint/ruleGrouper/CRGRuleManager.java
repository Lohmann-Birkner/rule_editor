package de.lb.ruleprocessor.checkpoint.ruleGrouper;


import de.lb.ruleprocessor.checkpoint.ruleGrouper.data.CRGRulePool;
import de.lb.ruleprocessor.checkpoint.ruleGrouper.data.CRGRuleTypes;
import de.lb.ruleprocessor.checkpoint.server.appServer.AppResources;
import de.lb.ruleprocessor.checkpoint.server.data.caseRules.DatCaseRuleAttributes;
import de.lb.ruleprocessor.checkpoint.server.exceptions.ExcException;
import de.lb.ruleprocessor.checkpoint.server.xml.XMLDOMWriter;
import java.io.*;
import java.rmi.*;
import java.text.*;
import java.util.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;

import org.w3c.dom.*;
import java.util.logging.Level;
import java.util.logging.Logger;

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
public abstract class CRGRuleManager implements CRGRuleManagerIF, Serializable
{

    private static final Logger LOG = Logger.getLogger(CRGRuleManager.class.getName());
    
	protected static CRGRuleManager m_ruleManager = null;

	public static final int MAX_POOLS = 100;
	public final static String m_tablesSuff = ".lb";

	protected static HashMap m_rulePools = null;
	protected static HashMap m_deletedRules = null;
	protected static HashMap m_changedRules = null;
	public static String m_rulesPath = null;
	private static String m_webServerPath = null;

	protected static String m_myAppServer = null;
//	protected static String m_otherAppServer = null;

	protected Vector m_communicationPools = null;

	protected static Vector m_rulesIndex = null;
	protected static Vector m_years = null;
	protected static Vector <Integer> m_ruleYears = null;
	protected static HashMap m_htTableNamesForPool = new HashMap();
	protected static int m_isMedAllowed = 0;
	protected static int m_isRuleTest = 0;
	protected static int m_isCrossCaseModelAllowed = 0;
	protected static int m_isLaborAllowed = 0;
	protected static int m_isCareDataAllowed = 0;
	protected static int m_isCheckpoint_RuleGrouper = 1; // seit 2013 wir haben nur Javagrouper
	protected static int m_isGKRuleConstruct = 0;
        protected static int m_isSoleAllowed = 0;
        protected static Vector m_ruleFilter;

	protected static HashMap m_htTableInt = new HashMap();
	protected static HashMap m_htTableString = new HashMap();
	protected static HashMap m_htTableDouble = new HashMap();
	protected static HashMap m_htTableDates = new HashMap();
	protected static HashMap m_htTableDate = new HashMap();
	protected static HashMap m_htTableLong = new HashMap();
	protected static HashMap m_htUserPoolsForYear = new HashMap();

	/* 3.9.5 2015-09-02 DNi: #FINDBUGS - Attribut m_simpleDateFormat ist nicht threadsafe! */
	protected static SimpleDateFormat m_simpleDateFormat = new SimpleDateFormat(AppResources.DATEFORMAT_DATE);
	/* 3.9.5 2015-09-02 DNi: #FINDBUGS - Attribut m_simpleTimeFormat ist nicht threadsafe! */
	protected static SimpleDateFormat m_simpleTimeFormat = new SimpleDateFormat(AppResources.DATEFORMAT_TIME);

//	public abstract String[] getTableStringValues(String tableName, String identifier, int year) throws Exception;
//
//	public abstract int[] getTableIntValues(String tableName, String identifier, int year) throws Exception;
//
//	public abstract double[] getTableDoubleValues(String tableName, String identifier, int year) throws Exception;
//
//	public abstract java.util.Date[] getTableDateValues(String tableName, String identifier) throws Exception;
//
//	public abstract long[] getTableLongValues(String tableName, String identifier, int year) throws Exception;

	protected abstract boolean writeXMLDocument(Document doc, int year, int type, String identifier);

	public abstract Vector getRuleTypes() throws Exception;

	public abstract void resetRuleTypes() throws Exception;

	public abstract void resetRuleTables(String ident, int year) throws Exception;

	public abstract byte[] getRuleFileForPool(String identifier, int year) throws Exception;

	public abstract InputStream getRuleStreamForPool(String identifier, int year) throws Exception;

	public abstract boolean saveRuleFileForPool(Document doc, String identifier, int year, String v_User, long[] allRoles) throws Exception;

	public abstract void saveRuleTableFileForPool(String content, String name, String identifier, int year,
		String v_User) throws Exception;

	public abstract boolean saveStringTable(String tableName, String identifier, Object[] content) throws Exception;

	public abstract Vector getRuleTableNamesForPool(String identifier, int year) throws Exception;

	//public abstract byte[] createRulePoolDocument(String v_Identifier, int v_Year) throws Exception;

	protected abstract Document getXMLDocumentWithStreamAndFullId(String identifier, int year) throws Exception;

	public abstract int getRuleSequence() throws Exception;

	public abstract int getRuleTypeSequence() throws Exception;

	//public abstract boolean saveRuleType(String shortText, String displayText, int ident) throws Exception;
	public abstract boolean saveRuleType(CRGRuleTypes crg, int ident, String user) throws Exception;

	public abstract boolean deleteRuleType(CRGRuleTypes rt, Vector v) throws Exception;

	public abstract boolean updateRulesTypes(Vector rtypes, String user) throws Exception;

	public abstract boolean deleteRuleTypes(Vector rTypes) throws Exception;

	protected abstract Vector getRulePools(long userid, long[] roleid) throws Exception;

	public abstract Vector getRuleRightsForRole(long roleid, CRGRulePool rpool) throws Exception;

	protected HashMap m_tableContents = new HashMap(); // key = String : ident+year+name in der form "MA1+2008+Tab1"
	public abstract byte[] loadTable(String name, String identifier, int year) throws Exception;

	public abstract boolean saveRuleTypes(Vector v, String user) throws Exception;

	protected abstract void getCommunicationPools() throws Exception;

	public abstract void checkCommunicationPools() throws Exception;

	protected boolean m_tablesLoaded = false;
	protected HashMap m_userPools = new HashMap();

	protected boolean m_communicationPoolsChecked = false;

	private Calendar m_calendar = null;

        public abstract CRGRule[] getAllRules() throws Exception;
	/**
	 * wenn ein Regelpool gespeichert wird, wird userId zu diesen Pool in den Vector geschrieben.
	 * Nachdem die Regel in sein Regelkern gelesen wurden, wird seine userId aus dem Vector entfernt
	 */
	protected Vector rulerForUserUpdated = new Vector();

	public CRGRuleManager()
	{
	}

	public void loadRulesTables(long userId, long[] roles)
	{
		try {
			if(!m_tablesLoaded) {
				Thread thr = new RuleTableLoder(userId, roles);
				thr.start();
			}
		} catch(Exception e) {
			ExcException.createException(e);
		}
	}

	public boolean loadRulesTablesInThread(long userId, long[] roles)
	{
		if(m_tablesLoaded) {
			return true;
		}
		try {
			Vector pools = this.getRulePools(userId, roles);
			if(pools == null) {
				return true;
			}
			for(int i = 0; i < pools.size(); i++) {
				CRGRulePool pool = (CRGRulePool)pools.elementAt(i);
				Vector tabNames = getRuleTableNamesForPool(pool.crgpl_identifier,
								  pool.crgpl_year);
				if(tabNames == null) {
					return true;
				}
				for(int ii = 0; ii < tabNames.size(); ii++) {
					String tableName = (String)tabNames.elementAt(ii);

					byte[] currentTable = null;
					try {
						currentTable = loadTable(tableName, pool.crgpl_identifier, pool.crgpl_year);
					} catch(Exception e) {}
				}
			}
		} catch(Exception ex) {
			ex.printStackTrace();

		}
		return true;
	}

	/**
	 * liefert die Inhalt einer Tabellendatei
	 * @param name String
	 * @param identifier String
	 * @param year int
	 * @return byte[]
	 * @throws Exception
	 */
	public byte[] getRuleTableFileForPool(String name, String identifier, int year) throws Exception
	{
		while(!m_tablesLoaded) {
			Thread.sleep(500);
		}
		String key = getKeyForTablesHash(identifier, year, name);
		Object obj = m_tableContents.get(key);
		if(obj != null) {
			String content = (String)obj;
			return content.getBytes();
		}

		return loadTable(name, identifier, year);
	}

	public static CRGRuleManager ruleManager() throws Exception
	{
    /* 3.9.5 2015-09-01 DNi: #FINDBUGS - Wegen der Initialisierung des statischen Feldes m_ruleManager sollte die Methode synchronized sein. */
		if(m_ruleManager == null) {

                    m_ruleManager = new CRGFileRuleManager();
			
		}
		return m_ruleManager;
	}

	/**
	 * erzeugt eine Instanz für den CRGRuleManager der die Regeln von dem angegebenen Pfad
	 * auf der Dateisystem liest
	 * @param rulePath String Pfad zu einem Regeloberverzeichnis
	 * @return CRGRuleManager
	 * @throws Exception
	 */
	public static CRGRuleManager ruleManager(String rulePath) throws Exception
	{
    /* 3.9.5 2015-09-01 DNi: #FINDBUGS - Wegen der Initialisierung des statischen Feldes m_ruleManager sollte die Methode synchronized sein. */
		if(m_ruleManager == null) {
			m_ruleManager = new CRGFileRuleManager(rulePath);
		}
		return m_ruleManager;
	}

	public Vector getRulesListForPool(String identifier, int year, int what) throws Exception
	{
		return getRulesListForPool(identifier, year, null, 0, what);
	}

	/**
	 * Liefert die Regeln für den Pool, die auf dem aktuellen Server gespeichert sind oder null
	 * @param identifier String
	 * @param year int
	 * @param what int
	 * @return Vector
	 * @throws Exception
	 */
	protected Vector getRuleListForPoolServer(String identifier, int year,
		int what) throws Exception
	{
		if(m_rulePools != null){
			String key = identifier + "_" + String.valueOf(year);
			Object obj = m_rulePools.get(key);
			if(obj != null){
				HashMap hRes = (HashMap)obj;
				obj = hRes.get(String.valueOf(what));
				if(obj != null && obj instanceof Vector)
					return (Vector)obj;
			}
		}
		return null;
	}

	/**
	 * Liefert die Regeln für den Pool aus dem XML-Dokument
	 * wenn check - Datum != null, müssen sie immer neu geparced werden und nicht in HashMap gespeichert werden,
	 * da sie an den check - Datum angebunden sind
	 * @param identifier String
	 * @param year int
	 * @param check Date
	 * @return Vector
	 * @throws Exception
	 */
	public Vector getRulesListForPool(String identifier, int year, Date check, long checkTime,
		int what) throws Exception
	{
		int retInd = -1;
		Vector resAll = null;
		Vector resCP = null;
		Vector resRSA = null;
		Vector resGK = null;
		HashMap hRes = null;
		String key = identifier + "_" + String.valueOf(year);
		if(check == null) { // ist für RuleSuite interessant
	    /* 3.9.5 2015-09-01 DNi: #FINDBUGS - Wegen der Initialisierung des statischen Feldes m_rulePools sollte die Methode synchronized sein. */
			if(m_rulePools == null) {
				m_rulePools = new HashMap();
			} else {
				Object obj = m_rulePools.get(key);
				if(obj == null) {
					hRes = new HashMap();
				} else {
					hRes = (HashMap)obj;
					resAll = (Vector)hRes.get(String.valueOf(CRGRule.CHECK_ALL));
					resCP = (Vector)hRes.get(String.valueOf(CRGRule.CHECK_CP_ONLY));
					resRSA = (Vector)hRes.get(String.valueOf(CRGRule.CHECK_MRSA_ONLY));
					resGK = (Vector)hRes.get(String.valueOf(CRGRule.CHECK_GK));
				}
			}
		}
		switch(what) {
			case CRGRule.CHECK_ALL:
				if(resAll != null) {
					return resAll;
				}
				retInd = 0;
				break;
			case CRGRule.CHECK_CP_ONLY:
				if(resCP != null) {
					return resCP;
				}
				retInd = 1;
				break;
			case CRGRule.CHECK_MRSA_ONLY:
				if(resRSA != null) {
					return resRSA;
				}
				retInd = 2;
				break;
			case CRGRule.CHECK_GK:
				if(resGK != null) {
					return resGK;
				}
				retInd = 3;
				break;
		}
		Vector[] resVec = new Vector[4];
		int[] index = new int[] {0, 0, 0, 0};
		if(resAll == null) {
			resAll = new Vector();
			index[0] = CRGRule.CHECK_ALL;
		}
		if(resCP == null) {
			resCP = new Vector();
			index[1] = CRGRule.CHECK_CP_ONLY;
		}
		if(resRSA == null) {
			resRSA = new Vector();
			index[2] = CRGRule.CHECK_MRSA_ONLY;
		}
		if(resGK == null) {
			resGK = new Vector();
			index[3] = CRGRule.CHECK_GK;
		}
		resVec[0] = resAll;
		resVec[1] = resCP;
		resVec[2] = resRSA;
		resVec[3] = resGK;
// hier um die Zeit zu sparen, alle typ - hashmaps gleich füllen
		/*
				CRGRule.CHECK_ALL
		  CRGRule.CHECK_CP_ONLY
		  CRGRule.CHECK_MRSA_ONLY)
		  CRGRule.CHECK_GK
		 */
		Document doc = getXMLDocumentWithStreamAndFullId(identifier, year);
		//Vector v = null;
		if(doc != null) {
			NodeList nl = doc.getElementsByTagName(DatCaseRuleAttributes.ELEMENT_RULES);
			int nlCount = nl != null ? nl.getLength() : 0;
			List ruleTypes = getRuleTypes();
                        int count = 0;
			for(int i = 0; i < nlCount; i++) {
				Element ele = (Element)nl.item(i);
				CRGRule rule = new CRGRule(ele, identifier, year, ruleTypes, check);
				if((checkTime == 0 || (rule.m_validFromTime <= checkTime && checkTime <= rule.m_validToTime))) {
					if(index[0] == CRGRule.CHECK_ALL) {
						resAll.add(rule);
					}
					if(index[1] == CRGRule.CHECK_CP_ONLY && rule.isCPRule()) {
						resCP.add(rule);
					}
					if(index[2] == CRGRule.CHECK_MRSA_ONLY && rule.isRSARule()) {
						resRSA.add(rule);
					}
					if(index[3] == CRGRule.CHECK_GK && rule.isGKRule()) {
						resGK.add(rule);
					}
                                        count++;
				}
			}
                        LOG.log(Level.INFO, "F\u00fcr den Regelpool {0}_{1} wurden {2} Regel geladen", new Object[]{year, identifier, count});
		}
		if(check == null) {
			if(hRes == null) {
				hRes = new HashMap();
			}
			for(int i = 0; i < index.length; i++) {
				if(index[i] != 0) {
					hRes.put(String.valueOf(index[i]), resVec[i]);
				}
			}
			m_rulePools.put(key, hRes);
		}

		return resVec[retInd];
	}

	public CRGRule[] getRulesForPool(String identifier, int year) throws Exception
	{
		return getRulesForPool(identifier, year, false, null, 0, CRGRule.CHECK_CP_ONLY);
	}

	public CRGRule[] getRulesForPool(String identifier, int year, Date check, long checkTime,
		int what) throws Exception
	{
		return getRulesForPool(identifier, year, false, check, checkTime, what);
	}

	public CRGRule[] getRulesForPool(String identifier, int year, boolean onlyUsed, Date check, long checkTime,
		int what) throws Exception
	{
		Vector v = getRulesListForPool(identifier, year, check, checkTime, what);
		int n = v != null ? v.size() : 0;
		CRGRule[] res = new CRGRule[n];
		for(int i = 0; i < n; i++) {
			CRGRule r = (CRGRule)v.get(i);
			if(onlyUsed && r.m_isActive || !onlyUsed) {
				res[i] = (CRGRule)v.get(i);
			}
		}
		return res;
	}

	public CRGRule[] getRulesForYear(int year, String userLogin, long userId, long[] userRoles,
		int what) throws Exception
	{
		Vector v = getRulesVectorYear(year, userLogin, userId, userRoles, what);
		CRGRule[] res = null;
		int n = v != null ? v.size() : 0;
		res = new CRGRule[n];
		for(int i = 0; i < n; i++) {
			res[i] = (CRGRule)v.get(i);
		}
		return res;
	}

	public Vector getRulesVectorYear(int year, String userLogin, long userId, long[] userRoles,
		int what) throws Exception
	{
		Vector pools = getPoolsForYear(year, userLogin, userId, userRoles);
		Vector v = new Vector();
		for(int i = 0, n = pools.size(); i < n; i++) {
			v.addAll(getRulesListForPool(((CRGRulePool)pools.elementAt(i)).crgpl_identifier, year, what));
		}
		return v;
	}

	private Vector getPoolsForYear(int year, String userLogin, long userId, long[] userRoles) throws Exception
	{
		Vector yearPools = new Vector();
		Vector pools = null;
		Object obj = m_htUserPoolsForYear.get(new Integer(year));
		if(obj != null && obj instanceof Vector) {
			return(Vector)obj;
		}
		if(userLogin != null) {
			obj = m_htUserPoolsForYear.get(userLogin + "_" + year);
		} else {
			obj = m_htUserPoolsForYear.get("_" + year);
		}

		if(obj != null && obj instanceof Vector) {
			return(Vector)obj;
		}

		try {
			pools = this.getRulePools(userId, userRoles);
		} catch(RemoteException ex) {
			throw new CRGRuleGroupException("error by load Pools", ex);
		}
		if(pools != null) {
			for(int i = 0, n = pools.size(); i < n; i++) {
				obj = pools.get(i);
				if(obj != null && obj instanceof CRGRulePool && ((CRGRulePool)obj).crgpl_year == year) {
					yearPools.add(obj);
				}
			}
			if(userLogin != null) {
				m_htUserPoolsForYear.put(userLogin + "_" + year, yearPools);
			} else {
				m_htUserPoolsForYear.put("_" + year, yearPools);
			}
		}
		return yearPools;
	}

	protected static Document getXMLDocument(InputStream in) throws Exception
	{
		if(in != null) {
			try {
				System.setProperty("javax.xml.parsers.DocumentBuilderFactory",
					"org.apache.xerces.jaxp.DocumentBuilderFactoryImpl");
				DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
				DocumentBuilder db = dbf.newDocumentBuilder();
				Document doc = db.parse(in);
				return doc;
			} catch(Exception ex) {
				throw new CRGRuleGroupException("error by building XML Document", ex);
			}
		}
		return null;
	}

	public Vector getAllRulesList(long userid, long roleid) throws Exception
	{
		Vector v = this.getRulePools(userid, roleid);
                return getRulesFromPoolVector(v);
	}

	public Vector getAllRulesList(long userid, long[] roles) throws Exception
	{
		Vector v = this.getRulePools(userid, roles);
                return getRulesFromPoolVector(v);
	}
        
        private Vector getRulesFromPoolVector(Vector pools) throws Exception
        {
            
 		int n = pools != null ? pools.size() : 0;
		Vector res = new Vector();
		for(int i = 0; i < n; i++) {
			CRGRulePool rpool = (CRGRulePool)pools.get(i);
			CRGRule[] rules = this.getRulesForPool(rpool.crgpl_identifier, rpool.crgpl_year);
			res.addAll(Arrays.asList(rules));
		}
		return res;
       }

	public CRGRule[] getAllRules(long userid, long roleid) throws Exception
	{
		Vector v = this.getAllRulesList(userid, roleid);
		CRGRule[] res = new CRGRule[v.size()];
		v.toArray(res);
		return res;
	}

	public CRGRule[] getAllRules(long userid, long[] roles) throws Exception
	{
		Vector v = this.getAllRulesList(userid, roles);
		CRGRule[] res = new CRGRule[v.size()];
		v.toArray(res);
		return res;
	}

	public CRGRule[] getAllRules(long userid, long[] roles, Date check, Date from, Date to, int what) throws Exception
	{
		int checkYear = 0;
		long checkTime = 0;
		try {
			if(m_calendar == null) {
				m_calendar = new GregorianCalendar();
			}
			m_calendar.setTime(check);
			checkYear = m_calendar.get(Calendar.YEAR);
			checkTime = check.getTime();
		} catch(Exception e) {
		  /* 3.9.5 2015-09-02 DNi: #FINDBUGS - Exception wird unterdrückt! */
		}
		Vector v = this.getRulePools(userid, roles);
		int n = v != null ? v.size() : 0;
		Vector res = new Vector();
		for(int i = 0; i < n; i++) {
			CRGRulePool rpool = (CRGRulePool)v.get(i);
//			if(rpool.crgpl_year == 0 || (rpool.crgpl_year >= fromYear && rpool.crgpl_year <= toYear)){
			if(rpool.crgpl_year == 0 || checkYear == 0 || (rpool.crgpl_year == checkYear)) {
				CRGRule[] rules = getRulesForPool(rpool.crgpl_identifier, rpool.crgpl_year, check, checkTime, what);
				res.addAll(Arrays.asList(rules));
			}
		}
		CRGRule[] ress = new CRGRule[res.size()];
		res.toArray(ress);
		return ress;
	}

	/**
	 *
	 * @throws Exception
	 */
	/*	protected synchronized static void fillRulesArray() throws Exception
	 {
	  int poolSize = m_rulesPool != null ? m_rulesPool.length : 0;
	  int ruleSize = 0;
	  for(int i = 0; i < poolSize; i++) {
	   if(m_rulesPool[i] != null) {
	 ruleSize += m_rulesPool[i].length;
	   } //else
	   //break;
	  }
	  m_ruleSize = ruleSize;
	  m_rules = new CRGRule[ruleSize];
	  int index = 0;
	  for(int i = 0; i < poolSize; i++) {
	   if(m_rulesPool[i] != null) {
	 ruleSize = m_rulesPool[i].length;
	 for(int j = 0; j < ruleSize; j++) {
	  m_rules[index++] = m_rulesPool[i][j];
	 }
	   } //else
	   //break;
	  }
	 }
	 */
	protected synchronized static CRGRule[] getRulesForDocument(Document doc, String identifier,
		int year) throws Exception
	{
		CRGRule[] result = null;
		try {
			if(doc != null) {
				NodeList nl = doc.getElementsByTagName(DatCaseRuleAttributes.ELEMENT_RULES);
				int nlCount = nl != null ? nl.getLength() : 0;
				result = new CRGRule[nlCount];
				for(int i = 0; i < nlCount; i++) {
					Element ele = (Element)nl.item(i);
					CRGRule rule = new CRGRule(ele, identifier, new Vector(0));
					result[i] = rule;
				}
			}
		} catch(Exception ex) {
			throw new CRGRuleGroupException("error by reading filling rules array from document " + identifier, ex);
		}
		return result;

	}

	protected static synchronized Vector getPoolIndexVector()
	{
		if(m_rulesIndex == null) {
			m_rulesIndex = new Vector(MAX_POOLS);
		}
		return m_rulesIndex;
	}

	protected static synchronized Vector getYearVector()
	{
		if(m_years == null) {
			m_years = new Vector();
		}
		return m_years;
	}

	protected static synchronized void setPoolIndex(int index, String identifier, int year) throws Exception
	{
		Vector v = getPoolIndexVector();
		v.add(index, identifier);
		Integer iv = new Integer(year);
		v = getYearVector();
		if(!v.contains(iv)) {
			v.add(iv);
		}
	}

	protected static synchronized int getPoolIndex(String identifier) throws Exception
	{
		Vector v = getPoolIndexVector();
		int ind = v.indexOf(identifier);
		if(ind <= 0) {

		}
		return ind;
	}

	public void writeXMLDocument(Document doc, int year, String identifier)
	{
		writeXMLDocument(doc, year, 0, identifier);
	}

	/*
	 public static synchronized CRGRule[] getRulesForPool(String identifier) throws Exception
	 {
	  int index = getPoolIndex(identifier);
	  if(index >= 0) {
	   CRGRule[][] rulePool = getRulePool();
	   if(rulePool[index] != null) {
	 return rulePool[index];
	   }
	  }
	  return null;
	 }
	 */
	protected Document getXMLDocument(int year) throws Exception
	{
		return getXMLDocument(String.valueOf(year), year);
	}

	protected Document getXMLDocument(String identifier, int year) throws Exception
	{
		try {
			System.setProperty("javax.xml.parsers.DocumentBuilderFactory",
				"org.apache.xerces.jaxp.DocumentBuilderFactoryImpl");
			DocumentBuilderFactory dbf = DocumentBuilderFactory.
										 newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.newDocument();
			try {
				Element root = doc.createElement(
							   DatCaseRuleAttributes.ELEMENT_CASE_RULES);
				CRGRule[] rules = this.getRulesForPool(identifier, year);
				int n = rules != null ? rules.length : 0;
				CRGRule rule;
				for(int i = 0; i < n; i++) {
					rule = rules[i];
					Element ele = doc.createElement(DatCaseRuleAttributes.ELEMENT_RULES);
					rule.getValuesByElement(ele, doc);
					rule.setSuggestion(ele);
					root.appendChild(ele);
				}
				doc.appendChild(root);
			} catch(Exception exce) {
				ExcException.createException(exce);
			}
			return doc;
		} catch(Exception ex) {
			throw new CRGRuleGroupException("error by creating XML-Document", ex);
		}
	}

	/*	protected static CRGRule[][] getRulePool()
	 {
	  if(m_rulesPool == null) {
	   m_rulesPool = new CRGRule[MAX_POOLS][];
	  }
	  return m_rulesPool;
	 }
	 */
	public CRGRule getCRGRulesByID(String rid) throws Exception
	{
		String yy = rid.substring(rid.length() - 4);
		int year = Integer.parseInt(yy);
		CRGRule[] rules = getRulesForPool(String.valueOf(year), year);
		int n = rules != null ? rules.length : 0;
		for(int i = 0; i < n; i++) {
			if(rules[i].m_rid.equals(rid)) {
				return rules[i];
			}
		}
		return null;
	}

    @Override
	public CRGRuleTypes getRulesErrorTypeByText(String text) throws Exception
	{
		List ruleTypes = getRuleTypes();
		if(ruleTypes != null && text != null) {
			for(int i = 0, n = ruleTypes.size(); i < n; i++) {
				CRGRuleTypes rt = (CRGRuleTypes)ruleTypes.get(i);
				if(rt.getText().equals(text)) {
					return rt;
				}
			}
		}
		return null;
	}

    @Override
	public CRGRuleTypes getRulesErrorTypeByText(String text, List usingTypeList) throws Exception
	{
		if(usingTypeList != null && text != null) {
			for(int i = 0, n = usingTypeList.size(); i < n; i++) {
				if(usingTypeList.get(i) != null && usingTypeList.get(i) instanceof CRGRuleTypes) {
					CRGRuleTypes rt = (CRGRuleTypes)usingTypeList.get(i);
					if(rt.getText().equals(text)) {
						return rt;
					}
				}
			}
		}
		return null;
	}

	/**
	 * liefert regel für den pool ind rolle
	 */
	public Vector getRules(long roleid, int year) throws Exception
	{
		return getRules(roleid, String.valueOf(year), year);
	}

	public Vector getRules(long roleid, String identifier, int year) throws Exception
	{
		Vector v = new Vector();
		CRGRule[] rules = getRulesForPool(identifier, year);
		int n = rules != null ? rules.length : 0;
		for(int i = 0; i < n; i++) {
			if(rules[i].isForRole(roleid)) {
				v.add(rules[i]);
			}
		}
		return v;
	}

	public abstract boolean refreshXMLFile(String user, long usrId, long[] allRoleIds, int year, String identifier) throws Exception;

	public void resetRulerInterface(long usrId)
	{
		Long usr = new Long(usrId);
		if(rulerForUserUpdated.contains(usr)) {
			rulerForUserUpdated.remove(usr);
		}
	}

	public byte[] createRulePoolDocument(String v_Identifier, int v_Year) throws Exception
	{
		byte[] docResult = new byte[0];
		Document doc = this.createNewRulePoolDocument();
		docResult = getXMLByteArray(doc, "UTF-16");
		this.saveRuleFileForPool(doc, v_Identifier, v_Year, "system", null);
		return docResult;

	}

	protected Document createNewRulePoolDocument() throws Exception
	{
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document doc = db.newDocument();
		Element root = doc.createElement("caseRules");
		//root.setNodeValue("caseRules");
		root.setAttribute("maxrules", "0");
		doc.appendChild(root);
		return doc;
	}

	/**
	 * lädt die Regel in den Regelkern
	 * @param ruleCRG CheckpointRuleGrouper
	 * @param identifier string
	 * @param year int
	 * @param ruleList List
	 * @throws Exception exc
	 */
	public void loadRulesInRuleGrouper(CheckpointRuleGrouper ruleCRG, String identifier, int year, 
		java.util.List ruleList) throws Exception
	{
		if(ruleCRG != null) {
			ruleCRG.m_rules = null;
			if(ruleList == null){
				ruleCRG.resetRulesArrays(identifier, year);
				ruleCRG.importRulesFromList(identifier, year, ruleList);
			}
//                        else{
//				ruleCRG.resetRulesArrays();
//				Vector v = this.getRulePools(user.getID(), user.getAllRoleIDs());
//				for(int i = 0; i < v.size(); i++) {
//					CRGRulePool pool = (CRGRulePool)v.elementAt(i);
//					ruleCRG.importRulesFromList(pool.crgpl_identifier, pool.crgpl_year, ruleList);
//				}
//
//			}
			ruleCRG.distributeRules();

		}
	}


	public String insertRule(CRGRule rule, int year) throws Exception
	{
		return insertRule(rule, year, String.valueOf(year));
	}

	public int getMaxRuleID() throws Exception
	{
		return getRuleSequence();
	}

	public String insertRule(CRGRule rule, int year, String identifier) throws Exception
	{
		try {
			int maxID = getMaxRuleID();
			String rid = String.valueOf(++maxID) + String.valueOf(year);
			CRGRule[] rules = this.getRulesForPool(identifier, year);
			if(rules == null) {

			}
			int n = rules != null ? rules.length : 0;
			for(int i = 0; i < n; i++) {
				if(rules[i] == null) {
					rules[i] = (CRGRule)rule;
					break;
				}
			}
			rule.m_rid = rid;
			return rid;
		} catch(Exception ex) {
			ExcException.createException(ex);
			throw ex;
		}
	}

	public Vector getRulePools(long userid, long roleid) throws Exception
	{
		long[] roleIds = new long[1];
		roleIds[0] = roleid;
		return getRulePools(userid, roleIds);
	}

	public Vector getRulePoolsFromPath() throws Exception
	{
		String rulePath, rulesPath = getRulesPath();
		if(!rulesPath.endsWith(File.separator)) {
			rulesPath += File.separator;
		}
		File ruleDir = new File(rulesPath);
		if(!ruleDir.exists()) {
			throw new CRGRuleGroupException("root-path (rulePath) " + rulesPath + " does not exists");
		}
		String[] subDirs = ruleDir.list();
		int sdSz = subDirs != null ? subDirs.length : 0;
		if(sdSz > 0) {
			Vector v = new Vector();
			for(int i = 0; i < sdSz; i++) {
				File f = new File(rulesPath + subDirs[i]);
				if(f.isDirectory()) {
					rulePath = rulesPath + subDirs[i] + File.separator;
					CRGRulePool rpool = new CRGRulePool();
					v.add(rpool);
					rpool.id = i;
					rpool.crgpl_active = true;
					rpool.crgpl_identifier = subDirs[i];
					String year = subDirs[i];
					if(year.contains("_")){
						String[] parts = year.split("_");
						year = parts[0];
						rpool.crgpl_identifier = parts[1];
					}
					try {
						rpool.crgpl_year = Integer.parseInt(year);
					} catch(Exception ex) {
						ExcException.createException(ex);
					}
				}
			}
			return v;
		} else {
			throw new CRGRuleGroupWarning("Root path (rulePath) " + rulesPath + " has noch child folders");
		}
		//return null;
	}

	public static String getWebServerPath()
	{
		if(m_webServerPath == null) {
			m_webServerPath = "site";
		}
		return m_webServerPath;
	}

	protected static String getRulesPath()
	{
		if(m_rulesPath == null) {
			m_rulesPath = getWebServerPath() + File.separator + "rules";
			File aDir = new File(m_rulesPath);
			if(!aDir.exists()) {
				aDir.mkdir();
			}
		}
		return m_rulesPath;
	}

/**
	 *
	 * wird nur auf dem Client benutzt in RuleSuiteMgr verlegt
	public String createRuleIdentifier(int year, String rulepool, String number) throws RemoteException
	{
		try {
			int tryIdent = Integer.parseInt(rulepool);
			if(tryIdent == 0) {
				rulepool = CRGRulePool.GLOBAL_POOL_NAME;
			}
		} catch(Exception e) {}
		String identNo = rulepool + "_" + number + "_";
		java.sql.Date dt = null;

		dt = new java.sql.Date(System.currentTimeMillis());
		SimpleDateFormat m_simpleDateFormat = new SimpleDateFormat("MMdd");
		SimpleDateFormat m_simpleTimeFormat = new SimpleDateFormat("hhmmss");
		String date = m_simpleDateFormat.format(dt);
		NumberFormat nf = new DecimalFormat("0000");
		date = nf.format(year) + date;
		String time = m_simpleTimeFormat.format(dt);
		identNo += date + time;
		return identNo;
	}
*/
	public Vector getIntTableNamesList()
	{
		Set s = m_htTableInt.keySet();
		if((s != null) && (s.size() > 0)) {
			return new Vector(s);
		}
		return new Vector();
	}

	public Vector getStringTableNamesList()
	{
		Set s = m_htTableString.keySet();
		if((s != null) && (s.size() > 0)) {
			return new Vector(s);
		}
		return new Vector();

	}

	public Vector getDoubleTableNamesList()
	{
		Set s = m_htTableDouble.keySet();
		if((s != null) && (s.size() > 0)) {
			return new Vector(s);
		}
		return new Vector();

	}

	public Vector getDatesTableNamesList()
	{
		Set s = m_htTableDates.keySet();
		if((s != null) && (s.size() > 0)) {
			return new Vector(s);
		}
		return new Vector();

	}

	public void saveRuleForPool(CRGRule chgRule, String identifier, int year, long[] roleIDs) throws Exception
	{
		chgRule.setChanged(true);
		Vector changedRules = this.getChangedRules(identifier, year);
		if(changedRules != null ){
			int ind = changedRules.indexOf(chgRule);
			if(ind >= 0){
				CRGRule oldRule = (CRGRule)changedRules.elementAt(ind);
				if(chgRule.m_rolesVisibleChanged){
					boolean changed = chgRule.mergeRolesVisible(oldRule.m_rolesVisible, roleIDs);
					chgRule.m_rolesVisibleChanged = chgRule.m_rolesVisibleChanged || changed;

					if(changed)
						chgRule.setNoVisible(oldRule.m_rolesVisible, oldRule.m_rolesNoVisible);
				} else{
					//die flags aus der alter Regel übernehmen
					chgRule.m_rolesVisible = oldRule.m_rolesVisible;
					chgRule.m_rolesNoVisible = oldRule.m_rolesNoVisible;
					chgRule.m_rolesVisibleChanged = oldRule.m_rolesVisibleChanged;
				}
				changedRules.setElementAt(chgRule, ind);
			}
			else {
				if(chgRule.m_rolesVisibleChanged) // die Regel wurde von dem aktuellen Benutzer geändert, der den Flag visible mit den Rollen roleIDs angefasst hat
					chgRule.setNoVisible(roleIDs, null);
				changedRules.addElement(chgRule);
			}
		}
		saveRuleForPoolTheme(chgRule, identifier, year, CRGRule.CHECK_ALL);
		if(chgRule.isCPRule()) {
			saveRuleForPoolTheme(chgRule, identifier, year, CRGRule.CHECK_CP_ONLY);
		}
		if(chgRule.isRSARule()) {
			saveRuleForPoolTheme(chgRule, identifier, year, CRGRule.CHECK_MRSA_ONLY);
		}
		if(chgRule.isGKRule()) {
			saveRuleForPoolTheme(chgRule, identifier, year, CRGRule.CHECK_GK);
		}

	}

	protected void saveRuleForPoolTheme(CRGRule chgRule, String identifier, int year, int what) throws Exception
	{
		Vector v = this.getRulesListForPool(identifier, year, null, 0, what);
		int n = v != null ? v.size() : 0;
		CRGRule oldRule;
		for(int i = 0; i < n; i++) {
			oldRule = (CRGRule)v.get(i);
			if(oldRule.m_rid.equals(chgRule.m_rid)) {
				//v.remove(oldRule);
				v.setElementAt(chgRule, i);
				return;
			}
		}
    /* 3.9.5 2015-09-01 DNi: #FINDBUGS - Potenzielle NP-Exception, weil v != null nicht abgefragt wird.  */
		v.add(chgRule);

	}

	public void deleteRulesForPool(Vector deleteList, String identifier, int year) throws Exception
	{
		Vector vAll = this.getRulesListForPool(identifier, year, null, 0, CRGRule.CHECK_ALL);
		Vector vcp = this.getRulesListForPool(identifier, year, null, 0, CRGRule.CHECK_CP_ONLY);
		Vector vrsa = this.getRulesListForPool(identifier, year, null, 0, CRGRule.CHECK_MRSA_ONLY);
		Vector vgk = this.getRulesListForPool(identifier, year, null, 0, CRGRule.CHECK_GK);
		Vector deletedRules = getDeletedRules(identifier, year);
		int n = deleteList != null ? deleteList.size() : 0;
		for(int i = 0; i < n; i++) {
			CRGRule delRule = (CRGRule)deleteList.get(i);
			if(delRule == null)
				continue;
			deleteRulesForPoolTheme(delRule, vAll);
			if(this.m_myAppServer != null //&& this.m_otherAppServer != null
				&& deletedRules != null && !deletedRules.contains(delRule)) {
				deletedRules.addElement(delRule);
			}
			if(delRule.isCPRule()) {
				deleteRulesForPoolTheme(delRule, vcp);
			}
			if(delRule.isRSARule()) {
				deleteRulesForPoolTheme(delRule, vrsa);
			}
			if(delRule.isGKRule()) {
				deleteRulesForPoolTheme(delRule, vgk);
			}
		}
	}

	protected void deleteRulesForPoolTheme(CRGRule delRule, Vector v) throws Exception
	{
		if(v == null) {
			return;
		}
		/*		for(int j = 0; j < v.size(); j++) {
		  CRGRule oldRule = (CRGRule)v.get(j);
		  if(oldRule.m_rid.equals(delRule.m_rid)) {
		   v.remove(oldRule);
		   break;
		  }
		 }*/
		v.remove(delRule); // rule1 == rule2 when rule1.m_rid == rule2.m_rid sieh. CRGRule
	}

	protected Vector getDeletedRules(String identifier, int year){
            return getDeletedRules(identifier, year, false);
        }
/**
 * 
 * @param identifier
 * @param year
 * @param toRemove - wenn true, wird der Vector aus dem Hashmap entfernt
 * @return 
 */        
	protected Vector getDeletedRules(String identifier, int year, boolean toRemove)
	{
		if(this.m_myAppServer == null //|| this.m_otherAppServer == null
			) {
			return null;
		}
		try {
			this.getCommunicationPools();
		} catch(Exception e) {
			return null;
		}
		String key = identifier + "_" + String.valueOf(year);
		if(m_communicationPools == null || !m_communicationPools.contains(key)) {
			return null;
		}
    /* 3.9.5 2015-09-01 DNi: #FINDBUGS - Wegen der Initialisierung des statischen Feldes m_deletedRules sollte die Methode synchronized sein. */
		if(this.m_deletedRules == null) {
			m_deletedRules = new HashMap();
		}
		Object o = m_deletedRules.get(key);
		if(o == null) {
			Vector v = new Vector();
			m_deletedRules.put(key, v);
			return v;
		}else{
                    if(o != null && toRemove){
                      return (Vector)m_deletedRules.remove(key);
                    }
                }
		return(Vector)o;

	}

	protected Vector getChangedRules(String identifier, int year){
            return  getChangedRules(identifier, year, false);
        }
        /**
         * 
         * @param identifier
         * @param year
         * @param toRemove wenn true, wird der Vector aus dem Hashmap entfernt
         * @return 
         */
	protected Vector getChangedRules(String identifier, int year, boolean toRemove)
	{
		if(this.m_myAppServer == null// || this.m_otherAppServer == null
			) {
			return null;
		}
		try {
			this.getCommunicationPools();
		} catch(Exception e) {
			return null;
		}
		String key = identifier + "_" + String.valueOf(year);
		if(m_communicationPools == null || !m_communicationPools.contains(key)) {
			return null;
		}
    /* 3.9.5 2015-09-01 DNi: #FINDBUGS - Wegen der Initialisierung des statischen Feldes m_changedRules sollte die Methode synchronized sein. */
		if(this.m_changedRules == null) {
			m_changedRules = new HashMap();
		}
		Object o = m_changedRules.get(key);
		if(o == null) {
			Vector v = new Vector();
			m_changedRules.put(key, v);
			return v;
		}else{

                    if(o != null && toRemove){
                      return (Vector)m_changedRules.remove(key);
                    }                    
                }
		return(Vector)o;

	}

	protected void resetDeletedRuleId(String identifier, int year)
	{
		String key = identifier + "_" + String.valueOf(year);
		if(this.m_deletedRules == null) {
			return;
		}
		m_deletedRules.remove(key);
	}

	protected void resetChangedRules(String identifier, int year)
	{
		String key = identifier + "_" + String.valueOf(year);
		if(this.m_changedRules == null) {
			return;
		}
		m_changedRules.remove(key);
	}

	public int isMedicineAllowed()
	{
		return m_isMedAllowed;

	}
        
        public int isSoleAllowed()
	{
		return m_isSoleAllowed;

	}
        
        public Vector getRuleFilter()
	{
		return m_ruleFilter;

	}
        
	public int isRuleTest()
	{
		return m_isRuleTest;
	}

	public int isCheckpoint_RuleGrouper()
	{
		return m_isCheckpoint_RuleGrouper;
	}

	public int isCrossCaseModelAllowed()
	{
		return m_isCrossCaseModelAllowed;
	}

	public int isLaborAllowed()
	{
		return m_isLaborAllowed;
	}

	public int isCareDataAllowed()
	{
		return m_isCareDataAllowed;
	}

	public int isGKConstruct()
	{
		return m_isGKRuleConstruct;
	}

	public String getXMLString(Document doc) throws CRGRuleGroupException
	{
		try {

			ByteArrayOutputStream out = new ByteArrayOutputStream();
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer trans = tf.newTransformer();

			trans.setOutputProperty(OutputKeys.ENCODING, "UTF-16");
			Source input = new DOMSource(doc);
			Result output = new StreamResult(out);
			trans.transform(input, output);
			return new String(out.toByteArray(), "UTF-16");
		} catch(Exception ex) {
			throw new CRGRuleGroupException("error by transform rsult string", ex);
		}
	}

	public String getXMLString(Document doc, String charcode) throws CRGRuleGroupException
	{
		try {
			XMLDOMWriter writer = new XMLDOMWriter(false);
			java.io.StringWriter os = new java.io.StringWriter();
			PrintWriter pw = new PrintWriter(os);
			writer.setWriterEncoding(charcode);
			writer.print(doc, pw, false);
			return os.toString();
		} catch(Exception ex) {
			throw new CRGRuleGroupException("error by transform rsult string", ex);
		}
	}

	/**
	 * erzeugt ein XML - Document aud einem Regelarray
	 * @param rules CRGRule[]
	 * @return Document
	 */
	protected Document getRulesDocumentForRules(CRGRule[] rules) throws Exception
	{
		return getRulesDocumentForRules(rules, false);
	}

	protected Document getRulesDocumentForRules(CRGRule[] rules, boolean doThrow) throws Exception
	{
		int id = rules.length;
		System.setProperty("javax.xml.parsers.DocumentBuilderFactory",
			"org.apache.xerces.jaxp.DocumentBuilderFactoryImpl");
		Document doc = null; // Document muss aus den CRGRule[] erzeugt werden
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			doc = db.newDocument();
			Element root = doc.createElement(DatCaseRuleAttributes.ELEMENT_CASE_RULES);
			doc.appendChild(root);
			Element ele = null;
			for(int i = 0, n = rules.length; i < n; i++) {
				CRGRule rule = rules[i];
				ele = doc.createElement(DatCaseRuleAttributes.ELEMENT_RULES);
				ele.setAttribute(DatCaseRuleAttributes.ATT_RULES_ID, rule.m_rid);
				rule.getValuesByElement(ele, doc);
				root.appendChild(ele);

			}
			root.setAttribute(DatCaseRuleAttributes.ATT_CASE_RULES_MAX, String.valueOf(id));
		} catch(Exception exce) {
			if(doThrow)
				throw exce;
			else
				ExcException.createException(exce);
		}
		return doc;
	}

	public byte[] getXMLByteArray(Document doc, String charcode) throws CRGRuleGroupException
	{
		try {
			XMLDOMWriter writer = new XMLDOMWriter(false);
			java.io.ByteArrayOutputStream os = new java.io.ByteArrayOutputStream();
			PrintWriter pw = new PrintWriter(new OutputStreamWriter(os, charcode));
			writer.setWriterEncoding(charcode);
			writer.print(doc, pw, false);
			return os.toByteArray();
		} catch(Exception ex) {
			throw new CRGRuleGroupException("error by transform rsult string", ex);
		}
	}

	/**
	 * erzeugt ein XML - Document aus den für den Benutzer/Rolle zugelassenen Regel
	 * und gibt ihn als byte array zurück
	 */
	public byte[] getXMLDocumentForUserRole(String poolId, int year, long userid, long roleid) throws Exception
	{
		CRGRule[] rules = this.getRulesForPool(poolId, year);
		if(rules != null) {
			Document doc = getRulesDocumentForRules(rules);
			return getXMLByteArray(doc, "UTF-16");
		} else {
			return null;
		}
	}

	/**
	 * erzeugt ein XML - Document aus den für den Benutzer/Rolle zugelassenen Regel
	 * und gibt ihn als byte array zurück
	 */
	public byte[] getXMLDocumentForUserRole(String poolId, int year, long userid, long[] roleid) throws Exception
	{
		Vector v = this.getRulePools(userid, roleid); // Liste der Pools, die Benutzer+Rolle sehen/bearbeiten darf
		int n = v != null ? v.size() : 0;
		CRGRule[] rules = null;
		for(int i = 0; i < n; i++) {
			CRGRulePool rpool = (CRGRulePool)v.get(i);
			if(poolId.equals(rpool.crgpl_identifier) && (year == rpool.crgpl_year)) {
				// Regel werden geholt nur wenn dieser Pool  Benutzer+Rolle dafür berechtigt ist
				rules = this.getRulesForPool(rpool.crgpl_identifier, rpool.crgpl_year);
				break;
			}
		}
		if(rules != null) {
			Document doc = getRulesDocumentForRules(rules);
			return getXMLByteArray(doc, "UTF-16");
		} else {
			return null;
		}
	}

	/**
	 * erzeugt ein XML - Document aus den für den Benutzer/Rolle zugelassenen Regel
	 * und gibt ihn als byte array zurück
	 */
	public byte[] getXMLDocumentForUserRole(String poolId, int year, boolean onlyUsed) throws Exception
	{
		CRGRule[] rules = null;
		// Regel werden geholt nur wenn dieser Pool  Benutzer+Rolle dafür berechtigt ist
		rules = this.getRulesForPool(poolId, year);
		if(rules != null) {
			Document doc = getRulesDocumentForRules(rules);
			return getXMLByteArray(doc, "UTF-16");
		} else {
			return null;
		}
	}

	protected String getKeyForTablesHash(String ident, int year, String name)
	{
		return ident + "+" + String.valueOf(year) + "+" + name.replaceAll( "'", "");

	}

	/**
	 * liefert eine Regel aus dem Jahr year mit rid
	 * @param year int
	 * @param rid String
	 * @return Object
	 */
	public Object getRuleByID(int year, String rid, String userLogin, long userId, long[] userRoles, int what)
	{
		try {
			//CRGRule[] rules = getRulesForPool(String.valueOf(year), year);
			Vector rules = getRulesVectorYear(year, userLogin, userId, userRoles, what);
			if(rules == null) {
				return null;
			}
			for(int i = 0; i < rules.size(); i++) {
				CRGRule rule = (CRGRule)rules.elementAt(i);
				if(rule.m_rid.equals(rid)) {
					CRGRule r = rule;
//					r.m_element = null;
					return r;
				}
			}
		} catch(Exception e) {
			ExcException.createException(e);
		}
		return null;
	}


	/**
	 * liefert eine Regel für einen user, dabei wird Regeljahr nicht beachtet, alle regelpools des Benutzers werden durchsucht
	 * @param rid String
	 * @return Object
	 */
	public CRGRule getRuleByID(String rid, String userLogin, long userId, long[] userRoles, int what)
	{
		try {
			Vector pools = this.getRulePools(userId, userRoles);
			for(int i = 0; i < pools.size(); i++) {
				CRGRulePool pool = (CRGRulePool)pools.elementAt(i);
				Object obj = this.getRuleByID(pool.crgpl_year, rid, userLogin, userId, userRoles, what);
				if(obj != null && obj instanceof CRGRule) {
					return(CRGRule)obj;
				}
			}
		} catch(Exception e) {
			ExcException.createException(e);
		}
		return null;
	}

	/**
 * liefert eine Regel zu einem Regelidemntifier
 * Identifier besteht aus 3 Teilen, die durch '_' getrennt sind.
 * Das erste Teil ist der Poolname.
 * @param ident String
 * @return Object
 */
	public CRGRule getRuleByIdentifier(String ident, int year)
	{
		try {
			String parts[] = ident.split("_");
			Vector rules = getRulesListForPool(parts[0], year, CRGRule.CHECK_ALL);
			for(int i = 0; i < rules.size(); i++) {
				CRGRule rule = (CRGRule)rules.elementAt(i);
				if(rule.getRuleNumber().equalsIgnoreCase(ident))
					return rule;

			}
		} catch(Exception e) {
			ExcException.createException(e);
		}
		return null;
	}

	public CRGRuleForComplaints getRuleByIdentifier(String ident)
	{
		Vector <Integer> years = getRuleYears();
		if(years == null)
			return null;
		for(int i = 0; i< years.size(); i++){
			Integer year = years.elementAt(i);
			CRGRule rule = getRuleByIdentifier(ident, year.intValue());
			if(rule != null){
//				rule.m_element = null;
				CRGRuleForComplaints retRule = new CRGRuleForComplaints(rule.m_caption, rule.m_suggestion);
				return retRule;
			}
		}
		return null;
	}


	/**
	 * liefert alle jahre zu den Regelpools
	 * wenn roleId =0, dann für alle rollen
	 */

	public Vector getRuleYears(long userId)
	{

		Vector ret = new Vector();
		try {
			Vector pools = this.getRulePools(userId, null);
			if(pools == null) {
				return ret;
			}
			for(int i = 0; i < pools.size(); i++) {
				CRGRulePool pool = (CRGRulePool)pools.elementAt(i);
				Integer year = new Integer(pool.crgpl_year);
				if(!ret.contains(year)) {
					ret.addElement(year);
				}

			}
			Collections.sort(ret);
		} catch(Exception e) {
			ExcException.createException(e);
		}
		return ret;
	}

	public int getRuleTypeIdWithErrTypeText(String typeTxt)
	{
		CRGRuleTypes rt = null;
		try {
			Vector v = getRuleTypes();
			int n = v != null ? v.size() : 0;
			for(int i = 0; i < n; i++) {
				if(v.get(i) != null && v.get(i) instanceof CRGRuleTypes) {
					rt = (CRGRuleTypes)v.get(i);
					if(rt.getText().equals(typeTxt)) {
						return rt.getIdent();
					}
				}
			}
		} catch(Exception ex) {
			ExcException.createException(ex);
		}
		return -1;
	}



    protected class RuleTableLoder extends Thread
	{
		long m_userId = 0;
		long[] m_rolls = null;
		RuleTableLoder(long userId, long[] rolls)
		{
			m_userId = userId;
			m_rolls = rolls;
		}

		public void run()
		{
			try {
				m_tablesLoaded = loadRulesTablesInThread(m_userId, m_rolls); //alle;
			} catch(Exception ex) {
				ExcException.createException(ex);

			}

		}
	}

	protected void resetThemePools(String identifier, int year)
	{
		/*		String key = identifier + "_" + String.valueOf(year);
		  if(	this.m_rulePools!= null){
//			m_rulePools.remove(key);
		  HashMap rules = (HashMap)m_rulePools.get(key);
		  if(rules != null){
		   Vector v = (Vector)rules.get(String.valueOf(CRGRule.CHECK_ALL));
		   rules.clear();
		   if(v != null)
			rules.put(String.valueOf(CRGRule.CHECK_ALL), v);

		  }
		 }*/
	}

	public String getM_myAppServer()
	{
		return m_myAppServer;
	}


	/**
	 * liefert den Vektor der unterschiedlichen Jahren, für die die Regelpools angelegt wurden
	 * @return Vector
	 */
	protected Vector<Integer> getRuleYears()
	{
    /* 3.9.5 2015-09-01 DNi: #FINDBUGS - Wegen der Initialisierung des statischen Feldes m_ruleYears sollte die Methode synchronized sein. */
		if(m_ruleYears == null){
			m_ruleYears = getPoolYears();
		}
		return m_ruleYears;
	}

	protected abstract Vector<Integer> getPoolYears();

	protected Vector<Integer> getRulePoolYearsFromPath()
	{
		Vector<Integer> years = new Vector();
		try{
			Vector v = this.getRulePoolsFromPath();
			if(v != null){
				for(int i = 0; i < v.size(); i++){
					CRGRulePool pool = (CRGRulePool)v.elementAt(i);
					Integer year = new Integer(pool.crgpl_year);
					if(!years.contains(year))
						years.addElement(year);
				}
			}
		}catch(Exception e){
			ExcException.createException(e);
		}
		return years;
	}

        public void resetRulesForUser(long userID)
        {
             Object obj = m_userPools.get(new Long(userID));
            if(obj == null || m_rulePools == null)
                return;
            Vector pools = (Vector)obj;
            for(int i = 0; i < pools.size(); i++){
                CRGRulePool pool = (CRGRulePool)pools.elementAt(i);
                String key = pool.crgpl_identifier + "_" + pool.crgpl_year;
                m_rulePools.remove(key);
            }
        }
}