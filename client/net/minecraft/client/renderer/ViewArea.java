package net.minecraft.client.renderer;

import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.chunk.SectionRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelHeightAccessor;

public class ViewArea {
   protected final LevelRenderer levelRenderer;
   protected final Level level;
   protected int sectionGridSizeY;
   protected int sectionGridSizeX;
   protected int sectionGridSizeZ;
   private int viewDistance;
   public SectionRenderDispatcher.RenderSection[] sections;

   public ViewArea(SectionRenderDispatcher var1, Level var2, int var3, LevelRenderer var4) {
      super();
      this.levelRenderer = var4;
      this.level = var2;
      this.setViewDistance(var3);
      this.createSections(var1);
   }

   protected void createSections(SectionRenderDispatcher var1) {
      if (!Minecraft.getInstance().isSameThread()) {
         throw new IllegalStateException("createSections called from wrong thread: " + Thread.currentThread().getName());
      } else {
         int var2 = this.sectionGridSizeX * this.sectionGridSizeY * this.sectionGridSizeZ;
         this.sections = new SectionRenderDispatcher.RenderSection[var2];

         for(int var3 = 0; var3 < this.sectionGridSizeX; ++var3) {
            for(int var4 = 0; var4 < this.sectionGridSizeY; ++var4) {
               for(int var5 = 0; var5 < this.sectionGridSizeZ; ++var5) {
                  int var6 = this.getSectionIndex(var3, var4, var5);
                  SectionRenderDispatcher.RenderSection[] var10000 = this.sections;
                  Objects.requireNonNull(var1);
                  var10000[var6] = var1.new RenderSection(var6, var3 * 16, this.level.getMinBuildHeight() + var4 * 16, var5 * 16);
               }
            }
         }

      }
   }

   public void releaseAllBuffers() {
      SectionRenderDispatcher.RenderSection[] var1 = this.sections;
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         SectionRenderDispatcher.RenderSection var4 = var1[var3];
         var4.releaseBuffers();
      }

   }

   private int getSectionIndex(int var1, int var2, int var3) {
      return (var3 * this.sectionGridSizeY + var2) * this.sectionGridSizeX + var1;
   }

   protected void setViewDistance(int var1) {
      int var2 = var1 * 2 + 1;
      this.sectionGridSizeX = var2;
      this.sectionGridSizeY = this.level.getSectionsCount();
      this.sectionGridSizeZ = var2;
      this.viewDistance = var1;
   }

   public int getViewDistance() {
      return this.viewDistance;
   }

   public LevelHeightAccessor getLevelHeightAccessor() {
      return this.level;
   }

   public void repositionCamera(double var1, double var3) {
      int var5 = Mth.ceil(var1);
      int var6 = Mth.ceil(var3);

      for(int var7 = 0; var7 < this.sectionGridSizeX; ++var7) {
         int var8 = this.sectionGridSizeX * 16;
         int var9 = var5 - 8 - var8 / 2;
         int var10 = var9 + Math.floorMod(var7 * 16 - var9, var8);

         for(int var11 = 0; var11 < this.sectionGridSizeZ; ++var11) {
            int var12 = this.sectionGridSizeZ * 16;
            int var13 = var6 - 8 - var12 / 2;
            int var14 = var13 + Math.floorMod(var11 * 16 - var13, var12);

            for(int var15 = 0; var15 < this.sectionGridSizeY; ++var15) {
               int var16 = this.level.getMinBuildHeight() + var15 * 16;
               SectionRenderDispatcher.RenderSection var17 = this.sections[this.getSectionIndex(var7, var15, var11)];
               BlockPos var18 = var17.getOrigin();
               if (var10 != var18.getX() || var16 != var18.getY() || var14 != var18.getZ()) {
                  var17.setOrigin(var10, var16, var14);
               }
            }
         }
      }

   }

   public void setDirty(int var1, int var2, int var3, boolean var4) {
      int var5 = Math.floorMod(var1, this.sectionGridSizeX);
      int var6 = Math.floorMod(var2 - this.level.getMinSection(), this.sectionGridSizeY);
      int var7 = Math.floorMod(var3, this.sectionGridSizeZ);
      SectionRenderDispatcher.RenderSection var8 = this.sections[this.getSectionIndex(var5, var6, var7)];
      var8.setDirty(var4);
   }

   @Nullable
   protected SectionRenderDispatcher.RenderSection getRenderSectionAt(BlockPos var1) {
      int var2 = Mth.floorDiv(var1.getY() - this.level.getMinBuildHeight(), 16);
      if (var2 >= 0 && var2 < this.sectionGridSizeY) {
         int var3 = Mth.positiveModulo(Mth.floorDiv(var1.getX(), 16), this.sectionGridSizeX);
         int var4 = Mth.positiveModulo(Mth.floorDiv(var1.getZ(), 16), this.sectionGridSizeZ);
         return this.sections[this.getSectionIndex(var3, var2, var4)];
      } else {
         return null;
      }
   }
}
