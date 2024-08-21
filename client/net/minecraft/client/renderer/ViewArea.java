package net.minecraft.client.renderer;

import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.chunk.SectionRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelHeightAccessor;

public class ViewArea {
   protected final LevelRenderer levelRenderer;
   protected final Level level;
   protected int sectionGridSizeY;
   protected int sectionGridSizeX;
   protected int sectionGridSizeZ;
   private int viewDistance;
   private SectionPos cameraSectionPos;
   public SectionRenderDispatcher.RenderSection[] sections;

   public ViewArea(SectionRenderDispatcher var1, Level var2, int var3, LevelRenderer var4) {
      super();
      this.levelRenderer = var4;
      this.level = var2;
      this.setViewDistance(var3);
      this.createSections(var1);
      this.cameraSectionPos = SectionPos.of(this.viewDistance + 1, 0, this.viewDistance + 1);
   }

   protected void createSections(SectionRenderDispatcher var1) {
      if (!Minecraft.getInstance().isSameThread()) {
         throw new IllegalStateException("createSections called from wrong thread: " + Thread.currentThread().getName());
      } else {
         int var2 = this.sectionGridSizeX * this.sectionGridSizeY * this.sectionGridSizeZ;
         this.sections = new SectionRenderDispatcher.RenderSection[var2];

         for (int var3 = 0; var3 < this.sectionGridSizeX; var3++) {
            for (int var4 = 0; var4 < this.sectionGridSizeY; var4++) {
               for (int var5 = 0; var5 < this.sectionGridSizeZ; var5++) {
                  int var6 = this.getSectionIndex(var3, var4, var5);
                  this.sections[var6] = var1.new RenderSection(var6, SectionPos.asLong(var3, var4 + this.level.getMinSectionY(), var5));
               }
            }
         }
      }
   }

   public void releaseAllBuffers() {
      for (SectionRenderDispatcher.RenderSection var4 : this.sections) {
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

   public void repositionCamera(SectionPos var1) {
      for (int var2 = 0; var2 < this.sectionGridSizeX; var2++) {
         int var3 = var1.x() - this.viewDistance;
         int var4 = var3 + Math.floorMod(var2 - var3, this.sectionGridSizeX);

         for (int var5 = 0; var5 < this.sectionGridSizeZ; var5++) {
            int var6 = var1.z() - this.viewDistance;
            int var7 = var6 + Math.floorMod(var5 - var6, this.sectionGridSizeZ);

            for (int var8 = 0; var8 < this.sectionGridSizeY; var8++) {
               int var9 = this.level.getMinSectionY() + var8;
               SectionRenderDispatcher.RenderSection var10 = this.sections[this.getSectionIndex(var2, var8, var5)];
               long var11 = var10.getSectionNode();
               if (var11 != SectionPos.asLong(var4, var9, var7)) {
                  var10.setSectionNode(SectionPos.asLong(var4, var9, var7));
               }
            }
         }
      }

      this.cameraSectionPos = var1;
      this.levelRenderer.getSectionOcclusionGraph().invalidate();
   }

   public SectionPos getCameraSectionPos() {
      return this.cameraSectionPos;
   }

   public void setDirty(int var1, int var2, int var3, boolean var4) {
      SectionRenderDispatcher.RenderSection var5 = this.getRenderSection(var1, var2, var3);
      if (var5 != null) {
         var5.setDirty(var4);
      }
   }

   @Nullable
   protected SectionRenderDispatcher.RenderSection getRenderSectionAt(BlockPos var1) {
      return this.getRenderSection(SectionPos.asLong(var1));
   }

   @Nullable
   protected SectionRenderDispatcher.RenderSection getRenderSection(long var1) {
      int var3 = SectionPos.x(var1);
      int var4 = SectionPos.y(var1);
      int var5 = SectionPos.z(var1);
      return this.getRenderSection(var3, var4, var5);
   }

   @Nullable
   private SectionRenderDispatcher.RenderSection getRenderSection(int var1, int var2, int var3) {
      if (!this.containsSection(var1, var2, var3)) {
         return null;
      } else {
         int var4 = var2 - this.level.getMinSectionY();
         int var5 = Math.floorMod(var1, this.sectionGridSizeX);
         int var6 = Math.floorMod(var3, this.sectionGridSizeZ);
         return this.sections[this.getSectionIndex(var5, var4, var6)];
      }
   }

   private boolean containsSection(int var1, int var2, int var3) {
      if (var2 >= this.level.getMinSectionY() && var2 <= this.level.getMaxSectionY()) {
         return var1 < this.cameraSectionPos.x() - this.viewDistance || var1 > this.cameraSectionPos.x() + this.viewDistance
            ? false
            : var3 >= this.cameraSectionPos.z() - this.viewDistance && var3 <= this.cameraSectionPos.z() + this.viewDistance;
      } else {
         return false;
      }
   }
}
