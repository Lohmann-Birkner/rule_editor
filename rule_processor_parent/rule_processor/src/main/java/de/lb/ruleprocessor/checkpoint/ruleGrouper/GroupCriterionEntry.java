package de.lb.ruleprocessor.checkpoint.ruleGrouper;

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
public class GroupCriterionEntry extends CriterionEntry
{
	String m_suggToolTip="";

	public GroupCriterionEntry(int groupIndex, String groupName, String groupToolTip, int insugg, String suggToolTip)
	{
		this(groupIndex, groupIndex, groupName, groupToolTip, insugg);
		m_suggToolTip =  de.lb.ruleprocessor.checkpoint.installer.IAActions.CommonOperations.stringAsHTML(suggToolTip,
							toolTipLen);
	}

	public GroupCriterionEntry(int groupIndex, String groupName, String groupToolTip)
	{
		this(groupIndex, groupIndex, groupName, groupToolTip);
	}

	public GroupCriterionEntry(int groupIndex, int structGroup, String groupName, String groupToolTip, int insugg)
	{
		this(groupIndex, structGroup,  groupName, groupToolTip);
		m_usedInSugg = insugg;

	}

	public GroupCriterionEntry(int groupIndex, int structGroup, String groupName, String groupToolTip)
	{
		super(groupIndex, structGroup, groupName, groupName, groupToolTip);
	}

	public int compareTo(Object sr2)
	{
		if(sr2 instanceof GroupCriterionEntry)
			return this.getDisplayText().toUpperCase().compareTo(((GroupCriterionEntry)sr2).getDisplayText().toUpperCase()) ;
		else return 0;
	}

	public String getDescriptionText(boolean isSugg)
	{
		if(isSugg)
			return m_suggToolTip;
		return super.getDescriptionText();
	}


}
