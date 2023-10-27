package de.lb.ruleprocessor.checkpoint.ruleGrouper;

/**
 * <p>Überschrift: Checkpoint DRG</p>
 *
 * <p>Beschreibung: Warnungen Regelprüfung</p>
 *
 * <p>Copyright: Lohmann & Birkner Health Care Consulting GmbH </p>
 *
 * <p>Organisation: Lohmann & Birkner Health Care Consulting GmbH </p>
 *
 * @author unbekannt
 * @version 2.0
 */
public class CRGRuleGroupWarning  extends java.lang.Exception
{
	public CRGRuleGroupWarning(String message)
	{
		super(message);
	}

	public CRGRuleGroupWarning(String message, java.lang.Throwable cause)
	{
		super(message, cause);
	}
}
