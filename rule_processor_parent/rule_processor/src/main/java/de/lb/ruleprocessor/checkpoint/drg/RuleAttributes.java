package de.lb.ruleprocessor.checkpoint.drg;

import java.nio.charset.Charset;
// für DAK online grouper auskommentieren
//import de.checkpoint.server.appServer.AppResourceBundle;
//import de.checkpoint.server.appServer.AppResources;

/**
 * Objekt mit Regelinhalten.
 *
 * <p>
 * Beinhaltet die Information einer definierten Regel.
 *
 */
public class RuleAttributes
{
	public String dateFromAsString;
	public String dateToAsString;
	public boolean used;				//ATTLIST rule used
	public String ruleType;			//ATTLIST rule typ ( suggestion | error | warning )
	public String rolesString;			//ATTLIST rule role
	public String number;			    //ATTLIST rule number
	public String rulesNumber;	        //ATTLIST rule rules_number
	public String caption;		        //ATTLIST rule caption
	public String error_type;			//ATTLIST rule errror_type ( ZE | MV | OPS | Sonstige | ICD | DKR | DRG | EBM )
	public boolean entgelt;			//ATTLIST rule entgelt ( false | true )
	public boolean unchange;			//ATTLIST rule unchange
	public String rule_notice;		    //ATTLIST rule rules_notice
	public String suggestion_notice;   // ATTLIST suggestion sugg_text
	public String rule_text;			//ATTLIST rule text
	public String rid;
	public String rule_as_text;
	public String visiblesString;       // ATTLIST rule visible
//	ArrayList suggestions;
	public String suggestions;
//	java.util.Vector suggestions;

	public double m_ruleCW = 0d;
	public double m_deltaCW = 0d;
	private static Charset charset = Charset.forName("UTF-8");

/*
	private String dateFromAsString;
	private String dateToAsString;
	private boolean used;				//ATTLIST rule used
	private String ruleType;			//ATTLIST rule typ ( suggestion | error | warning )
	private String rolesString;			//ATTLIST rule role
	private String number;			    //ATTLIST rule number
	private String rulesNumber;	        //ATTLIST rule rules_number
	private String caption;		        //ATTLIST rule caption
	private String error_type;			//ATTLIST rule errror_type ( ZE | MV | OPS | Sonstige | ICD | DKR | DRG | EBM )
	private boolean entgelt;			//ATTLIST rule entgelt ( false | true )
	private boolean unchange;			//ATTLIST rule unchange
	private String rule_notice;		    //ATTLIST rule rules_notice
	private String rule_text;			//ATTLIST rule text
	private String rid;
	private String rule_as_text;
//	ArrayList suggestions;
	private String suggestions;
//	java.util.Vector suggestions;

	private double m_ruleCW = 0d;
	private double m_deltaCW = 0d;*/


	/**
	 * Konstruktor
	 */
	public RuleAttributes()
	{
//		int i = 0;
	}

	/**
	 * Gibt den Beginn des Gueltigkeitzeitraumes der Regel zurück.
	 * @return String
	 */
	public String getDateFromAsString(){
        return dateFromAsString;
    }

	/**
	 * Gibt das Ende des Gueltigkeitzeitraumes der Regel zurück.
	 * @return String
	 */
	public String getDateToAsString(){
        return dateToAsString;
    }

	/**
	 * Gibt zurück, ob die Regel aktiviert ist.
	 * <p>
	 * true - Regel ist aktiv, ansonsten false
	 * @return boolean
	 */
	public boolean isUsed(){
        return used;
    }

	/**
	 * Gibt den Typ der Regel zurück.
	 * 
	 * suggestion - Hinweis
	 * @return String
	 */
	public String getRuleType(){
        return ruleType;
    }

	/**
	 * Gibt die verwendenten Rollenberechtigung als String zurück.
	 * <p>
	 * Wenn z.B. die Regel für die Rolle mit der ID 1 und 3 freigebene ist,
	 * wird als Ergebnis "1, 3" zurück gegeben.
	 * @return String
	 */
	public String getRolesString(){
		return rolesString;
	}

	/**
	 * Gibt die Rollenberechtigung, für die die Regel ein visible - Flage hat, als String zurück.
	 * <p>
	 * Wenn z.B. die Regel für die Rolle mit der ID 1 und 3 Visible - Flag hat,
	 * wird als Ergebnis "1, 3" zurück gegeben.
	 * @return String
	 */
	public String getVisible(){
		return visiblesString;
	}


	/**
	 * Gibt die Regelnummer zurück.
	 * @return String
	 */
	public String getNumber(){
        return this.getAsUTF(number); //ATTLIST rule number
    }

	/**
	 * Gibt die Identnr der Regel zurück.
	 * @return String
	 */
	public String getRulesNumber(){
        return getAsUTF(rulesNumber); //ATTLIST rule rules_number
    }

	/**
	 * Gibt die Regelbeschreibung zurück.
	 * @return String
	 */
	public String getCaption(){
		return getAsUTF(caption);
    }

	/**
	 * Gibt den Fehlertyp zurück.
	 * <p>
	 * Dieser kann in der Regel frei definiert werden,
	 * z.B. DKR, EBM, DRG, etc.
	 * @return String
	 */
	public String getErrorType(){
        return error_type; //ATTLIST rule errror_type ( ZE | MV | OPS | Sonstige | ICD | DKR | DRG | EBM )
    }

	/**
	 * Gibt zurück, ob diese Regel entgeltrelevant ist.
	 * <p>
	 * true - die Regel wurde als entgeltrelevant eingestuft,
	 * ansonsten false.
	 * @return boolean
	 */
	public boolean isEntgelt(){
        return entgelt; //ATTLIST rule entgelt ( false | true )
    }

	/**
	 * Regel ist nicht editierbar.
	 * @return boolean
	 */
	public boolean isUnchange(){
        return unchange; //ATTLIST rule unchange
    }

	/**
	 * Gibt die Notizen zu der Regel zurück.
	 * @return String
	 */
	public String getRuleNotice(){
		return getAsUTF(rule_notice);
	}

	/**
	 * Gibt die Notizen zu dem Vorschlag zurück.
	 * @return String
	 */
	public String getSuggestionNotice(){
		return getAsUTF(suggestion_notice);
	}

	/**
	 * Gibt die Regelkaegorie zurück.
	 * <p>
	 * Dabei handelt es sich meistens um eine Kurzbeschreibung der Regel.
	 * @return String
	 */
	public String getRuleText(){
        return getAsUTF(rule_text); //ATTLIST rule text
    }

	/**
	 * Gibt die ID der Regel zurück.
	 * 
	 * Durch diese ID kann die Regel im Regelsatz eindeutig identifiziert werden.
	 * Diese IDs werden auch von Grouper als Prüfergebnis zurück gegeben.
	 * @return String
	 */
	public String getID(){
        return rid;
    }

	/**
	 * Gibt die Regeldefinition als logischen String zurück.
	 * @return String
	 */
	public String getRuleAsString(){
        return rule_as_text;
    }

	/**
	 * Gibt die Vorschläge zu der Regel zurück.
	 * <p>
	 * Vorschläge werden als logischer Ausdruck in Klammern zurück gegegben.
	 * 0, 1 oder 2 vor den Klammern entspricht der Vorschlagsoperation Enfernen, Zufügen, Ändern
	 * Sollten mehrere Vorschläge existieren, werde diese durch ein Komma getrennt.
	 * @return String
	 */
	public String getSuggestions(){
		StringBuffer strBuf = new StringBuffer();
		if((suggestions == null) || (suggestions.length() == 0)){
			return "";
		}
		String suggs[] = suggestions.split(", ");
		for(int i = 0; i < suggs.length; i++){
			String oneSugg = suggs[i];
			int opLen = oneSugg.indexOf("(");
			String op = oneSugg.substring(0, opLen);
			int opInt = Integer.parseInt(op);
			switch(opInt){
				case 0: strBuf.append(
//						AppResources.getResource(AppResourceBundle.TXT_DELETE,
						"Entfernen"
//						)
						+": ");
						break;
				case 1: strBuf.append(
//						AppResources.getResource(AppResourceBundle.TXT_ADD,
						"Zufügen"
//						)
						+": ");
						break;
				case 2: strBuf.append(
//					AppResources.getResource(AppResourceBundle.TXT_AENDERN,
					"Ändern"
//					)
					+ ": ");break;
			}
			strBuf.append(oneSugg.substring(oneSugg.indexOf("(")));
		}
       return strBuf.toString();
    }

	public void dumpSuggestions(){
		System.out.print("\t Vorschlag : " );
		if((suggestions == null) || (suggestions.length() == 0)){
			System.out.println();
			return;
		}
		String suggs[] = suggestions.split(", ");
		for(int i = 0; i < suggs.length; i++){
			String oneSugg = suggs[i];
			int opLen = oneSugg.indexOf("(");
			String op = oneSugg.substring(0, opLen);
			int opInt = Integer.parseInt(op);
			switch(opInt){
				case 0: System.out.print("Entfernen: "); break;
				case 1: System.out.print("Zufügen: ");break;
				case 2: System.out.print("Ändern: ");break;
			}
			System.out.println(oneSugg.substring(oneSugg.indexOf("(")));
		}

	}

	/**
	 * Hier kann man den CW hinterlegen, den ein Fall erreichen kann,
	 * wenn er den Vorschlag der Regel berücksichtigt.
	 * @param cw double
	 */
	public void setRuleCW(double cw){
		m_ruleCW = cw;
	}

	/**
	 * Gibt den CW zurück, den ein Fall erreichen kann,
	 * wenn er den Vorschlag der Regel berücksichtigt.
	 * <p>
	 * Hinweis: Dieser muss vorher von außen gesetzt werden oder durch die Methode
	 * <blockquote><code>
	 * getRuleValues(ArrayList ruleIDs, boolean withDCW)
	 * </code></blockquote>
	 * im Ruler Interface ermittelt werden.
	 * @return double
	 */
	public double getRuleCW(){
		return m_ruleCW;
	}

	/**
	 * Hier kann man den Delta-CW hinterlegen,
	 * den ein Fall in Bzug zum ursprünglichen CW-Wert erreichen kann,
	 * wenn er den Vorschlag der Regel berücksichtigt.
	 * @param cw double
	 */
	public void setDeltaCW(double cw){
		m_deltaCW = cw;
	}

	private synchronized static String getAsUTF(String str){

/*		try {
			// Convert from Unicode to UTF-8
			char[] utf8 = str.toCharArray();

			return new String(utf8);
		} catch (UnsupportedEncodingException e) {
		}
		return str;*
	try{
//		CharsetEncoder encoder = charset.newEncoder();
		ByteBuffer bbuf = charset.encode(str);
		byte [] byteArr = bbuf.array();
		int length = byteArr.length;
		for(int i = byteArr.length - 1; i >= 0; i--){
			if(byteArr[i] != 0)
				break;
			length--;
		}
		byte newArr[] = new byte[length];
		for(int i = 0; i < length; i++)
			newArr[i] = byteArr[i];
		return new String(newArr, "UTF-8");
//		return new String(newArr);
	}
	catch(Exception e1){
	}*/
	return str;


}
	/**
	 * Gibt den Delta-CW zurück,
	 * den ein Fall in Bzug zum ursprünglichen CW-Wert erreichen kann,
	 * wenn er den Vorschlag der Regel berücksichtigt.
	 * <p>
	 * Hinweis: Dieser muss vorher von außen gesetzt werden oder durch die Methode
	 * <blockquote><code>
	 * getRuleValues(ArrayList ruleIDs, boolean withDCW)
	 * </code></blockquote>
	 * im Ruler Interface ermittelt werden.
	 * @return double
	 */
	public double getDeltaCW(){
		return m_deltaCW;
	}

	public void finalizer()
	{
		System.out.print("finalizer" + this.toString());
	}

	public void dumpAttributes()
	{
		System.out.println("Regelattribute rid= " + rid);
		System.out.println("dateFromAsString = " + dateFromAsString);
		System.out.println("dateToAsString= " + dateToAsString);
		System.out.println("used= " + used);
		System.out.println("ruleType= " + ruleType);
		System.out.println("rolesString= " + rolesString);
		System.out.println("visiblesString= " + visiblesString);
		System.out.println("number= " + number);
		System.out.println("rulesNumber= " + rulesNumber);
		System.out.println("caption= " + caption);
		System.out.println("error_type= " + error_type);
		System.out.println("entgelt= " + entgelt);
		System.out.println("unchange= " + unchange);
		System.out.println("rule_notice= " + rule_notice);
		System.out.println("suggestion_notice= " + suggestion_notice);
		System.out.println("rule_text= " + rule_text);
		System.out.println("rule_as_text= " + rule_as_text);

		dumpSuggestions();


}
}
