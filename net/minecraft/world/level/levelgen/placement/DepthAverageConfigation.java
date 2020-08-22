package net.minecraft.world.level.levelgen.placement;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.world.level.levelgen.feature.configurations.DecoratorConfiguration;

public class DepthAverageConfigation implements DecoratorConfiguration {
   public final int count;
   public final int baseline;
   public final int spread;

   public DepthAverageConfigation(int var1, int var2, int var3) {
      this.count = var1;
      this.baseline = var2;
      this.spread = var3;
   }

   public Dynamic serialize(DynamicOps var1) {
      return new Dynamic(var1, var1.createMap(ImmutableMap.of(var1.createString("count"), var1.createInt(this.count), var1.createString("baseline"), var1.createInt(this.baseline), var1.createString("spread"), var1.createInt(this.spread))));
   }

   public static DepthAverageConfigation deserialize(Dynamic var0) {
      int var1 = var0.get("count").asInt(0);
      int var2 = var0.get("baseline").asInt(0);
      int var3 = var0.get("spread").asInt(0);
      return new DepthAverageConfigation(var1, var2, var3);
   }
}
