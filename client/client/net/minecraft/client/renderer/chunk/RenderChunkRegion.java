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
   private final int centerX;
   private final int centerZ;
   protected final RenderChunk[][] chunks;
   protected final Level level;

   RenderChunkRegion(Level var1, int var2, int var3, RenderChunk[][] var4) {
      super();
      this.level = var1;
      this.centerX = var2;
      this.centerZ = var3;
      this.chunks = var4;
   }

   @Override
   public BlockState getBlockState(BlockPos var1) {
      int var2 = SectionPos.blockToSectionCoord(var1.getX()) - this.centerX;
      int var3 = SectionPos.blockToSectionCoord(var1.getZ()) - this.centerZ;
      return this.chunks[var2][var3].getBlockState(var1);
   }

   @Override
   public FluidState getFluidState(BlockPos var1) {
      int var2 = SectionPos.blockToSectionCoord(var1.getX()) - this.centerX;
      int var3 = SectionPos.blockToSectionCoord(var1.getZ()) - this.centerZ;
      return this.chunks[var2][var3].getBlockState(var1).getFluidState();
   }

   @Override
   public float getShade(Direction var1, boolean var2) {
      return this.level.getShade(var1, var2);
   }

   @Override
   public LevelLightEngine getLightEngine() {
      return this.level.getLightEngine();
   }

   @Nullable
   @Override
   public BlockEntity getBlockEntity(BlockPos var1) {
      int var2 = SectionPos.blockToSectionCoord(var1.getX()) - this.centerX;
      int var3 = SectionPos.blockToSectionCoord(var1.getZ()) - this.centerZ;
      return this.chunks[var2][var3].getBlockEntity(var1);
   }

   @Override
   public int getBlockTint(BlockPos var1, ColorResolver var2) {
      return this.level.getBlockTint(var1, var2);
   }

   @Override
   public int getMinBuildHeight() {
      return this.level.getMinBuildHeight();
   }

   @Override
   public int getHeight() {
      return this.level.getHeight();
   }
}
