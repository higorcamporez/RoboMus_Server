/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package robomus.server;

import com.illposed.osc.OSCListener;
import com.illposed.osc.OSCMessage;
import com.illposed.osc.OSCPortIn;
import java.net.SocketException;
import java.util.List;

/**
 *
 * @author Higor
 */
public class Server {
    private static int port;
    
    
    public static void main(String[] args) throws SocketException {
        OSCPortIn receiver = new OSCPortIn(12345);
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
	receiver.startListening();    
    }
    
}
