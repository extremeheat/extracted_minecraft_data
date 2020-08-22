package net.minecraft.network.protocol.game;

import java.util.Random;
import java.util.UUID;

public class DebugMobNameGenerator {
   private static final String[] NAMES_FIRST_PART = new String[]{"Slim", "Far", "River", "Silly", "Fat", "Thin", "Fish", "Bat", "Dark", "Oak", "Sly", "Bush", "Zen", "Bark", "Cry", "Slack", "Soup", "Grim", "Hook"};
   private static final String[] NAMES_SECOND_PART = new String[]{"Fox", "Tail", "Jaw", "Whisper", "Twig", "Root", "Finder", "Nose", "Brow", "Blade", "Fry", "Seek", "Tooth", "Foot", "Leaf", "Stone", "Fall", "Face", "Tongue"};

   public static String getMobName(UUID var0) {
      Random var1 = getRandom(var0);
      return getRandomString(var1, NAMES_FIRST_PART) + getRandomString(var1, NAMES_SECOND_PART);
   }

   private static String getRandomString(Random var0, String[] var1) {
      return var1[var0.nextInt(var1.length)];
   }

   private static Random getRandom(UUID var0) {
      return new Random((long)(var0.hashCode() >> 2));
   }
}
