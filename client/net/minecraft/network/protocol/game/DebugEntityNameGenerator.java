package net.minecraft.network.protocol.game;

import java.util.UUID;
import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

public class DebugEntityNameGenerator {
   private static final String[] NAMES_FIRST_PART = new String[]{"Slim", "Far", "River", "Silly", "Fat", "Thin", "Fish", "Bat", "Dark", "Oak", "Sly", "Bush", "Zen", "Bark", "Cry", "Slack", "Soup", "Grim", "Hook", "Dirt", "Mud", "Sad", "Hard", "Crook", "Sneak", "Stink", "Weird", "Fire", "Soot", "Soft", "Rough", "Cling", "Scar"};
   private static final String[] NAMES_SECOND_PART = new String[]{"Fox", "Tail", "Jaw", "Whisper", "Twig", "Root", "Finder", "Nose", "Brow", "Blade", "Fry", "Seek", "Wart", "Tooth", "Foot", "Leaf", "Stone", "Fall", "Face", "Tongue", "Voice", "Lip", "Mouth", "Snail", "Toe", "Ear", "Hair", "Beard", "Shirt", "Fist"};

   public DebugEntityNameGenerator() {
      super();
   }

   public static String getEntityName(Entity var0) {
      if (var0 instanceof Player) {
         return var0.getName().getString();
      } else {
         Component var1 = var0.getCustomName();
         return var1 != null ? var1.getString() : getEntityName(var0.getUUID());
      }
   }

   public static String getEntityName(UUID var0) {
      RandomSource var1 = getRandom(var0);
      String var10000 = getRandomString(var1, NAMES_FIRST_PART);
      return var10000 + getRandomString(var1, NAMES_SECOND_PART);
   }

   private static String getRandomString(RandomSource var0, String[] var1) {
      return (String)Util.getRandom((Object[])var1, var0);
   }

   private static RandomSource getRandom(UUID var0) {
      return RandomSource.create((long)(var0.hashCode() >> 2));
   }
}
