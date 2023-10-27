/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.lb.ruleprocessor.ruleProcessor;





import de.lb.ruleprocessor.create_criterien.inoutCrit.Utils;
import de.lb.ruleprocessor.create_criterien.inoutCrit.Criterium;
import de.lb.ruleprocessor.create_criterien.inoutCrit.CriteriumContainer;
import de.lb.ruleprocessor.create_criterien.inoutCrit.Tooltip;
import de.lb.ruleprocessor.json_processor.JsonFileWriter;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jetbrains.annotations.NotNull;

/**
 *
 * @author gerschmann
 */
public class CreateJsonFromExcel {
    private final String mExcelPath;
    protected final String mDestPath;
    private static final Logger LOG = Logger.getLogger(CreateJsonFromExcel.class.getName());


    /**
     *
     * @param pExcelPath
     * @param pJsonPath
     */
    public CreateJsonFromExcel(String pExcelPath, String pJsonPath) {
        mExcelPath = pExcelPath;
        mDestPath = pJsonPath;
    }


    private void createTooltips4Criterium(@NotNull Sheet sheet, @NotNull Criterium crit) {
        Iterator<Row>itr = sheet.iterator();
        while(itr.hasNext()){
            Row row = itr.next();
            if(row.getRowNum() == 0){
                continue;
            }
            if(row.getCell(0) != null && row.getCell(0).getCellType()== CellType.STRING
                   && row.getCell(1) != null && row.getCell(1).getCellType() == CellType.STRING ){
                crit.addTooltip(new Tooltip(row.getCell(0).getStringCellValue(), row.getCell(1).getStringCellValue()));
            }          
        }
    }

    
    protected CriteriumContainer doExecute() throws Exception{
        
      File excelFile = new File(mExcelPath);
      Map<String, Criterium> tooltip2criterium = new HashMap<>();
      CriteriumContainer criterien = new CriteriumContainer();
      if(excelFile.isFile() && excelFile.canRead()){
          Workbook book = new XSSFWorkbook(new FileInputStream(excelFile));
          for(int i = 0; i< book.getNumberOfSheets(); i++){
              Sheet sheet = book.getSheetAt(i);
              String sheetName = sheet.getSheetName();
              Iterator<Row>itr = sheet.iterator();

              if(tooltip2criterium.get(sheetName.toLowerCase()) != null){
                  createTooltips4Criterium(sheet, tooltip2criterium.get(sheetName));
                  continue;
              }
              while(itr.hasNext()){
                  Row row = itr.next();
                  if(row.getRowNum() == 0){
                      continue;
                  }
                  if(row.getCell(0) != null && row.getCell(0).getCellType()== CellType.STRING
                          && row.getCell(1) != null && row.getCell(1).getCellType() == CellType.STRING 
                          && Utils.DATATYPES.get(row.getCell(1).getStringCellValue()) != null ){
                        Criterium crit = new Criterium(row.getCell(0).getStringCellValue(), row.getCell(1).getStringCellValue());
                        if(row.getCell(2) != null && row.getCell(2).getCellType() == CellType.STRING ){
                            crit.setDescription(row.getCell(2).getStringCellValue());
                        }
                        if(row.getCell(3) != null && row.getCell(3).getCellType() == CellType.STRING){
                            crit.setDisplayName(row.getCell(3).getStringCellValue());
                        }else{
                            crit.setDisplayName(crit.getName());
                        }

                        if(row.getCell(4) != null && row.getCell(4).getCellType() == CellType.STRING){
                            String tooltip = row.getCell(4).getStringCellValue();
                            crit.setTooltip(tooltip);
                            if(tooltip != null && tooltip.toLowerCase().startsWith("tooltip")){
 // create tooltips from extra sheet     
                                tooltip2criterium.put(tooltip.toLowerCase(), crit);
                            }
                        }
                         criterien.addCriterium(sheetName == null?"default":sheetName, crit);
                  } 
                  
              }

          }
      }
      return criterien;
     }
    
    public void write2json(CriteriumContainer criterien)  throws Exception{
        if(!criterien.isEmpty()){
          JsonFileWriter writer = new JsonFileWriter(mDestPath);
          writer.openFile();
          writer.writeFile(criterien);
          writer.closeFile();
      }

    }
    public static void main(String args[]){
        if(args.length < 2){
            LOG.log(Level.INFO, "Usage: <excel file path> <json file path>");
            System.exit(0);
        }
    
         CreateJsonFromExcel coverter = new CreateJsonFromExcel(args[0], args[1]);   
        try {
            CriteriumContainer container = coverter.doExecute();
            coverter.write2json(container);
        } catch (Exception ex) {
            Logger.getLogger(CreateJsonFromExcel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
