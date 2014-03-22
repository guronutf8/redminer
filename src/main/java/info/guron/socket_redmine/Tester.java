package info.guron.socket_redmine;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import info.guron.redminer.RedmineService;

/**
 * Created with IntelliJ IDEA.
 * User: Guron
 * Date: 04.09.13
 * Time: 18:07
 * To change this template use File | Settings | File Templates.
 */

public class Tester {
    private static Logger log = Logger.getLogger(Tester.class.getName());

    public static void main(String[] args){


        ControllerRedmine con = new ControllerRedmine();
        try{
            if(con.login(new Settings("http://192.168.0.112/","admin","admin",10000)))
            //if(con.login(new Settings("http://localhost","admin","admin")))
                System.out.println("Logined");
            else System.out.println("Logined error");
        }catch (IOException e){e.printStackTrace();}
//        List<Task> tasks = con.getTasks();
//        for (Task task : tasks) {
//            System.out.println(task.getTopic());
//        }
        try {
            con.sendJobTimeShort(32, 0.1f);
        }catch (IOException e){e.printStackTrace();}

        con.getTasks();

//        for(int iq = 0;iq<5;iq++){
//            try{
//                TimeUnit.SECONDS.sleep(1);
//                con.sendJobTimeShort(1,0.1f);
//                TimeUnit.SECONDS.sleep(1);
//                con.sendJobTimeShort(3,0.1f);
//                TimeUnit.SECONDS.sleep(1);
//                con.sendJobTimeShort(5,0.1f);
//            }catch (IOException e){e.printStackTrace();}
//            catch (InterruptedException e){e.printStackTrace();}
//        }


//        for (Task task:con.getTasks()){
//            System.out.println(task.getId()+" "+task.getTopic());
//        }
        System.out.println("");
    }
}
