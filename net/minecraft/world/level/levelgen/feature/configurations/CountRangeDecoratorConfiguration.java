package net.minecraft.world.level.levelgen.feature.configurations;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

public class CountRangeDecoratorConfiguration implements DecoratorConfiguration {
   public final int count;
   public final int bottomOffset;
   public final int topOffset;
   public final int maximum;

   public CountRangeDecoratorConfiguration(int var1, int var2, int var3, int var4) {
      this.count = var1;
      this.bottomOffset = var2;
      this.topOffset = var3;
      this.maximum = var4;
   }

   public Dynamic serialize(DynamicOps var1) {
      return new Dynamic(var1, var1.createMap(ImmutableMap.of(var1.createString("count"), var1.createInt(this.count), var1.createString("bottom_offset"), var1.createInt(this.bottomOffset), var1.createString("top_offset"), var1.createInt(this.topOffset), var1.createString("maximum"), var1.createInt(this.maximum))));
   }

   public static CountRangeDecoratorConfiguration deserialize(Dynamic var0) {
      int var1 = var0.get("count").asInt(0);
      int var2 = var0.get("bottom_offset").asInt(0);
      int var3 = var0.get("top_offset").asInt(0);
      int var4 = var0.get("maximum").asInt(0);
      return new CountRangeDecoratorConfiguration(var1, var2, var3, var4);
   }
}
