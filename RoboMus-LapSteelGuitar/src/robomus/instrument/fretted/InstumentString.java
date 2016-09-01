/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package robomus.instrument.fretted;

/**
 *
 * @author Higor
 */
public class InstumentString {
    private int stringNumber;
    private String openStringNote;

    public int getStringNumber() {
        return stringNumber;
    }

    public void setStringNumber(int stringNumber) {
        this.stringNumber = stringNumber;
    }

    public String getOpenStringNote() {
        return openStringNote;
    }

    public void setOpenStringNote(String openStringNote) {
        this.openStringNote = openStringNote;
    }   
}
