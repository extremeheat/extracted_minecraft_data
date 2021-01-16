package net.minecraft.world.level.chunk;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.data.worldgen.biome.Biomes;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.AABB;

public class EmptyLevelChunk extends LevelChunk {
   private static final Biome[] BIOMES;

   public EmptyLevelChunk(Level var1, ChunkPos var2) {
      super(var1, var2, new ChunkBiomeContainer(var1.registryAccess().registryOrThrow(Registry.BIOME_REGISTRY), BIOMES));
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

   @Nullable
   public LevelLightEngine getLightEngine() {
      return null;
   }

   public int getLightEmission(BlockPos var1) {
      return 0;
   }

   public void addEntity(Entity var1) {
   }

   public void removeEntity(Entity var1) {
   }

   public void removeEntity(Entity var1, int var2) {
   }

   @Nullable
   public BlockEntity getBlockEntity(BlockPos var1, LevelChunk.EntityCreationType var2) {
      return null;
   }

   public void addBlockEntity(BlockEntity var1) {
   }

   public void setBlockEntity(BlockPos var1, BlockEntity var2) {
   }

   public void removeBlockEntity(BlockPos var1) {
   }

   public void markUnsaved() {
   }

   public void getEntities(@Nullable Entity var1, AABB var2, List<Entity> var3, Predicate<? super Entity> var4) {
   }

   public <T extends Entity> void getEntitiesOfClass(Class<? extends T> var1, AABB var2, List<T> var3, Predicate<? super T> var4) {
   }

   public boolean isEmpty() {
      return true;
   }

   public boolean isYSpaceEmpty(int var1, int var2) {
      return true;
   }

   public ChunkHolder.FullChunkStatus getFullStatus() {
      return ChunkHolder.FullChunkStatus.BORDER;
   }

   static {
      BIOMES = (Biome[])Util.make(new Biome[ChunkBiomeContainer.BIOMES_SIZE], (var0) -> {
         Arrays.fill(var0, Biomes.PLAINS);
      });
   }
}
