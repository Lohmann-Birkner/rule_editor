package de.lb.ruleprocessor.checkpoint.ruleGrouper;

import java.util.Vector;
import java.util.ArrayList;

public class CRGRefElement {

/*
 01 = ICD
 02 = OPS
 03 = Entgeltschlüssel
 04 = ATC Code
 05 = PZN Code
 06 = Hilfsmittelpositionsnummer
 07 = Heilmittelpositionsnummer
 08 = (Referenz-)Fallnummern
*/
public final static String REF_CODE_TYPE_ICD = "01";
public final static String REF_CODE_TYPE_OPS = "02";
public final static String REF_CODE_TYPE_FEE = "03";
public final static String REF_CODE_TYPE_ATC = "04";
public final static String REF_CODE_TYPE_PZN = "05";
public final static String REF_CODE_TYPE_HPN = "06";
public final static String REF_CODE_TYPE_HEN = "07";
public final static String REF_CODE_TYPE_OTHERCASE_NR = "08";
public final static String REF_CODE_TYPE_PSYCH ="09";
public final static String REF_CODE_TYPE_VPS ="10";

	private final static int REF_ARRAY_LENGTH = 200;
	public boolean m_ref = false;
/*	public String m_MainDiag = null;
	public String[] m_DiagAux = new String[REF_ARRAY_LENGTH];
	public String[] m_Diag = new String[REF_ARRAY_LENGTH];
	public String[] m_Proc = new String[REF_ARRAY_LENGTH];
	public String[] m_Fee = new String[REF_ARRAY_LENGTH];
	public int m_DiagAuxCount = -1;
	public int m_DiagCount = -1;
	public int m_ProcCount = -1;
	public int m_FeeCount = -1;*/
   Vector<CRGRefElementInd> m_RuleReferences = null;


	public void setClean(){
		m_ref = false;
/*		m_MainDiag = null;
		m_DiagAux = new String[REF_ARRAY_LENGTH];
		m_DiagAuxCount = -1;
		m_Diag = new String[REF_ARRAY_LENGTH];
		m_DiagCount = -1;
		m_Proc = new String[REF_ARRAY_LENGTH];
		m_ProcCount = -1;
		m_Fee = new String[REF_ARRAY_LENGTH];
		m_FeeCount = -1;*/
	  m_RuleReferences = null;
	}

	public void addCritValue(int critIndex, String value)
	{
		CRGRefElementInd tmp = new CRGRefElementInd(critIndex);
		if(m_RuleReferences == null)
			m_RuleReferences = new Vector<CRGRefElementInd>();
		int ind = m_RuleReferences.indexOf(tmp);
		if( ind < 0 )
			m_RuleReferences.addElement(tmp);
		else{
			tmp = m_RuleReferences.elementAt(ind);
		}
		tmp.addValue(value);
	}

	public String getForCritIndex(int critIndex)
	{
		if(m_RuleReferences == null)
			return null;
		CRGRefElementInd tmp = new CRGRefElementInd(critIndex);
		int ind = m_RuleReferences.indexOf(tmp);
		if( ind >=0 ){
			tmp = m_RuleReferences.elementAt(ind);
			if(tmp.getCount() > 0)
				return tmp.m_critValues.get(0);
		}
		return null;

	}

	public ArrayList<String> getForCritArrayIndex(int critIndex)
	{
		if(m_RuleReferences == null)
			return null;
		CRGRefElementInd tmp = new CRGRefElementInd(critIndex);
		int ind = m_RuleReferences.indexOf(tmp);
		if( ind >=0 ){
			tmp = m_RuleReferences.elementAt(ind);
			if(tmp.getCount() > 0){
				return tmp.m_critValues;
			}
		}
		return null;

	}

	public String getForCritArrayStringIndex(int critIndex)
	{
		if(m_RuleReferences == null)
			return "";

		CRGRefElementInd tmp = new CRGRefElementInd(critIndex);
		int ind = m_RuleReferences.indexOf(tmp);
		if( ind >=0 ){
			tmp = m_RuleReferences.elementAt(ind);
			StringBuffer ret = new StringBuffer();
			for(int i = 0, size = tmp.getCount(); i < size; i++) {
				ret.append(tmp.m_critValues.get(i));
				ret.append(",");
			}
			String str =  ret.toString();
			if(str.endsWith(","))
				str = str.substring(0, str.length() - 1);
			return str;

		}
		return "";

	}

	public String getAllReferencesMapped()
	{
		if(m_RuleReferences == null || m_RuleReferences.size() == 0)
			return "";
		StringBuffer ret = new StringBuffer();
		for(int i = 0; i < m_RuleReferences.size(); i++){
			String type = "";
			CRGRefElementInd tmp = m_RuleReferences.elementAt(i);
			ArrayList<String>values = tmp.m_critValues;
			int size = values.size();
			if(size == 0)
				continue;
			switch(tmp.m_critIndex){
	
					default: type = "";
			}
			if(type.length() > 0){
				for(int j = 0; j < size; j++) {
					ret.append(values.get(j));
					ret.append("(");
					ret.append(type);
					ret.append(")");
					ret.append(",");
				}

			}
		}
		String str =  ret.toString();
		if(str.endsWith(","))
			str = str.substring(0, str.length() - 1);
		return str;

	}
        
        /**
         * liefert dir Referenzen für einen bestimmten Kriteriumstyp
         * @param critIndex
         * @return 
         */
        private ArrayList<String>  getRefefences2Crit(int critIndex){
    		if(m_RuleReferences == null || m_RuleReferences.size() == 0)
			return null;  
                for(CRGRefElementInd refs:m_RuleReferences){
                    if(refs.m_critIndex == critIndex)
                        return refs.m_critValues;
                }
                return null;
        }

	class CRGRefElementInd
	{
		public int m_critIndex;
	//	public String[] m_critValues = new String[REF_ARRAY_LENGTH];
		public ArrayList<String> m_critValues = new ArrayList<String>();
//		public int m_count = -1;

		public CRGRefElementInd( int index)
		{
			m_critIndex = index;
		}

		public boolean equals(Object obj)
		{
			return ((obj instanceof CRGRefElementInd) && (((CRGRefElementInd)obj).m_critIndex == this.m_critIndex));
		}
                

		public void addValue(String value)
		{
/*			if(m_count < REF_ARRAY_LENGTH){
				m_count ++;
				m_critValues[m_count] = value;
			}*/
				if(!m_critValues.contains(value) )
					m_critValues.add(value);
		}
		public int getCount()
		{
			return m_critValues.size();
		}

	}

}
