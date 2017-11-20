/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package htm;

import graph.AbstractNetworkNode;
import graph.NodeInterface;


/**
 *
 * @author farmetta
 */
public class MyColumn extends AbstractNetworkNode {

    /**
     * TODO : Au cours de l'apprentissage, chaque colonne doit atteindre un taux d'activation. 
     * Une colonnne est activée si elle reçoit suffisament de retours positif de ses synapses 
     * (le retour est positif si la synapse est active et que son entrée associée l'est également).
     * 
     * Pour l'apprentissage, parcourir les synapses en entrée, et faire évoluer les poids synaptiques adéquatement.
     * 
     */

    private double activatedSynapses;
    private double minDutyCycle;
    private double activeDutyCycle;
    private double overlapDutyCycle;
    private double boost = 1;
    private boolean activated;

    public void setActivated(boolean activated) {
        this.activated = activated;
    }

    public MyColumn(NodeInterface _node) {
        super(_node);
    }

    public double getActivatedSynapses() {
        return activatedSynapses;
    }

    public void setActivatedSynapses(double activatedSynapses) {
        this.activatedSynapses = activatedSynapses;
    }

    public boolean isActivated()
    {
        return activated;
    }

    public double getMinDutyCycle() {
        return minDutyCycle;
    }

    public void setMinDutyCycle(double minDutyCycle) {
        this.minDutyCycle = minDutyCycle;
    }

    public double getActiveDutyCycle() {
        return activeDutyCycle;
    }

    public void setActiveDutyCycle(double activeDutyCycle) {
        this.activeDutyCycle = activeDutyCycle;
    }

    public double getOverlapDutyCycle() {
        return overlapDutyCycle;
    }

    public void setOverlapDutyCycle(double overlapDutyCycle) {
        this.overlapDutyCycle = overlapDutyCycle;
    }

    public double getBoost() {
        return boost;
    }

    public void setBoost(double boost) {
        this.boost = boost;
    }
}


