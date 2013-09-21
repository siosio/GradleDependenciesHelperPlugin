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

    /** mavenのアクセス結果からライブラリ名と最新バージョンを抽出する正規表現 */
    private static final Pattern PATTERN = Pattern.compile(
            "\\{\"id\":\"([^\"]+)\"");

    /** url */
    private static final String FIND_URL =
            "http://search.maven.org/solrsearch/select?q=a:\"%s\"&core=gav&rows=30&wt=json";

    public List<String> find(String text) {

        List<String> result = new ArrayList<String>(30);
        BufferedReader reader = null;
        try {
            HttpURLConnection connection = getConnection(String.format(FIND_URL, text));

            InputStream stream = getResponse(connection);
            reader = new BufferedReader(new InputStreamReader(stream));

            String line = null;
            while ((line = reader.readLine()) != null) {
                Matcher matcher = PATTERN.matcher(line);
                while (matcher.find()) {
                    result.add(matcher.group(1));
                }
            }
        } catch (IOException ignore) {
            return result;
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException ignore) {
                }
            }
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

