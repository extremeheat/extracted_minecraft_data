package net.minecraft.world.level.lighting;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.DataLayer;

public interface LayerLightEventListener extends LightEventListener {
   @Nullable
   DataLayer getDataLayerData(SectionPos var1);

   int getLightValue(BlockPos var1);

   public static record ConstantLayer(int c) implements LayerLightEventListener {
      private final int lightLevel;
      public static final LayerLightEventListener.ConstantLayer ZERO = new LayerLightEventListener.ConstantLayer(0);
      public static final LayerLightEventListener.ConstantLayer FULL_BRIGHT = new LayerLightEventListener.ConstantLayer(15);

      public ConstantLayer(int var1) {
         super();
         this.lightLevel = var1;
      }

      @Nullable
      @Override
      public DataLayer getDataLayerData(SectionPos var1) {
         return null;
      }

      @Override
      public int getLightValue(BlockPos var1) {
         return this.lightLevel;
      }

      @Override
      public void checkBlock(BlockPos var1) {
      }

      @Override
      public boolean hasLightWork() {
         return false;
      }

      @Override
      public int runLightUpdates() {
         return 0;
      }

      @Override
      public void updateSectionStatus(SectionPos var1, boolean var2) {
      }

      @Override
      public void setLightEnabled(ChunkPos var1, boolean var2) {
      }

      @Override
      public void propagateLightSources(ChunkPos var1) {
      }
   }
}
