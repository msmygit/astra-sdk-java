package com.datastax.astra.shell;

import com.datastax.astra.shell.cmd.ExitCommand;
import com.datastax.astra.shell.cmd.HelpCustomCommand;
import com.datastax.astra.shell.cmd.ShowConfigCommand;
import com.datastax.astra.shell.cmd.db.ShowDatabasesCommand;
import com.datastax.astra.shell.cmd.shell.ConnectCommand;
import com.datastax.astra.shell.cmd.shell.EmptyCommand;
import com.datastax.astra.shell.jansi.Out;
import com.datastax.astra.shell.jansi.TextColor;
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
    HelpCustomCommand.class,
    ExitCommand.class
},
  groups = {
          @Group(
              name = "show",
              description = "Listing details of an entity or entity list",
              commands = { ShowConfigCommand.class, ShowDatabasesCommand.class }
          )
    })
public class AstraShell {
    
    public static void main(String[] args) {
        com.github.rvesse.airline.Cli<Runnable> cli = 
                new com.github.rvesse.airline.Cli<>(AstraShell.class);
        try {
            // Parsing
            Runnable cmd = cli.parse(args);
            
            // Interogation failed
            cmd.run();
            
        } catch(ParseArgumentsUnexpectedException ex) {
            Out.println("Invalid command: " + ex.getMessage(), TextColor.RED);
            
        } catch(Exception e) {
            Out.println("An error occured during exection " + e.getMessage(), TextColor.RED);
            
        }
    }

}