package nstu.rgz.cache;

import java.util.Map;

public class LruStrategy<K> implements EvictionStrategy<K> {
    
    @Override
    public K selectEvictionKey(Map<K, CacheEntry<K>> cacheEntries) {
        K lruKey = null;
        long minAccessTime = Long.MAX_VALUE;
        
        for (Map.Entry<K, CacheEntry<K>> entry : cacheEntries.entrySet()) {
            if (entry.getValue().getLastAccessTime() < minAccessTime) {
                minAccessTime = entry.getValue().getLastAccessTime();
                lruKey = entry.getKey();
            }
        }
        
        return lruKey;
    }
    
    @Override
    public void onAccess(K key, CacheEntry<K> entry) {
        entry.updateAccessTime();
    }
    
    @Override
    public void onAdd(K key, CacheEntry<K> entry) {
    }
    
    @Override
    public void onRemove(K key) {
    }
    
    @Override
    public void reset() {
    }
    
    @Override
    public String getName() {
        return "LRU";
    }
}
