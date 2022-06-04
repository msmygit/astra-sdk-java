/*
 * Copyright DataStax, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS"
 * BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */

package com.datastax.astra.sdk.utils;

import static com.datastax.astra.sdk.config.AstraClientConfig.ASTRA_DB_APPLICATION_TOKEN;
import static com.datastax.astra.sdk.config.AstraClientConfig.ASTRA_DB_CLIENT_ID;
import static com.datastax.astra.sdk.config.AstraClientConfig.ASTRA_DB_CLIENT_SECRET;
import static com.datastax.astra.sdk.config.AstraClientConfig.ASTRA_DB_ID;
import static com.datastax.astra.sdk.config.AstraClientConfig.ASTRA_DB_KEYSPACE;
import static com.datastax.astra.sdk.config.AstraClientConfig.ASTRA_DB_REGION;
import static com.datastax.astra.sdk.config.AstraClientConfig.ASTRA_DB_SCB_FOLDER;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datastax.astra.sdk.databases.DatabasesClient;
import com.datastax.astra.sdk.databases.domain.Database;
import com.datastax.stargate.sdk.utils.Utils;

/**
 * Utility class to load/save .astrarc file. This file is used to store Astra configuration.
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public class AstraRcParser {

    /** Logger for our Client. */
    private static final Logger LOGGER = LoggerFactory.getLogger(AstraRcParser.class);

    /** Default filename we are looking for. */
    public static final String ASTRARC_FILENAME = ".astrarc";

    /** Default filename we are looking for. */
    public static final String ASTRARC_DEFAULT = "default";

    /** Environment variable coding user home. */
    public static final String ENV_USER_HOME = "user.home";

    /** line separator. */
    public static final String ENV_LINE_SEPERATOR = "line.separator";
    
    /** line separator. */
    public static final String LINE_SEPARATOR = System.getProperty(ENV_LINE_SEPERATOR);

    /** Sections in the file. [sectionName] -> key=Value. */
    private final Map<String, Map<String, String>> sections;
    
    /**
     * Load from ~/.astrarc
     */
    public AstraRcParser() {
        this.sections = AstraRcParser.load().getSections();
    }

    /**
     * Load from specified file
     * 
     * @param fileName
     *            String
     */
    public AstraRcParser(String fileName) {
        this.sections = AstraRcParser.load(fileName).getSections();
    }

    /**
     * Load from a set of keys (sections / Key / Value)
     * 
     * @param s
     *            Map
     */
    public AstraRcParser(Map<String, Map<String, String>> s) {
        this.sections = s;
    }

    /**
     * Getter accessor for attribute 'sections'.
     *
     * @return current value of 'sections'
     */
    public Map<String, Map<String, String>> getSections() {
        return sections;
    }

    /**
     * Display output in the console
     */
    public void print() {
        System.out.println(generateFileContent(getSections()));
    }

    /**
     * Helper to react a key in the file based on section name and key
     * 
     * @param sectionName
     *            String
     * @param key
     *            String
     * @return String
     */
    public String read(String sectionName, String key) {
        return (!sections.containsKey(sectionName)) ? null : sections.get(sectionName).get(key);
    }

    // -- Static operations --

    /**
     * Check if file ~/.astrac is present in the filesystem
     * 
     * @return File
     */
    public static boolean exists() {
        return getDefaultConfigFile().exists();
    }
    
    /**
     * Provide the configuration {@link File}.
     *
     * @return
     *      config file.
     */
    public static File getDefaultConfigFile() {
        return new File(System.getProperty(ENV_USER_HOME) + File.separator + ASTRARC_FILENAME);
    }
    
    /**
     * Create configuration file if not exist.
     * 
     * @return
     *      if the file has been created
     */
    public static boolean createIfNotExists() {
        File f = new File(System.getProperty(ENV_USER_HOME) + File.separator + ASTRARC_FILENAME);
        if (!f.exists()) {
            try {
                return f.createNewFile();
            } catch (IOException e) {
                throw new IllegalStateException("Cannot save configuration file in home directory " + 
                        System.getProperty(ENV_USER_HOME));
            }
        }
        return false;
    }

    /**
     * Generate astrarc based on values in DB using devops API.
     *
     * @param token
     *            token
     */
    public static void create(String token) {
        save(extractDatabasesInfos(token));
    }

    /**
     * Generate astrarc based on values in DB using devops API.
     *
     * @param token
     *            token
     * @param destination
     *            output to save the values
     */
    public static void create(String token, File destination) {
        save(extractDatabasesInfos(token), destination);
    }

    /**
     * Update only one key.
     * 
     * @param section
     *            String
     * @param key
     *            String
     * @param value
     *            String
     */
    public static void save(String section, String key, String value) {
        Map<String, Map<String, String>> astraRc = new HashMap<>();
        Map<String, String> val = new HashMap<>();
        val.put(key, value);
        astraRc.put(section, val);
        save(astraRc);
    }

    /**
     * Create the file from a list of key, merging with existing
     *
     * @param astraRc
     *            update .astrarc file.
     * @param destination
     *            destination to save the file
     */
    public static void save(Map<String, Map<String, String>> astraRc, File destination) {
        // This map is empty if file does not exist
        Map<String, Map<String, String>> targetAstraRc = astraRc;

        if (exists()) {
            targetAstraRc = load().getSections();

            // Merge if needed, append otherwize
            for (String dbName : astraRc.keySet()) {
                if (targetAstraRc.containsKey(dbName)) {
                    // overriding keys (merge)
                    targetAstraRc.get(dbName).putAll(astraRc.get(dbName));
                } else {
                    // Append
                    targetAstraRc.put(dbName, astraRc.get(dbName));
                }
                LOGGER.info("+ updating [" + dbName + "]");
            }
        }

        FileWriter out = null;
        try {
            out = new FileWriter(destination);
            out.write(generateFileContent(targetAstraRc));
            LOGGER.info("File {} has been successfully updated.", destination.getAbsolutePath());
        } catch (IOException e) {
            throw new IllegalStateException("Cannot save astrarc file", e);
        } finally {
            if (null != out) {
                try {
                    out.close();
                } catch (IOException e) {}
            }
        }
    }

    /**
     * Create the file from a list of key, merging with existing
     *
     * @param astraRc
     *            update .astrarc file.
     */
    public static void save(Map<String, Map<String, String>> astraRc) {
        LOGGER.info("Updating .astrarc file");
        save(astraRc, new File(System.getProperty(ENV_USER_HOME) + File.separator + ASTRARC_FILENAME));
    }

    /**
     * Loading ~/.astrarc (if present).
     * 
     * @return AstraRc
     */
    public static AstraRcParser load() {
        createIfNotExists();
        return load(getDefaultConfigFile().getAbsolutePath());
    }

    /**
     * Load configuration file.
     *  
     * @param file
     *      configuration file
     * @return
     *      parser.
     */
    public static AstraRcParser load(File file) {
        Map<String, Map<String, String>> sections = new HashMap<>();
        try (Scanner scanner = new Scanner(file)) {
            if (file.exists()) {
                String sectionName = "";
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    if (line.startsWith("[")) {
                        // Starting a new section
                        sectionName = line.replaceAll("\\[", "").replaceAll("\\]", "").trim();
                        sections.put(sectionName, new HashMap<>());
                    } else if (!line.startsWith("#") && !"".equals(line)) {
                        int off = line.indexOf("=");
                        if (off < 0) {
                            throw new IllegalArgumentException(
                                    "Cannot parse file " + file.getName() + ", line '" + line + "' invalid format expecting key=value");
                        }
                        String key = line.substring(0, off);
                        String val = line.substring(off + 1);
                        sections.get(sectionName).put(key, val);
                    }
                }
                scanner.close();
            }
        } catch (FileNotFoundException e) {
            throw new IllegalArgumentException("Cannot read configuration file", e);
        }
        return new AstraRcParser(sections);
    }
    
    /**
     * Loading ~/.astrarc (if present). Key = block name (dbname of default), then key/value
     * 
     * @param fileName
     *            String
     * @return AstraRc
     */
    public static AstraRcParser load(String fileName) {
        return load(new File(fileName));
        
    }

    /**
     * Prepare file content
     * 
     * @param astraRc
     *            Map
     */
    private static String generateFileContent(Map<String, Map<String, String>> astraRc) {
        StringBuilder sb = new StringBuilder();
        astraRc.entrySet().forEach(e -> {
            sb.append(LINE_SEPARATOR + "[" + e.getKey() + "]"+ LINE_SEPARATOR);
            e.getValue().entrySet().forEach(line -> {
                sb.append(line.getKey() + "=" + line.getValue() + LINE_SEPARATOR);
            });
        });
        return sb.toString();
    }

    /**
     * Use Astra Devops Api to list databases.
     * 
     * @param token
     *            token
     */
    private static Map<String, Map<String, String>> extractDatabasesInfos(String token) {
        // Look for 'non terminated DB' (limit 100),

        List<Database> dbs = new DatabasesClient(token).databasesNonTerminated().collect(Collectors.toList());

        // [default]
        Map<String, Map<String, String>> result = new HashMap<>();
        result.put(ASTRARC_DEFAULT, new TreeMap<>());
        result.get(ASTRARC_DEFAULT).put(ASTRA_DB_APPLICATION_TOKEN, token);
        if (dbs.size() > 0) {
            result.get(ASTRARC_DEFAULT).putAll(dbKeys(dbs.get(0), token));
        }
        // Loop on each database
        dbs.forEach(db -> result.put(db.getInfo().getName(), dbKeys(db, token)));
        return result;
    }

    /**
     * dbKeys
     * 
     * @param db
     *            Database
     * @param token
     *            String
     * @return Map
     */
    private static Map<String, String> dbKeys(Database db, String token) {
        Map<String, String> dbKeys = new HashMap<>();
        dbKeys.put(ASTRA_DB_ID, db.getId());
        dbKeys.put(ASTRA_DB_REGION, db.getInfo().getRegion());
        dbKeys.put(ASTRA_DB_KEYSPACE, db.getInfo().getKeyspace());
        dbKeys.put(ASTRA_DB_APPLICATION_TOKEN, token);
        dbKeys.put(ASTRA_DB_CLIENT_ID, "");
        dbKeys.put(ASTRA_DB_CLIENT_SECRET, "");
        dbKeys.put(ASTRA_DB_SCB_FOLDER, "");
        return dbKeys;
    }

    /**
     * Syntaxic sugar to read environment variables.
     *
     * @param arc
     *            AstraRc
     * @param key
     *            environment variable
     * @param sectionName
     *            section Name
     * @return if the value is there
     */
    public static Optional<String> readRcVariable(AstraRcParser arc, String key, String sectionName) {
        Map<String, String> section = arc.getSections().get(sectionName);
        if (section != null && section.containsKey(key) && Utils.hasLength(section.get(key))) {
            return Optional.ofNullable(section.get(key));
        }
        return Optional.empty();
    }

}