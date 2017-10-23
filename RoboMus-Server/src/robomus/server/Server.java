/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package robomus.server;

import com.illposed.osc.OSCBundle;
import com.illposed.osc.OSCListener;
import com.illposed.osc.OSCMessage;
import com.illposed.osc.OSCPortIn;
import com.illposed.osc.OSCPortOut;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**%
 *
 * @author Higor
 */
public class Server {
    private int port;
    private List<Instrument> instruments;
    private List<Client> clients;
    private int sychInterval; //interval in milisecund
    FileWriter arq, arq2, arq3;
    PrintWriter laplapArq, laplap2Arq, infoSaidaArq;
    public long id;
    private SychTime syncTime;
    private String oscAdress;
    private String name;
    private Date date;
    private SimpleDateFormat dateFormat;
    private int lastIdReceived;
    
    public Server(int port) {
        this.sychInterval = 10000; 
        this.port = port;
        this.oscAdress = "/server";
        this.name = "server";
        this.clients = new ArrayList<>();
        this.instruments = new ArrayList<>();
        this.lastIdReceived = -1;       
        System.out.println("server started");
        this.id = 0;
        //iniciando os arquivos 
      
        try {
            arq = new FileWriter("laplap.txt");
            laplapArq = new PrintWriter(arq);
            arq2 = new FileWriter("laplap2.txt");
            laplap2Arq = new PrintWriter(arq2);
            arq3 = new FileWriter("saida.txt");
            infoSaidaArq = new PrintWriter(arq3);
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        //date
        this.date = new Date();
        this.dateFormat = new SimpleDateFormat("HH:mm:ss.SSS");
        
        
        
    }

    public List<Instrument> getInstruments() {
        return instruments;
    }

    public void setInstruments(List<Instrument> instruments) {
        this.instruments = instruments;
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
    
    public void receiveHandshakeInstrument(OSCMessage message){
        Instrument instrument = new Instrument();
        List arguments = message.getArguments();
        
        instrument.setName((String)arguments.get(0));
        instrument.setOscAddress((String)arguments.get(1));
        instrument.setIp((String)arguments.get(2));
        instrument.setReceivePort((int)arguments.get(3));
        instrument.setPolyphony((int)arguments.get(4));
        instrument.setTypeFamily((String)arguments.get(5));
        instrument.setSpecificProtocol((String)arguments.get(6));
        
        if(!this.instruments.contains(instrument)){
            this.instruments.add(instrument);
            sendInstrument(instrument);
        }
        
        
        OSCMessage msg = new OSCMessage(instrument.getOscAddress()+"/handshake");
        //msg.addArgument(0);
        //msg.addArgument(1254);
        msg.addArgument(this.name);
        msg.addArgument(this.oscAdress);
        try {
            msg.addArgument(InetAddress.getLocalHost().getHostAddress());
        } catch (UnknownHostException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
        msg.addArgument(this.port);
        OSCPortOut sender = null;
        try {
            sender = new OSCPortOut(InetAddress.getByName(instrument.getIp()), instrument.getReceivePort());
        } catch (SocketException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnknownHostException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            sender.send(msg);
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public void receiveHandshakeClient(OSCMessage message){
        Client client = new Client();
        List arguments = message.getArguments();
        
        client.setName(arguments.get(0).toString());
        client.setOscAdress(arguments.get(1).toString());
        client.setIpAdress(arguments.get(2).toString());
        client.setPort(Integer.parseInt(arguments.get(3).toString()));
        
        if(!this.clients.contains(client)){
            this.clients.add(client);
        } 
        
        //send server informations
        OSCMessage msg = new OSCMessage(client.getOscAdress()+"/handshake");
        msg.addArgument(this.name);
        msg.addArgument(this.oscAdress);
        try {
            msg.addArgument(InetAddress.getLocalHost().getHostAddress());
        } catch (UnknownHostException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
        msg.addArgument(this.port);
        OSCPortOut sender = null;
        try {
            sender = new OSCPortOut(InetAddress.getByName(client.getIpAdress()), client.getPort());
        } catch (SocketException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnknownHostException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            sender.send(msg);
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
     
    public void logBlink(OSCMessage oscMessage){    
        String[] divideAddress = divideAddress(oscMessage.getAddress());
        //System.out.println("ad="+divideAddress[2]);
        if(divideAddress[2].equals("laplap")){
            laplapArq.printf("%d %d%n", System.nanoTime(),
                    oscMessage.getArguments().get(0) );
        }else if(divideAddress[2].equals("laplap2")){
            
            laplap2Arq.printf("%l %l%n", System.nanoTime(),
                    oscMessage.getArguments().get(0) );
        }
    }
    public String[] divideAddress(String address){
        String aux = address;
        if (aux.startsWith("/")) {
            aux = address.substring(1);
        }

        String[] split = aux.split("/", -1);
        
        return split;
        
    }
    public void forwardMessage(OSCMessage oscMessage){
        String[] dividedAdress = divideAddress(oscMessage.getAddress());
        String instrumentAdress = dividedAdress[1];
        Instrument instrument = null;
        for (Instrument inst : instruments) {
            System.out.println(inst.getOscAddress()+" "+instrumentAdress);
            if(inst.getOscAddress().equals("/"+instrumentAdress)){
                instrument = inst;
                break;
            }
        }
        if(instrument != null){
            System.out.println("encontrou");
            OSCPortOut sender = null;
            try {
                sender = new OSCPortOut(InetAddress.getByName(instrument.getIp()), instrument.getReceivePort());
                OSCMessage msg = new OSCMessage("/"+dividedAdress[1]+"/"+dividedAdress[2],oscMessage.getArguments());
                
                try {
                    sender.send(msg);
                    System.out.println("enviou "+instrument.getIp()+" "+instrument.getReceivePort());
                } catch (IOException ex) {
                    Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                }
            } catch (SocketException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            } catch (UnknownHostException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
    }
    public void sendInstruments(OSCMessage oscMessage){
        
        OSCPortOut sender = null;
        OSCMessage msg  = null;
            try {
                sender = new OSCPortOut(oscMessage.getIp(), 12345);
                if( !oscMessage.getArguments().isEmpty()){
                    System.out.println("end="+(String) oscMessage.getArguments().get(0)+"/instruments");
                    
                    OSCBundle oscBundle = new OSCBundle();
                    
                    for (Instrument inst : instruments) {
                        msg = new OSCMessage((String) oscMessage.getArguments().get(0)+"/instrument");
                        msg.addArgument(inst.getName());
                        msg.addArgument(inst.getPolyphony());
                        msg.addArgument(inst.getTypeFamily());
                        msg.addArgument(inst.getSpecificProtocol());
                        msg.addArgument(inst.getOscAddress());
                        
                        oscBundle.addPacket(msg);
                        
                    }
                    try {
                        sender.send(oscBundle);
                    } catch (IOException ex) {
                        Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                    } 
                     
                }else{
                    System.out.println("erro");
                    return;
                }

                
               
            } catch (SocketException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
   
        
    }
    public void sendInstrument(Instrument instrument){
        
        OSCPortOut sender = null;
        OSCMessage msg  = null;
           
                    
        for (Client client : this.clients) {

            try {
                sender = new OSCPortOut(InetAddress.getByName(
                                         client.getIpAdress()),
                                        client.getPort());

                msg = new OSCMessage(client.getOscAdress()+"/instrument");
                msg.addArgument(instrument.getName());
                msg.addArgument(instrument.getPolyphony());
                msg.addArgument(instrument.getTypeFamily());
                msg.addArgument(instrument.getSpecificProtocol());
                msg.addArgument(instrument.getOscAddress());


                sender.send(msg);
            } catch (UnknownHostException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SocketException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
         
    }
        
   
        
    
    public void disconnect(OSCMessage oscMessage){
        String[] dividedAdress = divideAddress(oscMessage.getAddress());
        if (dividedAdress.length == 3 && oscMessage.getArguments().size() >= 1) {
            //System.out.println("2: "+ dividedAdress[2]+ " a: "+oscMessage.getArguments().get(0).toString());
            if(dividedAdress[2].equals("client")){
                Client client = new Client();
                client.setOscAdress(oscMessage.getArguments().get(0).toString());
                if(this.clients.remove(client)){
                    System.out.println("Client '"+oscMessage.getArguments().get(0).toString()+"' disconnected");
                }
            }
            if(dividedAdress[2].equals("instrument")){ 
                Instrument instrument = new Instrument();
                instrument.setOscAddress(oscMessage.getArguments().get(0).toString());
                if ( this.instruments.remove(instrument) ){
                    System.out.println("Instrument '"+oscMessage.getArguments().get(0).toString()+"' disconnected");
                }
                sendInstrumentDisconnected(instrument);
            }
        }  
    }
    public void sendInstrumentDisconnected(Instrument instrument){
        OSCPortOut sender = null;

        for (Client client : this.clients) {
            OSCMessage msg = new OSCMessage(client.getOscAdress()+"/disconnect/instrument");
            msg.addArgument(instrument.getOscAddress());
            try {
                sender = new OSCPortOut(InetAddress.getByName(client.getIpAdress()), client.getPort());
                try {
                    sender.send(msg);
                } catch (IOException ex) {
                    Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                }
            } catch (SocketException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            } catch (UnknownHostException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
            
    }
    
    public void comparatorId(int id){
        if(this.lastIdReceived == -1){
            return;
        }else if(id != (this.lastIdReceived + 1) ){
            System.out.println("=========================> ERRO id="+id);
        }else{
            this.lastIdReceived = id;
        }
    }
    
    public void receiveMessages(){
        
        OSCPortIn receiver;
 
        try {
            receiver = new OSCPortIn(1234);
            OSCListener listener = new OSCListener() {
                @Override
                public void acceptMessage(java.util.Date time, OSCMessage message) {

                    String[] dividedAdress = divideAddress(message.getAddress());
                    if (dividedAdress.length >= 2) {
                        System.out.println(dividedAdress[1]);
 
                        switch (dividedAdress[1]) {
                            /*case "handshake":
                                receiveHandshake(message);
                                break;*/
                            case "blink":
                                System.out.println("recebeu blink resposta");
                                logBlink(message);
                                break;
                            case "action":
                                System.out.println("recebeu action resposta: "
                                        +message.getArguments().get(0)+" - "+ dateFormat.format(GregorianCalendar.getInstance().getTime()) );
                                
                                break;
                            case "getInstruments":
                                
                                sendInstruments(message);
                               
                                break;
                            case "disconnect":
                                
                                disconnect(message);
                               
                                break;
                            default:
                                System.out.println("recebeu msg default");
                                forwardMessage(message);
                                break;

                        }
                        
                    }
                }
            };
            
            OSCListener listenerHandshake = new OSCListener() {
                @Override
                public void acceptMessage(java.util.Date time, OSCMessage message) {
    
                    String[] dividedAdress = divideAddress(message.getAddress());
                    if (dividedAdress.length >= 2) {
 
                        switch (dividedAdress[1]) {
                            case "client":
                                System.out.println("/handshake/client");
                                receiveHandshakeClient(message);
                                break;
                            case "instrument":
                                System.out.println("/handshake/instrument");
                                receiveHandshakeInstrument(message);
                                break;
                            default:
                                System.out.println("handshake unknown");
                                break;

                        }
                    }    
                    

                }
            };

            receiver.addListener("/handshake/*", listenerHandshake);
            receiver.addListener("/server/*", listener);
            receiver.addListener("/server/*/*", listener);
            
            receiver.startListening();
            
            
        } catch (SocketException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
            
                
    }
    public void smartphoneBlink(long time, int cor){
        OSCPortOut sender = null;
        for (Instrument instrument : instruments) {
            try {
                sender = new OSCPortOut(InetAddress.getByName(instrument.getIp()), instrument.getReceivePort());
                OSCMessage msg = new OSCMessage(instrument.getOscAddress()+"/blink");
                msg.addArgument(time);
                msg.addArgument(cor);
                msg.addArgument(id);
                try {
                    sender.send(msg);
                } catch (IOException ex) {
                    Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                }
            } catch (SocketException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            } catch (UnknownHostException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        /*
        OSCPortOut sender2 = null;
        try {
            sender = new OSCPortOut(InetAddress.getByName("10.0.0.101") , 1234);
        } catch (UnknownHostException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SocketException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        try {
            sender2 = new OSCPortOut(InetAddress.getByName("10.0.0.102") , 1234);
            //System.out.println("sender2");
        } catch (UnknownHostException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SocketException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
        
         List args = new ArrayList<>();
        
        args.add(time);
        args.add(cor);
        args.add(id);
        long id1 = this.id;
        this.id++;
        
         OSCMessage msg = new OSCMessage("/laplap/blink" ,args);
         OSCMessage msg2 = new OSCMessage("/laplap2/blink" );          
         msg2.addArgument(time);
         msg2.addArgument(cor);
         msg2.addArgument(id);
         long id2 = this.id;
         this.id++;

        try {
            sender.send(msg);
            infoSaidaArq.printf("%d %d %d%n",System.nanoTime() ,time,id1 );
            infoSaidaArq.printf("%d %d %d%n",System.nanoTime() ,time,id2 );
            sender2.send(msg2);
            
            System.out.println("msg sent");
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }*/
    }
    public void fecharArq(){
        this.laplap2Arq.close();
        this.laplapArq.close();
        this.infoSaidaArq.close();
        System.out.println("fechou");
    }
    public void playNoteTest(int fretNumber, int stringNumber){
        OSCPortOut sender = null;
        for (Instrument instrument : instruments) {
            try {
                sender = new OSCPortOut(InetAddress.getByName(instrument.getIp()), instrument.getReceivePort());
                OSCMessage msg = new OSCMessage(instrument.getOscAddress()+"/playNoteTest");
                msg.addArgument((long)100);
                msg.addArgument(fretNumber);
                msg.addArgument(stringNumber);
                //msg.addArgument(id);
                try {
                    sender.send(msg);
                } catch (IOException ex) {
                    Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                }  
            } catch (SocketException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            } catch (UnknownHostException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public void printClients(){
        if(this.clients.size() == 0){
            System.out.println("No client");
        }else{
            for (Client client : this.clients) {
                System.out.println(client.toString());
            }
        }
        
    }
    
    public void printInstruments(){
        if(this.instruments.size() == 0){
            System.out.println("No instrument");
        }else{
            for (Instrument instrument : this.instruments) {
                System.out.println(instrument.toString());
            }
        }
    }
    
    
}
