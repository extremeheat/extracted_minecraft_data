package net.minecraft.world.level.chunk;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.FullChunkStatus;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;

public class EmptyLevelChunk extends LevelChunk {
   private final Holder<Biome> biome;

   public EmptyLevelChunk(Level var1, ChunkPos var2, Holder<Biome> var3) {
      super(var1, var2);
      this.biome = var3;
   }

   public BlockState getBlockState(BlockPos var1) {
      return Blocks.VOID_AIR.defaultBlockState();
   }

   @Nullable
   public BlockState setBlockState(BlockPos var1, BlockState var2, boolean var3) {
      return null;
   }

   public FluidState getFluidState(BlockPos var1) {
      return Fluids.EMPTY.defaultFluidState();
   }

   public int getLightEmission(BlockPos var1) {
      return 0;
   }

   @Nullable
   public BlockEntity getBlockEntity(BlockPos var1, LevelChunk.EntityCreationType var2) {
      return null;
   }

   public void addAndRegisterBlockEntity(BlockEntity var1) {
   }

   public void setBlockEntity(BlockEntity var1) {
   }

   public void removeBlockEntity(BlockPos var1) {
   }

   public boolean isEmpty() {
      return true;
   }

   public boolean isYSpaceEmpty(int var1, int var2) {
      return true;
   }

   public boolean isSectionEmpty(int var1) {
      return true;
   }

   public FullChunkStatus getFullStatus() {
      return FullChunkStatus.FULL;
   }

   public Holder<Biome> getNoiseBiome(int var1, int var2, int var3) {
      return this.biome;
   }
}
