package net.minecraft.world.level.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;

public interface BoundingBoxRenderable {
   Mode renderMode();

   RenderableBox getRenderableBox();

   public static record RenderableBox(BlockPos localPos, Vec3i size) {
      public RenderableBox(BlockPos var1, Vec3i var2) {
         super();
         this.localPos = var1;
         this.size = var2;
      }

      public static RenderableBox fromCorners(int var0, int var1, int var2, int var3, int var4, int var5) {
         int var6 = Math.min(var0, var3);
         int var7 = Math.min(var1, var4);
         int var8 = Math.min(var2, var5);
         return new RenderableBox(new BlockPos(var6, var7, var8), new Vec3i(Math.max(var0, var3) - var6, Math.max(var1, var4) - var7, Math.max(var2, var5) - var8));
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
