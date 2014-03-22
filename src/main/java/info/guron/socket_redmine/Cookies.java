package info.guron.socket_redmine;

import java.util.LinkedList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Guron
 * Date: 04.09.13
 * Time: 18:32
 * To change this template use File | Settings | File Templates.
 */
public class Cookies {
    public Cookies(List<String> cookies){
        if(cookies==null)return;
        int count = cookies.size();
        if (count<1)return;
        for(int i=0;i<count;i++){
            this.listCookies.add(cookies.get(i).split("; ")[0]);
            this.strCookies+=cookies.get(i).split("; ")[0] + ((i+1)==count?"":"; ");
        }
    }
    public String getStrCookies(){
        return strCookies;
    }

    private List<String> listCookies = new LinkedList<String>();
    private String strCookies = "";
}
