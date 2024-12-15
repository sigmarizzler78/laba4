package com.example.lab4;

import android.content.Context;
import android.widget.ImageView;
import android.widget.Toast; // Для показа сообщений

import androidx.room.Room; // Для работы с Room базой данных

import com.android.volley.Request; // Тип запроса (GET, POST и т.д.)
import com.android.volley.RequestQueue; // Очередь для сетевых запросов
import com.android.volley.Response; // Обработка ответа от сервера
import com.android.volley.VolleyError; // Обработка ошибок от сервера
import com.android.volley.toolbox.JsonObjectRequest; // Тип запроса для JSON
import com.android.volley.toolbox.Volley; // Для создания очереди запросов
import com.squareup.picasso.Callback; // Для обратного вызова при загрузке изображения
import com.squareup.picasso.Picasso; // Для загрузки изображений

import org.json.JSONException; // Обработка исключений JSON
import org.json.JSONObject; // Для работы с JSON

public class DogApiManager {

    // Адрес API для получения случайной картинки собаки
    private static final String DOG_API_URL = "https://dog.ceo/api/breeds/image/random";
    private RequestQueue requestQueue; // Объект для отправки сетевых запросов
    private Context context; // Контекст приложения (нужен для Toast и Room)
    private DogImageDatabase appDatabase; // База данных для хранения ссылок на картинки
    private OnImageLoadedListener listener; //  Слушатель, который будет уведомлен о загрузке картинки

    // Интерфейс для уведомления о загрузке картинки
    public interface OnImageLoadedListener {
        void onImageLoaded(String imageUrl); // Метод, который вызовется после загрузки
    }

    public DogApiManager(Context context) {
        this.context = context; // Сохранение контекста
        requestQueue = Volley.newRequestQueue(context); // Создание очереди запросов
        appDatabase = Room.databaseBuilder(context, DogImageDatabase.class, "dog_database") // Создание базы данных Room
                .fallbackToDestructiveMigration() //  ВНИМАНИЕ!  Только для разработки! Удаляет базу при изменении схемы.  В продакшене использовать миграции!
                .build(); // Построение базы данных
    }

    // Установка слушателя для уведомлений о загрузке картинки
    public void setOnImageLoadedListener(OnImageLoadedListener listener) {
        this.listener = listener; // Сохранение слушателя
    }

    // Загрузка случайной картинки собаки и отображение её в ImageView
    public void getRandomDogImage(ImageView imageView) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest( // Создание запроса на получение JSON
                Request.Method.GET, DOG_API_URL, null, // GET запрос по указанному адресу
                response -> { // Что делать, если запрос прошел успешно
                    try {
                        String imageUrl = response.getString("message"); // Получаем ссылку на картинку из ответа
                        Picasso.get().load(imageUrl).into(imageView, new Callback() { // Загружаем картинку с помощью Picasso
                            @Override
                            public void onSuccess() { // Если загрузка прошла успешно
                                if (listener != null) { // Если есть слушатель
                                    listener.onImageLoaded(imageUrl); // Уведомляем слушателя о загрузке
                                }
                            }

                            @Override
                            public void onError(Exception e) { // Если произошла ошибка
                                showError("Ошибка загрузки изображения: " + e.getMessage()); // Показываем сообщение об ошибке
                            }
                        });
                    } catch (JSONException e) { // Если произошла ошибка обработки JSON ответа
                        showError("Ошибка обработки ответа: " + e.getMessage()); // Показываем сообщение об ошибке
                    }
                },
                error -> showError("Ошибка загрузки изображения: " + error.getMessage()) // Что делать, если запрос завершился с ошибкой
        );
        requestQueue.add(jsonObjectRequest); // Отправляем запрос
    }

    // Сохранение ссылки на картинку в базе данных
    public void saveDogImage(String imageUrl) {
        if (imageUrl == null || imageUrl.isEmpty()) { // Проверка, есть ли ссылка
            showError("Изображение еще не загружено!"); // Если нет, показываем сообщение
            return; // Выходим из метода
        }
        DogImage dogImage = new DogImage(imageUrl); // Создаем объект DogImage
        new Thread(() -> { // Запускаем новый поток для сохранения, чтобы не блокировать основной
            try {
                appDatabase.dogImageDao().insert(dogImage); // Сохраняем в базу данных
            } catch (Exception e) { // Обработка возможных ошибок
                showError("Ошибка сохранения изображения в базу данных: " + e.getMessage()); // Показываем сообщение об ошибке
            }
        }).start(); // Запускаем поток
    }

    // Показ сообщения об ошибке с помощью Toast
    private void showError(String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show(); // Показываем сообщение
    }

    // Возвращает объект базы данных
    public DogImageDatabase getAppDatabase() {
        return appDatabase; // Возвращает объект базы данных
    }
}
