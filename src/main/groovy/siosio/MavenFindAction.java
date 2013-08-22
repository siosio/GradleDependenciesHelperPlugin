package siosio;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MavenFindAction {

    private static final Pattern PATTERN = Pattern.compile(
            "\\{\"id\":\"([^\"]+)\"[^\\}]+\"latestVersion\":\"([^\"]+)\"");

    public List<String> find(String text) {
        List<String> result = new ArrayList<String>();
        try {
            HttpURLConnection connection = getConnection(
                    "http://search.maven.org/solrsearch/select?q=a:" + text + "+OR+a:" + text + "*&rows=20&wt=json");

            InputStream stream = getResponse(connection);
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));

            String line = null;
            while ((line = reader.readLine()) != null) {
                Matcher matcher = PATTERN.matcher(line);
                while (matcher.find()) {
                    result.add(matcher.group(1) + ':' + matcher.group(2));
                }
            }
        } catch (IOException ignore) {
            return result;
        }
        return result;
    }

    private static HttpURLConnection getConnection(final String spec) throws IOException {
        URL url = new URL(spec);
        return (HttpURLConnection) url.openConnection();
    }

    private static InputStream getResponse(HttpURLConnection connection) throws IOException {
        if (connection.getResponseCode() != 200) {
            throw new IOException("response is abnormal end.");
        }
        return connection.getInputStream();
    }
}

