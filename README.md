[![](https://jitpack.io/v/Ringo-Freek/Photos.svg)](https://jitpack.io/#Ringo-Freek/Photos)

Позволяет:
* добавлять фотографии с камеры
* добавлять изображения из галереи
* сохранять файлы под заданным именем в стандартных папках для MEDIA
* изменять размер фотографии
* обрезать под квадрат
* поворачивать
* поучать облегченную версию изображения из оригинального файла

### Подключение

    allprojects {
	    repositories {
			maven { url 'https://jitpack.io' }
		}
	}
    
    dependencies {
	    implementation 'com.github.Ringo-Freek.Photos:photos:vX.X.X'
        implementation 'gun0912.ted:tedpermission:X.X.X'
	}

Обработка разрешений для Android 6.0 и выше реализована.

### Объявление

    photos = new Photos(Fragment)

или 

    photos = new Photos(Activity)
    
Необходимо переобпределить метод onActivityResult и вызвать в нем метод onResult

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        photos.onResult(requestCode, resultCode, data);
    }
    
### Реализуемые интерфесы
    
Слушатель на получение результата (абсолютного пути к файлу)

    PhotoResultListener

Слушатель на получение сообщений о возникших ошибках

    PhotoErrorListener

В метод подается код ошибки. Все коды ошибок описаны в ErrorCodes

    onError(int errorCode)

Слушатель на начало и конец длительных операций при получение пукти к файлу

    PhotoWaitListener

При реализации PhotoResultListener интерфейсов необходимо добавить следующий код в метод startIntentForPhoto для activity или fragment взависмости, где необходмо отловить результат

    startActivityForResult(intent, requestCode);

Пример реализации интерфейса PhotoResultListener

    @Override
    public void getPath(String path) {
        // Например, отобразить фотографию в ImageView
    }

    @Override
    public void startIntentForPhoto(Intent intent, int requestCode) {
        startActivityForResult(intent, requestCode);
    }

Слушатель на получение результата (абсолютного пути к файлу). Резльтат возвращается для камеры и галереи в отдельных методах.

    PhotoDifferentResultsListener

Пример реализации интерфейса PhotoDifferentResultsListener

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

### Открытие камеры

    photos.takePhotoFromCamera();
    
### Открытие галереи

    photos.selectedPhotoFromGallery();
