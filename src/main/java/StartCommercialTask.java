import com.google.gson.Gson;
import discord4j.common.util.Snowflake;
import discord4j.core.GatewayDiscordClient;
import discord4j.rest.entity.RestChannel;
import io.github.cdimascio.dotenv.Dotenv;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;
import java.util.TimerTask;

public class StartCommercialTask extends TimerTask {
  String BOT_CHANNEL_ID = "849718191827451964";
  String JAREDEZZ_USER_ID = "58566197";
  
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
  }
  
  public void run() {
/*
    try {
      HttpClient httpClient = HttpClient.newHttpClient();

      HttpRequest request =
          HttpRequest.newBuilder(URI.create("https://api.twitch.tv/helix/channels/commercial"))
              .header("Authorization", String.format("Bearer %s", accessToken))
              .header("Client-Id", clientID)
              .header("Content-Type", "application/json")
              .POST(
                  HttpRequest.BodyPublishers.ofString(
                      gson.toJson(Map.of("broadcaster_id", JAREDEZZ_USER_ID, "length", "180"))))
              .build();

      HttpResponse<String> response =
          httpClient.send(request, HttpResponse.BodyHandlers.ofString());

      Map<String, Object> responseMap = gson.fromJson(response.body(), Map.class);
      ArrayList data = (ArrayList) responseMap.get("data");
      if (data != null) {
        botChannel.createMessage("Commercial Started").block();
      } else {
        botChannel.createMessage(response.body()).block();
      }
    } catch (InterruptedException | IOException e) {
      e.printStackTrace();
      botChannel.createMessage(e.getMessage()).block();
    }
*/
  }
}
