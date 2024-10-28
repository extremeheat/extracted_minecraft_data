package net.minecraft.world.level.lighting;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.chunk.DataLayer;
import net.minecraft.world.level.chunk.LightChunkGetter;

public class LevelLightEngine implements LightEventListener {
   public static final int LIGHT_SECTION_PADDING = 1;
   protected final LevelHeightAccessor levelHeightAccessor;
   @Nullable
   private final LightEngine<?, ?> blockEngine;
   @Nullable
   private final LightEngine<?, ?> skyEngine;

   public LevelLightEngine(LightChunkGetter var1, boolean var2, boolean var3) {
      super();
      this.levelHeightAccessor = var1.getLevel();
      this.blockEngine = var2 ? new BlockLightEngine(var1) : null;
      this.skyEngine = var3 ? new SkyLightEngine(var1) : null;
   }

   public void checkBlock(BlockPos var1) {
      if (this.blockEngine != null) {
         this.blockEngine.checkBlock(var1);
      }

      if (this.skyEngine != null) {
         this.skyEngine.checkBlock(var1);
      }

   }

   public boolean hasLightWork() {
      if (this.skyEngine != null && this.skyEngine.hasLightWork()) {
         return true;
      } else {
         return this.blockEngine != null && this.blockEngine.hasLightWork();
      }
   }

   public int runLightUpdates() {
      int var1 = 0;
      if (this.blockEngine != null) {
         var1 += this.blockEngine.runLightUpdates();
      }

      if (this.skyEngine != null) {
         var1 += this.skyEngine.runLightUpdates();
      }

      return var1;
   }

   public void updateSectionStatus(SectionPos var1, boolean var2) {
      if (this.blockEngine != null) {
         this.blockEngine.updateSectionStatus(var1, var2);
      }

      if (this.skyEngine != null) {
         this.skyEngine.updateSectionStatus(var1, var2);
      }

   }

   public void setLightEnabled(ChunkPos var1, boolean var2) {
      if (this.blockEngine != null) {
         this.blockEngine.setLightEnabled(var1, var2);
      }

      if (this.skyEngine != null) {
         this.skyEngine.setLightEnabled(var1, var2);
      }

   }

   public void propagateLightSources(ChunkPos var1) {
      if (this.blockEngine != null) {
         this.blockEngine.propagateLightSources(var1);
      }

      if (this.skyEngine != null) {
         this.skyEngine.propagateLightSources(var1);
      }

   }

   public LayerLightEventListener getLayerListener(LightLayer var1) {
      if (var1 == LightLayer.BLOCK) {
         return (LayerLightEventListener)(this.blockEngine == null ? LayerLightEventListener.DummyLightLayerEventListener.INSTANCE : this.blockEngine);
      } else {
         return (LayerLightEventListener)(this.skyEngine == null ? LayerLightEventListener.DummyLightLayerEventListener.INSTANCE : this.skyEngine);
      }
   }

   public String getDebugData(LightLayer var1, SectionPos var2) {
      if (var1 == LightLayer.BLOCK) {
         if (this.blockEngine != null) {
            return this.blockEngine.getDebugData(var2.asLong());
         }
      } else if (this.skyEngine != null) {
         return this.skyEngine.getDebugData(var2.asLong());
      }

      return "n/a";
   }

   public LayerLightSectionStorage.SectionType getDebugSectionType(LightLayer var1, SectionPos var2) {
      if (var1 == LightLayer.BLOCK) {
         if (this.blockEngine != null) {
            return this.blockEngine.getDebugSectionType(var2.asLong());
         }
      } else if (this.skyEngine != null) {
         return this.skyEngine.getDebugSectionType(var2.asLong());
      }

      return LayerLightSectionStorage.SectionType.EMPTY;
   }

   public void queueSectionData(LightLayer var1, SectionPos var2, @Nullable DataLayer var3) {
      if (var1 == LightLayer.BLOCK) {
         if (this.blockEngine != null) {
            this.blockEngine.queueSectionData(var2.asLong(), var3);
         }
      } else if (this.skyEngine != null) {
         this.skyEngine.queueSectionData(var2.asLong(), var3);
      }

   }

   public void retainData(ChunkPos var1, boolean var2) {
      if (this.blockEngine != null) {
         this.blockEngine.retainData(var1, var2);
      }

      if (this.skyEngine != null) {
         this.skyEngine.retainData(var1, var2);
      }

   }

   public int getRawBrightness(BlockPos var1, int var2) {
      int var3 = this.skyEngine == null ? 0 : this.skyEngine.getLightValue(var1) - var2;
      int var4 = this.blockEngine == null ? 0 : this.blockEngine.getLightValue(var1);
      return Math.max(var4, var3);
   }

   public boolean lightOnInSection(SectionPos var1) {
      long var2 = var1.asLong();
      return this.blockEngine == null || this.blockEngine.storage.lightOnInSection(var2) && (this.skyEngine == null || this.skyEngine.storage.lightOnInSection(var2));
   }

   public int getLightSectionCount() {
      return this.levelHeightAccessor.getSectionsCount() + 2;
   }

   public int getMinLightSection() {
      return this.levelHeightAccessor.getMinSection() - 1;
   }

   public int getMaxLightSection() {
      return this.getMinLightSection() + this.getLightSectionCount();
   }
}
