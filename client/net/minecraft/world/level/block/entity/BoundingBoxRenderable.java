package net.minecraft.world.level.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;

public interface BoundingBoxRenderable {
   Mode renderMode();

   RenderableBox getRenderableBox();

   public static record RenderableBox(BlockPos localPos, Vec3i size) {
      public RenderableBox {
         super();
      }

      public static RenderableBox fromCorners(final int x1, final int y1, final int z1, final int x2, final int y2, final int z2) {
         int x = Math.min(x1, x2);
         int y = Math.min(y1, y2);
         int z = Math.min(z1, z2);
         return new RenderableBox(new BlockPos(x, y, z), new Vec3i(Math.max(x1, x2) - x, Math.max(y1, y2) - y, Math.max(z1, z2) - z));
      }
   }

   public static enum Mode {
      NONE,
      BOX,
      BOX_AND_INVISIBLE_BLOCKS;

      private Mode() {
      }

      // $FF: synthetic method
      private static Mode[] $values() {
         return new Mode[]{NONE, BOX, BOX_AND_INVISIBLE_BLOCKS};
      }
   }
}
