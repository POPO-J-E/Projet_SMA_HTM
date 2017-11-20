package htm.util;

/**
 * Created by kifkif on 20/11/2017.
 */
public class Helper {
    public static void printData(boolean[] data)
    {
        System.out.println(getDataString(data));
    }

    public static String getDataString(boolean[] encodedData) {
        StringBuilder buffer = new StringBuilder();
        buffer.append("[");
        for (boolean anEncodedData : encodedData) {
            buffer.append(anEncodedData ? "1" : "0").append(",");
        }
        buffer.append("]");

        return buffer.toString();
    }
}
