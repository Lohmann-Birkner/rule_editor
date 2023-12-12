package de;

import java.io.IOException;
import java.util.Iterator;
import java.io.InputStream;
import java.io.BufferedReader;
import java.util.ArrayList;
import java.io.InputStreamReader;
import java.util.List;
import java.net.URL;
import java.util.jar.JarFile;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.Set;
import java.util.HashSet;
import java.io.File;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2004</p>
 *
 * <p>Company: </p>
 *
 * @author unbekannt
 * @version 1.0
 */
public class FileUtils
{

    private static final Logger LOG = Logger.getLogger(FileUtils.class.getName());
    
    public static List<String> readTextFromJar(String s) {
       InputStream is = null;
       BufferedReader br = null;
       String line;
       ArrayList<String> list = new ArrayList<String>();
//       System.out.println(s);
       try {
         is = FileUtils.class.getResourceAsStream("/" + s);
         br = new BufferedReader(new InputStreamReader(is));
         while (null != (line = br.readLine())) {
            list.add(line);
         }
       }
       catch (Exception e) {
        LOG.log(Level.SEVERE, "error in file name:" + (s == null?"null":s), e);
        System.out.println((s == null?"null":s));
         e.printStackTrace();
       }
       finally {
         try {
           if (br != null) br.close();
           if (is != null) is.close();
         }
         catch (IOException e) {
           e.printStackTrace();
         }
       }
       return list;
     }

     public static void main(String args[]) throws IOException{
       List<String> list = FileUtils.readTextFromJar("/datafile1.txt");
       Iterator<String> it = list.iterator();
       while(it.hasNext()) {
         System.out.println(it.next());
       }

       list = FileUtils.readTextFromJar("/test/datafile2.txt");
       it = list.iterator();
       while(it.hasNext()) {
         System.out.println(it.next());
       }
     }

     /**
   * List directory contents for a resource folder. Not recursive.
   * This is basically a brute-force implementation.
   * Works for regular files and also JARs.
   *
   * @author Greg Briggs
   * @param clazz Any java class that lives in the same place as the resources you want.
   * @param path Should end with "/", but not start with one.
   * @return Just the name of each member item, not the full paths.
   * @throws URISyntaxException exc
   * @throws IOException exc
   */
  public static String[] getResourceListing(Class clazz, String path) throws URISyntaxException, IOException {
      URL dirURL = clazz.getClassLoader().getResource(path);
      if (dirURL != null && dirURL.getProtocol().equals("file")) {
        /* A file path: easy enough */
        return new File(dirURL.toURI()).list();
      }

      if (dirURL == null) {
        /*
         * In case of a jar file, we can't actually find a directory.
         * Have to assume the same jar as clazz.
         */
        String me = clazz.getName().replace(".", "/")+".class";
        dirURL = clazz.getClassLoader().getResource(me);
      }
      return getEntryListFromJar(dirURL, ".class", path);

  }
  public static String[] getEntryListFromJar(URL dirURL, String suffix, String path) throws IOException
{
//    System.out.println("jar Protocol = " + dirURL.getProtocol());
//    System.out.println("jar Path = " + dirURL.getPath());
      if (dirURL.getProtocol().equals("jar")) {
        /* A JAR path */
        String jarPath = dirURL.getPath().substring(5, dirURL.getPath().indexOf("!")); //strip out only the JAR file
 //       System.out.println("jarPath = " + jarPath);
        JarFile jar = new JarFile(URLDecoder.decode(jarPath, "UTF-8"));
        Enumeration<JarEntry> entries = jar.entries(); //gives ALL entries in jar
        Set<String> result = new HashSet<>(); //avoid duplicates in case it is a subdirectory
        while(entries.hasMoreElements()) {
          String name = entries.nextElement().getName();
          if (name.startsWith(path)) { //filter according to the path
            String entry = name.substring(path.length() + 1);
//            System.out.println(name+ " " + entry);
            if(entry.endsWith(".class")){
                 result.add(entry);
            }
          }
        }
        return result.toArray(new String[result.size()]);
      }
    if (dirURL.getProtocol().equals("vfs")) {
    /* A JAR path */
        String jarPath = dirURL.getPath().substring(1, dirURL.getPath().indexOf(".jar"))+".jar"; //strip out only the JAR file
        JarFile jar = new JarFile(URLDecoder.decode(jarPath, "UTF-8"));
        Enumeration<JarEntry> entries = jar.entries(); //gives ALL entries in jar
        Set<String> result = new HashSet<>(); //avoid duplicates in case it is a subdirectory
        while(entries.hasMoreElements()) {
            String name = entries.nextElement().getName();
            if(!name.equals(path)){
                if (name.startsWith(path)) { //filter according to the path
                    String entry = name.substring(path.length()+1);
                    if(entry.endsWith(".class")){//suffix)){
                         result.add(entry);
                    }
                }
            }
        }
    }
      throw new UnsupportedOperationException("Cannot list files for URL "+dirURL);
  }

}
