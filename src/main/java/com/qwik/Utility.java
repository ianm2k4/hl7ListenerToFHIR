package com.qwik;

import java.text.SimpleDateFormat;

public class Utility {
    public static java.util.Date getDT(String st){
        java.util.Date d=null;
        try {
            SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMddHHmm");
            d = fmt.parse(st);
            
            
        } catch (Exception ex){
        
        }
        return d;
        }
      
      public static String getEVN2(String msg){
        String op = null;
        String[] message = msg.split("\\r");
                    for (int i=0; i<message.length;i++){
                        if(message[i].startsWith("EVN")){
                           String EVN = message[i];
                           //System.out.println(PID); 
                           String[] comp = EVN.split("\\|");
                           //System.out.println(comp[2]); 
                           //String[] ele = comp[3].split("\\^");
                           op=comp[2]; 
                        }
                    }
        return  op;
        }
        
      public static String getEVN6(String msg){
        String op = null;
        String[] message = msg.split("\\r");
                    for (int i=0; i<message.length;i++){
                        if(message[i].startsWith("EVN")){
                           String EVN = message[i];
                           //System.out.println(PID); 
                           String[] comp = EVN.split("\\|");
                           //System.out.println(comp[2]); 
                           //String[] ele = comp[3].split("\\^");
                           op=comp[6]; 
                        }
                    }
        return  op;
        }
        
        
      public static String getPID31(String msg){
        String op = null;
        String[] message = msg.split("\\r");
                    for (int i=0; i<message.length;i++){
                        if(message[i].startsWith("PID")){
                           String PID = message[i];
                           //System.out.println(PID); 
                           String[] comp = PID.split("\\|");
                           //System.out.println(comp[3]); 
                           String[] ele = comp[3].split("\\^");
                           op=ele[0]; 
                        }
                    }
        return  op;
    }
    
}
