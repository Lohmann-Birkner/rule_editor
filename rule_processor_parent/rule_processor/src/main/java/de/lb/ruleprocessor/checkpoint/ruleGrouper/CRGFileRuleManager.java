package de.lb.ruleprocessor.checkpoint.ruleGrouper;

import java.io.*;
import java.rmi.*;
import java.util.*;

import javax.xml.parsers.*;

import org.w3c.dom.*;
import de.lb.ruleprocessor.checkpoint.server.xml.XMLDOMWriter;
import de.lb.ruleprocessor.checkpoint.ruleGrouper.data.CRGRulePool;
import de.lb.ruleprocessor.checkpoint.ruleGrouper.data.CRGRuleTypes;
import de.lb.ruleprocessor.checkpoint.drg.RulesMgr;
import de.lb.ruleprocessor.checkpoint.server.data.caseRules.DatCaseRuleAttributes;
import de.lb.ruleprocessor.checkpoint.server.exceptions.ExcException;
import de.lb.ruleprocessor.checkpoint.utils.UtlFileManager;
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
public class CRGFileRuleManager extends CRGRuleManager
{

    private static final Logger LOG = Logger.getLogger(CRGFileRuleManager.class.getName());

	public final static String m_rulesName = "csrules.xml";
	public final static String m_rulesNameLinked = "csrules_linked.xml";

	public CRGFileRuleManager()
	{
		super();
	}

	public CRGFileRuleManager(String rulePath)
	{
		super();
		m_rulesPath = rulePath;
		m_isMedAllowed = 1;
		m_isLaborAllowed = 1;
		m_isCheckpoint_RuleGrouper = 1;
	}

	private String getPathByYear(int year)
	{
		return getPathByYear(year, 0);
	}

	private String getPathByYear(int year, int type)
	{
		String path = getRulesPath() + File.separator + String.valueOf(year);
		File aDir = new File(path);
		if(!aDir.exists()) {
			aDir.mkdir();
		}
		return path + File.separator + String.valueOf(year) + (type == 1 ? m_rulesNameLinked : m_rulesName);
	}

	public String getPathByIdentifier(String identifier)
	{
		String path = getRulesPath() + File.separator + identifier;
		File aDir = new File(path);
		if(!aDir.exists()) {
			aDir.mkdir();
		}
		return path + File.separator + identifier + m_rulesName;
	}

	public String getPathByIdentifier(String identifier, int year)
	{
		return getPathByIdentifier(year + "_" + identifier);
	}

	public Vector getRulePoolsFromPath(String rulesPath) throws Exception
	{
		String rulePath;
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
				rulePath = rulesPath + subDirs[i] + File.separator;
				CRGRulePool rpool = new CRGRulePool();
				v.add(rpool);
				rpool.id = i;
				rpool.crgpl_active = true;
				rpool.crgpl_identifier = rulePath;
				try {
					rpool.crgpl_year = Integer.parseInt(subDirs[i]);
				} catch(Exception ex) {
				}
			}
			return v;
		} else {
			throw new CRGRuleGroupWarning("Root path (rulePath) " + rulesPath + " has noch child folders");
		}
		//return null;
	}

	public CRGRule[] getAllRulesFromPath(String rulesPath) throws Exception
	{
		String rulePath;
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
				rulePath = rulesPath + subDirs[i] + File.separator;
				File rDir = new File(rulePath);
				String[] ruleFiles = rDir.list();
				if(ruleFiles == null)
					continue;
				for(int j = 0; j < ruleFiles.length; j++){
					if(!ruleFiles[j].endsWith(m_rulesName))
						continue;
					CRGRule[] rules = getRulesForFile(subDirs[i], new File(rulePath + ruleFiles[j]));
					if(rules != null)
						v.addAll(Arrays.asList(rules));
				}
			}
			CRGRule[] res = new CRGRule[v.size()];
			v.toArray(res);
			return res;
		} else {
			throw new CRGRuleGroupWarning("Root path (rulePath) " + rulesPath + " has noch child folders");
		}
	}


	/**
	 * Parsed die Regeln aus eine XML-Regeldatei
	 * @param rulePath String : Root-pfad der Regel
	 * @param xmlRuleFile File : Directory mit XML-Regel
	 * @return CRGRule[]
	 * @throws Exception
	 */
	public synchronized CRGRule[] getRulesForFile(String rulePath, File xmlRuleFile) throws Exception
	{
		CRGRule[] result = null;
		try {
			Document doc = getXMLDocument(xmlRuleFile);
			if(doc != null) {
				NodeList nl = doc.getElementsByTagName(DatCaseRuleAttributes.ELEMENT_RULES);
				int nlCount = nl != null ? nl.getLength() : 0;
				result = new CRGRule[nlCount];
				for(int i = 0; i < nlCount; i++) {
					Element ele = (Element)nl.item(i);
					CRGRule rule = new CRGRule(ele, rulePath, getRuleTypes(), CRGRule.RULE_ANALYSE_FULL);
					result[i] = rule;
				}
			}
		} catch(Exception ex) {
		  /* 3.9.5 2015-09-01 DNi: #FINDBUGS - Neues Objekt wird weder einer Variablen zugewiesen noch sonstwie verarbeitet. */
			throw new CRGRuleGroupException("error by reading XML-File " + xmlRuleFile.getAbsolutePath() +
				" / " + rulePath, ex);
		}
		return result;
	}

	/**
	 * Creating a XML Document
	 * @param file File
	 * @return Document
	 * @throws RemoteException
	 */
	public static Document getXMLDocument(File file) throws Exception
	{
		if(file != null) {
			try {
				System.setProperty("javax.xml.parsers.DocumentBuilderFactory",
					"org.apache.xerces.jaxp.DocumentBuilderFactoryImpl");
				DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
				DocumentBuilder db = dbf.newDocumentBuilder();
				FileInputStream fin = new FileInputStream(file);
				Document doc = db.parse(fin);
				return doc;
			} catch(Exception ex) {
				throw new CRGRuleGroupException("error by building XML Document", ex);
			}
		}
		return null;
	}

	/**
	 * Liest die Werte aus einer Tabellen Tabelle-Dateio und liefert diese als String-Werte zurück
	 * @param tableName String : Datei-Name der Tabelle
	 * @param identifier String : Pfad zu der Tabellen-Datei
	 * @return String[]
	 * @throws Exception
	 */
	public String[] getTableStringValues(String tableName, String identifier, int year) throws Exception
	{
		synchronized(m_htTableString) {
			try {
				String path = getValidRuleTableName(identifier, tableName);
				Object obj = m_htTableString.get(path);
				if(obj != null) {
					return(String[])obj;
				} else {
					try {
						Vector v = getTableContent(path);
						if(!v.isEmpty()) {
							int n = v.size();
							String[] vals = new String[n];
							for(int i = 0; i < n; i++) {
								vals[i] = v.get(i).toString().replace(" ", "");
							}
							java.util.Arrays.sort(vals);
							m_htTableString.put(path, vals);
							return vals;
						} else {
							return null;
						}
					} catch(CRGRuleGroupException ex) {
						throw ex;
					} catch(Exception ex) {
						throw new CRGRuleGroupException("error by parsing string table values " + path, ex);
					}
				}
			} catch(Exception ex) {
				throw new CRGRuleGroupException("error by reading string table values " + tableName + " from " +
					identifier, ex);
			}
		}
	}

	/**
	 * Liest die Werte aus einer Tabellen Tabelle-Datei und liefert diese als Integer-Werte zurück
	 * @param tableName String : Datei-Name der Tabelle
	 * @param identifier String : Pfad zu der Tabellen-Datei
	 * @return int[]
	 * @throws Exception
	 */
	public int[] getTableIntValues(String tableName, String identifier , int year) throws Exception
	{
		synchronized(m_htTableInt) {
			try {
				String path = getValidRuleTableName(identifier, tableName);
				Object obj = m_htTableInt.get(path);
				if(obj != null) {
					return(int[])obj;
				} else {
					try {
						Vector v = getTableContent(path);
						int n = v.size();
						int[] vals = new int[n];
						for(int i = 0; i < n; i++) {
							//bei falscher Regeleingabe in der Tabelle Strings statt integer
							if(v.get(i).toString().matches("\\d*")) {
								vals[i] = Integer.parseInt(v.get(i).toString());
							} else {
								vals[i] = CRGInputOutputBasic.DEFAULT_INT_VALUE;
							}
						}
						m_htTableInt.put(path, vals);
						return vals;
					} catch(CRGRuleGroupException ex) {
						throw ex;
					} catch(Exception ex) {
						throw new CRGRuleGroupException("error by parsing integer table values " + path, ex);
					}
				}
			} catch(Exception ex) {
				throw new CRGRuleGroupException("error by reading integer table values " + tableName + " from " +
					identifier, ex);
			}
		}
	}

	/**
	 * Liest die Werte aus einer Tabellen Tabelle-Datei und liefert diese als Double-Werte zurück
	 * @param tableName String : Datei-Name der Tabelle
	 * @param identifier String : Pfad zu der Tabellen-Datei
	 * @return double[]
	 * @throws Exception
	 */
	public double[] getTableDoubleValues(String tableName, String identifier, int year) throws Exception
	{
		synchronized(m_htTableDouble) {
			try {
				String path = getValidRuleTableName(identifier, tableName);
				Object obj = m_htTableDouble.get(path);
				if(obj != null) {
					return(double[])obj;
				} else {
					try {
						Vector v = getTableContent(path);
						int n = v.size();
						double[] vals = new double[n];
						for(int i = 0; i < n; i++) {
							vals[i] = Double.parseDouble(v.get(i).toString());
						}
						m_htTableDouble.put(path, vals);
						return vals;
					} catch(CRGRuleGroupException ex) {
						throw ex;
					} catch(Exception ex) {
						throw new CRGRuleGroupException("error by parsing double table values " + path, ex);
					}
				}

			} catch(Exception ex) {
				throw new CRGRuleGroupException("error by reading double table values " + tableName + " from " +
					identifier,
					ex);
			}
		}
	}

	/**
	 * Liest die Werte aus einer Tabellen Tabelle-Datei und liefert diese als Date-Werte zurück
	 * @param tableName String : Datei-Name der Tabelle
	 * @param identifier String : Pfad zu der Tabellen-Datei
	 * @return java.util.Date[]
	 * @throws Exception
	 */
	public java.util.Date[] getTableDateValues(String tableName, String identifier) throws Exception
	{
		synchronized(m_htTableDate) {
			try {
				String path = getValidRuleTableName(identifier, tableName);
				Object obj = m_htTableDate.get(path);
				if(obj != null) {
					return(java.util.Date[])obj;
				} else {
					try {
						Vector v = getTableContent(path);
						int n = v.size();
						java.util.Date[] vals = new java.util.Date[n];
						for(int i = 0; i < n; i++) {
							if(v.get(i).toString().indexOf(':') >= 0) {
								vals[i] = m_simpleTimeFormat.parse(v.get(i).toString());
							} else {
								vals[i] = m_simpleDateFormat.parse(v.get(i).toString());
							}
						}
						m_htTableDate.put(path, vals);
						return vals;
					} catch(CRGRuleGroupException ex) {
						throw ex;
					} catch(Exception ex) {
						throw new CRGRuleGroupException("error by parsing date table values " + path, ex);
					}
				}

			} catch(Exception ex) {
				throw new CRGRuleGroupException("error by reading date table values " + tableName + " from " +
					identifier,
					ex);
			}
		}
	}

	/**
	 * Liest die Werte aus einer Tabellen Tabelle-Datei und liefert diese als Long-Werte zurück
	 * @param tableName String : Datei-Name der Tabelle
	 * @param identifier String : Pfad zu der Tabellen-Datei
	 * @return long[]
	 * @throws Exception
	 */
	public long[] getTableLongValues(String tableName, String identifier, int year) throws Exception
	{
		synchronized(m_htTableLong) {
			try {
				String path = getValidRuleTableName(identifier, tableName);
				Object obj = m_htTableLong.get(path);
				if(obj != null) {
					return(long[])obj;
				} else {
					try {
						Vector v = getTableContent(path);
						int n = v.size();
						long[] vals = new long[n];
						for(int i = 0; i < n; i++) {
							if(v.get(i).toString().indexOf(':') >= 0) {
								vals[i] = m_simpleTimeFormat.parse(v.get(i).toString()).getTime();
							} else {
								vals[i] = m_simpleDateFormat.parse(v.get(i).toString()).getTime();
							}
						}
						m_htTableLong.put(path, vals);
						return vals;
					} catch(CRGRuleGroupException ex) {
						throw ex;
					} catch(Exception ex) {
						throw new CRGRuleGroupException("error by parsing long table values " + path, ex);
					}
				}

			} catch(Exception ex) {
				throw new CRGRuleGroupException("error by reading long table values " + tableName + " from " +
					identifier,
					ex);
			}
		}
	}

	/**
	 * Liest die Werte aus einer Tabellen Tabelle-Datei und schreibt diese als String in ein Vector
	 * @param path String : absoluter Pfad zur Tabellen-Datei
	 * @return Vector
	 * @throws Exception
	 */
	private static synchronized Vector getTableContent(String path) throws Exception
	{
		File f = new File(path);
		Vector v = new Vector();
		if(!f.exists()) {
			//throw new CRGRuleGroupException("table does not exists at " + path);
			//yyy nur eine Nachricht
			System.out.println("table does not exist at " + path);
		} else {
			BufferedReader in = null;
			try {
				in = new BufferedReader(new InputStreamReader(new
					 FileInputStream(f)));
				String line = "";
				while((line = in.readLine()) != null) {
					v.add(line.replaceAll("'", ""));
				}

			} catch(Exception ex) {
				throw new CRGRuleGroupException("error by reading table file " + path, ex);
			}
		}
		return v;

	}

	/**
	 *
	 * @param identifier String  Pfad zu der Tabellen-Datei
	 * @param year int
	 * @return Vector
	 * @throws Exception
	 */
	public Vector getRuleTableNamesForPool(String identifier, int year) throws Exception
	{
		synchronized(m_htTableNamesForPool) {
			try {
				identifier = getRulesPath() + File.separator + getIdentYearFileName(identifier,
							 year) + File.separator + "table";
				Object obj = m_htTableNamesForPool.get(identifier);
				if(obj != null) {
					return(Vector)obj;
				} else {
					Vector v = getTableDirContent(identifier);
					m_htTableNamesForPool.put(identifier, v);
					return v;
				}
			} catch(Exception ex) {
				throw new CRGRuleGroupException("error by reading table directory " + identifier + " " + year, ex);
			}

		}
	}

	/**
	 * Liefert die Liste der Tabellen auf dem Pfad path
	 * @param path String
	 * @return Vector
	 */
	private Vector getTableDirContent(String path) throws Exception
	{
		Vector v = new Vector();
		try {
			File f = new File(path);
			if(!f.exists() || !f.isDirectory()) {
				try {
					f.mkdir();
				} catch(Exception e) {
					throw new CRGRuleGroupException("directory does not exists at " + path);
				}
			} else {
				File[] fList = f.listFiles(new CRGFileTableFilter());
				if(fList != null) {
					for(int i = 0; i < fList.length; i++) {
						String name = fList[i].getName().toUpperCase();
						int ind = name.indexOf(".LB");
						if(ind > 0) {
							name = name.substring(0, ind);
						}
						name = "\'" + name + "\'";
						v.addElement(name);
					}
				}
			}

		} catch(Exception ex) {
			throw new CRGRuleGroupException("error by reading table directory " + path, ex);
		}
		return v;
	}

	private static synchronized String getValidRuleTableName(String identifier, String tableName) throws Exception
	{
		tableName = tableName.replaceAll("'", "");
		identifier = getRulesPath() + File.separator + identifier;
		if(!identifier.endsWith(File.separator)) {
			identifier += File.separator;
		}
		if(!tableName.endsWith(m_tablesSuff)) {
			tableName += m_tablesSuff;
		}
		identifier = identifier + "table" + File.separator + tableName;
		return identifier;
	}

	public boolean writeXMLDocument(Document doc, int year, int type, String identifier)
	{
		synchronized(doc) {
			try {
				String path;
				File xmlFile = null;
				OutputStream os = new ByteArrayOutputStream();
				PrintWriter pw = new PrintWriter(new OutputStreamWriter(os,
								 "UTF-16"));
				if(identifier == null) {
					if(type == 1) {
						path = this.getPathByYear(year, 1);
					} else {
						path = this.getPathByYear(year);
					}
				} else {
					if(identifier.equals(String.valueOf(year))) {
						path = this.getPathByIdentifier(identifier); //identifier;
					} else {
						path = this.getPathByIdentifier(year + "_" + identifier);
					}
				}
				if(path != null) {
					xmlFile = new File(path);
				} else {
					xmlFile = File.createTempFile("test", ".xml");
					xmlFile.deleteOnExit();
				}
				FileOutputStream fos = new FileOutputStream(xmlFile);
				DataOutputStream output = new DataOutputStream(fos);

				XMLDOMWriter writer = new XMLDOMWriter(false);

				writer.setWriterEncoding("UTF-16");
				writer.print(doc, pw, false);
				output.writeBytes(os.toString());
				output.flush();
				output.close();
				if(fos != null) {
					fos.flush();
					fos.close();
					fos = null;
				}
			} catch(Exception ex) {
				ExcException.createException(ex);
				return false;
			}
		}
		return true;
	}

	public void writeXMLDocument(byte[] bytes, int year, int type, String identifier) throws Exception
	{
		String path;
		try {
			if(identifier.equals(String.valueOf(year))) {
				path = this.getPathByIdentifier(identifier); //identifier;
			} else {
				path = this.getPathByIdentifier(year + "_" + identifier);
			}
			File f = new File(path);
			if(f != null && bytes != null) {
				FileOutputStream fos = new FileOutputStream(f);
				OutputStream os = new BufferedOutputStream(fos);
				os.write(bytes);
				os.flush();
				fos.close();
				os.close();
			}
		} catch(Exception ex) {
			throw ex;
		}
	}

	protected Document getXMLDocumentWithStreamAndFullId(String identifier, int year) throws Exception
	{
		try {
			String fileName = getIdentYearFileName(identifier, year);
			String ruleFilePath = this.getRulesPath() + File.separator + fileName + File.separator + fileName +
								  m_rulesName;
			File f = new File(ruleFilePath);
			if(f.exists()) {
				return getXMLDocument(f);
			} else {
// der Pool ist nicht vorhanden, einen neuen Dokument anlegen
				LOG.log(Level.INFO, "Das Regelpool  "+
					String.valueOf(year)
					+ "_" +
					identifier +
					" ist nicht vorhanden, wird leeres Document angelegt");
				return createNewRulePoolDocument();
			}
		} catch(Exception ex) {
			ExcException.createException(ex);
		}

		return null;
	}

	private String getIdentYearFileName(String identifier, int year)
	{
		String fileName = "";
		if(year >= 0) {
			fileName = String.valueOf(year);
		}
		if(!fileName.equals(identifier)) {
			fileName = fileName + "_" + identifier;
		}
		return fileName;
	}

	public byte[] getRuleFileForPool(String identifier, int year) throws Exception
	{
		String fileName = getIdentYearFileName(identifier, year);
		String ruleFilePath = this.getRulesPath() + File.separator + fileName + File.separator + fileName + m_rulesName;
		File f = new File(ruleFilePath);
		if(f.exists()) {
			byte[] b = UtlFileManager.fileManager().getBytes(f); 
			return b; 
		} else {

// versuchen nicht existierenden Pool anzulegen
// der Pool ist nicht vorhanden, einen neuen Dokument anlegen
			LOG.log(Level.INFO, "Das Regelpool  "+
				String.valueOf(year)
				+ "_" +
				identifier +
				" ist nicht vorhanden, wird leeres Document angelegt");
			return createRulePoolDocument(identifier, year);
		}


	}

	public InputStream getRuleStreamForPool(String identifier, int year) throws Exception
	{
		String fileName = getIdentYearFileName(identifier, year);
		String ruleFilePath = this.getRulesPath() + File.separator + fileName + File.separator + fileName + m_rulesName;
		File f = new File(ruleFilePath);
		if(f.exists()) {
			FileInputStream in = new FileInputStream(f);
			return in;
		} else {
// versuchen nicht existierenden Pool anzulegen
// der Pool ist nicht vorhanden, einen neuen Dokument anlegen
			createRulePoolDocument(identifier, year);
			LOG.log(Level.INFO, "Das Regelpool  "+
				String.valueOf(year)
				+ "_" +
				identifier +
				" ist nicht vorhanden, wird leeres Document angelegt");
			f = new File(ruleFilePath);
			if(f.exists()) {
				FileInputStream in = new FileInputStream(f);
				return in;
			} else {
				throw new ExcException("Regelpool " + identifier + "_" + String.valueOf(year)
					+ "konnte nicht gelesen / angelegt werden", false);
			}
		}
	}

	public byte[] loadTable(String name, String identifier, int year) throws Exception
	{
		String key = getKeyForTablesHash(identifier, year, name);
		String ident = getIdentYearFileName(identifier, year);
		String ruleFilePath = getValidRuleTableName(ident, name);
		File f = new File(ruleFilePath);
		if(f.exists()) {
			byte[] b = UtlFileManager.fileManager().getBytes(f);
			m_tableContents.put(key, new String(b));
			return b;
		} else {
			return new byte[0];
		}
	}

        @Override
	public int getRuleSequence() throws Exception
	{
		return -1;
	}

	public int getRuleTypeSequence() throws Exception
	{
		return -1;
	}

	public boolean saveStringTable(String tableName, String identifier, Object[] content) throws Exception
	{
		String path = this.getValidRuleTableName(identifier, tableName);
		File file = new File(path);
		synchronized(file) {
			if(!file.exists()) {
				file.createNewFile();
			}
			BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new
								 FileOutputStream(file)));
			for(int i = 0; i < content.length; i++) {
				String line = (String)content[i];
				out.write(line);
				out.newLine();
			}
			out.close();
		}
		m_htTableString.remove(path);
		return true;
	}

	public Vector getRuleTypes() throws Exception
	{
		String path = getRulesPath() + File.separator + "csrules_types.xml";
		File f = new File(path);
		Vector rulesTypes = null;
		if(f.exists()) {
			Document doc = this.getXMLDocument(f);
			rulesTypes = new Vector();
			Element root = doc.getDocumentElement();
			NodeList nl = root.getElementsByTagName(DatCaseRuleAttributes.ELEMENT_RULES_TYPE);
			for(int i = 0; i < nl.getLength(); i++) {
				Element ele = (Element)nl.item(i);
				CRGRuleTypes rt = new CRGRuleTypes();
				String ident = ele.getAttribute(DatCaseRuleAttributes.ATT_RULES_TYPE_IDENT).trim();
				rt.id = Integer.parseInt(ident);
				rt.setIdent((int)rt.id);
				//rt.creation_date = new java.sql.Date(Long.parseLong(ele.getAttribute(DatCaseRuleMgr.ATT_RULES_TYPE_CREATION_DATE)));
				rt.setDisplayText(ele.getAttribute(DatCaseRuleAttributes.ATT_RULES_TYPE_DISPLAY_TEXT));
				rt.setText(ele.getAttribute(DatCaseRuleAttributes.ATT_RULES_TYPE_SHORT_TEXT));
				try {
					rt.setType(Integer.parseInt(ele.getAttribute(DatCaseRuleAttributes.ATT_RULES_TYPE_ID)));
				} catch(Exception ex) {
					ExcException.createException(ex);
				}
				rulesTypes.add(rt);
			}
		}
		return rulesTypes;
	}

	private void saveOneType(Document doc, Element root, CRGRuleTypes rt)
	{
		Element ele = doc.createElement(DatCaseRuleAttributes.ELEMENT_RULES_TYPE);
		ele.setAttribute(DatCaseRuleAttributes.ATT_RULES_TYPE_IDENT, String.valueOf(rt.getIdent()));
		java.util.Date cdt = new java.sql.Date(System.currentTimeMillis());
		ele.setAttribute(DatCaseRuleAttributes.ATT_RULES_TYPE_CREATION_DATE, String.valueOf(cdt.getTime()));
		ele.setAttribute(DatCaseRuleAttributes.ATT_RULES_TYPE_DISPLAY_TEXT, String.valueOf(rt.getDisplayText()));
		ele.setAttribute(DatCaseRuleAttributes.ATT_RULES_TYPE_SHORT_TEXT, String.valueOf(rt.getText()));
		ele.setAttribute(DatCaseRuleAttributes.ATT_RULES_TYPE_ID, String.valueOf(rt.getType()));
		root.appendChild(ele);
	}

	private Document saveOneType(CRGRuleTypes rt) throws java.rmi.RemoteException
	{
		try {
			String path = getRulesPath() + File.separator + "csrules_types.xml";
			File f = new File(path);
			Vector rulesTypes = null;
			if(f.exists()) {
				Document doc = this.getXMLDocument(f);
				Element root = doc.getDocumentElement();
				saveOneType(doc, root, rt);
				return doc;
			}
		} catch(Exception ex) {
			throw new ExcException(ex.getMessage(), ex, false);
		}
		return null;
	}

	public boolean refreshXMLFile(String user, long usrId, long[] allRoleIds, int year, String identifier) throws Exception
	{
		CRGRule[] rules = this.getRulesForPool(identifier, year, null, 0, CRGRule.CHECK_ALL);
		Document doc = getRulesDocumentForRules(rules);
		boolean res = saveRuleFileForPool(doc, identifier, year, user, null);
		resetRulerInterface(usrId);
		return res;
	}

	public boolean saveRuleFileForPool(Document doc, String identifier, int year, String v_User, long[] allRoles) throws Exception
	{

		if(writeXMLDocument(doc, year, 0, identifier)){
			resetThemePools(identifier, year);
			return true;
		}
		return false;
	}

	public void saveRuleTableFileForPool(String content, String name, String identifier, int year,
		String v_User) throws Exception
	{
		try {
// speichern tabellennahme in hashmap
			String ident = getRulesPath() + File.separator + getIdentYearFileName(identifier,
						   year) + File.separator + "table";
			File aDir = new File(ident);
			if(!aDir.exists()) {
				aDir.mkdir();
			}

			Object obj = m_htTableNamesForPool.get(identifier);
			Vector names = null;
			if(obj != null) {
				names = (Vector)obj;
			} else {
				names = (Vector)m_htTableNamesForPool.get(ident);
			}
			String nm = "\'" + name + "\'";
			if(!names.contains(nm)) {
				names.addElement(nm);
			}
// speichern tabelleninhalt in hashmap
			String key = getKeyForTablesHash(identifier, year, name);
			m_tableContents.put(key, content);

			String[] entries = content.split( UtlFileManager.getSeparatorLine());
			for( int i = 0; i < entries.length; ++i) {
				entries[ i] = entries[ i].replaceAll( "\'", "");
			}
			String path = getValidRuleTableName(identifier, name);
			java.util.Arrays.sort( entries);
			m_htTableString.put(path, entries);

			ident = getIdentYearFileName(identifier, year);
			String tabPath = this.getValidRuleTableName(ident, name);
			File f = new File(tabPath);
			String[] tabContent = new String[0];
			if(content.length() > 0) {
				tabContent = content.split( UtlFileManager.getSeparatorLine());
			}
			if(!f.exists()) {
				f.createNewFile();
			}
			FileOutputStream fos = new FileOutputStream(f);
			OutputStreamWriter osw = new OutputStreamWriter(fos);
			BufferedWriter out = new BufferedWriter(osw);
			for(int i = 0; i < tabContent.length; i++) {
				String line = tabContent[i];
				out.write(line);
				out.newLine();
			}
			fos.flush();
			osw.flush();
			out.flush();
			fos.close();
			osw.close();
			out.close();
		} catch(Exception ex) {
			ExcException.createException(ex);
		}
	}

	public void saveRuleTableFileForPool(byte[] content, String name, String identifier, int year,
		String v_User) throws Exception
	{
		saveRuleTableFileForPool(content,  name,  identifier,  year,
		 v_User, false);
	}

	public void saveRuleTableFileForPool(byte[] content, String name, String identifier, int year,
			String v_User, boolean doThrow) throws Exception
	{
		try {
			String ident = getRulesPath() + File.separator + getIdentYearFileName(identifier,
						   year) + File.separator + "table";
			File aDir = new File(ident);
			if(!aDir.exists()) {
				aDir.mkdir();
			}

			Object obj = m_htTableNamesForPool.get(identifier);
			Vector names = null;
			if(obj != null) {
				names = (Vector)obj;
			} else {
				names = (Vector)m_htTableNamesForPool.get(ident);
			}
			if(names == null) {
				names = new Vector();
				m_htTableNamesForPool.put(identifier, names);
			}
			if(!names.contains(name)) {
				names.addElement("\'" + name + "\'");
			}
			ident = getIdentYearFileName(identifier, year);
			String tabPath = this.getValidRuleTableName(ident, name);
			File f = new File(tabPath);
			if(!f.exists()) {
				f.createNewFile();
			}
			if(f != null && content != null) {
				FileOutputStream fos = new FileOutputStream(f);
				OutputStream os = new BufferedOutputStream(fos);
				os.write(content);
				fos.flush();
				os.flush();
				fos.close();
				os.close();
			}
		} catch(Exception ex) {
			if(doThrow)
				throw ex;
			else
				ExcException.createException(ex);
		}
	}

	private static void writeErrTypesFile(Document doc) throws java.rmi.RemoteException
	{
		try {
			File typesFile = null;
			OutputStream os = new ByteArrayOutputStream();
			PrintWriter pw = new PrintWriter(new OutputStreamWriter(os, "UTF-16"));
			String path = getRulesPath() + File.separator + "csrules_types.xml";

			if(path != null) {
				typesFile = new File(path);
			} else {
				typesFile = File.createTempFile("test", ".xml");
				typesFile.deleteOnExit();
			}

			FileOutputStream fos = new FileOutputStream(typesFile);
			DataOutputStream output = new DataOutputStream(fos);

			XMLDOMWriter writer = new XMLDOMWriter(false);
			writer.setWriterEncoding("UTF-16");
			writer.print(doc, pw, false);
			output.writeBytes(os.toString());
			output.flush();
			output.close();
		} catch(Exception ex) {
			throw new ExcException(ex.getMessage(), ex, false);

		}
	}

	public boolean saveRuleType(CRGRuleTypes crg, int type, String user) throws Exception
	{
		boolean exist = false;
		Vector v = getRuleTypes();
		for(int i = 0, n = v.size(); i < n && !exist; i++) {
			Object obj = v.elementAt(i);
			if(obj != null && obj instanceof CRGRuleTypes) {
				if(((CRGRuleTypes)obj).getText().equals(crg.getText())
					&& ((CRGRuleTypes)obj).getDisplayText().equals(crg.getDisplayText())) {
					exist = true;
				}
			}
		}
		if(!exist) {
			CRGRuleTypes rt = new CRGRuleTypes();
			rt.setText(crg.getText());
			rt.setDisplayText(crg.getDisplayText());
			rt.setIdent(type);
			rt.setType(crg.getType());
			Document doc = saveOneType(rt);
			if(doc != null) {
				writeErrTypesFile(doc);
			}
			RulesMgr.getRulesManager().updateRulesTypes();
		}
		return true;
	}

	/**
	 * fügt die Typen die rageltypen aus dem Vector in die cs_ruletypes datei
	 * @param v Vector
	 * @return boolean
	 * @throws Exception
	 */
	public boolean saveRuleTypes(Vector v, String user) throws Exception
	{
		String path = getRulesPath() + File.separator + "csrules_types.xml";
		File f = new File(path);
		Vector rulesTypes = null;

		if(f.exists()) {
			Document doc = this.getXMLDocument(f);
			/* 3.9.5 2015-09-02 DNi: #FINDBUGS - Potenzielle NP-Exception, weil doc != null nicht abgefragt wird. */
			Element root = doc.getDocumentElement();
			Vector oldTypes = getRuleTypes();

			if(v == null || v.size() == 0) {
				return false;
			}
			int ident = oldTypes.size();
			Iterator it = v.iterator();
			while(it.hasNext()) {
				Object obj = it.next();
				if(obj != null && obj instanceof CRGRuleTypes) {

					CRGRuleTypes rt = (CRGRuleTypes)obj;
					/*boolean exist = false;
					  for(int i = 0, n = oldTypes.size(); i < n && !exist; i++) {
					 Object obj = oldTypes.elementAt(i);
					 if(obj != null && obj instanceof CRGRuleTypes) {
					  if(((CRGRuleTypes)obj).crgrt_shorttext.equals(rt.crgrt_shorttext)
					   && ((CRGRuleTypes)obj).crgrt_displaytext.equals(rt.crgrt_displaytext)) {
					   exist = true;
					  }
					 }
					  }
					  if(!exist) {
//					rt.crgrt_ident = ident;
					 //ident++;
					 rt.crgrt_ident = getRuleSequence();
					 saveOneType(doc, root, rt);
					 }*/
					if(rt.getIdent() < 0) {
						int sequence = getRuleTypeSequence();
						rt.setIdent( sequence);
					}
					saveOneType(doc, root, rt);
				}
			}
			if(doc != null) {
				writeErrTypesFile(doc);
			}
			RulesMgr.getRulesManager().updateRulesTypes();

		}
		return true;
	}

	public boolean deleteRuleType(CRGRuleTypes rt, Vector v) throws Exception
	{
//		rt.delete();
		return true;
	}

	public boolean deleteRuleTypes(Vector rTypes)
	{
		try {
			Vector v = getRuleTypes();
			for(int i = 0, n = rTypes.size(); i < n; i++) {
				v.remove((CRGRuleTypes)rTypes.elementAt(i));
			}
		} catch(Exception ex) {
		}
		return true;
	}

	public boolean updateRulesTypes(Vector rtypes, String user) throws Exception
	{

		System.setProperty("javax.xml.parsers.DocumentBuilderFactory",
			"org.apache.xerces.jaxp.DocumentBuilderFactoryImpl");
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document doc = db.newDocument();

		/* 3.9.5 2015-09-02 DNi: #FINDBUGS - Potenzielle NP-Exception, weil doc != null nicht abgefragt wird. */
		Element root = doc.createElement(DatCaseRuleAttributes.ELEMENT_RULES_TYPES);

		for(int i = 0, n = rtypes.size(); i < n; i++) {
			Object obj = rtypes.get(i);
			if(obj != null && obj instanceof CRGRuleTypes) {
				CRGRuleTypes rt = (CRGRuleTypes)obj;
				if(rt.getIdent() < 0) {
					int sequence = getRuleTypeSequence();
					rt.setIdent(sequence);
				}
				saveOneType(doc, root, rt);
			}
		}
		doc.appendChild(root);
		if(doc != null) {
			writeErrTypesFile(doc);
		}
		return true;
	}

	protected Vector getRulePools(long userid, long[] roleid) throws Exception
	{
		Object obj = m_userPools.get(new Long(userid));
		if(obj != null && obj instanceof Vector)
			return (Vector)obj;
		Vector v = getRulePoolsFromPath();
		m_userPools.put(new Long(userid), v);

		return v;
	}

	public Vector getRuleRightsForRole(long roleid, CRGRulePool rpool) throws Exception
	{
		return new Vector();
	}

	public void resetRuleTypes() throws Exception
	{
	}
	public void resetRuleTables(String ident, int year) throws Exception

	{}

	protected void getCommunicationPools() throws Exception
	{
	}

	public void checkCommunicationPools() throws Exception
	{
		m_communicationPoolsChecked = true;
	}

	protected Vector getPoolYears()
	{
		return getRulePoolYearsFromPath();
	}

    @Override 
    public CRGRule[] getAllRules() throws Exception {
        return getAllRulesFromPath(m_rulesPath);
    }

}

class CRGFileRuleFilter implements FilenameFilter
{
	public boolean accept(File dir, String name)
	{
		return name.endsWith(".xml") && !(name.indexOf("linked") > 0);
	}
}

class CRGFileTableFilter implements FilenameFilter
{
	public boolean accept(File dir, String name)
	{
		return name.endsWith(".lb");
	}
}
