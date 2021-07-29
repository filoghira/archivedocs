package GhiraUtils;

import java.io.File;

public class General {
    public static String homePath(){
        return System.getProperty("user.home");
    }

    public static String[] getDirTree(String pathname){

        String[] pathnames;

        File f = new File(pathname);
        pathnames = f.list();

        return pathnames;
    }

    public static String getParentDir(String path){
        File file = new File(path);
        return file.getParentFile().toString();
    }

    public static String quote(String input){
        return "'" + input + "'";
    }
}
