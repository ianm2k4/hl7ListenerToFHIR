package com.qwik;

import java.util.Map;
import ca.uhn.hl7v2.protocol.ReceivingApplicationExceptionHandler;

public final class MyExceptionHandler
        implements ReceivingApplicationExceptionHandler {
    public String processException(String theIncomingMessage, Map<String, Object> theIncomingMetadata,
            String theOutgoingMessage, Exception theE) {
        return theOutgoingMessage;
    }
}