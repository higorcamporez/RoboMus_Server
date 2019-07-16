/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package robomus.instrument;

import java.util.List;

/**
 *
 * @author higor
 */
public class Action {
    private String actionAdress;
    private List<Argument> arguments;
    private String csvInput;
            
    public Action() {
    }
    
    public Action(String messageName, List params) {
        this.actionAdress = messageName;
        this.arguments = params;
    }

    public String getActionAdress() {
        return actionAdress;
    }

    public void setActionAdress(String actionAdress) {
        this.actionAdress = actionAdress;
    }

    public List<Argument> getArguments() {
        return arguments;
    }

    public void setArguments(List<Argument> arguments) {
        this.arguments = arguments;
    }

    public void convertToOscMessage(){
    
    }
    
    
}
