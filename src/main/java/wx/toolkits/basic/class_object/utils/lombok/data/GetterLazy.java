package wx.toolkits.basic.class_object.utils.lombok.data;

import lombok.Getter;

/**
 * Created by apple on 16/5/19.
 */
class GetterLazy {

    @Getter(lazy = true)
    private final double[] cached = expensive();

    private double[] expensive() {
        double[] result = new double[1000000];
        for (int i = 0; i < result.length; i++) {
            result[i] = Math.asin(i);
        }
        return result;
    }

    public static void main(String[] args) {
        GetterLazy getterLazy = new GetterLazy();

        getterLazy.getCached();
    }
}