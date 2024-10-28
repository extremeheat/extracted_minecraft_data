package net.minecraft.server.level;

import java.util.function.IntFunction;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.OptionEnum;

public enum ParticleStatus implements OptionEnum {
   ALL(0, "options.particles.all"),
   DECREASED(1, "options.particles.decreased"),
   MINIMAL(2, "options.particles.minimal");

   private static final IntFunction<ParticleStatus> BY_ID = ByIdMap.continuous(ParticleStatus::getId, values(), ByIdMap.OutOfBoundsStrategy.WRAP);
   private final int id;
   private final String key;

   private ParticleStatus(final int var3, final String var4) {
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
      return (ParticleStatus)BY_ID.apply(var0);
   }

   // $FF: synthetic method
   private static ParticleStatus[] $values() {
      return new ParticleStatus[]{ALL, DECREASED, MINIMAL};
   }
}
