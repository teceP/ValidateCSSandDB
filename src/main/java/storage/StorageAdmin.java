package storage;

import logger.Logger;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class StorageAdmin implements StorageAdminInterface{

    private final String DELIMETER = "#";


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

        } catch (FileNotFoundException e) {
            Logger.log("File not found while trying to write to a file.");
            e.printStackTrace();
        }



    }


    @Override
    public List<String> restoreList(String filename, boolean artifact) throws FileNotFoundException{
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




}
