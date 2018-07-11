Модуль для добавления фотографий с камеры и из галереи. Обработка разрешений для Android 6.0 и выше реализована.

### Подключение

    allprojects {
	    repositories {
			maven { url 'https://jitpack.io' }
		}
	}
    
    dependencies {
	    compile 'com.github.Ringo-Freek:Photos:v2.0'
	}
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

При реализации данных интерфейсов необходимо добавить следующий код в метод startIntentForPhoto для activity или fragment взависмости, где необходмо отловить результат

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
