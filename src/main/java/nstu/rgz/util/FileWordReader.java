package nstu.rgz.util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Класс для последовательного чтения слов из текстового файла
 */
public class FileWordReader implements Iterator<String>, AutoCloseable {
    
    private static final Pattern WORD_PATTERN = Pattern.compile("[\\p{L}\\p{N}]+", Pattern.UNICODE_CHARACTER_CLASS);
    
    private BufferedReader reader;
    private String currentLine;
    private Matcher currentMatcher;
    private String nextWord;
    private boolean hasNextCalled = false;
    
    /**
     * Открывает файл для чтения слов
     * @param filePath путь к файлу
     * @throws IOException если не удалось открыть файл
     */
    public FileWordReader(String filePath) throws IOException {
        this.reader = new BufferedReader(
            new InputStreamReader(new FileInputStream(filePath), StandardCharsets.UTF_8)
        );
        advanceToNextWord();
    }
    
    /**
     * Переходит к следующему слову
     */
    private void advanceToNextWord() {
        nextWord = null;
        
        while (nextWord == null) {
            // Если есть текущий matcher, пытаемся найти следующее слово
            if (currentMatcher != null && currentMatcher.find()) {
                nextWord = currentMatcher.group().toLowerCase();
                return;
            }
            
            // Читаем следующую строку
            try {
                currentLine = reader.readLine();
            } catch (IOException e) {
                currentLine = null;
            }
            
            if (currentLine == null) {
                return; // Конец файла
            }
            
            currentMatcher = WORD_PATTERN.matcher(currentLine);
        }
    }
    
    @Override
    public boolean hasNext() {
        hasNextCalled = true;
        return nextWord != null;
    }
    
    @Override
    public String next() {
        if (!hasNextCalled) {
            hasNext();
        }
        
        if (nextWord == null) {
            throw new NoSuchElementException("Нет больше слов в файле");
        }
        
        String word = nextWord;
        hasNextCalled = false;
        advanceToNextWord();
        return word;
    }
    
    @Override
    public void close() throws IOException {
        if (reader != null) {
            reader.close();
            reader = null;
        }
    }
    
    /**
     * Читает все слова из файла в список
     * @param filePath путь к файлу
     * @return список слов
     * @throws IOException если не удалось прочитать файл
     */
    public static List<String> readAllWords(String filePath) throws IOException {
        List<String> words = new ArrayList<>();
        try (FileWordReader reader = new FileWordReader(filePath)) {
            while (reader.hasNext()) {
                words.add(reader.next());
            }
        }
        return words;
    }
    
    /**
     * Подсчитывает количество слов в файле
     * @param filePath путь к файлу
     * @return количество слов
     * @throws IOException если не удалось прочитать файл
     */
    public static int countWords(String filePath) throws IOException {
        int count = 0;
        try (FileWordReader reader = new FileWordReader(filePath)) {
            while (reader.hasNext()) {
                reader.next();
                count++;
            }
        }
        return count;
    }
}
