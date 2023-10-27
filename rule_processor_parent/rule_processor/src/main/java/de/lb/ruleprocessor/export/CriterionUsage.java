/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.lb.ruleprocessor.export;

import de.lb.ruleprocessor.checkpoint.server.data.caseRules.DatCaseRuleAttributes;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author gerschmann
 */
public class CriterionUsage {

    private static final Logger LOG = Logger.getLogger(CriterionUsage.class.getName());
    private static String inputDirectoryPath;
    private static String outputCsvPath;
    private final Map <String, Map<String, Integer>> year2criteriumCount = new HashMap<>();
    
    FileFilter xmlFilefilter = new FileFilter() {
        //Override accept method
        public boolean accept(File file) {
           if (file.getName().endsWith(".xml")) {
              return true;
           }
           return false;
        }

     };    
    public void doIt()
    {
        try{
            File inputDir = new File(inputDirectoryPath);
            if(inputDir.exists() && inputDir.isDirectory()){
// check for rule xml file
                File[] files = inputDir.listFiles(xmlFilefilter);
                if (files != null && files.length > 0){
                    for(File file: files){
                        String filePath = file.getAbsolutePath();
                        String year = filePath.substring(filePath.lastIndexOf("20"), filePath.lastIndexOf("20") + 4);
                        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                        DocumentBuilder db = dbf.newDocumentBuilder();
                        Document impDoc = null;
                        Map <String, Integer> crterium2count = year2criteriumCount.get(year);
                        if(crterium2count == null){
                            crterium2count = new HashMap<>();
                            year2criteriumCount.put(year, crterium2count);
                        }
                        String path = file.getAbsolutePath();
                        URL url = new URL("file", null, path);

                        impDoc = db.parse(url.openStream());
                        Element impRoot = impDoc.getDocumentElement();
                        if(impRoot != null){
                            NodeList lst = impRoot.getElementsByTagName(DatCaseRuleAttributes.ELEMENT_RULES_VALUES);
                            if(lst != null){
                                for(int i = 0; i < lst.getLength(); i++){
                                    Element el = (Element)lst.item(i);
// kriteriumname
                                    String critName = el.getAttribute(DatCaseRuleAttributes.ATT_RULES_TYPE);
                                    if(critName.trim().length() == 0){
                                        continue;
                                    }
                                    critName = critName.trim();
                                     Integer count = crterium2count.get(critName);

                                    if(count == null){

                                        
                                        crterium2count.put(critName, 1);
                                    }else{
                                        crterium2count.put(critName, count + 1);
                                    }
                                }
                            }
                        }

                    }
                }
// now write output
                makeResult();
            }
        }catch(Exception ex){
            LOG.log(Level.SEVERE, "", ex);
        }
    }
    
        public void makeResult() {
        try{

            java.io.BufferedWriter out_result = new java.io.BufferedWriter(new
                    java.io.FileWriter(outputCsvPath));
            
            Set<String> years = year2criteriumCount.keySet();
            List<String> yearsSort = new ArrayList<>(years);
            Collections.sort(yearsSort);
            for(String year: yearsSort) {
                out_result.write(year +"\r\n");

                Map< String, Integer> criterum2count = year2criteriumCount.get(year);
                List<String> critSort = new ArrayList<>(criterum2count.keySet());
                Collections.sort(critSort);
                for(String crit:critSort){
                    StringBuilder resultStr = new StringBuilder();
                    resultStr.append(crit).append(";").append(String.valueOf(criterum2count.get(crit))).append(";").append("\r\n");
                    out_result.write(resultStr.toString());
                }
            }

            out_result.close();
        } catch (java.io.IOException e) {
        }

    }


    public static void main(String args[]){
        if(args == null || args.length < 2){
            LOG.log(Level.INFO, " usage: path to input directory, path to output csv file");
            System.exit(0);
        }
        inputDirectoryPath = args[0];
        outputCsvPath = args[1];
        CriterionUsage usage = new CriterionUsage();
        usage.doIt();
        System.exit(0);
    }
}
