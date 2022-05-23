package com.datastax.astra.cmd;

import com.datastax.astra.ansi.BackgroundColor;
import com.datastax.astra.ansi.Out;
import com.datastax.astra.ansi.TextColor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Render all component for the FF4J commands.
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public class ShellPrinter {
  
	/** Default constructor. */
	private ShellPrinter() {}
	
	/** Start Banner. */
    public static void printBanner() {
        Out.setup(TextColor.CYAN, null, null);
        System.out.println("");
        System.out.print("  █████╗ ███████╗████████╗██████╗  █████╗   ");
        Out.setup(TextColor.MAGENTA, null, null);
        System.out.println("  ███████╗██╗  ██╗");
        Out.setup(TextColor.CYAN, null, null);
        System.out.print(" ██╔══██╗██╔════╝╚══██╔══╝██╔══██╗██╔══██╗  ");
        Out.setup(TextColor.BLUE, null, null);
        System.out.println("  ██╔════╝██║  ██║");
        Out.setup(TextColor.CYAN, null, null);
        System.out.print(" ███████║███████╗   ██║   ██████╔╝███████║  ");
        Out.setup(TextColor.GREEN, null, null);
        System.out.println("  ███████╗███████║");
        Out.setup(TextColor.CYAN, null, null);
        System.out.print(" ██╔══██║╚════██║   ██║   ██╔══██╗██╔══██║  ");
        Out.setup(TextColor.YELLOW, null, null);
        System.out.println("  ╚════██║██╔══██║");
        Out.setup(TextColor.CYAN, null, null);
        System.out.print(" ██║  ██║███████║   ██║   ██║  ██║██║  ██║  ");
        Out.setup(TextColor.RED, null, null);
        System.out.println("  ███████║██║  ██║");
        Out.setup(TextColor.CYAN, null, null);
        System.out.print(" ╚═╝  ╚═╝╚══════╝   ╚═╝   ╚═╝  ╚═╝╚═╝  ╚═╝  ");
        Out.setup(TextColor.RED, null, null);
        System.out.println("  ╚══════╝╚═╝  ╚═╝");
        System.out.println("");
        Out.setup(TextColor.RESET, null, null);
        System.out.print(" Version: ");
        Out.print("0.1.0", TextColor.GREEN);
        System.out.println("\n");
    }
    
    private static final ObjectMapper MAPPER = new ObjectMapper();
    
    /**
     * Show object as Json in console.
     *
     * @param obj
     *      object
     * @param color
     *      color
     */
    public static final void printObjectAsJson(Object obj, TextColor color) {
        try {
            Out.println(MAPPER
                  .writerWithDefaultPrettyPrinter()
                  .writeValueAsString(obj), color);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Cannot serialize object as JSON", e);
        }
    }
    
	/**
	 * Print the Datastax Devs logo in console
	 */
	public static final void printDatastaxDevs() {
	    Out.setup(TextColor.BLUE, BackgroundColor.WHITE, null);
        System.out.println("    ___      _        __ _                 ___                ");
        System.out.println("   /   \\__ _| |_ __ _/ _\\ |_ __ ___  __   /   \\_____   _____  ");
        System.out.println("  / /\\ / _` | __/ _` \\ \\| __/ _` \\ \\/ /  / /\\ / _ \\ \\ / / __\\ ");
        System.out.println(" / /_// (_| | || (_| |\\ \\ || (_| |>  <  / /_//  __/\\ V /\\__ \\ ");
        System.out.println("/___,' \\__,_|\\__\\__,_\\__/\\__\\__,_/_/\\_\\/___,' \\___| \\_/ |___/ ");
        System.out.println("                                                              ");
        Out.setup(TextColor.RESET, null, null);
        
	}
 	
	/**
	 * Will print Promt based on the current state.
	 * 
	 * @param ctx
	 *         current context.
	 */
	public static void printPrompt(ShellContext ctx) {
	    System.out.println("");
	    if (ctx.getOrganization() != null) {
	        Out.print(ctx.getOrganization().getName(), TextColor.GREEN);
	    }
	    Out.print(">", TextColor.GREEN);
	}
	
}
