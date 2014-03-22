package info.guron.socket_redmine;


import javax.net.ssl.HttpsURLConnection;
import javax.xml.parsers.DocumentBuilder;
import java.io.*;
import java.net.ProtocolException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.logging.Logger;

import org.htmlcleaner.*;
import info.guron.socket_redmine.KitConnect;

/**private static Logger log = Logger.getLogger(ControllerRedmine.class.getName());
 * Created with IntelliJ IDEA.
 * User: Guron
 * Date: 04.09.13
 * Time: 13:20
 * To change this template use File | Settings | File Templates.
 */
public class ControllerRedmine{
    private static Logger log = Logger.getLogger(ControllerRedmine.class.getName());
    private List<Task> tasks= null;
    private int countPages = 0;

    public ControllerRedmine(){

    }

    /**
     * Excecute Login.
     * 1. Get login page, for form anf parse token.
     * 2. Send form and socket/
     * 3. Parse status code. If code 302, then all good! Else code 200 then bad..;
     * @param setting
     * @return
     * @throws IOException
     */
    public boolean login(Settings setting)throws IOException{
        this.setting = setting;

        String postAuthenticity_token="";
        String postBtnLogin="";
        String postUtf8="";

        //get login page
        KitConnect connect = new KitConnect(setting,"/login",false, true);
        Map<String,List<String>> headers = connect.getHeaderFieldsLow();
        this.cookies = new Cookies(headers.get("set-cookie"));

        StringBuilder page = connect.getPage();
        TagNode node = new HtmlCleaner().clean(page.toString());
        try{
            //**********get postAuthenticity_token************
            Object[] objTags = node.evaluateXPath("//*[@id='login-form']/form/div/input[@name='authenticity_token']");
            if(objTags.length==1){
                TagNode tagMeta = (TagNode)objTags[0];
                postAuthenticity_token =tagMeta.getAttributeByName("value");
                log.fine("Found token:"+postAuthenticity_token);
            }else{
                log.warning("Token not found");
                return false;
            }

            //**********get postUtf8************
            objTags = node.evaluateXPath("//*[@id='login-form']/form/div/input[@name='utf8']");
            if(objTags.length==1){
                TagNode tagMeta = (TagNode)objTags[0];
                postUtf8 =tagMeta.getAttributeByName("value");
                log.fine("Found utf8:"+postUtf8);
            }else{
                log.warning("Utf8 not found");
                return false;
            }

            //**********get postBtnLogon************
            objTags = node.evaluateXPath("//*[@id='login-form']//*[@name='login']");
            if(objTags.length==1){
                TagNode tagMeta = (TagNode)objTags[0];
                postBtnLogin =tagMeta.getAttributeByName("value");
                log.fine("Found guton Login:"+postBtnLogin);
            }else{
                log.warning("Button Login not found");
                return false;
            }
        }catch (org.htmlcleaner.XPatherException e){e.printStackTrace();}

        connect.disconnect();

        //login in
        connect = new KitConnect(setting,"/login", true, true);
        connect.setCookies(this.cookies);
        String post = "utf8="+postUtf8+"&authenticity_token="+postAuthenticity_token+"&username="+setting.getUsername()+"&password="+setting.getPassword()+"&login="+postBtnLogin;//;+"&autologin=1";

        connect.sendPost(post);

        if(connect.getResponseCode()==200){ //ошибка
            log.fine(connect.getPage().toString());
            log.warning("Login error: 200 ok or 200");
            return false;
        }
        if(connect.getResponseCode()==302){ //все хорошо...
            headers = connect.getHeaderFieldsLow();
            //StringBuilder pagePost = connect.getPage();
            this.cookies = new Cookies(headers.get("set-cookie"));
            return true;
        }
        return false;
    }

    public List<Task> getTasks() {
            countPages = 0;
            List<Task> tasks = getTasks(1);
            if (countPages == 1){
                return tasks;
            }else {
                for (int i = 2; i <= countPages; i++) {
                    tasks.addAll(getTasks(i));
                }
            }
        return tasks;
    }
    public List<Task> getTasks(int numPage){
        KitConnect connect = new KitConnect(setting,"/issues?assigned_to_id=me&set_filter=1&sort=priority%3Adesc%2Cupdated_on%3Adesc&page="+numPage, false, true);

        connect.setCookies(this.cookies);
        StringBuilder page = connect.getPage();

        HtmlCleaner cleaner = new HtmlCleaner();
        TagNode node = cleaner.clean(page.toString());


        List<Task> tasks = new LinkedList<Task>();
        try{
            Object[] objTags = node.evaluateXPath("//*[@id='content']/form[2]/div[2]/table/tbody/tr");

            if (objTags.length<1){ return  null;}

            for(Object objTag :objTags){
                TagNode tag = (TagNode)objTag;
                List<org.htmlcleaner.TagNode> columns = tag.getChildTagList();
                int taskId = 0;
                String taskTopic = "";
                String taskProject = null;
                for (TagNode td:columns){
                    String tdClass = td.getAttributeByName("class");
                    if(tdClass.equals("id")){
                        taskId  = Integer.parseInt(td.getText().toString());
                    }
                    if(tdClass.equals("subject")){
                        taskTopic  = td.getText().toString();
                    }
                    if(tdClass.equals("project")){
                        taskProject  = td.getText().toString().replace("&quot;","\"");
                    }
                }
                tasks.add(new Task(taskId,taskTopic,taskProject));
            }

        }catch (org.htmlcleaner.XPatherException e){e.printStackTrace();}

        //узначем колличество страниц с задачами
        try {
            Object[] objTags = node.evaluateXPath("//p[@class='pagination']/a");
            if (objTags.length>1){
                for(Object objTag :objTags){
                    TagNode tag = (TagNode)objTag;
                    if(!tag.getAttributeByName("href").contains("per_page")) {
                        try {
                            countPages = Math.max(Integer.parseInt(tag.getText().toString()), countPages);
                        } catch (java.lang.NumberFormatException e) {
                        }
                    }


                }
            }

        }catch (org.htmlcleaner.XPatherException e){e.printStackTrace();}

        return tasks;
    }

    public void sendJobTimeShort(int IDtask, Float Hours) throws  IOException {
        sendJobTime(IDtask, Hours, new GregorianCalendar(TimeZone.getDefault()), "test");
    }

    /**
     * <p>Отправляет запрос, который проставляет время</p>
     * <p>ID проекта, токен, и дейстие парсится.</p>
     * @param IDtask
     * @param Hours
     * @param date
     * @param comment
     * @throws IOException
     */
    public void sendJobTime(int IDtask, Float Hours, GregorianCalendar date, String comment) throws  IOException{
        KitConnect connect = new KitConnect(setting,"/issues/"+String.valueOf(IDtask)+"/time_entries/new",false, true);
        connect.setCookies(this.cookies);
        StringBuilder page = connect.getPage();
        Map<String,List<String>> headers = connect.getHeaderFieldsLow();
        this.cookies = new Cookies(headers.get("set-cookie"));
        String postAuthenticity_token=""; //Токен, без него запрос не пройдет
        String IDproject = ""; // ID проекта, в котором задача
        String IDactivity = ""; // ID действия
        String submit = ""; //Кнопочка "отпарвить"

        TagNode node = new HtmlCleaner().clean(page.toString());
        try{
            //**********get postAuthenticity_token************
            Object[] objTags = node.evaluateXPath("//*[@id='new_time_entry']/div/input[@name='authenticity_token']");
            if(objTags.length==1){
                TagNode tagMeta = (TagNode)objTags[0];
                postAuthenticity_token =URLEncoder.encode(tagMeta.getAttributeByName("value"), "UTF8");
                System.out.println("Found token:"+postAuthenticity_token);
            }else{
                System.out.println("Token not found");
            }
        }catch (org.htmlcleaner.XPatherException e){e.printStackTrace();}

        /**
         * <p>Парсинг ID проекта</p>
         */
        try{
            //**********get postAuthenticity_token************
            Object[] objTags = node.evaluateXPath("//*[@id='time_entry_project_id']");
            if(objTags.length==1){
                TagNode tagMeta = (TagNode)objTags[0];
                IDproject =tagMeta.getAttributeByName("value");
                //System.out.println("IDproject:"+IDproject);
            }else{
                System.out.println("IDproject not found");
            }
        }catch (org.htmlcleaner.XPatherException e){e.printStackTrace();}

        /**
         * <p>Кнопочка "отправить и продолжить"</p>
         */
        try{
            //**********get postAuthenticity_token************
            Object[] objTags = node.evaluateXPath("//*[@name='continue']");
            if(objTags.length==1){
                TagNode tagMeta = (TagNode)objTags[0];
                submit =tagMeta.getAttributeByName("value");
               // System.out.println("submit:"+submit);
            }else{
                System.out.println("submit not found");
            }
        }catch (org.htmlcleaner.XPatherException e){e.printStackTrace();}


        /**
         * <p>Список "деятельностей".</p>
         * <p>На форме он обязателен для заполнения, если в списке всего одно значение("-") выбираем его и передаем.
         * если значений несколько то выбираем второе, т.к. первое это "--- Выберите ---", его передать нельзя</p>
         */
        LinkedList<String> options = new LinkedList<String>();
        try{
            Object[] objTags = node.evaluateXPath("//*[@id='time_entry_activity_id']/option");
            for(Object objTag :objTags){
                TagNode option = (TagNode)objTag;
                options.add(option.getAttributeByName("value"));
            }
            if(options.get(0).toString().equals("")) {
                IDactivity = options.get(1).toString();
            }else IDactivity = options.get(0).toString();

        }catch (org.htmlcleaner.XPatherException e){
            System.out.println("Don't found activity_id");
            e.printStackTrace();
        }

        connect.disconnect();


        connect = new KitConnect(setting,"/time_entries", true, true);
        connect.setCookies(this.cookies);
        //gString token = java.net.URLEncoder.encode(postAuthenticity_token);

        String post = "utf8=%E2%9C%93&authenticity_token="+postAuthenticity_token
                +"&back_url=" +
                this.setting.getURLRedmine() +
                //"issues%2F" +IDtask + //ИД задачи +
                "&time_entry%5Bproject_id%5D=" + IDproject+ // ИД проекта +
                "&time_entry%5Bissue_id%5D=" +IDtask + //ИД задачи +
                "&time_entry%5Bspent_on%5D=" + date.get(GregorianCalendar.YEAR)+"-"+ date.get(GregorianCalendar.MONTH)+"-"+ date.get(GregorianCalendar.DAY_OF_MONTH)+ //2013-11-17 дата +
                "&time_entry%5Bhours%5D=" + String.valueOf(Hours)+ //затраченое время +
                "&time_entry%5Bcomments%5D=" + //коммент -
                "&time_entry%5Bactivity_id%5D="+IDactivity + //действие, если действий нет то прочерк, если есть то  пока первое +
                "&commit="+submit; //создать и продолжить +
                //"&commit=%D0%A1%D0%BE%D0%B7%D0%B4%D0%B0%D1%82%D1%8C"; //создать и продолжить
                //"&commit=%D0%A1%D0%BE%D0%B7%D0%B4%D0%B0%D1%82%D1%8C+%D0%B8+%D0%BF%D1%80%D0%BE%D0%B4%D0%BE%D0%BB%D0%B6%D0%B8%D1%82%D1%8C"; //создать и продолжить
                //%D0%A1%D0%BE%D0%B7%D0%B4%D0%B0%D1%82%D1%8C"; //коммит епта
        connect.sendPost(post);
        //connect.sendPost(URLEncoder.encode(post));


        if(connect.getResponseCode()==200){
            System.out.println("200");
        }

        //System.out.println(connect.getResponseCode());
        System.out.println(connect.getPage());
        //Map<String, List<String>> headerFieldsLow = connect.getHeaderFieldsLow();
        //System.out.println(headerFieldsLow);
    }




    private Settings setting;
    public Cookies cookies;
}
