package com.enderpay.api;

import com.enderpay.Enderpay;
import com.enderpay.MessageBroadcaster;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class EnderpayApi {

    public static String API_DOMAIN = "https://api.enderpay.com";
    public static int API_VERSION = 1;

    public static String ENDPOINT_PLUGIN_LISTING_GET = "plugin/listing";
    public static String ENDPOINT_PLUGIN_STORE_GET = "plugin/store";
    public static String ENDPOINT_PLUGIN_COMMAND_QUEUE = "plugin/queue";

    public static String METHOD_GET = "GET";
    public static String METHOD_PUT = "PUT";

    public static String HEADER_API_KEY = "X-Enderpay-Api-Key";
    public static String HEADER_API_SECRET = "X-Enderpay-Api-Secret";

    private String apiKey;
    private String apiSecret;

    public EnderpayApi() {

        this.apiKey = Enderpay.getPlugin().getConfig().getString("api-key");
        this.apiSecret = Enderpay.getPlugin().getConfig().getString("api-secret");

    }

    public JSONObject makeRequest(String endpoint, String method, JSONObject requestBody) throws IOException {

        URL url = new URL(buildPath(endpoint));

        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        httpURLConnection.setRequestProperty("Accept", "*/*");

        // add headers
        httpURLConnection.setRequestProperty("Content-Type", "application/json");
        httpURLConnection.setRequestProperty(HEADER_API_KEY, this.apiKey);
        httpURLConnection.setRequestProperty(HEADER_API_SECRET, this.apiSecret);
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

        return new JSONObject(responseBody.toString());
    }

    private static String buildPath(String endpoint) {

        return API_DOMAIN + "/v" + API_VERSION + "/" + endpoint;
    }
}
