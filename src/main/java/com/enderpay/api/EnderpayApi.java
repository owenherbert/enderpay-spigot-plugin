package com.enderpay.api;

import com.enderpay.Enderpay;
import com.enderpay.MessageBroadcaster;
import org.bukkit.Bukkit;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class EnderpayApi {

    public static String API_DOMAIN = "https://api.enderpay.com";
    public static int API_VERSION = 1;

    public static String ENDPOINT_PLUGIN_LISTING_GET = "plugin/listing"; // the listing endpoint
    public static String ENDPOINT_PLUGIN_STORE_GET = "plugin/store"; // the store endpoint
    public static String ENDPOINT_PLUGIN_COMMAND_QUEUE = "plugin/queue"; // the queue endpoint
    public static String ENDPOINT_PLUGIN_DONATORS_GET = "plugin/donators"; // the donators endpoint
    public static String ENDPOINT_PLUGIN_PLAYERS = "plugin/players"; // the active players endpoint
    public static String ENDPOINT_PLUGIN_DONATION_PARTIES_GET = "plugin/donation-parties"; // the donation parties endpoint

    public static String METHOD_GET = "GET";
    public static String METHOD_PUT = "PUT";

    public static String HEADER_API_KEY = "X-Enderpay-Api-Key";
    public static String HEADER_API_SECRET = "X-Enderpay-Api-Secret";

    public void makeRequestAsync(final String endpoint, final String method, final JSONObject requestBody,
                                        final ApiResponseCallback apiResponseCallback) {

        // run API calls asynchronously to avoid server lag
        Bukkit.getScheduler().runTaskAsynchronously(Enderpay.getPlugin(), () -> {

            try {
                URL url = new URL(buildPath(endpoint));

                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestProperty("Accept", "*/*");

                // add headers
                httpURLConnection.setRequestProperty("Content-Type", "application/json");
                httpURLConnection.setRequestProperty(HEADER_API_KEY, Enderpay.getPlugin().getConfig().getString("api-key"));
                httpURLConnection.setRequestProperty(HEADER_API_SECRET, Enderpay.getPlugin().getConfig().getString("api-secret"));
                httpURLConnection.setRequestMethod(method);

                if (method == METHOD_PUT) {
                    httpURLConnection.setDoOutput(true);

                    try (DataOutputStream dataOutputStream = new DataOutputStream( httpURLConnection.getOutputStream())) {
                        dataOutputStream.write(requestBody.toString().getBytes());
                    }

                }

                InputStream inputStream = httpURLConnection.getInputStream();

                BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));

                StringBuilder responseBody = new StringBuilder();
                String currentLine;

                while ((currentLine = in.readLine()) != null) responseBody.append(currentLine);

                in.close();

                httpURLConnection.disconnect();

                apiResponseCallback.onResponse(new JSONObject(responseBody.toString()));
            } catch (Exception exception) {
                MessageBroadcaster.toConsole("An error occurred while communicating with the Enderpay API: " + exception.getMessage());
            }
        });

    }

    // builds the API endpoint based on the domain, version, and specified endpoint.
    private static String buildPath(String endpoint) {

        // check if debug mode is enabled
        boolean debugMode = Enderpay.getPlugin().getConfig().getBoolean("debug-mode");

        String domain = debugMode ? "http://api.localtest.me:8000" : API_DOMAIN;

        return domain + "/v" + API_VERSION + "/" + endpoint;
    }
}
