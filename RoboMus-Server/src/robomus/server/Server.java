/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package robomus.server;

import com.illposed.osc.OSCListener;
import com.illposed.osc.OSCMessage;
import com.illposed.osc.OSCPort;
import com.illposed.osc.OSCPortIn;
import com.illposed.osc.OSCPortOut;
import com.sun.corba.se.pept.transport.ListenerThread;
import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Higor
 */
public class Server {
    private static int port;
    
    public void TestMsg(){
     
        OSCPortOut sender = null;
        try {
            sender = new OSCPortOut(InetAddress.getByName("192.168.1.232") , 1234);
        } catch (UnknownHostException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SocketException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
         List args = new ArrayList<>();
        
        args.add(1);
        args.add("1");
        
         OSCMessage msg = new OSCMessage("/laplap" ,args);
        
             
        try {
            sender.send(msg);
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
                       
    }   
            
            
    
    public static void main(String[] args) throws SocketException {
        Server server = new Server();
        server.TestMsg();
        
       /* OSCPortIn receiver = new OSCPortIn(12345);
	OSCListener listener = new OSCListener() {
                @Override
		public void acceptMessage(java.util.Date time, OSCMessage message) {
			System.out.println("Message received! end: " + message.getAddress());
                        List l = message.getArguments();
                        //put in a buffer
                        System.out.println("tam= "+l.size());
                        for (Object l1 : l) {
                            System.out.println("ob = "+l1);
                    }
		}
	};
        
	receiver.addListener("/handshake", listener);
	receiver.startListening();    */
    }
    
}
