package com.qwik;

import org.slf4j.LoggerFactory;

import ca.uhn.hl7v2.DefaultHapiContext;
import ca.uhn.hl7v2.HapiContext;
import ca.uhn.hl7v2.app.Connection;
import ca.uhn.hl7v2.app.ConnectionListener;
import ca.uhn.hl7v2.app.HL7Service;
import ca.uhn.hl7v2.protocol.ReceivingApplication;
import ca.uhn.hl7v2.protocol.ReceivingApplicationExceptionHandler;

public class HL7ListenerToFHIR {

    // private static ConnectionFactory connectionFactory;

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(HL7ListenerToFHIR.class);

    // private static Queue ADTQueue;
    private static Connection con;
    // public static javax.jms.MessageProducer ADTmessageProducer;
    // public static javax.jms.Message ADTtextMessage;
    // public static javax.jms.Connection ADTconnection;
    // public static javax.jms.Session ADTsession;
    // public static Logger logger;

    public static void main(String[] args) {
        /*
         * System.out.println(args.length);
         * if(args.length != 5 ){
         * System.out.println("Incorrect Arguments - see config document");
         * System.exit(0);
         * }
         * 
         * System.out.println("URL:     "+args[0]);
         * System.out.println("Queue:   "+args[1]);
         * System.out.println("userName:"+args[2]);
         * System.out.println("passwd:  "+args[3]);
         * System.out.println("Port:    "+args[4]);
         */
        // String url = "mq://localhost:7676";
        // String quName = "eMeds";
        // for live
        // String userName = "openesb";
        // String passwd = "CH465utr";
        // for test
        // String userName = "admin ";
        // String passwd = "admin";
        int port = 30002;
        // logger = LogManager.getLogger(Hl7ListenerToFHIR.class.getName());
        // BasicConfigurator.configure();
        // Logger.getRootLogger().setLevel(Level.INFO);
        try {

            // convert to database connection
            // Class.forName("com.mariadb.jdbc.Driver");
            // con =
            // DriverManager.getConnection("jdbc:mariadb://localhost:3306/test","user","password");

        } catch (Exception je) {
            // logger.error("Exception during database initialise: " + je);
        }

        boolean useTls = false;

        HapiContext context = new DefaultHapiContext();
        context.getParserConfiguration().setValidating(false);
        // context.setValidationContext(ValidationContextFactory.noValidation());
        // context.setValidationContext(ValidationContextFactory.noValidation());

        HL7Service server = context.newServer(port, useTls);

        ReceivingApplication ADThandler = new ADTReceiverApplication();
        /*
         * server.registerApplication("ADT", "A01", ADThandler);
         * server.registerApplication("ADT", "A02", ADThandler);
         * server.registerApplication("ADT", "A03", ADThandler);
         * server.registerApplication("ADT", "A05", ADThandler);
         * server.registerApplication("ADT", "A08", ADThandler);
         * server.registerApplication("ADT", "A11", ADThandler);
         * server.registerApplication("ADT", "A12", ADThandler);
         * server.registerApplication("ADT", "A13", ADThandler);
         * server.registerApplication("ADT", "A21", ADThandler);
         * server.registerApplication("ADT", "A22", ADThandler);
         * server.registerApplication("ADT", "A28", ADThandler);
         * server.registerApplication("ADT", "A31", ADThandler);
         * server.registerApplication("ADT", "A38", ADThandler);
         * server.registerApplication("ADT", "A40", ADThandler);
         * server.registerApplication("ADT", "A52", ADThandler);
         * server.registerApplication("ADT", "A53", ADThandler);
         * server.registerApplication("REF", "I12", ADThandler);
         * server.registerApplication("REF", "I13", ADThandler);
         * server.registerApplication("REF", "I14", ADThandler);
         */
        // ****************************
        // * Accept all message types *
        // ****************************
        server.registerApplication("*", "*", ADThandler);
        server.registerConnectionListener((ConnectionListener) new MyConnectionListener());
        server.setExceptionHandler((ReceivingApplicationExceptionHandler) new MyExceptionHandler());
        try {
            server.startAndWait();
        } catch (Exception e) {
            System.out.println("Exception: " + e);
        }
    }

}