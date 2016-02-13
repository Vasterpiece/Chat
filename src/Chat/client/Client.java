package Chat.client;

import Chat.Connection;
import Chat.ConsoleHelper;
import Chat.Message;
import Chat.MessageType;

import java.io.IOException;
import java.net.Socket;

public class Client {
    protected Connection connection;
    private volatile boolean clientConnected = false;

    public static void main(String[] args) {
        Client client = new Client();
        client.run();
    }

    protected String getServerAddress() {
        ConsoleHelper.writeMessage("Введите адрес сервера.");
        return ConsoleHelper.readString();
    }

    public class SocketThread extends Thread {
        protected void processIncomingMessage(String message) {
            ConsoleHelper.writeMessage(message);
        }

        protected void informAboutAddingNewUser(String userName) {
            ConsoleHelper.writeMessage("Пользователь: " + userName + ". Присоединилс к чату.");
        }

        protected void informAboutDeletingNewUser(String userName) {
            ConsoleHelper.writeMessage("Пользователь: " + userName + " покинул чат.");
        }

        protected void notifyConnectionStatusChanged(boolean clientConnected) {
            Client.this.clientConnected = clientConnected;
            synchronized (Client.this) {
                Client.this.notify();
            }
        }

        protected void clientHandshake() throws IOException, ClassNotFoundException {
            while (true) {
                Message message = Client.this.connection.receive();
                if (message.getType() == MessageType.NAME_REQUEST)
                    connection.send(new Message(MessageType.USER_NAME, getUserName()));
                else if (message.getType() == MessageType.NAME_ACCEPTED) {
                    notifyConnectionStatusChanged(true);
                    break;
                } else
                    throw new IOException("Unexpected MessageType");
            }
        }

        protected void clientMainLoop() throws IOException, ClassNotFoundException {
            while (true) {
                Message message = connection.receive();
                if (message.getType() == MessageType.TEXT)
                    processIncomingMessage(message.getData());
                else if (message.getType() == MessageType.USER_ADDED) {
                    if (message.getData() != null)
                    informAboutAddingNewUser(message.getData());
                }
                else if (message.getType() == MessageType.USER_REMOVED)
                    informAboutDeletingNewUser(message.getData());
                else
                    throw new IOException("Unexpected MessageType");
            }
        }

        @Override
        public void run() {
            String serverAddress = getServerAddress();
            int portNumber = getServerPort();
            try (Socket socket = new Socket(serverAddress, portNumber)) {
                Client.this.connection = new Connection(socket);
                clientHandshake();
                clientMainLoop();
            } catch (Exception e) {
                notifyConnectionStatusChanged(false);
            }

        }
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
        SocketThread socketThread = getSocketThread();
        socketThread.setDaemon(true);
        socketThread.start();
        synchronized (this) {
            try {
                this.wait();
            } catch (InterruptedException e) {
                System.out.println("Произошла ощибка при вызове метода wait");
                System.exit(-1);
            }
        }
        if (clientConnected)
            ConsoleHelper.writeMessage("Соединение установлено. Для выхода наберите команду 'exit'.");
        else
            ConsoleHelper.writeMessage("Произошла ошибка во время работы клиента.");
        while (clientConnected) {
            String s = ConsoleHelper.readString();
            if (s.equals("exit"))
                break;
            if (shouldSentTextFromConsole())
                sendTextMessage(s);
        }
    }
}
/*
    Последний, но самый главный метод класса SocketThread – это метод void run(). Добавь
его. Его реализация с учетом уже созданных методов выглядит очень просто. Давай
напишем ее:
17.1.	Запроси адрес и порт сервера с помощью методов getServerAddress() и
getServerPort().
17.2.	Создай новый объект класса java.net.Socket, используя данные, полученные в
п.17.1.
17.3.	Создай объект класса Connection, используя сокет из п.17.2.
17.4.	Вызови метод, реализующий "рукопожатие" клиента с сервером
(clientHandshake()).
17.5.	Вызови метод, реализующий основной цикл обработки сообщений сервера.
17.6.	При возникновении исключений IOException или ClassNotFoundException
сообщи главному потоку о проблеме, используя notifyConnectionStatusChanged и false
в качестве параметра.
Клиент готов, можешь запустить сервер, несколько клиентов и проверить как все работает.*/