package net.minecraft.client;

import java.util.Arrays;
import java.util.Comparator;
import net.minecraft.util.Mth;
import net.minecraft.util.OptionEnum;

public enum ParticleStatus implements OptionEnum {
   ALL(0, "options.particles.all"),
   DECREASED(1, "options.particles.decreased"),
   MINIMAL(2, "options.particles.minimal");

   private static final ParticleStatus[] BY_ID = (ParticleStatus[])Arrays.stream(values()).sorted(Comparator.comparingInt(ParticleStatus::getId)).toArray((var0) -> {
      return new ParticleStatus[var0];
   });
   private final int id;
   private final String key;

   private ParticleStatus(int var3, String var4) {
      this.id = var3;
      this.key = var4;
   }

   public String getKey() {
      return this.key;
   }

   public int getId() {
      return this.id;
   }

   public static ParticleStatus byId(int var0) {
      return BY_ID[Mth.positiveModulo(var0, BY_ID.length)];
   }

   // $FF: synthetic method
   private static ParticleStatus[] $values() {
      return new ParticleStatus[]{ALL, DECREASED, MINIMAL};
   }
}
