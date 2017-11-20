package graph;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kifkif on 20/11/2017.
 */
public abstract class AbstractEncoder<D, A extends AbstractData<D>> implements Encoder<D, A> {
    public List<A> encodeDataValues(D[] dataValues)
    {
        List<A> encodeDataValues = new ArrayList();
        for (D aDataValue : dataValues) {
            encodeDataValues.add(this.encode(aDataValue));
        }

        return encodeDataValues;
    }
}
