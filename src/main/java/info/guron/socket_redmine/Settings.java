package info.guron.socket_redmine;

/**
 * Created with IntelliJ IDEA.
 * User: Guron
 * Date: 03.09.13
 * Time: 21:00
 * To change this template use File | Settings | File Templates.
 */
public class Settings {
    public Settings(String URL,String username, String password, int timeout){
        this.URLRedmine = URL.toLowerCase().trim();
        this.username = username;
        this.password = password;
        this.timeout = timeout;
    }

    public String getURLRedmine(){
        return URLRedmine;
    }
    public String getUsername(){
        return username;
    }
    public String getPassword(){
        return password;
    }
    public int getTimeout() {return timeout;}


    private String username = null;
    private String password = null;
    private String URLRedmine = null;
    private boolean https = true;
    private int timeout;
}
