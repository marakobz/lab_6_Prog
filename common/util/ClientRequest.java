package util;

import java.io.Serializable;

/**
 * Class for get request value.
 */
public class ClientRequest implements Serializable {
    private String commandName;
    private String commandArguments;
    private Serializable objectArgument;

    public ClientRequest(String commandName, String commandArguments, Serializable objectArgument) {
        this.commandName = commandName;
        this.commandArguments = commandArguments;
        this.objectArgument = objectArgument;
    }
    public ClientRequest(String commandName, String commandStringArgument) {
        this(commandName, commandStringArgument, null);
    }

    public ClientRequest() {
        this("", "");
    }
    public String getCommandName() {
        return commandName;
    }

    public String getCommandArguments() {
        return commandArguments;
    }

    public Object getObjectArgument() {
        return objectArgument;
    }

    public boolean isEmpty(){
        return commandName.isEmpty() && commandArguments.isEmpty() && objectArgument==null;
    }

    @Override
    public String toString() {
        return "ClientRequest{"
                + " commandName='" + commandName + '\''
                + ", commandArguments='" + commandArguments + '\''
                + ", objectArgument=" + objectArgument
                + '}';
    }
}
