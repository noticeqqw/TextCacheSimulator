package nstu.rgz.cache;

public class CacheStatistics {
    private long totalRequests;      // Общее количество обращений
    private long cacheHits;          // Количество попаданий в кэш
    private long cacheMisses;        // Количество промахов кэша
    private long evictions;          // Количество вытеснений
    private int currentSize;         // Текущий размер кэша
    private int maxSize;             // Максимальный размер кэша
    private int uniqueWords;         // Количество уникальных слов
    
    public CacheStatistics(int maxSize) {
        this.maxSize = maxSize;
        reset();
    }
    
    public void reset() {
        this.totalRequests = 0;
        this.cacheHits = 0;
        this.cacheMisses = 0;
        this.evictions = 0;
        this.currentSize = 0;
        this.uniqueWords = 0;
    }
    
    public void recordHit() {
        totalRequests++;
        cacheHits++;
    }
    
    public void recordMiss() {
        totalRequests++;
        cacheMisses++;
    }
    
    public void recordEviction() {
        evictions++;
    }
    
    public void setCurrentSize(int size) {
        this.currentSize = size;
    }
    
    public void setMaxSize(int maxSize) {
        this.maxSize = maxSize;
    }
    
    public void incrementUniqueWords() {
        this.uniqueWords++;
    }
    
    public long getTotalRequests() {
        return totalRequests;
    }
    
    public long getCacheHits() {
        return cacheHits;
    }
    
    public long getCacheMisses() {
        return cacheMisses;
    }
    
    public long getEvictions() {
        return evictions;
    }
    
    public int getCurrentSize() {
        return currentSize;
    }
    
    public int getMaxSize() {
        return maxSize;
    }
    
    public int getUniqueWords() {
        return uniqueWords;
    }
    
    /** @return */
    public double getHitRate() {
        if (totalRequests == 0) {
            return 0.0;
        }
        return (double) cacheHits / totalRequests * 100.0;
    }
    
    /** @return */
    public double getMissRate() {
        if (totalRequests == 0) {
            return 0.0;
        }
        return (double) cacheMisses / totalRequests * 100.0;
    }

    /** @return */
    public double getFillRate() {
        if (maxSize == 0) {
            return 0.0;
        }
        return (double) currentSize / maxSize * 100.0;
    }
    
    @Override
    public String toString() {
        return String.format(
            "Статистика кэша:%n" +
            "  Размер кэша: %d/%d (%.1f%%)%n" +
            "  Всего запросов: %d%n" +
            "  Попаданий: %d (%.2f%%)%n" +
            "  Промахов: %d (%.2f%%)%n" +
            "  Вытеснений: %d%n" +
            "  Уникальных слов: %d",
            currentSize, maxSize, getFillRate(),
            totalRequests,
            cacheHits, getHitRate(),
            cacheMisses, getMissRate(),
            evictions,
            uniqueWords
        );
    }
}
