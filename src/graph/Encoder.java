package graph;

import java.util.List;

/**
 * Created by kifkif on 20/11/2017.
 */
public interface Encoder<D, A extends AbstractData<D>> {
    public A encode(D data);
    public List<A> encodeDataValues(D[] dataValues);

    public int getLength();
}
