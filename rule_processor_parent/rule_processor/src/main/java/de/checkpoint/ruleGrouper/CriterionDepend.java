package de.checkpoint.ruleGrouper;


import java.io.Serializable;
// für die Vorschlagskriterien: benutzen die CriterionDepend - Klasse aber mit der anderen Bedeutung von attributen:
//	 m_index - Typ des Kriteriums, m_indexDepend - index des Kriterums

public class CriterionDepend  implements Serializable
{
	String m_name;
	int m_index;
	int m_indexDepend;
	/** Array der Kriterienindizies, die bei der Vorschlagsbearbeitung  - Zufügen - müssen überprüft werden
	 * z.B.
	 * bei der Bearbeitung des Entgeltvorschlags, wenn in ein Entgeltart Kriterium für Zufügen
	 * vorgeschlagen wurde, müssen die darauffolgenden Vorschläge auf Zufügen der Entgeltanzahl
	 * und Entgelteinzelbetrags überprüft werden, die als Gruppe vor der weireren Verschlagsauswertung
	 * zu betrachten sind Dieser Array soll in dem führenden Kriterium definiert werden, der
	 * m_indexDepend == 0 hat;
	 * m_suggCheckDepend [i][0]  - Kriteriumstyp
	 * m_suggCheckDepend [i][1] - Kriteriumsindex
	 */

	int[][] m_suggCheckDepend = null;
	/**
	 * Name der speziellen Methode zur Überptüfung des Vorschlags zu zusammengehörigen Kriterien
	 * z. Z. Entgelte Typ und Anzahl der Parameter folgen aus m_suggCheckDepend
	 */
	String m_suggCheckMethod = null;


	public CriterionDepend(String name, int index, int indexDepend, int[][] suggCheckDepend, String suggCheckMethod)
	{
		this(name, index, indexDepend);
		m_suggCheckDepend = suggCheckDepend;
		m_suggCheckMethod = suggCheckMethod;
	}

	public CriterionDepend(String name, int index, int indexDepend)
	{
		m_name = name;
		m_index = index;
		m_indexDepend = indexDepend;
	}

	/**
	 * Wenn CriterionDepend in dem Array für die Vorschlagskriterien benutzt wird, ist es
	 * die ID des Kriteriums, das durch das Vorschlagskriterium beeinflusst wird.
	 * @return int : ID des Kriteriums
	 */
	public int getCritId()
	{
		return m_indexDepend;
	}

	/**
	 * Wenn CriterionDepend in dem Array für die Vorschlagskriterien benutzt wird, ist es
	 * der Typ des Kriteriums, das durch das Vorschlagskriterium beeinflusst wird.
	 * @return int : Typ des Kriteriums
	 */
	public int getCritType()
	{
		return m_index;
	}
}
