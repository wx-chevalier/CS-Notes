package wx.toolkits.storage.database.mybatis.typehandlers;

import java.io.IOException;
import java.io.Serializable;
import java.io.StringReader;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;

/**
 *
 */
public class PropertiesLazyWrapper implements Serializable, Map<String, String> {

    private static final long serialVersionUID = 8759561336610460894L;

    private final String source;

    private Map<String, String> map;

    public PropertiesLazyWrapper(String properties) {
        Objects.requireNonNull(properties);
        this.source = properties;
    }

    private Map<String, String> map() {
        if (map == null) {
            synchronized (this) {
                if (map == null) {
                    if (this.source.trim().isEmpty()) {
                        map = Collections.EMPTY_MAP;
                    } else {
                        Properties p = new Properties();
                        try {
                            p.load(new StringReader(this.source));
                        } catch (IOException ex) {
                            throw new RuntimeException("Can not load k->v properties from string.\n"
                                + ex.getMessage(), ex);
                        }

                        if (p.isEmpty()) {
                            map = Collections.EMPTY_MAP;
                        } else {
                            // It is awkward but I can not see other way to convert 
                            // Properties to Map<String, String> 
                            int s = p.size() > 6 ? p.size() : 6;
                            int size = (int)Math.ceil((double)s / 0.75) + 1;
                            map = new HashMap<>(size, 0.75F);
                            for (Object keyo : p.keySet()) {
                                String k = String.valueOf(keyo);
                                map.put(k, p.getProperty(k));
                            }
                        }
                    }
                }
            }
        }
        return map;
    }

    @Override
    public int size() {
        return map().size();
    }

    @Override
    public boolean isEmpty() {
        return map().isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return map().containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return map().containsValue(value);
    }

    @Override
    public String get(Object key) {
        return map().get(key);
    }

    @Override
    public String put(String key, String value) {
        return map().put(key, value);
    }

    @Override
    public String remove(Object key) {
        return map().remove(key);
    }

    @Override
    public void putAll(Map<? extends String, ? extends String> m) {
        map().putAll(m);
    }

    @Override
    public void clear() {
        map().clear();
    }

    @Override
    public Set<String> keySet() {
        return map().keySet();
    }

    @Override
    public Collection<String> values() {
        return map().values();
    }

    @Override
    public Set<Entry<String, String>> entrySet() {
        return map().entrySet();
    }
}
