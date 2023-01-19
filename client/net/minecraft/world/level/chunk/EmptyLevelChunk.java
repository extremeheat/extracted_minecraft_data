package net.minecraft.world.level.chunk;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ChunkHolder;
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

   @Override
   public BlockState getBlockState(BlockPos var1) {
      return Blocks.VOID_AIR.defaultBlockState();
   }

   @Nullable
   @Override
   public BlockState setBlockState(BlockPos var1, BlockState var2, boolean var3) {
      return null;
   }

   @Override
   public FluidState getFluidState(BlockPos var1) {
      return Fluids.EMPTY.defaultFluidState();
   }

   @Override
   public int getLightEmission(BlockPos var1) {
      return 0;
   }

   @Nullable
   @Override
   public BlockEntity getBlockEntity(BlockPos var1, LevelChunk.EntityCreationType var2) {
      return null;
   }

   @Override
   public void addAndRegisterBlockEntity(BlockEntity var1) {
   }

   @Override
   public void setBlockEntity(BlockEntity var1) {
   }

   @Override
   public void removeBlockEntity(BlockPos var1) {
   }

   @Override
   public boolean isEmpty() {
      return true;
   }

   @Override
   public boolean isYSpaceEmpty(int var1, int var2) {
      return true;
   }

   @Override
   public ChunkHolder.FullChunkStatus getFullStatus() {
      return ChunkHolder.FullChunkStatus.BORDER;
   }

   @Override
   public Holder<Biome> getNoiseBiome(int var1, int var2, int var3) {
      return this.biome;
   }
}
