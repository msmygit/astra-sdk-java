package com.datastax.astra.shell;

import com.datastax.astra.shell.cmd.ExitCommand;
import com.datastax.astra.shell.cmd.HelpCommand;
import com.datastax.astra.shell.cmd.QuitCommand;
import com.datastax.astra.shell.cmd.db.DbListShell;
import com.datastax.astra.shell.cmd.iam.ShowRoleCommand;
import com.datastax.astra.shell.cmd.iam.ShowRolesCommand;
import com.datastax.astra.shell.cmd.iam.ShowUserCommand;
import com.datastax.astra.shell.cmd.iam.ShowUsersCommands;
import com.datastax.astra.shell.cmd.shell.ConnectCommand;
import com.datastax.astra.shell.cmd.shell.CqlShCommand;
import com.datastax.astra.shell.cmd.shell.EmptyCommand;
import com.datastax.astra.shell.cmd.show.ShowConfigsCommand;
import com.datastax.astra.shell.cmd.use.UseDb;
import com.datastax.astra.shell.utils.LoggerShell;
import com.github.rvesse.airline.annotations.Cli;
import com.github.rvesse.airline.annotations.Group;
import com.github.rvesse.airline.parser.errors.ParseArgumentsUnexpectedException;

/**
 * Shell in an interactive CLI.
 *
 * @author Cedrick LUNVEN (@clunven)
 */
@Cli(
  name        = "shell", 
  description = "Interactive Shell for DataStax Astra",
  defaultCommand = 
    EmptyCommand.class, 
  commands       = { 
    ConnectCommand.class,
    EmptyCommand.class,
    HelpCommand.class,
    ExitCommand.class,
    QuitCommand.class,
    CqlShCommand.class
},
  groups = {
          @Group(
              name = "show",
              description = "Listing details of an entity or entity list",
              commands = { 
                      DbListShell.class,
                      ShowRoleCommand.class, ShowRolesCommand.class, 
                      ShowUserCommand.class, ShowUsersCommands.class,
                      ShowConfigsCommand.class }
          ),
          @Group(
               name = "use",
               description = "Focus on an entity (context & prompt changed)",
               commands = {
                      UseDb.class
               }
          )
    })
public class AstraShell {
    
    /**
     * Main program for the interactive Shell.
     * 
     * @param args
     *      cli arguments
     */
    public static void main(String[] args) {
        try {

            new com.github.rvesse.airline.Cli<Runnable>(AstraShell.class)
               .parse(args)  // Find the processor for the command 
               .run();       // Run the command
            
        } catch(ParseArgumentsUnexpectedException ex) {
            LoggerShell.error("Invalid command: " + ex.getMessage());
        } catch(Exception e) {
            LoggerShell.error("Execution error:" + e.getMessage());
        }
    }

}
