package net.minecraft.world.level.lighting;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
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
      public DataLayer getDataLayerData(SectionPos var1) {
         return null;
      }

      public int getLightValue(BlockPos var1) {
         return 0;
      }

      public void updateSectionStatus(SectionPos var1, boolean var2) {
      }
   }
}
