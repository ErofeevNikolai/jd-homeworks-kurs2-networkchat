package ru.netologi;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;

import javax.swing.*;
import java.awt.event.ActionEvent;

import static org.junit.jupiter.api.Assertions.*;

class ClientTest {
    Client client;

    @BeforeEach
    void clientStart() {
        client = new Client();
    }


//    @Test
//    void actionPerformedWhenWhenExit() {
//        ActionEvent actionMock = Mockito.mock(ActionEvent.class);
//        client.filedInput = Mockito.mock(JTextField.class);
//        Mockito.when(client.filedInput.getText()).thenReturn(testText);
//        System system = Mockito.spy(System.class);
//        Mockito.doNothing().when(system).exit(1);
//        client.connection = Mockito.mock(Connection.class);
//        Mockito.doNothing().when(client.connection).sendMessage(Mockito.anyString());
//
//        //запуск метода
//        client.actionPerformed(actionMock);
//
//        //проверка результата
//        Mockito.verify(system, Mockito.times(1)).exit(Mockito.any());
//    }

    @ParameterizedTest(name = "{index} - {0} is a palindrome")
    @ValueSource(strings = {"", "test"})
    void actionPerformed(String testText) {
        /*
        Определение заглушек
        1. для ActionEvent
        2. для filedInput
            2.1 переопределяем метод getText();
            2.2 переопределяем метод setText();
        3. для Connection
            3.1 переопределяем метод sendMessage();
         */
        ActionEvent actionMock = Mockito.mock(ActionEvent.class);
        client.filedInput = Mockito.mock(JTextField.class);
        Mockito.when(client.filedInput.getText()).thenReturn(testText);
        Mockito.doNothing().when(client.filedInput).setText(Mockito.any());
        client.connection = Mockito.mock(Connection.class);
        Mockito.doNothing().when(client.connection).sendMessage(Mockito.any());

        //запуск метода
        client.actionPerformed(actionMock);

        //проверка результата
        if (testText.equals("")) {
            Mockito.verify(client.connection, Mockito.times(0)).sendMessage(Mockito.any());
        } else {
            Mockito.verify(client.connection, Mockito.times(1)).sendMessage(Mockito.any());
        }
    }

    @Test
    void ready() {
    }

    @Test
    void intString() {
    }

    @Test
    void disconnect() {
    }

    @Test
    void exception() {
    }
}