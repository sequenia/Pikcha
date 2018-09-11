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

Интерфейс, возвращающий абсолютный путь к изображению, сделанному с камеры

    ResultFromCamera
    
Интерфейс, возвращающий абсолютный путь к изображению, выбранному из галереи

    ResultFromGallery

Интерфейс, возвращающий обработанные ошибки при работе с камерой и галерей

    PhotoErrors

Интерфейс, сообщающий о том, что начинается длительная операция, пользователю необходимо подождать завершения. Такая ситуация редкая, но встречается на некоторых моделях при добавлении фотографий с камеры. Файл уже создан, результат вернулся в intent, но сама фотография еще не записана в файл. Иногда запись может занять от 1-15 секунд.

    PhotoWait

При реализации ResultFromCamera и ResultFromGallery интерфейсов необходимо добавить следующий код в метод startIntentForPhoto для activity или fragment взависмости, где необходмо отловить результат

    startActivityForResult(intent, requestCode);

Пример реализации интерфейса ResultFromGallery

    @Override
    public void getPathFromGallery(String path) {
        // Например, отобразить фотографию в ImageView
    }

    @Override
    public void startIntentForPhoto(Intent intent, int requestCode) {
        startActivityForResult(intent, requestCode);
    }
    
### Открытие камеры

    photos.takePhotoFromCamera();
    
### Открытие галереи

    photos.selectedPhotoFromGallery(false);

Параметр отвечает за множественный выбор фотографии (false - выбирается один элемент)
