/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package robomus.instrument.fretted;

import java.net.InetAddress;
import lapsteelguitarrobot.instrument.Instrument;

/**
 *
 * @author Higor
 */
public abstract class FrettedInstrument extends Instrument{
    
    protected int nFrets;
    protected InstumentString[] strings;

    public FrettedInstrument(int nFrets, InstumentString[] strings, String name,
            int polyphony, String OscAddress, InetAddress severAddress, 
            int sendPort, int receivePort, String typeFamily) {
        super(name, polyphony, OscAddress, severAddress, sendPort, receivePort,
                typeFamily);
        
        this.nFrets = nFrets;
        this.strings = strings;
    }
    
    public String formatStrings(){
        String formated = "";
        for (InstumentString s : strings) {
            formated = formated + 
        }
        
    }
    
    
}
