package Main;

import Database.Database;
import GeneralUtils.SQLUtils;

public class Main {
    public static void main(String[] args){
        Database db = new Database("bho", "");
        SQLUtils.printResultSet(db.selectAll("test"), new String[]{"id", "col1"});
    }
}
