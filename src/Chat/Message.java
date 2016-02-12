package Chat;

import java.io.Serializable;

/**
 * Created by Login on 10.02.2016.
 */
public class Message implements Serializable{
    private final MessageType type;
    private final String data;

    public Message(MessageType type) {
        this.type = type;
        this.data = null;
    }

    public String getData() {
        return data;
    }

    public MessageType getType() {
        return type;
    }

    public Message(MessageType type, String data) {
        this.type = type;
        this.data = data;
    }
}

