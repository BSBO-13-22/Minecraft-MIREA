package fun.mirea.common.network;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.http.HttpHeaders;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class MireaApiClient {

    private static final String GROUP_DATA_URL = "https://mirea.xyz/api/v1.3/groups/certain?name=%s";
    private static final String CURRENT_WEEK_URL = "https://mirea.xyz/api/v1.3/time/week";
    private final CloseableHttpClient client;
    private final RequestConfig config;

    public MireaApiClient() {
        HttpClientBuilder clientBuilder = HttpClients.custom();
        clientBuilder.setDefaultHeaders(List.of(new BasicHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType())));
        this.client = clientBuilder.build();
        this.config = RequestConfig.custom()
                .setSocketTimeout(3000)
                .setConnectTimeout(3000)
                .setConnectionRequestTimeout(3000)
                .build();
    }

    public CompletableFuture<JsonObject> getGroupSchedule(String groupName) {
        return CompletableFuture.supplyAsync(() -> {
            HttpGet getRequest = new HttpGet(String.format(GROUP_DATA_URL, groupName));
            getRequest.setConfig(config);
            getRequest.addHeader("User-Agent", "Mozilla/5.0");
            try {
                CloseableHttpResponse httpResponse = client.execute(getRequest);
                JsonObject schedule = JsonParser.parseReader(new InputStreamReader(httpResponse.getEntity().getContent(), StandardCharsets.UTF_8))
                        .getAsJsonArray().get(0).getAsJsonObject();
                httpResponse.close();
                return schedule;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        });
    }

    public CompletableFuture<Integer> getCurrentWeek() {
        return CompletableFuture.supplyAsync(() -> {
            HttpGet getRequest = new HttpGet(CURRENT_WEEK_URL);
            getRequest.setConfig(config);
            getRequest.addHeader("User-Agent", "Mozilla/5.0");
            try {
                CloseableHttpResponse response = client.execute(getRequest);
                int currentWeek = Integer.parseInt(new BufferedReader(new InputStreamReader(response.getEntity().getContent())).readLine());
                response.close();
                return currentWeek;
            } catch (IOException e) {
                e.printStackTrace();
                return 0;
            }
        });
    }
}
