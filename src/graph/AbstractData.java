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

    @Override
    public String toString() {
        return data + ":" + Helper.getDataString(encodedData);
    }
}
