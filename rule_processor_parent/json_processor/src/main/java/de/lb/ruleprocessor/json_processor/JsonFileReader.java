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

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import de.lb.ruleprocessor.create_criterien.inoutCrit.CriteriumContainer;


import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author niemeier
 */
public class JsonFileReader extends JsonFileManager {

    /**
     *
     */
    public static final String ENCODING = "Cp1252";
    private static final Logger LOG = Logger.getLogger(JsonFileReader.class.getName());
    private JsonReader mReader = null;

    /**
     *
     * @param pFilename file name
     */
    public JsonFileReader(String pFilename) {
        super(pFilename, true);
    }

    /**
     *
     * @throws IOException cannot open file
     */
    @Override
    public synchronized void openFile() throws IOException {
        if (mReader == null) {
            mReader = new JsonReader(new InputStreamReader(new FileInputStream(getFile()), ENCODING));
            mReader.setLenient(true);
        }
    }

    /**
     *
     * @return case container
     * @throws IOException cannot read file
     */
    public synchronized CriteriumContainer readFile() throws IOException {
        openFile();
        CriteriumContainer container = null;
        if (mReader.hasNext()) {
            JsonToken lToken = mReader.peek();
            if (lToken == JsonToken.END_DOCUMENT) {
                return container;
            }
            // Read data into object model
            //Person person = gson.fromJson(reader, Person.class);
            container = getGson().fromJson(mReader, CriteriumContainer.class);
        }


        return container;
    }

    /**
     *
     */
    @Override
    public synchronized void closeFile() {
        if (mReader != null) {
            try {
                mReader.close();
            } catch (IOException lEx) {
                LOG.log(Level.SEVERE, lEx.getMessage(), lEx);
            }
        }
    }

}
