package de.checkpoint.drg;

import java.io.*;
import java.util.*;

import org.w3c.dom.*;
import de.checkpoint.server.exceptions.*;
import de.checkpoint.server.appServer.*;
import de.checkpoint.server.data.caseRules.DatRuleElement;
import de.checkpoint.server.data.caseRules.DsrRule;
import de.checkpoint.server.data.caseRules.DatInterval;
import de.checkpoint.server.data.caseRules.DatRuleVal;
import de.checkpoint.server.data.caseRules.DatRuleOp;
import de.checkpoint.server.data.caseRules.DatRulerErrorType;
import de.checkpoint.server.data.caseRules.DatCaseRuleAttributes;

public class RulesMgr
{
	private static String m_path = null;
	public final static String RULES_FILE = File.separator + "csrules.xml";
	public final static String RULES_DIRECTORY = File.separator + "rules";
	public final static String RULE_TYPE_FILE = File.separator + "csrules_types.xml";
	public final static String RULE_TABLE_PATH = File.separator + "tables" + File.separator;
	public final static String m_tablesSuff = ".lb";
	public final static String m_ruleTypesURL = "rules/csrules_types.xml";

	private static Document m_document = null;
	private static Document m_docRuleTypes = null;
	private static Hashtable m_ruleElements = null;
	private static Hashtable m_rules = null;
	protected static Vector m_lstError = null;
	protected static Vector m_lstWarning = null;
	protected static Vector m_lstSuggestion = null;
	protected static Vector m_rulesTypes = new Vector();
	protected static boolean m_reloadRules = false;
	protected static RulesMgr m_ruleManager = null;

	protected RulesMgr()
	{
	}

	public static String getRulesPath()
	{
		if(m_path == null) {
//			m_path = AppServer.getWebServerPathProperty() + RULES_DIRECTORY;
		}
		return m_path;
	}
	public static final RulesMgr getRulesManager()
	{
		if(m_ruleManager == null){
			try {
					m_ruleManager = new RulesMgr();
			} catch(Exception e) {
				ExcException.createException(e);
			}
		}
		return m_ruleManager;
	}

	public static String getRulesTablePath()
	{
		return getRulesPath() + RULE_TABLE_PATH;
	}

	public void resetRuleTypes()
	{
		m_reloadRules = false;
		m_rulesTypes = null;
	}

	private static String getOperatorString(String tt)
	{
		try {
			tt = de.checkpoint.server.data.caseRules.DatCaseRuleMgr.getDisplayText(tt);
			return tt;
		} catch(Exception ex) {
			ExcException.createException(ex);
			return tt;
		}
	}

	public static String getElementString(Element ele)
	{
		return getElementString(ele, null);
	}

	public static String getElementString(Element ele, DatRuleElement datRule)
	{
		return getElementString(ele, datRule, null, null);
	}

	//nur interne Abfrage, Regelzusammenstellung
	public static String getElementString(Element ele, DatRuleElement datRule, Vector tableNames, HashMap<Integer, DatRuleVal> simpleTerms)
	{
		try {
			String tt = "";
			boolean isMed = false, isSole = false;
			NodeList lst = ele.getChildNodes();
			for(int i = 0; i < lst.getLength(); i++) {
				Node nd = lst.item(i);
				if(nd instanceof Element) {
                                    String mark = ((Element)nd).getAttribute(DatCaseRuleAttributes.ATT_RULES_MARK);
					if(nd.getNodeName().equals(DatCaseRuleAttributes.
						ELEMENT_RULES_ELEMENT)) {
						boolean isNested = ((Element)nd).getAttribute(DatCaseRuleAttributes.ATTR_NESTED) != null
										   &&((Element)nd).getAttribute(DatCaseRuleAttributes.ATTR_NESTED).
								equalsIgnoreCase("true");
						boolean hasNot = (((Element)nd).getAttribute(DatCaseRuleAttributes.ATT_RULES_VALUE_NOT) != null
										 && ((Element)nd).getAttribute(DatCaseRuleAttributes.ATT_RULES_VALUE_NOT).equalsIgnoreCase("true"));
						String o_bracket = " (";
						String c_bracket = ") ";
						try {
							if(isNested) {
								o_bracket = "{ ";
								c_bracket = "} ";
							}

						} catch(NullPointerException e) {
							// has no attributes
						}
						DatRuleElement newDat = null;
						if(datRule != null) {
							newDat = new DatRuleElement(datRule, mark);  
							datRule.addElement(newDat);
						}
						if(isNested && hasNot){
							o_bracket = "not (";
							if(newDat != null){
								newDat.m_isNot = true;
								newDat.m_not = "true";
							}
						}
						tt = tt + o_bracket + getElementString((Element)nd, newDat, tableNames, simpleTerms) + c_bracket;
					} else if(nd.getNodeName().equals(DatCaseRuleAttributes.
						ELEMENT_RULES_VALUES)) {
						String not = DsrRule.getDisplayText(((Element)nd).getAttribute(DatCaseRuleAttributes.
									 ATT_RULES_VALUE_NOT));
						String suff = " ";
						String nt = not;
						if(nt != null && nt.equals("true")) {
							nt = "!(";
							suff = ") ";
						} else {
							nt = "";
						}
						String krit = DsrRule.getDisplayText(((Element)nd).getAttribute(DatCaseRuleAttributes.ATT_RULES_TYPE));
                                                String method = DsrRule.getDisplayText(((Element)nd).getAttribute(DatCaseRuleAttributes.ATT_RULES_METHOD));
                                                String parameter = DsrRule.getDisplayText(((Element)nd).getAttribute(DatCaseRuleAttributes.ATT_RULES_PARAMETER));
                                                String operator = "";
                                                String value = "";
                                                if(method.length() > 0){
                                                    tt +=  nt + " " + method + "(" + krit +", " + parameter + ")";
                                                }else{
                                                     operator = DsrRule.getDisplayText((((Element)nd).getAttribute(DatCaseRuleAttributes.
                                                                                      ATT_RULES_OPERATOR)));
                                                    value = DsrRule.getDisplayText(((Element)nd).getAttribute(DatCaseRuleAttributes.ATT_RULES_VALUE));
                                                    String v = value.replaceAll("'", "");
                                                    tt = tt + " " + nt + krit;
                                                    String val = value;
                                                    if(krit.equals("Geschlecht")) {
                                                            /* 3.9.5 2015-09-01 DNi: #FINDBUGS - R�ckgabewert wird gar nicht verarbeitet.
                                                             * Korrektur: val = val.trim(); */
                                                            val.trim();
                                                            if(val.equalsIgnoreCase(AppResources.getResource(AppResourceBundle.GENDER_MASCULINE))) {
                                                                    val = "1";
                                                            } else if(val.equalsIgnoreCase(AppResources.getResource(AppResourceBundle.GENDER_FEMININE))) {
                                                                    val = "2";
                                                            } else if(val.equalsIgnoreCase(AppResources.getResource(AppResourceBundle.GENDER_DIVERSE))) {
                                                                    val = "4";
                                                            }
                                                    } else if(krit.equals("Aufnahmewochentag")
                                                            || krit.equals("Entlassungswochentag")
                                                            || krit.equals("Opwochentag")) {
                                                            val = val.toLowerCase();
                                                            if(val.equals("mo")) {
                                                                    val = String.valueOf(Calendar.MONDAY);
                                                            } else if(val.equals("di")) {
                                                                    val = String.valueOf(Calendar.TUESDAY);
                                                            } else if(val.equals("mi")) {
                                                                    val = String.valueOf(Calendar.WEDNESDAY);
                                                            } else if(val.equals("do")) {
                                                                    val = String.valueOf(Calendar.THURSDAY);
                                                            } else if(val.equals("fr")) {
                                                                    val = String.valueOf(Calendar.FRIDAY);
                                                            } else if(val.equals("sa")) {
                                                                    val = String.valueOf(Calendar.SATURDAY);
                                                            } else if(val.equals("so")) {
                                                                    val = String.valueOf(Calendar.SUNDAY);
                                                            }
                                                    } else if(krit.equals("DiagnoseLokalisation")
                                                            || krit.equals("OPSLokalisation")
                                                            || krit.equals("NebendiagnoseLokalisation")) {
                                                            val = val.toLowerCase().replaceAll("'", "");
                                                            char c = val.charAt(0);
                                                            switch(c) {
                                                                    case 'r':
                                                                    case '1':
                                                                            val = "1"; break;
                                                                    case 'l':
                                                                    case '2':
                                                                            val = "2"; break;
                                                                    case 'b':
                                                                    case '3':
                                                                            val = "3"; break;
                                                                    default:
                                                                            val = "0";
                                                            }
                                                    } else if(krit.equals("DiagnoseType")
                                                            || krit.equals("NebendiagnoseType")) {
                                                            val = val.toLowerCase().replaceAll("'", "");
                                                            char c = val.charAt(0);
                                                            switch(c) {
                                                                    case 'k':
                                                                    case '1':
                                                                            val = "1"; break;
                                                                    case 's':
                                                                    case '2':
                                                                            val = "2"; break;
                                                                    case 'z':
                                                                    case '3':
                                                                            val = "3"; break;
                                                                    default:
                                                                            val = "0";
                                                            }
                                                    }
                                                    /*
                                                     * Festlegen von einer Boolschen Variablen, ob es ein Medikamentenkriterium ist
                                                     * das gleiche f�r Sonderleistung.
                                                     * d.h. 2 neue Variablen bei der Klasse DatRuleVal
                                                     * m_isMed und m_isSole
                                                     * Jetzt hier bestimmen, leider �ber die Namen
                                                     * sind die Werte allgemein g�ltiger, dann hier raus: return mit leer
                                                     * und Ebene h�her pr�fen. volker
                                                     */
                                                    else if(krit.equals("ATCCose")
                                                            || krit.equals("PZNCode")
                                                            || krit.equals("MVerordnungsdatum")
                                                            || krit.equals("MPreis")
                                                            || krit.equals("Dosierung")
                                                            || krit.equals("Dosierungseinheit")
                                                            || krit.equals("GesamtPZNPreis")
                                                            || krit.equals("GesamtATCPreis")
                                                            || krit.equals("GesamtMPreis")
                                                            || krit.equals("GesamtPZNDosierung")
                                                            || krit.equals("GesamtATCDosierung")) {
                                                            isMed = true;
                                                    } else if(krit.equals("HPNCose")
                                                            || krit.equals("HiMiNr")
                                                            || krit.equals("SVerordnungsdatum")
                                                            || krit.equals("SPreis")
                                                            || krit.equals("Menge")
                                                            || krit.equals("GesamtHPNPreis")
                                                            || krit.equals("GesamtHiMiNrPreis")
                                                            || krit.equals("GesamtSPreis")
                                                            || krit.equals("GesamtHiMiMenge")
                                                            || krit.equals("GesamtHPNMenge")) {
                                                            isSole = true;
                                                    }

                                                    String op = operator;
                                                    if(val.indexOf("'") == -1) {
                                                            val = "'" + val + "'";
                                                    }
                                                    if(op.equals("@")) {
                                                            op = "IN @";
                                                            val = val.toLowerCase().substring(0, val.length() - 1);
                                                            val = val + m_tablesSuff + "'";
                                                            if(tableNames != null) {
                                                                    tableNames.add(v);
                                                            }
                                                    } else if(op.equals("NOT IN @")) {
                                                            op = "NOT IN @";
                                                            val = val.toLowerCase().substring(0, val.length() - 1);
                                                            val = val + m_tablesSuff + "'";
                                                            if(tableNames != null) {
                                                                    tableNames.add(v);
                                                            }
                                                    } else if(op.equals("@@")) {
                                                            op = "!! @";
                                                            val = val.toLowerCase().substring(0, val.length() - 1);
                                                            val = val + m_tablesSuff + "'";
                                                            if(tableNames != null) {
                                                                    tableNames.add(v);
                                                            }
                                                    } else if(op.equals("#@")) {
                                                            op = "## @";
                                                            val = val.toLowerCase().substring(0, val.length() - 1);
                                                            val = val + m_tablesSuff + "'";
                                                            if(tableNames != null) {
                                                                    tableNames.add(v);
                                                            }
                                                    } else if(op.equals("IN") || op.equals("NOT IN")) {
                                                            op = op + " ";
                                                            val = val.replaceAll("'", "");
                                                            String[] vals = val.split(",");
                                                            val = "[";
                                                            for(int j = 0; j < vals.length; j++) {
                                                                    val = val + "'" + vals[j] + "', ";
                                                            }
                                                            if(val.length() > 2) {
                                                                    val = val.substring(0, val.length() - 2) + "]";
                                                            }
                                                    } else {
                                                            op = op + " ";
                                                    }
                                                    tt = tt + " " + op + val.trim() + suff;
                                                }
						//System.out.println("regelwerte: " + tt + "::" + value + "::" + val);
						/**
						 * f�r Regelsuite intervall f�r den term �berpr�fen
						 */
						DatInterval interval = null;
						 String hasInterval = DsrRule.getDisplayText(( ( (Element) nd).getAttribute(DatCaseRuleAttributes.
							   ATT_RULES_HAS_INTERVAL)));
							  String from = DsrRule.getDisplayText(( ( (Element) nd).getAttribute(DatCaseRuleAttributes.
							   ATT_RULES_INTERVAL_FROM)));
							  String to = DsrRule.getDisplayText(( ( (Element) nd).getAttribute(DatCaseRuleAttributes.
							   ATT_RULES_INTERVAL_TO)));
							  if(hasInterval.equalsIgnoreCase("true")){
							   interval = new DatInterval(from, to);
							  }

						if(datRule != null) {
                                                    DatRuleVal newVal = new DatRuleVal(krit, not, operator, value, datRule, isMed, isSole, interval, method, parameter);
                                                    newVal.m_mark = mark;
                                                    datRule.addElement(newVal);//value.replaceAll("'", ""), datRule)); zu fr�h volker
                                                    if(simpleTerms!= null){
                                                        simpleTerms.put(newVal.hashCode(), newVal);
                                                    }
						}
					} else if(nd.getNodeName().equals(DatCaseRuleAttributes.
						ELEMENT_RULES_OPERATOR)) {
						String op = DsrRule.getDisplayText(((Element)nd).getAttribute(DatCaseRuleAttributes.
									ATT_OPERATOR_TYPE));
						tt = tt + " " + op + " ";
						if(datRule != null) {
                                                    DatRuleOp  newOp = new DatRuleOp(op, datRule);
                                                    newOp.m_mark = mark;
							datRule.addElement(newOp);
						}
					}
				}
			}
			return tt;
		} catch(Exception ex) {
			ExcException.createException(ex);
			return "";
		}

	}


	public static boolean isRulesError(String rid)
	{
		if(m_lstError.contains(rid)) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean isRulesWarning(String rid)
	{
		if(m_lstWarning.contains(rid)) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean isRulesSuggestion(String rid)
	{
		if(m_lstWarning.contains(rid)) {
			return true;
		} else {
			return false;
		}
	}


	private static void writeCSRulesTypes(File f)
	{
		try {
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			PrintWriter wr = new PrintWriter(new OutputStreamWriter(os, "UTF-16"));
			FileOutputStream fos = new FileOutputStream(f);
			DataOutputStream output = new DataOutputStream(fos);

			wr.print("<rules_types rtypes_maxi=\"6\">");
			wr.print("\n");
			wr.print("<rules_type creation_date=\"1099498750765\" display_text=\"Sonstige\" role_id=\"0\" rtype_id=\"0\" rtype_ident=\"0\" rtype_orgid=\"0\" short_text=\"Sonstige\" user_id=\"0\"/>");
			wr.print("\n");
			wr.print("<rules_type creation_date=\"1099498750765\" display_text=\"Deutsche Kodierrichtlinien (DKR)\" role_id=\"0\" rtype_id=\"0\" rtype_ident=\"1\" rtype_orgid=\"0\" short_text=\"DKR\" user_id=\"0\"/>");
			wr.print("\n");
			wr.print("<rules_type creation_date=\"1099498750765\" display_text=\"Diagnoseregeln (ICD)\" role_id=\"0\" rtype_id=\"0\" rtype_ident=\"2\" rtype_orgid=\"0\" short_text=\"ICD\" user_id=\"0\"/>");
			wr.print("\n");
			wr.print("<rules_type creation_date=\"1099498750765\" display_text=\"Prozedurenregeln (OPS)\" role_id=\"0\" rtype_id=\"0\" rtype_ident=\"3\" rtype_orgid=\"0\" short_text=\"OPS\" user_id=\"0\"/>");
			wr.print("\n");
			wr.print("<rules_type creation_date=\"1099498750765\" display_text=\"Fallpauschalenregeln (DRG)\" role_id=\"0\" rtype_id=\"0\" rtype_ident=\"4\" rtype_orgid=\"0\" short_text=\"DRG\" user_id=\"0\"/>");
			wr.print("\n");
			wr.print("<rules_type creation_date=\"1099498750765\" display_text=\"Medizinverst�ndnis\" role_id=\"1\" rtype_id=\"1\" rtype_ident=\"5\" rtype_orgid=\"0\" short_text=\"MV\" user_id=\"1\"/>");
			wr.print("\n");
			wr.print("<rules_type creation_date=\"1099498750765\" display_text=\"Prozedurlogik\" role_id=\"1\" rtype_id=\"1\" rtype_ident=\"6\" rtype_orgid=\"0\" short_text=\"PL\" user_id=\"1\"/>");
			wr.print("\n");
			wr.print("</rules_types>");

			wr.flush();
			os.writeTo(output);
			output.flush();
			output.close();
			fos.close();
			wr.close();
		} catch(Exception ex) {
			ExcException.createException(ex);
		}
	}

	public static void checkRulesTypes() throws java.rmi.RemoteException
	{
		try {
			File f = new File(getRulesPath() + RULE_TYPE_FILE);
			if(!f.exists()) {
				writeCSRulesTypes(f);
			}
		} catch(Exception ex) {
			throw new ExcException(ex.getMessage(), ex, false);
		}
	}

	public Vector getRulesTypes() throws java.rmi.RemoteException
	{
	  /* 3.9.5 2015-09-01 DNi: #FINDBUGS - Wegen der Initialisierung des statischen Feldes m_rulesTypes sollte die Methode synchronized sein. */
//		if(m_rulesTypes == null) {
//			m_rulesTypes = new Vector();
//			Vector rulesTypes = CDbCommonManager.getCommonMgr().getCRGRuleTypes();
//			if(rulesTypes == null)
//				return new Vector();
//			try{
//				for(int i = 0; i < rulesTypes.size(); i++){
//					CRGRuleTypes type = (CRGRuleTypes)rulesTypes.elementAt(i);
//					DatRulerErrorType datType = type.getAsDatErrorType();
//					m_rulesTypes.addElement(datType);
//				}
//			}catch(Exception e)
//			{
//				ExcException.createException(e);
//				return new Vector();
//			}
//		}
		return m_rulesTypes;

	}

	/**
	 * sieh Dokument "Die Rollen und Benutzer Ebene entf�llt."
	 * @param roleID long
	 * @return Vector
	 * @throws RemoteException
	 */
	public Vector getRulesTypeForRole(long roleID) throws java.rmi.RemoteException
	{
		while(m_reloadRules) {
			try {
				Thread.sleep(1000);
			} catch(Exception ex) {
				ExcException.createException(ex);
			}
		}
		Vector lst = getRulesTypes();

		return lst;
	}

	public static void updateRulesTypes() throws java.rmi.RemoteException
	{
		m_reloadRules = true;
	}

	public void updateRulesTypes(java.util.HashMap rtypes) throws java.rmi.RemoteException
	{
		m_reloadRules = true;
		m_rulesTypes = null;
		getRulesTypes();
	}


	public void updateRulesTypes(java.util.List rtypes, long roleID) throws java.rmi.RemoteException
	{
		m_reloadRules = true;
		m_rulesTypes = null;
		getRulesTypes();
	}


	public static void saveOneType(Element root, DatRulerErrorType rt)
	{
		Element ele = m_docRuleTypes.createElement(DatCaseRuleAttributes.ELEMENT_RULES_TYPE);
		ele.setAttribute(DatCaseRuleAttributes.ATT_RULES_TYPE_IDENT, String.valueOf(rt.id));
		java.util.Date cdt = new java.sql.Date(System.currentTimeMillis());
		ele.setAttribute(DatCaseRuleAttributes.ATT_RULES_TYPE_CREATION_DATE, String.valueOf(cdt.getTime()));
		ele.setAttribute(DatCaseRuleAttributes.ATT_RULES_TYPE_DISPLAY_TEXT, String.valueOf(rt.displayText));
		ele.setAttribute(DatCaseRuleAttributes.ATT_RULES_TYPE_SHORT_TEXT, String.valueOf(rt.shortText));
		ele.setAttribute(DatCaseRuleAttributes.ATT_RULES_TYPE_ID, String.valueOf(rt.type));
		ele.setAttribute(DatCaseRuleAttributes.ATT_RULES_TYPE_ROLE, rt.getRolesAsString());
		ele.setAttribute(DatCaseRuleAttributes.ATT_RULES_TYPE_USER, String.valueOf(rt.userid));
		ele.setAttribute(DatCaseRuleAttributes.ATT_RULES_TYPE_ORGID, String.valueOf(rt.m_orgID));
		root.appendChild(ele);
	}

	public  DatRulerErrorType getRulesErrorTypeByText(String text) throws java.rmi.RemoteException
	{
		List ruleTypes = getRulesTypes();
		if(ruleTypes != null && text != null) {
			for(int i = 0, n = ruleTypes.size(); i < n; i++) {
				DatRulerErrorType rt = (DatRulerErrorType)ruleTypes.get(i);
				if(rt.getText().equals(text)) {
					return rt;
				}
			}
		}
		return null;
	}

	public  DatRulerErrorType getRulesErrorTypeByID(int tid) throws java.rmi.RemoteException
	{
		List ruleTypes = getRulesTypes();
		if(ruleTypes != null && tid > 0) {
			for(int i = 0, n = ruleTypes.size(); i < n; i++) {
				DatRulerErrorType rt = (DatRulerErrorType)ruleTypes.get(i);
				if(tid == rt.getIdent()) {
					return rt;
				}
			}
		}
		return null;
	}

	public static DatRulerErrorType getRulesErrorTypeByText(String text,
		List usingTypeList) throws java.rmi.RemoteException
	{
		if(usingTypeList != null && text != null) {
			for(int i = 0, n = usingTypeList.size(); i < n; i++) {
				DatRulerErrorType rt = (DatRulerErrorType)usingTypeList.get(i);
				if(rt.getText().equals(text)) {
					return rt;
				}
			}
		}
		return null;
	}

	public  DatRulerErrorType newRulesErrorType(String shortText, String text, long userID,
		long roleID) throws java.rmi.RemoteException
	{
		List types = getRulesTypes();
		DatRulerErrorType ret = new DatRulerErrorType();
		ret.shortText = shortText;
		ret.displayText = text;
		ret.type = 1;
		ret.userid = userID;
		long[] arr = new long[1];
		arr[0] = roleID;
		ret.setRoleIds(arr);
		types.add(ret);
		updateRulesTypes(types, roleID);
		return ret;
	}
}
