package ru.netologi;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;


public class Client extends JFrame implements ActionListener, ConnectionEvent {
    //Создаем настройки для окна ввод/вывода чата:
    // 1. зададим размеры нашего окна ширина и высота
    private final int WIDTH = 600;
    private final int HEIGHT = 600;
    // 2. создаем поле в котором будет отображаться переписка в чате
    private final JTextArea log = new JTextArea();
    // 3. создаем ПОЛЕ с никнеймом
    private final JTextField fieldNikName = new JTextField();
    // 4. создаем ПОЛЕ с текстом отправляемого сообщения
    protected JTextField filedInput = new JTextField();
    //5. путь к файлу с настройками
    private final String directorySet = "D://коля//JAVA//ДЗ//JavaCore//jd-homeworks-kurs2-networkchat//settings.txt";
    //6. путь к файлу с историей сообщений полученных клиентом
    private final String directoryHistory = "D://коля//JAVA//ДЗ//JavaCore//jd-homeworks-kurs2-networkchat//History.txt";
    // 7. создаем соединение
    protected Connection connection;


    public static void main(String[] args) {
        new Client().startClient();
    }

    public void startClient() {
        //Получаем настройки для соединения с сервером
        Settings set = getSettings(directorySet);

        //Настраиваем выспевающие окно чата:
        //задаем логику работы программы при закрытии окна чата крестиком
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        //устанавливаем размер окна
        setSize(WIDTH, HEIGHT);
        //уст вывод окна на середину раб стола
        setLocationRelativeTo(null);
        //запрещаем редактирование поля с выводом сообщений
        log.setEditable(false);
        //поле вывода сообщений из чата по центу
        add(log, BorderLayout.CENTER);
        //поле ввода снизу
        add(filedInput, BorderLayout.SOUTH);
        //поле никнейма снизу
        add(fieldNikName, BorderLayout.NORTH);
        //говорим ActionListener чтобы он следил за полем вода (filedInput) передав методу себя
        filedInput.addActionListener(this);
        // делаем окно видимым
        setVisible(true);

        //Создание соединения нашего окна с сервером.
        try {
            connection = new Connection(set.ip, set.port, this);
            connection.startConnection();
        } catch (IOException e) {
            printMessage("Ошибка подключения: " + e);
        }
    }

    //Логика работы программы при нажатии на кнопку интер
    @Override
    public void actionPerformed(ActionEvent e) {
        //Получение набранного текста
        String msg = filedInput.getText();
        //Проверка строки на пустоту("") и на команду выход
        if (msg.equals("")) {
            return;
        } else if (msg.equals("/exit")) {
            System.exit(0);
        }
        //очистка поля ввода сообщения
        filedInput.setText(null);
        //передача считанных ранее данных для отправки на сервер
        connection.sendMessage("[" + fieldNikName.getText() + "]: " + msg);
    }

    // уведомление на экран чата при удачном подключении
    @Override
    public void ready(Connection connection) {
        printMessage("Соединение выполнено");
    }

    //печатаем принятую строчку
    @Override
    public void intString(Connection connection, String value) {
        printMessage(value);
    }

    // уведомление на экран чата при разрыве соединения
    @Override
    public void disconnect(Connection connection) {
        printMessage("Соединение прервано");
    }

    @Override
    public void exception(Connection connection, Exception exception) {
        printMessage("Ошибка подключения: " + exception);
    }

    //Метод, который пишет в наше текстовое поле
    //он используется из разных потоков. Потоки, которые выводят отчеты, и потоки которые отображают сообщение - требует синхронизации
    private synchronized void printMessage(String msg) {
        saveHistory(msg);
        log.append(msg + "\n");
    }

    //метод считывающий настройки с сервера.
    private synchronized Settings getSettings(String directory) {
        Settings settings = null;
        try (FileInputStream fis = new FileInputStream(directory);
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            settings = (Settings) ois.readObject();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return settings;
    }

    // метод сохраняющий строки
    private synchronized void saveHistory(String msg) {
        try (FileWriter writer = new FileWriter(directoryHistory, true)) {
            writer.write(msg + "\n");
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

}