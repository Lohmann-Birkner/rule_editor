/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.lb.ruleprocessor.hl7reader;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.model.api.ICompositeDatatype;
import ca.uhn.fhir.model.api.IDatatype;
import ca.uhn.fhir.model.dstu2.composite.AddressDt;
import ca.uhn.fhir.model.dstu2.composite.BoundCodeableConceptDt;
import ca.uhn.fhir.model.dstu2.composite.CodeableConceptDt;
import ca.uhn.fhir.model.dstu2.composite.ContactPointDt;
import ca.uhn.fhir.model.dstu2.composite.HumanNameDt;
import ca.uhn.fhir.model.dstu2.composite.IdentifierDt;
import ca.uhn.fhir.model.dstu2.composite.PeriodDt;
import ca.uhn.fhir.model.dstu2.composite.ResourceReferenceDt;
import ca.uhn.fhir.model.dstu2.resource.BaseResource;
import ca.uhn.fhir.model.dstu2.resource.Patient;
import ca.uhn.fhir.model.dstu2.resource.Patient.Communication;
import ca.uhn.fhir.model.dstu2.resource.Patient.Contact;
import ca.uhn.fhir.model.dstu2.resource.Patient.Link;
import ca.uhn.fhir.model.dstu2.valueset.MaritalStatusCodesEnum;
import ca.uhn.fhir.model.primitive.BooleanDt;
import ca.uhn.fhir.parser.DataFormatException;

import ca.uhn.fhir.parser.IParser;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.hl7.fhir.instance.model.api.IPrimitiveType;

/**
 *
 * @author gerschmann
 */
public class ReadHl7Patient {

    private static final Logger LOG = Logger.getLogger(ReadHl7Patient.class.getName());
    private static String jsonPath;
    
    public static void main(String args[]){
        if(args.length == 0){
            LOG.log(Level.INFO,"usage: Path to json");
            System.exit(0);
        }
        
        ReadHl7Patient patient = new ReadHl7Patient(args[0]);
        try {
            patient.readJson();
        } catch (Exception ex) {
            Logger.getLogger(ReadHl7Patient.class.getName()).log(Level.SEVERE, null, ex);
        }
         System.exit(0);
    }

    private ReadHl7Patient(String pPath) {
        jsonPath = pPath;
    }

    private void readJson()throws Exception{
        try {
                FhirContext context = FhirContext.forDstu2();
                IParser parser = context.newJsonParser();

                Package pack = Patient.class.getPackage();

                File jFile = new File(jsonPath);
                if(jFile.exists() && jFile.canRead()){
                    byte[] bytes =  Files.readAllBytes(Paths.get(jsonPath));
//                    ObjectMapper mapper = new ObjectMapper();
//                    Map<String, String> map = new HashMap<>();
//                    map = mapper.readValue(bytes, HashMap.class);
//                    dumpMap(map);
                    Patient pat = parser.parseResource(Patient.class, new FileInputStream(jFile));
                    if(pat != null){
                        Link link = pat.getLinkFirstRep();
                        StringBuilder dump = new StringBuilder();
                        dumpResource(pat, dump);
                        LOG.log(Level.INFO, dump.toString());
                        
                        if(dump.toString().length() > 0){
                            return;
                        } 
                        List<IdentifierDt> identifiers = pat.getIdentifier();
                        dumpIdent( pat.getIdentifierFirstRep(), true);
                        identifiers.forEach((dt) -> {
                            dumpIdent(dt, false);
                        });
                        
                        List<HumanNameDt> names = pat.getName();

                        dumpName(pat.getNameFirstRep(), true);
                        names.forEach((name) -> {
                            dumpName(name, false);
                        });
                        
                        AddressDt address = pat.getAddressFirstRep();
                        dumpAddress(address, true);
                        List<AddressDt> addrs = pat.getAddress();
                        addrs.forEach((addr) -> {
                            dumpAddress(addr, false);
                        });

                        Date birthDate = pat.getBirthDate();
                        System.out.println("Birthdate: " + birthDate.toString());
                        IDatatype mb = pat.getMultipleBirth();
                        String cls = mb.getClass().getSimpleName();
                        if(cls.equals("BooleanDt")){
                            BooleanDt mbB = (BooleanDt)mb;
                            System.out.println("getMultipleBirth: " + mbB.toString());
                        }
                        List<ResourceReferenceDt>  careProvider = pat.getCareProvider();
                        if(careProvider != null && !careProvider.isEmpty()){
                            //TODO:
                        }
                        
                        Communication communication = pat.getCommunicationFirstRep();
                        dumpCommunication(communication, true);
                        List<Communication> comms = pat.getCommunication();
                        if(comms != null && !comms.isEmpty()){
                            comms.forEach((comm) -> {
                                dumpCommunication(comm, false);
                            });
                        }
                        
                        Contact cont = pat.getContactFirstRep();
                        dumpContact(cont, true);
                        
                        List<Contact> conts = pat.getContact();
                        if(conts != null && !conts.isEmpty()){
                            conts.forEach((cnt) -> {
                                dumpContact(cnt, false);
                            });
                        }
                        
                        IDatatype deseased = pat.getDeceased();
                        if(deseased != null){
                            System.out.println(deseased.toString());
                        }
                        
                        String gender = pat.getGender();
                        if(gender != null){
                            System.out.println("Gender: " + gender);
                        }
                        
                        BoundCodeableConceptDt<MaritalStatusCodesEnum>  mart = pat.getMaritalStatus();
                        if(mart != null){
                            System.out.println("getMaritalStatus: " + mart.getText());
                        }
                        
                        ContactPointDt telecom = pat.getTelecomFirstRep();
                        dumpTelecom(telecom, true);
                        List<ContactPointDt> tels = pat.getTelecom();
                        if(tels != null &&!tels.isEmpty()){
                            tels.forEach((tel) -> {
                                dumpTelecom(tel, false);
                            });
                        }
                        
                    }
                }
            } catch ( DataFormatException | FileNotFoundException ex) {
                Logger.getLogger(ReadHl7Patient.class.getName()).log(Level.SEVERE, null, ex);
            }

    }
    
    private void dumpName(HumanNameDt name, boolean isRep){
        System.out.println(isRep?"\nNameFirstRep":"");
        if(name == null){
            return;
        }
        String use = name.getUse();
        String family = name.getFamilyAsSingleString();
        String prefix = name.getPrefixAsSingleString();
        String given = name.getGivenAsSingleString();

        System.out.println("use: " + use);
        System.out.println("family: " + family);
        System.out.println("prefix: " + prefix);
        System.out.println("given: " + given);
       
    }
    
    private void dumpIdent(IdentifierDt pIdent, boolean isRep){
        String idSystemString = pIdent.getSystem();
        String idValueString = pIdent.getValue();

        System.out.println(isRep?"\nRep":"" + "Ident: " + idSystemString + " " + idValueString);
        
    }
    

    private void dumpAddress(AddressDt address, boolean isRep) {
        System.out.println(isRep?"\ngetAddressFirstRep":"");        
        if(address == null){
            return;
        }
        address.getPostalCode();
        System.out.println("city: " +  address.getCity());
        System.out.println("state: " + address.getState());
        System.out.println("country: " + address.getCountry());
        System.out.println("postalCode: " + address.getPostalCode());

    }

    private void dumpCommunication(Communication communication, boolean isRep) {
        System.out.println(isRep?"\ngetCommunicationRep":"");        
        if(communication == null){
            return;
        }
        CodeableConceptDt lg = communication.getLanguage();

        System.out.println("CodeableConceptDt: " +  lg == null?"null":lg.getText());

    }

    private void dumpContact(Contact contact, boolean isRep) {
        System.out.println(isRep?"\ngetContactRep":"");        
        if(contact == null){
            return;
        }
        dumpAddress(contact.getAddress(), false);
        System.out.println("Contact.gender: " + contact.getGender());
        dumpName(contact.getName(), false);
        System.out.println("Contact.organisation: " + (contact.getOrganization() == null?"null":contact.getOrganization().toString()));
        
       dumpPeriod(contact.getPeriod());
       dumpRelationship(contact.getRelationshipFirstRep(), true);
        
         
        List<CodeableConceptDt>  rls = contact.getRelationship();
        if(rls!= null && !rls.isEmpty()){
            rls.forEach((rl) -> {
                dumpRelationship(rl, false);
            });
        }
        
        dumpTelecom(contact.getTelecomFirstRep(), true);
        List<ContactPointDt> tels = contact.getTelecom();
        if(tels != null &&!tels.isEmpty()){
            tels.forEach((tel) -> {
                dumpTelecom(tel, false);
            });
        }
    }

    private void dumpTelecom(ContactPointDt telecom, boolean isRep) {
        System.out.println(isRep?"\ngetTelecomRep":"");   
        if(telecom == null){
            return;
        }
        dumpPeriod(telecom.getPeriod());
             
        System.out.println("telecom.getRank: " + (telecom.getRank() == null?"null":telecom.getRank().intValue()));
        
        System.out.println("telecom.getSystem: " + (telecom.getSystem() == null?"null":telecom.getSystem()));
        
        System.out.println("telecom.getUse: " + (telecom.getUse() == null?"null":telecom.getUse()));
        
        System.out.println("telecom.getValue: " + (telecom.getValue()== null?"null":telecom.getValue()));
    }
    
    
    private void dumpResource(Object pResource, StringBuilder dump ) throws Exception{
       
//        Field[] fields = pResource.getClass().getDeclaredFields();
//        for(Field field:fields){
//            Class cl =field.getType();
//            field.setAccessible(true);
////            switch(cl.getTypeName()){
////                case "java.lang.String":
//                    dump.append(field.getName()).append(": ").append(" datatype: ").append(cl.getTypeName());
////                    break;
////.append(
////                    field.get(pPatient))
////            }
////        }
////             
//            dump.append("\n");
//        }
        dump.append("\n");
        Method[] methods = pResource.getClass().getDeclaredMethods();
        for(Method method: methods){
            if(method.getName().startsWith("get") && method.getName().endsWith("Rep") && method.getParameterCount() == 0){
                Class sc =method.getReturnType();
                dump.append(method.getName()).append(" return type: ").append(sc.getCanonicalName()).append("\n");

                Object obj = method.invoke(pResource);
                if(obj == null){
                    LOG.log(Level.INFO, "{0} returns null", method.getName());
                    continue;
                }
                //simple java classes
                String pack = obj.getClass().getPackageName();
                if(pack.startsWith("java")){
                    if(obj instanceof List){

                        dump.append(" listtype: ").append(obj.getClass().arrayType().getComponentType().getTypeName());
                        if(!((List)obj).isEmpty()){
                            Object obj1 = ((List)obj).get(0);
                            obj1.getClass().getTypeName();
                            dump.append(" list members type: ").append(obj1.getClass().getTypeName()).append("\n");
                            for(Object o: (List)obj){
                                dumpResource(o, dump);
                            }
                        }
                        
                    }    else{
                        dump.append(" value: ").append(String.valueOf(obj));
                    }
                }else if(obj instanceof BaseResource || obj instanceof ICompositeDatatype){
                    dumpResource(obj, dump);
                }else if (obj instanceof IPrimitiveType){
                    dump.append("value: ").append(((IPrimitiveType) obj).getValueAsString());
                }
                dump.append("\n");
            }
            
        }
        LOG.log(Level.INFO, dump.toString());
    }

    private void dumpPeriod(PeriodDt period) {
        System.out.println("period: ");
        if(period == null){
            return;
        }
        System.out.println("period.start: " + period.getStart() == null?"null":period.getStart());
        System.out.println("period.end: " + period.getEnd() == null?"null":period.getEnd());
    }

    private void dumpRelationship(CodeableConceptDt relationship, boolean isRep) {
        System.out.println("relationship: ");
        if(relationship == null){
            return;
        }

        System.out.println("relationship.getText: " + (relationship.getText() == null?"null":relationship.getText()));
    }

    private void dumpMap(Map<String, String> map) {
        Set<String> keys = map.keySet();
        for(String key:keys){
            Object obj = map.get(key);
            System.out.println("key: " + key);
            switch(obj.getClass().getSimpleName()){
                case "String": System.out.println("Stringvalue: "+ (String)obj);
                break;
                case "ArrayList":
                    dumpArrayList((ArrayList)obj);
                    break;
                default: System.out.println("class: " + obj.getClass().getSimpleName());
                    
            }
        }
    }

    private void dumpArrayList(ArrayList list){
        System.out.println("Arraylist size: " + list.size());
        for(Object obj: list){
            System.out.println("class: " + obj.getClass().getSimpleName());
            if(obj instanceof Map){
                dumpMap((Map)obj);
            }
        }
    }
}
