package net.minecraft.client.renderer.chunk;

import com.mojang.blaze3d.vertex.VertexFormat;
import java.util.Collections;
import java.util.List;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jspecify.annotations.Nullable;

public interface SectionMesh extends AutoCloseable {
   default boolean isDifferentPointOfView(final TranslucencyPointOfView pointOfView) {
      return false;
   }

   default boolean hasRenderableLayers() {
      return false;
   }

   default boolean hasTranslucentGeometry() {
      return false;
   }

   default boolean isEmpty(final ChunkSectionLayer layer) {
      return true;
   }

   default List<BlockEntity> getRenderableBlockEntities() {
      return Collections.emptyList();
   }

   boolean facesCanSeeEachother(Direction direction1, Direction direction2);

   default @Nullable SectionDraw getSectionDraw(final ChunkSectionLayer layer) {
      return null;
   }

   default void close() {
   }

   public static record SectionDraw(int indexCount, VertexFormat.IndexType indexType, boolean hasCustomIndexBuffer) {
      public SectionDraw {
         super();
      }
   }
}
