package wx.storage.cache;

/**
 * Created by apple on 15/11/17.
 */
public abstract class WxAbstractCache {


    /**
     * @required methods
     */
    /**
     * @function get Instance Of this Cache
     */
    public abstract WxAbstractCache getSingleInstance();

    /**
     * @return get Object from Cache
     */
    public abstract Object get(Object key);

    /**
     * @param key
     * @param value
     * @return
     * @function set Object
     */
    public abstract boolean put(Object key, Object value);

    /**
     * @return
     * @function Destroy The Global Cache
     */
    public abstract boolean destroy();

}
