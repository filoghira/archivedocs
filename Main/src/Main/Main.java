package Main;

import Database.Database;
import GeneralUtils.SQLUtils;

public class Main {
    public static void main(String[] args){
        Database db = new Database("bho", "");
        db.addTable("test", new String[][]{{"col1", "INT"}});
    }
}
