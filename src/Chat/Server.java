package Chat;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * Created by Login on 10.02.2016.
 */
public class Server {
    private static Map<String,Connection> connectionMap = new ConcurrentHashMap<String, Connection>();

    public static void sendBroadcastMessage(Message message)
    {
        for (Connection c:connectionMap.values()
             ) {
            c.send(message);
        }
    }

    private static class Handler extends Thread
    {
        private Socket socket;

        public Handler(Socket socket) {
            this.socket = socket;
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

/*		Т.к. сервер может одновременно работать с несколькими клиентами, нам понадобится
метод для отправки сообщения сразу всем.
Добавь в класс Server:
7.1.	Статическое поле Map<String, Connection> connectionMap, где ключом будет имя
клиента, а значением - соединение с ним.
7.2.	Инициализацию поля из п.7.1 с помощью подходящего Map из библиотеки
java.util.concurrent, т.к. работа с этим полем будет происходить из разных потоков и
нужно обеспечить потокобезопасность.
7.3.	Статический метод void sendBroadcastMessage(Message message), который должен
отправлять сообщение  message по всем соединениям из connectionMap. Если при
отправке сообщение произойдет исключение IOException, нужно отловить его и
сообщить пользователю, что не смогли отправить сообщение.*/