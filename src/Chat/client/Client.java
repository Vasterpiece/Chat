package Chat.client;

import Chat.Connection;
import Chat.ConsoleHelper;
import Chat.Message;
import Chat.MessageType;

import java.io.IOException;

public class Client {
    protected Connection connection;
    private volatile boolean clientConnected = false;

    protected String getServerAddress() {
        ConsoleHelper.writeMessage("Введите адрес сервера.");
        return ConsoleHelper.readString();
    }

    public class SocketThread extends Thread {

    }

    protected int getServerPort() {
        ConsoleHelper.writeMessage("Введите номер порта.");
        return ConsoleHelper.readInt();
    }

    protected String getUserName() {
        ConsoleHelper.writeMessage("Введите имя пользователя.");
        return ConsoleHelper.readString();
    }

    protected boolean shouldSentTextFromConsole() {
        return true;
    }

    protected SocketThread getSocketThread() {
        return new SocketThread();
    }

    protected void sendTextMessage(String text) {
        try {
            connection.send(new Message(MessageType.TEXT, text));
        } catch (IOException e) {
            clientConnected = false;
            ConsoleHelper.writeMessage("Соединение с сервером прервано.");
        }
    }

    public void run() {

    }
}
/*Приступим к написанию главного функционала класса Client.
14.1.	Добавь метод void run(). Он должен создавать вспомогательный поток
SocketThread, ожидать пока тот установит соединение с сервером, а после этого
в цикле считывать сообщения с консоли и отправлять их серверу. Условием выхода
из цикла будет отключение клиента или ввод пользователем команды 'exit'.
Для информирования главного потока, что соединение установлено во
вспомогательным потоке, используй методы wait и notify объекта класса Client.
Реализация метода run должна:
14.1.1.	Создавать новый сокетный поток с помощью метода getSocketThread.
14.1.2.	Помечать созданный поток как daemon, это нужно для того, чтобы при выходе
из программы вспомогательный поток прервался автоматически.
14.1.3.	Запустить вспомогательный поток.
14.1.4.	Заставить текущий поток ожидать, пока он не получит нотификацию из другого
потока. Подсказка: используй wait и синхронизацию на уровне объекта. Если во
время ожидания возникнет исключение, сообщи об этом пользователю и выйди
из программы.
14.1.5.	После того, как поток дождался нотификации, проверь значение
clientConnected. Если оно true – выведи "Соединение установлено. Для выхода
наберите команду 'exit'.". Если оно false – выведи "Произошла ошибка во время
работы клиента.".
14.1.6.	Считывай сообщения с консоли пока клиент подключен. Если будет введена
команда 'exit', то выйди из цикла.
14.1.7.	После каждого считывания, если метод shouldSentTextFromConsole()
возвращает true, отправь считанный текст с помощью метода  sendTextMessage().
14.2.	Добавь метод main(). Он должен создавать новый объект класса Client и
вызывать у него метод run().*/