package ru.netologi;

import java.io.FileWriter;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


/*
Требования к серверу:
        - Установка порта для подключения клиентов через файл настроек (например, settings.txt);
        - Возможность подключиться к серверу в любой момент и присоединиться к чату;
        - Отправка новых сообщений клиентам;
        - Запись всех отправленных через сервер сообщений с указанием имени пользователя и времени отправки.

Сервер должен уметь одновременно ожидать новых пользователей и обрабатывать поступающие сообщения от пользователей
*/

public class Server implements ConnectionEvent {
    protected ArrayList<Connection> clients = new ArrayList<>();
    private static int port = 8087;
    private static String directorySet = "D://коля//JAVA//ДЗ//JavaCore//jd-homeworks-kurs2-networkchat//settings.txt";
    private String logServerDirectory = "D://коля//JAVA//ДЗ//JavaCore//jd-homeworks-kurs2-networkchat//logServer.txt";


    SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
    Date date = new Date(System.currentTimeMillis());


    public static void main(String[] args) throws UnknownHostException {
        new Settings(port).saveSettings(directorySet);
        new Server().serverStart();
    }

    protected void serverStart() {
        System.out.println("Server start work");
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            /*
            Сервер постоянно отслеживает входящие соединение от нового пользователя и при появлении нового пользователя описывать логику работы через отдельный класс.
            И сервер может держать несколько входящих соединений активными, и принимать сообщения от одного пользователя и рассылать его нескольким.

            Для постоянной работы сервиса заводим бесконечный цикл while(true)
             */
            while (true) {
                try {
                    Connection connection = new Connection(serverSocket.accept(), this);
                    connection.startConnection();
                } catch (IOException e) {
                    //ловит исключения
                    System.out.println("ошибка при создании подключения: " + e);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public synchronized void ready(Connection connection) {
        clients.add(connection);
        sendMassage("Выполнено подключение" + connection.toString() + "\n");
    }

    @Override
    public synchronized void intString(Connection connection, String value) {
        sendMassage(value);
    }

    @Override
    public synchronized void disconnect(Connection connection) {
        clients.remove(connection);
        sendMassage("Отключение клиента" + connection.toString() + "\n");
    }

    @Override
    public synchronized void exception(Connection connection, Exception exception) {
        System.out.println("Connection exception: " + exception);
    }

    protected synchronized void sendMassage(String message) {
        logServer(message);
        System.out.println(formatter.format(date) + " " + message);
        for (int i = 0; i < clients.size(); i++) {
            clients.get(i).sendMessage(message);
        }
    }

    protected void logServer(String msg) {
        try (FileWriter writer = new FileWriter(logServerDirectory, true)) {
            writer.write(msg + "\n");
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

}