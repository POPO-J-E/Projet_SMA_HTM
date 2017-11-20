/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package htm;

import graph.*;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 * @author farmetta
 */
public class MyNetwork implements Runnable {

    private NodeBuilder nb;
    private EdgeBuilder eb;
    
    ArrayList<MyNeuron> lstMN;
    ArrayList<MyColumn> lstMC;
    ArrayList<MyColumn> activeColumns = new ArrayList<MyColumn>();
    private final int desiredLocalActivity = 2;
    private double inhibitionRadius;

    private AbstractData data;
    private int lastPos;
    private List<AbstractData> values;
    private Random rnd = new Random();


    public MyNetwork(NodeBuilder _nb, EdgeBuilder _eb) {
        nb = _nb;
        eb = _eb;
    }
    
    
    private static final int DENSITE_INPUT_COLUMNS = 8;
    public void buildNetwork(int min, int max, int nbColumns) {

        Encoder encoder = new IntEncoder(min,max,5);
        int length = encoder.getLength();

        int range = max-min;
        Integer[] dataValues = new Integer[range];
        for (int i = 0; i < range; i++)
        {
            dataValues[i] = min+i;
        }
        values = encoder.encodeDataValues(dataValues);

        lastPos = -1;
        
        // création des entrées
        lstMN = new ArrayList<MyNeuron>();
        for (int i = 0; i < length; i++) {
            NodeInterface ni = nb.getNewNode();
            MyNeuron n = new MyNeuron(ni);
            n.getNode().setPosition(i, 0);
            ni.setAbstractNetworkNode(n);
            lstMN.add(n);
        }
        // création des colonnes
        lstMC = new ArrayList<MyColumn>();
        for (int i = 0; i < nbColumns; i++) {
            NodeInterface ni = nb.getNewNode();
            MyColumn c = new MyColumn(ni);
            c.getNode().setPosition(i*2, 2);
            ni.setAbstractNetworkNode(c);
            
            lstMC.add(c);

            for (MyNeuron n : lstMN){
                EdgeInterface e = eb.getNewEdge(n.getNode(), c.getNode());
                MySynapse s = new MySynapse(e);
                e.setAbstractNetworkEdge(s);
            }
        }

//        // Connection entre entrées et colonnes
//        for (int i = 0; i < DENSITE_INPUT_COLUMNS * lstMC.size(); i++) {
//
//            MyNeuron n = lstMN.get(rnd.nextInt(lstMN.size()));
//            MyColumn c = lstMC.get(rnd.nextInt(lstMC.size()));
//
//            if (!n.getNode().isConnectedTo(c.getNode())) {
//                EdgeInterface e = eb.getNewEdge(n.getNode(), c.getNode());
//                MySynapse s = new MySynapse(e);
//                e.setAbstractNetworkEdge(s);
//
//            } else {
//                i--;
//            }
//        }
    }

    @Override
    public void run() {
        while (true) {

            performRun();

            // processus de démontration qui permet de voyager dans le graphe et de faire varier les état des synaptes, entrées et colonnes
            
//            for (MyColumn c : lstMC) {
//
//                if (new Random().nextBoolean()) {
//                    c.getNode().setState(NodeInterface.State.ACTIVATED);
//                } else {
//                    c.getNode().setState(NodeInterface.State.DESACTIVATED);
//                }
//
//                for (EdgeInterface e : c.getNode().getEdgeIn()) {
//
//                    ((MySynapse) e.getAbstractNetworkEdge()).currentValueUdpate(new Random().nextDouble() - 0.5);
//
//
//
//                    MyNeuron n = (MyNeuron) e.getNodeIn().getAbstractNetworkNode(); // récupère le neurone d'entrée
//                    if (new Random().nextBoolean()) {
//                        n.getNode().setState(NodeInterface.State.ACTIVATED);
//                    } else {
//                        n.getNode().setState(NodeInterface.State.DESACTIVATED);
//                    }
//
//
//                    try {
//                        Thread.sleep(10);
//                    } catch (InterruptedException ex) {
//                        Logger.getLogger(MyNetwork.class.getName()).log(Level.SEVERE, null, ex);
//                    }
//                }
//            }
            
        }
    }

    private void performRun() {
        genNextData();

        boolean[] encodedData = data.getEncodedData();

        for (int i = 0; i < encodedData.length; i++) {
            MyNeuron neuron = lstMN.get(i);
            neuron.setActivated(encodedData[i]);

            if(neuron.isActivated())
            {
                neuron.getNode().setState(NodeInterface.State.ACTIVATED);

//                for (EdgeInterface e : neuron.getNode().getEdgeOut()) {
//                    ((MySynapse) e.getAbstractNetworkEdge()).currentValueUdpate(1);
//                }
            }
            else
            {
                neuron.getNode().setState(NodeInterface.State.DESACTIVATED);

//                for (EdgeInterface e : neuron.getNode().getEdgeOut()) {
//                    ((MySynapse) e.getAbstractNetworkEdge()).currentValueUdpate(0);
//                }
            }
        }

        for (MyColumn c : lstMC) {

            double activatedData = 0;

            for (EdgeInterface e : c.getNode().getEdgeIn()) {

                MySynapse synapse = (MySynapse)e.getAbstractNetworkEdge();
                MyNeuron n = (MyNeuron) e.getNodeIn().getAbstractNetworkNode();
                if(n.isActivated() && synapse.isActivated())
                {
                    activatedData++;
                }
            }

            c.setActivatedSynapses(activatedData);

            c.getNode().setState(NodeInterface.State.DESACTIVATED);
            c.setActivated(false);
        }

        inhibition();
        learning();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            Logger.getLogger(MyNetwork.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void genNextData()
    {
        lastPos = (lastPos+1)%values.size();
        data = values.get(lastPos);
    }

    public void learningColumn(MyColumn column){
        for (EdgeInterface e : column.getNode().getEdgeIn())
        {
            MySynapse synapse = (MySynapse)e.getAbstractNetworkEdge();
            MyNeuron neuron = (MyNeuron) e.getNodeIn().getAbstractNetworkNode();

            if (neuron.isActivated()) {
                synapse.currentValueUdpate(0.1);
            } else {
                synapse.currentValueUdpate(-0.1);
            }
        }
    }

    public void learning() {
        for (MyColumn column : activeColumns) {
            learningColumn(column);
        }

        for (MyColumn column : lstMC) {
            column.setMinDutyCycle(0.01 * getMaxDutyCycle(getNeighbors(column)));
            column.setActiveDutyCycle(updateActiveDutyCycle(column));
            column.setBoost(boostFunction(column.getBoost(),column.getActiveDutyCycle(),column.getMinDutyCycle()));
            column.setOverlapDutyCycle(updateOverlapDutyCycle(column));

            if (column.getOverlapDutyCycle() < column.getMinDutyCycle())
            {
                increasePermanences(column,0.1*connectedPerm());
            }
        }

        inhibitionRadius = averageReceptiveFieldSize();
    }

    private void increasePermanences(MyColumn column, double v) {
        for (EdgeInterface e : column.getNode().getEdgeIn())
        {
            MySynapse synapse = (MySynapse)e.getAbstractNetworkEdge();
            synapse.setTHRESHOLD(synapse.getTHRESHOLD()+v);
        }
    }

    private double averageReceptiveFieldSize()
    {
        double activatedData = 0;
        for (MyColumn c : lstMC) {
            for (EdgeInterface e : c.getNode().getEdgeIn()) {
                MySynapse synapse = (MySynapse)e.getAbstractNetworkEdge();
                if(synapse.isActivated())
                {
                    activatedData++;
                }
            }
        }
        return activatedData / (double)lstMC.size();
    }

    private double connectedPerm() {
        return 0.5;
    }

    public double getMaxDutyCycle(List<MyColumn> columns){
        double max = 0;
        for (MyColumn c : lstMC) {
            if(c.getActiveDutyCycle() > max)
            {
                max = c.getActiveDutyCycle();
            }
        }
        return max;
    }

    public double updateActiveDutyCycle(MyColumn column) {
        return 3;
    }

    public double updateOverlapDutyCycle(MyColumn column) {
        return 3;
    }

    public double boostFunction(double boots, double activeDutyCycle, double minDutyCycle) {
        return (activeDutyCycle > minDutyCycle) ? 1 : boots+0.1;
    }

    public void inhibition(){
        double minLocalActivity = 0;

        Collections.sort(getNeighbors(null), new Comparator<MyColumn>() {
            @Override
            public int compare(MyColumn o1, MyColumn o2) {
                int compare = (o1.getActivatedSynapses() > o2.getActivatedSynapses()) ? 1 : 0;
                if (compare==0)
                    compare = (o1.getActivatedSynapses() == o2.getActivatedSynapses()) ? 0 : -1;

                return compare;
            }
        });

        for (int i=0;i<desiredLocalActivity;i++)
        {
            MyColumn column = lstMC.get(i);
            activeColumns.add(column);
            column.setActivated(true);
            column.getNode().setState(NodeInterface.State.ACTIVATED);
        }
    }

    //TODO
    public List<MyColumn> getNeighbors(MyColumn column){
        return lstMC;
    }
}
