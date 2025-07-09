package com.shuvo.ttit.onegpx.gpxConverter;

import android.location.Location;
import android.util.Log;

import com.shuvo.ttit.onegpx.arrayLists.ArrrayFile;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class GPXFileDecoder {

    public static ArrayList<Location> decodeGPX(File file){
        ArrayList<Location> list = new ArrayList<Location>();

        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            FileInputStream fileInputStream = new FileInputStream(file);
            Document document = documentBuilder.parse(fileInputStream);
            Element elementRoot = document.getDocumentElement();

            NodeList nodelist_trkpt = elementRoot.getElementsByTagName("trkpt");

            for(int i = 0; i < nodelist_trkpt.getLength(); i++){

                Node node = nodelist_trkpt.item(i);
                NamedNodeMap attributes = node.getAttributes();

                String newLatitude = attributes.getNamedItem("lat").getTextContent();
                Double newLatitude_double = Double.parseDouble(newLatitude);

                String newLongitude = attributes.getNamedItem("lon").getTextContent();
                Double newLongitude_double = Double.parseDouble(newLongitude);

                String newLocationName = newLatitude + ":" + newLongitude;
                Location newLocation = new Location(newLocationName);
                newLocation.setLatitude(newLatitude_double);
                newLocation.setLongitude(newLongitude_double);

                list.add(newLocation);

            }

            fileInputStream.close();

        } catch (ParserConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SAXException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return list;
    }

    public static String decoder(InputStream inputStream) {
        String desc = "";
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document = documentBuilder.parse(inputStream);
            Element elementRoot = document.getDocumentElement();

            NodeList nameww = elementRoot.getElementsByTagName("desc");



            //desc = nameww;

            for(int i = 0; i < nameww.getLength(); i++){

                Node node = nameww.item(i);
                String attributes = node.getTextContent();


//                String newLatitude = attributes.getNamedItem("lat").getTextContent();
//                Double newLatitude_double = Double.parseDouble(newLatitude);
//
//                String newLongitude = attributes.getNamedItem("lon").getTextContent();
//                Double newLongitude_double = Double.parseDouble(newLongitude);
//
//                String newLocationName = newLatitude + ":" + newLongitude;
//                Location newLocation = new Location(newLocationName);
//                newLocation.setLatitude(newLatitude_double);
//                newLocation.setLongitude(newLongitude_double);

//                list.add(newLocation);
                System.out.println(attributes);
                desc += attributes + "\n";

            }

            inputStream.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (desc.endsWith("\n")) {
            desc = desc.substring(0, desc.length() - 1);
        }
        return desc;
    }

    public static ArrayList<ArrrayFile> multiLine(InputStream inputStream){
        ArrayList<ArrrayFile> list = new ArrayList<>();
        String lens = "";

        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document = documentBuilder.parse(inputStream);
            Element elementRoot = document.getDocumentElement();

            NodeList nodelist_trkpt = elementRoot.getElementsByTagName("trk");

            for (int i = 0; i < nodelist_trkpt.getLength(); i++){
                ArrayList<Location> lllList = new ArrayList<Location>();

                Node node = nodelist_trkpt.item(i);
                Log.i("Node", node.toString());
                //NodeList nodeList = elementRoot.getElementsByTagName("trkpt");
                NodeList attributesTRK = node.getChildNodes();
                Log.i("Child Node", attributesTRK.toString());

                String attributesTRKD = node.getNodeName();
                String attributesTRKDe = node.getNodeName();
                Log.i("Node Name", attributesTRKDe.toString());

                //NodeList nodeList = elementRoot.getElementsByTagName("trkpt");

                for (int j = 0; j < attributesTRK.getLength(); j++) {
                    Node node1 = attributesTRK.item(j);

                    String name = node1.getNodeName();
                    Log.i("Child Node Name", name);

                    if (name.contains("desc")) {
                        lens = node1.getTextContent();
                        Log.i("Length From Desc:", lens);
                    }

                    NodeList newNode = node1.getChildNodes();

                    for (int k = 0; k < newNode.getLength(); k++) {
                        Node node2 = newNode.item(k);
                        String bb = node2.getNodeName();
                        Log.i("Node Node", bb);

                        if (bb.contains("trkpt")) {
                            NamedNodeMap attributes = node2.getAttributes();
                            Log.i("Attrr", attributes.toString());
                            String newLatitude = attributes.getNamedItem("lat").getTextContent();
                            Double newLatitude_double = Double.parseDouble(newLatitude);
                            Log.i("LAt; " , newLatitude);
                            String newLongitude = attributes.getNamedItem("lon").getTextContent();
                            Double newLongitude_double = Double.parseDouble(newLongitude);

                            String newLocationName = newLatitude + ":" + newLongitude;
                            Location newLocation = new Location(newLocationName);
                            newLocation.setLatitude(newLatitude_double);
                            newLocation.setLongitude(newLongitude_double);

                            lllList.add(newLocation);
                        }


//                        String newLatitude = attributes.getNamedItem("lat").getTextContent();
//                        Double newLatitude_double = Double.parseDouble(newLatitude);
//
//                        Log.i("LAt; " , newLatitude);


                    }
                }

//                for (int j = 0; j < nodeList.getLength(); j++) {
//                    Node node1 = nodelist_trkpt.item(j);
//                    String no = node1.getNodeName();
//                    Log.i("NAMEEEEE", no);
//                    NamedNodeMap attributes = node1.getAttributes();
//                    String newLatitude = attributes.getNamedItem("lat").getTextContent();
//                    Double newLatitude_double = Double.parseDouble(newLatitude);
//
//                    String newLongitude = attributes.getNamedItem("lon").getTextContent();
//                    Double newLongitude_double = Double.parseDouble(newLongitude);
//
//                    String newLocationName = newLatitude + ":" + newLongitude;
//                    Location newLocation = new Location(newLocationName);
//                    newLocation.setLatitude(newLatitude_double);
//                    newLocation.setLongitude(newLongitude_double);
//
//                    list.add(newLocation);
//                }


                list.add(new ArrrayFile(lllList, "File" + i, lens));
            }

            inputStream.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public static ArrayList<Location> decodeWPT(InputStream inputStream) {
        ArrayList<Location> list = new ArrayList<>();

        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document = documentBuilder.parse(inputStream);
            Element elementRoot = document.getDocumentElement();

            NodeList nodelist_trkpt = elementRoot.getElementsByTagName("wpt");

            for (int i = 0; i < nodelist_trkpt.getLength(); i++) {

                Node node = nodelist_trkpt.item(i);
                NamedNodeMap attributes = node.getAttributes();

                String newLatitude = attributes.getNamedItem("lat").getTextContent();
                Double newLatitude_double = Double.parseDouble(newLatitude);

                String newLongitude = attributes.getNamedItem("lon").getTextContent();
                Double newLongitude_double = Double.parseDouble(newLongitude);

                String newLocationName = newLatitude + ":" + newLongitude;
                Location newLocation = new Location(newLocationName);
                newLocation.setLatitude(newLatitude_double);
                newLocation.setLongitude(newLongitude_double);

                list.add(newLocation);

            }

            inputStream.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }
}
