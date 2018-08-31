/*
 * @(#) Main.java
 *
 * JOTM: Java Open Transaction Manager
 *
 *
 * This module was originally developed by
 *
 *  - Bull S.A. as part of the JOnAS application server code released in
 *    July 1999 (www.bull.com)
 *
 * --------------------------------------------------------------------------
 *  The original code and portions created by Bull SA are
 *  Copyright (c) 1999 BULL SA
 *  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * -Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * -Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * --------------------------------------------------------------------------
 * Contributor(s): 
 *          Guillaume Riviere
 *
 * 23/09/04 Andy Glick 
 *          Updated parameter handling for commons-cli 1.0
 *
 * -------------------------------------------------------------------------
 * $Id: Main.java 1156 2011-06-08 13:58:41Z benoitf $
 * --------------------------------------------------------------------------
 */
package org.objectweb.jotm;

import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

import org.objectweb.jotm.Jotm;

/**
 * This class is used to start JOTM as a standalone transaction manager.
 *
 * @author  Christophe Ney -  cney@batisseurs.com
 * Created on Feb 26, 2002
 * */
public class Main extends Thread {

    private static final String progname = "JOTM";

    // command line options
    private static Options cmdLineOptions = null;

    // verbose mode
    static boolean verbose = false;
    // debug mode
    static boolean debug = false;
    // default transaction timeout
    static int timeout = 0;
    // is transaction factory remote
    static boolean remote = false;
    // user transaction URL
    static String userTransactionName = null;
    // transaction manager URL
    static String transactionManagerName = null;
    // transaction synchronization registry URL
    static String transactionSynchronizationRegistryName = null;
    // naming context
    static Context ictx = null;
    // logWriter
    static PrintWriter logWriter = new PrintWriter(System.out, true);

    // instance of JOTM
    private static Jotm jotm;

    /**
     *  used as shutdown hook
     */
    public void run() {
        System.out.print("shutting down...");
        try {
            ictx.unbind(userTransactionName);
        } catch (Exception e) {
            // ignore
        }
        try {
            ictx.unbind(transactionManagerName);
        } catch (Exception e) {
            // ignore
        }
        // stop jotm
        try {
            jotm.stop();
        } catch (Exception e) {
            // ignore
        }
        logWriter.close();
    }

    public static void printHelp(Options cmdLineOptions) {
        HelpFormatter hf = new HelpFormatter();
        hf.printHelp("JOTM [options...]", cmdLineOptions);
    }

    private static void verbose(String msg) {
        if (verbose) {
            System.out.println(msg);
        }
    }

    private static void checkRegistryMessage() {
        System.err.println("Current JNDI settings are:");
        boolean found = false;
        try {
            Hashtable env = ictx.getEnvironment();
            for (Enumeration e = env.keys(); e.hasMoreElements();) {
                String key = (String) e.nextElement();
                System.err.println("- " + key + "=" + env.get(key));
                found = true;
            }
        } catch (NamingException e) {
            // ignore
        }
        if (!found) {
            System.err.println("JNDI properties are not set!");
        } else {
            System.err.println("Check that registry is running on a port matching JNDI properties");
        }
    }


    /**
     * Used to start JOTM from a command line interface.
     *
     * @param args JOTM arguments
     */
    public static void main(String[] args) {

        cmdLineOptions = new Options();
        // option parameters are: short description (String), long description (String), has arguments (boolean), 
        //                        description (String)
        cmdLineOptions.addOption("d", "debug", false, "debug mode");
        cmdLineOptions.addOption("v", "verbose", false, "verbose mode");
        cmdLineOptions.addOption("h", "help", false, "print this message and exit");
        cmdLineOptions.addOption("m", "transaction_manager", true, "JNDI URL of the TransactionManager");
        cmdLineOptions.addOption("r", "remote", false, "lookup remote transaction factory");
        cmdLineOptions.addOption("t", "timeout", true, "default transaction timeout (in seconds)");
        cmdLineOptions.addOption("u", "user_transaction", true, "JNDI URL of the UserTransaction");
        cmdLineOptions.addOption("s", "transaction_synchronizationregistry", true, "JNDI URL of the TransactionSynchronizationRegistry");

        CommandLine cmd = null;

        CommandLineParser parser = new PosixParser();

        try {
            cmd = parser.parse(cmdLineOptions, args, true);
        } catch (ParseException e) {
            System.err.println("\n" + e.getMessage());
            printHelp(cmdLineOptions);
            System.err.println();
            System.exit(1);
        }

        debug = cmd.hasOption('d');
        remote = cmd.hasOption('r');
        verbose = cmd.hasOption('v');
        if (cmd.hasOption('h')) {
            printHelp(cmdLineOptions);
            System.exit(1);
        }
        if (cmd.hasOption('m')) {
            transactionManagerName = cmd.getOptionValue('m');
        }
        if (cmd.hasOption('t')) {
            try {
                timeout = Integer.parseInt(cmd.getOptionValue('t'));
            } catch (NumberFormatException e) {
                System.err.println("\ntimeout is not a number");
                printHelp(cmdLineOptions);
                System.err.println();
                System.exit(1);
            }
        }
        if (cmd.hasOption('u')) {
            userTransactionName = cmd.getOptionValue('u');
        }
        if (cmd.hasOption('s')) {
            transactionSynchronizationRegistryName = cmd.getOptionValue('s');
        }

        verbose("UserTransaction Name =" + userTransactionName);
        verbose("TransactionManager Name =" + transactionManagerName);
        verbose("TransactionSynchronizationRegistry Name =" + transactionSynchronizationRegistryName);
        verbose("Transaction factory =" + (remote ? "remote" : "local"));
        verbose("Default transaction timeout =" + timeout);

        TraceTimer.setLogWriter(logWriter);
        TraceTimer.setVerbose(verbose);
        TraceTimer.setDebug(debug);

        try {
            // create an instance of JOTM
            jotm = new Jotm(!remote, true);
        } catch (NamingException e) {
            System.out.println("unable to start JOTM!: "+ e.getMessage());
            System.exit(1);
        }

        // trap program termination
        Runtime.getRuntime().addShutdownHook(new Main());

        try {
            ictx = new InitialContext();
        } catch (NamingException e) {
            System.err.println("No initial context: " + e.getExplanation());
            e.printStackTrace();
            System.exit(1);
        }

        try {
            if (userTransactionName != null) {
                ictx.rebind(userTransactionName, jotm.getUserTransaction());
                System.out.println("UserTransaction object bound in JNDI with name " + userTransactionName);
            }
        } catch (NamingException e) {
            System.err.println("UserTransaction rebind failed :" + e.getExplanation());
            e.printStackTrace();
            checkRegistryMessage();
            System.exit(1);
        }
        try {
            if (transactionManagerName != null) {
                ictx.rebind(transactionManagerName, jotm.getTransactionManager());
                System.out.println("TransactionManager object bound in JNDI with name " + transactionManagerName);
            }
        } catch (NamingException e) {
            System.err.println("TransactionManager rebind failed :" + e.getExplanation());
            e.printStackTrace();
            checkRegistryMessage();
            System.exit(1);
        }
        try {
            if (transactionSynchronizationRegistryName != null) {
                ictx.rebind(transactionSynchronizationRegistryName, jotm.getTransactionSynchronizationRegistry());
                System.out.println("TransactionSynchronizationRegistry object bound in JNDI with name " + transactionSynchronizationRegistryName);
            }
        } catch (NamingException e) {
            System.err.println("TransactionSynchronizationRegistry rebind failed :" + e.getExplanation());
            e.printStackTrace();
            checkRegistryMessage();
            System.exit(1);
        }

        System.out.print(progname + " is running...");
    }
}