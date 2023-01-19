package net.minecraft.sounds;

public enum SoundSource {
   MASTER("master"),
   MUSIC("music"),
   RECORDS("record"),
   WEATHER("weather"),
   BLOCKS("block"),
   HOSTILE("hostile"),
   NEUTRAL("neutral"),
   PLAYERS("player"),
   AMBIENT("ambient"),
   VOICE("voice");

   private final String name;

   private SoundSource(String var3) {
      this.name = var3;
   }

   public String getName() {
      return this.name;
   }
}
