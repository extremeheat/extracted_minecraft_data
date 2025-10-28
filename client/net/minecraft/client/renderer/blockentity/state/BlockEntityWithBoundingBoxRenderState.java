package net.minecraft.client.renderer.blockentity.state;

import net.minecraft.world.level.block.entity.BoundingBoxRenderable;
import org.jspecify.annotations.Nullable;

public class BlockEntityWithBoundingBoxRenderState extends BlockEntityRenderState {
   public boolean isVisible;
   public BoundingBoxRenderable.Mode mode;
   public BoundingBoxRenderable.RenderableBox box;
   public @Nullable BlockEntityWithBoundingBoxRenderState.InvisibleBlockType @Nullable [] invisibleBlocks;
   public boolean @Nullable [] structureVoids;

   public BlockEntityWithBoundingBoxRenderState() {
      super();
   }

   public static enum InvisibleBlockType {
      AIR,
      BARRIER,
      LIGHT,
      STRUCTURE_VOID;

      private InvisibleBlockType() {
      }

      // $FF: synthetic method
      private static InvisibleBlockType[] $values() {
         return new InvisibleBlockType[]{AIR, BARRIER, LIGHT, STRUCTURE_VOID};
      }
   }
}
