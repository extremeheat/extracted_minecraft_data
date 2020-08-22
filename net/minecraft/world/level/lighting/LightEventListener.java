package net.minecraft.world.level.lighting;

import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;

public interface LightEventListener {
   default void updateSectionStatus(BlockPos var1, boolean var2) {
      this.updateSectionStatus(SectionPos.of(var1), var2);
   }

   void updateSectionStatus(SectionPos var1, boolean var2);
}
