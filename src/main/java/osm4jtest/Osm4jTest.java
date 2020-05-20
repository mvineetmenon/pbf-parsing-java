/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package osm4jtest;

import de.topobyte.osm4j.core.model.iface.EntityContainer;
import de.topobyte.osm4j.core.model.iface.OsmNode;
import de.topobyte.osm4j.core.model.util.OsmModelUtil;
import de.topobyte.osm4j.pbf.seq.PbfIterator;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

/**
 *
 * @author vineet
 */
public class Osm4jTest {
    
    String fileName; 
    
    public Osm4jTest(String fileName){
        this.fileName = fileName;
    }

    public String nearestNode(double lat, double lon) {
        try ( FileInputStream input = new FileInputStream(fileName)) {
            Iterator<EntityContainer> iterator = new PbfIterator(input, false);

            double shortestDistance = 0;
            String cityName = "";

            while (iterator.hasNext()) {
                EntityContainer entityContainer = iterator.next();
                switch (entityContainer.getType()) {
                    case Node:
                        OsmNode ent = (OsmNode) entityContainer.getEntity();

                        Map map = OsmModelUtil.getTagsAsMap(ent);
                        if (map.containsKey("place") && (map.get("place").equals("municipality") || map.get("place").equals("city") || map.get("place").equals("town"))) {
                            String name = OsmModelUtil.getTagsAsMap(ent).get("name");
                            double entlat = ent.getLatitude();
                            double entlon = ent.getLongitude();
                            double currentDistance = Utils.getDistanceFromLatLonInKm(lat, lon, entlat, entlon);
                            if ((shortestDistance == 0) || (shortestDistance > currentDistance)) {
                                shortestDistance = currentDistance;
                                cityName = name;
                            }
                            //System.out.println(map.get("place") + " " + map.get("name") + ": " + ent.getLatitude() + ", " + ent.getLongitude() + " at a distance of " + Utils.getDistanceFromLatLonInKm(lat, lon, entlat, entlon));
                        }
                        break;
                }
            }
            return "Nearest City is " + cityName + " at a distance of " + shortestDistance + " from " + lat + ", " + lon;
        } catch (IOException e) {
            return "";
        }
    }

    public static void main(String[] args) {
        //System.out.println(args[0]);
        String fileName = args[0];
        String strLat = args[1];
        String strLon = args[2];
        double lat = Double.valueOf(strLat);
        double lon = Double.valueOf(strLon);
        System.out.println(new Osm4jTest(fileName).nearestNode(lat, lon));
    }
}
