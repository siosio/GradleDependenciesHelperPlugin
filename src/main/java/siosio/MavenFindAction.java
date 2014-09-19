package siosio;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MavenFindAction {

    /** mavenのアクセス結果からライブラリ名と最新バージョンを抽出する正規表現 */
    private static final Pattern PATTERN = Pattern.compile(
            "\\{\"id\":\"((?:[^:]+:){2})[^\"]+\"");
    private static final Pattern VERSION_PATTERN = Pattern.compile(
            "\\{\"id\":\"([^\"]+)\"");

    /** url */
    private static final String FIND_URL =
            "http://search.maven.org/solrsearch/select?q=a:\"%s\"&core=gav&rows=30&wt=json";

    /** get version url */
    private static final String FIND_VERSION_URL =
            "http://search.maven.org/solrsearch/select?q=g:\"%s\"+AND+a:\"%s\"&rows=20&core=gav&wt=json";


    public Set<String> find(String text) {
        return getMavenResult(String.format(FIND_URL, text), PATTERN);
    }

    public Set<String> findVersion(String group, String name) {
        return getMavenResult(String.format(FIND_VERSION_URL, group, name), VERSION_PATTERN);
    }

    private Set<String> getMavenResult(String url, Pattern pattern) {
        TreeSet<String> result = new TreeSet<String>();
        BufferedReader reader = null;
        try {
            HttpURLConnection connection = getConnection(url);

            InputStream stream = getResponse(connection);
            reader = new BufferedReader(new InputStreamReader(stream));

            String line;
            while ((line = reader.readLine()) != null) {
                Matcher matcher = pattern.matcher(line);
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
        return result.descendingSet();
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

