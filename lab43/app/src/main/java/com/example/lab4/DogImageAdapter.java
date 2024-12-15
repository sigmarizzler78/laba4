package com.example.lab4;

import android.content.Context; // для работы с контекстом приложения
import android.view.LayoutInflater; // для "раздувания" xml-макетов
import android.view.View; // базовый класс для всех view
import android.view.ViewGroup; // базовый класс для группировки view
import android.widget.Button; // кнопка
import android.widget.ImageView; // картинка

import androidx.annotation.NonNull; // аннотация, не будем вдаваться в детали
import androidx.recyclerview.widget.RecyclerView; // для работы с RecyclerView

import com.squareup.picasso.Picasso; // для загрузки картинок

import java.util.List; // для работы со списками

public class DogImageAdapter extends RecyclerView.Adapter<DogImageAdapter.ViewHolder> { // Адаптер для RecyclerView

    private List<DogImage> dogImages; // Список картинок собак
    private Context context; // Контекст приложения
    private DogApiManager dogApiManager; // Менеджер для работы с API и базой данных

    // Конструктор адаптера
    public DogImageAdapter(List<DogImage> dogImages, Context context, DogApiManager dogApiManager) {
        this.dogImages = dogImages; // Список картинок
        this.context = context; // Контекст приложения
        this.dogApiManager = dogApiManager; // Менеджер API и базы данных
    }

    // Создает новый ViewHolder
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_dog_image, parent, false); // Создает view из макета item_dog_image
        return new ViewHolder(view, this); // Возвращает новый ViewHolder
    }

    // Привязывает данные к ViewHolder
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DogImage dogImage = dogImages.get(position); // Получает картинку из списка
        Picasso.get().load(dogImage.imageUrl).into(holder.imageView); // Загружает картинку в ImageView
        holder.bind(dogImage, position); // Привязывает данные и обработчик нажатия кнопки
    }

    // Возвращает количество элементов в списке
    @Override
    public int getItemCount() {
        return dogImages.size(); // Размер списка
    }

    // Внутренний класс ViewHolder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView; // Картинка собаки
        Button deleteButton; // Кнопка удаления
        private final DogImageAdapter adapter; // Ссылка на адаптер

        // Конструктор ViewHolder
        public ViewHolder(@NonNull View itemView, DogImageAdapter adapter) {
            super(itemView); // Вызов конструктора родительского класса
            imageView = itemView.findViewById(R.id.dogImageView); // Находим ImageView в макете
            deleteButton = itemView.findViewById(R.id.deleteButton); // Находим кнопку удаления в макете
            this.adapter = adapter; // Сохраняем ссылку на адаптер
        }

        // Привязывает данные и обработчик нажатия кнопки
        public void bind(DogImage dogImage, int position) {
            deleteButton.setOnClickListener(v -> { // Обработчик нажатия кнопки удаления
                adapter.removeItem(position); // Вызывает метод удаления элемента в адаптере
            });
        }
    }

    // Удаляет элемент из списка и базы данных
    public void removeItem(int position) {
        if (position >= 0 && position < dogImages.size()) { // Проверка корректности позиции
            DogImage dogImage = dogImages.get(position); // Получаем картинку для удаления
            new Thread(() -> { // Запускаем отдельный поток для удаления из базы данных
                try {
                    dogApiManager.getAppDatabase().dogImageDao().delete(dogImage); // Удаляем из базы данных
                } catch (Exception e) {
                    // Ошибка удаления -  не обрабатывается, но в идеале нужно добавить обработку
                }
            }).start(); // Запускаем поток
            dogImages.remove(position); // Удаляем из списка
            notifyItemRemoved(position); // Уведомляем RecyclerView об удалении элемента
            notifyItemRangeChanged(position, dogImages.size()); // Уведомляем RecyclerView об изменении диапазона
        }
    }
}
