package de.checkpoint.utils;

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
public class UtlUnit
{
	public int id;
	public String shortName;

	public UtlUnit(int id, String sn)
	{
		this.id = id;
		shortName = sn;
	}

	public String toString(){
		return shortName;
	}

	public boolean equals(Object obj)
{
    /* 3.9.5 2015-09-01 DNi: #FINDBUGS - Potenzielle NP-Exception, weil obj != null nicht abgefragt wird! */
		return obj != null && this.toString().equals(obj.toString());
}


}
