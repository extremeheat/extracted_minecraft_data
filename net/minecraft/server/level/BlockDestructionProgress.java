package net.minecraft.server.level;

import net.minecraft.core.BlockPos;

public class BlockDestructionProgress implements Comparable {
   private final int id;
   private final BlockPos pos;
   private int progress;
   private int updatedRenderTick;

   public BlockDestructionProgress(int var1, BlockPos var2) {
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

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 != null && this.getClass() == var1.getClass()) {
         BlockDestructionProgress var2 = (BlockDestructionProgress)var1;
         return this.id == var2.id;
      } else {
         return false;
      }
   }

   public int hashCode() {
      return Integer.hashCode(this.id);
   }

   public int compareTo(BlockDestructionProgress var1) {
      return this.progress != var1.progress ? Integer.compare(this.progress, var1.progress) : Integer.compare(this.id, var1.id);
   }

   // $FF: synthetic method
   public int compareTo(Object var1) {
      return this.compareTo((BlockDestructionProgress)var1);
   }
}
