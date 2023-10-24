package de.checkpoint.ruleGrouper;

import java.io.*;

public class CRGRuleForComplaints implements Serializable
{
	public String m_caption = "";
	public String m_suggestion = "";
	public CRGRuleForComplaints()
	{}

	public CRGRuleForComplaints(String capt, String sugg)
	{
		m_caption = capt;
		m_suggestion = sugg;
	}
}
