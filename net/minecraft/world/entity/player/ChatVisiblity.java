package net.minecraft.world.entity.player;

import java.util.Arrays;
import java.util.Comparator;
import net.minecraft.util.Mth;

public enum ChatVisiblity {
   FULL(0, "options.chat.visibility.full"),
   SYSTEM(1, "options.chat.visibility.system"),
   HIDDEN(2, "options.chat.visibility.hidden");

   private static final ChatVisiblity[] BY_ID = (ChatVisiblity[])Arrays.stream(values()).sorted(Comparator.comparingInt(ChatVisiblity::getId)).toArray((var0) -> {
      return new ChatVisiblity[var0];
   });
   private final int id;
   private final String key;

   private ChatVisiblity(int var3, String var4) {
      this.id = var3;
      this.key = var4;
   }

   public int getId() {
      return this.id;
   }

   public String getKey() {
      return this.key;
   }

   public static ChatVisiblity byId(int var0) {
      return BY_ID[Mth.positiveModulo(var0, BY_ID.length)];
   }
}
