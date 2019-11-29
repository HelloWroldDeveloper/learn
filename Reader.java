import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Reader {
    public static ArrayList<String> readTxt(String path)throws IOException{
        ArrayList<String> txt=new ArrayList<>();
        BufferedReader reader=new BufferedReader(new FileReader(path));
        String line;
        while((line=reader.readLine())!=null){
            txt.add(line);
        }
        if(txt.size()!=0){
            return txt;
        }else{
            return null;
        }
    }
}
