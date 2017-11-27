/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package htm;

import graph.AbstractNetworkNode;
import graph.NodeInterface;
import htm.util.AverageCycle;


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

    private double overlap;
    private double minDutyCycle;
    private double boost = 1;
    private boolean activated;
    private AverageCycle activeDutyCycle;
    private AverageCycle overlapDutyCycle;

    private final static int cycleSize = 100;

    public void setActivated(boolean activated) {
        this.activated = activated;
    }

    public MyColumn(NodeInterface _node) {
        super(_node);
        this.activeDutyCycle = new AverageCycle(cycleSize);
        this.overlapDutyCycle = new AverageCycle(cycleSize);
    }

    public double getOverlap() {
        return overlap;
    }

    public void setOverlap(double overlap) {
        this.overlap = overlap;
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
        return activeDutyCycle.getAverage();
    }

    public void updateActiveDutyCycle(boolean activeDutyCycle) {
        this.activeDutyCycle.update(activeDutyCycle);
    }

    public double getOverlapDutyCycle() {
        return overlapDutyCycle.getAverage();
    }

    public void updateOverlapDutyCycle(boolean overlapDutyCycle) {
        this.overlapDutyCycle.update(overlapDutyCycle);
    }

    public double getBoost() {
        return boost;
    }

    public void setBoost(double boost) {
        this.boost = boost;
    }
}


