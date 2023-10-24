package de.checkpoint.server.data.caseRules;

public class DatRuleRoot implements java.io.Serializable
{
	public DatRuleElement m_parent = null;
        public String m_mark = null;

	public DatRuleRoot(DatRuleElement parent)
	{
		m_parent = parent;
	}

	public boolean equalsContent(Object obj)
	{
		return this.equals(obj);
}
}
