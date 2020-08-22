package net.minecraft.client;

import java.util.Arrays;
import java.util.Comparator;
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
   private final String key;

   private NarratorStatus(int var3, String var4) {
      this.id = var3;
      this.key = var4;
   }

   public int getId() {
      return this.id;
   }

   public String getKey() {
      return this.key;
   }

   public static NarratorStatus byId(int var0) {
      return BY_ID[Mth.positiveModulo(var0, BY_ID.length)];
   }
}
