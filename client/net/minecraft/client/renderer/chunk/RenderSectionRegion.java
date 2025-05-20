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

public class RenderSectionRegion implements BlockAndTintGetter {
   public static final int RADIUS = 1;
   public static final int SIZE = 3;
   private final int minSectionX;
   private final int minSectionY;
   private final int minSectionZ;
   private final SectionCopy[] sections;
   private final Level level;

   RenderSectionRegion(Level var1, int var2, int var3, int var4, SectionCopy[] var5) {
      super();
      this.level = var1;
      this.minSectionX = var2;
      this.minSectionY = var3;
      this.minSectionZ = var4;
      this.sections = var5;
   }

   public BlockState getBlockState(BlockPos var1) {
      return this.getSection(SectionPos.blockToSectionCoord(var1.getX()), SectionPos.blockToSectionCoord(var1.getY()), SectionPos.blockToSectionCoord(var1.getZ())).getBlockState(var1);
   }

   public FluidState getFluidState(BlockPos var1) {
      return this.getSection(SectionPos.blockToSectionCoord(var1.getX()), SectionPos.blockToSectionCoord(var1.getY()), SectionPos.blockToSectionCoord(var1.getZ())).getBlockState(var1).getFluidState();
   }

   public float getShade(Direction var1, boolean var2) {
      return this.level.getShade(var1, var2);
   }

   public LevelLightEngine getLightEngine() {
      return this.level.getLightEngine();
   }

   @Nullable
   public BlockEntity getBlockEntity(BlockPos var1) {
      return this.getSection(SectionPos.blockToSectionCoord(var1.getX()), SectionPos.blockToSectionCoord(var1.getY()), SectionPos.blockToSectionCoord(var1.getZ())).getBlockEntity(var1);
   }

   private SectionCopy getSection(int var1, int var2, int var3) {
      return this.sections[index(this.minSectionX, this.minSectionY, this.minSectionZ, var1, var2, var3)];
   }

   public int getBlockTint(BlockPos var1, ColorResolver var2) {
      return this.level.getBlockTint(var1, var2);
   }

   public int getMinY() {
      return this.level.getMinY();
   }

   public int getHeight() {
      return this.level.getHeight();
   }

   public static int index(int var0, int var1, int var2, int var3, int var4, int var5) {
      return var3 - var0 + (var4 - var1) * 3 + (var5 - var2) * 3 * 3;
   }
}
