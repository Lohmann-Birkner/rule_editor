package de.lb.ruleprocessor.checkpoint.server.data.caseRules;

/**
 * <p>Überschrift: Checkpoint DRG</p>
 *
 * <p>Beschreibung: Fallmanagement DRG</p>
 *
 * <p>Copyright: </p>
 *
 * <p>Organisation: </p>
 *
 * @author not attributable
 * @version 2.0
 */
public class DatRuleOp  extends DatRuleRoot
{
	public String m_op = "";

	public DatRuleOp(DatRuleElement parent){
		super(parent);
	}

	public DatRuleOp(String op, DatRuleElement parent)
	{
		super(parent);
		m_op = op;
	}

	public boolean equalsContent(Object obj)
	{
		if(obj == null)
			return false;
		if(obj instanceof DatRuleOp)
			return m_op.equals(((DatRuleOp)obj).m_op);
		return false;
	}

	public String toString()
	{
		return (m_mark == null || m_mark.isEmpty())?m_op:("mark: " + m_mark + " " + m_op) ;
	}
}
