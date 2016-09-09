/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package robomus;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import robomus.arduinoCommunication.PortControl;

/**
 *
 * @author Higor
 */
public class RoboMusLapSteelGuitar {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        PortControl portControl = new PortControl("COM8", 9600);
        byte b[] = {1,2,3,4,5};
        
        try {
            portControl.sendData(b);
        } catch (IOException ex) {
            System.out.println("Não foi possível enviar o dado. ");
        }
        
        try {
            portControl.close();
        } catch (IOException ex) {
            System.out.println("Não foi possível fechar porta COM.");
        }
    }
    
}
