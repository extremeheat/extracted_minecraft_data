package net.minecraft.client.gui.screens;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.GameNarrator;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;

public class ReceivingLevelScreen extends Screen {
   private static final Component DOWNLOADING_TERRAIN_TEXT = Component.translatable("multiplayer.downloadingTerrain");
   private static final long CHUNK_LOADING_START_WAIT_LIMIT_MS = 2000L;
   private boolean loadingPacketsReceived = false;
   private boolean oneTickSkipped = false;
   private final long createdAt = System.currentTimeMillis();

   public ReceivingLevelScreen() {
      super(GameNarrator.NO_TITLE);
   }

   public boolean shouldCloseOnEsc() {
      return false;
   }

   public void render(PoseStack var1, int var2, int var3, float var4) {
      this.renderDirtBackground(0);
      drawCenteredString(var1, this.font, DOWNLOADING_TERRAIN_TEXT, this.width / 2, this.height / 2 - 50, 16777215);
      super.render(var1, var2, var3, var4);
   }

   public void tick() {
      boolean var1 = this.oneTickSkipped || System.currentTimeMillis() > this.createdAt + 2000L;
      if (var1 && this.minecraft != null && this.minecraft.player != null) {
         BlockPos var2 = this.minecraft.player.blockPosition();
         boolean var3 = this.minecraft.level != null && this.minecraft.level.isOutsideBuildHeight(var2.getY());
         if (var3 || this.minecraft.levelRenderer.isChunkCompiled(var2)) {
            this.onClose();
         }

         if (this.loadingPacketsReceived) {
            this.oneTickSkipped = true;
         }

      }
   }

   public void loadingPacketsReceived() {
      this.loadingPacketsReceived = true;
   }

   public boolean isPauseScreen() {
      return false;
   }
}
