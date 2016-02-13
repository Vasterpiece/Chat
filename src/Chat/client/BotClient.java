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

