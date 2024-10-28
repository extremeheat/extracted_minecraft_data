package net.minecraft.world.level;

import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.QuartPos;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.SectionPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;

public interface LevelReader extends BlockAndTintGetter, CollisionGetter, SignalGetter, BiomeManager.NoiseBiomeSource {
   @Nullable
   ChunkAccess getChunk(int var1, int var2, ChunkStatus var3, boolean var4);

   /** @deprecated */
   @Deprecated
   boolean hasChunk(int var1, int var2);

   int getHeight(Heightmap.Types var1, int var2, int var3);

   int getSkyDarken();

   BiomeManager getBiomeManager();

   default Holder<Biome> getBiome(BlockPos var1) {
      return this.getBiomeManager().getBiome(var1);
   }

   default Stream<BlockState> getBlockStatesIfLoaded(AABB var1) {
      int var2 = Mth.floor(var1.minX);
      int var3 = Mth.floor(var1.maxX);
      int var4 = Mth.floor(var1.minY);
      int var5 = Mth.floor(var1.maxY);
      int var6 = Mth.floor(var1.minZ);
      int var7 = Mth.floor(var1.maxZ);
      return this.hasChunksAt(var2, var4, var6, var3, var5, var7) ? this.getBlockStates(var1) : Stream.empty();
   }

   default int getBlockTint(BlockPos var1, ColorResolver var2) {
      return var2.getColor((Biome)this.getBiome(var1).value(), (double)var1.getX(), (double)var1.getZ());
   }

   default Holder<Biome> getNoiseBiome(int var1, int var2, int var3) {
      ChunkAccess var4 = this.getChunk(QuartPos.toSection(var1), QuartPos.toSection(var3), ChunkStatus.BIOMES, false);
      return var4 != null ? var4.getNoiseBiome(var1, var2, var3) : this.getUncachedNoiseBiome(var1, var2, var3);
   }

   Holder<Biome> getUncachedNoiseBiome(int var1, int var2, int var3);

   boolean isClientSide();

   /** @deprecated */
   @Deprecated
   int getSeaLevel();

   DimensionType dimensionType();

   default int getMinBuildHeight() {
      return this.dimensionType().minY();
   }

   default int getHeight() {
      return this.dimensionType().height();
   }

   default BlockPos getHeightmapPos(Heightmap.Types var1, BlockPos var2) {
      return new BlockPos(var2.getX(), this.getHeight(var1, var2.getX(), var2.getZ()), var2.getZ());
   }

   default boolean isEmptyBlock(BlockPos var1) {
      return this.getBlockState(var1).isAir();
   }

   default boolean canSeeSkyFromBelowWater(BlockPos var1) {
      if (var1.getY() >= this.getSeaLevel()) {
         return this.canSeeSky(var1);
      } else {
         BlockPos var2 = new BlockPos(var1.getX(), this.getSeaLevel(), var1.getZ());
         if (!this.canSeeSky(var2)) {
            return false;
         } else {
            for(var2 = var2.below(); var2.getY() > var1.getY(); var2 = var2.below()) {
               BlockState var3 = this.getBlockState(var2);
               if (var3.getLightBlock(this, var2) > 0 && !var3.liquid()) {
                  return false;
               }
            }

            return true;
         }
      }
   }

   default float getPathfindingCostFromLightLevels(BlockPos var1) {
      return this.getLightLevelDependentMagicValue(var1) - 0.5F;
   }

   /** @deprecated */
   @Deprecated
   default float getLightLevelDependentMagicValue(BlockPos var1) {
      float var2 = (float)this.getMaxLocalRawBrightness(var1) / 15.0F;
      float var3 = var2 / (4.0F - 3.0F * var2);
      return Mth.lerp(this.dimensionType().ambientLight(), var3, 1.0F);
   }

   default ChunkAccess getChunk(BlockPos var1) {
      return this.getChunk(SectionPos.blockToSectionCoord(var1.getX()), SectionPos.blockToSectionCoord(var1.getZ()));
   }

   default ChunkAccess getChunk(int var1, int var2) {
      return this.getChunk(var1, var2, ChunkStatus.FULL, true);
   }

   default ChunkAccess getChunk(int var1, int var2, ChunkStatus var3) {
      return this.getChunk(var1, var2, var3, true);
   }

   @Nullable
   default BlockGetter getChunkForCollisions(int var1, int var2) {
      return this.getChunk(var1, var2, ChunkStatus.EMPTY, false);
   }

   default boolean isWaterAt(BlockPos var1) {
      return this.getFluidState(var1).is(FluidTags.WATER);
   }

   default boolean containsAnyLiquid(AABB var1) {
      int var2 = Mth.floor(var1.minX);
      int var3 = Mth.ceil(var1.maxX);
      int var4 = Mth.floor(var1.minY);
      int var5 = Mth.ceil(var1.maxY);
      int var6 = Mth.floor(var1.minZ);
      int var7 = Mth.ceil(var1.maxZ);
      BlockPos.MutableBlockPos var8 = new BlockPos.MutableBlockPos();

      for(int var9 = var2; var9 < var3; ++var9) {
         for(int var10 = var4; var10 < var5; ++var10) {
            for(int var11 = var6; var11 < var7; ++var11) {
               BlockState var12 = this.getBlockState(var8.set(var9, var10, var11));
               if (!var12.getFluidState().isEmpty()) {
                  return true;
               }
            }
         }
      }

      return false;
   }

   default int getMaxLocalRawBrightness(BlockPos var1) {
      return this.getMaxLocalRawBrightness(var1, this.getSkyDarken());
   }

   default int getMaxLocalRawBrightness(BlockPos var1, int var2) {
      return var1.getX() >= -30000000 && var1.getZ() >= -30000000 && var1.getX() < 30000000 && var1.getZ() < 30000000 ? this.getRawBrightness(var1, var2) : 15;
   }

   /** @deprecated */
   @Deprecated
   default boolean hasChunkAt(int var1, int var2) {
      return this.hasChunk(SectionPos.blockToSectionCoord(var1), SectionPos.blockToSectionCoord(var2));
   }

   /** @deprecated */
   @Deprecated
   default boolean hasChunkAt(BlockPos var1) {
      return this.hasChunkAt(var1.getX(), var1.getZ());
   }

   /** @deprecated */
   @Deprecated
   default boolean hasChunksAt(BlockPos var1, BlockPos var2) {
      return this.hasChunksAt(var1.getX(), var1.getY(), var1.getZ(), var2.getX(), var2.getY(), var2.getZ());
   }

   /** @deprecated */
   @Deprecated
   default boolean hasChunksAt(int var1, int var2, int var3, int var4, int var5, int var6) {
      return var5 >= this.getMinBuildHeight() && var2 < this.getMaxBuildHeight() ? this.hasChunksAt(var1, var3, var4, var6) : false;
   }

   /** @deprecated */
   @Deprecated
   default boolean hasChunksAt(int var1, int var2, int var3, int var4) {
      int var5 = SectionPos.blockToSectionCoord(var1);
      int var6 = SectionPos.blockToSectionCoord(var3);
      int var7 = SectionPos.blockToSectionCoord(var2);
      int var8 = SectionPos.blockToSectionCoord(var4);

      for(int var9 = var5; var9 <= var6; ++var9) {
         for(int var10 = var7; var10 <= var8; ++var10) {
            if (!this.hasChunk(var9, var10)) {
               return false;
            }
         }
      }

      return true;
   }

   RegistryAccess registryAccess();

   FeatureFlagSet enabledFeatures();

   default <T> HolderLookup<T> holderLookup(ResourceKey<? extends Registry<? extends T>> var1) {
      Registry var2 = this.registryAccess().registryOrThrow(var1);
      return var2.asLookup().filterFeatures(this.enabledFeatures());
   }
}
