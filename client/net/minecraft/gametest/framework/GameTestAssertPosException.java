package net.minecraft.gametest.framework;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;

public class GameTestAssertPosException extends GameTestAssertException {
   private final BlockPos absolutePos;
   private final BlockPos relativePos;
   private final long tick;

   public GameTestAssertPosException(String var1, BlockPos var2, BlockPos var3, long var4) {
      super(var1);
      this.absolutePos = var2;
      this.relativePos = var3;
      this.tick = var4;
   }

   public String getMessage() {
      int var10000 = this.absolutePos.getX();
      String var1 = var10000 + "," + this.absolutePos.getY() + "," + this.absolutePos.getZ() + " (relative: " + this.relativePos.getX() + "," + this.relativePos.getY() + "," + this.relativePos.getZ() + ")";
      return super.getMessage() + " at " + var1 + " (t=" + this.tick + ")";
   }

   @Nullable
   public String getMessageToShowAtBlock() {
      return super.getMessage();
   }

   @Nullable
   public BlockPos getRelativePos() {
      return this.relativePos;
   }

   @Nullable
   public BlockPos getAbsolutePos() {
      return this.absolutePos;
   }
}
