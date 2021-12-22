package net.minecraft.server.players;

import java.util.Iterator;
import java.util.List;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;

public class SleepStatus {
   private int activePlayers;
   private int sleepingPlayers;

   public SleepStatus() {
      super();
   }

   public boolean areEnoughSleeping(int var1) {
      return this.sleepingPlayers >= this.sleepersNeeded(var1);
   }

   public boolean areEnoughDeepSleeping(int var1, List<ServerPlayer> var2) {
      int var3 = (int)var2.stream().filter(Player::isSleepingLongEnough).count();
      return var3 >= this.sleepersNeeded(var1);
   }

   public int sleepersNeeded(int var1) {
      return Math.max(1, Mth.ceil((float)(this.activePlayers * var1) / 100.0F));
   }

   public void removeAllSleepers() {
      this.sleepingPlayers = 0;
   }

   public int amountSleeping() {
      return this.sleepingPlayers;
   }

   public boolean update(List<ServerPlayer> var1) {
      int var2 = this.activePlayers;
      int var3 = this.sleepingPlayers;
      this.activePlayers = 0;
      this.sleepingPlayers = 0;
      Iterator var4 = var1.iterator();

      while(var4.hasNext()) {
         ServerPlayer var5 = (ServerPlayer)var4.next();
         if (!var5.isSpectator()) {
            ++this.activePlayers;
            if (var5.isSleeping()) {
               ++this.sleepingPlayers;
            }
         }
      }

      return (var3 > 0 || this.sleepingPlayers > 0) && (var2 != this.activePlayers || var3 != this.sleepingPlayers);
   }
}
