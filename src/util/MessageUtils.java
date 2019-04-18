package util;

import model.Command;
import model.Message;
import model.MessageType;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.List;

public class MessageUtils {

    public Message getMessage(InputStream inputStream) {
        BufferedInputStream bufferedInputStream = null;
        ObjectInputStream objectInputStream = null;
        Message message = null;
        try {
            bufferedInputStream = new BufferedInputStream(inputStream);
            objectInputStream = new ObjectInputStream(bufferedInputStream);
            message = (Message) objectInputStream.readObject();
            objectInputStream.close();
            bufferedInputStream.close();
            inputStream.close();
        } catch (IOException e) {
        } catch (ClassNotFoundException e) {

        }
        return message;
    }

    public Command getCommand(Message message) {
        return message.getCommand();
    }

    public int getMessageLength(Message message) {
        return message.getLength();
    }

    public MessageType getMessageType(Message message) {
        return message.getType();
    }

    public List<Command> getCommandList(Message message){
        return message.getCommandList();
    }


}
