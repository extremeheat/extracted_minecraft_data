package net.minecraft.client;

import java.util.Arrays;
import java.util.Comparator;
import net.minecraft.util.Mth;

public enum CloudStatus {
   OFF(0, "options.off"),
   FAST(1, "options.clouds.fast"),
   FANCY(2, "options.clouds.fancy");

   private static final CloudStatus[] BY_ID = (CloudStatus[])Arrays.stream(values()).sorted(Comparator.comparingInt(CloudStatus::getId)).toArray((var0) -> {
      return new CloudStatus[var0];
   });
   private final int id;
   private final String key;

   private CloudStatus(int var3, String var4) {
      this.id = var3;
      this.key = var4;
   }

   public int getId() {
      return this.id;
   }

   public String getKey() {
      return this.key;
   }

   public static CloudStatus byId(int var0) {
      return BY_ID[Mth.positiveModulo(var0, BY_ID.length)];
   }
}
