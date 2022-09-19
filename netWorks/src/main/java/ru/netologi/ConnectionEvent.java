package ru.netologi;
/*
Напишем универсальный интерфейс, где определим шаблоны событий (методы), которые у нас могут возникнуть.
 */
public interface ConnectionEvent {

    //соединение готово (передаем само соединение чтобы у той сущности которая реализует данный интерфейс был к нему доступ)
    void ready(Connection connection);

    //прием строчки(в обработчики нам нужно узнать что за строчка)
    void intString( Connection connection,String value);

    //дисконект
    void disconnect(Connection connection);

    // событие исключение
    void exception(Connection connection, Exception exception);
    //
}
