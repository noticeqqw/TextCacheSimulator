package nstu.rgz.cache;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @param <K>
 */
public class ObjectCache<K> {
    
    private final Map<K, CacheEntry<K>> cache;
    private int maxSize;
    private EvictionStrategy<K> strategy;
    private final CacheStatistics statistics;
    private long insertionCounter = 0;
    private final Set<K> allSeenKeys;
    
    /**
     * @param maxSize 
     * @param strategy
     */
    public ObjectCache(int maxSize, EvictionStrategy<K> strategy) {
        this.maxSize = maxSize;
        this.strategy = strategy;
        this.cache = new HashMap<>();
        this.statistics = new CacheStatistics(maxSize);
        this.allSeenKeys = new HashSet<>();
    }
    
    /**
     * @param key 
     * @return 
     */
    public boolean access(K key) {
        if (!allSeenKeys.contains(key)) {
            allSeenKeys.add(key);
            statistics.incrementUniqueWords();
        }
        
        CacheEntry<K> entry = cache.get(key);
        
        if (entry != null) {
            strategy.onAccess(key, entry);
            statistics.recordHit();
            return true;
        } else {
            statistics.recordMiss();
            addToCache(key);
            return false;
        }
    }
    
    /**
     * @param key
     */
    private void addToCache(K key) {
        if (cache.size() >= maxSize && maxSize > 0) {
            K evictionKey = strategy.selectEvictionKey(cache);
            if (evictionKey != null) {
                cache.remove(evictionKey);
                strategy.onRemove(evictionKey);
                statistics.recordEviction();
            }
        }
        
        if (maxSize > 0) {
            CacheEntry<K> newEntry = new CacheEntry<>(key, insertionCounter++);
            cache.put(key, newEntry);
            strategy.onAdd(key, newEntry);
        }
        
        statistics.setCurrentSize(cache.size());
    }
    
    /**
     * @param newSize
     */
    public void resize(int newSize) {
        this.maxSize = newSize;
        statistics.setMaxSize(newSize);
    
        while (cache.size() > maxSize && maxSize > 0) {
            K evictionKey = strategy.selectEvictionKey(cache);
            if (evictionKey != null) {
                cache.remove(evictionKey);
                strategy.onRemove(evictionKey);
                statistics.recordEviction();
            }
        }
        
        statistics.setCurrentSize(cache.size());
    }
    
    /**
     * @param strategy
     */
    public void setStrategy(EvictionStrategy<K> strategy) {
        this.strategy = strategy;
        strategy.reset();
    }
    
    public void clear() {
        cache.clear();
        strategy.reset();
        statistics.reset();
        allSeenKeys.clear();
        insertionCounter = 0;
    }
    
    /**
     * @return
     */
    public CacheStatistics getStatistics() {
        return statistics;
    }
    
    /**
     * @return
     */
    public int size() {
        return cache.size();
    }
    
    /**
     * @return
     */
    public int getMaxSize() {
        return maxSize;
    }
    
    /**
     * @param key 
     * @return
     */
    public boolean contains(K key) {
        return cache.containsKey(key);
    }
    
    /**
     * @return
     */
    public EvictionStrategy<K> getStrategy() {
        return strategy;
    }
}
