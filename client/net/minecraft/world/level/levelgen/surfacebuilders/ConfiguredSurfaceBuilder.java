package net.minecraft.world.level.levelgen.surfacebuilders;

import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.function.Supplier;
import net.minecraft.core.Registry;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;

public class ConfiguredSurfaceBuilder<SC extends SurfaceBuilderConfiguration> {
   public static final Codec<ConfiguredSurfaceBuilder<?>> DIRECT_CODEC;
   public static final Codec<Supplier<ConfiguredSurfaceBuilder<?>>> CODEC;
   public final SurfaceBuilder<SC> surfaceBuilder;
   public final SC config;

   public ConfiguredSurfaceBuilder(SurfaceBuilder<SC> var1, SC var2) {
      super();
      this.surfaceBuilder = var1;
      this.config = var2;
   }

   public void apply(Random var1, ChunkAccess var2, Biome var3, int var4, int var5, int var6, double var7, BlockState var9, BlockState var10, int var11, long var12) {
      this.surfaceBuilder.apply(var1, var2, var3, var4, var5, var6, var7, var9, var10, var11, var12, this.config);
   }

   public void initNoise(long var1) {
      this.surfaceBuilder.initNoise(var1);
   }

   public SC config() {
      return this.config;
   }

   static {
      DIRECT_CODEC = Registry.SURFACE_BUILDER.dispatch((var0) -> {
         return var0.surfaceBuilder;
      }, SurfaceBuilder::configuredCodec);
      CODEC = RegistryFileCodec.create(Registry.CONFIGURED_SURFACE_BUILDER_REGISTRY, DIRECT_CODEC);
   }
}
