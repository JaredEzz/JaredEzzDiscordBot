import discord4j.common.util.Snowflake;
import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Attachment;
import discord4j.core.object.entity.GuildEmoji;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import discord4j.core.object.reaction.ReactionEmoji;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.core.spec.MessageCreateSpec;
import discord4j.discordjson.json.EmbedData;
import discord4j.discordjson.json.MessageCreateRequest;
import discord4j.discordjson.json.MessageData;
import discord4j.rest.entity.RestChannel;
import io.github.cdimascio.dotenv.Dotenv;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Set;
import java.util.Timer;

public class Main {
  private static final String BOT_CHANNEL_ID = "849718191827451964";
  
  public static void main(String[] args) {
    Dotenv dotenv = Dotenv.load();
    final String DISCORD_TOKEN = dotenv.get("DISCORD_TOKEN");
  
    final DiscordClient client = DiscordClient.create(DISCORD_TOKEN);
    GatewayDiscordClient gateway = client.login().block();
  
    //delete error messages
    assert gateway != null;
    gateway.on(MessageCreateEvent.class).subscribe(event -> {
      final Message message = event.getMessage();
      final String author = message.getAuthor().map(User::getUsername).orElse(null);
      if (
          message.getContent().contains("The server responded with error") ||
          message.getContent().contains("You can't use that command here.")
      ) {
        message.delete().block();
      } else if (message.getChannelId().equals(Snowflake.of(BOT_CHANNEL_ID)) && "jaredezz".equals(author)){
        RestChannel restChannel = gateway.getChannelById(Snowflake.of(BOT_CHANNEL_ID)).block().getRestChannel();
        StringBuilder messageContentBuilder = new StringBuilder(message.getContent());
        Set<Attachment> attachments = message.getAttachments();
        if (attachments.size() == 1) {
          Attachment attachment = attachments.stream().findFirst().get();
          messageContentBuilder.append(String.format("%n%n%s", attachment.getUrl()));
        }
        MessageData block = restChannel.createMessage(messageContentBuilder.toString()).block();
      }
    });
  
    boolean retry = false;
    do {
      try {
        // Update Channel Viewers
        int minutesBetweenUpdate = 10;
        Timer timer = new Timer("UpdateViewersTimer");
        UpdateViewersTask updateViewers = new UpdateViewersTask(gateway, dotenv);
        timer.schedule(updateViewers, 0, minutesBetweenUpdate * 60 * 1000);

        StartCommercialTask commercialTask = new StartCommercialTask(gateway, dotenv);
        timer.schedule(commercialTask, 0, minutesBetweenUpdate * 60 * 1000);
      } catch (Exception e) {
        retry = true;
        System.out.println(e.getMessage());
      }

    } while (retry);
  
    gateway.onDisconnect().block();
  }
}