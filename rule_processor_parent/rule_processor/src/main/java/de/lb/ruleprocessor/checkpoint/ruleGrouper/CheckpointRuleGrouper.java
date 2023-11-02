package de.lb.ruleprocessor.checkpoint.ruleGrouper;




import de.lb.ruleprocessor.checkpoint.drg.RulesCheckOut;
import de.lb.ruleprocessor.checkpoint.drg.RulesRefOut;
import de.lb.ruleprocessor.checkpoint.utils.UtlDateTimeConverter;
import java.lang.reflect.Constructor;
import java.util.*;
import java.util.logging.Logger;


/**
 * <p>Überschrift: Checkpoint Regelkern</p>
 * <p>Beschreibung: Klasse zur Ein- und Ausgabe der Prüfkriterien</p>
 *
 * <p>Copyright: Lohmann & Birkner Health Care Consulting GmbH</p>
 *
 * <p>Organisation: Lohmann & Birkner Health Care Consulting GmbH</p>
 *
 * @author Thomas Dehne
 * @version 2.0
 */
public class CheckpointRuleGrouper 
{

    private static final Logger LOG = Logger.getLogger(CheckpointRuleGrouper.class.getName());
    
	public static int MAX_CASE_COUNT = 100;
	public static int MAX_CASE_AFTER = MAX_CASE_COUNT / 2;
	public static int MAX_CASE_VOR = MAX_CASE_AFTER;
	protected static int m_formerMax = MAX_CASE_COUNT + MAX_CASE_VOR;

	private static int m_usedGrouperID = 3;

	//Objekt für die Übergabe der Daten und Auslesen der Ergebnisse
	protected CRGInputOutputBasic m_inout = null;
	protected CRGInputOutputBasic m_inoutSugg = null;

	
	//Array mit Regeln
	protected CRGRule[] m_rules = null;
	private int m_ruleSize = 0;
	private int[] m_formerSort = new int[m_formerMax];
	private int m_formerCount = 0;
	private int m_tmpFormerCount = 0;
	private CRGInputOutputBasic[] m_formerInOuts = new CRGInputOutputBasic[m_formerMax];
	private CRGInputOutputBasic[] m_tmpFormerInOuts = new CRGInputOutputBasic[m_formerMax]; // wird in den CC - Methoden benutzt, die nicht mehr benutzt werden
	private int m_formerAkt = -1;
	private int m_tmpFormerAkt = -1;

	private long m_ruleIntervalStart = 0;
	private long m_ruleIntervalEnd = 0;
	private boolean m_hasRuleInterval = false;

	protected static final int MAX_STATE = 100;
	protected CRGRule[][] m_allRulesWithYears = new CRGRule[MAX_STATE][]; // Regeln nach Jahren;
        // aus welchen Supergruppen sind die Regeln in dem Jahr vorhanden; 
        // Damit die Gruppen, die nicht fereigegeben wurden nicht angewendet werden
        // z.B. wenn fallübegr. Prüfung freigegeben wurde und es keine solche Regeln im Jahr vorhanden sind
        // werden die historischen Fälle nicht hinzugezogen
        Map<Integer, Map<Integer, Boolean>> mYear2RypeTypes = new HashMap<>(); 
	protected int[] m_allRulesWithYearsSize = new int[MAX_STATE];
	protected static int ALL_RULES_INDEX = 99;
	protected static int BASIC_YEAR = 2000;
	protected static int ALL_USED_INDEX = 98;
	protected static int FOR_2000_INDEX = 97;

	Calendar calc = new GregorianCalendar();
	//----------------------------------------------------
	// Unterschied zwischen 31.12 und 1.1. ist 86400000
	protected final long NEARLY_ONE_DAY = 86399999;
	protected final long ONE_DAY = 86400000;

	private int m_state = 0;
	private int durchlauf = 0;
	private int m_RefZaehler = -1;
	private int m_RefRule = -1;
	private static final int MIN_ARRAY_LENGTH = 500;
	private CRGRefElement[] m_RefEle = new CRGRefElement[MIN_ARRAY_LENGTH];
	private int[] m_RefIndex1 = new int[MIN_ARRAY_LENGTH];
	private int[] m_RefIndex2 = new int[MIN_ARRAY_LENGTH];
	private String[] m_RefWert = new String[MIN_ARRAY_LENGTH];
	private int[] m_RefIndex = new int[MIN_ARRAY_LENGTH];

//	private SQLConnection currentConnection = null;
	private long m_diagCaseID = -1;
	private int diagnoseCursorID = -1;

	private boolean m_isGK = true;

	private boolean m_isCrossCase = false;


	private boolean m_setCheckDateFromAdmDate = false;

	private Vector<String> m_corruptRules = new Vector<String>();
	private boolean isESKA = false;
        private CRGRuleManagerIF mRuleManager;
	/**
	 * Gibt das Ein- und Ausgabe-Objekt des Regelkerns zurück.
	 * <p>
	 * Hier kann das Ein- und Ausgabe-Objekt für das DRG-Grouping / MRSA-Grouping und der Regelprüfung abgefragt werden.<br>
	 * Es müssen vor Aufruf einer Perform-Methode alle Eingangsparameter übergeben werden.<br>
	 * Das Ergebnis wird ebenfalls als Variablen in dieser Klasse hinterlegt.<br>
	 * Das Objekt muss aus dieser Klasse mit dieser Methode geladen oder eine Instanz von aussen hinzugefügt werden (Methode setInout).
	 * @return CRGInputOutputBasic : Ein- und Ausgabe-Objekt
	 */
	public CRGInputOutputBasic getInout()
	{
		return m_inout;
	}

	public CRGInputOutputBasic getSuggInout()
	{
		return this.m_inoutSugg;
	}

	/**
	 * Setzen des Ein- und Ausgabe-Objekts.
	 * <p>
	 * Hier kann die Übergabe des Ein- und Ausgabe-Objekts für das DRG-Grouping / MRSA Grouping und der Regelprüfung erfolgen.<br>
	 * Es müssen vor Aufruf einer Perform-Methode alle Eingangsparameter übergeben werden.<br>
	 * Das Ergebnis wird ebenfalls als Variablen in dieser Klasse hinterlegt.<br>
	 * Das Objekt muss aus dieser Klasse geladen werden (Methode getInout) oder eine Instanz von aussen mit dieser Methode hinzugefügt werden.
	 * @param inout CRGInputOutputBasic
	 */
	public void setInout(CRGInputOutputBasic inout)
	{
		m_inout = inout;
	}

	/**
	 * Setzt die ID des zu verwendenen DRG Groupers.
	 * <p>
	 * ACHTUNG: statische Methode muss vor dem Konstruktur aufgerufen werden,
	 * ansonsten wird default der Checkpoint Grouper verwendet.<br><br>
	 * ID zur Verwendung des Kermanog-Groupers
	 * (nur notwendig, wenn nach 2004 gegroupt werden soll)<br>
	 * public static final int KERMANOG_GROUPER = 1;<br>
	 * Kermanog Grouper muss lokal installiert sein.
	 * <br><br>
	 * ID zur Verwendung des GTI-Groupers
	 * (nur notwendig, wenn nach 2003-2004 gegroupt werden soll)<br>
	 * public static final int GROUPIT_GROUPER = 2;<br>
	 * GTI Grouper muss lokal installiert sein.
	 * <br><br>
	 * ID des Checkpoint-Groupers (Default)<br>
	 * public static final int CHECKPOINT_GROUPER = 3;<br>
	 * @param usedGrouperID int : ID des zu verwendenen Groupers
	 */
	public static void setUsedGrouperID(int usedGrouperID)
	{
		m_usedGrouperID = usedGrouperID;
	}

	/**
	 * Gibt die ID des verwendeten DRG Groupers zurück.
	 * @return int : 1 - Kermanog Grouper, 2 - GTI-Grouper,	3 - Checkpoint Grouper (Default)

	 */
	public int getUsedGrouperID()
	{
		return m_usedGrouperID;
	}

	/**
	 * Konstruktor
     * @throws java.lang.Exception
	 */
	public CheckpointRuleGrouper(CRGRuleManagerIF ruleManager) throws Exception
	{
            mRuleManager = ruleManager;
            CRGRuleGrouperManager.instance();
            initRuler(null);
            

	}


	private void initRuler(long roles[]) throws Exception
	{
		try {
			/*m_rules = (CRGCheckpointGrouperFileManager.ruleManager()).getAllRulesFromPath(rulePath);
			 m_ruleSize = m_rules != null ? m_rules.length : 0;*/
			this.resetRulesArrays();
			m_allRulesWithYears[ALL_RULES_INDEX] = mRuleManager.getAllRules();
			distributeRules();
		} catch(CRGRuleGroupException e) {
			throw e;
		} catch(CRGRuleGroupWarning ex) {
			throw ex;
		} catch(Exception ex) {
			throw new CRGRuleGroupException("error by reading xml-Rules", ex);
		}
	}

	protected void resetRulesArrays()
	{
		for(int i = 0; i < this.MAX_STATE; i++) {
			m_allRulesWithYearsSize[i] = 0;
			m_allRulesWithYears[i] = null;
		}
		m_corruptRules = new Vector<String>();
	}

	protected void resetRulesArrays(String ident, int year)
	{
		if(m_isGK) {
			resetRulesArrays();
		} else {
			CRGRule[] newAll = new CRGRule[m_allRulesWithYears[ALL_RULES_INDEX].length];
			int j = 0;
			for(int i = 0; i < m_allRulesWithYears[ALL_RULES_INDEX].length; i++) {
				CRGRule rule = m_allRulesWithYears[ALL_RULES_INDEX][i];
				if(rule != null && (rule.getYear() != year || !rule.getRuleIdentifier().equals(ident))) {
					newAll[j] = rule;
					j++;
				}
			}
			m_allRulesWithYears[ALL_RULES_INDEX] = new CRGRule[j];
			System.arraycopy(newAll, 0, m_allRulesWithYears[ALL_RULES_INDEX], 0, j);
			m_allRulesWithYearsSize[ALL_RULES_INDEX] = j;
			for(int i = 0; i < ALL_RULES_INDEX; i++) {
				m_allRulesWithYearsSize[i] = 0;
				m_allRulesWithYears[i] = null;
			}
		}
		m_corruptRules = new Vector<String>();
	}

	public boolean hasCorruptRules()
	{
		return m_corruptRules.size() != 0;
	}

	public void freeFields()
	{
		try {
                    resetFormerCases();
                    m_inout = null;
                    resetRulesArrays();
		} catch(Exception e) {
		  /* 3.9.5 2015-09-02 DNi: #FINDBUGS - Exception wird unterdrückt! */
		}
	}

	protected void distributeRules()
	{
		if(m_allRulesWithYears[ALL_RULES_INDEX] == null) {
			return;
		}
		int ruleYear = 0;
		CRGRule rule = null;
                mYear2RypeTypes.clear();
		try {
			for(int i = 0; i < m_allRulesWithYears[ALL_RULES_INDEX].length; i++) {
				rule = m_allRulesWithYears[ALL_RULES_INDEX][i];
				if(rule.m_isCorrupt) {
					if(!this.m_corruptRules.contains(rule.m_ruleNumber)) {
						m_corruptRules.addElement(rule.m_ruleNumber);
					}
				} else {
// alle 'used'
					if(rule.m_isActive) {
						if(m_allRulesWithYears[this.ALL_USED_INDEX] == null) {
							m_allRulesWithYears[this.ALL_USED_INDEX] = new CRGRule[m_allRulesWithYears[ALL_RULES_INDEX].
								length +
								1];
						}
						m_allRulesWithYears[this.ALL_USED_INDEX][m_allRulesWithYearsSize[ALL_USED_INDEX]] = rule;
						m_allRulesWithYearsSize[ALL_USED_INDEX]++;
						ruleYear = 0;
						if(rule.m_adt != null) {
							calc.setTime(rule.m_adt);
							ruleYear = calc.get(Calendar.YEAR);
						} else if(rule.m_year > 0) {
							ruleYear = rule.m_year;
						}
						if(ruleYear > 0 && ruleYear >= BASIC_YEAR && ruleYear - BASIC_YEAR < ALL_USED_INDEX) { // index year - BASIC_YEAR
							if(m_allRulesWithYears[ruleYear - BASIC_YEAR] == null) {
								m_allRulesWithYears[ruleYear -
									BASIC_YEAR] = new CRGRule[m_allRulesWithYears[ALL_RULES_INDEX].length + 1];
							}
							m_allRulesWithYears[ruleYear - BASIC_YEAR][m_allRulesWithYearsSize[ruleYear -
								BASIC_YEAR]] = rule;
							m_allRulesWithYearsSize[ruleYear - BASIC_YEAR]++;
                                                        checkRuleUsage(rule, ruleYear);

						} else { //index - FOR_2000_INDEX
							if(m_allRulesWithYears[FOR_2000_INDEX] == null) {
								m_allRulesWithYears[FOR_2000_INDEX] = new CRGRule[m_allRulesWithYears[ALL_RULES_INDEX].
									length +
									1];
							}
							m_allRulesWithYears[FOR_2000_INDEX][m_allRulesWithYearsSize[FOR_2000_INDEX]] = rule;
							m_allRulesWithYearsSize[FOR_2000_INDEX]++;
						}

					}
				}
			}
		} catch(ArrayIndexOutOfBoundsException ex) {
			System.out.println("ruleYear = " + ruleYear);
			ex.printStackTrace();
		}
		;
	}

	private void prepareRulesForInout(CRGInputOutputBasic inout, Date poolDate)
	{
		int ruleYear = 0;
		if(m_isGK) {
			m_rules = m_allRulesWithYears[this.ALL_USED_INDEX];
			m_ruleSize = m_allRulesWithYearsSize[ALL_USED_INDEX];
			return;
		} 
//else if(inout.m_admissionDate != 0 || inout.m_admissionDate == 0 && poolDate != null) {
//			Long check = inout.m_admissionDate;
//			if(check == 0) {
//				check = poolDate.getTime();
//			}
//			calc.setTimeInMillis(check);
//			ruleYear = calc.get(Calendar.YEAR);
//		} else if(inout.m_checkYear > 0) {
//			ruleYear = inout.m_checkYear;
//		}
		if(ruleYear >= BASIC_YEAR) {
			m_rules = m_allRulesWithYears[ruleYear - BASIC_YEAR];
			m_ruleSize = m_allRulesWithYearsSize[ruleYear - BASIC_YEAR];
		} else {
			m_rules = m_allRulesWithYears[FOR_2000_INDEX];
			m_ruleSize = m_allRulesWithYearsSize[FOR_2000_INDEX];
		}
		/*		for(int i = 0; i < m_ruleSize; i++){
		   System.out.println(m_rules[i].m_number);
		  }*/
	}

	/**
	 * Lädt die Bibliothek RulerInterface
	 */
	static void loadJNILibrary() throws CRGRuleGroupException
	{
		try {
			System.loadLibrary("RulerInterface");
		} catch(Exception e) {
			throw new CRGRuleGroupException("error by load of library", e);
		}
	}


//
//	/**
//	 * Initialisiert die Regeln aus den Unterverzeichnissen des übergebenen Regel-Pfades.
//	 * @param SQLConnection conn: bestehende Datenbankverbindung
//	 */
//	private void initRuler(long userid, long[] userRoles, java.util.List ruleList) throws Exception
//	{
//		try {
//			CRGRuleManager.ruleManager().m_rulePools = null;
//			this.resetRulesArrays();
//			if(ruleList == null) {
//				//	m_rules = CRGRuleManager.ruleManager().getAllRules(userid, userRoles);
//				m_allRulesWithYears[ALL_RULES_INDEX] = CRGRuleManager.ruleManager().getAllRules(userid, userRoles);
//			} else {
//				CRGRule[] rules1 = null;
//				rules1 = CRGRuleManager.ruleManager().getAllRules(userid, userRoles);
//				int[] iRule = new int[rules1.length];
//				int anz = -1;
//				Iterator itr = ruleList.iterator();
//				while(itr.hasNext()) {
//					String rid = (String)itr.next();
//					for(int i = 0; i < rules1.length; i++) {
//						if(rules1[i].m_rid.equals(rid)) {
//							anz++;
//							iRule[anz] = i;
//							break;
//						}
//					}
//				}
//				//	m_rules = new CRGRule[anz + 1];
//				m_allRulesWithYears[ALL_RULES_INDEX] = new CRGRule[anz + 1];
//				for(int i = 0; i <= anz; i++) {
//					//	m_rules[i] = rules1[iRule[i]];
//					m_allRulesWithYears[ALL_RULES_INDEX][i] = rules1[iRule[i]];
//				}
//			}
//			//m_ruleSize = m_rules != null ? m_rules.length : 0;
//			distributeRules();
//		} catch(CRGRuleGroupException e) {
//			throw e;
//		} catch(CRGRuleGroupWarning ex) {
//			throw ex;
//		} catch(Exception ex) {
//			throw new CRGRuleGroupException("error by reading xml-Rules", ex);
//		}
//	}
//
	/**
	 * Über diese Methode kann eine Regelliste zum Regelkern hinzu gefügt werden.
	 * <p>
	 * Die Regelliste muss vom Typ CRGRule sein. <br>
	 * Der Identifier wird zur Erkennung der Regel-Sets benötigt, um diesen ggf. zu ersetzen.<br>
	 * Jedes Regel-Set wird einem Jahr zugeordnet.
	 * @param identifier String : Identifier des Regel-Sets
	 * @param year int : Jahr des Regel-Sets
	 * @param ruleList List : Liste der Regeln
	 * @throws Exception
	 */
	public void importRulesFromList(String identifier, int year, java.util.List ruleList) throws Exception
	{
		try {
			if(CRGRuleManager.ruleManager().m_rulePools != null) {
				String key = identifier + "_" + String.valueOf(year);
				if(CRGRuleManager.ruleManager().m_rulePools.get(key) != null) {
					CRGRuleManager.ruleManager().m_rulePools.remove(key);
				}
			}
			if(ruleList == null) {
				/*	if(m_rules != null) {
				  CRGRule[] rules1 = CRGRuleManager.ruleManager().getRulesForPool(identifier, year);
				  CRGRule[] rules2 = new CRGRule[m_rules.length];
				  rules2 = m_rules.clone();
				  m_rules = new CRGRule[rules1.length + rules2.length];
				  System.arraycopy(rules2, 0, m_rules, 0, rules2.length);
				  System.arraycopy(rules1, 0, m_rules, rules2.length, rules1.length);
				 } else {
				  m_rules = CRGRuleManager.ruleManager().getRulesForPool(identifier, year);
				 }*/
				if(m_allRulesWithYears[ALL_RULES_INDEX] != null) {
					CRGRule[] rules1 = CRGRuleManager.ruleManager().getRulesForPool(identifier, year);
					CRGRule[] rules2 = new CRGRule[m_allRulesWithYears[ALL_RULES_INDEX].length];
					rules2 = m_allRulesWithYears[ALL_RULES_INDEX].clone();
					m_allRulesWithYears[ALL_RULES_INDEX] = new CRGRule[rules1.length + rules2.length];
					System.arraycopy(rules2, 0, m_allRulesWithYears[ALL_RULES_INDEX], 0, rules2.length);
					System.arraycopy(rules1, 0, m_allRulesWithYears[ALL_RULES_INDEX], rules2.length, rules1.length);
				} else {
					m_allRulesWithYears[ALL_RULES_INDEX] = CRGRuleManager.ruleManager().getRulesForPool(identifier,
						year);
				}
			} else {
				CRGRule[] rules1 = CRGRuleManager.ruleManager().getRulesForPool(identifier, year);
				int[] iRule = new int[rules1.length];
				int anz = -1;
				Iterator itr = ruleList.iterator();
				while(itr.hasNext()) {
					String rid = (String)itr.next();
					for(int i = 0; i < rules1.length; i++) {
						if(rules1[i].m_rid.equals(rid)) {
							anz++;
							iRule[anz] = i;
							break;
						}
					}
				}
				//m_rules = new CRGRule[anz + 1];
				m_allRulesWithYears[ALL_RULES_INDEX] = new CRGRule[anz + 1];
				for(int i = 0; i <= anz; i++) {
					//m_rules[i] = rules1[iRule[i]];
					m_allRulesWithYears[ALL_RULES_INDEX][i] = rules1[iRule[i]];
				}
			}
			//m_ruleSize = m_rules != null ? m_rules.length : 0;
		} catch(CRGRuleGroupException e) {
			throw e;
		} catch(CRGRuleGroupWarning ex) {
			throw ex;
		} catch(Exception ex) {
			throw new CRGRuleGroupException("error by reading xml-Rules", ex);
		}
	}

	/**
	 * Setzt alle Werte der letzten Prüfung zurück.
	 * <p>
	 * Dies muss vor jeder Neuberechnung aufgerufen werden,
	 * damit alle Parameter der zuvor durchgeführten Berechnung nicht mehr vorhanden sind.
	 * @throws Exception
	 */
	public CRGInputOutputBasic newCase() throws Exception
	{
		/*		m_inout.newCase();
		  if (m_inout.m_inout!=null)
		   m_inout.m_inout.newCase();*/
		m_inout.newCase();
		return m_inout;

	}

	/**
	 * Setzt alle Werte der letzten Prüfung zurück und gibt ein Ein- und Ausgabe-Objekt zurück, welches ein Prüfdatum enthält.
	 * <p>
	 * Dies ist für die Datenübergreifende Prüfung notwendig.<br>
	 * Für die DRG-Berechnung und RSA-Berechnung ist diese Methode nicht zu verwenden.
	 * @param checkDate Date : Prüfdatum
	 * @return CRGInputOutputBasic : Ein- und Ausgabe-Objekt
	 * @throws Exception
	 */
	public CRGInputOutputBasic newCase(Date checkDate) throws Exception
	{
		m_inout = newCase();
//		if(checkDate != null) {
//			m_inout.m_checkDate = checkDate.getTime();
//		}
		return m_inout;
	}

	/**
	 * Setzt alle Werte zu einem Fall zurück.
	 * @param CRGInputOutputBasic inout
	 * @throws Exception
	 */
	protected CRGInputOutputBasic newCase(CRGInputOutputBasic inout) throws Exception
	{
		inout = new CRGInputOutputBasic();
		
		return inout;
	}
	public RulesRefOut setCodeReferences(RulesCheckOut chk, int index) throws Exception 
	{
            RulesRefOut ret = getCodeReferences(index);
            if(ret != null){
                chk.m_refList.add(ret);
            }
            return ret;
        }

	public RulesRefOut getCodeReferences(int index) throws Exception
	{
		if(m_RefEle[index] != null) {
			RulesRefOut chkRef = new RulesRefOut();
//			chkRef.m_princRef = m_RefEle[index].getForCritIndex(CRGRuleGrouperStatics.CRITSTR_INDEX_MAINDIAG);
//			chkRef.m_auxDiagList = m_RefEle[index].getForCritArrayIndex(CRGRuleGrouperStatics.
//								   CRITARRAYSTRING_INDEX_DIAG_AUX);
//			chkRef.m_diagList = m_RefEle[index].getForCritArrayIndex(CRGRuleGrouperStatics.CRITARRAYSTRING_INDEX_DIAG);
//			chkRef.m_procList = m_RefEle[index].getForCritArrayIndex(CRGRuleGrouperStatics.CRITARRAYSTRING_INDEX_PROC);
//			chkRef.m_feeList = m_RefEle[index].getForCritArrayIndex(CRGRuleGrouperStatics.
//							   CRITARRAYSTRING_INDEX_ENTGELTE);
//                        chkRef.m_psychOpsList = m_RefEle[index].getForCritArrayIndex(CRGRuleGrouperStatics.
//							   CRITARRAYSTRING_INDEX_PSYCH_OPS_CODE);
//                        chkRef.m_VPSList = m_RefEle[index].getForCritArrayIndex(CRGRuleGrouperStatics.
//							   CRITARRAYSTRING_INDEX_VPS_ERR_NUMBER);
//                        chkRef.m_historyCaseIdents = m_RefEle[index].getForCritArrayIndex(CRGRuleGrouperStatics.CRITARRAYSTRING_INDEX_CLINIC_CASE_NR);

			return chkRef;
		}
		return null;
	}

	/**
	 * nach der Regelauswertung für ein Fall werden die Referenzierten Objekte (Prozeduren,
	 * Diagnosen) in ein String zusammengefasst
	 * @param index int
	 * @return String
	 * @throws Exception
	 */
	public String getCodeReferencesForRule(int index) throws Exception
	{
		StringBuffer ret = new StringBuffer();
		String toAdd = "";
		int lenRet = 0;
		if(m_RefEle[index] != null) {
//			ret.append(this.getCodeRefPdx(index));
//			toAdd = this.getCodeRefSdx(index);
//			if((lenRet = ret.toString().length()) > 0 && (toAdd.length()) > 0) {
//				ret.append(",");
//				ret.append(toAdd);
//			} else if(toAdd.length() > 0) {
//				ret.append(toAdd);
//			}
//			lenRet = ret.toString().length();
//			toAdd = this.getCodeRefDdx(index);
//			if(lenRet > 0 && (toAdd.length()) > 0) {
//				ret.append(",");
//				ret.append(toAdd);
//			} else if(toAdd.length() > 0) {
//				ret.append(toAdd);
//			}
//			lenRet = ret.toString().length();
//			toAdd = this.getCodeRefSrg(index);
//			if(lenRet > 0 && (toAdd.length()) > 0) {
//				ret.append(",");
//				ret.append(toAdd);
//			} else if(toAdd.length() > 0) {
//				ret.append(toAdd);
//			}
//			toAdd = this.getCodeRefFee(index);
//			if(lenRet > 0 && (toAdd.length()) > 0) {
//				ret.append(",");
//				ret.append(toAdd);
//			} else if(toAdd.length() > 0) {
//				ret.append(toAdd);
//			}
		}
		return ret.toString();
	}

//	public String getCodeRefPdx(int index) throws Exception
//	{
//		if(m_RefEle[index] != null) {
//			String str = m_RefEle[index].getForCritIndex(CRGRuleGrouperStatics.CRITSTR_INDEX_MAINDIAG);
//			if(str == null) {
//				return "";
//			}
//			return str;
//		}
//		return "";
//	}

//	public String getCodeRefDdx(int index) throws Exception
//	{
//		StringBuffer ret = new StringBuffer();
//		if(m_RefEle[index] != null) {
//			return m_RefEle[index].getForCritArrayStringIndex(CRGRuleGrouperStatics.CRITARRAYSTRING_INDEX_DIAG);
//		}
//		return "";
//	}
//
//	public String getCodeRefSdx(int index) throws Exception
//	{
//		if(m_RefEle[index] != null) {
//			return m_RefEle[index].getForCritArrayStringIndex(CRGRuleGrouperStatics.CRITARRAYSTRING_INDEX_DIAG_AUX);
//		}
//		return "";
//	}
//
//	public String getCodeRefSrg(int index) throws Exception
//	{
//		if(m_RefEle[index] != null) {
//			return m_RefEle[index].getForCritArrayStringIndex(CRGRuleGrouperStatics.CRITARRAYSTRING_INDEX_PROC);
//		}
//		return "";
//	}
//
//        
//	public String getCodeRefFee(int index) throws Exception
//	{
//		if(m_RefEle[index] != null) {
//			return m_RefEle[index].getForCritArrayStringIndex(CRGRuleGrouperStatics.CRITARRAYSTRING_INDEX_ENTGELTE);
//		}
//		return "";
//
//	}

	/**
	 * für ESKA Dunkelverarbeitung werden alle Referenzen zusammengetragen und mit den Typs versehen
	 * 01 = ICD
	 * 02 = OPS
	 * 03 = Entgeltschlüssel
	 * 04 = ATC Code
	 * 05 = PZN Code
	 * 06 = Hilfsmittelpositionsnummer
	 * 07 = Heilmittelpositionsnummer
	 * 08 = (Referenz-)Fallnummern

	 * @param index int
	 * @return String
	 * @throws Exception
	 */
	public String getAllRefCodesMappedForRule(int index) throws Exception
	{
		if(m_RefEle[index] != null) {
			return m_RefEle[index].getAllReferencesMapped();
		}
		return "";
	}
        
//	public ArrayList<String> getVpsRulesReferences(int index) throws Exception
//	{
//		if(m_RefEle[index] != null) {
//			return m_RefEle[index].getVpsRulesReferences();
//		}
//		return null;
//	}
//        


	/**
	 * Führt die Regelprüfung anhand der vorab übergebenen Parameter durch
	 * und gibt als Ergebnis die Regelverstösse zurück
	 * @return CRGRule[] : Array mit allen Werten der angeschlagenen Regeln
	 * @throws Exception
	 */
	public CRGRule[] performCheck() throws Exception
	{
		return performCheck(m_inout);
	}

	public CRGRule[] performCheck(CRGInputOutputBasic inout) throws Exception
	{
		return performCheck(inout, null);
	}

	public CRGRule[] performCheck(CRGInputOutputBasic inout, Date poolDate) throws Exception
	{
//System.out.println(this.dumpInputOutputValues());
		prepareRulesForInout(inout, poolDate);
		int[] iRule = new int[m_ruleSize];
		int count = -1;
		m_RefRule = -1;
		CRGInputOutputBasic[] inouts = new CRGInputOutputBasic[1];
		inouts[0] = inout;
		for(int i = 0; i < m_ruleSize; i++) {
			/*			if( m_rules[i].m_number.startsWith("2.01.3"))
				 System.out.println("here");*/
			if(checkRule(m_rules[i], inout)) {
				count++;
				iRule[count] = i;
			}
		}
		CRGRule[] rule = new CRGRule[count + 1];
		for(int i = 0; i <= count; i++) {
			rule[i] = m_rules[iRule[i]];
		}
		return rule;
	}


	/**
	 * Führt die Regelprüfung anhand der vorab übergebenen Parameter durch
	 * und gibt als Ergebnis die Regelverstösse mit Referenzen (Codes) zurück.
	 * <p>
	 * Es werden für jeden Regelverstoß die Referenzen auf die
	 * regelrelevanten Hauptdiagnosen, Diagnosen, Nebendiagnosen und Prozeduren ermittelt.
	 * @return CRGRule[] : Array mit allen Werten der angeschlagenen Regeln
	 * @throws Exception
	 */
	public CRGRule[] performCheckRef() throws Exception
	{
		return performCheckRef(m_inout);
	}

	public CRGRule[] performCheckRef(CRGInputOutputBasic inout) throws Exception
	{
		return performCheckRef(inout, null);

	}

	public CRGRule[] performCheckRef(CRGInputOutputBasic inout, Date poolDate) throws Exception
	{
		return performCheckRef(inout, poolDate, null);
	}

	public CRGRule[] performCheckRef(CRGInputOutputBasic inout, Date poolDate, long[] roles) throws Exception
	{
		prepareRulesForInout(inout, poolDate);
		int[] iRule = new int[m_ruleSize];
		int count = -1;
		m_RefRule = -1;
		CRGInputOutputBasic[] inouts = new CRGInputOutputBasic[1];
		inouts[0] = inout;
		for(int i = 0; i < m_ruleSize; i++) {
			CRGRule rule = m_rules[i];
			if(roles != null && roles.length > 0 && roles[0] != 0 && !rule.isForRoleSorted(roles)) { // wenn 0 - d.h. für alle Rollen, muss immer an der ersten Stelle stehen
				continue;
			}
//			if((isESKA && !rule.isForFeeGroup(inout.getFeeGroupAsLong()))
//                                || (!isESKA && (!rule.isForFeeGroup(inout.getIsDrgCase() == 0?1:inout.getIsDrgCase())) && 
//				     (rule.m_feeGroups != null && rule.m_feeGroups.length > 0)) 
//				|| (!isESKA && (rule.m_feeGroups == null || rule.m_feeGroups.length == 0) 
//				    && inout.getIsDrgCase() == RmcCaseBase.PEPP_CASE)) {
//				continue;
//			}
			if(checkRuleRef(m_rules[i], inout)) {
				count++;
				iRule[count] = i;
			}
		}
		CRGRule[] rule = new CRGRule[count + 1];
		for(int i = 0; i <= count; i++) {
			rule[i] = m_rules[iRule[i]];
		}
		return rule;
	}

	/**
	 * bereitet SoLe, Medikamente für Regelauswertung mit der Berücksichtigung der Regelintervalls for
	 * @param inout CRGInputOutputBasic
	 * @param rule CRGRule
	 * @param inouts CRGInputOutputBasic[]
	 * @param sort int[]
	 * @throws Exception
	 */
	private void prepareCummulatedValues(CRGInputOutputBasic inout) throws Exception
	{
//		inout.sortClinicCases();
	}

	/**
	 * Prüft den Fall (Parameter des CRGInputOutputBasic-Objektes) auf die übergebene Regel.
	 * @param rule CRGRule : Objekt mit Regel-Werten
	 * @return boolean : Regel angeschlagen ja/nein
	 * @throws Exception
	 */
	private boolean checkRule(CRGRule rule, CRGInputOutputBasic inout) throws Exception
	{
		boolean ret = false;
		/*		if(rule.m_year == 2007) {
		   System.out.println("");
		  }*/
		if(rule.m_isActive &&(
//			((inout.m_checkYear > 0 && rule.m_year == inout.m_checkYear)
//			||
//			((rule.getValidFromTime() <= inout.m_admissionDate) &&
//			((rule.getValidToTime() + NEARLY_ONE_DAY) >= inout.m_admissionDate))
//			||
			m_isGK

			)
			) {
			try {
				m_RefZaehler = 0;
//yyy				System.out.println(rule.m_rid + ": " + rule.m_ruleText);
				/*
				  if(!rule.m_rid.equals("1031818")){
				 return ret;
				  }
				 */

				if(checkRule(rule.getRuleElement(), inout)) {
					durchlauf++;
//yyy				System.out.println("----->>Regel: " + rule.m_number + " : " + rule.m_ruleText + " : " + durchlauf );
					ret = true;
				} else {
//yyy 				System.out.println("Regel nicht angeschlagen: " + rule.m_number + " : " + rule.m_ruleText);
					ret = false;
				}
			} catch(Exception ex) {
				CRGRuleGroupException cpex = CheckpointRuleGrouper.createRuleException(rule, ex.getMessage(), ex);
				System.err.println("error by rule " + rule.m_rid);
				cpex.printStackTrace();
			} finally {
				m_ruleIntervalStart = 0;
				m_ruleIntervalEnd = 0;
				this.m_hasRuleInterval = false;

			}
		}
		return ret;
	}

	/**
         * Prüft den Fall (Parameter des CRGInputOutputBasic-Objektes) auf die übergebene Regel.
	 * Für die angeschlagenen Regeln werden die Referenzen auf die
	 * regelrelevanten Hauptdiagnosen, Diagnosen, Nebendiagnosen und Prozeduren gesammelt,
	 * die über die Methode setCodeReferences abgefragt werden.
	 * @param rule CRGRule : Objekt mit Regel-Werten
	 * @throws Exception
	 */
	private boolean checkRuleRef(CRGRule rule, CRGInputOutputBasic inout) throws Exception
	{

		int caseId = 0;
		if(
//                        (inout.m_checkYear > 0 && rule.m_year == m_inout.m_checkYear) ||
//			(rule.getValidFromTime() <= inout.m_admissionDate)
//			&& ((rule.getValidToTime() + NEARLY_ONE_DAY) >= inout.m_admissionDate)
//			&& 
                        rule.m_isActive) {
			try {
//				caseId = inout.getCaseId();
                                return checkRuleElementRef(rule.getRuleElement(), inout);
//				m_RefZaehler = -1;
//				m_RefIndex1 = new int[MIN_ARRAY_LENGTH];
//				m_RefIndex2 = new int[MIN_ARRAY_LENGTH];
//				if(m_RefEle[m_RefRule + 1] != null) {
//					m_RefEle[m_RefRule + 1].setClean();
//				} else {
//					m_RefEle[m_RefRule + 1] = new CRGRefElement();
//				}

//				if(checkRule(rule.getRuleElement(), inout)) {
//					durchlauf++;
////yyy				System.out.println("----->>Regel: " + rule.m_number + " : " + rule.m_ruleText + " : " + durchlauf );
//					ret = true;
//					// nun Referenzen schreiben.
//					m_RefRule++;
//					if(m_RefZaehler >= 0) {
//						m_RefEle[m_RefRule].m_ref = true;
//						for(int i = 0; i <= m_RefZaehler; i++) {
//							if((m_RefIndex1[i] == -1) &&
//								(m_RefIndex2[i] == CRGRuleGrouperStatics.CRITSTR_INDEX_MAINDIAG)) {
//								m_RefEle[m_RefRule].addCritValue(m_RefIndex2[i], m_RefWert[i]);
//							}
//							if(m_RefIndex1[i] > 0) {
//								m_RefEle[m_RefRule].addCritValue(m_RefIndex2[i], m_RefWert[i]);
//							}
//						}
//					} else {
//						m_RefEle[m_RefRule].m_ref = false;
//					}
//				} else {
////yyy 				System.out.println("Regel nicht angeschlagen: " + rule.m_number + " : " + rule.m_ruleText);
//					ret = false;
//				}
			} catch(Exception ex) {
				CRGRuleGroupException cpex = CheckpointRuleGrouper.createRuleException(rule, ex.getMessage(), ex);
				if(!m_corruptRules.contains(rule.m_ruleNumber)) {
					m_corruptRules.addElement(rule.m_ruleNumber);
				}
				System.err.println("error by rule " + rule.m_rid + " bei Regelauswertung des Falles = " + caseId);
				cpex.printStackTrace();
			} finally {
				this.m_hasRuleInterval = false;
				this.m_ruleIntervalStart = 0;
				this.m_ruleIntervalEnd = 0;
			}
		}
		return false;
	}
        
    private  boolean checkRuleElementRef(CRGRuleElement ruleElement, CRGInputOutputBasic inout)throws Exception{
        boolean ret = false;
        m_RefZaehler = -1;
        m_RefIndex1 = new int[MIN_ARRAY_LENGTH];
        m_RefIndex2 = new int[MIN_ARRAY_LENGTH];
        if(m_RefEle[m_RefRule + 1] != null) {
                m_RefEle[m_RefRule + 1].setClean();
        } else {
                m_RefEle[m_RefRule + 1] = new CRGRefElement();
        }

        if(checkRule(ruleElement, inout)) {
            durchlauf++;
//yyy				System.out.println("----->>Regel: " + rule.m_number + " : " + rule.m_ruleText + " : " + durchlauf );
            ret = true;
            // nun Referenzen schreiben.
            m_RefRule++;
            if(m_RefZaehler >= 0) {
                m_RefEle[m_RefRule].m_ref = true;
                for(int i = 0; i <= m_RefZaehler; i++) {
//                    if((m_RefIndex1[i] == -1) 
//                            &&
//                            (m_RefIndex2[i] == CRGRuleGrouperStatics.CRITSTR_INDEX_MAINDIAG)) {
//                            m_RefEle[m_RefRule].addCritValue(m_RefIndex2[i], m_RefWert[i]);
//                    }
                    if(m_RefIndex1[i] > 0) {
                            m_RefEle[m_RefRule].addCritValue(m_RefIndex2[i], m_RefWert[i]);
                    }
                }
            } else {
                    m_RefEle[m_RefRule].m_ref = false;
            }
    } 
    return ret;
}
	/**
	 * Prüft den Fall (Parameter des CRGInputOutputBasic-Objektes) auf die übergebene Regel.
	 * @param rule CRGRule : Objekt mit Regel-Werten
	 * @throws Exception
	 */
	/*	private boolean checkRuleRefCC(CRGRule rule) throws Exception
	 {
	  boolean ret = false;
	  if((rule.getValidFromTime() <= m_inout.m_admissionDate)
	   && ((rule.getValidToTime() + NEARLY_ONE_DAY) >= m_inout.m_admissionDate)
	   && rule.m_isActive) {
	   try {
//yyy				System.out.println(rule.m_rid + ": " + rule.m_ruleText);
		m_RefZaehler = -1;
		if(m_RefEle[m_RefRule + 1] != null) {
		 m_RefEle[m_RefRule + 1].setClean();
		} else {
		 m_RefEle[m_RefRule + 1] = new CRGRefElement();
		}
//Regel hat Interval:
		checkRulesIntervalCC(rule);
		if(checkRuleCC(rule.getRuleElement())) {
		 durchlauf++;
//yyy				System.out.println("----->>Regel: " + rule.m_number + " : " + rule.m_ruleText + " : " + durchlauf );
		 ret = true;
		 // nun Referenzen schreiben.
		 m_RefRule++;
		 if(m_RefZaehler >= 0) {
		  m_RefEle[m_RefRule].m_ref = true;
		  for(int i = 0; i <= m_RefZaehler; i++) {
		   if((m_RefIndex1[i] == -1) &&
			(m_RefIndex2[i] == CRGRuleGrouperStatics.CRITSTR_INDEX_MAINDIAG)) {
			m_RefEle[m_RefRule].m_MainDiag = m_RefWert[i];
		   }
		   if((m_RefIndex1[i] == CRGRuleGrouperStatics.CRITARRAYSTRING_INDEX_DIAG_AUX)) {
			m_RefEle[m_RefRule].m_DiagAuxCount++;
			m_RefEle[m_RefRule].m_DiagAux[m_RefEle[m_RefRule].m_DiagAuxCount] = m_RefWert[i];
		   }
		   if((m_RefIndex1[i] == CRGRuleGrouperStatics.CRITARRAYSTRING_INDEX_DIAG)) {
			m_RefEle[m_RefRule].m_DiagCount++;
			m_RefEle[m_RefRule].m_Diag[m_RefEle[m_RefRule].m_DiagCount] = m_RefWert[i];
		   }
		   if((m_RefIndex1[i] == CRGRuleGrouperStatics.CRITARRAYSTRING_INDEX_PROC)) {
			m_RefEle[m_RefRule].m_ProcCount++;
			m_RefEle[m_RefRule].m_Proc[m_RefEle[m_RefRule].m_ProcCount] = m_RefWert[i];
		   }
		  }
		 } else {
		  m_RefEle[m_RefRule].m_ref = false;
		 }
		} else {
//yyy 				System.out.println("Regel nicht angeschlagen: " + rule.m_number + " : " + rule.m_ruleText);
		 ret = false;
		}
	   } catch(Exception ex) {
		CRGRuleGroupException cpex = CheckpointRuleGrouper.createRuleException(rule, ex.getMessage(), ex);
		System.err.println("error by rule " + rule.m_rid);
		cpex.printStackTrace();
	   } finally {
		m_tmpFormerCount = 0;
		m_tmpFormerAkt = 0;
		m_tmpFormerInOuts = null;
		m_ruleIntervalStart = 0;
		m_ruleIntervalEnd = 0;
		m_hasRuleInterval = false;
	   }
	  }
	  return ret;
	 }
	 */
	/**
	 * Prüft den Fall (Parameter des CRGInputOutputBasic-Objektes) auf das
	 * übergebene Regel-Element (Startknoten).
	 * @param ruleElement CRGRuleElement : Root-Element der Regel
	 * @param inout CRGInputOutputBasic : Objekt mit Fall-Werten
	 * @return boolean
	 * @throws Exception
	 */
	private boolean checkRule(CRGRuleElement ruleElement, CRGInputOutputBasic inout) throws Exception
	{
		if(ruleElement == null) {
			return false;
		}
		return checkRuleElements(ruleElement.m_childElements, ruleElement.m_childCount, ruleElement.m_isDepended,
			null, inout, false, ruleElement.m_isNot).getLastState();
	}
        
        public boolean checkTerm4Rule(CRGRuleElement ruleElement, CRGInputOutputBasic inout) throws Exception
        {
            return checkRule(ruleElement, inout);
        }
        
        public boolean checkTermRef4Rule(CRGRuleElement ruleElement, CRGInputOutputBasic inout) throws Exception
        {
            m_RefRule = -1;
            return checkRuleElementRef(ruleElement, inout);
        }

	/**
	 * Prüft den Fall (Parameter des CRGInputOutputBasic-Objektes) auf das
	 * übergebene Regel-Element (Startknoten).
	 * @param ruleElement CRGRuleElement : Root-Element der Regel
	 * @param inout CRGInputOutputBasic : Objekt mit Fall-Werten
	 * @return boolean
	 * @throws Exception
	 */
	/*	private boolean checkRuleCC(CRGRuleElement ruleElement) throws Exception
	 {
	  if(ruleElement == null) {
	   return false;
	  }
	  //------------------------------------------------------------------------------------------
	  // hier um fallübergreifend erweitern
	  // was muss gemacht werden:
	  // wichtig ist das inout-Objekt, welches muss genommen werden
	  // nur bei entsprechender regel
	  // aus performance gründen hier 2 funktionen
	  // mit oder ohne rücksicht auf fallübergreifende regeln
	  //------------------------------------------------------------------------------------------
	  return checkRuleElementsCC(ruleElement.m_childElements, ruleElement.m_childCount, ruleElement.m_isDepended,
	   null).getLastState();
	  //}
	 }
	 */
	private CheckValue checkOneElement(CRGRuleElement ruleEle, CRGInputOutputBasic inout, int state, boolean depended,
		CheckValue chkValue, boolean isCC) throws Exception
	{

		if(ruleEle.m_firstChildType >= 0 &&
			((state > CRGRuleGrouperStatics.OP_OR)
			|| (ruleEle.m_nextOperantType > CRGRuleGrouperStatics.OP_OR))) { //Es muss gerechnet werden
			//System.out.println("Element");
			//es kommt vor, dass der erste Wert in einer Klammer ein ARRAY ist.
			//hier wenn vor oder nach etwas anderes steht als AND, OR oder Nichts
			//und die Klammer muss ausgewertet werden, selten der Fall
			// m_firstChildType == m_criterionType
			switch(ruleEle.m_firstChildType) { //entscheidet am Typ des 1. Unterlementes weiteres Vorgehen
				case CRGRuleGrouperStatics.DATATYPE_INTEGER: //Integer und Double müssen im gleichen Bereich bleiben zum Vergleich
				case CRGRuleGrouperStatics.DATATYPE_DOUBLE: {
					chkValue.setDoubleValue(getRuleElementDoubleValue(ruleEle.m_childElements,
						ruleEle.m_childCount, inout));
					chkValue.setActualType(CRGRuleGrouperStatics.DATATYPE_DOUBLE);
					//hier folgt eine Auswertung mit TYPE_VALUE:
					//z.B. regel 039: beatmungsdauer >= ( Verweildauer + 1 * 24)
					//der Ausdruck der Klammer ist Value
					chkValue.setLastState(chkValue.getCheckValue(state, chkValue.getLastState(),
						CRGRuleGrouperStatics.TYPE_VALUE));
					break;
				}
				case CRGRuleGrouperStatics.DATATYPE_ARRAY_INTEGER: //Integer und Double müssen im gleichen Bereich bleiben zum Vergleich
				case CRGRuleGrouperStatics.DATATYPE_ARRAY_DOUBLE: {
					//es kann ein Array erwartet werden, es wird erwartet!
					//z.B. Diagnose_Lokalisation == 'links' bzw. '2'
					double[] doubleArray = null;
					if(ruleEle.m_firstChildType == CRGRuleGrouperStatics.DATATYPE_ARRAY_INTEGER) {
						doubleArray = getRuleElementIntegerArray(ruleEle.m_childElements,
									  ruleEle.m_childCount, inout);
					} else {
						doubleArray = getRuleElementDoubleArray(ruleEle.m_childElements,
									  ruleEle.m_childCount, inout);
					}
					chkValue.setDoubleArray(doubleArray);
					chkValue.setActualType(CRGRuleGrouperStatics.DATATYPE_ARRAY_DOUBLE);
					chkValue.setLastState(chkValue.getCheckValue(state, chkValue.getLastState(),
						CRGRuleGrouperStatics.TYPE_ELEMENT));
					break;
				}
				case CRGRuleGrouperStatics.DATATYPE_STRING: {
					// es steht: <= usw. oder + usw.
					chkValue.setStringValue(getRuleElementStringValue(ruleEle.m_childElements,
						ruleEle.m_childCount, inout));
					chkValue.setActualType(CRGRuleGrouperStatics.DATATYPE_STRING);
					chkValue.setLastState(chkValue.getCheckValue(state, chkValue.getLastState(),
						CRGRuleGrouperStatics.TYPE_ELEMENT));
					break;
				}
				case CRGRuleGrouperStatics.DATATYPE_ARRAY_STRING: {
					//frage wie oben. und array erwartet
					chkValue.setStringArray(getRuleElementStringArray(ruleEle.m_childElements,
						ruleEle.m_childCount, inout));
					chkValue.setActualType(CRGRuleGrouperStatics.DATATYPE_ARRAY_STRING);
					chkValue.setLastState(chkValue.getCheckValue(state, chkValue.getLastState(),
						CRGRuleGrouperStatics.TYPE_ELEMENT));
					break;
				}
				case CRGRuleGrouperStatics.DATATYPE_DATE: {
					chkValue.setLongValue(getRuleElementLongValue(ruleEle.m_childElements,
						ruleEle.m_childCount, inout));
					chkValue.setActualType(CRGRuleGrouperStatics.DATATYPE_DATE);
					chkValue.setLastState(chkValue.getCheckValue(state, chkValue.getLastState(),
						CRGRuleGrouperStatics.TYPE_ELEMENT));
					break;
				}
				case CRGRuleGrouperStatics.DATATYPE_DAY_TIME: {
					chkValue.setLongValue(getRuleElementLongValue(ruleEle.m_childElements,
						ruleEle.m_childCount, inout));
					chkValue.setActualType(CRGRuleGrouperStatics.DATATYPE_DAY_TIME);
					chkValue.setLastState(chkValue.getCheckValue(state, chkValue.getLastState(),
						CRGRuleGrouperStatics.TYPE_ELEMENT));
					break;
				}
				case CRGRuleGrouperStatics.DATATYPE_ARRAY_DATE: {
					chkValue.setLongArray(getRuleElementLongArray(ruleEle.m_childElements,
						ruleEle.m_childCount, inout));
					chkValue.setActualType(CRGRuleGrouperStatics.DATATYPE_ARRAY_DATE);
					chkValue.setLastState(chkValue.getCheckValue(state, chkValue.getLastState(),
						CRGRuleGrouperStatics.TYPE_ELEMENT));
					break;
				}
				case CRGRuleGrouperStatics.DATATYPE_ARRAY_DAY_TIME: {
					chkValue.setLongTimeArray(getRuleElementLongArray(ruleEle.m_childElements,
						ruleEle.m_childCount, inout));
					chkValue.setActualType(CRGRuleGrouperStatics.DATATYPE_ARRAY_DAY_TIME);
					chkValue.setLastState(chkValue.getCheckValue(state, chkValue.getLastState(),
						CRGRuleGrouperStatics.TYPE_ELEMENT));
					break;
				}
				/*case CRGRuleGrouperStatics.DATATYPE_UNFORMATTED:{
				 CheckValue chk = checkRuleElements(ruleEle.m_childElements, ruleEle.m_childCount, depended, null);
				 chkValue.setCheckValues(chk);
				 chkValue.setLastState(chkValue.getCheckValue(state, chkValue.getLastState(), CRGRuleGrouperStatics.TYPE_ELEMENT);
				 break;
				  }*/
			}

		} else { //ohne Berechnung wird Bedingung auf true geprüft
			CheckValue newValue = new CheckValue();
			newValue.groupDependResults = chkValue.groupDependResults;
			newValue.dependIndex = chkValue.dependIndex;
			newValue.lastDepended = chkValue.lastDepended;
			newValue.lastDependIndex = chkValue.lastDependIndex;
			newValue.minCountForDependCompareOperation = chkValue.minCountForDependCompareOperation;
			if(isCC) {
				newValue = checkRuleElementsCC(ruleEle.m_childElements, ruleEle.m_childCount, depended, newValue, ruleEle.m_isNot);
			} else {
				newValue = checkRuleElements(ruleEle.m_childElements, ruleEle.m_childCount, depended, newValue, inout,
						   isCC, ruleEle.m_isNot);
			}
			if(state > CRGRuleGrouperStatics.OP_OR) {
				switch(newValue.getActualType()) {
					case CRGRuleGrouperStatics.DATATYPE_INTEGER: //Integer und Double müssen im gleichen Bereich bleiben zum Vergleich
					case CRGRuleGrouperStatics.DATATYPE_DOUBLE: {
						chkValue.setDoubleValue(newValue.getActualDouble());
						chkValue.setActualType(newValue.getActualType());
						break;
					}
					case CRGRuleGrouperStatics.DATATYPE_ARRAY_INTEGER: //Integer und Double müssen im gleichen Bereich bleiben zum Vergleich
					case CRGRuleGrouperStatics.DATATYPE_ARRAY_DOUBLE: {
						chkValue.setDoubleArray(newValue.getActualDoubleArray());
						chkValue.setActualType(newValue.getActualType());
						break;
					}
					case CRGRuleGrouperStatics.DATATYPE_STRING: {
						// es steht: <= usw. oder + usw.
						chkValue.setStringValue(newValue.getActualString());
						chkValue.setActualType(CRGRuleGrouperStatics.DATATYPE_STRING);
						break;
					}
					case CRGRuleGrouperStatics.DATATYPE_ARRAY_STRING: {
						//frage wie oben. und array erwartet
						chkValue.setStringArray(newValue.getActualStringArray());
						chkValue.setActualType(CRGRuleGrouperStatics.DATATYPE_ARRAY_STRING);
						break;
					}
					case CRGRuleGrouperStatics.DATATYPE_DAY_TIME:
						;
					case CRGRuleGrouperStatics.DATATYPE_DATE: {
						chkValue.setLongValue(newValue.getActualLong());
						chkValue.setActualType(CRGRuleGrouperStatics.DATATYPE_DATE);
						break;
					}
					/*					case CRGRuleGrouperStatics.DATATYPE_DAY_TIME: {
						  chkValue.setLongTimeValue(newValue.getActualLongTime());
						  chkValue.setActualType(CRGRuleGrouperStatics.DATATYPE_DAY_TIME);
						  break;
						 }*/
					case CRGRuleGrouperStatics.DATATYPE_ARRAY_DAY_TIME:
					case CRGRuleGrouperStatics.DATATYPE_ARRAY_DATE: {
						chkValue.setLongArray(newValue.getActualLongArray());
						chkValue.setActualType(CRGRuleGrouperStatics.DATATYPE_ARRAY_DATE);
						break;
					}
					/*					case CRGRuleGrouperStatics.DATATYPE_ARRAY_DAY_TIME: {
						  chkValue.setLongTimeArray(newValue.getActualLongTimeArray());
						  chkValue.setActualType(CRGRuleGrouperStatics.DATATYPE_ARRAY_DAY_TIME);
						  break;
						 }*/
				}
				chkValue.setLastState(chkValue.getCheckValue(state, chkValue.getLastState(),
					CRGRuleGrouperStatics.TYPE_ELEMENT));
			} else {
				if(chkValue.lastGroupIndex == newValue.lastGroupIndex && chkValue.lastGroupIndex > 0) {
					newValue.setLastState(newValue.checkGroupIndexValues(state, newValue.getLastState(),
						chkValue.getGroupResults(chkValue.lastGroupIndex)));
				}
				if(newValue.getLastState()
					&& ruleEle.m_isDepended
					&& (chkValue.lastDependIndex == newValue.lastDependIndex)
					&& (chkValue.lastDepended != newValue.lastDepended)
					&& newValue.lastDependIndex > -1
					) {
					newValue.minCountForDependCompareOperation = Math.max(newValue.minCountForDependCompareOperation, chkValue.minCountForDependCompareOperation);
					newValue.setLastState(newValue.checkDependValues(state, newValue.getLastState(),
						chkValue.dependIndex));
				}
				chkValue = newValue;
			}
		}
		return chkValue;
	}
 
         private CheckValue doMaxIntervalPropertyMethod(CRGRuleElement ruleEle, CheckValue chkValue, CRGInputOutputBasic inout) throws Exception
         {
            String[] critValues = inout.getArrayStringValue(ruleEle.m_criterionIndex);
             if(critValues == null){
                 return chkValue;
             }
             long[] dateValues = inout.getArrayLongValue(ruleEle.m_method_dependency_crit);
              Date[] dateValuesDat = inout.getArrayDateValue(ruleEle.m_method_dependency_crit);
             if(dateValues == null || dateValues.length != critValues.length)
                 return chkValue;
             
             int count = 0;
             int days = 0;
              ArrayList<Integer> asss = null;
              ArrayList<Integer> ass_equal = null;
             try{
                 count = ((Integer)ruleEle.m_parameter[2]).intValue();
                 days = ((Integer)ruleEle.m_parameter[3]).intValue();
                 asss = (ArrayList<Integer>)ruleEle.m_parameter[0]; // hat die Indizies von den entsprechenden Validierungssets
                 ass_equal = (ArrayList<Integer>)ruleEle.m_parameter[1]; // hat die Indizies von den entsprechenden Validierungssets
             }catch(Exception e){
                 // die Parameter wurden nicht richtig oder gar nicht gesetzt
                 return chkValue;
             }
             int len = asss == null?0:asss.size();
             if(len == 0)
                return chkValue;
             int lenEqual = ass_equal == null?0:ass_equal.size();
             String[][] validities = new String[len][];
            String[][] validitiesEqual = new String[lenEqual][];
             for(int i = 0; i < len; i++){
                 validities[i] = inout.getArrayStringValue(asss.get(i).intValue());
             }
             for(int i = 0; i < lenEqual; i++){
                 validitiesEqual[i] = inout.getArrayStringValue(ass_equal.get(i).intValue());
             }
             long prevValue = 0;
             int prevInd = -1;
             int maxCount = 0;
             for(int i = 0; i < dateValues.length; i++){
                 if(prevValue == 0 ||  (dateValues[i] - prevValue)/ONE_DAY > days){
                     prevValue = dateValues[i];
                     prevInd = i;
                     maxCount = 0;
                     continue;
                 }
                 // Überprüfung der Validierungsmerkmalen an einem Tag
                 int ind = i ;
                 while(dateValues.length > ind && (dateValues[ind] - prevValue)/ONE_DAY <= days){
                    int equals = 0;
                    for(int k = 0; k < lenEqual; k++){
                        // Bedingung bei gleichem... wird zuerst überprüft
                        /* 3.9.5 2015-09-01 DNi: #FINDBUGS - String-Vergleich mit == statt mit equals.
                         * Beim Ändern ggf. sicherstellen, dass validitiesEqual[k][ind] nicht null ist! */
                        if(validitiesEqual[k][ind].equals(validitiesEqual[k][prevInd])){
                            equals++;
                        }
                    }
                    if(equals < lenEqual){
                        // die bedingung bei gleichen ist nicht erfüllt
                         ind++;
                        continue;
                    }
                    for(int j = 0; j < len; j++){
                        if(count > 0 ){
                                if(validities[j][ind] == validities[j][prevInd]){
                                    maxCount++;
                                    if(maxCount < count){
                                        break;
                                    }
                                chkValue.setLastState(!ruleEle.m_notElement);
                                m_RefZaehler++;
                                setRefValues(m_RefZaehler, doStringWithDateReference(doRefValidity(critValues[prevInd], validities[j][prevInd]), dateValuesDat[prevInd]), ruleEle.m_criterionIndex, 99);
                                m_RefZaehler++;
                                setRefValues(m_RefZaehler, doStringWithDateReference(doRefValidity(critValues[ind], validities[j][ind]), dateValuesDat[ind]), ruleEle.m_criterionIndex, 99);
                                return chkValue;
                            }
                        }else{
                            // count <= 0  Fehler wenn ungleich ist
                            if(validities[j][ind] != validities[j][prevInd]){
                                chkValue.setLastState(!ruleEle.m_notElement);
                                m_RefZaehler++;
                                setRefValues(m_RefZaehler, doStringWithDateReference(doRefValidity(critValues[prevInd], validities[j][prevInd]), dateValuesDat[prevInd]), ruleEle.m_criterionIndex, 99);
                                m_RefZaehler++;
                                setRefValues(m_RefZaehler, doStringWithDateReference(doRefValidity(critValues[ind], validities[j][ind]), dateValuesDat[ind]), ruleEle.m_criterionIndex, 99);
                                return chkValue;
                            }
                        }

                    }
                    ind++;
                 }

                prevValue = dateValues[i];
                prevInd = i;

                 
                
             }
             chkValue.setLastState(ruleEle.m_notElement);
             return chkValue;
         }
         
         private String doRefValidity(String value1, String value2)
         {
             return value1 + "[" + value2 + "]";
         }
/**
 * baut eine Refernz aus einem Sring und einem Datum zusammen
 */         
         private String doStringWithDateReference(String value, Date datValue)
         {
             return value + "(" + UtlDateTimeConverter.converter().formatToGermanDate(datValue, false) + ")";
         }
        
 /**
  * Dieser Method überprüft ob das Kriterium ruleEle.m_criterionIndex, der von dem Typ DATATYPE_ARRAY_STRING ist
  * und das zusammenhängenden Datumsarray(ruleEle.m_method_dependency_type == DATATYPE_ARRAY_DATE)
  * ruleEle.m_method_dependency_crit dar gleichen Länge hat auf der ganzen VWD von dem Fall (von Aufnahme bis Entlassungsdatum, 
  * mit berücksichtigung der Unterbrechungen auf das Pseudoableilung) nur die Anzahl der Vorkomnissen in der Liste ruleEle.m_strArrayValue
  * die in ruleEle.m_parameter[1] jedes subinterval von ruleEle.m_parameter[2] Tagen
  * @param ruleEle
  * @param chkValue
  * @param inout
  * @return 
  */
//         private CheckValue doMaxIntervalTableMethod(CRGRuleElement ruleEle, CheckValue chkValue, CRGInputOutputBasic inout)throws Exception
//         {
//             String[] critValues = inout.getArrayStringValue(ruleEle.m_criterionIndex);
//             if(critValues == null){
//                 return chkValue;
//             }
//             long[] dateValues = inout.getArrayLongValue(ruleEle.m_method_dependency_crit);
//              Date[] dateValuesDat = inout.getArrayDateValue(ruleEle.m_method_dependency_crit);
//             if(dateValues == null || dateValues.length != critValues.length)
//                 return chkValue;
//             if(ruleEle.m_strArrayValue == null || ruleEle.m_strArrayValue.length == 0){
//                 return chkValue;
//             }
//             int count = 0;
//             int days = 0;
//             try{
//                 count = ((Integer)ruleEle.m_parameter[1]).intValue();
//                 days = ((Integer)ruleEle.m_parameter[2]).intValue();
//             }catch(Exception e){
//                 // die Parameter wurden nicht richtig oder gar nicht gesetzt
//                 return chkValue;
//             }
//             String value = null;
//             long lastDate = 0;
//             long admDate = inout.getAdmissionDateLong();
//             int count4Interval = 0;
//             long[] admDepsPseudo = inout.getDepartmentsPseudoAdmissionLong();
//             long[] admDepsFirstAfterPseudo = inout.getDepartmentsAdmissionFirstAfterPseudoLong();
//
//             int pseudoInd = 0;
//             boolean hasPseudo = admDepsPseudo.length > 0;
//             if(critValues.length == 0){
//                 chkValue.setLastState(!ruleEle.m_notElement);
//                m_RefZaehler++;
//                setRefValues(m_RefZaehler, doStringWithDateReference("---", inout.getAdmissionDate()), ruleEle.m_criterionIndex, 99);
//                return chkValue;
//                 
//             }
//             for(int i = 0, n = critValues.length; i < n; i++){
//                 value = critValues[i];
//                 if(checkRuleStringValue(value, ruleEle.m_strArrayValue, CRGRuleGrouperStatics.OP_IN_TABLE, ruleEle.m_strArrayValue.length)){
//                    if(lastDate == 0){
//                         lastDate = dateValues[i];
//// Erste prozedur muss am aufnahmetag durchgeführt sein                         
//                         if(lastDate != admDate){
//                             chkValue.setLastState(!ruleEle.m_notElement);
//                             m_RefZaehler++;
//                             setRefValues(m_RefZaehler, doStringWithDateReference("---", inout.getAdmissionDate()), ruleEle.m_criterionIndex, 99);
//                            return chkValue;
//                         } else{
//                             count4Interval = 1;                         
//                             continue;
//                         }
//                     }
//                     
//// berechnen des Zeitintervals zwischen zwei Treffer und Vergleich mit count und days Parameter  
//                         long interval = 0;
//                         if(hasPseudo){
//                             if(pseudoInd >= admDepsPseudo.length || dateValues[i] < admDepsPseudo[pseudoInd]){
//                                 interval = (int)(dateValues[i] - lastDate )/ONE_DAY;
//                             } else{
//                                  
// // die Zeit für die Pseudoabteilungen abziehen
//                                 // if(pseudoInd < admDepsPseudo.length){
//                                      if(dateValues[i] == admDepsFirstAfterPseudo[pseudoInd]){                                     
//                                        pseudoInd++;
//                                        count4Interval = 1;
//                                        lastDate = dateValues[i];
//                                        continue;
//                                      } else{
//// nach der Pseudoabteilung muss Prozedur auf dem ersten Tag der Aufnahme durchgeführt werden
//                                        m_RefZaehler++;
//                                        setRefValues(m_RefZaehler, doStringWithDateReference("---", new Date(admDepsFirstAfterPseudo[pseudoInd])), ruleEle.m_criterionIndex, 99);
//                                        chkValue.setLastState(!ruleEle.m_notElement);
//                                        return chkValue;
//                                          
//                                      }
//                                 //}
//                                  
//                              
//                                
//                             }
//                         }else{
//                            interval = (dateValues[i] - lastDate )/ONE_DAY;
//                         }
//                         if((interval == 0 && count4Interval == 1) || interval <= days){
//                             
//                            if(interval == days){
//                                count4Interval = 1;
//                            } else{
//                                count4Interval++;
//                            }
//                            int j = i;
//                             while((i < n -1) && dateValues[j] == dateValues[++i]){
//                                 value = critValues[i];
//                                 if(checkRuleStringValue(value, ruleEle.m_strArrayValue, 
//                                         CRGRuleGrouperStatics.OP_IN_TABLE, 
//                                         ruleEle.m_strArrayValue.length)){
//                                     count4Interval++;
//                                    
//                               }
//                             }
//                             if(count4Interval < count){
//                                chkValue.setLastState(!ruleEle.m_notElement);
//                                for(int k = j; k < i; k++){
//                                    m_RefZaehler++;
//                                    setRefValues(m_RefZaehler, doStringWithDateReference(critValues[k], dateValuesDat[k]), ruleEle.m_criterionIndex, 99);
//                                }
//                                chkValue.setLastState(!ruleEle.m_notElement);
//                                return chkValue; 
//                             }else{
//                                 lastDate = dateValues[j];
//                                 if(j < i)
//                                    i--; // muss -1, damit keine verloren geht, da in der while - Schleife wurde schon hochgezählt
//                                 continue;
//                             }
//                         }else{
//                             // entfernung zwischen Zwei Prozeduren nicht entspricht dem angegebenen interval
//                                m_RefZaehler++;
//                                if(interval > days){
//                                    setRefValues(m_RefZaehler, doStringWithDateReference("---", new Date(lastDate + ONE_DAY * days)), ruleEle.m_criterionIndex, 99);                                   
//                                }else{
//                                    setRefValues(m_RefZaehler, doStringWithDateReference(value, dateValuesDat[i]), ruleEle.m_criterionIndex, 99);
//                                }
//                             chkValue.setLastState(!ruleEle.m_notElement);
//                             return chkValue;
//                         }
//                 }
//                 
//             }
//             if(hasPseudo && pseudoInd < admDepsPseudo.length){
//                   m_RefZaehler++;
//                    setRefValues(m_RefZaehler, doStringWithDateReference("---", new Date(admDepsFirstAfterPseudo[pseudoInd])), ruleEle.m_criterionIndex, 99);
//                    chkValue.setLastState(!ruleEle.m_notElement);
//             }else{
//                if((inout.getDischargeDateLong() - lastDate )/86400000 < days){
//                    chkValue.setLastState(ruleEle.m_notElement);
//                }else{
//                    m_RefZaehler++;
//                    setRefValues(m_RefZaehler, doStringWithDateReference("---", new Date(lastDate + ONE_DAY * days)), ruleEle.m_criterionIndex, 99);
//                    chkValue.setLastState(!ruleEle.m_notElement);
//                }
//             }
//                 
//            return chkValue;
//         }
        
  /**
  * bearbeitet eingebaute Method:
  * @param ruleEle
  * @param chkValue
  * @param inout
  * @return 
  */       
        private CheckValue doMethod(CRGRuleElement ruleEle, CheckValue chkValue, CRGInputOutputBasic inout)throws Exception
        
        {
//            nach der Enpfehlung von Herrn Vollrath rausgenommen, da nicht mehr aktuell ist Siehe CPX-630
//            switch(ruleEle.m_methodType){
//                case CRGRuleGrouperStatics.METHOD_TYPE_MAX_INTERVAL_PROPERTY:
//                    return doMaxIntervalPropertyMethod(ruleEle, chkValue, inout);
//                case CRGRuleGrouperStatics.METHOD_TYPE_MAX_INTERVAL_TABLE:
//                    return doMaxIntervalTableMethod(ruleEle, chkValue, inout);
//                    
//            }
            return chkValue;
        }
/**
 * überprüft ein Regelterm 
 */
	private CheckValue checkOneElementValue(CRGRuleElement ruleEle, CheckValue chkValue, int state,
		CRGInputOutputBasic inout, boolean depended) throws Exception
	{
		if(ruleEle.m_operantCompute) { // (+ - * /) und computeType ist ( both, krit, value, operator, method)
                    if(ruleEle.m_computeType == CRGRuleGrouperStatics.COMPUTE_TYPE_METHOD){
                       return doMethod(ruleEle, chkValue, inout);
                    }else if(ruleEle.m_computeType == CRGRuleGrouperStatics.COMPUTE_TYPE_VALUE) { //Inhalt muss berechnet werden
				switch(ruleEle.m_valueType) {
					case CRGRuleGrouperStatics.DATATYPE_INTEGER:
					case CRGRuleGrouperStatics.DATATYPE_DOUBLE: {
						double doubleValue = ruleEle.m_valueType == CRGRuleGrouperStatics.DATATYPE_INTEGER ?
											 ruleEle.m_intValue : ruleEle.m_doubleValue;
						chkValue.setDoubleValue(doubleValue);
						chkValue.setActualType(CRGRuleGrouperStatics.DATATYPE_DOUBLE);
						chkValue.setLastState(chkValue.getCheckValue(state, chkValue.getLastState(),
							CRGRuleGrouperStatics.TYPE_VALUE));
						break;
					}
					case CRGRuleGrouperStatics.DATATYPE_ARRAY_INTEGER:
					case CRGRuleGrouperStatics.DATATYPE_ARRAY_DOUBLE: {
						double[] doubleArray = null;
						if(ruleEle.m_valueType == CRGRuleGrouperStatics.DATATYPE_ARRAY_INTEGER) {
							doubleArray = new double[ruleEle.m_intArrayValue.length];
							for(int j = 0; j < ruleEle.m_intArrayValue.length; j++) {
								doubleArray[j] = ruleEle.m_intArrayValue[j];
							}
						} else {
							doubleArray = ruleEle.m_doubleArrayValue;
						}
						chkValue.setDoubleArray(doubleArray);
						chkValue.setActualType(CRGRuleGrouperStatics.DATATYPE_ARRAY_DOUBLE);
						chkValue.setLastState(chkValue.getCheckValue(state, chkValue.getLastState(),
							CRGRuleGrouperStatics.TYPE_VALUE));
						break;
					}
					case CRGRuleGrouperStatics.DATATYPE_STRING: {
						chkValue.setStringValue(ruleEle.m_strValue);
						chkValue.setActualType(CRGRuleGrouperStatics.DATATYPE_STRING);
						chkValue.setLastState(chkValue.getCheckValue(state, chkValue.getLastState(),
							CRGRuleGrouperStatics.TYPE_VALUE));
						break;
					}
					case CRGRuleGrouperStatics.DATATYPE_ARRAY_STRING: {
						chkValue.setStringArray(ruleEle.m_strArrayValue);
						chkValue.setActualType(CRGRuleGrouperStatics.DATATYPE_ARRAY_STRING);
						chkValue.setLastState(chkValue.getCheckValue(state, chkValue.getLastState(),
							CRGRuleGrouperStatics.TYPE_VALUE));
						break;
					}
					case CRGRuleGrouperStatics.DATATYPE_DATE: {
						chkValue.setLongValue(ruleEle.m_longValue);
						chkValue.setActualType(CRGRuleGrouperStatics.DATATYPE_DATE);
						chkValue.setLastState(chkValue.getCheckValue(state, chkValue.getLastState(),
							CRGRuleGrouperStatics.TYPE_VALUE));
						break;
					}
					case CRGRuleGrouperStatics.DATATYPE_DAY_TIME: {
						chkValue.setLongValue(ruleEle.m_longtimeValue);
						chkValue.setActualType(CRGRuleGrouperStatics.DATATYPE_DAY_TIME);
						chkValue.setLastState(chkValue.getCheckValue(state, chkValue.getLastState(),
							CRGRuleGrouperStatics.TYPE_VALUE));
						break;
					}
					case CRGRuleGrouperStatics.DATATYPE_ARRAY_DAY_TIME: {
						chkValue.setLongTimeArray(ruleEle.m_longtimeArrayValue);
						chkValue.setActualType(CRGRuleGrouperStatics.DATATYPE_ARRAY_DAY_TIME);
						chkValue.setLastState(chkValue.getCheckValue(state, chkValue.getLastState(),
							CRGRuleGrouperStatics.TYPE_VALUE));
						break;
					}
					case CRGRuleGrouperStatics.DATATYPE_ARRAY_DATE: {
						chkValue.setLongArray(ruleEle.m_longArrayValue);
						chkValue.setActualType(CRGRuleGrouperStatics.DATATYPE_ARRAY_DATE);
						chkValue.setLastState(chkValue.getCheckValue(state, chkValue.getLastState(),
							CRGRuleGrouperStatics.TYPE_VALUE));
						break;
					}
				}
			} else {
// hier wird ein Kriterium überprüft, wenn es ein Interval hat, muss es zuerst überprüft werden
//							if(!m_isGK || (m_isGK && checkRuleElementInterval(ruleEle, inout))) { // interval überprüfen
				if(ruleEle.m_criterionDependIndex > 0 && (chkValue.lastGroupIndex < 0 ||
					chkValue.lastGroupIndex > 0 && chkValue.lastGroupIndex != ruleEle.m_criterionDependIndex
                                        )
                                        || ruleEle.hasInterval
                                        ) {
					chkValue.resetGroupIndexResult(chkValue.lastGroupIndex);
//                                       if(!ruleEle.hasInterval){
                                            chkValue.lastGroupIndex = ruleEle.m_criterionDependIndex;
//                                        }

				}
				switch(ruleEle.m_criterionType) {
					case CRGRuleGrouperStatics.DATATYPE_INTEGER:
					case CRGRuleGrouperStatics.DATATYPE_DOUBLE: {
//						if(!m_isGK || (m_isGK && checkRuleElementInterval(ruleEle, inout, 0))) {
						if(checkRuleElementInterval(ruleEle, inout, 0)) {
							double doubleValue = ruleEle.m_criterionType ==
												 CRGRuleGrouperStatics.DATATYPE_INTEGER ?
												 getRuleIntegerArray(ruleEle, inout)[0] : getRuleDoubleArray(ruleEle,
												 inout)[0];
							chkValue.setDoubleValue(doubleValue);
							chkValue.setActualType(CRGRuleGrouperStatics.DATATYPE_DOUBLE);
							chkValue.setLastState(chkValue.getCheckValue(state, chkValue.getLastState(),
								CRGRuleGrouperStatics.TYPE_VALUE));
							break;
						} else {
							chkValue.setLastState(false);
							return chkValue;
						}
					}
					case CRGRuleGrouperStatics.DATATYPE_ARRAY_INTEGER:
					case CRGRuleGrouperStatics.DATATYPE_ARRAY_DOUBLE: {
						double[] doubleArray = null;
						if(ruleEle.m_criterionType == CRGRuleGrouperStatics.DATATYPE_ARRAY_INTEGER) {
							doubleArray = getRuleIntegerArray(ruleEle, inout);
						} else {
							doubleArray = getRuleDoubleArray(ruleEle, inout);
						}
						chkValue.setDoubleArray(doubleArray);
						chkValue.setActualType(CRGRuleGrouperStatics.DATATYPE_ARRAY_DOUBLE);
						chkValue.setLastState(chkValue.getCheckValue(state, chkValue.getLastState(),
							CRGRuleGrouperStatics.TYPE_VALUE));
						break;
					}
					case CRGRuleGrouperStatics.DATATYPE_DATE: {
						chkValue.setLongValue(getRuleLongArray(ruleEle, inout)[0]);
						chkValue.setActualType(CRGRuleGrouperStatics.DATATYPE_DATE);
						chkValue.setLastState(chkValue.getCheckValue(state, chkValue.getLastState(),
							CRGRuleGrouperStatics.TYPE_VALUE));
						break;
					} 
					case CRGRuleGrouperStatics.DATATYPE_DAY_TIME: {
						chkValue.setLongValue(getRuleLongArray(ruleEle, inout)[0]);
						chkValue.setActualType(CRGRuleGrouperStatics.DATATYPE_DAY_TIME);
						chkValue.setLastState(chkValue.getCheckValue(state, chkValue.getLastState(),
							CRGRuleGrouperStatics.TYPE_VALUE));
						break;
					}
					case CRGRuleGrouperStatics.DATATYPE_ARRAY_DATE: {
						chkValue.setLongArray(getRuleLongArray(ruleEle, inout));
						chkValue.setActualType(CRGRuleGrouperStatics.DATATYPE_ARRAY_DATE);
						chkValue.setLastState(chkValue.getCheckValue(state, chkValue.getLastState(),
							CRGRuleGrouperStatics.TYPE_VALUE));
						break;
					}
					case CRGRuleGrouperStatics.DATATYPE_ARRAY_DAY_TIME: {
						chkValue.setLongTimeArray(getRuleLongArray(ruleEle, inout));
						chkValue.setActualType(CRGRuleGrouperStatics.DATATYPE_ARRAY_DAY_TIME);
						chkValue.setLastState(chkValue.getCheckValue(state, chkValue.getLastState(),
							CRGRuleGrouperStatics.TYPE_VALUE));
						break;
					}
					case CRGRuleGrouperStatics.DATATYPE_STRING: {
						chkValue.setStringValue(getRuleStringArray(ruleEle, inout)[0]);
						chkValue.setActualType(CRGRuleGrouperStatics.DATATYPE_STRING);
						chkValue.setLastState(chkValue.getCheckValue(state, chkValue.getLastState(),
							CRGRuleGrouperStatics.TYPE_VALUE));
						break;
					}
					case CRGRuleGrouperStatics.DATATYPE_ARRAY_STRING: {
						chkValue.setStringArray(getRuleStringArray(ruleEle, inout));
                                                
						chkValue.setActualType(CRGRuleGrouperStatics.DATATYPE_ARRAY_STRING);
						chkValue.setLastState(chkValue.getCheckValue(state, chkValue.getLastState(),
							CRGRuleGrouperStatics.TYPE_VALUE));
						break;
					}
				}
				/*							}
				 else {
				 chkValue.setLastState(false);
				  return chkValue;
				 }*/
			}
		} else {
			switch(ruleEle.m_criterionType) {
				case CRGRuleGrouperStatics.DATATYPE_INTEGER:
				case CRGRuleGrouperStatics.DATATYPE_DOUBLE:
				case CRGRuleGrouperStatics.DATATYPE_STRING:
				case CRGRuleGrouperStatics.DATATYPE_DAY_TIME:
				case CRGRuleGrouperStatics.DATATYPE_DATE: {

					break;
				}
				case CRGRuleGrouperStatics.DATATYPE_ARRAY_STRING:
				case CRGRuleGrouperStatics.DATATYPE_ARRAY_INTEGER:
				case CRGRuleGrouperStatics.DATATYPE_ARRAY_DOUBLE:
				case CRGRuleGrouperStatics.DATATYPE_ARRAY_DAY_TIME:
				case CRGRuleGrouperStatics.DATATYPE_ARRAY_DATE: {
					chkValue.setIsArray(true);
					break;
				}
			}
			//-----------------------------------------
			// häufigste Eingang
			//-----------------------------------------
			chkValue.setLastState(checkRuleValue(ruleEle, inout, chkValue, state));
		}
		chkValue = checkDependances(ruleEle, chkValue, inout, depended);
		return chkValue;
	}

	//Wenn das Element abhängig von anderen abhängig ist, diese prüfen
	//isDepend = (ruleEle.m_depend!=CRGRuleGrouperStatics.DEPEND_NO)? true:isDepend;
	//-----------------------------------
	//ruleEle <-- aktual geprüfte Element
	// kein Array, d.h. dass bisher kein Array war und auch der aktuelle kein Array beinhaltet
	// da alle abhängigkeiten Array-abhängig sind, sollte der false-teil nie durchlaufen werden.
	// d.h. das der crittype vom Datentyp Array ist, der ValueTyp muss aber nicht vom Datentyp Array sein
	//-----------------------------
	// wie Regel MD129: Diagnose == 'D62' && Prozedur == '8-800*'
	// es sind beides Kriterien, zu denen Abhängigkeiten bestehen können
	// die Regel ist so, dass dann folgend nur noch zum zweiten Kriterium (hier: Prozedur)
	// nachfolgend Abhängigkeiten angegeben werden können.
	//z.B. Diagnose == 'D62' && Prozedur == '8-800*' && OPSDatum == '20080202' möglich
	//     Diagnose == 'D62' && Prozedur == '8-800*' && Diagnose-Lokalisation == '2' nicht möglich
//-------------
	// es muss aber noch überprüft werden, ob in dependInex == ruleEle.m_depend, dann natürlich nicht zurücksetzen
	// auch nicht zurücksetzen wenn dependInex überall == 0, dann muss ein false kommen bei Abhängigkeit!!!!
	// nein in diesem Fall ist sowieso lastState = false !
//-------------
	// muss zusätzliche Variable einfügen, in der m_criterionDepend.m_index bei m_criterionDepend.m_indexDepend == 0
	// bzw. m_criterionDepend.m_indexDepend != 0.
	//---nicht nötig bisher.
	/*
	  Beispiel: OPSDatum					mehrere Möglichkeiten: 	1. bis && ist true, aber bei keiner Prozedur 5*
	   <													2. bis && ist true, auch bei Prozedur 5*
	   AufnahmeDatum + 2T									3. bis && ist false
	   &&
	 Prozedur == '5*'			nicht löschen, wenn in dependInex der richtige Abhängigkeitswert steht!!!
	 (in diesem Fall der für Prozedur)

	  Beispiel: Diagnose == 'A*'			Die Reihenfolge ist richtig
	   &&
	   Lokalisation == 1
	   &&
	   Prozedur == '5*'
	 */
	private CheckValue checkDependances(CRGRuleElement ruleEle, CheckValue chkValue,
		CRGInputOutputBasic inout, boolean depended) throws Exception
	{
		if(depended) {
			if((ruleEle.m_depend != CRGRuleGrouperStatics.DEPEND_NO) && (chkValue.getLastState()
				|| (!chkValue.getIsCompare() && !chkValue.getIsCompute()))) {
				if(chkValue.getIsArray()) {
					//wie Regel MD129: hier dann oldDependVals zurücksetzen.
					if(chkValue.dependIndex != null) {
						if((ruleEle.m_criterionDepend[ruleEle.m_depend - 1].m_indexDepend == 0) &&
							(!chkValue.isIndexDepend) ||
// zwei gleichen Kriterien nebeneiander
							ruleEle.m_criterionDepend[ruleEle.m_depend - 1].m_index == chkValue.lastDepended) {
							chkValue.resetDepends();
						} else {
							boolean found = false;
							for(int j = 0; j < chkValue.dependIndex.length; j++) {
								int fou = ruleEle.m_criterionDepend[ruleEle.m_depend - 1].m_indexDepend ==
										  0 ?
										  ruleEle.m_criterionDepend[ruleEle.m_depend - 1].m_index
										  : ruleEle.m_criterionDepend[ruleEle.m_depend - 1].m_indexDepend;
								if(fou == chkValue.dependIndex[j]) {
										found = true;
								}
							}
							if(!found) {
								chkValue.resetDepends();
							}
						}
					}
					//System.out.println("depend");
					chkValue.dependIndex = getDependIndexValues(ruleEle, inout, chkValue);
				} else {
					chkValue.dependIndex = getDependIndexValues(ruleEle, inout, chkValue);
				}
				//----------------------------------------------------------------------
				// Wenn die Abhängigkeit zum ersten Mal gesetzt wird
				// soll nicht geprüft werden, kann auch bei i > 0
				// der Fall sein.
				//----------------------------------------------------------------------
				if(chkValue.isDepend) { //Ermittelt aktuellen Status anhand der abhängigen Werte
					chkValue.setLastState(this.getDependState(chkValue.dependIndex));
					// hier an der Stelle fehlte das negative. volker
					if(ruleEle.m_notElement) {
						chkValue.setLastState(!chkValue.getLastState());
					}
				}
				chkValue.isDepend = true;
				chkValue.isIndexDepend = ruleEle.m_criterionDepend[ruleEle.m_depend - 1].m_indexDepend == 0 ? false : true;
				chkValue.lastDepended = ruleEle.m_criterionDepend[ruleEle.m_depend - 1].m_index;
				chkValue.lastDependIndex = ruleEle.m_criterionDepend[ruleEle.m_depend - 1].m_indexDepend ==
										   0 ?
										   ruleEle.m_criterionDepend[ruleEle.m_depend - 1].m_index
										   : ruleEle.m_criterionDepend[ruleEle.m_depend - 1].m_indexDepend;

				if(CRGRuleGrouperManager.isOperatorMany( ruleEle.m_operantType))
					chkValue.setDependCountForMany();
				else
					chkValue.setDependCountForSingle();
			} else {
				if(chkValue.isDepend) {
					//hier muss nur in dependInex geändert werden:
					//dort wo in stateArray ein false ist darf kein Wert mehr in dependInex stehen.
					chkValue.dependIndex = this.getDependState(chkValue.dependIndex, chkValue.getStateArray());
				}
			}

		}
		return chkValue;

	}

	/**
	 * Prüft die Regel-Elemente eines Regelknotens.
	 * @param childElements CRGRuleElement[] : Array mit Regel-Elementen (Child-Knoten)
	 * @param rsize int : Anzahl der Regel-Elemente
	 * @param inout CRGInputOutputBasic : Objekt mit Falldaten
	 * @return boolean
	 * @throws Exception
	 */
	private CheckValue checkRuleElements(CRGRuleElement[] childElements, int rsize, boolean depended,
		CheckValue chkValue, CRGInputOutputBasic inout, boolean isCC, boolean hasNot) throws Exception
	{

		int state = CRGRuleGrouperStatics.OP_NO_OPERATION; //Status 1 = UND, Status 2 = ODER, Status 0 = kein
		if(chkValue == null) {
			chkValue = new CheckValue();
		}
		CRGRuleElement ruleEle;
		/*boolean
		 lastState = false,isCompare = false, isCompute = false, isDepend = false, isIndexDepend = false;*/
		//boolean isDepend = false; doch nur Abhängigkei prüfen, wenn wirklich vorhanden nicht immer reingehen
		//boolean[] lastStateArray = null;
		int i = 0;
		chkValue.setAllStateArray();
		boolean skipTillOr = false;
		while(i < rsize) {
			ruleEle = childElements[i];
			if(ruleEle.m_isCrossCase) {
				chkValue.setLastState(false);
				return chkValue;
			}
			switch(ruleEle.m_type) {
				case CRGRuleGrouperStatics.TYPE_ELEMENT: { //weiteres verschachteltes Element
					if(!skipTillOr) {
						chkValue = checkOneElement(ruleEle, inout, state, depended, chkValue, isCC);
					}
					break;
				}
				case CRGRuleGrouperStatics.TYPE_VALUE: { //es handelt sich um Kriteriums-Wert
					//bei allen Durchläufen, auch mehrmals
					if(!skipTillOr) {
						chkValue = checkOneElementValue(ruleEle, chkValue, state, inout, depended);
					}
					break;

				}
				case CRGRuleGrouperStatics.TYPE_OPERATOR: {
					state = ruleEle.m_operantType;
					boolean isCompare = CRGRuleGrouperManager.isOperatorCompare(state);
					chkValue.setIsCompare(isCompare);
					boolean isCompute = CRGRuleGrouperManager.isOperatorCompute(state);
					chkValue.setIsCompute(isCompute);
					if(state <= CRGRuleGrouperStatics.OP_OR) {
						chkValue.setLastCompareState(0);
					}
					break;
				}
			}
			if(skipTillOr) { // weiterleiten bis OR
				if(state == CRGRuleGrouperStatics.OP_OR) {
					skipTillOr = false;
				} else {
					i++;
					continue;
				}

			}
			if(!chkValue.getLastState() && state == CRGRuleGrouperStatics.OP_AND) {
				skipTillOr = true;
			}

			if(chkValue.getLastState() && state == CRGRuleGrouperStatics.OP_OR
				&& (ruleEle.m_nextOperantType == CRGRuleGrouperStatics.OP_OR
				|| ruleEle.m_nextOperantType <= 0)) {
				chkValue.setLastState(true);
//				return chkValue;
				break;
			}
			i++;
		}
/*		if(childElements.length >=3){ // wenn es in den Klammern mehr als 2 elementen steht (Z.b. prozedure && opsLokalisation sind eingeklammert, werden die Dependencies nach dem Klammernverlassen zurückgesetzt
			chkValue.resetDepends();
		}*/
		chkValue.setLastStateWithNot(hasNot);
//                if(chkValue.getLastState()){
//                    int[] indVal = inout.getClinicIndexValues();
//                    if(indVal != null && indVal.length > 0 && chkValue.groupDependResults != null){
//                        for(int ii = 0; ii < indVal.length; ii++){
//                           if(indVal[ii] < chkValue.groupDependResults.length && chkValue.groupDependResults[indVal[ii]]) {
//                                m_RefZaehler++;
//                                setRefValues(m_RefZaehler, inout.getHospitalCaseIdent(indVal[ii]), CRGRuleGrouperStatics.CRITARRAYSTRING_INDEX_CLINIC_CASE_NR, 99);
//	
//                           }
//                        }
//                    }
//                }
		return chkValue;

	}

	/**
	 * Prüft die Regel-Elemente eines Regelknotens.
	 * @param childElements CRGRuleElement[] : Array mit Regel-Elementen (Child-Knoten)
	 * @param rsize int : Anzahl der Regel-Elemente
	 * @param inout CRGInputOutputBasic : Objekt mit Falldaten
	 * @return boolean
	 * @throws Exception
	 */
	private CheckValue checkRuleElementsCC(CRGRuleElement[] childElements, int rsize, boolean depended,
		CheckValue chkValue, boolean isNot) throws Exception
	{
		int state = CRGRuleGrouperStatics.OP_NO_OPERATION; //Status 1 = UND, Status 2 = ODER, Status 0 = kein
		int lastOperand = -1, lastButOneOperand = -1;
		CRGInputOutputBasic inout = null;
		if(chkValue == null) {
			chkValue = new CheckValue();
		}
		CRGRuleElement ruleEle;
		boolean isVorhanden = false;
		boolean lastButOneState = false;

		chkValue.setAllStateArray();
		//------------------------------------------------
		// außenrum die Schleife mit den anderen Fällen
		//------------------------------------------------
		int i = 0;
		while(i < rsize) {
			ruleEle = childElements[i];
			if(ruleEle.m_isCrossCase) {
				isVorhanden = true;
				break;
			}
			i++;
		}
		int startIndex = 0;
		// aktuellen Fall und dann auch abhängig von dem Zeitinterval
		// in m_formerAkt ist der aktuelle Fall
		// in m_formerSort steht die Reihenfolge der Fälle
		// in m_formerCount steht die Anzahl der Fälle
		if(isVorhanden) {
			boolean endLoop = false;
			//int lauf = 0;
			//for(int lauf1=0;lauf1<m_formerCount;lauf1++){
			for(int lauf = 0; lauf < m_tmpFormerCount; lauf++) {
				//lauf = m_formerSort[lauf1];
				if(lauf != m_tmpFormerAkt) {
					i = startIndex;
					while(i < rsize) {
						ruleEle = childElements[i];
						if(ruleEle.m_isCrossCase) {
							inout = m_tmpFormerInOuts[lauf];
						} else {
							inout = m_tmpFormerInOuts[m_tmpFormerAkt];
						}
						switch(ruleEle.m_type) {
							case CRGRuleGrouperStatics.TYPE_ELEMENT: { //weiteres verschachteltes Element
								lastButOneState = chkValue.getLastState();
								chkValue = checkOneElement(ruleEle, inout, state, depended, chkValue, true);
								lastButOneOperand = lastOperand;
								lastOperand = i;
								break;
							}
							case CRGRuleGrouperStatics.TYPE_VALUE: { //es handelt sich um Kriteriums-Wert
								//bei allen Durchläufen, auch mehrmals
								lastButOneState = chkValue.getLastState();
								chkValue = checkOneElementValue(ruleEle, chkValue, state, inout, depended);
								if(lastOperand != i) {
									lastButOneOperand = lastOperand;
									lastOperand = i;
								}
								break;
							}
							case CRGRuleGrouperStatics.TYPE_OPERATOR: {
								state = ruleEle.m_operantType;
								boolean isCompare = CRGRuleGrouperManager.isOperatorCompare(state);
								chkValue.setIsCompare(isCompare);
								boolean isCompute = CRGRuleGrouperManager.isOperatorCompute(state);
								chkValue.setIsCompute(isCompute);
								if(state <= CRGRuleGrouperStatics.OP_OR) {
									chkValue.setLastCompareState(0);
								}
								break;
							}
						}
						if(state == CRGRuleGrouperStatics.OP_AND && !lastButOneState && lastButOneOperand >= 0
							/*&& childElements[lastButOneOperand].m_isCrossCase */
							&&
							childElements[lastOperand].m_isCrossCase) { // da bei fallübergreifenden Kriterien bei lastState ==false und AND nicht abgebrochen wird, muss false weitergereicht werden
							chkValue.setLastState(false);
						}
						if(!chkValue.getLastState() && state == CRGRuleGrouperStatics.OP_AND) {
							if(lastButOneOperand >= 0 && lastButOneOperand < lastOperand
								&& !childElements[lastButOneOperand].m_isCrossCase
								&& childElements[lastOperand].m_isCrossCase) { // weitere Fälle nur auf den Rest der Regel zu überprüfen
								if(lastOperand > startIndex) {
									startIndex = lastOperand;
									lauf = -1;
									chkValue.setLastState(lastButOneState);
								} else {
									if(lauf != m_tmpFormerCount - 2) { // m_formerCount - 1 is immer AktCase
										chkValue.setLastState(lastButOneState);
									} else {
										if(i > startIndex) {
											startIndex = i; // weil es weiter || kommen kann, nur den Rest der Regel überprüfen
											lauf = -1;
											chkValue.setLastState(false);
										} else {
											startIndex++;
											lauf = -1;
											if(startIndex >= rsize) { // es wurden alle Fälle bis zum letzten Operan überprüft
												endLoop = true;
											}
										}
									}
								}
								break;
							} else {
								/*								if(!childElements[lastOperand].m_isCrossCase) {
								   chkValue.setLastState(false);
								   return chkValue;
								  }*/
							}
						}
						if(chkValue.getLastState() && state == CRGRuleGrouperStatics.OP_OR) {
							return chkValue;
						}
						i++;
					} // ende von while(i<rsize)
					if(!childElements[lastOperand].m_isCrossCase || (startIndex == 0 && lauf != -1) || endLoop) {
						break;
					}
				} //ende von if(lauf!=m_formerAkt)
				//--------------------------------------------------------
				// hier zu klären, was zu tun ist:
				// wenn nur in einem Fall, dann bei true ein break
				//
				//--------------------------------------------------------
				/*				if(chkValue.getLastState()) {
				  break;
				 }*/
			} //ende von for(int lauf=0;lauf<m_formerCount;lauf++)
		} else { //else von if(isVorhanden)
			chkValue = checkRuleElements(childElements, rsize, depended, null, m_formerInOuts[m_formerAkt], false, isNot);
		} //ende von if(isVorhanden)
		return chkValue;
	}

	/** 1.Stufe
	 * Ermittelt aus den Regel-Elementen eines Knotens den Integer-Wert
	 * ACHTUNG: nur anwendbar, wenn auch Rechen-Operatoren vorhanden sind
	 * @param childElements CRGRuleElement[] : Regel-Elemente
	 * @param rsize int : Anzahl der Regel-Elemente
	 * @param inout CRGInputOutputBasic : Objekt mir Falldaten
	 * @return double : errechneter Wert aus den Regel-Elementen
	 * @throws Exception
	 */
	private double getRuleElementIntegerValue(CRGRuleElement[] childElements, int rsize,
		CRGInputOutputBasic inout) throws Exception
	{
		int state = CRGRuleGrouperStatics.OP_NO_OPERATION; //Status 1 = UND, Status 2 = ODER, Status 0 = kein
		CRGRuleElement ruleEle;
		double lastValue = 0, actualVal = 0;
		int i = 0;
		while(i < rsize) {
			ruleEle = childElements[i];
			switch(ruleEle.m_type) {
				case CRGRuleGrouperStatics.TYPE_ELEMENT: {
					if(state > CRGRuleGrouperStatics.OP_OR) {
						actualVal = getRuleElementIntegerValue(ruleEle.m_childElements, ruleEle.m_childCount, inout);
					} else {
						actualVal = 0;
					}
					lastValue = getRuleComputeValueDouble(lastValue, actualVal, state);
					break;
				}
				case CRGRuleGrouperStatics.TYPE_VALUE: {
					if(ruleEle.m_operantCompute) {
						actualVal = getRuleIntegerArray(ruleEle, inout)[0];
					} else {
						actualVal = 0;
					}
					lastValue = getRuleComputeValueDouble(lastValue, actualVal, state);
					break;
				}
				case CRGRuleGrouperStatics.TYPE_OPERATOR: {
					state = ruleEle.m_operantType;
					break;
				}
			}
			i++;
		}
		return lastValue;
	}

	private double[] getRuleElementIntegerArray(CRGRuleElement[] childElements, int rsize,
		CRGInputOutputBasic inout) throws Exception
	{
		int state = CRGRuleGrouperStatics.OP_NO_OPERATION; //Status 1 = UND, Status 2 = ODER, Status 0 = kein
		CRGRuleElement ruleEle;
		double[] lastValue = new double[0];
		double[] actualVal = new double[0];
		int i = 0;
		while(i < rsize) {
			ruleEle = childElements[i];
			switch(ruleEle.m_type) {
				case CRGRuleGrouperStatics.TYPE_ELEMENT: {
					double[] act = getRuleElementIntegerArray(ruleEle.m_childElements, ruleEle.m_childCount, inout);
					if(state > CRGRuleGrouperStatics.OP_OR) {
						actualVal = act;
					} else {
						if(act != null) {
							actualVal = new double[act.length];
						}
					}
					for(int j = 0; j < actualVal.length; j++) {
						if(lastValue.length < j + 1) {
							lastValue = setArrayDoubleValue(j, 0, lastValue);
						}
						lastValue[j] = getRuleComputeValueDouble(lastValue[j], actualVal[j], state);
					}
					break;
				}
				case CRGRuleGrouperStatics.TYPE_VALUE: {
					double[] act = getRuleIntegerArray(ruleEle, inout); //getRuleLongValue(ruleEle, inout);
					if(act == null ||
						act.length == 0 && ruleEle.m_computeType == CRGRuleGrouperStatics.COMPUTE_TYPE_CRITERIUM) {
						return lastValue;
					}
					if(ruleEle.m_operantCompute) {
						actualVal = act;
					} else {
						if(act != null) {
							actualVal = new double[act.length];
						}
					}
					for(int j = 0; j < actualVal.length; j++) {
						if(lastValue.length < j + 1) {
							lastValue = setArrayDoubleValue(j, 0, lastValue);
						}
						lastValue[j] = getRuleComputeValueDouble(lastValue[j], actualVal[j], state);
					}
					break;
				}
				case CRGRuleGrouperStatics.TYPE_OPERATOR: {
					state = ruleEle.m_operantType;
					break;
				}
			}
			i++;
		}
		return lastValue;
	}

	private double[] setArrayDoubleValue(int ind, double value, double[] array)
	{
		try {
			array[ind] = value;
		} catch(IndexOutOfBoundsException e) {
			int i;
			double[] tmp = new double[ind + 1];
			for(i = 0; i < array.length; i++) {
				tmp[i] = array[i];
			}
			for(i = i; i < tmp.length; i++) {
				tmp[i] = -1;
			}
			array = tmp;
			array = setArrayDoubleValue(ind, value, array);
		}
		return array;
	}

	/** 1.Stufe
	 * Ermittelt aus den Regel-Elementen eines Knotens den Double-Wert
	 * ACHTUNG: nur anwendbar, wenn auch Rechen-Operatoren vorhanden sind
	 * @param childElements CRGRuleElement[] : Regel-Elemente
	 * @param rsize int : Anzahl der Regel-Elemente
	 * @param inout CRGInputOutputBasic : Objekt mir Falldaten
	 * @return double : errechneter Wert aus den Regel-Elementen
	 * @throws Exception
	 */
	private double getRuleElementDoubleValue(CRGRuleElement[] childElements, int rsize,
		CRGInputOutputBasic inout) throws Exception
	{
		int state = CRGRuleGrouperStatics.OP_NO_OPERATION; //Status 1 = UND, Status 2 = ODER, Status 0 = kein
		CRGRuleElement ruleEle;
		double lastValue = 0d, actualVal = 0d;
		int i = 0;
		while(i < rsize) {
			ruleEle = childElements[i];
			switch(ruleEle.m_type) {
				case CRGRuleGrouperStatics.TYPE_ELEMENT: {
					if(state > CRGRuleGrouperStatics.OP_OR) {
						actualVal = getRuleElementDoubleValue(ruleEle.m_childElements, ruleEle.m_childCount, inout);
					} else {
						actualVal = 0d;
					}
					lastValue = getRuleComputeValueDouble(lastValue, actualVal, state);
					break;
				}
				case CRGRuleGrouperStatics.TYPE_VALUE: {
					if(ruleEle.m_operantCompute) {
						//hier unterscheiden integer oder double
						if(ruleEle.m_valueType == CRGRuleGrouperStatics.DATATYPE_INTEGER) {
							try {
								actualVal = getRuleIntegerArray(ruleEle, inout)[0];
							} catch(Exception e) {

								actualVal = 0;
//								ExcException.createException(e);
							}
						} else {
							try {
								actualVal = getRuleDoubleArray(ruleEle, inout)[0];
							} catch(Exception e) {
								actualVal = 0;
//								ExcException.createException(e);
							}
						}
					} else {
						actualVal = 0d;
					}
					lastValue = getRuleComputeValueDouble(lastValue, actualVal, state);
					break;
				}
				case CRGRuleGrouperStatics.TYPE_OPERATOR: {
					state = ruleEle.m_operantType;
					break;
				}
			}
			i++;
		}
		return lastValue;
	}

	private double[] getRuleElementDoubleArray(CRGRuleElement[] childElements, int rsize,
		CRGInputOutputBasic inout) throws Exception
	{
		int state = CRGRuleGrouperStatics.OP_NO_OPERATION; //Status 1 = UND, Status 2 = ODER, Status 0 = kein
		CRGRuleElement ruleEle;
		double[] lastVal = null;
		double[] actualVal = null;
		int i = 0;
		while(i < rsize) {
			ruleEle = childElements[i];
			switch(ruleEle.m_type) {
				case CRGRuleGrouperStatics.TYPE_ELEMENT: {
					if(state > CRGRuleGrouperStatics.OP_OR) {
						actualVal = getRuleElementDoubleArray(ruleEle.m_childElements, ruleEle.m_childCount, inout);
					}
					if(actualVal != null) {
						if(lastVal == null) {
							lastVal = actualVal;
						} else {
							int laenge = lastVal.length > actualVal.length ? lastVal.length : actualVal.length;
							for(int j = 0; j < laenge; j++) {
								lastVal[j] = getRuleComputeValueDouble(lastVal[j], actualVal[j], state);
							}
						}
					}
					break;
				}
				case CRGRuleGrouperStatics.TYPE_VALUE: { //Kriterium,Wert oder BOTH
					if(ruleEle.m_operantCompute) {
						switch(ruleEle.m_valueType) {
							case CRGRuleGrouperStatics.DATATYPE_ARRAY_DOUBLE:
							case CRGRuleGrouperStatics.DATATYPE_DOUBLE: {
								actualVal = getRuleDoubleArray(ruleEle, inout); //getRuleLongValue(ruleEle, inout);
								break;
							}
							case CRGRuleGrouperStatics.DATATYPE_ARRAY_INTEGER:
							case CRGRuleGrouperStatics.DATATYPE_INTEGER: {
								actualVal = getRuleIntegerArray(ruleEle, inout); //getRuleLongValue(ruleEle, inout);
								break;
							}
						}
					}
					if(lastVal == null) {
						if(actualVal != null) {
							lastVal = actualVal;
						}
					} else {
						if(actualVal.length == 1) {
							for(int j = 0; j < lastVal.length; j++) {
								lastVal[j] = getRuleComputeValueDouble(lastVal[j], actualVal[0], state);
							}
						} else {
							int laenge = lastVal.length > actualVal.length ? lastVal.length : actualVal.length;
							for(int j = 0; j < laenge; j++) {
								lastVal[j] = getRuleComputeValueDouble(lastVal[j], actualVal[j], state);
							}
						}
					}
					break;
				}
				case CRGRuleGrouperStatics.TYPE_OPERATOR: {
					state = ruleEle.m_operantType;
					break;
				}
			}
			i++;
		}
		return lastVal;
	}

	/** 1.Stufe
	 * Ermittelt aus den Regel-Elementen eines Knotens den zusammengesetzen String-Wert
	 * ACHTUNG: nur anwendbar, wenn auch Rechen-Operatoren vorhanden sind
	 * @param childElements CRGRuleElement[]
	 * @param rsize int
	 * @param inout CRG2InputOutput
	 * @return String
	 * @throws Exception
	 */
	private String getRuleElementStringValue(CRGRuleElement[] childElements, int rsize,
		CRGInputOutputBasic inout) throws Exception
	{
		int state = CRGRuleGrouperStatics.OP_NO_OPERATION; //Status 1 = UND, Status 2 = ODER, Status 0 = kein
		CRGRuleElement ruleEle;
		String lastValue = "", actualVal = "";
		int i = 0;
		/* 3.9.5 2015-09-01 DNi: #FINDBUGS - Endlosschleife, weil i >= rsize niemals true sein wird!
		 * Korrektur: Bitte sicherstellen, dass i auch inkrementiert wird! */
		while(i < rsize) {
			ruleEle = childElements[i];
			switch(ruleEle.m_type) {
				case CRGRuleGrouperStatics.TYPE_ELEMENT: {
					if(state > CRGRuleGrouperStatics.OP_OR) {
						actualVal = getRuleElementStringValue(ruleEle.m_childElements, ruleEle.m_childCount, inout);
					} else {
						actualVal = "";
					}
					lastValue = getRuleComputeValueString(lastValue, actualVal, state);
					break;
				}
				case CRGRuleGrouperStatics.TYPE_VALUE: {
					if(ruleEle.m_operantCompute) {
						actualVal = getRuleStringArray(ruleEle, inout)[0];
					} else {
						actualVal = "";
					}
					lastValue = getRuleComputeValueString(lastValue, actualVal, state);
					break;
				}
				case CRGRuleGrouperStatics.TYPE_OPERATOR: {
					state = ruleEle.m_operantType;
					break;
				}
			}
		}
		return lastValue;
	}

	private String[] getRuleElementStringArray(CRGRuleElement[] childElements, int rsize,
		CRGInputOutputBasic inout) throws Exception
	{
		int state = CRGRuleGrouperStatics.OP_NO_OPERATION; //Status 1 = UND, Status 2 = ODER, Status 0 = kein
		CRGRuleElement ruleEle;
		String[] lastValue = null;
		String[] actualVal = null;
		int i = 0;
		while(i < rsize) {
			ruleEle = childElements[i];
			switch(ruleEle.m_type) {
				case CRGRuleGrouperStatics.TYPE_ELEMENT: {
					if(state > CRGRuleGrouperStatics.OP_OR) {
						actualVal = getRuleElementStringArray(ruleEle.m_childElements, ruleEle.m_childCount, inout);
					}
					if(actualVal != null) {
						if(lastValue == null) {
							lastValue = actualVal;
						} else {
							int laenge = lastValue.length > actualVal.length ? lastValue.length : actualVal.length;
							for(int j = 0; j < laenge; j++) {
								lastValue[j] = getRuleComputeValueString(lastValue[j], actualVal[j], state);
							}
						}
					}
					break;
				}
				case CRGRuleGrouperStatics.TYPE_VALUE: {
					if(ruleEle.m_operantCompute) {
						actualVal = getRuleStringArray(ruleEle, inout); //getRuleLongValue(ruleEle, inout);
					}
					if(actualVal != null) {
						if(lastValue == null) {
							lastValue = actualVal;
						} else {
							int laenge = lastValue.length > actualVal.length ? lastValue.length : actualVal.length;
							for(int j = 0; j < laenge; j++) {
								lastValue[j] = getRuleComputeValueString(lastValue[j], actualVal[j], state);
							}
						}
					}
					break;
				}
				case CRGRuleGrouperStatics.TYPE_OPERATOR: {
					state = ruleEle.m_operantType;
					break;
				}
			}
			i++;
		}
		return lastValue;
	}

	/** 1.Stufe
	 * Ermittele aus den Regel-Elementen eines Knotens den Long-Wert
	 * ACHTUNG: nur anwendbar, wenn auch Rechen-Operatoren vorhanden sind
	 * @param childElements CRGRuleElement[] : Regel-Elemente
	 * @param rsize int : Anzahl der Regel-Elemente
	 * @param inout CRG2InputOutput : Objekt mir Falldaten
	 * @return long : errechneter Wert aus den Regel-Elementen
	 * @throws Exception
	 */
	private long getRuleElementLongValue(CRGRuleElement[] childElements, int rsize,
		CRGInputOutputBasic inout) throws Exception
	{
		int state = CRGRuleGrouperStatics.OP_NO_OPERATION; //Status 1 = UND, Status 2 = ODER, Status 0 = kein
		CRGRuleElement ruleEle;
		long lastValue = 0, actualVal = 0;
		int i = 0;
		while(i < rsize) {
			ruleEle = childElements[i];
			switch(ruleEle.m_type) {
				case CRGRuleGrouperStatics.TYPE_ELEMENT: {
					if(state > CRGRuleGrouperStatics.OP_OR) {
						actualVal = getRuleElementLongValue(ruleEle.m_childElements, ruleEle.m_childCount, inout);
					} else {
						actualVal = 0;
					}
					lastValue = getRuleComputeValueLong(lastValue, actualVal, state);
					break;
				}
				case CRGRuleGrouperStatics.TYPE_VALUE: {
					if(ruleEle.m_operantCompute) {
						actualVal = getRuleLongArray(ruleEle, inout)[0]; //getRuleLongValue(ruleEle, inout);
					} else {
						actualVal = 0;
					}
					lastValue = getRuleComputeValueLong(lastValue, actualVal, state);
					break;
				}
				case CRGRuleGrouperStatics.TYPE_OPERATOR: {
					state = ruleEle.m_operantType;
					break;
				}
			}
			i++;
		}
		return lastValue;
	}

	private long[] getRuleElementLongArray(CRGRuleElement[] childElements, int rsize,
		CRGInputOutputBasic inout) throws Exception
	{
		int state = CRGRuleGrouperStatics.OP_NO_OPERATION; //Status 1 = UND, Status 2 = ODER, Status 0 = kein
		CRGRuleElement ruleEle;
		long[] lastValue = null;
		long[] actualVal = null;
		int i = 0;
		while(i < rsize) {
			ruleEle = childElements[i];
			switch(ruleEle.m_type) {
				case CRGRuleGrouperStatics.TYPE_ELEMENT: {
					if(state > CRGRuleGrouperStatics.OP_OR) {
						actualVal = getRuleElementLongArray(ruleEle.m_childElements, ruleEle.m_childCount, inout);
					}
					if(actualVal != null) {
						if(lastValue == null) {
							lastValue = actualVal;
						} else {
							int laenge = lastValue.length > actualVal.length ? lastValue.length : actualVal.length;
							for(int j = 0; j < laenge; j++) {
								lastValue[j] = getRuleComputeValueLong(lastValue[j], actualVal[j], state);
							}
						}
					}
					break;
				}
				case CRGRuleGrouperStatics.TYPE_VALUE: {
					if(ruleEle.m_operantCompute) {
						actualVal = getRuleLongArray(ruleEle, inout); //getRuleLongValue(ruleEle, inout);
					}
			    /* 3.9.5 2015-09-01 DNi: #FINDBUGS - Potenzielle NP-Exception, weil actualVal != null nicht abgefragt wird.  
			     * Korrektur: Ist hier vielleicht actualValue == null || ... gemeint? */
					if(actualVal != null || actualVal.length == 0) {
						if(lastValue == null || lastValue.length == 0) {
							int len = actualVal.length;
							lastValue = new long[len];
							for(int j = 0; j < len; j++) {
								lastValue[j] = actualVal[j];
							}
						} else {
							int laenge = lastValue.length > actualVal.length ? lastValue.length : actualVal.length;
							for(int j = 0; j < laenge; j++) {
								long act = 0;
								if(j < actualVal.length) {
									act = actualVal[j];
								} else {
									if(ruleEle.m_criterionIndex == CRGRuleGrouperStatics.CRITINT_INDEX_NO_CRIT) {
										act = actualVal[0];
									}
								}
								lastValue[j] = getRuleComputeValueLong(lastValue[j], act, state);
							}
						}
					}
					break;
				}
				case CRGRuleGrouperStatics.TYPE_OPERATOR: {
					state = ruleEle.m_operantType;
					break;
				}
			}
			i++;
		}
		return lastValue;
	}

	/**2. Stufe
	 * Ermittelt aus einem Regel-Element den Long-Wert
	 * ACHTUNG: nur anwendbar, wenn auch Rechen-Operatoren vorhanden sind
	 * @param ruleElement CRGRuleElement : Regel-Element
	 * @param inout CRGInputOutputBasic : Objekt mit Falldaten
	 * @return long : Wert des Regel-Elements
	 * @throws Exception
	 */
	private long[] getRuleLongArray(CRGRuleElement ruleElement, CRGInputOutputBasic inout) throws Exception
	{
		switch(ruleElement.m_computeType) {
			case CRGRuleGrouperStatics.COMPUTE_TYPE_BOTH: {
				return getRuleLongArray(ruleElement, ruleElement.m_criterionType, ruleElement.m_criterionIndex, inout);
			}
			case CRGRuleGrouperStatics.COMPUTE_TYPE_CRITERIUM: {
				long[] ret = getRuleLongArray(ruleElement.m_criterionType, ruleElement.m_criterionIndex, inout);
				return checkLongCriterionWithInterval(ruleElement, ret, inout);

			}
			case CRGRuleGrouperStatics.COMPUTE_TYPE_VALUE: {
				int i = ruleElement.m_criterionIndex >= 0 ?
						inout.getArrayDateValue(ruleElement.m_criterionIndex).length : 0;
				return getRuleLongArray(ruleElement, ruleElement.m_valueType, i);
			}
			default:
				return null;
		}
	}

	private double[] checkDoubleCriterionWithInterval(CRGRuleElement ruleElement, double[] critValues,
		CRGInputOutputBasic inout) throws Exception
	{
		if(critValues == null)
			return critValues;
		int len = critValues.length;
		if(!ruleElement.hasInterval || len == 0) {
			return critValues;
		}
		long[] testValues = new long[len];
		for(int i = 0; i < len; i++){
			testValues[i] = (long)critValues[i];
		}
		testValues = checkLongCriterionWithInterval(ruleElement, testValues, inout);
		len = testValues.length;
		double[] ret = new double[len];
		for(int i = 0; i < len; i++){
			ret[i] = (double)testValues[i];
		}
		return ret;
	}

	private long[] checkLongCriterionWithInterval(CRGRuleElement ruleElement, long[] critValues,
			CRGInputOutputBasic inout) throws Exception
	{
		int len = critValues.length;
		if(!ruleElement.hasInterval || len == 0) {
			return critValues;
		}
                // Zeitliche Grenzen des ermittelten Intervalls
		long[] interval = getIntervalLimits(ruleElement, inout);
                // Indizes der Fälle die in den ermittelten Intervall reinpassen für Grenzen akt.Fall - Fall+-n
                int[] retSortInds = ruleElement.interval.getRetSortInds();
		long intStart = interval[0];
		long intEnd = interval[1];
		long[] ret = new long[len];
		int newLen = 0;
//                int[] sortInds = inout.getM_sortedClinicAdmDatesInds();
                int[] sortInds = inout.getSortedStartDatesInds();
                if(ruleElement.m_criterionType == CRGRuleGrouperStatics.DATATYPE_ARRAY_DATE) {
                    if(m_setCheckDateFromAdmDate && ruleElement.interval.isCaseInterval() && retSortInds != null && sortInds != null){
 			for(int i = retSortInds[0]; i <= retSortInds[1]; i++) {
                            if(i < sortInds.length){
				ret[newLen] = critValues[sortInds[i]];
				newLen++;
                            }
				
			}
                       
                    }else{
			interval = null;
			for(int i = 0; i < len; i++) {
				long val = critValues[i];
				if(intStart <= val && val <= intEnd) {
					ret[newLen] = val;
					newLen++;
				}
			}
                    }
		} else {
			for(int i = 0; i < len; i++) {
				long val = critValues[i];
				if(checkIntervalWithValues(ruleElement, inout, i, intStart, intEnd)) {
					ret[newLen] = val;
					newLen++;
				}
			}
		}
		if(newLen < len) {
			long[] ret1 = new long[newLen];
			System.arraycopy(ret, 0, ret1, 0, newLen);
			ret = ret1;
		}
		return ret;
	}

	/** 3.Stufe und 2.
	 * Ermittelt den Long-Wert aus dem Kriterium
	 * @param critType int : Datentyp des Kriteriums
	 * @param critIndex int : Index des Kriteriums im Fall-Objekt
	 * @param inout CRG2InputOutput : Fallobjekt
	 * @return long : Wert des Kriteriums
	 * @throws Exception
	 */
	private long[] getRuleLongArray(int critType, int critIndex, CRGInputOutputBasic inout) throws Exception
	{
		switch(critType) {
			case CRGRuleGrouperStatics.DATATYPE_DATE: {
				long[] longValue = new long[1];
				longValue[0] = inout.getLongValue(critIndex);
				return longValue;
			}
			case CRGRuleGrouperStatics.DATATYPE_DAY_TIME: {
				long[] longValue = new long[1];
				longValue[0] = inout.getLongTimeValue(critIndex);
				return longValue;
			}
			case CRGRuleGrouperStatics.DATATYPE_DOUBLE: {
				long[] longValue = new long[1];
				longValue[0] = (long)inout.getDoubleValue(critIndex);
				return longValue;
			}
			case CRGRuleGrouperStatics.DATATYPE_INTEGER: {
				long[] longValue = new long[1];
				longValue[0] = (long)inout.getIntegerValue(critIndex);
				return longValue;
			}
			case CRGRuleGrouperStatics.DATATYPE_ARRAY_DATE: {
				return inout.getArrayLongValue(critIndex);
			}
			case CRGRuleGrouperStatics.DATATYPE_ARRAY_DAY_TIME: {
				return inout.getArrayLongtimeValue(critIndex);
			}
			default:
				return null;
		}
	}

	/** 3.Stufe und 3.
	 * Ermittelt aus einem Regel-Element den Double-Wert des Kriteriums
	 * ACHTUNG: nur anwendbar, wenn auch Rechen-Operatoren vorhanden sind
	 * @param ruleElement CRGRuleElement : Regel-Element
	 * @param valType int : Datentyp des Kriteriums
	 * @return double : Wert des Kriteriums
	 * @throws Exception
	 */
	private long[] getRuleLongArray(CRGRuleElement ruleElement, int valType, int index) throws Exception
	{
		switch(valType) {
			case CRGRuleGrouperStatics.DATATYPE_DATE: {
				long[] longValue = new long[1];
				longValue[0] = ruleElement.m_longValue;
				return longValue;
			}
			case CRGRuleGrouperStatics.DATATYPE_DAY_TIME: {
				long[] longValue = new long[1];
				longValue[0] = ruleElement.m_longtimeValue;
				return longValue;
			}
			case CRGRuleGrouperStatics.DATATYPE_ARRAY_DATE: {
				long[] longValue = new long[index];
				for(int i = 0; i < index; i++) {
					longValue[i] = ruleElement.m_longValue;
				}
				return longValue;
			}
			case CRGRuleGrouperStatics.DATATYPE_ARRAY_DAY_TIME: {
				long[] longValue = new long[index];
				for(int i = 0; i < index; i++) {
					longValue[i] = ruleElement.m_longtimeValue;
				}
				return longValue;
			}
			default:
				return null;
		}
	}

	/** 3.Stufe und 1.
	 * Ermittelt aus einem Regel-Element (Kriterium+Wert) den Long-Wert
	 * @param ruleElement CRGRuleElement : Regel-Element
	 * @param critType int : Datentyp des Kriteriums
	 * @param critIndex int : Index des Kriteriums im Fall-Objekt
	 * @param inout CRG2InputOutput : Objekt mit Falldaten
	 * @return long : errechneter Wert aus Kriterium und Fall-Wert
	 * @throws Exception
	 */
	private long[] getRuleLongArray(CRGRuleElement ruleElement, int critType, int critIndex,
		CRGInputOutputBasic inout) throws Exception
	{
		switch(critType) {
			case CRGRuleGrouperStatics.DATATYPE_DATE: {
				long[] longValue = new long[1];
				longValue[0] = inout.getLongValue(critIndex);
				return getRuleComputeArrayLong(ruleElement, longValue, critType, inout);
			}
			case CRGRuleGrouperStatics.DATATYPE_DAY_TIME: {
				long[] longValue = new long[1];
				longValue[0] = inout.getLongTimeValue(critIndex);
				return getRuleComputeArrayLong(ruleElement, longValue, critType, inout);
			}
			case CRGRuleGrouperStatics.DATATYPE_ARRAY_DATE: {
				return getRuleComputeArrayLong(ruleElement, inout.getArrayLongValue(critIndex), critType, inout);
			}
			case CRGRuleGrouperStatics.DATATYPE_ARRAY_DAY_TIME: {
				return getRuleComputeArrayLong(ruleElement, inout.getArrayLongtimeValue(critIndex), critType, inout);
			}
			default:
				return null;
		}
	}

	/** 4.Stufe
	 * Berechnet aus einem Regel-Element den dort enthaltenen Long-Wert
	 * @param ruleElement CRGRuleElement : Regel-Element
	 * @param value long : long-Wert aus dem Fall
	 * @param valType int : Datentyp des Fall-Wertes
	 * @return long
	 * @throws Exception
	 */
	private long[] getRuleComputeArrayLong(CRGRuleElement ruleElement, long[] value, int valType,
		CRGInputOutputBasic inout) throws Exception
	{
		switch(valType) {
			case CRGRuleGrouperStatics.DATATYPE_DATE: {
				long[] longValue = new long[1];
//				if(!m_isGK || (m_isGK && checkRuleElementInterval(ruleElement, inout, 0))) {
				if(checkRuleElementInterval(ruleElement, inout, 0)) {
					longValue[0] = getRuleComputeValueLong(value[0], ruleElement.m_longValue, ruleElement.m_operantType);
				}
				return longValue;
			}
			case CRGRuleGrouperStatics.DATATYPE_DAY_TIME: {
				long[] longValue = new long[1];
//				if(!m_isGK || (m_isGK && checkRuleElementInterval(ruleElement, inout, 0))) {
				if(checkRuleElementInterval(ruleElement, inout, 0)) {
					longValue[0] = getRuleComputeValueLong(value[0], ruleElement.m_longtimeValue,
								   ruleElement.m_operantType);
				}
				return longValue;
			}
			case CRGRuleGrouperStatics.DATATYPE_ARRAY_DATE: {
				long[] longValue = new long[value.length];
				for(int i = 0; i < value.length; i++) {
					//if (value[i] == -1)						break;
					if(longValue[i] == 0) {
						break;
					}
//					if(!m_isGK || (m_isGK && checkRuleElementInterval(ruleElement, inout, i))) {
					if(checkRuleElementInterval(ruleElement, inout, i)) {

						longValue[i] = getRuleComputeValueLong(value[i], ruleElement.m_longValue,
									   ruleElement.m_operantType);
					}
				}
				return longValue;
			}
			case CRGRuleGrouperStatics.DATATYPE_ARRAY_DAY_TIME: {
				long[] longValue = new long[value.length];
				for(int i = 0; i < value.length; i++) {
					if(value[i] == -1) { //			break;
						//if(longValue[i]==0)
						break;
					}
//					if(!m_isGK || (m_isGK && checkRuleElementInterval(ruleElement, inout, i))) {
					if(checkRuleElementInterval(ruleElement, inout, i)) {
						longValue[i] = getRuleComputeValueLong(value[i], ruleElement.m_longtimeValue,
									   ruleElement.m_operantType);
					}
				}
				return longValue;
			}
			default:
				return null;
		}
	}

	/** 5.Stufe
	 * Ermittelt den Wert aus Fall und Kriterium
	 * @param value long : Wert aus dem Fall
	 * @param critValue long : Kriterium-Wert
	 * @param opType int : Typ des Operators
	 * @return long : Wert aus Kriterium, Operator und Fall-Wert
	 * @throws Exception
	 */
	protected static long getRuleComputeValueLong(long value, long critValue, int opType) throws Exception
	{
		switch(opType) {
			case CRGRuleGrouperStatics.OP_PLUS: {
				return value + critValue;
			}
			case CRGRuleGrouperStatics.OP_MINUS: {
				return value - critValue;
			}
			case CRGRuleGrouperStatics.OP_MULTIPL: {
				return value * critValue;
			}
			case CRGRuleGrouperStatics.OP_DIVIDE: {
				return value / critValue;
			}
			case CRGRuleGrouperStatics.OP_NO_OPERATION: {
				return critValue;
			}
			default:
				return 0;
		}
	}

	/** 2.Stufe
	 * Ermittelt aus einem Regel-Element den Double-Wert
	 * ACHTUNG: nur anwendbar, wenn auch Rechen-Operatoren vorhanden sind
	 * @param ruleElement CRGRuleElement : Regel-Element
	 * @param inout CRG2InputOutput : Objekt mit Falldaten
	 * @return double : Wert des Regel-Elements
	 * @throws Exception
	 */
	private double[] getRuleDoubleArray(CRGRuleElement ruleElement, CRGInputOutputBasic inout) throws Exception
	{
		switch(ruleElement.m_computeType) {
			case CRGRuleGrouperStatics.COMPUTE_TYPE_BOTH: {
				return getRuleDoubleArray(ruleElement, ruleElement.m_criterionType, ruleElement.m_criterionIndex, inout);
			}
			case CRGRuleGrouperStatics.COMPUTE_TYPE_CRITERIUM: {
				return getRuleDoubleArray(ruleElement.m_criterionType, ruleElement.m_criterionIndex, inout);
			}
			case CRGRuleGrouperStatics.COMPUTE_TYPE_VALUE: {
				int i = ruleElement.m_criterionIndex >= 0 &&
						ruleElement.m_criterionIndex != CRGRuleGrouperStatics.CRITINT_INDEX_NO_CRIT ?
						inout.getArrayDoubleValue(ruleElement.m_criterionIndex).length : 0;
				return getRuleDoubleArray(ruleElement, ruleElement.m_valueType, i); //criterionType???????volker
			}
			default:
				return null;
		}
	}

	/** 2.Stufe
	 * Ermittelt aus einem Regel-Element den Double-Wert
	 * ACHTUNG: nur anwendbar, wenn auch Rechen-Operatoren vorhanden sind
	 * @param ruleElement CRGRuleElement : Regel-Element
	 * @param inout CRG2InputOutput : Objekt mit Falldaten
	 * @return double : Wert des Regel-Elements
	 * @throws Exception
	 */
	private double[] getRuleIntegerArray(CRGRuleElement ruleElement, CRGInputOutputBasic inout) throws Exception
	{
		switch(ruleElement.m_computeType) {
			case CRGRuleGrouperStatics.COMPUTE_TYPE_BOTH: {
				return getRuleIntegerArray(ruleElement, ruleElement.m_criterionType, ruleElement.m_criterionIndex,
					inout);
			}
			case CRGRuleGrouperStatics.COMPUTE_TYPE_CRITERIUM: {
				double[] ret =  getRuleIntegerArray(ruleElement.m_criterionType, ruleElement.m_criterionIndex, inout);
				return checkDoubleCriterionWithInterval(ruleElement, ret, inout);

			}
			case CRGRuleGrouperStatics.COMPUTE_TYPE_VALUE: {
				int i = ruleElement.m_criterionIndex >= 0 ?
						inout.getArrayIntegerValue(ruleElement.m_criterionIndex).length : 0;
				return getRuleIntegerArray(ruleElement, ruleElement.m_valueType, i);
			}
			default:
				return null;
		}
	}

	/** 3.Stufe und 2.
	 * Ermittelt aus einem Regel-Element den Double-Wert des Falles
	 * ACHTUNG: nur anwendbar, wenn auch Rechen-Operatoren vorhanden sind
	 * @param critType int : Datentyp ds Kriteriums
	 * @param critIndex int : Index des Kriteriums, indem der Wert enthalten ist
	 * @param inout CRG2InputOutput : Objekt mit Falldaten
	 * @return double : Wert des Kriteriums aus dem Fall
	 * @throws Exception
	 */
	private double[] getRuleDoubleArray(int critType, int critIndex, CRGInputOutputBasic inout) throws Exception
	{
		switch(critType) {
			case CRGRuleGrouperStatics.DATATYPE_DOUBLE: {
				double[] doubleValue = new double[1];
				doubleValue[0] = inout.getDoubleValue(critIndex);
				return doubleValue;
			}
			case CRGRuleGrouperStatics.DATATYPE_ARRAY_DOUBLE: {
				double[] arr = inout.getArrayDoubleValue(critIndex);
				if(arr != null) {
					double[] retArr = new double[arr.length];
					System.arraycopy(arr, 0, retArr, 0, arr.length);
					return retArr;
				}
				return null;
			}
			default:
				return null;
		}
	}

	/** 3.Stufe und 2.
	 * Ermittelt aus einem Regel-Element den Double-Wert des Falles
	 * ACHTUNG: nur anwendbar, wenn auch Rechen-Operatoren vorhanden sind
	 * @param critType int : Datentyp ds Kriteriums
	 * @param critIndex int : Index des Kriteriums, indem der Wert enthalten ist
	 * @param inout CRG2InputOutput : Objekt mit Falldaten
	 * @return double : Wert des Kriteriums aus dem Fall
	 * @throws Exception
	 */
	private double[] getRuleIntegerArray(int critType, int critIndex, CRGInputOutputBasic inout) throws Exception
	{
		switch(critType) {
			case CRGRuleGrouperStatics.DATATYPE_INTEGER: {
				double[] intValue = new double[1];
				intValue[0] = inout.getIntegerValue(critIndex);
				return intValue;
			}
			case CRGRuleGrouperStatics.DATATYPE_ARRAY_INTEGER: {
				int[] intValue = inout.getArrayIntegerValue(critIndex);
				double[] doubleValue = new double[intValue.length];
				for(int j = 0; j < intValue.length; j++) {
					doubleValue[j] = intValue[j];
				}
				return doubleValue;
			}
			default:
				return null;
		}
	}

	/** 3.Stufe und 3.
	 * Ermittelt aus einem Regel-Element den Double-Wert des Kriteriums
	 * ACHTUNG: nur anwendbar, wenn auch Rechen-Operatoren vorhanden sind
	 * @param ruleElement CRGRuleElement : Regel-Element
	 * @param valType int : Datentyp des Kriteriums
	 * @return double : Wert des Kriteriums
	 * @throws Exception
	 */
	private double[] getRuleDoubleArray(CRGRuleElement ruleElement, int valType, int index) throws Exception
	{
		switch(valType) {
			case CRGRuleGrouperStatics.DATATYPE_DOUBLE: {
				double[] doubleValue = new double[1];
				doubleValue[0] = ruleElement.m_doubleValue;
				return doubleValue;
			}
			case CRGRuleGrouperStatics.DATATYPE_ARRAY_DOUBLE: {
				double[] doubleValue = new double[index];
				for(int i = 0; i < index; i++) {
					doubleValue[i] = ruleElement.m_doubleValue;
				}
				return doubleValue;
			}
			default:
				return null;
		}
	}

	/** 3.Stufe und 3.
	 * Ermittelt aus einem Regel-Element den Double-Wert des Kriteriums
	 * ACHTUNG: nur anwendbar, wenn auch Rechen-Operatoren vorhanden sind
	 * @param ruleElement CRGRuleElement : Regel-Element
	 * @param valType int : Datentyp des Kriteriums
	 * @return double : Wert des Kriteriums
	 * @throws Exception
	 */
	private double[] getRuleIntegerArray(CRGRuleElement ruleElement, int valType, int index) throws Exception
	{
		switch(valType) {
			case CRGRuleGrouperStatics.DATATYPE_INTEGER: {
				int[] intValue = new int[1];
				intValue[0] = ruleElement.m_intValue;
				double[] doubleValue = new double[1];
				doubleValue[0] = intValue[0];
				return doubleValue;
			}
			case CRGRuleGrouperStatics.DATATYPE_ARRAY_INTEGER: {
				int[] intValue = new int[index];
				for(int i = 0; i < index; i++) {
					intValue[i] = ruleElement.m_intValue;
				}
				double[] doubleValue = new double[intValue.length];
				for(int j = 0; j < intValue.length; j++) {
					doubleValue[j] = intValue[j];
				}
				return doubleValue;
			}
			default:
				return null;
		}
	}

	/** 3.Stufe und 1.
	 * Ermittelt aus einem Regel-Element (Kriterium+Wert) den Double-Wert
	 * @param ruleElement CRGRuleElement : Regel-Element
	 * @param critType int : Datentyp des Kriteriums
	 * @param critIndex int : Index des Kriteriums im Fall-Objekt
	 * @param inout CRG2InputOutput : Objekt mit Falldaten
	 * @return double : errechneter Wert aus Kriterium und Fall-Wert
	 * @throws Exception
	 */
	private double[] getRuleDoubleArray(CRGRuleElement ruleElement, int critType, int critIndex,
		CRGInputOutputBasic inout) throws Exception
	{
		int type = ruleElement.m_valueType;
		if(critType != type) {
			type = CRGRuleGrouperStatics.DATATYPE_ARRAY_DOUBLE;
		}
		switch(critType) {
			case CRGRuleGrouperStatics.DATATYPE_DOUBLE: {
				double[] doubleValue = new double[1];
				double val = 0;
				if(!ruleElement.m_isCummulative) {
					val = inout.getDoubleValue(critIndex);
				} else {
					double[] vals = this.fillCummulatedValuesForCrit(ruleElement, inout, critIndex);
					if(vals.length > 0) {
						val = vals[0];
					}
				}
				doubleValue[0] = val;
				return getRuleComputeArrayDouble(ruleElement, doubleValue, type, inout);
			}
			case CRGRuleGrouperStatics.DATATYPE_ARRAY_DOUBLE: {
				double[] values = this.fillCummulatedValuesForCrit(ruleElement, inout, critIndex);
				return getRuleComputeArrayDouble(ruleElement, values, type, inout);
			}
			default:
				return null;
		}
	}

	/** 3.Stufe und 1.
	 * Ermittelt aus einem Regel-Element (Kriterium+Wert) den Double-Wert
	 * @param ruleElement CRGRuleElement : Regel-Element
	 * @param critType int : Datentyp des Kriteriums
	 * @param critIndex int : Index des Kriteriums im Fall-Objekt
	 * @param inout CRG2InputOutput : Objekt mit Falldaten
	 * @return double : errechneter Wert aus Kriterium und Fall-Wert
	 * @throws Exception
	 */
	private double[] getRuleIntegerArray(CRGRuleElement ruleElement, int critType, int critIndex,
		CRGInputOutputBasic inout) throws Exception
	{
		int type = ruleElement.m_valueType;
		if(critType != type) {
			type = CRGRuleGrouperStatics.DATATYPE_ARRAY_INTEGER;
		}
		switch(critType) {
			case CRGRuleGrouperStatics.DATATYPE_INTEGER: {
				double[] intValue = new double[1];
				intValue[0] = inout.getIntegerValue(critIndex);
				return getRuleComputeArrayInteger(ruleElement, inout, intValue, type /*ruleElement.m_valueType*/);
			}
			case CRGRuleGrouperStatics.DATATYPE_ARRAY_INTEGER: {
				double[] values = this.fillCummulatedValuesForCrit(ruleElement, inout, critIndex);
				return getRuleComputeArrayInteger(ruleElement, inout, values,
					type
					/*ruleElement.m_valueType*/);
			}
			default:
				return null;
		}
	}

	/** 4.Stufe
	 * Berechnet aus einem Regel-Element den dort enthaltenen Double-Wert
	 * @param ruleElement CRGRuleElement : Regel-Element
	 * @param value double : Double-Wert aus dem Fall
	 * @param valType int : Datentyp des Fall-Wertes
	 * @return double
	 * @throws Exception
	 */
	private double[] getRuleComputeArrayDouble(CRGRuleElement ruleElement, double[] value,
		int valType, CRGInputOutputBasic inout) throws Exception
	{
		switch(valType) {
			case CRGRuleGrouperStatics.DATATYPE_DOUBLE: {
				double[] doubleValue = new double[1];
//				if(!m_isGK || (m_isGK && checkRuleElementInterval(ruleElement, inout, 0))) {
				if(checkRuleElementInterval(ruleElement, inout, 0)) {
					doubleValue[0] = getRuleComputeValueDouble(value[0], ruleElement.m_doubleValue,
									 ruleElement.m_operantType);
				}
				return doubleValue;
			}
			case CRGRuleGrouperStatics.DATATYPE_ARRAY_DOUBLE: {
				double[] doubleValue = new double[value.length];
				//TODO: hier cummutative Werte ermitteln
				for(int i = 0; i < value.length; i++) {
					if(value[i] == -1) {
						break;
					}
//					if(!m_isGK || (m_isGK && checkRuleElementInterval(ruleElement, inout, i))) {
					if(checkRuleElementInterval(ruleElement, inout, i)) {
						doubleValue[i] = getRuleComputeValueDouble(value[i], ruleElement.m_doubleValue,
										 ruleElement.m_operantType);
					}
				}
				return doubleValue;
			}
			default:
				return null;
		}
	}

	/** 4.Stufe
	 * Berechnet aus einem Regel-Element den dort enthaltenen Integer-Wert
	 * @param ruleElement CRGRuleElement : Regel-Element
	 * @param value int : Double-Wert aus dem Fall
	 * @param valType int : Datentyp des Fall-Wertes
	 * @return int[]
	 * @throws Exception
	 */
	private double[] getRuleComputeArrayInteger(CRGRuleElement ruleElement, CRGInputOutputBasic inout, int[] value,
		int valType) throws Exception
	{
		double[] intValue = new double[value.length];
		for(int i = 0; i < value.length; i++) {
			intValue[i] = value[i];
		}
		return getRuleComputeArrayInteger(ruleElement, inout, intValue, valType);
	}

	private double[] getRuleComputeArrayInteger(CRGRuleElement ruleElement, CRGInputOutputBasic inout, double[] value,
		int valType) throws Exception
	{
		switch(valType) {
			case CRGRuleGrouperStatics.DATATYPE_INTEGER: {
				double[] intValue = new double[1];
//				if(!m_isGK || (m_isGK && checkRuleElementInterval(ruleElement, inout, 0))) {
				if(checkRuleElementInterval(ruleElement, inout, 0)) {
					intValue[0] = getRuleComputeValueDouble(value[0], ruleElement.m_intValue, ruleElement.m_operantType);
				}
				return intValue;
			}
			case CRGRuleGrouperStatics.DATATYPE_ARRAY_INTEGER: {
				double[] intValue = new double[value.length];
				for(int i = 0; i < value.length; i++) {
					if(value[i] == -1) {
						break;
					}
//					if(!m_isGK || (m_isGK && checkRuleElementInterval(ruleElement, inout, i))) {
					if(checkRuleElementInterval(ruleElement, inout, i)) {
						intValue[i] = getRuleComputeValueDouble(value[i], ruleElement.m_intValue,
									  ruleElement.m_operantType);
					}
				}
				return intValue;
			}
			default:
				return null;
		}
	}

	/** 5.Stufe
	 * Ermittelt den Wert aus Fall und Kriterium
	 * @param value double : Wert aus dem Fall
	 * @param critValue double : Kriterium-Wert
	 * @param opType int : Typ des Operators
	 * @return double : Wert aus Kriterium, Operator und Fall-Wert
	 * @throws Exception
	 */
	protected static synchronized double getRuleComputeValueDouble(double value, double critValue,
		int opType) throws Exception
	{
		return getRuleComputeValueDouble(value, critValue, opType, 10000);
	}

	protected static synchronized double getRuleComputeValueDouble(double value, double critValue, int opType,
		int decimalPlace) throws Exception
	{
		switch(opType) {
			case CRGRuleGrouperStatics.OP_PLUS: {
				return round(value + critValue, decimalPlace);
			}
			case CRGRuleGrouperStatics.OP_MINUS: {
				return round(value - critValue, decimalPlace);
			}
			case CRGRuleGrouperStatics.OP_MULTIPL: {
				return round(value * critValue, decimalPlace);
			}
			case CRGRuleGrouperStatics.OP_DIVIDE: {
				return round(value / critValue, decimalPlace);
			}
			case CRGRuleGrouperStatics.OP_NO_OPERATION: {
				return critValue;
			}
			default:
				return 0d;
		}
	}

	public static synchronized double round(double value, int decimalPlace)
	{
		return Math.round(value * decimalPlace) / (double)decimalPlace;
	}

	/** 5.Stufe
	 * Ermittelt den Wert aus Fall und Kriterium
	 * @param value double : Wert aus dem Fall
	 * @param critValue double : Kriterium-Wert
	 * @param opType int : Typ des Operators
	 * @return double : Wert aus Kriterium, Operator und Fall-Wert
	 * @throws Exception
	 */
	/*	private double getRuleComputeValueInteger(double value, double critValue, int opType)throws Exception{
	  switch(opType) {
	   case CRGRuleGrouperStatics.OP_PLUS: {
	 return value + critValue;
	   }
	   case CRGRuleGrouperStatics.OP_MINUS: {
	 return value - critValue;
	   }
	   case CRGRuleGrouperStatics.OP_MULTIPL: {
	 return value * critValue;
	   }
	   case CRGRuleGrouperStatics.OP_DIVIDE: {
	 return value / critValue;
	   }
	   case CRGRuleGrouperStatics.OP_NO_OPERATION: {
	 return critValue;
	   }
	   default:
	 return 0;
	  }
	 }
	 */

	/** 2.Stufe
	 * Ermittelt zu einem Regel-Element den zusammengesetzten String
	 * @param ruleElement CRGRuleElement : Regel-ELement
	 * @param inout CRG2InputOutput : Objekt des Falles
	 * @return String : String-Wert des Regel-Elementes
	 * @throws Exception
	 */
	private String[] getRuleStringArray(CRGRuleElement ruleElement, CRGInputOutputBasic inout) throws Exception
	{
		switch(ruleElement.m_computeType) {
			case CRGRuleGrouperStatics.COMPUTE_TYPE_BOTH: {
				return getRuleStringArray(ruleElement, ruleElement.m_criterionType, ruleElement.m_criterionIndex, inout);
			}
			case CRGRuleGrouperStatics.COMPUTE_TYPE_CRITERIUM: {
//				return getRuleStringArray(ruleElement.m_criterionType, ruleElement.m_criterionIndex, inout);
				String[] value = getRuleStringArray(ruleElement.m_criterionType, ruleElement.m_criterionIndex, inout);
				String[] strValue = new String[value.length];
				int j = 0;
				for(int i = 0; i < value.length; i++) {
          /* 3.9.5 2015-09-01 DNi: #FINDBUGS - String-Vergleich mit == statt mit equals.
           * Beim Ändern ggf. sicherstellen, dass value[i] nicht null ist! */
					if(value[i] == null || value[i].isEmpty()) {
						break;
					}
//					if(!m_isGK || (m_isGK && checkRuleElementInterval(ruleElement, inout, i))) {
					if(checkRuleElementInterval(ruleElement, inout, i)) {
						strValue[j] = value[i];
                                        }else{
                                            strValue[j] = " ";
                                        }
					j++;
					
				}
				if(strValue.length > j){
					String[] newret = new String[j];
					if(j > 0){
						System.arraycopy(strValue, 0, newret, 0, j);
					}
					return newret;
				}
				return strValue;
			}
			case CRGRuleGrouperStatics.COMPUTE_TYPE_VALUE: {
				int i = ruleElement.m_criterionIndex >= 0 ?
						inout.getArrayStringValue(ruleElement.m_criterionIndex).length : 0;
				return getRuleStringArray(ruleElement, ruleElement.m_valueType, i);
			}
			default:
				return null;
		}
	}

	/** 3.Stufe und 2.
	 * Ermittelt den String-Wert aus dem Kriterium
	 * @param critType int : Datentyp des Kriteriums
	 * @param critIndex int : Index des Kriteriums im Fall-Objekt
	 * @param inout CRG2InputOutput : Fallobjekt
	 * @return String : Wert des Kriteriums
	 * @throws Exception
	 */
	private String[] getRuleStringArray(int critType, int critIndex, CRGInputOutputBasic inout) throws Exception
	{
		switch(critType) {
			case CRGRuleGrouperStatics.DATATYPE_STRING: {
				String[] strValue = new String[1];
				strValue[0] = inout.getStringValue(critIndex);
				return strValue;
			}
			case CRGRuleGrouperStatics.DATATYPE_ARRAY_STRING: {
				return inout.getArrayStringValue(critIndex);
			}
			default:
				return null;
		}
	}

	/** 3.Stufe und 3.
	 * Ermittelt den String Wert aus der Regel
	 * @param ruleElement CRGRuleElement : Regel-Element
	 * @param valType int : Datentyp des Kriteriums
	 * @return String : Wert des Kriteriums
	 * @throws Exception
	 */
	private String[] getRuleStringArray(CRGRuleElement ruleElement, int valType, int index) throws Exception
	{
		switch(valType) {
			case CRGRuleGrouperStatics.DATATYPE_STRING: {
				String[] strValue = new String[1];
				strValue[0] = ruleElement.m_strValue;
				return strValue;
			}
			case CRGRuleGrouperStatics.DATATYPE_ARRAY_STRING: {
				String[] strValue = new String[index];
				for(int i = 0; i < index; i++) {
					strValue[i] = ruleElement.m_strValue;
				}
				return strValue;
			}
			default:
				return null;
		}
	}

	/** 3.Stufe und 1.
	 * Ermittelt aus einem Regel-Element den zusammengesetzten String aus Kriterium und Fall-Wert
	 * @param ruleElement CRGRuleElement : Regel-Element
	 * @param critType int : Datentyp des Kriteriums
	 * @param critIndex int : Index des Kriteriums im Fall-Objekt
	 * @param inout CRG2InputOutput : Fallobjekt
	 * @return String : String aus Kriterium + Fall-Wert
	 * @throws Exception
	 */
	private String[] getRuleStringArray(CRGRuleElement ruleElement, int critType, int critIndex,
		CRGInputOutputBasic inout) throws Exception
	{
		switch(critType) {
			case CRGRuleGrouperStatics.DATATYPE_STRING: {
				String[] strValue = new String[1];
				strValue[0] = inout.getStringValue(critIndex);
				return getRuleComputeArrayString(ruleElement, inout, strValue, ruleElement.m_valueType);
			}
			case CRGRuleGrouperStatics.DATATYPE_ARRAY_STRING: {
				return getRuleComputeArrayString(ruleElement, inout, inout.getArrayStringValue(critIndex),
					ruleElement.m_valueType);
			}
			default:
				return null;
		}
	}

	/** 4.Stufe
	 * Ermittelt den zusammengesetzten String aus einem Regel-Element
	 * @param ruleElement CRGRuleElement
	 * @param value String
	 * @param valType int
	 * @return String
	 * @throws Exception
	 */
	private String[] getRuleComputeArrayString(CRGRuleElement ruleElement, CRGInputOutputBasic inout, String[] value,
		int valType) throws Exception
	{
		switch(valType) {
			case CRGRuleGrouperStatics.DATATYPE_STRING: {
				String[] strValue = new String[1];
//				if(!m_isGK || (m_isGK && checkRuleElementInterval(ruleElement, inout, 0))) {
				if(checkRuleElementInterval(ruleElement, inout, 0)) {
					strValue[0] = getRuleComputeValueString(value[0], ruleElement.m_strValue, ruleElement.m_operantType);
				}
				return strValue;
			}
			case CRGRuleGrouperStatics.DATATYPE_ARRAY_STRING: {
				String[] strValue = new String[value.length];
				for(int i = 0; i < value.length; i++) {
          /* 3.9.5 2015-09-01 DNi: #FINDBUGS - String-Vergleich mit == statt mit equals.
           * Beim Ändern ggf. sicherstellen, dass value[i] nicht null ist! */
					if(value[i] == null || value[i].isEmpty()) {
						break;
					}
//					if(!m_isGK || (m_isGK && checkRuleElementInterval(ruleElement, inout, i))) {
					if(checkRuleElementInterval(ruleElement, inout, i)) {
						strValue[i] = getRuleComputeValueString(value[i], ruleElement.m_strValue,
									  ruleElement.m_operantType);
					}
				}
				return strValue;
			}
			default:
				return null;
		}
	}

	/** 5.Stufe
	 * Ermittelt den Wert aus Fall und Kriterium
	 * @param value string : Wert aus dem Fall
	 * @param critValue string : Kriterium-Wert
	 * @param opType int : Typ des Operators
	 * @return string : Wert aus Kriterium, Operator und Fall-Wert
	 * @throws Exception
	 */
	protected static String getRuleComputeValueString(String value, String critValue, int opType) throws Exception
	{
		switch(opType) {
			case CRGRuleGrouperStatics.OP_PLUS: {
				return critValue + value;
			}
			case CRGRuleGrouperStatics.OP_MINUS: {
				return critValue.replaceAll(value, "");
			}
			case CRGRuleGrouperStatics.OP_NO_OPERATION: {
				return critValue;
			}
			case CRGRuleGrouperStatics.OP_CONCATENATE:
				return value + critValue;
			default:
				return "";
		}
	}

	private boolean checkRuleElementInterval(CRGRuleElement ruleElement, CRGInputOutputBasic inout,
		int valIndex) throws Exception
	{
		if(ruleElement.hasInterval) {
			long[] interval = getIntervalLimits(ruleElement, inout);
			long intStart = interval[0];
			long intEnd = interval[1];
			interval = null;
			return checkIntervalWithValues(ruleElement, inout, valIndex, intStart, intEnd);
		}
		/*else if(this.m_hasRuleInterval){ wurde schon vorbereitet
		 return checkIntervalWithValues(ruleElement, inout, valIndex, this.m_ruleIntervalStart, this.m_ruleIntervalEnd);

		   } */ else {
			return true;
		}
	}

	private boolean checkIntervalWithValues(CRGRuleElement ruleElement, CRGInputOutputBasic inout,
		int valIndex, long intStart, long intEnd) throws Exception
	{
		if(ruleElement.m_criterionCheckIndex < 0) {
			return true;
		}
		long[] vals = inout.getCheckLongDateValue(ruleElement.m_criterionCheckIndex);
		if(ruleElement.m_isCummulative) {
			vals = inout.getArrayLongValue(ruleElement.m_indCummulateDate); // bei den cummutativen Kriterien checkdate kann nicht richtig gesetzt werden, wird zum Laufzeit aus dem gesetzten Einzelldatum ermittelt
		}
		if(vals != null && valIndex < vals.length) {
			if(intStart <= vals[valIndex] && vals[valIndex] <= intEnd) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	private boolean checkRuleElementInterval(CRGRuleElement ruleElement, CRGInputOutputBasic inout) throws Exception
	{
		if(ruleElement.hasInterval) {
			if(ruleElement.m_criterionDependIndex < 0) {
				long val = inout.getCheckLongDateValue(ruleElement.m_criterionCheckIndex)[0];
				if(ruleElement.intervalStart <= val && val <= ruleElement.intervalEnd) {
					return true;
				} else {
					return false;
				}
			} else {
				int[] index = inout.getArrayIntegerValue(ruleElement.m_criterionDependIndex);
				int ind = -1;
				for(int i = 0; i < index.length; i++) {
					if(ind != index[i]) {
						boolean ret = checkRuleElementInterval(ruleElement, inout, index[i]);
						if(!ret) {
							return false;
						}
						ind = index[i];
					}
				}
				return true;
			}
		} else {
			return true;
		}
	}

	private boolean checkRuleValue(CRGRuleElement ruleElement, CRGInputOutputBasic inout,
		CheckValue checkValue, int state) throws Exception
	{
		int[] differentCases = new int[1];
		differentCases[0] = -99;
		boolean retValue = false;
		boolean fullRetValue = false;
//		if(!m_isGK || (m_isGK && checkRuleElementInterval(ruleElement, inout))) { // interval überprüfen
		if(ruleElement.m_criterionDependIndex > 0) {
			differentCases = inout.getArrayIntegerValue(ruleElement.m_criterionDependIndex);

		}
		if(checkValue.lastGroupIndex != ruleElement.m_criterionDependGroupIndex) {
			checkValue.resetGroupIndexResult(checkValue.lastGroupIndex);
			checkValue.lastGroupIndex = -1;
		}
		if(differentCases == null || differentCases.length == 0) {
			// kann passieren, nur wenn ruleElement.m_criterionDependIndex > 0 und es gibt dazu keine Einträge
			if(ruleElement.m_computeType == CRGRuleGrouperStatics.COMPUTE_TYPE_BOTH) {
				// jede Vergleich muss false liefern
				if(ruleElement.m_notElement) {
					return true;
				} else {
					return false;
				}
			}

		}
		int lastCase = -1;
		for(int i = 0; i < differentCases.length; i++) {
			if(lastCase != differentCases[i]) {
				lastCase = differentCases[i];
				switch(ruleElement.m_computeType) {
					case CRGRuleGrouperStatics.COMPUTE_TYPE_BOTH: {
						boolean chk = checkRuleValue(ruleElement, ruleElement.m_criterionType,
									  ruleElement.m_criterionIndex,
									  inout, lastCase);
						/*						if(ruleElement.m_notElement) {
							   retValue = !chk;
							  } else {
							   retValue = chk;
							  }*/
						retValue = chk;
						break;
					}
					case CRGRuleGrouperStatics.COMPUTE_TYPE_CRITERIUM: {
						retValue = true;
						break;
					}
					case CRGRuleGrouperStatics.COMPUTE_TYPE_VALUE: {
						retValue = true;
						break;
					}
					default:
						retValue = true;
				}
				/*		} else {
				   return false;
				  }*/
				fullRetValue = fullRetValue || retValue;
				if(ruleElement.m_criterionDependIndex > 0 && lastCase >= 0) {
					checkValue.setGroupDependResultsValue(lastCase, retValue);
				}
			}
		}
		if(ruleElement.m_criterionDependGroupIndex >= 0 && ruleElement.interval == null) {
			if(checkValue.lastGroupIndex == -1 || checkValue.lastGroupIndex == ruleElement.m_criterionDependGroupIndex) {
				if(checkValue.getGroupResults(ruleElement.m_criterionDependGroupIndex) == null) {
					checkValue.setGroupResults(ruleElement.m_criterionDependGroupIndex);
					checkValue.lastGroupIndex = ruleElement.m_criterionDependGroupIndex;
				} else {
					fullRetValue = checkValue.checkGroupIndexValues(ruleElement.m_criterionDependGroupIndex,
								   state, fullRetValue);
				}
			}
		}
		if(ruleElement.m_notElement) {
			fullRetValue = !fullRetValue;
		}
		return fullRetValue;
	}

	private boolean checkRuleValue(CRGRuleElement ruleElement, int critType, int critIndex,
		CRGInputOutputBasic inout, int lastCase) throws Exception
	{
		switch(critType) {
			case CRGRuleGrouperStatics.DATATYPE_STRING: {
//				if(!m_isGK || (m_isGK && checkRuleElementInterval(ruleElement, inout, 0))) {
				if(checkRuleElementInterval(ruleElement, inout, 0)) {

					switch(ruleElement.m_operantType) {
						case CRGRuleGrouperStatics.OP_MANY_IN_TABLE:
						case CRGRuleGrouperStatics.OP_MANY_IN:
						case CRGRuleGrouperStatics.OP_NOT_DOUBLE_IN_TABLE:
						case CRGRuleGrouperStatics.OP_NOT_DOUBLE_IN: {
							return false;
						}
						default: {
							String s = inout.getStringValue(critIndex);
							if((s == null) /*||(s=="")*/) {
								return false;
							} else {
								if(checkRuleStringValue(ruleElement, s, ruleElement.m_valueType)) {
									m_RefZaehler++;
									setRefValues(m_RefZaehler, s, critIndex, -1);
									return true;
								} else {
									return false;
								}
							}
						}
					}
				}
			}
			case CRGRuleGrouperStatics.DATATYPE_INTEGER: {
				if(inout.getIntegerValue(critIndex) == CRGInputOutputBasic.DEFAULT_INT_VALUE) {
					return false;
				} else {
//					if(!m_isGK || (m_isGK && checkRuleElementInterval(ruleElement, inout, 0))) {
//					if(checkRuleElementInterval(ruleElement, inout, 0)) {
					int val = 0;
					if(!ruleElement.m_isCummulative && checkRuleElementInterval(ruleElement, inout, 0)) {
						val = inout.getIntegerValue(critIndex);
					} else {
						int[] ints = this.fillCummulatedCount(ruleElement, inout, critIndex);
						if(ints.length > 0) {
							val = ints[0];
						}
					}
					return checkRuleIntegerValue(ruleElement, val,
						ruleElement.m_valueType);
//					} else {
//						return false;
//					}
				}
			}
			case CRGRuleGrouperStatics.DATATYPE_DOUBLE: {
				if(inout.getDoubleValue(critIndex) == CRGInputOutputBasic.DEFAULT_INT_VALUE) {
					return false;
				} else {
//					if(!m_isGK || (m_isGK && checkRuleElementInterval(ruleElement, inout, 0))) {
//					if(checkRuleElementInterval(ruleElement, inout, 0)) {
					double val = 0;
					if(!ruleElement.m_isCummulative && checkRuleElementInterval(ruleElement, inout, 0)) {
						val = inout.getDoubleValue(critIndex);
					} else {
						double[] vals = this.fillCummulatedValuesForCrit(ruleElement, inout, critIndex);
						if(vals.length > 0) {
							val = vals[0];
						}
					}
					return checkRuleDoubleValue(ruleElement, val,
						ruleElement.m_valueType);
				}
//					return false;
//				}
			}
			case CRGRuleGrouperStatics.DATATYPE_DATE: {
				//return checkRuleDateValue(ruleElement, inout.getDateValue(critIndex), ruleElement.m_valueType);
				if(inout.getLongValue(critIndex) == CRGInputOutputBasic.DEFAULT_LONG_VALUE) {
					return false;
				} else {
//					if(!m_isGK || (m_isGK && checkRuleElementInterval(ruleElement, inout, 0))) {
					if(checkRuleElementInterval(ruleElement, inout, 0)) {
						return checkRuleLongValue(ruleElement, inout.getLongValue(critIndex), ruleElement.m_valueType);
					}
					return false;
				}

			}
			case CRGRuleGrouperStatics.DATATYPE_DAY_TIME: {
				//return checkRuleDateValue(ruleElement, inout.getDateValue(critIndex), ruleElement.m_valueType);
				if(inout.getLongTimeValue(critIndex) == CRGInputOutputBasic.DEFAULT_LONG_VALUE) {
					return false;
				} else {
//					if(!m_isGK || (m_isGK && checkRuleElementInterval(ruleElement, inout, 0))) {
					if(checkRuleElementInterval(ruleElement, inout, 0)) {
						return checkRuleLongValue(ruleElement, inout.getLongTimeValue(critIndex),
							ruleElement.m_valueType);
					}
					return false;
				}
			}
			case CRGRuleGrouperStatics.DATATYPE_ARRAY_STRING: {
				String[] s = inout.getArrayStringValue(critIndex);
				if(s == null) {
					return false;
				} else {
					if(checkRuleStringValue(ruleElement, inout, s, ruleElement.m_valueType, lastCase)) {
						/*						for(int i = 0; i <= m_RefZaehler; i++) {
							   m_RefIndex1[i] = critIndex;
							  }*/
						return true;
					} else {
						return false;
					}
				}
			}
			case CRGRuleGrouperStatics.DATATYPE_ARRAY_INTEGER: {
				return checkRuleIntegerValue(ruleElement, inout, critIndex,
					ruleElement.m_valueType, lastCase);
			}
			case CRGRuleGrouperStatics.DATATYPE_ARRAY_DOUBLE: {
				return checkRuleDoubleValue(ruleElement, inout, critIndex, ruleElement.m_valueType, lastCase);
			}
			case CRGRuleGrouperStatics.DATATYPE_ARRAY_DATE: {
				//return checkRuleDateValue(ruleElement, inout.getArrayDateValue(critIndex), ruleElement.m_valueType);
				return checkRuleLongValue(ruleElement, inout, inout.getArrayLongValue(critIndex),
					ruleElement.m_valueType, lastCase);
			}
			case CRGRuleGrouperStatics.DATATYPE_ARRAY_DAY_TIME: {
				//return checkRuleDateValue(ruleElement, inout.getArrayDateValue(critIndex), ruleElement.m_valueType);
				return checkRuleLongValue(ruleElement, inout, inout.getArrayLongtimeValue(critIndex),
					ruleElement.m_valueType, lastCase);
			}
			default:
				return false;
		}
	}

	private boolean checkRuleLongValue(CRGRuleElement ruleElement, long value, int valType) throws Exception
	{
		switch(valType) {
			case CRGRuleGrouperStatics.DATATYPE_DATE: {
				return checkRuleLongValue(value, ruleElement.m_longValue, ruleElement.m_operantType);
			}
			case CRGRuleGrouperStatics.DATATYPE_DAY_TIME: {
				return checkRuleLongValue(value, ruleElement.m_longtimeValue, ruleElement.m_operantType);
			}
			case CRGRuleGrouperStatics.DATATYPE_ARRAY_DATE: {
				return checkRuleLongValue(value, ruleElement.m_longArrayValue, ruleElement.m_operantType);
			}
			case CRGRuleGrouperStatics.DATATYPE_ARRAY_DAY_TIME: {
				return checkRuleLongValue(value, ruleElement.m_longtimeValue, ruleElement.m_operantType);
			}
			default:
				return false;
		}
	}

	protected static boolean checkRuleLongValue(long value, long critValue, int opType) throws Exception
	{
		switch(opType) {
			case CRGRuleGrouperStatics.OP_EQUAL: {
				return critValue == value;
			}
			case CRGRuleGrouperStatics.OP_NOT_EQUAL: {
				return critValue != value;
			}
			case CRGRuleGrouperStatics.OP_GT: {
				return value > critValue;
			}
			case CRGRuleGrouperStatics.OP_GT_EQUAL: {
				return value >= critValue;
			}
			case CRGRuleGrouperStatics.OP_LT: {
				return value < critValue;
			}
			case CRGRuleGrouperStatics.OP_LT_EQUAL: {
				return value <= critValue;
			}
			case CRGRuleGrouperStatics.OP_PLUS: {
				return true;
			}
			case CRGRuleGrouperStatics.OP_MINUS: {
				return true;
			}
			case CRGRuleGrouperStatics.OP_MULTIPL: {
				return true;
			}
			case CRGRuleGrouperStatics.OP_DIVIDE: {
				return true;
			}
			default:
				return false;
		}
	}

	private boolean checkRuleLongValue(long value, long[] critValues, int opType) throws Exception
	{
		switch(opType) {
			case CRGRuleGrouperStatics.OP_IN:
			case CRGRuleGrouperStatics.OP_IN_TABLE: {
				for(int i = 0; i < critValues.length; i++) {
					if(critValues[i] == value) {
						return true;
					}
				}
				return false;
			}
			case CRGRuleGrouperStatics.OP_NOT_EQUAL:
			case CRGRuleGrouperStatics.OP_NOT_IN_TABLE: {
				for(int i = 0; i < critValues.length; i++) {
					if(critValues[i] == value) {
						return false;
					}
				}
				return true;
			}
			default:
				return false;
		}

	}

	private boolean checkRuleLongValue(CRGRuleElement ruleElement, CRGInputOutputBasic inout, long[] values,
		int valType, int groupIndex) throws Exception
	{
            for(int i = 0; i < values.length; i++) {
                    if(values[i] == CRGInputOutputBasic.DEFAULT_LONG_VALUE) {
                            continue;
                    }
                    if(checkRuleElementInterval(ruleElement, inout, i)
                            && (groupIndex < 0
                            ||
                            (groupIndex >= 0) &&
                            inout.getArrayIntegerValue(ruleElement.m_criterionDependIndex)[i] == groupIndex)) {
                        
                    
                            switch(valType) {
                                case CRGRuleGrouperStatics.DATATYPE_DATE: {

                                    if(checkRuleLongValue(values[i], ruleElement.m_longValue, ruleElement.m_operantType)) {
                                            return true;
                                    }
                                    break;
                                }
                                case CRGRuleGrouperStatics.DATATYPE_DAY_TIME: {

                                    if(checkRuleLongValue(values[i], ruleElement.m_longtimeValue, ruleElement.m_operantType)) {
                                                                return true;
                                    }
                                    break;
                                }
                                case CRGRuleGrouperStatics.DATATYPE_ARRAY_DATE: {
                                    if(checkRuleLongValue(values[i], ruleElement.m_longArrayValue, ruleElement.m_operantType)) {
                                            return true;
                                    }
                                    break;
                                }
                                case CRGRuleGrouperStatics.DATATYPE_ARRAY_DAY_TIME: {
                                    if(checkRuleLongValue(values[i], ruleElement.m_longtimeArrayValue, ruleElement.m_operantType)) {
                                            return true;
                                    }
                                    break;
                                }
                                default:
                                        return false;
                        }
                    }
            }
            return false;
	}

	private boolean checkRuleStringValue(CRGRuleElement ruleElement, String value, int valType) throws Exception
	{
		switch(valType) {
			case CRGRuleGrouperStatics.DATATYPE_STRING: {
				//System.out.println("String--------");
				return checkRuleStringValue(value, ruleElement.m_strValue, ruleElement.m_operantType); //yyy, ruleElement.m_WildCard);
			}
			case CRGRuleGrouperStatics.DATATYPE_ARRAY_STRING: {
				if(ruleElement.m_arrayLength <= 0) {
					return false;
				}
				//System.out.println("String--------array");
				boolean ret = checkRuleStringValue(value, ruleElement.m_strArrayValue, ruleElement.m_operantType,
							  ruleElement.m_length); //yyy, ruleElement.m_WildCardArray);
				if((ruleElement.m_operantType == CRGRuleGrouperStatics.OP_NOT_IN)
					|| (ruleElement.m_operantType == CRGRuleGrouperStatics.OP_NOT_IN_TABLE)) {
					return!ret;
				} else {
					return ret;
				}

			}
			default:
				return false;
		}
	}

	protected static boolean checkRuleStringValue(String value, String critValue, int opType) throws Exception
	{
		switch(opType) {
			case CRGRuleGrouperStatics.OP_EQUAL: {
				return compareStringValue(value, critValue);
			}
			case CRGRuleGrouperStatics.OP_NOT_EQUAL: {
				return!compareStringValue(value, critValue);
			}
			default:
				return false;
		}
	}

	protected static boolean checkRuleStringValue(String value, String critValue, int opType,
		int wildCard) throws Exception
	{
		switch(opType) {
			case CRGRuleGrouperStatics.OP_EQUAL: {
				switch(wildCard) {
					case 0:
						return value.equals(critValue);
					case 1: {
						String s = critValue.substring(0, critValue.length() - 1);
						return value.indexOf(s) == 0 ? true : false;
					}
					case 2:
						return compareStringValue(value, critValue);
				}
			}
			case CRGRuleGrouperStatics.OP_NOT_EQUAL: {
				switch(wildCard) {
					case 0:
						return!value.equals(critValue);
					case 1: {
						String s = critValue.substring(0, critValue.length() - 1);
						return value.indexOf(s) == 0 ? false : true;
					}
					case 2:
						return!compareStringValue(value, critValue);
				}
			}
			default:
				return false;
		}
	}
        
        /**
         * überprüft das Vorkomnis eines Kriteriumsausprägung in der Liste/Tabelle mit den Werten String[] critValues
         * @param value
         * @param critValues
         * @param opType
         * @param length
         * @return
         * @throws Exception 
         */
	private boolean checkRuleStringValue(String value, String[] critValues, int opType, int length) throws Exception
	{
		switch(opType) {
			case CRGRuleGrouperStatics.OP_IN_TABLE:
			case CRGRuleGrouperStatics.OP_MANY_IN_TABLE:
			case CRGRuleGrouperStatics.OP_NOT_DOUBLE_IN_TABLE: {
				length = value.length() < length ? value.length() : length;
				String v = value.substring(0, length).toUpperCase();
				//if(wildTyp == 1)
				if(v.compareTo(critValues[critValues.length - 1].substring(0, length).toUpperCase()) > 0) {
					return false;
				}
				for(int i = 0; i < critValues.length; i++) {
					if(v.compareTo(critValues[i].substring(0, length).toUpperCase()) < 0) {
						return false;
					}
					if(compareStringValue(value, critValues[i])) {
						return true;
					}
				}
				return false;
			}
			case CRGRuleGrouperStatics.OP_IN:
			case CRGRuleGrouperStatics.OP_MANY_IN:
			case CRGRuleGrouperStatics.OP_NOT_DOUBLE_IN:
			case CRGRuleGrouperStatics.OP_NOT_IN:
			case CRGRuleGrouperStatics.OP_NOT_IN_TABLE: {
				length = value.length() < length ? value.length() : length;
				String v = value.substring(0, length).toUpperCase();
				//if(wildTyp == 1)
				for(int i = 0; i < critValues.length; i++) {
					if(compareStringValue(value, critValues[i])) {
						return true;
					}
				}
				return false;
			}
			/*			case CRGRuleGrouperStatics.OP_NOT_IN:
			   case CRGRuleGrouperStatics.OP_NOT_IN_TABLE:
			   {
			 for (int i=0; i<critValues.length; i++){
			  // muss raus, sobald eine Übereinstimmung gefunden wird, mit false
			  // und nicht weiter prüfen, da nicht notwendig, volker
			  if (compareStringValue(value, critValues[i]))
			   return false;
			 }
			 return true;
			   }*/
			default:
				return false;
		}
	}

	private boolean checkRuleStringValue(String value, String[] critValues, int opType, int length,
		int[] wildCardArray) throws Exception
	{
		switch(opType) {
			case CRGRuleGrouperStatics.OP_IN:
			case CRGRuleGrouperStatics.OP_IN_TABLE:
			case CRGRuleGrouperStatics.OP_MANY_IN_TABLE:
			case CRGRuleGrouperStatics.OP_MANY_IN:
			case CRGRuleGrouperStatics.OP_NOT_DOUBLE_IN_TABLE:
			case CRGRuleGrouperStatics.OP_NOT_DOUBLE_IN: {
				length = value.length() < length ? value.length() : length;
				String v = value.substring(0, length).toUpperCase();
				if(v.compareTo(critValues[critValues.length - 1].substring(0, length).toUpperCase()) > 0) {
					return false;
				}
				for(int i = 0; i < critValues.length; i++) {
					if(v.compareTo(critValues[i].substring(0, length).toUpperCase()) < 0) {
						return false;
					}
					boolean b = false;
					//* 3.9.5 2015-09-01 DNi: #FINDBUGS - Bei dem switch-case sollten die break-Anweisungen ergänzt werden! 
					switch(wildCardArray[i]) {
						case 0:
							b = value.toUpperCase().equals(critValues[i].toUpperCase());
						case 1: {
							String s = critValues[i].substring(0, critValues[i].length() - 1).toUpperCase();
							b = value.toUpperCase().indexOf(s) == 0 ? true : false;
						}
						case 2:
							b = compareStringValue(value, critValues[i]);
					}
					if(b) {
						return true;
					}
				}
				return false;
			}
			case CRGRuleGrouperStatics.OP_NOT_IN:
			case CRGRuleGrouperStatics.OP_NOT_IN_TABLE: {
				for(int i = 0; i < critValues.length; i++) {
					// muss raus, sobald eine Übereinstimmung gefunden wird, mit false
					// und nicht weiter prüfen, da nicht notwendig, volker
					boolean b = false;
					switch(wildCardArray[i]) {
						case 0:
							b = value.toUpperCase().equals(critValues[i].toUpperCase());
						case 1: {
							String s = critValues[i].substring(0, critValues[i].length() - 1).toUpperCase();
							b = value.toUpperCase().indexOf(s) == 0 ? true : false;
						}
						case 2:
							b = compareStringValue(value, critValues[i]);
					}
					if(b) {
						return false;
					}
				}
				return true;
			}
			default:
				return false;
		}
	}

	private boolean checkRuleStringValue(CRGRuleElement ruleElement, CRGInputOutputBasic inout, String[] values,
		int valType, int groupIndex) throws Exception
	{
		switch(valType) {
			case CRGRuleGrouperStatics.DATATYPE_STRING: {
				boolean ret = false;
				for(int i = 0; i < values.length; i++) {
					if((values[i] == null) && (!ret)) {
						return false;
					}
//					if(!m_isGK || (m_isGK && checkRuleElementInterval(ruleElement, inout, i))) {
					if(checkRuleElementInterval(ruleElement, inout, i)
						&& (groupIndex < 0
						||
						(groupIndex >= 0) &&
						inout.getArrayIntegerValue(ruleElement.m_criterionDependIndex)[i] == groupIndex)) {
						switch(ruleElement.m_operantType) {
							case CRGRuleGrouperStatics.OP_NOT_EQUAL:
							case CRGRuleGrouperStatics.OP_EQUAL: {
								if(checkRuleStringValue(values[i], ruleElement.m_strValue, ruleElement.m_operantType)) { //yyy, ruleElement.m_WildCard)){
									m_RefZaehler++;
									setRefValues(m_RefZaehler, values[i], ruleElement.m_criterionIndex, 99);
									ret = true;
								}
							}
						}
					}
				}
				return ret;

			}
			case CRGRuleGrouperStatics.DATATYPE_ARRAY_STRING: {
				int count = 0;
				boolean ret = false;
				int zaehlerEin = m_RefZaehler;
				if(ruleElement.m_arrayLength <= 0) {
					return false;
				}
				for(int i = 0; i < values.length; i++) {
					if(values[i] == null) {
						return false;
					}
//					if(!m_isGK || (m_isGK && checkRuleElementInterval(ruleElement, inout, i))) {
					if(checkRuleElementInterval(ruleElement, inout, i)
						&& (groupIndex < 0
						||
						(groupIndex >= 0) &&
						inout.getArrayIntegerValue(ruleElement.m_criterionDependIndex)[i] == groupIndex)) {
						if(checkRuleStringValue(values[i], ruleElement.m_strArrayValue, ruleElement.m_operantType,
							ruleElement.m_length)) { //yyy, ruleElement.m_WildCardArray)){
							switch(ruleElement.m_operantType) {
								case CRGRuleGrouperStatics.OP_MANY_IN:
								case CRGRuleGrouperStatics.OP_MANY_IN_TABLE: {
									m_RefZaehler++;
									setRefValues(m_RefZaehler, values[i], ruleElement.m_criterionIndex, 99);
									count++;
									if(count > 1) {
										ret = true;
									}
									break;
								}
								case CRGRuleGrouperStatics.OP_NOT_IN:
								case CRGRuleGrouperStatics.OP_NOT_IN_TABLE: {
									/*								m_RefZaehler++;
									  m_RefWert[m_RefZaehler] = values[i];
									  m_RefIndex2[m_RefZaehler] = i;
									  ret = true;*/
									break;
								}
								case CRGRuleGrouperStatics.OP_NOT_DOUBLE_IN:
								case CRGRuleGrouperStatics.OP_NOT_DOUBLE_IN_TABLE: {
									//m_RefZaehler++;
									//m_RefWert[m_RefZaehler] = values[i];
									//m_RefIndex2[m_RefZaehler] = i;
									count++;
									if(count > 1) {
										ret = false;
									}
									break;
								}
								default: {
									m_RefZaehler++;
									setRefValues(m_RefZaehler, values[i], ruleElement.m_criterionIndex, 99);
									return true;
								}
							}
						} else {
							switch(ruleElement.m_operantType) {
								case CRGRuleGrouperStatics.OP_NOT_IN:
								case CRGRuleGrouperStatics.OP_NOT_IN_TABLE: {
									m_RefZaehler++;
									setRefValues(m_RefZaehler, values[i], ruleElement.m_criterionIndex, 99);
									ret = true;
									break;
								}
							}
						}
					}
				}
				if(count > 1) {
					return ret;
				}
				if(count == 1) {
					switch(ruleElement.m_operantType) {
						case CRGRuleGrouperStatics.OP_NOT_DOUBLE_IN:
						case CRGRuleGrouperStatics.OP_NOT_DOUBLE_IN_TABLE: {
							if(count == 1) {
								return true;
							}
						}
					}
				}
				m_RefZaehler = zaehlerEin;
				return ret;
			}
			default:
				return false;
		}
	}

	private boolean checkRuleIntegerValue(CRGRuleElement ruleElement, int value, int valType) throws Exception
	{
		switch(valType) {
			case CRGRuleGrouperStatics.DATATYPE_INTEGER: {
				return checkRuleIntegerValue(value, ruleElement.m_intValue, ruleElement.m_operantType);
			}
			case CRGRuleGrouperStatics.DATATYPE_ARRAY_INTEGER: {
				return checkRuleIntegerValue(value, ruleElement.m_intArrayValue, ruleElement.m_operantType);
			}
			default:
				return false;
		}
	}

	private boolean checkRuleIntegerValue(int value, int critValue, int opType) throws Exception
	{
		switch(opType) {
			case CRGRuleGrouperStatics.OP_EQUAL: {
				return critValue == value;
			}
			case CRGRuleGrouperStatics.OP_NOT_EQUAL: {
				return critValue != value;
			}
			case CRGRuleGrouperStatics.OP_GT: {
				return value > critValue;
			}
			case CRGRuleGrouperStatics.OP_GT_EQUAL: {
				return value >= critValue;
			}
			case CRGRuleGrouperStatics.OP_LT: {
				return value < critValue;
			}
			case CRGRuleGrouperStatics.OP_LT_EQUAL: {
				return value <= critValue;
			}
			case CRGRuleGrouperStatics.OP_PLUS: {
				return true;
			}
			case CRGRuleGrouperStatics.OP_MINUS: {
				return true;
			}
			case CRGRuleGrouperStatics.OP_MULTIPL: {
				return true;
			}
			case CRGRuleGrouperStatics.OP_DIVIDE: {
				return true;
			}
			default:
				return false;
		}
	}

	private boolean checkRuleIntegerValue(int value, int[] critValues, int opType) throws Exception
	{
		switch(opType) {
			case CRGRuleGrouperStatics.OP_IN:
			case CRGRuleGrouperStatics.OP_IN_TABLE:
			case CRGRuleGrouperStatics.OP_MANY_IN_TABLE:
			case CRGRuleGrouperStatics.OP_MANY_IN:
			case CRGRuleGrouperStatics.OP_NOT_DOUBLE_IN:
			case CRGRuleGrouperStatics.OP_NOT_DOUBLE_IN_TABLE: {
				for(int i = 0; i < critValues.length; i++) {
					if(critValues[i] == value) {
						return true;
					}
				}
				return false;
			}
			case CRGRuleGrouperStatics.OP_NOT_EQUAL: {
				for(int i = 0; i < critValues.length; i++) {
					if(critValues[i] == value) {
						return false;
					}
				}
				return true;
			}
			case CRGRuleGrouperStatics.OP_NOT_IN:
			case CRGRuleGrouperStatics.OP_NOT_IN_TABLE: {
				for(int i = 0; i < critValues.length; i++) {
					if(critValues[i] == value) {
						return false;
					}
				}
				return true;
			}
			default:
				return false;
		}

	}

	private boolean checkRuleIntegerValue(CRGRuleElement ruleElement, CRGInputOutputBasic inout, int critIndex,
		int valType, int groupIndex) throws Exception
	{
		double[] values = fillCummulatedValuesForCrit(ruleElement, inout, critIndex);
		switch(valType) {
			case CRGRuleGrouperStatics.DATATYPE_INTEGER: {
				for(int i = 0; i < values.length; i++) {
					if(values[i] == CRGInputOutputBasic.DEFAULT_INT_VALUE) {
						return false;
					}
//					if(!m_isGK || (m_isGK && checkRuleElementInterval(ruleElement, inout, i))) {
					if(checkRuleElementInterval(ruleElement, inout, i)
						&& (groupIndex < 0
						||
						(groupIndex >= 0) &&
						inout.getArrayIntegerValue(ruleElement.m_criterionDependIndex)[i] == groupIndex)
						) {
						if(checkRuleIntegerValue((int)values[i], ruleElement.m_intValue, ruleElement.m_operantType)) {
							return true;
						}
					}
				}
				return false;
			}
			case CRGRuleGrouperStatics.DATATYPE_ARRAY_INTEGER: {

				int count = 0;
				boolean ret = false;
				for(int i = 0; i < values.length; i++) {
					if(((int)values[i]) == CRGInputOutputBasic.DEFAULT_INT_VALUE) {
						return false;
					}
//					if(!m_isGK || (m_isGK && checkRuleElementInterval(ruleElement, inout, i))) {
					if(checkRuleElementInterval(ruleElement, inout, i)
						&& (groupIndex < 0
						||
						(groupIndex >= 0) &&
						inout.getArrayIntegerValue(ruleElement.m_criterionDependIndex)[i] == groupIndex)) {
						if(checkRuleIntegerValue((int)values[i], ruleElement.m_intArrayValue, ruleElement.m_operantType)) {
							switch(ruleElement.m_operantType) {
								case CRGRuleGrouperStatics.OP_MANY_IN_TABLE:
								case CRGRuleGrouperStatics.OP_MANY_IN: {
									count++;
									if(count > 1) {
										return true;
									} else {
										continue;
									}
								}
								case CRGRuleGrouperStatics.OP_NOT_DOUBLE_IN_TABLE:
								case CRGRuleGrouperStatics.OP_NOT_DOUBLE_IN: {
									count++;
									if(count == 1) {
										ret = true;
									}
									if(count > 1) {
										return false;
									}
								}
								default:
									return true;
							}
						}
					}
				}
				return ret;

			}
			default:
				return false;
		}
	}

	private boolean checkRuleDoubleValue(CRGRuleElement ruleElement, double value, int valType) throws Exception
	{
		switch(valType) {
			case CRGRuleGrouperStatics.DATATYPE_DOUBLE: {
				return checkRuleDoubleValue(value, ruleElement.m_doubleValue, ruleElement.m_operantType);
			}
			case CRGRuleGrouperStatics.DATATYPE_ARRAY_DOUBLE: {
				return checkRuleDoubleValue(value, ruleElement.m_doubleArrayValue, ruleElement.m_operantType);
			}
			default:
				return false;
		}
	}

	protected static boolean checkRuleDoubleValue(double value, double critValue, int opType) throws Exception
	{
		switch(opType) {
			case CRGRuleGrouperStatics.OP_EQUAL: {
//				return value == critValue;
//vorschlag
				return Math.abs(value - critValue) < 0.00001;
			}
			case CRGRuleGrouperStatics.OP_NOT_EQUAL: {
				return value != critValue;
			}
			case CRGRuleGrouperStatics.OP_GT: {
				return value > critValue;
			}
			case CRGRuleGrouperStatics.OP_GT_EQUAL: {
				return value >= critValue;
			}
			case CRGRuleGrouperStatics.OP_LT: {
				return value < critValue;
			}
			case CRGRuleGrouperStatics.OP_LT_EQUAL: {
				return value <= critValue;
			}
			case CRGRuleGrouperStatics.OP_PLUS: {
				return true;
			}
			case CRGRuleGrouperStatics.OP_MINUS: {
				return true;
			}
			case CRGRuleGrouperStatics.OP_MULTIPL: {
				return true;
			}
			case CRGRuleGrouperStatics.OP_DIVIDE: {
				return true;
			}
			default:
				return false;
		}
	}

	private boolean checkRuleDoubleValue(double value, double[] critValues, int opType) throws Exception
	{
		switch(opType) {
			case CRGRuleGrouperStatics.OP_IN:
			case CRGRuleGrouperStatics.OP_IN_TABLE:
			case CRGRuleGrouperStatics.OP_MANY_IN_TABLE:
			case CRGRuleGrouperStatics.OP_MANY_IN:
			case CRGRuleGrouperStatics.OP_NOT_DOUBLE_IN:
			case CRGRuleGrouperStatics.OP_NOT_DOUBLE_IN_TABLE: {
				for(int i = 0; i < critValues.length; i++) {
					if(critValues[i] == value) {
						return true;
					}
				}
				return false;
			}
			case CRGRuleGrouperStatics.OP_NOT_EQUAL:
			case CRGRuleGrouperStatics.OP_NOT_IN_TABLE:
			case CRGRuleGrouperStatics.OP_NOT_IN: {
				for(int i = 0; i < critValues.length; i++) {
					if(critValues[i] == value) {
						return false;
					}
				}
				return true;
			}
			default:
				return false;
		}

	}

	private int[] fillCummulatedCount(CRGRuleElement ruleElement, CRGInputOutputBasic inout,
		int critIndex) throws Exception
	{
		int[] count = new int[1];
		count[0] = 0;
		if(ruleElement.m_valueType == CRGRuleGrouperStatics.DATATYPE_INTEGER
			|| ruleElement.m_valueType == CRGRuleGrouperStatics.DATATYPE_ARRAY_INTEGER) {
			if(!ruleElement.m_isCummulative) {
				count[0] = inout.getIntegerValue(critIndex);
				return count;

			}
			long[] interval = getIntervalLimits(ruleElement, inout);
			long intervalStart = interval[0];
			long intervalEnd = interval[1];
			interval = null;
			if(ruleElement.m_indCummulateWhat < 0
				&& ruleElement.m_indCummulateBase < 0
				&& ruleElement.m_indCummulateNumber < 0
				&& ruleElement.m_indCummulateDate >= 0) {
				// es mus die Anzahl der Einträge für datum ermittelt werden z.B. Fallanzahl
				if(intervalStart == 0 && intervalEnd == 0) {
					count[0] = inout.getIntegerValue(critIndex);
					return count;
				}
				if(ruleElement.m_indCummulateDate >= 0) {
					long[] dates = inout.getArrayLongValue(ruleElement.m_indCummulateDate);
					for(int i = 0; i < dates.length; i++) {
						if(dates[i] >= intervalStart && dates[i] <= intervalEnd) {
							count[0]++;
						}
					}
					return count;
				}
			}
		}

		return count;
	}

	private long[] getIntervalLimits(CRGRuleElement ruleElement, CRGInputOutputBasic inout) throws Exception
	{
		long[] ret = new long[2];
		if(ruleElement.hasInterval) {

			ret[0] = ruleElement.intervalStart;
			ret[1] = ruleElement.intervalEnd;
//			if(ret[0] == 0 && ret[1] == 0) {
//				if(m_setCheckDateFromAdmDate) {
//					ruleElement.interval.checkIntervalValuesCP(inout);
//					ret[0] = ruleElement.interval.getStartIntervalValue();
//					ret[1] = ruleElement.interval.getEndIntervalValue();
//				}
//			}
		}
		return ret;
	}

	private double[] fillCummulatedValuesForCrit(CRGRuleElement ruleElement, CRGInputOutputBasic inout,
		int critIndex) throws Exception
	{
		double[] what = null;
		double[] values = null;
		long[] interval = getIntervalLimits(ruleElement, inout);
		long intervalStart = interval[0];
		long intervalEnd = interval[1];
		interval = null;
		switch(ruleElement.m_valueType) {
			case CRGRuleGrouperStatics.DATATYPE_DOUBLE:
				;
			case CRGRuleGrouperStatics.DATATYPE_ARRAY_DOUBLE:
				values = inout.getArrayDoubleValue(critIndex); // wurde für die Regel schon ermittelt
				if(ruleElement.m_indCummulateWhat < 0 || ruleElement.m_criterionCheckIndex < 0) {
					return values;
				}
				what = inout.getArrayDoubleValue(ruleElement.m_indCummulateWhat);
				break;
			case CRGRuleGrouperStatics.DATATYPE_INTEGER:
				;
				if(ruleElement.m_isCummulative) {
					if(ruleElement.m_indCummulateWhat < 0
						&& ruleElement.m_indCummulateBase < 0
						&& ruleElement.m_indCummulateNumber < 0
						&& ruleElement.m_indCummulateDate >= 0) {
						// es mus die Anzahl der Einträge für datum ermittelt werden z.B. Fallanzahl
						double[] count = new double[1];
						count[0] = 0;
						if(intervalStart == 0 && intervalEnd == 0) {
							count[0] = inout.getIntegerValue(critIndex);
							return count;
						}
						if(ruleElement.m_indCummulateDate >= 0) {
							long[] dates = inout.getArrayLongValue(ruleElement.m_indCummulateDate);
							for(int i = 0; i < dates.length; i++) {
								if(dates[i] >= intervalStart && dates[i] <= intervalEnd) {
									count[0]++;
								}
							}
							return count;
						}
					}
				}
			case CRGRuleGrouperStatics.DATATYPE_ARRAY_INTEGER: {
				int[] intValues = inout.getArrayIntegerValue(critIndex);
				values = new double[intValues.length];
				if(ruleElement.m_indCummulateWhat < 0 || ruleElement.m_criterionCheckIndex < 0) {
					for(int i = 0; i < intValues.length; i++) {
						values[i] = intValues[i];
					}
					return values;
				}
				int[] whatInt = inout.getArrayIntegerValue(ruleElement.m_indCummulateWhat);
				what = new double[whatInt.length];
				for(int i = 0; i < whatInt.length; i++) {
					what[i] = whatInt[i];
				}
				break;
			}
		}
		if(what == null || what.length == 0) {
			return values;
		}
		String[] base = new String[what.length];
		if(ruleElement.m_indCummulateBase >= 0) {
			base = inout.getArrayStringValue(ruleElement.m_indCummulateBase);
		}
		int[] number = new int[what.length];
		if(ruleElement.m_indCummulateNumber >= 0) {
			number = inout.getArrayIntegerValue(ruleElement.m_indCummulateNumber);
		}
		long[] dates = new long[what.length];
		if(ruleElement.m_indCummulateDate >= 0) {
			dates = inout.getArrayLongValue(ruleElement.m_indCummulateDate);
		}
		values = new double[what.length];
		for(int i = 0; i < values.length; i++) {
			values[i] = 0;
		}

		for(int i = 0; i < what.length; i++) {
			if((intervalStart == 0 && intervalEnd == 0) || (dates[i] >= intervalStart && dates[i] <= intervalEnd)) {
				for(int j = 0; j < what.length; j++) {
					if(ruleElement.m_indCummulateBase < 0 || base[j].equals(base[i])) {
						if(ruleElement.m_indCummulateWhat == ruleElement.m_indCummulateNumber) {
							values[j] += what[i];
						} else {
							values[j] += what[i] * number[i];
						}
					}
				}
			}
		}

		return values;
	}

	private boolean checkRuleDoubleValue(CRGRuleElement ruleElement, CRGInputOutputBasic inout, int critIndex,
		int valType, int groupIndex) throws Exception
	{
		double[] values = fillCummulatedValuesForCrit(ruleElement, inout, critIndex);
		switch(valType) {
			case CRGRuleGrouperStatics.DATATYPE_DOUBLE: {
				for(int i = 0; i < values.length; i++) {
					if(values[i] == CRGInputOutputBasic.DEFAULT_DOUBLE_VALUE) {
						return false;
					}
//					if(!m_isGK || (m_isGK && checkRuleElementInterval(ruleElement, inout, i))) {
					if(checkRuleElementInterval(ruleElement, inout, i)
						&& (groupIndex < 0
						||
						(groupIndex >= 0) &&
						inout.getArrayIntegerValue(ruleElement.m_criterionDependIndex)[i] == groupIndex)) {

						if(checkRuleDoubleValue(values[i], ruleElement.m_doubleValue, ruleElement.m_operantType)) {
							return true;
						}
					}
				}
				return false;
			}
			case CRGRuleGrouperStatics.DATATYPE_ARRAY_DOUBLE: {
				int count = 0;
				boolean ret = false;
				for(int i = 0; i < values.length; i++) {
					if(values[i] == CRGInputOutputBasic.DEFAULT_DOUBLE_VALUE) {
						return false;
					}
//					if(!m_isGK || (m_isGK && checkRuleElementInterval(ruleElement, inout, i))) {
					if(checkRuleElementInterval(ruleElement, inout, i) && (groupIndex < 0
						||
						(groupIndex >= 0) &&
						inout.getArrayIntegerValue(ruleElement.m_criterionDependIndex)[i] == groupIndex)
						) {
						if(checkRuleDoubleValue(values[i], ruleElement.m_doubleArrayValue, ruleElement.m_operantType)) {
							switch(ruleElement.m_operantType) {
								case CRGRuleGrouperStatics.OP_MANY_IN_TABLE:
								case CRGRuleGrouperStatics.OP_MANY_IN: {
									count++;
									if(count > 1) {
										return true;
									} else {
										continue;
									}
								}
								case CRGRuleGrouperStatics.OP_NOT_DOUBLE_IN_TABLE:
								case CRGRuleGrouperStatics.OP_NOT_DOUBLE_IN: {
									count++;
									if(count == 1) {
										ret = true;
									}
									if(count > 1) {
										return false;
									}
								}
								default:
									return true;
							}
						}
					}
				}
				return ret;
			}
			default:
				return false;
		}
	}

	/**
	 * Führt zu einer bereits ermittelten Prüfregel die hinterlegten Vorschläge aus
	 * und gibt als Ergebnis ein mögliches Delta CW zurück.
	 * <p>
	 * Für die Regel werden immer, in Bezug auf den zuvor übergebenen Parameter,
	 * die in der Regel definierten Vorschläge angewendet und ein neues DRG-Ergebnis ermittelt.<br>
	 * Der dort berechnete effektive CW wird als Differenz zu den zuvor
	 * ermittelten effektiven CWs des Orginalfalles zurück gegeben.
	 * @param rule CRGRule : Prüfregel
	 * @param orgInOut CRGInputOutputBasic : Ein- und Ausgabe Objekt
	 * @return double : Delta CW
	 * @throws Exception
	 */
//	public double performSuggestion(CRGRule rule, CRGInputOutputBasic orgInOut) throws Exception
//	{
//		CRGSuggestion[] suggs = rule.m_suggestions;
////		m_grouper.setInoutFormOtherInout(orgInOut, orgInOut.m_inout);
//		//kopieren der erhaltenen Werte, hier sind suggInOut und orgInOut identisch
//		m_grouper.copyInSuggInout(orgInOut);
////		m_inoutSugg.copyCase(orgInOut, true);
//		//nun müssen die Vorschläge in suggInOut eingebaut werden.
//		//d.h. CRGInputOutputBasic - Object entsprechend anpassen.
//		//------------------------
//		//in suggs stehen die Werte, die zu ändern sind in den Arrays, Werten
//		//also die, die != null,0 sind in suggInOut übertragen
//		//dann die autocorrektur durchführen
//		//dann neu groupen
//		//dabei ist dann nur dCW wichtig???
//		int lg = suggs != null ? suggs.length : 0;
//		int value = 0;
//		m_inoutSugg = m_grouper.getSuggCRGInputObject();
//		for(int i = 0; i < lg; i++) {
//			if(suggs[i].m_suggType != CriterionEntry.CRIT_RULE_AND_SUGG_AFTER_FEE_VALIDATION) {
//				value = m_inoutSugg.setSuggestionValues(suggs[i]);
//			}
//		}
//
////		System.out.println("Original");
////		double ret = checkAutoCorrectur(rule, orgInOut, m_inoutSugg, value);
//		double ret = m_grouper.checkAutoCorrectur(orgInOut);
//		m_inoutSugg = m_grouper.getSuggCRGInputObject();
//		if(m_inoutSugg.getLengthOfStay() <= 0)
//			m_inoutSugg.setLengthOfStay(orgInOut.getLengthOfStay());
//		for(int i = 0; i < lg; i++) {
//			if(suggs[i].m_suggType == CriterionEntry.CRIT_RULE_AND_SUGG_AFTER_FEE_VALIDATION) {
//				if(suggs[i].getActionType() == DatCaseRuleAttributes.SUGG_DELETE) {
//					m_inoutSugg.setSuggestionValues(suggs[i]);
//				} else if(suggs[i].getActionType() == DatCaseRuleAttributes.SUGG_ADD) {
//					// für hunzufügen muss der darauffolgenden Vorschlag ügerprüft werden;
//					// das Austauschen von Daten ist nicht möglich
//					i = checkSuggestionMethod(suggs, i);
//				}
//			}
//		}
////		System.out.println(m_inoutSugg.dumpRecord());
//		return ret;
//	}

        /**
         * wertet einen Vorschlag der Regel aus
         * @param sugg
         * @param orgInOut
         * @return
         * @throws Exception 
         */
//        public double performOneSuggestion(CRGSuggestion sugg, CRGInputOutputBasic orgInOut) throws Exception
//        {
//            m_grouper.copyInSuggInout(orgInOut);
//            m_inoutSugg = m_grouper.getSuggCRGInputObject();
//            m_inoutSugg.setSuggestionValues(sugg);
//            return m_grouper.checkAutoCorrectur(orgInOut);
//        }
	/**
	 * werden die Vorschläge angewendet, die für die Anwendung nach der Entgeltvalidierung markiert sind.
	 * Sie werden auf dem m_inoutSugg durchgeführt, der schon alle anderen Vorschläge beinhaltet
	 * @param rule CRGRule
	 * @throws Exception
	 */
//	public double performSuggestionsAfterFeeValidation(CRGRule rule) throws Exception
//	{
//		CRGSuggestion[] suggs = rule.m_suggestions;
//		int lg = suggs != null ? suggs.length : 0;
//		int value = 0;
//		for(int i = 0; i < lg; i++) {
//			if(suggs[i].m_suggType == CriterionEntry.CRIT_RULE_AND_SUGG_AFTER_FEE_VALIDATION) {
//				if(suggs[i].getActionType() == DatCaseRuleAttributes.SUGG_DELETE) {
//					m_inoutSugg.setSuggestionValues(suggs[i]);
//				} else if(suggs[i].getActionType() == DatCaseRuleAttributes.SUGG_ADD) {
//					// für hunzufügen muss der darauffolgenden Vorschlag ügerprüft werden;
//					// das Austauschen von Daten ist nicht möglich
//					i = checkSuggestionMethod(suggs, i);
//				}
//			}
//		}
//		String[] fees = m_inoutSugg.getFeeTypeList();
//		double[] values = m_inoutSugg.getFeeValuesList();
//		int[] counts = m_inoutSugg.getFeeNumberList();
//		double sum = 0;
//		for(int i = 0; i < fees.length; i++) {
//			if(fees[i].length() > 0) {
//				sum += values[i] * counts[i];
//			}
//		}
//		return sum;
//	}

	private int checkSuggestionMethod(CRGSuggestion[] suggs, int i)
	{
		int[][] deps = suggs[i].m_suggCheckDepend;
		if(deps == null || deps.length == 0) {
			return i;
		}
		String method = suggs[i].m_suggCheckMethod;
		if(method == null || method.length() == 0) {
			return i;
		}
		int len = deps.length;
		try {
			Class[] param = new Class[len + 1];
			Object[] args = new Object[len + 1];
			int j = 0;
			int lastFilled = -1;
			for(j = 0; j < len; j++) {
				// bei Zusamenhängenden Kriterien ist nur Einzelleingabe möglich, Listen werden nicht bearbeitet
				param[j] = CRGRuleGrouperManager.getSimpleClassToCritType(deps[j][0]); 
				int checkInd = i + j;
				if(checkInd < suggs.length && suggs[checkInd].m_criterionType == deps[j][0]
					&& suggs[checkInd].m_operantType == CRGRuleGrouperStatics.OP_EQUAL
					&& suggs[checkInd].m_actionType == suggs[i].m_actionType
					&& deps[j][1] == suggs[checkInd].m_criterionIndex) {
					args[j] = suggs[checkInd].getSimpleSuggValue();
				} else {
					Constructor con = param[j].getConstructor(new Class[] {String.class});
					args[j] = con.newInstance(new Object[] {String.valueOf(deps[j][2])});
					if(lastFilled < 0) {
						lastFilled = j;
					}
				}
			}
			if(lastFilled < 0) {
				i += j - 1;
			} else {
				i += lastFilled - 1;
			}
			param[len] = this.m_inoutSugg.getClass();
			args[len] = m_inoutSugg;
			java.lang.reflect.Method doMethod = this.getClass().getMethod(method, param);
			Object o = doMethod.invoke(this, args);

		} catch(Exception e) {
			e.printStackTrace();
			return i;
		}
		return i;
	}

	/**
	 * Dieser methode ist in  depends für die zusammenhängenden Entgeltkriterien definiert
	 * z.Z. ist rausgenommen, da es entschieden worden, dass die Entgeltkriterien nicht nach der neuen
	 * Entgeltprüfung angewendet werden, sonden zusammen mit den anderen Vorschlagskriterien, statt dessen wird den entsprechende
	 * Entgelt in die Menge der Entgelte zugefügt
	 * @param feeKey String
	 * @param feeValue Double
	 * @param feeCount Integer
	 * @param inout CRGInputOutputBasic
	 * @return boolean
	 */
//	public boolean checkFeeMethod(String feeKey,  Integer feeCount,Double feeValue,
//		CRGInputOutputBasic inout) throws Exception
//	{
//		inout.addFeeSuggRecord(feeKey, feeValue, feeCount);
////		getM_suggFeeCalculator().addOneFeeWithoutValidation(feeKey, feeCount.intValue(), feeValue.doubleValue(), inout);
//		return true;
//	}
//

/*	private double checkAutoCorrectur(CRGRule rule, CRGInputOutputBasic orgInOut, CRGInputOutputBasic suggInOut,
		int value) throws Exception
	{
		try {
//			RulerInputObject inout = orgInOut.getGrouperInputObject();
			RulerInputObject inout = this.m_grouper.getInputObject();
			//-------------------------
			// alles wird zurückgesetzt, d.h. alle Werte wieder einfügen, auch die
			// die nicht über die Vorschläge eingestellt werden können, aber wichtig für die Berechnung sind
			inout.newCase();
			//weitere Falldaten
//			   inout.setIkz(orgInOut.getStringValue(CRGRuleGrouperStatics.CRITSTR_INDEX_IKZ));
//			   inout.setKasse(orgInOut.getStringValue(CRGRuleGrouperStatics.CRITSTR_INDEX_KASSE));
//			   inout.setBaseRate(orgInOut.getDoubleValue(CRGRuleGrouperStatics.CRITDOUBLE_INDEX_BASERATE));
//			 
			inout.setAdmissionCause(orgInOut.getIntegerValue(CRGRuleGrouperStatics.CRITINT_INDEX_ADMCAUSE));
			inout.setAdmissionReason12(orgInOut.getIntegerValue(CRGRuleGrouperStatics.CRITINT_INDEX_ADMREASON12));
			inout.setDiscargeReason12(orgInOut.getIntegerValue(CRGRuleGrouperStatics.CRITINT_INDEX_DISCHARGEREASON12));
			inout.setAdmissionReason34(orgInOut.getIntegerValue(CRGRuleGrouperStatics.CRITINT_INDEX_ADMREASON34));
			inout.setDiscargeReason3(orgInOut.getIntegerValue(CRGRuleGrouperStatics.CRITINT_INDEX_DISCHARGEREASON3));

//			inout.setAdmissionDiagnosis(orgInOut.getStringValue(CRGRuleGrouperStatics.CRITSTR_INDEX_ADM_DIAG),
//				orgInOut.getStringValue(CRGRuleGrouperStatics.CRITSTR_INDEX_ADM_DIAG_OFF),
//				orgInOut.getIntegerValue(CRGRuleGrouperStatics.CRITINT_INDEX_ADM_DIAG_REFTYPE));
//			   inout.setAdmissionHospital(orgInOut.getStringValue(CRGRuleGrouperStatics.CRITSTR_INDEX_ADM_HOS));
//			   inout.setAdmissionDoc(orgInOut.getStringValue(CRGRuleGrouperStatics.CRITSTR_INDEX_ADM_DOC));

			   //Patientendaten
//			   inout.setCity(orgInOut.getStringValue(CRGRuleGrouperStatics.CRITSTR_INDEX_CITY));
//			   inout.setZipCode(orgInOut.getStringValue(CRGRuleGrouperStatics.CRITSTR_INDEX_ZIP_CODE));
			 
			inout.setNALOS(orgInOut.getIntegerValue(CRGRuleGrouperStatics.CRITINT_INDEX_NALOS));
			inout.setReduceLos(orgInOut.getIntegerValue(CRGRuleGrouperStatics.CRITINT_INDEX_TOB));
			inout.setDateOfBirth(orgInOut.getDateValue(CRGRuleGrouperStatics.CRITDATE_INDEX_DATEOFBIRTH));
//			inout.setCareState(orgInOut.getIntegerValue(CRGRuleGrouperStatics.CRITINT_INDEX_CARE));
			inout.setAdmissionDate(orgInOut.getDateValue(CRGRuleGrouperStatics.CRITDATE_INDEX_ADMISSION));
			inout.setDischargeDate(orgInOut.getDateValue(CRGRuleGrouperStatics.CRITDATE_INDEX_DISCHARGE));
			inout.setTransferDate(orgInOut.getDateValue(CRGRuleGrouperStatics.CRITARRAYDATE_INDEX_TRANSFER));
			inout.setLeaveOfAbsence(orgInOut.getIntegerValue(CRGRuleGrouperStatics.CRITINT_INDEX_LEAVEOFABSENCE));
//						if(orgInOut.getArrayStringValue(CRGRuleGrouperStatics.CRITARRAYSTRING_INDEX_DEPARTMENT) != null) {
//				String[] department = orgInOut.getArrayStringValue(CRGRuleGrouperStatics.
//					   CRITARRAYSTRING_INDEX_DEPARTMENT);
//				for(int i = 0; i < department.length; i++) {
//				 inout.addDepartment(department[i]);
//				}
//			   }
			inout.setDepartmentType(orgInOut.getIntegerValue(CRGRuleGrouperStatics.CRITINT_INDEX_DEPARTMENTTYPE));
			boolean isTrans = orgInOut.getIntegerValue(CRGRuleGrouperStatics.CRITINT_INDEX_ISTRANSFERED) == 0 ? false : true;
			inout.setIsTransfer(isTrans);
//			inout.setInvoluntary(orgInOut.getIntegerValue(CRGRuleGrouperStatics.CRITINT_INDEX_INVOLUNTARY));
//			   inout.setReceptDepartment(orgInOut.getStringValue(CRGRuleGrouperStatics.CRITSTR_INDEX_SPEC_ADM));
//			   inout.setReleaseDepartment(orgInOut.getStringValue(CRGRuleGrouperStatics.CRITSTR_INDEX_SPEC_DIS));
//			   inout.setCombatDepartment(orgInOut.getStringValue(CRGRuleGrouperStatics.CRITSTR_INDEX_SPEC_TREAT));
			 
			inout.setIntensiveDepStay(orgInOut.getIntegerValue(CRGRuleGrouperStatics.CRITINT_INDEX_INTENSIVESTAY));
//		if(orgInOut.getArrayStringValue(CRGRuleGrouperStatics.CRITARRAYSTRING_INDEX_STATIONS) != null) {
//				String[] stations = orgInOut.getArrayStringValue(CRGRuleGrouperStatics.CRITARRAYSTRING_INDEX_STATIONS);
//				for(int i = 0; i < stations.length; i++) {
//				 inout.addStation(stations[i]);
//				}
//			   }
//			   inout.setInsuranceState(orgInOut.getIntegerValue(CRGRuleGrouperStatics.CRITINT_INDEX_INSURANCE_STATUS));

//			   inout.setCaseNumeric1(orgInOut.getIntegerValue(CRGRuleGrouperStatics.CRITINT_INDEX_CASE_NUMERIC1));
//			   inout.setCaseNumeric2(orgInOut.getIntegerValue(CRGRuleGrouperStatics.CRITINT_INDEX_CASE_NUMERIC2));
//			   inout.setCaseNumeric3(orgInOut.getIntegerValue(CRGRuleGrouperStatics.CRITINT_INDEX_CASE_NUMERIC3));
//			   inout.setCaseNumeric4(orgInOut.getIntegerValue(CRGRuleGrouperStatics.CRITINT_INDEX_CASE_NUMERIC4));
//			   inout.setCaseNumeric5(orgInOut.getIntegerValue(CRGRuleGrouperStatics.CRITINT_INDEX_CASE_NUMERIC5));
			 
			//Vorschlagsdaten
			if(suggInOut.getIntegerValue(CRGRuleGrouperStatics.CRITINT_INDEX_RESPIRATION) >= 0) {
				inout.setRespirationLength(suggInOut.getIntegerValue(CRGRuleGrouperStatics.CRITINT_INDEX_RESPIRATION));
				//inout.setRespirationLength(rule.sugg_breathing);
			} else {
				inout.setRespirationLength(orgInOut.getIntegerValue(CRGRuleGrouperStatics.CRITINT_INDEX_RESPIRATION));
			}
			if(suggInOut.getIntegerValue(CRGRuleGrouperStatics.CRITINT_INDEX_AGEY) >= 0) {
				inout.setAgeY(suggInOut.getIntegerValue(CRGRuleGrouperStatics.CRITINT_INDEX_AGEY));
				//inout.setAgeY(rule.sugg_age_years);
			} else {
				inout.setAgeY(orgInOut.getIntegerValue(CRGRuleGrouperStatics.CRITINT_INDEX_AGEY));
			}
			if(suggInOut.getIntegerValue(CRGRuleGrouperStatics.CRITINT_INDEX_AGED) >= 0) {
				inout.setAgeD(suggInOut.getIntegerValue(CRGRuleGrouperStatics.CRITINT_INDEX_AGED));
				//inout.setAgeD(rule.sugg_age_months);
			} else {
				inout.setAgeD(orgInOut.getIntegerValue(CRGRuleGrouperStatics.CRITINT_INDEX_AGED));
			}
			if(suggInOut.getIntegerValue(CRGRuleGrouperStatics.CRITINT_INDEX_SEX) >= 0) {
				inout.setSex(suggInOut.getIntegerValue(CRGRuleGrouperStatics.CRITINT_INDEX_SEX));
				//inout.setSex(rule.sugg_sex.charAt(0));
			} else {
				inout.setSex(orgInOut.getIntegerValue(CRGRuleGrouperStatics.CRITINT_INDEX_SEX));
			}
			if(suggInOut.getDoubleValue(CRGRuleGrouperStatics.CRITDOUBLE_INDEX_WEIGHT) >= 0) {
				inout.setWeight(suggInOut.getDoubleValue(CRGRuleGrouperStatics.CRITDOUBLE_INDEX_WEIGHT));
				//inout.setWeight(rule.sugg_weight);
			} else {
				inout.setWeight(orgInOut.getDoubleValue(CRGRuleGrouperStatics.CRITDOUBLE_INDEX_WEIGHT));
			}
			if(suggInOut.getStringValue(CRGRuleGrouperStatics.CRITSTR_INDEX_ADMCAUSE) != null) { //Aufnahmeanlass
				inout.setAdmissionCause(suggInOut.getStringValue(CRGRuleGrouperStatics.CRITSTR_INDEX_ADMCAUSE));
				//inout.setAdmissionCause(rule.sugg_adm_cause);
			} else {
				inout.setAdmissionCause(orgInOut.getStringValue(CRGRuleGrouperStatics.CRITSTR_INDEX_ADMCAUSE));
			}
			if(suggInOut.getIntegerValue(CRGRuleGrouperStatics.CRITINT_INDEX_ADMREASON12) >= 0) {
				inout.setAdmissionReason12(suggInOut.getIntegerValue(CRGRuleGrouperStatics.CRITINT_INDEX_ADMREASON12));
				//inout.setAdmissionReason12(rule.sugg_adm_type);
			} else {
				inout.setAdmissionReason12(orgInOut.getIntegerValue(CRGRuleGrouperStatics.CRITINT_INDEX_ADMREASON12));
			}
			if(suggInOut.getIntegerValue(CRGRuleGrouperStatics.CRITINT_INDEX_DISCHARGEREASON12) >= 0) {
				inout.setDiscargeReason12(suggInOut.getIntegerValue(CRGRuleGrouperStatics.
					CRITINT_INDEX_DISCHARGEREASON12));
				//inout.setDiscargeReason12(rule.sugg_dis_type);
			} else {
				inout.setDiscargeReason12(orgInOut.getIntegerValue(CRGRuleGrouperStatics.
					CRITINT_INDEX_DISCHARGEREASON12));
			}

			
//			 int vwd = rule.sugg_vwd;
//			 if(vwd > 0) {
//			 Date dt = fall.getEntlassungsDatum();
//			 if(dt != null) {
//			  int op = rule.sugg_vwd_op;
//			 }
//			 }
			if(suggInOut.getIntegerValue(CRGRuleGrouperStatics.CRITINT_INDEX_LOS) >= 0) {
				if(suggInOut.getIntegerValue(CRGRuleGrouperStatics.CRITINT_INDEX_SUGGESTION_LOS_SET_VALUE) == 0) {
					inout.setLOS(suggInOut.getIntegerValue(CRGRuleGrouperStatics.CRITINT_INDEX_LOS),
						suggInOut.getIntegerValue(CRGRuleGrouperStatics.CRITINT_INDEX_TOB)); //Verweildauer aus dem Vorschlag
				} else {
					inout.setLOS(suggInOut.getIntegerValue(CRGRuleGrouperStatics.CRITINT_INDEX_LOS));
				}
//				inout.setLOS(suggInOut.getIntegerValue(CRGRuleGrouperStatics.CRITINT_INDEX_LOS),
//				  suggInOut.getIntegerValue(CRGRuleGrouperStatics.CRITINT_INDEX_TOB)); //Verweildauer
			} else {
				//			inout.setLOS(orgInOut.getIntegerValue(CRGRuleGrouperStatics.CRITINT_INDEX_LOS_ORIGINAL)); //Verweildauer
				inout.setLOS(orgInOut.getIntegerValue(CRGRuleGrouperStatics.CRITINT_INDEX_LOS_ORIGINAL),
					orgInOut.getIntegerValue(CRGRuleGrouperStatics.CRITINT_INDEX_TOB)); //Verweildauer
			}
			//Verweildauer in Stunden und Verweildauer kliener 24H als Vorschlag nicht möglich
			
//			 if(suggInOut.getIntegerValue(CRGRuleGrouperStatics.CRITINT_INDEX_VWD_INHOUR) >= 0){
//			 }
//			 if(suggInOut.getIntegerValue(CRGRuleGrouperStatics.CRITINT_INDEX_VISIT24) >= 0){
//			 }
			 

//			 sind doch nicht geändert worden
//			 inout.setIkz(orgInOut.getStringValue(CRGRuleGrouperStatics.CRITSTR_INDEX_IKZ));
//			 inout.setKasse(orgInOut.getStringValue(CRGRuleGrouperStatics.CRITSTR_INDEX_KASSE));
//			 inout.setBaseRate(orgInOut.getDoubleValue(CRGRuleGrouperStatics.CRITDOUBLE_INDEX_BASERATE));
			 //weitere Falldaten
//			 setPatientDates(m_inout, fall);
//			 setCaseDates(m_inout, fall, false);
			 
			//Patientendaten: PLZ, Versichertenstatus, Wohnort

			// ganz wichtig!! Diagnosen, Haupdiagnose, Nebendiagnosen und Prozeduren
			if(suggInOut.getStringValue(CRGRuleGrouperStatics.CRITSTR_INDEX_MAINDIAG) != null) {
				inout.setPrincipalDiagnosis(suggInOut.getStringValue(CRGRuleGrouperStatics.CRITSTR_INDEX_MAINDIAG),
					suggInOut.getStringValue(CRGRuleGrouperStatics.CRITSTR_INDEX_MAINDIAG), 0);
			} else {
				inout.setPrincipalDiagnosis(orgInOut.getStringValue(CRGRuleGrouperStatics.CRITSTR_INDEX_MAINDIAG),
					orgInOut.getStringValue(CRGRuleGrouperStatics.CRITSTR_INDEX_MAINDIAG), 0);
			}

			if(suggInOut.getArrayStringValue(CRGRuleGrouperStatics.CRITARRAYSTRING_INDEX_PROC) != null) {
				String[] proc = null;
				String[] procOff = null;
				int[] loc = null;
				java.util.Date[] dat = null;
				if(suggInOut.getArrayStringValue(CRGRuleGrouperStatics.CRITARRAYSTRING_INDEX_PROC) != null) {
					proc = suggInOut.getArrayStringValue(CRGRuleGrouperStatics.CRITARRAYSTRING_INDEX_PROC);
					procOff = suggInOut.getArrayStringValue(CRGRuleGrouperStatics.CRITARRAYSTRING_INDEX_PROC_OFF);
					loc = suggInOut.getArrayIntegerValue(CRGRuleGrouperStatics.CRITARRAYINT_INDEX_PROC_LOCAL);
					dat = suggInOut.getArrayDateValue(CRGRuleGrouperStatics.CRITARRAYDATE_INDEX_PROC_DATE);
					if(procOff != null) {
						for(int i = 0; i < procOff.length; i++) {
							inout.addProcedure(proc[i], loc[i], dat[i], procOff[i]);
						}
						for(int i = procOff.length; i < proc.length; i++) {
							inout.addProcedure(proc[i], loc[i], dat[i], null);
						}
					} else {
						for(int i = 0; i < proc.length; i++) {
							inout.addProcedure(proc[i], loc[i], dat[i], null);
						}
					}
				}
			} else {
				String[] proc = null;
				String[] procOff = null;
				int[] loc = null;
				java.util.Date[] dat = null;
				if(orgInOut.getArrayStringValue(CRGRuleGrouperStatics.CRITARRAYSTRING_INDEX_PROC) != null) {
					proc = orgInOut.getArrayStringValue(CRGRuleGrouperStatics.CRITARRAYSTRING_INDEX_PROC);
					procOff = orgInOut.getArrayStringValue(CRGRuleGrouperStatics.CRITARRAYSTRING_INDEX_PROC_OFF);
					loc = orgInOut.getArrayIntegerValue(CRGRuleGrouperStatics.CRITARRAYINT_INDEX_PROC_LOCAL);
					dat = orgInOut.getArrayDateValue(CRGRuleGrouperStatics.CRITARRAYDATE_INDEX_PROC_DATE);
					if(procOff != null) {
						for(int i = 0; i < proc.length; i++) {
							inout.addProcedure(proc[i], loc[i], dat[i], procOff[i]);
						}
						for(int i = procOff.length; i < proc.length; i++) {
							inout.addProcedure(proc[i], loc[i], dat[i], null);
						}
					} else {
						for(int i = 0; i < proc.length; i++) {
							inout.addProcedure(proc[i], loc[i], dat[i], null);
						}
					}
				}
			}

			//---------------------------------------------
			// grundsätzlich von Stringarray Diagnosen übernehmen <-------???????
			// nicht von Nebendiagnosen
			if((suggInOut.getArrayStringValue(CRGRuleGrouperStatics.CRITARRAYSTRING_INDEX_DIAG_AUX) == null) &&
				(suggInOut.getArrayStringValue(CRGRuleGrouperStatics.CRITARRAYSTRING_INDEX_DIAG) == null) &&
				(value == 0)) {
				String[] diag = null;
				String[] odiag = null;
				int[] ref = null;
				int[] loc = null;
				if(orgInOut.getArrayStringValue(CRGRuleGrouperStatics.CRITARRAYSTRING_INDEX_DIAG_AUX) != null) {
					diag = orgInOut.getArrayStringValue(CRGRuleGrouperStatics.CRITARRAYSTRING_INDEX_DIAG_AUX);
					odiag = orgInOut.getArrayStringValue(CRGRuleGrouperStatics.CRITARRAYSTRING_INDEX_DIAG_OFF_AUX);
					ref = orgInOut.getArrayIntegerValue(CRGRuleGrouperStatics.CRITARRAYINT_INDEX_DIAG_REFTYPE_AUX);
					loc = orgInOut.getArrayIntegerValue(CRGRuleGrouperStatics.CRITARRAYINT_INDEX_DIAG_LOCAL_AUX);
					for(int i = 0; i < diag.length; i++) {
						inout.addAuxiliaryDiagnosis(diag[i], odiag[i], ref[i], loc[i]);
					}
				}
			} else {
				String[] diag = null;
				String[] odiag = null;
				int[] ref = null;
				int[] loc = null;
				if(suggInOut.getArrayStringValue(CRGRuleGrouperStatics.CRITARRAYSTRING_INDEX_DIAG_AUX) != null) {
					diag = suggInOut.getArrayStringValue(CRGRuleGrouperStatics.CRITARRAYSTRING_INDEX_DIAG_AUX);
					odiag = suggInOut.getArrayStringValue(CRGRuleGrouperStatics.CRITARRAYSTRING_INDEX_DIAG_OFF_AUX);
					ref = suggInOut.getArrayIntegerValue(CRGRuleGrouperStatics.CRITARRAYINT_INDEX_DIAG_REFTYPE_AUX);
					loc = suggInOut.getArrayIntegerValue(CRGRuleGrouperStatics.CRITARRAYINT_INDEX_DIAG_LOCAL_AUX);
					for(int i = 0; i < diag.length; i++) {
						inout.addAuxiliaryDiagnosis(diag[i], odiag[i], ref[i], loc[i]);
					}
				}
			}

			// isTransfer - Flag setzen nicht für DAK
			if(suggInOut.getIntegerValue(CRGRuleGrouperStatics.CRITINT_INDEX_ISTRANSFERED_ADM) == 0) {
				inout.setIsTransfer(GrouperInterfaceBasic.checkAdmDisFlag(suggInOut.getIntegerValue(
					CRGRuleGrouperStatics.
					CRITINT_INDEX_ADMCAUSE),
					suggInOut.getDiscargeReason12()));
			}
			
//			   inout.setDiagHD(hd)
//			   DsrRule.setAutoCheckMainDiagnose(m_inout, rule.sugg_princ_diag, this.m_curMainDiag); // das zuerst, falls der Fall keine HD hat und hier eine gesetzt wird
//			   DsrRule.setAutoCheckDiagnose(m_inout, rule.sugg_diag, this.m_curDiags); // falls der Fall keine HD hat, und bei autoCheckMain keine gesetzt wurd, hier eine HD erzeugen und keine ND
			   //Prozeduren
//			   DsrRule.setAutoCheckProcedure(m_inout, rule.sugg_proc, this.m_curProcs);
			 

			//-----------------------------------------------------------------------
			//bringt keine Werte
			//m_grouper.performGroupForRule(rule.m_rid, "site\\rules\\2007\\table\\");
			//System.out.println(inout.getEffectiveCostWeight());

			m_grouper.performGroup();
//			inout.dumpCheckRecord();
//			suggInOut.performGroupValues(inout);
//			setDefaultGroupValuesIfNotDRG(suggInOut);
			//System.out.println(inout.getEffectiveCostWeight());
			return inout.getEffectiveCostWeight();
			//this.setDiagnosis(1, null);
		} catch(Exception ex) {
			throw createRuleException(rule, "error by perform suggestion", ex);
		}
	}
*/
	private int[] getDependIndexValues(CRGRuleElement ruleElement, CRGInputOutputBasic inout, CheckValue chkValue) throws Exception
	{
		switch(ruleElement.m_computeType) {
			case CRGRuleGrouperStatics.COMPUTE_TYPE_CRITERIUM: // das Kriterium steht alleine, z.B. Diagnose,  d.h. dass der m_valueType unwichtig ist!!
				return getDependIndexValues(ruleElement, ruleElement.m_criterionType, ruleElement.m_criterionIndex,
					inout, chkValue.dependIndex, true);

			case CRGRuleGrouperStatics.COMPUTE_TYPE_BOTH: { // das Kriterium steht nicht allein, z.B. Diagnose IN @ 'xyz', der Aufruf ist derselbe
				return getDependIndexValues(ruleElement, ruleElement.m_criterionType, ruleElement.m_criterionIndex,
					inout, chkValue.dependIndex, false);
			}
			default:
				return null;
		}
	}

	private int[] getDependIndexValues(CRGRuleElement ruleElement, int critType, int critIndex, CRGInputOutputBasic inout,
		int[] oldDependVals, boolean doNew) throws Exception
	{
		int[] ret = null;
		int arrLen = 0;
		switch(critType) {
			case CRGRuleGrouperStatics.DATATYPE_ARRAY_STRING: {
				if((ruleElement.m_arrayLength <= 0) && (ruleElement.m_strValue == null)) {
					return null;
				}
				if(inout.getArrayStringValue(critIndex) == null) {
					return null;
				}
				ret = getDependIndexStringValues(ruleElement, inout.getArrayStringValue(critIndex),
					  ruleElement.m_valueType, oldDependVals);
				arrLen = inout.getArrayStringValue(critIndex).length;
				break;
				/*
				  if((inout.getArrayStringValue(critIndex)==null)||(ruleElement.m_strArrayValue==null))
				 return null;
				  else
				 return getDependIndexStringValues(ruleElement, inout.getArrayStringValue(critIndex),  ruleElement.m_valueType, oldDependVals, stateArray);
				 */
			}
			case CRGRuleGrouperStatics.DATATYPE_ARRAY_DOUBLE:
				;
			case CRGRuleGrouperStatics.DATATYPE_ARRAY_INTEGER: {
				double[] values = fillCummulatedValuesForCrit(ruleElement, inout, critIndex);
				/*				return getDependIndexIntegerValues(ruleElement, values,
				  ruleElement.m_valueType, oldDependVals, stateArray);*/
				ret = getDependIndexDoubleValues(ruleElement, values,
					  ruleElement.m_valueType, oldDependVals);
				arrLen = inout.getArrayDoubleValue(critIndex).length;
				break;
			}
			case CRGRuleGrouperStatics.DATATYPE_ARRAY_DATE: {
				ret = getDependIndexLongValues(ruleElement, inout.getArrayLongValue(critIndex),
					  ruleElement.m_valueType, oldDependVals);
				arrLen = inout.getArrayLongValue(critIndex).length;
				break;
			}
			case CRGRuleGrouperStatics.DATATYPE_ARRAY_DAY_TIME: {
				ret = getDependIndexLongValues(ruleElement, inout.getArrayLongtimeValue(critIndex),
					  ruleElement.m_valueType, oldDependVals);
				arrLen = inout.getArrayLongtimeValue(critIndex).length;
				break;
			}
			default:
				return null;
		}
		if(ret == null && doNew) {
			ret = new int[arrLen];
		}

		return ret;
	}

	private boolean getDependState(int[] dependVals)
	{
		int sz = dependVals != null ? dependVals.length : 0;
		for(int i = 0; i < sz; i++) {
			if(dependVals[i] == -1) {
				return false;
			} else if(dependVals[i] > 0) {
				return true;
			}

		}
		return false;
	}

	private int[] getDependState(int[] dependVals, boolean[] stateArray)
	{
		int sz = dependVals != null ? dependVals.length : 0;
		int sa = stateArray != null ? stateArray.length : 0;
		sz = sa < sz ? sa : sz;
		for(int i = 0; i < sz; i++) {
			if(!stateArray[i]) {
				dependVals[i] = 0;
			}
		}
		return dependVals;
	}

	private int checkOldDependsVal(int[] oldDependVals, int index, int size, CRGRuleElement ruleEle)
	{
		//nun überprüfen, in der neuen Klasse CriterionDepend, nach dem Index suchen, dort den Abhängigen Index rausholen und überprüfen
		//und an der gleichen position, wenn abhängigindex > 0
		int indDepend = 0;
		for(int i = 0; i < ruleEle.m_criterionDepend.length; i++) {
			if(ruleEle.m_depend == ruleEle.m_criterionDepend[i].m_index) {
				indDepend = ruleEle.m_criterionDepend[i].m_indexDepend;
				break;
			}
		}
		if(indDepend == 0) {
			indDepend = ruleEle.m_depend;
		}
		if(size == -1) {
			return indDepend;
		} else {
			if(index < size) {
				if(oldDependVals[index] == indDepend) {
					return oldDependVals[index];
				} else {
					return 0;
				}
			} else {
				return 0;
			}
		}
	}

	private int[] getDependIndexDoubleValues(CRGRuleElement ruleElement, double[] values, int valType,
		int[] oldDependVals) throws Exception
	{
		switch(valType) {
			case CRGRuleGrouperStatics.DATATYPE_DOUBLE: {
				int oldSize = oldDependVals != null ? oldDependVals.length : -1;
				int[] dependVals = new int[values.length];
				for(int i = 0; i < values.length; i++) {
					if(values[i] == CRGInputOutputBasic.DEFAULT_DOUBLE_VALUE) {
						break;
					} else {
						if(ruleElement.m_operantType == CRGRuleGrouperStatics.OP_NO_OPERATION) {
							dependVals[i] = checkOldDependsVal(oldDependVals, i, oldSize, ruleElement);
						} else {
							if(checkRuleDoubleValue(values[i], ruleElement.m_doubleValue, ruleElement.m_operantType)) {
								dependVals[i] = checkOldDependsVal(oldDependVals, i, oldSize, ruleElement);
							} else {
								dependVals[i] = 0;
							}
						}
					}
				}
				return dependVals;
			}
			case CRGRuleGrouperStatics.DATATYPE_ARRAY_DOUBLE: {
				int oldSize = oldDependVals != null ? oldDependVals.length : -1;
				int[] dependVals = new int[values.length];
				for(int i = 0; i < values.length; i++) {
					if(values[i] == CRGInputOutputBasic.DEFAULT_DOUBLE_VALUE) {
						break;
					} else {
						if(checkRuleDoubleValue(values[i], ruleElement.m_doubleArrayValue, ruleElement.m_operantType)) {
							dependVals[i] = checkOldDependsVal(oldDependVals, i, oldSize, ruleElement);
						} else {
							dependVals[i] = 0;
						}
					}
				}
				return dependVals;
			}
			case CRGRuleGrouperStatics.DATATYPE_INTEGER: {
				int oldSize = oldDependVals != null ? oldDependVals.length : -1;
				int[] dependVals = new int[values.length];
				for(int i = 0; i < values.length; i++) {
					int vali = (int)values[i];
					if(vali == CRGInputOutputBasic.DEFAULT_INT_VALUE) {
						break;
					} else {
						if(ruleElement.m_operantType == CRGRuleGrouperStatics.OP_NO_OPERATION) {
							dependVals[i] = checkOldDependsVal(oldDependVals, i, oldSize, ruleElement);
						} else {
							if(checkRuleIntegerValue(vali, ruleElement.m_intValue, ruleElement.m_operantType)) {
								dependVals[i] = checkOldDependsVal(oldDependVals, i, oldSize, ruleElement);
							} else {
								dependVals[i] = 0;
							}
						}
					}
				}
				return dependVals;
			}
			case CRGRuleGrouperStatics.DATATYPE_ARRAY_INTEGER: {
				int oldSize = oldDependVals != null ? oldDependVals.length : -1;
				int[] dependVals = new int[values.length];
				for(int i = 0; i < values.length; i++) {
					int vali = (int)values[i];
					if(vali == CRGInputOutputBasic.DEFAULT_INT_VALUE) {
						break;
					} else {
						if(checkRuleIntegerValue(vali, ruleElement.m_intArrayValue, ruleElement.m_operantType)) {
							dependVals[i] = checkOldDependsVal(oldDependVals, i, oldSize, ruleElement);
						} else {
							dependVals[i] = 0;
						}
					}
				}
				return dependVals;
			}
			default:
				return null;
		}
	}

	private int[] getDependIndexLongValues(CRGRuleElement ruleElement, long[] values, int valType, int[] oldDependVals) throws Exception
	{
		switch(valType) {
			case CRGRuleGrouperStatics.DATATYPE_DATE: {
				int oldSize = oldDependVals != null ? oldDependVals.length : -1;
				int[] dependVals = new int[values.length];
				for(int i = 0; i < values.length; i++) {
					if(values[i] == CRGInputOutputBasic.DEFAULT_LONG_VALUE) {
						break;
					} else {
						if(ruleElement.m_operantType == CRGRuleGrouperStatics.OP_NO_OPERATION) {
							dependVals[i] = checkOldDependsVal(oldDependVals, i, oldSize, ruleElement);
						} else {
							if(checkRuleLongValue(values[i], ruleElement.m_longValue, ruleElement.m_operantType)) {
								dependVals[i] = checkOldDependsVal(oldDependVals, i, oldSize, ruleElement);
							} else {
								dependVals[i] = 0;
							}
						}
					}
				}
				return dependVals;
			}
			case CRGRuleGrouperStatics.DATATYPE_DAY_TIME: {
				int oldSize = oldDependVals != null ? oldDependVals.length : -1;
				int[] dependVals = new int[values.length];
				for(int i = 0; i < values.length; i++) {
					if(values[i] == CRGInputOutputBasic.DEFAULT_LONG_VALUE) {
						break;
					} else {
						if(ruleElement.m_operantType == CRGRuleGrouperStatics.OP_NO_OPERATION) {
							dependVals[i] = checkOldDependsVal(oldDependVals, i, oldSize, ruleElement);
						} else {
							if(checkRuleLongValue(values[i], ruleElement.m_longtimeValue, ruleElement.m_operantType)) {
								dependVals[i] = checkOldDependsVal(oldDependVals, i, oldSize, ruleElement);
							} else {
								dependVals[i] = 0;
							}
						}
					}
				}
				return dependVals;
			}
			case CRGRuleGrouperStatics.DATATYPE_ARRAY_DATE: {
				int oldSize = oldDependVals != null ? oldDependVals.length : -1;
				int[] dependVals = new int[values.length];
				for(int i = 0; i < values.length; i++) {
					if(values[i] == CRGInputOutputBasic.DEFAULT_LONG_VALUE) {
						break;
					} else {
						if(checkRuleLongValue(values[i], ruleElement.m_longArrayValue, ruleElement.m_operantType)) {
							dependVals[i] = checkOldDependsVal(oldDependVals, i, oldSize, ruleElement);
						} else {
							dependVals[i] = 0;
						}
					}
				}
				return dependVals;
			}
			case CRGRuleGrouperStatics.DATATYPE_ARRAY_DAY_TIME: {
				int oldSize = oldDependVals != null ? oldDependVals.length : -1;
				int[] dependVals = new int[values.length];
				for(int i = 0; i < values.length; i++) {
					if(values[i] == CRGInputOutputBasic.DEFAULT_LONG_VALUE) {
						break;
					} else {
						if(checkRuleLongValue(values[i], ruleElement.m_longtimeArrayValue, ruleElement.m_operantType)) {
							dependVals[i] = checkOldDependsVal(oldDependVals, i, oldSize, ruleElement);
						} else {
							dependVals[i] = 0;
						}
					}
				}
				return dependVals;
			}
			default:
				return null;
		}
	}

	private int[] getDependIndexIntegerValues(CRGRuleElement ruleElement, int[] values, int valType,
		int[] oldDependVals, boolean[] stateArray) throws Exception
	{
		switch(valType) {
			case CRGRuleGrouperStatics.DATATYPE_INTEGER: {
				int oldSize = oldDependVals != null ? oldDependVals.length : -1;
				int[] dependVals = new int[values.length];
				for(int i = 0; i < values.length; i++) {
					if(values[i] == CRGInputOutputBasic.DEFAULT_INT_VALUE) {
						break;
					} else {
						if(ruleElement.m_operantType == CRGRuleGrouperStatics.OP_NO_OPERATION) {
							dependVals[i] = checkOldDependsVal(oldDependVals, i, oldSize, ruleElement);
						} else {
							if(checkRuleIntegerValue(values[i], ruleElement.m_intValue, ruleElement.m_operantType)) {
								dependVals[i] = checkOldDependsVal(oldDependVals, i, oldSize, ruleElement);
							} else {
								dependVals[i] = 0;
							}
						}
					}
				}
				return dependVals;
			}
			case CRGRuleGrouperStatics.DATATYPE_ARRAY_INTEGER: {
				int oldSize = oldDependVals != null ? oldDependVals.length : -1;
				int[] dependVals = new int[values.length];
				for(int i = 0; i < values.length; i++) {
					if(values[i] == CRGInputOutputBasic.DEFAULT_INT_VALUE) {
						break;
					} else {
						if(checkRuleIntegerValue(values[i], ruleElement.m_intArrayValue, ruleElement.m_operantType)) {
							dependVals[i] = checkOldDependsVal(oldDependVals, i, oldSize, ruleElement);
						} else {
							dependVals[i] = 0;
						}
					}
				}
				return dependVals;
			}
			default:
				return null;
		}
	}

	private int[] getDependIndexStringValues(CRGRuleElement ruleElement, String[] values, int valType,
		int[] oldDependVals) throws Exception
	{
		//vom type Array nur, wenn isOperatorArray true ist
		//nun kann es sein, dass ein einzelner Wert existiert, aber auch nicht!!
		//wenn kein einzelner Wert existiert, muss nicht geprüft werden, einfach nur überall den entsprechenden Wert für die Abhängigkeit eintragen
		switch(valType) {
			case CRGRuleGrouperStatics.DATATYPE_STRING: {
				int oldSize = oldDependVals != null ? oldDependVals.length : -1;
				int[] dependVals = new int[values.length];
				for(int i = 0; i < values.length; i++) {
					if(ruleElement.m_operantType == CRGRuleGrouperStatics.OP_NO_OPERATION) {
						dependVals[i] = checkOldDependsVal(oldDependVals, i, oldSize, ruleElement);
					} else {
						if(checkRuleStringValue(values[i], ruleElement.m_strValue, ruleElement.m_operantType)) { //yyy, ruleElement.m_WildCard))
							dependVals[i] = checkOldDependsVal(oldDependVals, i, oldSize, ruleElement);
						} else {
							dependVals[i] = 0;
						}
					}
					//}
				}
				return dependVals;
			}
			case CRGRuleGrouperStatics.DATATYPE_ARRAY_STRING: {
				int oldSize = oldDependVals != null ? oldDependVals.length : -1;
				int[] dependVals = new int[values.length];
				if(ruleElement.m_arrayLength <= 0) {
					return null;
				}
				for(int i = 0; i < values.length; i++) {
					//if (values[i]==null)
					//	break;
					//else{
					if(checkRuleStringValue(values[i], ruleElement.m_strArrayValue, ruleElement.m_operantType,
						ruleElement.m_length)) { //yyy, ruleElement.m_WildCardArray))
						dependVals[i] = checkOldDependsVal(oldDependVals, i, oldSize, ruleElement);
					} else {
						dependVals[i] = 0;
					}
					//}
				}
				return dependVals;
			}
			default:
				return null;
		}
	}

	public static boolean compareStringValue(String value, String critValue) throws Exception
	{
//		if ((critValue!=null)&&(value!=null)){
		if(value == null) {
			return false;
		}
//		value = value.trim().toUpperCase();
//		value = value.replaceAll(" ", "");
//		critValue = critValue.trim().toUpperCase();
//		critValue = critValue.replaceAll(" ", "");
		int lgCrit = critValue.length();
		int lgVal = value.length();

		if(lgVal == 0 || lgCrit == 0) {
			return false;
		}
		int i = 0, j = 0;
		char charVal, charCrit;
		boolean placeholder;
		boolean repeatPlaceHolder = false;
		while(i < lgCrit) {
			placeholder = false;
			charCrit = critValue.charAt(i);
			switch(charCrit) {
				case '%': {
					if(i == (lgCrit - 1)) {
						return true;
					}
					i++;
					placeholder = true;
					break;
				}
				case '?': {
					j++;
					break;
				}
				default: {
					if(j < lgVal) {
						charVal = value.charAt(j);
						if(charVal != charCrit) {
							return false;
						}
						j++;
					} else {
						return false;
					}
				}
			}
			repeatPlaceHolder = false;
			// falls % wie ? benutzt wurde
			if(placeholder) {
				charCrit = critValue.charAt(i);
				if(value.length() > j + 1) {
					charVal = value.charAt(j + 1);
					if(charCrit == charVal) {
						j++;
					}
				} else {
					break;
				}
			} while(placeholder) {
				charCrit = critValue.charAt(i);
				while(j < lgVal) {
					charVal = value.charAt(j);
					if(charVal == charCrit) {
//						while(j < i) {
						j++;
						if(j >= lgVal) {
							//z.b. die Diagnose ist kürzer als der Wert auf den getestet wird
							//aber nur wenn auch i == lgCrit
							//z.b. E14.1 auf wert E1*.*1
							if(i == (lgCrit - 1)) {
								return true;
							} else {
								return false;
							}
						} else {
							int firstAfterPLaceHolder = i;
							while(i < lgCrit - 1) {
								i++;
								charVal = value.charAt(j);
								charCrit = critValue.charAt(i);
								if(charCrit == '%' || charCrit == '?') {
									placeholder = false;
									break;
								}
								if(charVal != charCrit) {
									i = firstAfterPLaceHolder;
									charCrit = critValue.charAt(i);
									break;
								}
								if(j < lgVal - 1) {
									j++;
								} else {
									placeholder = false;
									break;
								}
							}
//								break;
						}
//						}
						if(!placeholder) {
							break;
						}
					}
					j++;
					if(j == lgVal) {
						break;
					}
				}
				if(j == lgVal) {
					return false;
				} else {
					j++;
				}
			}

			i++;
		}
		if(((critValue.charAt(lgCrit - 1) == '%') && (i == lgCrit))
			|| (j == lgVal)) {
			return true;
		} else {
			return false;
		}
//		} else{
//			System.out.println("critvalue="+critValue+"----value="+value);
//			return false;
//		}
	}

	/**
	 * Gibt alle Ein- und Ausgabe-Parameter des Objektes CRGInputOutputBasic zurück.
	 * @return String : Ein- und Ausgabe-Parameter als Text
	 */
	public String dumpInputOutputValues()
	{
		return this.m_inout.dumpRecord().toString();
	}

	private static CRGRuleGroupException createRuleException(CRGRule rule, String spezialText, Throwable ex)
	{
		String tt = "error in rule id: " + rule.m_rid +
					"\n\tnumber: " + rule.m_number +
					"\n\tidentifier: " + rule.getRuleIdentifier() +
					"\n\tcaption: " + rule.m_caption +
					"\n\tText: " + rule.m_ruleText;
		if(spezialText != null && spezialText.length() > 0) {
			tt = spezialText + "\n" + tt;
		}
		return new CRGRuleGroupException(tt, ex);
	}

	private void setRefValues(int ind, String s, int critIndex, int ind1)
	{
		try {
			if(!checkRefs(critIndex)) {
				return;
			}
			if(s == null || s.length() == 0) {
				return;
			}
			m_RefWert[m_RefZaehler] = s;
			m_RefIndex2[m_RefZaehler] = critIndex;
			if(ind1 != -999) {
				m_RefIndex1[m_RefZaehler] = ind1;
			}
		} catch(IndexOutOfBoundsException e) {
			String[] refWert = new String[m_RefWert.length + MIN_ARRAY_LENGTH];
			int[] refInd1 = new int[m_RefIndex1.length + MIN_ARRAY_LENGTH];
			int[] refInd2 = new int[m_RefIndex2.length + MIN_ARRAY_LENGTH];

			System.arraycopy(m_RefWert, 0, refWert, 0, m_RefWert.length);
			System.arraycopy(m_RefIndex1, 0, refInd1, 0, m_RefIndex1.length);
			System.arraycopy(m_RefIndex2, 0, refInd2, 0, m_RefIndex2.length);
			m_RefWert = refWert;
			m_RefIndex1 = refInd1;
			m_RefIndex2 = refInd2;
			setRefValues(ind, s, critIndex, ind1);
		}
	}

	/**
	 * begrenzt speicherung der Referenzen nur auf die, die danach benutzt werden
	 * @param critIndex int
	 * @return boolean
	 */
	private boolean checkRefs(int critIndex)
	{
//		return critIndex == CRGRuleGrouperStatics.CRITSTR_INDEX_MAINDIAG
//			|| critIndex == CRGRuleGrouperStatics.CRITARRAYSTRING_INDEX_DIAG
//			|| critIndex == CRGRuleGrouperStatics.CRITARRAYSTRING_INDEX_DIAG_AUX
//			|| critIndex == CRGRuleGrouperStatics.CRITARRAYSTRING_INDEX_PROC
//			|| critIndex == CRGRuleGrouperStatics.CRITARRAYSTRING_INDEX_ENTGELTE
//			|| critIndex == CRGRuleGrouperStatics.CRITARRAYSTRING_INDEX_ATC
//			|| critIndex == CRGRuleGrouperStatics.CRITARRAYSTRING_INDEX_PZN
//			|| critIndex == CRGRuleGrouperStatics.CRITARRAYSTRING_INDEX_SOLE_PZN
//			|| critIndex == CRGRuleGrouperStatics.CRITARRAYSTRING_INDEX_REMEDIES_HE_NUMBER
//			|| critIndex == CRGRuleGrouperStatics.CRITARRAYSTRING_INDEX_PSYCH_OPS_CODE
//			|| critIndex == CRGRuleGrouperStatics.CRITARRAYSTRING_INDEX_VPS_ERR_NUMBER
//                        || critIndex == CRGRuleGrouperStatics.CRITARRAYSTRING_INDEX_CLINIC_CASE_NR; 
            return false;
	}

//
//	/**
//	 * Setzt Ein- und Ausgabe Objekt für vorangegangene klinische Fälle (fallübergreifende Prüfung)
//	 * @param inout RulerInputObject
//	 * @param i int - Index des Falles
//	 * @return CRGInputOutputBasic - Ein- und Ausgabe Objekt
//	 */
//	public CRGInputOutputBasic setFormerInout(RulerInputObject inout, int i)
//	{
//		try {
//			if(m_formerInOuts[i] == null) {
//				m_formerInOuts[i] = new CRGInputOutputBasic(inout);
//			}
//			m_formerInOuts[i].newCase();
//			m_formerInOuts[i].m_inout.newCase();
//			return m_formerInOuts[i];
//		} catch(ArrayIndexOutOfBoundsException ex) {
//			extendFormerInOutsAndSorts();
//			return setFormerInout(inout, i);
//		}
//	}

	private void extendFormerInOutsAndSorts()
	{
		CRGInputOutputBasic[] newFormers = new CRGInputOutputBasic[m_formerInOuts.length + m_formerMax];
		System.arraycopy(this.m_formerInOuts, 0, newFormers, 0, m_formerInOuts.length);
		m_formerInOuts = newFormers;
		int[] newSorts = new int[m_formerInOuts.length];
		System.arraycopy(this.m_formerSort, 0, newSorts, 0, m_formerSort.length);
		m_formerSort = newSorts;
	}

	/**
	 * Gibt Ein- und Ausgabe Objekte für vorangegangene klinische Fälle (fallübergreifende Prüfung) zurück
	 * @return CRGInputOutputBasic[]
	 */
	public CRGInputOutputBasic[] getFormerInputoutput()
	{
		return m_formerInOuts;
	}

	/**
	 * Gibt Ein- und Ausgabe Objekt für vorangegangene klinische Fälle (fallübergreifende Prüfung) zurück
	 * @param i int - Index des Falles
	 * @return CRGInputOutputBasic
	 */
	public CRGInputOutputBasic getFormerInout(int i)
	{
		return m_formerInOuts[i];
	}

	public void resetFormerCases()
	{
//		if(m_formerInOuts != null) {
//			for(int i = 0; i < m_formerInOuts.length; i++) {
//				if(m_formerInOuts[i] != null && m_formerInOuts[i].m_inout != null) {
//					m_formerInOuts[i].m_inout.finalize();
//				}
//
//			}
//		}
	}

	/**
	 * Anzahl die maximal mögliche Anzahl von vorangegangenen klinischen Fälle an
	 * @return int
	 */
	public static int getFormerMax()
	{
		return m_formerMax;
	}


	/**
	 * Gibt die Anzahl der übergebenen vorrangegangenen Fällen an
	 * @return int
	 */
	public int getFormerCount()
	{
		return m_formerCount;
	}

	/**
	 * Setzt den Index auf den aktuellen vorrangegangenen Fall
	 * @param i int
	 */
	public void setFormerCount(int i)
	{
		m_formerCount = i;
	}

	/**
	 * Bitte nicht verwenden! - Interner Methodenaufruf.
	 * @param i int
	 * @param j int
	 */
	public void setFormerSort(int i, int j)
	{
		m_formerSort[i] = j;
	}

	/**
	 * Bitte nicht verwenden! - Interner Methodenaufruf.
	 * @param i int
	 */
	public void setFormerAkt(int i)
	{
		m_formerAkt = i;
	}

	/**
	 * Bitte nicht verwenden! - Interner Methodenaufruf.
	 * @return int[]
	 */
	public int[] getFormerSort()
	{
		return m_formerSort;
	}

	/**
	 * Bitte nicht verwenden! - Interner Methodenaufruf.
	 * @return int
	 */
	public int getFormerAkt()
	{
		return m_formerAkt;
	}

	/**
	 * Setzt alle Werte der letzten Prüfung zurück und gibt ein Ein- und Ausgabe Objekt zurück
	 * @throws Exception
	 */
	public CRGInputOutputBasic newCaseAll() throws Exception
	{
		/*		for (int i=0; i<m_formerCount; i++){
		   this.m_formerInOuts[i] = null;
		  }*/
		m_formerCount = 0;
		/*		for (int i=0; i<m_pastCount; i++){
		   this.m_pastInOuts[i] = null;
		  }*/
		resetFormerCases();

		m_formerInOuts = new CRGInputOutputBasic[m_formerMax];
		this.m_formerSort = new int[m_formerMax];
//		m_inout.newCase();

		m_inout = new CRGInputOutputBasic();
		return m_inout;
	}

	/**
	 * fallübergreifend
	 * Führt das native DRG-Groupen durch und
	 * setzt die ermittelten Werte in das CRGInputOutputBasic Objekt
	 * @return CRGInputOutputBasic
	 * @throws Exception
	 * Führt das Groupen eines Falles durch und
	 * ermittelt zusätzlich für jede Nebendiagnose eine DRG,
	 * indem diese als Hauptdiagnose gesetzt wird.
	 * Danach wird die Regelprüfung gemacht.
	 * @return CRGRule[] : angeschlagene Regeln
	 * @throws Exception
	 */
	/*	public CRGRule[] performGroupSimulateAndCheckCC() throws Exception
	 {
	  performGroupSimulate();
	  return performCheckRefCC();
	 }
	 */
	/**
	 * fallübergreifend
	 * Einzel- und Batchermittelung der anschlagenden Regeln
	 * aus Performance - Gründen strikt trennen
	 * @param isBatch boolean : Einzel(false)- oder Batchermittelung(true)
	 * @return CRGRule : angeschlagene Regeln
	 * @throws Exception
	 */
	/*	public CRGRule[] performGroupAndCheckCC(boolean isBatch) throws Exception
	 {
	  performGroup();
	  if(isBatch) {
	   return performCheckCC();
	  } else {
	   return performCheckRefCC();
	  }
	 }
	 */
	/**
	 * fallübergreifend
	 * Führt die Regelprüfung durch für batchgrouping
	 * @return CRGRule[] : Array mit allen Werten der angeschlagenen Regeln
	 * @throws Exception
	 */
	/*	public CRGRule[] performCheckCC() throws Exception
	 {
	  prepareRulesForInout();
	  int[] iRule = new int[m_ruleSize];
	  int count = -1;
	  m_RefRule = -1;
	  for(int i = 0; i < m_ruleSize; i++) {
	   if(checkRuleCC(m_rules[i])) {
		count++;
		iRule[count] = i;
	   }
	  }
	  CRGRule[] rule = new CRGRule[count + 1];
	  for(int i = 0; i <= count; i++) {
	   rule[i] = m_rules[iRule[i]];
	  }
	  return rule;
	 }
	 */
	/**
	 * fallübergreifend
	 * Führt die Regelprüfung durch für einzelgrouping
	 * Für die angeschlagenen Regeln werden die Referenzen auf die
	 * regelrelevanten Hauptdiagnosen, Diagnosen, Nebendiagnosen, Prozeduren gesammelt,
	 * die über die Methode setCodeReferences abgefragt werden.
	 * @return CRGRule[] : Array mit allen Werten der angeschlagenen Regeln
	 * @throws Exception
	 */
	/*	private CRGRule[] performCheckRefCC() throws Exception
	 {
	  prepareRulesForInout();
	  int[] iRule = new int[m_ruleSize];
	  int count = -1;
	  m_RefRule = -1;
	  for(int i = 0; i < m_ruleSize; i++) {
	   if(checkRuleRefCC(m_rules[i])) {
		count++;
		iRule[count] = i;
	   }
	  }
	  CRGRule[] rule = new CRGRule[count + 1];
	  for(int i = 0; i <= count; i++) {
	   rule[i] = m_rules[iRule[i]];
	  }
	  return rule;
	 }
	 */
	/**
	 * fallübergreifend
	 * sortiert alle übergebenen Fälle nach Aufnahmedatum aufsteigend
	 */
	public void sortFormerCasesWithAdmissionDate()
	{
//		int fallCount = getFormerCount();
//		long l = 0;
//		long[] arrL = new long[m_formerSort.length];
//		boolean b = false;
//		if(fallCount > 0) {
//			arrL[0] = getFormerInout(0).m_admissionDate;
//			setFormerSort(0, 0);
//		}
//		for(int i = 1; i < fallCount; i++) {
//			l = getFormerInout(i).m_admissionDate;
//			b = false;
//			for(int j = 0; j < i; j++) {
//				if(l < arrL[j]) {
//					for(int i1 = i; i1 > j; i1--) {
//						arrL[i1] = arrL[i1 - 1];
//						setFormerSort(i1, m_formerSort[i1 - 1]);
//					}
//					arrL[j] = l;
//					setFormerSort(j, i);
//					b = true;
//					break;
//				}
//			}
//			if(!b) {
//				arrL[i] = l;
//				setFormerSort(i, i);
//			}
//		}
	}

//	/**
//	 * fallübergreifend
//	 * groupt alle übergebenen Fälle
//	 * @param all boolean - wenn true, wird auch der aktueller Fall gegroupt
//	 * @throws Exception
//	 */
//	public void groupFormerCases(boolean all, boolean simulate) throws Exception
//	{
//		int fallCount = getFormerCount();
//		for(int i = 0; i < fallCount; i++) {
//			if(all || i != getFormerAkt()) {
//				//Jetzt den Fall zum Groupen in das aktuelle InputOutput-Object schieben
//				//---------------------------------------
//				//und jetzt alle Werte hineinkopieren aus:
//				m_inout.newCase();
////				m_inout.copyCase(getFormerInout(i));
////				m_inout.m_inout.copyCaseAll(getFormerInout_Inout(i));
//				m_grouper.setInoutFormOtherInout(getFormerInout(i), getFormerInout_Inout(i));
//				// nur bei aktuellem Fall Simulate
//				if(simulate) {
//					performGroupSimulate();
//				} else {
//					this.performGroup(false);
////					setDefaultGroupValuesIfNotDRG(m_inout);
//				}
//				//m_ruleGrouper.getFormerInout_Inout(i).copyCaseAll(m_inout);eigentlich nicht notwendig
//				getFormerInout(i).newCase();
//				getFormerInout(i).m_inout.newCase();
//				getFormerInout(i).copyCase(m_grouper.getCRGInputObject());
//				getFormerInout(i).m_inout.copyCaseAll(m_grouper.getInputObject());
//			}
//		}
//	}
//
	public void setActualCaseFromFormerCase(int i) throws Exception
	{
		if(i < this.getFormerCount()) {
			m_inout.copyCase(this.getFormerInout(i));
		}
	}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	public boolean resetRuleBase()
	{
		resetRulesArrays();
		return true;
	}

	public void setM_isCrossCase(boolean m_isCrossCase)
	{
	  /* 3.9.5 2015-09-01 DNi: #FINDBUGS - Zuweisung fehlerhaft, wahrscheinlich ist this.m_isCrossCase = m_isCrossCase; gemeint! */
		this.m_isCrossCase = m_isCrossCase;
		m_isGK = m_isCrossCase;
	}

	public void setM_setCheckDateFromAdmDate(boolean setCheckDateFromAdmDate)
	{
		this.m_setCheckDateFromAdmDate = setCheckDateFromAdmDate;
	}


//
//        /**
//         * aus der Liste der Fälle zu einem Patienten werden die Fälle ohne Schlußrechnung rausgenommen,
//         * die nicht in laufenden Import importiert wurden
//     * @return die Liste der gebliebenen caseIds
//     * @throws java.lang.Exception
//         **/
//    public ArrayList<Integer> removeNotCompletedCasesFromCaseList() throws Exception {
//        ArrayList<Integer>used = new ArrayList<>();
//       CRGInputOutputBasic[] formerInOuts = new CRGInputOutputBasic[m_formerMax];
//       int[] formerSort = new int[m_formerMax];
//       int count = 0;
//       for(int i = 0; i < m_formerCount; i++){
//           CRGInputOutputBasic inout = m_formerInOuts[m_formerSort[i]];
//           if(inout.getCheckMark() == 1 
//                   || (inout.getCheckMark() < 0 &&( // vor- und Nachfälle haben kein Checkmark = 2;
//                    inout.getFeeGroupAsLong() >= 1 
//                        && inout.getFeeGroupAsLong() <= 12
//                   || inout.getFeeGroupAsLong() >= 34 
//                        && inout.getFeeGroupAsLong() <= 38) )){
//               formerInOuts[count] = inout;
//               formerSort[count] = count;
//               used.add(inout.getCaseId());
//               count++;
//           }
//       }
//       m_formerInOuts = formerInOuts;
//       m_formerSort = formerSort;
//       m_formerCount = count;
//       return used;
//    }
    
    public boolean hasHistoryRules4Year(int pYear){
        Map<Integer, Boolean> type2value = mYear2RypeTypes.get(pYear);
        if(type2value == null || type2value.isEmpty()){
            return false;
        }
        Boolean ret = type2value.get(CRGRule.CHECK_KH_ONLY);
        return ret == null?false:ret;
    }
    

    private void checkRuleUsage(CRGRule rule, int ruleYear) {
//	public static final int CHECK_CP_ONLY = 1; basis kriterien
//	public static final int CHECK_MRSA_ONLY = 2; rsa
//	public static final int CHECK_ACG_ONLY = 3; acg
//	public static final int CHECK_AMBU_MED_ONLY = 4; medi
//	public static final int CHECK_KH_ONLY = 5; fallübergreifende
//	public static final int CHECK_INS_ONLY = 6; versicherung
//	public static final int CHECK_GK = 7; GK
//	public static final int CHECK_AMBU_SOLE_ONLY = 8; sole
//	public static final int CHECK_GK_RSA_ONLY = 9;
//	public static final int CHECK_CP_MED_ONLY = 10; med
//	public static final int CHECK_AMBU_CARE_ONLY = 11;
// erstmal uns ineressiert CHECK_KH_ONLY
        Map<Integer, Boolean> type2value = mYear2RypeTypes.get(ruleYear);
        if(type2value == null){
            type2value = new HashMap<>();
            mYear2RypeTypes.put(ruleYear, type2value);
        }
        check4type(CRGRule.CHECK_CP_ONLY, rule, type2value);
        check4type(CRGRule.CHECK_KH_ONLY, rule, type2value);
        check4type(CRGRule.CHECK_ACG_ONLY, rule, type2value);
        check4type(CRGRule.CHECK_AMBU_SOLE_ONLY, rule, type2value);
        check4type(CRGRule.CHECK_AMBU_MED_ONLY, rule, type2value);
        check4type(CRGRule.CHECK_CP_MED_ONLY, rule, type2value);
        check4type(CRGRule.CHECK_AMBU_CARE_ONLY, rule, type2value);
    }
    
    private void check4type(int checkType, CRGRule rule, Map<Integer, Boolean> type2value){
       Boolean check =  type2value.get(checkType);
       if(check!= null && check){
           return;
       }
       if((check == null || ! check) && rule.hasType(checkType)) {
           type2value.put(checkType, true);
       }
        
    }

    public CRGRule[][] getRules() {
        return this.m_allRulesWithYears;
    }


}
