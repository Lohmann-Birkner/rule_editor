/* 
 * Copyright (c) 2016 Lohmann & Birkner.
 * NOTICE:  All information contained herein is, and remains
 * the property of Lohmann & Birkner and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Lohmann & Birkner
 * and its suppliers and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Lohmann & Birkner.
 * http://www.lohmann-birkner.de/de/index.php
 *
 * Contributors:
 *    2016  Somebody - initial API and implementation and/or initial documentation
 */
package de.lb.ruleprocessor.json_processor;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 *
 * @author niemeier
 */
public abstract class JsonFileManager {

    private String mFilename = "";
    private File mFile = null;
    private Gson mGson = null;

    /**
     *
     * @param pFilename file name
     * @param pIsReader is readonly?
     */
    public JsonFileManager(final String pFilename, final boolean pIsReader) {
        mFilename = pFilename;
        mFile = new File(mFilename);
        initGson(pIsReader);
    }

    /**
     *
     * @throws IOException cannot open file
     */
    public abstract void openFile() throws IOException;

    /**
     *
     */
    public abstract void closeFile();

    /**
     *
     * @return file
     */
    public File getFile() {
        return mFile;
    }

    /**
     *
     * @return file name
     */
    public String getFilename() {
        return mFilename;
    }

    private void initGson(final boolean pIsReader) {
        GsonBuilder lBuilder = new GsonBuilder();
        //if (!Iskv21cDownloaderProperties.getInstance().getCompactDump()) {
        lBuilder = lBuilder.setPrettyPrinting();
        //}

        mGson = lBuilder
                .serializeNulls()            
                .create();
    }

    /**
     *
     * @return gson
     */
    protected Gson getGson() {
        return this.mGson;
    }

    /**
     *
     * @throws IOException cannot compress json file
     */
    public void compressFile() throws IOException {
        //Nur als ZIP komprimieren, falls die Datei größer als 0,5 MB ist
        //if (lFile.length()/1024 > 512) {
        zipFile(getFile().getAbsolutePath());
        //lFile.delete();
        //}
    }

    /**
     *
     * @param pSourceFileName source file name
     * @throws IOException cannot compress file
     */
    protected void zipFile(String pSourceFileName) throws IOException {
        pSourceFileName = toStr(pSourceFileName);
        String pTargetFileName = pSourceFileName + ".zip";
        zipFile(pSourceFileName, pTargetFileName);
    }

    /**
     *
     * @param pSourceFileName source file name
     * @param pTargetFileName target file name
     * @throws IOException cannot compress file
     */
    protected void zipFile(String pSourceFileName, String pTargetFileName) throws IOException {
        pSourceFileName = toStr(pSourceFileName);
        pTargetFileName = toStr(pTargetFileName);

        File lSourceFile = new File(pSourceFileName);

        if (pSourceFileName.isEmpty()) {
            return;
        }

        byte[] lBuffer = new byte[1024];

        FileOutputStream lFos = new FileOutputStream(pTargetFileName);
        try (ZipOutputStream lZos = new ZipOutputStream(lFos)) {
            ZipEntry lZe = new ZipEntry(lSourceFile.getName());
            lZos.putNextEntry(lZe);
            try (FileInputStream lIn = new FileInputStream(pSourceFileName)) {
                int lLength;
                while ((lLength = lIn.read(lBuffer)) > 0) {
                    lZos.write(lBuffer, 0, lLength);
                }
            }
            lZos.closeEntry();
        }
    }

    public static String toStr(final String pValue) {
        return (pValue == null) ? "" : pValue.trim();
    }
}
