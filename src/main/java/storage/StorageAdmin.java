package storage;

import logger.Logger;
import models.MyTable;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class StorageAdmin extends StorageAdminHelper implements StorageAdminInterface {

    /**
     * Stores a List of Strings
     *
     * @param list The list which you want to store
     * @param file The filename, which you want to use. Filenamestring stored in StorageAdminInterface.
     * @param artifact true: will be stored in artifact folder, false: will be stored in mainfolder
     *
     */
    @Override
    public void storeList(List<String> list, String file, boolean artifact) {
        StringBuilder sb = new StringBuilder();
        PrintWriter pw;

        File dir = new File(ARTIFACT);
        if(!dir.exists()){
            dir.mkdir();
        }

        try {
            if(artifact){
                File f = new File(ARTIFACT + file);
                if(f.exists()){
                    f.delete();
                }
                pw = new PrintWriter(f);
            }else{
                File f = new File(file);
                if(f.exists()){
                    f.delete();
                }
                pw = new PrintWriter(f);
            }

            for(String s : list){
                sb.append(s);
                sb.append(DELIMETER);
            }

            pw.write(sb.toString());
            pw.flush();
            pw.close();

            Logger.log("*****************************************");
            Logger.log("New artifact stored: '" + file + "'");
            Logger.log("*****************************************");

        } catch (FileNotFoundException e) {
            Logger.log("File not found while trying to write to a file.");
            e.printStackTrace();
        }
    }

    /**
     * Restores a List of Strings
     *
     * @param filename The path of your file, you want to restore
     * @param artifact true: file is in artifact folder, false: file is in maindir.
     * @return
     */
    @Override
    public List<String> restoreList(String filename, boolean artifact){
        try {

            if(artifact){
                filename = ARTIFACT + filename;
            }

            FileReader in = new FileReader(filename);
            BufferedReader br = new BufferedReader(in);
            StringBuilder line = new StringBuilder();
            String newLine = "";

            while((newLine = br.readLine())!= null) {
                line.append(newLine);
            }

            String[] temp = line.toString().split(DELIMETER);
            List<String> list = new ArrayList<>();
            for(String s : temp){
                list.add(s);
            }

            return list;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void storeTables(List<MyTable> tables){
        StringBuilder sb;
        List<String> list = new ArrayList<>();

        /*   "# table name ~ column + column + column #"
         *
         */

        for(MyTable t : tables){
            sb = new StringBuilder();
            sb.append(t.getTableName());
            sb.append(ATTRIBUT_DELIMETER);
            sb.append(this.columnsToString(t.getColumn()));
            list.add(sb.toString());
        }

        this.storeList(list, TABLES, true);
    }

    public List<MyTable> restoreTables(){
        List<String> list = this.restoreList(TABLES, true);
        List<MyTable> restoredTables = new ArrayList<>();
        MyTable newTable = null;

        // "# table name ~ column + column + column #"
        String[] restoredContent;

        for(String content : list){
            newTable = new MyTable("tmp");
            restoredContent = content.split(ATTRIBUT_DELIMETER);

            newTable.setTablename(restoredContent[0]);
            newTable.setColumn(this.stringToColumns(restoredContent[1]));

            restoredTables.add(newTable);
        }

        return restoredTables;
    }
}

