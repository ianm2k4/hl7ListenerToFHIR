package com.qwik;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.logging.Level;

import org.hl7.fhir.r4.model.Address;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Encounter;
import org.hl7.fhir.r4.model.Enumerations;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.RequestGroup;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.hl7v2.AcknowledgmentCode;
import ca.uhn.hl7v2.DefaultHapiContext;
import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.v23.message.ORM_O01;
import ca.uhn.hl7v2.parser.PipeParser;
import ca.uhn.hl7v2.protocol.ReceivingApplication;
import ca.uhn.hl7v2.protocol.ReceivingApplicationException;
import ca.uhn.hl7v2.util.Terser;
import ca.uhn.hl7v2.validation.impl.ValidationContextFactory;


public final class ADTReceiverApplication implements ReceivingApplication {

  public boolean canProcess(Message theIn) {
    return true;
  }

  @Override
  public Message processMessage(Message theMessage, Map theMetadata)
      throws ReceivingApplicationException, HL7Exception {

    boolean debug = false;
    Message ack = null;
    Message nack = null;
    String identifier = null;
    String idSystem = "http://acme.org/mrns";
    // Setup a pipe parser and a simple terser to dig into the message
    // other way of reading the msg content is to
    String encodedMessage = new DefaultHapiContext().getPipeParser().encode(theMessage);
    // String encodedMessage = new
    // DefaultHapiContext().getXMLParser().encode(theMessage);
    // logger.info(encodedMessage);
    // new HL7ListenerforeMeds().ADTproduceMessages(theMessage.toString());
    // logger.info("Starting pipe parser");
    PipeParser ourPipeParser = new PipeParser();
    ourPipeParser.setValidationContext(ValidationContextFactory.noValidation());
    // logger.info("Starting parse");
    Message hl7msg = ourPipeParser.parse(encodedMessage);
    // create a terser object instance by wrapping it around the message object
    Terser hl7 = new Terser(hl7msg);
    // logger.info("use terser");
    // String msgType = hl7.get("/MSH-9-2");
    String msg = encodedMessage.replace("&amp;", "&");

    /* This is where we process the message and do stuff */
    /////////////////////////////////////////////////////////////////////////////////////////////////////////// FHIR
    /////////////////////////////////////////////////////////////////////////////////////////////////////////// Stuff
    /////////////////////////////////////////////////////////////////////////////////////////////////////////// grafted
    /////////////////////////////////////////////////////////////////////////////////////////////////////////// in
    // Ok create/Cast hl7msg to ORM_O01
    ORM_O01 orm = (ORM_O01) hl7msg;
    identifier = orm.getPATIENT().getPID().getPid3_PatientIDInternalID(0).getCx1_ID().toString();
    // orm.getPATIENT().getPID().getPatientName(0).getFamilyName().toString();
    // orm.getPATIENT().getPID().getPatientName(0).getGivenName().toString();
    // orm.getPATIENT().getPID().getPatientAddress(0).getStreetAddress();
    // orm.getPATIENT().getPID().getSex().toString();
    String bdate = orm.getPATIENT().getPID().getDateOfBirth().toString();
    Date dob = null;
    try {
      dob = new SimpleDateFormat("yyyymmdd").parse(bdate);
    } catch (Exception ex) {
      System.out.println(ex);
    }
    RequestGroup req = new RequestGroup();

    // TODO code application logic here
    // Create a patient object
    Patient patient = new Patient();
    patient.addIdentifier()
        .setSystem(idSystem)
        .setValue(orm.getPATIENT().getPID().getPid3_PatientIDInternalID(0).getCx1_ID().toString());
    patient.addName()
        .setFamily(orm.getPATIENT().getPID().getPatientName(0).getFamilyName().toString())
        .addGiven(orm.getPATIENT().getPID().getPatientName(0).getGivenName().toString());

    if (orm.getPATIENT().getPID().getSex().toString().matches("M|m|0")) {
      patient.setGender(Enumerations.AdministrativeGender.MALE);
    } else if (orm.getPATIENT().getPID().getSex().toString().matches("F|f|1")) {
      patient.setGender(Enumerations.AdministrativeGender.FEMALE);
    }

    patient.setBirthDate(dob);

    Address patient_address = new Address();
    patient_address.addLine(orm.getPATIENT().getPID().getPatientAddress(0).getStreetAddress().toString());
    patient_address.setDistrict(orm.getPATIENT().getPID().getPatientAddress(0).getStateOrProvince().toString());
    patient_address.setCity(orm.getPATIENT().getPID().getPatientAddress(0).getCity().toString());
    patient_address.setPostalCode(orm.getPATIENT().getPID().getPatientAddress(0).getXad5_ZipOrPostalCode().toString());
    patient.addAddress(patient_address);
    // Give the patient a temporary UUID so that other resources in
    // the transaction can refer to it
    patient.setId(IdType.newRandomUuid());
    Encounter encounter = new Encounter();

    /*
     * // Create an observation object
     * Observation observation = new Observation();
     * 
     * observation.setStatus(Observation.ObservationStatus.FINAL);
     * observation
     * .getCode()
     * .addCoding()
     * .setSystem("http://loinc.org")
     * .setCode("789-8")
     * .setDisplay("Erythrocytes [#/volume] in Blood by Automated count");
     * observation.setValue(
     * new Quantity()
     * .setValue(4.12)
     * .setUnit("10 trillion/L")
     * .setSystem("http://unitsofmeasure.org")
     * .setCode("10*12/L"));
     * 
     * // The observation refers to the patient using the ID, which is already
     * // set to a temporary UUID
     * //observation.setSubject(new Reference(patient.getIdElement().getValue())
     * {});
     */
    // Create a bundle that will be used as a transaction
    Bundle bundle = new Bundle();

    bundle.setType(Bundle.BundleType.TRANSACTION);

    // Add the patient as an entry. This entry is a POST with an
    // If-None-Exist header (conditional create) meaning that it
    // will only be created if there isn't already a Patient with
    // the identifier 12345
    bundle.addEntry()
        .setFullUrl(patient.getIdElement().getValue())
        .setResource(patient)
        .getRequest()
        .setUrl("Patient")
        .setIfNoneMatch(idSystem + "|" + identifier)
        .setMethod(Bundle.HTTPVerb.POST);

    // Add the observation. This entry is a POST with no header
    // (normal create) meaning that it will be created even if
    // a similar resource already exists.

    // bundle.addEntry()
    // .setResource(observation)
    // .getRequest()
    // .setUrl("Observation")
    // .setMethod(Bundle.HTTPVerb.POST);

    // Log the request
    FhirContext ctx = FhirContext.forR4();

    // this just prints the patient resource to post to a fhir server uncomment the
    // three following //- lines
    System.out.println(ctx.newJsonParser().setPrettyPrint(true).encodeResourceToString(patient));

    // Create a client and post the transaction to the server
    IGenericClient client = ctx.newRestfulGenericClient("http://192.168.80.10:8080/my-fhir-test/fhir");
    Bundle resp = client.transaction().withBundle(bundle).execute();

    // Log the response
    System.out.println(ctx.newJsonParser().setPrettyPrint(true).encodeResourceToString(resp));
    // logger.info(ctx.newJsonParser().setPrettyPrint(true).encodeResourceToString(resp));

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* just dump the msg */
    // System.out.println(msg);

    try {

      try {
        // to create a nack, create an exception text string
        HL7Exception hl7e = new HL7Exception("Something Broke");
        ack = theMessage.generateACK();
        // and generate the nack, and return it instead of the ack
        nack = theMessage.generateACK(AcknowledgmentCode.AE, hl7e);

      } catch (IOException ex) {
        java.util.logging.Logger.getLogger(HL7ListenerToFHIR.class.getName()).log(Level.SEVERE, null, ex);
      }

      return ack;
    } catch (Exception ex) {
      return nack;
    }
  }




}