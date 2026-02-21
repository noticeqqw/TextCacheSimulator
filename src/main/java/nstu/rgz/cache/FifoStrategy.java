package nstu.rgz.cache;

import java.util.Map;

public class FifoStrategy<K> implements EvictionStrategy<K> {
    
    @Override
    public K selectEvictionKey(Map<K, CacheEntry<K>> cacheEntries) {
        K oldestKey = null;
        long minOrder = Long.MAX_VALUE;
        
        for (Map.Entry<K, CacheEntry<K>> entry : cacheEntries.entrySet()) {
            if (entry.getValue().getInsertionOrder() < minOrder) {
                minOrder = entry.getValue().getInsertionOrder();
                oldestKey = entry.getKey();
            }
        }
        
        return oldestKey;
    }
    
    @Override
    public void onAccess(K key, CacheEntry<K> entry) {
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
        return "FIFO";
    }
}
