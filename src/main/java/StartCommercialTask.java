import com.google.gson.Gson;
import discord4j.common.util.Snowflake;
import discord4j.core.GatewayDiscordClient;
import discord4j.rest.entity.RestChannel;
import io.github.cdimascio.dotenv.Dotenv;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.*;

public class StartCommercialTask extends TimerTask {
  String BOT_CHANNEL_ID = "849718191827451964";
  String JAREDEZZ_USER_ID = "58566197";
  final String TWITCH_ACCESS_TOKEN;
  final String TWITCH_CLIENT_ID;
  
  Gson gson = new Gson();
  final RestChannel botChannel;
  final GatewayDiscordClient gateway;
  
  public StartCommercialTask(GatewayDiscordClient gateway, Dotenv dotenv) {
    this.gateway = gateway;
    this.botChannel =
        Objects.requireNonNull(
            Objects.requireNonNull(gateway)
                .getChannelById(Snowflake.of(BOT_CHANNEL_ID))
                .block())
            .getRestChannel();
    TWITCH_ACCESS_TOKEN = dotenv.get("TWITCH_ACCESS_TOKEN");
    TWITCH_CLIENT_ID = dotenv.get("TWITCH_CLIENT_ID");
  }
  
  public void run() {
    try {
      HttpClient httpClient = HttpClientBuilder.create().build();
      HttpPost httpPost = new HttpPost("https://api.twitch.tv/helix/channels/commercial");
      httpPost.setHeader("Authorization", String.format("Bearer %s", TWITCH_ACCESS_TOKEN));
      httpPost.setHeader("Client-Id", TWITCH_CLIENT_ID);
      httpPost.setHeader("Content-Type", "application/json");
      Map<String, String> body = new HashMap<>();
      body.put("broadcaster_id", JAREDEZZ_USER_ID);
      body.put("length", "180");
      httpPost.setEntity(new StringEntity(gson.toJson(body)));
      HttpResponse response = httpClient.execute(httpPost);
      BufferedReader rd = new BufferedReader(new InputStreamReader(
          response.getEntity().getContent()));
      
      StringBuilder responseBuilder = new StringBuilder();
      String line;
      while ((line = rd.readLine()) != null) {
        responseBuilder.append(line);
      }
      System.out.println(responseBuilder.toString());
      Map<String, Object> responseMap = gson.fromJson(responseBuilder.toString(), Map.class);
      ArrayList data = (ArrayList) responseMap.get("data");
      if (data != null) {
        botChannel.createMessage("Commercial Started").block();
      } else {
        botChannel.createMessage(responseBuilder.toString()).block();
      }
    } catch (IOException e) {
      e.printStackTrace();
      botChannel.createMessage(e.getMessage()).block();
    }
  }
}
