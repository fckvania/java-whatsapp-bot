package mybot.maple.lib;

import java.io.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

public class Functions {

    /**
     *
     * @param url
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    public String Fetch(String url) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .build();
        HttpResponse<String> response = client.send(request,
                HttpResponse.BodyHandlers.ofString());

        return response.body();
    }

    /**
     *
     * @param stream
     * @return
     * @throws IOException
     */
    public String convertStreamToString(InputStream stream) throws IOException {
        if (stream != null) {
            Writer writer = new StringWriter();

            char[] buffer = new char[1024];
            try (stream) {
                Reader reader = new BufferedReader(new InputStreamReader(stream,
                        StandardCharsets.UTF_8));
                int length;
                while ((length = reader.read(buffer)) != -1) {
                    writer.write(buffer, 0, length);
                }
            }
            return writer.toString();
        }
        return "";
    }
}
