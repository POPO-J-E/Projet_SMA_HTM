/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package htm;

import graph.EdgeBuilder;
import graph.EdgeInterface;
import graph.NodeBuilder;
import graph.NodeInterface;

import java.util.ArrayList;
import java.util.Random;
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

    private boolean[] data;
    private Random rnd = new Random();
    
    
    public MyNetwork(NodeBuilder _nb, EdgeBuilder _eb) {
        nb = _nb;
        eb = _eb;
    }
    
    
    private static final int DENSITE_INPUT_COLUMNS = 8;
    public void buildNetwork(int nbInputs, int nbColumns) {

        data = new boolean[nbInputs];
        
        // création des entrées
        lstMN = new ArrayList<MyNeuron>();
        for (int i = 0; i < nbInputs; i++) {
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
        }

        // Connection entre entrées et colonnes
        for (int i = 0; i < DENSITE_INPUT_COLUMNS * lstMC.size(); i++) {
            
            MyNeuron n = lstMN.get(rnd.nextInt(lstMN.size()));
            MyColumn c = lstMC.get(rnd.nextInt(lstMC.size()));
            
            if (!n.getNode().isConnectedTo(c.getNode())) {
                EdgeInterface e = eb.getNewEdge(n.getNode(), c.getNode());
                MySynapse s = new MySynapse(e);
                e.setAbstractNetworkEdge(s);
                
            } else {
                i--;
            }
        }
        
        
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
        genRandData();

        for (int i = 0; i < data.length; i++) {
            MyNeuron neuron = lstMN.get(i);
            if(data[i])
            {
                neuron.getNode().setState(NodeInterface.State.ACTIVATED);
            }
            else
            {
                neuron.getNode().setState(NodeInterface.State.DESACTIVATED);
            }
        }

//        for (MyColumn c : lstMC) {
//
//            int activatedData = 0;
//
//            for (EdgeInterface e : c.getNode().getEdgeIn()) {
//
//                ((MySynapse) e.getAbstractNetworkEdge()).currentValueUdpate(new Random().nextDouble() - 0.5);
//
//
//
//                MyNeuron n = (MyNeuron) e.getNodeIn().getAbstractNetworkNode(); // récupère le neurone d'entrée
//                if (new Random().nextBoolean()) {
//                    n.getNode().setState(NodeInterface.State.ACTIVATED);
//                } else {
//                    n.getNode().setState(NodeInterface.State.DESACTIVATED);
//                }
//
//
//                try {
//                    Thread.sleep(10);
//                } catch (InterruptedException ex) {
//                    Logger.getLogger(MyNetwork.class.getName()).log(Level.SEVERE, null, ex);
//                }
//            }
//
//
//            if (new Random().nextBoolean()) {
//                c.getNode().setState(NodeInterface.State.ACTIVATED);
//            } else {
//                c.getNode().setState(NodeInterface.State.DESACTIVATED);
//            }
//        }

        try {
            Thread.sleep(100);
        } catch (InterruptedException ex) {
            Logger.getLogger(MyNetwork.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void genRandData()
    {
        for (int i = 0; i < data.length; i++) {
            data[i] = rnd.nextBoolean();
        }
    }
}
