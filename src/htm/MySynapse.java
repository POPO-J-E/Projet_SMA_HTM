/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package htm;

import graph.AbstractNetworkEdge;
import graph.EdgeInterface;
import java.util.Random;

/**
 *
 * @author farmetta
 */
public class MySynapse extends AbstractNetworkEdge {
    
    private double currentValue = new Random().nextDouble();
    private double THRESHOLD = 0.5;
    
    
    protected MySynapse(EdgeInterface _edge) {
        super(_edge);
        currentValueUdpate(0);
    }
    
    public void currentValueUdpate(double delta) {
        currentValue += delta;

        if (currentValue > 1) {
            currentValue = 1;
        }
        if (currentValue < 0) {
            currentValue = 0;
        }

        if (isActivated()) {
            getEdge().setState(EdgeInterface.State.ACTIVATED);
        } else {
            getEdge().setState(EdgeInterface.State.DESACTIVATED);
        }
    }

    public boolean isActivated()
    {
        return currentValue >= THRESHOLD;
    }

    public double getCurrentValue() {
        return currentValue;
    }

    public void setCurrentValue(double currentValue) {
        this.currentValue = currentValue;
    }

    public double getTHRESHOLD() {
        return THRESHOLD;
    }

    public void setTHRESHOLD(double THRESHOLD) {
        this.THRESHOLD = THRESHOLD;
    }
}
