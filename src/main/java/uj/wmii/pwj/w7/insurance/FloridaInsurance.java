package uj.wmii.pwj.w7.insurance;

import java.io.*;
import java.math.BigDecimal;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;


class InsuranceEntry {
    Map<String, String> traits = new HashMap<>();
}

public class FloridaInsurance {

    public static void fileMaker (String filename, String contents) throws IOException {
        File file = new File(filename);
        BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        writer.write(contents);
        writer.close();
    }

    public static void count(List<InsuranceEntry> list) throws IOException {
        List<String> counties = new ArrayList<>();
        for (InsuranceEntry entry : list) {
            String county = entry.traits.get("county");
            if (!counties.contains(county))
                counties.add(county);
        }
        fileMaker("count.txt", Integer.toString(counties.size()));
    }


    public static void tiv2012(List<InsuranceEntry> list) throws IOException {
        BigDecimal sum = BigDecimal.ZERO;
        for (InsuranceEntry entry : list) {
            String sValue = entry.traits.get("tiv_2012");
            BigDecimal value = new BigDecimal(sValue);
            sum = sum.add(value);
        }
        fileMaker("tiv2012.txt", sum.toString());
    }


    public static void most_valuable(List<InsuranceEntry> list) throws IOException {
        Map<String, Double> counties = new HashMap<>();

        for (InsuranceEntry entry : list) {
            String county = entry.traits.get("county");
            Double before = Double.parseDouble(String.valueOf(entry.traits.get("tiv_2011")));
            Double after = Double.parseDouble(String.valueOf(entry.traits.get("tiv_2012")));
            Double gain = after - before;
            if (counties.containsKey(county))
                counties.put(county, counties.get(county) + gain);
            else
                counties.put(county, gain);
        }

        List<Map.Entry<String, Double>> countyList = new ArrayList<>(counties.entrySet());
        countyList.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));

        String table = "country,value\n";
        for (int i = 0; i < 10; i++)
            table += countyList.get(i).getKey() + "," + String.format(Locale.US,"%.2f", countyList.get(i).getValue()) + "\n";

        fileMaker("most_valuable.txt", table);
    }


    public static void main(String[] args) {

        try {
            ZipFile file = new ZipFile("FL_insurance.csv.zip");
            ZipEntry table = file.getEntry("FL_insurance.csv");
            InputStream is = file.getInputStream(table);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            String[] keys = reader.readLine().split(",");

            List<InsuranceEntry> list = new ArrayList<>();

            reader.lines().forEach(line -> {
                list.add(new InsuranceEntry());
                String[] values = line.split(",");
                for (int i = 0; i < keys.length; i++)
                    list.getLast().traits.put(keys[i], values[i]);
            });

            count (list);
            tiv2012(list);
            most_valuable(list);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
