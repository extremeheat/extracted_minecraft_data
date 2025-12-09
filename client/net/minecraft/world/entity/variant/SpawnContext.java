package net.minecraft.world.entity.variant;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.attribute.EnvironmentAttributeReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.biome.Biome;

public record SpawnContext(BlockPos pos, ServerLevelAccessor level, EnvironmentAttributeReader environmentAttributes, Holder<Biome> biome) {
   public SpawnContext(BlockPos var1, ServerLevelAccessor var2, EnvironmentAttributeReader var3, Holder<Biome> var4) {
      super();
      this.pos = var1;
      this.level = var2;
      this.environmentAttributes = var3;
      this.biome = var4;
   }

   public static SpawnContext create(ServerLevelAccessor var0, BlockPos var1) {
      Holder var2 = var0.getBiome(var1);
      return new SpawnContext(var1, var0, var0.environmentAttributes(), var2);
   }
}
