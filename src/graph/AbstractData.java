package graph;

import htm.util.Helper;

/**
 * Created by kifkif on 20/11/2017.
 */
public class AbstractData<D> {
    private D data;
    private boolean[] encodedData;

    public AbstractData(D data, boolean[] encodedData) {
        this.data = data;
        this.encodedData = encodedData;
    }

    public D getData() {
        return data;
    }

    public boolean[] getEncodedData() {
        return encodedData;
    }

    @Override
    public String toString() {
        return data + ":" + Helper.getDataString(encodedData);
    }

    public boolean[] getEncodedDataWithNoise() {
        boolean[] noiseData = new boolean[encodedData.length];
        for (int i = 0; i < encodedData.length; i++) {
            noiseData[i] = Math.random() < 0.9 ? encodedData[i] : !encodedData[i];
        }
        return noiseData;
    }
}
