//@@author andyrobert3
package seedu.addressbook.common;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpResponseException;
import org.javatuples.Pair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Stores each Police Resource location and get ETA time
 * from different locations with Google Maps API
 */
public class Location {
    private static final String DISTANCE_MATRIX_BASE_URL = "https://maps.googleapis.com/maps/api/distancematrix/json?";
    private static final String GOOGLE_MAPS_BASE_URL = "https://www.google.com/maps/place/";

    private String googleMapsApiKey;
    private double longitude;
    private double latitude;

    public Location(double latitude, double longitude) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.googleMapsApiKey = getGoogleMapsApiKey();
    }

    public double getLongitude() {
        return longitude;
    }
    public double getLatitude() {
        return latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    private String getGoogleMapsApiKey() {
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader("env"));
            return bufferedReader.readLine();
        } catch (IOException ioe) {
            ioe.getMessage();
        }

        return null;
    }

    private ArrayList<Pair<Integer, String>> sortEta(ArrayList<Pair<Integer, String>> etaList) {
        etaList.sort(Comparator.comparing(Pair::getValue0));
        return etaList;
    }

    /**
     * Returns Google Maps API URL for HTTP GET request from multiple origins.
     *
     * @param origins multiple origins ETA to this location
     * @return Google Maps URL String
     */
    private String getMapsDistanceUrl(ArrayList<Location> origins) {
        StringBuilder originCoordinatesString = new StringBuilder(origins.get(0).latitude + ","
                + origins.get(0).longitude);

        for (int i = 1; i < origins.size(); i++) {
            originCoordinatesString
                    .append("|").append(origins.get(i).latitude).append(",").append(origins.get(i).longitude);
        }

        return DISTANCE_MATRIX_BASE_URL + "origins=" + originCoordinatesString
                + "&destinations=" + this.latitude + "," + this.longitude + "&key=" + googleMapsApiKey;
    }

    /**
     * Returns List of Pairs of Estimated Time of Arrival (ETA) from JSON ETA data
     *
     * @param jsonData from Google Maps GET Request
     * @return ArrayList of Pair- ETA in seconds, ETA in natural text
     */
    private ArrayList<Pair<Integer, String>> getEtaFromJsonObject(JSONObject jsonData) {
        ArrayList<Pair<Integer, String>> etaList = new ArrayList<>();

        try {
            JSONArray etaRows = jsonData.getJSONArray("rows");

            for (int i = 0; i < etaRows.length(); i++) {
                JSONArray etaTimeElements = etaRows.getJSONObject(i).getJSONArray("elements");
                JSONObject etaDuration = etaTimeElements.getJSONObject(0).getJSONObject("duration");

                Pair<Integer, String> durationPair = new Pair<>(etaDuration.getInt("value"),
                        etaDuration.getString("text"));
                etaList.add(durationPair);
            }

        } catch (JSONException jsonE) {
            jsonE.getMessage();
        }

        return etaList;
    }

    /**
     * Returns ETA for this location from multiple locations
     *
     * @param locations list of origins to this destination
     * @return ArrayList of Pair- Number of seconds ETA, Text description of ETA
     */

    public ArrayList<Pair<Integer, String>> getEtaFrom(ArrayList<Location> locations)
            throws IOException, JSONException {
        ArrayList<Pair<Integer, String>> etaList;

        HttpRestClient httpRestClient = new HttpRestClient();
        HttpResponse response = httpRestClient.requestGetResponse(getMapsDistanceUrl(locations));
        int responseStatusCode = response.getStatusLine().getStatusCode();

        if (responseStatusCode != 200 && responseStatusCode != 201 && responseStatusCode != 204) {
            throw new HttpResponseException(responseStatusCode, "Request to Google Maps API did not work. Try again");
        }

        String jsonString = IOUtils.toString(response.getEntity().getContent(), "UTF-8");
        etaList = getEtaFromJsonObject(new JSONObject(jsonString));

        return sortEta(etaList);
    }

    public String getGoogleMapsUrl() {
        return GOOGLE_MAPS_BASE_URL + this.getLatitude() + "," + this.getLongitude();
    }

}
