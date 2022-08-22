package com.shuvo.ttit.onegpx.gpxConverter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class GPXWriterForMultiple {

    private static final String XML_HEADER = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>";
    private static final String TAG_GPX = "<gpx"
            + " xmlns=\"http://www.topografix.com/GPX/1/1\""
            + " version=\"1.1\""
            + " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""
            + " xsi:schemaLocation=\"http://www.topografix.com/GPX/1/1 http://www.topografix.com/GPX/1/1/gpx.xsd\">";
    private static final SimpleDateFormat POINT_DATE_FORMATTER = new SimpleDateFormat(
            "yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());

    public static void writeGpxFile(String trackName,
                                    ArrayList<String> trkpt, File target) throws IOException {
        FileWriter fw = new FileWriter(target);
        try {
            fw.write(XML_HEADER + "\n");
            fw.write(TAG_GPX + "\n");
            for (int i = 0; i < trkpt.size(); i++) {
                fw.write(trkpt.get(i));
            }

//            for (int i = 0; i< activities.size(); i++) {
//                ArrayList<LatLng> latLngs = activities.get(i).getMyLocation();
//                String desc = activities.get(i).getLength();
//                for (int c = 0; c < latLngs.size(); c++ ) {
//                    Log.i("Write: ", latLngs.get(c).toString() );
//                }
//                writeTrackPoints(trackName, fw, latLngs, desc);
//            }

//            for (Arraylatlng ddd : activities) {
//                ArrayList<LatLng> ddddddd= ddd.getMyLocation();
//                String desc = ddd.getLength();
//                writeTrackPoints(trackName, fw, ddddddd , desc);
//            }

            fw.write("</gpx>");
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static void writeTrackPoints(String trackName, FileWriter fw,
                                         String ttrrt, String des) throws IOException {
        fw.write("\t" + "<trk>" + "\n");
        fw.write("\t\t" + "<name>" + trackName + "</name>" + "\n");
        fw.write("\t\t" + "<desc>" + "Length: " +des + "</desc>" + "\n");
        fw.write("\t\t" + "<trkseg>" + "\n");
        fw.write(ttrrt);

//        for (int c = 0; c < listActivity.size(); c++ ) {
//            Log.i("Write: ", listActivity.get(c).toString() );
//            StringBuffer out = new StringBuffer();
//            out.append("\t\t\t" + "<trkpt lat=\""
//                    + listActivity.get(c).latitude + "\" " + "lon=\""
//                    + listActivity.get(c).longitude + "\">" + "\n");
////                out.append("\t\t\t\t" + "<ele>" + data.latitude
////                        + "</ele>" + "\n");
////                out.append("\t\t\t\t" + "<time>"
////                        + POINT_DATE_FORMATTER.format(new Date())
////                        + "</time>" + "\n");
////                out.append("\t\t\t\t" + "<cmt>speed="
////                        + data.longitude + "</cmt>" + "\n");
////                out.append("\t\t\t\t" + "<hdop>" + data.latitude
////                        + "</hdop>" + "\n");
//            out.append("\t\t\t" + "</trkpt>" + "\n");
//
//            fw.write(out.toString());
//        }
//
//        for (LatLng data : listActivity)
//             {
//                StringBuffer out = new StringBuffer();
//                out.append("\t\t\t" + "<trkpt lat=\""
//                        + data.latitude + "\" " + "lon=\""
//                        + data.longitude + "\">" + "\n");
////                out.append("\t\t\t\t" + "<ele>" + data.latitude
////                        + "</ele>" + "\n");
////                out.append("\t\t\t\t" + "<time>"
////                        + POINT_DATE_FORMATTER.format(new Date())
////                        + "</time>" + "\n");
////                out.append("\t\t\t\t" + "<cmt>speed="
////                        + data.longitude + "</cmt>" + "\n");
////                out.append("\t\t\t\t" + "<hdop>" + data.latitude
////                        + "</hdop>" + "\n");
//                out.append("\t\t\t" + "</trkpt>" + "\n");
//
//                fw.write(out.toString());
//            }

        fw.write("\t\t" + "</trkseg>" + "\n");
        fw.write("\t" + "</trk>" + "\n");
    }
}
