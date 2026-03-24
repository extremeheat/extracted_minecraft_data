package net.minecraft.client.renderer;

import java.util.Objects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.chunk.SectionRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelHeightAccessor;
import org.jspecify.annotations.Nullable;

public class ViewArea {
   protected final LevelRenderer levelRenderer;
   protected final Level level;
   protected int sectionGridSizeY;
   protected int sectionGridSizeX;
   protected int sectionGridSizeZ;
   private int viewDistance;
   private SectionPos cameraSectionPos;
   public SectionRenderDispatcher.RenderSection[] sections;

   public ViewArea(final SectionRenderDispatcher sectionRenderDispatcher, final Level level, final int renderDistance, final LevelRenderer levelRenderer) {
      super();
      this.levelRenderer = levelRenderer;
      this.level = level;
      this.setViewDistance(renderDistance);
      this.createSections(sectionRenderDispatcher);
      this.cameraSectionPos = SectionPos.of(this.viewDistance + 1, 0, this.viewDistance + 1);
   }

   protected void createSections(final SectionRenderDispatcher sectionRenderDispatcher) {
      if (!Minecraft.getInstance().isSameThread()) {
         throw new IllegalStateException("createSections called from wrong thread: " + Thread.currentThread().getName());
      } else {
         int totalSections = this.sectionGridSizeX * this.sectionGridSizeY * this.sectionGridSizeZ;
         this.sections = new SectionRenderDispatcher.RenderSection[totalSections];

         for(int x = 0; x < this.sectionGridSizeX; ++x) {
            for(int y = 0; y < this.sectionGridSizeY; ++y) {
               for(int z = 0; z < this.sectionGridSizeZ; ++z) {
                  int index = this.getSectionIndex(x, y, z);
                  SectionRenderDispatcher.RenderSection[] var10000 = this.sections;
                  Objects.requireNonNull(sectionRenderDispatcher);
                  var10000[index] = sectionRenderDispatcher.new RenderSection(index, SectionPos.asLong(x, y + this.level.getMinSectionY(), z));
               }
            }
         }

      }
   }

   public void releaseAllBuffers() {
      for(SectionRenderDispatcher.RenderSection section : this.sections) {
         section.reset();
      }

   }

   private int getSectionIndex(final int x, final int y, final int z) {
      return (z * this.sectionGridSizeY + y) * this.sectionGridSizeX + x;
   }

   protected void setViewDistance(final int renderDistance) {
      int dist = renderDistance * 2 + 1;
      this.sectionGridSizeX = dist;
      this.sectionGridSizeY = this.level.getSectionsCount();
      this.sectionGridSizeZ = dist;
      this.viewDistance = renderDistance;
   }

   public int getViewDistance() {
      return this.viewDistance;
   }

   public LevelHeightAccessor getLevelHeightAccessor() {
      return this.level;
   }

   public void repositionCamera(final SectionPos cameraSectionPos) {
      for(int gridX = 0; gridX < this.sectionGridSizeX; ++gridX) {
         int lowestX = cameraSectionPos.x() - this.viewDistance;
         int newSectionX = lowestX + Math.floorMod(gridX - lowestX, this.sectionGridSizeX);

         for(int gridZ = 0; gridZ < this.sectionGridSizeZ; ++gridZ) {
            int lowestZ = cameraSectionPos.z() - this.viewDistance;
            int newSectionZ = lowestZ + Math.floorMod(gridZ - lowestZ, this.sectionGridSizeZ);

            for(int gridY = 0; gridY < this.sectionGridSizeY; ++gridY) {
               int newSectionY = this.level.getMinSectionY() + gridY;
               SectionRenderDispatcher.RenderSection section = this.sections[this.getSectionIndex(gridX, gridY, gridZ)];
               long sectionNode = section.getSectionNode();
               if (sectionNode != SectionPos.asLong(newSectionX, newSectionY, newSectionZ)) {
                  section.setSectionNode(SectionPos.asLong(newSectionX, newSectionY, newSectionZ));
               }
            }
         }
      }

      this.cameraSectionPos = cameraSectionPos;
      this.levelRenderer.getSectionOcclusionGraph().invalidate();
   }

   public SectionPos getCameraSectionPos() {
      return this.cameraSectionPos;
   }

   public void setDirty(final int sectionX, final int sectionY, final int sectionZ, final boolean playerChanged) {
      SectionRenderDispatcher.RenderSection section = this.getRenderSection(sectionX, sectionY, sectionZ);
      if (section != null) {
         section.setDirty(playerChanged);
      }

   }

   protected SectionRenderDispatcher.@Nullable RenderSection getRenderSectionAt(final BlockPos pos) {
      return this.getRenderSection(SectionPos.asLong(pos));
   }

   protected SectionRenderDispatcher.@Nullable RenderSection getRenderSection(final long sectionNode) {
      int sectionX = SectionPos.x(sectionNode);
      int sectionY = SectionPos.y(sectionNode);
      int sectionZ = SectionPos.z(sectionNode);
      return this.getRenderSection(sectionX, sectionY, sectionZ);
   }

   private SectionRenderDispatcher.@Nullable RenderSection getRenderSection(final int sectionX, final int sectionY, final int sectionZ) {
      if (!this.containsSection(sectionX, sectionY, sectionZ)) {
         return null;
      } else {
         int y = sectionY - this.level.getMinSectionY();
         int x = Math.floorMod(sectionX, this.sectionGridSizeX);
         int z = Math.floorMod(sectionZ, this.sectionGridSizeZ);
         return this.sections[this.getSectionIndex(x, y, z)];
      }
   }

   private boolean containsSection(final int sectionX, final int sectionY, final int sectionZ) {
      if (sectionY >= this.level.getMinSectionY() && sectionY <= this.level.getMaxSectionY()) {
         if (sectionX >= this.cameraSectionPos.x() - this.viewDistance && sectionX <= this.cameraSectionPos.x() + this.viewDistance) {
            return sectionZ >= this.cameraSectionPos.z() - this.viewDistance && sectionZ <= this.cameraSectionPos.z() + this.viewDistance;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }
}
