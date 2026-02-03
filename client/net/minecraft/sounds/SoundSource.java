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
   VOICE("voice"),
   UI("ui");

   private final String name;

   private SoundSource(final String name) {
      this.name = name;
   }

   public String getName() {
      return this.name;
   }

   // $FF: synthetic method
   private static SoundSource[] $values() {
      return new SoundSource[]{MASTER, MUSIC, RECORDS, WEATHER, BLOCKS, HOSTILE, NEUTRAL, PLAYERS, AMBIENT, VOICE, UI};
   }
}
