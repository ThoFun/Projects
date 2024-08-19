package ch.zhaw.it.pm3.model;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

public final class  Finder {
    private static List<Advertisement> possibleAdvertisements = new ArrayList<>();
    private static double maxCommuteDistance = 50;
    private static HashMap<Integer,ArrayList<Double>> geoLocationMap = new HashMap<>();

    /**
     * findAdvertisements is the method to be called when the provider wants to search for a job
     * @param advertisements
     * @param serviceProvider
     * @return a list of possible Advertisements for the service provider
     * @throws IOException
     */
    public static List<Advertisement> findAdvertisements(List<Advertisement> advertisements, ServiceProvider serviceProvider) {
        try {
            if(!advertisements.isEmpty() && serviceProvider != null) {
                //Build a map with all regions
                for (String region : readCSVFile()){
                    createGeoLocationMap(splitLine(region));
                }
                //Filter the advertisements according to skill and commute distance
                for (Advertisement advertisement : advertisements) {
                    if (filterSkillLevel(advertisement, serviceProvider) && isInDistance(advertisement,serviceProvider)) {
                        possibleAdvertisements.add(advertisement);
                    }
                }
            }
        } catch (IOException exception) {
            exception.printStackTrace();
        }

        return possibleAdvertisements;
    }

    /**
     * Verifies whether the service provider has the necessary skill
     * @param advertisement
     * @param serviceprovider
     * @return true if one skill has matched.
     */
    private static boolean filterSkillLevel(Advertisement advertisement, ServiceProvider serviceprovider){
        Skills requiredSkill = advertisement.getRequiredSkill();
        Skills providerSkill = serviceprovider.getSkill();
        return providerSkill.equals(requiredSkill);
    }

    /**
     * Verifies whether the commute distance would be too long.
     * TODO: Change hard-coded values with advertisement and serviceProvier properties.
     * @param advertisement
     * @param serviceProvider
     * @return true or false
     */
    private static boolean isInDistance(Advertisement advertisement, ServiceProvider serviceProvider ){
        int latitudeIndex = 0;
        int longitudeIndex = 1;

        double latitudeAdv = geoLocationMap.get(advertisement.getPostalCode()).get(latitudeIndex);
        double longitudeAdv = geoLocationMap.get(advertisement.getPostalCode()).get(longitudeIndex);
        double latitudeSer = geoLocationMap.get(serviceProvider.getPostalCode()).get(latitudeIndex);
        double longitudeSer = geoLocationMap.get(serviceProvider.getPostalCode()).get(longitudeIndex);

        return calculateDistance(latitudeAdv,longitudeAdv,latitudeSer,longitudeSer) < maxCommuteDistance;
    }
    /**
     * Example:
     * Reads a CSV row in the format A: Postcode B: Longitude C:Altitude,
     * Turns it into {Postcode,Latitude, Longitude} and stores it in an array.
     * @return returns an array with all regions
     */

    private static List<String> readCSVFile() throws IOException {
        String path = "files/geonames-postal-code.csv";
        InputStream file = Finder.class.getClassLoader().getResourceAsStream(path);
        List<String> content = new ArrayList<>();

        try(BufferedReader br = new BufferedReader(new InputStreamReader(file))) {
            String line = "";
            while ((line = br.readLine()) != null) {
                content.add(line);
            }
        } catch (FileNotFoundException e) {
        }
        return content;
    }

    /**
     * Split a string into single elements and return them in an string array.
     * Example: "8500, 42.2333, 7.0233" into {8500, 42.2333, 7.0233}
     * @param line
     * @return
     */
    private static String[] splitLine(String line){

        String[] strArray = null;
        String patternStr = ",";
        Pattern ptr = Pattern.compile(patternStr);
        strArray = ptr.split(line);

        return strArray;
    }

    /**
     * Puts a string array into a hashmap.
     * Example {9500, 47.2333, 7.2311} into <Postcode,ArrayList<Double>
     * @param strArray
     * @return geoLocationMap<Integer, {Latitude, Longitude}
     */
    private static HashMap<Integer,ArrayList<Double>> createGeoLocationMap(String[] strArray){
        //Indices
        int postcodeIndex = 0;
        int latitudeIndex = 1;
        int longitudeIndex = 2;

        //Postcode
        Integer postcode = Integer.parseInt(strArray[postcodeIndex]);

        //Geo Coordinates
        ArrayList<Double> coordinates = new ArrayList<>();
        coordinates.add(Double.valueOf(strArray[latitudeIndex]));
        coordinates.add(Double.valueOf(strArray[longitudeIndex]));

        geoLocationMap.put(postcode,coordinates);

        return geoLocationMap;
    }

    /**
     * Distance calculation between two coordinates using the Haversine Formula
     * @param lat1
     * @param lon1
     * @param lat2
     * @param lon2
     * @return Distance in Kilometers
     */
    private static double calculateDistance(double lat1, double lon1, double lat2, double lon2){

        // Distance between latitudes and longitudes
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        // Convert To radians
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);

        // Formula
        double a = Math.pow(Math.sin(dLat / 2), 2) +
                Math.pow(Math.sin(dLon / 2), 2) *
                        Math.cos(lat1) *
                        Math.cos(lat2);
        double rad = 6371;
        double c = 2 * Math.asin(Math.sqrt(a));
        return rad * c;
    }
}
