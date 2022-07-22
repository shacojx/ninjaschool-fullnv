import lombok.val;

import java.io.*;

public class Reader {
    public static void main(String[] args) {
        File file = new File("C:\\Users\\dungle\\Desktop\\Eff\\IMG");
        for (File listFile : file.listFiles()) {
            String[] names =  listFile.getName().split(" ");

            listFile.renameTo(new File("C:\\Users\\dungle\\Desktop\\Eff\\IMG\\ImgEffect " + names[names.length-1]));
        }

    }
}
