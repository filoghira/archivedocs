package GhiraUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class General {
    public static String homePath(){
        return System.getProperty("user.home");
    }

    public static String[] getDirTree(String pathname){

        String[] pathNames;

        File f = new File(pathname);
        pathNames = f.list();

        return pathNames;
    }

    public static String getParentDir(String path){
        File file = new File(path);
        return file.getParentFile().toString();
    }

    public static String quote(String input){
        return "'" + input + "'";
    }

    public static String checksum(String filepath, String algorithm) throws IOException {

        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance(algorithm);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        // file hashing with DigestInputStream
        try (DigestInputStream dis = new DigestInputStream(new FileInputStream(filepath), md)) {
            //noinspection StatementWithEmptyBody
            while (dis.read() != -1) ; // Empty loop to clear the data
            md = dis.getMessageDigest();
        }

        // bytes to hex
        StringBuilder result = new StringBuilder();
        for (byte b : md.digest()) {
            result.append(String.format("%02x", b));
        }
        return result.toString();

    }


}
