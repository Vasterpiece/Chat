package Chat;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;



public class Server {
    private static Map<String,Connection> connectionMap = new ConcurrentHashMap<String, Connection>();

    public static void sendBroadcastMessage(Message message)
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

    private static class Handler extends Thread
    {
        private Socket socket;
        public Handler(Socket socket) {
            this.socket = socket;
        }

        String serverHandshake(Connection connection) throws IOException, ClassNotFoundException {
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

