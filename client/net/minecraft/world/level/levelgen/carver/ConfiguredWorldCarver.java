package net.minecraft.world.level.levelgen.carver;

import com.mojang.serialization.Codec;
import java.util.BitSet;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.ChunkAccess;

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

   public boolean isStartChunk(Random var1, int var2, int var3) {
      return this.worldCarver.isStartChunk(var1, var2, var3, this.config);
   }

   public boolean carve(ChunkAccess var1, Function<BlockPos, Biome> var2, Random var3, int var4, int var5, int var6, int var7, int var8, BitSet var9) {
      return this.worldCarver.carve(var1, var2, var3, var4, var5, var6, var7, var8, var9, this.config);
   }

   static {
      DIRECT_CODEC = Registry.CARVER.dispatch((var0) -> {
         return var0.worldCarver;
      }, WorldCarver::configuredCodec);
      CODEC = RegistryFileCodec.create(Registry.CONFIGURED_CARVER_REGISTRY, DIRECT_CODEC);
      LIST_CODEC = RegistryFileCodec.homogeneousList(Registry.CONFIGURED_CARVER_REGISTRY, DIRECT_CODEC);
   }
}
