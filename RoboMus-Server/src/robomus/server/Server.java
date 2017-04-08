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
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
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
    FileWriter arq, arq2, arq3;
    PrintWriter laplapArq, laplap2Arq, infoSaidaArq;
    public long id;
    private SychTime syncTime;
    public Server(int port) {
        this.sychInterval = 10000; 
        this.port = port;
        this.instrumentsHandshakes = new ArrayList<OSCMessage>();
        this.instruments = new ArrayList<>();
//        this.instruments.add(new Instrument("laplap", 1, "/laplap",
//                1234, 1234, "seila", "nao sei","10.0.0.101", 100));
//        this.instruments.add(new Instrument("laplap", 1, "/laplap2",
//                1234, 1234, "seila", "nao sei","10.0.0.102", 100));
               
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
        Instrument instrument = new Instrument();
        List arguments = message.getArguments();
        
        instrument.setName((String)arguments.get(0));
        instrument.setPolyphony((int)arguments.get(1));
        instrument.setTypeFamily((String)arguments.get(2));
        instrument.setSpecificProtocol((String)arguments.get(3));
        instrument.setOscAddress((String)arguments.get(4));
        instrument.setIp((String)arguments.get(5));
        instrument.setReceivePort((int)arguments.get(6));
        instrument.setSendPort((int)arguments.get(6));
        this.instruments.add(instrument);
        //this.instrumentsHandshakes.add(message);
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
                sender = new OSCPortOut(InetAddress.getByName(instrument.getIp()), instrument.getSendPort());
                OSCMessage msg = new OSCMessage("/"+dividedAdress[1]+"/"+dividedAdress[2],oscMessage.getArguments());
                
                try {
                    sender.send(msg);
                    System.out.println("enviou "+instrument.getIp()+" "+instrument.getSendPort());
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
    public void receiveMessages(){
        
        OSCPortIn receiver;
 
        try {
            receiver = new OSCPortIn(1234);
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
                    //System.out.println("chegou");
                    String[] dividedAdress = divideAddress(message.getAddress());
                    if (dividedAdress.length >= 2) {
                        switch (dividedAdress[1]) {
                            case "handshake":
                                System.out.println("handshake");
                                receiveHandshake(message);
                                break;
                            case "blink":
                                System.out.println("recebeu blink resposta");
                                logBlink(message);
                                break;
                            default:
                                System.out.println("recebeu msg default");
                                forwardMessage(message);
                                break;

                        }
                    }
                }
            };

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
                sender = new OSCPortOut(InetAddress.getByName(instrument.getIp()), instrument.getSendPort());
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
                sender = new OSCPortOut(InetAddress.getByName(instrument.getIp()), instrument.getSendPort());
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
    
    public static void main(String[] args) throws SocketException {
        Server server = new Server(12345);
        server.receiveMessages();
        while(true){
            System.out.println("Digite uma tecla pra enviar playNoteTest");
            Scanner ler = new Scanner(System.in);
            ler.nextLine();
            server.playNoteTest(4,4);
            try {
                Thread.sleep(4000);
            } catch (InterruptedException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
            server.playNoteTest(6,6);
            System.out.println("Enviou");
        }
        
        
         /*   Scanner ler = new Scanner(System.in);
            
            while(true){
                System.out.println("Escolha o tempo: ");
                long tempo = ler.nextInt();
                System.out.println("Escolha a cor: ");
                int cor = ler.nextInt();
                server.smartphoneBlink(tempo*1000000000,cor);
                server.smartphoneBlink(tempo*1000000000 + 2000000000,cor+1);
            }
        */
            //server.smartphoneBlink();
        
//        int bpm = 15;
//        int contMsg = 1; 
//        
//        
//        Scanner ler = new Scanner(System.in);
//            long timeSleep, timeToExecute;
//            while(true){
//                System.out.println("Precione qq tecla para enviar: ");
//                long tempo = ler.nextInt();
//                if (tempo == 0) {
//                    server.fecharArq();
//                }else{
//                    timeSleep = 250;
//                    for(int i=5; i <= contMsg+5; i++){                
//                        
//                        
//                        
//                        server.smartphoneBlink(timeSleep,i%4+1);
//                        if(i%4 == 0){
//                            System.out.println("espera");
//                            try {
//                                //server.smartphoneBlink(0,i%4+1);
//                                Thread.sleep(500);
//                            } catch (InterruptedException ex) {
//                                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
//                            }
//                        }
//                        /*try {
//                                Thread.sleep(50);
//                            } catch (InterruptedException ex) {
//                                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
//                            }*/
//                    }
//                    System.out.println("id="+server.id);
//                }
//                //server.fecharArq();
//            }
       
    } 
    
}
