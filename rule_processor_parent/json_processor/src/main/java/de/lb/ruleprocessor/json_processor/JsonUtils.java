/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.lb.ruleprocessor.json_processor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.checkpoint.ruleGrouper.CRGRule;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;
import org.apache.commons.math3.util.Precision;

/**
 *
 * @author gerschmann
 */
public class JsonUtils {

    private static final Logger LOG = Logger.getLogger(JsonUtils.class.getName());
    
    
    public static Integer toInt(final String pValue) {
        final String value = pValue == null ? "" : pValue.trim();
        if (value.isEmpty()) {
            return 0;
        }
        return Integer.valueOf(value);
    }
    
    public static boolean isInt(String pValue){
        pValue = Objects.requireNonNullElse(pValue, "").trim();
        try{
            return pValue.equals(String.valueOf(toInt(pValue)));
        }catch(NumberFormatException ex){
            return false;
        }
    }
    
    public static Double toDouble(final String pValue) {
        final String value = pValue == null ? "" : pValue.trim();
        if (value.isEmpty()) {
            return null;
        }
        return Double.valueOf(value.replace(",", "."));
    }
    
    public static String toStr(String pValue) {
        if (pValue == null) {
            return "";
        }
        return pValue.trim();
    }
    
    public static String convert2json(Object object){
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(object);
        }catch(JsonProcessingException ex){
            LOG.log(Level.SEVERE, "Couldnot create json string for patient data", ex);
            return ex.getMessage();
        }
    }
    
    
    public static CRGRule convertSingleJsonStringToObjectOrNull(String pJsonObject) throws IOException{
        List<CRGRule> result = convertJsonStringToObject(pJsonObject);
        if(result.isEmpty()|| result.size()>1){
            return null;
        }
        return result.get(0);
    }
    public static List<CRGRule> convertJsonStringToObject(String pJsonObject) throws IOException{
        return Arrays.asList(new ObjectMapper().readValue(pJsonObject, CRGRule.class));
    }
    

    public static List<CRGRule> convertJsonStringToListObject(String pJsonObject) throws IOException{
        return  new ObjectMapper().readValue(pJsonObject, new TypeReference<List<CRGRule>>(){});
    }

    public static <E> E unserialize(final File pFile) {
        E catalogObj = null;
        int i = 0;
        int maxRetries = 3;
        //Try it more than once, because the exception "Der Prozess kann nicht auf die Datei zugreifen, da sie von einem anderen Prozess verwendet wird" can appear, when windows had not completed copying catalog file
        while (i <= maxRetries) {
            i++;
            try (FileInputStream fileIn = new FileInputStream(pFile); 
                GZIPInputStream gis = new GZIPInputStream(fileIn)) {
                catalogObj = unserialize(gis);
                break;
            } catch (IOException ex) {
                if (i >= maxRetries) {
                    LOG.log(Level.SEVERE, "Cannot extract catalog zip file", ex);
                }
            }
        }
        return catalogObj;
    }
    @SuppressWarnings("unchecked")
    public static <E> E unserialize(final GZIPInputStream gis) throws IOException {
        if (gis == null) {
            return null;
        }
        ObjectInputStream ois = new ObjectInputStream(gis);
        E obj = null;
        try {
            obj = (E) ois.readObject();
        } catch (ClassNotFoundException ex) {
            LOG.log(Level.SEVERE, "Was not able to uncompress data", ex);
        }
        return obj;
    }
    
    public static double round(double pVal, int digits){
        return Precision.round(pVal, 2);
    }
    
    public static String getCurrentDate(){
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
            LocalDateTime now = LocalDateTime.now();
            return dtf.format(now); //09/03/2021 10:40:00
    }
}
