package fun.mirea.common.network;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import fun.mirea.common.user.skin.SkinData;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class MojangClient {

    private static final String USERS_SERVICE_URL = "https://api.mojang.com/users/profiles/minecraft/{name}";
    private static final String SESSION_SERVICE_URL = "https://sessionserver.mojang.com/session/minecraft/profile/{uuid}?unsigned=false";

    private final CloseableHttpClient client;
    private final RequestConfig config;

    public MojangClient() {
        this.client = HttpClients.createDefault();
        this.config = RequestConfig.custom()
                .setSocketTimeout(3000)
                .setConnectTimeout(3000)
                .setConnectionRequestTimeout(3000)
                .build();
    }

    public CompletableFuture<Optional<String>> getLicenseId(String name) {
        return CompletableFuture.supplyAsync(() -> {
            HttpGet uuidRequest = new HttpGet(USERS_SERVICE_URL.replace("{name}", name));
            uuidRequest.setConfig(config);
            uuidRequest.addHeader("User-Agent", "Mozilla/5.0");
            try {
                CloseableHttpResponse response = client.execute(uuidRequest);
                if (response.getStatusLine().getStatusCode() == 200) {
                    JsonObject object = JsonParser.parseReader(new InputStreamReader(response.getEntity().getContent())).getAsJsonObject();
                    response.close();
                    return Optional.of(object.get("id").getAsString());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return Optional.empty();
        });
    }

    public CompletableFuture<Optional<SkinData>> getLicenseSkin(String uuid) {
        return CompletableFuture.supplyAsync(() -> {
            HttpGet userRequest = new HttpGet(SESSION_SERVICE_URL.replace("{uuid}", uuid));
            userRequest.setConfig(config);
            userRequest.addHeader("User-Agent", "Mozilla/5.0");
            try {
                CloseableHttpResponse response = client.execute(userRequest);
                if (response.getStatusLine().getStatusCode() == 200) {
                    JsonObject object = JsonParser.parseReader(new InputStreamReader(response.getEntity().getContent())).getAsJsonObject();
                    response.close();
                    JsonArray properties = object.get("properties").getAsJsonArray();
                    JsonObject property = properties.get(0).getAsJsonObject();
                    String value = property.has("value") ? property.get("value").getAsString() : "";
                    String signature = property.has("signature") ? property.get("signature").getAsString() : "";
                    return Optional.of(new SkinData(value, signature));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return Optional.empty();
        });
    }
}
