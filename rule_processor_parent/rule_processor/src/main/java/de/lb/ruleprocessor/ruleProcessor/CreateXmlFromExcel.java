/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.lb.ruleprocessor.ruleProcessor;

import de.lb.ruleprocessor.checkpoint.server.xml.XMLDOMWriter;
import de.lb.ruleprocessor.create_criterien.inoutCrit.Criterium;
import de.lb.ruleprocessor.create_criterien.inoutCrit.CriteriumContainer;
import de.lb.ruleprocessor.create_criterien.inoutCrit.Tooltip;
import de.lb.ruleprocessor.create_criterien.inoutCrit.Tooltip.TOOLTIP_TYPE;
import de.lb.ruleprocessor.create_criterien.inoutCrit.Utils;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author gerschmann
 */
public class CreateXmlFromExcel extends CreateJsonFromExcel{

    private static final Logger LOG = Logger.getLogger(CreateXmlFromExcel.class.getName());
    
    
   public static void main(String args[]){
        if(args.length < 2){
            LOG.log(Level.INFO, "Usage: <excel file path> <xml file path>");
            System.exit(0);
        }
    
         CreateXmlFromExcel coverter = new CreateXmlFromExcel(args[0], args[1]);   
        try {
            CriteriumContainer container = coverter.doExecute();
            coverter.write2xml(container);
        } catch (Exception ex) {
            Logger.getLogger(CreateXmlFromExcel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public CreateXmlFromExcel(String pExcelPath, String pXmlPath) {
       super(pExcelPath, pXmlPath);
    }

    public void write2xml(CriteriumContainer container) throws Exception{
        if(container == null || container.getCriterien().isEmpty()){
            Logger.getLogger(CreateXmlFromExcel.class.getName()).log(Level.INFO, " no criterien found");
            return;
        }
        Map <String, List<Criterium>> criterien = container.getCriterien();
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.newDocument();
        Element root = doc.createElement(Utils.TAGS.CRITERION_TREE.name().toLowerCase());
        doc.appendChild(root);
        Element superGroup = doc.createElement(Utils.TAGS.SUPERGROUP.name().toLowerCase());
        superGroup.setAttribute(Utils.ATTRIBUTES.CPNAME.name().toLowerCase(), "HL7");
        root.appendChild(superGroup);
        Set<String> groupNames = criterien.keySet();
        List <Element> spTooltips = new ArrayList<>();
        for(String groupName : groupNames){
           List<Element> tooltips = new ArrayList<>();
           List<Element> elements = new ArrayList<>();
           List<Criterium> crits = criterien.get(groupName);
            Element group = doc.createElement(Utils.TAGS.GROUP.name().toLowerCase());
            group.setAttribute(Utils.ATTRIBUTES.NAME.name().toLowerCase(), groupName);
            group.setAttribute(Utils.ATTRIBUTES.CPNAME.name().toLowerCase(), groupName);
            for(Criterium crit: crits){
                Element critXml = doc.createElement(Utils.TAGS.CRITERION.name().toLowerCase());
                critXml.setAttribute(Utils.ATTRIBUTES.NAME.name().toLowerCase(), crit.getDisplayName());
                critXml.setAttribute(Utils.ATTRIBUTES.CPNAME.name().toLowerCase(), crit.getName().toLowerCase());
                critXml.setAttribute(Utils.ATTRIBUTES.DESCRIPTION.name().toLowerCase(), crit.getDescription());
                critXml.setAttribute(Utils.ATTRIBUTES.CRITERION_TYPE.name().toLowerCase(), crit.getType());
                critXml.setAttribute(Utils.ATTRIBUTES.ACCESS_METHOD.name().toLowerCase(), crit.getAccessMethod());
                critXml.setAttribute(Utils.ATTRIBUTES.USAGE.name().toLowerCase(), Utils.USAGE.CRIT_RULE_ONLY.name());
                List<Tooltip> tt = crit.getTooltips();
                if(tt.isEmpty() && crit.getTooltip() != null && !crit.getTooltip().isEmpty()){
                    Element ttip = doc.createElement(Utils.TAGS.TOOLTIP.name().toLowerCase());
                    ttip.setAttribute(Utils.ATTRIBUTES.CPNAME.name().toLowerCase(), crit.getTooltip());
                    critXml.appendChild(ttip);
                } else{

                    for(Tooltip t: tt){
                        Element ttip = doc.createElement(Utils.TAGS.TOOLTIP.name().toLowerCase());
                        if(t.getType().equals(TOOLTIP_TYPE.VALUE)){
                            ttip.setAttribute(Utils.ATTRIBUTES.DESCRIPTION.name().toLowerCase(), t.getFullDescription());
                            ttip.setAttribute(Utils.ATTRIBUTES.VALUE.name().toLowerCase(), t.getValue());
                        }else{
                            ttip.setAttribute(Utils.ATTRIBUTES.CPNAME.name().toLowerCase(), t.getDescription());
                        }
                        critXml.appendChild(ttip);
                    }
                }
                
                elements.add(critXml);
                Element tooltip = doc.createElement(Utils.TAGS.TOOLTIP.name().toLowerCase());
                tooltip.setAttribute(Utils.ATTRIBUTES.CPNAME.name().toLowerCase(), crit.getDisplayName());
                tooltips.add(tooltip);
                
            }
            tooltips.forEach((el) -> {
                group.appendChild(el);
            });
            elements.forEach((el) -> {
                group.appendChild(el);
            });
             Element tooltip = doc.createElement(Utils.TAGS.TOOLTIP.name().toLowerCase());
             tooltip.setAttribute(Utils.ATTRIBUTES.CPNAME.name().toLowerCase(), groupName);
            superGroup.appendChild(group);
            superGroup.appendChild(tooltip);
        }
        writeXMLDocument(doc);
    }
    
        private boolean writeXMLDocument(Document doc)
    {
        try {
            File xmlFile = null;
            OutputStream os = new ByteArrayOutputStream();
            PrintWriter pw = new PrintWriter(new OutputStreamWriter(os,
                                             "UTF-16"));

            if(mDestPath != null) {
                    xmlFile = new File(mDestPath);
            } else {
                    xmlFile = File.createTempFile("test", ".xml");
                    xmlFile.deleteOnExit();
            }
            FileOutputStream fos = new FileOutputStream(xmlFile);
            DataOutputStream output = new DataOutputStream(fos);

            XMLDOMWriter.setWriterEncoding("UTF-16");
            XMLDOMWriter.print(doc, pw, false);
            output.writeBytes(os.toString());
            output.flush();
            output.close();
            if(fos != null) {
                fos.flush();
                fos.close();
            }
        } catch(Exception ex) {
            LOG.log(Level.SEVERE, " Error on creating of the xml file", ex);
            return false;
        }

        return true;
    }

}
