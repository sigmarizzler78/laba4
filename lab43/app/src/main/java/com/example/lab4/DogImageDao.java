package com.example.lab4;

import androidx.room.Dao; // интерфейс для доступа к базе данных
import androidx.room.Delete; //  метод удаляет что-то
import androidx.room.Insert; // метод добавляет что-то
import androidx.room.Query; //  метод запрашивает что-то

import java.util.List; //  для работы со списками

@Dao //  интерфейс для доступа к базе данных
public interface DogImageDao {
    @Insert // Добавить картинку в базу данных
    void insert(DogImage dogImage); // Добавляем картинку

    @Query("SELECT * FROM dog_images") // Получить все картинки из базы данных
    List<DogImage> getAll(); //  Получаем список всех картинок

    @Delete // Удалить картинку из базы данных
    void delete(DogImage dogImage); // Удаляем картинку
}
