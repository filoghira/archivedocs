public class TagColumns{
    public static Column mainID = new Column("main_ID", "INT");

    public static String[][] getData(int id){
        return new String[][]{
                {mainID.name(), Integer.toString(id), mainID.type()}
        };
    }

    public static Column[] getColumns(){
        return new Column[]{
                mainID
        };
    }
}
