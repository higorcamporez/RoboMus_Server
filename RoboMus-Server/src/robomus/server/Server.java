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
import java.util.Timer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**%
 *
 * @author Higor
 */
public class Server {
    private int port;
    private List<OSCMessage> instrumentsHandshakes;
    private List<Instrument> instruments;
    private int sychInterval; //interval in milisecund
    
    public Server(int port) {
        this.sychInterval = 5000; 
        this.port = port;
        this.instrumentsHandshakes = new ArrayList<OSCMessage>();
        this.instruments = new ArrayList<>();
        this.instruments.add(new Instrument("laplap", 1, "/laplap",
                1234, 1234, "seila", "nao sei","192.168.14.241", 100));
       
        Timer timer = new Timer();
        //t.schedule(new SychTime(instruments) , this.sychInterval);
        //timer.scheduleAtFixedRate(new SychTime(instruments), this.sychInterval, this.sychInterval);
        System.out.println("server started");
    }
    
    
    
    public void TestMsg(){
     
        OSCPortOut sender = null;
        try {
            sender = new OSCPortOut(InetAddress.getByName("192.168.1.200") , 1234);
        } catch (UnknownHostException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SocketException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
         List args = new ArrayList<>();
        
        args.add(System.currentTimeMillis() + 1000);
        args.add(2500);
        args.add(1);
        
         OSCMessage msg = new OSCMessage("/laplap/playString" ,args);
        
             
        try {
            sender.send(msg);
            System.out.println("msg sent");
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
                       
    }   
            
            
    public void playString(int stringNumber){
        OSCPortOut sender = null;
        try {
            sender = new OSCPortOut(InetAddress.getByName("192.168.0.114") , 1234);
        } catch (UnknownHostException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SocketException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
         List args = new ArrayList<>();
        
        args.add(System.currentTimeMillis() + 100);
        args.add(2500);
        args.add(stringNumber);
        
         OSCMessage msg = new OSCMessage("/laplap/playString" ,args);
         OSCMessage msg2 = new OSCMessage("/laplap/playString" );           
         
         msg2.addArgument(System.currentTimeMillis() + 200);
         msg2.addArgument(250);
         msg2.addArgument(stringNumber+1);

        try {
            sender.send(msg);
      
            sender.send(msg2);
            System.out.println("msg sent");
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public void receiveHandshake(OSCMessage message){
        this.instrumentsHandshakes.add(message);
    }
    public String[] divideAddress(String address){
        String aux = address;
        if (aux.startsWith("/")) {
            aux = address.substring(1);
        }

        String[] split = aux.split("/", -1);
        
        return split;
        
    }
    public void receiveMessages(){
        
        OSCPortIn receiver;
 
        try {
            receiver = new OSCPortIn(12345);
            OSCListener listener = new OSCListener() {
                @Override
                public void acceptMessage(java.util.Date time, OSCMessage message) {
                    /*System.out.println("Message received! end: " + message.getAddress());
                     List l = message.getArguments();
                     System.out.println("time "+time);
                     //put in a buffer
                     System.out.println("tam= "+l.size());
                     for (Object l1 : l) {
                     System.out.println("ob = "+l1);
                     }*/
                    String[] dividedAdress = divideAddress(message.getAddress());
                    if (dividedAdress.length >= 2) {
                        switch (dividedAdress[1]) {
                            case "handshake":
                                receiveHandshake(message);
                                break;

                        }
                    }
                }
            };

            receiver.addListener("/server/*", listener);
            receiver.startListening();
            
        } catch (SocketException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
            
                
    }
    public void smartphoneBlink(){
        OSCPortOut sender = null;
        try {
            sender = new OSCPortOut(InetAddress.getByName("192.168.14.241") , 1234);
        } catch (UnknownHostException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SocketException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
         List args = new ArrayList<>();
        
        args.add(System.currentTimeMillis() + 2000);
        
         OSCMessage msg = new OSCMessage("/laplap/blink" ,args);
                   
         

        try {
            sender.send(msg);
      
            System.out.println("msg sent");
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }    
    public static void main(String[] args) throws SocketException {
        Server server = new Server(12345);
        Scanner ler = new Scanner(System.in);
        try {
            /*while(true){
            System.out.println("Escolha a corda: ");
            ler.nextInt();
            server.smartphoneBlink();
            }*/
            Thread.sleep(2000);
        } catch (InterruptedException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
        server.smartphoneBlink();
        
    } 
    
}
