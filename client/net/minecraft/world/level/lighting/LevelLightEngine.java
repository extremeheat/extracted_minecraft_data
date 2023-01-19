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
   public static final int MAX_SOURCE_LEVEL = 15;
   public static final int LIGHT_SECTION_PADDING = 1;
   protected final LevelHeightAccessor levelHeightAccessor;
   @Nullable
   private final LayerLightEngine<?, ?> blockEngine;
   @Nullable
   private final LayerLightEngine<?, ?> skyEngine;

   public LevelLightEngine(LightChunkGetter var1, boolean var2, boolean var3) {
      super();
      this.levelHeightAccessor = var1.getLevel();
      this.blockEngine = var2 ? new BlockLightEngine(var1) : null;
      this.skyEngine = var3 ? new SkyLightEngine(var1) : null;
   }

   @Override
   public void checkBlock(BlockPos var1) {
      if (this.blockEngine != null) {
         this.blockEngine.checkBlock(var1);
      }

      if (this.skyEngine != null) {
         this.skyEngine.checkBlock(var1);
      }
   }

   @Override
   public void onBlockEmissionIncrease(BlockPos var1, int var2) {
      if (this.blockEngine != null) {
         this.blockEngine.onBlockEmissionIncrease(var1, var2);
      }
   }

   @Override
   public boolean hasLightWork() {
      if (this.skyEngine != null && this.skyEngine.hasLightWork()) {
         return true;
      } else {
         return this.blockEngine != null && this.blockEngine.hasLightWork();
      }
   }

   @Override
   public int runUpdates(int var1, boolean var2, boolean var3) {
      if (this.blockEngine != null && this.skyEngine != null) {
         int var4 = var1 / 2;
         int var5 = this.blockEngine.runUpdates(var4, var2, var3);
         int var6 = var1 - var4 + var5;
         int var7 = this.skyEngine.runUpdates(var6, var2, var3);
         return var5 == 0 && var7 > 0 ? this.blockEngine.runUpdates(var7, var2, var3) : var7;
      } else if (this.blockEngine != null) {
         return this.blockEngine.runUpdates(var1, var2, var3);
      } else {
         return this.skyEngine != null ? this.skyEngine.runUpdates(var1, var2, var3) : var1;
      }
   }

   @Override
   public void updateSectionStatus(SectionPos var1, boolean var2) {
      if (this.blockEngine != null) {
         this.blockEngine.updateSectionStatus(var1, var2);
      }

      if (this.skyEngine != null) {
         this.skyEngine.updateSectionStatus(var1, var2);
      }
   }

   @Override
   public void enableLightSources(ChunkPos var1, boolean var2) {
      if (this.blockEngine != null) {
         this.blockEngine.enableLightSources(var1, var2);
      }

      if (this.skyEngine != null) {
         this.skyEngine.enableLightSources(var1, var2);
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

   public void queueSectionData(LightLayer var1, SectionPos var2, @Nullable DataLayer var3, boolean var4) {
      if (var1 == LightLayer.BLOCK) {
         if (this.blockEngine != null) {
            this.blockEngine.queueSectionData(var2.asLong(), var3, var4);
         }
      } else if (this.skyEngine != null) {
         this.skyEngine.queueSectionData(var2.asLong(), var3, var4);
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
