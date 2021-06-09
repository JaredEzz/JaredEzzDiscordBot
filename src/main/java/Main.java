import io.github.cdimascio.dotenv.Dotenv;

public class Main {
  public static void main(String[] args) {
    Dotenv dotenv = Dotenv.load();
    String discordToken = dotenv.get("DISCORD_TOKEN");
    System.out.println(discordToken);
  }
}