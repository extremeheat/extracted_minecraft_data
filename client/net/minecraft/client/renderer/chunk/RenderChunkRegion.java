package net.minecraft.client.renderer.chunk;

import java.util.Iterator;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.ColorResolver;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.level.material.FluidState;

public class RenderChunkRegion implements BlockAndTintGetter {
   protected final int centerX;
   protected final int centerZ;
   protected final BlockPos start;
   protected final int xLength;
   protected final int yLength;
   protected final int zLength;
   protected final LevelChunk[][] chunks;
   protected final BlockState[] blockStates;
   protected final FluidState[] fluidStates;
   protected final Level level;

   @Nullable
   public static RenderChunkRegion createIfNotEmpty(Level var0, BlockPos var1, BlockPos var2, int var3) {
      int var4 = SectionPos.blockToSectionCoord(var1.getX() - var3);
      int var5 = SectionPos.blockToSectionCoord(var1.getZ() - var3);
      int var6 = SectionPos.blockToSectionCoord(var2.getX() + var3);
      int var7 = SectionPos.blockToSectionCoord(var2.getZ() + var3);
      LevelChunk[][] var8 = new LevelChunk[var6 - var4 + 1][var7 - var5 + 1];

      for(int var9 = var4; var9 <= var6; ++var9) {
         for(int var10 = var5; var10 <= var7; ++var10) {
            var8[var9 - var4][var10 - var5] = var0.getChunk(var9, var10);
         }
      }

      if (isAllEmpty(var1, var2, var4, var5, var8)) {
         return null;
      } else {
         boolean var12 = true;
         BlockPos var13 = var1.offset(-1, -1, -1);
         BlockPos var11 = var2.offset(1, 1, 1);
         return new RenderChunkRegion(var0, var4, var5, var8, var13, var11);
      }
   }

   public static boolean isAllEmpty(BlockPos var0, BlockPos var1, int var2, int var3, LevelChunk[][] var4) {
      for(int var5 = SectionPos.blockToSectionCoord(var0.getX()); var5 <= SectionPos.blockToSectionCoord(var1.getX()); ++var5) {
         for(int var6 = SectionPos.blockToSectionCoord(var0.getZ()); var6 <= SectionPos.blockToSectionCoord(var1.getZ()); ++var6) {
            LevelChunk var7 = var4[var5 - var2][var6 - var3];
            if (!var7.isYSpaceEmpty(var0.getY(), var1.getY())) {
               return false;
            }
         }
      }

      return true;
   }

   public RenderChunkRegion(Level var1, int var2, int var3, LevelChunk[][] var4, BlockPos var5, BlockPos var6) {
      super();
      this.level = var1;
      this.centerX = var2;
      this.centerZ = var3;
      this.chunks = var4;
      this.start = var5;
      this.xLength = var6.getX() - var5.getX() + 1;
      this.yLength = var6.getY() - var5.getY() + 1;
      this.zLength = var6.getZ() - var5.getZ() + 1;
      this.blockStates = new BlockState[this.xLength * this.yLength * this.zLength];
      this.fluidStates = new FluidState[this.xLength * this.yLength * this.zLength];

      BlockPos var8;
      LevelChunk var11;
      int var12;
      for(Iterator var7 = BlockPos.betweenClosed(var5, var6).iterator(); var7.hasNext(); this.fluidStates[var12] = var11.getFluidState(var8)) {
         var8 = (BlockPos)var7.next();
         int var9 = SectionPos.blockToSectionCoord(var8.getX()) - var2;
         int var10 = SectionPos.blockToSectionCoord(var8.getZ()) - var3;
         var11 = var4[var9][var10];
         var12 = this.index(var8);
         this.blockStates[var12] = var11.getBlockState(var8);
      }

   }

   protected final int index(BlockPos var1) {
      return this.index(var1.getX(), var1.getY(), var1.getZ());
   }

   protected int index(int var1, int var2, int var3) {
      int var4 = var1 - this.start.getX();
      int var5 = var2 - this.start.getY();
      int var6 = var3 - this.start.getZ();
      return var6 * this.xLength * this.yLength + var5 * this.xLength + var4;
   }

   public BlockState getBlockState(BlockPos var1) {
      return this.blockStates[this.index(var1)];
   }

   public FluidState getFluidState(BlockPos var1) {
      return this.fluidStates[this.index(var1)];
   }

   public float getShade(Direction var1, boolean var2) {
      return this.level.getShade(var1, var2);
   }

   public LevelLightEngine getLightEngine() {
      return this.level.getLightEngine();
   }

   @Nullable
   public BlockEntity getBlockEntity(BlockPos var1) {
      return this.getBlockEntity(var1, LevelChunk.EntityCreationType.IMMEDIATE);
   }

   @Nullable
   public BlockEntity getBlockEntity(BlockPos var1, LevelChunk.EntityCreationType var2) {
      int var3 = SectionPos.blockToSectionCoord(var1.getX()) - this.centerX;
      int var4 = SectionPos.blockToSectionCoord(var1.getZ()) - this.centerZ;
      return this.chunks[var3][var4].getBlockEntity(var1, var2);
   }

   public int getBlockTint(BlockPos var1, ColorResolver var2) {
      return this.level.getBlockTint(var1, var2);
   }

   public int getSectionsCount() {
      return this.level.getSectionsCount();
   }

   public int getMinSection() {
      return this.level.getMinSection();
   }
}
