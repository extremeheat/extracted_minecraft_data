package net.minecraft.world.level.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.AABB;

public abstract class ContainerOpenersCounter {
   private int openCount;

   public ContainerOpenersCounter() {
      super();
   }

   protected abstract void onOpen(Level var1, BlockPos var2, BlockState var3);

   protected abstract void onClose(Level var1, BlockPos var2, BlockState var3);

   protected abstract void openerCountChanged(Level var1, BlockPos var2, BlockState var3, int var4, int var5);

   protected abstract boolean isOwnContainer(Player var1);

   public void incrementOpeners(Level var1, BlockPos var2, BlockState var3) {
      int var4 = this.openCount++;
      if (var4 == 0) {
         this.onOpen(var1, var2, var3);
         scheduleRecheck(var1, var2, var3);
      }

      this.openerCountChanged(var1, var2, var3, var4, this.openCount);
   }

   public void decrementOpeners(Level var1, BlockPos var2, BlockState var3) {
      int var4 = this.openCount--;
      if (this.openCount == 0) {
         this.onClose(var1, var2, var3);
      }

      this.openerCountChanged(var1, var2, var3, var4, this.openCount);
   }

   private int getOpenCount(Level var1, BlockPos var2) {
      int var3 = var2.getX();
      int var4 = var2.getY();
      int var5 = var2.getZ();
      float var6 = 5.0F;
      AABB var7 = new AABB((double)((float)var3 - 5.0F), (double)((float)var4 - 5.0F), (double)((float)var5 - 5.0F), (double)((float)(var3 + 1) + 5.0F), (double)((float)(var4 + 1) + 5.0F), (double)((float)(var5 + 1) + 5.0F));
      return var1.getEntities(EntityTypeTest.forClass(Player.class), var7, this::isOwnContainer).size();
   }

   public void recheckOpeners(Level var1, BlockPos var2, BlockState var3) {
      int var4 = this.getOpenCount(var1, var2);
      int var5 = this.openCount;
      if (var5 != var4) {
         boolean var6 = var4 != 0;
         boolean var7 = var5 != 0;
         if (var6 && !var7) {
            this.onOpen(var1, var2, var3);
         } else if (!var6) {
            this.onClose(var1, var2, var3);
         }

         this.openCount = var4;
      }

      this.openerCountChanged(var1, var2, var3, var5, var4);
      if (var4 > 0) {
         scheduleRecheck(var1, var2, var3);
      }

   }

   public int getOpenerCount() {
      return this.openCount;
   }

   private static void scheduleRecheck(Level var0, BlockPos var1, BlockState var2) {
      var0.getBlockTicks().scheduleTick(var1, var2.getBlock(), 5);
   }
}
