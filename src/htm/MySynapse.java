/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package htm;

import graph.AbstractNetworkEdge;
import graph.EdgeInterface;

/**
 *
 * @author farmetta
 */
public class MySynapse extends AbstractNetworkEdge {

    private double permanence;
    
    
    protected MySynapse(EdgeInterface _edge, double permanence) {
        super(_edge);
        this.permanence = permanence;
        updatePermanence(0);
    }
    
    public void updatePermanence(double delta) {
        permanence += delta;

        if (permanence > 1) {
            permanence = 1;
        }
        if (permanence < 0) {
            permanence = 0;
        }

        if (isActivated()) {
            getEdge().setState(EdgeInterface.State.ACTIVATED);
        } else {
            getEdge().setState(EdgeInterface.State.DESACTIVATED);
        }
    }

    public boolean isActivated()
    {
        return MyNetwork.connectedPerm <= permanence;
    }

    public double getPermanence() {
        return permanence;
    }

    public void setPermanence(double permanence) {
        this.permanence = permanence;
    }
}
