package fun.mirea.common.utility;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class WebUtils {

    public static void main(String[] args) throws IOException {
        HttpClient client = HttpClientBuilder.create().build();
        HttpResponse response = client.execute(new HttpGet("https://mirea.xyz/api/v1.3/groups/all"));
//        JsonArray array = JsonParser.parseReader(new JsonReader(new InputStreamReader(response.getEntity().getContent()))).getAsJsonArray();
//        for (JsonElement group : array) {
//            System.out.println(group.toString());
//        }
    }

}
