package ru.netologi;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;

import java.io.*;
import java.net.Socket;

import static org.junit.jupiter.api.Assertions.*;

class ConnectionTest {
    Connection connection;
    ByteArrayOutputStream out;


    /*
        1. Для создания объекта создаем заглушки передаваемы в него
            1.1 заглушка интерфейса ConnectionEvent
            1.2 заглушка сокета
                1.2.1 у сокета подменяем значение возвращаемое вызовом метода .getOutputStream()
                     1.2.1.1 создаем экземпляр класса ByteArrayOutputStream
                1.2.2 у сокета подменяем значение возвращаемое вызовом метода .getInputStream()
                    1.2.2.1 создаем экземпляр класса ByteArrayInputStream

        2. Создаем объект, метод которого тестируем
    */
    @BeforeEach
    void connectionStart() throws IOException {
        Socket socketMock = Mockito.mock(Socket.class);
        ConnectionEvent connectionEvent = Mockito.mock(ConnectionEvent.class);
        out = new ByteArrayOutputStream();
        Mockito.when(socketMock.getOutputStream()).thenReturn(out);
        ByteArrayInputStream in = Mockito.mock(ByteArrayInputStream.class);
        Mockito.when(socketMock.getInputStream()).thenReturn(in);
        connection = new Connection(socketMock, connectionEvent);
    }


    @org.junit.jupiter.api.Test
    void sendMessage() throws IOException {
        // объект создан перед тестом

        //метод
        connection.sendMessage("test");
        //результат
        assertEquals("test" + "\r\n", out.toString());
    }

    @org.junit.jupiter.api.Test
    void exit() throws IOException {
        //
        connection.TREAD = Mockito.mock(Thread.class);
        Mockito.when(connection.TREAD.isInterrupted()).thenReturn(true);

        connection.exit();

        Mockito.verify(connection.SOСKED, Mockito.times(1)).close();
    }

}