package siosio;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jetbrains.idea.maven.model.MavenArtifactInfo;
import org.jetbrains.idea.maven.services.MavenRepositoryServicesManager;

public class MavenFindAction {

    public Set<String> find(String text) {
        String[] urls = MavenRepositoryServicesManager.getServiceUrls();
        MavenArtifactInfo info = new MavenArtifactInfo(null, null, null, "jar", null, text, null);
        Set<String> result = new HashSet<String>();
        for (String url : urls) {
            List<MavenArtifactInfo> artifacts = MavenRepositoryServicesManager.findArtifacts(info, url);
            for (MavenArtifactInfo artifact : artifacts) {
                if (artifact == null) {
                    return result;
                }
                result.add(artifact.toString());
                if (result.size() >= 20) {
                    return result;
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

