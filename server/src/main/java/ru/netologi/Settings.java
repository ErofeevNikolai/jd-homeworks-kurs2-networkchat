package ru.netologi;

import java.io.*;
import java.net.Inet4Address;
import java.net.UnknownHostException;


public class Settings implements Serializable {
    private static final long serialVersionUID = 1L;
    private String ip;
    private int port;

    protected Settings(int port) throws UnknownHostException {
        this.ip = Inet4Address.getLocalHost().getHostAddress();
        this.port = port;
    }

    protected void saveSettings(String directory) {
        Settings set = Settings.this;
        try (FileOutputStream files = new FileOutputStream(directory);
             ObjectOutput out = new ObjectOutputStream(files)
        ) {
            out.writeObject(set);

        } catch (Exception e) {
            System.out.println(" При сохранении настроек возникла следующая ошибка:" + e);
        }
    }
}
