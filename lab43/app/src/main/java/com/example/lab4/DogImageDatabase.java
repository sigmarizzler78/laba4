package com.example.lab4;

import androidx.room.Database; // для работы с базой данных Room
import androidx.room.RoomDatabase; //  тоже для работы с базой данных Room

//  описание базы данных для картинок собак
@Database(entities = {DogImage.class}, version = 1, exportSchema = false) //  entities - что храним (картинки собак), version - версия базы, exportSchema - неважно
public abstract class DogImageDatabase extends RoomDatabase { //  класс базы данных
    //  как получить доступ к картинкам в базе данных
    public abstract DogImageDao dogImageDao(); //  Получаем доступ к картинкам
}
