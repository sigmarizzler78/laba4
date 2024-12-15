package com.example.lab4;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.squareup.picasso.Picasso; // Библиотека для загрузки картинок

import java.util.List; // Для работы со списками

public class SavedImagesActivity extends AppCompatActivity { // Активити для отображения сохраненных картинок

    private DogApiManager dogApiManager; // Менеджер для работы с API и базой данных
    private RecyclerView recyclerView; // Список для отображения картинок
    private DogImageAdapter adapter; // Адаптер для списка

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_images); // Устанавливаем макет активити

        recyclerView = findViewById(R.id.recyclerView); // Находим RecyclerView в макете
        recyclerView.setLayoutManager(new LinearLayoutManager(this)); // Устанавливаем менеджер компоновки
        dogApiManager = new DogApiManager(this); // Создаем менеджер
        loadSavedImages(); // Загружаем сохраненные картинки
    }

    // Загрузка сохраненных картинок из базы данных
    private void loadSavedImages() {
        new Thread(() -> { // Запускаем новый поток
            try {
                List<DogImage> dogImages = dogApiManager.getAppDatabase().dogImageDao().getAll(); // Получаем все картинки из базы данных
                runOnUiThread(() -> { // Обновляем UI в главном потоке
                    if (dogImages != null && !dogImages.isEmpty()) { // Проверяем, есть ли картинки
                        adapter = new DogImageAdapter(dogImages, this, dogApiManager); // Создаем адаптер
                        recyclerView.setAdapter(adapter); // Устанавливаем адаптер для RecyclerView
                    } else {
                        Toast.makeText(this, "Нет сохраненных изображений!", Toast.LENGTH_SHORT).show(); // Показываем сообщение
                    }
                });
            } catch (Exception e) { // Обработка ошибок
                runOnUiThread(() -> Toast.makeText(this, "Ошибка загрузки изображений: " + e.getMessage(), Toast.LENGTH_SHORT).show()); // Показываем сообщение об ошибке
                Log.e("SavedImagesActivity", "Error loading images from database", e); // Выводим лог об ошибке
            }
        }).start(); // Запускаем поток
    }
}
