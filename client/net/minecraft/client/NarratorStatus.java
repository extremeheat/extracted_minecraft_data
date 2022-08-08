package net.minecraft.client;

import java.util.Arrays;
import java.util.Comparator;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

public enum NarratorStatus {
   OFF(0, "options.narrator.off"),
   ALL(1, "options.narrator.all"),
   CHAT(2, "options.narrator.chat"),
   SYSTEM(3, "options.narrator.system");

   private static final NarratorStatus[] BY_ID = (NarratorStatus[])Arrays.stream(values()).sorted(Comparator.comparingInt(NarratorStatus::getId)).toArray((var0) -> {
      return new NarratorStatus[var0];
   });
   private final int id;
   private final Component name;

   private NarratorStatus(int var3, String var4) {
      this.id = var3;
      this.name = Component.translatable(var4);
   }

   public int getId() {
      return this.id;
   }

   public Component getName() {
      return this.name;
   }

   public static NarratorStatus byId(int var0) {
      return BY_ID[Mth.positiveModulo(var0, BY_ID.length)];
   }

   public boolean shouldNarrate(ChatType.Narration.Priority var1) {
      boolean var10000;
      switch (this) {
         case OFF:
            var10000 = false;
            break;
         case ALL:
            var10000 = true;
            break;
         case CHAT:
            var10000 = var1 == ChatType.Narration.Priority.CHAT;
            break;
         case SYSTEM:
            var10000 = var1 == ChatType.Narration.Priority.SYSTEM;
            break;
         default:
            throw new IncompatibleClassChangeError();
      }

      return var10000;
   }

   // $FF: synthetic method
   private static NarratorStatus[] $values() {
      return new NarratorStatus[]{OFF, ALL, CHAT, SYSTEM};
   }
}
