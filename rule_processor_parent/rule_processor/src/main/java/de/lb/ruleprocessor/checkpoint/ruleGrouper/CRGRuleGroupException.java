package de.lb.ruleprocessor.checkpoint.ruleGrouper;

/**
 * <p>Überschrift: Checkpoint DRG</p>
 *
 * <p>Beschreibung: Fehlermeldungen Regelprüfung</p>
 *
 *
 * @author unbekannt
 * @version 2.0
 */
public class CRGRuleGroupException extends java.lang.Exception
{
	private int m_errorNumber = -1;

	public CRGRuleGroupException(String message)
	{
		this(message, -1);
	}

	public CRGRuleGroupException(String message, int errorNumber)
	{
		super(message);
	}

	public CRGRuleGroupException(String message, java.lang.Throwable cause)
	{
		this(message, -1, cause);
	}

	public CRGRuleGroupException(String message, int errorNumber, java.lang.Throwable cause)
	{
		super(message, cause);
	}
}
