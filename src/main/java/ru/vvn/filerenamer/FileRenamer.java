
package ru.vvn.filerenamer;
import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class FileRenamer {
    private static String srcDir;
    private static String trgDir;
    private static String searchByRegex;
    private static Integer addMS;

    public static void main(String[] args){

        List<String> argList = Arrays.asList(args);
        System.out.println("args count: " + argList.size());
        if (argList.size() == 3 || argList.size() == 4){
            srcDir = argList.get(0); // "/home/vyalmiskin/dirtmp1/"; // 1
            searchByRegex = argList.get(1); //"[0-9]{8}-([0-9]{6})-DSC[0-9]{4}.jpg"; // 2
            addMS = Integer.valueOf(argList.get(2)) * 1000; //Integer.valueOf("60000"); // 3
            trgDir = argList.size() == 4 ? argList.get(3) : srcDir; //"/home/vyalmiskin/dirtmp1/"; // 4

        } else {
            System.out.println("! undefined args:\n"
                    + "1 - source directory\n"
                    + "2 - search by regex\n"
                    + "3 - add seconds\n"
                    + "4 - target directory (optional, default target = source)\n"
					+ " Example \"/home/vyalmiskin/dirtmp1/\" \"[0-9]{8}-([0-9]{6})-DSC[0-9]{4}.jpg\" \"60\" \"/home/vyalmiskin/dirtmp1/\"");
            return;
        }
        System.out.println("start");
        argList.forEach(a -> System.out.println(a));
        Pattern pattern = Pattern.compile(searchByRegex);

        DateFormat formatter = new SimpleDateFormat("HHmmss");
        File directory = new File(srcDir);
        List<File> files = Arrays.asList(directory.listFiles());
        System.out.println("all files count: " + files.size());
        files = files.stream().filter(f -> f.getName().matches(searchByRegex)).collect(Collectors.toList());
        System.out.println("filtered files count: " + files.size());
        files.forEach(f -> {
            //String absolutePath = f.getAbsolutePath(); 
            String name = f.getName();

            Matcher matcher = pattern.matcher(name);
            matcher.matches();
            String timeStr = matcher.group(1);
            Date date = null;
            try {
                date = (Date)formatter.parse(timeStr);
                Date newDate = new Date(date.getTime() + addMS);
                String newName = name.replace(timeStr, formatter.format(newDate));
                File renameTo = new File(trgDir + newName); // new File(f.getAbsolutePath().replace(name, newName));
                if (!renameTo.exists()){
                    try {
                        if (f.renameTo(renameTo)) {
                            System.out.println("renamed " + f.getAbsolutePath() + " to " + renameTo.getAbsolutePath());
                        } else {
                            throw new Exception();
                        }
                    } catch (Exception e){
                        System.out.println("cant rename " + f.getAbsolutePath() + " to " + renameTo.getAbsolutePath());
                    }
                } else {
                    System.out.println("cant rename " + f.getAbsolutePath() + " to " + renameTo.getAbsolutePath() + ". its exist");
                }
            } catch (ParseException ex) {
                System.out.println("cant parse time " + timeStr + " in file " + f.getAbsolutePath());
            }
        });
    }
}
