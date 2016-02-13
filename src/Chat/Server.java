package Chat;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;



public class Server {
    private static Map<String,Connection> connectionMap = new ConcurrentHashMap<String, Connection>();

    private static void sendBroadcastMessage(Message message)
    {
        for (Connection c:connectionMap.values()
             ) {
            try {
                c.send(message);
            }
            catch (IOException e)
            {
                System.out.println("Не удалось отправить сообщение.");
            }
        }
    }

    private static void serverMainLoop(Connection connection, String userName) throws IOException, ClassNotFoundException {
        while (true) {
            Message message = connection.receive();
            if (message.getType() == MessageType.TEXT)
                sendBroadcastMessage(new Message(message.getType(), userName + ": " + message.getData()));
            else
                ConsoleHelper.writeMessage("Ошибка!");
        }
    }
    private static class Handler extends Thread
    {
        private Socket socket;

        private Handler(Socket socket) {
            this.socket = socket;
        }

        private String serverHandshake(Connection connection) throws IOException, ClassNotFoundException {
            String name;
            while (true) {
                connection.send(new Message(MessageType.NAME_REQUEST));
                Message message = connection.receive();
                if (message.getType() != MessageType.USER_NAME)
                    continue;

                name = message.getData();
                if (name == null || name.isEmpty())
                    continue;
                if (connectionMap.containsKey(name)) {
                    connection.send(new Message(MessageType.TEXT, "Пользователь с таким именем уже существует!"));
                    continue;
                }

                connectionMap.put(name, connection);
                connection.send(new Message(MessageType.NAME_ACCEPTED));
                break;
            }
            return name;
        }

        private void sendListOfUsers(Connection connection, String userName) throws IOException {
            for (String s : connectionMap.keySet()
                    ) {
                if (s.equals(userName))
                    continue;
                connection.send(new Message(MessageType.USER_ADDED, s));
            }
        }

        @Override
        public void run() {
            System.out.println("Установлено соединение с удаленным адресом: " + this.socket.getRemoteSocketAddress());
            String userName = "";
            try (Connection connection = new Connection(socket);) {
                userName = serverHandshake(connection);
                sendBroadcastMessage(new Message(MessageType.USER_ADDED));
                sendListOfUsers(connection, userName);
                serverMainLoop(connection, userName);
            } catch (Exception e) {
                System.out.println("Произошла ошибка при обмене данными с удаленным адресом.");
            }
            if (!userName.isEmpty())
                connectionMap.remove(userName);
            sendBroadcastMessage(new Message(MessageType.USER_REMOVED));
            System.out.println("Соединение с удаленным адресом закрыто: " + this.socket.getRemoteSocketAddress());
        }
    }

    public static void main(String[] args) {
        ConsoleHelper.writeMessage("Введите номер порта.");
        int portNumber = ConsoleHelper.readInt();
        try(ServerSocket serverSocket = new ServerSocket(portNumber))
        {
            ConsoleHelper.writeMessage("Сервер запущен!");
            while (true)
            {
                Socket clientSocket = serverSocket.accept();
                new Handler(clientSocket).start();
            }
        }
        catch (Exception e)
        {
            ConsoleHelper.writeMessage("Ошибка запуска сервера!");
        }
    }
}

