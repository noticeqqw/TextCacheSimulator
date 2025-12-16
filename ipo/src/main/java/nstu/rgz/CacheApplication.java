package nstu.rgz;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import nstu.rgz.cache.*;
import nstu.rgz.util.FileWordReader;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CacheApplication extends Application {
    
    private TextField filePathField;
    private Spinner<Integer> cacheSizeSpinner;
    private ComboBox<String> strategyComboBox;
    private Button loadFileButton;
    private Button runButton;
    private Label totalWordsLabel;
    private Label uniqueWordsLabel;
    private Label cacheHitsLabel;
    private Label cacheMissesLabel;
    private Label hitRateLabel;
    private Label evictionsLabel;
    private Label cacheFillLabel;

    private TextArea logArea;
    
    private List<String> currentWords;
    
    private ExecutorService executor = Executors.newSingleThreadExecutor();
    
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Кэш объектов - Анализ стратегий вытеснения");
        
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(15));
        root.setStyle("-fx-background-color: #f5f5f5;");
        
        VBox mainPanel = new VBox(15);
        mainPanel.setPadding(new Insets(15));
        mainPanel.setStyle("-fx-background-color: #e8e8e8; -fx-background-radius: 8; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 5, 0, 0, 2);");
        
        mainPanel.getChildren().addAll(
            createTopPanel(),
            createStatsPanel(),
            createLogPanel()
        );
        
        root.setCenter(mainPanel);
        
        Scene scene = new Scene(root, 900, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
        
        updateStatsDisplay(null);
    }
    
    private VBox createTopPanel() {
        VBox topPanel = new VBox(12);
        topPanel.setPadding(new Insets(10, 10, 15, 10));
        topPanel.setStyle("-fx-background-color: transparent;");
        
        HBox filePanel = new HBox(10);
        filePanel.setAlignment(Pos.CENTER_LEFT);
        
        Label fileLabel = new Label("Файл:");
        fileLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: 500;");
        filePathField = new TextField();
        filePathField.setPrefWidth(450);
        filePathField.setEditable(false);
        filePathField.setStyle("-fx-font-size: 13px; -fx-background-color: #f8f8f8;");
        
        loadFileButton = new Button("Выбрать файл...");
        loadFileButton.setOnAction(e -> selectFile());
        loadFileButton.setStyle("-fx-font-size: 13px; -fx-padding: 6 15 6 15; -fx-background-color: #4a90e2; -fx-text-fill: white; -fx-background-radius: 5; -fx-cursor: hand;");
        
        filePanel.getChildren().addAll(fileLabel, filePathField, loadFileButton);
        
        HBox settingsPanel = new HBox(20);
        settingsPanel.setAlignment(Pos.CENTER_LEFT);
        
        Label cacheSizeLabel = new Label("Размер кэша:");
        cacheSizeLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: 500;");
        cacheSizeSpinner = new Spinner<>(1, 10000, 100, 10);
        cacheSizeSpinner.setEditable(true);
        cacheSizeSpinner.setPrefWidth(100);
        cacheSizeSpinner.setStyle("-fx-font-size: 13px;");
        
        Label strategyLabel = new Label("Стратегия:");
        strategyLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: 500;");
        strategyComboBox = new ComboBox<>(FXCollections.observableArrayList("FIFO", "LRU", "RAND"));
        strategyComboBox.setValue("LRU");
        strategyComboBox.setStyle("-fx-font-size: 13px;");
        
        runButton = new Button("Запустить тест");
        runButton.setOnAction(e -> runSingleTest());
        runButton.setDisable(true);
        runButton.setStyle("-fx-font-size: 13px; -fx-padding: 6 20 6 20; -fx-background-color: #5cb85c; -fx-text-fill: white; -fx-background-radius: 5; -fx-cursor: hand;");

        settingsPanel.getChildren().addAll(
            cacheSizeLabel, cacheSizeSpinner,
            strategyLabel, strategyComboBox,
            runButton
        );

        topPanel.getChildren().addAll(filePanel, settingsPanel);
        
        return topPanel;
    }
    
    private HBox createStatsPanel() {
        HBox statsPanel = new HBox(40);
        statsPanel.setPadding(new Insets(10, 20, 10, 20));
        statsPanel.setAlignment(Pos.CENTER);
        
        VBox fileInfo = createStatSection("Информация о файле");
        totalWordsLabel = createStatLabel("Всего слов: -");
        uniqueWordsLabel = createStatLabel("Уникальных слов: -");
        fileInfo.getChildren().addAll(totalWordsLabel, uniqueWordsLabel);
        
        VBox cacheInfo = createStatSection("Состояние кэша");
        cacheFillLabel = createStatLabel("Заполненность: -");
        cacheInfo.getChildren().add(cacheFillLabel);
        
        VBox testResults = createStatSection("Результаты теста");
        cacheHitsLabel = createStatLabel("Попаданий: -");
        cacheMissesLabel = createStatLabel("Промахов: -");
        hitRateLabel = createStatLabel("Процент попаданий: -");
        evictionsLabel = createStatLabel("Вытеснений: -");
        testResults.getChildren().addAll(cacheHitsLabel, cacheMissesLabel, hitRateLabel, evictionsLabel);
        
        statsPanel.getChildren().addAll(fileInfo, cacheInfo, testResults);
        
        return statsPanel;
    }
    
    private VBox createStatSection(String title) {
        VBox section = new VBox(8);
        section.setPadding(new Insets(5, 15, 5, 15));
        section.setAlignment(Pos.TOP_CENTER);
        
        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #333333;");
        
        Separator separator = new Separator();
        
        section.getChildren().addAll(titleLabel, separator);
        
        return section;
    }
    
    private Label createStatLabel(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-font-family: 'SF Mono', 'Consolas', monospace; -fx-font-size: 13px; -fx-text-fill: #555555;");
        return label;
    }
    
    private VBox createLogPanel() {
        VBox logPanel = new VBox(8);
        logPanel.setPadding(new Insets(10, 10, 15, 10));
        
        Label title = new Label("Журнал");
        title.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #333333;");
        
        logArea = new TextArea();
        logArea.setEditable(false);
        logArea.setPrefRowCount(8);
        logArea.setStyle("-fx-font-family: 'SF Mono', 'Consolas', monospace; -fx-font-size: 12px; -fx-control-inner-background: #fafafa; -fx-text-fill: #333333;");
        
        logPanel.getChildren().addAll(title, logArea);
        
        return logPanel;
    }
    
    private void selectFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Выберите текстовый файл");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Текстовые файлы", "*.txt"),
            new FileChooser.ExtensionFilter("Все файлы", "*.*")
        );
        
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            loadFile(file);
        }
    }
    
    private void loadFile(File file) {
        setUIEnabled(false);
        
        executor.submit(() -> {
            try {
                currentWords = FileWordReader.readAllWords(file.getAbsolutePath());
                
                Platform.runLater(() -> {
                    filePathField.setText(file.getAbsolutePath());
                    totalWordsLabel.setText("Всего слов: " + currentWords.size());
                    
                    long uniqueCount = currentWords.stream().distinct().count();
                    uniqueWordsLabel.setText("Уникальных слов: " + uniqueCount);
                    
                    log("Загружен файл: " + file.getName());
                    log("Всего слов: " + currentWords.size() + ", уникальных: " + uniqueCount);
                    
                    setUIEnabled(true);
                    runButton.setDisable(false);
                });
                
            } catch (IOException e) {
                Platform.runLater(() -> {
                    showError("Ошибка загрузки файла", e.getMessage());
                    setUIEnabled(true);
                });
            }
        });
    }
    
    private void runSingleTest() {
        if (currentWords == null || currentWords.isEmpty()) {
            showError("Ошибка", "Сначала загрузите файл");
            return;
        }
        
        int cacheSize = cacheSizeSpinner.getValue();
        String strategyName = strategyComboBox.getValue();
        
        setUIEnabled(false);
        log("Выполнение теста...");
        
        executor.submit(() -> {
            EvictionStrategy<String> strategy = createStrategy(strategyName);
            ObjectCache<String> cache = new ObjectCache<>(cacheSize, strategy);
            
            for (String word : currentWords) {
                cache.access(word);
            }
            
            CacheStatistics stats = cache.getStatistics();
            
            Platform.runLater(() -> {
                updateStatsDisplay(stats);
                log(String.format("Тест завершен [%s, размер=%d]: попаданий %.2f%%",
                    strategyName, cacheSize, stats.getHitRate()));
                
                setUIEnabled(true);
            });
        });
    }
    
    private EvictionStrategy<String> createStrategy(String name) {
        return switch (name) {
            case "FIFO" -> new FifoStrategy<>();
            case "LRU" -> new LruStrategy<>();
            case "RAND" -> new RandomStrategy<>();
            default -> new LruStrategy<>();
        };
    }
    
    private void updateStatsDisplay(CacheStatistics stats) {
        if (stats == null) {
            cacheFillLabel.setText("Заполненность: -");
            cacheHitsLabel.setText("Попаданий: -");
            cacheMissesLabel.setText("Промахов: -");
            hitRateLabel.setText("Процент попаданий: -");
            evictionsLabel.setText("Вытеснений: -");
        } else {
            cacheFillLabel.setText(String.format("Заполненность: %d/%d (%.1f%%)",
                stats.getCurrentSize(), stats.getMaxSize(), stats.getFillRate()));
            cacheHitsLabel.setText(String.format("Попаданий: %d", stats.getCacheHits()));
            cacheMissesLabel.setText(String.format("Промахов: %d", stats.getCacheMisses()));
            hitRateLabel.setText(String.format("Процент попаданий: %.2f%%", stats.getHitRate()));
            evictionsLabel.setText(String.format("Вытеснений: %d", stats.getEvictions()));
        }
    }
    
    private void setUIEnabled(boolean enabled) {
        loadFileButton.setDisable(!enabled);
        cacheSizeSpinner.setDisable(!enabled);
        strategyComboBox.setDisable(!enabled);
        runButton.setDisable(!enabled || currentWords == null);
    }
    
    private void log(String message) {
        String timestamp = java.time.LocalTime.now().toString().substring(0, 8);
        logArea.appendText("[" + timestamp + "] " + message + "\n");
    }
    
    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    @Override
    public void stop() {
        executor.shutdownNow();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}
