package net.minecraft.world.level.levelgen.carver;

import com.mojang.serialization.Codec;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.CarvingMask;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.Aquifer;

public class ConfiguredWorldCarver<WC extends CarverConfiguration> {
   public static final Codec<ConfiguredWorldCarver<?>> DIRECT_CODEC;
   public static final Codec<Supplier<ConfiguredWorldCarver<?>>> CODEC;
   public static final Codec<List<Supplier<ConfiguredWorldCarver<?>>>> LIST_CODEC;
   private final WorldCarver<WC> worldCarver;
   private final WC config;

   public ConfiguredWorldCarver(WorldCarver<WC> var1, WC var2) {
      super();
      this.worldCarver = var1;
      this.config = var2;
   }

   public WC config() {
      return this.config;
   }

   public boolean isStartChunk(Random var1) {
      return this.worldCarver.isStartChunk(this.config, var1);
   }

   public boolean carve(CarvingContext var1, ChunkAccess var2, Function<BlockPos, Biome> var3, Random var4, Aquifer var5, ChunkPos var6, CarvingMask var7) {
      return SharedConstants.debugVoidTerrain(var2.getPos()) ? false : this.worldCarver.carve(var1, this.config, var2, var3, var4, var5, var6, var7);
   }

   static {
      DIRECT_CODEC = Registry.CARVER.byNameCodec().dispatch((var0) -> {
         return var0.worldCarver;
      }, WorldCarver::configuredCodec);
      CODEC = RegistryFileCodec.create(Registry.CONFIGURED_CARVER_REGISTRY, DIRECT_CODEC);
      LIST_CODEC = RegistryFileCodec.homogeneousList(Registry.CONFIGURED_CARVER_REGISTRY, DIRECT_CODEC);
   }
}
