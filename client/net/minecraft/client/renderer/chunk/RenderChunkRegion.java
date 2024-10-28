package net.minecraft.client.renderer.chunk;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.ColorResolver;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.level.material.FluidState;

public class RenderChunkRegion implements BlockAndTintGetter {
   public static final int RADIUS = 1;
   public static final int SIZE = 3;
   private final int minChunkX;
   private final int minChunkZ;
   protected final RenderChunk[] chunks;
   protected final Level level;

   RenderChunkRegion(Level var1, int var2, int var3, RenderChunk[] var4) {
      super();
      this.level = var1;
      this.minChunkX = var2;
      this.minChunkZ = var3;
      this.chunks = var4;
   }

   public BlockState getBlockState(BlockPos var1) {
      return this.getChunk(SectionPos.blockToSectionCoord(var1.getX()), SectionPos.blockToSectionCoord(var1.getZ())).getBlockState(var1);
   }

   public FluidState getFluidState(BlockPos var1) {
      return this.getChunk(SectionPos.blockToSectionCoord(var1.getX()), SectionPos.blockToSectionCoord(var1.getZ())).getBlockState(var1).getFluidState();
   }

   public float getShade(Direction var1, boolean var2) {
      return this.level.getShade(var1, var2);
   }

   public LevelLightEngine getLightEngine() {
      return this.level.getLightEngine();
   }

   @Nullable
   public BlockEntity getBlockEntity(BlockPos var1) {
      return this.getChunk(SectionPos.blockToSectionCoord(var1.getX()), SectionPos.blockToSectionCoord(var1.getZ())).getBlockEntity(var1);
   }

   private RenderChunk getChunk(int var1, int var2) {
      return this.chunks[index(this.minChunkX, this.minChunkZ, var1, var2)];
   }

   public int getBlockTint(BlockPos var1, ColorResolver var2) {
      return this.level.getBlockTint(var1, var2);
   }

   public int getMinBuildHeight() {
      return this.level.getMinBuildHeight();
   }

   public int getHeight() {
      return this.level.getHeight();
   }

   public static int index(int var0, int var1, int var2, int var3) {
      return var2 - var0 + (var3 - var1) * 3;
   }
}
