# Zebra Pack Scanner - первая версия

Это исходный проект Android-приложения для терминалов Zebra. Сейчас он принимает штрихкод через Zebra DataWedge, показывает номер пачки и ведёт локальную историю. IdoSell API пока намеренно не подключён.

## Подключение Zebra DataWedge

1. Установить APK и открыть приложение хотя бы один раз.
2. Открыть DataWedge на терминале.
3. Создать профиль `Zebra Pack Scanner`.
4. В `Associated apps` добавить пакет:
   `pl.shishasklep.zebrapackscanner`
5. Включить `Barcode input`.
6. Включить `Keystroke output` можно выключить - приложение получает данные через Intent.
7. Включить `Intent output`.
8. Установить:
   - Intent action: `pl.shishasklep.zebrapackscanner.SCAN`
   - Intent category: `android.intent.category.DEFAULT`
   - Intent delivery: `Broadcast intent`

После этого физическая кнопка сканера должна передавать код в приложение.

## Сборка APK

Открыть папку проекта в Android Studio (Hedgehog или новее), дождаться синхронизации Gradle и выбрать:

`Build` -> `Build Bundle(s) / APK(s)` -> `Build APK(s)`

APK появится в:
`app/build/outputs/apk/debug/app-debug.apk`

Требования:
- Android 6.0+ (minSdk 23)
- Zebra DataWedge
- Для старых терминалов Zebra с Android 8.1 совместимость предусмотрена.

## Что пока не работает

- Поиск номера пачки в IdoSell.
- Получение связанного заказа.
- Изменение статуса заказа.

Эти функции добавим после получения API и уточнения того, где именно IdoSell хранит номер пачки.
