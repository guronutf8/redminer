package info.guron.socket_redmine;


import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocketFactory;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Main {

    public static void main(String[] args) {
        new Main().testIt();

    }
    private void testIt(){

        String https_url = "https://redmine.lanitp.ru/login";

        //String https_url = "http://localhost/post.php";

        //String https_url = "https://redmine.lanitp.ru/issues/7351";
        //String https_url = "https://google.ru/";
        URL url;
        try {

            url = new URL(https_url);

            HttpsURLConnection con = (HttpsURLConnection)url.openConnection();
            con.setDoOutput(true);
            con.setDoInput(true);

            //con.addRequestProperty("Cookie","_redmine_session=11BAh7CToMdXNlcl9pZGkCDAE6CmN0aW1lbCsHP58lUjoKYXRpbWVsKwc%2FnyVSSSIPc2Vzc2lvbl9pZAY6BkVGSSIlOWUxMzM1NzFkYjRkOTM4OTlkMTkyMjJjMmM4Zjg1YTYGOwhU--82511d4438491dbd4ca9a203df2006d039adf6fe;");
            String post = "utf8=%E2%9C%93&username=admin&password=admin&autologin=1";

            con.addRequestProperty("Content-Length",String.valueOf(post.length()));

            System.out.println("*****request method*****");
            con.setRequestMethod("POST");
            con.connect();
            BufferedWriter bw =
                    new BufferedWriter(
                            new OutputStreamWriter(con.getOutputStream()));
            bw.write(post);
            bw.flush();
            bw.close();

            System.out.println("*****response status*****");
            System.out.println(con.getHeaderField("Status"));


            System.out.println("*****response Set-Cookie*****");
            System.out.println(con.getHeaderField("Set-Cookie"));

            Map<String,List<String>> headers = con.getHeaderFields();
            List<String> cookies = headers.get("Set-Cookie");

            List<String> cookiesRequest =new LinkedList <String>();


            for (String cookie:cookies){
                System.out.println(cookie);
                cookiesRequest.add("d");
            }
            print_content(con);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void print_content(HttpsURLConnection con){
        if(con!=null){

            try {

                System.out.println("****** Content of the URL ********");
                BufferedReader br =
                        new BufferedReader(
                                new InputStreamReader(con.getInputStream()));

                String input;

                while ((input = br.readLine()) != null){
                    System.out.println(input);
                }
                br.close();

            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }


}
