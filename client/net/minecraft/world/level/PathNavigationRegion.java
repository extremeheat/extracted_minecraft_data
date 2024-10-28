package net.minecraft.world.level;

import com.google.common.base.Suppliers;
import java.util.List;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.SectionPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkSource;
import net.minecraft.world.level.chunk.EmptyLevelChunk;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.VoxelShape;

public class PathNavigationRegion implements BlockGetter, CollisionGetter {
   protected final int centerX;
   protected final int centerZ;
   protected final ChunkAccess[][] chunks;
   protected boolean allEmpty;
   protected final Level level;
   private final Supplier<Holder<Biome>> plains;

   public PathNavigationRegion(Level var1, BlockPos var2, BlockPos var3) {
      super();
      this.level = var1;
      this.plains = Suppliers.memoize(() -> {
         return var1.registryAccess().registryOrThrow(Registries.BIOME).getHolderOrThrow(Biomes.PLAINS);
      });
      this.centerX = SectionPos.blockToSectionCoord(var2.getX());
      this.centerZ = SectionPos.blockToSectionCoord(var2.getZ());
      int var4 = SectionPos.blockToSectionCoord(var3.getX());
      int var5 = SectionPos.blockToSectionCoord(var3.getZ());
      this.chunks = new ChunkAccess[var4 - this.centerX + 1][var5 - this.centerZ + 1];
      ChunkSource var6 = var1.getChunkSource();
      this.allEmpty = true;

      int var7;
      int var8;
      for(var7 = this.centerX; var7 <= var4; ++var7) {
         for(var8 = this.centerZ; var8 <= var5; ++var8) {
            this.chunks[var7 - this.centerX][var8 - this.centerZ] = var6.getChunkNow(var7, var8);
         }
      }

      for(var7 = SectionPos.blockToSectionCoord(var2.getX()); var7 <= SectionPos.blockToSectionCoord(var3.getX()); ++var7) {
         for(var8 = SectionPos.blockToSectionCoord(var2.getZ()); var8 <= SectionPos.blockToSectionCoord(var3.getZ()); ++var8) {
            ChunkAccess var9 = this.chunks[var7 - this.centerX][var8 - this.centerZ];
            if (var9 != null && !var9.isYSpaceEmpty(var2.getY(), var3.getY())) {
               this.allEmpty = false;
               return;
            }
         }
      }

   }

   private ChunkAccess getChunk(BlockPos var1) {
      return this.getChunk(SectionPos.blockToSectionCoord(var1.getX()), SectionPos.blockToSectionCoord(var1.getZ()));
   }

   private ChunkAccess getChunk(int var1, int var2) {
      int var3 = var1 - this.centerX;
      int var4 = var2 - this.centerZ;
      if (var3 >= 0 && var3 < this.chunks.length && var4 >= 0 && var4 < this.chunks[var3].length) {
         ChunkAccess var5 = this.chunks[var3][var4];
         return (ChunkAccess)(var5 != null ? var5 : new EmptyLevelChunk(this.level, new ChunkPos(var1, var2), (Holder)this.plains.get()));
      } else {
         return new EmptyLevelChunk(this.level, new ChunkPos(var1, var2), (Holder)this.plains.get());
      }
   }

   public WorldBorder getWorldBorder() {
      return this.level.getWorldBorder();
   }

   public BlockGetter getChunkForCollisions(int var1, int var2) {
      return this.getChunk(var1, var2);
   }

   public List<VoxelShape> getEntityCollisions(@Nullable Entity var1, AABB var2) {
      return List.of();
   }

   @Nullable
   public BlockEntity getBlockEntity(BlockPos var1) {
      ChunkAccess var2 = this.getChunk(var1);
      return var2.getBlockEntity(var1);
   }

   public BlockState getBlockState(BlockPos var1) {
      if (this.isOutsideBuildHeight(var1)) {
         return Blocks.AIR.defaultBlockState();
      } else {
         ChunkAccess var2 = this.getChunk(var1);
         return var2.getBlockState(var1);
      }
   }

   public FluidState getFluidState(BlockPos var1) {
      if (this.isOutsideBuildHeight(var1)) {
         return Fluids.EMPTY.defaultFluidState();
      } else {
         ChunkAccess var2 = this.getChunk(var1);
         return var2.getFluidState(var1);
      }
   }

   public int getMinBuildHeight() {
      return this.level.getMinBuildHeight();
   }

   public int getHeight() {
      return this.level.getHeight();
   }

   public ProfilerFiller getProfiler() {
      return this.level.getProfiler();
   }
}
