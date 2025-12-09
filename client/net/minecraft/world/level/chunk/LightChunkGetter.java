package net.minecraft.world.level.chunk;

import net.minecraft.core.SectionPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LightLayer;
import org.jspecify.annotations.Nullable;

public interface LightChunkGetter {
   @Nullable LightChunk getChunkForLighting(int var1, int var2);

   default void onLightUpdate(LightLayer var1, SectionPos var2) {
   }

   BlockGetter getLevel();
}
