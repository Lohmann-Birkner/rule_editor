package de.lb.ruleprocessor.checkpoint.ruleGrouper.data;

import de.lb.ruleprocessor.checkpoint.server.db.OSObject;

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
public class CRGPoolRight extends OSObject implements java.io.Serializable
{
	public static final String TABLE_NAME = "crg_poolrights";

	public static final String ATT_POOL_ID = "crg_rulepools_id";
	public static final String ATT_ROLE_ID = "role_id";
	public static final String ATT_USER_ID = "dbuser_id";

	public long crg_rulepools_id;
	public long role_id;
	public long dbuser_id;

	public CRGPoolRight()
	{
		super(TABLE_NAME, false);
	}
}
