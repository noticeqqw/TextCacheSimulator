package nstu.rgz.cache;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class RandomStrategy<K> implements EvictionStrategy<K> {
    
    private final Random random = new Random();
    
    @Override
    public K selectEvictionKey(Map<K, CacheEntry<K>> cacheEntries) {
        List<K> keys = new ArrayList<>(cacheEntries.keySet());
        if (keys.isEmpty()) {
            return null;
        }
        return keys.get(random.nextInt(keys.size()));
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
        return "RAND";
    }
}
