package com.datastax.astra.shell.cmd;

import java.util.Scanner;

import com.datastax.astra.sdk.AstraClient;
import com.datastax.astra.sdk.config.AstraClientConfig;
import com.datastax.astra.sdk.organizations.domain.Organization;
import com.datastax.astra.sdk.utils.AstraRc;
import com.datastax.astra.shell.jansi.Out;
import com.datastax.astra.shell.jansi.TextColor;
import com.datastax.astra.shell.utils.CommandLineUtils;
import com.datastax.astra.shell.utils.ShellPrinter;
import com.github.rvesse.airline.annotations.Command;

/**
 * Setup the configuration
 *
 * @author Cedrick LUNVEN (@clunven)
 */
@Command(name = "setup", description = "Initialize config file in ~/.astrarc")
public class SetupCommand implements Runnable {

    /** {@inheritDoc} */
    @Override
    public void run() {
        ShellPrinter.banner();
        System.out.println("+-------------------------------+");
        System.out.println("+-           Setup.            -+");
        System.out.println("+-------------------------------+");
        System.out.println("\nWelcome to Astra Shell/CLI. We will guide you to start.");
        
        Out.println("\nHow it works ?\n", TextColor.CYAN);
        System.out.println("Astra Cli and shell (interactive) leverage a configuration file (~/.astrarc) avoiding users to have to enter credentials each time. "
                + "The file is divided in sections identified by a name. If user does not specify section name the [default] is used."
                + " For each section, key 'ASTRA_DB_APPLICATION_TOKEN' is mandatory: it is the authentication token "
                + " used to invoke Astra Apis. More keys can be added to change scope or settings. Here is a sample file:");
        
        Out.color(TextColor.YELLOW);
        System.out.println("\n[default]");
        System.out.println(AstraClientConfig.ASTRA_DB_APPLICATION_TOKEN + "=AstraCS:aaaaa......");
        System.out.println("\n[my_dev_env]");
        System.out.println(AstraClientConfig.ASTRA_DB_APPLICATION_TOKEN + "=AstraCS:abcde......");
        System.out.println(AstraClientConfig.ASTRA_DB_ID + "=924e6ab3-eeb5-45e1-9861-5abcdc62f343");
        System.out.println(AstraClientConfig.ASTRA_DB_REGION + "=europe-west-1");
        System.out.println("\n[my_prod_env]");
        System.out.println(AstraClientConfig.ASTRA_DB_APPLICATION_TOKEN + "=AstraCS:12345......");
        System.out.println(AstraClientConfig.ASTRA_DB_ID + "=924e6ab3-eeb5-45e1-9861-5abcdc62f34444");
        System.out.println(AstraClientConfig.ASTRA_DB_REGION + "=europe-west-1");
        Out.reset();
        
        Out.println("\nGetting Started\n", TextColor.CYAN);
        System.out.println("You need to get an Astra token, here is the procedure to create one :\nhttps://awesome-astra.github.io/docs/pages/astra/create-token/");
        
        System.out.println("\nWe will now create a section. "
                + "(if first, will be set as default).");
        
        AstraRc.createIfNotExists();
        AstraRc existingConfiguration = AstraRc.load();
        
        String token = null;
        try(Scanner scanner = new Scanner(System.in)) {
            boolean valid_token = false;
            while (!valid_token) {
                Out.print("\n - Enter a token (eg: AstraCS...) : ", TextColor.CYAN);
                token = scanner.nextLine();
                if (!token.startsWith("AstraCS:")) {
                    Out.error("Your token should start with 'AstraCS:'");
                } else {
                    try {
                        Organization o = AstraClient
                                .builder()
                                .withToken(token)
                                .build()
                                .apiDevopsOrganizations()
                                .organization();
                        valid_token = true;
                        Out.success("Valid Token, related organization is '" + o.getName() + "'");
                        System.out.println("");
                        String sectionName = o.getName();
                        if (CommandLineUtils.askForConfirmation(scanner, 
                                " - Do you want to set custom section name (current: " + sectionName + ")")) {
                            Out.print("Enter a name for this config : ", TextColor.CYAN);
                            sectionName = scanner.nextLine();
                        }
                        if (existingConfiguration.getSections().isEmpty()) {
                            AstraRc.save(AstraRc.ASTRARC_DEFAULT, 
                                    AstraClientConfig.ASTRA_DB_APPLICATION_TOKEN, token);
                        }
                        AstraRc.save(sectionName, AstraClientConfig.ASTRA_DB_APPLICATION_TOKEN, token);
                        Out.success("Configuration Saved.\n");
                    } catch(IllegalArgumentException iexo) {
                        Out.error("Your token seems invalid, it was not possible to connect to Astra."); 
                    }
                }
            }
           
            new ShowConfigsCommand().run();
            System.out.println("");
            
            if (CommandLineUtils.askForConfirmation(scanner, 
                    "Do you want it to be your default organization")) {
                    AstraRc.save(AstraRc.ASTRARC_DEFAULT, 
                            AstraClientConfig.ASTRA_DB_APPLICATION_TOKEN, token);
            }
            
            System.out.println("");
            new ShowConfigsCommand().run();
            
            System.out.println("\nTo change default organization: astra default-org <orgName>");
        }
    }
    
    

}
