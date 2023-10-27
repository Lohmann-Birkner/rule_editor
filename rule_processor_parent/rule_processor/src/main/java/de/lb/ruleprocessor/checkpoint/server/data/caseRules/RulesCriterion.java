package de.lb.ruleprocessor.checkpoint.server.data.caseRules;

import java.io.*;

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
public class RulesCriterion implements Serializable
{
	/**
	 * wie Kriteriumname in dem Kriteriumbaum erscheint
	 */
	protected String m_displayText = "";

	/**
	 * wie Kriteriumname in der xml - Regeldatei erscheint
	 */
	protected String m_name = "";

	public RulesCriterion()
	{
	}

	public String getDisplayText()
	{
		return m_displayText;
	}

	public String getWorkText()
	{
		return m_name;
	}

}
