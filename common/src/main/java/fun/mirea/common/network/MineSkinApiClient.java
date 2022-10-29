package fun.mirea.common.network;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import fun.mirea.common.user.skin.SkinData;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.CompletableFuture;

public class MineSkinApiClient {

    private static final String GENERATE_ENDPOINT = "https://api.mineskin.org/generate/";

    private final String apiToken;
    private final CloseableHttpClient client;
    private final RequestConfig config;

    public MineSkinApiClient(String apiToken) {
        this.apiToken = apiToken;
        this.client = HttpClients.createDefault();
        this.config = RequestConfig.custom()
                .setSocketTimeout(3000)
                .setConnectTimeout(3000)
                .setConnectionRequestTimeout(3000)
                .build();
    }

    public CompletableFuture<SkinData> uploadSkin(File file) {
        return CompletableFuture.supplyAsync(() -> {
            HttpPost uploadRequest = new HttpPost(GENERATE_ENDPOINT + "upload");
            uploadRequest.setConfig(config);
            uploadRequest.setHeader("User-Agent", "Mozilla/5.0");
            uploadRequest.setHeader("Authorization", "Bearer " + apiToken);
            String value = "";
            String signature = "";
            try {
                HttpEntity entity = MultipartEntityBuilder.create()
                        .addTextBody("name", "skin")
                        .addTextBody("visibility", "1")
                        .addPart("file", new FileBody(file)).build();
                uploadRequest.setEntity(entity);
                CloseableHttpResponse response = client.execute(uploadRequest);
                BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                String line = reader.readLine();
                JsonObject object = JsonParser.parseString(line).getAsJsonObject();
                if (response.getStatusLine().getStatusCode() == 200) {
                    JsonObject data = object.getAsJsonObject("data");
                    JsonObject texture = data.getAsJsonObject("texture");
                    value = texture.get("value").getAsString();
                    signature = texture.get("signature").getAsString();
                }
                response.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return new SkinData(value, signature);
        });
    }
}
