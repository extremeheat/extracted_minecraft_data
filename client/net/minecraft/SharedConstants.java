package net.minecraft;

import com.mojang.bridge.game.GameVersion;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.datafixers.types.constant.NamespacedStringType;
import io.netty.util.ResourceLeakDetector;
import io.netty.util.ResourceLeakDetector.Level;
import net.minecraft.commands.BrigadierExceptions;
import net.minecraft.util.datafix.schemas.NamespacedSchema;

public class SharedConstants {
   public static final Level NETTY_LEAK_DETECTION;
   public static boolean IS_RUNNING_IN_IDE;
   public static final char[] ILLEGAL_FILE_CHARACTERS;
   private static GameVersion CURRENT_VERSION;

   public static boolean isAllowedChatCharacter(char var0) {
      return var0 != 167 && var0 >= ' ' && var0 != 127;
   }

   public static String filterText(String var0) {
      StringBuilder var1 = new StringBuilder();
      char[] var2 = var0.toCharArray();
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         char var5 = var2[var4];
         if (isAllowedChatCharacter(var5)) {
            var1.append(var5);
         }
      }

      return var1.toString();
   }

   public static String filterUnicodeSupplementary(String var0) {
      StringBuilder var1 = new StringBuilder();

      for(int var2 = 0; var2 < var0.length(); var2 = var0.offsetByCodePoints(var2, 1)) {
         int var3 = var0.codePointAt(var2);
         if (!Character.isSupplementaryCodePoint(var3)) {
            var1.appendCodePoint(var3);
         } else {
            var1.append('\ufffd');
         }
      }

      return var1.toString();
   }

   public static GameVersion getCurrentVersion() {
      if (CURRENT_VERSION == null) {
         CURRENT_VERSION = DetectedVersion.tryDetectVersion();
      }

      return CURRENT_VERSION;
   }

   static {
      NETTY_LEAK_DETECTION = Level.DISABLED;
      ILLEGAL_FILE_CHARACTERS = new char[]{'/', '\n', '\r', '\t', '\u0000', '\f', '`', '?', '*', '\\', '<', '>', '|', '"', ':'};
      ResourceLeakDetector.setLevel(NETTY_LEAK_DETECTION);
      CommandSyntaxException.ENABLE_COMMAND_STACK_TRACES = false;
      CommandSyntaxException.BUILT_IN_EXCEPTIONS = new BrigadierExceptions();
      NamespacedStringType.ENSURE_NAMESPACE = NamespacedSchema::ensureNamespaced;
   }
}
