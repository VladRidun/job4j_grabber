# job4j_grabber

## Проект реализует парсер вакансий популярного сайта в рамках курса job4j.ru.
### Агрегатор вакансий:
Система запускается по расписанию - раз в минуту.  Период запуска указывается в настройках - app.properties. </br>
Первый сайт будет career.habr.com. Работаем с разделом https://career.habr.com/vacancies/java_developer.ru </br>
Программа должна считывать все вакансии c первых 5 страниц относящиеся к Java и записывать их в базу.</br>

Расширение:</br>

1. В проект можно добавить новые сайты без изменения кода.</br>

2. В проекте можно сделать параллельный парсинг сайтов.</br>
