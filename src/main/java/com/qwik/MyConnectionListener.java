package com.qwik;


import ca.uhn.hl7v2.app.ConnectionListener;

public final class MyConnectionListener
    implements ConnectionListener {
  public void connectionReceived(ca.uhn.hl7v2.app.Connection c) {
    System.out.println("New connection received: " + c.getRemoteAddress().toString());
    /*
     * here we can check the inbound ip addresses and reject it, could have a
     * configured list of addresses we'd listent to
     * closing the connection doesn't stop the listener, it will just wait for
     * another inbound connection.
     */
    // if(c.getRemoteAddress().toString().contains("127.0.0.1")){
    // } else {
    // System.out.println("Closing illegal address");
    // c.close();
    // }
  }

  public void connectionDiscarded(ca.uhn.hl7v2.app.Connection c) {
    System.out.println("Lost connection from: " + c.getRemoteAddress().toString());
  }
}