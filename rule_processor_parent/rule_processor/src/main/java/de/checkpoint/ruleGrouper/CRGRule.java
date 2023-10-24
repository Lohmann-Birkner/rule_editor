package de.checkpoint.ruleGrouper;



import de.checkpoint.drg.RuleAttributes;
import de.checkpoint.installer.IAActions.CommonOperations; 
import de.checkpoint.ruleGrouper.data.CRGRuleTypes;
import de.checkpoint.ruleGrouper.ue.CRGRuleUEMessage;
import de.checkpoint.server.appServer.AppResources;
import de.checkpoint.server.data.caseRules.DatCaseRuleAttributes;
import de.checkpoint.server.data.caseRules.DatCaseRuleConstants;
import de.checkpoint.server.data.caseRules.DatInterval;
import de.checkpoint.server.data.caseRules.DatRuleElement;
import de.checkpoint.server.data.caseRules.DatRuleOp;
import de.checkpoint.server.data.caseRules.DatRuleSuggestion;
import de.checkpoint.server.data.caseRules.DatRuleTerm;
import de.checkpoint.server.data.caseRules.DatRuleVal;
import de.checkpoint.server.data.caseRules.DatRulesAction;
import de.checkpoint.server.data.caseRules.DsrRule;
import static de.checkpoint.server.data.caseRules.DsrRule.getXMLText;
import de.checkpoint.server.db.OSObject;
import de.checkpoint.server.exceptions.ExcException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Vector;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import java.util.ArrayList;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * <p>Überschrift: Checkpoint DRG</p>
 *
 * <p>Beschreibung: CRGRule</p>
 *
 * <p>Copyright: Lohmann & Birkner Health Care Consulting GmbH </p>
 *
 * <p>Organisation: Lohmann & Birkner Health Care Consulting GmbH </p>
 *
 * @author unbekannt
 * @version 2.0
 */
public class CRGRule extends DsrRule
{
	public static final int CHECK_NOTHING = 0;
	public static final int CHECK_CP_ONLY = 1;
	public static final int CHECK_MRSA_ONLY = 2;
	public static final int CHECK_ACG_ONLY = 3;
	public static final int CHECK_AMBU_MED_ONLY = 4;
	public static final int CHECK_KH_ONLY = 5;
	public static final int CHECK_INS_ONLY = 6;
	public static final int CHECK_GK = 7;
	public static final int CHECK_AMBU_SOLE_ONLY = 8;
	public static final int CHECK_GK_RSA_ONLY = 9;
	public static final int CHECK_CP_MED_ONLY = 10;
	public static final int CHECK_AMBU_CARE_ONLY = 11;
	public static final int CHECK_NEEDS_GROUPING = 12;
	public static final int CHECK_ALL = 99;
	public static final int RULE_ANALYSE_FULL = 1;
	public static final int RULE_ANALYSE_NO_TABLES = 2;

	protected CRGRuleElement m_ruleElement = null;
	//hier ist davon auszugehen, dass das Format für Zeit: hhmm und für Datum yyyyMMdd ist
	private static SimpleDateFormat m_simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
	private static SimpleDateFormat m_simpleTimeFormat = new SimpleDateFormat(AppResources.DATEFORMAT_TIME);
	private static SimpleDateFormat m_simpleDateAndTimeFormat = new SimpleDateFormat("yyyyMMdd HH:mm", Locale.GERMAN);

	protected String m_ruleIdentififier;
	List m_types = null;
	protected CRGRuleTypes m_errorType = null;

	CRGSuggestion[] m_suggestions = null;
	private int m_suggestionLength = -1;

	private int m_fullRuleAnalysis = -1;
    // temporäre Abhilfe um den Zugriff auf einigen Klassen zu vermeiden bei CheckpointGrouper ohne CheckpoinDRG
    /// solange c- grouper benutzt wird, ist es kein Bedarf die Regeln füe den Javagrouper zu vorbereiten
	private String m_poolIdent = "";
        private boolean m_checkTables = true;
	private Date m_checkDate = null;
	private CRGRuleManager m_mgr = null;


	private Vector m_ueMessages = null;

	private boolean m_hasCPCrit = false; // flag auf true wenn in der regel kommen einige Kriterien aus der Einzelfall - Supergruppe vor
	private boolean m_hasCPMedCrit = false; // flag auf true wenn in der regel kommen einige Kriterien aus der Einzelfall - Supergruppe/Medikament vor
	private boolean m_hasRSACrit = false; // flag auf true wenn in der regel kommen einige Kriterien aus der MRSA - Supergruppe vor
	private boolean m_hasACGCrit = false; // flag auf true wenn in der regel kommen einige Kriterien aus der ACG - Supergruppe vor
	private boolean m_hasAMBUMedCrit = false; // flag auf true wenn in der regel kommen einige Kriterien aus der ambulante Daten - Supergruppe vor
	private boolean m_hasKHCrit = false; // flag auf true wenn in der regel kommen einige Kriterien aus der Krankenhausdaten - Supergruppe vor
	private boolean m_hasInsCrit = false; // flag auf true wenn in der regel kommen einige Kriterien aus der Versicherten Stammdaten - Supergruppe vor
	private boolean m_hasAMBUSoLeCrit = false; // flag auf true wenn in der regel kommen einige Kriterien SoLe - Supergruppe vor
	private boolean m_hasGKRsaCrit = false; // flag auf true wenn in der regel kommen einige Kriterien aus der Versicherten Stammdaten - Supergruppe vor
	private boolean m_hasCareCrit = false;
	private boolean m_needGroupResults = false;
	private CRGIntervalEntry m_rulesInterval = null;
        private Date m_admDate_backup = null;
        private Date m_disDate_backup = null;
        private int m_year_backup = 0;
	public long[] m_feeGroups = null;

	private boolean isTest = false;
        private List<CRGRisk> m_risks = new ArrayList<>(); 
        private List<DatRuleTerm> m_allSimpleTerms = new ArrayList<>();
        private byte[] errorMessage = null;
        
    public void setFullRuleAnalysis(int m_fullRuleAnalysis) {
        this.m_fullRuleAnalysis = m_fullRuleAnalysis;
    }


    public void setCheckTables(boolean m_checkTables) {
        this.m_checkTables = m_checkTables;
    }

	/**
	 * Konstruktor
	 */
	public CRGRule()
	{
		this(null, 0);
	}

	/**
	 * dieser Konstruktor soll nur für Mergeaktion benutzt werden;
	 * der Element wird nicht analystiert, wird nur m_rid interessant, damit
	 * equals - Methode angewedet werden kann
	 *
	 * @param ele Element
	 */
	public CRGRule(Element ele)
	{
//		m_element = ele;
		if(ele != null) {
			m_rid = ele.getAttribute(DatCaseRuleAttributes.ATT_RULES_ID);
		}
		m_fullRuleAnalysis = 0;
	}

	/**
	 * Konstruktor
	 * @param year int : Jahr
	 */
	public CRGRule(int year)
	{
		this(null, year);
	}

	/**
	 * Konstruktor
	 * @param ele Element : Root-Element der Regel
	 * @param year int : Jahr
	 */
	protected CRGRule(Element ele, int year)
	{
		super("", false);
		m_year = year;
		init(ele, null);
	}

	/**
	 * Konstruktor
	 * @param ele Element : Root-Element der Regel
	 * @param ident String : Regelidentifier
	 * @param year int : Jahr
	 */
	protected CRGRule(Element ele, String ident, int year)
	{
		super("", false);
		m_ruleIdentififier = ident;
		m_year = year;
//		m_element = ele;
		setBasics(ele, null);
	}

//	protected CRGRule(Element ele, String ident, int year, List ruleTypes, int analyseGrad, CRGRuleManager 	mgr)
//	{
//		super("", false);
//		m_ruleIdentififier = ident;
//		m_year = year;
//		m_fullRuleAnalysis = analyseGrad;
////		m_element = ele;
//		m_mgr = mgr;
//		init(ele, ruleTypes);
//	}

	/**
	 * Konstruktor: Wird von Regelparser verwendet.
	 * @param ele Element : Root-Element der Regel
	 * @param identifier String : Regelidentifier
	 * @param ruleTypes List : Regeltypen
	 */
	public CRGRule(Element ele, String identifier, List ruleTypes)
	{
		super("", false);
		m_ruleIdentififier = identifier;
		init(ele, ruleTypes);
	}

	/**
	 * Konstruktor: Wird von Regelparser verwendet.
	 * @param ele Element : Root-Element der Regel
	 * @param identifier String : Regelidentifier
	 * @param ruleTypes List : Regeltypen
	 * @param analysisGrade: setzt den Flag  der bestimmt, ob die Regel
	 * zum Regelauswertung oder nur zur Regelanzeige vorbereitet werden soll
         * 2 - ohne tabellenauswertung
	 * 1 - voll
	 * 0 - nur für die Regeldarstellung
	 */
	public CRGRule(Element ele, String identifier, List ruleTypes, int analysisGrade)
	{
		super();
		m_ruleIdentififier = identifier;
		m_fullRuleAnalysis = analysisGrade;
		m_isCheckpoint = false;
                m_checkTables = analysisGrade != RULE_ANALYSE_NO_TABLES;
		init(ele, ruleTypes);
	}

	/**
	 * Konstruktor: wird von Regelparser verwendet.
	 * @param ele Element : Root-Element der Regel
	 * @param identifier String : Regelidentifier
	 * @param ruleTypes List : Regeltypen
	 * @param checkDate Date : Prüfdatum
	 */
	public CRGRule(Element ele, String identifier, int year, List ruleTypes, Date checkDate)
	{
		super("", false);
		m_ruleIdentififier = identifier;
		m_checkDate = checkDate;
		init(ele, ruleTypes);
	}

	/**
	 *
	 * @param ele Element : Root-Element der Regel
	 * @param ruleTypes List : Liste der Regeltypen
	 */
    @Override
	protected void init(Element ele, List ruleTypes)
	{

		m_types = ruleTypes;
		if(ele != null) {
			m_rid = ele.getAttribute(DatCaseRuleAttributes.ATT_RULES_ID);
			//if(m_rid.equals("92006"))
			//	System.out.println("check");
			setValuesByElement(ele, ruleTypes);
		}
	}

	/**
	 * Erzeugt das Regel-Objekt aus dem Root-Element der Regel: wird von Regelparser verwendet.
	 * @param ele Element : Root-Element der Regel
	 * @param identifier String : Regelidentifier
	 */
    @Override
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
                        setRisks(ele);
			setFeeGroups(ele);
			setRolesVisible(ele);
                        createSimpleTermList();
		}
	}

	/**
	 *
	 * @param ele Element : Root-Element der Regel
	 * @param ruleTypes List : Liste der Regeltypen
	 */
    @Override
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
                            // bei nicht DAK - Regeln wird dieser Feld immer ller, also die java.lang.NumberFormatException
                            // muss hier unterdruckt werden
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
			if(m_isCheckpoint) {
				if(ruleTypes == null) {
					m_errorType = getRuleManager().getRulesErrorTypeByText(m_errorTypeText);
				} else {
					m_errorType = getRuleManager().getRulesErrorTypeByText(m_errorTypeText, ruleTypes);
				}
			}
		} catch(Exception ex) {
			ExcException.createException(ex);
		}
	}

	protected CRGRuleManager getRuleManager() throws Exception
	{

		if(m_mgr == null) {
            m_mgr = CRGRuleManager.ruleManager();
        }
		return m_mgr;
	}

	private void setFeeGroups(Element ele)
	{
		String fg = ele.getAttribute(DatCaseRuleAttributes.ATT_RULES_FEE_GROUPS);
		this.m_feeGroups = CommonOperations.getLongArrayFromString(fg, ",");

	}

    @Override
	protected void getFeeGroups(Element ele)
	{
		try {
			ele.setAttribute(DatCaseRuleAttributes.ATT_RULES_FEE_GROUPS, getStringFromLongArray(m_feeGroups));
		} catch(Exception ex) {
			ExcException.createException(ex);
		}
	}

	public boolean sameFeeGroups(long[] fgs)
	{

		return compareRoleArrays(m_feeGroups, fgs);
	}

	/**
	 *
	 * @param element DatRuleElement
	 * @throws Exception
	 */
    @Override
	protected void setRuleDefinition(DatRuleElement element) throws Exception
	{
		if(element != null) {
			try {
				//if ((m_rid.equals("1562005"))) //313=7742008,310=5012007,212=3642007,304=4952007,302=4922007,uGVD=7662008,KK002=6662008,214=6482008,md255=7712008,md211=5412008,4912007,300=7692008,301=7702008 022=5622008  049=5912008 Med12=7752008
				//	System.out.println("check");
				m_ruleElement = getRuleElement(null, element); // um die regeltyps zu ermetteln
				if(isTest) {
					System.out.println(toString());
				}

				if(!this.isServer()) {
					m_ruleElement = null;
				}
			} catch(CRGRuleGroupException ex) {
				m_ruleElement = null;
				this.m_isCorrupt = true;
				throw ex;
			} catch(Exception ex) {
				m_ruleElement = null;
				this.m_isCorrupt = true;
				throw this.createRuleException("error by setting rule definition", ex);
			}
		} else {

		}
	}

	/**
	 * Gibt das Root-Element der Regeldefinition zurück : wird vom Regelparser verwendet.
	 * @return CRGRuleElement : Root-Element der Regeldefinition
	 */
	public CRGRuleElement getRuleElement()
	{
		return m_ruleElement;
	}

	/**
	 *
	 * @param parent CRGRuleElement
	 * @param element DatRuleElement
	 * @return CRGRuleElement
	 * @throws Exception
	 */
	private CRGRuleElement getRuleElement(CRGRuleElement parent, DatRuleElement element) throws Exception
	{
		CRGRuleElement ruleEle = new CRGRuleElement(parent);
		ruleEle.m_type = CRGRuleGrouperStatics.TYPE_ELEMENT;
		ruleEle.m_isNested = element.hasNestedElements();
		ruleEle.m_isNot = element.m_isNot;
                ruleEle.m_mark = element.m_mark;

//		if(!isTest)
//			addChildRuleElements(element.getElements(), ruleEle, parent);
///		else
			addChildRuleElementsNew(element.getElements(), ruleEle, parent, 1);
		return ruleEle;
	}

	private void addChildRuleElementsNew(Vector v, CRGRuleElement ruleEle, CRGRuleElement parent,
		int range) throws Exception
	{
		try {
			int n = v != null ? v.size() : 0;
			ruleEle.m_childCount = 0;
			if(n > 3) {
				Vector childElements = doNested(v, ruleEle, parent, range);

				if(ruleEle.m_childElements == null){
					ruleEle.m_childElements = new CRGRuleElement[childElements.size()];
					childElements.toArray(ruleEle.m_childElements);
					if(parent != null) {
						parent.m_isNested = parent.m_isNested || (ruleEle.m_childElements.length < v.size());
					}
					ruleEle.m_childCount = ruleEle.m_childElements.length;
					ruleEle.m_hasChilds = (ruleEle.m_childCount > 0);
					if(ruleEle.m_hasChilds) {
                                            ruleEle.m_firstChildType = ruleEle.m_childElements[0].m_criterionType;
                                        }
				}
			} else {
				doOneTerm(ruleEle, v, parent);
			}
		} catch(CRGRuleGroupException ex) {
			throw ex;
		} catch(Exception ex) {
			throw this.createRuleException("error by appanding rule child", ex);
		}
	}

	private Vector doNested(Vector v, CRGRuleElement ruleEle, CRGRuleElement parent,
		int operationRange) throws Exception
	{
		Vector childElements = new Vector();
		int n = v != null ? v.size() : 0;
		if(n > 3) {
			int start = 0;
			for(int i = 0; i < n; i++) {
				Object obj = v.get(i);
/*				if(obj instanceof DatRuleElement) {
					childElements.addElement(getRuleElement(ruleEle, (DatRuleElement)obj));
					if(i + 2 < n && v.get(i + 1) instanceof DatRuleOp) {
						childElements.addElement(getRuleOperator(parent, (DatRuleOp)v.get(i + 1)));
						start = i + 2;
						i++;
					} else {
						start = i + 1;
					}
				}*/
				if(obj instanceof DatRuleOp &&
					CRGRuleGrouperManager.getOperationRange(((DatRuleOp)obj).m_op) == operationRange) {
					if(start < i) {
                        CRGRuleElement ele = new CRGRuleElement(ruleEle);
                        childElements.add(doNestedNextRange(ele, ruleEle, v, start, i, operationRange));
					}
					childElements.add(getRuleOperator(parent, (DatRuleOp)obj));
					start = i + 1;
				}

			}
			if(start < n) {
				if(start == 0) {
                    childElements.add(doNestedNextRange(ruleEle, parent, v, start, n, operationRange));
                } else {
					if(n - start > 2){
						CRGRuleElement ele = new CRGRuleElement(ruleEle);
						childElements.add(doNestedNextRange(ele, ruleEle, v, start, n, operationRange));
					}else{
						for(int i = start; i < n; i++){
							childElements.add(getRuleElementToDatElement( ruleEle, v.get(i)));
						}
					}
				}
			}
		} else {
			doOneTerm(ruleEle, v, parent);
		}
		return childElements;
	}

	private CRGRuleElement doNestedNextRange(CRGRuleElement ruleEle, CRGRuleElement parent, Vector v, int from, int to,
		int range) throws Exception
	{
		ruleEle.m_type = CRGRuleGrouperStatics.TYPE_ELEMENT;
		Vector newV = new Vector(v.subList(from, to));
		int n = newV.size();
		if(range < CRGRuleGrouperStatics.OP_RANGE_DOT && n > 3) {
			range++;
			addChildRuleElementsNew(newV, ruleEle, parent, range);
		} else {
			doOneTerm(ruleEle, newV, parent);
			if(parent != null) {
				parent.m_isNested = parent.m_isNested || (ruleEle.m_childElements.length < v.size());
			}

		}

		return ruleEle;
	}

	private void doOneTerm(CRGRuleElement ruleEle, Vector newV, CRGRuleElement parent) throws Exception
	{
		int n = newV.size();
		ruleEle.m_childElements = new CRGRuleElement[n];
		for(int i = 0; i < n; i++) {
			Object obj = newV.get(i);
			ruleEle.m_childElements[i] = getRuleElementToDatElement(ruleEle, obj);
			if(ruleEle.m_childElements[i].m_isDepended ){
				ruleEle.m_isDepended = true;
				ruleEle.m_criterionDepend = ruleEle.m_childElements[i].m_criterionDepend;
				ruleEle.m_depend = ruleEle.m_childElements[i].m_depend;
				if(parent != null) {
                    parent.m_isDepended = true;
                }
			}
		}
		ruleEle.m_childCount = ruleEle.m_childElements.length;
		ruleEle.m_hasChilds = (ruleEle.m_childCount > 0);
		if(ruleEle.m_hasChilds ) {
            ruleEle.m_firstChildType = ruleEle.m_childElements[0].m_criterionType;
        }

	}

	private CRGRuleElement getRuleElementToDatElement(CRGRuleElement ruleEle, Object obj)throws Exception
	{

		if(obj instanceof DatRuleElement) {
			return getRuleElement(ruleEle, (DatRuleElement)obj);
		} else if(obj instanceof DatRuleVal) {
			return getRuleValue(ruleEle, (DatRuleVal)obj);
		} else if(obj instanceof DatRuleOp) {
			return getRuleOperator(ruleEle, (DatRuleOp)obj);
		}
		return null;
	}

	/**
	 *
	 * @param v Vector
	 * @param ruleEle CRGRuleElement
	 * @param parent CRGRuleElement
	 * @throws Exception
	 */
	private void addChildRuleElements(Vector v, CRGRuleElement ruleEle, CRGRuleElement parent) throws Exception
	{
		try {
			int opType = -1;
			int n = v != null ? v.size() : 0;
			ruleEle.m_childCount = 0;
			if(n > 0) {
				CRGRuleElement[] childs = new CRGRuleElement[n];
				int j = 0;
				for(int i = 0, k = 0; i < n; i++, k++) {
					Object obj = v.get(i);
					if(obj instanceof DatRuleElement) {
						childs[k] = getRuleElement(ruleEle, (DatRuleElement)obj);
					} else if(obj instanceof DatRuleVal) {
						childs[k] = getRuleValue(ruleEle, (DatRuleVal)obj);
					} else if(obj instanceof DatRuleOp) {
						CRGRuleElement op = getRuleOperator(ruleEle, (DatRuleOp)obj);

						if((parent == null && k > 0 || k > 1)
							&& (
							childs[k - 1].m_type == CRGRuleGrouperStatics.TYPE_VALUE
							&&
							(CRGRuleGrouperManager.isDotOperation(op.m_operantType) 
							|| (CRGRuleGrouperManager.isDashOperation(op.m_operantType)
							&& !CRGRuleGrouperManager.isDotOperation(opType))))) {

							CRGRuleElement elem = new CRGRuleElement(parent);
							j = createNestedElement(elem, v, i - 1, op.m_operantType);
							childs[k - 1] = elem;
							ruleEle.m_childCount--;
							if(j < n - 1) { // nicht bis zu Ende gesprüft
								k--;
								i = j - 1;
							} else {
								i = j;
							}
						} else {
							childs[k] = op;
							if(k > 0) {
								childs[k - 1].m_nextOperantType = childs[k].m_operantType;
							}

						}
						opType = op.m_operantType;
					}
					if(k == 0 && parent != null) {
						ruleEle.m_firstChildType = childs[k].m_criterionType;
					}
					ruleEle.m_childCount++;
				}
				CRGRuleElement[] newChilds = new CRGRuleElement[ruleEle.m_childCount];
				System.arraycopy(childs, 0, newChilds, 0, ruleEle.m_childCount);
				ruleEle.m_childElements = setBracesForOr(newChilds, parent);
				if(parent != null) {
					parent.m_isNested = parent.m_isNested || (ruleEle.m_childElements.length < childs.length);
				}
				ruleEle.m_childCount = ruleEle.m_childElements.length;
				ruleEle.m_hasChilds = (ruleEle.m_childCount > 0);
			}
		} catch(CRGRuleGroupException ex) {
			throw ex;
		} catch(Exception ex) {
			throw this.createRuleException("error by appanding rule child", ex);
		}
	}

	/**
	 * setzt Klammern für die Ausdrucke zwischen den OR Operationen
	 */
	private CRGRuleElement[] setBracesForOr(CRGRuleElement[] childs, CRGRuleElement parent) throws Exception
	{
		try {
			if(childs == null || childs.length <= 3) {
				return childs;
			}
			CRGRuleElement[] retChilds = new CRGRuleElement[childs.length];
			int pos1 = -1;
			int pos2 = -1;
			int i = 0, ind = 0;
			for(i = 0, ind = 0; i < childs.length; i++, ind++) {
				if(childs[i].m_type == CRGRuleGrouperStatics.TYPE_OPERATOR &&
					childs[i].m_operantType == CRGRuleGrouperStatics.OP_OR) {
					pos1 = i;
					for(int j = i + 1; j < childs.length; j++) {
						if(childs[j].m_type == CRGRuleGrouperStatics.TYPE_OPERATOR &&
							childs[j].m_operantType == CRGRuleGrouperStatics.OP_OR || j == childs.length - 1) {
							pos2 = j;
							if(j > pos1 + 2) {
								// wenn zwischen den || Operationen nur ein Element steht ist das Einklammern nicht nötig

								int capacity = pos2 - pos1 - 1;
								if(j == childs.length - 1) {
									capacity++;
								}
								CRGRuleElement elem = new CRGRuleElement(parent);
								elem.m_type = CRGRuleGrouperStatics.TYPE_ELEMENT;
								CRGRuleElement[] elChilds = new CRGRuleElement[capacity];
								for(int k = pos1 + 1, kk = 0; k < pos2; k++, kk++) {
									elChilds[kk] = childs[k];
								}
								elem.m_childElements = elChilds;
								elem.m_childCount = elChilds.length;
								elem.m_firstChildType = elChilds[0].m_criterionType;
								retChilds[ind] = childs[pos1]; ind++;
								retChilds[ind] = elem;
								if(j == childs.length - 1) {
									elChilds[capacity - 1] = childs[pos2];
									i = pos2;
									retChilds[ind].m_nextOperantType = -1;
								} else {
									retChilds[ind].m_nextOperantType = childs[pos2].m_operantType;
									i = pos2 - 1;
									pos1 = -1;
								}

							} else {
								for(i = pos1; i < pos2; ind++, i++) {
									retChilds[ind] = childs[i];
								}
								ind--; // weil wird an for wieder hochgezählt
								i--;
							}
							break;
						}
					}
				} else {
					retChilds[ind] = childs[i];
				}

			}
			CRGRuleElement[] newChilds = new CRGRuleElement[ind];
			System.arraycopy(retChilds, 0, newChilds, 0, ind);
			return newChilds;
		} catch(Exception ex) {
			throw this.createRuleException("error by appanding rule child", ex);
		}

	}

	private int createNestedElement(CRGRuleElement parent, Vector v, int startPos, int opType) throws Exception
	{

	  /* 3.9.5 2015-09-02 DNi: #FINDBUGS - Potenzielle NP-Exception, weil parent != null nicht abgefragt wird. */
		parent.m_type = CRGRuleGrouperStatics.TYPE_ELEMENT;
		int lastPos = startPos;
		int n = v != null ? v.size() : 0;
		if(n == 0) {
			return startPos;
		}
		int j = 0;
		CRGRuleElement[] childs = new CRGRuleElement[n - startPos];
		for(int i = startPos, k = 0; i < n; i++, k++) {
			Object obj = v.get(i);
			if(obj instanceof DatRuleElement) {
				childs[k] = getRuleElement(parent, (DatRuleElement)obj);
			} else if(obj instanceof DatRuleVal) {
				childs[k] = getRuleValue(parent, (DatRuleVal)obj);
			} else if(obj instanceof DatRuleOp) {
				CRGRuleElement op = getRuleOperator(parent, (DatRuleOp)obj);
				if(k > 0) {
					if(CRGRuleGrouperManager.isDotOperation(op.m_operantType) //nach +- kommt */
						&& CRGRuleGrouperManager.isDashOperation(opType))

					{
						CRGRuleElement elem = new CRGRuleElement(parent);
						j = createNestedElement(elem, v, i - 1, op.m_operantType);
						childs[k - 1] = elem;
						k--;
						parent.m_childCount--;
						parent.m_isNested = true;
						i = j - 1;
					} else {
						if(CRGRuleGrouperManager.isDotOperation(op.m_operantType) 
							&& CRGRuleGrouperManager.isDotOperation(opType) // die Zeichenfolge soll beibehalten bleiben
							|| CRGRuleGrouperManager.isDashOperation(op.m_operantType)
							&& CRGRuleGrouperManager.isDashOperation(opType)) {
							childs[k] = op;
							if(k > 0) {
								childs[k - 1].m_nextOperantType = childs[k].m_operantType;
							}

						} else {
							// Element beenden
							lastPos = i;
							break;
						}
					}
				} else {
					childs[k] = op;
					if(k > 0) {
						childs[k - 1].m_nextOperantType = childs[k].m_operantType;
					}

				}
				opType = op.m_operantType;
			}
			if(k == 0 && parent != null) {
				parent.m_firstChildType = childs[k].m_criterionType;
			}
			parent.m_childCount++;
			lastPos++;

		}
		parent.m_childElements = childs;
		parent.m_hasChilds = (parent.m_childCount > 0);
		return lastPos;
	}

	/**
	 *
	 * @param parent CRGRuleElement
	 * @param op DatRuleOp
	 * @return CRGRuleElement
	 */
	private CRGRuleElement getRuleOperator(CRGRuleElement parent, DatRuleOp op)
	{
		CRGRuleElement ruleEle = new CRGRuleElement(parent);
		ruleEle.m_type = CRGRuleGrouperStatics.TYPE_OPERATOR;
		ruleEle.m_operantType = CRGRuleGrouperManager.getOperatorType(op.m_op);
                ruleEle.m_mark = op.m_mark;
		return ruleEle;
	}

	private void setRuleGroupType()
	{
		if(m_rootElement != null) {
			m_hasCPCrit = m_rootElement.hasCritTypes(CHECK_CP_ONLY);
			m_hasCPMedCrit = m_rootElement.hasCritTypes(CHECK_CP_MED_ONLY);
			m_hasRSACrit = m_rootElement.hasCritTypes(CHECK_MRSA_ONLY);
			m_hasACGCrit = m_rootElement.hasCritTypes(CHECK_ACG_ONLY);
			m_hasAMBUMedCrit = m_rootElement.hasCritTypes(CHECK_AMBU_MED_ONLY);
			m_hasAMBUSoLeCrit = m_rootElement.hasCritTypes(CHECK_AMBU_SOLE_ONLY);
			m_hasKHCrit = m_rootElement.hasCritTypes(CHECK_KH_ONLY);
			m_hasInsCrit = m_rootElement.hasCritTypes(CHECK_INS_ONLY);
			m_hasGKRsaCrit = m_rootElement.hasCritTypes(CHECK_GK_RSA_ONLY);
			m_hasCareCrit = m_rootElement.hasCritTypes(CHECK_AMBU_CARE_ONLY);
                        m_needGroupResults = m_rootElement.hasCritTypes(CHECK_NEEDS_GROUPING);
		}
	}

	/**
	 * Setzt Flags der Supergruppenzugehörigkeit der Regel.
         * Wird in CpxCRGRule überschrieben, da ein Flag genötigt wird, ob die Regel einen gegroupen Fall braucht
	 * @param entry CriterionEntry
	 */
	protected void setRulesGroupType(CriterionEntry entry) throws Exception
	{
		Vector v = getRuleCharacteristics(entry);
		
		if(v != null && v.size() == 10) {
			if(!m_hasCPCrit) {
				m_hasCPCrit = ((Boolean)v.elementAt(0)).booleanValue();
			}
			if(!m_hasRSACrit) { // flag auf true wenn in der regel kommen einige Kriterien aus der MRSA - Supergruppe vor
				m_hasRSACrit = ((Boolean)v.elementAt(1)).booleanValue();
			}
			if(!m_hasGKRsaCrit) { // flag auf true wenn in der regel kommen einige Kriterien aus der MRSA - Supergruppe vor
				m_hasGKRsaCrit = ((Boolean)v.elementAt(2)).booleanValue();
			}
			if(!m_hasACGCrit) { // flag auf true wenn in der regel kommen einige Kriterien aus der ACG - Supergruppe vor
				m_hasACGCrit = ((Boolean)v.elementAt(3)).booleanValue();
			}
			if(!m_hasAMBUMedCrit) { // flag auf true wenn in der regel kommen einige Kriterien aus der ambulante Daten - Supergruppe vor
				m_hasAMBUMedCrit = ((Boolean)v.elementAt(4)).booleanValue();
			}
			if(!m_hasAMBUSoLeCrit) { // flag auf true wenn in der regel kommen einige Kriterien aus der ambulante Daten - Supergruppe vor
				m_hasAMBUSoLeCrit = ((Boolean)v.elementAt(5)).booleanValue();
			}
			if(!m_hasKHCrit) { // flag auf true wenn in der regel kommen einige Kriterien aus der Krankenhausdaten - Supergruppe vor
				m_hasKHCrit = ((Boolean)v.elementAt(6)).booleanValue();
			}
			if(!m_hasInsCrit) { // flag auf true wenn in der regel kommen einige Kriterien aus der Versicherten Stammdaten - Supergruppe vor
				m_hasInsCrit = ((Boolean)v.elementAt(7)).booleanValue();
			}
//			if(!m_hasCPMedCrit) { immer false
//				m_hasCPMedCrit = ((Boolean)v.elementAt(8)).booleanValue();
//			}
			if(!m_hasCareCrit) {
				m_hasCareCrit = ((Boolean)v.elementAt(8)).booleanValue();
			}if(!m_needGroupResults){
                            m_needGroupResults = ((Boolean)v.elementAt(9)).booleanValue();
                        }
		}
	}

	private Vector getRuleCharacteristics(CriterionEntry entry) throws Exception
	{
		if(m_isCheckpoint) {
			return null;
		}
		Vector v = new Vector();
//		Boolean m_hasCPCrit = new Boolean(CRGRuleGrouperStatics.isCPEntry(entry, 0, 1, 1, 1));
//		Boolean m_hasRSACrit = new Boolean(CRGRuleGrouperStatics.isRSAEntry(entry));
//		Boolean m_hasGKRsaCrit = new Boolean(CRGRuleGrouperStatics.isGKRSAEntry(entry));
//		Boolean m_hasACGCrit = new Boolean(CRGRuleGrouperStatics.isACGEntry(entry));
//		Boolean m_hasAMBUMedCrit = new Boolean(CRGRuleGrouperStatics.isAMBUMedEntry(entry));
//		Boolean m_hasAMBUSoLeCrit = new Boolean(CRGRuleGrouperStatics.isAMBUSoLeEntry(entry));
//		Boolean m_hasKHCrit = new Boolean(CRGRuleGrouperStatics.isKHEntry(entry));
//		Boolean m_hasInsCrit = new Boolean(CRGRuleGrouperStatics.isInsEntry(entry));
//		Boolean m_hasCareCrit = new Boolean(CRGRuleGrouperStatics.isAmbuCareEntry(entry));
//                Boolean m_needGroupResults = CRGRuleGrouperStatics.isNeedsGroupingEntry(entry);
		v.addElement(m_hasCPCrit);
		v.addElement(m_hasRSACrit);
		v.addElement(m_hasGKRsaCrit);
		v.addElement(m_hasACGCrit);
		v.addElement(m_hasAMBUMedCrit);
		v.addElement(m_hasAMBUSoLeCrit);
		v.addElement(m_hasKHCrit);
		v.addElement(m_hasInsCrit);
		v.addElement(m_hasCareCrit);
                v.addElement(m_needGroupResults);
		return v;

	}
	/**
	 * 	überprüfen, ob dieser Oparation zufäälig nicht in der Value gelandet ist: rückwärtskompatibilität mit der fehlerhaften DAK Regel
	 * ugvd "+ 1"
	 */

	private int checkOpTypeWithValue(int opType, DatRuleVal value)
	{
		String val = value.m_val;
		int len = 0;
		if(opType == CRGRuleGrouperStatics.OP_NO_OPERATION && value.m_val != null && val.length() > 0	){
			val = val.replaceAll("'", "").trim();
			if(val.startsWith("&&")) {
				opType = CRGRuleGrouperStatics.OP_AND;
				len = 2;
			} else if(val.startsWith("||")) {
				opType =  CRGRuleGrouperStatics.OP_OR;
				len = 2;
			} else if(val.startsWith("==")) {
				opType =  CRGRuleGrouperStatics.OP_EQUAL;
				len = 2;
			} else if(val.startsWith(">")) {
				opType =  CRGRuleGrouperStatics.OP_GT;
				len = 1;
			} else if(val.startsWith(">=")) {
				opType =  CRGRuleGrouperStatics.OP_GT_EQUAL;
				len = 2;
			} else if(val.startsWith("<")) {
				opType =  CRGRuleGrouperStatics.OP_LT;
				len = 1;
			} else if(val.startsWith("<=")) {
				opType =  CRGRuleGrouperStatics.OP_LT_EQUAL;
				len = 2;
			} else if(val.startsWith("!=")) {
				opType =  CRGRuleGrouperStatics.OP_NOT_EQUAL;
				len = 2;
			} else if(val.startsWith("+")) {
				opType =  CRGRuleGrouperStatics.OP_PLUS;
				len = 1;
			} else if(val.startsWith("-")) {
				opType =  CRGRuleGrouperStatics.OP_MINUS;
			} else if(val.startsWith("*")) {
				opType =  CRGRuleGrouperStatics.OP_MULTIPL;
				len = 1;
			} else if(val.startsWith("/")) {
				opType =  CRGRuleGrouperStatics.OP_DIVIDE;
				len = 12;
			} else if(val.startsWith("|")) {
				opType =  CRGRuleGrouperStatics.OP_CONCATENATE;
				len = 1;
			} else if(val.startsWith("IN")) {
				opType =  CRGRuleGrouperStatics.OP_IN;
				len = 2;
			} else if(val.startsWith("NOT IN")) {
				opType =  CRGRuleGrouperStatics.OP_NOT_IN;
				len = 6;
			} else if(val.startsWith("NOT IN @")) {
				opType =  CRGRuleGrouperStatics.OP_NOT_IN_TABLE;
				len = 8;
			} else if(val.startsWith("@")) {
				opType =  CRGRuleGrouperStatics.OP_IN_TABLE;
				len = 1;
			} else if(val.startsWith("!!")) {
				opType =  CRGRuleGrouperStatics.OP_NOT_DOUBLE_IN;
				len = 2;
			} else if(val.startsWith("!!@")) {
				opType =  CRGRuleGrouperStatics.OP_NOT_DOUBLE_IN_TABLE;
				len = 3;
			} else if(val.startsWith("##")) {
				opType =  CRGRuleGrouperStatics.OP_MANY_IN;
				len = 2;
			} else if(val.startsWith("#@")){
				opType = CRGRuleGrouperStatics.OP_MANY_IN_TABLE;
				len = 2;
			}
			if(opType > 0 && len > 0){
				value.m_val = val.substring(len).trim();
				value.m_op = val.substring(0, len);
				return opType;
			}
	}
		return opType;
	}
        /**
         * füllt RuleElement mit den Methodeneigenschaften für die Verarbeitung der Regel in dem CheckpointRuleGrouper
         */
//        private void fillMethodElementFromCriterionValue(CRGRuleElement parent, CriterionEntry entry, CRGRuleElement ruleEle, String method, String parameter) throws Exception
//        {
//            ruleEle.m_criterionType = -1;
//            ruleEle.m_criterionIndex = -1;
//            ruleEle.m_criterionDependIndex = -1;
//            ruleEle.m_isDepended = false;
//            ruleEle.m_isCrossCase = false;
//            ruleEle.m_depend = 0;
//            ruleEle.m_criterionCheckIndex = -1;        
//            if((entry != null)) { //&&(!value.m_crit.equals(""))
//                if((entry.m_index < 0)) {
//                        throw this.createRuleException("invalid index by criterion " + entry.getWorkText(), null);
//                }
//                ruleEle.m_criterionType = entry.m_type;
//                ruleEle.m_criterionIndex = entry.m_index;
//                ruleEle.m_computeType = CRGRuleGrouperStatics.COMPUTE_TYPE_METHOD;
//                ruleEle.m_operantCompute = true;
//                ruleEle.m_methodType = CRGRuleGrouperStatics.getMethodType(method);   
//                ruleEle.m_method_dependency_crit = entry.m_method_dependency_index; 
//                ruleEle.m_method_dependency_type = entry.m_method_dependency_type;
//                switch(ruleEle.m_methodType){
//                    case CRGRuleGrouperStatics.METHOD_TYPE_MAX_INTERVAL_PROPERTY:
//                    {
//                        ruleEle.setParameter4MaxIntervalPropertyMethod( parameter);
//                        break;
//                    }
//                    case CRGRuleGrouperStatics.METHOD_TYPE_MAX_INTERVAL_TABLE:
//                     {
//                        ruleEle.setParameter4MaxIntervalTableMethod( parameter); 
//                        Object tableNameObj = ruleEle.getMethodParameter(0);
//                        if(tableNameObj != null && tableNameObj instanceof String){
//                            setStringArrayTable(ruleEle, (String)tableNameObj);
//                        }
//                        break;
//                    }
//                    
//                }
//            } 
//        }
  	private void fillCRGRuleElementFromCriterionEntry(CRGRuleElement parent, CriterionEntry entry, CRGRuleElement ruleEle, int opType, String value) throws Exception
	{
                ruleEle.m_criterionType = -1;
                ruleEle.m_criterionIndex = -1;
                ruleEle.m_criterionDependIndex = -1;
                ruleEle.m_isDepended = false;
                ruleEle.m_isCrossCase = false;
                ruleEle.m_depend = 0;
		ruleEle.m_criterionCheckIndex = -1;
		if((entry != null)) { //&&(!value.m_crit.equals(""))
			if((entry.m_index < 0)) {
				throw this.createRuleException("invalid index by criterion " + entry.getWorkText(), null);
			}
			ruleEle.m_criterionType = entry.m_type;
			ruleEle.m_criterionIndex = entry.m_index;
			ruleEle.m_criterionCheckIndex = entry.m_dependCritCheckIndex;
			ruleEle.m_criterionDependGroupIndex = entry.m_dependGroupIndex;
			ruleEle.m_criterionDependIndex = entry.m_dependIndex;
			ruleEle.m_isDepended = entry.m_isDepended;
			ruleEle.m_depend = entry.m_depend;
			ruleEle.m_isCrossCase = entry.m_isCrossCase;
			ruleEle.m_isCummulative = entry.m_isCummulative;
			ruleEle.m_indCummulateBase = entry.m_indCummulateBase;
			ruleEle.m_indCummulateDate = entry.m_indCummulateDate;
			ruleEle.m_indCummulateNumber = entry.m_indCummulateNumber;
			ruleEle.m_indCummulateWhat = entry.m_indCummulateWhat;
			if(ruleEle.m_isCrossCase) {
				parent.m_isCrossCase = true;
			}
			if(entry.m_depend > 0) {
				ruleEle.m_criterionDepend = CRGRuleGrouperManager.getCriterionDepend();   
				if(ruleEle.m_criterionDepend[ruleEle.m_depend - 1].m_indexDepend != 0) {
					parent.m_isDepended = true;
				}
			}
		} 
		setComputeType(ruleEle, opType, value, entry);
		if(entry != null && entry.m_index == CRGRuleGrouperStatics.CRITINT_INDEX_NO_CRIT) { 
			setUnformatedValue(ruleEle, value, opType);
		} else {
			if(entry != null) {
				switch(entry.m_type) {
					case CRGRuleGrouperStatics.DATATYPE_STRING:
					case CRGRuleGrouperStatics.DATATYPE_ARRAY_STRING: {
						setStringValue(ruleEle, value, opType);
						break;
					}
					case CRGRuleGrouperStatics.DATATYPE_INTEGER:
					case CRGRuleGrouperStatics.DATATYPE_ARRAY_INTEGER: {
						setIntegerValue(ruleEle, value, opType);
						break;
					}
					case CRGRuleGrouperStatics.DATATYPE_DOUBLE:
					case CRGRuleGrouperStatics.DATATYPE_ARRAY_DOUBLE: {
						setDoubleValue(ruleEle, value, opType);
						break;
					}
					case CRGRuleGrouperStatics.DATATYPE_ARRAY_DAY_TIME:
					case CRGRuleGrouperStatics.DATATYPE_DAY_TIME:
						setDateTimeValue(ruleEle, value, opType);
						break;
					case CRGRuleGrouperStatics.DATATYPE_DATE:
					case CRGRuleGrouperStatics.DATATYPE_ARRAY_DATE: {
						setDateValue(ruleEle, value, opType);
						break;
					}
				}
			} else {
				setUnformatedValue(ruleEle, value, opType);
			}
		}
	}

	/**
	 * 	Rückwärtskompatibilität zu C - Grouper DAK Regel aufnahmetag = entlassungstag

	 */
	private CRGRuleElement  createCRGRuleElement(CriterionEntry entryLeft, CriterionEntry entryRight, CRGRuleElement parent, int opType) throws Exception
	{
		parent.m_type = CRGRuleGrouperStatics.TYPE_ELEMENT;
		parent.m_isNested = true;
		parent.m_hasChilds = true;
		parent.m_childCount = 3;
		parent.m_childElements = new CRGRuleElement[3];
		CRGRuleElement ruleEleLeft = new CRGRuleElement(parent);
		ruleEleLeft.m_type = CRGRuleGrouperStatics.TYPE_VALUE;
		ruleEleLeft.m_isNested = false;
		ruleEleLeft.m_childCount = 0;
		ruleEleLeft.m_hasChilds = false;
		fillCRGRuleElementFromCriterionEntry(parent, entryLeft, ruleEleLeft, opType, "");
		parent.m_childElements[0] = ruleEleLeft;
		CRGRuleElement ruleEleOp = new CRGRuleElement(parent);
		ruleEleOp.m_type = CRGRuleGrouperStatics.TYPE_OPERATOR;
		ruleEleOp.m_operantType = opType;
		parent.m_childElements[1] = ruleEleOp;

		CRGRuleElement ruleEleRight = new CRGRuleElement(parent);
		ruleEleRight.m_type = CRGRuleGrouperStatics.TYPE_VALUE;
		ruleEleRight.m_isNested = false;
		ruleEleRight.m_childCount = 0;
		ruleEleRight.m_hasChilds = false;
		fillCRGRuleElementFromCriterionEntry(parent, entryRight, ruleEleRight, opType, "");
		parent.m_childElements[2] = ruleEleRight;

		return parent;
	}



	/**
	 *
	 * @param parent CRGRuleElement
	 * @param value DatRuleVal
	 * @return CRGRuleElement
	 * @throws Exception
	 */
	private CRGRuleElement getRuleValue(CRGRuleElement parent, DatRuleVal value) throws Exception
	{
		CRGRuleElement ruleEle = new CRGRuleElement(parent);
		try {
			ruleEle.m_type = CRGRuleGrouperStatics.TYPE_VALUE;
			ruleEle.m_isNested = false;
			ruleEle.m_childCount = 0;
			ruleEle.m_hasChilds = false;
                        ruleEle.m_mark = value.m_mark;
			if(!this.isServer()) {
				return ruleEle;
			}
			CriterionEntry entry = CRGRuleGrouperManager.getCriterionByText(value.m_crit);
			if(entry != null){
				setRulesGroupType(entry);
			}
			ruleEle.m_notElement = value.m_isNot;
                        
                            int opType = CRGRuleGrouperManager.getOperatorType(value.m_op);
// überprüfen, ob dieser Oparation zufäälig nicht in der Value gelandet ist: rückwärtskompatibilität mit der fehlerhaften DAK Regel
// ugvd "+ 1"
                            opType = checkOpTypeWithValue(opType, value);
// überprüfen, ob ein Kriteriumsnamen als Wert des anderen Kriterien eingetragen ist
// Rückwärtskompatibilität zu C - Grouper DAK Regel aufnahmetag = entlassungstag
                            if(value.m_val != null && value.m_val.length() > 0){
                                    CriterionEntry entryTest = CRGRuleGrouperManager.getCriterionByText(value.m_val);
                                    if(entryTest != null) {
                                            setRulesGroupType(entryTest);
                                            ruleEle = null;
                                            return createCRGRuleElement(entry, entryTest, parent, opType);
                                    }
                            }
                            fillCRGRuleElementFromCriterionEntry(parent, entry, ruleEle, opType, value.m_val);
                            ruleEle.m_operantType = opType;
                            /* 3.9.5 2015-09-02 DNi: #FINDBUGS - Die Variable value.m_val könnte null sein, obwohl dies in der Methode nicht erwartet bzw. nicht abgefangen wird! */
                            ruleEle.m_WildCard = setWildCard(value.m_val);
                        
// interval zufügen
			if(value.hasInterval()) {
				setIntervalValues(ruleEle, value.getInterval());
			}
		} catch(CRGRuleGroupException ex) {
			throw ex;
		} catch(Exception ex) {
			throw createRuleException("error by setting rule value " + value.m_val +
				" in criterium " + value.m_crit +
				" operator " + value.m_op, ex);
		}
		return ruleEle;
	}

	/**
	 *
	 * @param val String
	 * @return int
	 * @throws Exception
	 */
	private int setWildCard(String val) throws Exception
	{
		if(val.contains("?")) {
			return 2;
		} else if(val.contains("%")) {
			if(val.indexOf("%") == (val.length() - 2)) {
				return 1;
			} else {
				return 2;
			}
		}
		return 0;
	}

	/**
	 *
	 * @param ruleEle CRGRuleElement
	 * @param val String
	 * @param opType int
	 * @throws Exception
	 */
	private void setStringValue(CRGRuleElement ruleEle, String val, int opType) throws Exception
	{
		if(CRGRuleGrouperManager.isOperatorArray(opType)) {
			ruleEle.m_valueType = CRGRuleGrouperStatics.DATATYPE_ARRAY_STRING;
			setStringArray(ruleEle, val, opType);
		} else {
			ruleEle.m_valueType = CRGRuleGrouperStatics.DATATYPE_STRING;
			ruleEle.m_strValue = checkStringValue(val);
			ruleEle.m_WildCard = setWildCard(val);
			int place = 10, zPlace;
			zPlace = ruleEle.m_strValue.indexOf("%");
			if(zPlace == -1) {
				zPlace = ruleEle.m_strValue.length();
			}
			if(zPlace < place) {
				place = zPlace;
			}
			zPlace = ruleEle.m_strValue.indexOf("?");
			if(zPlace == -1) {
				zPlace = ruleEle.m_strValue.length();
			}
			if(zPlace < place) {
				place = zPlace;
			}
			ruleEle.m_length = place;
		}
	}
        
        /**
         * ausgegliedert ermitteln der Tabelleninhalten aus setStringArrayTable, setStringConditionValue und setStringArrayTable
         * damit die Tabellen abhängig von unterschiedlichen Regelmanagers gefüllt werden können
         * @param tableName
         * @param ruleIdent
         * @param year
         * @return
         * @throws Exception 
         */
        protected String[] getStringArrayFromRuleTables(String tableName, String ruleIdent, int year)throws Exception{
//            if(this.m_isCheckpoint) {
//                    return getRuleManager().getTableStringValues(tableName, ruleIdent, year);
//            } else {
                if(m_checkTables){ 
                   return getRuleManager().getTableStringValues(tableName, ruleIdent, year);
//                }
            }

            return null;
        }

	private void setStringConditionValue(CRGSuggestion ruleEle, String condition_val, int condition_opType) throws Exception
	{
		ruleEle.m_strConditionValues = new ArrayList<String>();
		String[] vals = null;
		if(CRGRuleGrouperManager.isOperatorArray(condition_opType)) {
			if(CRGRuleGrouperManager.isOperatorTable(condition_opType)){

                            vals = getStringArrayFromRuleTables(condition_val, m_ruleIdentififier, this.m_year);

			}else{
				vals = condition_val.split(",");
			}
			int len = vals != null ? vals.length : 0;
			for(int i = 0; i < len; i++) {
				vals[i] = vals[i].replaceAll("'", "").trim();
				ruleEle.m_strConditionValues.add(vals[i]);
			}

		}else{
			ruleEle.m_strConditionValues.add(condition_val);
		}
	}

	/**
	 *
	 * @param ruleEle CRGSuggestion
	 * @param val String
	 * @param opType int
	 * @throws Exception
	 */
	private void setStringValue(CRGSuggestion ruleEle, String val, int opType) throws Exception
	{
		if(CRGRuleGrouperManager.isOperatorArray(opType)) {
			ruleEle.m_valueType = CRGRuleGrouperStatics.DATATYPE_ARRAY_STRING;
			setStringArray(ruleEle, val, opType);
		} else {
			ruleEle.m_valueType = CRGRuleGrouperStatics.DATATYPE_STRING;
			ruleEle.m_strValue = checkStringValue(val);
			ruleEle.m_WildCard = setWildCard(val);
			int place = 10, zPlace;
			zPlace = ruleEle.m_strValue.indexOf("%");
			if(zPlace == -1) {
				zPlace = ruleEle.m_strValue.length();
			}
			if(zPlace < place) {
				place = zPlace;
			}
			zPlace = ruleEle.m_strValue.indexOf("?");
			if(zPlace == -1) {
				zPlace = ruleEle.m_strValue.length();
			}
			if(zPlace < place) {
				place = zPlace;
			}
			ruleEle.m_length = place;
		}
	}

	/**
	 *
	 * @param val String
	 * @return String
	 */
	private String checkStringValue(String val)
	{
		int si = 0, ei = 0, lg = val.length();
		if(!val.equals("")) {
			if(val.charAt(0) == '\'') {
				si = 1;
			}
			if(val.charAt(lg - 1) == '\'') {
				ei = lg - 1;
			} else {
				ei = lg;
			}
			val = val.substring(si, ei);
			val = val.replace(" ", "");
			return val.toUpperCase();
		} else {
			return "";
		}
	}

	/**
	 *
	 * @param ruleEle CRGRuleElement
	 * @param val String
	 * @param opType int
	 * @throws Exception
	 */
	private void setIntegerValue(CRGRuleElement ruleEle, String val, int opType) throws Exception
	{
		if(CRGRuleGrouperManager.isOperatorArray(opType)) {
			ruleEle.m_valueType = CRGRuleGrouperStatics.DATATYPE_ARRAY_INTEGER;
			setIntegerArray(ruleEle, val, opType);
		} else {
			ruleEle.m_valueType = CRGRuleGrouperStatics.DATATYPE_INTEGER;
			if(val != null && val.length() > 0) {
				float f = Float.parseFloat(val);
				try {
					//Regel mit Verweildauer >= 3.01
					// also erst aufrunden und dann integer
					ruleEle.m_intValue = (int)Double.parseDouble(val);
					if(f != (float)ruleEle.m_intValue) {
						ruleEle.m_intValue++;
					}
				} catch(Exception ex) {
					ruleEle.m_intValue = (int)Double.parseDouble(val);
					if(f != (float)ruleEle.m_intValue) {
						ruleEle.m_intValue++;
					}
				}
			}
		}
	}

	/**
	 *
	 * @param ruleEle CRGSuggestion
	 * @param val String
	 * @param opType int
	 * @throws Exception
	 */
	private void setIntegerValue(CRGSuggestion ruleEle, String val, int opType) throws Exception
	{
		if(CRGRuleGrouperManager.isOperatorArray(opType)) {
			ruleEle.m_valueType = CRGRuleGrouperStatics.DATATYPE_ARRAY_INTEGER;
			setIntegerArray(ruleEle, val, opType);
		} else {
			ruleEle.m_valueType = CRGRuleGrouperStatics.DATATYPE_INTEGER;
			if(val != null && val.length() > 0) {
				float f = Float.parseFloat(val);
				try {
					//Regel mit Verweildauer >= 3.01
					// also erst aufrunden und dann integer
					ruleEle.m_intValue = (int)Double.parseDouble(val);
					if(f != (float)ruleEle.m_intValue) {
						ruleEle.m_intValue++;
					}
				} catch(Exception ex) {
					ruleEle.m_intValue = (int)Double.parseDouble(val);
					if(f != (float)ruleEle.m_intValue) {
						ruleEle.m_intValue++;
					}
				}
			}
		}
	}

	/**
	 *
	 * @param ruleEle CRGRuleElement
	 * @param val String
	 * @param opType int
	 * @throws Exception
	 */
	private void setDoubleValue(CRGRuleElement ruleEle, String val, int opType) throws Exception
	{
		if(CRGRuleGrouperManager.isOperatorArray(opType)) {
			ruleEle.m_valueType = CRGRuleGrouperStatics.DATATYPE_ARRAY_DOUBLE;
			setDoubleArray(ruleEle, val, opType);
		} else {
			ruleEle.m_valueType = CRGRuleGrouperStatics.DATATYPE_DOUBLE;
			if(val != null && val.length() > 0) {
				ruleEle.m_doubleValue = Double.parseDouble(val);
			}
		}
	}

	/**
	 *
	 * @param ruleEle CRGSuggestion
	 * @param val String
	 * @param opType int
	 * @throws Exception
	 */
	private void setDoubleValue(CRGSuggestion ruleEle, String val, int opType) throws Exception
	{
		if(CRGRuleGrouperManager.isOperatorArray(opType)) {
			ruleEle.m_valueType = CRGRuleGrouperStatics.DATATYPE_ARRAY_DOUBLE;
			setDoubleArray(ruleEle, val, opType);
		} else {
			ruleEle.m_valueType = CRGRuleGrouperStatics.DATATYPE_DOUBLE;
			if(val != null && val.length() > 0) {
				ruleEle.m_doubleValue = Double.parseDouble(val);
			}
		}
	}

	/**
	 *
	 * @param str String
	 * @return long
	 */
	private long getTimeMillisFromDayOrHourString(String str)
	{
		str = str.toUpperCase();
		str = str.trim();
		if(str.endsWith("S") || str.endsWith("T")) {
			String last = str.substring(str.length() - 1, str.length());
			str = str.substring(0, str.length() - 1);
			long tm = 0;
			long koeff = 0;
			if(last.equals("S")) {
				koeff = 3600000; //(60*60*1000)
			}
			if(last.equals("T")) {
				koeff = 3600000 * 24; //(60*60*1000 * 24)
			}
			try {
				if(str.contains(".")) {
					return(long)(Float.parseFloat(str) * koeff);
				} else {
					return Integer.parseInt(str) * koeff;
				}

			} catch(Exception e) {
				return 0;
			}
		}
		return 0;
	}

	/**
	 *
	 * @param val String
	 * @return long
	 */
	private long getTimeFromString(String val)
	{
		if(val != null && val.length() >= 5) {
			int h = 0;
			int m = 0;
			String s = val.replaceAll("'", "");
			String[] ss = s.split(":");
			if(ss.length == 1) {
				return -1;
				/*				try {
				  h = Integer.parseInt(ss[0]);
				 } catch(Exception e) {
				  return -1;
				 }*/
			}
			if(ss.length > 1) {
				try {
					h = Integer.parseInt(ss[0]);
					m = Integer.parseInt(ss[1]);
				} catch(Exception e) {
					return -1;
				}
			}

//			return(long)(((h - 1) * 3600 + (m) * 60) * 1000);
			return(long)(((h) * 3600 + (m) * 60) * 1000);

		}
		return -1;
	}

	/**
	 *
	 * @param ruleEle CRGRuleElement
	 * @param val String
	 * @param opType int
	 * @throws Exception
	 */
	private void setDateTimeValue(CRGRuleElement ruleEle, String val, int opType) throws Exception
	{
                ruleEle.m_helpStringDateTimeValue = val;
		if(CRGRuleGrouperManager.isOperatorArray(opType)) {
			ruleEle.m_valueType = CRGRuleGrouperStatics.DATATYPE_ARRAY_DAY_TIME;
			setDateArray(ruleEle, val, opType);
		} else {
			if(val != null && val.length() > 0) {
				ruleEle.m_valueType = CRGRuleGrouperStatics.DATATYPE_DAY_TIME;
				// versuchen die Konstellation nnT oder nnS
				long millis = getTimeMillisFromDayOrHourString(val);
				if(millis > 0) {
					ruleEle.m_datetimeValue = new Date(millis);
					ruleEle.m_longtimeValue = millis;
					return;
				}
				if(val.length() >= 5) {
					ruleEle.m_longtimeValue = getTimeFromString(val);
					ruleEle.m_datetimeValue = new Date(ruleEle.m_longtimeValue);
				}
				//System.out.println(ruleEle.m_longValue);
                                ruleEle.m_helpDateUnformat = val;
			}
		}
	}

	/**
	 *
	 * @param ruleEle CRGRuleElement
	 * @param val String
	 * @param opType int
	 * @throws Exception
	 */
	private void setDateValue(CRGRuleElement ruleEle, String val, int opType) throws Exception
	{
		if(CRGRuleGrouperManager.isOperatorArray(opType)) {
			ruleEle.m_valueType = CRGRuleGrouperStatics.DATATYPE_ARRAY_DATE;
			setDateArray(ruleEle, val, opType);
		} else {
			if(val != null && val.length() > 0) {
				ruleEle.m_valueType = CRGRuleGrouperStatics.DATATYPE_DATE;
				long millis = getTimeMillisFromDayOrHourString(val);
				if(millis > 0) {
					ruleEle.m_dateValue = new Date(millis);
					ruleEle.m_longValue = millis;
					return;
				}
                                setDateFromStringWithFormat(ruleEle, val);
				//System.out.println(ruleEle.m_longValue);
			}
		}
	}
        
        private void setDateFromStringWithFormat(CRGRuleElement ruleEle, String val) throws Exception
        {
            Date date = null;
            if(val.length() >= 14) {
                    date = m_simpleDateAndTimeFormat.parse(val.replaceAll("'", ""));
                    if(date != null){
                        ruleEle.m_dateValue = date;
                        ruleEle.m_longValue = ruleEle.m_dateValue.getTime();
                        return;
                    }
            }
            if(val.length() >= 8) {

                     date = m_simpleDateFormat.parse(val.replaceAll("'", ""));
                     if(date != null){
                         ruleEle.m_dateValue = date;
                       ruleEle.m_longValue = CRGInputOutputBasic.getDateWithoutTimeAsLong(ruleEle.m_dateValue);
                     }
            }
        }

	/**
	 *
	 * @param ruleEle CRGSuggestion
	 * @param val String
	 * @param opType int
	 * @throws Exception
	 */
	private void setDateValue(CRGSuggestion ruleEle, String val, int opType) throws Exception
	{
		if(CRGRuleGrouperManager.isOperatorArray(opType)) {
			ruleEle.m_valueType = CRGRuleGrouperStatics.DATATYPE_ARRAY_DATE;
			setDateArray(ruleEle, val, opType);
		} else {
			if(val != null && val.length() > 0) {
				if(val.length() >= 8) {
					ruleEle.m_valueType = CRGRuleGrouperStatics.DATATYPE_DATE;
					ruleEle.m_dateValue = m_simpleDateFormat.parse(val);
					ruleEle.m_longValue = CRGInputOutputBasic.getDateWithoutTimeAsLong(ruleEle.m_dateValue);
				} else {
					ruleEle.m_valueType = CRGRuleGrouperStatics.DATATYPE_DAY_TIME;
					ruleEle.m_longtimeValue = getTimeFromString(val);
					ruleEle.m_datetimeValue = new Date(ruleEle.m_longtimeValue);
				}
			}
		}
	}

	/**
	 *
	 * @param ruleEle CRGRuleElement
	 * @param val String
	 * @param opType int
	 * @throws Exception
	 */
	private void setUnformatedValue(CRGRuleElement ruleEle, String val, int opType) throws Exception
	{
		if(val != null && val.length() > 0) {
			try {
				int intVal = Integer.parseInt(val);
				ruleEle.m_valueType = CRGRuleGrouperStatics.DATATYPE_INTEGER;
				ruleEle.m_intValue = intVal;
				return;
			} catch(Exception ex) {}
			try {
				double doubleVal = Double.parseDouble(val);
				ruleEle.m_valueType = CRGRuleGrouperStatics.DATATYPE_DOUBLE;
				ruleEle.m_doubleValue = doubleVal;
				return;
			} catch(Exception ex) {
                        }
			try {
				String str;
				long dat;
				String v = val.toUpperCase();
				v = v.trim();
				v = v.replaceAll("'", "");
				int z = v.indexOf("T");
				if(v.endsWith("T")) { //also Tage
					str = v.substring(0, z);
					if(str.contains(".")) {
						dat = (long)Float.parseFloat(str) * 24 * 60 * 60 * 1000;
					} else {
						dat = Long.parseLong(str) * 24 * 60 * 60 * 1000;
					}
					ruleEle.m_valueType = CRGRuleGrouperStatics.DATATYPE_DATE;
					ruleEle.m_dateValue = new Date(dat);
					ruleEle.m_longValue = dat;
                                        ruleEle.m_helpDateUnformat = val; 
					return;
				} else {
					if(v.endsWith("S")) { // also Stunden
						z = v.indexOf("S");
						str = v.substring(0, z);
						if(str.contains(".")) {
							dat = (long)(Float.parseFloat(str) * 60 * 60 * 1000);
						} else {
							dat = Integer.parseInt(str) * 60 * 60 * 1000;
						}
						ruleEle.m_valueType = CRGRuleGrouperStatics.DATATYPE_DAY_TIME;
						ruleEle.m_longtimeValue = dat;
						ruleEle.m_datetimeValue = new Date(dat);
                                                ruleEle.m_helpDateUnformat = val; 
						return;
					} else {
						v = val.replaceAll("'", "");
						if(val.length() >= 8) {
							ruleEle.m_valueType = CRGRuleGrouperStatics.DATATYPE_DATE;
                                                        setDateFromStringWithFormat(ruleEle, v);
                                                        return;
						} else {
							ruleEle.m_valueType = CRGRuleGrouperStatics.DATATYPE_DAY_TIME;
							dat = getTimeFromString(v);
							if(dat != -1) {
								ruleEle.m_datetimeValue = new Date(dat);
								ruleEle.m_longtimeValue = dat;
								return;
							}
						}
					}
				}
			} catch(Exception ex) {}
			ruleEle.m_valueType = CRGRuleGrouperStatics.DATATYPE_STRING;
			ruleEle.m_strValue = checkStringValue(val);
			ruleEle.m_WildCard = setWildCard(val);
			int place = 10, zPlace;
			zPlace = ruleEle.m_strValue.indexOf("%");
			if(zPlace == -1) {
				zPlace = ruleEle.m_strValue.length();
			}
			if(zPlace < place) {
				place = zPlace;
			}
			zPlace = ruleEle.m_strValue.indexOf("?");
			if(zPlace == -1) {
				zPlace = ruleEle.m_strValue.length();
			}
			if(zPlace < place) {
				place = zPlace;
			}

			ruleEle.m_length = place;
		}
	}

	/**
	 *
	 * @param ruleEle CRGRuleElement
	 * @param val String
	 * @param opType int
	 * @throws Exception
	 */
	private void setStringArray(CRGRuleElement ruleEle, String val, int opType) throws Exception
	{
		if(CRGRuleGrouperManager.isOperatorTable(opType)) {
			setStringArrayTable(ruleEle, val);
		} else {
                        ruleEle.m_helpStringListValue = val;
			String[] vals = val.split(",");
			ruleEle.m_arrayLength = vals != null ? vals.length : 0;
			if(ruleEle.m_arrayLength > 0) {
				ruleEle.m_strArrayValue = new String[ruleEle.m_arrayLength];
//				ruleEle.m_WildCardArray = new int[ruleEle.m_arrayLength];
				for(int i = 0; i < ruleEle.m_arrayLength; i++) {
					ruleEle.m_strArrayValue[i] = vals[i].replaceAll("'", "").trim();
//					ruleEle.m_WildCardArray[i] = setWildCard(ruleEle.m_strArrayValue[i]);
				}
				int place = 10, zPlace = 0;
				for(int i = 0; i < ruleEle.m_strArrayValue.length; i++) {
					zPlace = ruleEle.m_strArrayValue[i].indexOf("%");
					if(zPlace == -1) {
						zPlace = ruleEle.m_strArrayValue[i].length();
					}
					if(zPlace < place) {
						place = zPlace;
					}
					zPlace = ruleEle.m_strArrayValue[i].indexOf("?");
					if(zPlace == -1) {
						zPlace = ruleEle.m_strArrayValue[i].length();
					}
					if(zPlace < place) {
						place = zPlace;
					}
				}
				ruleEle.m_length = place;
			}
		}
	}

	/**
	 *
	 * @param ruleEle CRGSuggestion
	 * @param val String
	 * @param opType int
	 * @throws Exception
	 */
	private void setStringArray(CRGSuggestion ruleEle, String val, int opType) throws Exception
	{
		if(CRGRuleGrouperManager.isOperatorTable(opType)) {
			setStringArrayTable(ruleEle, val, opType);
		} else {
			String[] vals = val.split(",");
			ruleEle.m_arrayLength = vals != null ? vals.length : 0;
			ruleEle.m_strArrayValue = new String[ruleEle.m_arrayLength];
//			ruleEle.m_WildCardArray = new int[ruleEle.m_arrayLength];
			if(ruleEle.m_arrayLength > 0) {
				for(int i = 0; i < ruleEle.m_arrayLength; i++) {
					ruleEle.m_strArrayValue[i] = vals[i].replaceAll("'", "").trim();
//					ruleEle.m_WildCardArray[i] = setWildCard(ruleEle.m_strArrayValue[i]);
				}
				int place = 10;
				for(int i = 0; i < ruleEle.m_strArrayValue.length; i++) {
					int zPlace = ruleEle.m_strArrayValue[i].indexOf("%");
					if(zPlace == -1) {
						zPlace = ruleEle.m_strArrayValue[i].length();
					}
					if(zPlace < place) {
						place = zPlace;
					}
					zPlace = ruleEle.m_strArrayValue[i].indexOf("?");
					if(zPlace == -1) {
						zPlace = ruleEle.m_strArrayValue[i].length();
					}
					if(zPlace < place) {
						place = zPlace;
					}
				}
				ruleEle.m_length = place;
			}
		}
	}

	/**
	 *
	 * @param ruleEle CRGRuleElement
	 * @param val String
	 * @param opType int
	 * @throws Exception
	 */
	private void setIntegerArray(CRGRuleElement ruleEle, String val, int opType) throws Exception
	{
	    ruleEle.m_helpStringIntValue = val;   
                if(CRGRuleGrouperManager.isOperatorTable(opType)) {
			setIntegerArrayTable(ruleEle, val, opType);
		} else {
                         val = val.replaceAll("'", "");
			String[] vals = val.split(",");
			int n = vals != null ? vals.length : 0;
			ruleEle.m_intArrayValue = new int[n];
			for(int i = 0; i < n; i++) {
				if(vals[i].matches("\\D*")) {
					ruleEle.m_intArrayValue[i] = CRGInputOutputBasic.DEFAULT_INT_VALUE;
				} else {
					ruleEle.m_intArrayValue[i] = Integer.parseInt(vals[i].trim());
				}
			}
		}
	}

	/**
	 *
	 * @param ruleEle CRGSuggestion
	 * @param val String
	 * @param opType int
	 * @throws Exception
	 */
	private void setIntegerArray(CRGSuggestion ruleEle, String val, int opType) throws Exception
	{
		if(CRGRuleGrouperManager.isOperatorTable(opType)) {
			setIntegerArrayTable(ruleEle, val, opType);
		} else {
			String[] vals = val.split(",");
			int n = vals != null ? vals.length : 0;
			ruleEle.m_intArrayValue = new int[n];
			for(int i = 0; i < n; i++) {
				ruleEle.m_intArrayValue[i] = Integer.parseInt(vals[i]);
			}
		}
	}

	/**
	 *
	 * @param ruleEle CRGRuleElement
	 * @param val String
	 * @param opType int
	 * @throws Exception
	 */
	private void setDoubleArray(CRGRuleElement ruleEle, String val, int opType) throws Exception
	{
                ruleEle.m_helpStringDoubleValue = val;  
        	if(CRGRuleGrouperManager.isOperatorTable(opType)) {
			setDoubleArrayTable(ruleEle, val, opType);
		} else {
			String[] vals = val.split(",");
			int n = vals != null ? vals.length : 0;
			ruleEle.m_doubleArrayValue = new double[n];
			for(int i = 0; i < n; i++) {
				ruleEle.m_doubleArrayValue[i] = Double.parseDouble(vals[i]);
			}
		}
	}

	/**
	 *
	 * @param ruleEle CRGSuggestion
	 * @param val String
	 * @param opType int
	 * @throws Exception
	 */
	private void setDoubleArray(CRGSuggestion ruleEle, String val, int opType) throws Exception
	{
		if(CRGRuleGrouperManager.isOperatorTable(opType)) {
			setDoubleArrayTable(ruleEle, val, opType);
		} else {
			String[] vals = val.split(",");
			int n = vals != null ? vals.length : 0;
			ruleEle.m_doubleArrayValue = new double[n];
			for(int i = 0; i < n; i++) {
				ruleEle.m_doubleArrayValue[i] = Double.parseDouble(vals[i]);
			}
		}
	}

	/**
	 *
	 * @param ruleEle CRGRuleElement
	 * @param val String
	 * @param opType int
	 * @throws Exception
	 */
	private void setDateArray(CRGRuleElement ruleEle, String val, int opType) throws Exception
	{
                ruleEle.m_helpStringDateValue = val;  
		if(CRGRuleGrouperManager.isOperatorTable(opType)) {
			setDateArrayTable(ruleEle, val, opType);
		} else {
			String[] vals = val.split(",");
			int n = vals != null ? vals.length : 0;
			ruleEle.m_dateArrayValue = new java.util.Date[n];
			ruleEle.m_longArrayValue = new long[n];
			for(int i = 0; i < n; i++) {
                            if(val.length() >=14){
                                     ruleEle.m_dateArrayValue[i] = m_simpleDateAndTimeFormat.parse(vals[i]);
                                    if(ruleEle.m_dateArrayValue[i] != null){
                                        ruleEle.m_longArrayValue[i] = ruleEle.m_dateArrayValue[i].getTime();
                                    }
                            }
                            else if(val.length() >= 8) {
                                    ruleEle.m_dateArrayValue[i] = m_simpleDateFormat.parse(vals[i]);
                                    if(ruleEle.m_dateArrayValue[i] != null){

                                        ruleEle.m_longArrayValue[i] = CRGInputOutputBasic.getDateWithoutTimeAsLong(ruleEle.m_dateArrayValue[i]);
                                    }
                           } else {
                                    ruleEle.m_dateArrayValue[i] = m_simpleTimeFormat.parse(vals[i]);
                                    if(ruleEle.m_dateArrayValue[i] != null){
                                        
                                        ruleEle.m_longArrayValue[i] = ruleEle.m_dateArrayValue[i].getTime();
                                    }
                            }
                            if(ruleEle.m_dateArrayValue[i] == null)
                                ruleEle.m_longArrayValue[i] = 0;
 			}
		}
	}

	/**
	 *
	 * @param ruleEle CRGSuggestion
	 * @param val String
	 * @param opType int
	 * @throws Exception
	 */
	private void setDateArray(CRGSuggestion ruleEle, String val, int opType) throws Exception
	{
		if(CRGRuleGrouperManager.isOperatorTable(opType)) {
			setDateArrayTable(ruleEle, val, opType);
		} else {
			String[] vals = val.split(",");
			int n = vals != null ? vals.length : 0;
			ruleEle.m_dateArrayValue = new java.util.Date[n];
			ruleEle.m_longArrayValue = new long[n];

			for(int i = 0; i < n; i++) {
                            
                            if(val.length() >= 14){
                                ruleEle.m_dateArrayValue[i] = m_simpleDateAndTimeFormat.parse(vals[i]);
                            } else if(val.length() >= 8) {
                                    ruleEle.m_dateArrayValue[i] = m_simpleDateFormat.parse(vals[i]);
                            } else {
                                    ruleEle.m_dateArrayValue[i] = m_simpleTimeFormat.parse(vals[i]);
                            }
                            if(ruleEle.m_dateArrayValue[i] != null){

                                ruleEle.m_longArrayValue[i] = ruleEle.m_dateArrayValue[i].getTime();
                            }else{
                                ruleEle.m_longArrayValue[i] = 0;
                            }
			}
		}
	}

	/**
	 *
	 * @param ruleEle CRGRuleElement
	 * @param val String
	 * @param opType int
	 * @throws Exception
	 */
	protected void setStringArrayTable(CRGRuleElement ruleEle, String val) throws Exception
	{

                ruleEle.m_strArrayValue = getStringArrayFromRuleTables(val, m_ruleIdentififier, m_year);
                ruleEle.m_tableName = val;
		if(ruleEle.m_strArrayValue == null) {
			ruleEle.m_arrayLength = 0;
			return;
		}
		ruleEle.m_arrayLength = ruleEle.m_strArrayValue.length;
//		ruleEle.m_WildCardArray = new int[ruleEle.m_arrayLength];
		int place = 10, zPlace = 0;
		for(int i = 0; i < ruleEle.m_arrayLength; i++) {
			//Apostrophe entfernen
			ruleEle.m_strArrayValue[i] = ruleEle.m_strArrayValue[i].replace("'", "").trim().toUpperCase();
//			ruleEle.m_WildCardArray[i] = setWildCard(ruleEle.m_strArrayValue[i]);
			zPlace = ruleEle.m_strArrayValue[i].indexOf("%");
			if(zPlace == -1) {
				zPlace = ruleEle.m_strArrayValue[i].length();
			}
			if(zPlace < place) {
				place = zPlace;
			}
			zPlace = ruleEle.m_strArrayValue[i].indexOf("?");
			if(zPlace == -1) {
				zPlace = ruleEle.m_strArrayValue[i].length();
			}
			if(zPlace < place) {
				place = zPlace;
			}
		}
		ruleEle.m_length = place;
		if(ruleEle.m_arrayLength == 0) {
			ruleEle.m_length = 0;
		}
	}

	/**
	 *
	 * @param ruleEle CRGSuggestion
	 * @param val String
	 * @param opType int
	 * @throws Exception
	 */
	private void setStringArrayTable(CRGSuggestion ruleEle, String val, int opType) throws Exception
	{

		ruleEle.m_strArrayValue = getStringArrayFromRuleTables(val, m_ruleIdentififier, this.m_year);
		if(ruleEle.m_strArrayValue == null) {
			ruleEle.m_arrayLength = 0;
			return;
		}
		ruleEle.m_arrayLength = ruleEle.m_strArrayValue.length;
//		ruleEle.m_WildCardArray = new int[ruleEle.m_arrayLength];
		int place = 10, zPlace = 0;
		for(int i = 0; i < ruleEle.m_arrayLength; i++) {
			//Apostrophe entfernen
			ruleEle.m_strArrayValue[i] = ruleEle.m_strArrayValue[i].replace("'", "").trim();
//			ruleEle.m_WildCardArray[i] = setWildCard(ruleEle.m_strArrayValue[i]);
			zPlace = ruleEle.m_strArrayValue[i].indexOf("%");
			if(zPlace == -1) {
				zPlace = ruleEle.m_strArrayValue[i].length();
			}
			if(zPlace < place) {
				place = zPlace;
			}
			zPlace = ruleEle.m_strArrayValue[i].indexOf("?");
			if(zPlace == -1) {
				zPlace = ruleEle.m_strArrayValue[i].length();
			}
			if(zPlace < place) {
				place = zPlace;
			}
		}
		ruleEle.m_length = place;
		if(ruleEle.m_arrayLength == 0) {
			ruleEle.m_length = 0;
		}
	}

    protected int[] getIntArrayFromRuleTables(String tableName, String ruleIdentififier, int year) throws Exception {

                return getRuleManager().getTableIntValues(tableName, ruleIdentififier, year);
     }


	/**
	 *
	 * @param ruleEle CRGRuleElement
	 * @param val String
	 * @param opType int
	 * @throws Exception
	 */
	protected void setIntegerArrayTable(CRGRuleElement ruleEle, String val, int opType) throws Exception
	{
                ruleEle.m_intArrayValue = getIntArrayFromRuleTables(val, m_ruleIdentififier, m_year);
//                ruleEle.m_strArrayValue = getStringArrayFromRuleTables(val, m_ruleIdentififier, m_year);

		if(ruleEle.m_intArrayValue == null) {
                    ruleEle.m_arrayLength = 0;
                    return;
		}
                ruleEle.m_arrayLength = ruleEle.m_intArrayValue.length;
	}

	/**
	 *
	 * @param ruleEle CRGSuggestion
	 * @param val String
	 * @param opType int
	 * @throws Exception
	 */
	private void setIntegerArrayTable(CRGSuggestion ruleEle, String val, int opType) throws Exception
	{
                ruleEle.m_intArrayValue = getIntArrayFromRuleTables(val, m_ruleIdentififier, m_year);
		if(ruleEle.m_intArrayValue == null) {
			ruleEle.m_arrayLength = 0;
			return;
		}
		ruleEle.m_arrayLength = ruleEle.m_intArrayValue.length;
	}


        protected double[] getDoubleArrayFromRuleTables(String tableName, String ruleIdentififier, int year) throws Exception {

			return CRGFileRuleManager.ruleManager().getTableDoubleValues(tableName,
										 ruleIdentififier, year);
		
        }
	/**
	 *
	 * @param ruleEle CRGRuleElement
	 * @param val String
	 * @param opType int
	 * @throws Exception
	 */
	protected void setDoubleArrayTable(CRGRuleElement ruleEle, String val, int opType) throws Exception
	{
//                ruleEle.m_intArrayValue = getIntArrayFromRuleTables(val, m_ruleIdentififier, m_year);
                ruleEle.m_doubleArrayValue = getDoubleArrayFromRuleTables(val, m_ruleIdentififier, m_year);

                if(ruleEle.m_doubleArrayValue == null) {
			ruleEle.m_arrayLength = 0;
			return;
		}
		ruleEle.m_arrayLength = ruleEle.m_doubleArrayValue.length;
	}

	/**
	 *
	 * @param ruleEle CRGSuggestion
	 * @param val String
	 * @param opType int
	 * @throws Exception
	 */
	private void setDoubleArrayTable(CRGSuggestion ruleEle, String val, int opType) throws Exception
	{
                ruleEle.m_doubleArrayValue = getDoubleArrayFromRuleTables(val, m_ruleIdentififier, m_year);
		if(ruleEle.m_doubleArrayValue == null) {
			ruleEle.m_arrayLength = 0;
			return;
		}
		ruleEle.m_arrayLength = ruleEle.m_doubleArrayValue.length;
	}

    protected Date[] getDateArrayFromRuleTables(String tableName, String ruleIdentififier, int year) throws Exception{

			return CRGFileRuleManager.ruleManager().getTableDateValues(tableName,
										 ruleIdentififier);
			
    }

    protected long[] getLongArrayFromRuleTables(String tableName, String ruleIdentififier, int year) throws Exception{

			return CRGFileRuleManager.ruleManager().getTableLongValues(tableName,
									   m_ruleIdentififier, m_year);
		
    }
	/**
	 *
	 * @param ruleEle CRGRuleElement
	 * @param val String
	 * @param opType int
	 * @throws Exception
	 */
	protected void setDateArrayTable(CRGRuleElement ruleEle, String val, int opType) throws Exception
	{
                ruleEle.m_dateArrayValue = getDateArrayFromRuleTables(val, m_ruleIdentififier, m_year);
                ruleEle.m_longArrayValue = getLongArrayFromRuleTables(val, m_ruleIdentififier, m_year);
		if(ruleEle.m_longArrayValue == null) {
			ruleEle.m_arrayLength = 0;
			return;
		}
		ruleEle.m_arrayLength = ruleEle.m_longArrayValue.length;
	}

	/**
	 *
	 * @param ruleEle CRGSuggestion
	 * @param val String
	 * @param opType int
	 * @throws Exception
	 */
	private void setDateArrayTable(CRGSuggestion ruleEle, String val, int opType) throws Exception
	{


			ruleEle.m_dateArrayValue = getRuleManager().getTableDateValues(val, m_ruleIdentififier);
			ruleEle.m_longArrayValue = getRuleManager().getTableLongValues(val, m_ruleIdentififier, m_year);
	
		if(ruleEle.m_longArrayValue == null) {
			ruleEle.m_arrayLength = 0;
			return;
		}
		ruleEle.m_arrayLength = ruleEle.m_longArrayValue.length;
	}

	/**
	 * Fügt den in der Regel definierten Interval zu
	 * @param ruleEle CRGRuleElement
	 * @param interval DatInterval
	 * @throws Exception
	 */
	private void setIntervalValues(CRGRuleElement ruleEle, DatInterval interval) throws Exception
	{
		if(interval == null) {
			return;
		}
		ruleEle.interval = new CRGIntervalEntry(interval);
		ruleEle.hasInterval = true;
//		System.out.println("from = " + interval.getIntervalFromCritText() + interval.getFromVal());
//		System.out.println("to = " + interval.getIntervalToCritText() + interval.getToVal());
		/** m_checkDate wird nur bei GK - Prüfung gesetzt. Deswegen kann man die Intervalle zur Zeit des
		 *  Regelparsens ausgewertet. Wenn es nicht der Fall ist (Checkpoint), dann
		 *  muss die Intervallauswertung erst bei Regelauswertung erfolgen
		 */

		if(m_checkDate != null) {
			ruleEle.interval.checkIntervalValues(this.m_checkDate);
			ruleEle.intervalStart = ruleEle.interval.getStartIntervalValue();
			ruleEle.intervalEnd = ruleEle.interval.getEndIntervalValue();
//			ruleEle.interval.printIntervalValues();
		}
	}

	/**
	 *
	 * @param ruleEle CRGRuleElement
	 * @param opType int
	 * @param val String
	 * @param entry CriterionEntry
	 * @throws Exception
	 */
	private void setComputeType(CRGRuleElement ruleEle, int opType, String val, CriterionEntry entry) throws Exception
	{
		if(entry == null) {
			ruleEle.m_operantCompute = true;
			if(val != null && val.length() > 0) {
				ruleEle.m_computeType = CRGRuleGrouperStatics.COMPUTE_TYPE_VALUE;
			} else if(opType > 0) {
				ruleEle.m_computeType = CRGRuleGrouperStatics.COMPUTE_TYPE_OPERATOR;
			}
		} else {
			if(entry.m_index == CRGRuleGrouperStatics.CRITINT_INDEX_NO_CRIT) {
				ruleEle.m_operantCompute = true;
				if(val != null && val.length() > 0) {
					ruleEle.m_computeType = CRGRuleGrouperStatics.COMPUTE_TYPE_VALUE;
				} else if(opType > 0) {
					ruleEle.m_computeType = CRGRuleGrouperStatics.COMPUTE_TYPE_OPERATOR;
				}
			} else {
				if(val != null && val.length() > 0) {
					ruleEle.m_operantCompute = CRGRuleGrouperManager.isOperatorCompute(opType);
					ruleEle.m_computeType = CRGRuleGrouperStatics.COMPUTE_TYPE_BOTH;
				} else {
					ruleEle.m_operantCompute = true;
					ruleEle.m_computeType = CRGRuleGrouperStatics.COMPUTE_TYPE_CRITERIUM;
				}
			}
		}
	}

	/**
	 *
	 * @param ruleEle CRGSuggestion
	 * @param opType int
	 * @param val String
	 * @param entry CriterionEntry
	 * @throws Exception
	 */
	private void setComputeType(CRGSuggestion ruleEle, int opType, String val, CriterionEntry entry) throws Exception
	{
		if(val != null && val.length() > 0) {
			ruleEle.m_operantCompute = CRGRuleGrouperManager.isOperatorCompute(opType);
			ruleEle.m_computeType = CRGRuleGrouperStatics.COMPUTE_TYPE_BOTH;
		} else {
			ruleEle.m_operantCompute = true;
			ruleEle.m_computeType = CRGRuleGrouperStatics.COMPUTE_TYPE_CRITERIUM;
		}
	}

	/**
	 *
	 * @param snl NodeList
	 * @return int
	 */
    @Override
	protected int getSuggestionLength(NodeList snl)
	{
		m_suggestionLength = super.getSuggestionLength(snl); 
		m_suggestions = new CRGSuggestion[m_suggestionLength];
		return m_suggestionLength;
	}

	/**
	 * Gibt die Anzahl der hinterlegten Vorschläge zurück.
	 * <p>
	 * Jede Aktion wird durch eine Regeldefinition definiert.
	 * Hier wird die Anzahl der vorhandenen Vorschlagsdefinitionen angegeben.
	 * @return int : Anzahl der hinterlegten Vorschläge
	 */
	public int getSuggestionCount()
	{
		return m_suggestionLength;
	}

	/**
	 * Gibt die Liste der Vorschlags-Definitionen zurück.
	 * @return CRGSuggestion[] : Objekte mit Definition des Vorschlags.
	 */
	public CRGSuggestion[] getSuggestions()
	{
		return m_suggestions;
	}

	/**
	 *
	 * @return int
	 */
	protected int getIdent()
	{
		try {
			return Integer.parseInt(this.m_ruleIdentififier);
		} catch(Exception e) {
			return m_year;
		}
	}

	/**
	 *
	 * @param actID String
	 * @param crit String
	 * @param op String
	 * @param val String
	 */
	protected void addSuggItem(String actID, String crit, String op, String val)
	{
		try {
			CriterionEntry criter = CRGRuleGrouperManager.getCriterionByWorkText(crit);
			DatRulesAction ract = DatCaseRuleConstants.getActionByWorkID(actID);
			DatRuleSuggestion suggItem = new DatRuleSuggestion(criter, op, val, ract);
			m_lstSuggItems.add(suggItem);
		} catch(Exception ex) {
			ExcException.createException(ex);
		}
	}

	/**
	 *
	 * @param actID String
	 * @param crit String
	 * @param op String
	 * @param val String
	 * @param index int
	 * @throws Exception
	 */
	protected void setSuggestionValues(String actID, String crit, String op, String val, int index, String condition_op, String condition_val) throws Exception
	{
		if(this.m_isCheckpoint) {
			if(!this.isServer()) {
				return;
			}
			super.setSuggestionValues(actID, crit, op, val, index, condition_op, condition_val);
		}
		CriterionEntry entry = CRGRuleGrouperManager.getCriterionByText(crit);
		if(entry != null) {
			if(entry.m_index < 0 && !(entry instanceof SuggCriterionEntry)) {
				m_suggestions[index] = null;
				throw this.createRuleException("invalid index by suggestion criterion " + entry.getWorkText(), null);
			}
			CRGSuggestion sugg = new CRGSuggestion();
			sugg.m_criterionText = entry.getWorkText();
			sugg.m_operantText = op;
			sugg.m_valueText = val;
			sugg.m_criterionType = entry.m_type;
			sugg.m_criterionIndex = entry.m_index;
			sugg.m_conditionOperantText = condition_op;
			sugg.m_conditionValueText = condition_val;
			sugg.m_conditionOperantType = CRGRuleGrouperManager.getOperatorType(condition_op);
			sugg.m_suggType = entry.getM_usedInSugg();
			if(sugg.m_suggType == CriterionEntry.CRIT_RULE_AND_SUGG_AFTER_FEE_VALIDATION && entry.m_depend > 0) {
				CriterionDepend dep = CRGRuleGrouperManager.getCriterionDepend()[entry.m_depend - 1]; // -1 weil die Dependindexes werden von 1 numeriert
				if(dep.m_indexDepend == 0 && dep.m_suggCheckDepend != null) {
					sugg.m_suggCheckDepend = dep.m_suggCheckDepend;
					sugg.m_suggCheckMethod = dep.m_suggCheckMethod;
				}
			}
			if(actID != null && actID.length() > 0) {
				try {
					sugg.m_actionType = Integer.parseInt(actID);
				} catch(Exception ex) {
					throw this.createRuleException("error by parsing suggestion action type", ex);
				}
			} else {
				sugg.m_actionType = DatCaseRuleAttributes.SUGG_CHANGE;
			}
			int opType = CRGRuleGrouperManager.getOperatorType(op);
			sugg.m_operantType = opType;
			this.setComputeType(sugg, opType, val, entry);
			if(entry instanceof SuggCriterionEntry) {
				sugg.setCritIdsToAffect(((SuggCriterionEntry)entry).getM_critsToAffect());
			}
			switch(entry.m_type) {
				case CRGRuleGrouperStatics.DATATYPE_STRING:
				case CRGRuleGrouperStatics.DATATYPE_ARRAY_STRING: {
					setStringValue(sugg, val, opType);
					setStringConditionValue(sugg, condition_val, CRGRuleGrouperManager.getOperatorType(condition_op));
					break;
				}
				case CRGRuleGrouperStatics.DATATYPE_INTEGER:
				case CRGRuleGrouperStatics.DATATYPE_ARRAY_INTEGER: {
					setIntegerValue(sugg, val, opType);
					break;
				}
				case CRGRuleGrouperStatics.DATATYPE_DOUBLE:
				case CRGRuleGrouperStatics.DATATYPE_ARRAY_DOUBLE: {
					setDoubleValue(sugg, val, opType);
					break;
				}
				case CRGRuleGrouperStatics.DATATYPE_DAY_TIME:
				case CRGRuleGrouperStatics.DATATYPE_ARRAY_DAY_TIME:
				case CRGRuleGrouperStatics.DATATYPE_DATE:
				case CRGRuleGrouperStatics.DATATYPE_ARRAY_DATE: {
					setDateValue(sugg, val, opType);
					break;
				}
			}
			m_suggestions[index] = sugg;
		} else {
			m_suggestions[index] = null;
			throw this.createRuleException("invalid index by suggestion criterion " + crit, null);
		}
	}

	/**
	 *
	 * @param spezialText String
	 * @param ex Throwable
	 * @return CRGRuleGroupException
	 */
	private CRGRuleGroupException createRuleException(String spezialText, Throwable ex)
	{
		String tt = "error in rule id: " + this.m_rid +
					"\n\tnumber: " + this.m_number +
					"\n\tidentifier: " + this.m_ruleIdentififier +
					"\n\tcaption: " + this.m_caption +
					"\n\tText: " + this.m_ruleText;
		if(spezialText != null && spezialText.length() > 0) {
			tt = spezialText + "\n" + tt;
		}
		return new CRGRuleGroupException(tt, ex);
	}

	/**
	 *
	 * @return boolean
	 */
	private boolean isServer()
	{
		return true;
	}

	/**
	 * Erzeugt eine Kopie des Regel-Objektes (ohne XML Definition).
	 * @return CRGRule: neues Objekt CRGRule als Kopie der aktuellen Instanz
	 */
    @Override
	public CRGRule clone()
	{
/*		CRGRule rule = new CRGRule(this.m_element, this.m_ruleIdentififier, new Vector(0));*/
                 /* 3.9.5 2015-09-02 DNi: #FINDBUGS - clone()-Methode wird verwendet, aber das Interface Cloneable wird gar nicht implementiert! */
                 CRGRule rule = (CRGRule)copyObject();
		rule.setRuleValues(this);
 		return rule;
	}

	/**
	 * Erzeugt eine inhaltliche Kopie des Regel-Objektes (mit XML Definition).
	 * @return OSObject : CRGRule-Objekt für Regelparser
	 */
    @Override
	public OSObject copyObject()
	{
		CRGRule ret = (CRGRule)super.copyObject();
//		ret.m_rootElement = ret.copyRootElement();
		ret.m_hasCPCrit = m_hasCPCrit; // flag auf true wenn in der regel kommen einige Kriterien aus der Einzelfall - Supergruppe vor
		ret.m_hasRSACrit = m_hasRSACrit; // flag auf true wenn in der regel kommen einige Kriterien aus der MRSA - Supergruppe vor
		ret.m_hasACGCrit = m_hasACGCrit; // flag auf true wenn in der regel kommen einige Kriterien aus der ACG - Supergruppe vor
		ret.m_hasAMBUMedCrit = m_hasAMBUMedCrit; // flag auf true wenn in der regel kommen einige Kriterien aus der ambulante Daten - Supergruppe vor
		ret.m_hasKHCrit = m_hasKHCrit; // flag auf true wenn in der regel kommen einige Kriterien aus der Krankenhausdaten - Supergruppe vor
		ret.m_hasInsCrit = m_hasInsCrit; // flag auf true wenn in der regel kommen einige Kriterien aus der Versicherten Stammdaten - Supergruppe vor
		ret.m_hasAMBUSoLeCrit = m_hasAMBUSoLeCrit; // flag auf true wenn in der regel kommen einige Kriterien aus der Versicherten Stammdaten - Supergruppe vor		ret.m_hasGKRsaCrit = m_hasGKRsaCrit; // flag auf true wenn in der regel kommen einige Kriterien aus der Versicherten Stammdaten - Supergruppe vor
		ret.m_hasCareCrit = m_hasCareCrit; // flag auf true wenn in der regel kommen einige Kriterien aus der Versicherten Stammdaten - Supergruppe vor		ret.m_hasGKRsaCrit = m_hasGKRsaCrit; // flag auf true wenn in der regel kommen einige Kriterien aus der Versicherten Stammdaten - Supergruppe vor
		return ret;

	}

	/**
	 * Setzt die Werte der übergebenden Regel in das aktuelle Objekt.
	 * <p>
	 * Wird beim Kopieren der Regel in der Regel Suite verwendet.
	 * @param rule CRGRule : Regel
	 */
	public void setRuleValues(CRGRule rule)
	{
		m_ruleIdentififier = rule.m_ruleIdentififier;
		super.setRuleValues(rule);
		setRuleGroupType();
	}

	/**
	 * Setzt den Identifikator des Regel-Set, in dem die Regel zugeordnet ist.
	 * <p>
	 * Wird vom Regelparser verwendet.
	 * @param ident String : Identifikator des Regel-Set
	 */
	public void setPoolIdent(String ident)
	{
		m_poolIdent = ident;
	}

	/**
	 * Gibt den eindeutigen Identifikator des Regel-Set zurück, zu dem die Regel zugeordnet ist.
	 * @return String : Identifikator des Regel-Set
	 */
	public String getPoolIdent()
	{
		return m_poolIdent;
	}


	/**
	 * Setzt Meldung zur Prüfung der Regel : wird vom Regelparser verwendet.
	 * @param msg CRGRuleUEMessage : Meldung
	 */
	public void addUEMessages(CRGRuleUEMessage msg)
	{
		if(m_ueMessages == null) {
			m_ueMessages = new Vector();
		}
		m_ueMessages.add(msg);
	}

	/**
	 * Gibt alle Meldungen zur Prüfung der Regel zurück: wird vom Regelparser verwendet.
	 * @return Vector : Liste von Meldungen
	 */
	public Vector getUEMessages()
	{
		return m_ueMessages;
	}

	/**
	 * Gibt an, ob es Meldungen bei der Prüfung der Regel gab : wird vom Regelparser verwendet.
	 * @return boolean : Meldungen ja/nein
	 */
	public boolean hasUEMessages()
	{
		return m_ueMessages != null;
	}

	/**
	 * Prüft Eigenschaft, ob die Regel eine reine CheckpointDRG-Regel ist.
	 * @return boolean : CheckpointDRG-Regel ja/nein
	 */
	public boolean isCPRule()
	{

            boolean isSL = false;
            boolean isMed = false;
            boolean isKH = false;
            boolean isJavaKern = false;
            boolean isCare = false;
            try {
                    isSL = (getRuleManager().isMedicineAllowed() == 1 && m_hasAMBUSoLeCrit);
                    isMed = (getRuleManager().isMedicineAllowed() == 1 && m_hasCPMedCrit);
                    isJavaKern = getRuleManager().isCheckpoint_RuleGrouper() == 1;
                    isKH = (getRuleManager().isCrossCaseModelAllowed() == 1
                                && isJavaKern) && m_hasKHCrit;
                    isCare = isJavaKern && m_hasCareCrit;
            } catch(Exception e) {}
            return(!isJavaKern && ((m_hasCPCrit || isSL || isMed)
                    && !(m_hasRSACrit || m_hasACGCrit /*|| m_hasAMBUMedCrit */ || m_hasKHCrit || m_hasGKRsaCrit)))
                    || (isJavaKern && ((m_hasCPCrit || isSL || isKH || isMed || isCare)
                    && !(m_hasRSACrit || m_hasACGCrit /*|| m_hasAMBUMedCrit */ || m_hasGKRsaCrit)));
	}

	/**
	 * Prüft Eigenschaft, ob RSA-Elemente vorhanden sind.
	 * @return boolean : RSA-Regel ja/nein
	 */
	public boolean isRSARule()
	{
		return(m_hasRSACrit || m_hasKHCrit || m_hasCPMedCrit /* m_hasAMBUMedCrit*/ ||
			m_hasAMBUSoLeCrit) && !(m_hasCPCrit || m_hasACGCrit ||
			m_hasGKRsaCrit);
	}

	/**
	 * Prüft Eigenschaft, ob Regel Elemente aus mehreren Modulen enthält.
	 * @return boolean : Regel enthält Elemente aus mehreren Modulen ja/nein
	 */
	public boolean isGKRule()
	{
		return(m_hasGKRsaCrit || m_hasACGCrit || m_hasCPMedCrit /*m_hasAMBUMedCrit*/ || m_hasAMBUSoLeCrit ||
			m_hasKHCrit ||
			m_hasInsCrit) && !(m_hasCPCrit || m_hasRSACrit);
	}

	/**
	 *
	 * @param what int
	 * @return boolean
	 */
/*	private boolean toCheckRule(int what)
	{
		switch(what) {
			case CHECK_CP_ONLY:
				return isCPRule();
			case CHECK_MRSA_ONLY:
				return isRSARule();
			case CHECK_GK:
				return isGKRule();
			case CHECK_ALL:
				return true;
		}
		return false;
	}
*/
	/**
	 * Setzt den frei definierbaren Regeltyp der Regel.
	 * @param errorType CRGRuleTypes : Definition des Regeltyps
	 */
	public void setRuleDescType(CRGRuleTypes errorType)
	{
		m_errorType = errorType;
	}

	/**
	 * Gibt den aktuell, frei definierbaren Regeltyp zurück.
	 * @return CRGRuleTypes : Definition des Regeltyps
	 */
	public CRGRuleTypes getRuleDescrType()
	{
		return m_errorType;
	}

	/**
	 * Gibt die ID des aktuell, frei definierbaren Regeltyp zurück.
	 * @return int : eindeutige ID des Regeltyps
	 */
    @Override
	public int getRuleDescrTypeID()
	{
		if(this.m_errorType != null) {
			return m_errorType.getIdent();
		} else {
			return super.getRuleDescrTypeID();
		}
	}

	/**
	 * Setzt den eindeutigen Identifikator der Regel.
	 * ACHTUNG: Dieser wird vom System generiert und sollte nicht von aussen gesetzt werden.
	 * @param ruleIdentififier String : Identifier der Regel
	 */
	public void setRuleIdentifier(String ruleIdentififier)
	{
		m_ruleIdentififier = ruleIdentififier;
	}

	/**
	 * Gibt den eindeutigen Identifikator der Regel zurück.
	 * @return String : Identifikator der Regel
	 */
	public String getRuleIdentifier()
	{
		return m_ruleIdentififier;
	}

//=========Abbildung der Vererbung====================================

	/**
	 * Gibt die Regelnummer der Regel zurück.
	 * <p>
	 * Diese kann bei der Regeldefinition frei vergeben werden.
	 * @return String : Regelnummer
	 */
	public String getRuleNumber()
	{
		return m_ruleNumber;
	}

	/**
	 * Gibt die eindeutige ID der Regel zurück.
	 * <p>
	 * Die Regel-ID ist im ganzen System eindeutig, während der Identifier
	 * nur in dem jeweiligen Regel-Set eindeutig vergeben wird.
	 * @return String : ID der Regel
	 */
	public String getRuleID()
	{
		return m_rid;
	}

	/**
	 * Gibt den Kurztext (Kategorie) der Regel zurück.
	 * @return String : Kategorie der Regel
	 */
	public String getRuleShortDescription()
	{
		return m_caption;
	}

	/**
	 * Gibt die Beschreibung der Regel zurück.
	 * @return String : Beschreibung der Regel
	 */
	public String getRuleDescription()
	{
		return m_text;
	}

	/**
	 * Gibt den Vorschlagstext der Regel zurück.
	 * <p>
	 * Neben der Definition der Vorschläge kann ein entsprechender Text hinterlegt werden.
	 * @return String : Vorschlagstext
	 */
	public String getRuleSuggestion()
	{
		return m_suggestion;
	}
        
        public String getMassNumber()
        {
            return m_massNumber;
        }
        
        public String getMedType()
        {
            return m_medType;
        }
        
        public float getProfit()
        {
            return m_profit;
        }
        
	/**
	 * Gibt an, ob diese Regel aktiv geschaltet ist.
	 * <p>
	 * Ist die Regel nicht aktiv, wird diese bei der Regelprüfung nicht angewendet.
	 * @return boolean : true, wenn aktiv
	 */
	public boolean getRuleActiveState()
	{
		return m_isActive;
	}

	/**
	 * Gibt an, ob diese Regel sichtbar geschaltet ist.
	 * <p>
	 * Ist die Regel nicht sichtbar, wird diese zwar berücksichtigt, kann aber
	 * für den Anwender nicht sichtbar gemacht werden.
	 * @return boolean :  true, wenn sichtbar
	 */
	public boolean getRuleVisibleState()
	{
		return m_isVisible;
	}

	/**
	 * Gibt an, ob die Regel vom Ersteller als Entgelt-relevant eingestuft wurde.
	 * <p>
	 * Enthält nicht die Aussage, ob die Prüfregel letztendlich im Einzelfall wirklich Entgelt-relevant ist.
	 * @return boolean : true, wenn Entgelt-relevant
	 */
	public boolean getRuleFeeState()
	{
		return m_isEntgelt;
	}

	/**
	 * Gibt die Notizen zurück, die in der Regel definiert wurden.
	 * <p>
	 * Enthält teilweise lange und komplexe Texte mit carriage return und Sonderzeichen.
	 * @return String : Notizen zur Regel
	 */
	public String getRuleNotice()
	{
		return m_notice;
	}

	/**
	 * Gibt den Beginn der Gültigkeit der Prüfregel zurück.
	 * @return Date : Datum des Gültigkeits-Beginn
	 */
	public java.util.Date getRuleValidFrom()
	{
		return m_adt;
	}

	/**
	 * Gibt das Ende der Gültigkeit der Prüfregel zurück.
	 * @return Date : Datum des Gültigkeits-Ende
	 */
	public java.util.Date getRuleValidTo()
	{
		return m_ddt;
	}

    @Override
	protected void setInterval(Element ele)
	{
		super.setInterval(ele);
		if(hasInterval) {
			DatInterval datInt = new DatInterval(this.intervalFromCrit, this.intervalFromVal, this.intervalToCrit,
								 this.intervalToVal);
			try {
				m_rulesInterval = new CRGIntervalEntry(datInt);
			} catch(Exception ex) {

				ExcException.createException(ex);
			}

		}
	}

	/**
	 * gibt den Regelinterval zurück
	 * @return CRGIntervalEntry
	 */
	public CRGIntervalEntry getRuleInterval()
	{
		return m_rulesInterval;
	}

    @Override
	public boolean equals(Object o)
	{
		return(o instanceof CRGRule && ((CRGRule)o).m_rid.equals(m_rid));
	}

	public boolean isM_hasAMBUSoLeCrit()
	{
		return m_hasAMBUSoLeCrit;
	}

	public boolean isM_hasCPMedCrit()
	{
		return m_hasCPMedCrit;
	}

	public boolean isM_hasAMBUMedCrit()
	{
		return m_hasAMBUMedCrit;
	}

	private String getRolesString(long[] roles)
	{
		if(roles == null) {
			return "";
		}
		StringBuilder rolesStr = new StringBuilder();
		for(int i = 0; i < roles.length; i++) {
			rolesStr.append(String.valueOf(roles[i]));
			if(i != roles.length - 1) {
				rolesStr.append(", ");
			}
		}
		return rolesStr.toString();
	}

	/**
	 *  liefert die Regelattribute, verfasst in der RuleAttributes
	 * Klasse zurück
	 * @return RuleAttributes
	 */
	public RuleAttributes getRuleAttributes()
	{
		RuleAttributes attr = new RuleAttributes();
		attr.dateFromAsString = m_simpleDateFormat.format(m_adt);
		attr.dateToAsString = m_simpleDateFormat.format(this.m_ddt);
		attr.used = m_isActive; //ATTLIST rule used
		attr.ruleType = this.m_typeText; //ATTLIST rule typ ( suggestion | error | warning )
		attr.rolesString = getRolesString(m_roles); //ATTLIST rule role
		attr.number = m_number; //ATTLIST rule number
		attr.rulesNumber = this.getRuleNumber(); //ATTLIST rule rules_number
		attr.caption = this.getRuleShortDescription(); //ATTLIST rule caption
		attr.error_type = m_errorTypeText; //ATTLIST rule errror_type ( ZE | MV | OPS | Sonstige | ICD | DKR | DRG | EBM )
		attr.entgelt = m_isEntgelt; //ATTLIST rule entgelt ( false | true )
		attr.unchange = m_unchangedType == 2; //ATTLIST rule unchange
		attr.rule_notice = m_notice; //ATTLIST rule rules_notice
		attr.suggestion_notice = m_suggestion; // ATTLIST suggestion sugg_text
		attr.rule_text = m_text; //ATTLIST rule text
		attr.rid = getRuleID();
//		attr.rule_as_text;
		attr.visiblesString = getRolesString(m_rolesVisible); // ATTLIST rule visible
		if(this.m_hasSuggestions) {
			attr.suggestions = String.valueOf(m_hasSuggestions);
		} else {
			attr.suggestions = null;
		}

		attr.m_ruleCW = 0d;
		attr.m_deltaCW = 0d;
		return attr;
	}

        /**
         * fügt die visible Flags zusammen
         * @param roles - Rollen, die in der Regel, die mit dieser Regel zusammengefühgt wird, sichtbar sind
         * @param allRoles - alle Rollen des aktuellen Benutzers,ist null, wenn die andere Regel aus der datenbank kommt, 
         * nicht null, wenn die Regel auf demselben AppServer zusammengeführt werden
         * @return true, wenn die Regel geändert wurde, false, wenn die eigenen Flags übernommen werden
         */
	public synchronized boolean mergeRolesVisible(long[] roles, long[] allRoles)
	{
		long[] newRoles = null;
		int pos = 0;
		long[] oldRoles = null;
		System.out.println(this.m_ruleNumber);
		printLongArray("m_rolesVisible ", m_rolesVisible);
		printLongArray("m_rolesNoVisible ", m_rolesNoVisible);
		printLongArray("m_rolesVisibleBasic ", m_rolesVisibleBasic);
		printLongArray("roles ", roles);
		try {
			if(m_rolesVisible != null) {
				oldRoles = new long[m_rolesVisible.length];
				System.arraycopy(m_rolesVisible, 0, oldRoles, 0, m_rolesVisible.length);
			}

			if(allRoles == null) {
				// Mergen beim Speichern der Regel in die Datenbank, hier ist es nicht bekannt, wer die Regel geändert hat,
				// muss man aus den gelöschten und gesetzten Flägs ausgehen
				if(roles == null || roles.length == 0) {
					if(this.m_rolesNoVisible == null || m_rolesNoVisible.length == 0) {
						// es wurden auf dem aktuellen Server keine Visible - Flags gelöscht;
						if(this.m_rolesVisibleBasic == null || this.m_rolesVisibleBasic.length == 0) {
							//die eigene Flags übernehmen
							return false;
						} else {
							if(isEqualLongArrays(m_rolesVisibleBasic, m_rolesVisible)) { // es wurden keine Flags gesetzt
								return false;
							}
							if(m_rolesVisibleBasic.length < m_rolesVisible.length) { // es wurden neue Flags zugefügt
								// es sollen nur die Rollen bleiben, die NICHT in m_rolesVisibleBasic vorhanden sind
								newRoles = new long[m_rolesVisible.length - m_rolesVisibleBasic.length];
								pos = 0;
								for(int i = 0; i < m_rolesVisible.length; i++) {
									if(isInLongArray(m_rolesVisibleBasic, m_rolesVisible[i]) == -1) {
										newRoles[pos] = m_rolesVisible[i];
										pos++;
									}
								}
								m_rolesVisible = new long[pos];
								System.arraycopy(newRoles, 0, m_rolesVisible, 0, pos);
								return true;
							}
						}
					}
					return false;
				}
				if(m_rolesVisible == null) {
					m_rolesVisible = new long[0];
				}
				if(m_rolesNoVisible == null) {
					m_rolesNoVisible = new long[0];
				}
				newRoles = new long[m_rolesVisible.length + roles.length + m_rolesNoVisible.length];
				if(newRoles.length == 0) {
					return false;
				}

//				System.arraycopy(m_rolesVisible, 0, newRoles, 0, m_rolesVisible.length);
				if(m_rolesVisible.length == 0
					&& m_rolesNoVisible.length == 0
					&& roles != null && roles.length > 0
					&& m_rolesVisibleBasic != null && m_rolesVisibleBasic.length > 0) {
					// es sollen aus dem roles nur die Rollen übertragen werden, die nicht in dem m_rolesVisibleBasic vorhanden sind
					for(int i = 0; i < roles.length; i++) {
						if(isInLongArray(m_rolesVisibleBasic, roles[i]) == -1) {
							newRoles[pos] = m_rolesVisible[i];
							pos++;

						}
					}

				} else {
					pos = 0;
					for(int i = 0; i < m_rolesVisible.length; i++) {
						if((isInLongArray(this.m_rolesVisibleBasic, m_rolesVisible[i]) != -1)
							&& (isInLongArray(this.m_rolesNoVisible, m_rolesVisible[i]) == -1)
							&& (isInLongArray(roles, m_rolesVisible[i]) == -1)) {
							continue;
						}
						newRoles[pos] = m_rolesVisible[i];
						pos++;

					}
					for(int i = 0; i < m_rolesNoVisible.length; i++) { // die auf dem aktuellen server gelöschten Flägs werden auf -1 gesetzt
						int ind = isInLongArray(roles, m_rolesNoVisible[i]);
						if(ind != -1) {
							roles[ind] = -1;
						}
					}
					for(int i = 0; i < roles.length; i++) {
						if(roles[i] != -1 && isInLongArray(m_rolesVisible, roles[i]) == -1) {
							newRoles[pos] = roles[i];
							pos++;
						}
					}
				}
				m_rolesVisible = new long[pos];
				if(pos > 0) {
					System.arraycopy(newRoles, 0, m_rolesVisible, 0, pos);
				}
				return!isEqualLongArrays(m_rolesVisible, oldRoles);
			} else {
				// Mergen auf demselben Server, allRoles - die Rollen des Benutzers, der die Regel gerade geändert hat
				// und damit sie auf den Server geschickt
				/*				if(!isLeiter(allRoles))
				  return false;*/
				if(roles == null || roles.length == 0) {
					if(m_rolesVisible == null || m_rolesVisible.length == 0) {
						return false;
					}

// nur eigene behalten
					newRoles = new long[allRoles.length];
					for(int i = 0; i < allRoles.length; i++) {
						for(int j = 0; j < m_rolesVisible.length; j++) {
							if(allRoles[i] == m_rolesVisible[j]) {
								newRoles[pos] = allRoles[i];
								pos++;
								break;
							}
						}
					}
					m_rolesVisible = new long[pos];
					System.arraycopy(newRoles, 0, m_rolesVisible, 0, pos);
					return!isEqualLongArrays(m_rolesVisible, oldRoles);

				}
				int newRolesLen = roles.length;

				if(m_rolesVisible != null && m_rolesVisible.length > 0) {
					newRolesLen += m_rolesVisible.length;
					if(newRolesLen == 0) {
						return false;
					}
					newRoles = new long[newRolesLen];

					pos = 0;
					// alle, die in beiden Arrays vorhanden sind
					for(int i = 0; i < m_rolesVisible.length; i++) {

						for(int j = 0; j < roles.length; j++) {
							if(m_rolesVisible[i] == roles[j]) {
								// nur wenn es keine eigene sind
								boolean found = false;
								for(int n = 0; n < allRoles.length; n++) {
									if(m_rolesVisible[i] == allRoles[n]) {
										found = true;
										break;
									}
								}
								if(!found) {
									newRoles[pos] = m_rolesVisible[i];
									pos++;
									break;
								}
							}
						}
					}
// eigene Rollen, wenn sie vorhanden sind
					for(int i = 0; i < allRoles.length; i++) {
						for(int j = 0; j < m_rolesVisible.length; j++) {
							if(allRoles[i] == m_rolesVisible[j]) {
								newRoles[pos] = allRoles[i];
								pos++;
								break;
							}
						}
					}
// dazugekommene Rollen, wenn sie nicht eigene sind, zufügen
					for(int i = 0; i < roles.length; i++) {
						boolean found = false;
						for(int j = 0; j < m_rolesVisible.length; j++) {
							if(m_rolesVisible[j] == roles[i]) {
								found = true;
								break;
							}
						}
						if(!found) {
// überprüfen, ob diese zu den eigenen Rollen gehört
							boolean found1 = false;
							for(int n = 0; n < allRoles.length; n++) {
								if(roles[i] == allRoles[n]) {
									found1 = true;
									break;

								}
							}
							if(!found1) {
								newRoles[pos] = roles[i];
								pos++;

							}
						}
					}
				}

				m_rolesVisible = new long[pos];
				if(pos > 0) {
					System.arraycopy(newRoles, 0, m_rolesVisible, 0, pos);
				}
				return!isEqualLongArrays(m_rolesVisible, oldRoles);
			}
		} catch(Exception ex) {
			ExcException.createException(ex);
			return false;
		}
	}

	/**
	 * ermittelt die evt. gelöschten visible - Rollen
	 * @param oldVisibles long[]
	 * @param oldNoVisibles long[]
	 */
	public void setNoVisible(long[] oldVisibles, long[] oldNoVisibles)
	{
		if(oldVisibles == null) {
			return;
		}
		if(m_rolesVisible == null) {
			if(m_rolesNoVisible == null) {
				m_rolesNoVisible = oldVisibles;

				return;
			}
		}
		if(isEqualLongArrays(m_rolesVisible, oldVisibles)) {
			return;
		}
		if(oldNoVisibles == null) {
			oldNoVisibles = new long[0];
		}
		long[] deleted = new long[oldNoVisibles.length + m_rolesVisible.length + oldVisibles.length];
		System.arraycopy(oldNoVisibles, 0, deleted, 0, oldNoVisibles.length);
		int pos = 0;
		if(oldNoVisibles.length > 0) {
			pos = oldNoVisibles.length;
		}
		for(int i = 0; i < oldVisibles.length; i++) {
			int ind = isInLongArray(m_rolesVisible, oldVisibles[i]);
			if(ind < 0 && isInLongArray(deleted, oldVisibles[i]) == -1) {
				deleted[pos] = oldVisibles[i];
				pos++;
			}
		}
		if(pos > 0) {
			m_rolesNoVisible = new long[pos];
			System.arraycopy(deleted, 0, m_rolesNoVisible, 0, pos);
		}
	}

	private int isInLongArray(long[] longArray, long test)
	{
		if(longArray == null || longArray.length == 0) {
			return -1;
		}
		for(int i = 0; i < longArray.length; i++) {
			if(longArray[i] == test) {
				return i;
			}
		}
		return -1;
	}

	private boolean isEqualLongArrays(long[] arr1, long[] arr2)
	{
		if(arr1 == null && arr2 == null) {
			return true;
		}
    /* 3.9.5 2015-09-01 DNi: #FINDBUGS - Potenzielle NP-Exception, weil arr1 != null bzw. arr2 != null nicht abgefragt wird.  */
		if(arr1.length != arr2.length) {
			return false;
		}
		for(int i = 0; i < arr1.length; i++) {
			if(isInLongArray(arr2, arr1[i]) == -1) {
				return false;
			}
		}
		return true;
	}

	private boolean isLeiter(long[] allRoles)
	{
		boolean isLeiter = false;
		if(allRoles.length == 2) {
			String roleid1 = String.valueOf(allRoles[0]);
			String roleid2 = String.valueOf(allRoles[1]);
			char[] r1c = roleid1.toCharArray();
			char[] r2c = roleid2.toCharArray();
			isLeiter = ((r1c.length > 0 && r2c.length > 0)
					   && Math.abs(r1c.length - r2c.length) == 1
					   && (r1c[r1c.length - 1] == r2c[r2c.length - 1]));

		}
		return isLeiter;
	}

	private void printLongArray(String name, long[] arr)
	{
		System.out.println(name);
		if(arr == null) {
			System.out.println(" is null");
			return;
		}
		if(arr.length == 0) {
			System.out.println(" ist leer");
			return;
		}
		for(int i = 0; i < arr.length; i++) {
			System.out.println(" i = " + arr[i]);
		}

	}

	/**
	 * voraussetzung dass m_feeGroups is Sortiert
	 * @param feeGroup long
	 * @return boolean
	 */
	public boolean isForFeeGroup(long feeGroup)
	{
		if(feeGroup == 0) {
            return false;
        }
		if(m_feeGroups == null ||  m_feeGroups.length == 0){
			return false;
		}
		return  isInArraySorted(feeGroup, m_feeGroups); //DatCaseRuleMgr.isForRole(roleID, m_roles); // -1 ist für die 'alle' Rolle !
	}

    @Override
	public String toString()
	{
		StringBuilder res = new StringBuilder();
		if(!this.isServer()) {
			if(this.m_rootElement != null) {
				Vector v = m_rootElement.getElements();

				for(int i = 0; i < v.size(); i++) {
					res.append(v.elementAt(i).toString());
				}
			}
		} else {
			if(this.m_ruleElement != null) {
				CRGRuleElement[] ch = m_ruleElement.m_childElements;
				for(int i = 0; i < ch.length; i++) {
					res.append(ch[i].toString());
				}
			}
		}
		return res.toString();
	}

    public boolean isNeedGroupResults() {
        return m_needGroupResults;
    }

    public boolean isM_hasCPCrit() {
        return m_hasCPCrit;
    }

    public boolean isM_hasACGCrit() {
        return m_hasACGCrit;
    }

    public boolean isM_hasKHCrit() {
        return m_hasKHCrit;
    }

    public boolean isM_hasCareCrit() {
        return m_hasCareCrit;
    }
    
    public boolean hasType(int type){
        switch(type){
            case CHECK_CP_ONLY:
                return isM_hasCPCrit();
            case CHECK_KH_ONLY:
                return isM_hasKHCrit();
            case CHECK_ACG_ONLY:
                return isM_hasACGCrit();
            case CHECK_AMBU_SOLE_ONLY:
                return isM_hasAMBUSoLeCrit();
            case CHECK_CP_MED_ONLY:
            case CHECK_AMBU_MED_ONLY:
                return isM_hasAMBUMedCrit();
            case CHECK_AMBU_CARE_ONLY:
                return isM_hasCareCrit();
            default:
                return false;
        }
    }

    private void setRisks(Element ele) {
        try{
            m_risks.clear();
            NodeList nl = ele.getElementsByTagName(DatCaseRuleAttributes.ELEMENT_RISK_AREA);
            if(nl == null){
                return;
            }
            for(int i = 0, len = nl.getLength(); i < len; i++){
                Element riskEl = (Element)nl.item(i);
                String riskName = riskEl.getAttribute(DatCaseRuleAttributes.ATT_RISK_AREA_NAME);
                if(riskName.isEmpty()){
                    continue;
                }
                CRGRisk risk = new CRGRisk(riskName);
                
                risk.setRiskWastePercentValue(riskEl.getAttribute(DatCaseRuleAttributes.ATT_RISK_WASTE_PERCENT_VALUE));
                risk.setRiskAuditPercentValue(riskEl.getAttribute(DatCaseRuleAttributes.ATT_RISK_AUDIT_PERCENT_VALUE));
                risk.setRiskDefaultWasteValue(riskEl.getAttribute(DatCaseRuleAttributes.ATT_RISK_DEFAULT_WASTE_VALUE));
                risk.setRiskComment(getDisplayText(riskEl.getAttribute(DatCaseRuleAttributes.ATT_RISK_COMMENT)));
                m_risks.add(risk);
            }
        }catch(Exception ex) {
            ExcException.createException(ex);
        }
    }
    
    public Element getValuesByElement(Element ele, Document doc){
        Element retElem = super.getValuesByElement(ele, doc);
        return getRisks(retElem, doc);
    }

    private Element getRisks(Element ele, Document doc) {
		try {
			NodeList nl = ele.getElementsByTagName(DatCaseRuleAttributes.ELEMENT_RISK);
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
			Element risk = doc.createElement(DatCaseRuleAttributes.ELEMENT_RISK);

			for(CRGRisk riskEl:m_risks) {
				Element riskArea = doc.createElement(DatCaseRuleAttributes.ELEMENT_RISK_AREA);
				riskArea.setAttribute(DatCaseRuleAttributes.ATT_RISK_AREA_NAME, riskEl.getRiskName());
				riskArea.setAttribute(DatCaseRuleAttributes.ATT_RISK_COMMENT, getXMLText(riskEl.getRiskComment()));
				riskArea.setAttribute(DatCaseRuleAttributes.ATT_RISK_WASTE_PERCENT_VALUE, riskEl.getRiskWastePercentValue());
				riskArea.setAttribute(DatCaseRuleAttributes.ATT_RISK_AUDIT_PERCENT_VALUE, riskEl.getRiskAuditPercentValue());
				riskArea.setAttribute(DatCaseRuleAttributes.ATT_RISK_DEFAULT_WASTE_VALUE, riskEl.getRiskDefaultWasteValue());
		
				risk.appendChild(riskArea);
			}
			ele.appendChild(risk);
		} catch(Exception ex) {
			ExcException.createException(ex, "Regel ID " + this.m_rid + " Nummer " + this.m_ruleNumber);
		}
		return ele;
	}

    public List<CRGRisk> getRuleRisks() {
        return m_risks;
    }

    public byte[] getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(byte[] errorMessage) {
        this.errorMessage = errorMessage;
    }

    private void createSimpleTermList() {
        m_allSimpleTerms.clear();
        if(m_simpleTerms != null){
            m_allSimpleTerms.addAll(m_simpleTerms.values());
        }
        if(m_lstSuggItems != null){
            m_allSimpleTerms.addAll(m_lstSuggItems);
        }
    }

    public List<DatRuleTerm> getAllSimpleTerms() {
        return m_allSimpleTerms;
    }

    public Date getAdmDateBackup() {
        return m_admDate_backup;
    }

    public void setAdmDateBackup(Date pAdmDate) {
        this.m_admDate_backup = pAdmDate;
    }

    public Date getDisDateBackup() {
        return m_disDate_backup;
    }

    public void setDisDateBackup(Date pDisDate) {
        this.m_disDate_backup = pDisDate;
    }

    public int getYearBackup() {
        return m_year_backup;
    }

    public void setYearBackup(int pYear) {
        this.m_year_backup = pYear;
    }
    
    
}
