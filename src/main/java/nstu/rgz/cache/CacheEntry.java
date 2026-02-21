package nstu.rgz.cache;

public class CacheEntry<K> {
    private final K key;
    private long insertionOrder;  
    private long lastAccessTime; 
    private int accessCount;    
    
    public CacheEntry(K key, long insertionOrder) {
        this.key = key;
        this.insertionOrder = insertionOrder;
        this.lastAccessTime = System.nanoTime();
        this.accessCount = 1;
    }
    
    public K getKey() {
        return key;
    }
    
    public long getInsertionOrder() {
        return insertionOrder;
    }
    
    public void setInsertionOrder(long insertionOrder) {
        this.insertionOrder = insertionOrder;
    }
    
    public long getLastAccessTime() {
        return lastAccessTime;
    }
    
    public void updateAccessTime() {
        this.lastAccessTime = System.nanoTime();
        this.accessCount++;
    }
    
    public int getAccessCount() {
        return accessCount;
    }
}
