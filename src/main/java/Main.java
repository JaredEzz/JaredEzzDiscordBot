import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import io.github.cdimascio.dotenv.Dotenv;

import java.util.Timer;

public class Main {
  public static void main(String[] args) {
    Dotenv dotenv = Dotenv.load();
    final String DISCORD_TOKEN = dotenv.get("DISCORD_TOKEN");
  
    final DiscordClient client = DiscordClient.create(DISCORD_TOKEN);
    GatewayDiscordClient gateway = client.login().block();
  
    //delete error messages
    assert gateway != null;
    gateway.on(MessageCreateEvent.class).subscribe(event -> {
      final Message message = event.getMessage();
      if (message.getContent().contains("The server responded with error")) {
        message.delete().block();
      }
    });
  
    boolean retry = false;
    do {
      try {
        // Update Channel Viewers
        int minutesBetweenUpdate = 10;
        Timer timer = new Timer("UpdateViewersTimer");
        UpdateViewersTask updateViewers = new UpdateViewersTask(gateway, dotenv);
        timer.schedule(updateViewers, 0, minutesBetweenUpdate * 1 * 1000);
      
        StartCommercialTask commercialTask = new StartCommercialTask(gateway, dotenv);
        timer.schedule(commercialTask, 0, minutesBetweenUpdate * 1 * 1000);
      } catch (Exception e) {
        retry = true;
        System.out.println(e.getMessage());
      }
    
    } while (retry);
  
    gateway.onDisconnect().block();
  }
}