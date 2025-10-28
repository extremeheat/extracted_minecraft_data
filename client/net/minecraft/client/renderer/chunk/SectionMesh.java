package net.minecraft.client.renderer.chunk;

import java.util.Collections;
import java.util.List;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jspecify.annotations.Nullable;

public interface SectionMesh extends AutoCloseable {
   default boolean isDifferentPointOfView(TranslucencyPointOfView var1) {
      return false;
   }

   default boolean hasRenderableLayers() {
      return false;
   }

   default boolean hasTranslucentGeometry() {
      return false;
   }

   default boolean isEmpty(ChunkSectionLayer var1) {
      return true;
   }

   default List<BlockEntity> getRenderableBlockEntities() {
      return Collections.emptyList();
   }

   boolean facesCanSeeEachother(Direction var1, Direction var2);

   default @Nullable SectionBuffers getBuffers(ChunkSectionLayer var1) {
      return null;
   }

   default void close() {
   }
}
