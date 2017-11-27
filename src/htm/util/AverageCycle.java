package htm.util;

/**
 * Created by kifkif on 25/11/2017.
 */
public class AverageCycle {
    private boolean[] cycle;
    private int pos;
    private int nbTotCycle;

    private int nbActive;
    private int size;

    public AverageCycle(int size) {
        this.cycle = new boolean[size];
        this.pos = 0;
        this.nbTotCycle = 0;
        this.nbActive = 0;
        this.size = size;
    }

    public boolean[] getCycle() {
        return cycle;
    }

    public int getPos() {
        return pos;
    }

    public int getNbTotCycle() {
        return nbTotCycle;
    }

    public void update(boolean value)
    {
        if(cycle[pos])
            nbActive--;
        cycle[pos] = value;
        if(cycle[pos])
            nbActive++;
        pos = (pos+1)%size;
    }

    public double getAverage()
    {
        return ((double)nbActive)/((double)size);
    }
}
