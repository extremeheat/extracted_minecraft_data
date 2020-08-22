package net.minecraft.world.level.levelgen.feature.configurations;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

public class ChanceRangeDecoratorConfiguration implements DecoratorConfiguration {
   public final float chance;
   public final int bottomOffset;
   public final int topOffset;
   public final int top;

   public ChanceRangeDecoratorConfiguration(float var1, int var2, int var3, int var4) {
      this.chance = var1;
      this.bottomOffset = var2;
      this.topOffset = var3;
      this.top = var4;
   }

   public Dynamic serialize(DynamicOps var1) {
      return new Dynamic(var1, var1.createMap(ImmutableMap.of(var1.createString("chance"), var1.createFloat(this.chance), var1.createString("bottom_offset"), var1.createInt(this.bottomOffset), var1.createString("top_offset"), var1.createInt(this.topOffset), var1.createString("top"), var1.createInt(this.top))));
   }

   public static ChanceRangeDecoratorConfiguration deserialize(Dynamic var0) {
      float var1 = var0.get("chance").asFloat(0.0F);
      int var2 = var0.get("bottom_offset").asInt(0);
      int var3 = var0.get("top_offset").asInt(0);
      int var4 = var0.get("top").asInt(0);
      return new ChanceRangeDecoratorConfiguration(var1, var2, var3, var4);
   }
}
