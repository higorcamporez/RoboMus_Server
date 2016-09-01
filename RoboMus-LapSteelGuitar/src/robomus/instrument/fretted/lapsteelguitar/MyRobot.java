/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package robomus.instrument.fretted.lapsteelguitar;

import com.illposed.osc.OSCMessage;
import com.illposed.osc.OSCPortOut;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import robomus.instrument.fretted.FrettedInstrument;
import robomus.instrument.fretted.InstumentString;


/**
 *
 * @author Higor ghghghghg
 */
public class MyRobot extends FrettedInstrument{

    public MyRobot(int nFrets, InstumentString[] strings, String name,
            int polyphony, String OscAddress, InetAddress severAddress,
            int sendPort, int receivePort, String typeFamily) {
        super(nFrets, strings, name, polyphony, OscAddress, severAddress,
                sendPort, receivePort, typeFamily);
    }
    
    
    public void handshake(){
        
        
        OSCPortOut sender = new OSCPortOut(this.address , this.portaEnvio);
	
        List args = new ArrayList<>();
        
        Object object1 = this.name;
        Object object2 = this.polyphony;
        Object object3 = this.typeFamily;        
        Object object4 = this.specificProtocol;
        
        Object object = this.nFrets;
        Object object = this.strings;
        
	args.add(object1);
        args.add(object2);
        args.add(object3);
        args.add(object4);
        args.add(object5);
      
	OSCMessage msg = new OSCMessage("/handshake", args);
        
	 try{
             
               sender.send(msg);
               
	 } catch (Exception e) {
		// showError("Couldn't send");
	 }
    
	
        
    }
        
    
}
