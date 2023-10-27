package de.lb.ruleprocessor.checkpoint.drg;

import java.util.*;

public class RulesRefOut implements java.io.Serializable
{

	public String m_princRef = null;
	public java.util.List m_auxDiagList = new ArrayList();
	public java.util.List m_diagList = new ArrayList();
	public java.util.List m_procList = new ArrayList();
	public java.util.List m_feeList = new ArrayList();
	public java.util.List m_psychOpsList = new ArrayList();
	public java.util.List m_VPSList = new ArrayList();
	public java.util.List m_historyCaseIdents = new ArrayList();

    public RulesRefOut()
    {
    }

    public String toString(){

        String seperator = ",";
        String references = "";
// Arrays.toString(myList.toArray()); 
        if (m_princRef != null) {
            references = references.concat(m_princRef);
        }
        if (m_auxDiagList != null && !m_auxDiagList.isEmpty()) {
            references = references.concat(!references.isEmpty() ? seperator : "");
//            references = references.concat(String.join(seperator, m_auxDiagList));
            references = references.concat( Arrays.toString(m_auxDiagList.toArray()));
        }
        if (m_diagList != null && !m_diagList.isEmpty()) {
            references = references.concat(!references.isEmpty() ? seperator : "");
//            references = references.concat(String.join(seperator, m_diagList));
            references = references.concat( Arrays.toString(m_diagList.toArray()));
        }
        if (m_feeList != null && !m_feeList.isEmpty()) {
            references = references.concat(!references.isEmpty() ? seperator : "");
            references = references.concat( Arrays.toString( m_feeList.toArray()));
//           references = references.concat(String.join(seperator, m_feeList));
        }
        if (m_procList != null && !m_procList.isEmpty()) {
            references = references.concat(!references.isEmpty() ? seperator : "");
            references = references.concat( Arrays.toString( m_procList.toArray()));
//           references = references.concat(String.join(seperator, m_procList));
        }
        if (m_psychOpsList != null && !m_psychOpsList.isEmpty()) {
            references = references.concat(!references.isEmpty() ? seperator : "");
            references = references.concat( Arrays.toString( m_psychOpsList.toArray()));
//           references = references.concat(String.join(seperator, m_psychOpsList));
        }
        if(m_historyCaseIdents != null && !m_historyCaseIdents.isEmpty()){
            references = references.concat(!references.isEmpty() ? seperator : "");
            references = references.concat( Arrays.toString( m_historyCaseIdents.toArray()));
            
        }
         return references;
    }
        
    public static void main(String args[]){
        RulesRefOut ref = new RulesRefOut();
        ref.m_princRef = "HDX";
        ref.m_auxDiagList.add("AUX1");
        ref.m_auxDiagList.add("AUX2");
        ref.m_auxDiagList.add("AUX3");
        ref.m_procList.add("PROC1");
        System.out.println(ref.toString());
        System.exit(0);
    }
}
