#Pikcha

Позволяет:
* добавлять фотографии с камеры
* добавлять изображения из галереи
* сохранять файлы под заданным именем в стандартных папках для MEDIA
* изменять размер фотографии
* обрезать под квадрат
* поворачивать
* поучать облегченную версию изображения из оригинального файла

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

### Объявление

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

### Реализуемые интерфесы
    
`PhotoResultListener` - Слушатель на получение результата (абсолютного пути к файлу). При реализации интерфейса `PhotoResultListener` необходимо добавить следующий код в метод `startIntentForPhoto` для `activity` или `fragment` взависмости, где необходмо отловить результат
```
@Override
public void getPath(String path) {
    // Например, отобразить фотографию в ImageView
}

@Override
public void startIntentForPhoto(Intent intent, int requestCode) {
    startActivityForResult(intent, requestCode);
}
```
`PhotoErrorListener` - Слушатель на получение сообщений о возникших ошибках. В метод `onError(int errorCode)` подается код ошибки. Все коды ошибок описаны в `ErrorCodes`

`PhotoWaitListener` - Слушатель на начало и конец длительных операций при получение пукти к файлу

`PhotoDifferentResultsListener` - Слушатель на получение результата (абсолютного пути к файлу). Резльтат возвращается для камеры и галереи в отдельных методах. Пример реализации интерфейса `PhotoDifferentResultsListener`

```
@Override
public void getPathFromGallery(String path) {
   // Например, отобразить фотографию в ImageView
}

@Override
public void getPathFromCamera(String path) {
   // Например, отобразить фотографию в ImageView
}

@Override
public void startIntentForPhoto(Intent intent, int requestCode) {
   startActivityForResult(intent, requestCode);
}
```

### Открытие камеры

```
photos.takePhotoFromCamera();
```
    
### Открытие галереи

```
photos.selectedPhotoFromGallery();
```