[![Build Status](https://www.bitrise.io/app/f1ed955353e03ac0/status.svg?token=Wqb0mgqD9kUlaLhINjCOTg)](https://www.bitrise.io/app/f1ed955353e03ac0)
# Translator
Simple and fast translator. Test task for the Mobile Development School on Android, organized by Yandex. Features:
- More than 90 languages.
- Voice input and text scoring (only English, Russian, Ukrainian, Turkish).
- Saving translations to favourites.

# Screenshots
<img src="https://github.com/bacecek/translator/blob/master/img/main_clear.png" width="240" height="480"> <img src="https://github.com/bacecek/translator/blob/master/img/main.png" width="240" height="480"> <img src="https://github.com/bacecek/translator/blob/master/img/favourites.png" width="240" height="480">

# Getting started
## Clone the repository
As usual, you get started by cloning the project to your local machine:
```
https://github.com/bacecek/translator.git
```
## Build the app
Run this command in the root directory
```
./gradlew assembleDebug
```
Apk file will be in the following path:
```
../app/build/outputs/apk/debug
```
## Install the app
Connect device and run this command in the root directory
```
./gradlew installDebug
```
# Built With
- [Yandex Translator API](https://tech.yandex.ru/translate/) & [Yandex Dictionary API](https://tech.yandex.ru/dictionary/) - Used to translate everything;
- [Yandex SpeechKit](https://tech.yandex.ru/speechkit/) - Used for pronouncing text and recognizing speech;
- [Moxy](https://github.com/Arello-Mobile/Moxy) - MVP Framework;
- [Dagger 2](https://google.github.io/dagger/) - Dependency Injection;
- [Realm](https://github.com/realm/realm-java) - Database;
- [Retrofit](http://square.github.io/retrofit/) - Used for working with network;
- [RxJava 2](https://github.com/ReactiveX/RxJava) - Easy work with multithreading.

# License

MIT License

Copyright (c) 2016 Denis Buzmakov

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
