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

   public static enum DummyLightLayerEventListener implements LayerLightEventListener {
      INSTANCE;

      private DummyLightLayerEventListener() {
      }

      @Nullable
      @Override
      public DataLayer getDataLayerData(SectionPos var1) {
         return null;
      }

      @Override
      public int getLightValue(BlockPos var1) {
         return 0;
      }

      @Override
      public void checkBlock(BlockPos var1) {
      }

      @Override
      public void onBlockEmissionIncrease(BlockPos var1, int var2) {
      }

      @Override
      public boolean hasLightWork() {
         return false;
      }

      @Override
      public int runUpdates(int var1, boolean var2, boolean var3) {
         return var1;
      }

      @Override
      public void updateSectionStatus(SectionPos var1, boolean var2) {
      }

      @Override
      public void enableLightSources(ChunkPos var1, boolean var2) {
      }
   }
}
