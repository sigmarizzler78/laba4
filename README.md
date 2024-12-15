# Лабораторная работа №4. Взаимодействие с сервером.
Студент: Перов Иван, ИСП-221С

Приложение использует API  https://dog.ceo/dog-api/ для загрузки случайных изображений собак.  Пользователь может загрузить изображение, сохранить его в локальную базу данных Room и просмотреть сохраненные изображения в отдельном списке.

## 1. Обзор MainActivity

MainActivity отвечает за:

• Взаимодействие с пользователем (нажатие кнопок "Рандомная собачка", "Сохранить", "Показать сохраненные").

• Отправку запросов к API https://dog.ceo/dog-api/ и обработку ответов через DogApiManager.

• Взаимодействие с базой данных Room (косвенно, через DogApiManager).

• Обновление пользовательского интерфейса (UI).


## 2. Элементы UI

Экран MainActivity содержит следующие элементы:

• ImageView (dogImageView): Отображение загруженного изображения собаки.

• Button (getDogButton): Кнопка для загрузки случайного изображения. Обработчик клика: dogApiManager.getRandomDogImage(dogImageView);

• Button (saveDogButton): Кнопка для сохранения текущего изображения в базу данных. Обработчик клика: dogApiManager.saveDogImage(currentImageUrl);

• Button (showSavedButton): Кнопка для перехода к экрану сохраненных изображений. Запускает SavedImagesActivity.


## 3. Обработка ввода пользователя (Загрузка изображения)

При нажатии кнопки "Рандомная собачка" (getDogButton):

1. DogApiManager выполняет GET-запрос к https://dog.ceo/api/breeds/image/random с помощью Volley. (см. код DogApiManager.getRandomDogImage)
2. Полученный JSON-ответ парсится, извлекается URL изображения.
3. Изображение загружается с помощью Picasso и отображается в dogImageView.
4. URL изображения сохраняется в переменной currentImageUrl в MainActivity через OnImageLoadedListener.
```
public void getRandomDogImage(ImageView imageView) {
    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
            Request.Method.GET, DOG_API_URL, null,
            response -> {
                try {
                    String imageUrl = response.getString("message");
                    Picasso.get().load(imageUrl).into(imageView, new Callback() {
                        @Override
                        public void onSuccess() {
                            if (listener != null) {
                                listener.onImageLoaded(imageUrl);
                            }
                        }

                        @Override
                        public void onError(Exception e) {
                            showError("Ошибка загрузки изображения: " + e.getMessage());
                        }
                    });
                } catch (JSONException e) {
                    showError("Ошибка обработки ответа: " + e.getMessage());
                }
            },
            error -> showError("Ошибка загрузки изображения: " + error.getMessage())
    );
    requestQueue.add(jsonObjectRequest);
}
```
## 5. Взаимодействие с базой данных Room

• Сохранение изображения (saveDogImage в DogApiManager):
```
public void saveDogImage(String imageUrl) {
    if (imageUrl == null || imageUrl.isEmpty()) {
        showError("Изображение еще не загружено!");
        return;
    }
    DogImage dogImage = new DogImage(imageUrl);
    new Thread(() -> {
        try {
            appDatabase.dogImageDao().insert(dogImage);
        } catch (Exception e) {
            showError("Ошибка сохранения изображения в базу данных: " + e.getMessage());
        }
    }).start();
}
```
• Загрузка сохраненных изображений (в SavedImagesActivity):
```
private void loadSavedImages() {
    new Thread(() -> {
        try {
            List<DogImage> dogImages = dogApiManager.getAppDatabase().dogImageDao().getAll();
            runOnUiThread(() -> {
                if (dogImages != null && !dogImages.isEmpty()) {
                    adapter = new DogImageAdapter(dogImages, this, dogApiManager);
                    recyclerView.setAdapter(adapter);
                } else {
                    Toast.makeText(this, "Нет сохраненных изображений!", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            runOnUiThread(() -> Toast.makeText(this, "Ошибка загрузки изображений: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            Log.e("SavedImagesActivity", "Error loading images from database", e);
        }
    }).start();
}
```
• Удаление изображения (в DogImageAdapter.removeItem):
```
public void removeItem(int position) {
    if (position >= 0 && position < dogImages.size()) {
        DogImage dogImage = dogImages.get(position);
        new Thread(() -> {
            try {
                dogApiManager.getAppDatabase().dogImageDao().delete(dogImage);
            } catch (Exception e) {
                // Обработка исключений - вывести сообщение об ошибке
            }
        }).start();
        dogImages.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, dogImages.size());
    }
}
```
## 6. Использование Volley и адаптера

• Volley: Используется в DogApiManager для выполнения сетевых запросов.

• Picasso: Используется в DogApiManager для загрузки и отображения изображений.

• DogImageAdapter: Адаптер RecyclerView в SavedImagesActivity обрабатывает отображение списка и удаление элементов.

## 7. Управление состоянием приложения

Ошибки и сообщения отображаются через Toast в DogApiManager и SavedImagesActivity.

## 8. Обработка событий

Обработка нажатий кнопок в MainActivity происходит через setOnClickListener и вызовы методов DogApiManager.

## Сборка проекта:

1. Клонирование репозитория: Клонируйте репозиторий с помощью git clone [URL репозитория].
2. Открытие проекта: Откройте проект в Android Studio.
3. Запуск приложения: Нажмите кнопку "Run" в Android Studio.
