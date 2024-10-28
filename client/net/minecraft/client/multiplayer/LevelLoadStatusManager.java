package net.minecraft.client.multiplayer;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;

public class LevelLoadStatusManager {
   private final LocalPlayer player;
   private final ClientLevel level;
   private final LevelRenderer levelRenderer;
   private Status status;

   public LevelLoadStatusManager(LocalPlayer var1, ClientLevel var2, LevelRenderer var3) {
      super();
      this.status = LevelLoadStatusManager.Status.WAITING_FOR_SERVER;
      this.player = var1;
      this.level = var2;
      this.levelRenderer = var3;
   }

   public void tick() {
      switch (this.status.ordinal()) {
         case 1:
            BlockPos var1 = this.player.blockPosition();
            boolean var2 = this.level.isOutsideBuildHeight(var1.getY());
            if (var2 || this.levelRenderer.isSectionCompiled(var1) || this.player.isSpectator() || !this.player.isAlive()) {
               this.status = LevelLoadStatusManager.Status.LEVEL_READY;
            }
         case 0:
         case 2:
         default:
      }
   }

   public boolean levelReady() {
      return this.status == LevelLoadStatusManager.Status.LEVEL_READY;
   }

   public void loadingPacketsReceived() {
      if (this.status == LevelLoadStatusManager.Status.WAITING_FOR_SERVER) {
         this.status = LevelLoadStatusManager.Status.WAITING_FOR_PLAYER_CHUNK;
      }

   }

   static enum Status {
      WAITING_FOR_SERVER,
      WAITING_FOR_PLAYER_CHUNK,
      LEVEL_READY;

      private Status() {
      }

      // $FF: synthetic method
      private static Status[] $values() {
         return new Status[]{WAITING_FOR_SERVER, WAITING_FOR_PLAYER_CHUNK, LEVEL_READY};
      }
   }
}
