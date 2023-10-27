package de.lb.ruleprocessor.checkpoint.server.appServer;


import java.util.*;

import java.io.FileInputStream;
import java.io.*;
import java.net.URL;

import de.lb.ruleprocessor.checkpoint.server.exceptions.ExcException;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class AppResources
{

    private static final Logger LOG = Logger.getLogger(AppResources.class.getName());
    
	public static final String LEFT = "left";
	public static final String RIGHT = "right";
	/** localised Ressources */
	protected static final String LOCAL_RESOURCE_NAME = "de.checkpoint.server.appServer.AppResourceBundle";
	protected static final String LANGUAGE_PATH = "site" + File.separator + "lib" + File.separator;

	/** Server Resourcen*/
	public static final String SERVER_RESOURCE_NAME = "checkpoint_server";
	protected static final String IMPORTSERVER_RESOURCE_NAME = "checkpoint_importserver";
	protected static final String CLIENT_RESOURCE_NAME = "checkpoint";
	public static final String CLIENT_PRINT_DEBUG_INFO = "PRINT_DEBUG_INFO";

	/** ResourceBundles*/
	
	/** ProtoBufServer*/
	public final static String ProtoBufServerPort= "ProtoBufServerPort";
	/** ProtoBufServer*/
	
	
	private static ResourceBundle m_localResourceBundle = null;
        private static ResourceBundle m_localResourceBundleESKA = null;

	public final static String HOST_NAME = "HOST_NAME";
	public final static String DEFAULT_DB = "DefaultDatabase";

	/** Konstanten für Server Properties */
	public final static String LANGUAGE = "LANGUAGE";
	public final static String COUNTRY = "COUNTRY";
	public final static String DEBUGLEVEL = "DebugLevel";
	public final static String LOGFILE = "LogFile";
	public final static String LOGFILE_NAME = "LogFileName";
        /* Ganglia Server*/
        public final static String METRIC_SERVER_ACTIV = "MetricServerActiv";
        public final static String METRIC_SERVER_IP = "MetricServerIp";
        public final static String METRIC_GROUP_NAME = "MetricGroupName";
        public final static String METRIC_SPOOF_HOST_IP = "MetricSpoofHostIp";        
        public final static String METRIC_SPOOF_HOST_NAME = "MetricSpoofHostName";
        public final static String METRIC_TIME_DELAY_SECONDS = "MetricDelaySeconds";
        

	public final static String UPDATE_FILE = "updateFile";
	public final static String UPDATE_COMMON_FILE = "updateCommonFile";

	public final static String BATCHTEMPDIR = "batchGrouperTempDir";

	public final static String SMTP_SERVER = "SMTP_SERVER";
	public final static String SMTP_SERVER_INTERNAL = "SMTP_SERVER_INTERNAL";
	public final static String MAIL_TO = "MAIL_TO";
	public final static String MAIL_FROM = "MAIL_FROM";

	public final static String JDBC_SECURE_STATE = "secureState";
	public final static String JDBC_SQLSERVER_URL = "jdbcUrl";
	public final static String JDBC_GWI_URL = "jdbcGWIUrl";
	public final static String JDBC_GWI_DRIVER = "jdbcGWIDriver";
	public final static String JDBC_DRIVER = "jdbcDriver";
	public final static String DB_ADAPTOR = "dbAdaptor";
	public final static String DB_HOST = "dbHost";
	public final static String DB_DATABASE = "dbDatabase";
	public final static String DB_USER = "dbUser";
	public final static String DB_PASSWORD = "dbPassword";
//	public final static String DB_SID = "dbSID";
	public final static String DB_PORT = "dbPort";
	public final static String DB_ADAPTOR_ORA = "de.checkpoint.db.OracleServerInterface";
	public final static String DB_ADAPTOR_MS_SQL_SERVER_INTERFACE = "de.checkpoint.db.SQLServerInterface";
	public final static String DB_ADAPTOR_MS_SQL_JTDS_SERVER_INTERFACE = "de.checkpoint.db.JTDSServerInterface";
	public final static String DB_DEBUGLEVEL = "dbSQLDebugLevel";
	public final static String DB_DEBUGPATH = "dbSQLDebugFilePath";
	public final static String DB_DEBUGSAVEKEEPING = "dbSQLDebugFileSavekeeping";
	public final static String DB_GWI_USER = "dbGWIUser";
	public final static String DB_GWI_PASSWORD = "dbGWIPassword";
	public final static String SYSTEM_OUT_REDIRECT = "SYSTEM_OUT_REDIRECT";
        public final static String LOG_GROUP_DETAILS = "LOG_GROUP_DETAILS";

	public final static String SAP_STORNO_PASSWORD = "dbGWIPassword";

	/** Importer GWIImportDriver **/
	public final static String GWI_IMPORTER = "GWIImportDriver";

	public final static String PATIENT_VERSIONS = "PATIENT_VERSIONS";

	public final static String RMIRegistryPort = "RMIRegistryPort";
	public final static String RMIRegistryBindServerName = "RMIRegistryBindServerName";
	public final static String RMIObjectServerPort = "RMIObjectServerPort";
	public final static String RMIObjectServerExternalName = "RMIObjectServerExternalName";
	public final static String RMIObjectServerXORPattern = "RMIObjectServerXORPattern";

	/** Upload Server fuer Datenimport (§21 und FDSE) **/
	public final static String IMPORTDBADAPTOR = "ImportServer.dbAdaptor";
	public final static String IMPORTDBHOST = "ImportServer.dbHost";
	public final static String IMPORTDBUSER = "ImportServer.dbUser";
	public final static String IMPORTDBPASSWORD = "ImportServer.dbPassword";
	public final static String IMPORTUPLOADPORT = "ImportServer.uploadPort";
	public final static String IMPORTUPLOADDIR = "ImportServer.uploadDirectory";
	public final static String IMPORTSQLDEBUGLVL = "ImportServer.dbSQLDebugLevel";
	public final static String IMPORTSQLDEBUGPAT = "ImportServer.dbSQLDebugFilePath";

	/** Upload Client fuer Datenimport (§21 und FDSE) **/
	public final static String IMPORTUPLOADSERVER = "ImportClient.uploadServer";
	public final static String IMPORTUPLOADSERVERPORT = "ImportClient.uploadPort";

	/** Konstanten für Client Properties */
	public final static String CONTEXT_CASE_TREE_SHOW_LEVEL = "contextCaseTreeShowLevel";
	public final static String GK_TREE_SHOW_LEVEL = "GKTreeShowLevel";
	public final static String DETAIL_CASE_TREE_SHOW_LEVEL = "detailCaseTreeShowLevel";
	public final static String LOAD_SERVER = "LOAD_SERVER";
	public final static String WEB_SERVER_PORT = "WebServerPort";
	public final static String GROUPER_TYPE = "GrouperType";
	public final static String JOBSERVER = "JobServer";
/*	public final static String CHECKPOINT_VERSION = "CHECKPOINT_VERSION";
	public final static String CHECKPOINT_FM_VERSION = "CHECKPOINT_FM_VERSION";
	public final static String CHECKPOINT_DATA_VERSION = "CHECKPOINT_DATA_VERSION";*/
	public final static String WORD_PATH = "WORD_PATH";
	public final static String RULE_POOL_TYPE = "RULE_POOL_TYPE";
	public final static String RULE_CRIT_MED = "RULE_CRIT_MED";
	public final static String RULE_CRIT_CROSS_CASE = "RULE_CRIT_CROSS";
	public final static String RULE_CRIT_LABOR = "RULE_CRIT_LABOR";
	public final static String CHECKPOINT_RULE_GROUPER = "Checkpoint_RuleGrouper";
	public final static String CHECKPOINT_MY_APP_SERVER = "CHECKPOINT_MY_APP_SERVER";
	public final static String CHECKPOINT_OTHER_APP_SERVER = "CHECKPOINT_OTHER_APP_SERVER";
	public final static String ORACLE_RULES = "OracleRules";
//	public final static String DB_SID_ORACLE = "dbSID_Oracle";
//	public final static String DB_PORT_ORACLE = "dbPort_Oracle";
	public final static String RULE_KERNEL ="RULE_KERNEL";
	public final static String CLIENT_LOG_PATH ="CLIENT_LOG_PATH";
	public final static String RMI_DEBUG ="RMIDebug";
	public final static String RULE_TEST = "RULE_TEST";
	public final static String DATA_ACG = "DATA_ACG";
	public final static String DATA_RSA = "DATA_RSA";
	public final static String DATA_MEDI = "DATA_MEDI";
	public final static String DATA_SOLE = "DATA_SOLE";
	public final static String DATA_HOSPITAL = "DATA_HOSPITAL";
	public final static String DATA_FILTER = "DATA_FILTER";
	public final static String FEE_GROUP_MERGE_CASE= "FEE_GROUP_MERGE_CASE";
	public final static String CARE_DATA= "CARE_DATA";
	public final static String BUD_GETOE_FROM_ENCOUNTER = "BUD_GETOE_FROM_ENCOUNTER";
	public final static String BUD_MODUL_6_11_STRALSUND = "BUD_MODUL_6_11_STRALSUND";
    public final static String NUMBER_SIMULTANEOUS_GROUPER = "NUMBER_SIMULTANEOUS_GROUPER";
	public final static String DISABLE_JAVA_VERSION_CHECK = "DISABLE_JAVA_VERSION_CHECK";
        public final static String SOLE = "SOLE";
        public final static String RULE_FILTER = "RULE_FILTER";
        
	//** Konstante für MedSC Erweiterung */
	public final static String MEDSC = "MedSC";

	// Datumskonstanten: Vorsicht - muessen zur Internationalisierung in den Ressource-String
	// fuer Java
	public static final String DATEFORMAT_DATE = "dd.MM.yyyy";
	public static final String DATEFORMAT_MINUTE = "dd.MM.yyyy HH:mm";
	public static final String DATEFORMAT_SECOND = "dd.MM.yyyy HH:mm:ss";
	public static final String DATEFORMAT_TIME = "HH:mm";

	// Konstanten fuer die Datenbank-Datumsformate
	public static final String DATEFORMAT_DB_DATE = "DD.MM.YYYY";
	public static final String DATEFORMAT_DB_MINUTE = "DD.MM.YYYY HH24:MI";
	public static final String DATEFORMAT_DB_SECOND = "DD.MM.YYYY HH24:MI:SS";

	// Konstanten fuer Datenbank-Commandos
	public static final String C_COMMIT = "COMMIT";
	public static final String C_ROLLBACK = "ROLLBACK";
	public static final String C_SELECT = "SELECT";
	public static final String C_PURGE = "PURGE";

	//Archiv
	public final static String ARCHIVE_TYPE = "ARCHIVE_TYPE";
	public final static String ARCHIVE_LOAD_TYPE = "ARCHIVE_LOADTYPE";
	public final static String ARCHIVE_HEYDT_SERVER = "ARCHIVE_HEYDTSERVER";
	public final static String ARCHIVE_DEBUGMODE = "ARCHIVE_DEBUGMODE";
	public final static String ARCHIVE_HEYDT_PORT = "ARCHIVE_HEYDTPORT";
	public final static String ARCHIVE_HEYDT_MANDANT = "ARCHIVE_HEYDTMANDANT";
	public final static String ARCHIVE_HEYDT_MANDANT_PRAEFIX = "ARCHIVE_HEYDTMANDANT_PRAEFIX";
	public final static String ARCHIVE_HEYDT_POLLINGFOLDER = "ARCHIVE_HEYDTPOLLINGFOLDER";
	public final static String ARCHIVE_HEYDT_ARCHIVEFOLDER = "ARCHIVE_HEYDTARCHIVEFOLDER";
	public final static String ARCHIVE_HEYDT_USER = "ARCHIVE_HEYDTUSER";
	public final static String ARCHIVE_HEYDT_PASSWORD = "ARCHIVE_HEYDTPASSWORD";
	public final static String ARCHIVE_HEYDT_HOSPITALKZ = "ARCHIVE_HEYDTHOSPITALKZ";

	/** Parameter für Authetifizierung per ActivDirectory **/
	public final static String LDAP_UseAuthentification = "LDAP.UseAuthentification";
	public final static String LDAP_SERVER = "LDAP.Server";
	public final static String LDAP_PORT = "LDAP.Port";
	public final static String LDAP_CONNECTPARAMETER = "LDAP.ConnectParameter";

	public final static String MESSAGE_SERVER = "messageServer";

	/** Konstanten für den ORACLE-Datenbanken*/
	public static final String C_CHECKPOINTDB = "checkpointDB";
	public static final String C_CHECKPOINTDB_ROLE = "CHECKPOINTDB_COMMON_CPUSER";
	public static final String C_COMMON = "common";
	public static final String C_ORA_PORT_1521 = "1521";
	public static final String C_ORA_SID_ORCL = "ORCL";

	// Konstanten für jar Update
	public static final String C_APP_UPDATE = "APP_UPDATE";

	/** Auf dem Server definierten ResourceBundle*/
	private static ResourceBundle m_serverResourceBundle = null;
	private static ResourceBundle m_ImportserverResourceBundle = null;
	private static HashMap m_serverResources = null;

	/** ResourceBundle für den Client*/
	private static ResourceBundle m_clientResourceBundle = null;
	private static String LOCAL_RESOURCE_FILE = "RESOURCEFILE";

	private static String m_clientResourceName = CLIENT_RESOURCE_NAME;
        
        
        public static final String MDK_DA_VERFAHRENSKENNUNG = "MDK_DA_VERFAHRENSKENNUNG";
        public static final String MDK_DA_KKS_EMPFAENGER_IK = "MDK_DA_KKS_EMPFAENGER_IK";
        public static final String MDK_DA_ABSENDER_IK = "MDK_DA_ABSENDER_IK";
        public static final String MDK_DA_TA_VERSION = "MDK_DA_TA_VERSION";
        public static final String MDK_DA_POSTEINGANG_FOLDER = "MDK_DA_POSTEINGANG_FOLDER";
        public static final String MDK_DA_POSTAUSGANG_FOLDER = "MDK_DA_POSTAUSGANG_FOLDER";
        public static final String MDK_DA_IMPORT_BEGUTACHTUNG_ERROR_FOLDER = "MDK_DA_IMPORT_BEGUTACHTUNG_ERROR_FOLDER";
        public static final String MDK_DA_IMPORT_BEGUTACHTUNG_ARCHIV_FOLDER = "MDK_DA_IMPORT_BEGUTACHTUNG_ARCHIV_FOLDER";
        public static final String MDK_DA_IMPORT_BEGUTACHTUNG_DEFAULT_DOCTYPE_ID = "MDK_DA_IMPORT_BEGUTACHTUNG_DEFAULT_DOCTYPE_ID";
        public static final String MDK_DA_USE_PRUEFKENNZEICHEN = "MDK_DA_USE_PRUEFKENNZEICHEN";
        public static final String MDK_DA_IMPORT_BEGUTACHTUNG_ANHANG_BASE64DECODING="MDK_DA_IMPORT_BEGUTACHTUNG_ANHANG_BASE64DECODING";

	public AppResources()
	{
	}

	static synchronized ResourceBundle getServerResourceBundle() throws Exception
	{
		if(m_serverResourceBundle == null) {
			m_serverResourceBundle = getResourceBundle(SERVER_RESOURCE_NAME,
									 Locale.getDefault());
		}
		return m_serverResourceBundle;
	}

	public static void resetServerProperties(){
		m_serverResourceBundle = null;
	}

	static ResourceBundle getImportServerResourceBundle() throws Exception
	{
    /* 3.9.5 2015-09-01 DNi: #FINDBUGS - Wegen der Initialisierung des statischen Feldes m_ImportserverResourceBundle sollte die Methode synchronized sein. */
		if(m_ImportserverResourceBundle == null) {
			m_ImportserverResourceBundle = getResourceBundle(
										   IMPORTSERVER_RESOURCE_NAME, Locale.getDefault());
		}
		return m_ImportserverResourceBundle;
	}

	public static ResourceBundle getClientResourceBundle() throws Exception
	{
    /* 3.9.5 2015-09-01 DNi: #FINDBUGS - Wegen der Initialisierung des statischen Feldes m_clientResourceBundle sollte die Methode synchronized sein. */
		if(m_clientResourceBundle == null){
			try{
				m_clientResourceBundle = getResourceBundle(m_clientResourceName,
										 Locale.getDefault());
			}catch(Exception e){

                                LOG.log(Level.WARNING,"checkpoint.properties - Datei  " + m_clientResourceName + " nicht gefunden. Es wird die standart Datei genommen");;	
				m_clientResourceBundle = getResourceBundle(CLIENT_RESOURCE_NAME,
										 Locale.getDefault());
			}
		}
		return m_clientResourceBundle;
	}

	public static void setClientResourceName(String name) throws Exception
	{
		if(m_clientResourceName.equals(CLIENT_RESOURCE_NAME)) {
			m_clientResourceName = name;
			//3.9.5 2015-08-24 DNi: Wenn der Name der Property-Datei geändert wird, soll auch das Neuladen der Resourcedatei erzwungen werden.
			m_clientResourceBundle = null; //NEU
		}
	}

       
	private static ResourceBundle getResourceBundle(String resourceName,
		Locale locale) throws Exception
	{
		try {
			return ResourceBundle.getBundle(resourceName, locale);
		} catch(MissingResourceException mre) {
			throw new ExcException("The Resource-Bundle " + resourceName +
				" could not be found!", mre, false);
		}
	}



	public static String getResource(String key)
	{
		return getResource(key, null);
	}

	public static String getResource(String key, String defaultText)
	{
            if (defaultText == null) {
                    return key;
            } else {
                    return defaultText;
            }
		
	}
}
