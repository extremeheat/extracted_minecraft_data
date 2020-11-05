package net.minecraft.realms;

import java.time.Duration;
import java.util.Arrays;
import net.minecraft.Util;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.TextComponent;

public class NarrationHelper {
   private static final RepeatedNarrator REPEATED_NARRATOR = new RepeatedNarrator(Duration.ofSeconds(5L));

   public static void now(String var0) {
      NarratorChatListener var1 = NarratorChatListener.INSTANCE;
      var1.clear();
      var1.handle(ChatType.SYSTEM, new TextComponent(fixNarrationNewlines(var0)), Util.NIL_UUID);
   }

   private static String fixNarrationNewlines(String var0) {
      return var0.replace("\\n", System.lineSeparator());
   }

   public static void now(String... var0) {
      now((Iterable)Arrays.asList(var0));
   }

   public static void now(Iterable<String> var0) {
      now(join(var0));
   }

   public static String join(Iterable<String> var0) {
      return String.join(System.lineSeparator(), var0);
   }

   public static void repeatedly(String var0) {
      REPEATED_NARRATOR.narrate(fixNarrationNewlines(var0));
   }
}
