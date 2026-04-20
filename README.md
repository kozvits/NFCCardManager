# NFC Card Manager

Приложение для управления NFC картами на Android. Позволяет сканировать, сохранять и эмулировать NFC карты.

## Возможности

### Сканирование NFC карт
- Автоматическое определение NFC меток при поднесении к телефону
- Поддержка карт Mifare Classic 1K/4K
- Поддержка карт Mifare Ultrtralight
- Чтение UID карты и данных из памяти
- Сохранение карт в локальную базу данных

### Управление картами
- Просмотр списка сохранённых карт
- Удаление карт из списка
- Выбор карты для эмуляции
- Просмотр детальной информации о карте (UID, тип, дата создания)

### Эмуляция NFC карт (ключевая функция!)
- Эмуляция сохранённой карты через HCE (Host Card Emulation)
- **Автоматический запуск эмуляции** при открытии приложения, если карта выбрана
- Телефон работает как NFC метка для турникетов, домофонов и других считывателей
- Автоматический старт эмуляции при открытии экрана

## Технические детали

### Архитектура
- MVVM с использованием Hilt для внедрения зависимостей
- Clean Architecture (UI → ViewModel → UseCase → Repository)
- Kotlin Coroutines + Flow для асинхронных операций
- Room Database для локального хранения
- Jetpack Compose для UI

### Требования
- Android 8.0 (API 26) и выше
- NFC модуль на устройстве

### Используемые технологии
- Jetpack Compose + Material 3
- Navigation Compose
- Hilt (Dependency Injection)
- Room Database
- Kotlin Coroutines
- NFC API (Foreground Dispatch, HCE)

## сборка

```bash
./gradlew assembleDebug
```

APK файл будет создан по пути:
`app/build/outputs/apk/debug/app-debug.apk`

## Установка

```bash
./gradlew installDebug
```

или установите APK вручную через файловый менеджер.

## Структура проекта

```
app/src/main/java/com/nfccardmanager/
├── data/
│   ├── local/          # Room Database (DAO, Entity)
│   └── repository/    # Реализация репозитория
├── di/                # Hilt модули
├── domain/
│   ├── model/        # Доменные модели
│   ├── repository/   # Интерфейсы репозитория
│   └── usecase/      # Бизнес-логика
├── nfc/              # NFC утилиты
├── presentation/
│   ├── ui/           # Compose экраны
│   └── viewmodel/    # ViewModel-и
├── service/          # HCE Service для эмуляции
└── util/            # Утилиты
```

## Лицензия

MIT License