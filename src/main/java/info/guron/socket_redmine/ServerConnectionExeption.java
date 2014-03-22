package info.guron.socket_redmine;

import java.io.IOException;

/**
 * Created by guron on 13.02.14.
 */
public class ServerConnectionExeption extends IOException{
    public ServerConnectionExeption(){}
    public ServerConnectionExeption(String gripe) {
        super(gripe);
    }
}
