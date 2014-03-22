package info.guron.socket_redmine;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;
import java.util.logging.Logger;

import org.htmlcleaner.*;

/**
 * Created with IntelliJ IDEA.
 * User: Guron
 * Date: 04.09.13
 * Time: 22:25
 * To change this template use File | Settings | File Templates.
 */
public class KitConnect {
    private static Logger log = Logger.getLogger(KitConnect.class.getName());

    @Deprecated
    public KitConnect(Settings setting, String page){
        new KitConnect(setting,page, false, true);
    }

    public KitConnect(Settings setting, String page, boolean setDoOutput, boolean setDoInput){
        HttpConnect = null;
        HttpsConnect = null;
        if (setting == null) throw new NullPointerException("You must determine Setting");
        try{
            if(setting.getURLRedmine().startsWith("https://")){
                URL url = new URL(setting.getURLRedmine()+page);
                HttpsConnect = (HttpsURLConnection)url.openConnection();
                HttpsConnect.setConnectTimeout(setting.getTimeout());
                isHttp = false;
                HttpsConnect.setDoOutput(setDoOutput);
                HttpsConnect.setDoInput(setDoInput);
                HttpsConnect.setInstanceFollowRedirects(false);
            }
            if(setting.getURLRedmine().startsWith("http://")){
                URL url = new URL(setting.getURLRedmine()+page);
                HttpConnect = (HttpURLConnection)url.openConnection();
                HttpConnect.setConnectTimeout(setting.getTimeout());
                isHttp = true;
                HttpConnect.setDoOutput(setDoOutput);
                HttpConnect.setDoInput(setDoInput);
                HttpConnect.setInstanceFollowRedirects(false);
            }


        }catch (IOException e){e.printStackTrace();}
    }

    public void setRequestMethod(String method)throws ProtocolException {
        if(isHttp){
            HttpConnect.setRequestMethod(method);
        }else{
            HttpsConnect.setRequestMethod(method);
        }
    }

    public InputStream getInputStream(){
        try{
            if(isHttp){
                return HttpConnect.getInputStream();
            }else{
                return HttpsConnect.getInputStream();
            }
        }catch (IOException e){e.printStackTrace();}
        return null;
    }
    public OutputStream getOutputStream(){
        try{
            if(isHttp){
                return HttpConnect.getOutputStream();
            }else {
                return HttpsConnect.getOutputStream();
            }
        }catch (IOException e){e.printStackTrace();}
        return null;
    }

    public void setCookies(Cookies cookies){
        if(isHttp){
            HttpConnect.setRequestProperty("Cookie",cookies.getStrCookies());
        }else {
            HttpsConnect.setRequestProperty("Cookie",cookies.getStrCookies());
        }
    }

    public void addRequestProperty(String property, String value){
        if(isHttp){
            HttpConnect.setRequestProperty(property,value);
        }else {
            HttpsConnect.setRequestProperty(property,value);
        }
    }

    public void connect(){
        try{
            if(isHttp){
                HttpConnect.connect();
            }else {
                HttpsConnect.connect();
            }
        }catch (IOException e){e.printStackTrace();}
    }

    public StringBuilder getPage(){
        StringBuilder buff = new StringBuilder();
        try {

            BufferedReader br =
                    new BufferedReader(
                            new InputStreamReader(getInputStream()));

            String input;

            while ((input = br.readLine()) != null){
                //System.out.println(input);
                buff.append(input+"\n");
            }
            br.close();

        } catch (IOException e) {
            e.printStackTrace();
        }


        return buff;
    }

    public void sendPost(String data)throws IOException{
        setRequestMethod("POST");
        //addRequestProperty("Content-Length", String.valueOf(data.length()));

        //connect();
        BufferedWriter bw =
                new BufferedWriter(
                        new OutputStreamWriter(getOutputStream()));

        bw.write(data);
        bw.flush();
        bw.close();
    }


    public Map<String,List<String>> getHeaderFieldsLow() throws IOException, SocketTimeoutException{
        Map<String,List<String>> mapHeaders;
        Map<String,List<String>> mapHeadersReturn = new HashMap<String, List<String>>();
        if(isHttp){
            HttpConnect.connect();
            mapHeaders = HttpConnect.getHeaderFields();
        }else {
//            try{
                HttpsConnect.connect();
//            }catch (SocketTimeoutException e){e.printStackTrace();}
//            catch (IOException e){e.printStackTrace();}
            mapHeaders =  HttpsConnect.getHeaderFields();
        }
        List<String> values;
        for(Map.Entry<String, List<String>> entry :mapHeaders.entrySet()){
            values = new LinkedList<String>();
            for (String value:entry.getValue()){
                values.add(value);
            }
            mapHeadersReturn.put(entry.getKey()==null?null:entry.getKey().toLowerCase(),values);

        }
        log.fine(mapHeadersReturn.toString());
        return mapHeadersReturn;
    }


    public String getHeaderField(String header) throws SocketTimeoutException,IOException {

        Map<String,List<String>> mapHeaders;
        if(isHttp){
            mapHeaders = getHeaderFieldsLow();
        }else {
            mapHeaders =  getHeaderFieldsLow();
        }
        for (Map.Entry<String,List<String>> entry: mapHeaders.entrySet()){
            if(entry.getKey()!=null && entry.getKey().equals(header)){
                if(entry.getValue().size()==1)
                    return entry.getValue().get(0);
                else if(entry.getValue().size()>1)
                    return entry.getValue().get(0);
                else if(entry.getValue().size()==0)
                    return null; //А такое может быть вообще?
            }
        }
        return null;
    }

    public void disconnect(){
        if(isHttp){
            HttpConnect.disconnect();
        }else {
            HttpsConnect.disconnect();
        }
    }
    public int getResponseCode()throws IOException{
        if(isHttp){
            return HttpConnect.getResponseCode();
        }else {
            return HttpsConnect.getResponseCode();
        }
    }
    private HttpsURLConnection HttpsConnect;
    private HttpURLConnection HttpConnect;
    boolean isHttp;
}
