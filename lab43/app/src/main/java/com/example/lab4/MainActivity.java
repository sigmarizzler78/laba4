package com.example.lab4;

import androidx.appcompat.app.AppCompatActivity; // Основной класс для активити
import android.content.Intent; // Для перехода между активити
import android.os.Bundle; // Для работы с данными активити
import android.widget.Button; // Кнопка
import android.widget.ImageView; // Картинка
import android.widget.Toast; // Для показа сообщений

public class MainActivity extends AppCompatActivity implements DogApiManager.OnImageLoadedListener { // Главное окно приложения

    private ImageView dogImageView; // Картинка собаки
    private DogApiManager dogApiManager; // Менеджер для работы с API и базой данных
    private Button getDogButton; // Кнопка "Получить картинку"
    private Button saveDogButton; // Кнопка "Сохранить картинку"
    private Button showSavedButton; // Кнопка "Показать сохраненные картинки"
    private String currentImageUrl; // URL текущей картинки

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // Устанавливаем макет главного окна

        dogImageView = findViewById(R.id.dogImageView); // Находим ImageView в макете
        getDogButton = findViewById(R.id.getDogButton); // Находим кнопку "Получить картинку"
        saveDogButton = findViewById(R.id.saveDogButton); // Находим кнопку "Сохранить картинку"
        showSavedButton = findViewById(R.id.showSavedButton); // Находим кнопку "Показать сохраненные картинки"
        dogApiManager = new DogApiManager(this); // Создаем менеджер
        dogApiManager.setOnImageLoadedListener(this); // Устанавливаем слушателя для получения URL картинки

        // Обработчик нажатия кнопки "Получить картинку"
        getDogButton.setOnClickListener(v -> dogApiManager.getRandomDogImage(dogImageView));

        // Обработчик нажатия кнопки "Сохранить картинку"
        saveDogButton.setOnClickListener(v -> {
            if (currentImageUrl != null) { // Проверяем, загружена ли картинка
                dogApiManager.saveDogImage(currentImageUrl); // Сохраняем картинку
                Toast.makeText(this, "Изображение сохранено!", Toast.LENGTH_SHORT).show(); // Показываем сообщение
            } else {
                Toast.makeText(this, "Сначала загрузите изображение!", Toast.LENGTH_SHORT).show(); // Показываем сообщение
            }
        });

        // Обработчик нажатия кнопки "Показать сохраненные картинки"
        showSavedButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, SavedImagesActivity.class); // Создаем намерение для перехода на другую активити
            startActivity(intent); // Запускаем другую активити
        });
    }

    // Метод, который вызывается после загрузки картинки
    @Override
    public void onImageLoaded(String imageUrl) {
        currentImageUrl = imageUrl; // Сохраняем URL картинки
    }
}
