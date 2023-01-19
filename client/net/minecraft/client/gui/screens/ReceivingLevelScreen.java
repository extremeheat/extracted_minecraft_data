package net.minecraft.client.gui.screens;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.GameNarrator;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;

public class ReceivingLevelScreen extends Screen {
   private static final Component DOWNLOADING_TERRAIN_TEXT = Component.translatable("multiplayer.downloadingTerrain");
   private static final long CHUNK_LOADING_START_WAIT_LIMIT_MS = 30000L;
   private boolean loadingPacketsReceived = false;
   private boolean oneTickSkipped = false;
   private final long createdAt = System.currentTimeMillis();

   public ReceivingLevelScreen() {
      super(GameNarrator.NO_TITLE);
   }

   @Override
   public boolean shouldCloseOnEsc() {
      return false;
   }

   @Override
   public void render(PoseStack var1, int var2, int var3, float var4) {
      this.renderDirtBackground(0);
      drawCenteredString(var1, this.font, DOWNLOADING_TERRAIN_TEXT, this.width / 2, this.height / 2 - 50, 16777215);
      super.render(var1, var2, var3, var4);
   }

   @Override
   public void tick() {
      if (System.currentTimeMillis() > this.createdAt + 30000L) {
         this.onClose();
      } else {
         if (this.oneTickSkipped) {
            if (this.minecraft.player == null) {
               return;
            }

            BlockPos var1 = this.minecraft.player.blockPosition();
            boolean var2 = this.minecraft.level != null && this.minecraft.level.isOutsideBuildHeight(var1.getY());
            if (var2 || this.minecraft.levelRenderer.isChunkCompiled(var1) || this.minecraft.player.isSpectator() || !this.minecraft.player.isAlive()) {
               this.onClose();
            }
         } else {
            this.oneTickSkipped = this.loadingPacketsReceived;
         }
      }
   }

   public void loadingPacketsReceived() {
      this.loadingPacketsReceived = true;
   }

   @Override
   public boolean isPauseScreen() {
      return false;
   }
}
