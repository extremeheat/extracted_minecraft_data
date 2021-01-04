package net.minecraft.world.level.levelgen.placement;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.world.level.levelgen.feature.DecoratorConfiguration;

public class DecoratorFrequencyChance implements DecoratorConfiguration {
   public final int count;
   public final float chance;

   public DecoratorFrequencyChance(int var1, float var2) {
      super();
      this.count = var1;
      this.chance = var2;
   }

   public <T> Dynamic<T> serialize(DynamicOps<T> var1) {
      return new Dynamic(var1, var1.createMap(ImmutableMap.of(var1.createString("count"), var1.createInt(this.count), var1.createString("chance"), var1.createFloat(this.chance))));
   }

   public static DecoratorFrequencyChance deserialize(Dynamic<?> var0) {
      int var1 = var0.get("count").asInt(0);
      float var2 = var0.get("chance").asFloat(0.0F);
      return new DecoratorFrequencyChance(var1, var2);
   }
}
