package Chat.client;


import Chat.ConsoleHelper;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

public class BotClient extends Client {
    @Override
    protected BotSocketThread getSocketThread() {
        return new BotSocketThread();
    }

    @Override
    protected boolean shouldSentTextFromConsole() {
        return false;
    }

    @Override
    protected String getUserName() {
        return new SimpleDateFormat("H:mm:ss").format(new Date()) + "_bot_" + Math.random() * 99;
    }

    public static void main(String[] args) {
        BotClient b = new BotClient();
        b.run();
    }

    public class BotSocketThread extends SocketThread {
        @Override
        protected void clientMainLoop() throws IOException, ClassNotFoundException {
            sendTextMessage("Привет! Я бот. Понимаю команды: дата, день, месяц, год, время, час, минуты, секунды.");
            super.clientMainLoop();
        }

        @Override
        protected void processIncomingMessage(String message) {
            ConsoleHelper.writeMessage(message);
            String[] array = message.split("[ ]");
            if (array.length == 2) {
                SimpleDateFormat simpleDateFormat = null;
                String mess = array[1];
                if (mess.equals("дата"))
                    simpleDateFormat = new SimpleDateFormat("d.MM.YYYY");
                else if (mess.equals("день"))
                    simpleDateFormat = new SimpleDateFormat("d");
                else if (mess.equals("месяц"))
                    simpleDateFormat = new SimpleDateFormat("MMMM");
                else if (mess.equals("год"))
                    simpleDateFormat = new SimpleDateFormat("YYYY");
                else if (mess.equals("время"))
                    simpleDateFormat = new SimpleDateFormat("H:mm:ss");
                else if (mess.equals("час"))
                    simpleDateFormat = new SimpleDateFormat("H");
                else if (mess.equals("минуты"))
                    simpleDateFormat = new SimpleDateFormat("m");
                else if (mess.equals("секунды"))
                    simpleDateFormat = new SimpleDateFormat("s");
                String info = simpleDateFormat.format(GregorianCalendar.getInstance().getTime());
                sendTextMessage("Информация для " + array[0] + " " + info);
            }
        }
    }
}

/*	  Сегодня будем реализовывать класс BotSocketThread, вернее переопределять некоторые
его методы, весь основной функционал он уже унаследовал от SocketThread.
19.1.   Переопредели метод clientMainLoop():
19.1.1. С помощью метода sendTextMessage() отправь сообщение с текстом
"Привет чатику. Я бот. Понимаю команды: дата, день, месяц, год, время, час, минуты, секунды."
19.1.2. Вызови реализацию clientMainLoop() родительского класса.
19.2.   Переопредели метод processIncomingMessage(String message). Он должен
следующим образом обрабатывать входящие сообщения:
19.2.1. Вывести в консоль текст полученного сообщения message.
19.2.2. Получить из message имя отправителя и текст сообщения. Они разделены ": ".
19.2.3. Отправить ответ в зависимости от текста принятого сообщения. Если текст
сообщения:
"дата" – отправить сообщение содержащее текущую дату в формате "d.MM.YYYY";
"день" – в формате"d";
"месяц" - "MMMM";
"год" - "YYYY";
"время" - "H:mm:ss";
"час" - "H";
"минуты" - "m";
"секунды" - "s".
Указанный выше формат используй для создания объекта SimpleDateFormat. Для
получения текущей даты необходимо использовать класс Calendar и метод
getTime().
Ответ должен содержать имя клиента, который прислал запрос и ожидает ответ,
например, если Боб отправил запрос "время", мы должны отправить ответ
"Информация для Боб: 12:30:47".
Наш бот готов. Запусти сервер, запусти бота, обычного клиента и убедись, что все работает правильно.
Помни, что message бывают разных типов и не всегда содержат ":"*/