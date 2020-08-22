package net.minecraft.world.level.levelgen.feature;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.chunk.ChunkGenerator;

public class WeightedConfiguredFeature {
   public final ConfiguredFeature feature;
   public final float chance;

   public WeightedConfiguredFeature(ConfiguredFeature var1, float var2) {
      this.feature = var1;
      this.chance = var2;
   }

   public Dynamic serialize(DynamicOps var1) {
      return new Dynamic(var1, var1.createMap(ImmutableMap.of(var1.createString("name"), var1.createString(Registry.FEATURE.getKey(this.feature.feature).toString()), var1.createString("config"), this.feature.config.serialize(var1).getValue(), var1.createString("chance"), var1.createFloat(this.chance))));
   }

   public boolean place(LevelAccessor var1, ChunkGenerator var2, Random var3, BlockPos var4) {
      return this.feature.place(var1, var2, var3, var4);
   }

   public static WeightedConfiguredFeature deserialize(Dynamic var0) {
      return ConfiguredFeature.deserialize(var0).weighted(var0.get("chance").asFloat(0.0F));
   }
}
