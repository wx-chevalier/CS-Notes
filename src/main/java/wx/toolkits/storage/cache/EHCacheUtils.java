package wx.storage.cache;

import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.CacheManagerBuilder;
import org.ehcache.config.CacheConfigurationBuilder;

/**
 * Created by apple on 15/11/17.
 */
public class EHCacheUtils extends WxAbstractCache {

    private final Cache<String, String> preConfigured;

    public EHCacheUtils() {
        CacheManager cacheManager = CacheManagerBuilder.newCacheManagerBuilder()
                .withCache("preConfigured",
                        CacheConfigurationBuilder.newCacheConfigurationBuilder().buildConfig(String.class, String.class))
                .build(true);

        preConfigured = cacheManager.getCache("preConfigured", String.class, String.class);

    }


    @Override
    public WxAbstractCache getSingleInstance() {
        return Holder.instance;
    }

    @Override
    public Object get(Object key) {
        return null;
    }

    @Override
    public boolean put(Object key, Object value) {
        return false;
    }

    @Override
    public boolean destroy() {
        return false;
    }


    /**
     * @region Inner Helper
     */
    /**
     * @function Inner Class Holder
     */
    private static class Holder {
        static final EHCacheUtils instance = new EHCacheUtils();
    }

    public static void main(String args[]) {

    }
}
