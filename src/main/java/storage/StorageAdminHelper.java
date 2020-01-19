package storage;

import models.CssClass;
import models.Match;
import models.MyTable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class StorageAdminHelper{

    /**
     * Delimeter for Match-object attributes.
     */
    private String MATCHES_ATTRIBUTE_DELIMETER ="+";

    /**
     * Converts a list of CssClasses (own Object: CssClass), to a list of Strings
     *
     * @param list List of CssClasses
     * @return list of strings
     */
    public List<String> cssClassToString(List<CssClass> list){

        List<String> strings = new ArrayList<>();
        StringBuilder sb;

        for(CssClass c : list){
            //init, to get empty sb
            sb = new StringBuilder();

            /**
             * 1. name
             * 2. file
             * 3. indexColumn
             * 4. indexRow
             * 5. length
             */

            sb.append(c.getName());
            sb.append(StorageAdminInterface.ATTRIBUT_DELIMETER);
            sb.append(c.getFile().getName());
            sb.append(StorageAdminInterface.ATTRIBUT_DELIMETER);
            sb.append(c.getCharacterIndexColumn());
            sb.append(StorageAdminInterface.ATTRIBUT_DELIMETER);
            sb.append(c.getCharacterIndexRow());
            sb.append(StorageAdminInterface.ATTRIBUT_DELIMETER);
            sb.append(c.getLength());
            sb.append(StorageAdminInterface.ATTRIBUT_DELIMETER);

            //add to list
            strings.add(sb.toString());
        }

        return strings;
    }

    /**
     * Converts a string to a list of CssClass-objects.
     *
     * @return List of CssClass-objects
     */
    public List<CssClass> stringToCssClass(){

        StorageAdminInterface sa = new StorageAdmin();
        List<CssClass> classes = new ArrayList<>();
        CssClass cssClass;

            List<String> temp = sa.restoreList(StorageAdminInterface.CSS_CLASSES, true);

            for(String s : temp){
                String[] attributes = s.split(StorageAdminInterface.ATTRIBUT_DELIMETER);

                    /**
                     * 1. name
                     * 2. file
                     * 3. indexColumn
                     * 4. indexRow
                     * 5. length
                     */
                    cssClass = new CssClass(new File(attributes[1]),
                            attributes[0],
                            Integer.parseInt(attributes[3]),
                            Integer.parseInt(attributes[2]),
                            Integer.parseInt(attributes[4]));

                    classes.add(cssClass);

                    //CssClass(File file, String name, int characterIndexRow, int characterIndexColumn, int length)
            }

        return classes;
    }

    /**
     * Converts a list of objects, type Match, to a String, which can be stored by the StorageAdmin.
     *
     * @param list of Matches
     * @return List of Strings
     */
    public List<String> matchesToString(List<Match> list){

        List<String> strings = new ArrayList<>();
        StringBuilder sb;

        for(Match m : list){
            //init, to get empty sb
            sb = new StringBuilder();

            /**
             * 1. CssClass
             * 2. MyTable
             * 3. boolean isFile as 0/1
             * 4. double percentage
             */
            List<CssClass> css = new ArrayList<>();
            css.add(m.getCssClass());
            List<String> cssClass = this.cssClassToString(css);

            //Could be null, if Match is based on a css file, not on a css class
            if(m.getCssClass() != null){
                sb.append(cssClass.get(0));
                sb.append(MATCHES_ATTRIBUTE_DELIMETER);
            }else{
                sb.append("null");
                sb.append(MATCHES_ATTRIBUTE_DELIMETER);
            }

            sb.append(m.getTableName());
            sb.append(MATCHES_ATTRIBUTE_DELIMETER);

            if(m.isFile()){
                sb.append(1);
                sb.append(MATCHES_ATTRIBUTE_DELIMETER);
            }else{
                sb.append(0);
                sb.append(MATCHES_ATTRIBUTE_DELIMETER);
            }

            if(m.getPercentage() != null)
            {
                sb.append(m.getPercentage());
                sb.append(MATCHES_ATTRIBUTE_DELIMETER);
            }else{
                sb.append("null");
                sb.append(MATCHES_ATTRIBUTE_DELIMETER);
            }

            //add to list
            strings.add(sb.toString());
        }

        return strings;
    }

    /**
     * Converts a list of String to a list of matches.
     *
     * @return list of Matches.
     */
    public List<Match> stringToMatches(){

        StorageAdminInterface sa = new StorageAdmin();
        List<Match> matches = new ArrayList<>();
        Match match;

        List<String> temp = sa.restoreList(StorageAdminInterface.MATCHES, true);

        for(String s : temp){
            String[] attributes = s.split(StorageAdminInterface.ATTRIBUT_DELIMETER);

            /**
             * 1. CssClass
             * 2. MyTable
             * 3. boolean isFile as 0/1
             * 4. double percentage
             */
            CssClass cssClass;
            MyTable myTable;
            boolean isFile;
            Double percentage;

            if(attributes[0].equals("null")){
                cssClass = null;
            }else{
                String[] cssAttributes = attributes[0].split(MATCHES_ATTRIBUTE_DELIMETER);
                cssClass = new CssClass(new File(attributes[1]),
                        attributes[0],
                        Integer.parseInt(attributes[3]),
                        Integer.parseInt(attributes[2]),
                        Integer.parseInt(attributes[4]));
            }

            myTable = new MyTable(attributes[1]);

            if(attributes[2].equals("0")){
                isFile = false;
            }else {
                isFile = true;
            }

            if(attributes[3].equals("null")){
                percentage = null;
            }else{
                percentage = Double.parseDouble(attributes[3]);
            }

            match = new Match(cssClass,myTable,isFile,percentage);

            matches.add(match);

        }
        return matches;

    }
}
