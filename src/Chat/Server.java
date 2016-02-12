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

    private void serverMainLoop(Connection connection, String userName) throws IOException, ClassNotFoundException {
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
            super.run();
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

/*		Пришло время написать главный метод класса Handler, который будет вызывать все
вспомогательные методы, написанные ранее. Добавим метод void run() в класс Handler.
Он должен:
11.1.	Выводить сообщение, что установлено новое соединение с удаленным
адресом, который можно получить с помощью метода getRemoteSocketAddress
11.2.	Создавать Connection, используя поле Socket
11.3.	Вызывать метод, реализующий рукопожатие с клиентом, сохраняя имя нового
клиента
11.4.	Рассылать всем участникам чата информацию об имени присоединившегося
участника (сообщение с типом USER_ADDED). Подумай, какой метод подойдет для
этого лучше всего.
11.5.	Сообщать новому участнику о существующих участниках
11.6.	Запускать главный цикл обработки сообщений сервером
11.7.	Обеспечить закрытие соединения при возникновении исключения
11.8.	Отловить все исключения типа IOException и ClassNotFoundException, вывести в
консоль информацию, что произошла ошибка при обмене данными с удаленным
адресом
11.9.	После того как все исключения обработаны, если п.11.3 отработал и возвратил
нам имя, мы должны удалить запись для этого имени из connectionMap и разослать
всем остальным участникам сообщение с типом USER_REMOVED и сохраненным
именем.
11.10.	Последнее, что нужно сделать в методе run() – вывести сообщение,
информирующее что соединение с удаленным адресом закрыто.
Наш сервер полностью готов. Попробуй его запустить.*/
