package com.shuvo.ttit.onegpx.gpxConverter;

import com.google.android.gms.maps.model.LatLng;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class GPXFileWriter {

    private static final String XML_HEADER = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>";
    private static final String TAG_GPX = "<gpx"
            + " xmlns=\"http://www.topografix.com/GPX/1/1\""
            + " version=\"1.1\""
            + " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""
            + " xsi:schemaLocation=\"http://www.topografix.com/GPX/1/1 http://www.topografix.com/GPX/1/1/gpx.xsd\">";
    private static final SimpleDateFormat POINT_DATE_FORMATTER = new SimpleDateFormat(
            "yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());

    public static void writeGpxFile(String trackName,
                                    ArrayList<LatLng> activities, File target, String desc) throws IOException {
        FileWriter fw = new FileWriter(target);
        try {
            fw.write(XML_HEADER + "\n");
            fw.write(TAG_GPX + "\n");
            writeTrackPoints(trackName, fw, activities, desc);
            fw.write("</gpx>");
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static void writeTrackPoints(String trackName, FileWriter fw,
                                         ArrayList<LatLng> listActivity, String des) throws IOException {
        fw.write("\t" + "<trk>" + "\n");
        fw.write("\t\t" + "<name>" + trackName + "</name>" + "\n");
        fw.write("\t\t" + "<desc>" + "Length: " +des + "</desc>" + "\n");
        fw.write("\t\t" + "<trkseg>" + "\n");

        for (LatLng data : listActivity)
        {
            StringBuffer out = new StringBuffer();
            out.append("\t\t\t" + "<trkpt lat=\""
                    + data.latitude + "\" " + "lon=\""
                    + data.longitude + "\">" + "\n");
//                out.append("\t\t\t\t" + "<ele>" + data.latitude
//                        + "</ele>" + "\n");
//                out.append("\t\t\t\t" + "<time>"
//                        + POINT_DATE_FORMATTER.format(new Date())
//                        + "</time>" + "\n");
//                out.append("\t\t\t\t" + "<cmt>speed="
//                        + data.longitude + "</cmt>" + "\n");
//                out.append("\t\t\t\t" + "<hdop>" + data.latitude
//                        + "</hdop>" + "\n");
            out.append("\t\t\t" + "</trkpt>" + "\n");

            fw.write(out.toString());
        }

        fw.write("\t\t" + "</trkseg>" + "\n");
        fw.write("\t" + "</trk>" + "\n");
    }
}
