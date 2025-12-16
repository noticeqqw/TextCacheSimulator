package nstu.rgz.cache;
import java.util.Map;

public interface EvictionStrategy<K> {
    
    /**
     * @param cacheEntries
     * @return
     */
    K selectEvictionKey(Map<K, CacheEntry<K>> cacheEntries);
    
    /**
     * @param key
     * @param entry
     */
    void onAccess(K key, CacheEntry<K> entry);
    
    /**
     * @param key
     * @param entry
     */
    void onAdd(K key, CacheEntry<K> entry);
    
    /**
     * @param key
     */
    void onRemove(K key);
    
    void reset();
    
    /**
     * @return
     */
    String getName();
}
