/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package robomus.instrument;

import com.illposed.osc.OSCMessage;
import com.illposed.osc.OSCPortOut;
import java.io.IOException;
import robomus.instrument.Action;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import robomus.util.Note;
import robomus.util.Notes;

        
/**
 *
 * @author Higor
 */
public class Instrument implements Serializable{
    
    protected String name; // nome do instrumento   
    protected int polyphony; // quantidade de notas
    protected String OscAddress; //endereço do OSC do instrumento
    protected int sendPort; // porta para envio msgOSC
    protected int receivePort; // porta pra receber msgOSC
    protected String typeFamily; //tipo do instrumento
    protected String specificProtocol; //procolo especifico do robo
    protected String ip;
    protected int threshold;
    protected List<Action> Actions;
    protected Action lastAction;
    protected OSCPortOut sender;
    protected List<Delay> delays; 
    protected boolean waitingDelay;
    
    public Instrument(){
    }

    public Instrument(String OscAddress) {
        this.OscAddress = OscAddress;
    }
    
    public Instrument(String name, int polyphony, String OscAddress,
            int sendPort, int receivePort,
            String typeFamily, String specificProtocol,
            String ip, int threshold) {
        
        this.name = name;
        this.polyphony = polyphony;
        this.OscAddress = OscAddress;
        
        this.sendPort = sendPort;
        this.receivePort = receivePort;
        this.typeFamily = typeFamily;
        this.specificProtocol = specificProtocol;
        this.ip = ip;
        this.threshold = threshold;
        this.Actions = new ArrayList<Action>(); 
        this.delays = new ArrayList<Delay>();
        
        try {
            this.sender = new OSCPortOut(InetAddress.getByName(this.getIp()), this.getReceivePort());
        } catch (UnknownHostException ex) {
            Logger.getLogger(Instrument.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SocketException ex) {  
            Logger.getLogger(Instrument.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    private void setActions(){
        String strActions[] = this.specificProtocol.split(">");
        List actionParametersName = new ArrayList<String>();
       
        for (String strAct : strActions){   
            Action action = new Action();
            //separando os parametros da ação
            String strArgs[] = strAct.split(";");
            //adicionando o nome da ação
            action.setActionAdress(strArgs[0]);
            
            //lista de argumentos
            List argsList = new ArrayList<>();
            
            for (int i  = 1; i < strArgs.length; i++) {
                 
                String name = strArgs[i].split("_")[0];
                String type = strArgs[i].split("_")[1];
                
                //se for do tipo note(n)    
                if(type == "n"){
                    Argument argument = new Argument(name, 'n');
                    argsList.add(argument);
                }
             
            }
            action.setArguments(argsList);
            this.Actions.add(action);
            
        }
       
    }

    public boolean isWaitingDelay() {
        return waitingDelay;
    }

    public void setWaitingDelay(boolean waitingDelay) {
        this.waitingDelay = waitingDelay;
    }
    
    public int getThreshold() {
        return threshold;
    }

    public void setThreshold(int threshold) {
        this.threshold = threshold;
    }

    public String getName() {
        return name;
    }
    
    public String getIp() {
        return this.ip;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPolyphony() {
        return polyphony;
    }

    public void setPolyphony(int polyphony) {
        this.polyphony = polyphony;
    }

    public String getOscAddress() {
        return OscAddress;
    }

    public void setOscAddress(String OscAddress) {
        this.OscAddress = OscAddress;
    }

    public int getSendPort() {
        return sendPort;
    }

    public void setSendPort(int sendPort) {
        this.sendPort = sendPort;
    }

    public int getReceivePort() {
        return receivePort;
    }

    public void setReceivePort(int receivePort) {
        this.receivePort = receivePort;
    }

    public String getTypeFamily() {
        return typeFamily;
    }

    public void setTypeFamily(String typeFamily) {
        this.typeFamily = typeFamily;
    }

    public String getSpecificProtocol() {
        return specificProtocol;
    }

    public void setSpecificProtocol(String specificProtocol) {
        this.specificProtocol = specificProtocol;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    @Override
    public String toString() {
        return "Instrument{" + "name=" + name +
                ", polyphony=" + polyphony +
                ", OscAddress=" + OscAddress +
                ", sendPort=" + sendPort +
                ", receivePort=" + receivePort +
                ", typeFamily=" + typeFamily +
                ", specificProtocol=" + specificProtocol +
                ", ip=" + ip +
                ", threshold=" + threshold + '}';
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Instrument other = (Instrument) obj;
        if (!Objects.equals(this.OscAddress, other.OscAddress)) {
            return false;
        }
        return true;
    }
    
    public List createNewAction(Long id){
        Random rand = new Random();
        Integer index = rand.nextInt(this.Actions.size());
        Action act = this.Actions.get(index);
        List<Argument> args = act.getArguments();
        List row = new ArrayList<>();
        OSCMessage oscMessage = null;
        
        //adiciona index que representa ação
        row.add(index);
                
        for (Argument arg : args) {
            if(arg.getType() == 'n'){
                Note note = Notes.generateNote();
                //adiciona valor midi que representa a nota
                row.add(note.getMidiValue());
                
                oscMessage = new OSCMessage(this.getOscAddress()+act.getActionAdress());
                oscMessage.addArgument(id);
                oscMessage.addArgument(note.getSymbol()+note.getOctavePitch());
            }
            //tem que criar os ifs para os outros tipos
        }
        List list = new ArrayList<>();
        
        list.add(oscMessage);
        list.add(row);
        
        return list;
    }
    
    public void send(OSCMessage oscMessge){
        
        try {
            sender.send(oscMessge);
        } catch (IOException ex) {
            Logger.getLogger(Instrument.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void sendTrainMessage(OSCMessage oscMessge){
        this.send(oscMessge);
        this.setWaitingDelay(true);
    }

    public void setLastDelay(Integer id, Integer delay) {
        
        
        Delay d = this.delays.get(this.delays.size() - 1);
        if(d.getMessageId() == id){
           d.setDelay(delay);
        }        
                
    }
    
}
