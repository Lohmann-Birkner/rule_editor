package de.checkpoint.server.data.caseRules;

import de.checkpoint.server.db.OSObject;

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
public class DPDataset extends OSObject
{
	public static final String TABLE_NAME = "dp_datasets";

	public static final String ATT_YEAR = "year";
	public static final String ATT_HOPITALCASE_COUNT = "hospitalcase_count";
	public static final String ATT_MEDICAMENT_COUNT = "medicament_count";
	public static final String ATT_SOLE_COUNT = "sole_count";
	public static final String ATT_MRSA_COUNT = "mrsa_count";
	public static final String ATT_ACG_COUNT = "acg_count";


	public int year=0;
	public int hospitalcase_count=0;
	public int medicament_count=0;
	public int sole_count=0;
	public int mrsa_count=0;
	public int acg_count=0;

	public DPDataset(){
		super(TABLE_NAME, true);
	}

}
