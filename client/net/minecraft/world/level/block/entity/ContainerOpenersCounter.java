package net.minecraft.world.level.block.entity;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;

public abstract class ContainerOpenersCounter {
   private static final int CHECK_TICK_DELAY = 5;
   private int openCount;
   private double maxInteractionRange;

   public ContainerOpenersCounter() {
      super();
   }

   protected abstract void onOpen(Level var1, BlockPos var2, BlockState var3);

   protected abstract void onClose(Level var1, BlockPos var2, BlockState var3);

   protected abstract void openerCountChanged(Level var1, BlockPos var2, BlockState var3, int var4, int var5);

   protected abstract boolean isOwnContainer(Player var1);

   public void incrementOpeners(Player var1, Level var2, BlockPos var3, BlockState var4) {
      int var5 = this.openCount++;
      if (var5 == 0) {
         this.onOpen(var2, var3, var4);
         var2.gameEvent(var1, GameEvent.CONTAINER_OPEN, var3);
         scheduleRecheck(var2, var3, var4);
      }

      this.openerCountChanged(var2, var3, var4, var5, this.openCount);
      this.maxInteractionRange = Math.max(var1.blockInteractionRange(), this.maxInteractionRange);
   }

   public void decrementOpeners(Player var1, Level var2, BlockPos var3, BlockState var4) {
      int var5 = this.openCount--;
      if (this.openCount == 0) {
         this.onClose(var2, var3, var4);
         var2.gameEvent(var1, GameEvent.CONTAINER_CLOSE, var3);
         this.maxInteractionRange = 0.0;
      }

      this.openerCountChanged(var2, var3, var4, var5, this.openCount);
   }

   private List<Player> getPlayersWithContainerOpen(Level var1, BlockPos var2) {
      double var3 = this.maxInteractionRange + 4.0;
      AABB var5 = new AABB(var2).inflate(var3);
      return var1.getEntities(EntityTypeTest.forClass(Player.class), var5, this::isOwnContainer);
   }

   public void recheckOpeners(Level var1, BlockPos var2, BlockState var3) {
      List var4 = this.getPlayersWithContainerOpen(var1, var2);
      this.maxInteractionRange = 0.0;

      for (Player var6 : var4) {
         this.maxInteractionRange = Math.max(var6.blockInteractionRange(), this.maxInteractionRange);
      }

      int var9 = var4.size();
      int var10 = this.openCount;
      if (var10 != var9) {
         boolean var7 = var9 != 0;
         boolean var8 = var10 != 0;
         if (var7 && !var8) {
            this.onOpen(var1, var2, var3);
            var1.gameEvent(null, GameEvent.CONTAINER_OPEN, var2);
         } else if (!var7) {
            this.onClose(var1, var2, var3);
            var1.gameEvent(null, GameEvent.CONTAINER_CLOSE, var2);
         }

         this.openCount = var9;
      }

      this.openerCountChanged(var1, var2, var3, var10, var9);
      if (var9 > 0) {
         scheduleRecheck(var1, var2, var3);
      }
   }

   public int getOpenerCount() {
      return this.openCount;
   }

   private static void scheduleRecheck(Level var0, BlockPos var1, BlockState var2) {
      var0.scheduleTick(var1, var2.getBlock(), 5);
   }
}
