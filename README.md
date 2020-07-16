Библиотека позволяет легко добавить изображения в ваше приложение с камеры или из галереи. 

### Подключение

```
allprojects {
    repositories {
        maven { url 'https://jitpack.io' }
    }
}

dependencies {

    implementation 'gun0912.ted:tedpermission:X.X.X'
}
```

Обработка разрешений для Android 6.0 и выше реализована.

### Инициализация

```
photos = new Photos(Fragment)
```
или 
```
photos = new Photos(Activity)
```
    
Необходимо переопределить метод `onActivityResult` и вызвать в нем метод `onResult`

```
@Override
public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    photos.onResult(requestCode, resultCode, data);
}
```

### Открытие камеры

```
photos.takePhotoFromCamera(context);
```
    
### Открытие галереи

```
photos.selectedPhotoFromGallery(context);
```

### Открытие Intent для выбора источника 

```
photos.selectMethodOfAddingPhoto(context)
```

### Получение результата
    
Для получения результата необходимо реализовать один из двух слушателей: `PhotoResultListener`, `PhotoDifferentResultsListener`. При реализации слушателей необходимо добавить следующий код в метод `startIntentForPhoto` для `activity` или `fragment`.

```
@Override
public void startIntentForPhoto(Intent intent, int requestCode) {
    startActivityForResult(intent, requestCode);
}
```

Слушатель `PhotoResultListener` возвращает результат без разделения на источник данных. Слушатель `PhotoDifferentResultsListener` возвращает результат отдельно для камеры и галереи.

В методы слушателей возвращаются `Uri` на файл. Обратите внимание, что начиная с 29 android sdk файлы из галереи недоступны по абсолютному пути. Для доступности файла по абсолютному пути необходимо скопировать файл в директорию приложения. Рекомендуется отказаться от данного решения! Поэтому для загрузки файлов на сервер стоит отказаться от использования `File`, вместо этого использовать `byte[]`, считанный напрямую из `Uri`. Так же для отображения изображений по `Uri` рекомендуется использовать библиотеку [glide](https://github.com/bumptech/glide). Она позволяет избежать ошибок с ориентацией изображения.

### Ожидание результата

Для некоторых устройств изображение с камеры возвращается не сразу после подтверждения. Для таких случаев реализуется слушатель `PhotoWaitListener`, метод которого вызывается при старте и завершении ожидания изображения с камеры.

### Обработка ошибок

Для получения обработанной ошибки необходимо реализовать слушатель `PhotoErrorListener`.

Коды ошибок

- `FILE_PATH_NOT_FOUND` - Не указан путь к файлу
- `NO_CAMERA_ON_THE_DEVICE` - На устройстве не удалось найти камеру
- `CAN_NOT_CREATE_FILE` - Не удалось создать файл
- `INTENT_NOT_SET` - Не задан intent для обработки результата
- `PERMISSION_DENIED` - Отказано в выдаче разрешений
- `CONTEXT_NOT_FOUND` - Потерян контекст
- `FILE_NOT_FOUND` - По указанному пути не найден файл
- `EXCEPTION` - Обработанное исключение
