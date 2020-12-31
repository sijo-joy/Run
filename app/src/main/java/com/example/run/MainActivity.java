package com.example.run;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MainActivity extends AppCompatActivity {
    private static ArrayList<Double> speed_list;
    private static HashMap<Integer, Double> hashmap_distance = new HashMap<Integer, Double>();
    private static HashMap<Integer, Long> hashmap_time = new HashMap<Integer, Long>();
    private static Double total_distance;
    private static long total_sec;
    private static String total_speed_final = "";
    private LocationManager lm;
    private File file_to_write;
    private File file_to_write2;
    private BufferedWriter bw;
    private BufferedWriter bw2;
    private String location_data_to_write;
    private Button start;
    private LocationListener loc_lis;
    private static TextView avgSpeed;
    private static TextView KmavgSpeed;
    private Button stop;
    CustomViewLive custom_view;
    private static DecimalFormat df2;
    Date currentTime;
    DateFormat dateFormat;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        custom_view =  findViewById(R.id.customView);
        start = (Button) findViewById(R.id.start);
        df2 = new DecimalFormat("#.##");
        avgSpeed = (TextView) findViewById(R.id.avgSpeed);
        KmavgSpeed = (TextView) findViewById(R.id.KmavgSpeed);
        stop = (Button) findViewById(R.id.stop);
        stop.setEnabled(false);

    }
    public static void search(final String pattern, final File folder, List<String> result) {
        for (final File f : folder.listFiles()) {

            if (f.isDirectory()) {
                search(pattern, f, result);
            }

            if (f.isFile()) {
                if (f.getName().matches(pattern)) {
                    result.add(f.getAbsolutePath());
                }
            }


        }
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void startListening(View view) throws ParseException {
        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        stop.setEnabled(true);
        String dd = String.valueOf(getFilesDir());
        final File folder = new File(dd);

        List<String> result = new ArrayList<>();

        search(".*\\.txt", folder, result);
        for (String s : result) {
            System.out.println(s);
        }
//        String ddd = "52.99317055\t-3.03067674\t2019-08-19 09:08:52\t110.0\n" +
//                "52.99204123\t-3.03114418\t2019-08-19 09:08:57\t110.0\n" +
//                "52.99096218\t-3.03167918\t2019-09-19 09:09:02\t110.0\n" +
//                "52.98986645\t-3.03238693\t2019-09-19 09:09:07\t110.0";
//        getAvgSpeedPerKm(ddd,true);
        start.setEnabled(false);
        currentTime = Calendar.getInstance().getTime();
        dateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
        String strDate = dateFormat.format(currentTime);
        String file_name = strDate+".txt";
        file_to_write = new File(getFilesDir(), file_name);
        file_to_write2 = new File("D:\\Studies\\Android\\assignment3", "hh.txt");
        location_data_to_write = "";
        loc_lis = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                currentTime = Calendar.getInstance().getTime();
                dateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
                String strDate = dateFormat.format(currentTime);
                location_data_to_write += location.getLatitude() + "\t" + location.getLongitude() + "\t" + strDate + "\t" + location.getAltitude() +"\n";
                try {
                    getAvgSpeedPerKm(location_data_to_write,false);
                    custom_view.init();
                    custom_view.invalidate();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
        addLocationListener();
    }
    public static void getAvgSpeedPerKm(String location_data, boolean finished) throws ParseException {
        total_distance = 0.0;
        total_sec = 0;
        String[] line_locaion_data = location_data.split("\n");
        speed_list = new ArrayList<Double>();
        double start_lat1 = 0.0;
        double lat1 = 0.0;
        double start_lon1 = 0.0;
        double lon1 = 0.0;
        double lat2 = 0.0;
        double stop_lat2 = 0.0;
        double stop_lon2 = 0.0;
        double lon2 = 0.0;
        String start_time = "";
        String start_start_time = "";
        String stop_time = "";
        String stop_stop_time = "";
        int count_i = 0;
        int count_hm = 0;

        for(String loc_data : line_locaion_data){

            String[] each_loc_data = loc_data.split("\t");
            if (each_loc_data.length >= 3){
                if (count_i == 0){
                    start_start_time = each_loc_data[2];
                    start_lat1 = Double. parseDouble(each_loc_data[0]);
                    start_lon1 = Double. parseDouble(each_loc_data[1]);
                }
                stop_lat2 = Double. parseDouble(each_loc_data[0]);
                stop_lon2 = Double. parseDouble(each_loc_data[1]);
                stop_stop_time = each_loc_data[2];
                count_i++;
                if(lat1 == 0.0 && lon1 == 0.0){
                    lat1 = Double. parseDouble(each_loc_data[0]);
                    lon1 = Double. parseDouble(each_loc_data[1]);
                    start_time = each_loc_data[2];
                }
                else if(lat1 != 0.0 && lat2 == 0.0){
                    lat2 = Double. parseDouble(each_loc_data[0]);
                    lon2 = Double. parseDouble(each_loc_data[1]);
                    Double distance = findDistance(lat1, lon1, lat2, lon2, "K");
                    if(distance >= 1.0 ||  count_i == line_locaion_data.length){
                        count_hm++;
                        stop_time = each_loc_data[2];
                        Date start_time_date=new SimpleDateFormat("yyyy-mm-dd hh:mm:ss").parse(start_time);
                        Date stop_time_date=new SimpleDateFormat("yyyy-mm-dd hh:mm:ss").parse(stop_time);
                        String from_time = new SimpleDateFormat("H:mm:ss").format(start_time_date);
                        String to_time = new SimpleDateFormat("H:mm:ss").format(stop_time_date);
                        SimpleDateFormat time_format = new SimpleDateFormat("HH:mm:ss");
                        Date date1 = time_format.parse(from_time);
                        Date date2 = time_format.parse(to_time);
                        long time_diff = date2.getTime() - date1.getTime();
                        total_sec += time_diff;
                        total_distance += distance;
                        hashmap_distance.put(count_hm, distance);
                        hashmap_time.put(count_hm, total_sec);
                        Double time_diff_hours   = (Double) (((time_diff / 1000) / 60.0) / 60.0) ;
                        Double speed = distance / time_diff_hours;

                        speed_list.add(speed);

                        lat1 = 0.0;
                        lon1 = 0.0;
                        start_time = "";
                        lat2 = 0.0;
                        lon2 = 0.0;
                        stop_time = "";
                    }

                    else{
                        lat2 = 0.0;
                        lon2 = 0.0;
                        stop_time = "";

                    }
                }
            }

        }

        int count = 1;
        Double tot_speed = 0.0;
        total_speed_final = "";
        for (Double speed : speed_list) {
            if(count == speed_list.size()) {
                total_speed_final += "Last " + ":\t" + String.valueOf(df2.format(speed)) + "\n";
            }
            else{
                total_speed_final += "KM " + count + ":\t" + String.valueOf(df2.format(speed)) + "\n";
            }

            tot_speed += speed;
            count++;
        }
        Double total_avng_speed  = tot_speed / speed_list.size();
        KmavgSpeed.setText(String.valueOf(total_speed_final));
        avgSpeed.setText("Overall avg speed : "+String.valueOf(df2.format(total_avng_speed)));

    }
    public void stopListening(View view) throws ParseException {
        stop.setEnabled(false);
        start.setEnabled(true);
        lm.removeUpdates(loc_lis);
        lm = null;
        getAvgSpeedPerKm(location_data_to_write,true);
        custom_view.init();
        custom_view.invalidate();
        try {
            bw = new BufferedWriter(new FileWriter(file_to_write));
//            getAvgSpeed(location_data_to_write);

            bw.write(location_data_to_write);
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public Double getTotKm(){
//        ArrayList<Double> speed_list = new ArrayList<Double>();
        if(total_distance == null){
            return 0.0;
        }
        return total_distance;
    }
    public HashMap getDistanceHm(){
        return hashmap_distance;
    }
    public HashMap getTimeHm(){
        return hashmap_time;
    }
    public int getTotSec(){
//        ArrayList<Double> speed_list = new ArrayList<Double>();
        if( total_sec > 1000){
            int time_in_sec = (int) total_sec / 1000;
            return time_in_sec;
        }
        return 0;
    }
    public ArrayList getDatas(){
//        ArrayList<Double> speed_list = new ArrayList<Double>();
        return speed_list;
    }
    private static double findDistance(double lat1, double lon1, double lat2, double lon2, String unit) {
        if ((lat1 == lat2) && (lon1 == lon2)) {
            return 0;
        }
        else {
            double theta = lon1 - lon2;
            double dist = Math.sin(Math.toRadians(lat1)) * Math.sin(Math.toRadians(lat2)) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.cos(Math.toRadians(theta));
            dist = Math.acos(dist);
            dist = Math.toDegrees(dist);
            dist = dist * 60 * 1.1515;
            if (unit.equals("K")) {
                dist = dist * 1.609344;
            } else if (unit.equals("N")) {
                dist = dist * 0.8684;
            }
            return (dist);
        }
    }
    // private method that will add a location listener to the location manager
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void addLocationListener() {
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    Activity#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for Activity#requestPermissions for more details.
            return;
        }
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5, loc_lis );
    }
}
