package net.minecraft.server.level;

import net.minecraft.core.BlockPos;

public class BlockDestructionProgress {
   private final int id;
   private final BlockPos pos;
   private int progress;
   private int updatedRenderTick;

   public BlockDestructionProgress(int var1, BlockPos var2) {
      super();
      this.id = var1;
      this.pos = var2;
   }

   public BlockPos getPos() {
      return this.pos;
   }

   public void setProgress(int var1) {
      if (var1 > 10) {
         var1 = 10;
      }

      this.progress = var1;
   }

   public int getProgress() {
      return this.progress;
   }

   public void updateTick(int var1) {
      this.updatedRenderTick = var1;
   }

   public int getUpdatedRenderTick() {
      return this.updatedRenderTick;
   }
}
