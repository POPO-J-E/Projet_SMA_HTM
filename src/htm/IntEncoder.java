package htm;

import graph.AbstractEncoder;

/**
 * Created by kifkif on 20/11/2017.
 */
public class IntEncoder extends AbstractEncoder<Integer, IntData> {

    private int length;
    private int min;
    private int max;
    private int w;
    private int buckets;
    private int range;

    public IntEncoder(int min, int max, int w) {
        this.min = min;
        this.max = max;
        this.range = getRange();
        this.buckets = range;
        this.w = w;
        this.length = getLength();
    }

    private int getRange()
    {
        return max - min;
    }

    public int getLength()
    {
        return buckets + w - 1;
    }

    @Override
    public IntData encode(Integer data) {
        boolean[] encodedData =  new boolean[length];

        int bucket = getBucket(data);
        for(int i = bucket; i < bucket + w; i++)
        {
            encodedData[i] = true;
        }

        return new IntData(data, encodedData);
    }

    public int getBucket(int v)
    {
        return (int)(buckets * (v - min) / range);
    }
}
