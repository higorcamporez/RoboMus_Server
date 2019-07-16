/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package robomus.instrument;

import java.util.List;
import java.util.Objects;

/**
 *
 * @author higor
 */
public class Delay {
    
    private Integer messageId;
    private String input1;
    private String input2;
    private Integer delay;

    public Delay() {
        delay = -1;
    }

    public Integer getMessageId() {
        return messageId;
    }

    public void setMessageId(Integer messageId) {
        this.messageId = messageId;
    }

    public Integer getDelay() {
        return delay;
    }

    public void setDelay(Integer delay) {
        this.delay = delay;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Delay other = (Delay) obj;
        if (!Objects.equals(this.messageId, other.messageId)) {
            return false;
        }
        return true;
    }
    
    
}
