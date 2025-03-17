package com.playgrounds;

import org.apache.synapse.MessageContext; 
import org.apache.synapse.mediators.AbstractMediator;
    
public class TestMed extends AbstractMediator { 

    @Override
    public boolean mediate(MessageContext context) { 
        return true;
    }
}
