/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package robomus.instrument.fretted.lapsteelguitar;

import com.illposed.osc.OSCMessage;
import com.illposed.osc.OSCPortOut;
import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import robomus.instrument.fretted.FrettedInstrument;
import robomus.instrument.fretted.InstrumentString;


/**
 *
 * @author Higor ghghghghg
 */
public class MyRobot extends FrettedInstrument{
    
        
    public MyRobot(int nFrets, ArrayList<InstrumentString> strings, String name,
            int polyphony, String OscAddress, InetAddress severAddress,
            int sendPort, int receivePort, String typeFamily, String specificProtocol) {
        super(nFrets, strings, name, polyphony, OscAddress, severAddress,
                sendPort, receivePort, typeFamily, specificProtocol);
    }
   
    
    public void handshake(){
     
        
        OSCPortOut sender = null;
        try {
            sender = new OSCPortOut(this.severAddress , this.sendPort);
        } catch (SocketException ex) {
            Logger.getLogger(MyRobot.class.getName()).log(Level.SEVERE, null, ex);
        }
	
        List args = new ArrayList<>();
        
        //instrument attributes
        args.add(this.name);
        args.add(this.polyphony);
        args.add(this.typeFamily);
        args.add(this.specificProtocol);
  
        //amount of attributes
        args.add(2);
        //fretted instrument attributs
        args.add(this.nFrets);
        args.add(convertInstrumentoStringToString());

      
	OSCMessage msg = new OSCMessage("/handshake", args);
        
             
        try {
            sender.send(msg);
        } catch (IOException ex) {
            Logger.getLogger(MyRobot.class.getName()).log(Level.SEVERE, null, ex);
        }
               
	 
	
        
    }
    public static void main(String[] args) {
        ArrayList<InstrumentString> l = new ArrayList();
        l.add(new InstrumentString(0, "A"));
        l.add(new InstrumentString(0, "B"));
        String specificP = "</slide;posicaoInicial_int><>";
        
        try {
            MyRobot myRobot = new MyRobot(12, l, "laplap", 6, "/laplap", InetAddress.getByName("10.0.0.128"),
                    12345, 1234, "Fretted", specificP);
            myRobot.handshake();
        } catch (UnknownHostException ex) {
            Logger.getLogger(MyRobot.class.getName()).log(Level.SEVERE, null, ex);
        }
                
    }
        
    
}
