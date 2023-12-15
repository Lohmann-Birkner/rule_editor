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
//import de.lb.ruleprocessor.json_processor.JsonFileWriter;

import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import org.apache.poi.ss.usermodel.Cell;
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
public class CriteriaCreaterFromExcel {
    private final String mExcelPath;
    protected final String mDestPath;
    private static final Logger LOG = Logger.getLogger(CriteriaCreaterFromExcel.class.getName());


    /**
     *
     * @param pExcelPath
     * @param pJsonPath
     */
    public CriteriaCreaterFromExcel(String pExcelPath, String pJsonPath) {
        mExcelPath = pExcelPath;
        mDestPath = pJsonPath;
    }


    private void createTooltips4Criterium(@NotNull Sheet sheet, @NotNull List<Criterium >crits) {
        Iterator<Row>itr = sheet.iterator();
        while(itr.hasNext()){
            Row row = itr.next();
            if(row.getRowNum() == 0){
                continue;
            }
            if(row.getCell(0) != null && row.getCell(0).getCellType()== CellType.STRING
                   && (row.getCell(1) == null || row.getCell(1) != null && row.getCell(1).getCellType() == CellType.STRING )){
                for(Criterium crit: crits){
                    crit.addTooltip(new Tooltip(row.getCell(0).getStringCellValue(), row.getCell(1) == null?null: row.getCell(1).getStringCellValue()));
                }
            }          
        }
    }

    
    public CriteriumContainer doExecute() throws Exception{
        
      File excelFile = new File(mExcelPath);
      Map<String, List<Criterium>> tooltip2criterium = new HashMap<>();
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
              Map<String, Integer> columnName2position = new HashMap<>();
              while(itr.hasNext()){
                  Row row = itr.next();
                  if(row.getRowNum() == 0){
// create mapping column to attribute
                      createMappingColumn2Position(columnName2position, row);  
                      continue;
                  }
                  Integer cpNamePos = columnName2position.get(Utils.ATTRIBUTES.CPNAME.name());
                  Method[] critMethods =  Criterium.class.getMethods();
                  if(cpNamePos != null && row.getCell(cpNamePos) != null && row.getCell(cpNamePos).getCellType() == CellType.STRING){
                   
                    Criterium crit = new Criterium(row.getCell(cpNamePos).getStringCellValue());
                    criterien.addCriterium(sheetName == null?"default":sheetName, crit);
                  
                    Set<String> keys = columnName2position.keySet();
                    for(String key: keys){
                        Integer pos = columnName2position.get(key);
                        if(key.equalsIgnoreCase(Utils.ATTRIBUTES.CPNAME.name())){
                            continue;
                        }
                        if(key.equalsIgnoreCase(Utils.TAGS.TOOLTIP.name())){
                            if(row.getCell(pos) != null && row.getCell(pos).getCellType() == CellType.STRING){
                                String tooltip = row.getCell(pos).getStringCellValue();
                                crit.setTooltip(tooltip);
                                if(tooltip != null && tooltip.toLowerCase().startsWith(Utils.TAGS.TOOLTIP.name().toLowerCase()+ "_")){
     // create tooltips from extra sheet  
                                    List<Criterium> crits = tooltip2criterium.get(tooltip.toLowerCase());
                                    if(crits == null){
                                        crits = new ArrayList<>();
                                        tooltip2criterium.put(tooltip.toLowerCase(),  crits);
                                    }
                                    crits.add(crit);
                                }
                            }
                        
                        }
                        String key1 = key.replaceAll("_", "");
                        for(Method method : critMethods){
                           
                            if(method.getName().startsWith("set") && method.getName().toLowerCase().endsWith(key1.toLowerCase())){
                                
                                 if(pos != null && row.getCell(pos) != null && row.getCell(pos).getCellType() == CellType.STRING){
                                    method.invoke(crit, row.getCell(pos).getStringCellValue());
                                 }
                            }
                        }
                    }
                  }
                  
              }

          }
      }
      return criterien;
     }
    

    private void createMappingColumn2Position(Map<String, Integer> columnName2position,  Row row) {
         Iterator<Cell> cells = row.cellIterator();
         while(cells.hasNext()){
             Cell cell = cells.next();
             if(cell != null && cell.getCellType() == CellType.STRING){
                 String name = cell.getStringCellValue();
                 if(name.equalsIgnoreCase(Utils.TAGS.TOOLTIP.name()) || Utils.checkIsCriteriaAttribute(name)){
                    columnName2position.put(name, cell.getColumnIndex());
                 }
             }
         }
    }
    

    
}
