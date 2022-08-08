package net.minecraft.client;

import java.util.Arrays;
import java.util.Comparator;
import net.minecraft.util.Mth;
import net.minecraft.util.OptionEnum;

public enum AmbientOcclusionStatus implements OptionEnum {
   OFF(0, "options.ao.off"),
   MIN(1, "options.ao.min"),
   MAX(2, "options.ao.max");

   private static final AmbientOcclusionStatus[] BY_ID = (AmbientOcclusionStatus[])Arrays.stream(values()).sorted(Comparator.comparingInt(AmbientOcclusionStatus::getId)).toArray((var0) -> {
      return new AmbientOcclusionStatus[var0];
   });
   private final int id;
   private final String key;

   private AmbientOcclusionStatus(int var3, String var4) {
      this.id = var3;
      this.key = var4;
   }

   public int getId() {
      return this.id;
   }

   public String getKey() {
      return this.key;
   }

   public static AmbientOcclusionStatus byId(int var0) {
      return BY_ID[Mth.positiveModulo(var0, BY_ID.length)];
   }

   // $FF: synthetic method
   private static AmbientOcclusionStatus[] $values() {
      return new AmbientOcclusionStatus[]{OFF, MIN, MAX};
   }
}
