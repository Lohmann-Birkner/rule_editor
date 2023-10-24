package de.checkpoint.ruleGrouper;

import java.sql.Date;

public class CRGFeeGroup implements  java.io.Serializable
{
	public int id;
	public String name;
	public int nr;
	public int sort;
	public Date valid_from;
	public Date valid_to;
	public boolean dogroup;

	public CRGFeeGroup()
	{
	}

        public CRGFeeGroup(int nnr, String nname)
        {
            nr = nnr;
            name = nname;
        }
        
	public String getDisplayText()
	{
		return String.valueOf(nr) + " " + name;
	}

	public boolean equals(CRGFeeGroup fg)
	{
	  return this.getDisplayText().equalsIgnoreCase(fg.getDisplayText());
	}

	public String toString()
	{
		return getDisplayText();
	}
}
