import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import discord4j.common.util.Snowflake;
import discord4j.core.GatewayDiscordClient;
import discord4j.discordjson.json.ChannelModifyRequest;
import discord4j.discordjson.json.ImmutableChannelModifyRequest;
import discord4j.rest.entity.RestChannel;
import io.github.cdimascio.dotenv.Dotenv;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;
import java.util.TimerTask;

public class UpdateViewersTask extends TimerTask {
  String BOT_CHANNEL_ID = "849718191827451964";
  String VIEWERS_CHANNEL_ID = "849703376937811968";
  
  Gson gson = new Gson();
  final RestChannel botChannel;
  final RestChannel viewersChannel;
  final GatewayDiscordClient gateway;
  final String TWITCH_ACCESS_TOKEN;
  final String TWITCH_CLIENT_ID;
  
  public UpdateViewersTask(GatewayDiscordClient gateway, Dotenv dotenv) {
    this.gateway = gateway;
    this.botChannel =
        Objects.requireNonNull(
            Objects.requireNonNull(gateway)
                .getChannelById(Snowflake.of(BOT_CHANNEL_ID))
                .block())
            .getRestChannel();
    this.viewersChannel =
        Objects.requireNonNull(
            Objects.requireNonNull(gateway)
                .getChannelById(Snowflake.of(VIEWERS_CHANNEL_ID))
                .block())
            .getRestChannel();
    TWITCH_ACCESS_TOKEN = dotenv.get("TWITCH_ACCESS_TOKEN");
    TWITCH_CLIENT_ID = dotenv.get("TWITCH_CLIENT_ID");
  }
  
  public void run() {
    try {
      HttpClient client = HttpClientBuilder.create().build();
      HttpGet request = new HttpGet("https://api.twitch.tv/helix/streams?user_login=jaredezz");
      request.setHeader("Authorization", String.format("Bearer %s", TWITCH_ACCESS_TOKEN));
      request.setHeader("Client-Id", TWITCH_CLIENT_ID);
      HttpResponse response = client.execute(request);
      
      // Get the response
      BufferedReader rd = new BufferedReader
          (new InputStreamReader(
              response.getEntity().getContent()));
      
      String line = "";
      StringBuilder responseBuilder = new StringBuilder();
      while ((line = rd.readLine()) != null) {
        responseBuilder.append(line);
      }
      System.out.println(responseBuilder.toString());

      Map<String, Object> responseMap = gson.fromJson(responseBuilder.toString(), Map.class);
      ArrayList data = (ArrayList) responseMap.get("data");
      LinkedTreeMap dataMap = (LinkedTreeMap) data.get(0);

      int viewerCount = ((Double) dataMap.get("viewer_count")).intValue();
      String viewerCountString = String.format("Viewer Count: %d", viewerCount);
      botChannel.createMessage(viewerCountString).block();
      ChannelModifyRequest modifyRequest =
          ImmutableChannelModifyRequest.builder().name(viewerCountString).build();
      viewersChannel.modify(modifyRequest, "Update Viewer Count").block();
    } catch (IOException e) {
      e.printStackTrace();
      botChannel.createMessage(e.getMessage()).block();
    }
  }
}
