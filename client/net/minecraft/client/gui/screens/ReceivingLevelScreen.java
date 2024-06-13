package net.minecraft.client.gui.screens;

import java.util.function.BooleanSupplier;
import javax.annotation.Nullable;
import net.minecraft.client.GameNarrator;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.Blocks;

public class ReceivingLevelScreen extends Screen {
   private static final Component DOWNLOADING_TERRAIN_TEXT = Component.translatable("multiplayer.downloadingTerrain");
   private static final long CHUNK_LOADING_START_WAIT_LIMIT_MS = 30000L;
   private final long createdAt;
   private final BooleanSupplier levelReceived;
   private final ReceivingLevelScreen.Reason reason;
   @Nullable
   private TextureAtlasSprite cachedNetherPortalSprite;

   public ReceivingLevelScreen(BooleanSupplier var1, ReceivingLevelScreen.Reason var2) {
      super(GameNarrator.NO_TITLE);
      this.levelReceived = var1;
      this.reason = var2;
      this.createdAt = System.currentTimeMillis();
   }

   @Override
   public boolean shouldCloseOnEsc() {
      return false;
   }

   @Override
   protected boolean shouldNarrateNavigation() {
      return false;
   }

   @Override
   public void render(GuiGraphics var1, int var2, int var3, float var4) {
      super.render(var1, var2, var3, var4);
      var1.drawCenteredString(this.font, DOWNLOADING_TERRAIN_TEXT, this.width / 2, this.height / 2 - 50, 16777215);
   }

   @Override
   public void renderBackground(GuiGraphics var1, int var2, int var3, float var4) {
      switch (this.reason) {
         case NETHER_PORTAL:
            var1.blit(0, 0, -90, var1.guiWidth(), var1.guiHeight(), this.getNetherPortalSprite());
            break;
         case END_PORTAL:
            var1.fillRenderType(RenderType.endPortal(), 0, 0, this.width, this.height, 0);
            break;
         case OTHER:
            this.renderPanorama(var1, var4);
            this.renderBlurredBackground(var4);
            this.renderMenuBackground(var1);
      }
   }

   private TextureAtlasSprite getNetherPortalSprite() {
      if (this.cachedNetherPortalSprite != null) {
         return this.cachedNetherPortalSprite;
      } else {
         this.cachedNetherPortalSprite = this.minecraft.getBlockRenderer().getBlockModelShaper().getParticleIcon(Blocks.NETHER_PORTAL.defaultBlockState());
         return this.cachedNetherPortalSprite;
      }
   }

   @Override
   public void tick() {
      if (this.levelReceived.getAsBoolean() || System.currentTimeMillis() > this.createdAt + 30000L) {
         this.onClose();
      }
   }

   @Override
   public void onClose() {
      this.minecraft.getNarrator().sayNow(Component.translatable("narrator.ready_to_play"));
      super.onClose();
   }

   @Override
   public boolean isPauseScreen() {
      return false;
   }

   public static enum Reason {
      NETHER_PORTAL,
      END_PORTAL,
      OTHER;

      private Reason() {
      }
   }
}