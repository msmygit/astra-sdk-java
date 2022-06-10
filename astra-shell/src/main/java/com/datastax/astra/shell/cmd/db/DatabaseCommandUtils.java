package com.datastax.astra.shell.cmd.db;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.datastax.astra.sdk.databases.DatabaseClient;
import com.datastax.astra.sdk.databases.DatabasesClient;
import com.datastax.astra.sdk.databases.domain.Database;
import com.datastax.astra.shell.ShellContext;
import com.datastax.astra.shell.jansi.Out;

public class DatabaseCommandUtils {

    /** Command constants. */
    public static final String DB        = "db";
    public static final String DATABASE  = "database";
    
    public static final String DBS       = "dbs"; 
    public static final String DATABASES = "databases"; 
    
    /**
     * Load the databaseClient by user input.
     * 
     * @param db
     *      database name or identifier
     * @return
     *      db id
     */
    public static Optional<DatabaseClient> retrieveDatabaseClient(String db) {
        DatabasesClient dbsClient = ShellContext.getInstance()
                    .getAstraClient()
                    .apiDevopsDatabases();
        
        // Try with the id (fastest)
        DatabaseClient dbClient = dbsClient.database(db);
        if (dbClient.exist()) {
            return Optional.ofNullable(dbClient);
        }
        
        // Not found, try with the name
        List<Database> dbs = dbsClient
                .databasesNonTerminatedByName(db)
                .collect(Collectors.toList());
        
        // Multiple db with this name
        if (dbs.size() > 1) {
            Out.error("There are '" + dbs.size() + "' dbs with this name, try with id.");
            return Optional.empty();
        }
        
        // Db Found
        if (1 == dbs.size()) {
            return Optional.ofNullable(dbsClient.database(dbs.get(0).getId()));
        }
        
        Out.error("'" + db + "' database not found.");
        return Optional.empty();
    }
        
}
