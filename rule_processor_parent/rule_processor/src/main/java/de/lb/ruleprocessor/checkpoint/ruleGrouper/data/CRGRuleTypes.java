package de.lb.ruleprocessor.checkpoint.ruleGrouper.data;

import de.lb.ruleprocessor.checkpoint.server.data.caseRules.DatCaseRuleAttributes;
import de.lb.ruleprocessor.checkpoint.server.data.caseRules.DatRulerErrorType;
import org.w3c.dom.Element;
import de.lb.ruleprocessor.checkpoint.server.data.caseRules.DsrRule;
import de.lb.ruleprocessor.checkpoint.server.db.OSObject;

/**
 * <p>Überschrift: Checkpoint DRG</p>
 *
 * <p>Beschreibung: Regeltypen</p>
 *
 * @author unbekannt
 * @version 2.0
 */
public class CRGRuleTypes  extends OSObject
{
	public static final String TABLE_NAME = "crg_ruletypes";
	public static final String ATT_CRGRT_IDENT = "CRGRT_IDENT";
	public static final String ATT_CRGRT_DISPLAYTEXT = "CRGRT_DISPLAYTEXT";
	public static final String ATT_CRGRT_SHORTTEXT = "CRGRT_SHORTTEXT";

	public String crgrt_shorttext;
	public String crgrt_displaytext;
	public int crgrt_type;
	public int crgrt_ident;

	/**
	 * Konstruktor
	 */
	public CRGRuleTypes()
	{
		super(TABLE_NAME, true);
	}

	/**
	 * Konstruktor
	 * @param rule Element : Rule Element
	 * @param ident int : Identifaktor
	 */
	public CRGRuleTypes(Element rule, int ident){
		this();
		crgrt_shorttext = DsrRule.getDisplayText(rule.getAttribute(DatCaseRuleAttributes.ATT_CAPTION_ERROR_TYPE).trim());
		crgrt_displaytext = crgrt_shorttext;
		crgrt_ident = ident;
	}

	/**
	 * Gibt die Bezeichnung des Regeltyps zurück.
	 * <p>
	 * Kurzbezeichnung des Regeltyps
	 * @return String : Kurzbezeichnung für den Regeltyp
	 */
	public String getText()
	{
		return crgrt_shorttext;
	}

	/**
	 * Setzt die Bezeichnung für den Regeltyp.
	 * @param text String : Kurzbezeichnung für den Regeltyp
	 */
	public void setText(String text)
	{
		crgrt_shorttext = text;
	}

	public String toString()
	{
		return getDisplayText();
	}

	/**
	 * Setzt den Anzeigetext des Regeltyps (langer Text).
	 * @param text String : Anzeigetext des Regeltyps
	 */
	public void setDisplayText(String text){
		crgrt_displaytext = text;
	}

	/**
	 * Gibt den Anzeigetext des Regeltyps (langer Text) zurück.
	 * <p>
	 * Langer Text des Regeltyps
	 * @return String : Anzeigetext des Regeltyps
	 */
	public String getDisplayText(){
		return crgrt_displaytext;
	}


	/**
	 * Gibt den Typ des Regeltyps zurück.
	 * <p>
	 * 0 = Default-Typ (kann nicht gelöscht werden)
	 * 1 = erstellter Regeltyp (systemabhängig)
	 * @return int : Typ des Regeltyps
	 */
	public int getType()
	{
		return crgrt_type;
	}

	/**
	 * Setzt den Typ des Regeltyps : wird vom System vergeben.
	 * @param type int : Typ des Regeltyps
	 */
	public void setType(int type)
	{
		crgrt_type = type;
	}

	/**
	 * Gibt die eindeutige ID des Regeltyps zurück.
	 * @return int : eindeutige ID
	 */
	public int getIdent()
	{
		return crgrt_ident;
	}

	/**
	 * Setzt die ID des Regeltyps : wird vom System vergeben.
	 * @param ident int : eindeutige ID
	 */
	public void setIdent(int ident)
	{
		crgrt_ident = ident;
	}

	/**
	 * Erzeugt aus dieser Instanz ein Objekt DatRulerErrorType.
	 * @return DatRulerErrorType : DatRulerErrorType-Objekt
	 */
	public DatRulerErrorType getAsDatErrorType()
	{
		DatRulerErrorType type = new DatRulerErrorType();
		type.displayText = this.crgrt_displaytext;
		type.id = (int)this.crgrt_ident;
		type.isForAll = true;
		type.shortText = this.crgrt_shorttext;
		type.type = this.crgrt_type;
		return type;
	}

        @Override
	public boolean equals(Object obj)
	{
		return (obj instanceof CRGRuleTypes && ((CRGRuleTypes)obj).crgrt_shorttext.equalsIgnoreCase(this.crgrt_shorttext));
	}

        @Override
	public int compareTo(Object obj)
	{
            if(obj instanceof CRGRuleTypes)
		return crgrt_shorttext.compareToIgnoreCase(((CRGRuleTypes)obj).crgrt_shorttext);
            if(obj instanceof String)
                return crgrt_shorttext.compareToIgnoreCase(((String)obj));
            return 0;
	}

}
