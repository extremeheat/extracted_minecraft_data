package net.minecraft.world.level.lighting;

import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.DataLayer;
import org.jspecify.annotations.Nullable;

public interface LayerLightEventListener extends LightEventListener {
   @Nullable DataLayer getDataLayerData(final SectionPos pos);

   int getLightValue(final BlockPos pos);

   public static enum DummyLightLayerEventListener implements LayerLightEventListener {
      INSTANCE;

      private DummyLightLayerEventListener() {
      }

      public @Nullable DataLayer getDataLayerData(final SectionPos pos) {
         return null;
      }

      public int getLightValue(final BlockPos pos) {
         return 0;
      }

      public void checkBlock(final BlockPos pos) {
      }

      public boolean hasLightWork() {
         return false;
      }

      public int runLightUpdates() {
         return 0;
      }

      public void updateSectionStatus(final SectionPos pos, final boolean sectionEmpty) {
      }

      public void setLightEnabled(final ChunkPos pos, final boolean enable) {
      }

      public void propagateLightSources(final ChunkPos pos) {
      }

      // $FF: synthetic method
      private static DummyLightLayerEventListener[] $values() {
         return new DummyLightLayerEventListener[]{INSTANCE};
      }
   }
}
