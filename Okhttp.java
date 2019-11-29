import net.sf.json.JSONArray;
import okhttp3.*;
import okio.BufferedSource;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.*;

public class Okhttp {
    public static void main(String[] args){
        testJson();
        testGetRequest();
    }

    /**
     *
     * @param url 发送get请求的目标url
     * @return 返回一个响应对象
     * @throws IOException
     */
    static Response sendGetRequest(String url) throws IOException{
        OkHttpClient client = new OkHttpClient.Builder().build();
        Request request = new Request.Builder().url(url).get().build();
        Call call = client.newCall(request);
        Response response=null;
        response=call.execute();
        return response;
    }

    /**
     *
     * @param url 发送post请求的目标url
     * @param form 要发送的表单
     * @return 返回一个响应对象
     * @throws IOException
     */
    static Response sendPostRequest(String url,HashMap<String,String> form) throws IOException{
        Response response=null;
        OkHttpClient client = new OkHttpClient.Builder().build();
        FormBody.Builder builder = new FormBody.Builder();
        Set<String> keys=form.keySet();
        Iterator<String> iterator=keys.iterator();
        while(iterator.hasNext()){
            String key=(String) iterator.next();
            builder.add(key,form.get(key));
        }
        FormBody body=builder.build();
        Request request = new Request.Builder().url(url).post(body).build();
        Call call = client.newCall(request);
        response=call.execute();
        return response;
    }

    /**
     *
     * @param url 发送get请求的目标url
     * @param headers 设置get请求的请求头
     * @return 返回一个响应对象
     * @throws IOException
     */
    static Response sendGetRequest(String url,Headers headers) throws IOException{
        OkHttpClient client = new OkHttpClient.Builder().build();
        Request request = new Request.Builder().headers(headers).url(url).get().build();
        Call call = client.newCall(request);
        Response response=null;
        response=call.execute();
        return response;
    }

    /**
     *
     * @param response 要获取响应头的响应对象
     * @return 响应头的键值表
     */
    static HashMap<String,String> getResponseHeaders(Response response){
        Headers h = response.headers();
        HashMap<String,String> headers=new HashMap<>();
        for(int i=0;i<h.size();i++){
            headers.put(h.name(i),h.value(i));
        }
        if(headers.size()!=0){
            return headers;
        }else{
            return null;
        }
    }

    /**
     *
     * @param response 要获取响应体的响应对象
     * @return 响应体(出错时返回null)
     */
    static String getResponseBodies(Response response){
        ResponseBody bodies=response.body();
        try{
            return bodies.string();
        }catch (IOException e){
            e.printStackTrace();
            return null;
        }
    }

    /**
     *
     * @param response 要获取响应体的响应对象
     * @param path 设置出错时的输出路径
     * @return 响应体(出错时将响应体输出到指定路径)
     */
    static String getResponseBodies(Response response,String path){
        ResponseBody bodies=response.body();
        try{
            return bodies.string();
        }catch (IOException e){
            e.printStackTrace();
            //使用流式输出
            try(BufferedSource source =bodies.source();FileWriter writer=new FileWriter(path);)
            {
                while(!source.exhausted()){
                    writer.write(source.readString(Charset.forName("utf-8")));
                }
                writer.flush();
            }catch (IOException e1){
                e1.printStackTrace();
            }
            return null;
        }
    }

    /**
     *
     * @param response 要获取状态码的响应对象
     * @return 状态码
     */
    static int getResponseStatus(Response response){
        return response.code();
    }
    static ArrayList<Student> parseJson(String json){
        ArrayList<Student> students=new ArrayList<>();
        JSONArray jsonArray = JSONArray.fromObject(json);
        for (int i = 0; i < jsonArray.size(); i++) {
            String name=jsonArray.getJSONObject(i).getString("name");
            boolean male=jsonArray.getJSONObject(i).getBoolean("male");
            JSONArray scoreArray=jsonArray.getJSONObject(i).getJSONArray("score");
            Score scores=new Score(scoreArray.getJSONObject(0).getInt("chinese")
                    ,scoreArray.getJSONObject(0).getInt("math"),
                    scoreArray.getJSONObject(0).getInt("english"));
            students.add(new Student(name,male,scores));
        }
        return students;
    }
    static void testJson(){
        try{
            ArrayList<Student> students=parseJson(Reader.readTxt("json.txt").get(0));
            for(Student student:students){
                System.out.println(student+"\n");
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }
    static void testGetRequest(){
        try{
            Response response=sendGetRequest("http://gank.io/");
            System.out.println("状态码: "+getResponseStatus(response));
            System.out.println();

            HashMap<String,String> headers=getResponseHeaders(response);
            System.out.println("响应头: ");
            Iterator iter = headers.entrySet().iterator();
            while (iter.hasNext()) {
                System.out.println(iter.next());
            }
            System.out.println();

            System.out.println("响应体: ");
            String bodies=getResponseBodies(response,"response.txt");
            System.out.println(bodies);
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
class Student{
    private String name;
    private boolean male;
    private Score scores;
    Student(String name,boolean male,Score score){
        this.name=name;
        this.male=male;
        this.scores=score;
    }
    String getName(){
        return this.name;
    }
    String getSex(){
        return male ? "男":"女";
    }
    ArrayList<Integer> getScores(){
        ArrayList<Integer> grades=new ArrayList<>();
        Score score=this.scores;
        grades.add(score.getChinese());
        grades.add(score.getMath());
        grades.add(score.getEnglish());
        return grades;
    }
    @Override
    public String toString() {
        String str="";
        str=str.concat("名字="+getName()+"\n");
        str=str.concat("性别="+getSex()+"\n");
        str=str.concat("分数=语"+scores.getChinese()+",数"+scores.getMath()+",英"+scores.getEnglish()+"\n");
        return str;
    }
}
class Score{
    private int chinese;
    private int math;
    private int english;
    Score(int chinese,int math,int english){
        this.chinese=chinese;
        this.math=math;
        this.english=english;
    }
    int getChinese(){
        return this.chinese;
    }
    int getMath(){
        return this.math;
    }
    int getEnglish(){
        return this.english;
    }
}
