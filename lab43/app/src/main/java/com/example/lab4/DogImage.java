package com.example.lab4;

import androidx.room.Entity; // Аннотация, указывающая, что это класс сущности для Room
import androidx.room.PrimaryKey; // Аннотация, указывающая на первичный ключ

// Аннотация @Entity указывает, что этот класс представляет таблицу в базе данных Room
@Entity(tableName = "dog_images") // Имя таблицы в базе данных: "dog_images"
public class DogImage {
    // Первичный ключ, который автоматически генерируется базой данных
    @PrimaryKey(autoGenerate = true) // autoGenerate = true -  база данных сама сгенерирует значение
    public int id; // ID записи в базе данных
    // URL изображения собаки
    public String imageUrl; // URL картинки собаки

    // Конструктор класса
    public DogImage(String imageUrl) {
        this.imageUrl = imageUrl; // Инициализация поля imageUrl
    }
}
