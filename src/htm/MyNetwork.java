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
    private double inhibitionRadius = Integer.MAX_VALUE;

    private AbstractData data;
    private int lastPos;
    private List<AbstractData> values;
    private Random rnd = new Random();
    private double minOverlap = 3;
    public static final double connectedPerm = 0.5;

    private double permanenceInc = 0.01;
    private double permanenceDec = 0.01;

    private int learningSteps = 1000;
    private int currentStep = 0;


    public MyNetwork(NodeBuilder _nb, EdgeBuilder _eb) {
        nb = _nb;
        eb = _eb;
    }
    
    
    private static final int DENSITE_INPUT_COLUMNS = 6;
    public void buildNetwork(int min, int max, int nbColumns) {

        Encoder encoder = new IntEncoder(min,max,DENSITE_INPUT_COLUMNS);
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

        double dist = ((double)lstMN.size());
        double centerSize = dist/((double)nbColumns);

        // création des colonnes
        lstMC = new ArrayList<MyColumn>();
        for (int i = 0; i < nbColumns; i++) {
            NodeInterface ni = nb.getNewNode();
            MyColumn c = new MyColumn(ni);
            c.getNode().setPosition(i*2, 2);
            ni.setAbstractNetworkNode(c);
            
            lstMC.add(c);

            double center = i*centerSize + centerSize/2.;

            int j = 0;
            for (MyNeuron n : lstMN) {
                double d = Math.abs(center - j) / dist;

                if (d < 0.5) {
                    EdgeInterface e = eb.getNewEdge(n.getNode(), c.getNode());
                    MySynapse s = new MySynapse(e, 0.2 * Math.random() + 0.5 - 0.2 * d);
                    e.setAbstractNetworkEdge(s);
                }
                j++;
            }
        }
    }

    @Override
    public void run() {
        while (true) {
            performRun();
        }
    }

    private void performRun() {
        updateData();

        overlap();

        inhibition();

        if(learningSteps > currentStep) {
            learning();
            try {
                Thread.sleep(10);
            } catch (InterruptedException ex) {
                Logger.getLogger(MyNetwork.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        else
        {
            updateInfos();
            System.out.println(inhibitionRadius);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                Logger.getLogger(MyNetwork.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        currentStep++;
    }

    private void updateData() {
        genNextData();

        boolean[] encodedData = data.getEncodedData();

        for (int i = 0; i < encodedData.length; i++) {
            MyNeuron neuron = lstMN.get(i);
            neuron.setActivated(encodedData[i]);

            if(learningSteps <= currentStep)
            {
                if(neuron.isActivated())
                {
                    neuron.getNode().setState(NodeInterface.State.ACTIVATED);

//                for (EdgeInterface e : neuron.getNode().getEdgeOut()) {
//                    ((MySynapse) e.getAbstractNetworkEdge()).updatePermanence(1);
//                }
                }
                else
                {
                    neuron.getNode().setState(NodeInterface.State.DESACTIVATED);

//                for (EdgeInterface e : neuron.getNode().getEdgeOut()) {
//                    ((MySynapse) e.getAbstractNetworkEdge()).updatePermanence(0);
//                }
                }
            }

            neuron.getNode().setInfos(neuron.isActivated() ? "1" : "0");
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
                synapse.updatePermanence(permanenceInc);
            } else {
                synapse.updatePermanence(-permanenceDec);
            }
        }
    }

    public void learning() {
        for (MyColumn column : activeColumns) {
            learningColumn(column);
        }

        int i = 0;
        for (MyColumn column : lstMC) {
            column.setMinDutyCycle(0.01 * getMaxDutyCycle(getNeighbors(i)));
            updateActiveDutyCycle(column);
            column.setBoost(boostFunction(column.getBoost(),column.getActiveDutyCycle(),column.getMinDutyCycle()));
            updateOverlapDutyCycle(column);

            if (column.getOverlapDutyCycle() < column.getMinDutyCycle())
            {
                increasePermanences(column,0.1*connectedPerm());
            }
            i++;
        }

        inhibitionRadius = averageReceptiveFieldSize();
    }

    private void increasePermanences(MyColumn column, double v) {
        for (EdgeInterface e : column.getNode().getEdgeIn())
        {
            MySynapse synapse = (MySynapse)e.getAbstractNetworkEdge();
            synapse.setPermanence(synapse.getPermanence()+v);
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
        return connectedPerm;
    }

    public double getMaxDutyCycle(List<MyColumn> columns){
        double max = 0;
        for (MyColumn c : columns) {
            if(c.getActiveDutyCycle() > max)
            {
                max = c.getActiveDutyCycle();
            }
        }
        return max;
    }

    public void updateActiveDutyCycle(MyColumn column) {
        column.updateActiveDutyCycle(column.isActivated());
    }

    public void updateOverlapDutyCycle(MyColumn column) {
        column.updateOverlapDutyCycle(column.getOverlap() > 0);
    }

    public double boostFunction(double boots, double activeDutyCycle, double minDutyCycle) {
        return (activeDutyCycle > minDutyCycle) ? 1 : boots+0.1;
    }

    public void inhibition(){
        activeColumns.clear();
        double minLocalActivity = 0;

        int i = 0;
        for (MyColumn column : lstMC)
        {
            minLocalActivity = getKthScore(getNeighbors(i), desiredLocalActivity);

            if(column.getOverlap() > 0 && column.getOverlap() >= minLocalActivity)
            {
                activeColumns.add(column);
                column.setActivated(true);
                if(learningSteps <= currentStep)
                {
                    column.getNode().setState(NodeInterface.State.ACTIVATED);
                }
            }
            i++;
        }
    }

    public double getKthScore(List<MyColumn> columns, int k)
    {
        columns.sort((o1, o2) -> {
            int compare = (o1.getOverlap() > o2.getOverlap()) ? 1 : 0;
            if (compare == 0)
                compare = (o1.getOverlap() == o2.getOverlap()) ? 0 : -1;

            return compare;
        });

        if(columns.size() > k)
        {
            return columns.get(k).getOverlap();
        }

        return 0;
    }

    //TODO
    public List<MyColumn> getNeighbors(int icolumn){
        int start = (int)(icolumn - inhibitionRadius);
        int end = (int)(icolumn + inhibitionRadius + 1);

        if(start < 0)
        {
            start = 0;
        }

        if(end > lstMC.size())
        {
            end = lstMC.size();
        }

        List<MyColumn> neighbors = new ArrayList<>();

        for(int i = start; i < end; i++)
        {
            if(i != icolumn)
            {
                neighbors.add(lstMC.get(i));
            }
        }

        return neighbors;
    }

    public void overlap(MyColumn collumn)
    {
        double overlap = 0;

        for (EdgeInterface e : collumn.getNode().getEdgeIn()) {

            MySynapse synapse = (MySynapse)e.getAbstractNetworkEdge();
            MyNeuron n = (MyNeuron) e.getNodeIn().getAbstractNetworkNode();
            if(n.isActivated() && synapse.isActivated())
            {
                overlap += 1;
            }
        }

        if(overlap < minOverlap)
        {
            overlap = 0;
        }
        else
        {
            overlap = overlap * collumn.getBoost();
        }

        collumn.setOverlap(overlap);

        collumn.getNode().setState(NodeInterface.State.DESACTIVATED);
        collumn.setActivated(false);
    }

    public void overlap()
    {
        for (MyColumn c : lstMC) {

            overlap(c);
        }
    }

    public void logs()
    {
        System.out.println("Inhibition radius : " + inhibitionRadius);

    }

    public void updateInfos()
    {
        for (MyColumn column : lstMC)
        {
            updateInfos(column);
        }
    }

    public void updateInfos(MyColumn column)
    {
        StringBuilder infos = new StringBuilder("");

        infos.append("overlap : ").append(formatNumber(column.getOverlap())).append("\n");
        infos.append("active duty cycle : ").append(formatNumber(column.getActiveDutyCycle())).append("\n");
        infos.append("overlap duty cycle : ").append(formatNumber(column.getOverlapDutyCycle())).append("\n");
        infos.append("min duty cycle : ").append(formatNumber(column.getMinDutyCycle())).append("\n");
        infos.append("boost : ").append(formatNumber(column.getBoost())).append(">");

        column.getNode().setInfos(infos.toString());

        for(EdgeInterface edgeInterface : column.getNode().getEdgeIn())
        {
            updateInfos((MySynapse)edgeInterface.getAbstractNetworkEdge());
        }
    }

    public void updateInfos(MySynapse synapse)
    {
        StringBuilder infos = new StringBuilder("");

        infos.append("permanence : ").append(formatNumber(synapse.getPermanence()));

        synapse.getEdge().setInfos(infos.toString());
    }

    private String formatNumber(double n)
    {
        return String.format("%.3f%n", n);
    }
}
