package ru.netologi;

import java.io.*;
import java.net.Socket;
import java.nio.charset.Charset;

/*
Класс которые будет реализовывать логику работы соединения(взаимодействия сервиса с 1 клиентом)
 */
public class Connection implements Runnable{

    protected final Socket SOСKED;                            // сокет самого соединения (вроде как пользователя - уточнить )
    protected Thread TREAD;                                   // будет постоянно читать поток ввода
    protected BufferedReader BUFFERED_READER;           //
    protected BufferedWriter BUFFERED_WRITER;           //
    private final ConnectionEvent CONNECTION_EVENT;         // нас слушатель событий которому передаем какое событие

    //Конструктор для клиента(из которого мы вызываем конструктор для сервера)
    public Connection(String ipAddress, int port, ConnectionEvent connectionEvent) throws IOException {
        this(new Socket(ipAddress, port), connectionEvent);
    }

    //Конструктор для сервера
    public Connection(Socket socket, ConnectionEvent connectionEvent) throws IOException {
        this.SOСKED = socket;
        this.CONNECTION_EVENT = connectionEvent;
        this.BUFFERED_READER = new BufferedReader(new InputStreamReader(SOСKED.getInputStream(), Charset.forName("UTF-8")));
        this.BUFFERED_WRITER = new BufferedWriter(new OutputStreamWriter(SOСKED.getOutputStream(), Charset.forName("UTF-8")));
        this.TREAD = new Thread(this);
    }

    public void startConnection() throws IOException {
        TREAD.start();
    }


    public synchronized void sendMessage(String message) {
        try {
            BUFFERED_WRITER.write(message + "\r\n");
            BUFFERED_WRITER.flush();
        } catch (IOException e) {
            CONNECTION_EVENT.exception(Connection.this, e);
            exit();
        }
    }

    public synchronized void exit() {
        TREAD.isInterrupted();
        try {
            SOСKED.close();
        } catch (IOException e) {
            CONNECTION_EVENT.exception(Connection.this, e);
        }
    }

    @Override
    public String toString(){
        return SOСKED.getInetAddress() + ":" + SOСKED.getPort();
    }

    @Override
    public void run() {
        try {
            //При старте потока сообщаем об этом слушателю событий
            CONNECTION_EVENT.ready(Connection.this);

            //Далее требуется получать строчки. Для этого запускаем бесконечный цикл
            while (!TREAD.isInterrupted()) {
                //получаем строчку из потока
                String massage = BUFFERED_READER.readLine();
                // отдаем ее слушателю
                CONNECTION_EVENT.intString(Connection.this, massage);
            }
        } catch (IOException e) {
            CONNECTION_EVENT.exception(Connection.this, e);
        } finally {
            CONNECTION_EVENT.disconnect(Connection.this);
        }
    }
}
