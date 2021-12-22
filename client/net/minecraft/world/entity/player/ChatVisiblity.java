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
   // $FF: renamed from: id int
   private final int field_365;
   private final String key;

   private ChatVisiblity(int var3, String var4) {
      this.field_365 = var3;
      this.key = var4;
   }

   public int getId() {
      return this.field_365;
   }

   public String getKey() {
      return this.key;
   }

   public static ChatVisiblity byId(int var0) {
      return BY_ID[Mth.positiveModulo(var0, BY_ID.length)];
   }

   // $FF: synthetic method
   private static ChatVisiblity[] $values() {
      return new ChatVisiblity[]{FULL, SYSTEM, HIDDEN};
   }
}
