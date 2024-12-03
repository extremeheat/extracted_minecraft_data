package net.minecraft.world.level.levelgen.carver;

import com.mojang.serialization.Codec;
import java.util.function.Function;
import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.CarvingMask;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.Aquifer;

public record ConfiguredWorldCarver<WC extends CarverConfiguration>(WorldCarver<WC> worldCarver, WC config) {
   public static final Codec<ConfiguredWorldCarver<?>> DIRECT_CODEC;
   public static final Codec<Holder<ConfiguredWorldCarver<?>>> CODEC;
   public static final Codec<HolderSet<ConfiguredWorldCarver<?>>> LIST_CODEC;

   public ConfiguredWorldCarver(WorldCarver<WC> var1, WC var2) {
      super();
      this.worldCarver = var1;
      this.config = var2;
   }

   public boolean isStartChunk(RandomSource var1) {
      return this.worldCarver.isStartChunk(this.config, var1);
   }

   public boolean carve(CarvingContext var1, ChunkAccess var2, Function<BlockPos, Holder<Biome>> var3, RandomSource var4, Aquifer var5, ChunkPos var6, CarvingMask var7) {
      return SharedConstants.debugVoidTerrain(var2.getPos()) ? false : this.worldCarver.carve(var1, this.config, var2, var3, var4, var5, var6, var7);
   }

   static {
      DIRECT_CODEC = BuiltInRegistries.CARVER.byNameCodec().dispatch((var0) -> var0.worldCarver, WorldCarver::configuredCodec);
      CODEC = RegistryFileCodec.<Holder<ConfiguredWorldCarver<?>>>create(Registries.CONFIGURED_CARVER, DIRECT_CODEC);
      LIST_CODEC = RegistryCodecs.homogeneousList(Registries.CONFIGURED_CARVER, DIRECT_CODEC);
   }
}
